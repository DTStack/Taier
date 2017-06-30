package com.dtstack.rdos.base.flink;

import com.dtstack.rdos.common.util.GrokUtil;
import com.dtstack.rdos.engine.execution.base.operator.stream.CreateResultOperator;
import com.dtstack.rdos.engine.execution.flink120.sink.csv.RdosCsvSink;
import com.dtstack.rdos.engine.execution.flink120.sink.hbase.RdosHbaseSink;
import com.google.common.collect.Maps;
import oi.thekraken.grok.api.Grok;
import oi.thekraken.grok.api.exception.GrokException;
import org.apache.commons.io.IOUtils;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.java.StreamTableEnvironment;
import org.apache.hadoop.util.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        list.add("xxxxx#2222");
        list.add("yyyyy#1111");
        DataStream<Tuple2<String, Integer>> ds = env.fromCollection(list)
                .map(new MapFunction<String, Tuple2<String, Integer>>() {
                    @Override
                    public Tuple2<String, Integer> map(String s) throws Exception {
                        String[] arr = s.split("#");
                        return new Tuple2<String, Integer>(arr[0], Integer.valueOf(arr[1]));
                    }
                });

        Table table = tableEnv.fromDataStream(ds, "col1,col2");
        table.writeToSink(rdosHbaseSink);

    }

    public static void main(String[] args) throws Exception {
        parseAndExecute();
    }

}
