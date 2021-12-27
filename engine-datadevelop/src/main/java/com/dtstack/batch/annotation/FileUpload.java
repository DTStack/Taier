package com.dtstack.batch.annotation;

import java.lang.annotation.*;

/**
 *
 * @description:
 * @author: liuxx
 * @date: 2021/3/18
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FileUpload {
    String value() default "";
}
