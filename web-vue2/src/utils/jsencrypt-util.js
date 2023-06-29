// 导入 jsencrypt 库
import JSEncrypt from 'jsencrypt';

// 公钥
const publicKey = 'MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCdSR523juSOUKRfyCwxLMnueQ5HbFH3ORgSzsIFIuz40lUIeZqRPjNkFFBbV159XQfXGs0pfoX68Y+ylTdWaCCeHHxNjx+qVkAE9tMyosxDXSUj+Yz8ruZA920u0ne2VSVZd41AtrlLaM8DaFVOfC7dngFx12rosfSlxrx0yPU8wIDAQAB'

// 公钥加密方法（Java后端私钥解密）
export function encryptRSA(data) {
  const encrypt = new JSEncrypt();
  encrypt.setPublicKey(publicKey);
  const encryptedData = encrypt.encrypt(data);
  return encryptedData;
}

// 公钥解密方法（Java后端私钥加密）
export function decryptRSA(encryptedData) {
  const decrypt = new JSEncrypt();
  decrypt.setPublicKey(publicKey);
  const decryptedData = decrypt.decrypt(encryptedData);
  return decryptedData;
}

