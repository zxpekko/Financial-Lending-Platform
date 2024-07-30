package com.atguigu.srb.core.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.common.exception.Assert;
import com.atguigu.common.result.ResponseEnum;
import com.atguigu.srb.core.enums.TransTypeEnum;
import com.atguigu.srb.core.hfb.FormHelper;
import com.atguigu.srb.core.hfb.HfbConst;
import com.atguigu.srb.core.hfb.RequestHelper;
import com.atguigu.srb.core.mapper.TransFlowMapper;
import com.atguigu.srb.core.mapper.UserAccountMapper;
import com.atguigu.srb.core.mapper.UserInfoMapper;
import com.atguigu.srb.core.pojo.bo.TransFlowBO;
import com.atguigu.srb.core.pojo.entity.TransFlow;
import com.atguigu.srb.core.pojo.entity.UserAccount;
import com.atguigu.srb.core.pojo.entity.UserInfo;
import com.atguigu.srb.core.service.TransFlowService;
import com.atguigu.srb.core.service.UserAccountService;
import com.atguigu.srb.core.service.UserBindService;
import com.atguigu.srb.core.service.UserInfoService;
import com.atguigu.srb.core.util.LendNoUtils;
import com.atguigu.srb.dto.SmsDTO;
import com.atguigu.srb.rabbitutil.constant.MQConst;
import com.atguigu.srb.rabbitutil.service.MQService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 用户账户 服务实现类
 * </p>
 *
 * @author zxp
 * @since 2024-02-07
 */
@Service
@Slf4j
public class UserAccountServiceImpl extends ServiceImpl<UserAccountMapper, UserAccount> implements UserAccountService {
    @Resource
    private UserInfoMapper userInfoMapper;
    @Resource
    private TransFlowService transFlowService;
    @Resource
    private TransFlowMapper transFlowMapper;
    @Resource
    private UserBindService userBindService;

    @Resource
    private UserAccountService userAccountService;
    @Resource
    private UserAccountMapper userAccountMapper;
    @Resource
    private UserInfoService userInfoService;
    @Resource
    private MQService mqService;

    @Override
    public String commitCharge(BigDecimal chargeAmt, Long userId) {

        UserInfo userInfo = userInfoMapper.selectById(userId);
        String bindCode = userInfo.getBindCode();
        //判断账户绑定状态
        Assert.notEmpty(bindCode, ResponseEnum.USER_NO_BIND_ERROR);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("agentId", HfbConst.AGENT_ID);
        paramMap.put("agentBillNo", LendNoUtils.getNo());
        paramMap.put("bindCode", bindCode);
        paramMap.put("chargeAmt", chargeAmt);
        paramMap.put("feeAmt", new BigDecimal("0"));
        paramMap.put("notifyUrl", HfbConst.RECHARGE_NOTIFY_URL);//检查常量是否正确
        paramMap.put("returnUrl", HfbConst.RECHARGE_RETURN_URL);
        paramMap.put("timestamp", RequestHelper.getTimestamp());
        String sign = RequestHelper.getSign(paramMap);
        paramMap.put("sign", sign);

        //构建充值自动提交表单
        String formStr = FormHelper.buildForm(HfbConst.RECHARGE_URL, paramMap);
        return formStr;
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String notify(Map<String, Object> paramMap) {
        //尽心幂等性判断
        String agentBillNo = (String)paramMap.get("agentBillNo");
        if(!judgeExsit(agentBillNo)){
            log.info("充值成功：" + JSONObject.toJSONString(paramMap));

            String bindCode = (String)paramMap.get("bindCode"); //充值人绑定协议号
            String chargeAmt = (String)paramMap.get("chargeAmt"); //充值金额

            //优化
            baseMapper.updateAccount(bindCode, new BigDecimal(chargeAmt), new BigDecimal(0));

            //增加交易流水
            //TODO


            TransFlowBO transFlowBO = new TransFlowBO(agentBillNo, bindCode, new BigDecimal(chargeAmt), TransTypeEnum.RECHARGE, "充值");
            transFlowService.saveTransFlow(transFlowBO);

            //发消息
            log.info("发消息");
            String mobile = userInfoService.getMobileByBindCode(bindCode);
            SmsDTO smsDTO = new SmsDTO();
            smsDTO.setMobile(mobile);
            smsDTO.setMessage("充值成功");
            mqService.sendMessage(MQConst.EXCHANGE_TOPIC_SMS, MQConst.ROUTING_SMS_ITEM, smsDTO);

            return "success";
        }

        log.info("幂等性返回");
        return "success";

    }

    @Override
    public BigDecimal getAccount(Long userId) {
        QueryWrapper<UserAccount> userAccountQueryWrapper = new QueryWrapper<>();
        userAccountQueryWrapper.eq("user_id",userId);
        UserAccount userAccount = baseMapper.selectOne(userAccountQueryWrapper);

        return userAccount.getAmount();
    }

    @Override
    public String commitWithdraw(BigDecimal fetchAmt, Long userId) {

        //账户可用余额充足：当前用户的余额 >= 当前用户的提现金额
        BigDecimal amount = userAccountService.getAccount(userId);//获取当前用户的账户余额
        Assert.isTrue(amount.doubleValue() >= fetchAmt.doubleValue(),
                ResponseEnum.NOT_SUFFICIENT_FUNDS_ERROR);


        String bindCode = userBindService.getBindCodeByUserId(userId);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("agentId", HfbConst.AGENT_ID);
        paramMap.put("agentBillNo", LendNoUtils.getWithdrawNo());
        paramMap.put("bindCode", bindCode);
        paramMap.put("fetchAmt", fetchAmt);
        paramMap.put("feeAmt", new BigDecimal(0));
        paramMap.put("notifyUrl", HfbConst.WITHDRAW_NOTIFY_URL);
        paramMap.put("returnUrl", HfbConst.WITHDRAW_RETURN_URL);
        paramMap.put("timestamp", RequestHelper.getTimestamp());
        String sign = RequestHelper.getSign(paramMap);
        paramMap.put("sign", sign);

        //构建自动提交表单
        String formStr = FormHelper.buildForm(HfbConst.WITHDRAW_URL, paramMap);
        return formStr;
    }

    @Override
    public void notifyWithdraw(Map<String, Object> paramMap) {
        log.info("提现成功");
        String agentBillNo = (String)paramMap.get("agentBillNo");
        boolean result=transFlowService.judgeExsit(agentBillNo);
        if(result){
           log.info("幂等性返回");
           return;
        }
        String fetchAmt = (String) paramMap.get("fetchAmt");
        String bindCode = (String) paramMap.get("bindCode");
        userAccountMapper.updateAccount(bindCode,new BigDecimal("-"+fetchAmt),new BigDecimal(0));//更新余额
        TransFlowBO transFlowBO = new TransFlowBO(agentBillNo, bindCode, new BigDecimal(fetchAmt), TransTypeEnum.WITHDRAW, "提现");
        transFlowService.saveTransFlow(transFlowBO);//生成订单流水号
    }

    @Override
    public UserAccount getUserAccountByUserId(Long userId) {
        QueryWrapper<UserAccount> userAccountQueryWrapper = new QueryWrapper<>();
        userAccountQueryWrapper.eq("user_id",userId);
        return baseMapper.selectOne(userAccountQueryWrapper);
    }

    public boolean judgeExsit(String agentBillNo){
        QueryWrapper<TransFlow> transFlowQueryWrapper = new QueryWrapper<>();
        transFlowQueryWrapper.eq("trans_no",agentBillNo);
        TransFlow transFlow = transFlowMapper.selectOne(transFlowQueryWrapper);

        return transFlow!=null;
    }
}
