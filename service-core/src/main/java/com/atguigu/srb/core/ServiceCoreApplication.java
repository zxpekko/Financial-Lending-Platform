package com.atguigu.srb.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Author:zxp
 * @Description:
 * @Date:17:17 2024/2/7
 */
@SpringBootApplication
@ComponentScan({"com.atguigu.srb","com.atguigu.common"})
public class ServiceCoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceCoreApplication.class, args);
    }
}
