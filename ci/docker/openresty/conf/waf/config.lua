--规则存放目录
RulePath = "conf/waf/wafconf/"
--是否开启攻击信息记录，需要配置logdir
Attacklog = "on"
--log存储目录，该目录需要用户自己新建，切需要nginx用户的可写权限
Logdir = "logs/hack/"

--是否拦截url访问
UrlDeny="on"
--是否拦截后重定向，如果设置为off,则为学习模式，学习模式只记录匹配日志，不进行实际拦截
Redirect="off"
--是否拦截cookie攻击
CookieMatch="on"
--是否拦截post攻击
PostMatch="on"

--是否开启URL白名单
WhiteModule="on"

--是否启用user-agent白名单，默认不要启用
WhiteUA = "off"

--填写不允许上传文件后缀类型
Black_fileExt={"php","jsp","sh","py","asp","js","html"}

--ip白名单，这些IP放行，多个ip用逗号分隔
IpWhitelist={"127.0.0.1"}

--ip黑名单,黑名单列表中的IP禁止访问，多个ip用逗号分隔,也可以使用网段172.11.34.0/24
IpBlocklist={"1.0.0.1"}

--是否开启拦截cc攻击(需要nginx.conf的http段增加lua_shared_dict limit 10m;)
CCDeny="off"
--设置cc攻击频率，单位为秒.
--默认1分钟同一个IP只能请求同一个地址100次
CCrate="100/60"

--是否启用请求方法白名单
WhiteMethod="on"
--请求头白名单
Methodlist={"GET","POST","OPTIONS","PUT",'DELETE'}
--是否开启libinjection C库监测XSS、SQLI
Openlibinjection="on"

--效率很低禁用，改为使用nginx的deny和allow模块
OpenChinaIps="off"

--================================================
--以下为加强响应头部信息，应对政府教育部门漏洞扫描工具
--防止点击劫持的http头部 `X-Frame-Options` 缺失漏洞
--http头部 `X-Frame-Options` 默认值DENY
--DENY // 拒绝任何域加载
--SAMEORIGIN // 允许同源域下加载
--ALLOW-FROM // 可以定义允许frame加载的页面地址
--想要允许跨域访问时，置空：XFrameOptions=""
XFrameOptions="SAMEORIGIN"
--跨域
--Access-Control-Allow-Origin头部配置，默认为空
--Access-Control-Allow-Origin: https://developer.mozilla.org
--Access-Control-Allow-Origin: *
AccessControlAllowOrigin=""

--解决"Referral Policy" Security 头缺失漏洞
ReferrerPolicy="strict-origin-when-cross-origin"
--解决"Content-Security-Policy"头缺失漏洞，具体参数待测试
--ContentSecurityPolicy="default-src 'self'; script-src 'self'; frame-ancestors 'self'; style-src 'self' 'unsafe-inline' "
ContentSecurityPolicy=""



--=============以下为修改响应内容，开启性能下降15%以上，正则数据越多性能下降越大=================
--是否开启响应内容过滤-启用后性能降低
ResponseBodyFilter="off"
--是否开启敏感信息屏蔽，使用******替换-启用后性能降低
ResponseBodyReplace="off"



Html=[[
  <HTML>
  <HEAD>
      <TITLE>访问禁止</TITLE>
      <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
      <STYLE type=text/css>
          p {
              color: #666;
              FONT-SIZE: 12pt
          }  
          A {
              TEXT-DECORATION: none
          }
      </STYLE>
  </HEAD>
  <BODY topMargin=50>
      <TABLE cellSpacing=0 width=600 align=center border=0 cepadding="0">
          <TR>
              <TD>
                  <p><b>访问禁止</b></p>
                  <HR noShade SIZE=0>
                  <P style="height:9px">☉ 确保浏览器的地址栏中的地址拼写和格式正确无误；</P>
                  <P style="height:9px;">☉ 单击<A href="javascript:history.back(1)">后退</A>返回上一页；</P>
              </TD>
          </TR>
      </TABLE>
  </BODY>
  </HTML>
]]
Html2=[[
  <HTML>
  <HEAD>
      <TITLE>404</TITLE>
      <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
      <STYLE type=text/css>
          p {
              color: #666;
              FONT-SIZE: 16pt
          }  
          A {
              TEXT-DECORATION: none
          }
      </STYLE>
  </HEAD>
  <BODY topMargin=50>
      <TABLE cellSpacing=0 width=600 align=center border=0 cepadding="0">
          <TR>
              <TD>
                  <p><b>访问页面不存在！单击<A href="javascript:history.back(1)">后退</A>返回上一页</b></p>
              </TD>
          </TR>
      </TABLE>
  </BODY>
  </HTML>
]]
