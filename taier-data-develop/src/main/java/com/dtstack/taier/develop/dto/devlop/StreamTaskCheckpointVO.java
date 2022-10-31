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

package com.dtstack.taier.develop.dto.devlop;

public class StreamTaskCheckpointVO {

    private static String SPLIT = "_";


    /**
     * 构成: rdos_stream_task_checkpoint的记录id + checkpoint内容的具体id
     */
    private String id;


    private Long time;

    /**
     * 其实应该服务器重新查询,太麻烦了暂时直接让客户端回传
     */
    private String externalPath;

    public StreamTaskCheckpointVO(Long dbId, Long cpId, Long time, String externalPath) {
        this.id = dbId + SPLIT + cpId;
        this.time = time;
        this.externalPath = externalPath;
    }

    public StreamTaskCheckpointVO() {
    }

    public static String getSPLIT() {
        return SPLIT;
    }

    public static void setSPLIT(String SPLIT) {
        StreamTaskCheckpointVO.SPLIT = SPLIT;
    }

    public String getId() {
        return id;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }


    public void setId(String id) {
        this.id = id;
    }

    public String getExternalPath() {
        return externalPath;
    }

    public void setExternalPath(String externalPath) {
        this.externalPath = externalPath;
    }
}
