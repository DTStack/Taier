package com.dtstack.rdos.engine.execution.flink130.sink.stream.elasticsearch;

import com.dtstack.rdos.engine.execution.base.operator.stream.StreamCreateResultOperator;
import com.dtstack.rdos.engine.execution.flink130.sink.stream.IStreamSinkGener;
import com.google.common.collect.Lists;
import org.apache.flink.util.Preconditions;

import java.util.Arrays;
import java.util.List;

/**
 *
 * Date: 2017/7/27
 * Company: www.dtstack.com
 * @ahthor xuchao
 */

public class RdosElasticsearchSink implements IStreamSinkGener<Elasticsearch5TableSink> {

    private static final String CLUSTER_NAME = "es.cluster.name";

    private static final String ES_ADDRESS = "es.address";

    private static final String ES_INDEX_NAME = "es.index";

    private static final String ES_TYPE = "es.type";

    private static final String ES_ID_FIELDS_INDEX = "es.id.fields.index";

    private static final String ES_BULK_FLUSH_SIZE = "bulk.flush.max.actions";

    @Override
    public Elasticsearch5TableSink genStreamSink(StreamCreateResultOperator resultOperator) {

        Preconditions.checkState(resultOperator.getProperties().containsKey(CLUSTER_NAME), "need param of " + CLUSTER_NAME);
        Preconditions.checkState(resultOperator.getProperties().containsKey(ES_ADDRESS), "need param of " + ES_ADDRESS);
        Preconditions.checkState(resultOperator.getProperties().containsKey(ES_INDEX_NAME), "need param of " + ES_INDEX_NAME);
        Preconditions.checkState(resultOperator.getProperties().containsKey(ES_TYPE), "need param of " + ES_TYPE);
        Preconditions.checkState(resultOperator.getProperties().containsKey(ES_ID_FIELDS_INDEX), "need param of " + ES_ID_FIELDS_INDEX);


        String clusterName = resultOperator.getProperties().getProperty(CLUSTER_NAME);
        String esAddress = resultOperator.getProperties().getProperty(ES_ADDRESS);
        String indexName = resultOperator.getProperties().getProperty(ES_INDEX_NAME);
        String esType = resultOperator.getProperties().getProperty(ES_TYPE);
        String fieldIndex = resultOperator.getProperties().getProperty(ES_ID_FIELDS_INDEX);


        String[] addressArr = esAddress.split(",");
        if(addressArr.length == 0){
            throw new RuntimeException("need to set address of elasticSearch.");
        }

        List<String> esAddressList = Arrays.asList(addressArr);
        List<Integer> indexList = Lists.newArrayList();
        for(String indexStr : fieldIndex.split(",")){
            indexList.add(Integer.valueOf(indexStr.trim()));
        }

        Elasticsearch5TableSink tableSink = new Elasticsearch5TableSink(clusterName, esAddressList, indexName, esType, indexList);

        if(resultOperator.getProperties().containsKey(ES_BULK_FLUSH_SIZE)){
            int bulkFlushMaxActions = Integer.valueOf(resultOperator.getProperties().getProperty(ES_BULK_FLUSH_SIZE));
            if(bulkFlushMaxActions < 1){
                throw new RuntimeException(ES_BULK_FLUSH_SIZE + " can't less then 1.");
            }
            tableSink.setBulkFlushMaxActions(bulkFlushMaxActions);
        }

        return tableSink;
    }
}
