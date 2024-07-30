package com.atguigu.srb.sms;

import com.atguigu.srb.sms.util.SmsProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author:zxp
 * @Description:
 * @Date:14:56 2024/2/23
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class UtilsTest {
    @Test
    public void testProperties(){
        System.out.println(SmsProperties.REGION_Id);
        System.out.println(SmsProperties.SIGN_NAME);
        System.out.println(SmsProperties.TEMPLATE_CODE);
        System.out.println(SmsProperties.KEY_ID);
        System.out.println(SmsProperties.KEY_SECRET);
    }
}
