package com.haier.interconn.hcloud.eureka;

import com.netflix.appinfo.InstanceInfo;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.core.header.InBoundHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;
import sun.misc.BASE64Encoder;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Author: bandd
 * Mailto:bandd@haier.com
 * On: 2017-08-30  15:03
 */
public class AppNameCheckClientFilter extends ClientFilter{

    private Environment environment;

    protected Logger logger = LoggerFactory.getLogger(AppNameCheckClientFilter.class);

    public AppNameCheckClientFilter(Environment environment){

        this.environment = environment;

    }


    @Override
    public ClientResponse handle(ClientRequest cr) throws ClientHandlerException {
        InstanceInfo instanceInfo = (InstanceInfo) cr.getEntity();
        ClientResponse clientResponse = new ClientResponse(400,new InBoundHeaders(),null,null);
        BASE64Encoder base64Encoder = new BASE64Encoder();
        if(null!=instanceInfo){

            if(StringUtils.isEmpty(instanceInfo.getASGName())){
               logger.error(" unregister  to eureka,because:Missing asgName,asgName not allow null");
                return clientResponse;
            }

            Map<String,String> metadata = instanceInfo.getMetadata();
            // no version can not register
            //add by bandiandian  2017年9月25日15:48:06
            if(null==metadata||StringUtils.isEmpty(metadata.get("version"))){
                logger.error(" unregister  to eureka,because:Missing version,version not allow null");
                return clientResponse;

            }

            //向metadata中注入上下文,如果存在的话
            // add by bandiandian 2017年9月26日16:14:47
            String contextPath = environment.getProperty("server.context-path");
            if(!StringUtils.isEmpty(contextPath)){
                metadata.put("context-path",contextPath);
            }

            Boolean bool = false;
            try {
                bool = instanceInfo.getASGName().equals(base64Encoder.encode(instanceInfo.getAppName().getBytes("utf-8")));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if(!bool){
                logger.error("unregister  to eureka,because:appName and asgName not match");
                return clientResponse;
            }
            return getNext().handle(cr);
        }else{
            return getNext().handle(cr);
        }
    }
}
