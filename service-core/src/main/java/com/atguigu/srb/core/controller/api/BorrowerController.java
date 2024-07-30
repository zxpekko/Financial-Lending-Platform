package com.atguigu.srb.core.controller.api;


import com.atguigu.common.result.R;
import com.atguigu.srb.base.util.JwtUtils;
import com.atguigu.srb.core.pojo.vo.BorrowerVO;
import com.atguigu.srb.core.service.BorrowerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 借款人 前端控制器
 * </p>
 *
 * @author zxp
 * @since 2024-02-07
 */
@RestController
@RequestMapping("/api/core/borrower")
@Slf4j
@Api(tags = "借款人信息填充")
public class BorrowerController {
    @Resource
    private BorrowerService borrowerService;
    @PostMapping("/auth/save")
    @ApiOperation("借款人信息填充")
    public R save(
            @ApiParam(value = "借款人表单信息",required = true)
            @RequestBody BorrowerVO borrowerVO,
            @ApiParam(value = "http请求头")
            HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        borrowerService.saveBorrowerVOByUserId(borrowerVO, userId);
        return R.ok().message("信息提交成功");
    }

}

