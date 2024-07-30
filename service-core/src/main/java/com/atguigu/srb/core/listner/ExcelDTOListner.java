package com.atguigu.srb.core.listner;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.atguigu.srb.core.mapper.DictMapper;
import com.atguigu.srb.core.pojo.dto.ExcelDictDTO;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author:zxp
 * @Description:
 * @Date:16:52 2024/2/21
 */
@Slf4j
@NoArgsConstructor
public class ExcelDTOListner extends AnalysisEventListener<ExcelDictDTO> {
    private List<ExcelDictDTO> list=new ArrayList<>();
    private static final int BATCH_COUNT=5;
    public ExcelDTOListner(DictMapper dictMapper){
        this.dictMapper=dictMapper;
    }
    private DictMapper dictMapper;
    @Override
    public void invoke(ExcelDictDTO excelDictDTO, AnalysisContext analysisContext) {

        log.info("解析到一条记录:{}",excelDictDTO);
        list.add(excelDictDTO);
        if(list.size()>=BATCH_COUNT){
            saveData();
            list.clear();
        }
    }
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        log.info("所有数据解析完成");
        saveData();
    }
    private void saveData(){
        //保存
        log.info("开始保存：{}",list.size());
        dictMapper.insertBatch(list);
        log.info("保存结束");
    }
}