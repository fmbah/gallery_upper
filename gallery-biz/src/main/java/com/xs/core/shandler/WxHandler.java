package com.xs.core.shandler;

import com.alibaba.fastjson.JSON;
import com.xs.configurer.sannotation.IgnoreAuth;
import com.xs.core.ResponseBean;
import com.xs.core.ResultCode;
import com.xs.utils.IpUtils;
import com.xs.utils.RespUtil;
import com.xs.utils.SpringBootBeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import tk.mybatis.mapper.util.StringUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

import static com.xs.core.ProjectConstant.WX_USER_FONT_TOKEN;
import static com.xs.core.ProjectConstant.WX_USER_TOKEN;

/**
 * @Auther: zx
 * @Date: 2018/5/12 12:06
 * @Description:
 */
@Component
public class WxHandler extends HandlerInterceptorAdapter {

    private final Logger logger = LoggerFactory.getLogger(WxHandler.class);

    @Autowired
    private JedisPool jedisPool;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if(request != null && request.getMethod() != null && request.getMethod().toUpperCase() == "OPTIONS"){
            return true;
        }

        logger.info("----------------wx开始认证-----------------");
        if (jedisPool == null) {
            jedisPool = (JedisPool)SpringBootBeanUtil.getBean("jedisPool");
        }

        if((handler != null && !(handler instanceof HandlerMethod)) || (handler == null)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod)handler;
        // 从方法处理器中获取出要调用的方法
        Method method = handlerMethod.getMethod();
        if (method != null) {
            IgnoreAuth annotation = method.getAnnotation(IgnoreAuth.class);
            boolean pass = false;
            if(null == annotation){
                String token = request.getHeader(WX_USER_FONT_TOKEN);
                if (StringUtil.isNotEmpty(token) && token.split("_").length > 0) {
                    try (Jedis jedis = jedisPool.getResource()) {
                        String key = String.format(WX_USER_TOKEN, token.split("_")[1]);
                        Boolean exists = jedis.exists(key);
                        if (exists) {
                            pass = true;
                        }
                    }
                }
                if (!pass) {
                    logger.warn("签名验证失败，请求接口：{}，请求IP：{}，请求参数：{}",
                            request.getRequestURI(), IpUtils.getIpAddr(request), JSON.toJSONString(request.getParameterMap()));
                    ResponseBean result = new ResponseBean();
                    result.setCode(ResultCode.UNAUTHORIZED).setMsg("非法请求，请重新登录！");
                    RespUtil.responseResult(response, result);
                    return false;
                }
            }
        }
        return  true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
    }

}
