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

package com.dtstack.taier.common.util;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.pluginapi.enums.ComputeType;
import org.apache.commons.lang3.StringUtils;

/**
 * @author yuebai
 * @date 2020-09-08
 */
public class JobGraphUtil {

    /**
     * {
     * "jobId":"6cdc5bb954874d922eaee11a8e7b5dd5",
     * "taskVertices":[
     * {
     * "output":[
     * "591df72d6cd956a5de0e6d6a2e993000"
     * ],
     * "inputs":[
     * <p>
     * ],
     * "subJobVertices":[
     * {
     * "name":"Source: Collection Source ",
     * "id":"6cdc5bb954874d922eaee11a8e7b5dd5"
     * },
     * {
     * "name":" Map",
     * "id":"eb99017e0f9125fa6648bf56123bdcf7"
     * }
     * ],
     * "jobVertexId":"6cdc5bb954874d922eaee11a8e7b5dd5",
     * "jobVertexName":"Source: Collection Source -> Map"
     * },
     * {
     * "output":[
     * "df3028bbd23b4fb792b8e0ba17bac080"
     * ],
     * "inputs":[
     * <p>
     * ],
     * "subJobVertices":[
     * {
     * "name":"Source: Collection Source ",
     * "id":"cbc357ccb763df2852fee8c4fc7d55f2"
     * },
     * {
     * "name":" Map",
     * "id":"2be4fe38b4ce63aa5bffc06b65e24e03"
     * }
     * ],
     * "jobVertexId":"cbc357ccb763df2852fee8c4fc7d55f2",
     * "jobVertexName":"Source: Collection Source -> Map"
     * },
     * {
     * "output":[
     * <p>
     * ],
     * "inputs":[
     * "df3028bbd23b4fb792b8e0ba17bac080",
     * "591df72d6cd956a5de0e6d6a2e993000"
     * ],
     * "subJobVertices":[
     * {
     * "name":"Window(TumblingEventTimeWindows(2000), EventTimeTrigger, CoGroupWindowFunction) ",
     * "id":"8b481b930a189b6b1762a9d95a61ada1"
     * },
     * {
     * "name":" Sink: Print to Std. Out",
     * "id":"1005f03a37a9d727782a1597ca596a1c"
     * }
     * ],
     * "jobVertexId":"8b481b930a189b6b1762a9d95a61ada1",
     * "jobVertexName":"Window(TumblingEventTimeWindows(2000), EventTimeTrigger, CoGroupWindowFunction) -> Sink: Print to Std. Out"
     * }
     * ]
     * }
     *
     * @param engineTaskId
     * @param latencyMarkerInfo
     * @param computeType       流计算才需存储jobGraph
     * @return
     */
    public static String formatJSON(String engineTaskId, String latencyMarkerInfo, ComputeType computeType) {
        if (!ComputeType.STREAM.equals(computeType)) {
            return null;
        }
        if (StringUtils.isBlank(latencyMarkerInfo)) {
            return null;
        }
        JSONObject data = new JSONObject();
        data.put("jobId", engineTaskId);
        JSONObject markInfoJSON = JSONObject.parseObject(latencyMarkerInfo);
        if (null != markInfoJSON) {
            data.put("taskVertices", markInfoJSON.values());
        }
        data.put("startTime", System.currentTimeMillis());
        return data.toJSONString();
    }

}
