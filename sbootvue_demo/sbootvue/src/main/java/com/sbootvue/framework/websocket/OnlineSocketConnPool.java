package com.sbootvue.framework.websocket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.websocket.Session;

import org.springframework.stereotype.Component;

@Component
public class OnlineSocketConnPool {

    private static Map<Session,String> userconnections = new HashMap<Session,String>();
    
    
    /**
	 * 获取user
	 * 
	 * @param session
	 */
	public static String getUserBySession(Session session) {
		return userconnections.get(session);
	}
	
	/**
	 * 获取WebSocket
	 * 
	 * @param user
	 */
	public static Session getSessionByUser(String user) {
		Set<Session> keySet = userconnections.keySet();
		synchronized (keySet) {
			for (Session conn : keySet) {
				String cuser = userconnections.get(conn);
				if (cuser.equals(user)) {
					return conn;
				}
			}
		}
		return null;
	}

	
    /**
	 * 向连接池中添加连接
	 * 
	 * @param inbound
	 */
	public static void addUser(Session conn,String user) {
		userconnections.put(conn,user); // 添加连接
	}

	/**
	 * 移除连接池中的连接
	 * 
	 * @param inbound
	 */
	public static boolean removeUser(Session conn) {
		if (userconnections.containsKey(conn)) {
			userconnections.remove(conn); // 移除连接
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 向特定的用户发送数据
	 * 
	 * @param user
	 * @param message
	 */
	public static void sendMessageToUser(Session conn, String message) {
		if (null != conn) {
			try {
				conn.getBasicRemote().sendText(message);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
