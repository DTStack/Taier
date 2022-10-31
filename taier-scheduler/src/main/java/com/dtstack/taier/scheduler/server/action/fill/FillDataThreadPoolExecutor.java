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

package com.dtstack.taier.scheduler.server.action.fill;

import com.dtstack.taier.common.env.EnvironmentContext;
import com.dtstack.taier.pluginapi.CustomThreadFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

/**
 * @Auther: dazhi
 * @Date: 2021/9/10 2:05 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Component
public class FillDataThreadPoolExecutor implements InitializingBean, DisposableBean {

    @Autowired
    private EnvironmentContext environmentContext;

    private final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

    @Override
    public void afterPropertiesSet() throws Exception {
        //线程池维护线程的最少数量
        executor.setCorePoolSize(environmentContext.getFillDataThreadPoolCorePoolSize());
        //线程池维护线程的最大数量
        executor.setMaxPoolSize(environmentContext.getMaxFillDataThreadPoolSize());
        //线程池所使用的缓冲队列
        executor.setQueueCapacity(environmentContext.getFillDataQueueCapacity());
        // 设置线程工程类
        executor.setThreadFactory(new CustomThreadFactory(this.getClass().getSimpleName()));
        executor.initialize();
    }

    @Override
    public void destroy() throws Exception {
        executor.shutdown();
    }

    public void submit(FillDataRunnable fillDataRunnable) {
        executor.execute(fillDataRunnable);
    }

}
