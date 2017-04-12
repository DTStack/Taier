package com.dtstack.rdos.engine.entrance.test;

import com.dtstack.rdos.engine.entrance.sql.SqlParser;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.SubmitContainer;
import com.dtstack.rdos.engine.execution.base.enumeration.ComputeType;
import com.dtstack.rdos.engine.execution.base.enumeration.EJobType;
import com.dtstack.rdos.engine.execution.base.operator.Operator;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Reason:
 * Date: 2017/3/6
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
 */

public class TestRunSql {

    @Test
    public void testSubmitSql() throws Exception {
        Map<String, Object> prop = new HashMap<>();
        prop.put("slots", 1);
        prop.put("engineZkAddress", "172.16.1.151:2181");
        prop.put("jarTmpDir", "D:\\tmp");

        SubmitContainer submitContainer = SubmitContainer.createSubmitContainer(prop);

        String sql = "ADDJAR ADD JAR WITH http://114.55.63.129/flinktest-1.0-SNAPSHOT.jar;\n" +
                "CREATE SOURCE TABLE MyTable(\n" +
                "message STRING) WITH (\n" +
                "type='KAFKA09',\n" +
                "bootstrapServers='172.16.1.151:9092',\n" +
                "offsetReset='earliest',\n" +
                "topic='dt_distribute_log'\n" +
                ");\n" +
                "CREATE SCALA FUNCTION hashCode WITH com.xc.udf.MyHashCode;\n" +
                "select message, hashCode(message) as hashcode from MyTable;\n" +
                "CREATE RESULT TABLE MyResult(\n" +
                "message STRING, hashcode int) WITH (\n" +
                "type='mysql',\n" +
                "dbURL='jdbc:mysql://172.16.1.203:3306/flink_test',\n" +
                "userName='dtstack_xc',\n" +
                "password='dtstack_xc',\n" +
                "tableName='flink_test'\n" +
                ");";
        List<Operator> operators = SqlParser.parserSql(sql);
        JobClient jobClient = new JobClient();
        jobClient.setJobType(EJobType.SQL);
        jobClient.setTaskId("test_sql_job");
        jobClient.setJobName("test_sql_job");
        jobClient.setComputeType(ComputeType.STREAM);
        jobClient.addOperators(operators);

        jobClient.submit();

        System.out.println("---------wait----------");
    }
}
