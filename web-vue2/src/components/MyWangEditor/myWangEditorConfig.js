/**
 * 图片上传配置
 * @param server 图片上传地址
 * @param limit 图片上传大小限制
 * @param Message 弹窗
 */
export function imagesDefaultOptions(server, limit, Message) {
  return {
    server: server,
    // 单个文件的最大体积限制，默认为 2M
    maxFileSize: limit,
    // 小于该值就插入 base64 格式（而不上传），默认为 0
    base64LimitSize: 20 * 1024, // 20kb
    // 单个文件上传失败
    onFailed(file, res) {
      console.log('onFailed', file, res)
      this.$message.error(res.message)
    },
    // 上传错误，或者触发 timeout 超时
    onError(file, err, res) {
      if (err.message.indexOf('exceeds maximum allowed size of') >= 0) {
        let errStr = `${file.name} 上传出错！文件大小超出限制`
        let errArr = err.message.split('exceeds maximum allowed size of')
        if (errArr.length === 2) {
          errStr += '，最大上传' + errArr[1];
        }
        Message.error(errStr)
      }
      console.log(`${file.name} 上传出错`, err, res)
    }
  }
}

export function videosDefaultOptions(server, limit, Message) {
  return {
    server: server,
    // 单个文件的最大体积限制，默认为 50M
    maxFileSize: limit,
    // 单个文件上传失败
    onFailed(file, res) {
      console.log('onFailed', file, res)
      this.$message.error(res.message)
    },
    // 上传错误，或者触发 timeout 超时
    onError(file, err, res) {
      if (err.message.indexOf('exceeds maximum allowed size of') >= 0) {
        let errStr = `${file.name} 上传出错！文件大小超出限制`
        let errArr = err.message.split('exceeds maximum allowed size of')
        if (errArr.length === 2) {
          errStr += '，最大上传' + errArr[1];
        }
        Message.error(errStr)
      }
      console.log(`${file.name} 上传出错`, err, res)
    }
  }
}

// 自定义转换视频地址
export function customParseVideoSrc(src) {
  if (src) {
    // todo 可作其他处理，比如外部视频链接处理
    // 视频文件地址
    return process.env.VUE_APP_BASE_API + src
  }
  return src
}

// 自定义转换图片地址
export function customParseImageSrc(src) {
  if (src) {
    return process.env.VUE_APP_BASE_API + src
  }
  return src
}
