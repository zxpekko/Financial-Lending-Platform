package com.atguigu.srb.sms.controller.api;

import com.atguigu.common.exception.Assert;
import com.atguigu.common.result.R;
import com.atguigu.common.result.ResponseEnum;
import com.atguigu.common.util.RandomUtils;
import com.atguigu.common.util.RegexValidateUtils;
import com.atguigu.srb.sms.client.CoreUserInfoClient;
import com.atguigu.srb.sms.service.SmsService;
import com.atguigu.srb.sms.util.SmsProperties;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * @Author:zxp
 * @Description:
 * @Date:16:15 2024/2/23
 */
@RestController
@RequestMapping("/api/sms")
@Api(tags = "短信管理")
//@CrossOrigin //跨域
@Slf4j
public class ApiSmsController {
    @Resource
    private SmsService smsService;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private CoreUserInfoClient coreUserInfoClient;
    @GetMapping("/send/{mobile}")
    @ApiOperation("获取验证码")
    public R send(
            @ApiParam(value = "手机号码",required = true)
            @PathVariable String mobile){
        Assert.notEmpty(mobile, ResponseEnum.MOBILE_NULL_ERROR);
        Assert.isTrue(RegexValidateUtils.checkCellphone(mobile),ResponseEnum.MOBILE_ERROR);
//        Object o = redisTemplate.opsForValue().get("srb:sms:code"+mobile);
//        if(o!=null){
//           return R.error().message("发送过于频繁");
//        }
        boolean result = coreUserInfoClient.checkMobile(mobile);
//        boolean isExist = (boolean)r.getData().get("isExist");
        Assert.isTrue(!result,ResponseEnum.MOBILE_EXIST_ERROR);
        //判断手机号是否注册

        HashMap<String, Object> stringObjectHashMap = new HashMap<>();
        String sixBitRandom = RandomUtils.getSixBitRandom();
        stringObjectHashMap.put("code",sixBitRandom);

        smsService.send(mobile, SmsProperties.TEMPLATE_CODE,stringObjectHashMap);
        redisTemplate.opsForValue().set("srb:sms:code"+mobile,sixBitRandom,5, TimeUnit.MINUTES);
        return R.ok().message("手机验证码发送成功，有效期五分钟");
    }
}
