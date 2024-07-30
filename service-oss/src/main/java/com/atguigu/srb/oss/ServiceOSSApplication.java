package com.atguigu.srb.oss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Author:zxp
 * @Description:
 * @Date:13:10 2024/2/26
 */
@SpringBootApplication
@ComponentScan({"com.atguigu.srb","com.atguigu.common"})
public class ServiceOSSApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceOSSApplication.class,args);
    }
}
