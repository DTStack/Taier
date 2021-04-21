package com.dtstack.engine.learning;

import com.alibaba.fastjson.JSON;
import com.dtstack.engine.api.pojo.ParamAction;
import com.dtstack.engine.common.JobClient;
import org.junit.Test;

/**
 * @description:
 * @program: engine-all
 * @author: lany
 * @create: 2021/04/01 10:34
 */
public class DataUtil {

    public static String getPluginInfo() throws Exception {
        return getJobClient().getPluginInfo();
    }

    public static JobClient getJobClient() throws Exception {
        ParamAction paramAction = JSON.parseObject(getRequestJson(), ParamAction.class);
        return new JobClient(paramAction);
    }

    private static String getRequestJson() {
        return "{\n" +
                "  \"appType\": 8,\n" +
                "  \"computeType\": 1,\n" +
                "  \"cycTime\": \"20210401101316\",\n" +
                "  \"engineType\": \"learning\",\n" +
                "  \"exeArgs\": \"--files hdfs://172.16.100.170:8020/dtInsight/task/tensorflow_25_85_TensorFlow_1617243196789.py --python-version 3.x --launch-cmd cHl0aG9uIHRlbnNvcmZsb3dfMjVfODVfVGVuc29yRmxvd18xNjE3MjQzMTk2Nzg5LnB5ICU3QiUyMnJlbW90ZVBhdGglMjIlM0ElMjIlMkZkdEluc2lnaHQlMkZzY2llbmNlJTJGbm90ZWJvb2tfbW9kZWwlMkZ0bXAlMkY1OSUyRjIwMjEwNDAxMTAxMzE2JTIyJTJDJTIyaGFkb29wX2hvc3RzJTIyJTNBJTIyaGRmcyUzQSUyRiUyRjE3Mi4xNi4xMDAuMTcwJTNBODAyMCUyMiUyQyUyMmF1dGglMjIlM0ElN0IlMjJtb2RlJTIyJTNBJTIya2VyYmVyb3MlMjIlMkMlMjJwcmluY2lwYWwlMjIlM0ElMjJoZGZzJTJGZW5nLWNkaDElNDBEVFNUQUNLLkNPTSUyMiUyQyUyMmhpdmVQcmluY2lwYWwlMjIlM0ElMjJoaXZlJTJGZW5nLWNkaDMlNDBEVFNUQUNLLkNPTSUyMiUyQyUyMmhkZnNIQUNvbmZpZyUyMiUzQSU3QiUyMmRmcy5uYW1lbm9kZS5zZXJ2aWNlcnBjLWFkZHJlc3MlMjIlM0ElMjIxNzIuMTYuMTAwLjE3MCUzQTgwMjIlMjIlN0QlN0QlMkMlMjJsb2NhbFBhdGglMjIlM0ElMjIlMkZob21lJTJGYWRtaW4lMkZhcHAlMkZkdC1jZW50ZXItZGF0YVNjaWVuY2UlMkZ1cGxvYWQlMkY1OSUyRjIwMjEwNDAxMTAxMzE2JTIyJTJDJTIyaGFkb29wX3VzZXJuYW1lJTIyJTNBJTIyYWRtaW4lMjIlN0Qg --app-type TENSORFLOW --app-name TensorFlow\",\n" +
                "  \"generateTime\": 1617243203296,\n" +
                "  \"isFailRetry\": false,\n" +
                "  \"lackingCount\": 0,\n" +
                "  \"maxRetryNum\": 0,\n" +
                "  \"name\": \"runJob_TensorFlow_20210401101316\",\n" +
                "  \"priority\": 0,\n" +
                "  \"requestStart\": 0,\n" +
                "  \"sqlText\": \"ADD JAR WITH hdfs://172.16.100.170:8020/dtInsight/task/tensorflow_25_85_TensorFlow_1617243196789.py AS %s;\",\n" +
                "  \"stopJobId\": 0,\n" +
                "  \"submitExpiredTime\": 0,\n" +
                "  \"taskId\": \"9044a30a\",\n" +
                "  \"taskParams\": \"worker.memory=1024m\\nworker.num=1\\nworker.cores=1\\njob.priority=10\\n\",\n" +
                "  \"taskSourceId\": 59,\n" +
                "  \"taskType\": 3,\n" +
                "  \"tenantId\": 431\n" +
                "}";
    }
}
