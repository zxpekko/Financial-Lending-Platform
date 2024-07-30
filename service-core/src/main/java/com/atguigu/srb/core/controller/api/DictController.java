package com.atguigu.srb.core.controller.api;


import com.atguigu.common.result.R;
import com.atguigu.srb.core.pojo.entity.Dict;
import com.atguigu.srb.core.service.DictService;
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
 * 数据字典 前端控制器
 * </p>
 *
 * @author zxp
 * @since 2024-02-07
 */
@RestController
@RequestMapping("api/core/dict")
@Api(tags = "字典信息获取")
@Slf4j
public class DictController {
    @Resource
    private DictService dictService;
    @ApiOperation("根据下拉列表获取数据字典")
    @GetMapping("/getDict/{dictCode}")
    public R getDict(
            @ApiParam(value = "dict编码",required = true)
            @PathVariable("dictCode")
                     String dictCode){
        List<Dict> result= dictService.getChilden(dictCode);
        return R.ok().message("获取数据字典成功").data("data",result);
    }
}

