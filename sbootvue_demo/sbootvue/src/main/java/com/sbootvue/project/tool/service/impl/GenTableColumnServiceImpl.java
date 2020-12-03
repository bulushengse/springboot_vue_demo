package com.sbootvue.project.tool.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sbootvue.common.utils.Convert;
import com.sbootvue.project.tool.domain.GenTableColumn;
import com.sbootvue.project.tool.mapper.GenTableColumnMapper;
import com.sbootvue.project.tool.service.IGenTableColumnService;

/**
 * 业务字段 服务层实现
 * 
 * @author zhoubc
 */
@Service
public class GenTableColumnServiceImpl implements IGenTableColumnService {
	@Autowired
	private GenTableColumnMapper genTableColumnMapper;

	/**
	 * 查询业务字段列表
	 * 
	 * @param genTableColumn 业务字段编号
	 * @return 业务字段集合
	 */
	@Override
	public List<GenTableColumn> selectGenTableColumnListByTableId(String tableId) {
		return genTableColumnMapper.selectGenTableColumnListByTableId(tableId);
	}

	/**
	 * 新增业务字段
	 * 
	 * @param genTableColumn 业务字段信息
	 * @return 结果
	 */
	@Override
	public int insertGenTableColumn(GenTableColumn genTableColumn) {
		return genTableColumnMapper.insertGenTableColumn(genTableColumn);
	}

	/**
	 * 修改业务字段
	 * 
	 * @param genTableColumn 业务字段信息
	 * @return 结果
	 */
	@Override
	public int updateGenTableColumn(GenTableColumn genTableColumn) {
		return genTableColumnMapper.updateGenTableColumn(genTableColumn);
	}

	/**
	 * 删除业务字段对象
	 * 
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	@Override
	public int deleteGenTableColumnByTableIds(String ids) {
		return genTableColumnMapper.deleteGenTableColumnByTableIds(Convert.toStrArray(ids));
	}
}