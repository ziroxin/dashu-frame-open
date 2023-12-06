if ResponseBodyFilter or ResponseBodyReplace then
    local waf = require("conf/waf/waf")
    waf.responseBody_filter()
end