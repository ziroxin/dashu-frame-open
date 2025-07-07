<template>
  <div>111456</div>
<!--  <div class="app-container">
    &lt;!&ndash; 新闻表-测试-管理按钮 &ndash;&gt;
    <div style="margin-bottom: 10px;">
      <el-input v-model="searchData.newsTitle" style="width: 150px;margin-right: 10px;"
                class="filter-item" placeholder="请输入新闻标题查询" size="small"
      />
      <el-input v-model="searchData.newsContent" style="width: 150px;margin-right: 10px;"
                class="filter-item" placeholder="请输入新闻内容查询" size="small"
      />
      <el-button v-waves class="filter-item" type="primary" icon="el-icon-search"
                 size="small" @click="searchBtnHandle">查询
      </el-button>
      <el-button v-waves class="filter-item" type="info" icon="el-icon-refresh"
                 size="small" @click="resetTableList">重置
      </el-button>
      <div style="float: right;">
        <el-button v-waves v-permission="'news-news-add'" type="primary" icon="el-icon-plus"
                   @click="openAdd" size="small">新增
        </el-button>
        <el-button v-waves v-permission="'news-news-update'" type="info" icon="el-icon-edit"
                   @click="openUpdate(null)" size="small">修改
        </el-button>
        <el-button v-waves v-permission="'news-news-delete'" type="danger" icon="el-icon-delete"
                   @click="deleteByIds(null)" size="small">删除
        </el-button>
        <el-button v-waves v-permission="'news-news-exportExcel'" type="success" icon="el-icon-printer"
                   @click="exportExcel" size="small">导出Excel
        </el-button>
      </div>
    </div>
    &lt;!&ndash; 新闻表-测试-列表 &ndash;&gt;
    <el-table ref="dataTable" :data="tableData" stripe border @selection-change="handleTableSelectChange">
      <el-table-column type="selection" width="50" align="center" header-align="center"/>
      <el-table-column label="新闻标题" prop="newsTitle" align="center"/>
      <el-table-column label="顺序" prop="orderIndex" align="center"/>
      <el-table-column label="添加时间" prop="createTime" align="center"/>
      <el-table-column label="修改时间" prop="updateTime" align="center"/>
      <el-table-column fixed="right" label="操作" width="120" align="center">
        <template v-slot="scope">
          <el-button type="text" size="mini" style="color: #4dd219;"
                     @click="openView(scope.row)">详情
          </el-button>
          <el-button v-permission="'news-news-update'" size="mini"
                     type="text" @click="openUpdate(scope.row)">修改
          </el-button>
          <el-button v-permission="'news-news-delete'" size="mini" style="color: #f56c6c;"
                     type="text" @click="deleteByIds(scope.row)">删除
          </el-button>
          <br/>
          <el-button size="mini" v-if="scope.row.msgId" style="color: red;"
                     type="text" @click="messageRead(scope.row)">[标记已读]
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    &lt;!&ndash; 新闻表-测试-分页 &ndash;&gt;
    <el-pagination style="text-align: center;margin-top:10px;" layout="total,prev,pager,next,sizes,jumper"
                   :page-size="pager.limit" :current-page="pager.page"
                   :total="pager.totalCount" @current-change="handleCurrentChange"
                   @size-change="handleSizeChange"
    />
    &lt;!&ndash; 添加修改弹窗 &ndash;&gt;
    <el-dialog :title="titleMap[dialogType]" :visible.sync="dialogFormVisible" width="900px"
               :close-on-click-modal="dialogType !== 'view' ? false : true"
               @close="resetTemp" :key="'myDialog'+dialogIndex">
      <el-form ref="dataForm" :model="temp" label-position="right" label-width="50px" :disabled="dialogType==='view'">
        <el-form-item label-width="0px" prop="newsTitle"
                      :rules="[{required: true, message: '新闻标题不能为空'}]">
          <el-input v-model="temp.newsTitle" maxlength="30" :show-word-limit="true"
                    placeholder="请输入新闻标题"/>
        </el-form-item>
        <el-form-item label-width="0px" prop="newsContent"
                      :rules="[{required: true, message: '新闻内容不能为空'}]">
          <my-wang-editor ref="myEditor" v-model="temp.newsContent" height="300px" :key="dialogFormVisible"
                          placeholder="请输入新闻内容"/>
        </el-form-item>
        <el-row>
          <el-col :span="8">
            <el-form-item label="顺序" prop="orderIndex"
                          :rules="[{required: true, message: '顺序不能为空'},{type: 'number', message: '必须为数字'}]">
              <el-input-number v-model="temp.orderIndex" :min="0" step-strictly/>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            &lt;!&ndash; 选择消息发送的用户 &ndash;&gt;
            <el-form-item label="" prop="messageSend" label-width="50px">
              <el-select v-model="messageSendData.type" placeholder="请选择消息发送类型">
                <el-option label="选择用户" value="user"></el-option>
                <el-option label="选择组织机构" value="org"></el-option>
                <el-option label="选择角色" value="role"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="10">
            <message-send v-model="messageSendData.ids" label-width="80px"
                          :scope="messageSendData.scope" :type="messageSendData.type"/>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button v-if="dialogType !== 'view'" v-waves type="primary" @click="saveData">保存</el-button>
        <el-button v-waves @click="dialogFormVisible=false">取消</el-button>
      </div>
    </el-dialog>
  </div>-->
</template>
