import JSEncrypt from 'jsencrypt';

// 配置（要和后台配合加密和解密，下面的配置就必须和后台保持对应一致）
const config = {
  // 公钥（必须和Java后台配置的私钥相匹配）
  publicKey: 'MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCdSR523juSOUKRfyCwxLMnueQ5HbFH3ORgSzsIFIuz40lUIeZqRPjNkFFBbV159XQfXGs0pfoX68Y+ylTdWaCCeHHxNjx+qVkAE9tMyosxDXSUj+Yz8ruZA920u0ne2VSVZd41AtrlLaM8DaFVOfC7dngFx12rosfSlxrx0yPU8wIDAQAB',
  // 分隔符（必须和后台配置的分隔符相匹配）
  separator: '@dashu@',
  // 最大分块大小
  maxChunkSize: 117,
  // 是否全局加密
  isEncrypt: true,
  // 不加密白名单（例如：验证码接口）
  whiteList: ['/captcha/get']
}


/**
 * 导出全局配置：是否加密传输
 */
export function isEncrypt(apiUrl) {
  // 全局加密传输
  if (config.isEncrypt) {
    // 判断加密白名单，包含在白名单内的接口不加密
    return !config.whiteList.includes(apiUrl);
  }
  // 不加密
  return false;
}


/**
 * 公钥加密方法（Java后端私钥解密）
 * @param data {string} 待加密数据
 * @return 加密后的数据
 */
export function encryptRSA(data) {
  const encrypt = new JSEncrypt();
  encrypt.setPublicKey(config.publicKey);
  let chunks = [];// 分块加密数组
  for (let i = 0; i < data.length; i += config.maxChunkSize) {
    const chunk = data.slice(i, i + config.maxChunkSize);
    chunks.push(encrypt.encrypt(chunk));
  }
  // 将加密后的块用某种分隔符连接起来
  return chunks.join(config.separator);
}
