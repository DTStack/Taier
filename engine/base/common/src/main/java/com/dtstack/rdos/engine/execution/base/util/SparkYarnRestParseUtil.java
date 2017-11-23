package com.dtstack.rdos.engine.execution.base.util;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * Reason:
 * Date: 2017/11/23
 * Company: www.dtstack.com
 * @ahthor xuchao
 */

public class SparkYarnRestParseUtil {

    private static final Logger logger = LoggerFactory.getLogger(SparkYarnRestParseUtil.class);

    public static final String APPLICATION_WS_FORMAT = "/ws/v1/cluster/apps/%s";

    public static final String APPLICATION_LOG_URL_FORMAT = "%s/stderr/?start=0";

    private final static ObjectMapper OBJ_MAPPER = new ObjectMapper();

    public static String getContainerLogURL(String log) throws IOException {
        Map<String, Object> info = OBJ_MAPPER.readValue(log, Map.class);
        if(!info.containsKey("app")){
            return null;
        }

        Map<String, Object> appInfo = (Map<String, Object>) info.get("app");
        if(appInfo.containsKey("amContainerLogs")){
            return null;
        }

        String amContainerLogs = (String) appInfo.get("amContainerLogs");
        return amContainerLogs;
    }

    public static void main(String[] args) throws IOException {
        String str = "{\"app\":{\"id\":\"application_1511354731320_0010\",\"user\":\"admin\",\"name\":\"P_xc_first_test_2017_11_23_1-xc_first_test-20171121000000\",\"queue\":\"default\",\"state\":\"FINISHED\",\"finalStatus\":\"SUCCEEDED\",\"progress\":100.0,\"trackingUI\":\"History\",\"trackingUrl\":\"http://node03:8088/proxy/application_1511354731320_0010/\",\"diagnostics\":\"\",\"clusterId\":1511354731320,\"applicationType\":\"SPARK\",\"applicationTags\":\"\",\"startedTime\":1511423516742,\"finishedTime\":1511423537077,\"elapsedTime\":20335,\"amContainerLogs\":\"http://node03:8042/node/containerlogs/container_e01_1511354731320_0010_01_000001/admin\",\"amHostHttpAddress\":\"node03:8042\",\"allocatedMB\":-1,\"allocatedVCores\":-1,\"runningContainers\":-1,\"memorySeconds\":69207,\"vcoreSeconds\":56,\"preemptedResourceMB\":0,\"preemptedResourceVCores\":0,\"numNonAMContainerPreempted\":0,\"numAMContainerPreempted\":0}}";
        System.out.println(getContainerLogURL(str));
    }


}
