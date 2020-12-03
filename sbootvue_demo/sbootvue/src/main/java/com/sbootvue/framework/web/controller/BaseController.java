package com.sbootvue.framework.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sbootvue.common.constant.HttpStatus;
import com.sbootvue.common.utils.ServletUtils;
import com.sbootvue.common.utils.SqlUtil;
import com.sbootvue.common.utils.string.StringUtils;
import com.sbootvue.framework.web.domain.AjaxResult;
import com.sbootvue.framework.web.page.PageDomain;
import com.sbootvue.framework.web.page.TableDataInfo;
import com.sbootvue.framework.web.page.TableSupport;

/**
 * web层通用数据处理
 * 
 * @author zhoubc
 */
public class BaseController {
	protected final Logger logger = LoggerFactory.getLogger(BaseController.class);

	/**
	 * 将前台传递过来的日期格式的字符串，自动转化为Date类 型
	 */
	/*@InitBinder
	public void initBinder(WebDataBinder binder) {
		// Date 类型转换
		binder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
			@Override
			public void setAsText(String text) {
				setValue(DateUtils.parseDate(text));
			}
		});
	}*/

	/**
	 * 设置请求分页数据
	 */
	protected void startPage() {
		PageDomain pageDomain = TableSupport.buildPageRequest();
		Integer pageNum = pageDomain.getPageNum();
		Integer pageSize = pageDomain.getPageSize();
		if (StringUtils.isNotNull(pageNum) && StringUtils.isNotNull(pageSize)) {
			String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy());
			PageHelper.startPage(pageNum, pageSize, orderBy);
		}
	}

	/**
	 * 响应请求分页数据
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected TableDataInfo getDataTable(List<?> list) {
		TableDataInfo rspData = new TableDataInfo();
		rspData.setCode(HttpStatus.SUCCESS);
		rspData.setRows(list);
		rspData.setTotal(new PageInfo(list).getTotal());
		return rspData;
	}

	/**
	 * 响应返回结果
	 * 
	 * @param rows 影响行数
	 * @return 操作结果
	 */
	protected AjaxResult toAjax(int rows) {
		return rows > 0 ? AjaxResult.success() : AjaxResult.error();
	}
	
	/**
	 * 获取完整的请求路径，包括：域名，端口，上下文访问路径
	 * 
	 * @return 服务地址
	 */
	public String getUrl() {
		HttpServletRequest request = ServletUtils.getRequest();
		StringBuffer url = request.getRequestURL();
		String contextPath = request.getServletContext().getContextPath();
		return url.delete(url.length() - request.getRequestURI().length(), url.length()).append(contextPath).toString();
	}

}
