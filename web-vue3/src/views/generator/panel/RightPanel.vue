<template>
  <el-tabs class="w-full" stretch model-value="componentTab">
    <!-- tab1组件属性 -->
    <el-tab-pane label="组件属性" name="componentTab">
      <div class="ml-10px mr-5px h-[calc(100vh-55px)] overflow-x-hidden overflow-y-auto">
        <el-form v-if="current?.__id" :model="current" label-width="auto" size="small">
          <!-- 组件类型 -->
          <el-form-item label="组件类型" prop="__key">
            <el-select v-model="current.__key" filterable @change="componentChange" class="w-65%!">
              <el-option-group v-for="group in componentList" :key="group.name" :label="group.name">
                <el-option v-for="item in group.list" :key="item.__key" :label="item.__name" :value="item.__key">
                  <my-icon v-if="item.__icon" :icon="item.__icon"/>
                  {{ item.__name }}
                </el-option>
              </el-option-group>
            </el-select>
            <a v-if="current.__docLink" :href="current.__docLink" target="_blank" class="ml-2% w-33%!">
              <base-button type="primary" plain icon="el-icon-link" class="w-full">文档</base-button>
            </a>
            <base-button v-else type="info" link plain class="ml-2% w-33%!">无文档</base-button>
          </el-form-item>
          <!-- input-文本类型 -->
          <el-form-item v-if="'el-input'===current.__key" label="文本类型">
            <el-select v-model="current.__attrs.type" @change="typeChange">
              <el-option label="单行文本框" value="text"/>
              <el-option label="多行文本框" value="textarea"/>
              <el-option label="密码框" value="password"/>
            </el-select>
          </el-form-item>
          <!-- date-日期类型 -->
          <el-form-item v-if="'el-date-picker'===current.__key" label="日期类型">
            <el-select v-model="current.__attrs.type" filterable @change="dateTypeChange">
              <el-option v-for="item in dateType" :label="item" :value="item" :key="item"/>
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

          <!-- select数据 -->
          <template v-if="'el-select'===current.__key">
            <data-select v-model="current" :key="current.__id"/>
          </template>
          <!-- cascader数据 -->
          <template v-if="'el-cascader'===current.__key">
            <data-cascader v-model="current" :key="current.__id"/>
          </template>
          <!-- radio-group数据 -->
          <template v-if="'el-radio-group'===current.__key">
            <data-radio-group v-model="current" :key="current.__id"/>
          </template>
          <!-- radio数据 -->
          <template v-if="'el-radio'===current.__key">
            <data-radio v-model="current" :key="current.__id"/>
          </template>
          <!-- checkbox-group数据 -->
          <template v-if="'el-checkbox-group'===current.__key">
            <data-checkbox-group v-model="current" :key="current.__id"/>
          </template>
          <!-- checkbox数据 -->
          <template v-if="'el-checkbox'===current.__key">
            <data-checkbox v-model="current" :key="current.__id"/>
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
                         inactive-text="回车无操作" :active-value="true" :inactive-value="false"/>
            </el-form-item>
          </template>
          <!-- datetime属性 -->
          <template v-if="'el-date-picker'===current.__key">
            <el-form-item label="可输入">
              <el-switch v-model="current.__attrs.editable" active-text="是" inactive-text="否"
                         :active-value="true" :inactive-value="false"/>
            </el-form-item>
            <el-form-item label="清除按钮">
              <el-switch v-model="current.__attrs.clearable" active-text="显示" inactive-text="隐藏"
                         :active-value="true" :inactive-value="false"/>
            </el-form-item>
            <el-form-item label="显示格式">
              <el-input v-model="current.__attrs.format" clearable placeholder="显示在输入框中的格式"/>
              <a href="https://day.js.org/docs/zh-CN/display/format" target="_blank">
                <base-button type="primary" link icon="el-icon-link">更多格式文档</base-button>
              </a>
            </el-form-item>
            <el-form-item label="绑定格式">
              <el-input v-model="current.__attrs.valueFormat" clearable placeholder="绑定值的格式"/>
            </el-form-item>
            <template v-if="['datetimerange','daterange','monthrange','yearrange'].includes(current.__attrs.type)">
              <el-form-item label="开始占位">
                <el-input v-model="current.__attrs.startPlaceholder" clearable placeholder="范围选择-开始日期占位内容"/>
              </el-form-item>
              <el-form-item label="结束占位">
                <el-input v-model="current.__attrs.endPlaceholder" clearable placeholder="范围选择-结束日期占位内容"/>
              </el-form-item>
              <el-form-item label="分隔符">
                <el-input v-model="current.__attrs.rangeSeparator" clearable placeholder="选择范围时的分隔符"/>
              </el-form-item>
            </template>
            <template v-if="['dates','months','years','datetime','datetimerange'].includes(current.__attrs.type)">
              <el-form-item label="页脚">
                <el-switch v-model="current.__attrs.showFooter" active-text="显示" inactive-text="隐藏"
                           :active-value="true" :inactive-value="false"/>
              </el-form-item>
              <el-form-item label="确定按钮">
                <el-switch v-model="current.__attrs.showConfirm" active-text="显示" inactive-text="隐藏"
                           :active-value="true" :inactive-value="false"/>
              </el-form-item>
              <el-form-item label="周数">
                <el-switch v-model="current.__attrs.showWeekNumber" active-text="显示" inactive-text="隐藏"
                           :active-value="true" :inactive-value="false"/>
              </el-form-item>
            </template>
          </template>
          <!-- cascader属性 -->
          <template v-if="'el-cascader'===current.__key">
            <el-form-item label="清除按钮">
              <el-switch v-model="current.__attrs.clearable" active-text="显示" inactive-text="隐藏"
                         :active-value="true" :inactive-value="false"/>
            </el-form-item>
            <el-form-item label="显示路径">
              <el-switch v-model="current.__attrs.showAllLevels" active-text="显示" inactive-text="输入框不显示完整路径"
                         :active-value="true" :inactive-value="false"/>
            </el-form-item>
            <template v-if="current.__attrs.props?.multiple">
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
            <el-form-item label="分隔符">
              <el-input v-model="current.__attrs.separator" clearable placeholder="分隔选项的字符"/>
            </el-form-item>
            <el-form-item label="筛选">
              <el-switch v-model="current.__attrs.filterable" active-text="可查询" inactive-text="不可查询"
                         :active-value="true" :inactive-value="false"/>
            </el-form-item>
            <el-form-item label="去抖延迟">
              <el-input-number v-model="current.__attrs.debounce" :step="100" class="w-90px!"/>
              <span class="text-12px text-#999 ml-5px">毫秒</span>
              <div class="text-12px text-#999">输入搜索词时的去抖延迟,默认300ms</div>
            </el-form-item>
          </template>
          <!-- radio-group属性 -->
          <template v-if="'el-radio-group'===current.__key">
            <div class="flex flex-1 justify-center items-center text-12px my-20px color-gray-5">无</div>
          </template>
          <!-- radio属性 -->
          <template v-if="'el-radio'===current.__key">
            <el-form-item label="单选类型">
              <el-switch v-model="current.radioType" active-text="按钮" inactive-text="原始"
                         active-value="button" inactive-value="radio"/>
            </el-form-item>
            <el-form-item label="边框">
              <el-switch v-model="current.__attrs.border" active-text="有边框" inactive-text="无边框"
                         :active-value="true" :inactive-value="false"/>
            </el-form-item>
          </template>
          <!-- checkbox-group属性 -->
          <template v-if="'el-checkbox-group'===current.__key">
            <el-form-item label="最小数">
              <el-input-number v-model="current.__attrs.min" class="w-90px!"/>
              <span class="text-12px text-#999 ml-5px">可被勾选的最小数</span>
            </el-form-item>
            <el-form-item label="最大数">
              <el-input-number v-model="current.__attrs.max" class="w-90px!"/>
              <span class="text-12px text-#999 ml-5px">可被勾选的最大数</span>
            </el-form-item>
          </template>
          <!-- checkbox属性 -->
          <template v-if="'el-checkbox'===current.__key">
            <el-form-item label="多选类型">
              <el-switch v-model="current.checkboxType" active-text="按钮" inactive-text="原始"
                         active-value="button" inactive-value="radio"/>
            </el-form-item>
            <el-form-item label="边框">
              <el-switch v-model="current.__attrs.border" active-text="有边框" inactive-text="无边框"
                         :active-value="true" :inactive-value="false"/>
            </el-form-item>
          </template>
          <!-- switch属性 -->
          <template v-if="'el-switch'===current.__key">
            <el-form-item label="文字位置">
              <el-switch v-model="current.__attrs.inlinePrompt" active-text="内部" inactive-text="显示在按钮外部"
                         :active-value="true" :inactive-value="false"/>
            </el-form-item>
            <el-form-item label="开启文字">
              <el-input v-model="current.__attrs.activeText" clearable placeholder="开启时显示的文字"/>
            </el-form-item>
            <el-form-item label="关闭文字">
              <el-input v-model="current.__attrs.inactiveText" clearable placeholder="关闭时显示的文字"/>
            </el-form-item>
            <el-divider>Value属性</el-divider>
            <el-form-item label="value类型">
              <el-radio-group v-model="current.__valueType" class="w-full mb-5px" @change="switchValueTypeChange">
                <el-radio-button label="boolean" value="boolean"/>
                <el-radio-button label="number" value="number"/>
                <el-radio-button label="string" value="string"/>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="开启value">
              <el-switch v-if="current.__valueType==='boolean'" v-model="current.__attrs.activeValue"
                         active-text="true" inactive-text="false" :active-value="true" :inactive-value="false"/>
              <el-input-number v-if="current.__valueType==='number'" v-model="current.__attrs.activeValue"/>
              <el-input v-if="current.__valueType==='string'" v-model="current.__attrs.activeValue"/>
            </el-form-item>
            <el-form-item label="关闭value">
              <el-switch v-if="current.__valueType==='boolean'" v-model="current.__attrs.inactiveValue"
                         active-text="true" inactive-text="false" :active-value="true" :inactive-value="false"/>
              <el-input-number v-if="current.__valueType==='number'" v-model="current.__attrs.inactiveValue"/>
              <el-input v-if="current.__valueType==='string'" v-model="current.__attrs.inactiveValue"/>
            </el-form-item>
          </template>
          <!-- wangEditor属性 -->
          <template v-if="'my-wang-editor'===current.__key">
            <el-form-item label="编辑器高度">
              <el-input v-model="current.__attrs.height" placeholder="编辑器高度"/>
            </el-form-item>
          </template>
          <!-- imageAvatar属性、imageOne属性 -->
          <template v-if="['image-avatar','image-one'].includes(current.__key)">
            <el-form-item label="大小限制">
              <el-input-number v-model="current.__attrs.limitSize" :step="1024" placeholder="上传图片大小限制"/>
              <span class="text-12px text-#999 ml-5px">单位：b</span>
              <div class="text-12px text-red">上传图片大小限制：{{ formatSize(current.__attrs.limitSize) || '' }}</div>
            </el-form-item>
          </template>
          <!-- imageUpload属性 -->
          <template v-if="'image-upload'===current.__key">
            <el-form-item label="多选">
              <el-switch v-model="current.__attrs.multiple" active-text="多选" inactive-text="单选"
                         :active-value="true" :inactive-value="false"/>
            </el-form-item>
            <el-form-item label="大小限制">
              <el-input-number v-model="current.__attrs.limitSize" :step="1024" placeholder="上传图片大小限制"/>
              <span class="text-12px text-#999 ml-5px">单位：b</span>
              <div class="text-12px text-red">上传图片大小限制：{{ formatSize(current.__attrs.limitSize) || '' }}</div>
            </el-form-item>
            <el-form-item label="个数限制">
              <el-input-number v-model="current.__attrs.limitCount" placeholder="上传图片个数限制"/>
              <span class="text-12px text-#999 ml-5px">0表示不限制</span>
            </el-form-item>
          </template>
          <!-- fileUpload属性 -->
          <template v-if="'file-upload'===current.__key">
            <el-form-item label="类型限制">
              <el-tooltip placement="left" content="以本输入框内容为最终限制类型（下拉框可快速选择常见类型）">
                <el-input v-model="current.__attrs.accept" clearable placeholder="允许上传的文件类型"/>
              </el-tooltip>
              <el-select v-model="current.acceptSelect" placeholder="请选择文件类型" multiple clearable class="mt-10px">
                <el-option label="图片-常用" value=".jpg,.jpeg,.png,.gif,.bmp"/>
                <el-option label="Excel" value=".xls,.xlsx"/>
                <el-option label="Word" value=".doc,.docx"/>
                <el-option label="PPT" value=".ppt,.pptx"/>
                <el-option label="Pdf" value=".pdf"/>
                <el-option label="Txt" value=".txt"/>
                <el-option label="Zip" value=".zip"/>
                <el-option label="Rar" value=".rar"/>
                <el-option label="Mp4" value=".mp4"/>
                <el-option label="Mp3" value=".mp3"/>
                <el-option label="图片-全" value="image/*"/>
                <el-option label="视频-全" value="video/*"/>
                <el-option label="音频-全" value="audio/*"/>
              </el-select>
            </el-form-item>
            <el-form-item label="按钮文字">
              <el-input v-model="current.__attrs.btnTitle" clearable placeholder="按钮文字"/>
            </el-form-item>
            <el-form-item label="提示信息">
              <el-switch v-model="current.__attrs.showTip" active-text="显示" inactive-text="隐藏"
                         :active-value="true" :inactive-value="false"/>
              <el-input v-if="current.__attrs.showTip" class="mt-5px"
                        v-model="current.__attrs.tipInfo" clearable placeholder="提示文字"/>
            </el-form-item>
            <el-form-item label="多选">
              <el-switch v-model="current.__attrs.multiple" active-text="多选" inactive-text="单选"
                         :active-value="true" :inactive-value="false"/>
            </el-form-item>
            <el-form-item label="文件列表">
              <el-switch v-model="current.__attrs.showFileList" active-text="显示" inactive-text="隐藏"
                         :active-value="true" :inactive-value="false"/>
            </el-form-item>
            <el-form-item label="自动上传">
              <el-switch v-model="current.__attrs.autoUpload" active-text="自动上传" inactive-text="手动上传"
                         :active-value="true" :inactive-value="false"/>
            </el-form-item>
            <el-form-item label="大小限制">
              <el-input-number v-model="current.__attrs.limitSize" :step="1024" placeholder="上传图片大小限制"/>
              <span class="text-12px text-#999 ml-5px">单位：b</span>
              <div class="text-12px text-red">上传图片大小限制：{{ formatSize(current.__attrs.limitSize) || '' }}</div>
            </el-form-item>
            <el-form-item label="个数限制">
              <el-input-number v-model="current.__attrs.limitCount" placeholder="上传图片个数限制"/>
              <span class="text-12px text-#999 ml-5px">0表示不限制</span>
            </el-form-item>
          </template>
          <!-- slider属性 -->
          <template v-if="'el-slider'===current.__key">
            <el-form-item label="最小值">
              <el-input-number v-model="current.__attrs.min"/>
            </el-form-item>
            <el-form-item label="最大值">
              <el-input-number v-model="current.__attrs.max"/>
            </el-form-item>
            <el-form-item label="步长">
              <el-input-number v-model="current.__attrs.step"/>
            </el-form-item>
            <el-form-item label="输入框">
              <el-switch v-model="current.__attrs.showInput" active-text="显示" inactive-text="隐藏"
                         :active-value="true" :inactive-value="false"/>
            </el-form-item>
            <el-form-item label="控制按钮" v-if="current.__attrs?.showInput">
              <el-switch v-model="current.__attrs.showInputControls" active-text="显示" inactive-text="隐藏"
                         :active-value="true" :inactive-value="false"/>
            </el-form-item>
            <el-form-item label="间断点">
              <el-switch v-model="current.__attrs.showStops" active-text="显示" inactive-text="隐藏"
                         :active-value="true" :inactive-value="false"/>
            </el-form-item>
            <el-form-item label="ToolTip">
              <el-switch v-model="current.__attrs.showTooltip" active-text="显示" inactive-text="隐藏"
                         :active-value="true" :inactive-value="false"/>
            </el-form-item>
            <el-form-item label="位置" v-if="current.__attrs.showTooltip">
              <el-select v-model="current.__attrs.placement" clearable filterable class="w-100px!">
                <el-option v-for="(r,i) in placementList" :key="i" :value="r" :label="r"/>
              </el-select>
              <span class="text-12px color-gray-5 ml-5px">ToolTip显示位置</span>
            </el-form-item>
            <el-form-item label="选择范围">
              <el-switch v-model="current.__attrs.range" active-text="开启" inactive-text="关闭"
                         :active-value="true" :inactive-value="false"/>
            </el-form-item>
            <el-form-item label="垂直模式">
              <el-switch v-model="current.__attrs.vertical" active-text="开启" inactive-text="关闭"
                         :active-value="true" :inactive-value="false"/>
            </el-form-item>
            <el-form-item label="滑块高度" v-if="current.__attrs.vertical">
              <el-input v-model="current.__attrs.height" clearable placeholder="滑块高度（垂直模式必填）"/>
            </el-form-item>
            <el-form-item label="标记">
              <el-input v-model="current.__attrs.marks" clearable placeholder="突出显示标记的点（格式：{10:'提示'}）"/>
            </el-form-item>

          </template>
          <!-- rate属性 -->
          <template v-if="'el-rate'===current.__key">
            <el-form-item label="最大值">
              <el-input-number v-model="current.__attrs.max"/>
            </el-form-item>
            <el-form-item label="半选">
              <el-switch v-model="current.__attrs.allowHalf" active-text="允许半选" inactive-text="不允许"
                         :active-value="true" :inactive-value="false"/>
            </el-form-item>
            <el-form-item label="低-中分">
              <el-input-number v-model="current.__attrs.lowThreshold"/>
              <span class="text-12px text-gray-5">低分和中等分数的界限值，值本身算低分</span>
            </el-form-item>
            <el-form-item label="中-高分">
              <el-input-number v-model="current.__attrs.highThreshold"/>
              <span class="text-12px text-gray-5">高分和中等分数的界限值，值本身算高分</span>
            </el-form-item>
            <el-form-item label="分段颜色">
              <span class="mr-5px">低分:</span>
              <el-color-picker v-model="current.__attrs.colors[0]" value-on-clear="#f7ba2a"/>
              <span class="ml-10px mr-5px">中分:</span>
              <el-color-picker v-model="current.__attrs.colors[1]" value-on-clear="#f7ba2a"/>
              <span class="ml-10px mr-5px">高分:</span>
              <el-color-picker v-model="current.__attrs.colors[2]" value-on-clear="#f7ba2a"/>
            </el-form-item>
            <el-form-item label="未选颜色">
              <span class="mr-5px">正常未选中:</span>
              <el-color-picker v-model="current.__attrs.voidColor" value-on-clear="#c6d1de"/>
              <span class="ml-10px mr-5px">只读时未选中:</span>
              <el-color-picker v-model="current.__attrs.disabledVoidColor" value-on-clear="#eff2f7"/>
            </el-form-item>
            <el-form-item label="辅助文本">
              <el-select v-model="current.rateTxtType" clearable>
                <el-option :value="'score'" label="显示当前分数"/>
                <el-option :value="'text'" label="显示辅助文字"/>
              </el-select>
            </el-form-item>
            <template v-if="current.rateTxtType==='score'">
              <el-form-item label="分数">
                <el-switch v-model="current.__attrs.showScore" active-text="显示" inactive-text="隐藏"
                           :active-value="true" :inactive-value="false"/>
              </el-form-item>
              <el-form-item label="分数模板" v-if="current.__attrs.showScore">
                <el-input v-model="current.__attrs.scoreTemplate" clearable placeholder="{value}分"/>
              </el-form-item>
            </template>
            <template v-if="current.rateTxtType==='text'">
              <el-form-item label="辅助文字">
                <el-switch v-model="current.__attrs.showText" active-text="显示" inactive-text="隐藏"
                           :active-value="true" :inactive-value="false"/>
              </el-form-item>
              <el-form-item label="最大值" v-if="current.__attrs.showText">
                <el-input v-model="current.__attrs.texts" clearable placeholder="['极差', '差', '一般', '好', '极好']"/>
              </el-form-item>
            </template>
            <el-form-item label="辅助颜色" v-if="current.rateTxtType">
              <el-color-picker v-model="current.__attrs.textColor"/>
            </el-form-item>
          </template>
          <!-- color属性 -->
          <template v-if="'el-color-picker'===current.__key">
            <el-form-item label="透明度">
              <el-switch v-model="current.__attrs.showAlpha" active-text="支持透明度" inactive-text="不支持"
                         :active-value="true" :inactive-value="false"/>
            </el-form-item>
            <el-form-item label="颜色格式">
              <el-select v-model="current.__attrs.colorFormat" clearable>
                <el-option v-for="(r,i) in ['rgb','hsl','hsv','hex','rgb','hex']" :key="i" :value="r" :label="r"/>
              </el-select>
            </el-form-item>
            <el-form-item label="预定义">
              <el-input v-model="current.__attrs.predefine" clearable placeholder="示例：['#67C23A','#F56C6C']"/>
            </el-form-item>
            <el-form-item label="清空值">
              <el-input v-model="current.__attrs.valueOnClear" clearable placeholder="示例：'#909399'"/>
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
import { cloneDeep } from 'lodash-es'
import { formatSize } from '@/utils/tools'
import allConfig from '@/views/generator/panel/config/allConfig'
import FormItemRules from '@/views/generator/panel/FormItemRules'
import DataSelect from '@/views/generator/panel/DataSelect'
import DataCascader from '@/views/generator/panel/DataCascader'
import DataRadio from '@/views/generator/panel/DataRadio'
import DataRadioGroup from '@/views/generator/panel/DataRadioGroup'
import DataCheckbox from '@/views/generator/panel/DataCheckbox'
import DataCheckboxGroup from '@/views/generator/panel/DataCheckboxGroup'

// 全部组件类型
const componentList: any = cloneDeep(allConfig)

// 绑定组件属性
const current = defineModel('current', {type: Object, default: () => {}})
// 绑定表单属性
const formProps = defineModel('formProps', {type: Object, default: () => {}})
// 日期选择类型
const dateType = ref(['date', 'datetime', 'daterange', 'datetimerange',
  'dates', 'week', 'month', 'months', 'monthrange', 'year', 'years', 'yearrange'])
const placementList = ref(['top', 'top-start', 'top-end', 'bottom', 'bottom-start', 'bottom-end',
  'left', 'left-start', 'left-end', 'right', 'right-start', 'right-end'])

// 表单验证规则（切换当前组件时，自动更新）
const currentRules = ref([])
watch(() => current.value.__id, () => { currentRules.value = current.value?.__formItemAttrs?.rules || [] })

// 切换组件类型
const componentChange = (val) => {
  // 保留旧组件的数据
  const oldData = {
    __id: current.value.__id,
    __modelName: current.value.__modelName,
    __name: current.value.__name,
    __formItemAttrs: {...current.value.__formItemAttrs}
  }
  // 查找新组件类型
  const newData = componentList.flatMap(o => o.list).find(i => i.__key === val)
  // 重置
  current.value = {}
  current.value = cloneDeep({...newData, ...oldData})
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
// date组件，日期类型变化
const dateTypeChange = (val) => {
  const formatStr = ['year', 'years', 'yearrange'].includes(val) ? 'YYYY' :
      ['month', 'months', 'monthrange'].includes(val) ? 'YYYY-MM' :
          ['date', 'dates', 'daterange'].includes(val) ? 'YYYY-MM-DD' :
              ['datetime', 'datetimerange'].includes(val) ? 'YYYY-MM-DD HH:mm:ss' :
                  'week' === val ? 'YYYY-wo' : 'YYYY-MM-DD'
  current.value.__attrs.format = formatStr
  current.value.__attrs.valueFormat = formatStr
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
// switch数据类型切换
const switchValueTypeChange = (val) => {
  current.value.__attrs.activeValue = val === 'boolean' ? true : val === 'number' ? 1 : 'on'
  current.value.__attrs.inactiveValue = val === 'boolean' ? false : val === 'number' ? 0 : 'off'
}
// file-upload监听showTip变更
watch(() => current.value.__attrs?.showTip, (val) => {
  if (val) current.value.__attrs.tipInfo = '支持图片、Word、Excel、Pdf、Rar、Zip格式的文件'
  else delete current.value.__attrs.tipInfo
})
// file-upload监听文件类型变更
watch(() => current.value.acceptSelect, (val) => {
  if (val) current.value.__attrs.accept = val.join(',')
})
// slider监听属性变化
watch(() => current.value.__attrs?.showInput, (val) => { if (!val) delete current.value.__attrs.showInputControls })
watch(() => current.value.__attrs?.showTooltip, (val) => { if (!val) delete current.value.__attrs.placement })
watch(() => current.value.__attrs?.vertical, (val) => { if (!val) delete current.value.__attrs.height })
// rate监听属性变化
watch(() => current.value?.rateTxtType, (val) => {
  console.log(222)
  if (val === 'score') delete current.value.__attrs.showText
  else if (val === 'text') delete current.value.__attrs.showScore
  else {
    delete current.value.__attrs.showScore
    delete current.value.__attrs.showText
    delete current.value.__attrs.textColor
  }
})
watch(() => current.value.__attrs?.showScore, (val) => { if (!val) delete current.value.__attrs.scoreTemplate })
watch(() => current.value.__attrs?.showText, (val) => { if (!val) delete current.value.__attrs.texts })


</script>

<style lang="less" scoped>
</style>