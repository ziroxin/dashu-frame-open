package com.kg.core.xss;

import cn.hutool.core.util.EscapeUtil;
import org.springframework.util.StringUtils;

import java.util.LinkedList;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * XSS html处理工具类
 *
 * @author ziro
 * @date 2023-01-28 17:29:03
 */
public class XssFormatUtil {
    /**
     * 清除脚本，编译HTML
     */
    public static String cleanHtml(CharSequence html) {
        String value = html != null ? html.toString() : "";
        if (StringUtils.hasText(html)) {
            // 清除脚本
            Pattern scriptPattern = Pattern.compile("<[\r\n| | ]*script[\r\n| | ]*>(.*?)</[\r\n| | ]*script[\r\n| | ]*>", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");
            scriptPattern = Pattern.compile("</[\r\n| | ]*script[\r\n| | ]*>", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");
            scriptPattern = Pattern.compile("<[\r\n| | ]*script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");
            scriptPattern = Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");
            scriptPattern = Pattern.compile("e-xpression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");
            scriptPattern = Pattern.compile("javascript[\r\n| | ]*:[\r\n| | ]*", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");
            scriptPattern = Pattern.compile("vbscript[\r\n| | ]*:[\r\n| | ]*", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");
            String eventStrs = "abort,afterprint,animationend,animationiteration,animationstart,beforeprint,beforeunload,blur,canplay," +
                    "canplaythrough,change,click,contextmenu,copy,cut,dblclick,drag,dragend,dragenter,dragleave,dragover,dragstart," +
                    "drop,durationchange,ended,error,focus,focusin,focusout,fullscreenchange,fullscreenerror,hashchange,input," +
                    "invalid,keydown,keypress,keyup,load,loadeddata,loadedmetadata,loadstart,message,mousedown,mouseenter,mouseleave," +
                    "mousemove,mouseout,mouseover,mouseup,mousewheel,offline,online,open,pagehide,pageshow,paste,pause,play,playing," +
                    "popstate,progress,ratechange,reset,resize,scroll,search,seeked,seeking,select,show,stalled,storage,submit,suspend," +
                    "timeupdate,toggle,touchcancel,touchend,touchmove,touchstart,transitionend,unload,volumechange,waiting,wheel";
            for (String event : eventStrs.split(",")) {
                scriptPattern = Pattern.compile("on" + event + "=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
                value = scriptPattern.matcher(value).replaceAll("");
                scriptPattern = Pattern.compile(event + "=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
                value = scriptPattern.matcher(value).replaceAll("");
            }
        }
        // 根据 " 分成数组进行编码， " 为特殊字符，不编码
        String[] strs = value.split("\"");
        LinkedList<String> resultList = new LinkedList<>();
        for (String str : strs) {
            resultList.add(EscapeUtil.escapeHtml4(str));
        }
        return resultList.stream().collect(Collectors.joining("\""));
    }

    /**
     * 反编译成html
     */
    public static String toHtml(CharSequence value) {
        String html = value != null ? value.toString() : "";
        if (StringUtils.hasText(html)) {
            // 反编译html
            html = EscapeUtil.unescapeHtml4(html);
        }
        return html;
    }
}
