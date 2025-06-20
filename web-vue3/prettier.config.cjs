module.exports = {
  printWidth: 100, // 设置每行代码的最大字符数为100
  tabWidth: 2, // 设置每个缩进级别的空格数为2
  useTabs: false, // 使用空格而不是制表符进行缩进
  semi: false, // 不在语句末尾添加分号
  vueIndentScriptAndStyle: false, // 在Vue文件中不缩进<script>和<style>标签的内容
  singleQuote: true, // 使用单引号代替双引号
  quoteProps: 'as-needed', // 只有在需要时才为对象的属性加引号
  trailingComma: 'none', // 不在多行代码的末尾添加逗号
  jsxSingleQuote: false, // 在JSX中使用双引号而不是单引号
  arrowParens: 'always', // 在箭头函数参数周围总是使用括号
  insertPragma: false, // 不在文件开头插入@format注释
  requirePragma: false, // 不要求文件开头必须有@format注释才能格式化
  proseWrap: 'never', // 不要对Markdown中的换行符进行格式化
  htmlWhitespaceSensitivity: 'strict', // 严格模式下，对HTML中的空白字符敏感
  endOfLine: 'auto', // 根据文件系统自动检测行尾结束符
  rangeStart: 0 // 格式化代码的起始位置
}
