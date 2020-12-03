package com.sbootvue.project.system.controller;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.sbootvue.common.annotion.Log;
import com.sbootvue.common.enums.BusinessType;
import com.sbootvue.project.system.domain.SysTest;
import com.sbootvue.project.system.service.ISysTestService;
import com.sbootvue.framework.web.controller.BaseController;
import com.sbootvue.framework.web.domain.AjaxResult;
import com.sbootvue.common.utils.ExcelUtil;
import com.sbootvue.common.utils.SecurityUtils;
import com.sbootvue.framework.web.page.TableDataInfo;

/**
 * 测试信息Controller
 * 
 * @author zhoubc
 * @date 2020-12-01
 */
@RestController
@RequestMapping("/system/test")
public class SysTestController extends BaseController {

    @Autowired
    private ISysTestService sysTestService;

    /**
     * 查询测试信息列表
     */
    @PreAuthorize("@ss.hasPermi('system:test:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysTest sysTest) {
        startPage();
        List<SysTest> list = sysTestService.selectSysTestList(sysTest);
        return getDataTable(list);
    }

    /**
     * 导出测试信息列表
     */
    @PreAuthorize("@ss.hasPermi('system:test:export')")
    @Log(title = "测试信息", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(SysTest sysTest) {
        List<SysTest> list = sysTestService.selectSysTestList(sysTest);
        ExcelUtil<SysTest> util = new ExcelUtil<SysTest>(SysTest.class);
        return util.exportExcel(list, "test");
    }

    /**
     * 获取测试信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:test:query')")
    @GetMapping(value = "/{testId}")
    public AjaxResult getInfo(@PathVariable("testId") String testId) {
        return AjaxResult.success(sysTestService.selectSysTestById(testId));
    }

    /**
     * 新增测试信息
     */
    @PreAuthorize("@ss.hasPermi('system:test:add')")
    @Log(title = "测试信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysTest sysTest) {
    	sysTest.setCreateBy(SecurityUtils.getUsername());
        return toAjax(sysTestService.insertSysTest(sysTest));
    }

    /**
     * 修改测试信息
     */
    @PreAuthorize("@ss.hasPermi('system:test:edit')")
    @Log(title = "测试信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysTest sysTest) {
    	sysTest.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(sysTestService.updateSysTest(sysTest));
    }

    /**
     * 删除测试信息
     */
    @PreAuthorize("@ss.hasPermi('system:test:remove')")
    @Log(title = "测试信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{testIds}")
    public AjaxResult remove(@PathVariable String[] testIds) {
        return toAjax(sysTestService.deleteSysTestByIds(testIds));
    }
}
