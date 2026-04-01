package com.drv.assetmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {RedisRepositoriesAutoConfiguration.class})
@EnableScheduling
public class AssetManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(AssetManagementApplication.class, args);
    }
}