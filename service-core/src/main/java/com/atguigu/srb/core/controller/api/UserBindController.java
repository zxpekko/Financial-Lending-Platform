package com.atguigu.srb.core.controller.api;


import com.alibaba.fastjson.JSON;
import com.atguigu.common.result.R;
import com.atguigu.srb.base.util.JwtUtils;
import com.atguigu.srb.core.hfb.RequestHelper;
import com.atguigu.srb.core.pojo.vo.UserBindVO;
import com.atguigu.srb.core.service.UserBindService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * <p>
 * 用户绑定表 前端控制器
 * </p>
 *
 * @author zxp
 * @since 2024-02-07
 */
@Api(tags = "会员账号绑定")
@RestController
@RequestMapping("/api/core/userBind")
@Slf4j
public class UserBindController {
    @Resource
    private UserBindService userBindService;

    @PostMapping("/auth/bind")
    @ApiOperation("账户绑定提交数据")
    public R bind(@RequestBody UserBindVO userBindVO, HttpServletRequest request){
        //验证是否登录
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
//        boolean b = JwtUtils.checkToken(token);

        //根据userId绑定
        String formStr=userBindService.commitBindUser(userBindVO,userId);

        return R.ok().data("formStr",formStr);
    }
    @ApiOperation("异步通知")
    @PostMapping("/notify")
    public String notify(HttpServletRequest request){
        Map<String, Object> paramMap = RequestHelper.switchMap(request.getParameterMap());
        log.info("用户账号绑定异步回调：" + JSON.toJSONString(paramMap));
        boolean signEquals = RequestHelper.isSignEquals(paramMap);
        if(!signEquals){
            log.error("用户信息不正确，拒绝绑定");
            return "error";
        }
        log.info("验签成功，开始绑定用户");
        userBindService.notify(paramMap);
        return "success";
    }
}

