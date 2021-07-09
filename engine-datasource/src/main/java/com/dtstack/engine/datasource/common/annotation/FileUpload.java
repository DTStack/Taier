package com.dtstack.engine.datasource.common.annotation;

import java.lang.annotation.*;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FileUpload {
    String value() default "";
}
