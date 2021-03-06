package com.sbootvue.framework.task;

import org.springframework.stereotype.Component;

import com.sbootvue.common.utils.string.StringUtils;

/**
 * 定时任务调度测试
 * 
 * @author zhoubc
 */
@Component("timedTask")
public class TimedTask {
	
	public void testMultipleParams(String s, Boolean b, Long l, Double d, Integer i) {
		System.out.println(StringUtils.format("定时任务测试：执行多参方法： 字符串类型{}，布尔类型{}，长整型{}，浮点型{}，整形{}", s, b, l, d, i));
	}

	public void testParams(String params) {
		System.out.println("定时任务测试：执行有参方法：" + params);
	}

	public void testNoParams() {
		System.out.println("定时任务测试：执行无参方法");
	}

}
