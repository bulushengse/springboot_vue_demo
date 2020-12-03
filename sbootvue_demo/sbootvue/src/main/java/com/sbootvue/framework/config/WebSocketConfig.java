package com.sbootvue.framework.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * 开启WebSocket支持
 * 
 * @author zhoubc
 */
@Configuration  
public class WebSocketConfig {

	@Bean
    public ServerEndpointExporter serverEndpointExporter() {  
        return new ServerEndpointExporter();  
    }
	
}
