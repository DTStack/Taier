package com.dtstack.rdos.base.flink;

import com.dtstack.rdos.engine.execution.base.operator.stream.CreateResultOperator;
import com.dtstack.rdos.engine.execution.flink120.sink.hbase.RdosHbaseSink;
import com.google.common.collect.Maps;
import oi.thekraken.grok.api.Grok;
import org.apache.commons.io.IOUtils;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.java.StreamTableEnvironment;
import org.apache.hadoop.util.StringUtils;

import java.io.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by softfly on 17/6/30.
 */
public class TestRdosHbaseSink {

    private static String patternFile = "pattern";

    private static Map<String,Grok> groks = Maps.newConcurrentMap();

    public static void parseAndExecute() throws Exception {
        List<String> lines = IOUtils.readLines(new FileInputStream("/Users/softfly/Desktop/expr/sqlt.txt"));
        String content = StringUtils.join("", lines);
        System.out.println(content);
        CreateResultOperator operator = new CreateResultOperator();
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

        Table table = tableEnv.fromDataStream(ds, "col1,col2,col3");
        table.writeToSink(rdosHbaseSink);

        env.execute();

    }

    public static void main(String[] args) throws Exception {
        parseAndExecute();
    }

}
