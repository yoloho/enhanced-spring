package com.yoloho.spring.util;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;

import com.yoloho.spring.MySpringJUnit4ClassRunner;

@ContextConfiguration(locations = { "classpath:context.xml" })
@RunWith(MySpringJUnit4ClassRunner.class)
public class DayimaPropertyTest {
    
    @Test
    public void getPropertyTest() {
        Assert.assertNotEquals("val2", PropertyUtil.getProperty("key1"));
        Assert.assertEquals("val1", PropertyUtil.getProperty("key1"));
        Assert.assertEquals("val2", PropertyUtil.getProperty("key2"));
        //测试ERU=1环境中，ENV对placeholder的覆盖功能
        Assert.assertEquals("newVal", PropertyUtil.getProperty("jdbc.url"));
        Assert.assertEquals("newVal1", PropertyUtil.getProperty("jdbc.url_new"));
    }
}
