package com.dtstack.engine.flink.util;

import com.alibaba.fastjson.JSON;
import com.dtstack.engine.flink.entity.LatencyMarkerInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobgraph.JobVertex;

import java.util.Arrays;
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


    public static String buildLatencyMarker(JobGraph jobGraph) {
        if(null == jobGraph){
            return null;
        }
        final String reg = "\\s\\([^)]+\\)$";
        Map<String, LatencyMarkerInfo> latencyMarker = Maps.newLinkedHashMap();

        Iterable<JobVertex> iterable = () -> jobGraph.getVertices().iterator();
        List<JobVertex> jobVertices = StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toList());
        Collections.reverse(jobVertices);

        jobVertices.stream().map(jobVertex -> {
            String jobVertexID = jobVertex.getID().toString();
            String JobVertexName = jobVertex.getName();
            List<String> inputs = jobVertex.getInputs()
                    .stream()
                    .map(e -> e.getSourceId().toHexString())
                    .collect(Collectors.toList());

            List<String> output = jobVertex.getProducedDataSets()
                    .stream()
                    .map(e -> e.getId().toHexString())
                    .collect(Collectors.toList());

            String[] subJobVertexNames = JobVertexName.split("->");
            subJobVertexNames[subJobVertexNames.length - 1] = subJobVertexNames[subJobVertexNames.length - 1].replaceAll(reg, "");

            List<String> subJobVertexIDs = Lists.reverse(jobVertex.getOperatorIDs()
                    .stream()
                    .map(e -> e.toString())
                    .collect(Collectors.toList()));

            List<Tuple2> subJobVertices = zipVertexIDAndName(subJobVertexIDs, Arrays.asList(subJobVertexNames));

            LatencyMarkerInfo latencyMarkerInfo = LatencyMarkerInfo.builder()
                    .setJobVertexName(JobVertexName)
                    .setJobVertexId(jobVertexID)
                    .setInputs(inputs)
                    .setOutput(output)
                    .setParallelism(jobVertex.getParallelism())
                    .setMaxParallelism(jobVertex.getMaxParallelism())
                    .setSubJobVertex(subJobVertices)
                    .build();

            latencyMarker.put(jobVertexID, latencyMarkerInfo);
            return null;
        }).count();
        return JSON.toJSONString(latencyMarker);
    }

    private static List<Tuple2> zipVertexIDAndName(List<String> subJobVertexIDs, List<String> subJobVertexNames) {
        List<Tuple2> subJobVertices = Lists.newArrayList();
        for (int i = 0; i < subJobVertexIDs.size(); i++) {
            subJobVertices.add(new Tuple2(subJobVertexIDs.get(i), subJobVertexNames.get(i)));
        }
        return subJobVertices;
    }


}
