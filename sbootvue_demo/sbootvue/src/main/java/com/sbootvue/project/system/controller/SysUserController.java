package com.sbootvue.project.system.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sbootvue.common.annotion.Log;
import com.sbootvue.common.constant.UserConstants;
import com.sbootvue.common.enums.BusinessType;
import com.sbootvue.common.utils.ExcelUtil;
import com.sbootvue.common.utils.SecurityUtils;
import com.sbootvue.common.utils.ServletUtils;
import com.sbootvue.common.utils.string.StringUtils;
import com.sbootvue.framework.security.LoginUser;
import com.sbootvue.framework.security.service.TokenService;
import com.sbootvue.framework.web.controller.BaseController;
import com.sbootvue.framework.web.domain.AjaxResult;
import com.sbootvue.framework.web.page.TableDataInfo;
import com.sbootvue.project.system.domain.SysUser;
import com.sbootvue.project.system.service.ISysPostService;
import com.sbootvue.project.system.service.ISysRoleService;
import com.sbootvue.project.system.service.ISysUserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 用户信息
 * 
 * @author zhoubc
 */
@RestController
@RequestMapping("/system/user")
@Api(value="/system/user", tags= "用户管理API")
public class SysUserController extends BaseController {
	@Autowired
	private ISysUserService userService;

	@Autowired
	private ISysRoleService roleService;

	@Autowired
	private ISysPostService postService;

	@Autowired
	private TokenService tokenService;

	/**
	 * 获取用户列表
	 */
	@ApiOperation(value="查询用户列表", httpMethod="GET", notes="根据筛选条件，分页查询用户列表")
	@PreAuthorize("@ss.hasPermi('system:user:list')")
	@GetMapping("/list")
	public TableDataInfo list(@ApiParam(value="查询条件", required=true) SysUser user) {
		startPage();
		List<SysUser> list = userService.selectUserList(user);
		return getDataTable(list);
	}
	
	/**
	 *  用户导出excel
	 */
	@Log(title = "用户管理", businessType = BusinessType.EXPORT)
	@PreAuthorize("@ss.hasPermi('system:user:export')")
	@GetMapping("/export")
	public AjaxResult export(SysUser user) {
		List<SysUser> list = userService.selectUserList(user);
		ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
		return util.exportExcel(list, "用户数据");
	}

	/**
	 *  用户导入，批量新增用户
	 */
	@Log(title = "用户管理", businessType = BusinessType.IMPORT)
	@PreAuthorize("@ss.hasPermi('system:user:import')")
	@PostMapping("/importData")
	public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
		ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
		List<SysUser> userList = util.importExcel(file.getInputStream());
		LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
		String operName = loginUser.getUsername();
		String message = userService.importUser(userList, updateSupport, operName);
		return AjaxResult.success(message);
	}

	/**
	 *  用户导入模版
	 * @return
	 */
	@GetMapping("/importTemplate")
	public AjaxResult importTemplate() {
		ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
		return util.importTemplateExcel("用户数据");
	}

	/**
	 * 根据用户编号获取详细
	 */
	@PreAuthorize("@ss.hasPermi('system:user:query')")
	@GetMapping(value = { "/getDetail/{userId}" }) 
	public AjaxResult getUserDetail(@PathVariable(value = "userId", required = false) String userId) {
		AjaxResult ajax = AjaxResult.success();
		if (StringUtils.isNotNull(userId)) {
			SysUser user  = userService.selectUserById(userId);
			String postGroup = userService.selectUserPostGroup(user.getUserName());
			String roleGroup = userService.selectUserRoleGroup(user.getUserName());
			ajax.put(AjaxResult.DATA_TAG, user);
			ajax.put("postNames", postGroup.split(","));  
			ajax.put("roleNames", roleGroup.split(","));
		}
		return ajax;
	}
	
	/**
	 * 根据用户编号获取详细信息，
	 */
	@PreAuthorize("@ss.hasPermi('system:user:query')")
	@GetMapping(value = { "/", "/{userId}" })
	public AjaxResult getInfo(@PathVariable(value = "userId", required = false) String userId) {
		AjaxResult ajax = AjaxResult.success();
		ajax.put("roles", roleService.selectRoleAll());
		ajax.put("posts", postService.selectPostAll());
		if (StringUtils.isNotNull(userId)) {
			ajax.put(AjaxResult.DATA_TAG, userService.selectUserById(userId));
			ajax.put("postIds", postService.selectPostListByUserId(userId));
			ajax.put("roleIds", roleService.selectRoleListByUserId(userId));
		}
		return ajax;
	}

	/**
	 * 新增用户
	 */
	@PreAuthorize("@ss.hasPermi('system:user:add')")
	@Log(title = "用户管理", businessType = BusinessType.INSERT)
	@PostMapping
	public AjaxResult add(@Validated @RequestBody SysUser user) {
		if (UserConstants.NOT_UNIQUE.equals(userService.checkUserNameUnique(user.getUserName()))) {
			return AjaxResult.error("新增用户'" + user.getUserName() + "'失败，登录账号已存在");
		} else if (UserConstants.NOT_UNIQUE.equals(userService.checkPhoneUnique(user))) {
			return AjaxResult.error("新增用户'" + user.getUserName() + "'失败，手机号码已存在");
		} else if (UserConstants.NOT_UNIQUE.equals(userService.checkEmailUnique(user))) {
			return AjaxResult.error("新增用户'" + user.getUserName() + "'失败，邮箱账号已存在");
		}
		user.setCreateBy(SecurityUtils.getUsername());
		user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
		return toAjax(userService.insertUser(user));
	}

	/**
	 * 修改用户
	 */
	@PreAuthorize("@ss.hasPermi('system:user:edit')")
	@Log(title = "用户管理", businessType = BusinessType.UPDATE)
	@PutMapping
	public AjaxResult edit(@Validated @RequestBody SysUser user) {
		userService.checkUserAllowed(user);
		if (UserConstants.NOT_UNIQUE.equals(userService.checkPhoneUnique(user))) {
			return AjaxResult.error("修改用户'" + user.getUserName() + "'失败，手机号码已存在");
		} else if (UserConstants.NOT_UNIQUE.equals(userService.checkEmailUnique(user))) {
			return AjaxResult.error("修改用户'" + user.getUserName() + "'失败，邮箱账号已存在");
		}
		user.setUpdateBy(SecurityUtils.getUsername());
		return toAjax(userService.updateUser(user));
	}

	/**
	 * 删除用户
	 */
	@PreAuthorize("@ss.hasPermi('system:user:remove')")
	@Log(title = "用户管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{userIds}")
	public AjaxResult remove(@PathVariable String[] userIds) {
		return toAjax(userService.deleteUserByIds(userIds));
	}

	/**
	 * 重置密码
	 */
	@PreAuthorize("@ss.hasPermi('system:user:edit')")
	@Log(title = "用户管理", businessType = BusinessType.UPDATE)
	@PutMapping("/resetPwd")
	public AjaxResult resetPwd(@RequestBody SysUser user) {
		userService.checkUserAllowed(user);
		user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
		user.setUpdateBy(SecurityUtils.getUsername());
		return toAjax(userService.resetPwd(user));
	}

	/**
	 * 状态修改
	 */
	@PreAuthorize("@ss.hasPermi('system:user:edit')")
	@Log(title = "用户管理", businessType = BusinessType.UPDATE)
	@PutMapping("/changeStatus")
	public AjaxResult changeStatus(@RequestBody SysUser user) {
		userService.checkUserAllowed(user);
		user.setUpdateBy(SecurityUtils.getUsername());
		return toAjax(userService.updateUserStatus(user));
	}
}