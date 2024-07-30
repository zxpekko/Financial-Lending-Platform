package com.atguigu.srb.core.controller.api;


import com.atguigu.common.exception.Assert;
import com.atguigu.common.result.R;
import com.atguigu.common.result.ResponseEnum;
import com.atguigu.common.util.RegexValidateUtils;
import com.atguigu.srb.base.util.JwtUtils;
import com.atguigu.srb.core.pojo.vo.LoginVO;
import com.atguigu.srb.core.pojo.vo.RegisterVO;
import com.atguigu.srb.core.pojo.vo.UserIndexVO;
import com.atguigu.srb.core.pojo.vo.UserInfoVO;
import com.atguigu.srb.core.service.UserInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 用户基本信息 前端控制器
 * </p>
 *
 * @author zxp
 * @since 2024-02-07
 */
@RestController
@RequestMapping("/api/core/userInfo")
@Api(tags = "会员接口")
@Slf4j
//@CrossOrigin
public class UserInfoController {
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private UserInfoService userInfoService;
    @PostMapping("/register")
    public R register(@RequestBody RegisterVO registerVO){
        //校验验证码
        String code = (String)redisTemplate.opsForValue().get("srb:sms:code" + registerVO.getMobile());
        String mobile = registerVO.getMobile();
        String code1 = registerVO.getCode();
        String password = registerVO.getPassword();
        Assert.isTrue(RegexValidateUtils.checkCellphone(mobile),ResponseEnum.MOBILE_ERROR);
        Assert.notEmpty(code1,ResponseEnum.CODE_NULL_ERROR);
        Assert.notEmpty(password,ResponseEnum.PASSWORD_NULL_ERROR);
//        if(!code.equals(registerVO.getCode()))
//            return R.error().message("验证码不正确");
        Assert.equals(code,registerVO.getCode(), ResponseEnum.CODE_ERROR);
        //注册
        userInfoService.register(registerVO);
        //返回结果
        return  R.ok().message("注册成功");
    }

    @ApiOperation("登录")
    @PostMapping("/login")
    public R Login(
            @ApiParam(value = "登录用户",required = true)
            @RequestBody LoginVO loginVO, HttpServletRequest httpServletRequest){
        String mobile = loginVO.getMobile();
        String password = loginVO.getPassword();
        Assert.notEmpty(mobile,ResponseEnum.MOBILE_NULL_ERROR);
        Assert.notEmpty(password,ResponseEnum.PASSWORD_NULL_ERROR);
        String ip = httpServletRequest.getRemoteAddr();

        UserInfoVO userInfoVO=userInfoService.login(loginVO,ip);
        return R.ok().message("登录成功").data("userInfo",userInfoVO);
    }
    @ApiOperation("校验令牌")
    @GetMapping("/checkToken")
    public R checkToken(HttpServletRequest request) {

        String token = request.getHeader("token");
        boolean result = JwtUtils.checkToken(token);

        if(result){
            return R.ok();
        }else{
            //LOGIN_AUTH_ERROR(-211, "未登录"),
            return R.setResult(ResponseEnum.LOGIN_AUTH_ERROR);
        }
    }

    @ApiOperation("校验手机号是否注册")
    @GetMapping("/checkMobile/{mobile}")
    public boolean checkMobile(@PathVariable("mobile") String mobile){
        boolean result=userInfoService.checkMobile(mobile);
        return result;
    }
    @ApiOperation("用户中心信息展示")
    @GetMapping("/auth/getIndexUserInfo")
    public R getIndexUserInfo(HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        UserIndexVO userIndexVO= userInfoService.selectVOByUserId(userId);
        return R.ok().data("userIndexVO",userIndexVO);
    }
}

