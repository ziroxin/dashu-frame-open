<template>
  <el-tabs class="w-full" stretch model-value="componentTab">
    <!-- tab1组件属性 -->
    <el-tab-pane label="组件属性" name="componentTab">
      <div class="ml-10px mr-5px h-[calc(100vh-55px)] overflow-x-hidden overflow-y-auto">
        <el-form v-if="current?.__id" :model="current" label-width="auto" size="small">
          <!-- 组件类型 -->
          <el-form-item label="组件类型" prop="__key">
            <el-select v-model="current.__key" @change="componentChange" class="w-65%!">
              <el-option-group v-for="group in componentList" :key="group.name" :label="group.name">
                <el-option v-for="item in group.list" :key="item.__key" :label="item.__name" :value="item.__key">
                  <my-icon v-if="item.__icon" :icon="item.__icon"/>
                  {{ item.__name }}
                </el-option>
              </el-option-group>
            </el-select>
            <a :href="current.__docLink || 'https://element-plus.org/zh-CN/'" target="_blank" class="ml-2% w-33%!">
              <base-button type="primary" plain icon="el-icon-link" class="w-full">文档</base-button>
            </a>
          </el-form-item>
          <!-- input-文本类型 -->
          <el-form-item v-if="'el-input'===current.__key" label="文本类型">
            <el-select v-model="current.__attrs.type" @change="typeChange">
              <el-option label="单行文本框" value="text"/>
              <el-option label="多行文本框" value="textarea"/>
              <el-option label="密码框" value="password"/>
            </el-select>
          </el-form-item>

          <el-divider>通用属性</el-divider>
          <el-form-item label="字段名" required>
            <el-input v-model="current.__modelName" placeholder="字段名(建议驼峰格式)"/>
          </el-form-item>
          <el-form-item label="Label" required>
            <el-input v-model="current.__formItemAttrs.label"/>
          </el-form-item>
          <el-form-item v-if="formProps.__layout.layout" label="栅格">
            <el-slider v-model="current.__span" :min="0" :max="24" :marks="{12:''}"/>
          </el-form-item>
          <el-form-item v-if="current?.__attrs?.placeholder" label="占位提示">
            <el-input v-model="current.__attrs.placeholder"/>
          </el-form-item>

          <template v-if="'el-select'===current.__key">
            <el-divider>数据</el-divider>
            <el-form-item label-width="0">
              <el-tabs v-model="current.dataType" class="dataTab w-full"
                       type="border-card" stretch @tab-change="dataTypeClick">
                <el-tab-pane label="静态" name="static">
                  <template v-if="'static'===current.dataType">
                    <el-form-item label="Props" label-width="60px" class="mt-10px">
                      <div class="color-#666">默认值不可修改</div>
                      <json-editor v-model="current.__attrs.props" :showLineNumbers="false" :showIcon="false"
                                   :collapsedOnClickBrackets="false" :editable="false"/>
                    </el-form-item>
                    <el-form-item label="Options" label-width="60px" class="mt-10px">
                      <vue-draggable v-model="current.__attrs.options" :animation="150"
                                     :key="'drag'+current.__attrs.options.length">
                        <div v-for="(item,idx) in current.__attrs.options" :key="idx" class="flex items-center mb-10px">
                          <my-icon icon="el-icon-rank" :size="20" color="#409eff" class="mr-5px cursor-grab"/>
                          <el-input v-model="item.label" class="mr-5px"/>
                          <el-input v-model="item.value" class="mr-5px"/>
                          <base-button type="danger" link icon="el-icon-delete" class="mr-5px" size="small"
                                       @click="()=>{current.__attrs.options.splice(idx,1)}"/>
                        </div>
                      </vue-draggable>
                      <base-button type="primary" link icon="el-icon-circle-plus-outline" size="small" class="mx-auto"
                                   @click="addStaticData">添加数据
                      </base-button>
                    </el-form-item>
                  </template>
                </el-tab-pane>
                <el-tab-pane label="动态" name="dynamic">
                  <template v-if="'dynamic'===current.dataType">
                    <el-form-item label="Props 可修改" label-width="55px" class="mt-10px">
                      <json-editor v-model="current.__attrs.props" :showLineNumbers="false" :showIcon="false"
                                   :collapsedOnClickBrackets="false"/>
                    </el-form-item>
                    <el-form-item label="接口" label-width="70px" class="mt-10px">
                      <el-input v-model="current.dataDynamic.url" placeholder="接口地址">
                        <template #prefix>
                          <span class="bg-#efefef color-#777 px-10px ml-[-6px]! b-rd-4px">GET</span>
                        </template>
                      </el-input>
                    </el-form-item>
                    <el-form-item label="数据字段" label-width="70px" class="mt-10px">
                      <el-input v-model="current.dataDynamic.dataKey" placeholder="数据字段"/>
                    </el-form-item>
                    <base-button type="primary" link icon="el-icon-refresh" size="small" class="ml-70px"
                                 @click="refreshDynamicData">刷新数据
                    </base-button>
                  </template>
                </el-tab-pane>
                <el-tab-pane label="字典" name="dict">
                  <template v-if="'dict'===current.dataType">
                    <el-form-item label="Props" label-width="60px" class="mt-10px">
                      <div class="color-#666">默认值不可修改</div>
                      <json-editor v-model="current.__attrs.props" :showLineNumbers="false" :showIcon="false"
                                   :collapsedOnClickBrackets="false" :editable="false"/>
                    </el-form-item>
                    <el-form-item label="字典类型code" label-width="100px" class="mt-10px">
                      <el-input v-model="current.dictCode" placeholder="字典类型code"/>
                    </el-form-item>
                    <base-button type="primary" link icon="el-icon-refresh" size="small" class="ml-100px"
                                 @click="refreshDictData">刷新数据
                    </base-button>
                  </template>
                </el-tab-pane>
              </el-tabs>
            </el-form-item>
          </template>

          <el-divider>更多属性</el-divider>
          <!-- input属性 -->
          <template v-if="'el-input'===current.__key">
            <el-form-item label="MaxLength">
              <el-input-number v-model="current.__attrs.maxlength"/>
            </el-form-item>
            <el-form-item label="清除按钮" v-if="'textarea'!==current.__attrs.type">
              <el-switch v-model="current.__attrs.clearable" active-text="显示" inactive-text="隐藏"
                         :active-value="true" :inactive-value="false"/>
            </el-form-item>
            <el-form-item label="统计字数" v-if="'password'!==current.__attrs.type">
              <el-switch v-model="current.__attrs.showWordLimit" active-text="显示" inactive-text="隐藏"
                         :active-value="true" :inactive-value="false"/>
            </el-form-item>
            <el-form-item label="查看密码图标" v-if="'password'===current.__attrs.type">
              <el-switch v-model="current.__attrs.showPassword" active-text="显示" inactive-text="隐藏"
                         :active-value="true" :inactive-value="false"/>
            </el-form-item>
            <el-form-item label="行数" v-if="'textarea'===current.__attrs.type">
              <el-input-number v-model="current.__attrs.rows" placeholder="默认：2"/>
            </el-form-item>
            <el-form-item label="高度自适应" v-if="'textarea'===current.__attrs.type">
              <el-radio-group v-model="current.autosizeType">
                <el-radio-button label="不配置" value="noset"/>
                <el-radio-button label="boolean" value="boolean"/>
                <el-radio-button label="object" value="object"/>
              </el-radio-group>
              <el-switch v-if="current.autosizeType==='boolean'" active-text="是" inactive-text="否"
                         :active-value="true" :inactive-value="false" class="mt-5px"/>
              <div v-if="current.autosizeType==='object'" class="flex items-center mt-10px">
                <span class="text-12px text-#777 mr-2px">minRows</span>
                <el-input-number v-model="current.__attrs.autosize.minRows" :controls="false" class="w-40px!"/>
                <span class="text-12px text-#777 ml-7px mr-2px">- maxRows</span>
                <el-input-number v-model="current.__attrs.autosize.maxRows" :controls="false" class="w-40px!"/>
              </div>
            </el-form-item>
          </template>
          <!-- input-number属性 -->
          <template v-if="'el-input-number'===current.__key">
            <el-form-item label="最小值">
              <el-input-number v-model="current.__attrs.min"/>
            </el-form-item>
            <el-form-item label="最大值">
              <el-input-number v-model="current.__attrs.max"/>
            </el-form-item>
            <el-form-item label="步长step">
              <el-input-number v-model="current.__attrs.step"/>
            </el-form-item>
            <el-form-item label="严格模式">
              <el-switch v-model="current.__attrs.stepStrictly" active-text="是" inactive-text="否"
                         :active-value="true" :inactive-value="false"/>
              <el-tag :type="current.__attrs.stepStrictly?'success':'info'" class="ml-10px">只输入step的倍数</el-tag>
            </el-form-item>
            <el-form-item label="精度">
              <el-input-number v-model="current.__attrs.precision"/>
            </el-form-item>
            <el-form-item label="控制按钮">
              <el-switch v-model="current.__attrs.controls" active-text="显示" inactive-text="隐藏"
                         :active-value="true" :inactive-value="false"/>
            </el-form-item>
            <el-form-item label="控钮位置" v-if="current.__attrs.controls">
              <el-switch v-model="current.__attrs.controlsPosition" active-text="右侧" inactive-text="默认"
                         :active-value="'right'" :inactive-value="''"/>
            </el-form-item>
            <el-form-item label="文本对齐">
              <el-radio-group v-model="current.__attrs.align">
                <el-radio-button label="Left" value="left"/>
                <el-radio-button label="Center" value="center"/>
                <el-radio-button label="Right" value="right"/>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="科学计数">
              <el-switch v-model="current.__attrs.disabledScientific" active-text="禁用" inactive-text="启用"
                         :active-value="true" :inactive-value="false" class="mr-10px"/>
              <el-tag v-if="current.__attrs.disabledScientific" type="info" disable-transitions>不可输入'e'</el-tag>
              <el-tag v-else type="success" disable-transitions>可输入'e'</el-tag>
            </el-form-item>
          </template>
          <!-- input-tag属性 -->
          <template v-if="'el-input-tag'===current.__key">
            <el-form-item label="标签限制">
              <el-input-number v-model="current.__attrs.max" class="w-90px!"/>
              <span class="text-12px text-#999 ml-5px">可添加标签最大数量</span>
            </el-form-item>
            <el-form-item label="标签类型">
              <el-select v-model="current.__attrs.tagType" clearable placeholder="请选择标签类型（可为空）">
                <el-option :value="t" :label="t" :key="t" v-for="t in ['primary','success','info','warning','danger']"/>
              </el-select>
            </el-form-item>
            <el-form-item label="标签效果">
              <el-select v-model="current.__attrs.tagEffect" clearable placeholder="请选择标签效果（可为空）">
                <el-option :value="e" :label="e" :key="e" v-for="e in ['light','dark','plain']"/>
              </el-select>
            </el-form-item>
            <el-form-item label="触发按键">
              <el-switch v-model="current.__attrs.trigger" active-text="空格Space" inactive-text="回车Enter"
                         active-value="Space" inactive-value="Enter"/>
            </el-form-item>
            <el-form-item label="拖动标签">
              <el-switch v-model="current.__attrs.draggable" active-text="允许拖动" inactive-text="禁止拖动"
                         :active-value="true" :inactive-value="false"/>
            </el-form-item>
            <el-form-item label="分隔符">
              <el-input v-model="current.__attrs.delimiter" placeholder="分隔符自动分割（为空时不分割）" clearable/>
            </el-form-item>
            <el-form-item label="标签折叠">
              <el-switch v-model="current.__attrs.collapseTags" active-text="折叠" inactive-text="不折叠"
                         :active-value="true" :inactive-value="false"/>
            </el-form-item>
            <template v-if="current.__attrs.collapseTags">
              <el-form-item label="折叠提示">
                <el-switch v-model="current.__attrs.collapseTagsTooltip" active-text="折叠提示"
                           inactive-text="折叠不提示" :active-value="true" :inactive-value="false"/>
              </el-form-item>
              <el-form-item label="不折叠数">
                <el-input-number v-model="current.__attrs.maxCollapseTags" class="w-90px!"/>
                <span class="text-12px text-#999 ml-5px">超出该数的标签才折叠</span>
              </el-form-item>
            </template>
            <el-form-item label="失焦保存">
              <el-select v-model="current.__attrs.saveOnBlur" clearable placeholder="请选择失去焦点时是否保存">
                <el-option :value="true" label="失去焦点时保存"/>
                <el-option :value="false" label="失去焦点时不保存"/>
              </el-select>
            </el-form-item>
            <el-form-item label="清除按钮">
              <el-switch v-model="current.__attrs.clearable" active-text="显示" inactive-text="隐藏"
                         :active-value="true" :inactive-value="false"/>
            </el-form-item>
          </template>
          <!-- select属性 -->
          <template v-if="'el-select'===current.__key">
            <el-form-item label="多选">
              <el-switch v-model="current.__attrs.multiple" active-text="是" inactive-text="否"
                         :active-value="true" :inactive-value="false"/>
            </el-form-item>
            <el-form-item label="value键名">
              <el-input v-model="current.__attrs.valueKey" placeholder="绑定值为对象类型时必填"/>
            </el-form-item>
            <el-form-item label="清除按钮">
              <el-switch v-model="current.__attrs.clearable" active-text="显示" inactive-text="隐藏"
                         :active-value="true" :inactive-value="false"/>
            </el-form-item>
            <template v-if="current.__attrs.multiple">
              <el-form-item label="最多可选">
                <el-input-number v-model="current.__attrs.multipleLimit" class="w-90px!"/>
                <span class="text-12px text-#999 ml-5px">最多可选数,0不限制</span>
              </el-form-item>
              <!-- 多选时，配置标签折叠 -->
              <el-form-item label="选项折叠">
                <el-switch v-model="current.__attrs.collapseTags" active-text="折叠" inactive-text="不折叠"
                           :active-value="true" :inactive-value="false"/>
              </el-form-item>
              <template v-if="current.__attrs.collapseTags">
                <el-form-item label="折叠提示">
                  <el-switch v-model="current.__attrs.collapseTagsTooltip" active-text="折叠提示"
                             inactive-text="折叠不提示" :active-value="true" :inactive-value="false"/>
                </el-form-item>
                <el-form-item label="不折叠数">
                  <el-input-number v-model="current.__attrs.maxCollapseTags" class="w-90px!"/>
                  <span class="text-12px text-#999 ml-5px">超出该数的才折叠</span>
                </el-form-item>
              </template>
            </template>
            <el-form-item label="主题">
              <el-switch v-model="current.__attrs.effect" active-text="Dark" inactive-text="Light"
                         active-value="dark" inactive-value="light"/>
            </el-form-item>
            <el-form-item label="筛选">
              <el-switch v-model="current.__attrs.filterable" active-text="可查询" inactive-text="不可查询"
                         :active-value="true" :inactive-value="false"/>
            </el-form-item>
            <template v-if="current.__attrs.filterable">
              <el-form-item label="筛选提示">
                <el-input v-model="current.__attrs.noMatchText" clearable placeholder="筛选条件无匹配时显示的文字"/>
              </el-form-item>
              <el-form-item label="新条目">
                <el-switch v-model="current.__attrs.allowCreate" active-text="允许" inactive-text="不允许用户创建新条目"
                           :active-value="true" :inactive-value="false"/>
              </el-form-item>
            </template>
            <el-form-item label="无数据">
              <el-input v-model="current.__attrs.noDataText" clearable placeholder="无选项时显示的文字"/>
            </el-form-item>
            <el-form-item label="回车键">
              <el-switch v-model="current.__attrs.defaultFirstOption" active-text="选中第一项"
                         inactive-text="回车无操作"
                         :active-value="true" :inactive-value="false"/>
            </el-form-item>

          </template>

          <el-divider>test</el-divider>
          <base-button type="primary" @click="()=>{console.log(JSON.stringify(current.__attrs))}">测试属性</base-button>


          <el-divider>表单规则</el-divider>
          <form-item-rules v-if="current?.__id" :key="current.__id" v-model="currentRules"/>

        </el-form>
        <!-- 未选择组件时显示 -->
        <div v-else class="text-center text-14px color-gray mt-10%">
          请选择中间区域组件，配置组件属性
        </div>
      </div>
    </el-tab-pane>

    <!-- tab2表单属性 -->
    <el-tab-pane label="表单属性" name="formTab">
      <div class="mx-10px">
        <el-form :model="formProps" label-width="auto" size="small">
          <el-divider>基础属性</el-divider>
          <el-form-item label="标签对齐">
            <el-radio-group v-model="formProps.__attrs.labelPosition">
              <el-radio-button label="左对齐" value="left"/>
              <el-radio-button label="右对齐" value="right"/>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="标签宽度">
            <el-input v-model="formProps.__attrs.labelWidth" placeholder="示例:auto、100px、10%"/>
          </el-form-item>
          <el-form-item label="标签后缀">
            <el-input v-model="formProps.__attrs.labelSuffix" placeholder="标签后缀"/>
          </el-form-item>
          <el-form-item label="必填项星号">
            <el-radio-group v-model="formProps.__attrs.hideRequiredAsterisk">
              <el-radio-button label="显示" :value="false"/>
              <el-radio-button label="隐藏" :value="true"/>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="必填星号位置">
            <el-radio-group v-model="formProps.__attrs.requireAsteriskPosition">
              <el-radio-button label="左侧" value="left"/>
              <el-radio-button label="右侧" value="right"/>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="尺寸">
            <el-radio-group v-model="formProps.__attrs.size">
              <el-radio-button label="Large" value="large"/>
              <el-radio-button label="Default" value="default"/>
              <el-radio-button label="Small" value="small"/>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="表单状态">
            <el-radio-group v-model="formProps.__attrs.disabled">
              <el-radio-button label="启用" :value="false"/>
              <el-radio-button label="禁用" :value="true"/>
            </el-radio-group>
          </el-form-item>
          <el-divider>校验相关属性</el-divider>
          <el-form-item label="校验错误提示">
            <el-radio-group v-model="formProps.__attrs.showMessage">
              <el-radio-button label="显示" :value="true"/>
              <el-radio-button label="隐藏" :value="false"/>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="校验错误位置">
            <el-radio-group v-model="formProps.__attrs.inlineMessage">
              <el-radio-button label="换行显示" :value="false"/>
              <el-radio-button label="行内显示" :value="true"/>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="校验反馈图标">
            <el-radio-group v-model="formProps.__attrs.statusIcon">
              <el-radio-button label="显示" :value="true"/>
              <el-radio-button label="隐藏" :value="false"/>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="定位校验错误">
            <el-radio-group v-model="formProps.__attrs.scrollToError">
              <el-radio-button label="自动聚焦" :value="true"/>
              <el-radio-button label="不聚焦" :value="false"/>
            </el-radio-group>
            <div class="w-full color-[var(--el-color-danger)] text-12px">
              {{ formProps.__attrs.scrollToError ? '校验失败时,滚动到第一个错误表单' : '校验失败时,不滚动' }}
            </div>
          </el-form-item>
          <el-divider>布局属性</el-divider>
          <el-form-item label="栅格布局">
            <el-radio-group v-model="formProps.__layout.layout">
              <el-radio-button label="普通布局" :value="false"/>
              <el-radio-button label="栅格布局" :value="true"/>
            </el-radio-group>
          </el-form-item>
          <template v-if="formProps.__layout.layout">
            <el-form-item label="栅格间隔">
              <el-input-number v-model="formProps.__layout.gutter"/>
            </el-form-item>
            <el-form-item label="水平排列方式">
              <el-select v-model="formProps.__layout.justify">
                <el-option label="Start" value="start"/>
                <el-option label="End" value="end"/>
                <el-option label="Center" value="center"/>
                <el-option label="SpaceAround" value="space-around"/>
                <el-option label="SpaceBetween" value="space-between"/>
                <el-option label="SpaceEvenly" value="space-evenly"/>
              </el-select>
            </el-form-item>
            <el-form-item label="垂直排列方式">
              <el-select v-model="formProps.__layout.align" clearable>
                <el-option label="Top" value="top"/>
                <el-option label="Middle" value="middle"/>
                <el-option label="Bottom" value="bottom"/>
              </el-select>
            </el-form-item>
          </template>

        </el-form>
      </div>
    </el-tab-pane>
  </el-tabs>
</template>

<script setup lang="ts">
import { ElMessage } from 'element-plus'
import { cloneDeep } from 'lodash-es'
import { VueDraggable } from 'vue-draggable-plus'
import { getDict } from '@/utils/dict-util'
import { JsonEditor } from '@/components/JsonEditor'
import allConfig from '@/views/generator/panel/config/allConfig'
import FormItemRules from '@/views/generator/panel/FormItemRules'
import request from '@/utils/request'

// 全部组件类型
const componentList: any = cloneDeep(allConfig)

// 绑定组件属性
const current = defineModel('current', {type: Object, default: () => {}})
// 绑定表单属性
const formProps = defineModel('formProps', {type: Object, default: () => {}})

// 表单验证规则（切换当前组件时，自动更新）
const currentRules = ref([])
watch(() => current.value.__id, () => { currentRules.value = current.value?.__formItemAttrs?.rules || [] })

// 切换组件类型
const componentChange = (val) => {
  current.value.__attrs = {...componentList.flatMap(o => o.list).find(i => i.__key === val).__attrs}
}
// input组件，文本类型变化
const typeChange = (val) => {
  if (val === 'textarea') delete current.value.__attrs.clearable
  if (val !== 'textarea') {
    delete current.value.__attrs.rows
    delete current.value.__attrs.autosize
  }
  if (val === 'password') delete current.value.__attrs.showWordLimit
  if (val !== 'password') delete current.value.__attrs.showPassword
}
// textarea组件，高度自适应变化
watch(() => current.value.autosizeType, (val) => {
  if (val === 'boolean') current.value.__attrs.autosize = true
  else if (val === 'object') current.value.__attrs.autosize = {minRows: 2, maxRows: 5}
  else delete current.value.__attrs.autosize
})
// input-number监听控制按钮显示/隐藏
watch(() => current.value.__attrs?.controls, (val) => {
  current.value.__attrs.controlsPosition = ''
  if (!val) delete current.value.__attrs.controlsPosition
})
// input-tag监听标签折叠/不折叠
watch(() => current.value.__attrs?.collapseTags, (val) => {
  delete current.value.__attrs.collapseTags
  delete current.value.__attrs.collapseTagsTooltip
  delete current.value.__attrs.maxCollapseTags
  if (val) {
    current.value.__attrs.collapseTags = true
    current.value.__attrs.collapseTagsTooltip = true
  }
})
// select组件，监听数据类型
const dataTypeClick = (val) => {
  console.log(222, val)
  if ('static' === val) {
    current.value.__attrs.props = {value: 'value', label: 'label', disabled: 'disabled'}
    current.value.__attrs.options = [{value: 'value1', label: 'label1'}]
  } else if ('dynamic' === val) {
    current.value.dataDynamic = {url: '/role/list', dataKey: 'data.records'}
    current.value.__attrs.props = {value: 'roleId', label: 'roleName', options: 'children', disabled: 'disabled'}
    current.value.__attrs.options = []
    refreshDynamicData()
  } else if ('dict' === val) {
    current.value.dictCode = 'sys_user_sex'
    current.value.__attrs.props = {value: 'dictValue', label: 'dictLabel', disabled: 'disabled'}
    current.value.__attrs.options = []
    refreshDictData()
  }

}
const addStaticData = () => {
  const obj = cloneDeep(current.value.__attrs.options)
  current.value.__attrs.options = []
  current.value.__attrs.options = [...obj, {value: 'value' + (obj.length + 1), label: 'label' + (obj.length + 1)}]
}
const refreshDynamicData = () => {
  const obj = current.value.dataDynamic
  if (!obj.url) {
    ElMessage({message: '接口地址不能为空！', type: 'warning', grouping: true})
    return
  }
  if (!obj.dataKey) {
    ElMessage({message: '数据字段不能为空！', type: 'warning', grouping: true})
    return
  }
  request({url: obj.url, method: 'get'}).then((response) => {
    current.value.__attrs.options = obj.dataKey.split('.').reduce((acc, k) => acc[k], response) || []
  })
}
const refreshDictData = () => {
  if (!current.value.dictCode) {
    ElMessage({message: '数据字典类型code不能为空！', type: 'warning', grouping: true})
    return
  }
  current.value.__attrs.options = getDict(current.value.dictCode)
}
</script>

<style lang="less" scoped>
.dataTab {
  :deep(.el-tabs__item) {
    height: 30px;
  }
  :deep(.el-tabs__content) {
    padding: 5px 5px 5px 0;
  }
}
</style>