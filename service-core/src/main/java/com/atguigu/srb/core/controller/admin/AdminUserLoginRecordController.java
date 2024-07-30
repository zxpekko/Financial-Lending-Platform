package com.atguigu.srb.core.controller.admin;


import com.atguigu.common.result.R;
import com.atguigu.srb.core.pojo.entity.UserLoginRecord;
import com.atguigu.srb.core.service.UserLoginRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 用户登录记录表 前端控制器
 * </p>
 *
 * @author zxp
 * @since 2024-02-07
 */
@RestController
@RequestMapping("/admin/core/userLoginRecord")
@Api(tags = "日志管理")
@Slf4j
//@CrossOrigin
public class AdminUserLoginRecordController {
    @Resource
    private UserLoginRecordService userLoginRecordService;
    @ApiOperation("显示用户日志")
    @GetMapping("/logback/{id}")
    public R logback(
            @ApiParam(value = "用户id",required = true)
            @PathVariable("id") Long id){
        List<UserLoginRecord> records= userLoginRecordService.getBatchById(id);
        return R.ok().message("获取日志成功").data("data",records);
    }
}

