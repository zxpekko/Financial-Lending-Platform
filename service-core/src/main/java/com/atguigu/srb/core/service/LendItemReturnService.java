package com.atguigu.srb.core.service;

import com.atguigu.srb.core.pojo.entity.LendItemReturn;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的出借回款记录表 服务类
 * </p>
 *
 * @author zxp
 * @since 2024-02-07
 */
public interface LendItemReturnService extends IService<LendItemReturn> {

    List<LendItemReturn> getLendItemReturnListByUserId(Long lendid,Long userId);

    List<Map<String, Object>> addReturnDetail(Long lendReturnId);
    List<LendItemReturn> getLendItemReturnByLendReutrnId(Long lendReturnId);
}
