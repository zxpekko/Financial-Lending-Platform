package com.atguigu.srb.core.controller.api;


import com.atguigu.common.result.R;
import com.atguigu.srb.base.util.JwtUtils;
import com.atguigu.srb.core.pojo.entity.LendItemReturn;
import com.atguigu.srb.core.service.LendItemReturnService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 标的出借回款记录表 前端控制器
 * </p>
 *
 * @author zxp
 * @since 2024-02-07
 */
@Api(tags = "回款计划")
@RestController
@RequestMapping("/api/core/lendItemReturn")
@Slf4j
public class LendItemReturnController {
    @Resource
    private LendItemReturnService lendItemReturnService;
    @ApiOperation("获取回款计划列表")
    @GetMapping("/list/{lendId}")
    public R getLendItemReturnListByUserId(
            @ApiParam(value = "标的id",required = true)
            @PathVariable("lendId") Long lendId,
            HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        List<LendItemReturn> list= lendItemReturnService.getLendItemReturnListByUserId(lendId,userId);
        return  R.ok().data("list",list);
    }
}
