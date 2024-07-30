package com.atguigu.srb.core.service.impl;

import com.atguigu.srb.core.mapper.LendItemMapper;
import com.atguigu.srb.core.mapper.LendItemReturnMapper;
import com.atguigu.srb.core.mapper.LendMapper;
import com.atguigu.srb.core.mapper.LendReturnMapper;
import com.atguigu.srb.core.pojo.entity.Lend;
import com.atguigu.srb.core.pojo.entity.LendItem;
import com.atguigu.srb.core.pojo.entity.LendItemReturn;
import com.atguigu.srb.core.pojo.entity.LendReturn;
import com.atguigu.srb.core.service.LendItemReturnService;
import com.atguigu.srb.core.service.UserBindService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的出借回款记录表 服务实现类
 * </p>
 *
 * @author zxp
 * @since 2024-02-07
 */
@Service
public class LendItemReturnServiceImpl extends ServiceImpl<LendItemReturnMapper, LendItemReturn> implements LendItemReturnService {
    @Resource
    private LendMapper lendMapper;
    @Resource
    private LendReturnMapper lendReturnMapper;
    @Resource
    private LendItemMapper lendItemMapper;
    @Resource
    private UserBindService userBindService;
    @Override
    public List<LendItemReturn> getLendItemReturnListByUserId(Long lendId,Long userId) {
        QueryWrapper<LendItemReturn> lendItemReturnQueryWrapper = new QueryWrapper<>();
        lendItemReturnQueryWrapper.eq("invest_user_id",userId)
                .eq("lend_id",lendId);
        List<LendItemReturn> list = baseMapper.selectList(lendItemReturnQueryWrapper);
        return list;
    }

    @Override
    public List<Map<String, Object>> addReturnDetail(Long lendReturnId) {
        List<LendItemReturn> lendItemReturnList = this.getLendItemReturnByLendReutrnId(lendReturnId);
        List<Map<String, Object>> list=new ArrayList<>();
        LendReturn lendReturn = lendReturnMapper.selectById(lendReturnId);
        Lend lend = lendMapper.selectById(lendReturn.getLendId());
        for(LendItemReturn lendItemReturn:lendItemReturnList){
            Map<String,Object> map=new HashMap<>();
            map.put("agentProjectCode",lend.getLendNo());
            LendItem lendItem = lendItemMapper.selectById(lendItemReturn.getLendItemId());
            map.put("voteBillNo",lendItem.getLendItemNo());
            Long investUserId = lendItemReturn.getInvestUserId();
            map.put("toBindCode",userBindService.getBindCodeByUserId(investUserId));
            //还款金额
            map.put("transitAmt", lendItemReturn.getTotal());
            //还款本金
            map.put("baseAmt", lendItemReturn.getPrincipal());
            //还款利息
            map.put("benifitAmt", lendItemReturn.getInterest());
            //商户手续费
            map.put("feeAmt", new BigDecimal("0"));
            list.add(map);
        }
        return list;
    }

    @Override
    public List<LendItemReturn> getLendItemReturnByLendReutrnId(Long lendReturnId) {
        QueryWrapper<LendItemReturn> lendItemReturnQueryWrapper = new QueryWrapper<>();
        lendItemReturnQueryWrapper.eq("lend_return_id",lendReturnId);
        return baseMapper.selectList(lendItemReturnQueryWrapper);
    }
}
