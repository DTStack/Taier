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

package com.dtstack.engine.dao;

import com.dtstack.engine.domain.EngineJobRetry;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/02/12
 */
public interface EngineJobRetryDao {

    void insert(EngineJobRetry engineJobRetry);

    List<EngineJobRetry> listJobRetryByJobId(@Param("jobId") String jobId);

    EngineJobRetry getJobRetryByJobId(@Param("jobId") String jobId, @Param("retryNum") int retryNum);

    String getRetryTaskParams(@Param("jobId")String jobId, @Param("retryNum") int retryNum);

    void removeByJobId(@Param("jobId")String jobId);

    void updateEngineLog(@Param("id") long id, @Param("engineLog") String engineLog);
}
