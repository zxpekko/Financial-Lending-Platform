package com.atguigu.srb.core.service.impl;

import com.atguigu.srb.core.enums.BorrowerStatusEnum;
import com.atguigu.srb.core.enums.IntegralEnum;
import com.atguigu.srb.core.mapper.BorrowerAttachMapper;
import com.atguigu.srb.core.mapper.BorrowerMapper;
import com.atguigu.srb.core.mapper.UserInfoMapper;
import com.atguigu.srb.core.mapper.UserIntegralMapper;
import com.atguigu.srb.core.pojo.entity.Borrower;
import com.atguigu.srb.core.pojo.entity.BorrowerAttach;
import com.atguigu.srb.core.pojo.entity.UserInfo;
import com.atguigu.srb.core.pojo.entity.UserIntegral;
import com.atguigu.srb.core.pojo.vo.BorrowerApprovalVO;
import com.atguigu.srb.core.pojo.vo.BorrowerAttachVO;
import com.atguigu.srb.core.pojo.vo.BorrowerDetailVO;
import com.atguigu.srb.core.pojo.vo.BorrowerVO;
import com.atguigu.srb.core.service.BorrowerAttachService;
import com.atguigu.srb.core.service.BorrowerService;
import com.atguigu.srb.core.service.DictService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 借款人 服务实现类
 * </p>
 *
 * @author zxp
 * @since 2024-02-07
 */
@Service
public class BorrowerServiceImpl extends ServiceImpl<BorrowerMapper, Borrower> implements BorrowerService {
    @Resource
    private UserInfoMapper userInfoMapper;
    @Resource
    private BorrowerMapper borrowerMapper;
    @Resource
    private BorrowerAttachMapper borrowerAttachMapper;
    @Resource
    private DictService dictService;
    @Resource
    private BorrowerAttachService borrowerAttachService;
    @Resource
    private UserIntegralMapper userIntegralMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBorrowerVOByUserId(BorrowerVO borrowerVO, Long userId) {
        UserInfo userInfo = userInfoMapper.selectById(userId);
        //借款人数据插入
        Borrower borrower = new Borrower();
        BeanUtils.copyProperties(borrowerVO,borrower);
        borrower.setUserId(userId);
        borrower.setIdCard(userInfo.getIdCard());
        borrower.setName(userInfo.getName());
        borrower.setStatus(BorrowerStatusEnum.AUTH_RUN.getStatus());
        borrower.setMobile(userInfo.getMobile());
        borrowerMapper.insert(borrower);

        //附件上传
        for (BorrowerAttach attach : borrowerVO.getBorrowerAttachList()) {
            BorrowerAttach borrowerAttach = new BorrowerAttach();
            BeanUtils.copyProperties(attach,borrowerAttach);
            borrowerAttach.setBorrowerId(userId);
            borrowerAttachMapper.insert(borrowerAttach);
        }
        //更新userinfo中借款人的认证状态
        userInfo.setBorrowAuthStatus(BorrowerStatusEnum.AUTH_RUN.getStatus());
        userInfoMapper.updateById(userInfo);
    }

    @Override
    public Integer getStatus(Long userId) {
        QueryWrapper<Borrower> borrowerQueryWrapper = new QueryWrapper<>();
        borrowerQueryWrapper.eq("user_id",userId);
        Borrower borrower = borrowerMapper.selectOne(borrowerQueryWrapper);
        if(borrower!=null)
            return borrower.getStatus();
        else {
            return BorrowerStatusEnum.NO_AUTH.getStatus();
        }
    }

    @Override
    public Page<Borrower> getListByPageAndLimit(Page<Borrower> borrowerPage, String keyword) {
        if(StringUtils.isBlank(keyword)){
            return borrowerMapper.selectPage(borrowerPage,null);
        }
        QueryWrapper<Borrower> borrowerQueryWrapper = new QueryWrapper<>();
        borrowerQueryWrapper.like("name",keyword)
                        .or().like("id_card",keyword)
                        .or().like("mobile",keyword)
                        .orderByDesc("id");

        return borrowerMapper.selectPage(borrowerPage, borrowerQueryWrapper);
//        return borrowerPage1;
    }

    @Override
    public BorrowerDetailVO getBorrowerDetailVOById(Long id) {
        BorrowerDetailVO borrowerDetailVO = new BorrowerDetailVO();
        Borrower borrower = borrowerMapper.selectById(id);
        BeanUtils.copyProperties(borrower,borrowerDetailVO);
        borrowerDetailVO.setSex(borrower.getSex()==1?"男":"女");
        borrowerDetailVO.setMarry(borrower.getMarry()?"是":"否");

        //计算下拉列表选中内容
        String education = dictService.getNameByParentDictCodeAndValue("education", borrower.getEducation());
        String industry = dictService.getNameByParentDictCodeAndValue("moneyUse", borrower.getIndustry());
        String income = dictService.getNameByParentDictCodeAndValue("income", borrower.getIncome());
        String returnSource = dictService.getNameByParentDictCodeAndValue("returnSource", borrower.getReturnSource());
        String contactsRelation = dictService.getNameByParentDictCodeAndValue("relation", borrower.getContactsRelation());

        //设置下拉列表选中内容
        borrowerDetailVO.setEducation(education);
        borrowerDetailVO.setIndustry(industry);
        borrowerDetailVO.setIncome(income);
        borrowerDetailVO.setReturnSource(returnSource);
        borrowerDetailVO.setContactsRelation(contactsRelation);

        //审批状态
        String status = BorrowerStatusEnum.getMsgByStatus(borrower.getStatus());
        borrowerDetailVO.setStatus(status);

        //获取附件VO列表
        List<BorrowerAttachVO> borrowerAttachVOList =  borrowerAttachService.selectBorrowerAttachVOList(borrower.getUserId());
        borrowerDetailVO.setBorrowerAttachVOList(borrowerAttachVOList);

        return borrowerDetailVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approval(BorrowerApprovalVO borrowerApprovalVO) {
        //借款人认证状态
        Long borrowerId = borrowerApprovalVO.getBorrowerId();
//        QueryWrapper<Borrower> borrowerQueryWrapper = new QueryWrapper<>();
//        borrowerQueryWrapper.eq("user_id",borrowerId);
//        Borrower borrower = baseMapper.selectOne(borrowerQueryWrapper);
        Borrower borrower = baseMapper.selectById(borrowerId);
        borrower.setStatus(borrowerApprovalVO.getStatus());
        baseMapper.updateById(borrower);

        Long userId = borrower.getUserId();
        UserInfo userInfo = userInfoMapper.selectById(userId);

        //添加积分
        UserIntegral userIntegral = new UserIntegral();
        userIntegral.setUserId(userId);
        userIntegral.setIntegral(borrowerApprovalVO.getInfoIntegral());
        userIntegral.setContent("借款人基本信息");
        userIntegralMapper.insert(userIntegral);

        int curIntegral = userInfo.getIntegral() + borrowerApprovalVO.getInfoIntegral();
        if(borrowerApprovalVO.getIsIdCardOk()) {
            curIntegral += IntegralEnum.BORROWER_IDCARD.getIntegral();
            userIntegral = new UserIntegral();
            userIntegral.setUserId(userId);
            userIntegral.setIntegral(IntegralEnum.BORROWER_IDCARD.getIntegral());
            userIntegral.setContent(IntegralEnum.BORROWER_IDCARD.getMsg());
            userIntegralMapper.insert(userIntegral);
        }

        if(borrowerApprovalVO.getIsHouseOk()) {
            curIntegral += IntegralEnum.BORROWER_HOUSE.getIntegral();
            userIntegral = new UserIntegral();
            userIntegral.setUserId(userId);
            userIntegral.setIntegral(IntegralEnum.BORROWER_HOUSE.getIntegral());
            userIntegral.setContent(IntegralEnum.BORROWER_HOUSE.getMsg());
            userIntegralMapper.insert(userIntegral);
        }

        if(borrowerApprovalVO.getIsCarOk()) {
            curIntegral += IntegralEnum.BORROWER_CAR.getIntegral();
            userIntegral = new UserIntegral();
            userIntegral.setUserId(userId);
            userIntegral.setIntegral(IntegralEnum.BORROWER_CAR.getIntegral());
            userIntegral.setContent(IntegralEnum.BORROWER_CAR.getMsg());
            userIntegralMapper.insert(userIntegral);
        }

        userInfo.setIntegral(curIntegral);
        //修改审核状态
        userInfo.setBorrowAuthStatus(borrowerApprovalVO.getStatus());
        userInfoMapper.updateById(userInfo);
    }

}
