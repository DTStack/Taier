package com.dtstack.engine.flink.option;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface OptionRequired {

    boolean required() default false;

    boolean hasArg() default true;

    String description() default "";
}
