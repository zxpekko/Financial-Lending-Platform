package com.atguigu.srb.sms.service;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;

import java.util.Map;

/**
 * @Author:zxp
 * @Description:
 * @Date:15:51 2024/2/23
 */

public interface SmsService {
    void send(String mobile, String templateCode, Map<String,Object> param);
}
