<template>
  <div>
    <el-drawer v-bind="$attrs" v-on="$listeners" @opened="onOpen" @close="onClose">
      <div style="height:100%">
        <el-row style="height:100%;overflow:auto">
          <el-col :md="24" :lg="12" class="left-editor">
            <div class="setting" title="资源引用" @click="showResource">
              <el-badge :is-dot="!!resources.length" class="item">
                <i class="el-icon-setting"/>
              </el-badge>
            </div>
            <el-tabs v-model="activeTab" type="card" class="editor-tabs">
              <el-tab-pane name="html">
                <span slot="label">
                  <i v-if="activeTab==='html'" class="el-icon-edit"/>
                  <i v-else class="el-icon-document"/>
                  template
                </span>
              </el-tab-pane>
              <el-tab-pane name="js">
                <span slot="label">
                  <i v-if="activeTab==='js'" class="el-icon-edit"/>
                  <i v-else class="el-icon-document"/>
                  script
                </span>
              </el-tab-pane>
              <el-tab-pane name="css">
                <span slot="label">
                  <i v-if="activeTab==='css'" class="el-icon-edit"/>
                  <i v-else class="el-icon-document"/>
                  css
                </span>
              </el-tab-pane>
            </el-tabs>
            <div v-show="activeTab==='html'" id="editorHtml" class="tab-editor"/>
            <div v-show="activeTab==='js'" id="editorJs" class="tab-editor"/>
            <div v-show="activeTab==='css'" id="editorCss" class="tab-editor"/>
          </el-col>
          <el-col :md="24" :lg="12" class="right-preview">
            <div class="action-bar" :style="{'text-align': 'left'}">
              <span class="bar-btn" @click="runCode">
                <i class="el-icon-refresh"/>刷新
              </span>
              <span class="bar-btn" @click="generateBefore">
                <i class="el-icon-s-promotion"/>生成代码
              </span>
              <span class="bar-btn delete-btn" @click="$emit('update:visible', false)">
                <i class="el-icon-circle-close"/>关闭
              </span>
            </div>
            <iframe
                v-show="isIframeLoaded"
                ref="previewPage"
                class="result-wrapper"
                frameborder="0"
                src="preview.html"
                @load="iframeLoad"
            />
            <div v-show="!isIframeLoaded" v-loading="true" class="result-wrapper"/>
          </el-col>
        </el-row>
      </div>
    </el-drawer>
    <resource-dialog
        :visible.sync="resourceVisible"
        :origin-resource="resources"
        @save="setResource"
    />
  </div>
</template>
<script>
import {parse} from '@babel/parser'
import ClipboardJS from 'clipboard'
import {saveAs} from 'file-saver'
import {cssStyle, makeUpHtml, vueScript, vueTemplate} from '@/components/generator/html'
import {makeUpJs} from '@/components/generator/js'
import {makeUpCss} from '@/components/generator/css'
import {beautifierConf, exportDefault} from '@/utils/index'
import ResourceDialog from './ResourceDialog'
import loadMonaco from '@/utils/loadMonaco'
import loadBeautifier from '@/utils/loadBeautifier'
import request from "@/utils/request"
import {generateHtml} from "@/components/generator/generateHtml"
import {generateJs} from "@/components/generator/generateJs"
import {generateCss} from "@/components/generator/generateCss"

const editorObj = {
  html: null,
  js: null,
  css: null
}
const mode = {
  html: 'html',
  js: 'javascript',
  css: 'css'
}
let beautifier
let monaco

export default {
  components: {ResourceDialog},
  props: ['formData', 'generateConf'],
  data() {
    return {
      activeTab: 'html',
      htmlCode: '',
      jsCode: '',
      cssCode: '',
      codeFrame: '',
      isIframeLoaded: false,
      isInitcode: false, // 保证open后两个异步只执行一次runcode
      isRefreshCode: false, // 每次打开都需要重新刷新代码
      resourceVisible: false,
      scripts: [],
      links: [],
      monaco: null
    }
  },
  computed: {
    resources() {
      return this.scripts.concat(this.links)
    }
  },
  watch: {},
  created() {
  },
  mounted() {
    window.addEventListener('keydown', this.preventDefaultSave)
    const clipboard = new ClipboardJS('.copy-btn', {
      text: trigger => {
        const codeStr = this.generateCode()
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
  },
  beforeDestroy() {
    window.removeEventListener('keydown', this.preventDefaultSave)
  },
  methods: {
    preventDefaultSave(e) {
      if (e.key === 's' && (e.metaKey || e.ctrlKey)) {
        e.preventDefault()
      }
    },
    onOpen() {
      const {type} = this.generateConf
      this.htmlCode = makeUpHtml(this.formData, type)
      this.jsCode = makeUpJs(this.formData, type)
      this.cssCode = makeUpCss(this.formData)

      loadBeautifier(btf => {
        beautifier = btf
        this.htmlCode = beautifier.html(this.htmlCode, beautifierConf.html)
        this.jsCode = beautifier.js(this.jsCode, beautifierConf.js)
        this.cssCode = beautifier.css(this.cssCode, beautifierConf.html)

        loadMonaco(val => {
          monaco = val
          this.setEditorValue('editorHtml', 'html', this.htmlCode)
          this.setEditorValue('editorJs', 'js', this.jsCode)
          this.setEditorValue('editorCss', 'css', this.cssCode)
          if (!this.isInitcode) {
            this.isRefreshCode = true
            this.isIframeLoaded && (this.isInitcode = true) && this.runCode()
          }
        })
      })
    },
    onClose() {
      this.isInitcode = false
      this.isRefreshCode = false
    },
    iframeLoad() {
      if (!this.isInitcode) {
        this.isIframeLoaded = true
        this.isRefreshCode && (this.isInitcode = true) && this.runCode()
      }
    },
    setEditorValue(id, type, codeStr) {
      if (editorObj[type]) {
        editorObj[type].setValue(codeStr)
      } else {
        editorObj[type] = monaco.editor.create(document.getElementById(id), {
          value: codeStr,
          theme: 'vs-dark',
          language: mode[type],
          automaticLayout: true
        })
      }
      // ctrl + s 刷新
      editorObj[type].onKeyDown(e => {
        if (e.keyCode === 49 && (e.metaKey || e.ctrlKey)) {
          this.runCode()
        }
      })
    },
    runCode() {
      const jsCodeStr = editorObj.js.getValue()
      try {
        const ast = parse(jsCodeStr, {sourceType: 'module'})
        const astBody = ast.program.body
        if (astBody.length > 1) {
          this.$confirm(
            'js格式不能识别，仅支持修改export default的对象内容',
            '提示',
            {
              type: 'warning'
            }
          )
          return
        }
        if (astBody[0].type === 'ExportDefaultDeclaration') {
          const postData = {
            type: 'refreshFrame',
            data: {
              generateConf: this.generateConf,
              html: editorObj.html.getValue(),
              js: jsCodeStr.replace(exportDefault, ''),
              css: editorObj.css.getValue(),
              scripts: this.scripts,
              links: this.links
            }
          }
          this.$refs.previewPage.contentWindow.postMessage(
            postData,
            location.origin
          )
        } else {
          this.$message.error('请使用export default')
        }
      } catch (err) {
        this.$message.error(`js错误：${err}`)
        console.error(err)
      }
    },
    generateCode() {
      const html = vueTemplate(editorObj.html.getValue())
      const script = vueScript(editorObj.js.getValue())
      const css = cssStyle(editorObj.css.getValue())
      return beautifier.html(html + script + css, beautifierConf.html)
    },
    exportFile() {
      this.$prompt('文件名:', '导出文件', {
        inputValue: `${+new Date()}.vue`,
        closeOnClickModal: false,
        inputPlaceholder: '请输入文件名'
      }).then(({value}) => {
        if (!value) value = `${+new Date()}.vue`
        const codeStr = this.generateCode()
        const blob = new Blob([codeStr], {type: 'text/plain;charset=utf-8'})
        saveAs(blob, value)
      })
    },
    // 生成表，并生成代码
    generateBefore() {
      request({url: '/generator/zFormGenerator/getById', method: 'get', params: {formId: this.formData.formId}})
          .then(response => {
            if (response.data.status === '1') {
              // 已生成过，用户确认是否继续生成
              this.$confirm('检测到代码已生成，是否重新生成？(代码生成后自动以ZIP格式下载)', '代码生成确认', {
                confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
              }).then(() => {
                this.generateCheckTable()
              })
            } else {
              // 未生成过，直接生成
              this.generateCheckTable()
            }
          })
    },
    generateCheckTable() {
      const tableName = this.formData.tableName
      request({url: '/generator/code/hasTables', method: 'get', params: {tableName: tableName}})
          .then(response => {
            if (response.data) {
              // 表存在，提示用户，是否覆盖原表，让用户选择
              this.$msgbox({
                message: '检测到数据库表【' + tableName + '】已存在，是否覆盖原表？<br>覆盖：会删除原表，生成新表，再生成代码；<br>不覆盖：直接生成代码',
                title: '代码生成确认', dangerouslyUseHTMLString: true, distinguishCancelAndClose: true,
                showCancelButton: true, confirmButtonText: '覆盖', cancelButtonText: '不覆盖', type: 'warning'
              }).then(() => {
                this.$confirm('确定要生成表【' + tableName + '】吗？本操作将删除数据库中同名的表，无法恢复！！！', '再次确认', {
                  confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
                }).then(() => {
                  this.generate(true)// 覆盖生成（生成表）
                })
              }).catch((action) => {
                if (action === 'cancel') {
                  this.generate(false)// 不覆盖生成（不生成表）
                }
              })
            } else {
              // 表不存在，直接生成（生成表）
              this.generate(true)
            }
          })
    },
    generate(isCoverTable) {
      let generateData = {
        template: encodeURIComponent(generateHtml(this.formData, this.generateConf)),
        css: encodeURIComponent(generateCss(this.formData)),
        ...generateJs(this.formData, this.generateConf)
      }
      let table = {...this.formData}
      let fields = []
      this.formData.fields.forEach(f => {
        if (f.__vModel__) {
          fields.push({
            name: f.__vModel__,
            title: f.__config__.label,
            type: f.__config__.fieldType,
            length: f.__config__.fieldLength,
            point: f.__config__.pointLength,
            required: f.__config__.required,
            key: f.__config__.isKey,
            childFileTable: f.__config__.childTableName
          })
        } else {
          // 行容器中的字段
          if (f.__config__.children) {
            f.__config__.children.forEach(c => {
              fields.push({
                name: c.__vModel__,
                title: c.__config__.label,
                type: c.__config__.fieldType,
                length: c.__config__.fieldLength,
                point: c.__config__.pointLength,
                required: c.__config__.required,
                key: c.__config__.isKey,
                childFileTable: c.__config__.childTableName
              })
            })
          }
        }
      })
      // 调用接口，开始生成代码
      let data = {...table, ...generateData, fields: fields, isCoverTable: isCoverTable || false}
      request({url: '/generator/code/byform', method: 'post', data})
          .then(resp => {
            this.$message({type: 'success', message: '代码生成成功！'})
            saveAs(this.$baseServer + resp.data, data.formName);
            console.log("代码地址：\n", resp.data)
            // 刷新父组件中，表单数据
            this.$emit('refresh', data.formId)
          })
    },
    showResource() {
      this.resourceVisible = true
    },
    setResource(arr) {
      const scripts = [], links = []
      if (Array.isArray(arr)) {
        arr.forEach(item => {
          if (item.endsWith('.css')) {
            links.push(item)
          } else {
            scripts.push(item)
          }
        })
        this.scripts = scripts
        this.links = links
      } else {
        this.scripts = []
        this.links = []
      }
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/styles/mixin.scss';

.tab-editor {
  position: absolute;
  top: 33px;
  bottom: 0;
  left: 0;
  right: 0;
  font-size: 14px;
}

.left-editor {
  position: relative;
  height: 100%;
  background: #1e1e1e;
  overflow: hidden;
}

.setting {
  position: absolute;
  right: 15px;
  top: 3px;
  color: #a9f122;
  font-size: 18px;
  cursor: pointer;
  z-index: 1;
}

.right-preview {
  height: 100%;

  .result-wrapper {
    height: calc(100vh - 33px);
    width: 100%;
    overflow: auto;
    padding: 12px;
    box-sizing: border-box;
  }
}

@include action-bar;
::v-deep .el-drawer__header {
  display: none;
}
</style>
