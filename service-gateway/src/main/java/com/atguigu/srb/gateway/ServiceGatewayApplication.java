package com.atguigu.srb.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

import java.util.LinkedList;
import java.util.Queue;

/**
    *@Author:zxp
    *@Description:
    *@Date:14:36 2024/3/5
*/
@SpringBootApplication
@EnableDiscoveryClient
//@ComponentScan({"com.atguigu.srb","com.atguigu.common"})
public class ServiceGatewayApplication {
    public static void main(String[] args) {
//        StringBuilder stringBuilder = new StringBuilder();

        SpringApplication.run(ServiceGatewayApplication.class, args);
    }
}