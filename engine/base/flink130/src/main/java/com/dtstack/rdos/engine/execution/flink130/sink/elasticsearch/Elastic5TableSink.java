package com.dtstack.rdos.engine.execution.flink130.sink.elasticsearch;

import com.google.common.collect.Maps;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.streaming.connectors.elasticsearch.util.NoOpFailureHandler;
import org.apache.flink.streaming.connectors.elasticsearch5.ElasticsearchSink;
import org.apache.flink.table.sinks.BatchTableSink;
import org.apache.flink.table.sinks.TableSink;
import org.apache.flink.types.Row;
import org.apache.flink.util.Preconditions;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Reason:
 * Date: 2017/7/18
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
 */

public class Elastic5TableSink implements BatchTableSink<Row> {

    protected String[] fieldNames;

    private TypeInformation[] fieldTypes;

    private ElasticsearchOutputFunction sinkFunction;

    public Elastic5TableSink(ElasticsearchOutputFunction sinkFunction){
        this.sinkFunction = sinkFunction;
    }

    @Override
    public TypeInformation<Row> getOutputType() {
        return new RowTypeInfo(fieldTypes, fieldNames);
    }

    @Override
    public String[] getFieldNames() {
        return fieldNames;
    }

    @Override
    public TypeInformation<?>[] getFieldTypes() {
        return fieldTypes;
    }

    @Override
    public TableSink<Row> configure(String[] fieldNames, TypeInformation<?>[] fieldTypes) {
        this.fieldNames = Preconditions.checkNotNull(fieldNames, "fieldNames");
        this.fieldTypes = Preconditions.checkNotNull(fieldTypes, "fieldTypes");
        return this;
    }

    @Override
    public void emitDataSet(DataSet<Row> dataSet) {

        //FIXME 参数初始化暂时写在这
        Map<String, String> userConfig = Maps.newHashMap();
        userConfig.put("cluster.name", "poc_dtstack");
        // This instructs the sink to emit after every element, otherwise they would be buffered
        userConfig.put(ElasticsearchSink.CONFIG_KEY_BULK_FLUSH_MAX_ACTIONS, "1");

        List<InetSocketAddress> transports = new ArrayList<>();

        try{
            transports.add(new InetSocketAddress(InetAddress.getByName("172.16.1.232"), 9300));
            transports.add(new InetSocketAddress(InetAddress.getByName("172.16.1.142"), 9300));
        }catch (Exception e){
            e.printStackTrace();
        }

        Elastic5OutputFormat elastic5OutputFormat = new Elastic5OutputFormat(userConfig, transports, sinkFunction, new NoOpFailureHandler());
        dataSet.output(elastic5OutputFormat);
    }
}
