<template>
  <div class="app-container">
    <!-- 新闻表-测试-管理按钮 -->
    <div class="searchPanel">
      <div class="searchForm">
        <el-input v-model="searchData.newsTitle" class="searchInput w-50!" placeholder="请输入新闻标题查询"/>
        <el-input v-model="searchData.newsContent" class="searchInput w-50!" placeholder="请输入新闻内容查询"/>
        <base-button class="searchBtn" type="primary" icon="el-icon-search" @click="searchBtnHandle">查询</base-button>
        <base-button class="searchBtn" type="info" icon="reset" @click="resetTableList">重置</base-button>
      </div>
      <div class="operatePanel w-685px!">
        <base-button type="primary" icon="el-icon-plus" @click="openAdd"
                     v-permission="'news-news-add'">新增
        </base-button>
        <base-button type="info" icon="el-icon-edit" @click="openUpdate(null)"
                     v-permission="'news-news-update'">修改
        </base-button>
        <base-button type="danger" icon="el-icon-delete" @click="deleteByIds(null)"
                     v-permission="'news-news-delete'">删除
        </base-button>
        <base-button type="success" icon="el-icon-printer" @click="exportExcel"
                     v-permission="'news-news-exportExcel'">导出Excel
        </base-button>
      </div>
    </div>
    <!-- 新闻表-测试-列表 -->
    <el-table ref="dataTableRef" :data="tableData" stripe border v-loading="isLoading"
              @selection-change="handleTableSelectChange">
      <el-table-column type="selection" width="50" align="center" header-align="center"/>
      <el-table-column label="新闻标题" prop="newsTitle" align="center"/>
      <el-table-column label="顺序" prop="orderIndex" align="center"/>
      <el-table-column label="添加时间" prop="createTime" align="center"/>
      <el-table-column label="修改时间" prop="updateTime" align="center"/>
      <el-table-column fixed="right" label="操作" width="140" align="center">
        <template #default="scope">
          <base-button link size="small" style="color: #13ce66" @click="openView(scope.row)">详情</base-button>
          <base-button v-permission="'news-news-update'"
                       link size="small" @click="openUpdate(scope.row)">修改
          </base-button>
          <base-button v-permission="'news-news-delete'" style="color: #ff6d6d"
                       link size="small" @click="deleteByIds(scope.row)">删除
          </base-button>
          <br/>
          <base-button v-if="scope.row.msgId" style="color: red"
                       link size="small" @click="messageRead(scope.row)">[标记已读]
          </base-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 新闻表-测试-分页 -->
    <el-pagination class="flex justify-center mt-10px" layout="total,prev,pager,next,sizes,jumper"
                   :page-size="pager.limit" :current-page="pager.page"
                   :total="pager.totalCount" @current-change="handleCurrentChange"
                   @size-change="handleSizeChange"/>
    <!-- 添加修改弹窗 -->
    <el-dialog :title="titleMap[dialogType]" v-model="dialogFormVisible" width="900px" :key="'myDialog'+dialogIndex"
               :close-on-click-modal="dialogType==='view'" draggable @close="closeDialog">
      <el-form ref="dataFormRef" :model="formData" label-width="60px" :disabled="dialogType==='view'">
        <el-form-item label-width="0px" prop="newsTitle" :rules="[{required:true,message:'新闻标题不能为空'}]">
          <el-input v-model="formData.newsTitle" maxlength="30" :show-word-limit="true" placeholder="请输入新闻标题"/>
        </el-form-item>
        <el-form-item label-width="0px" prop="newsContent" :rules="[{required:true,message:'新闻内容不能为空'}]">
          <my-wang-editor v-model="formData.newsContent" :key="'myWangEditor'+dialogIndex"
                          height="300px" :disabled="dialogType==='view'" placeholder="请输入新闻内容"/>
        </el-form-item>
        <el-row>
          <el-col :span="8">
            <el-form-item label="顺序" prop="orderIndex"
                          :rules="[{required: true, message: '顺序不能为空'},{type: 'number', message: '必须为数字'}]">
              <el-input-number v-model="formData.orderIndex" :min="0" step-strictly/>
            </el-form-item>
          </el-col>
          <template v-if="dialogType==='add'">
            <el-col :span="6">
              <!-- 选择消息发送的用户 -->
              <el-form-item label="" prop="messageSend" label-width="50px">
                <el-select v-model="messageSendData.type" placeholder="请选择消息发送类型">
                  <el-option label="选择用户" value="user"/>
                  <el-option label="选择组织机构" value="org"/>
                  <el-option label="选择角色" value="role"/>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="10">
              <message-send v-model="messageSendData.ids" label-width="80px"
                            :scope="messageSendData.scope" :type="messageSendData.type"/>
            </el-col>
          </template>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <base-button v-if="dialogType!=='view'" type="primary" icon="el-icon-check" @click="saveData">保存
          </base-button>
          <base-button icon="el-icon-close" @click="dialogFormVisible=false">取消</base-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import request from '@/utils/request'
import downloadUtil from '@/utils/download-util'
import { ElMessage, ElMessageBox, ElNotification } from 'element-plus'
import { MyWangEditor } from '@/components/MyWangEditor'
import { MessageSend } from '@/components/MessageSend'
import { useMessageStoreWithOut } from '@/store/modules/message'

// ==================== 1生命周期start ====================
onMounted(() => {
  loadTableList()
})
// ==================== 1生命周期end ====================


// ==================== 2查询_表格_分页_排序start ====================
// 分页数据
const pager = ref({page: 1, limit: 10, totalCount: 0})
// 表格
const dataTableRef = ref()
const tableData = ref([])
// 查询表单数据
const searchData = ref({})
// 选中行
const tableSelectRows = ref([])
// 是否加载中
const isLoading = ref(false)

// 查询按钮
const searchBtnHandle = () => {
  pager.value.page = 1
  loadTableList()
}
// 重置
const resetTableList = () => {
  pager.value.page = 1
  searchData.value = {}
  loadTableList()
}
// 加载表格
const loadTableList = () => {
  isLoading.value = true
  const params = {...pager.value, params: JSON.stringify(searchData.value)}
  request({url: '/news/news/list', method: 'get', params}).then((response) => {
    const {data} = response
    pager.value.totalCount = data.total
    tableData.value = data.records
    isLoading.value = false
  })
}
// 监听选中行
const handleTableSelectChange = (rows) => {
  tableSelectRows.value = rows
}
// 监听分页
const handleCurrentChange = (page) => {
  pager.value.page = page
  loadTableList()
}
// 分页条数改变
const handleSizeChange = (size) => {
  pager.value.limit = size
  loadTableList()
}
// 删除
const deleteByIds = (row) => {
  if (row) {
    dataTableRef.value.clearSelection()
    dataTableRef.value.toggleRowSelection(row, true)
  }
  if (tableSelectRows.value.length <= 0) {
    ElMessage({message: '请选择一条数据删除！', type: 'warning', grouping: true})
  } else {
    ElMessageBox.confirm('确定要删除吗?', '删除提醒', {
      confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
    }).then(() => {
      // 执行删除
      const data = tableSelectRows.value.map(r => r.newsId)
      request({url: '/news/news/delete', method: 'post', data}).then(response => {
        ElNotification({message: '删除成功！', title: '操作成功', type: 'success'})
        loadTableList()
      })
    })
  }
}
// ==================== 2查询_表格_分页_排序end ====================


// ==================== 3新增_修改_详情start ====================
// 弹窗标题
const titleMap = {add: '添加新闻表-测试', update: '修改新闻表-测试', view: '查看详情'}
// 添加/修改模式（add/update）
const dialogType = ref('')
// 弹窗显示隐藏
const dialogFormVisible = ref(false)
// 弹窗索引
const dialogIndex = ref(0)
// 表单临时数据
const dataFormRef = ref()
const formData = ref({orderIndex: 0})

// 关闭弹窗（清空表单temp数据）
const closeDialog = () => {
  dialogFormVisible.value = false
  formData.value = {orderIndex: 0}
  messageSendData.ids = []
  dialogIndex.value++
}
// 打开添加窗口
const openAdd = () => {
  dialogFormVisible.value = true
  dialogType.value = 'add'
  nextTick(() => { dataFormRef.value.clearValidate() })
}
// 打开修改窗口
const openUpdate = (row) => {
  if (row) {
    dataTableRef.value.clearSelection()
    dataTableRef.value.toggleRowSelection(row, true)
  }
  if (tableSelectRows.value.length <= 0) {
    ElMessage({message: '请选择一条数据修改！', type: 'warning', grouping: true})
  } else if (tableSelectRows.value.length > 1) {
    ElMessage({message: '修改时，只允许选择一条数据！！', type: 'warning', grouping: true})
  } else {
    // 修改弹窗
    formData.value = Object.assign({}, tableSelectRows.value[0])
    dialogType.value = 'update'
    dialogFormVisible.value = true
    nextTick(() => { dataFormRef.value.clearValidate() })
  }
}
// 打开查看窗口
const openView = (row) => {
  dialogFormVisible.value = true
  formData.value = Object.assign({}, row)
  dialogType.value = 'view'
  nextTick(() => { dataFormRef.value.clearValidate() })
}
// 添加、修改，保存事件
const saveData = () => {
  dataFormRef.value.validate((valid) => {
    if (valid) {
      let data = {...formData.value}
      if (dialogType.value === 'update') {
        request({url: '/news/news/update', method: 'post', data}).then(response => {
          ElNotification({message: '修改成功！', title: '操作成功', type: 'success'})
          loadTableList()
          dialogFormVisible.value = false
        })
      } else {
        if (messageSendData.ids.length > 0) {
          // 有选择消息，则填充消息实体字段
          data = {
            ...data,
            msgTitle: '发表了新闻《' + data.newsTitle + '》',
            msgContent: '发表了新闻《' + data.newsTitle + '》',
            msgRouter: '/demo/news', // 菜单管理-修改-菜单地址
            permissionName: 'news-news', // 菜单管理-修改-菜单标记
            toType: messageSendData.type,
            toIds: messageSendData.ids
          }
        }
        // 添加信息时，增加通知用户字段
        request({url: '/news/news/add', method: 'post', data}).then(response => {
          ElNotification({message: '添加成功！', title: '操作成功', type: 'success'})
          loadTableList()
          dialogFormVisible.value = false
        })
      }
    }
  })
}
// ==================== 3新增_修改_详情end ====================


// ==================== 4其他（消息相关、导出Excel）start ====================
// 消息实体数据
const messageSendData = reactive({
  ids: [], // 根据用户选择自动获取
  type: 'user', // 发送用户类型：user=用户；org=组织机构；role=角色（不根据scope查询）
  scope: 'all' // all=全部；children=下级；selfAndChildren=本机构及下级
})
// 标记消息已读按钮
const messageRead = (row) => {
  const params = {msgId: row.msgId}
  request({url: '/message/zMessage/read', method: 'get', params}).then((response) => {
    // 刷新消息计数
    useMessageStoreWithOut().refreshAllMessageCount()
    loadTableList()
  })
}
// 导出Excel文件
const exportExcel = () => {
  const params = {params: JSON.stringify(searchData.value)}
  downloadUtil.download('/news/news/export/excel', params, '新闻表-测试.xlsx')
}
// ==================== 4其他（消息相关、导出Excel）end ====================
</script>
