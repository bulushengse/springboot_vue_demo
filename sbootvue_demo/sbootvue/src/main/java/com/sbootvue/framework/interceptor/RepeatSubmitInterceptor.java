package com.sbootvue.framework.interceptor;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import com.alibaba.fastjson.JSONObject;
import com.sbootvue.common.annotion.RepeatSubmit;
import com.sbootvue.common.constant.Constants;
import com.sbootvue.common.utils.ServletUtils;
import com.sbootvue.common.utils.http.HttpHelper;
import com.sbootvue.common.utils.string.StringUtils;
import com.sbootvue.framework.filter.RepeatedlyRequestWrapper;
import com.sbootvue.framework.redis.RedisCache;
import com.sbootvue.framework.web.domain.AjaxResult;

/**
 * 防止重复提交拦截器
 * 
 * @author zhoubc
 */
@Component
public class RepeatSubmitInterceptor extends HandlerInterceptorAdapter {

	/**
	 * 间隔时间，单位:秒 默认10秒
	 * 两次相同参数的请求，如果间隔时间大于该参数，系统不会认定为重复提交的数据
	 */
	private int intervalTime = 10;

	@Autowired
	private RedisCache redisCache;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		System.out.println("------------------------------------------------interceptor-");
		if (handler instanceof HandlerMethod) {
			HandlerMethod handlerMethod = (HandlerMethod) handler;
			Method method = handlerMethod.getMethod();
			RepeatSubmit annotation = method.getAnnotation(RepeatSubmit.class);
			if (annotation != null) {
				if (this.isRepeatSubmit(request)) {
					AjaxResult ajaxResult = AjaxResult.error("不允许重复提交，请稍后再试");
					ServletUtils.renderString(response, JSONObject.toJSONString(ajaxResult));
					return false;
				}
			}
			return true;
		} else {
			return super.preHandle(request, response, handler);
		}
	}

	/**
	 * 验证是否重复提交
	 * 
	 * @param httpServletRequest
	 * @return
	 * @throws Exception
	 */
	public boolean isRepeatSubmit(HttpServletRequest request) {
		RepeatedlyRequestWrapper repeatedlyRequest = (RepeatedlyRequestWrapper) request;
		String nowParams = HttpHelper.getBodyString(repeatedlyRequest);

		// body参数为空，获取Parameter的数据
		if (StringUtils.isEmpty(nowParams)) {
			nowParams = JSONObject.toJSONString(request.getParameterMap());
		}
		Map<String, Object> nowDataMap = new HashMap<String, Object>();
		nowDataMap.put("repeatParams", nowParams);
		nowDataMap.put("repeatTime", System.currentTimeMillis());

		// 请求地址（作为存放cache的key值）
		String url = request.getRequestURI();

		Object sessionObj = redisCache.getCacheObject(Constants.CACHE_REPEAT_KEY);
		if (sessionObj != null) {
			Map<String, Object> sessionMap = (Map<String, Object>) sessionObj;
			if (sessionMap.containsKey(url)) {
				Map<String, Object> preDataMap = (Map<String, Object>) sessionMap.get(url);
				if (compareParams(nowDataMap, preDataMap) && compareTime(nowDataMap, preDataMap)) {
					return true;
				}
			}
		}
		Map<String, Object> cacheMap = new HashMap<String, Object>();
		cacheMap.put(url, nowDataMap);
		redisCache.setCacheObject(Constants.CACHE_REPEAT_KEY, cacheMap, intervalTime, TimeUnit.SECONDS);
		return false;
	};

	/**
	 * 判断参数是否相同
	 */
	private boolean compareParams(Map<String, Object> nowMap, Map<String, Object> preMap) {
		String nowParams = (String) nowMap.get("repeatParams");
		String preParams = (String) preMap.get("repeatParams");
		return nowParams.equals(preParams);
	}

	/**
	 * 判断两次间隔时间
	 */
	private boolean compareTime(Map<String, Object> nowMap, Map<String, Object> preMap) {
		long time1 = (Long) nowMap.get("repeatTime");
		long time2 = (Long) preMap.get("repeatTime");
		if ((time1 - time2) < (this.intervalTime * 1000)) {
			return true;
		}
		return false;
	}
}
