package com.dtstack.taier.flink.util;

import com.alibaba.fastjson.JSON;
import com.dtstack.taier.flink.info.LatencyMarkerInfo;
import com.dtstack.taier.pluginapi.exception.PluginDefineException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.runtime.jobgraph.JobEdge;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobgraph.JobVertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * build graph for display
 *
 * @author yuebai
 * @see{https://dtstack.yuque.com/rd-center/sm6war/zszvc2}
 * @date 2020-09-08
 */
public class JobGraphBuildUtil {

    private static final Logger logger = LoggerFactory.getLogger(JobGraphBuildUtil.class);

    public static String buildLatencyMarker(JobGraph jobGraph) {
        if (null == jobGraph) {
            return null;
        }
        try {
            Map<String, LatencyMarkerInfo> latencyMarker = Maps.newLinkedHashMap();

            List<JobVertex> jobVertices = Arrays.asList(jobGraph.getVerticesAsArray());
            Collections.reverse(Arrays.asList(jobGraph.getVerticesAsArray()));

            jobVertices.forEach((JobVertex jobVertex) -> {

                List<String> subJobVertexNames = jobGraph.getVertexOperatorNames()
                        .get(jobVertex.getID());
                // keep the same order as subJobVertexIDs
                Collections.reverse(subJobVertexNames);

                List<String> subJobVertexIDs = Lists.reverse(jobVertex.getOperatorIDs()
                        .stream()
                        .map((operatorIDPair) -> operatorIDPair.getGeneratedOperatorID().toString())
                        .collect(Collectors.toList()));

                // 校验解析的id与名称是否在数量上相等，如果不等，则解析失败
                checkSize(subJobVertexIDs, subJobVertexNames);
                List<Tuple2<String, String>> subJobVertices = zipVertexIDAndName(subJobVertexIDs, subJobVertexNames);

                String jobVertexID = jobVertex.getID().toString();
                String jobVertexName = handleName(jobVertex.getName());
                List<String> inputs = jobVertex.getInputs()
                        .stream().map(edge -> edge.getSourceId().toHexString())
                        .collect(Collectors.toList());
                Map<String, String> inputShipStrategyNames = jobVertex.getInputs()
                        .stream()
                        .collect(Collectors.toMap(jobEdge -> jobEdge.getSourceId().toHexString(), JobEdge::getShipStrategyName));
                List<String> outputs = jobVertex.getProducedDataSets()
                        .stream().map(intermediateDataSet -> intermediateDataSet.getId().toHexString())
                        .collect(Collectors.toList());

                LatencyMarkerInfo latencyMarkerInfo = LatencyMarkerInfo.builder()
                        .setJobVertexName(jobVertexName)
                        .setJobVertexId(jobVertexID)
                        .setInputs(inputs)
                        .setOutput(outputs)
                        .setParallelism(jobVertex.getParallelism())
                        .setMaxParallelism(jobVertex.getMaxParallelism())
                        .setSubJobVertex(subJobVertices)
                        .setInputShipStrategyName(inputShipStrategyNames)
                        .build();

                latencyMarker.put(jobVertexID, latencyMarkerInfo);
            });
            return JSON.toJSONString(latencyMarker);
        } catch (Exception e) {
            logger.info("build LatencyMarker failed.", e);
            return null;
        }
    }

    private static List<Tuple2<String, String>> zipVertexIDAndName(List<String> subJobVertexIDs, List<String> subJobVertexNames) {
        List<Tuple2<String, String>> subJobVertices = Lists.newArrayList();
        for (int i = 0; i < subJobVertexIDs.size(); i++) {
            subJobVertices.add(new Tuple2<>(subJobVertexIDs.get(i), subJobVertexNames.get(i)));
        }
        return subJobVertices;
    }

    /**
     * todo: remove magic number
     * substring if jobVertexName's length exceeded 203
     */
    private static String handleName(String jobVertexName) {
        if (jobVertexName.length() >= 203) {
            return String.format("%s...%s", jobVertexName.substring(0, 100), jobVertexName.substring(jobVertexName.length() - 100));
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
