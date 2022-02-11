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

package com.dtstack.taier.pluginapi.logstore;

import java.util.Collection;

/**
 * Created by sishu.yss on 2018/4/17.
 */
public abstract class AbstractLogStore {

    public abstract  int insert(String jobId, String jobInfo, int status);

    public abstract  int updateStatus(String jobId, int status);

    public abstract  void updateModifyTime(Collection<String> jobIds);

    public abstract void updateErrorLog(String jobId, String errorLog);

    public abstract Integer getStatusByJobId(String jobId);

    public abstract String getLogByJobId(String jobId);

    public abstract void timeOutDeal();

    public abstract void clearJob();

    }
