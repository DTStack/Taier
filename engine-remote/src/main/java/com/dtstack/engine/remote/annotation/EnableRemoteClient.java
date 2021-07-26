package com.dtstack.engine.remote.annotation;

import com.dtstack.engine.remote.config.RemoteClientRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @Auther: dazhi
 * @Date: 2020/9/3 2:58 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({RemoteClientRegistrar.class})
public @interface EnableRemoteClient {

    String basePackage() default "";

}
