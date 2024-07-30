package com.atguigu.srb.core.controller.admin;


import com.atguigu.common.exception.Assert;
import com.atguigu.common.exception.BusinessException;
import com.atguigu.common.result.R;
import com.atguigu.common.result.ResponseEnum;
import com.atguigu.srb.core.pojo.entity.IntegralGrade;
import com.atguigu.srb.core.service.IntegralGradeService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.checkerframework.checker.signature.qual.FieldDescriptorForPrimitiveOrArrayInUnnamedPackage;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 积分等级表 前端控制器
 * </p>
 *
 * @author zxp
 * @since 2024-02-07
 */
@Api(tags = "积分等级管理")
@RestController
//@CrossOrigin
@RequestMapping("/admin/core/integralGrade")
@Slf4j
public class AdminIntegralGradeController {
    @Resource
    private IntegralGradeService integralGradeService;
    @ApiOperation("积分等级列表")
    @GetMapping("/list")
    public R listAll(){
        List<IntegralGrade> list = integralGradeService.list();
        log.info("hi i'm helen");
        log.warn("warning!!!");
        log.error("it's a error");
        R ok = R.ok();
        Map<String,Object> data=new HashMap<>();
        data.put("data",list);
        ok.data(data);
        return ok.message("获取列表成功");
//        return integralGradeService.list();
    }
    @ApiOperation(value = "根据id删除",notes = "逻辑删除记录")
    @DeleteMapping("/remove/{id}")
    public R removeById(
            @ApiParam(value = "数据id",example = "10",required = true)
            @PathVariable Long id){
        boolean b = integralGradeService.removeById(id);
        if(b)
            return R.ok().data("是否成功",b).message("删除成功");
        else
            return R.ok().data("是否成功",b).message("删除失败");
//        return integralGradeService.removeById(id);
    }
    @ApiOperation(value = "新增积分等级")
    @PostMapping("/save")
    public R save(
            @ApiParam(value = "积分等级对象",required = true)
            @RequestBody IntegralGrade integralGrade){
//        if(integralGrade.getBorrowAmount()==null)
//            throw new BusinessException(ResponseEnum.BORROW_AMOUNT_NULL_ERROR);
        Assert.notNull(integralGrade.getBorrowAmount(),ResponseEnum.BORROW_AMOUNT_NULL_ERROR);

        boolean save = integralGradeService.save(integralGrade);
        if(save)
            return R.ok().message("保存成功");
        else
            return R.error().message("保存失败");
    }
    @ApiOperation(value = "删除积分等级")
    @DeleteMapping("/newremove/{id}")
    public R deleteById(
            @ApiParam(value = "根据id删除",required = true,example = "10")
            @PathVariable Long id){
        boolean b = integralGradeService.removeById(id);
        if(b)
            return R.ok().message("删除成功");
        else return R.error().message("删除失败");
    }
    @ApiOperation("修改")
    @PutMapping("/update")
    public R update(
            @ApiParam(value = "修改",required = true)
            @RequestBody IntegralGrade integralGrade){
        LambdaUpdateWrapper<IntegralGrade> integralGradeLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        integralGradeLambdaUpdateWrapper.eq(IntegralGrade::getId,integralGrade.getId());
        boolean update = integralGradeService.update(integralGrade, integralGradeLambdaUpdateWrapper);
        if(update)
            return R.ok().message("修改成功");
        else
            return R.error().message("修改失败");
    }
    @ApiOperation("根据id查询")
    @GetMapping("/select/{id}")
    public R select(
            @ApiParam(value ="根据id查询")
            @PathVariable Long id){
        QueryWrapper<IntegralGrade> integralGradeQueryWrapper = new QueryWrapper<>();
        integralGradeQueryWrapper.eq("id",id);
        List<IntegralGrade> list = integralGradeService.list(integralGradeQueryWrapper);
//        IntegralGrade byId = integralGradeService.getById(id);
        if(list.size()>0)
            return R.ok().message("查询成功").data("record",list.get(0));
        else return R.error().message("查询失败");
    }
}

