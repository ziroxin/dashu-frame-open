import axios from 'axios'
import { Message, Loading } from 'element-ui'
import { saveAs } from 'file-saver'
import { getToken } from '@/utils/auth'
import errorCode from '@/utils/errorCode'
import { blobValidate, tansParams } from '@/utils/tools';

const baseURL = process.env.VUE_APP_BASE_API

export default {
  zip(url, name) {
    let downloadLoadingInstance
    downloadLoadingInstance = Loading.service({ text: '正在下载数据，请稍候', spinner: 'el-icon-loading', background: 'rgba(0, 0, 0, 0.7)' })
    url = baseURL + url
    axios({
      method: 'get',
      url: url,
      responseType: 'blob',
      headers: { 'UserJwtToken': getToken() }
    }).then((res) => {
      const isBlob = blobValidate(res.data);
      if (isBlob) {
        const blob = new Blob([res.data], { type: 'application/zip' })
        this.saveAs(blob, name)
        downloadLoadingInstance.close();
      } else {
        this.printErrMsg(res.data);
        downloadLoadingInstance.close();
      }
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
  downLoad(url, params, filename) {
    let downloadLoadingInstance
    downloadLoadingInstance = Loading.service({ text: '正在下载数据，请稍候', spinner: 'el-icon-loading', background: 'rgba(255, 255, 255, 0.7)' })
    axios(
      {
        method: 'get',
        url: baseURL + url,
        data:params,
        headers: { 'UserJwtToken': getToken() }
      }
    ).then(async (res) => {
      if (res.data.code === '200') {
        let urlAs = baseURL + res.data.data
        console.log(urlAs)
        saveAs(urlAs, filename);
        downloadLoadingInstance.close();
      } else {
        Message.error(res.data.message)
      }
    }).catch((r) => {
      console.error(r)
      Message.error('下载文件出现错误，请联系管理员！')
      downloadLoadingInstance.close();
    })
  }
}
