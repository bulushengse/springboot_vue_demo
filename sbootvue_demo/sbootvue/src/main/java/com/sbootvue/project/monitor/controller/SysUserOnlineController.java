package com.sbootvue.project.monitor.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.sbootvue.common.annotion.Log;
import com.sbootvue.common.constant.Constants;
import com.sbootvue.common.enums.BusinessType;
import com.sbootvue.common.utils.string.StringUtils;
import com.sbootvue.framework.redis.RedisCache;
import com.sbootvue.framework.security.LoginUser;
import com.sbootvue.framework.web.controller.BaseController;
import com.sbootvue.framework.web.domain.AjaxResult;
import com.sbootvue.framework.web.page.TableDataInfo;
import com.sbootvue.framework.websocket.WebSocketServer;
import com.sbootvue.project.monitor.domain.SysUserOnline;
import com.sbootvue.project.monitor.service.ISysUserOnlineService;

/**
 * 在线用户监控
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/monitor/online")
public class SysUserOnlineController extends BaseController {
	@Autowired
	private ISysUserOnlineService userOnlineService;

	@Autowired
	private RedisCache redisCache;
	
	@Autowired
	private WebSocketServer webSocketServer;

	@PreAuthorize("@ss.hasPermi('monitor:online:list')")
	@GetMapping("/list")
	public TableDataInfo list(String ipaddr, String userName) {
		Collection<String> keys = redisCache.keys(Constants.LOGIN_TOKEN_KEY + "*");
		List<SysUserOnline> userOnlineList = new ArrayList<SysUserOnline>();
		for (String key : keys) {
			//LoginUser user = redisCache.getCacheObject(key); //spring-boot-devtools 引起转换问题
			Object obj = redisCache.getCacheObject(key);
            LoginUser user = JSON.parseObject(JSON.toJSON(obj).toString(), LoginUser.class);
            
			if (StringUtils.isNotEmpty(ipaddr) && StringUtils.isNotEmpty(userName)) {
				if (StringUtils.equals(ipaddr, user.getIpaddr()) && StringUtils.equals(userName, user.getUsername())) {
					userOnlineList.add(userOnlineService.selectOnlineByInfo(ipaddr, userName, user));
				}
			} else if (StringUtils.isNotEmpty(ipaddr)) {
				if (StringUtils.equals(ipaddr, user.getIpaddr())) {
					userOnlineList.add(userOnlineService.selectOnlineByIpaddr(ipaddr, user));
				}
			} else if (StringUtils.isNotEmpty(userName) && StringUtils.isNotNull(user.getUser())) {
				if (StringUtils.equals(userName, user.getUsername())) {
					userOnlineList.add(userOnlineService.selectOnlineByUserName(userName, user));
				}
			} else {
				userOnlineList.add(userOnlineService.loginUserToUserOnline(user));
			}
		}
		Collections.reverse(userOnlineList);
		userOnlineList.removeAll(Collections.singleton(null));
		return getDataTable(userOnlineList);
	}

	/**
	 * 强退用户
	 */
	@PreAuthorize("@ss.hasPermi('monitor:online:forceLogout')")
	@Log(title = "在线用户", businessType = BusinessType.DELETE)
	@DeleteMapping("/{username}")
	public AjaxResult forceLogout(@PathVariable String username) {
		webSocketServer.userOut(username,"goOut");
		return AjaxResult.success();
	}
}
