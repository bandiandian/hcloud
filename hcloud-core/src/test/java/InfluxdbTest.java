
import com.github.kevinsawicki.http.HttpRequest;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * Author: bandd
 * Mailto:bandd@haier.com
 * On: 2017-08-25  14:34
 */
public class InfluxdbTest {

    private String inflxudb_url = "http://10.138.25.211:8086/";



    @Test
    public void test() {

        //provider_service,host=server02,region=us-west value=1.6225
        //provider-service:8090,method=GET,path=/user/sayHello,remoteAddress=0:0:0:0:0:0:0:1,query=name=1234status=415
        int code = HttpRequest.post(inflxudb_url+"write?db=mydb").send("provider-service:8090,method=GET,path=/user/sayHello,remoteAddress=0:0:0:0:0:0:0:1,query={name\\=1234} status=418").code();
        System.out.println("code:"+code);
    }

}
