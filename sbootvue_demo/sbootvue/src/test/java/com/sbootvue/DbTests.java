package com.sbootvue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.sbootvue.project.system.domain.SysUser;
import com.sbootvue.project.system.service.ISysUserService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DbTests {

	@Autowired
	private ISysUserService iSysUserService;
	
	@Test
	public void contextLoads() {
		
		String id ="1";
		SysUser user = iSysUserService.selectUserById(id);
		
		System.out.println(user.toString());
	}

}
