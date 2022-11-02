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

package com.dtstack.taier.common.lang.coc;

import com.dtstack.taier.common.exception.DtCenterDefException;
import com.dtstack.taier.common.exception.TaierDefineException;
import com.dtstack.taier.common.lang.web.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller模板类
 *
 * @param <T> 前端返回类型
 */
public abstract class APITemplate<T> {
    /**
     * 业务执行时间默认阀值(10秒)
     */
    public static final long THRESHOLD = 10_000L;
    protected Logger log = LoggerFactory.getLogger(getClass());

    protected APITemplate() {

    }

    /**
     * 业务入参合法性校验
     *
     * @throws IllegalArgumentException 参数校验异常
     */
    protected void checkParams() throws IllegalArgumentException {

    }

    /**
     * 业务方法执行入口,业务异常时抛出BizException
     *
     * @return 业务执行结果信息返回
     * @throws TaierDefineException 业务执行异常
     */
    protected abstract T process() throws TaierDefineException;

    /**
     * 后置处理器
     */
    protected void afterProcess() {

    }

    /**
     * 业务处理成功回调函数
     */
    protected void onSuccess() {

    }

    /**
     * 业务处理执行异常回调
     *
     * @param e 业务执行异常
     */
    protected void onError(Throwable e) {
        if (log.isErrorEnabled()) {
            log.error("API Error while execute ", e);
        }
    }

    /**
     * 执行业务,返回统一的JSON格式(R)
     *
     * @return 业务执行返回R格式
     */
    public R<T> execute() {
        try {
            checkParams();
        } catch (IllegalArgumentException e) {
            if (log.isInfoEnabled()) {
                log.info("param check error:", e);
            }
            throw e;
        } catch (TaierDefineException e) {
            onError(e);
            return R.fail(e.getErrorCode().getCode(), e.getMessage());
        } catch (DtCenterDefException ex) {
            onError(ex);
            return R.fail(ex.getCode(), ex.getMessage());
        }
        long sTime = System.currentTimeMillis();
        try {
            T r = process();
            onSuccess();
            return R.ok(r);
        } catch (TaierDefineException be) {
            onError(be);
            return R.fail(be.getErrorCode().getCode(), be.getMessage());
        } catch (DtCenterDefException ex) {
            onError(ex);
            return R.fail(ex.getCode(), ex.getMessage());
        } catch (Throwable e) {
            onError(e);
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException(e);
        } finally {
            afterProcess();
            long costTime = System.currentTimeMillis() - sTime;
            if (costTime > THRESHOLD) {
                if (log.isWarnEnabled()) {
                    log.warn("this api method execute cost too long time, please optimize it:{} ms", costTime);
                }
            }
        }
    }


}