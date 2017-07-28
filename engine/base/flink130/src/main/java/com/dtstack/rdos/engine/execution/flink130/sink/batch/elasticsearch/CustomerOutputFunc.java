package com.dtstack.rdos.engine.execution.flink130.sink.batch.elasticsearch;

import com.google.common.collect.Lists;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.streaming.connectors.elasticsearch.RequestIndexer;
import org.apache.flink.types.Row;
import org.apache.logging.log4j.util.Strings;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Requests;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FIXME 需要对写入数据修改成json
 * Date: 2017/7/19
 * Company: www.dtstack.com
 * @ahthor xuchao
 */

public class CustomerOutputFunc implements ElasticsearchOutputFunction<Row> {

    private Logger logger = LoggerFactory.getLogger(CustomerOutputFunc.class);

    private String index;

    private String type;

    private List<Integer> idFieldIndexList;

    private char sp = '_'; //默认分隔符为'_'

    public CustomerOutputFunc(String index, String type, List<Integer> idFieldIndexes){
        this.index = index;
        this.type = type;
        this.idFieldIndexList = idFieldIndexes;
    }

    @Override
    public void process(Row element, RuntimeContext ctx, RequestIndexer indexer) {
        try{
            indexer.add(createIndexRequest(element));
        }catch (Throwable e){
            logger.error("", e);
        }
    }

    //FIXME 如何设置插入es的数据json格式
    private IndexRequest createIndexRequest(Row element) {

        List<String> idFieldList = Lists.newArrayList();
        for(int index : idFieldIndexList){
            if(index >= element.getArity()){
                continue;
            }

            idFieldList.add(element.getField(index).toString());
        }

        String id = Strings.join(idFieldList, sp);
        return Requests.indexRequest()
                .index(index)
                .type(type)
                .id(id)
                .source(element);
    }
}
