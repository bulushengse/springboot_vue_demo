<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="测试等级" prop="testRank">
        <el-select v-model="queryParams.testRank" placeholder="请选择测试等级" clearable size="small">
          <el-option
            v-for="dict in testRankOptions"
            :key="dict.dictValue"
            :label="dict.dictLabel"
            :value="dict.dictValue"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="测试结果" prop="testResult">
        <el-select v-model="queryParams.testResult" placeholder="请选择测试结果" clearable size="small">
          <el-option
            v-for="dict in testResultOptions"
            :key="dict.dictValue"
            :label="dict.dictLabel"
            :value="dict.dictValue"
          />
        </el-select>
      </el-form-item>
	  <el-form-item label="创建时间" prop="createTime">
       <el-date-picker clearable size="small" style="width: 240px"
	      v-model="dateRange"
	      type="daterange"
	      value-format="yyyy-MM-dd"
	      start-placeholder="开始日期"
	      end-placeholder="结束日期">
    	</el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="cyan" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['system:test:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['system:test:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:test:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:test:export']"
        >导出</el-button>
      </el-col>
	  <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="testList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="测试ID" align="center" prop="testId" />
      <el-table-column label="测试内容" align="center" prop="testContent" />
      <el-table-column label="测试等级" align="center" prop="testRank" :formatter="testRankFormat" />
      <el-table-column label="测试结果" align="center" prop="testResult" :formatter="testResultFormat" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{mi}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" width="180" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
           @click="handleDetail(scope.row)"
            v-hasPermi="['system:user:query']"
          >详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:test:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:test:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改测试信息对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="测试内容" prop="testContent">
          <el-input v-model="form.testContent" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="测试等级" prop="testRank">
          <el-select v-model="form.testRank" placeholder="请选择测试等级">
            <el-option
              v-for="dict in testRankOptions"
              :key="dict.dictValue"
              :label="dict.dictLabel"
              :value="dict.dictValue"            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="测试结果" prop="testResult">
          <el-select v-model="form.testResult" placeholder="请选择测试结果">
            <el-option
              v-for="dict in testResultOptions"
              :key="dict.dictValue"
              :label="dict.dictLabel"
              :value="dict.dictValue"            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="fid" prop="fid">
          <el-input v-model="form.fid" placeholder="请输入fid" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 详情 -->
    <el-dialog :title="title" :visible.sync="openView" width="600px">
      <el-form ref="form" :model="form" label-width="120px" size="mini">
        <el-form-item label="测试ID:">{{form.testId}}</el-form-item>
        <el-form-item label="测试内容:">{{form.testContent}}</el-form-item>
        <el-form-item label="测试等级:">{{testRankName}}</el-form-item>
        <el-form-item label="测试结果:">{{testResultName}}</el-form-item>
        <el-form-item label="创建者:">{{form.createBy}}</el-form-item>
        <el-form-item label="创建时间:">{{form.createTime}}</el-form-item>
        <el-form-item label="更新者:">{{form.updateBy}}</el-form-item>
        <el-form-item label="更新时间:">{{form.updateTime}}</el-form-item>
        <el-form-item label="备注:">{{form.remark}}</el-form-item>
        <el-form-item label="fid:">{{form.fid}}</el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="openView = false">关 闭</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import { listTest, getTest, delTest, addTest, updateTest, exportTest } from "@/api/system/test";

export default {
  name: "Test",
  components: {
  },
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 测试信息表格数据
      testList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 是否显示弹出层 详情页
      openView: false,
      // 日期范围
      dateRange: [],
      // 测试等级字典
      testRankOptions: [],
      // 测试结果字典
      testResultOptions: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        testContent: null,
        testRank: null,
        testResult: null,
        createTime: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        testId: [
          { required: true, message: "测试ID不能为空", trigger: "blur" }
        ],
      }
    };
  },
  computed: {
   testRankName(){
      for(const obj of this.testRankOptions){
        if(obj.dictValue == this.form.testRank){
          return obj.dictLabel;
        }
      }
      return '';
    },
   testResultName(){
      for(const obj of this.testResultOptions){
        if(obj.dictValue == this.form.testResult){
          return obj.dictLabel;
        }
      }
      return '';
    },
  },
  created() {
    this.getList();
    this.getDicts("sys_notice_type").then(response => {
      this.testRankOptions = response.data;
    });
    this.getDicts("sys_notice_status").then(response => {
      this.testResultOptions = response.data;
    });
  },
  methods: {
    /** 查询测试信息列表 */
    getList() {
      this.loading = true;
      listTest(this.addDateRange(this.queryParams, this.dateRange)).then(response => {
        this.testList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 测试等级字典翻译
    testRankFormat(row, column) {
      return this.selectDictLabel(this.testRankOptions, row.testRank);
    },
    // 测试结果字典翻译
    testResultFormat(row, column) {
      return this.selectDictLabel(this.testResultOptions, row.testResult);
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        testId: null,
        testContent: null,
        testRank: null,
        testResult: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
        remark: null,
        fid: null
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.testId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 详情 */
    handleDetail(row){
      this.reset();
      const testId = row.testId || this.ids
      getTest(testId).then(response => {
        this.form = response.data;
        this.openView = true;
        this.title = "测试信息详情";
      });
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加测试信息";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const testId = row.testId || this.ids
      getTest(testId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改测试信息";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.testId != null) {
            updateTest(this.form).then(response => {
              this.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addTest(this.form).then(response => {
              this.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const testIds = row.testId || this.ids;
      this.$confirm('是否确认删除测试信息编号为"' + testIds + '"的数据项?', "警告", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(function() {
          return delTest(testIds);
        }).then(() => {
          this.getList();
          this.msgSuccess("删除成功");
        })
    },
    /** 导出按钮操作 */
    handleExport() {
      const queryParams = this.queryParams;
      this.$confirm('是否确认导出所有测试信息数据项?', "警告", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(function() {
          return exportTest(queryParams);
        }).then(response => {
          this.download(response.msg);
        })
    }
  }
};
</script>
