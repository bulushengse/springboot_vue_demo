package com.sbootvue.project.system.mapper;

import java.util.List;
import com.sbootvue.project.system.domain.SysTest;

/**
 * 测试信息Mapper接口
 * 
 * @author zhoubc
 * @date 2020-12-01
 */
public interface SysTestMapper {

    /**
     * 查询测试信息
     * 
     * @param testId 测试信息ID
     * @return 测试信息
     */
    public SysTest selectSysTestById(String testId);

    /**
     * 查询测试信息列表
     * 
     * @param sysTest 测试信息
     * @return 测试信息集合
     */
    public List<SysTest> selectSysTestList(SysTest sysTest);

    /**
     * 新增测试信息
     * 
     * @param sysTest 测试信息
     * @return 结果
     */
    public int insertSysTest(SysTest sysTest);

    /**
     * 修改测试信息
     * 
     * @param sysTest 测试信息
     * @return 结果
     */
    public int updateSysTest(SysTest sysTest);

    /**
     * 删除测试信息
     * 
     * @param testId 测试信息ID
     * @return 结果
     */
    public int deleteSysTestById(String testId);

    /**
     * 批量删除测试信息
     * 
     * @param testIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteSysTestByIds(String[] testIds);
}
