## waf

### 用途：
    	
- 防止sql注入，本地包含，部分溢出，fuzzing测试，xss,SSRF等web攻击
- SQLI和XSS同时使用libinjection C解析库，相比传统正则匹配识别SQL注入在于速度快以及低误报，低漏报 
- 防止svn/备份之类文件泄漏
- 同时对上传文件内容进行解析(仅对普通明文文件进行解析)
- 防止ApacheBench之类压力测试工具的攻击
- 屏蔽常见的扫描黑客工具，扫描器
- 屏蔽异常的网络请求
- 屏蔽图片附件类目录php执行权限
- 防止webshell上传



### 前提条件



### 使用说明：

nginx安装路径假设为:/usr/local/openresty/nginx/conf/

把waf下载到conf目录下,解压命名为waf

在nginx.conf的http段添加

```
include waf/waf.conf;

```

如果单独server或者location使用，access_by_lua_file放到对应公的位置

```
     access_by_lua_file conf/waf/waf_access.lua;      
```
> access_by_lua_file的context: http, server, location, location if

配置config.lua里的waf规则目录(一般在waf/conf/目录下)

     RulePath = "/usr/local/openresty/nginx/conf/waf/wafconf/"

绝对路径如有变动，需对应修改

然后重启nginx即可

### 性能测试

未启用waf
```
[root@ywgl waf]# wrk -c 100 -t 10 -d 10s http://172.19.35.64
Running 10s test @ http://172.19.35.64
  10 threads and 100 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    23.53ms   69.63ms 924.88ms   96.61%
    Req/Sec   807.19    176.52     1.87k    80.57%
  78670 requests in 10.01s, 50.11MB read
Requests/sec:   7859.25
Transfer/sec:      5.01MB
```
启用waf
```
[root@ywgl waf]# wrk -c 100 -t 10 -d 10s http://172.19.35.64
Running 10s test @ http://172.19.35.64
  10 threads and 100 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    38.19ms  102.05ms   1.22s    95.57%
    Req/Sec   527.14    145.66     4.04k    92.85%
  50703 requests in 10.10s, 32.30MB read
Requests/sec:   5020.54
Transfer/sec:      3.20MB
```

### 配置文件详细说明：

      --规则存放目录
      RulePath = "conf/waf/wafconf/"
      --是否开启攻击信息记录，需要配置logdir
      Attacklog = "on"
      --log存储目录，该目录需要用户自己新建，切需要nginx用户的可写权限
      Logdir = "logs/hack/"

      --是否拦截url访问
      UrlDeny="on"
      --是否拦截后重定向，如果设置为off,则为学习模式，学习模式只记录匹配日志，不进行实际拦截
      Redirect="on"
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

      --ip白名单，多个ip用逗号分隔
      IpWhitelist={"127.0.0.1"}

      --ip黑名单，多个ip用逗号分隔,也可以使用网段172.11.34.0/24
      IpBlocklist={"1.0.0.1"}

      --是否开启拦截cc攻击(需要nginx.conf的http段增加lua_shared_dict limit 10m;)
      CCDeny="off"
      --设置cc攻击频率，单位为秒.
      --默认1分钟同一个IP只能请求同一个地址100次
      CCrate="100/60"

      --是否启用请求方法白名单
      WhiteMethod="on"
      --请求头白名单
      Methodlist={"GET","POST","OPTIONS","PUT"}
      --是否开启libinjection C库监测XSS、SQLI
      Openlibinjection="on"
        
### 检查规则是否生效

部署完毕可以尝试如下命令：        
  
        curl http://xxxx?id=../etc/passwd
        返回403状态码及文字提示，说明规则生效。

注意:默认，本机在白名单不过滤，可自行调整config.lua配置


### 效果图如下：

<img src="https://gitee.com/llxgit/imgs/raw/master/md/16691727496437.png" width=100% />






### 一些说明：

	过滤规则在wafconf下，可根据需求自行调整，每条规则需换行,或者用|分割
	
		args里面的规则get参数进行过滤的
		url是只在get请求url过滤的规则		
		post是只在post请求过滤的规则		
		whitelist是白名单，里面的url匹配到不做过滤		
		user-agent是对user-agent的过滤规则
	

	默认开启了get和post过滤，需要开启cookie过滤的，编辑waf.lua取消部分--注释即可
	
	日志文件名称格式如下:虚拟主机名_sec.log

