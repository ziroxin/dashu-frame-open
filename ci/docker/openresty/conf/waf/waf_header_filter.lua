ngx.header['x-Powered-By'] = nil
ngx.header['server'] = nil

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

if ResponseBodyFilter or ResponseBodyReplace then
    ngx.header['content_length'] = nil    
end
