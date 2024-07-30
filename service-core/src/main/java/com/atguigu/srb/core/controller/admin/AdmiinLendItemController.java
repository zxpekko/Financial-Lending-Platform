package com.atguigu.srb.core.controller.admin;


import com.atguigu.common.result.R;
import com.atguigu.srb.core.pojo.entity.LendItem;
import com.atguigu.srb.core.service.LendItemService;
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
 * 标的出借记录表 前端控制器
 * </p>
 *
 * @author zxp
 * @since 2024-02-07
 */
@Api(tags = "管理标的投资")
@RestController
@RequestMapping("/admin/core/lendItem")
@Slf4j
public class AdmiinLendItemController {
    @Resource
    LendItemService lendItemService;
    @ApiOperation("获取列表")
    @GetMapping("/list/{lendId}")
    public R list(
            @ApiParam(value = "标的id", required = true)
            @PathVariable Long lendId) {
        List<LendItem> lendItems = lendItemService.selectByLendId(lendId, 1);
        return R.ok().data("list",lendItems);
    }
}

