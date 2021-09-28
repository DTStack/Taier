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

package com.dtstack.engine.master;

import com.dtstack.engine.master.utils.CommonUtils;
import com.dtstack.engine.master.utils.ValueUtils;
import org.junit.runners.model.InitializationError;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.atomic.AtomicBoolean;


public class DtCenterSpringJUnit4ClassRunner extends SpringJUnit4ClassRunner {

    private final static AtomicBoolean init = new AtomicBoolean(false);

    /**
     * 设置 user.dir,使用项目根目录下的配置文件
     */
    public DtCenterSpringJUnit4ClassRunner(Class<?> clazz) throws InitializationError {
        super(clazz);
        //获得项目文件的根目录
        CommonUtils.setUserDirToTest();
    }


    @Override
    protected Object createTest() throws Exception {
        Object test = super.createTest();
        synchronized (DtCenterSpringJUnit4ClassRunner.class) {
            if (init.compareAndSet(false, true)) {
                try {
                    ValueUtils.initData();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return test;

    }

}
