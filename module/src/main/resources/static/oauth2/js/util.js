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