import axios from 'axios'
import {Loading, Message} from 'element-ui'
import {saveAs} from 'file-saver'
import {getToken} from '@/utils/auth'
import errorCode from '@/utils/error-code'
import request from '@/utils/request'

const baseURL = process.env.VUE_APP_BASE_API

export default {
  zip(url, name) {
    let downloadLoadingInstance = Loading.service({
      text: '正在下载数据，请稍候',
      spinner: 'el-icon-loading',
      background: 'rgba(255, 255, 255, 0.7)'
    })
    // 下载blob文件，需要单独处理axios请求，不用封装好的request
    axios({url: baseURL + url, method: 'get', responseType: 'blob', headers: {'UserJwtToken': getToken()}})
      .then((res) => {
        if (res.data.type !== 'application/json') {
          // 下载文件
          this.saveAs(new Blob([res.data], {type: 'application/zip'}), name)
        } else {
          const resObj = JSON.parse(res.data.text());
          Message.error(errorCode[resObj.code] || resObj.msg || errorCode['default'])
        }
        downloadLoadingInstance.close()
      })
  },
  /**
   * 下载文件（通过文件链接下载，并重命名下载文件名）
   * @param url 获取下载链接的api
   * @param params 参数
   * @param filename 下载文件名
   */
  download(url, params, filename) {
    let downloadLoadingInstance = Loading.service({
      text: '正在下载数据，请稍候',
      spinner: 'el-icon-loading',
      background: 'rgba(255, 255, 255, 0.7)'
    })
    // 获取下载链接
    request({url: url, method: 'get', params}).then(async (res) => {
      saveAs(baseURL + res.data, filename);
      downloadLoadingInstance.close()
    }).catch((r) => {
      console.error(r)
      downloadLoadingInstance.close()
    })
  },
  /**
   * 下载文件（通过文件链接下载，并重命名下载文件名）
   * @param text 文件url/blob
   * @param name 重命名文件名（下载文件名）
   * @param opts 其他参数
   */
  saveAs(text, name, opts) {
    saveAs(text, name, opts);
  }
}
