package com.kg.core.web;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 自定义错误
 *
 * @author ziro
 * @date 2023/9/6 17:18
 */
@Controller
@RequestMapping("/error")
public class MyErrorController implements ErrorController {

    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView handleError(Map<String, Object> model, HttpServletRequest request) {
        Object errorCode = request.getAttribute("javax.servlet.error.status_code");
        if (!ObjectUtils.isEmpty(errorCode)) {
            model.put("errorCode", errorCode);
        }
        // 自定义错误页
        return new ModelAndView("error-page", model);
    }


}
