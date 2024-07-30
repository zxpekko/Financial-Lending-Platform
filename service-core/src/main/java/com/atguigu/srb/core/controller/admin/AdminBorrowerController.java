package com.atguigu.srb.core.controller.admin;


import com.atguigu.common.result.R;
import com.atguigu.srb.core.pojo.entity.Borrower;
import com.atguigu.srb.core.pojo.vo.BorrowerApprovalVO;
import com.atguigu.srb.core.pojo.vo.BorrowerDetailVO;
import com.atguigu.srb.core.service.BorrowerService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 * 借款人 前端控制器
 * </p>
 *
 * @author zxp
 * @since 2024-02-07
 */
@RestController
@RequestMapping("/admin/core/borrower")
@Slf4j
@Api(tags = "借款人管理")
public class AdminBorrowerController {
    @Resource
    private BorrowerService borrowerService;
//    @Resource
//    private BorrowerMapper borrowerMapper;

    @ApiOperation("获取借款人分页列表")
    @GetMapping("/list/{page}/{limit}")
    public R getList(
            @ApiParam(value = "当前页码",required = true)
            @PathVariable("page") Long page,
                     @ApiParam(value = "每页显示数目",required = true)
                     @PathVariable("limit") Long limit,
                     @ApiParam(value = "关键字",required = false)
                     @RequestParam(required = false) String keyword){
        Page<Borrower> borrowerPage = new Page<>(page,limit);
        Page<Borrower> result=borrowerService.getListByPageAndLimit(borrowerPage,keyword);
//        Page<Borrower> borrowerPage1 = borrowerMapper.selectPage(borrowerPage);
        return R.ok().data("data",result);
    }
    @ApiOperation("获取借款人信息")
    @GetMapping("/show/{id}")
    public R show(
            @ApiParam(value = "借款人id",required = true)
            @PathVariable("id") Long id){
        BorrowerDetailVO borrowerDetailVO = borrowerService.getBorrowerDetailVOById(id);
        return R.ok().data("borrowerDetailVO",borrowerDetailVO);
    }
    @ApiOperation("借款额度审批")
    @PostMapping("/approval")
    public R approval(@RequestBody BorrowerApprovalVO borrowerApprovalVO) {
        borrowerService.approval(borrowerApprovalVO);
        return R.ok().message("审批完成");
    }

}

