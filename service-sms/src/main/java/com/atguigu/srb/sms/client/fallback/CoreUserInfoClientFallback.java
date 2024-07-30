package com.atguigu.srb.sms.client.fallback;

import com.atguigu.srb.sms.client.CoreUserInfoClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.websocket.server.ServerEndpoint;

/**
 * @Author:zxp
 * @Description:
 * @Date:14:04 2024/3/5
 */
@Service
@Slf4j
public class CoreUserInfoClientFallback implements CoreUserInfoClient {

    @Override
    public boolean checkMobile(String mobile) {
        log.info("远程调用熔断");
        return false;
    }
}
