package com.dtstack.rdos.engine.execution.flink130.sink.elasticsearch;

import com.dtstack.rdos.engine.execution.base.operator.batch.BatchCreateResultOperator;
import com.dtstack.rdos.engine.execution.flink130.sink.batch.IBatchSinkGener;
import com.google.common.collect.Maps;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.streaming.connectors.elasticsearch.util.NoOpFailureHandler;
import org.apache.flink.streaming.connectors.elasticsearch5.ElasticsearchSink;
import org.apache.flink.table.sinks.TableSink;
import org.apache.flink.types.Row;
import org.apache.flink.util.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 自定义es写入插件 for batch table
 * Date: 2017/7/18
 * Company: www.dtstack.com
 * @ahthor xuchao
 */

public class Elastic5BatchTableSink extends ElasticBatchTableBaseSink implements IBatchSinkGener<Elastic5BatchTableSink>{

    private Logger logger = LoggerFactory.getLogger(Elastic5BatchTableSink.class);

    private static final String CLUSTER_NAME = "es.cluster.name";

    private static final String ES_ADDRESS = "es.address";

    private static final String ES_INDEX_NAME = "es.index";

    private static final String ES_TYPE = "es.type";

    private static final String ES_BULK_FLUSH_SIZE = "bulk.flush.max.actions";

    private String clusterName;

    private List<String> esAddressList;

    protected String[] fieldNames;

    private TypeInformation[] fieldTypes;

    private int bulkFlushMaxActions = 1;//默认每次都提交

    private String indexName;

    private String esType;

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
        Map<String, String> userConfig = Maps.newHashMap();
        userConfig.put("cluster.name", clusterName);
        // This instructs the sink to emit after every element, otherwise they would be buffered
        userConfig.put(ElasticsearchSink.CONFIG_KEY_BULK_FLUSH_MAX_ACTIONS, "" + bulkFlushMaxActions);
        List<InetSocketAddress> transports = new ArrayList<>();

        for(String address : esAddressList){
            String[] infoArray = address.split(":");
            String host = null;
            int port = 9300;

            host = infoArray[0];
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

        ElasticsearchOutputFunction sinkFunction = new CustomerOutputFunc(indexName, esType);

        Elastic5OutputFormat elastic5OutputFormat = new Elastic5OutputFormat(userConfig, transports, sinkFunction, new NoOpFailureHandler());
        dataSet.output(elastic5OutputFormat);
    }

    @Override
    public Elastic5BatchTableSink genBatchSink(BatchCreateResultOperator resultOperator) {

        Preconditions.checkState(resultOperator.getProperties().contains(CLUSTER_NAME), "need param of " + CLUSTER_NAME);
        Preconditions.checkState(resultOperator.getProperties().contains(ES_ADDRESS), "need param of " + ES_ADDRESS);
        Preconditions.checkState(resultOperator.getProperties().contains(ES_INDEX_NAME), "need param of " + ES_INDEX_NAME);
        Preconditions.checkState(resultOperator.getProperties().contains(ES_TYPE), "need param of " + ES_TYPE);

        this.clusterName = resultOperator.getProperties().getProperty(CLUSTER_NAME);
        String esAddress = resultOperator.getProperties().getProperty(ES_ADDRESS);
        if(resultOperator.getProperties().containsKey(ES_BULK_FLUSH_SIZE)){
            bulkFlushMaxActions = Integer.valueOf(resultOperator.getProperties().getProperty(ES_BULK_FLUSH_SIZE));
        }

        this.indexName = resultOperator.getProperties().getProperty(ES_INDEX_NAME);
        this.esType = resultOperator.getProperties().getProperty(ES_TYPE);

        String[] addressArr = esAddress.split(",");
        if(addressArr.length == 0){
            throw new RuntimeException("need to set address of elasticSearch.");
        }

        this.esAddressList = Arrays.asList(addressArr);

        return this;
    }
}
