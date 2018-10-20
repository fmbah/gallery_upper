package com.xs.core.shandler;

import com.xs.configurer.sannotation.IgnoreAuth;
import com.xs.core.ResponseBean;
import com.xs.core.ResultCode;
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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;

import static com.xs.core.ProjectConstant.BACK_LOGIN_BZ;
import static com.xs.core.ProjectConstant.BACK_MANAGER_KEY;
import static com.xs.core.ProjectConstant.BACK_MANAGER_MENUID;


/**
 * @Auther: zx
 * @Date: 2018/5/30 09:06
 * @Description:
 */
@Component
public class BackHandler extends HandlerInterceptorAdapter {

    public final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JedisPool jedisPool;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if(request != null && request.getMethod() != null && request.getMethod().toUpperCase() == "OPTIONS"){
            logger.info("options试探性请求放行...");
            return true;
        }

        if((handler != null && !(handler instanceof HandlerMethod)) || (handler == null)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod)handler;
        Method method = handlerMethod.getMethod();
        if (method != null) {
            IgnoreAuth annotation = method.getAnnotation(IgnoreAuth.class);
            if(null == annotation){
                Cookie[] cookies = request.getCookies();
                if (cookies == null || cookies.length == 0) {
                    ResponseBean result = new ResponseBean();
                    result.setCode(ResultCode.UNAUTHORIZED).setMsg("登录标志不存在,请重新登录！");
                    RespUtil.responseResult(response, result);
                    return false;
                }

                boolean hasLogKey = false;
                String hasLogValue = null;
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equalsIgnoreCase(BACK_MANAGER_KEY)) {
                        hasLogKey = true;
                        hasLogValue = cookie.getValue();
                    }
                }

                if (!hasLogKey) {
                    ResponseBean result = new ResponseBean();
                    result.setCode(ResultCode.UNAUTHORIZED).setMsg("登录超时，请重新登录！");
                    RespUtil.responseResult(response, result);
                    return false;
                } else {
                    String username = hasLogValue.split("#")[0];
                    List<String> menuIds = Arrays.asList(URLDecoder.decode(hasLogValue.split("#")[1], "utf-8").split(","));

                    String menuId = request.getHeader(BACK_MANAGER_MENUID);
                    if (menuId == null) {
                        menuId = request.getParameter(BACK_MANAGER_MENUID);
                    }

                    if (menuId == null) {
                        ResponseBean result = new ResponseBean();
                        result.setCode(ResultCode.FAIL).setMsg("当前操作代码:0,未传菜单id");//未传菜单id
                        RespUtil.responseResult(response, result);
                        return false;
                    }

                    if (!menuIds.contains(menuId)) {
                        ResponseBean result = new ResponseBean();
                        result.setCode(ResultCode.UNAUTHORIZED).setMsg("当前操作代码:1,无权限访问");//无权限访问
                        RespUtil.responseResult(response, result);
                        return false;
                    }

                    if (jedisPool == null) {
                        jedisPool = (JedisPool)SpringBootBeanUtil.getBean("jedisPool");
                    }
                    try(Jedis jedis = jedisPool.getResource()) {
                        Long zrank = jedis.zrank(BACK_LOGIN_BZ, username);
                        if (zrank == null) {
                            ResponseBean result = new ResponseBean();
                            result.setCode(ResultCode.UNAUTHORIZED).setMsg("当前操作代码:2,帐号过期,请重新登录");//无权限访问
                            RespUtil.responseResult(response, result);
                            return false;
                        }
                    }
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
