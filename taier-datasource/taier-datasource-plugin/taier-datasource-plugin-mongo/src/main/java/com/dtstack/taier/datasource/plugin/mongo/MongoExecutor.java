package com.dtstack.taier.datasource.plugin.mongo;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.datasource.api.dto.SqlQueryDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.MongoSourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.util.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.BsonArray;
import org.bson.BsonString;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @company: www.dtstack.com
 * @Author ：wangchuan
 * @Date ：Created in 上午9:56 2020/8/4
 * @Description：mongodb自定义查询
 */
@Slf4j
public class MongoExecutor {

    private final static String FIND = "find";

    private final static String COUNT = "count";

    private final static String COUNT_DOCUMENTS = "countDocuments";

    private final static String FIND_ONE = "findOne";

    private final static String DISTINCT = "distinct";

    private final static String AGGREGATE = "aggregate";

    private final static String CREATE_COLLECTION = "create_collection";

    private final static String DISTINCT_TMPLATE = "[{'$match':%s},{'$project':{'%s':'$%s','_id':1}},{'$group':{'_id':null,'distinct':{'$addToSet':'$$ROOT'}}},{'$unwind':{'path':'$distinct','preserveNullAndEmptyArrays':false}},{'$replaceRoot':{'newRoot':'$distinct'}}])";

    private final List<String> OPERATIONS = Lists.newArrayList(FIND, FIND_ONE, COUNT, COUNT_DOCUMENTS, DISTINCT, AGGREGATE);

    private volatile static MongoExecutor mongoExecutor = null;

    private static final String EXECUTE_KEY = "result";

    private static final String COUNT_KEY = "count";

    private MongoExecutor() {
    }

    public static MongoExecutor getInstance() {
        if (mongoExecutor == null) {
            synchronized (MongoExecutor.class) {
                if (mongoExecutor == null) {
                    mongoExecutor = new MongoExecutor();
                }
            }
        }
        return mongoExecutor;
    }

    /**
     * 自定义查询
     *
     * @param source
     * @param queryDTO
     * @return
     */
    public List<Map<String, Object>> execute(ISourceDTO source, SqlQueryDTO queryDTO) {
        MongoSourceDTO mongoSourceDTO = (MongoSourceDTO) source;
        //构建mongodb自定义查询基类
        MongoQueryInfo mongoQueryInfo = new MongoQueryInfo(queryDTO.getSql(), mongoSourceDTO).build();
        String dataBaseName = mongoQueryInfo.getDataBaseName();
        String collectionName = mongoQueryInfo.getCollectionName();
        String operationName = mongoQueryInfo.getOperationName();
        String sqlQuery = mongoQueryInfo.getSqlQuery();
        List<Map<String, Object>> list = new ArrayList<>();

        Integer startRow = queryDTO.getStartRow();
        Integer limit = queryDTO.getLimit();
        MongoClient mongoClient = MongoDBUtils.getClient(mongoSourceDTO);
        MongoDatabase mongoDatabase = mongoClient.getDatabase(dataBaseName);
        MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);

        try {
            switch (operationName) {
                case FIND:
                    find(sqlQuery, startRow, limit, list, collection, false);
                    break;
                case FIND_ONE:
                    find(sqlQuery, startRow, limit, list, collection, true);
                    break;
                case COUNT:
                case COUNT_DOCUMENTS:
                    count(sqlQuery, list, collection);
                    break;
                case AGGREGATE:
                    aggregate(sqlQuery, list, collection);
                    break;
                case DISTINCT:
                    distinct(sqlQuery, list, collection);
                    break;
                case CREATE_COLLECTION:
                    createCollection(mongoDatabase, collectionName);
                    break;
                default:
                    throw new SourceException(String.format("not support request:db.%s.%s", dataBaseName, operationName));
            }
        } catch (Exception e) {
            throw new SourceException(e.getMessage(), e);
        } finally {
            if (!BooleanUtils.isTrue(MongoDBUtils.IS_OPEN_POOL.get()) && mongoClient != null) {
                mongoClient.close();
            }
        }

        return list;
    }

    private void find(String sqlQuery, Integer startRow, Integer limit, List<Map<String, Object>> list, MongoCollection<Document> collection, boolean isOne) {
        // 走 count 逻辑, 此时其他参数都不生效
        if (RegExpUtil.isCount(sqlQuery)) {
            count(sqlQuery, list, collection);
            return;
        }
        FindIterable<Document> findIterable;
        String queryStr = String.format("[%s]", RegExpUtil.getQuery(sqlQuery));
        BasicDBList queryList = (BasicDBList) JSON.parse(queryStr);
        //find
        BasicDBObject findObject = new BasicDBObject();
        if (queryList.size() > 0) {
            findObject = (BasicDBObject) queryList.get(0);
        }
        findIterable = collection.find(findObject);

        //projection
        if (queryList.size() > 1) {
            findIterable = findIterable.projection((Bson) queryList.get(1));
        }

        //skip|limit
        if (isOne) {
            findIterable.limit(1);
        } else {
            findIterable
                    .skip(!Objects.isNull(startRow) ? startRow : 0)
                    .limit(!Objects.isNull(limit) ? limit : 0);
        }

        //sort
        String sort = RegExpUtil.getSort(sqlQuery);
        if (StringUtils.isNotBlank(sort)) {
            findIterable.sort((BasicDBObject) JSON.parse(sort));
        }

        //batchSize
        String batchSize = RegExpUtil.getBatchSize(sqlQuery);
        if (StringUtils.isNotBlank(batchSize)) {
            findIterable.batchSize(Integer.valueOf(batchSize));
        }

        for (Document document : findIterable) {
            if (document == null) {
                continue;
            }
            if (document.containsKey("_id")) {
                Object value = document.get("_id");
                if (value != null) {
                    document.put("_id", value.toString());
                }
            }
            list.add(document);
        }

        //count -
        long count = collection.count(findObject);
        Map<String, Object> countMap = Maps.newHashMap();
        countMap.put(COUNT_KEY, count);
        list.add(0, countMap);
    }

    private void count(String sqlQuery, List<Map<String, Object>> list, MongoCollection<Document> collection) {
        String queryStr = RegExpUtil.getQuery(sqlQuery);
        long count;
        if (StringUtils.isNotBlank(queryStr)) {
            BasicDBObject queryObject = BasicDBObject.parse(queryStr);
            count = collection.countDocuments(queryObject);
        } else {
            count = collection.countDocuments();
        }
        Map<String, Object> result = Maps.newHashMap();
        result.put(EXECUTE_KEY, count);
        list.add(result);
    }

    private void aggregate(String sqlQuery, List<Map<String, Object>> list, MongoCollection<Document> collection) {
        String queryStr = RegExpUtil.getQuery(sqlQuery);
        queryStr = addBrackets(queryStr);
        aggregateWithQuery(sqlQuery, list, collection, queryStr);
    }

    private void distinct(String sqlQuery, List<Map<String, Object>> list, MongoCollection<Document> collection) {
        String fieldSql = RegExpUtil.getQuery(sqlQuery);
        if (StringUtils.isBlank(fieldSql)) {
            return;
        }

        BsonArray queryList;
        if (fieldSql.startsWith("[")) {
            queryList = BsonArray.parse(fieldSql);
        } else {
            String queryArray = String.format("[%s]", fieldSql);
            queryList = BsonArray.parse(queryArray);
        }
        String query = "{}";
        String field = "";
        if (queryList.size() > 1) {
            field = ((BsonString) queryList.get(0)).getValue();
            query = queryList.get(1).toString();
        } else {
            field = ((BsonString) queryList.get(0)).getValue();
        }

        String queryStr = String.format(DISTINCT_TMPLATE, query, field, field);
        aggregateWithQuery(sqlQuery, list, collection, queryStr);
    }

    /**
     * 聚合查询
     * 支持allowDiskUse
     *
     * @param sqlQuery
     * @param list
     * @param collection
     * @param queryStr
     */
    private void aggregateWithQuery(String sqlQuery, List<Map<String, Object>> list, MongoCollection<Document> collection, String queryStr) {
        AggregateIterable<Document> aggregateIterable;
        BasicDBList dbList = (BasicDBList) JSON.parse(queryStr);
        List<BasicDBObject> pipeline = new ArrayList<>();
        for (Object o : dbList) {
            BasicDBObject basicDBObject = (BasicDBObject) o;
            if (!(basicDBObject == null || basicDBObject.size() == 0)) {
                pipeline.add(basicDBObject);
            }
        }
        aggregateIterable = collection.aggregate(pipeline);

        //batchSize
        String batchSize = RegExpUtil.getBatchSize(sqlQuery);
        if (StringUtils.isNotBlank(batchSize)) {
            aggregateIterable.batchSize(Integer.valueOf(batchSize));
        }

        for (Document document : aggregateIterable) {
            if (document == null) {
                continue;
            }
            if (document.containsKey("_id")) {
                Object value = document.get("_id");
                if (value != null) {
                    document.put("_id", value.toString());
                }
            }
            list.add(document);

        }
    }

    /**
     * 创建mongo collection
     *
     * @param dataBase
     * @param cName
     */
    private void createCollection(MongoDatabase dataBase, String cName) {
        String name, option = null;
        int i = cName.indexOf(",");
        //以第一个逗号分隔，往前为name，往后为option
        if (i == -1) {
            name = cName.replace("'", "");
        } else {
            name = cName.substring(0, i).replace("'", "");
            option = cName.substring(i + 1);
        }
        CreateCollectionOptions options = null;

        //解析并创建options
        if (StringUtils.isNotBlank(option)) {
            try {
                JSONObject optionJson = JSONObject.parseObject(option);
                if (!optionJson.isEmpty()) {
                    options = new CreateCollectionOptions()
                            .capped(optionJson.getBoolean("capped"))
                            .maxDocuments(optionJson.getLong("max"))
                            .sizeInBytes(optionJson.getLong("size"));
                }
            } catch (Exception e) {
                throw new SourceException("mongo create collection get options error", e);
            }
        }

        if (options == null) {
            dataBase.createCollection(name);
        } else {
            dataBase.createCollection(name, options);
        }
    }

    private class MongoQueryInfo {
        private String sqlQuery;
        private MongoSourceDTO source;
        private String collectionName;
        private String operationName;
        private String dataBaseName;

        public MongoQueryInfo(String sqlQuery, MongoSourceDTO source) {
            this.sqlQuery = sqlQuery;
            this.source = source;
        }

        public String getSqlQuery() {
            return sqlQuery;
        }

        public String getCollectionName() {
            return collectionName;
        }

        public String getOperationName() {
            return operationName;
        }

        public String getDataBaseName() {
            return dataBaseName;
        }

        public MongoQueryInfo build() {
            if (log.isInfoEnabled()) {
                log.info("mongodb request with sql [{}]", sqlQuery);
            }
            //sql在传入时会去掉 ; , 解析数据时需要使用 ;
            if (!sqlQuery.endsWith(";")) {
                sqlQuery = sqlQuery + ";";
            }

            dataBaseName = StringUtils.isBlank(source.getSchema()) ? MongoDBUtils.dealSchema(source.getHostPort()) : source.getSchema();

            sqlQuery = sqlQuery.replaceAll("\"", "'");
            String[] sql = sqlQuery.split("\\.");
            if (sql.length < 3) {
                //增加判断是否为建表语句
                if (sql.length == 2 && (collectionName = RegExpUtil.getUnsetCName(sqlQuery)) != null) {
                    operationName = CREATE_COLLECTION;
                    return this;
                }
                throw new SourceException("This query is not supported, please check your query statement！[2]");
            }

            collectionName = RegExpUtil.getCollectionName(sql[1]);
            if (StringUtils.isEmpty(collectionName)) {
                collectionName = sql[1];
            }

            operationName = sql[2];
            if (!operationName.contains("(")) {
                throw new SourceException("This query is not supported, please check your query statement！[3]");
            }
            operationName = operationName.substring(0, operationName.indexOf("("));
            if (!OPERATIONS.contains(operationName)) {
                throw new SourceException(String.format("not supported this request:db.%s.%s", collectionName, operationName));
            }
            return this;
        }

    }

    private static String addBrackets(String sql) {
        if (sql.startsWith("{")) {
            sql = "[" + sql + "]";
        }
        return sql;
    }
}
