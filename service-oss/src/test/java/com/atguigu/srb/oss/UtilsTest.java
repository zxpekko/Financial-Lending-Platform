package com.atguigu.srb.oss;

import com.atguigu.srb.oss.util.OSSProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author:zxp
 * @Description:
 * @Date:13:07 2024/2/26
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class UtilsTest {
    @Test
    public void testProperties(){
        System.out.println(OSSProperties.ENDPOINT);
        System.out.println(OSSProperties.KEY_ID);
        System.out.println(OSSProperties.KEY_SECRET);
        System.out.println(OSSProperties.BUCKET_NAME);
//        System.out.println(SmsProperties.KEY_SECRET);
    }
}
