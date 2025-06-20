/**
 * stylelint 配置文件
 * Stylelint 是一个强大、先进的 CSS 代码检查器（linter），可以帮助你规避 CSS 代码中的错误并保持一致的编码风格。
 *
 * 详情参考官方文档：https://www.stylelint.cn/
 */
module.exports = {
  // 设定为 true 以表示这是配置文件的根目录
  root: true,
  // 使用的插件列表
  plugins: ['stylelint-order'],
  // 自定义语法，这里使用 postcss-html 以支持 HTML 文件中的样式检查
  customSyntax: 'postcss-html',
  // 扩展的配置文件列表
  extends: ['stylelint-config-standard'],
  // 规则列表
  rules: {
    // 忽略未知的伪类选择器，这里忽略 'global' 和 'deep'
    'selector-pseudo-class-no-unknown': [true, {ignorePseudoClasses: ['global', 'deep']}],
    // 忽略未知的 at 规则，这里忽略 'function', 'if', 'each', 'include', 'mixin'
    'at-rule-no-unknown': [true, {ignoreAtRules: ['function', 'if', 'each', 'include', 'mixin']}],
    // 禁用对无效媒体查询的检查
    'media-query-no-invalid': null,
    // 禁用对未知函数的检查
    'function-no-unknown': null,
    // 禁用对空源文件的检查
    'no-empty-source': null,
    // 禁用对无效命名网格区域的检查
    'named-grid-areas-no-invalid': null,
    // 禁用对规则特异性递减顺序的检查
    'no-descending-specificity': null,
    // 禁用对字体族缺少通用字体关键字的检查
    'font-family-no-missing-generic-family-keyword': null,
    // 规则前应该始终有空行，除非这条规则在注释之后或者它是嵌套规则的第一个
    'rule-empty-line-before': ['always', {ignore: ['after-comment', 'first-nested']}],
    // 忽略未知的单位，这里忽略 'rpx'
    'unit-no-unknown': [true, {ignoreUnits: ['rpx']}],
    // 样式顺序规则，指定样式块中属性的顺序
    'order/order': [
      ['dollar-variables', 'custom-properties', 'at-rules', 'declarations',
        {type: 'at-rule', name: 'supports'}, {type: 'at-rule', name: 'media'}, 'rules'],
      {severity: 'warning'} // 设置为警告级别
    ],
    // 属性顺序规则，指定样式中属性的具体顺序
    'order/properties-order': [
      'position', 'top', 'right', 'bottom', 'left', // 位置相关属性
      'z-index', 'display', 'float', // 显示相关属性
      'width', 'height', 'max-width', 'max-height', 'min-width', 'min-height', // 尺寸相关属性
      'padding', 'padding-top', 'padding-right', 'padding-bottom', 'padding-left', // 内边距相关属性
      'margin', 'margin-top', 'margin-right', 'margin-bottom', 'margin-left', // 外边距相关属性
      'margin-collapse', 'margin-top-collapse', 'margin-right-collapse', 'margin-bottom-collapse', 'margin-left-collapse', // 边距折叠属性（不常见）
      'overflow', 'overflow-x', 'overflow-y', // 溢出处理属性
      'clip', 'clear', // 剪裁和清除属性
      'font', 'font-family', 'font-size', 'font-smoothing', 'osx-font-smoothing', 'font-style', 'font-weight', // 字体相关属性
      'hyphens', 'src', 'line-height', 'letter-spacing', 'word-spacing', 'color', // 文本相关属性
      'text-align', 'text-decoration', 'text-indent', 'text-overflow', 'text-rendering', 'text-size-adjust', // 文本渲染相关属性
      'text-shadow', 'text-transform', 'word-break', 'word-wrap', 'white-space', 'vertical-align', // 文本格式化相关属性
      'list-style', 'list-style-type', 'list-style-position', 'list-style-image', // 列表样式相关属性
      'pointer-events', 'cursor', // 指针事件和光标相关属性
      'background', 'background-attachment', 'background-color', 'background-image', // 背景相关属性
      'background-position', 'background-repeat', 'background-size',
      'border', 'border-collapse', 'border-top', 'border-right', 'border-bottom', 'border-left', // 边框相关属性
      'border-color', 'border-image', 'border-top-color', 'border-right-color', 'border-bottom-color', 'border-left-color',
      'border-spacing', 'border-style', 'border-top-style', 'border-right-style', 'border-bottom-style', 'border-left-style',
      'border-width', 'border-top-width', 'border-right-width', 'border-bottom-width', 'border-left-width',
      'border-radius', 'border-top-right-radius', 'border-bottom-right-radius', 'border-bottom-left-radius', 'border-top-left-radius', // 边框半径相关属性
      'border-radius-topright', 'border-radius-bottomright', 'border-radius-bottomleft', 'border-radius-topleft', // 不常见的边框半径属性命名方式
      'content', 'quotes', 'outline', 'outline-offset', 'opacity', 'filter', 'visibility', 'size', 'zoom', 'transform', // 内容和变换相关属性
      'box-align', 'box-flex', 'box-orient', 'box-pack', 'box-shadow', 'box-sizing', 'table-layout', // 不常见的盒子模型属性
      'animation', 'animation-delay', 'animation-duration', 'animation-iteration-count', 'animation-name', // 动画相关属性
      'animation-play-state', 'animation-timing-function', 'animation-fill-mode',
      'transition', 'transition-delay', 'transition-duration', 'transition-property', 'transition-timing-function', // 过渡效果相关属性
      'background-clip', 'backface-visibility', 'resize', 'appearance', 'user-select', 'interpolation-mode', // 背景剪裁和其他不常见属性
      'direction', 'marks', 'page', 'set-link-source', 'unicode-bidi', 'speak' // 更多不常见的 CSS 属性
    ]
  },
  // 忽略的文件列表，这里忽略所有的 JavaScript 和 TypeScript 文件
  ignoreFiles: ['**/*.js', '**/*.jsx', '**/*.tsx', '**/*.ts'],
  // 覆盖规则，针对特定文件类型应用不同的规则
  overrides: [{
    files: ['*.vue', '**/*.vue', '*.html', '**/*.html'], // 针对 Vue 和 HTML 文件
    extends: ['stylelint-config-recommended', 'stylelint-config-html'], // 扩展推荐的 HTML 相关配置
    rules: {
      'keyframes-name-pattern': null, // 禁用对关键帧名称模式的检查
      'selector-class-pattern': null, // 禁用对类选择器名称模式的检查
      'no-duplicate-selectors': null, // 禁用对重复选择器的检查
      // 忽略未知的伪类选择器，这里忽略 'deep' 和 'global'
      'selector-pseudo-class-no-unknown': [true, {ignorePseudoClasses: ['deep', 'global']}],
      // 忽略未知的伪元素选择器，这里忽略 'v-deep', 'v-global', 'v-slotted'
      'selector-pseudo-element-no-unknown': [true, {ignorePseudoElements: ['v-deep', 'v-global', 'v-slotted']}]
    }
  }]
}
