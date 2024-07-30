package com.atguigu.srb.core.controller.admin;


import com.atguigu.common.exception.Assert;
import com.atguigu.common.result.R;
import com.atguigu.common.result.ResponseEnum;
import com.atguigu.common.util.RegexValidateUtils;
import com.atguigu.srb.base.util.JwtUtils;
import com.atguigu.srb.core.pojo.entity.UserInfo;
import com.atguigu.srb.core.pojo.entity.UserLoginRecord;
import com.atguigu.srb.core.pojo.query.UserInfoQuery;
import com.atguigu.srb.core.pojo.vo.LoginVO;
import com.atguigu.srb.core.pojo.vo.RegisterVO;
import com.atguigu.srb.core.pojo.vo.UserInfoVO;
import com.atguigu.srb.core.service.UserInfoService;
import com.atguigu.srb.core.service.UserLoginRecordService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.License;
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
@RequestMapping("/admin/core/userInfo")
@Api(tags = "会员管理")
@Slf4j
//@CrossOrigin
public class AdminUserInfoController {
    @Resource
    private UserInfoService userInfoService;


    @ApiOperation("获取会员分页列表")
    @GetMapping("/list/{page}/{limit}")
    public R listByPage(@ApiParam(value = "当前页码",required = true)
                            @PathVariable("page") Long page,
                        @ApiParam(value = "每页记录数")
                        @PathVariable("limit")
                        Long limit,
                        @ApiParam(value = "查询对象",required = false)
                        UserInfoQuery userInfoQuery
                                  ){
        Page<UserInfo> userInfoPage = new Page<UserInfo>(page,limit);
        IPage<UserInfo> pageModel = userInfoService.listPage(userInfoPage, userInfoQuery);
        return R.ok().data("pageModel",pageModel);
    }

    @ApiOperation("修改锁定状态")
    @PutMapping("/lock/{id}/{status}")
    public R modifyStatus(
            @ApiParam(value = "修改的id",required = true)
            @PathVariable("id") Long id,
            @ApiParam(value = "当前状态",required = true)
            @PathVariable("status") Integer status){
        userInfoService.lock(id,status);
        return  R.ok().message(status==1?"锁定成功":"解锁成功");
    }



}

