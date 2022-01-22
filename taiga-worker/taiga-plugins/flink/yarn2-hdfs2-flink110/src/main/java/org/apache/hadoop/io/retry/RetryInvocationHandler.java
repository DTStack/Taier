/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.io.retry;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.io.retry.FailoverProxyProvider.ProxyInfo;
import org.apache.hadoop.io.retry.RetryPolicy.RetryAction;
import org.apache.hadoop.ipc.Client;
import org.apache.hadoop.ipc.Client.ConnectionId;
import org.apache.hadoop.ipc.ProtocolTranslator;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.ipc.RpcConstants;
import org.apache.hadoop.ipc.RpcInvocationHandler;

import com.google.common.annotations.VisibleForTesting;

/**
 * This class implements RpcInvocationHandler and supports retry on the client
 * side.
 */
@InterfaceAudience.Private
public class RetryInvocationHandler<T> implements RpcInvocationHandler {
    public static final Log LOG = LogFactory.getLog(RetryInvocationHandler.class);
    private final FailoverProxyProvider<T> proxyProvider;

    /**
     * The number of times the associated proxyProvider has ever been failed over.
     */
    private long proxyProviderFailoverCount = 0;
    private volatile boolean hasMadeASuccessfulCall = false;

    private final RetryPolicy defaultPolicy;
    private final Map<String,RetryPolicy> methodNameToPolicyMap;
    private ProxyInfo<T> currentProxy;

    private final static int RETRY_LIMIT = 4;

    protected RetryInvocationHandler(FailoverProxyProvider<T> proxyProvider,
                                     RetryPolicy retryPolicy) {
        this(proxyProvider, retryPolicy, Collections.<String, RetryPolicy>emptyMap());
    }

    protected RetryInvocationHandler(FailoverProxyProvider<T> proxyProvider,
                                     RetryPolicy defaultPolicy,
                                     Map<String, RetryPolicy> methodNameToPolicyMap) {
        this.proxyProvider = proxyProvider;
        this.defaultPolicy = defaultPolicy;
        this.methodNameToPolicyMap = methodNameToPolicyMap;
        this.currentProxy = proxyProvider.getProxy();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        RetryPolicy policy = methodNameToPolicyMap.get(method.getName());
        if (policy == null) {
            policy = defaultPolicy;
        }

        // The number of times this method invocation has been failed over.
        int invocationFailoverCount = 0;
        final boolean isRpc = isRpcInvocation(currentProxy.proxy);
        final int callId = isRpc? Client.nextCallId(): RpcConstants.INVALID_CALL_ID;
        int retries = 0;
        while (true) {
            // The number of times this invocation handler has ever been failed over,
            // before this method invocation attempt. Used to prevent concurrent
            // failed method invocations from triggering multiple failover attempts.
            long invocationAttemptFailoverCount;
            synchronized (proxyProvider) {
                invocationAttemptFailoverCount = proxyProviderFailoverCount;
            }

            if (isRpc) {
                Client.setCallIdAndRetryCount(callId, retries);
            }
            try {
                Object ret = invokeMethod(method, args);
                hasMadeASuccessfulCall = true;
                return ret;
            } catch (Exception e) {
                if (Thread.currentThread().isInterrupted()) {
                    // If interrupted, do not retry.
                    throw e;
                }
                boolean isIdempotentOrAtMostOnce = proxyProvider.getInterface()
                        .getMethod(method.getName(), method.getParameterTypes())
                        .isAnnotationPresent(Idempotent.class);
                if (!isIdempotentOrAtMostOnce) {
                    isIdempotentOrAtMostOnce = proxyProvider.getInterface()
                            .getMethod(method.getName(), method.getParameterTypes())
                            .isAnnotationPresent(AtMostOnce.class);
                }
                RetryAction action = policy.shouldRetry(e, retries++,
                        invocationFailoverCount, isIdempotentOrAtMostOnce);

                if (retries == RETRY_LIMIT) {
                    LOG.error("RetryInvocationHandler invoke error, RetryAction: " + action.toString(), e);
                    throw e;
                }

                if (action.action == RetryAction.RetryDecision.FAIL) {
                    if (action.reason != null) {
                        LOG.warn("Exception while invoking " + currentProxy.proxy.getClass()
                                + "." + method.getName() + " over " + currentProxy.proxyInfo
                                + ". Not retrying because " + action.reason, e);
                    }
                    throw e;
                } else { // retry or failover
                    // avoid logging the failover if this is the first call on this
                    // proxy object, and we successfully achieve the failover without
                    // any flip-flopping
                    boolean worthLogging =
                            !(invocationFailoverCount == 0 && !hasMadeASuccessfulCall);
                    worthLogging |= LOG.isDebugEnabled();
                    if (action.action == RetryAction.RetryDecision.FAILOVER_AND_RETRY &&
                            worthLogging) {
                        String msg = "Exception while invoking " + method.getName()
                                + " of class " + currentProxy.proxy.getClass().getSimpleName()
                                + " over " + currentProxy.proxyInfo;

                        if (invocationFailoverCount > 0) {
                            msg += " after " + invocationFailoverCount + " fail over attempts";
                        }
                        msg += ". Trying to fail over " + formatSleepMessage(action.delayMillis);
                        LOG.info(msg, e);
                    } else {
                        if(LOG.isDebugEnabled()) {
                            LOG.debug("Exception while invoking " + method.getName()
                                    + " of class " + currentProxy.proxy.getClass().getSimpleName()
                                    + " over " + currentProxy.proxyInfo + ". Retrying "
                                    + formatSleepMessage(action.delayMillis), e);
                        }
                    }

                    if (action.delayMillis > 0) {
                        Thread.sleep(action.delayMillis);
                    }

                    if (action.action == RetryAction.RetryDecision.FAILOVER_AND_RETRY) {
                        // Make sure that concurrent failed method invocations only cause a
                        // single actual fail over.
                        synchronized (proxyProvider) {
                            if (invocationAttemptFailoverCount == proxyProviderFailoverCount) {
                                proxyProvider.performFailover(currentProxy.proxy);
                                proxyProviderFailoverCount++;
                            } else {
                                LOG.warn("A failover has occurred since the start of this method"
                                        + " invocation attempt.");
                            }
                            currentProxy = proxyProvider.getProxy();
                        }
                        invocationFailoverCount++;
                    }
                }
            }
        }
    }

    private static String formatSleepMessage(long millis) {
        if (millis > 0) {
            return "after sleeping for " + millis + "ms.";
        } else {
            return "immediately.";
        }
    }

    protected Object invokeMethod(Method method, Object[] args) throws Throwable {
        try {
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            return method.invoke(currentProxy.proxy, args);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

    @VisibleForTesting
    static boolean isRpcInvocation(Object proxy) {
        if (proxy instanceof ProtocolTranslator) {
            proxy = ((ProtocolTranslator) proxy).getUnderlyingProxyObject();
        }
        if (!Proxy.isProxyClass(proxy.getClass())) {
            return false;
        }
        final InvocationHandler ih = Proxy.getInvocationHandler(proxy);
        return ih instanceof RpcInvocationHandler;
    }

    @Override
    public void close() throws IOException {
        proxyProvider.close();
    }

    @Override //RpcInvocationHandler
    public ConnectionId getConnectionId() {
        return RPC.getConnectionIdForProxy(currentProxy.proxy);
    }

}
