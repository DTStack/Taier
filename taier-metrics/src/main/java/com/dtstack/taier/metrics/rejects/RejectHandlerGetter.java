
package com.dtstack.taier.metrics.rejects;

import com.dtstack.taier.metrics.collect.util.ExtensionServiceLoader;
import com.dtstack.taier.metrics.exception.TpException;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import static com.dtstack.taier.metrics.collect.em.RejectedTypeEnum.ABORT_POLICY;
import static com.dtstack.taier.metrics.collect.em.RejectedTypeEnum.CALLER_RUNS_POLICY;
import static com.dtstack.taier.metrics.collect.em.RejectedTypeEnum.DISCARD_OLDEST_POLICY;
import static com.dtstack.taier.metrics.collect.em.RejectedTypeEnum.DISCARD_POLICY;

/**
 * @author xingyi
 * @date 2025/9/17
 */
@Slf4j
public class RejectHandlerGetter {

    private RejectHandlerGetter() {
    }

    public static RejectedExecutionHandler buildRejectedHandler(String name) {
        if (Objects.equals(name, ABORT_POLICY.getName())) {
            return new ThreadPoolExecutor.AbortPolicy();
        } else if (Objects.equals(name, CALLER_RUNS_POLICY.getName())) {
            return new ThreadPoolExecutor.CallerRunsPolicy();
        } else if (Objects.equals(name, DISCARD_OLDEST_POLICY.getName())) {
            return new ThreadPoolExecutor.DiscardOldestPolicy();
        } else if (Objects.equals(name, DISCARD_POLICY.getName())) {
            return new ThreadPoolExecutor.DiscardPolicy();
        }
        List<RejectedExecutionHandler> loadedHandlers = ExtensionServiceLoader.get(RejectedExecutionHandler.class);
        for (RejectedExecutionHandler handler : loadedHandlers) {
            String handlerName = handler.getClass().getSimpleName();
            if (name.equalsIgnoreCase(handlerName)) {
                return handler;
            }
        }

        log.error("Cannot find specified rejectedHandler {}", name);
        throw new TpException("Cannot find specified rejectedHandler " + name);
    }

    public static RejectedExecutionHandler getProxy(String name) {
        return getProxy(buildRejectedHandler(name));
    }

    public static RejectedExecutionHandler getProxy(RejectedExecutionHandler handler) {
        return (RejectedExecutionHandler) Proxy
                .newProxyInstance(handler.getClass().getClassLoader(),
                        new Class[]{RejectedExecutionHandler.class},
                        new RejectedInvocationHandler(handler));
    }
}
