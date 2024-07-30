package com.atguigu.srb.core.controller.api;


import com.atguigu.common.result.R;
import com.atguigu.srb.base.util.JwtUtils;
import com.atguigu.srb.core.pojo.entity.BorrowInfo;
import com.atguigu.srb.core.service.BorrowInfoService;
import com.atguigu.srb.core.service.BorrowerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

/**
 * <p>
 * 借款信息表 前端控制器
 * </p>
 *
 * @author zxp
 * @since 2024-02-07
 */
@RestController
@Api(tags = "借款人信息")
@RequestMapping("/api/core/borrowInfo")
@Slf4j
public class BorrowInfoController {
    @Resource
    private BorrowerService borrowerService;
    @Resource
    private BorrowInfoService borrowInfoService;
    @GetMapping("/auth/getStatus")
    @ApiOperation("获取借款人审核状态")
    public R getStatus(HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        log.info("开始执行"+userId);
        Integer status=borrowerService.getStatus(userId);
        return R.ok().data("status",status);
    }
    @ApiOperation("获取借款额度")
    @GetMapping("/auth/getBorrowAmount")
    public R getBorrowAmount(HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        BigDecimal borrowAmount = borrowInfoService.getBorrowAmount(userId);
        return R.ok().data("borrowAmount", borrowAmount);
    }
    @ApiOperation("提交借款申请")
    @PostMapping("/auth/save")
    public R save(
            @ApiParam(value = "借款信息收集",required = true)
            @RequestBody BorrowInfo borrowInfo,HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        borrowInfoService.saveBorrowInfo(borrowInfo,userId);
        return R.ok().message("借款提交完成");
    }
    @ApiOperation("借款状态查询")
    @GetMapping("/auth/borrowStatus")
    public R borrowStatus(
            @ApiParam("前端的cookie")
            HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        Integer status=borrowInfoService.getBorrowStatus(userId);
        return R.ok().data("status",status);
    }
}

