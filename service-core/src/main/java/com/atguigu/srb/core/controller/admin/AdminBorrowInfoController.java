package com.atguigu.srb.core.controller.admin;


import com.atguigu.common.result.R;
import com.atguigu.srb.core.pojo.entity.BorrowInfo;
import com.atguigu.srb.core.pojo.vo.BorrowInfoApprovalVO;
import com.atguigu.srb.core.service.BorrowInfoService;
import com.atguigu.srb.core.service.BorrowerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 借款信息表 前端控制器
 * </p>
 *
 * @author zxp
 * @since 2024-02-07
 */
@RestController
@Api(tags = "借款人信息管理与审核")
@RequestMapping("/admin/core/borrowInfo")
@Slf4j
public class AdminBorrowInfoController {
    @Resource
    private BorrowerService borrowerService;
    @Resource
    private BorrowInfoService borrowInfoService;
    @ApiOperation("借款人申请信息列表获取")
    @GetMapping("/borrowerInfo")
    public R getBorrowerInfo(){
        List<BorrowInfo> borrowInfos= borrowInfoService.getBorrowerInfo();
        return R.ok().data("borrowerInfo",borrowInfos);
    }
    @ApiOperation("获取借款详细信息")
    @GetMapping("/show/{id}")
    public R show(
            @ApiParam(value = "借款id", required = true)
            @PathVariable Long id) {
        Map<String, Object> borrowInfoDetail = borrowInfoService.getBorrowInfoDetail(id);
        return R.ok().data("borrowInfoDetail", borrowInfoDetail);
    }
    @ApiOperation("审批借款信息")
    @PostMapping("/approval")
    public R approval(@RequestBody BorrowInfoApprovalVO borrowInfoApprovalVO) {

        borrowInfoService.approval(borrowInfoApprovalVO);
        return R.ok().message("审批完成");
    }

}

