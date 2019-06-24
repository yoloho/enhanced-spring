package com.yoloho.spring.support;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.yoloho.spring.config.MethodArgumentResolver;
import com.yoloho.spring.controller.CustomArgumentConverterTestController;
import com.yoloho.spring.controller.CustomArgumentConverterTestController.CommonParams;

/**
 * 自定义参数转换器测试
 * 
 * @author jason
 *
 */
@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = { "classpath:context.xml", "classpath:dispatcher-servlet.xml" })
public class CustomArgumentTest {
	
	@Autowired
	CustomArgumentConverterTestController controller;
	private MockMvc mockMvc;
	
	@Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
        		.setCustomArgumentResolvers(new MethodArgumentResolver()).build();
    }
	
	@Test
	public void test() {
		// 普通客户端api访问
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("id", "3");
		params.add("name", "demo");
		ResultActions resultActions;
		try {
			resultActions = mockMvc
			        .perform(MockMvcRequestBuilders.get("/test/first")
			        .header("x-forwarded-for", "127.0.0.1").params(params));
			Map<String, Object> result = resultActions.andReturn().getModelAndView().getModel();
			CommonParams commonParams = (CommonParams)result.get("data");
			Assert.assertNotNull(commonParams);
			Assert.assertEquals(3, commonParams.getId());
			Assert.assertEquals(0, commonParams.getLevel());
			Assert.assertEquals("test", commonParams.getName());
		} catch (Exception e) {
			Assert.fail();
		}
		
	}
	
}
