package com.sbootvue;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.sbootvue.project.system.controller.SysUserController;

@RunWith(SpringJUnit4ClassRunner.class)			//SpringBoot对JUnit4的支持
@SpringBootTest                                       //指定Spring Boot的启动类。
@WebAppConfiguration							//开启Web应用的配置， 用千模拟ServletContext。
//SpringBoot 提供的单元测试    
public class WebTests {
	
	private MockMvc mvc;
	
	@Before
	public void setUp() throws Exception{
		mvc = MockMvcBuilders.standaloneSetup(new SysUserController()).build();
	}
	
	@Test
	public void hello() throws Exception{
		mvc.perform(MockMvcRequestBuilders.get("/findUser").accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk() )
					.andExpect(content().string(equalTo("Hello World") ));
	}
}
