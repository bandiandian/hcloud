package com.haier.interconn.hcloud.trace;

import com.github.kevinsawicki.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.trace.Trace;
import org.springframework.boot.actuate.trace.TraceRepository;
import org.springframework.util.StringUtils;

import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 重写TraceRepository,把trace记录持久到influxdb中
 * Author: bandd
 * Mailto:bandd@haier.com
 * On: 2017-08-24  17:29
 */
public class InfluxdbTraceRepository implements TraceRepository {

    private static final Logger logger = LoggerFactory.getLogger(InfluxdbTraceRepository.class);

    private int capacity = 100;

    private boolean reverse = true;

    private final List<Trace> traces = new LinkedList<Trace>();

    private final List<Trace> tracesCopy = new CopyOnWriteArrayList<>();

    private boolean influxdbEnable =true;

    private   String INFLUXDB_URL =null ;


    private ScheduledExecutorService  scheduledExecutorService = Executors.newScheduledThreadPool(3,new NamedThreadFactory("appCollectSendTimer", true));


    InfluxdbTraceRepository(boolean influxdbEnable,String url){
        this.influxdbEnable  = influxdbEnable;
        this.INFLUXDB_URL  = url;
        scheduledExecutorService.scheduleWithFixedDelay(
                new Runnable() {
                    @Override
                    public void run() {
                        try{
                            send();
                        }catch (Throwable t){
                            logger.error("Unexpected error occur at send app request, cause: " + t.getMessage(), t);
                        }
                    }
                },
                3, 3, TimeUnit.SECONDS);



    }


    //fix tomcat终止后没有及时清理定时任务的线程
    @PreDestroy
    public void shutdownAppcollect(){
        logger.info("执行shutdownAppcollect方法，销毁线程appCollectSendTimer");
        scheduledExecutorService.shutdown();
    }



    public void send(){
        StringBuilder stringBuilder = new StringBuilder();
           for (int i=0;i<this.tracesCopy.size();i++) {
               Trace trace =  tracesCopy.get(i);
               String appname = ((Map<String, String>)((Map<String,Object>)(trace.getInfo().get("headers"))).get("response")).get("X-Application-Context").toString();
               String afterRepAppname = "error";
               if (!StringUtils.isEmpty(appname)) {
                   int colon_pos_first = appname.indexOf(":");
                   int clone_post_last = appname.lastIndexOf(":");
                   afterRepAppname = appname.substring(0, colon_pos_first) + appname.substring(clone_post_last);
               }

               stringBuilder.append(afterRepAppname).append(",");
               stringBuilder.append("hostport=").append(((Map<String, String>)((Map<String,Object>)(trace.getInfo().get("headers"))).get("request")).get("host")).append(",");
               stringBuilder.append("method=").append(trace.getInfo().get("method")).append(",");
               stringBuilder.append("path=").append(trace.getInfo().get("path")).append(",");
               stringBuilder.append("remoteAddress=").append(trace.getInfo().get("remoteAddress")).append(",");
               stringBuilder.append("query=").append(trace.getInfo().get("query")==null?"null":trace.getInfo().get("query").toString().replaceAll("=",":"));
               //method.delay.ms
               stringBuilder.append(" delay=").append(trace.getInfo().get("method.delay.ms")==null?"0":trace.getInfo().get("method.delay.ms").toString()).append(",");//延迟信息 单位毫秒
               stringBuilder.append("status=").append(((Map<String, String>)((Map<String,Object>)(trace.getInfo().get("headers"))).get("response")).get("status"));
               stringBuilder.append(" "+(trace.getTimestamp().getTime()-10*60*1000));//倒退10分钟入库到influxdb
               stringBuilder.append("\n");
               this.tracesCopy.remove(i);

            }



        if(this.influxdbEnable&& !StringUtils.isEmpty(this.INFLUXDB_URL)&&!StringUtils.isEmpty(stringBuilder.toString())){
            //发送infuxdbd
            //&precision=ms&rp=trace90
            //influxdb数据库的时间有误差，所以为了保险期间可以把每个trace的timestamp倒退10分钟，否则无法入库到influxdb
             HttpRequest.post(INFLUXDB_URL+"write?db=mydb&precision=ms&rp=trace90").send(stringBuilder.toString()).code();
        }



    }

    public void sendTOInfluxd(Trace trace){

        //调用http接口把trace发送到influxdb
    }

    /**
     * Flag to say that the repository lists traces in reverse order.
     * @param reverse flag value (default true)
     */
    public void setReverse(boolean reverse) {
        synchronized (this.traces) {
            this.reverse = reverse;
        }
    }

    /**
     * Set the capacity of the in-memory repository.
     * @param capacity the capacity
     */
    public void setCapacity(int capacity) {
        synchronized (this.traces) {
            this.capacity = capacity;
        }
    }

    @Override
    public List<Trace> findAll() {
        synchronized (this.traces) {
            return Collections.unmodifiableList(new ArrayList<Trace>(this.traces));
        }
    }

    @Override
    public void add(Map<String, Object> map) {
        Trace trace = new Trace(new Date(), map);




        synchronized (this.traces) {
            //for transfer to influxdb

            //去掉静态文件请求,html,js css png jpg
            String path = trace.getInfo().get("path").toString();
            if(!StringUtils.isEmpty(path)&&!path.endsWith(".js")&&!path.endsWith(".png")&&!path.endsWith(".ico")&&!path.endsWith(".css")&&!path.endsWith(".jpg")){
                tracesCopy.add(trace);
            }


            while (this.traces.size() >= this.capacity) {
                this.traces.remove(this.reverse ? this.capacity - 1 : 0);
            }
            if (this.reverse) {
                this.traces.add(0, trace);
            }
            else {
                this.traces.add(trace);
            }
        }
    }

}
