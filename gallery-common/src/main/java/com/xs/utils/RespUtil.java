package com.xs.utils;

import com.alibaba.fastjson.JSON;
import com.xs.core.ResponseBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: Fmbah
 * @Date: 18-10-17 上午10:37
 * @Description:
 */
public class RespUtil {

    private static final Logger logger = LoggerFactory.getLogger(RespUtil.class);

    public static void responseResult(HttpServletResponse response, ResponseBean result) {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-type", "application/json;charset=UTF-8");
        response.setStatus(200);
        try {
            response.getWriter().write(JSON.toJSONString(result));
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }
    }

    public static void responseResult(HttpServletResponse response, Map<String, String> msg) {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-type", "application/json;charset=UTF-8");
        response.setStatus(200);
        try {
            response.getWriter().write(JSON.toJSONString(msg));
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }
    }

    public static void responsePushResult(HttpServletResponse response) {
        try {
            HashMap hashMap = new HashMap();
            hashMap.put("result", true);
            hashMap.put("returnCode", "200");
            hashMap.put("message", "成功");
            response.getWriter().write(JSON.toJSONString(hashMap));
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }
    }

}
