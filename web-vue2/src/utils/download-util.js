import axios from 'axios'
import {Loading, Message} from 'element-ui'
import {saveAs} from 'file-saver'
import {getToken} from '@/utils/auth'
import errorCode from '@/utils/errorCode'
import {blobValidate} from '@/utils/tools';

const baseURL = process.env.VUE_APP_BASE_API

export default {
  zip(url, name) {
    let downloadLoadingInstance = Loading.service({
      text: '正在下载数据，请稍候',
      spinner: 'el-icon-loading',
      background: 'rgba(255, 255, 255, 0.7)'
    })
    axios({
      method: 'get',
      url: baseURL + url,
      responseType: 'blob',
      headers: {'UserJwtToken': getToken()}
    }).then((res) => {
      const isBlob = blobValidate(res.data);
      if (isBlob) {
        const blob = new Blob([res.data], {type: 'application/zip'})
        this.saveAs(blob, name)
        downloadLoadingInstance.close();
      } else {
        this.printErrMsg(res.data);
        downloadLoadingInstance.close();
      }
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
    axios({
      method: 'get',
      url: baseURL + url,
      params,
      headers: {'UserJwtToken': getToken()}
    }).then(async (res) => {
      if (res.data.code === '200') {
        saveAs(baseURL + res.data.data, filename);
        downloadLoadingInstance.close();
      } else {
        Message.error(res.data.message)
      }
    }).catch((r) => {
      console.error(r)
      Message.error('下载文件出现错误，请重试！')
      downloadLoadingInstance.close();
    })
  },
  saveAs(text, name, opts) {
    saveAs(text, name, opts);
  },
  async printErrMsg(data) {
    const resText = await data.text();
    const rspObj = JSON.parse(resText);
    const errMsg = errorCode[rspObj.code] || rspObj.msg || errorCode['default']
    Message.error(errMsg);
  },
}
