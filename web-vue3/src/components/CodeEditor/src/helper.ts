import * as monaco from 'monaco-editor'
import editorWorker from 'monaco-editor/esm/vs/editor/editor.worker?worker'
import jsonWorker from 'monaco-editor/esm/vs/language/json/json.worker?worker'
import cssWorker from 'monaco-editor/esm/vs/language/css/css.worker?worker'
import htmlWorker from 'monaco-editor/esm/vs/language/html/html.worker?worker'
import tsWorker from 'monaco-editor/esm/vs/language/typescript/ts.worker?worker'

self.MonacoEnvironment = {
  getWorker(_, label) {
    if (label === 'json') {
      return new jsonWorker()
    } else if (label === 'css' || label === 'scss' || label === 'less') {
      return new cssWorker()
    } else if (label === 'html' || label === 'handlebars' || label === 'razor') {
      return new htmlWorker()
    } else if (label === 'typescript' || label === 'javascript') {
      return new tsWorker()
    } else {
      return new editorWorker()
    }
  }
}

// 初始化编辑器
export const createEditor = (editorRef: any, language: string = 'html') => {
  if (!editorRef.value) return undefined
  const editor = monaco.editor.create(editorRef.value, {
    // 初始模型
    model: monaco.editor.createModel('', language),
    // 是否启用预览图
    minimap: {enabled: true},
    // 圆角
    roundedSelection: true,
    // 主题
    theme: 'vs',
    // 主键
    multiCursorModifier: 'ctrlCmd',
    // 滚动条
    scrollbar: {verticalScrollbarSize: 8, horizontalScrollbarSize: 8},
    // 行号
    lineNumbers: 'on',
    // 只读
    readOnly: true,
    // tab大小
    tabSize: 2,
    //字体大小
    fontSize: 14,
    // 控制编辑器在用户键入、粘贴、移动或缩进行时是否应自动调整缩进
    autoIndent: 'advanced',
    // 自动布局
    automaticLayout: true
  })
  return editor
}

// 数据更新
export const updateEditorVal = (editor: any, val: string) => {
  nextTick(() => {
    let isReadOnly = editor?.getOption(monaco.editor.EditorOption.readOnly)
    if (isReadOnly) {
      editor?.updateOptions({readOnly: false})
    }
    editor?.setValue(val)
    setTimeout(async () => {
      await editor?.getAction('editor.action.formatDocument')?.run()
      if (isReadOnly) {
        editor?.updateOptions({readOnly: true})
      }
    }, 10)
  })
}

// 切换主题
export const changeTheme = (newTheme: string) => {
  monaco.editor.setTheme(newTheme)
}