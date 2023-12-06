local core = require("conf/waf/core")

local _M = {}


function _M.initwaf()
	if core.deny_ips() then
	elseif core.white_method() then
	elseif core.whiteip() then
	elseif core.white_ua() then
	elseif core.blockip() then
	elseif core.denycc() then
	elseif ngx.var.http_Acunetix_Aspect then
		ngx.exit(444)
	elseif ngx.var.http_X_Scan_Memo then
		ngx.exit(444)
	elseif core.whiteurl() then
	elseif core.ua() then
	elseif core.url() then
	elseif core.args() then
	elseif core.cookie() then
	elseif core.post_check_new() then
	else
		return
	end
end
--web漏洞扫描工具常出现的一些漏洞
function _M.enforce_security()
	if XFrameOptions ~= "" then
		ngx.header["X-Frame-Options"] = XFrameOptions		
	end
	if AccessControlAllowOrigin ~= "" then
		ngx.header["Access-Control-Allow-Origin"] = AccessControlAllowOrigin
	end
	if ReferrerPolicy ~= "" then
		ngx.header["Referrer-Policy"] = ReferrerPolicy
	end
	if ContentSecurityPolicy ~="" then
		ngx.header["Content-Security-Policy"] = ContentSecurityPolicy
	end		
end



function  _M.responseBody_filter()
    if ResponseBodyFilter then
		core.res_body_filter()
	end
	if ResponseBodyReplace then
		core.res_body_replace()		
	end
end

return _M
