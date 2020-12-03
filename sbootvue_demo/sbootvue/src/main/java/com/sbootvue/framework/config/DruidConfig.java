package com.sbootvue.framework.config;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.properties.DruidStatProperties;
import com.alibaba.druid.util.Utils;

/**
 * druid 配置数据源
 * 
 * @author zhoubc
 */
@Configuration
public class DruidConfig {
	
	 @Value("${spring.datasource.driverClassName}")
    private String driverClassName;
	 
    @Value("${spring.datasource.url}")
    private String connectionUrl;
    
    @Value("${spring.datasource.username}")
    private String username;
    
    @Value("${spring.datasource.password}")
    private String password;
	    
	@Value("${spring.datasource.druid.initialSize}")
	private int initialSize;

	@Value("${spring.datasource.druid.minIdle}")
	private int minIdle;

	@Value("${spring.datasource.druid.maxActive}")
	private int maxActive;

	@Value("${spring.datasource.druid.maxWait}")
	private int maxWait;

	@Value("${spring.datasource.druid.timeBetweenEvictionRunsMillis}")
	private int timeBetweenEvictionRunsMillis;

	@Value("${spring.datasource.druid.minEvictableIdleTimeMillis}")
	private int minEvictableIdleTimeMillis;

	@Value("${spring.datasource.druid.maxEvictableIdleTimeMillis}")
	private int maxEvictableIdleTimeMillis;

	@Value("${spring.datasource.druid.validationQuery}")
	private String validationQuery;

	@Value("${spring.datasource.druid.testWhileIdle}")
	private boolean testWhileIdle;

	@Value("${spring.datasource.druid.testOnBorrow}")
	private boolean testOnBorrow;

	@Value("${spring.datasource.druid.testOnReturn}")
	private boolean testOnReturn;

	@Bean
	public DruidDataSource dataSource() {
		DruidDataSource datasource = new DruidDataSource();
		/** 配置数据库连接 */
		datasource.setDriverClassName(driverClassName);
		datasource.setUrl(connectionUrl);
		datasource.setUsername(username);
		datasource.setPassword(password);
		
		/** 配置初始化大小、最小、最大 */
		datasource.setInitialSize(initialSize);
		datasource.setMaxActive(maxActive);
		datasource.setMinIdle(minIdle);

		/** 配置获取连接等待超时的时间 */
		datasource.setMaxWait(maxWait);

		/** 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒 */
		datasource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);

		/** 配置一个连接在池中最小、最大生存的时间，单位是毫秒 */
		datasource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
		datasource.setMaxEvictableIdleTimeMillis(maxEvictableIdleTimeMillis);

		/**
		 * 用来检测连接是否有效的sql，要求是一个查询语句，常用select 'x'。如果validationQuery为null，testOnBorrow、testOnReturn、testWhileIdle都不会起作用。
		 */
		datasource.setValidationQuery(validationQuery);
		/** 建议配置为true，不影响性能，并且保证安全性。申请连接的时候检测，如果空闲时间大于timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效。 */
		datasource.setTestWhileIdle(testWhileIdle);
		/** 申请连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能。 */
		datasource.setTestOnBorrow(testOnBorrow);
		/** 归还连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能。 */
		datasource.setTestOnReturn(testOnReturn);
		return datasource;
	}
	
	/**
	 * 去除监控页面底部的广告
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Bean
	@ConditionalOnProperty(name = "spring.datasource.druid.statViewServlet.enabled", havingValue = "true")
	public FilterRegistrationBean removeDruidFilterRegistrationBean(DruidStatProperties properties) {
		// 获取web监控页面的参数
		DruidStatProperties.StatViewServlet config = properties.getStatViewServlet();
		// 提取common.js的配置路径
		String pattern = config.getUrlPattern() != null ? config.getUrlPattern() : "/druid/*";
		String commonJsPattern = pattern.replaceAll("\\*", "js/common.js");
		final String filePath = "support/http/resources/js/common.js";
		// 创建filter进行过滤
		Filter filter = new Filter() {
			@Override
			public void init(javax.servlet.FilterConfig filterConfig) throws ServletException {
			}

			@Override
			public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
					throws IOException, ServletException {
				chain.doFilter(request, response);
				// 重置缓冲区，响应头不会被重置
				response.resetBuffer();
				// 获取common.js
				String text = Utils.readFromResource(filePath);
				// 正则替换banner, 除去底部的广告信息
				text = text.replaceAll("<a.*?banner\"></a><br/>", "");
				text = text.replaceAll("powered.*?shrek.wang</a>", "");
				response.getWriter().write(text);
			}

			@Override
			public void destroy() {
			}
		};
		FilterRegistrationBean registrationBean = new FilterRegistrationBean();
		registrationBean.setFilter(filter);
		registrationBean.addUrlPatterns(commonJsPattern);
		return registrationBean;
	}

}
