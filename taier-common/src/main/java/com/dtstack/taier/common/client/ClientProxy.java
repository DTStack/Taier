/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.taier.common.client;

import com.dtstack.taier.common.exception.LimitResourceException;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.pluginapi.CustomThreadFactory;
import com.dtstack.taier.pluginapi.JobClient;
import com.dtstack.taier.pluginapi.JobIdentifier;
import com.dtstack.taier.pluginapi.callback.CallBack;
import com.dtstack.taier.pluginapi.callback.ClassLoaderCallBackMethod;
import com.dtstack.taier.pluginapi.client.IClient;
import com.dtstack.taier.pluginapi.enums.RdosTaskStatus;
import com.dtstack.taier.pluginapi.exception.ClientArgumentException;
import com.dtstack.taier.pluginapi.exception.ExceptionUtil;
import com.dtstack.taier.pluginapi.pojo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;

/**
 * 代理IClient实现类的proxy
 * Date: 2017/12/19
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public class ClientProxy implements IClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientProxy.class);

    private IClient targetClient;

    private ExecutorService executorService;

    private long timeout = 300000;

    public ClientProxy(IClient targetClient) {
        this.targetClient = targetClient;
        executorService = new ThreadPoolExecutor(10, 10, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(), new CustomThreadFactory(targetClient.getClass().getSimpleName() + "_" + this.getClass().getSimpleName()));
    }

    @Override
    public void init(Properties prop) throws Exception {
        try {
            CompletableFuture.supplyAsync(() -> {
                try {
                    return ClassLoaderCallBackMethod.callbackAndReset(new CallBack<String>() {
                        @Override
                        public String execute() throws Exception {
                            targetClient.init(prop);
                            return null;
                        }
                    }, targetClient.getClass().getClassLoader(), true);
                } catch (Exception e) {
                    throw new RdosDefineException(e);
                }
            }, executorService).get(timeout, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            // 初始化失败,关闭线程池
            executorService.shutdown();
            throw new RdosDefineException(e);
        }
    }

    @Override
    public JobResult submitJob(JobClient jobClient) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return ClassLoaderCallBackMethod.callbackAndReset(new CallBack<JobResult>() {

                        @Override
                        public JobResult execute() throws Exception {
                            return targetClient.submitJob(jobClient);
                        }
                    }, targetClient.getClass().getClassLoader(), true);
                } catch (Exception e) {
                    throw new RdosDefineException(e);
                }
            }, executorService).get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RdosDefineException(e);
        }
    }

    @Override
    public JobResult cancelJob(JobIdentifier jobIdentifier) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return ClassLoaderCallBackMethod.callbackAndReset(new CallBack<JobResult>() {

                        @Override
                        public JobResult execute() throws Exception {
                            return targetClient.cancelJob(jobIdentifier);
                        }
                    }, targetClient.getClass().getClassLoader(), true);
                } catch (Exception e) {
                    throw new RdosDefineException(e);
                }
            }, executorService).get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RdosDefineException(e);
        }
    }

    @Override
    public RdosTaskStatus getJobStatus(JobIdentifier jobIdentifier) throws IOException {
        try {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return ClassLoaderCallBackMethod.callbackAndReset(new CallBack<RdosTaskStatus>() {

                        @Override
                        public RdosTaskStatus execute() throws Exception {
                            return targetClient.getJobStatus(jobIdentifier);
                        }
                    }, targetClient.getClass().getClassLoader(), true);
                } catch (Exception e) {
                    throw new RdosDefineException(e);
                }
            }, executorService).get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RdosDefineException(e);
        }
    }

    @Override
    public String getJobMaster(JobIdentifier jobIdentifier) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return ClassLoaderCallBackMethod.callbackAndReset(new CallBack<String>() {

                        @Override
                        public String execute() throws Exception {
                            return targetClient.getJobMaster(jobIdentifier);
                        }
                    }, targetClient.getClass().getClassLoader(), true);
                } catch (Exception e) {
                    throw new RdosDefineException(e);
                }
            }, executorService).get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RdosDefineException(e);
        }
    }

    @Override
    public String getMessageByHttp(String path) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return ClassLoaderCallBackMethod.callbackAndReset(new CallBack<String>() {

                        @Override
                        public String execute() throws Exception {
                            return targetClient.getMessageByHttp(path);
                        }
                    }, targetClient.getClass().getClassLoader(), true);
                } catch (Exception e) {
                    throw new RdosDefineException(e);
                }
            }, executorService).get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RdosDefineException(e);
        }
    }

    @Override
    public String getJobLog(JobIdentifier jobIdentifier) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return ClassLoaderCallBackMethod.callbackAndReset(new CallBack<String>() {

                        @Override
                        public String execute() throws Exception {
                            return targetClient.getJobLog(jobIdentifier);
                        }
                    }, targetClient.getClass().getClassLoader(), true);
                } catch (Exception e) {
                    throw new RdosDefineException(e);
                }
            }, executorService).get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RdosDefineException(e);
        }
    }

    @Override
    public JudgeResult judgeSlots(JobClient jobClient) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return ClassLoaderCallBackMethod.callbackAndReset(new CallBack<JudgeResult>() {

                        @Override
                        public JudgeResult execute() throws Exception {
                            return targetClient.judgeSlots(jobClient);
                        }
                    }, targetClient.getClass().getClassLoader(), true);
                } catch (Exception e) {
                    return getJudgeResultWithException(e, e);
                }
            }, executorService).get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            return getJudgeResultWithException(e, e.getCause());
        }
    }

    private JudgeResult getJudgeResultWithException(Exception e, Throwable throwable) {
        if (throwable instanceof ClientArgumentException) {
            throw new ClientArgumentException(e);
        } else if (throwable instanceof LimitResourceException) {
            throw new LimitResourceException(e.getMessage());
        } else if (throwable instanceof RdosDefineException) {
            return JudgeResult.exception( "judgeSlots error" + ExceptionUtil.getErrorMessage(e));
        }
        throw new RdosDefineException(e);
    }


    @Override
    public String getCheckpoints(JobIdentifier jobIdentifier) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return ClassLoaderCallBackMethod.callbackAndReset(new CallBack<String>() {

                        @Override
                        public String execute() throws Exception {
                            return targetClient.getCheckpoints(jobIdentifier);
                        }
                    }, targetClient.getClass().getClassLoader(), true);
                } catch (Exception e) {
                    throw new RdosDefineException(e);
                }
            }, executorService).get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RdosDefineException(e);
        }
    }

    @Override
    public ComponentTestResult testConnect(String pluginInfo) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return ClassLoaderCallBackMethod.callbackAndReset(() -> targetClient.testConnect(pluginInfo),
                            targetClient.getClass().getClassLoader(), true);
                } catch (Exception e) {
                    throw new RdosDefineException(e);
                }
            }, executorService).get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RdosDefineException(e);
        }
    }

    @Override
    public List<List<Object>> executeQuery(String sql, String database) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return ClassLoaderCallBackMethod.callbackAndReset(() -> targetClient.executeQuery(sql,database),
                            targetClient.getClass().getClassLoader(), true);
                } catch (Exception e) {
                    throw new RdosDefineException(e);
                }
            }, executorService).get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RdosDefineException(e);
        }
    }

    @Override
    public String uploadStringToHdfs(String bytes, String hdfsPath) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return ClassLoaderCallBackMethod.callbackAndReset(() -> targetClient.uploadStringToHdfs(bytes,hdfsPath),
                            targetClient.getClass().getClassLoader(), true);
                } catch (Exception e) {
                    throw new RdosDefineException(e);
                }
            }, executorService).get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RdosDefineException(e);
        }
    }

    @Override
    public ClusterResource getClusterResource() {
        try {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return ClassLoaderCallBackMethod.callbackAndReset(() -> targetClient.getClusterResource(),
                            targetClient.getClass().getClassLoader(), true);
                } catch (Exception e) {
                    throw new RdosDefineException(e);
                }
            }, executorService).get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RdosDefineException(e);
        }
    }

    @Override
    public List<String> getRollingLogBaseInfo(JobIdentifier jobIdentifier) {
        try {
            return ClassLoaderCallBackMethod.callbackAndReset(() -> targetClient.getRollingLogBaseInfo(jobIdentifier), targetClient.getClass().getClassLoader(), true);
        } catch (Exception e) {
            throw new RdosDefineException(e.getMessage());
        }
    }


    @Override
    public CheckResult grammarCheck(JobClient jobClient) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return ClassLoaderCallBackMethod.callbackAndReset(() -> targetClient.grammarCheck(jobClient), targetClient.getClass().getClassLoader(), true);
                } catch (Exception e) {
                    throw new RdosDefineException(e);
                }
            }, executorService).get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RdosDefineException(e);
        }
    }

}
