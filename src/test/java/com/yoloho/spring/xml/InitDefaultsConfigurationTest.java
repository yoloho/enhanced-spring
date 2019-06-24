package com.yoloho.spring.xml;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.yoloho.spring.annotation.InitDefaults;
import com.yoloho.spring.util.PropertyUtil;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = InitDefaultsConfigurationTest.class)
@TestPropertySource(properties = {"test:dddd"})
@InitDefaults(projectName = "InitDefaultsConfigurationTest")
public class InitDefaultsConfigurationTest {
    
    @Test
    public void test() {
        assertEquals("dddd", PropertyUtil.getProperty("test"));
    }
}
