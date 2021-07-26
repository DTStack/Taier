package com.dtstack.engine.remote.annotation;

import com.dtstack.engine.remote.config.RemoteConditional;
import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

/**
 * @Auther: dazhi
 * @Date: 2020/9/3 1:47 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional({RemoteConditional.class})
public @interface RemoteClient {

    String value();

    Class<?> fallback() default void.class;
}
