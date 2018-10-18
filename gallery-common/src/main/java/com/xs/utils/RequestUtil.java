package com.xs.utils;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.StringTokenizer;

/**
 * @Auther: Fmbah
 * @Date: 18-10-16 上午9:32
 * @Description:
 */
public class RequestUtil {

    public static String getRealIP(HttpServletRequest request) {
        // We look if the request is forwarded
        // If it is not call the older function.
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null) {
            ip = request.getHeader("X-Real-IP");
            if (ip == null) {
                return request.getRemoteAddr();
            }

            return ip;
        } else {
            // Process the IP to keep the last IP (real ip of the computer on
            // the net)
            StringTokenizer tokenizer = new StringTokenizer(ip, ",");

            // Ignore all tokens, except the last one
            for (int i = 0; i < tokenizer.countTokens() - 1; i++) {
                tokenizer.nextElement();
            }

            ip = tokenizer.nextToken().trim();

            if (ip.equals("")) {
                ip = null;
            }
        }

        // If the ip is still null, we put 0.0.0.0 to avoid null values
        if (ip == null) {
            ip = "0.0.0.0";
        }

        return ip;
    }

    public static String getCookieValue(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0 && StringUtils.isNotBlank(cookieName)) {
            for (int i = 0; i < cookies.length; i++) {
                Cookie cookie = cookies[i];
                if (cookie != null && cookieName.equalsIgnoreCase(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    /**
     * 取得当前的request URL，包括query string。
     *
     * @param withQueryString 是否包含query string
     * @return 当前请求的request URL
     */
    public static String getRequestURL(HttpServletRequest request, boolean withQueryString) {
        return getRequestURL(request, true, false);
    }

    public static String getRequestURL(HttpServletRequest request) {
        return getRequestURL(request, true);
    }

    public static String getRequestURL(HttpServletRequest request, boolean withQueryString, boolean schemeHttps) {
        StringBuffer buffer = request.getRequestURL();
        if (withQueryString) {
            String queryString = StringUtils.trimToNull(request.getQueryString());

            if (queryString != null) {
                buffer.append('?').append(queryString);
            }
        }

        String url = buffer.toString();

        return schemeHttps ? url.replaceAll("^http:", "https:") : url;
    }

    public static String getResuestParams(HttpServletRequest request){
        String params = "";
        Enumeration paramNames = request.getParameterNames();
        if(paramNames.hasMoreElements()) {
            while (paramNames.hasMoreElements()) {
                String paramName = (String) paramNames.nextElement();
                String[] paramValues = request.getParameterValues(paramName);
                if (paramValues.length == 1) {
                    String paramValue = paramValues[0];
                    if (!org.springframework.util.StringUtils.isEmpty(paramValue)&&paramValue.length() != 0) {
                        params+= paramName + " = " + paramValue+",";
                    }
                }
            }
            if(!org.springframework.util.StringUtils.isEmpty(params)){
                return params.substring(0,params.length()-1);
            }
        }
        return "无";
    }

    public static String getIpAddr(HttpServletRequest request)
    {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 如果是多级代理，那么取第一个ip为客户端ip
        if (ip != null && ip.indexOf(",") != -1) {
            ip = ip.substring(0, ip.indexOf(",")).trim();
        }
        return ip;
    }

}
