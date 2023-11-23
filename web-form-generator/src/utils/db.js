const DRAWING_ITEMS = 'drawingItems'
const DRAWING_ITEMS_VERSION = '1.2'
const DRAWING_ITEMS_VERSION_KEY = 'DRAWING_ITEMS_VERSION'
const DRAWING_ID = 'idGlobal'
const TREE_NODE_ID = 'treeNodeId'
const FORM_CONF = 'formConf'
const MY_FORM_TABLE_DATA = 'myFormTableData'

export function getDrawingList() {
  // 加入缓存版本的概念，保证缓存数据与程序匹配
  const version = localStorage.getItem(DRAWING_ITEMS_VERSION_KEY)
  if (version !== DRAWING_ITEMS_VERSION) {
    localStorage.setItem(DRAWING_ITEMS_VERSION_KEY, DRAWING_ITEMS_VERSION)
    saveDrawingList([])
    return null
  }

  const str = localStorage.getItem(DRAWING_ITEMS)
  if (str) return JSON.parse(str)
  return null
}

export function saveDrawingList(list) {
  localStorage.setItem(DRAWING_ITEMS, JSON.stringify(list))
}

export function clearDrawingList() {
  localStorage.removeItem(DRAWING_ITEMS)
}

export function getIdGlobal() {
  const str = localStorage.getItem(DRAWING_ID)
  if (str) return parseInt(str, 10)
  return 100
}

export function saveIdGlobal(id) {
  localStorage.setItem(DRAWING_ID, `${id}`)
}

export function clearIdGlobal() {
  localStorage.removeItem(DRAWING_ID)
}

export function getTreeNodeId() {
  const str = localStorage.getItem(TREE_NODE_ID)
  if (str) return parseInt(str, 10)
  return 100
}

export function saveTreeNodeId(id) {
  localStorage.setItem(TREE_NODE_ID, `${id}`)
}

export function clearTreeNodeId() {
  localStorage.removeItem(TREE_NODE_ID)
}

export function getFormConf() {
  const str = localStorage.getItem(FORM_CONF)
  if (str) return JSON.parse(str)
  return null
}

export function saveFormConf(obj) {
  localStorage.setItem(FORM_CONF, JSON.stringify(obj))
}

export function clearFormConf() {
  localStorage.removeItem(FORM_CONF)
}

export function getMyFormTableData() {
  const str = localStorage.getItem(MY_FORM_TABLE_DATA)
  if (str) return JSON.parse(str)
  return null
}

export function saveMyFormTableData(obj) {
  localStorage.setItem(MY_FORM_TABLE_DATA, JSON.stringify(obj))
}

export function clearMyFormTableData() {
  localStorage.removeItem(MY_FORM_TABLE_DATA)
}
