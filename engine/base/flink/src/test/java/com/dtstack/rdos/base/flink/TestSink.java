package com.dtstack.rdos.base.flink;

import com.dtstack.rdos.engine.execution.base.operator.stream.StreamCreateResultOperator;
import com.dtstack.rdos.engine.execution.flink120.sink.hbase.RdosHbaseSink;
import com.dtstack.rdos.engine.execution.flink120.sink.hdfs.RdosHdfsSink;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.java.StreamTableEnvironment;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by softfly on 17/6/30.
 */
public class TestSink {

    public static void testHbaseSink() throws Exception {
        //List<String> lines = IOUtils.readLines(new FileInputStream("/Users/softfly/Desktop/expr/sqlt.txt"));
        //String content = StringUtils.join("", lines);
        //System.out.println(content);
        String content = "CREATE RESULT TABLE test1(col1 STRING,col2 INT,col3 TIMESTAMP) WITH (type='datahub',projectName='dtstack',host='172.16.1.151',port='2181',parent='/hbase137',columnFamily='cf1[col1:col2] cf2[col3]',rowkey='col1:col2:col3')";
        StreamCreateResultOperator operator = new StreamCreateResultOperator();
        operator.createOperator(content);
        RdosHbaseSink rdosHbaseSink = new RdosHbaseSink();
        rdosHbaseSink.genStreamSink(operator);

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment tableEnv = TableEnvironment.getTableEnvironment(env);

        List<String> list = new ArrayList<>();
        list.add("xxxxx#2222#444");
        list.add("yyyyy#1111#444");
        DataStream<Tuple3<String, Integer, String>> ds = env.fromCollection(list)
                .map(new MapFunction<String, Tuple3<String, Integer, String>>() {
                    @Override
                    public Tuple3<String, Integer, String> map(String s) throws Exception {
                        String[] arr = s.split("#");
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        java.util.Date d1 = new java.util.Date();
                        Timestamp ts = new Timestamp(d1.getTime());
//                        Date d2 = new Date(d1.getTime());
//                        System.out.println("input_date=" + format.format(d2));
                        return new Tuple3<String, Integer, String>(arr[0], Integer.valueOf(arr[1]), format.format(ts));
                    }
                });

        Table table = tableEnv.fromDataStream(ds, "col1,col2,col3 ");
        table.writeToSink(rdosHbaseSink);

        env.execute();

    }

    public static void testHdfsSink() throws Exception {
        String content = "CREATE RESULT TABLE sb4(col1 STRING,col2 INT,col3 INT,col4 INT) WITH (type='datahub',projectName='dtstack',defaultFS='hdfs://172.16.1.151:9000',path='/hyf',fileType='text',delimiter=':')";
        StreamCreateResultOperator operator = new StreamCreateResultOperator();
        operator.createOperator(content);
        RdosHdfsSink rdosHdfsSink = new RdosHdfsSink();
        rdosHdfsSink.genStreamSink(operator);

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment tableEnv = TableEnvironment.getTableEnvironment(env);

        List<String> list = new ArrayList<>();
        list.add("xxxxx#2222#444");
        list.add("yyyyy#1111#444");
        DataStream<Tuple3<String, Integer, Integer>> ds = env.fromCollection(list)
                .map(new MapFunction<String, Tuple3<String, Integer, Integer>>() {
                    @Override
                    public Tuple3<String, Integer, Integer> map(String s) throws Exception {
                        String[] arr = s.split("#");
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        java.util.Date d1 = new java.util.Date();
                        Timestamp ts = new Timestamp(d1.getTime());
//                        Date d2 = new Date(d1.getTime());
//                        System.out.println("input_date=" + format.format(d2));
                        //return new Tuple3<String, Integer, String>(arr[0], Integer.valueOf(arr[1]), format.format(ts));
                        return new Tuple3<String, Integer, Integer>(arr[0], Integer.valueOf(arr[1]), Integer.valueOf(arr[2]));
                    }
                });

        Table table = tableEnv.fromDataStream(ds, "col1,col2,col4");
        table.writeToSink(rdosHdfsSink);

        env.execute();
    }

    public static void main(String[] args) throws Exception {
        //testHbaseSink();
        testHdfsSink();
    }

}
