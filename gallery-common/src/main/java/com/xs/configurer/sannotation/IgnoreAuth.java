package com.xs.configurer.sannotation;

import java.lang.annotation.*;

/**
 * @Auther: Fmbah
 * @Date: 18-10-16 下午5:36
 * @Description:
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IgnoreAuth {

}
