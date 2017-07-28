package com.dtstack.rdos.engine.execution.flink130.sink.stream.elasticsearch;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.streaming.connectors.elasticsearch5.ElasticsearchSink;
import org.apache.flink.table.sinks.AppendStreamTableSink;
import org.apache.flink.table.sinks.TableSink;
import org.apache.flink.types.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 从table 写入 elastic5插件
 * Date: 2017/7/27
 * Company: www.dtstack.com
 * @ahthor xuchao
 */

public class Elasticsearch5TableSink implements AppendStreamTableSink<Row> {

    private final Logger logger = LoggerFactory.getLogger(Elasticsearch5TableSink.class);

    private String clusterName;

    private int bulkFlushMaxActions = 1;

    private List<String> esAddressList;

    private String index = "";

    private String type = "";

    private List<Integer> fieldIndex;

    protected String[] fieldNames;

    private TypeInformation[] fieldTypes;

    private int parallelism = -1;

    public Elasticsearch5TableSink(String clusterName, List<String> esAddressList, String index, String type,
                                   List<Integer> fieldIndex){
        this.clusterName = clusterName;
        this.esAddressList = esAddressList;
        this.index = index;
        this.type = type;
        this.fieldIndex = fieldIndex;

    }

    @Override
    public TableSink<Row> configure(String[] fieldNames, TypeInformation<?>[] fieldTypes) {
        this.fieldNames = fieldNames;
        this.fieldTypes = fieldTypes;
        return this;
    }

    @Override
    public TypeInformation<Row> getOutputType() {
        return new RowTypeInfo(getFieldTypes());
    }

    @Override
    public String[] getFieldNames() {
        return fieldNames;
    }

    @Override
    public TypeInformation<?>[] getFieldTypes() {
        return fieldTypes;
    }

    private RichSinkFunction createEsSinkFunction(){


        Map<String, String> userConfig = com.google.common.collect.Maps.newHashMap();
        userConfig.put("cluster.name", clusterName);
        // This instructs the sink to emit after every element, otherwise they would be buffered
        userConfig.put(ElasticsearchSink.CONFIG_KEY_BULK_FLUSH_MAX_ACTIONS, "" + bulkFlushMaxActions);
        List<InetSocketAddress> transports = new ArrayList<>();

        for(String address : esAddressList){
            String[] infoArray = address.split(":");
            int port = 9300;
            String host = infoArray[0];
            if(infoArray.length > 1){
                port = Integer.valueOf(infoArray[1].trim());
            }

            try {
                transports.add(new InetSocketAddress(InetAddress.getByName(host), port));
            }catch (Exception e){
                logger.error("", e);
                throw new RuntimeException(e);
            }
        }

        CustomerSinkFunc customerSinkFunc = new CustomerSinkFunc(index, type, fieldIndex);

        return new ElasticsearchSink<>(userConfig, transports, customerSinkFunc);
    }

    @Override
    public void emitDataStream(DataStream<Row> dataStream) {
        RichSinkFunction richSinkFunction = createEsSinkFunction();
        DataStreamSink streamSink = dataStream.addSink(richSinkFunction);
        if(parallelism > 0){
            streamSink.setParallelism(parallelism);
        }
    }

    public void setParallelism(int parallelism) {
        this.parallelism = parallelism;
    }

    public void setBulkFlushMaxActions(int bulkFlushMaxActions) {
        this.bulkFlushMaxActions = bulkFlushMaxActions;
    }
}
