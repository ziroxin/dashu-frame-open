import * as monaco from 'monaco-editor'

// 编辑器实体
let editor

// 获取实例
export const getEditor = () => { return editor }

// 初始化编辑器
export const createEditor = (editorRef, options: monaco.editor.IStandaloneEditorConstructionOptions = {}) => {
  if (!editorRef.value) return
  editor = monaco.editor.create(editorRef.value, {
    // 初始模型
    // model: monaco.editor.createModel('', language),
    // 是否启用预览图
    minimap: {enabled: true},
    // 圆角
    roundedSelection: true,
    // 主题
    theme: 'vs-dark',
    // 主键
    multiCursorModifier: 'ctrlCmd',
    // 滚动条
    scrollbar: {verticalScrollbarSize: 8, horizontalScrollbarSize: 8},
    // 行号
    lineNumbers: 'on',
    // tab大小
    tabSize: 2,
    //字体大小
    fontSize: 14,
    // 控制编辑器在用户键入、粘贴、移动或缩进行时是否应自动调整缩进
    autoIndent: 'advanced',
    // 自动布局
    automaticLayout: true,
    // 更多配置
    ...options
  })
  return editor
}

// 数据更新
export const updateEditorVal = (val: string) => {
  nextTick(() => {
    if (editor?.getOption(monaco.editor.EditorOption.readOnly)) {
      editor?.updateOptions({readOnly: false})
    }
    editor?.setValue(val)
    setTimeout(async () => {
      await editor?.getAction('editor.action.formatDocument')?.run()
    }, 10)
  })
}

// 切换主题
export const changeTheme = (newTheme: string) => {
  monaco.editor.setTheme(newTheme)
}