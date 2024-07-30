package com.atguigu.srb.core.service.impl;

import com.atguigu.common.exception.Assert;
import com.atguigu.common.result.ResponseEnum;
import com.atguigu.common.util.MD5;
import com.atguigu.srb.base.util.JwtUtils;
import com.atguigu.srb.core.mapper.UserAccountMapper;
import com.atguigu.srb.core.mapper.UserLoginRecordMapper;
import com.atguigu.srb.core.pojo.entity.UserAccount;
import com.atguigu.srb.core.pojo.entity.UserInfo;
import com.atguigu.srb.core.mapper.UserInfoMapper;
import com.atguigu.srb.core.pojo.entity.UserLoginRecord;
import com.atguigu.srb.core.pojo.query.UserInfoQuery;
import com.atguigu.srb.core.pojo.vo.LoginVO;
import com.atguigu.srb.core.pojo.vo.RegisterVO;
import com.atguigu.srb.core.pojo.vo.UserIndexVO;
import com.atguigu.srb.core.pojo.vo.UserInfoVO;
import com.atguigu.srb.core.service.UserAccountService;
import com.atguigu.srb.core.service.UserInfoService;
import com.atguigu.srb.core.service.UserLoginRecordService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * <p>
 * 用户基本信息 服务实现类
 * </p>
 *
 * @author zxp
 * @since 2024-02-07
 */
@Service
@Slf4j
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {
    @Resource
    private UserInfoMapper userInfoMapper;
    @Resource
    private UserAccountMapper userAccountMapper;
    @Resource
    private UserLoginRecordMapper userLoginRecordMapper;
    @Resource
    private UserAccountService userAccountService;
    @Resource
    private UserLoginRecordService userLoginRecordService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void register(RegisterVO registerVO) {
        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper();
        userInfoQueryWrapper.eq("mobile",registerVO.getMobile()).eq("user_type",registerVO.getUserType());
        Integer integer = userInfoMapper.selectCount(userInfoQueryWrapper);
        Assert.isTrue(integer==0, ResponseEnum.MOBILE_EXIST_ERROR);//判断手机号是否被注册
        //插入用户记录user_info
        UserInfo userInfo = new UserInfo();
        userInfo.setMobile(registerVO.getMobile());
        userInfo.setUserType(registerVO.getUserType());
        userInfo.setPassword(MD5.encrypt(registerVO.getPassword()));
        userInfo.setNickName(registerVO.getMobile());
        userInfo.setName(registerVO.getMobile());
        userInfo.setStatus(UserInfo.STATUS_NORMAL);
        userInfo.setHeadImg(UserInfo.USER_AVATAR);
        userInfoMapper.insert(userInfo);

        //插入用户账户
        UserAccount userAccount = new UserAccount();
        userAccount.setUserId(userInfo.getId());
        userAccountMapper.insert(userAccount);

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserInfoVO login(LoginVO loginVO, String ip) {
        //用户名是否存在
        String mobile = loginVO.getMobile();
        String password = loginVO.getPassword();
        Integer userType = loginVO.getUserType();
        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        userInfoQueryWrapper.eq("mobile",mobile).eq("user_type",userType);
        Integer integer = userInfoMapper.selectCount(userInfoQueryWrapper);
        log.info("integer"+integer);
        Assert.isTrue(integer>0,ResponseEnum.LOGIN_MOBILE_ERROR);
        //密码是否正确
        UserInfo userInfo = userInfoMapper.selectOne(userInfoQueryWrapper);
        String password1 = userInfo.getPassword();
        Assert.equals(MD5.encrypt(password),password1, ResponseEnum.LOGIN_PASSWORD_ERROR);

        //用户是否被禁用
        Integer status = userInfo.getStatus();
        Assert.equals(status,UserInfo.STATUS_NORMAL, ResponseEnum.LOGIN_LOKED_ERROR);
        //记录登录日志
        UserLoginRecord userLoginRecord = new UserLoginRecord();
        userLoginRecord.setUserId(userInfo.getId());
        userLoginRecord.setIp(ip);
        userLoginRecordMapper.insert(userLoginRecord);
        //生成jwt token
        String token = JwtUtils.createToken(userInfo.getId(), userInfo.getName());
        //组装UserInfoVO对象
        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setToken(token);
        userInfoVO.setUserType(userInfo.getUserType());
        userInfoVO.setName(userInfo.getName());
        userInfoVO.setNickName(userInfo.getNickName());
        userInfoVO.setMobile(userInfo.getMobile());
        userInfoVO.setHeadImg(userInfo.getHeadImg());

        return userInfoVO;
    }

    @Override
    public IPage<UserInfo> listPage(Page<UserInfo> userInfoPage, UserInfoQuery userInfoQuery) {
        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        if(userInfoQuery==null)
            return userInfoMapper.selectPage(userInfoPage,null);
        String mobile = userInfoQuery.getMobile();
        Integer status = userInfoQuery.getStatus();
        Integer userType = userInfoQuery.getUserType();
        userInfoQueryWrapper.eq(StringUtils.isNotBlank(mobile),"mobile",mobile)
                .eq(status!=null,"status",status)
                .eq(userType!=null,"user_type",userType);
        Page<UserInfo> userInfoPage1 = userInfoMapper.selectPage(userInfoPage, userInfoQueryWrapper);
        return userInfoPage1;
    }

    @Override
    public void lock(Long id, Integer status) {
        UpdateWrapper<UserInfo> userInfoUpdateWrapper = new UpdateWrapper<>();
        UserInfo userInfo = new UserInfo();
        userInfo.setId(id);
        userInfo.setStatus(1-status);
        userInfoMapper.updateById(userInfo);
    }

    @Override
    public boolean checkMobile(String mobile) {
        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        userInfoQueryWrapper.eq("mobile",mobile);
        Integer result = userInfoMapper.selectCount(userInfoQueryWrapper);
        return result > 0;
    }

    @Override
    public UserIndexVO selectVOByUserId(Long userId) {
        UserIndexVO userIndexVO = new UserIndexVO();
        userIndexVO.setUserId(userId);
        UserInfo userInfo = baseMapper.selectById(userId);
        UserAccount userAccountByUserId = userAccountService.getUserAccountByUserId(userId);
        UserLoginRecord userLoginRecord = userLoginRecordService.latestUserLoginRecordByUserId(userId);
        userIndexVO.setName(userInfo.getName());
        userIndexVO.setNickName(userInfo.getNickName());
        userIndexVO.setUserType(userInfo.getUserType());
        userIndexVO.setHeadImg(userInfo.getHeadImg());
        userIndexVO.setBindStatus(userInfo.getBindStatus());
        userIndexVO.setAmount(userAccountByUserId.getAmount());
        userIndexVO.setFreezeAmount(userAccountByUserId.getFreezeAmount());
        userIndexVO.setLastLoginTime(userLoginRecord.getCreateTime());
        return userIndexVO;
    }

    @Override
    public String getMobileByBindCode(String bindCode) {
        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        userInfoQueryWrapper.eq("bind_code", bindCode);
        UserInfo userInfo = baseMapper.selectOne(userInfoQueryWrapper);
        return userInfo.getMobile();

    }
}

