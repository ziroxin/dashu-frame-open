/**
 * 使用第三方组件 vue-data-dict
 * @see https://github.com/moxun1639/vue-data-dict
 * 使用方法：
 *    1.在script中引入（和data平级）：
 *      dicts: ['key1']
 *    2.作为变量使用（dict.dict.key1）：
 *      <el-option v-for="item in dict.dict.key1" :key="item.value"
 *                    :value="item.value" :label="item.label"></el-option>
 *    注意：【dict.dict.】 是固定写法
 *         【key1】 是字典类型的typeCode
 *         【value】 是字典数据值
 *         【label】 是字典数据标签
 */
import Vue from 'vue'
import VueDataDict from 'vue-data-dict'
import store from '@/store'
import {getDictData} from "@/api/dicts"

Vue.use(VueDataDict, {
  metas: {
    '*': {
      labelField: 'dictLabel',
      valueField: 'dictValue',
      request(dictMeta) {
        // 从store中查询字典是否存在
        const dictData = getDictDataByKey(store.getters.dict, dictMeta.type)
        if (dictData) {
          // 存在,直接返回
          return new Promise(resolve => {
            resolve(dictData)
          })
        } else {
          // 不存在,请求字典接口
          return new Promise((resolve, reject) => {
            getDictData(dictMeta.type).then(res => {
              // 存入store
              store.dispatch('dict/setDict', {key: dictMeta.type, value: res.data})
              resolve(res.data)
            }).catch(error => {
              reject(error)
            })
          })
        }
      },
      responseConverter(response, dictMeta) {
        // 格式化数据
        const dicts = response
        if (dicts === undefined) {
          return []
        }
        return dicts.map(d => VueDataDict.DictConverter(d, dictMeta))
      }
    }
  }
})

// 查询字典数据
function getDictDataByKey(dict, key) {
  if (key === null || key === "") {
    return null
  }
  try {
    for (let i = 0; i < dict.length; i++) {
      if (dict[i].key === key) {
        return dict[i].value
      }
    }
  } catch (e) {
    return null
  }
}
