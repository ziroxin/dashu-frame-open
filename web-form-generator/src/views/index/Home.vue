<template>
  <div class="container" v-loading="isLoading">
    <div class="left-board">
      <!-- 顶部左侧logo -->
      <div class="logo-wrapper">
        <div class="logo">
          <a style="font-size: 16px;color:#00afff;text-decoration: none;" target="_blank"
             href="https://gitee.com/ziro/dashu-frame-open">代码生成</a>
        </div>
      </div>
      <!-- 左侧组件面板 -->
      <el-scrollbar class="left-scrollbar">
        <div class="components-list">
          <div v-for="(item, listIndex) in leftComponents" :key="listIndex">
            <!-- 左侧分组标题 -->
            <div class="components-title">
              <svg-icon icon-class="component"/>
              {{ item.title }}
            </div>
            <!-- 左侧分组内组件 -->
            <draggable class="components-draggable" :list="item.list" :sort="false"
                       :group="{ name: 'componentsGroup', pull: 'clone', put: false }"
                       draggable=".components-item" :clone="cloneComponent" @end="onEnd">
              <div v-for="(element, index) in item.list" :key="index"
                   class="components-item" @click="addComponent(element)">
                <div class="components-body">
                  <svg-icon :icon-class="element.__config__.tagIcon"/>
                  {{ element.__config__.label }}
                </div>
              </div>
            </draggable>
          </div>
        </div>
      </el-scrollbar>
    </div>

    <div class="center-board">
      <!-- 中间顶部按钮 -->
      <div class="action-bar">
        <el-button icon="el-icon-video-play" type="text" @click="openSelectType">预览生成</el-button>
        <el-button icon="el-icon-view" type="text" @click="openJsonViewer">查看Json</el-button>
        <el-divider direction="vertical"></el-divider>
        <el-button icon="el-icon-coin" type="text" @click="tableToForm">导入表格</el-button>
        <el-divider direction="vertical"></el-divider>
        <el-button icon="el-icon-notebook-2" type="text" @click="dialogHistoryVisible=true">表单列表</el-button>
        <el-divider direction="vertical"></el-divider>
        <el-button class="delete-btn" icon="el-icon-delete" type="text" @click="empty">清空</el-button>
      </div>
      <!-- 当前表单显示 -->
      <div class="form-title" v-if="myFormTableData.formId">
        <span class="title">修改表单：{{ myFormTableData.formName }}</span>
        <el-button icon="el-icon-check" type="primary" class="btn" size="mini"
                   @click="dialogSaveFormVisible=true">保存
        </el-button>
      </div>
      <div class="form-title" v-else>
        <span class="title">新增表单 {{ myFormTableData.formName ? '：' + myFormTableData.formName : '' }}</span>
        <el-button icon="el-icon-plus" type="primary" class="btn" size="mini"
                   @click="dialogSaveFormVisible=true">保存
        </el-button>
      </div>
      <!-- 中间表单显示 -->
      <el-scrollbar class="center-scrollbar">
        <el-row class="center-board-row" :gutter="formConf.gutter">
          <el-form :size="formConf.size" :label-position="formConf.labelPosition"
                   :disabled="formConf.disabled" :label-width="formConf.labelWidth + 'px'">
            <draggable class="drawing-board" :list="drawingList" :animation="340" group="componentsGroup">
              <draggable-item v-for="(item, index) in drawingList" :key="item.renderKey"
                              :drawing-list="drawingList" :current-item="item" :index="index"
                              :active-id="activeId" :form-conf="formConf" @activeItem="activeFormItem"
                              @copyItem="drawingItemCopy" @deleteItem="drawingItemDelete"/>
            </draggable>
            <div v-show="!drawingList.length" class="empty-info">从左侧拖入或点选组件进行表单设计</div>
          </el-form>
        </el-row>
      </el-scrollbar>
    </div>
    <!-- 右侧面板 -->
    <right-panel :active-data="activeData" :form-conf="formConf" :show-field="!!drawingList.length"
                 @tag-change="tagChange" @fetch-data="fetchData"/>
    <!-- JSON查看弹窗 -->
    <json-drawer size="60%" :visible.sync="jsonDrawerVisible"
                 :json-str="JSON.stringify(formData)" @refresh="refreshJson"/>
    <!-- 选择生成文件类型弹窗，this.openGenerate()跳生成页面 -->
    <code-type-dialog :visible.sync="dialogVisible" title="选择生成类型"
                      :show-file-name="showFileName" @confirm="openGenerate"/>
    <!-- 生成代码页 -->
    <form-drawer :visible.sync="drawerVisible" :form-data="formData"
                 size="100%" :generate-conf="generateConf" @refresh="refreshFormTableData($event)"/>
    <!-- 复制代码input -->
    <input id="copyNode" type="hidden">

    <!-- 历史记录弹窗 -->
    <el-dialog title="历史记录" :visible.sync="dialogHistoryVisible"
               width="95%" top="5vh">
      <el-table :data="historyList" :height="tableHeight+'px'" border>
        <el-table-column label="表单名称" prop="formName" align="center"/>
        <el-table-column label="表单内容json格式" prop="formJson" align="center" show-overflow-tooltip/>
        <el-table-column label="表名" prop="tableName" align="center"/>
        <el-table-column label="表描述" prop="tableDecription" align="center"/>
        <el-table-column label="pom模块名" prop="basePackage" align="center"/>
        <el-table-column label="作者" prop="author" align="center"/>
        <el-table-column label="生成包名" prop="tablePackage" align="center"/>
        <el-table-column label="前端路径" prop="viewPath" align="center"/>
        <el-table-column label="状态" prop="status" align="center">
          <template v-slot="scope">
            <el-tag v-if="scope.row.status === '0'" type="info">未生成</el-tag>
            <el-tag v-if="scope.row.status === '1'" type="success">已生成</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="显示顺序" prop="orderIndex" align="center"/>
        <el-table-column fixed="right" label="操作" width="120" align="center">
          <template v-slot="scope">
            <el-button type="text" size="small" @click="updateFormTable(scope.row)">
              修改表单
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="text-align: center;" background layout="total,prev,pager,next,sizes"
                     :page-size="pager.limit" :current-page="pager.page"
                     :total="pager.totalCount" @current-change="handleCurrentChange"
                     @size-change="handleSizeChange"
      />
    </el-dialog>
    <!-- 保存当前表单弹窗 -->
    <el-dialog title="保存当前表单" :visible.sync="dialogSaveFormVisible">
      <el-form ref="myFormTable" :model="myFormTableData" label-width="100px">
        <el-form-item label="表单名称" prop="formName"
                      :rules="[{required: true, message: '表单名称必填'}]">
          <el-input v-model="myFormTableData.formName" placeholder="请输入表单名称"/>
        </el-form-item>
        <el-form-item label="顺序" prop="orderIndex"
                      :rules="[{required: true, message: '顺序必填'}, {type: 'number', message: '必须为数字'}]">
          <el-input-number v-model="myFormTableData.orderIndex" :min="0"/>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogSaveFormVisible=false">取 消</el-button>
        <el-button type="primary" @click="saveFormTable">确 定</el-button>
      </div>
    </el-dialog>

    <!-- 选择要导入的表弹窗 -->
    <el-dialog title="选择要导入的表" :visible.sync="dialogImportTableVisible"
               width="95%" top="5vh">
      <el-table :data="myImportTableList" :height="tableHeight+'px'" border>
        <el-table-column label="表名称" prop="tableName"/>
        <el-table-column label="表描述" prop="tableComment"/>
        <el-table-column fixed="right" label="操作" width="200" align="center">
          <template v-slot="scope">
            <el-button type="text" size="small" @click="tableImportToForm(scope.row)">
              导入表生成表单
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

  </div>
</template>

<script>
import draggable from 'vuedraggable'
import {debounce} from 'throttle-debounce'
import ClipboardJS from 'clipboard'
import render from '@/components/render/render'
import FormDrawer from './FormDrawer'
import JsonDrawer from './JsonDrawer'
import RightPanel from './RightPanel'
import {formConf, inputComponents, layoutComponents, selectComponents} from '@/components/generator/config'
import {beautifierConf, deepClone, isObjectObject} from '@/utils/index'
import {cssStyle, makeUpHtml, vueScript, vueTemplate} from '@/components/generator/html'
import {makeUpJs} from '@/components/generator/js'
import {makeUpCss} from '@/components/generator/css'
import drawingDefalut from '@/components/generator/drawingDefalut'
import CodeTypeDialog from './CodeTypeDialog'
import DraggableItem from './DraggableItem'
import {
  clearDrawingList,
  clearFormConf,
  clearIdGlobal,
  clearMyFormTableData,
  getDrawingList,
  getFormConf,
  getIdGlobal,
  getMyFormTableData,
  saveDrawingList,
  saveFormConf,
  saveIdGlobal,
  saveMyFormTableData
} from '@/utils/db'
import loadBeautifier from '@/utils/loadBeautifier'

let beautifier
let oldActiveId
let tempActiveData
const drawingListInDB = getDrawingList()
const formConfInDB = getFormConf()
const myFormTableDataInDB = getMyFormTableData()
const idGlobal = getIdGlobal()

export default {
  components: {
    draggable,
    render,
    FormDrawer,
    JsonDrawer,
    RightPanel,
    CodeTypeDialog,
    DraggableItem
  },
  data() {
    return {
      isLoading: true,
      idGlobal,
      formConf,
      inputComponents,
      selectComponents,
      layoutComponents,
      labelWidth: 100,
      drawingList: drawingDefalut,
      drawingData: {},
      activeId: drawingDefalut[0].formId,
      drawerVisible: false,
      formData: {},
      dialogVisible: false,
      jsonDrawerVisible: false,
      generateConf: null,
      showFileName: false,
      activeData: drawingDefalut[0],
      saveDrawingListDebounce: debounce(340, saveDrawingList),
      saveIdGlobalDebounce: debounce(340, saveIdGlobal),
      leftComponents: [
        {title: '输入型组件', list: inputComponents},
        {title: '选择型组件', list: selectComponents},
        {title: '布局型组件', list: layoutComponents}
      ],
      // 保存表单数据
      dialogSaveFormVisible: false,
      myFormTableData: {formName: '', orderIndex: 0},
      // 历史记录
      dialogHistoryVisible: false,
      tableHeight: window.innerHeight - 242,
      historyList: [],
      pager: {page: 1, limit: 10, totalCount: 0},
      // 选择要导入的表
      dialogImportTableVisible: false,
      myImportTableList: [],
    }
  },
  computed: {},
  watch: {
    'activeData.__config__.label': function (val, oldVal) {
      if (this.activeData.placeholder === undefined || this.activeData.placeholder.indexOf('请') < 0
          || !this.activeData.__config__.tag || oldActiveId !== this.activeId) {
        return
      }
      this.activeData.placeholder = this.activeData.placeholder.substr(0, 3) + val
    },
    activeId: {
      handler(val) {
        oldActiveId = val
      },
      immediate: true
    },
    drawingList: {
      handler(val) {
        this.saveDrawingListDebounce(val)
        if (val.length === 0) this.idGlobal = 100
      },
      deep: true
    },
    idGlobal: {
      handler(val) {
        this.saveIdGlobalDebounce(val)
      },
      immediate: true
    }
  },
  mounted() {
    if (Array.isArray(drawingListInDB) && drawingListInDB.length > 0) {
      // 中间表单部分：加载缓存数据
      this.drawingList = drawingListInDB
    } else {
      // 中间表单部分：加载默认
      this.drawingList = drawingDefalut
    }
    // 当前激活的表单
    this.activeFormItem(this.drawingList[0])
    if (formConfInDB) {
      this.formConf = formConfInDB
    }
    if (myFormTableDataInDB) {
      this.myFormTableData = myFormTableDataInDB
    }
    // json美化工具
    loadBeautifier(btf => {
      beautifier = btf
    })
    // 监听复制代码按钮
    const clipboard = new ClipboardJS('#copyNode', {
      text: trigger => {
        const codeStr = this.getCopyCodeData()
        this.$notify({
          title: '成功',
          message: '代码已复制到剪切板，可粘贴。',
          type: 'success'
        })
        return codeStr
      }
    })
    clipboard.on('error', e => {
      this.$message.error('代码复制失败')
    })
    // 加载历史记录
    if (this.$route.query.fid) {
      this.loadHistoryList(this.$route.query.fid)
    } else {
      this.loadHistoryList()
    }
  },
  methods: {
    // 保存当前表单
    saveFormTable() {
      this.AssembleFormData()
      this.$refs['myFormTable'].validate((valid) => {
        if (valid) {
          if (this.myFormTableData.formId) {
            // 更新
            this.update()
          } else {
            // 新增
            this.add()
          }
        }
      })
    },
    add() {
      const data = {...this.myFormTableData, ...this.formData, formJson: JSON.stringify(this.formData)}
      this.$request({url: '/generator/zFormGenerator/add', method: 'post', data})
          .then((response) => {
            this.$message({type: 'success', message: '表单保存成功！'})
            const formId = response.data;
            this.loadHistoryList(formId)
            this.dialogSaveFormVisible = false
          })
    },
    update() {
      const data = {...this.myFormTableData, ...this.formData, formJson: JSON.stringify(this.formData)}
      this.$request({url: '/generator/zFormGenerator/update', method: 'put', data})
          .then((response) => {
            this.$message({type: 'success', message: '表单保存成功！'})
            this.loadHistoryList()
            this.dialogSaveFormVisible = false
          })
    },
    // 加载历史记录
    loadHistoryList(formId) {
      const params = {...this.pager}
      this.$request({url: '/generator/zFormGenerator/list', method: 'get', params})
          .then((response) => {
            const {data} = response
            this.pager.totalCount = data.total
            this.historyList = data.records
            // 加载默认记录
            if (formId) {
              const list = this.historyList.filter(item => item.formId === formId);
              if (list.length > 0) {
                this.loadFormTable(list[0])
              }
            }
            this.isLoading = false
          })
    },
    // 修改表单
    updateFormTable(row) {
      this.$confirm('提醒：修改操作，会覆盖当前页面上的表单，建议先保存，确定要修改吗?', '修改确认', {
        confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
      }).then(() => {
        this.loadFormTable(row)
        this.$message({type: 'success', message: '表单加载成功，请修改！'})
        this.dialogHistoryVisible = false
      })
    },
    // 从接口数据，加载表单
    loadFormTable(row) {
      this.myFormTableData = {...row}
      const jsonData = JSON.parse(row.formJson)
      this.drawingList = deepClone(jsonData.fields)
      delete jsonData.fields
      this.formConf = jsonData
      // 保存缓存
      saveDrawingList(this.drawingList)
      saveFormConf(this.formConf)
      saveMyFormTableData(this.myFormTableData)
    },
    // 监听分页
    handleCurrentChange(page) {
      this.pager.page = page
      this.loadHistoryList()
    },
    // 分页条数改变
    handleSizeChange(size) {
      this.pager.limit = size
      this.loadHistoryList()
    },
    // 打开弹窗：选择生成类型
    openSelectType() {
      if (!this.myFormTableData.formId) {
        this.$message({message: '请先保存表单！', type: 'warning'})
        return;
      }
      this.dialogVisible = true
      this.showFileName = false
    },
    // 打开弹窗：代码生成和预览
    openGenerate(data) {
      this.generateConf = data
      this.AssembleFormData()
      this.update() // 更新表单
      // 特殊判断：上传附件时-子表名未填写，提示必须填写
      let errUp = this.formData.fields
          .filter(f => f.__config__.tag === 'el-upload' && !f.__config__.isTableField && !f.__config__.childTableName)
      if (errUp.length > 0) {
        let errMsg = '';
        errUp.forEach(f => errMsg += `组件【${f.__config__.label}】的‘子表名’未填写正确，请检查<br/>`)
        this.$message({dangerouslyUseHTMLString: true, showClose: true, type: 'error', message: errMsg})
      } else {
        this.formData = {...this.formData, ...this.myFormTableData}
        this.drawerVisible = true
      }
    },
    // 刷新
    refreshFormTableData(formId) {
      console.log(444, formId)
      this.loadHistoryList(formId)
    },
    // 左侧组件，拖拽结束事件
    onEnd(obj) {
      if (obj.from !== obj.to) {
        this.fetchData(tempActiveData)
        this.activeData = tempActiveData
        this.activeId = this.idGlobal
      }
    },
    // 左侧组件点击事件
    addComponent(item) {
      const clone = this.cloneComponent(item)
      this.fetchData(clone)
      this.drawingList.push(clone)
      this.activeFormItem(clone)
    },
    // 中间部分组件，显示拷贝按钮
    drawingItemCopy(item, list) {
      let clone = deepClone(item)
      clone = this.createIdAndKey(clone)
      list.push(clone)
      this.activeFormItem(clone)
    },
    // 点击复制组件按钮时，复制一个组件放到中间部分
    cloneComponent(origin) {
      const clone = deepClone(origin)
      const config = clone.__config__
      config.span = this.formConf.span // 生成代码时，会根据span做精简判断
      this.createIdAndKey(clone)
      clone.placeholder !== undefined && (clone.placeholder += config.label)
      tempActiveData = clone
      return tempActiveData
    },
    // 中间部分组件，显示删除按钮
    drawingItemDelete(index, list) {
      list.splice(index, 1)
      this.$nextTick(() => {
        const len = this.drawingList.length
        if (len) {
          this.activeFormItem(this.drawingList[len - 1])
        }
      })
    },
    // 中间部分，当前激活标签
    activeFormItem(currentItem) {
      this.activeData = currentItem
      this.activeId = currentItem.__config__.formId
    },
    // 把中间表单数据拷贝到 this.formData
    AssembleFormData() {
      this.formData = {
        fields: deepClone(this.drawingList),
        ...this.formConf
      }
    },
    // 清空中间所有组件
    empty() {
      this.$confirm('确定要清空所有组件吗，重建新表单吗？', '提示', {type: 'warning'}).then(() => {
        this.isLoading = true
        clearDrawingList()
        clearMyFormTableData()
        clearIdGlobal()
        clearFormConf()
        this.$message({type: 'success', message: '重置成功，正在重新加载！您可以添加新表单了'})
        setTimeout(() => {
          // 去掉url中的 ?fid=xxx
          location.href = location.href.split('?')[0]
          location.reload()
        }, 500)
      })
    },
    // 从组件中获取数据（属性等）
    fetchData(component) {
      const {dataType, method, url} = component.__config__
      if (dataType === 'dynamic' && method && url) {
        this.setLoading(component, true)
        this.$request({url: url, method: method}).then(resp => {
          this.setLoading(component, false)
          this.setRespData(component, resp)
        })
      } else {
        this.setRespData(component)
      }
    },
    // 设置组件请求数据（配置下拉、单选、多选等选项接口）
    setRespData(component, resp) {
      const {dataPath, renderKey, dataConsumer} = component.__config__
      if (!dataPath || !dataConsumer) return
      let respData = []
      if (resp) {
        respData = dataPath.split('.').reduce((pre, item) => pre[item], resp)
      }
      /*
      将请求回来的数据，赋值到指定属性。
      以el-tabel为例，根据Element文档，应该将数据赋值给el-tabel的data属性，所以dataConsumer的值应为'data';
      此时赋值代码可写成 component[dataConsumer] = respData；
      但为支持更深层级的赋值（如：dataConsumer的值为'options.data'）,使用setObjectValueReduce
      */
      this.setObjectValueReduce(component, dataConsumer, respData)
      const i = this.drawingList.findIndex(item => item.__config__.renderKey === renderKey)
      if (i > -1) {
        this.$set(this.drawingList, i, component)
      }
    },
    // 深层次属性赋值
    setObjectValueReduce(obj, strKeys, data) {
      const arr = strKeys.split('.')
      arr.reduce((pre, item, i) => {
        if (arr.length === i + 1) {
          pre[item] = data
        } else if (!isObjectObject(pre[item])) {
          pre[item] = {}
        }
        return pre[item]
      }, obj)
    },
    // 设置组件的loading
    setLoading(component, val) {
      const {directives} = component
      if (Array.isArray(directives)) {
        const t = directives.find(d => d.name === 'loading')
        if (t) t.value = val
      }
    },
    // 右侧面板，修改组件类型时，回调方法
    tagChange(newTag) {
      newTag = this.cloneComponent(newTag)
      newTag.__vModel__ = this.activeData.__vModel__
      // 配置 __config__
      const activeConfig = this.activeData.__config__
      // 把用户自定配置拷贝过来（如字段信息等）
      const config = newTag.__config__
      config.formId = this.activeId
      // 字段信息
      config.label = activeConfig.label
      config.isTableField = activeConfig.isTableField
      config.fieldType = activeConfig.fieldType
      config.fieldLength = activeConfig.fieldLength
      config.pointLength = activeConfig.pointLength
      config.isKey = activeConfig.isKey
      config.required = activeConfig.required
      // 外观信息
      config.labelWidth = activeConfig.labelWidth
      config.showLabel = activeConfig.showLabel
      config.span = activeConfig.span
      // 覆盖新组建配置
      newTag.__config__ = config

      // 拷贝公用属性
      Object.keys(newTag).forEach(key => {
        if (key === 'placeholder' || key === 'style' || key === 'clearable' || key === 'disabled') {
          if (this.activeData[key] !== undefined) {
            newTag[key] = this.activeData[key]
          }
        }
      })
      this.activeData = newTag
      this.updateDrawingList(newTag, this.drawingList)
    },
    // 更新drawingList数据
    updateDrawingList(newTag, list) {
      const index = list.findIndex(item => item.__config__.formId === this.activeId)
      if (index > -1) {
        list.splice(index, 1, newTag)
        this.fetchData(newTag)
      } else {
        list.forEach(item => {
          if (Array.isArray(item.__config__.children)) {
            this.updateDrawingList(newTag, item.__config__.children)
          }
        })
      }
    },
    // 创建唯一ID和唯一Key
    createIdAndKey(item) {
      const config = item.__config__
      config.formId = ++this.idGlobal
      config.renderKey = `${config.formId}${+new Date()}` // 改变renderKey后可以实现强制更新组件
      if (config.layout === 'colFormItem') {
        item.__vModel__ = `field${this.idGlobal}`
      } else if (config.layout === 'rowFormItem') {
        config.componentName = `row${this.idGlobal}`
        !Array.isArray(config.children) && (config.children = [])
        delete config.label // rowFormItem无需配置label属性
      }
      if (Array.isArray(config.children)) {
        config.children = config.children.map(childItem => this.createIdAndKey(childItem))
      }
      return item
    },
    // 获取待复制的代码
    getCopyCodeData() {
      const {type} = this.generateConf
      this.AssembleFormData()
      const script = vueScript(makeUpJs(this.formData, type))
      const html = vueTemplate(makeUpHtml(this.formData, type))
      const css = cssStyle(makeUpCss(this.formData))
      return beautifier.html(html + script + css, beautifierConf.html)
    },
    // 打开弹窗：json查看
    openJsonViewer() {
      this.AssembleFormData()
      this.jsonDrawerVisible = true
    },
    // Json查看弹窗中的刷新按钮回调方法
    refreshJson(data) {
      this.drawingList = deepClone(data.fields)
      delete data.fields
      this.formConf = data
    },
    // 导入表格，转成现有的表单
    tableToForm() {
      // 1 查询表列表，弹窗选择导入
      this.$request({
        url: '/generator/code/tableList', method: 'get'
      }).then((response) => {
        const {data} = response
        this.myImportTableList = data
        this.dialogImportTableVisible = true
      })
    },
    tableImportToForm(row) {
      // 导入已有表格，生成对应的表单
      this.$confirm('确定要导入表[' + row.tableName + ']吗?', '导入确认', {
        confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
      }).then(() => {
        // 2 查询表内所有字段
        const params = {tableName: row.tableName}
        console.log(params)
        this.$request({
          url: '/generator/code/tableInfo', method: 'get', params
        }).then((response) => {
          // 生成字段json
          let fields = []
          response.data.forEach(item => {
            let itemConfig;
            if (item.key) {
              // 主键
              itemConfig = inputComponents.find(comp => comp.__config__.tag === 'el-key');
            } else if (['int', 'tinyint', 'smallint', 'bigint'].includes(item.type)) {
              // 数字
              itemConfig = inputComponents.find(comp => comp.__config__.tag === 'el-input-number');
            } else if (['datetime', 'date'].includes(item.type)) {
              // 日期
              itemConfig = selectComponents.find(comp => comp.__config__.tag === 'el-date-picker');
            } else if (['text', 'longtext', 'tinytext', 'mediumtext', 'blob', 'longblob'].includes(item.type)) {
              // 富文本
              itemConfig = inputComponents.find(comp => comp.__config__.tag === 'my-wang-editor');
            } else if (['varchar', 'char'].includes(item.type) && item.length >= 255) {
              // 多行文本
              itemConfig = inputComponents.find(comp =>
                  comp.__config__.tag === 'el-input' && comp.__config__.tagIcon === 'textarea');
            } else {
              // 单行文本（默认）
              itemConfig = inputComponents.find(comp =>
                  comp.__config__.tag === 'el-input' && comp.__config__.tagIcon === 'input');
            }
            if (itemConfig) {
              let comp = deepClone(itemConfig);
              comp.__vModel__ = item.name
              comp.__config__.label = item.title
              comp.__config__.fieldType = item.type
              comp.__config__.fieldLength = item.length
              comp.__config__.pointLength = item.point
              comp.__config__.required = item.required
              comp.__config__.isKey = item.key
              comp.__config__.formId = ++this.idGlobal
              comp.__config__.renderKey = `${comp.__config__.formId}${+new Date()}` // 改变renderKey后可以实现强制更新组件
              fields.push(comp)
            }
          })
          this.drawingList = deepClone(fields)
          saveDrawingList(this.drawingList)
          // 生成表json
          const packageStr = row.tableName.replace(/_/g, '').replace(/-/g, '')
          this.formConf = {
            tableName: row.tableName,
            tableDecription: row.tableComment || row.tableName,
            basePackage: 'com.kg.module',
            tablePackage: packageStr,
            viewPath: '/' + packageStr,
            author: 'ziro', formRef: 'dataForm', formModel: 'temp', size: 'medium', labelPosition: 'right',
            labelWidth: 100, formRules: 'rules', gutter: 15, span: 24, formBtns: true
          }
          saveFormConf(this.formConf)
          // 生成表单json
          this.myFormTableData = {formName: row.tableComment || row.tableName, orderIndex: 0}
          saveMyFormTableData(this.myFormTableData)
          // 加载完成，关闭弹窗
          this.$message.success('导入表格成功，表单已生成')
          this.dialogImportTableVisible = false
        })
      })
    }
  }
}
</script>

<style lang='scss'>
@import '@/styles/home';

.action-bar {
  .el-button {
    padding-bottom: 3px;

    &:hover {
      border-radius: 2px;
      border-bottom: 3px solid;
    }
  }
}

.form-title {
  text-align: center;
  margin: 2px 12px 2px 12px;
  border-bottom: 1px dashed #cccccc;
  line-height: 40px;
  padding-bottom: 6px;

  .title {
    font-size: 16px;
    font-weight: bold;
    color: #D7000F;
  }

  .btn {
    margin-left: 10px;
    padding: 7px 7px 7px 6px;
  }
}
</style>
