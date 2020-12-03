package com.sbootvue.project.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sbootvue.project.system.mapper.SysTestMapper;
import com.sbootvue.project.system.domain.SysTest;
import com.sbootvue.project.system.service.ISysTestService;

/**
 * 测试信息Service业务层处理
 * 
 * @author zhoubc
 * @date 2020-12-01
 */
@Service
public class SysTestServiceImpl implements ISysTestService {

    @Autowired
    private SysTestMapper sysTestMapper;

    /**
     * 查询测试信息
     * 
     * @param testId 测试信息ID
     * @return 测试信息
     */
    @Override
    public SysTest selectSysTestById(String testId) {
        return sysTestMapper.selectSysTestById(testId);
    }

    /**
     * 查询测试信息列表
     * 
     * @param sysTest 测试信息
     * @return 测试信息
     */
    @Override
    public List<SysTest> selectSysTestList(SysTest sysTest) {
        return sysTestMapper.selectSysTestList(sysTest);
    }

    /**
     * 新增测试信息
     * 
     * @param sysTest 测试信息
     * @return 结果
     */
    @Override
    public int insertSysTest(SysTest sysTest) {
        return sysTestMapper.insertSysTest(sysTest);
    }

    /**
     * 修改测试信息
     * 
     * @param sysTest 测试信息
     * @return 结果
     */
    @Override
    public int updateSysTest(SysTest sysTest) {
        return sysTestMapper.updateSysTest(sysTest);
    }

    /**
     * 批量删除测试信息
     * 
     * @param testIds 需要删除的测试信息ID
     * @return 结果
     */
    @Override
    public int deleteSysTestByIds(String[] testIds) {
        return sysTestMapper.deleteSysTestByIds(testIds);
    }

    /**
     * 删除测试信息信息
     * 
     * @param testId 测试信息ID
     * @return 结果
     */
    @Override
    public int deleteSysTestById(String testId) {
        return sysTestMapper.deleteSysTestById(testId);
    }
 
}
