//package com.xs.core.shandler;
//
//import com.alibaba.dts.common.util.StringUtil;
//import com.alibaba.fastjson.JSON;
//import com.mr.core.ResponseBean;
//import com.mr.core.ResultCode;
//import com.mr.core.sannotation.IgnoreAuth;
//import com.mr.services.UserTokenService;
//import com.mr.utils.IpUtils;
//import com.mr.utils.RespUtil;
//import com.mr.utils.SpringBootBeanUtil;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.lang.Nullable;
//import org.springframework.stereotype.Component;
//import org.springframework.web.method.HandlerMethod;
//import org.springframework.web.servlet.ModelAndView;
//import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.lang.reflect.Method;
//import java.util.Date;
//
//import static com.mr.core.ProjectConstant.BACK_COOKIE_TOKEN_KEY;
//
///**
// * @Auther: zx
// * @Date: 2018/5/12 12:06
// * @Description:
// */
//@Component
//public class WxHandler extends HandlerInterceptorAdapter {
//
//    private final Logger logger = LoggerFactory.getLogger(WxHandler.class);
//
//    @Autowired
//    private UserTokenService userTokenService;
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//
//        if(request != null && request.getMethod() != null && request.getMethod().toUpperCase() == "OPTIONS"){
//            logger.info("options试探性请求放行...");
//            return true;
//        }
//
//        logger.info("----------------wx开始认证-----------------");
//        if (userTokenService == null) {
//            userTokenService = (UserTokenService)SpringBootBeanUtil.getBean("usertokenService");
//        }
//
//        if((handler != null && !(handler instanceof HandlerMethod)) || (handler == null)) {
//            return true;
//        }
//        HandlerMethod handlerMethod = (HandlerMethod)handler;
//        // 从方法处理器中获取出要调用的方法
//        Method method = handlerMethod.getMethod();
//        if (method != null) {
//            IgnoreAuth annotation = method.getAnnotation(IgnoreAuth.class);
//            boolean pass = false;
//            if(null == annotation){
//                String token = request.getHeader(BACK_COOKIE_TOKEN_KEY);
//                if (StringUtil.isNotEmpty(token)) {
//                    Date expiretime = userTokenService.getExpiretime(token);
//                    logger.info("当前token值：{}，失效时间：{}", token, expiretime);
//                    if (null != expiretime && expiretime.getTime() > System.currentTimeMillis()) {
//                        pass = true;
//                    }
//                    //验证签名
////                pass= ValUtils.validateSign(request);
//                }
//                if (!pass) {
//                    logger.warn("签名验证失败，请求接口：{}，请求IP：{}，请求参数：{}",
//                            request.getRequestURI(), IpUtils.getIpAddr(request), JSON.toJSONString(request.getParameterMap()));
//                    ResponseBean result = new ResponseBean();
//                    result.setCode(ResultCode.UNAUTHORIZED).setMsg("非法请求，请重新登录！");
//                    RespUtil.responseResult(response, result);
//                    return false;
//                }
//            }
//        }
//        return  true;
//    }
//
//    @Override
//    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
//    }
//
//    @Override
//    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
//    }
//
//}
