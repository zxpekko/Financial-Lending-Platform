package com.atguigu.srb.core.service.impl;

import com.alibaba.excel.EasyExcel;
import com.atguigu.srb.core.listner.ExcelDTOListner;
import com.atguigu.srb.core.pojo.dto.ExcelDictDTO;
import com.atguigu.srb.core.pojo.entity.Dict;
import com.atguigu.srb.core.mapper.DictMapper;
import com.atguigu.srb.core.service.DictService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 数据字典 服务实现类
 * </p>
 *
 * @author zxp
 * @since 2024-02-07
 */
@Slf4j
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {
    @Resource
    DictMapper dictMapper;
    @Resource
    private RedisTemplate redisTemplate;
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void importData(InputStream inputStream) {
        EasyExcel.read(inputStream, ExcelDictDTO.class, new ExcelDTOListner(dictMapper)).sheet().doRead();
        log.info("导入成功");
    }

    @Override
    public List<ExcelDictDTO> listDictData() {
        List<Dict> dicts = dictMapper.selectList(null);
        List<ExcelDictDTO> excelDictDTOS=new ArrayList<>();
        dicts.forEach(dict -> {
            ExcelDictDTO excelDictDTO = new ExcelDictDTO();
            BeanUtils.copyProperties(dict,excelDictDTO);
            excelDictDTOS.add(excelDictDTO);
        });
        return excelDictDTOS;
    }

    @Override
    public List<Dict> listByParentId(Long parentId) {
        try {
            List<Dict> o = (List<Dict>)redisTemplate.opsForValue().get("srb:core:dictList" + parentId);
            if(o!=null){
                log.info("从redis中获取数据列表");
                return o;
            }
        } catch (Exception e) {
//            throw new RuntimeException(e);
            log.info("redis服务器异常"+ ExceptionUtils.getStackTrace(e));
        }
        log.info("从数据库中获取数据");
        QueryWrapper<Dict> dictQueryWrapper = new QueryWrapper<>();
        dictQueryWrapper.eq("parent_id",parentId);
        List<Dict> dicts = dictMapper.selectList(dictQueryWrapper);
        dicts.forEach(dict -> {
            dict.setHasChildren(hasChildren(dict));
        });
        try {
            log.info("将数据存入redis");
            redisTemplate.opsForValue().set("srb:core:dictList" + parentId,dicts,5, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.info("redis服务器异常"+ ExceptionUtils.getStackTrace(e));
//            throw new RuntimeException(e);
        }
        return dicts;
    }

    @Override
    public List<Dict> getChilden(String dictCode) {
        QueryWrapper<Dict> dictQueryWrapper = new QueryWrapper<>();
        dictQueryWrapper.eq("dict_code",dictCode);
        Dict dict = dictMapper.selectOne(dictQueryWrapper);
        log.info("dict"+dict);
        Long parentId=dict.getId();
        List<Dict> dicts = listByParentId(parentId);
        return dicts;
    }

    @Override
    public String getNameByParentDictCodeAndValue(String dictCode, Integer value) {
        QueryWrapper<Dict> dictQueryWrapper = new QueryWrapper<>();
        dictQueryWrapper.eq("dict_code",dictCode);
        Dict dict = dictMapper.selectOne(dictQueryWrapper);
        if(dict==null)
            return "";
        dictQueryWrapper=new QueryWrapper<>();
        dictQueryWrapper.eq("parent_id",dict.getId())
                .eq("value",value);
        Dict dict1 = dictMapper.selectOne(dictQueryWrapper);
        if(dict1==null)
            return "";
        return dict1.getName();
    }

    private boolean hasChildren(Dict dict){
//        for (Dict dict1 : baseMapper.selectList(null)) {
//            if(dict.getId()==dict1.getParentId())
//                return true;
//        }
//        return false;
        QueryWrapper<Dict> dictQueryWrapper = new QueryWrapper<>();
        dictQueryWrapper.eq("parent_id",dict.getId());
        List<Dict> dicts = dictMapper.selectList(dictQueryWrapper);
        return dicts.size()>0;
    }
}
