package com.dtstack.engine.master.router;

import java.lang.annotation.*;

/**
 * @Auther: dazhi
 * @Date: 2021/5/11 6:58 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DtParamOrHeader {

    /**
     * 参数名
     *
     * @return
     */
    String value() default "";

    /**
     * header name
     *
     * @return
     */
    String header() default "";

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
