package com.dtstack.engine.master.router;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @Auther: dazhi
 * @Date: 2020/8/20 11:40 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DtHeader {

    /**
     * header name
     *
     * @return
     */
    String value() default "";

    /**
     * 如果 header = cookie时
     * 获取cookie内部信息
     *
     * @return
     */
    String cookie() default "";

    boolean required() default false;

    String description() default "";
}
