package com.atguigu.srb.core.service.impl;

import com.atguigu.srb.core.mapper.UserLoginRecordMapper;
import com.atguigu.srb.core.pojo.entity.UserLoginRecord;
import com.atguigu.srb.core.service.UserLoginRecordService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 用户登录记录表 服务实现类
 * </p>
 *
 * @author zxp
 * @since 2024-02-07
 */
@Service
public class UserLoginRecordServiceImpl extends ServiceImpl<UserLoginRecordMapper, UserLoginRecord> implements UserLoginRecordService {
    @Resource
    private UserLoginRecordMapper userLoginRecordMapper;

    @Override
    public List<UserLoginRecord> getBatchById(Long id) {
        QueryWrapper<UserLoginRecord> userLoginRecordQueryWrapper = new QueryWrapper<>();
        userLoginRecordQueryWrapper.eq("user_id",id).orderByDesc("id")
                .last("limit 50");
        List<UserLoginRecord> records = userLoginRecordMapper.selectList(userLoginRecordQueryWrapper);
        return records;
    }

    @Override
    public UserLoginRecord latestUserLoginRecordByUserId(Long userId) {
        QueryWrapper<UserLoginRecord> userLoginRecordQueryWrapper = new QueryWrapper<>();
        userLoginRecordQueryWrapper.eq("user_id",userId).orderByDesc("id").last("limit 1");
        return baseMapper.selectOne(userLoginRecordQueryWrapper);
//        return null;
    }
}
