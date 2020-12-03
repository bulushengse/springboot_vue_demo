package com.sbootvue.project.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.sbootvue.common.annotion.Excel;
import com.sbootvue.common.annotion.Excel.ColumnType;
import com.sbootvue.framework.web.domain.BaseEntity;

/**
 * 测试信息对象 sys_test
 * 
 * @author zhoubc
 * @date 2020-12-01
 */
public class SysTest extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 测试ID */
    @Excel(name = "测试ID", cellType = ColumnType.NUMERIC)
    private String testId;

    /** 测试内容 */
    @Excel(name = "测试内容")
    private String testContent;

    /** 测试等级 */
    @Excel(name = "测试等级")
    private String testRank;

    /** 测试结果 */
    @Excel(name = "测试结果")
    private String testResult;

    /** fid */
    @Excel(name = "fid")
    private String fid;

    public void setTestId(String testId) {
        this.testId = testId;
    }

    public String getTestId(){
        return testId;
    }
    public void setTestContent(String testContent) {
        this.testContent = testContent;
    }

    public String getTestContent(){
        return testContent;
    }
    public void setTestRank(String testRank) {
        this.testRank = testRank;
    }

    public String getTestRank(){
        return testRank;
    }
    public void setTestResult(String testResult) {
        this.testResult = testResult;
    }

    public String getTestResult(){
        return testResult;
    }
    public void setFid(String fid) {
        this.fid = fid;
    }

    public String getFid(){
        return fid;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("testId", getTestId())
            .append("testContent", getTestContent())
            .append("testRank", getTestRank())
            .append("testResult", getTestResult())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("fid", getFid())
            .toString();
    }
}
