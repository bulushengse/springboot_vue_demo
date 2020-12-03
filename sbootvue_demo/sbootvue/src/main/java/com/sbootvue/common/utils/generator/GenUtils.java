package com.sbootvue.common.utils.generator;

import java.util.Arrays;
import org.apache.commons.lang3.RegExUtils;

import com.sbootvue.common.constant.GenConstants;
import com.sbootvue.common.utils.string.StringUtils;
import com.sbootvue.framework.config.GenConfig;
import com.sbootvue.project.tool.domain.GenTable;
import com.sbootvue.project.tool.domain.GenTableColumn;

/**
 * 代码生成器 工具类
 * 
 * @author zhoubc
 */
public class GenUtils {
	/**
	 * 初始化表信息
	 */
	public static void initTable(GenTable genTable, String operName) {
		genTable.setClassName(convertClassName(genTable.getTableName()));
		genTable.setPackageName(GenConfig.getPackageName());
		genTable.setModuleName(getModuleName(GenConfig.getPackageName()));
		genTable.setBusinessName(getBusinessName(genTable.getTableName()));
		genTable.setFunctionName(replaceText(genTable.getTableComment()));
		genTable.setFunctionAuthor(GenConfig.getAuthor());
		genTable.setCreateBy(operName);
	}

	/**
	 * 初始化列属性字段
	 */
	public static void initColumnField(GenTableColumn column, GenTable table) {
		String columnName = column.getColumnName();
		column.setTableId(table.getTableId());
		column.setCreateBy(table.getCreateBy());
		// 设置java字段名
		column.setJavaField(StringUtils.toCamelCase(columnName));

		String dataType = column.getColumnType();
		if (arraysContains(GenConstants.COLUMNTYPE_STR, dataType)) {
			// 字符串长度超过500设置为文本域
			Integer dataLength = column.getDataLength();
			String htmlType = dataLength >= 500 ? GenConstants.HTML_TEXTAREA : GenConstants.HTML_INPUT;
			column.setJavaType(GenConstants.TYPE_STRING);
			column.setHtmlType(htmlType);
			
			if(StringUtils.endsWithIgnoreCase(columnName, "TIME") || StringUtils.endsWithIgnoreCase(columnName, "DATE")) {
				column.setHtmlType(GenConstants.HTML_DATETIME);
			}
		}else if (arraysContains(GenConstants.COLUMNTYPE_NUMBER, dataType)) {
			Integer dataPrecision = column.getDataPrecision();
			Integer dataScale = column.getDataScale();
			if(dataPrecision <= 10 && dataScale == 0) {
				// 如果是整形
				column.setJavaType(GenConstants.TYPE_INTEGER);
			}else if(dataPrecision > 10 && dataScale == 0){
				// 如果是长整形
				column.setJavaType(GenConstants.TYPE_LONG);
			}else if(dataScale > 0) {
				// 如果是浮点型
				column.setJavaType(GenConstants.TYPE_DOUBLE);
				if(StringUtils.endsWithIgnoreCase(columnName, "PRICE") || StringUtils.endsWithIgnoreCase(columnName, "AMOUNT")) {
					column.setJavaType(GenConstants.TYPE_BIGDECIMAL);
				}
			}
			
			if(StringUtils.endsWithIgnoreCase(columnName, "ID")) {
				column.setJavaType(GenConstants.TYPE_STRING);
			}
			
			column.setHtmlType(GenConstants.HTML_INPUT);
			
		}else if (arraysContains(GenConstants.COLUMNTYPE_TIME, dataType)) {
			column.setJavaType(GenConstants.TYPE_STRING);
			column.setHtmlType(GenConstants.HTML_DATETIME);
		}
		
		// 插入字段
		if(!arraysContains(GenConstants.COLUMNNAME_NOT_INSERT, columnName)) {
			column.setIsInsert(GenConstants.REQUIRE);
		}else {
			column.setIsInsert(GenConstants.NOT_REQUIRE);
		}
		// 编辑字段
		if (!arraysContains(GenConstants.COLUMNNAME_NOT_EDIT, columnName) && !column.isPk()) {
			column.setIsEdit(GenConstants.REQUIRE);
		}else{
			column.setIsEdit(GenConstants.NOT_REQUIRE);
		}
		// 列表字段
		if (!arraysContains(GenConstants.COLUMNNAME_NOT_LIST, columnName) && !column.isPk()) {
			column.setIsList(GenConstants.REQUIRE);
		}else{
			column.setIsList(GenConstants.NOT_REQUIRE);
		}
		// 查询字段
		if (!arraysContains(GenConstants.COLUMNNAME_NOT_QUERY, columnName) && !column.isPk()) {
			column.setIsQuery(GenConstants.REQUIRE);
		}else{
			column.setIsQuery(GenConstants.NOT_REQUIRE);
		}
		// 查询字段类型
		if (StringUtils.endsWithIgnoreCase(columnName, "name")) {
			column.setQueryType(GenConstants.QUERY_LIKE);
		}
		if (StringUtils.endsWithIgnoreCase(columnName, "time")) {
			column.setQueryType(GenConstants.QUERY_BETWEEN);
		}
		// 状态字段设置单选框
		if (StringUtils.endsWithIgnoreCase(columnName, "status")) {
			column.setHtmlType(GenConstants.HTML_RADIO);
		}
		// 类型&性别字段设置下拉框
		else if (StringUtils.endsWithIgnoreCase(columnName, "type") || StringUtils.endsWithIgnoreCase(columnName, "sex")) {
			column.setHtmlType(GenConstants.HTML_SELECT);
		}
	}

	/**
	 * 校验数组是否包含指定值
	 * 
	 * @param arr 数组
	 * @param targetValue 值
	 * @return 是否包含
	 */
	public static boolean arraysContains(String[] arr, String targetValue) {
		return Arrays.asList(arr).contains(targetValue.toLowerCase());
	}

	/**
	 * 获取模块名
	 * 
	 * @param packageName 包名
	 * @return 模块名
	 */
	public static String getModuleName(String packageName) {
		int lastIndex = packageName.lastIndexOf(".");
		int nameLength = packageName.length();
		String moduleName = StringUtils.substring(packageName, lastIndex + 1, nameLength);
		return moduleName;
	}

	/**
	 * 获取业务名
	 * 
	 * @param tableName 表名
	 * @return 业务名
	 */
	public static String getBusinessName(String tableName) {
		int lastIndex = tableName.lastIndexOf("_");
		int nameLength = tableName.length();
		String businessName = StringUtils.substring(tableName, lastIndex + 1, nameLength);
		return businessName;
	}

	/**
	 * 表名转换成Java类名
	 * 
	 * @param tableName 表名称
	 * @return 类名
	 */
	public static String convertClassName(String tableName) {
		boolean autoRemovePre = GenConfig.getAutoRemovePre();
		String tablePrefix = GenConfig.getTablePrefix();
		if (autoRemovePre && StringUtils.isNotEmpty(tablePrefix)) {
			String[] searchList = StringUtils.split(tablePrefix, ",");
			tableName = replaceFirst(tableName, searchList);
		}
		return StringUtils.convertToCamelCase(tableName);
	}

	/**
	 * 批量替换前缀
	 * 
	 * @param replacementm 替换值
	 * @param searchList 替换列表
	 * @return
	 */
	public static String replaceFirst(String replacementm, String[] searchList) {
		String text = replacementm;
		for (String searchString : searchList) {
			if (replacementm.startsWith(searchString)) {
				text = replacementm.replaceFirst(searchString, "");
				break;
			}
		}
		return text;
	}

	/**
	 * 关键字替换
	 * 
	 * @param name 需要被替换的名字
	 * @return 替换后的名字
	 */
	public static String replaceText(String text) {
		return RegExUtils.replaceAll(text, "(?:表|若依)", "");
	}

	/**
	 * 获取数据库类型字段
	 * 
	 * @param columnType 列类型
	 * @return 截取后的列类型
	 */
	/*	public static String getDbType(String columnType) {
			if (StringUtils.indexOf(columnType, "(") > 0) {
				return StringUtils.substringBefore(columnType, "(");
			} else {
				return columnType;
			}
		}*/

	/**
	 * 获取字段长度
	 * 
	 * @param columnType 列类型
	 * @return 截取后的列类型
	 */
	/*	public static Integer getColumnLength(String columnType) {
			if (StringUtils.indexOf(columnType, "(") > 0) {
				String length = StringUtils.substringBetween(columnType, "(", ")");
				return Integer.valueOf(length);
			} else {
				return 0;
			}
		}*/
}