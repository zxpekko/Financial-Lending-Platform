package com.atguigu.srb.sms.client;

import com.atguigu.common.result.R;
import com.atguigu.srb.sms.client.fallback.CoreUserInfoClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Author:zxp
 * @Description:
 * @Date:13:14 2024/3/5
 */
@FeignClient(value = "service-core",fallback = CoreUserInfoClientFallback.class)
public interface CoreUserInfoClient {
    @GetMapping("/api/core/userInfo//checkMobile/{mobile}")
    boolean checkMobile(@PathVariable("mobile") String mobile);
}
