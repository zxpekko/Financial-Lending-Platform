package com.atguigu.srb.core.service.impl;

import com.atguigu.common.exception.Assert;
import com.atguigu.common.result.ResponseEnum;
import com.atguigu.srb.core.enums.UserBindEnum;
import com.atguigu.srb.core.hfb.FormHelper;
import com.atguigu.srb.core.hfb.HfbConst;
import com.atguigu.srb.core.hfb.RequestHelper;
import com.atguigu.srb.core.mapper.UserBindMapper;
import com.atguigu.srb.core.mapper.UserInfoMapper;
import com.atguigu.srb.core.pojo.entity.UserBind;
import com.atguigu.srb.core.pojo.entity.UserInfo;
import com.atguigu.srb.core.pojo.vo.UserBindVO;
import com.atguigu.srb.core.service.UserBindService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 用户绑定表 服务实现类
 * </p>
 *
 * @author zxp
 * @since 2024-02-07
 */
@Service
public class UserBindServiceImpl extends ServiceImpl<UserBindMapper, UserBind> implements UserBindService {
    @Resource
    private UserInfoMapper userInfoMapper;
    @Resource
    private UserBindMapper userBindMapper;

    @Override
    public String commitBindUser(UserBindVO userBindVO, Long userId) {
        //不同的userid相同的身份证不允许绑定
        QueryWrapper<UserBind> userBindQueryWrapper = new QueryWrapper<>();
        userBindQueryWrapper.eq("id_card",userBindVO.getIdCard())
                .ne("user_id",userId);
        UserBind userBind = baseMapper.selectOne(userBindQueryWrapper);
        Assert.isNull(userBind, ResponseEnum.USER_BIND_IDCARD_EXIST_ERROR);


        //相同的userid判断用户是否曾经绑定过
        userBindQueryWrapper = new QueryWrapper<>();
        userBindQueryWrapper.eq("user_id",userId);
//        Integer count = baseMapper.selectCount(userBindQueryWrapper);
        userBind = baseMapper.selectOne(userBindQueryWrapper);
        if(userBind==null){
            userBind = new UserBind();
            BeanUtils.copyProperties(userBindVO,userBind);
            userBind.setUserId(userId);
            userBind.setStatus(UserBindEnum.NO_BIND.getStatus());
            baseMapper.insert(userBind);
        }
        else {
            BeanUtils.copyProperties(userBindVO,userBind);
//            userBind.setUserId(userId);//这两个属性不用写，查到了说明上次已经填充过了，而且无法改变userid，状态本来就是未绑定
//            userBind.setStatus(UserBindEnum.NO_BIND.getStatus());
            baseMapper.updateById(userBind);
        }
        //生成userbind对象并加入数据库

        //生成动态表单字符串
//        String form="<!DOCTYPE html>\n" +
//                "<html lang=\"en\" xmlns:th=\"http://www.thymeleaf.org\">\n" +
//                "  <head> </head>\n" +
//                "  <body>\n" +
//                "    <form name=\"form\" action=\"远程地址\" method=\"post\">\n" +
//                "      <!-- 参数 -->\n" +
//                "      <input type=\"text\" name=\"mobile\" value=\"13900000000\" />\n" +
//                "      <input type=\"text\" name=\"sign\" value=\"123456\" />\n" +
//                "    </form>\n" +
//                "    <script>\n" +
//                "      document.form.submit()\n" +
//                "    </script>\n" +
//                "  </body>\n" +
//                "</html>";
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("agentId", HfbConst.AGENT_ID);
        paramMap.put("agentUserId", userId);
        paramMap.put("idCard",userBindVO.getIdCard());
        paramMap.put("personalName", userBindVO.getName());
        paramMap.put("bankType", userBindVO.getBankType());
        paramMap.put("bankNo", userBindVO.getBankNo());
        paramMap.put("mobile", userBindVO.getMobile());
        paramMap.put("returnUrl", HfbConst.USERBIND_RETURN_URL);
        paramMap.put("notifyUrl", HfbConst.USERBIND_NOTIFY_URL);
        paramMap.put("timestamp", RequestHelper.getTimestamp());
        paramMap.put("sign", RequestHelper.getSign(paramMap));
        String formStr=FormHelper.buildForm(HfbConst.USERBIND_URL,paramMap);

        return formStr;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void notify(Map<String, Object> paramMap) {
        String agentUserId = (String)paramMap.get("agentUserId");
        String bindCode = (String)paramMap.get("bindCode");
        String resultCode = (String)paramMap.get("resultCode");
        UserInfo userInfo = new UserInfo();
        userInfo.setId(Long.parseLong(agentUserId));

//        userInfoMapper.updateById(userInfo);
        QueryWrapper<UserBind> userBindQueryWrapper = new QueryWrapper<>();
        userBindQueryWrapper.eq("user_id",agentUserId);
        UserBind userBind = userBindMapper.selectOne(userBindQueryWrapper);
        if("0001".equals(resultCode)){
            userBind.setBindCode(bindCode);
            userBind.setStatus(UserBindEnum.BIND_OK.getStatus());
            userBindMapper.updateById(userBind);
        }
        if("0001".equals(resultCode)){
            userInfo.setBindStatus(UserBindEnum.BIND_OK.getStatus());
            userInfo.setBindCode(bindCode);
            userInfo.setName(userBind.getName());
            userInfo.setIdCard(userBind.getIdCard());
            userInfoMapper.updateById(userInfo);
        }

    }

    @Override
    public String getBindCodeByUserId(Long investUserId) {
        QueryWrapper<UserBind> userBindQueryWrapper = new QueryWrapper<>();
        userBindQueryWrapper.eq("user_id",investUserId);
        UserBind userBind = baseMapper.selectOne(userBindQueryWrapper);
        return userBind.getBindCode();
    }
}
