package com.atguigu.srb.core.controller;


import com.atguigu.srb.core.pojo.entity.IntegralGrade;
import com.atguigu.srb.core.service.IntegralGradeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 积分等级表 前端控制器
 * </p>
 *
 * @author zxp
 * @since 2024-02-07
 */
@Api(tags = "网站等级积分接口")
@RestController
@RequestMapping("/api/core/integralGrade")
public class IntegralGradeController {
    @Resource
    private IntegralGradeService integralGradeService;
    @GetMapping("/test")
    @ApiOperation(value = "测试接口")
    public List<IntegralGrade> list(){
        return integralGradeService.list();
    }

}

