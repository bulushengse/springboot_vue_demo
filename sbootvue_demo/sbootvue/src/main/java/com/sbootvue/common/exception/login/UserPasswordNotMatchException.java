package com.sbootvue.common.exception.login;

/**
 * 用户密码不正确异常类
 * 
 * @author zhoubc
 */
public class UserPasswordNotMatchException extends UserException {
	private static final long serialVersionUID = 1L;

	public UserPasswordNotMatchException() {
		super("user.password.not.match", null);
	}
}
