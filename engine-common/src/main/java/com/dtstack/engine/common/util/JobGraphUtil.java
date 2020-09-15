package com.dtstack.engine.common.util;

import com.alibaba.fastjson.JSONObject;

import java.util.Collection;

/**
 * @author yuebai
 * @date 2020-09-08
 */
public class JobGraphUtil {

    /**
     * {
     *     "jobId":"6cdc5bb954874d922eaee11a8e7b5dd5",
     *     "taskVertices":[
     *         {
     *             "output":[
     *                 "591df72d6cd956a5de0e6d6a2e993000"
     *             ],
     *             "inputs":[
     *
     *             ],
     *             "subJobVertices":[
     *                 {
     *                     "name":"Source: Collection Source ",
     *                     "id":"6cdc5bb954874d922eaee11a8e7b5dd5"
     *                 },
     *                 {
     *                     "name":" Map",
     *                     "id":"eb99017e0f9125fa6648bf56123bdcf7"
     *                 }
     *             ],
     *             "jobVertexId":"6cdc5bb954874d922eaee11a8e7b5dd5",
     *             "jobVertexName":"Source: Collection Source -> Map"
     *         },
     *         {
     *             "output":[
     *                 "df3028bbd23b4fb792b8e0ba17bac080"
     *             ],
     *             "inputs":[
     *
     *             ],
     *             "subJobVertices":[
     *                 {
     *                     "name":"Source: Collection Source ",
     *                     "id":"cbc357ccb763df2852fee8c4fc7d55f2"
     *                 },
     *                 {
     *                     "name":" Map",
     *                     "id":"2be4fe38b4ce63aa5bffc06b65e24e03"
     *                 }
     *             ],
     *             "jobVertexId":"cbc357ccb763df2852fee8c4fc7d55f2",
     *             "jobVertexName":"Source: Collection Source -> Map"
     *         },
     *         {
     *             "output":[
     *
     *             ],
     *             "inputs":[
     *                 "df3028bbd23b4fb792b8e0ba17bac080",
     *                 "591df72d6cd956a5de0e6d6a2e993000"
     *             ],
     *             "subJobVertices":[
     *                 {
     *                     "name":"Window(TumblingEventTimeWindows(2000), EventTimeTrigger, CoGroupWindowFunction) ",
     *                     "id":"8b481b930a189b6b1762a9d95a61ada1"
     *                 },
     *                 {
     *                     "name":" Sink: Print to Std. Out",
     *                     "id":"1005f03a37a9d727782a1597ca596a1c"
     *                 }
     *             ],
     *             "jobVertexId":"8b481b930a189b6b1762a9d95a61ada1",
     *             "jobVertexName":"Window(TumblingEventTimeWindows(2000), EventTimeTrigger, CoGroupWindowFunction) -> Sink: Print to Std. Out"
     *         }
     *     ]
     * }
     * @param applicationId
     * @param latencyMarkerInfo
     * @return
     */
    public static String formatJSON(String applicationId,String latencyMarkerInfo){
        JSONObject data = new JSONObject();
        data.put("jobId",applicationId);
        JSONObject markInfoJSON = JSONObject.parseObject(latencyMarkerInfo);
        Collection<Object> values = markInfoJSON.values();
        data.put("taskVertices",values);
        data.put("startTime",System.currentTimeMillis());
        return data.toJSONString();
    }

    public static void main(String[] args) {
        System.out.println(JobGraphUtil.formatJSON("6cdc5bb954874d922eaee11a8e7b5dd5","{\"6cdc5bb954874d922eaee11a8e7b5dd5\":{\"inputs\":[],\"jobVertexId\":\"6cdc5bb954874d922eaee11a8e7b5dd5\",\"jobVertexName\":\"Source: Collection Source -> Map\",\"output\":[\"591df72d6cd956a5de0e6d6a2e993000\"],\"subJobVertices\":[{\"id\":\"6cdc5bb954874d922eaee11a8e7b5dd5\",\"name\":\"Source: Collection Source \"},{\"id\":\"eb99017e0f9125fa6648bf56123bdcf7\",\"name\":\" Map\"}]},\"cbc357ccb763df2852fee8c4fc7d55f2\":{\"inputs\":[],\"jobVertexId\":\"cbc357ccb763df2852fee8c4fc7d55f2\",\"jobVertexName\":\"Source: Collection Source -> Map\",\"output\":[\"df3028bbd23b4fb792b8e0ba17bac080\"],\"subJobVertices\":[{\"id\":\"cbc357ccb763df2852fee8c4fc7d55f2\",\"name\":\"Source: Collection Source \"},{\"id\":\"2be4fe38b4ce63aa5bffc06b65e24e03\",\"name\":\" Map\"}]},\"8b481b930a189b6b1762a9d95a61ada1\":{\"inputs\":[\"df3028bbd23b4fb792b8e0ba17bac080\",\"591df72d6cd956a5de0e6d6a2e993000\"],\"jobVertexId\":\"8b481b930a189b6b1762a9d95a61ada1\",\"jobVertexName\":\"Window(TumblingEventTimeWindows(2000), EventTimeTrigger, CoGroupWindowFunction) -> Sink: Print to Std. Out\",\"output\":[],\"subJobVertices\":[{\"id\":\"8b481b930a189b6b1762a9d95a61ada1\",\"name\":\"Window(TumblingEventTimeWindows(2000), EventTimeTrigger, CoGroupWindowFunction) \"},{\"id\":\"1005f03a37a9d727782a1597ca596a1c\",\"name\":\" Sink: Print to Std. Out\"}]}}"));
    }
}
