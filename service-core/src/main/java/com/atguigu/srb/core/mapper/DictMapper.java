package com.atguigu.srb.core.mapper;

import com.atguigu.srb.core.pojo.dto.ExcelDictDTO;
import com.atguigu.srb.core.pojo.entity.Dict;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 数据字典 Mapper 接口
 * </p>
 *
 * @author zxp
 * @since 2024-02-07
 */
public interface DictMapper extends BaseMapper<Dict> {
    int insertBatch(List<ExcelDictDTO> list);
}
