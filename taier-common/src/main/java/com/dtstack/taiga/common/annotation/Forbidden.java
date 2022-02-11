package com.dtstack.taiga.common.annotation;

import java.lang.annotation.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : hanbeikai
 * Date: 2021/12/15 11:31 下午
 * Description: No Description
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Forbidden {
}
