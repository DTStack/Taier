package com.dtstack.engine.flink.util;

import com.alibaba.fastjson.JSON;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.flink.entity.LatencyMarkerInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.runtime.jobgraph.JobEdge;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobgraph.JobVertex;
import org.apache.flink.util.AbstractID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


/**
 * @author yuebai
 * @date 2020-09-08
 */
public class JobGraphBuildUtil {

    private static final Logger logger = LoggerFactory.getLogger(JobGraphBuildUtil.class);

    private static final String SPLIT = "->";
    private static final String matchPattern = "(.*)->\\s\\((.*\\w)\\,(\\sSourceConversion.*)\\)";


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
                String jobVertexName = jobVertex.getName();
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


                List<String> subJobVertexNames = parseOperatorName(jobVertexName);

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
     * 解析OPERATOR NAME
     * @param name
     * @return
     */
    private static List<String> parseOperatorName(String name) {

        Pattern pattern = Pattern.compile(matchPattern);
        Matcher matcher = pattern.matcher(name);

        List<String> names = new ArrayList<>();

        if (matcher.find()) {
            for (int i = 1 ; i <= matcher.groupCount() ; i ++ ){
                names.addAll(splitName(matcher.group(i)));
            }
        } else {
            names.addAll(splitName(name));
        }

        return names;
    }

    private static List<String> splitName(String name) {
        String[] name_arr = name.split(SPLIT);
        if (name_arr != null) {
            return Arrays.asList(name_arr);
        }
        return null;
    }

    private static void checkSize(List<String> ids, List<String> names) {
        if (ids.size() != names.size()) {
            throw new RdosDefineException("id's size : [" + ids.size() + "] is not equal names's size : [" + names.size() + "]");
        }
    }

}
