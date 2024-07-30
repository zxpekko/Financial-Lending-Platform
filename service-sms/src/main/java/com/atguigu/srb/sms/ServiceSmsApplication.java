package com.atguigu.srb.sms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

import java.util.HashMap;

/**
 * @Author:zxp
 * @Description:
 * @Date:17:17 2024/2/7
 */
@SpringBootApplication
@ComponentScan({"com.atguigu.srb","com.atguigu.common"})
@EnableFeignClients
public class ServiceSmsApplication {
    public static void main(String[] args) {
//        Double a=0.0;
//        label:for(int i=0;i<3;i++){
//            break label;
//        }
        HashMap<Integer, Integer> hashMap = new HashMap<>();
        SpringApplication.run(ServiceSmsApplication.class, args);

    }
}
