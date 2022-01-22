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

package com.dtstack.taiga.flink.util;

import com.alibaba.fastjson.JSON;
import com.dtstack.taiga.flink.entity.LatencyMarkerInfo;
import com.dtstack.taiga.pluginapi.exception.PluginDefineException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.runtime.jobgraph.JobEdge;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobgraph.JobVertex;
import org.apache.flink.util.AbstractID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


/**
 * @author yuebai
 * @date 2020-09-08
 */
public class JobGraphBuildUtil {

    private static final Logger logger = LoggerFactory.getLogger(JobGraphBuildUtil.class);

    public static String buildLatencyMarker(JobGraph jobGraph) {
        if(null == jobGraph){
            return null;
        }
        try {
            Map<String, LatencyMarkerInfo> latencyMarker = Maps.newLinkedHashMap();

            Iterable<JobVertex> iterable = () -> jobGraph.getVertices().iterator();
            List<JobVertex> jobVertices = StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toList());
            Collections.reverse(jobVertices);

            jobVertices.stream().map(jobVertex -> {
                String jobVertexID = jobVertex.getID().toString();
                String jobVertexName = handleName(jobVertex.getName());
                List<String> inputs = jobVertex.getInputs()
                        .stream()
                        .map(e -> e.getSourceId().toHexString())
                        .collect(Collectors.toList());

                Map<String, String> inputShipStrategyName = jobVertex.getInputs()
                        .stream()
                        .collect(Collectors.toMap(e -> e.getSourceId().toHexString(), JobEdge::getShipStrategyName));

                List<String> output = jobVertex.getProducedDataSets()
                        .stream()
                        .map(e -> e.getId().toHexString())
                        .collect(Collectors.toList());


                List<String> subJobVertexNames = jobGraph.getVertexOperatorNames()
                        .get(jobVertex.getID());
                // keep the same order as subJobVertexIDs
                Collections.reverse(subJobVertexNames);

                List<String> subJobVertexIDs = Lists.reverse(jobVertex.getOperatorIDs()
                        .stream()
                        .map(AbstractID::toString)
                        .collect(Collectors.toList()));

                // 校验解析的id与名称是否在数量上相等，如果不等，则解析失败
                checkSize(subJobVertexIDs, subJobVertexNames);

                List<Tuple2> subJobVertices = zipVertexIDAndName(subJobVertexIDs, subJobVertexNames);

                LatencyMarkerInfo latencyMarkerInfo = LatencyMarkerInfo.builder()
                        .setJobVertexName(jobVertexName)
                        .setJobVertexId(jobVertexID)
                        .setInputs(inputs)
                        .setOutput(output)
                        .setParallelism(jobVertex.getParallelism())
                        .setMaxParallelism(jobVertex.getMaxParallelism())
                        .setSubJobVertex(subJobVertices)
                        .setInputShipStrategyName(inputShipStrategyName)
                        .build();

                latencyMarker.put(jobVertexID, latencyMarkerInfo);
                return null;
            }).count();
            return JSON.toJSONString(latencyMarker);
        } catch (Exception e) {
            logger.info("buildLatencyMarker happens error. {}", e);
            return null;
        }
    }

    private static List<Tuple2> zipVertexIDAndName(List<String> subJobVertexIDs, List<String> subJobVertexNames) {
        List<Tuple2> subJobVertices = Lists.newArrayList();
        for (int i = 0; i < subJobVertexIDs.size(); i++) {
            subJobVertices.add(new Tuple2(subJobVertexIDs.get(i), subJobVertexNames.get(i)));
        }
        return subJobVertices;
    }

    /**
     * substring if jobVertexName's length exceeded 203
     * @param jobVertexName
     * @return
     */
    private static String handleName(String jobVertexName) {
        if (jobVertexName.length() >= 203) {
            return String.format("%s...%s",jobVertexName.substring(0, 100), jobVertexName.substring(jobVertexName.length()-100));
        } else {
            return jobVertexName;
        }
    }

    private static void checkSize(List<String> ids, List<String> names) {
        if (names == null) {
            throw new PluginDefineException("Error! This jobVertex doesn't have operators.");
        }

        if (ids.size() != names.size()) {
            throw new PluginDefineException("id's size : [" + ids.size() + "] is not equal names's size : [" + names.size() + "]");
        }
    }

}
