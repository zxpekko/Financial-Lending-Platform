package com.atguigu.srb.core.controller.admin;


import com.atguigu.common.result.R;
import com.atguigu.srb.core.pojo.entity.LendReturn;
import com.atguigu.srb.core.service.LendReturnService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 还款记录表 前端控制器
 * </p>
 *
 * @author zxp
 * @since 2024-02-07
 */
@Api(tags = "还款计划")
@RestController
@RequestMapping("/admin/core/lendReturn")
@Slf4j
public class AdminLendReturnController {
    @Resource
    private LendReturnService lendReturnService;
    @ApiOperation("获取还款计划列表")
    @GetMapping("/list/{lendId}")
    public R lendReturn(@ApiParam(value = "还款计划对应的lendId",required = true)
                        @PathVariable("lendId") Long lendId){
        List<LendReturn> lendReturnList= lendReturnService.getListByLendId(lendId);
        return R.ok().data("list",lendReturnList);
    }

}

