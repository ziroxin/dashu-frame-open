-- 开启检测 NYI
-- local v = require "jit.v"
-- v.on("/usr/local/openresty/nginx/logs/jit.log")
package.cpath = package.cpath .. ";conf/waf/lib/?.so"
package.path = package.path .. ";conf/waf/lib/?.lua"

require 'config'

local optionIsOn = function(options) return options == "on" and true or false end
UrlDeny = optionIsOn(UrlDeny)
PostCheck = optionIsOn(PostMatch)
CookieCheck = optionIsOn(CookieMatch)
WhiteCheck = optionIsOn(WhiteModule)
PathInfoFix = optionIsOn(PathInfoFix)
Attacklog = optionIsOn(Attacklog)
CCDeny = optionIsOn(CCDeny)
Redirect = optionIsOn(Redirect)
WhiteMethod = optionIsOn(WhiteMethod)
WhiteUA = optionIsOn(WhiteUA)
Openlibinjection = optionIsOn(Openlibinjection)
Logpath = Logdir
ResponseBodyFilter = optionIsOn(ResponseBodyFilter)
ResponseBodyReplace = optionIsOn(ResponseBodyReplace)
if OpenChinaIps == nil then
    OpenChinaIps="off"    
end
OpenChinaIps = optionIsOn(OpenChinaIps)
local rulepath = RulePath
Html = Html


--自动创建日志目录
local result,status,code=os.execute("mkdir "..Logpath)
-- if result then
--     ngx.log(ngx.INFO,"自动创建日志目录"..Logpath)
-- end


local function init_chinaip()
    local t = {}
    local file = io.open(rulepath .. '/' .. "chinaIP.data", "r")
    if file == nil then
        return t
    end
    for line in file:lines() do
        table.insert(t, line)
    end
    file:close()      
    -- for index, value in ipairs(t) do
    --     print(value)
    -- end    
    return t
end
------------------------------------规则读取函数-------------------------------------------------------------------
-- local function read_rule(var)
--     local file = io.open(rulepath .. '/' .. var, "r")
--     if file == nil then
--         return
--     end
--     t = {}
--     for line in file:lines() do
--         table.insert(t, line)
--     end
--     file:close()
--     return (t)
-- end
--modify by lkh @20221116
local function read_rule(...)
    local t = {}
    for index, value in ipairs({...}) do
        local file = io.open(rulepath .. '/' .. value, "r")
        if file == nil then
            return t
        end
        for line in file:lines() do
            table.insert(t, line)
        end
        file:close()            
    end
    return (t)
end

Urlrules = read_rule('url')
Argsrules = read_rule('args','code.data','xss.data','sqli.data')
Uarules = read_rule('user-agent')
Wturlrules = read_rule('whiteurl')
Postrules = read_rule('post','code.data','xss.data','sqli.data')
Ckrules = read_rule('cookie','code.data','xss.data','sqli.data')
White_uasrules = read_rule('white_uas')
Code_leakages_rules = read_rule('code-leakages.data')
Replace_rules = read_rule('replace.data')
ChinaIPs = init_chinaip()
