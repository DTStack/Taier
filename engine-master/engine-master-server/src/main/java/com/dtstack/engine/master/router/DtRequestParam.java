package com.dtstack.engine.master.router;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/7/16
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DtRequestParam {

    @AliasFor("name")
    String value() default "";

    @AliasFor("value")
    String name() default "";

    boolean required() default false;

    String description() default "";
}