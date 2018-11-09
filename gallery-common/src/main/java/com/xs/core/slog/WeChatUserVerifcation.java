//package com.xs.core.slog;
//
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.annotation.AfterReturning;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Before;
//import org.aspectj.lang.annotation.Pointcut;
//import org.springframework.stereotype.Component;
//
//import java.io.Serializable;
//import java.util.Arrays;
//
///**
// * @ClassName WeChatUserVerifcation
// * @Description
// * @Author root
// * @Date 18-11-9 上午11:13
// * @Version 1.0
// **/
//@Aspect
//@Component
//public class WeChatUserVerifcation implements Serializable {
//
//
//    @Pointcut("execution(public * com.xs.services.WxAppAllService.*(..))")
//    public void userVerifcation(){}
//
//    @Before("userVerifcation()")
//    public Object doBefore(JoinPoint joinPoint) throws Throwable {
//        System.out.println(Arrays.toString(joinPoint.getArgs()));
//        return null;
//    }
//
//    @AfterReturning(returning = "ret", pointcut = "userVerifcation()")
//    public void doAfterReturning(Object ret) throws Throwable {
//        System.out.println(ret);
//    }
//}
