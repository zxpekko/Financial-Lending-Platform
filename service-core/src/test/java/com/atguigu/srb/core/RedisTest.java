package com.atguigu.srb.core;

import com.atguigu.srb.core.mapper.DictMapper;
import com.atguigu.srb.core.pojo.entity.Dict;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @Author:zxp
 * @Description:
 * @Date:18:28 2024/2/22
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class RedisTest {
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private DictMapper dictMapper;
    @Test
    public void saveDict(){
        Dict dict = dictMapper.selectById(1);
        redisTemplate.opsForValue().set("dict",dict,5, TimeUnit.MINUTES);
    }
    @Test
    public void getDict(){
        Object dict = redisTemplate.opsForValue().get("dict");
        System.out.println("dict:{}"+dict);
    }
}
