package org.alljeju.alljejuserver;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableScheduling
@EnableCaching
public class AllJejuServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AllJejuServerApplication.class, args);
    }

}
