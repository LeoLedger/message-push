package hw.topevery;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan({
        "hw.topevery.framework.**",
        "hw.topevery.cloud.client",
        "hw.topevery.**",
        "com.topevery.sdk.produce.**"
})
@SpringBootApplication(exclude = {DruidDataSourceAutoConfigure.class})
public class DataPushApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataPushApplication.class, args);
    }

}
