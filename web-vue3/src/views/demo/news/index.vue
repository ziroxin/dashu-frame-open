<template>
  <div class="app-container">
    <!-- 新闻表-测试-管理按钮 -->
    <div style="margin-bottom: 10px;">
      <el-input v-model="searchData.newsTitle" style="width: 150px;margin-right: 10px;"
                class="filter-item" placeholder="请输入新闻标题查询"/>
      <el-input v-model="searchData.newsContent" style="width: 150px;margin-right: 10px;"
                class="filter-item" placeholder="请输入新闻内容查询"/>
      <base-button v-waves class="filter-item" type="primary"
                   icon="el-icon-search" @click="searchBtnHandle">查询
      </base-button>
      <base-button v-waves class="filter-item" type="info"
                   icon="el-icon-refresh" @click="resetTableList">重置
      </base-button>
      <div style="float: right;">
        <base-button v-permission="'news-news-add'" type="primary"
                     icon="el-icon-plus" @click="openAdd">新增
        </base-button>
        <base-button v-permission="'news-news-update'" type="info"
                     icon="el-icon-edit" @click="openUpdate(null)">修改
        </base-button>
        <base-button v-permission="'news-news-delete'" type="danger"
                     icon="el-icon-delete" @click="deleteByIds(null)">删除
        </base-button>
        <base-button v-permission="'news-news-exportExcel'" type="success"
                     icon="el-icon-printer" @click="exportExcel">导出Excel
        </base-button>
      </div>
    </div>
    <!-- 新闻表-测试-列表 -->
    <el-table ref="dataTable" :data="tableData" stripe border @selection-change="handleTableSelectChange">
      <el-table-column type="selection" width="50" align="center" header-align="center"/>
      <el-table-column label="新闻标题" prop="newsTitle" align="center"/>
      <el-table-column label="顺序" prop="orderIndex" align="center"/>
      <el-table-column label="添加时间" prop="createTime" align="center"/>
      <el-table-column label="修改时间" prop="updateTime" align="center"/>
      <el-table-column fixed="right" label="操作" width="140" align="center">
        <template v-slot="scope">
          <el-button size="small" link style="color: #4dd219;" @click="openView(scope.row)">详情
          </el-button>
          <el-button v-permission="'news-news-update'" size="small" link @click="openUpdate(scope.row)">修改
          </el-button>
          <el-button v-permission="'news-news-delete'" size="small" link style="color: #f56c6c;"
                     @click="deleteByIds(scope.row)">删除
          </el-button>
          <br/>
          <el-button v-if="scope.row.msgId" size="small" link style="color: red;"
                     @click="messageRead(scope.row)">[标记已读]
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 新闻表-测试-分页 -->
    <el-pagination class="flex justify-center mt-10px" layout="total,prev,pager,next,sizes,jumper"
                   :page-size="pager.limit" :current-page="pager.page"
                   :total="pager.totalCount" @current-change="handleCurrentChange"
                   @size-change="handleSizeChange"
    />
    <!-- 添加修改弹窗 -->
    <el-dialog :title="titleMap[dialogType]" v-model="dialogFormVisible" width="900px"
               :close-on-click-modal="dialogType !== 'view' ? false : true"
               @close="resetTemp" :key="'myDialog'+dialogIndex">
      <el-form ref="dataForm" :model="temp" label-position="right" label-width="60px" :disabled="dialogType==='view'">
        <el-form-item label-width="0px" prop="newsTitle"
                      :rules="[{required: true, message: '新闻标题不能为空'}]">
          <el-input v-model="temp.newsTitle" maxlength="30" :show-word-limit="true"
                    placeholder="请输入新闻标题"/>
        </el-form-item>
        <el-form-item label-width="0px" prop="newsContent"
                      :rules="[{required: true, message: '新闻内容不能为空'}]">
          <my-wang-editor ref="myEditor" v-model="temp.newsContent" height="300px" :key="'myWangEditor'+dialogIndex"
                          placeholder="请输入新闻内容"/>
        </el-form-item>
        <el-row>
          <el-col :span="8">
            <el-form-item label="顺序" prop="orderIndex"
                          :rules="[{required: true, message: '顺序不能为空'},{type: 'number', message: '必须为数字'}]">
              <el-input-number v-model="temp.orderIndex" :min="0" step-strictly/>
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
          <el-button v-if="dialogType !== 'view'" type="primary" @click="saveData">保存</el-button>
          <el-button @click="dialogFormVisible=false">取消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import request from '@/utils/request'
import { MyWangEditor } from '@/components/MyWangEditor'
import { MessageSend } from '@/components/MessageSend'
import { ElMessage, ElMessageBox, ElNotification } from 'element-plus'
import { useMyGP } from '@/hooks/web/useMyGlobalProperties'

// 分页数据
const pager = ref({page: 1, limit: 10, totalCount: 0})
// 表格
const tableData = ref([])
// 查询表单数据
const searchData = ref({})
// 选中行
const tableSelectRows = ref([])
// 弹窗标题
const titleMap = {add: '添加新闻表-测试', update: '修改新闻表-测试', view: '查看详情'}
// 添加/修改模式（add/update）
const dialogType = ref('')
// 弹窗显示隐藏
const dialogFormVisible = ref(false)
// 表单临时数据
const temp = ref({orderIndex: 0})
const dialogIndex = ref(0)
const messageSendData = reactive({
  ids: [], // 根据用户选择自动获取
  type: 'user', // 发送用户类型：user=用户；org=组织机构；role=角色（不根据scope查询）
  scope: 'all' // all=全部；children=下级；selfAndChildren=本机构及下级
})

onMounted(() => {
  loadTableList()
  resetTemp()
})

// 查询按钮
const searchBtnHandle = () => {
  pager.value.page = 1
  loadTableList()
}

// 重置
const resetTableList = () => {
  pager.value.page = 1
  Object.assign(searchData.value, {})
  loadTableList()
}

// 加载表格
const loadTableList = () => {
  const params = {...pager.value, params: JSON.stringify(searchData.value)}
  request({url: '/news/news/list', method: 'get', params}).then((response) => {
    const {data} = response
    pager.value.totalCount = data.total
    tableData.value = data.records
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

// 清空表单temp数据
const resetTemp = () => {
  temp.value = {orderIndex: 0}
  messageSendData.ids = []
  dialogFormVisible.value = false
  dialogIndex.value++
}

// 打开添加窗口
const openAdd = () => {
  resetTemp()
  dialogFormVisible.value = true
  dialogType.value = 'add'
  nextTick(() => {
    dataForm.value.clearValidate()
  })
}

// 打开修改窗口
const openUpdate = (row) => {
  if (row) {
    dataTable.value.clearSelection()
    dataTable.value.toggleRowSelection(row, true)
  }
  if (tableSelectRows.value.length <= 0) {
    ElMessage({message: '请选择一条数据修改！', type: 'warning', grouping: true})
  } else if (tableSelectRows.value.length > 1) {
    ElMessage({message: '修改时，只允许选择一条数据！！', type: 'warning', grouping: true})
  } else {
    // 修改弹窗
    Object.assign(temp.value, tableSelectRows.value[0])
    dialogType.value = 'update'
    dialogFormVisible.value = true
    nextTick(() => {
      dataForm.value.clearValidate()
    })
  }
}

// 打开查看窗口
const openView = (row) => {
  Object.assign(temp.value, row)
  dialogType.value = 'view'
  dialogFormVisible.value = true
  nextTick(() => {
    dataForm.value.clearValidate()
  })
}

// 添加/修改，保存事件
const saveData = () => {
  dataForm.value.validate((valid) => {
    if (valid) {
      let data = {...temp.value}
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

// 删除
const deleteByIds = (row) => {
  if (row) {
    dataTable.value.clearSelection()
    dataTable.value.toggleRowSelection(row, true)
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

// 标记消息已读
const messageRead = (row) => {
  const params = {msgId: row.msgId}
  request({url: '/message/zMessage/read', method: 'get', params}).then((response) => {
    // 假设这里有一个方法来刷新消息计数
    // this.$store.dispatch('message/refreshMessageCount')
    loadTableList()
  })
}

// 导出Excel文件
const exportExcel = () => {
  const params = {params: JSON.stringify(searchData.value)}
  request({url: '/news/news/export/excel', method: 'get', params}).then(response => {
    // 创建a标签
    const link = document.createElement('a')
    // 组装下载地址
    link.href = useMyGP().gp.$baseServer + response.data
    // 修改文件名
    link.setAttribute('download', '新闻表-测试.xlsx')
    // 开始下载
    link.style.display = 'none'
    document.body.appendChild(link)
    link.click()
  })
}

// 引用
const dataTable = ref(null)
const dataForm = ref(null)
</script>
