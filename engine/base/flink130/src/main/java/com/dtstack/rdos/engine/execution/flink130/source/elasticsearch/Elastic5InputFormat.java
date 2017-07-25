package com.dtstack.rdos.engine.execution.flink130.source.elasticsearch;

import com.google.common.collect.Lists;
import org.apache.flink.api.common.io.DefaultInputSplitAssigner;
import org.apache.flink.api.common.io.RichInputFormat;
import org.apache.flink.api.common.io.statistics.BaseStatistics;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.ResultTypeQueryable;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.io.InputSplit;
import org.apache.flink.core.io.InputSplitAssigner;
import org.apache.flink.types.Row;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;

/**
 * flink 读取es5
 * Date: 2017/7/17
 * Company: www.dtstack.com
 * @ahthor xuchao
 */

public class Elastic5InputFormat extends RichInputFormat<Row, InputSplit> implements ResultTypeQueryable<Row> {

    private String cluster;

    private List<String> hosts;

    private String index;

    private String query;

    private RowTypeInfo rowTypeInfo;

    private String[] rowFieldNames;

    private Integer splitNum = 1;

    private transient TransportClient client;

    private transient SearchResponse searchResponse;

    private transient long endNum;

    private transient long currNum;

    private transient int hitTotal;

    private transient int currhit;

    private transient String scrollId;

    private transient Scroll esScroll;

    private transient SearchHit[] hitsArr;

    public Elastic5InputFormat(){}

    @Override
    public void openInputFormat() throws IOException {

        try {
            Settings settings = Settings.builder().put("cluster.name", cluster).build();
            client = new PreBuiltTransportClient(settings);

            for(String host : hosts){
                client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), 9300));
            }

        } catch (Exception e) {
            throw new RuntimeException(String.format("init elasticsearch client error, unknown host %s", hosts), e);
        }
    }

    @Override
    public void closeInputFormat(){
        if(client != null){
            client.close();
        }
    }

    @Override
    public void configure(Configuration parameters) {
    }

    @Override
    public BaseStatistics getStatistics(BaseStatistics cachedStatistics) throws IOException {
        return null;
    }

    @Override
    public InputSplit[] createInputSplits(int minNumSplits) throws IOException {

        if(splitNum < 1){
            throw new RuntimeException("split num can't less then 0");
        }

        QueryBuilder queryBuilder = QueryBuilders.queryStringQuery(query);

        Settings settings = Settings.builder().put("cluster.name", cluster).build();
        client = new PreBuiltTransportClient(settings);

        for(String host : hosts){
            client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), 9300));
        }

        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index).setQuery(queryBuilder);

        long total = searchRequestBuilder.execute().actionGet().getHits().getTotalHits();

        long perSize = total/splitNum;
        if(perSize == 0){
            return new InputSplit[]{new ElasticInputSplit(0, total, 1)};
        }

        List<ElasticInputSplit> splitList = Lists.newArrayList();
        for(int i=0; i<splitNum; i++){
            long beginIndex = i * perSize;
            long num = perSize;

            if(i == splitNum - 1){//最后一个分片需要读取到结束
                num = total - i * perSize;
            }

            ElasticInputSplit split = new ElasticInputSplit(beginIndex, num, i+1);
            splitList.add(split);
        }

        return splitList.toArray(new InputSplit[splitList.size()]);
    }

    @Override
    public InputSplitAssigner getInputSplitAssigner(InputSplit[] inputSplits) {
        return new DefaultInputSplitAssigner(inputSplits);
    }

    @Override
    public void open(InputSplit split) throws IOException {

        ElasticInputSplit inputSplit = (ElasticInputSplit) split;
        long beginIndex = inputSplit.getStartIndex();
        endNum = inputSplit.getEndIndex();

        QueryBuilder queryBuilder = QueryBuilders.queryStringQuery(query);
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index).setFrom((int) beginIndex)
                .setQuery(queryBuilder);

        searchResponse = searchRequestBuilder.execute().actionGet();
        scrollId = searchResponse.getScrollId();
    }

    @Override
    public boolean reachedEnd() throws IOException {
        return currNum >= endNum;
    }

    @Override
    public Row nextRecord(Row reuse) throws IOException {

        if(hitsArr == null){

            if(searchResponse == null){
                SearchScrollRequestBuilder scrollRequestBuilder = client.prepareSearchScroll(scrollId).setScroll(esScroll);
                searchResponse = scrollRequestBuilder.execute().actionGet();
            }

            SearchHits hits = searchResponse.getHits();
            hitsArr = hits.getHits();
            hitTotal = hitsArr.length;
            currhit = 0;
        }

        SearchHit hit = hitsArr[currhit];
        currhit++;
        currNum++;
        Map<String, Object> row = hit.getSource();// 一整行数据

        if(currhit == hitTotal){
            hitsArr = null;
            searchResponse = null;
        }

        for (int pos = 0; pos < reuse.getArity(); pos++) {
            String fieldName = rowFieldNames[pos];
            if(fieldName.indexOf(".") != -1){
                String[] childs = fieldName.split("\\.");
                Object val = row;
                for(String child : childs){
                    if(val instanceof Map){
                        val = ((Map)val).get(child);
                    }else{
                        val = null;
                        break;
                    }
                }

                reuse.setField(pos, val);
            }else{
                reuse.setField(pos, row.get(fieldName));
            }
        }

        return reuse;
    }

    @Override
    public void close() throws IOException {
        System.out.println("---------");
    }

    @Override
    public TypeInformation<Row> getProducedType() {
        return rowTypeInfo;
    }

    class ElasticInputSplit implements InputSplit{

        private long startIndex;

        private long endIndex;

        private int partitionNum;

        public ElasticInputSplit(long startIndex, long endIndex, int partitionNum){
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.partitionNum = partitionNum;
        }

        @Override
        public int getSplitNumber() {
            return this.partitionNum;
        }

        public long getStartIndex(){
            return this.startIndex;
        }

        public long getEndIndex(){
            return this.endIndex;
        }
    }

    public static Elastic5InputFormatBuilder buildElastic5InputFormat() {
        return new Elastic5InputFormatBuilder();
    }

    public static class Elastic5InputFormatBuilder {

        private final Elastic5InputFormat format;

        public Elastic5InputFormatBuilder(){
            format = new Elastic5InputFormat();
        }

        public Elastic5InputFormatBuilder setCluster(String cluster){
            format.cluster = cluster;
            return this;
        }

        public Elastic5InputFormatBuilder setHosts(List<String> hosts){
            format.hosts = hosts;
            return this;
        }

        public Elastic5InputFormatBuilder setIndex(String index){
            format.index = index;
            return this;
        }

        public Elastic5InputFormatBuilder setQuery(String query){
            format.query = query;
            return this;
        }

        public Elastic5InputFormatBuilder setRowTypeInfo(RowTypeInfo rowTypeInfo){
            format.rowTypeInfo = rowTypeInfo;
            return this;
        }

        public Elastic5InputFormatBuilder setRowFieldNames(String[] rowFieldNames){
            format.rowFieldNames = rowFieldNames;
            return this;
        }

        public Elastic5InputFormatBuilder setSplitNum(int splitNum){
            format.splitNum = splitNum;
            return this;
        }

        public Elastic5InputFormat finish(){

            if(format.hosts == null){
                throw new IllegalArgumentException("No hosts supplied");
            }

            if(format.index == null){
                throw new IllegalArgumentException("No index supplied");
            }

            if(format.query == null){
                throw new IllegalArgumentException("No query  supplied");
            }

            if(format.rowTypeInfo == null){
                throw new IllegalArgumentException("No rowTypeInfo supplied");
            }

            if(format.rowFieldNames == null){
                throw new IllegalArgumentException("No rowFieldNames supplied");
            }

            return format;
        }
    }
}
