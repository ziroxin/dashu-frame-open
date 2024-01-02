local libinjection = require('libinjection')

local _M={}

--local ipmatcher = require("resty.ipmatcher")
local ipmatcher = require("ipmatcher")
local upload = require("upload")
local cjson = require "cjson"

local match = string.match
local ngxmatch = ngx.re.match
local unescape = ngx.unescape_uri
local get_headers = ngx.req.get_headers


_M.PostCheck = PostCheck

local function getClientIp()
    local IP = ngx.var.remote_addr
    if IP == nil then
        IP = "unknown"
    end
    return IP
end

local function write(logfile, msg)
    local fd = io.open(logfile, "ab")
    if fd == nil then return end
    fd:write(msg)
    fd:flush()
    fd:close()
end

local function log(method, url, data, ruletag)
    if Attacklog then
        local realIp = getClientIp()
        local xforwardedfor=ngx.req.get_headers()["x_forwarded_for"]
        if xforwardedfor == nil then
            xforwardedfor = "-"
        end
        local ua = ngx.var.http_user_agent
        --local servername = ngx.var.server_name
        local servername = ngx.req.get_headers()["host"]
        local time = ngx.localtime()
        local line
        local content_type = get_headers()["Content-Type"]
        if content_type == nil then
            content_type = "-"
        end
        
        if ua then
            line = realIp .. " " .. xforwardedfor ..
                " [" .. time ..
                "] \"" .. method .. " " .. servername .. url .. "\" \"" .. content_type .. "\" \"" .. data .. "\"  \"" ..
                ua .. "\" \"" .. ruletag .. "\"\n"
        else
            line = realIp .. " " .. xforwardedfor .. " [" ..
                time .. "] \"" .. method .. " " .. servername .. url .. "\" \"" .. content_type .. "\" \"".. data .. "\" - \"" .. ruletag .. "\"\n"
        end
        local filename = Logpath .. '/' .. servername .. "_" .. ngx.today() .. "_sec.log"
        write(filename, line)
    end
end


local function say_html()
    if Redirect then
        ngx.header.content_type = "text/html"
        ngx.status = ngx.HTTP_FORBIDDEN
        ngx.say(Html)
        ngx.exit(ngx.status)
    end
end
local function say_html2()
    if Redirect then
        ngx.header.content_type = "text/html"
        ngx.status = ngx.HTTP_FORBIDDEN
        ngx.say(Html2)
        ngx.exit(ngx.status)
    end
end
function _M.whiteurl()
    if WhiteCheck then
        if Wturlrules ~= nil then
            for _, rule in ipairs(Wturlrules) do
                if ngxmatch(ngx.var.uri, rule, "isjo") then
                    return true
                end
            end
        end
    end
    return false
end

local function fileExtCheck(ext)
--    local items = Set(black_fileExt)
    ext = string.lower(ext)
    if ext then
        for _, rule in ipairs(Black_fileExt) do
            if ngx.re.match(ext, rule, "isjo") then
                log('POST', ngx.var.request_uri, "-", "file attack with ext " .. ext)
                say_html()
            end
        end
    end
    return false
end

function _M.Set(list)
    local set = {}
    for _, l in ipairs(list) do set[l] = true end
    return set
end
--libinjection.sqli(data)



function _M.args()    
    local args = ngx.req.get_uri_args()
    local data

--add by lkh @20221119 
--添加libinjection库检测xss和sqli
--https://github.com/libinjection/libinjection
    if Openlibinjection then
        for key, val in pairs(args) do
            if type(val) == 'table' then
                local t = {}
                for k, v in pairs(val) do
                    if v == true then
                        v = ""
                    end
                    table.insert(t, v)
                end
                data = table.concat(t, " ")
            else
                data = val
            end
            if data and type(data) ~= "boolean" then
                if libinjection.sqli(unescape(data)) then
                    log('GET', ngx.var.request_uri, "-", "匹配libinjection-sqli库")
                    say_html()
                    return true                    
                end
                if libinjection.xss(unescape(data)) then
                    log('GET', ngx.var.request_uri, "-", "匹配libinjection-xss库")
                    say_html()
                    return true                    
                end
            end
        end        
    end


    for _, rule in ipairs(Argsrules) do
        for key, val in pairs(args) do
            if type(val) == 'table' then
                local t = {}
                for k, v in pairs(val) do
                    if v == true then
                        v = ""
                    end
                    table.insert(t, v)
                end
                data = table.concat(t, " ")
            else
                data = val
            end

            
            if data and type(data) ~= "boolean" and rule ~= "" and ngxmatch(unescape(data), rule, "isjo") then
                log('GET', ngx.var.request_uri, "-", rule)
                say_html()
                return true
            end
        end
    end
    return false
end

function _M.url()
    if UrlDeny then
        for _, rule in ipairs(Urlrules) do
            if rule ~= "" and ngxmatch(ngx.var.request_uri, rule, "isjo") then
                log('GET', ngx.var.request_uri, "-", rule)
                say_html()
                return true
            end
        end                
    end
    return false
end

function _M.ua()
    if WhiteUA == false  then
        local ua = ngx.var.http_user_agent
        if ua ~= nil then
            for _, rule in ipairs(Uarules) do
                if rule ~= "" and ngxmatch(ua, rule, "isjo") then
                    log('UA', ngx.var.request_uri, "-", rule)
                    say_html()
                    return true
                end
            end
        end        
    end
    return false
end

local function body(data)
    if Openlibinjection then
        if data ~= "" then
            if libinjection.sqli(unescape(data)) then
                log('POST', ngx.var.request_uri, "-", data.."匹配libinjection-sqli库")
                say_html()
                return true                    
            end
            if libinjection.xss(unescape(data)) then
                log('POST', ngx.var.request_uri, "-", data.."匹配libinjection-xss库")
                say_html()
                return true                    
            end
        end            
    end
    
    for _, rule in ipairs(Postrules) do
        if rule ~= "" and data ~= "" and ngxmatch(unescape(data), rule, "isjo") then
            log('POST', ngx.var.request_uri, data, rule)
            say_html()
            return true
        end
    end
    return false
end

function _M.cookie()
    local ck = ngx.var.http_cookie
    if CookieCheck and ck then
        for _, rule in ipairs(Ckrules) do
            if rule ~= "" and ngxmatch(ck, rule, "isjo") then
                log('Cookie', ngx.var.request_uri, ck, rule)
                say_html()
                return true
            end
        end
    end
    return false
end

function _M.denycc()
    if CCDeny then
        local uri = ngx.var.uri
        CCcount = tonumber(string.match(CCrate, '(.*)/'))
        CCseconds = tonumber(string.match(CCrate, '/(.*)'))
        local token = getClientIp() .. uri
        local limit = ngx.shared.limit
        local req, _ = limit:get(token)
        if req then
            if req > CCcount then
                log('Denycc', uri, "-", "denycc")
                ngx.exit(ngx.HTTP_FORBIDDEN)
                return true
            else
                limit:incr(token, 1)
            end
        else
            limit:set(token, 1, CCseconds)
        end
    end
    return false
end

local function get_boundary()
    local header = get_headers()["content-type"]
    if not header then
        return nil
    end

    if type(header) == "table" then
        header = header[1]
    end

    local m = match(header, ";%s*boundary=\"([^\"]+)\"")
    if m then
        return m
    end

    return match(header, ";%s*boundary=([^\",;]+)")
end

function _M.whiteip()
    --modify by lkh @20221105
    --使用lua-resty-ipmatcher来匹配IP,效率更高，同时支持IP网段配置
    if next(IpWhitelist) ~= nil then
        local ip = ipmatcher.new(IpWhitelist)
        if ip ~=nil and  ip:match(getClientIp()) == true then
            return true
        end
    end
    return false

    -- if next(ipWhitelist) ~= nil then
    --     for _, ip in pairs(ipWhitelist) do
    --         if getClientIp() == ip then
    --             return true
    --         end
    --     end
    -- end
    -- return false
end

function _M.blockip()
    --modify by lkh @20221105
    --使用lua-resty-ipmatcher来匹配IP,效率更高，同时支持IP网段配置
    if next(IpBlocklist) ~= nil then
        local ip = ipmatcher.new(IpBlocklist)
        if ip ~= nil and  ip:match(getClientIp()) == true then
            ngx.exit(444)
            return true
        end
    end
    return false
    --  if next(ipBlocklist) ~= nil then
    --      for _,ip in pairs(ipBlocklist) do
    --          if getClientIp()==ip then
    --              ngx.exit(444)
    --              return true
    --          end
    --      end
    --  end
    --      return false
end

function _M.deny_ips()
    if OpenChinaIps then
        if ChinaIPs ~= nil then
            local ip = ipmatcher.new(ChinaIPs)
            if ip ~= nil and  ip:match(getClientIp()) == false then
                ngx.exit(444)
            end
        end            
    end
    return false
end

--add by lkh @20221105
--白名单模式匹配
function _M.white_ua()
    if WhiteUA then
        local ua = ngx.var.http_user_agent
        if ua ~= nil then
            local white_ua_flag
            for _, rule in ipairs(White_uasrules) do
                if ngxmatch(ua, rule, "isjo") then
                    white_ua_flag = "1"
                    break
                end
            end
            if white_ua_flag ~= "1" then
                log('WHITE_UA', ngx.var.request_uri, "-", "not match white_uas")
                say_html()
                return true
            end
    
        else
            log('WHITE_UA', ngx.var.request_uri, "-", "not match white_uas")
            say_html()
            return true
        end            
    end
    return false
end

--add by lkh@20221111
--请求方法白名单
function _M.white_method()
    if WhiteMethod then
        local method = ngx.req.get_method()
        for _, value in ipairs(Methodlist) do
            if ngxmatch(method,value,"isjo") then
                return false
            end
        end
        say_html()
        return true

    end
    return false
end


function _M.post_check()
    local method = ngx.req.get_method()
    if method == "POST" then
        local boundary = get_boundary()
        if boundary then
            local len = string.len
            local sock, err = ngx.req.socket()
            if not sock then
                return
            end
            ngx.req.init_body(128 * 1024)
            sock:settimeout(0)
            local content_length = nil
            content_length = tonumber(ngx.req.get_headers()['content-length'])
            local chunk_size = 4096
            if content_length < chunk_size then
                chunk_size = content_length
            end
            local size = 0
            while size < content_length do
                local data, err, partial = sock:receive(chunk_size)
                data = data or partial
                if not data then
                    return
                end
                ngx.req.append_body(data)
                if body(data) then
                    return true
                end
                size = size + len(data)

                local m = ngxmatch(data, [[Content-Disposition: form-data;(.+)filename="(.+)\.(.*)"]], 'ijo')
                local filetranslate
                if m then
                    fileExtCheck(m[3])
                    filetranslate = true
                else
                    if ngxmatch(data, "Content-Disposition:", 'isjo') then
                        filetranslate = false
                    end
                    if filetranslate == false then
                        if body(data) then
                            return true
                        end
                    end
                end
                local less = content_length - size
                if less < chunk_size then
                    chunk_size = less
                end
            end
            ngx.req.finish_body()
        else
            ngx.req.read_body()
            local args = ngx.req.get_post_args()
            if not args then
                return
            end
            local data
            for key, val in pairs(args) do
                -- ngx.log(ngx.ERR,key,val)
                if type(val) == "table" then
                    if type(val[1]) == "boolean" then
                        return
                    end
                    data = table.concat(val, ", ")
                else
                    data = val
                end
                if data and type(data) ~= "boolean" and body(data) then
                    body(key)
                end
            end
        end
    end
    return false
end

local function my_get_file_name(header)
    local file_name
    for i, ele in ipairs(header) do
        file_name = string.match(ele, 'filename="(.*)"')
        if file_name and file_name ~= '' then
            return file_name
        end
    end
    return nil
end



local function my_get_file_name_ext(header)
    local file_name_ext
    for i, ele in ipairs(header) do
        file_name_ext = ngxmatch(ele, [[filename="(.+)\.(.*)"]], 'ijo')
        if file_name_ext and file_name_ext ~= '' then
            return file_name_ext[2]
        end
        
    end
    return nil
end
function _M.post_check_new()
    local content_type = get_headers()["Content-Type"]    
    local method = ngx.req.get_method()
    if method == "POST" then
        -- ngx.log(ngx.ERR,"====",content_type)
        local boundary = get_boundary()
        if boundary then
            local len = string.len
            local sock, err = ngx.req.socket()
            if not sock then
                return
            end
            ngx.req.init_body(128 * 1024)
            sock:settimeout(0)
            local content_length = nil
            content_length = tonumber(ngx.req.get_headers()['content-length'])
            local chunk_size = 4096
            if content_length < chunk_size then
                chunk_size = content_length
            end
            local size = 0
            while size < content_length do
                local data, err, partial = sock:receive(chunk_size)
                data = data or partial
                if not data then
                    return
                end
                -- ngx.log(ngx.ERR,"++++++++++++++++++++",data)
                ngx.req.append_body(data)
                -- if body(data) then
                --     return true
                -- end
                size = size + len(data)

                local m = ngxmatch(data, [[Content-Disposition: form-data;(.+)filename="(.+)\.(.*)"]], 'ijo')
                if m then
                    fileExtCheck(m[3])
                end               

                -- local filetranslate
                -- if m then
                --     fileExtCheck(m[3])
                --     filetranslate = true
                -- else
                --     if ngxmatch(data, "Content-Disposition:", 'isjo') then
                --         filetranslate = false
                --     end
                --     if filetranslate == false then
                --         if body(data) then
                --             return true
                --         end
                --     end
                -- end
                local less = content_length - size
                if less < chunk_size then
                    chunk_size = less
                end
            end
            ngx.req.finish_body()        
        -- if boundary then
            -- local chunk_size = 4096
            -- local form,err = upload:new(chunk_size)
            -- if not form then
            --     ngx.log(ngx.ERR, "failed to new upload: ", err)
            --     return false
            -- end            
            -- while true do
            --     local typ, res, err = form:read()        
            --     if not typ then
            --          ngx.say("failed to read: ", err)
            --          return false
            --     end
            --     ngx.log(ngx.ERR,"=====read:",cjson.encode({typ,res}))

            --     if typ == "header" then
            --         -- for i, ele in ipairs(res) do
            --         --     ngx.log(ngx.ERR,i,"========",ele)
            --         -- end
            --         local ext = my_get_file_name_ext(res)
            --         if ext then
            --             fileExtCheck(ext)
            --         end        
            --     --针对multipart/form-data发送的post请求值暂时禁用过滤，容易对图片、excel文件误报
            --     -- elseif typ == "body" then
            --     --     if body(res) then
            --     --         return true
            --     --     end
            --     -- elseif typ == "eof" then
            --     --     break    
            --     end
            -- end
        else
            ngx.req.read_body()
            local args = ngx.req.get_post_args()
            if not args then
                return
            end
            local data
            for key, val in pairs(args) do
                -- ngx.log(ngx.ERR,"key=",key)
                -- ngx.log(ngx.ERR,"val=",val)
                if type(val) == "table" then
                    if type(val[1]) == "boolean" then
                        return
                    end
                    data = table.concat(val, ", ")
                else
                    data = val
                end
                --modify by lkh @20230110 request payload请求方式
                if content_type == "application/json" then
                    local json2 =cjson.decode(key)
                    -- ngx.log(ngx.ERR,type(json2))
                    for key, value in pairs(json2) do
                        body(value)  
                    end
--                    body(key)                    
                else
                    body(data) 
                end               

                -- if data and type(data) ~= "boolean" and body(data) then
                -- -- if data and type(data) == "" and body(data) == false then
                -- --     ngx.log(ngx.ERR,"====2",data)
                --     body(key)
                -- end
            end
        end
    end
    return false
end

function _M.res_body_replace()
    local resp_body, eof = ngx.arg[1], ngx.arg[2]
    if Replace_rules ~= nil then
        local new_resp_body = resp_body
        local flag
        for _, rule in ipairs(Replace_rules) do
--                local m = ngxmatch(resp_body, rule, "isjo")
            local newstr, n, err = ngx.re.gsub(new_resp_body, rule, "***", "i")
            -- if not newstr then
                -- ngx.log(ngx.ERR, "error: ", n)
            --     return
            -- end
            if n > 0 then
                flag = true
                log(ngx.req.get_method(), ngx.var.request_uri, resp_body, "敏感信息替换")
            end
            new_resp_body = newstr
        end
        if flag then
            ngx.arg[1] = new_resp_body
        end
    end                

end


function  _M.res_body_filter()
    local resp_body, eof = ngx.arg[1], ngx.arg[2]
    --  ngx.log(ngx.ERR,resp_body)
    --  ngx.log(ngx.ERR,"====",eof)
    -- local ctx = ngx.ctx 
    -- if ctx.buffers == nil then 
    --   ctx.buffers = {} 
    --   ctx.nbuffers = 0 
    -- end 
    -- local next_idx = ctx.nbuffers + 1 

    -- if not eof then 
    --   if resp_body then 
    --     ctx.buffers[next_idx] = resp_body 
    --     ctx.nbuffers = next_idx 
    --     -- Send nothing to the client yet. 
    --     ngx.arg[1] = nil 
    --   end 
    --   return 
    -- elseif resp_body then 
    --   ctx.buffers[next_idx] = resp_body 
    --   ctx.nbuffers = next_idx 
    -- end 
    -- ngx.arg[1] = "Cool... " .. table.concat(ngx.ctx.buffers) 

    if resp_body then
        if Code_leakages_rules ~= nil then
            for _, rule in ipairs(Code_leakages_rules) do
                local m = ngxmatch(resp_body, rule, "isjo")
                if m then
                    log(ngx.req.get_method(), ngx.var.request_uri, m[0], rule)
                    ngx.arg[1] = Html2
                    ngx.arg[2] = true
                    return
                end
            end
        end
                
    end
                

end

return _M