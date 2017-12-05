package com.haier.interconn.hcloud.ribbon;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.netflix.client.IClientConfigAware;
import com.netflix.client.config.CommonClientConfigKey;
import com.netflix.client.config.DefaultClientConfigImpl;
import com.netflix.client.config.IClientConfig;
import com.netflix.config.*;
import com.netflix.loadbalancer.*;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import com.netflix.servo.monitor.Counter;
import com.netflix.servo.monitor.Monitors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: bandd
 * Mailto:bandd@haier.com
 * On: 2017-09-25  11:04
 */
public class VersionServerListFilter<T extends Server>  extends AbstractServerListFilter<T> implements IClientConfigAware {


    private volatile boolean zoneAffinity = DefaultClientConfigImpl.DEFAULT_ENABLE_ZONE_AFFINITY;
    private volatile boolean zoneExclusive = DefaultClientConfigImpl.DEFAULT_ENABLE_ZONE_EXCLUSIVITY;
    private DynamicDoubleProperty activeReqeustsPerServerThreshold;
    private DynamicDoubleProperty blackOutServerPercentageThreshold;
    private DynamicIntProperty availableServersThreshold;
    private Counter overrideCounter;
    private ZoneAffinityPredicate zoneAffinityPredicate = new ZoneAffinityPredicate();




    private Environment environment;

    private  String versionPlaceholder = "microservice.reference.%s.version";


    public static final String META_DATA_KEY_VERSION = "version";

    private static Logger logger = LoggerFactory.getLogger(VersionServerListFilter.class);

    String zone;

    public static Map<String,String> refercenServiceInfo = new HashMap<>();

    public VersionServerListFilter(Environment environment) {
        this.environment = environment;
    }

    public VersionServerListFilter(IClientConfig niwsClientConfig) {
        initWithNiwsConfig(niwsClientConfig);
    }

    @Override
    public void initWithNiwsConfig(IClientConfig niwsClientConfig) {
        String sZoneAffinity = "" + niwsClientConfig.getProperty(CommonClientConfigKey.EnableZoneAffinity, false);
        if (sZoneAffinity != null){
            zoneAffinity = Boolean.parseBoolean(sZoneAffinity);
            logger.debug("ZoneAffinity is set to {}", zoneAffinity);
        }
        String sZoneExclusive = "" + niwsClientConfig.getProperty(CommonClientConfigKey.EnableZoneExclusivity, false);
        if (sZoneExclusive != null){
            zoneExclusive = Boolean.parseBoolean(sZoneExclusive);
        }
        if (ConfigurationManager.getDeploymentContext() != null) {
            zone = ConfigurationManager.getDeploymentContext().getValue(DeploymentContext.ContextKey.zone);
        }
        activeReqeustsPerServerThreshold = DynamicPropertyFactory.getInstance().getDoubleProperty(niwsClientConfig.getClientName() + "." + niwsClientConfig.getNameSpace() + ".zoneAffinity.maxLoadPerServer", 0.6d);
        logger.debug("activeReqeustsPerServerThreshold: {}", activeReqeustsPerServerThreshold.get());
        blackOutServerPercentageThreshold = DynamicPropertyFactory.getInstance().getDoubleProperty(niwsClientConfig.getClientName() + "." + niwsClientConfig.getNameSpace() + ".zoneAffinity.maxBlackOutServesrPercentage", 0.8d);
        logger.debug("blackOutServerPercentageThreshold: {}", blackOutServerPercentageThreshold.get());
        availableServersThreshold = DynamicPropertyFactory.getInstance().getIntProperty(niwsClientConfig.getClientName() + "." + niwsClientConfig.getNameSpace() + ".zoneAffinity.minAvailableServers", 2);
        logger.debug("availableServersThreshold: {}", availableServersThreshold.get());
        overrideCounter = Monitors.newCounter("ZoneAffinity_OverrideCounter");

        Monitors.registerObject("NIWSServerListFilter_" + niwsClientConfig.getClientName());
    }

    private boolean shouldEnableZoneAffinity(List<T> filtered) {
        if (!zoneAffinity && !zoneExclusive) {
            return false;
        }
        if (zoneExclusive) {
            return true;
        }
        LoadBalancerStats stats = getLoadBalancerStats();
        if (stats == null) {
            return zoneAffinity;
        } else {
            logger.debug("Determining if zone affinity should be enabled with given server list: {}", filtered);
            ZoneSnapshot snapshot = stats.getZoneSnapshot(filtered);
            double loadPerServer = snapshot.getLoadPerServer();
            int instanceCount = snapshot.getInstanceCount();
            int circuitBreakerTrippedCount = snapshot.getCircuitTrippedCount();
            if (((double) circuitBreakerTrippedCount) / instanceCount >= blackOutServerPercentageThreshold.get()
                    || loadPerServer >= activeReqeustsPerServerThreshold.get()
                    || (instanceCount - circuitBreakerTrippedCount) < availableServersThreshold.get()) {
                logger.debug("zoneAffinity is overriden. blackOutServerPercentage: {}, activeReqeustsPerServer: {}, availableServers: {}",
                        new Object[] {(double) circuitBreakerTrippedCount / instanceCount,  loadPerServer, instanceCount - circuitBreakerTrippedCount});
                return false;
            } else {
                return true;
            }

        }
    }

    @Override
    public List<T> getFilteredListOfServers(List<T> servers) {
        if (zone != null && (zoneAffinity || zoneExclusive) && servers !=null && servers.size() > 0){
            List<T> filteredServers = Lists.newArrayList(Iterables.filter(
                    servers, this.zoneAffinityPredicate.getServerOnlyPredicate()));
            if (shouldEnableZoneAffinity(filteredServers)) {
                return getFiltersByVersioninfo(filteredServers);
            } else if (zoneAffinity) {
                overrideCounter.increment();
            }
        }


        return getFiltersByVersioninfo(servers);
    }


    public List<T> getFiltersByVersioninfo(List<T> servers){
        List<Server> discoveryEnabledServerList  = (List<Server>) servers;
        List<T> filtedServers = new ArrayList<>();
        for(Server server:discoveryEnabledServerList){
            Map<String, String> metadata =    ((DiscoveryEnabledServer)server).getInstanceInfo().getMetadata();
            String serviceID = ((DiscoveryEnabledServer)server).getInstanceInfo().getAppName();
            logger.debug("serviceID:{}",serviceID);
            //get provider version
            String providerVersion = metadata.get(META_DATA_KEY_VERSION);
            logger.debug("providerVersion:{}",providerVersion);
            //get consumer version
            String consumerVersion = String.format(versionPlaceholder,serviceID).toLowerCase();
            String realConsumerVersion = environment.getProperty(consumerVersion);
            refercenServiceInfo.put(serviceID,realConsumerVersion); //保存引用的服务名称和verion
            logger.debug("realConsumerVersion:{}",realConsumerVersion);
            //版本控制逻辑如下
            //如果consumer端传递过来的version有值，找对应的版本，如果没有version信息的话,那匹配provider所有的版本
            if(!StringUtils.isEmpty(consumerVersion)&&!realConsumerVersion.equals(providerVersion)){
                    continue;
            }else{
                filtedServers.add((T) server);
            }
        }

        return  filtedServers;

    }


    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder("ZoneAffinityServerListFilter:");
        sb.append(", zone: ").append(zone).append(", zoneAffinity:").append(zoneAffinity);
        sb.append(", zoneExclusivity:").append(zoneExclusive);
        return sb.toString();
    }



}
