// 公钥
const publicKey = 'MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCdSR523juSOUKRfyCwxLMnueQ5HbFH3ORgSzsIFIuz40lUIeZqRPjNkFFBbV159XQfXGs0pfoX68Y+ylTdWaCCeHHxNjx+qVkAE9tMyosxDXSUj+Yz8ruZA920u0ne2VSVZd41AtrlLaM8DaFVOfC7dngFx12rosfSlxrx0yPU8wIDAQAB'

// 公钥加密方法（Java后端私钥解密）
function encryptRSA(data) {
  const encrypt = new JSEncrypt();
  encrypt.setPublicKey(publicKey);
  const encryptedData = encrypt.encrypt(data);
  return encryptedData;
}

// 公钥解密方法（Java后端私钥加密）
function decryptRSA(encryptedData) {
  const decrypt = new JSEncrypt();
  decrypt.setPublicKey(publicKey);
  const decryptedData = decrypt.decrypt(encryptedData);
  return decryptedData;
}

function setCookieToken(data) {
  // 设置 cookie 的有效期为 token 的有效期 - 10分钟（用于提前刷新token，防止token过期）
  var expirationDate = new Date(data.accessTokenValidTime);
  expirationDate.setMinutes(expirationDate.getMinutes() - 10);
  $.cookie('UserJwtToken', data.accessToken, {expires: expirationDate});
}

function checkCookieToken() {
  $("#loginPanel").hide()
  // 获取cookie中的token，如果有数据，直接跳转
  let value = $.cookie('UserJwtToken');
  if (value) {
    window.location.href = window.location.href + '&UserJwtToken=' + value;
  } else {
    $("#loginPanel").show()
  }
}

function getCookieForConfirmAccess() {
  let data = {token: $.cookie('UserJwtToken')};
  return data;
}