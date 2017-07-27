package com.dtstack.rdos.engine.execution.flink130.source.batch.elasticsearch;

import com.dtstack.rdos.engine.execution.flink130.source.batch.IBatchSourceGener;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.table.sources.BatchTableSource;

import java.util.List;
import java.util.Properties;

/**
 * Reason:
 * Date: 2017/7/26
 * Company: www.dtstack.com
 * @ahthor xuchao
 */

public class Elastic5SourceGenr implements IBatchSourceGener<BatchTableSource> {

    private static final String CLUSTER_NAME = "es.cluster.name";

    private static final String ES_ADDRESS = "es.address";

    private static final String ES_INDEX_NAME = "es.index";

    private static final String ES_QUERY = "es.query";

    @Override
    public BatchTableSource genBatchSource(Properties prop, String[] fieldNames, Class[] fieldTypes) {

        Preconditions.checkState(prop.containsKey(CLUSTER_NAME), "need to set param of " + CLUSTER_NAME);
        Preconditions.checkState(prop.containsKey(ES_ADDRESS), "need to set param of " + ES_ADDRESS);
        Preconditions.checkState(prop.containsKey(ES_INDEX_NAME), "need to set param of " + ES_INDEX_NAME);
        Preconditions.checkState(prop.containsKey(ES_QUERY), "need to set param of " + ES_QUERY);

        String clusterName = prop.getProperty(CLUSTER_NAME);
        String indexName = prop.getProperty(ES_INDEX_NAME);
        String addrStr = prop.getProperty(ES_ADDRESS);
        List<String> addrList = Lists.newArrayList(addrStr.split(","));
        String query = prop.getProperty(ES_QUERY);

        TypeInformation[] types = new TypeInformation[fieldTypes.length];
        for(int i=0; i<fieldTypes.length; i++){
            types[i] = TypeInformation.of(fieldTypes[i]);
        }

        BatchTableSource esTableSource = new Elastic5TableSource(clusterName, indexName, addrList, query, types, fieldNames);
        return esTableSource;
    }

}
