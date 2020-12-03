package com.sbootvue.framework.websocket;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.sbootvue.common.utils.SpringUtils;
import com.sbootvue.framework.security.LoginUser;
import com.sbootvue.framework.security.service.TokenService;

@Component
@ServerEndpoint("/websocket")
public class WebSocketServer {

	private static final Logger log = LoggerFactory.getLogger(WebSocketServer.class);
	
     // @ServerEndpoint不支持注入，所以使用SpringUtils获取IOC实例
    private TokenService tokenService = SpringUtils.getBean(TokenService.class);
    
    /**
     * 连接成功调用的方法
     * @param session  可选的参数。session为与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    @OnOpen
    public void onOpen(Session session){
    	
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session){
    	OnlineSocketConnPool.removeUser(session); 
    }

    /**
     * 发生错误时调用
     */
    @OnError
    public void onError(Session session, Throwable error){
    	log.error("websocket发生错误:"+error.getMessage());
        error.printStackTrace();
    }

    /**
     * 收到客户端消息后调用的方法
     * @param message 客户端发送过来的消息
     * @param session 可选的参数
     */
    @OnMessage
    public void onMessage(String message, Session session) {
    	System.out.println("收到消息："+message);
        if (null != message && message.startsWith("[join]")) {
			this.userjoin(message.replaceFirst("\\[join\\]", ""), session);
		}
    }
	
	/**
	 *  用户加入处理
	 * @param token
	 * @param conn
	 */
	public void userjoin(String token,Session conn) {
		LoginUser user = tokenService.getLoginUser(token);
		//判断redisCache是否存在用户token,不存在websocket连接要断开
		if (user == null) {
			log.info("websocket用户加入失败，错误的用户token:"+token);
			joinFail(conn);
			return ;
		}
		
		String username = user.getUsername();
		Session  curruser_conn = OnlineSocketConnPool.getSessionByUser(username);
 		//判断用户是否在其他终端登录
		if(curruser_conn == null) { 
			OnlineSocketConnPool.addUser(conn,username);
		}else {
			userOut(username,"getOut");  	//踢出其他终端登录的用户
			OnlineSocketConnPool.addUser(conn,username);
		}
		log.info("websocket有新用户加入，用户名："+username);
	}
	
	 /**
	  * 发送msg，强制用户下线
	  * @param conn
	  * @param type
	  */
	public void userOut(String username,String type) {
		JSONObject result = new JSONObject();
		result.put("type", type);
		Session conn = OnlineSocketConnPool.getSessionByUser(username);
		OnlineSocketConnPool.sendMessageToUser(conn, result.toString());
	}
		
	/**
	 * 发送msg，用户加入失败
	 * @param conn
	 * @param type
	 */
	public void joinFail(Session conn) {
		JSONObject result = new JSONObject();
		result.put("type", "joinFail");
		//result.put("msg", "joinFail");
		OnlineSocketConnPool.sendMessageToUser(conn, result.toString());
	}
	
}
