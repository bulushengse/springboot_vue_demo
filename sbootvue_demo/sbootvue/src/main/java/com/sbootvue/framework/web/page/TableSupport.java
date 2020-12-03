package com.sbootvue.framework.web.page;

import com.sbootvue.common.utils.ServletUtils;

/**
 * 表格分页参数数据处理
 * 
 * @author zhoubc
 */
public class TableSupport {
	/**
	 * 当前记录起始索引
	 */
	public static final String PAGE_NUM = "pageNum";

	/**
	 * 每页显示记录数
	 */
	public static final String PAGE_SIZE = "pageSize";

	/**
	 * 排序列
	 */
	public static final String ORDER_BY_COLUMN = "orderByColumn";

	/**
	 * 排序的方向 "desc" 或者 "asc".
	 */
	public static final String IS_ASC = "isAsc";

	/**
	 * 组装分页对象 PageDomain
	 */
	public static PageDomain buildPageRequest() {
		PageDomain pageDomain = new PageDomain();
		pageDomain.setPageNum(ServletUtils.getParameterToInt(PAGE_NUM));
		pageDomain.setPageSize(ServletUtils.getParameterToInt(PAGE_SIZE));
		pageDomain.setOrderByColumn(ServletUtils.getParameter(ORDER_BY_COLUMN));
		pageDomain.setIsAsc(ServletUtils.getParameter(IS_ASC));
		return pageDomain;
	}

}
