package com.dtstack.rdos.engine.execution.flink130.sink.elasticsearch;

import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.streaming.connectors.elasticsearch.RequestIndexer;
import org.apache.flink.types.Row;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Requests;

import java.util.HashMap;
import java.util.Map;

/**
 * FIXME 需要对写入数据修改成json
 * Date: 2017/7/19
 * Company: www.dtstack.com
 * @ahthor xuchao
 */

public class CustomerOutputFunc implements ElasticsearchOutputFunction<Row> {

    private String index;

    private String type;

    public CustomerOutputFunc(String index, String type){
        this.index = index;
        this.type = type;
    }

    @Override
    public void process(Row element, RuntimeContext ctx, RequestIndexer indexer) {
        indexer.add(createIndexRequest(element));
    }

    private IndexRequest createIndexRequest(Row element) {
        Map<String, Object> json = new HashMap<>();
        json.put("data", element);

        return Requests.indexRequest()
                .index(index)
                .type(type)
                .id(element.toString())
                .source(json);
    }
}
