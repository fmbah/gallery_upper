package com.xs.core.slog;

import com.xs.core.sexception.ServiceException;
import com.xs.utils.RequestUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.executable.ExecutableValidator;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Set;

/**
 * 控制器的行为记录
 */
@Aspect
@Order(5)
@Component
public class WebLogAspect implements Serializable {

    private static final long serialVersionUID = 100004L;

    private final Logger logger = LoggerFactory.getLogger(WebLogAspect.class);

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final ExecutableValidator methodValidator = factory.getValidator().forExecutables();
    private final Validator beanValidator = factory.getValidator();

    private <T> Set<ConstraintViolation<T>> validMethodParams(T obj, Method method, Object[] params){
        return methodValidator.validateParameters(obj, method, params);
    }

    private <T> Set<ConstraintViolation<T>> validBeanParams(T bean) {
        return beanValidator.validate(bean);
    }

    ThreadLocal<Long> startTime = new ThreadLocal<>();

    @Pointcut("execution(public * com.xs.controllers..*.*(..))")
    public void webLog(){}

    @Before("webLog()")
    public Object doBefore(JoinPoint joinPoint) throws Throwable {
        if (startTime != null) {
            startTime.set(System.currentTimeMillis());

            // 接收到请求，记录请求内容
            if (RequestContextHolder.getRequestAttributes() != null) {
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (attributes != null) {
                    HttpServletRequest request = attributes.getRequest();
                    // 记录下请求内容
                    if (request != null) {
                        logger.info("请求地址: {}" , request.getRequestURL() != null ? request.getRequestURL().toString() : "请求路径未获取到");
                        logger.info("请求方式: {}" , request.getMethod() != null ? request.getMethod() : "请求方式未获取到");
                        logger.info("网络地址: {}" , request.getRemoteAddr() != null ? request.getRemoteAddr() : "网路地址未获取到");
                        if (joinPoint != null && joinPoint.getSignature() != null) {
                            logger.info("调用函数: {}" , joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
//        String params = RequestUtil.getResuestParams(request);
                            String params = RequestUtil.getResuestParams(request);
                            logger.info("请求参数: {}",params != null ? params : "请求参数未获取到");

                            Method method = ((MethodSignature)joinPoint.getSignature()).getMethod();
                            Object[] args = joinPoint.getArgs();
                            Object target = joinPoint.getThis();
                            // 校验以基本数据类型 为方法参数的
                            if (method != null && args != null && args.length > 0 && target != null) {
                                Set<ConstraintViolation<Object>> validResult = validMethodParams(target, method, args);
                                String msg = "";
                                if (validResult != null) {
                                    Iterator<ConstraintViolation<Object>> violationIterator = validResult.iterator();
                                    if (violationIterator != null) {
                                        while (violationIterator.hasNext()) {
                                            // 此处可以抛个异常提示用户参数输入格式不正确
                                            msg = violationIterator.next().getMessage();
                                            throw  new ServiceException(msg);
                                        }
//                                        // 校验以java bean对象 为方法参数的
//                                        for (Object bean : args) {
//                                            if (null != bean) {
//                                                validResult = validBeanParams(bean);
//                                                violationIterator = validResult.iterator();
//                                                while (violationIterator.hasNext()) {
//                                                    msg = violationIterator.next().getMessage();
//                                                    throw  new ServiceException(msg);
//                                                }
//                                            }
//                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    @AfterReturning(returning = "ret", pointcut = "webLog()")
    public void doAfterReturning(Object ret) throws Throwable {
        // 处理完请求，返回内容
        logger.info("返回数据: {}" , ret);
        logger.info("请求耗时: {}" , (System.currentTimeMillis() - startTime.get()));
    }


}