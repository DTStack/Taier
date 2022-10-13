package com.dtstack.taier.datasource.plugin.mongo;

import com.dtstack.taier.datasource.api.dto.SqlQueryDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.MongoSourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.plugin.common.exception.IErrorPattern;
import com.dtstack.taier.datasource.plugin.common.service.ErrorAdapterImpl;
import com.dtstack.taier.datasource.plugin.common.service.IErrorAdapter;
import com.dtstack.taier.datasource.plugin.common.utils.AddressUtil;
import com.dtstack.taier.datasource.plugin.common.utils.SearchUtil;
import com.dtstack.taier.datasource.plugin.mongo.pool.MongoManager;
import com.google.common.collect.Lists;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Pair;
import org.bson.Document;

import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 15:28 2020/2/5
 * @Description：MongoDB 工具类
 */
@Slf4j
public class MongoDBUtils {
    private static final String HOST_SPLIT_REGEX = ",\\s*";

    private static Pattern HOST_PORT_PATTERN = Pattern.compile("(?<host>(.*)):((?<port>\\d+))*");

    private static Pattern HOST_PORT_SCHEMA_PATTERN = Pattern.compile("(?<host>(.*)):((?<port>\\d+))*/(?<schema>(.*))");

    private static Pattern USER_PWD_PATTERN = Pattern.compile("(?<username>(.*)):(?<password>(.*))@(?<else>(.*))");

    private static final Integer DEFAULT_PORT = 27017;

    public static final int TIME_OUT = 5 * 1000;

    private static final String POOL_CONFIG_FIELD_NAME = "poolConfig";

    private static MongoManager mongoManager = MongoManager.getInstance();

    public static final ThreadLocal<Boolean> IS_OPEN_POOL = new ThreadLocal<>();

    private static final IErrorPattern ERROR_PATTERN = new MongoErrorPattern();

    // 异常适配器
    private static final IErrorAdapter ERROR_ADAPTER = new ErrorAdapterImpl();

    public static boolean checkConnection(ISourceDTO iSource) {
        MongoSourceDTO mongoSourceDTO = (MongoSourceDTO) iSource;
        boolean check = false;
        MongoClient mongoClient = null;
        try {
            mongoClient = getClient(mongoSourceDTO);
            String schema = StringUtils.isBlank(mongoSourceDTO.getSchema()) ? dealSchema(mongoSourceDTO.getHostPort()) : mongoSourceDTO.getSchema();
            MongoDatabase mongoDatabase = mongoClient.getDatabase(schema);
            MongoIterable<String> mongoIterable = mongoDatabase.listCollectionNames();
            mongoIterable.iterator().hasNext();
            check = true;
        } catch (Exception e) {
            throw new SourceException(ERROR_ADAPTER.connAdapter(e.getMessage(), ERROR_PATTERN), e);
        } finally {
            if (!BooleanUtils.isTrue(IS_OPEN_POOL.get()) && mongoClient != null) {
                mongoClient.close();
                IS_OPEN_POOL.remove();
            }
        }
        return check;
    }

    /**
     * 获取数据库
     *
     * @param iSource
     * @return
     */
    public static List<String> getDatabaseList(ISourceDTO iSource) {
        MongoSourceDTO mongoSourceDTO = (MongoSourceDTO) iSource;
        MongoClient mongoClient = null;
        ArrayList<String> databases = new ArrayList<>();
        try {
            mongoClient = getClient(mongoSourceDTO);
            MongoIterable<String> dbNames = mongoClient.listDatabaseNames();
            for (String dbName : dbNames) {
                databases.add(dbName);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (!BooleanUtils.isTrue(IS_OPEN_POOL.get()) && mongoClient != null) {
                mongoClient.close();
                IS_OPEN_POOL.remove();
            }
        }
        return databases;
    }

    /**
     * 预览数据
     */
    public static List<List<Object>> getPreview(ISourceDTO iSource, SqlQueryDTO queryDTO) {
        MongoSourceDTO mongoSourceDTO = (MongoSourceDTO) iSource;
        List<List<Object>> dataList = new ArrayList<>();
        String schema = StringUtils.isBlank(mongoSourceDTO.getSchema()) ? dealSchema(mongoSourceDTO.getHostPort()) : mongoSourceDTO.getSchema();
        if (StringUtils.isBlank(queryDTO.getTableName()) || StringUtils.isBlank(schema)) {
            return dataList;
        }
        MongoClient mongoClient = null;
        try {
            mongoClient = getClient(mongoSourceDTO);
            //获取指定数据库
            MongoDatabase mongoDatabase = mongoClient.getDatabase(schema);
            //获取指定表
            MongoCollection<Document> collection = mongoDatabase.getCollection(queryDTO.getTableName());
            FindIterable<Document> documents = collection.find().limit(queryDTO.getPreviewNum());
            for (Document document : documents) {
                ArrayList<Object> list = new ArrayList<>();
                document.keySet().forEach(key -> list.add(new Pair<String, Object>(key, document.get(key))));
                dataList.add(list);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (!BooleanUtils.isTrue(IS_OPEN_POOL.get()) && mongoClient != null) {
                mongoClient.close();
                IS_OPEN_POOL.remove();
            }
        }
        return dataList;
    }

    /**
     * 获取指定库下的表名
     *
     * @param sourceDTO 数据源信息
     * @param queryDTO  查询条件
     * @return 表名集合
     */
    public static List<String> getTableList(ISourceDTO sourceDTO, SqlQueryDTO queryDTO) {
        MongoSourceDTO mongoSourceDTO = (MongoSourceDTO) sourceDTO;
        List<String> tableList = Lists.newArrayList();
        MongoClient mongoClient = null;
        try {
            String db = StringUtils.isBlank(mongoSourceDTO.getSchema()) ? dealSchema(mongoSourceDTO.getHostPort()) : mongoSourceDTO.getSchema();
            mongoClient = getClient(mongoSourceDTO);
            MongoDatabase mongoDatabase = mongoClient.getDatabase(db);
            MongoIterable<String> tableNames = mongoDatabase.listCollectionNames();
            for (String s : tableNames) {
                tableList.add(s);
            }
        } catch (Exception e) {
            log.error("get tablelist exception  {}", mongoSourceDTO, e);
        } finally {
            if (!BooleanUtils.isTrue(IS_OPEN_POOL.get()) && mongoClient != null) {
                mongoClient.close();
            }
        }
        return SearchUtil.handleSearchAndLimit(tableList, queryDTO);
    }

    public static List<ServerAddress> getServerAddress(String hostPorts) {
        List<ServerAddress> addresses = Lists.newArrayList();

        boolean isTelnet = true;
        StringBuilder errorHost = new StringBuilder();
        for (String hostPort : hostPorts.split(HOST_SPLIT_REGEX)) {
            if (hostPort.length() == 0) {
                continue;
            }

            Matcher matcher = HOST_PORT_PATTERN.matcher(hostPort);
            if (matcher.find()) {
                String host = matcher.group("host");
                String portStr = matcher.group("port");
                int port = portStr == null ? DEFAULT_PORT : Integer.parseInt(portStr);

                if (!AddressUtil.telnet(host, port)) {
                    errorHost.append(hostPort).append(" ");
                    isTelnet = false;
                }

                ServerAddress serverAddress = new ServerAddress(host, port);
                addresses.add(serverAddress);
            }
        }

        if (!isTelnet) {
            throw new SourceException("The database server port connection failed, please check your database configuration or service status: connection information：" + errorHost.toString());
        }

        return addresses;
    }

    public static MongoClient getClient(MongoSourceDTO mongoSourceDTO) {
        String hostPorts = mongoSourceDTO.getHostPort();
        String username = mongoSourceDTO.getUsername();
        String password = mongoSourceDTO.getPassword();
        String schema = mongoSourceDTO.getSchema();
        boolean check = false;
        //适配之前的版本，判断ISourceDTO类中有无获取isCache字段的方法
        Field[] fields = MongoSourceDTO.class.getDeclaredFields();
        for (Field field : fields) {
            if (POOL_CONFIG_FIELD_NAME.equals(field.getName())) {
                check = mongoSourceDTO.getPoolConfig() != null;
                break;
            }
        }
        IS_OPEN_POOL.set(check);
        log.info("get MongoDB connected, url : {}, username : {}", hostPorts, username);
        //不开启连接池
        if (!check) {
            return getClient(hostPorts, username, password, schema);
        }
        //开启连接池
        return mongoManager.getConnection(mongoSourceDTO);
    }

    /**
     * 1.  username:password@host:port,host:port/db?option
     * 2.  host:port,host:port/db?option
     */
    public static MongoClient getClient(String hostPorts, String username, String password, String db) {
        MongoClient mongoClient = null;
        hostPorts = hostPorts.trim();
        MongoClientOptions options = MongoClientOptions.builder()
                .serverSelectionTimeout(TIME_OUT)
                .readPreference(ReadPreference.secondaryPreferred())
                .build();
        Matcher matcher = USER_PWD_PATTERN.matcher(hostPorts);
        if (matcher.matches()) {
            String usernameUrl = matcher.group("username");
            String passwordUrl = matcher.group("password");
            String elseUrl = matcher.group("else");
            MongoClientURI clientURI;
            StringBuilder uri = new StringBuilder();
            uri.append(String.format("mongodb://%s:%s@%s", URLEncoder.encode(usernameUrl),
                    URLEncoder.encode(passwordUrl), elseUrl));
            clientURI = new MongoClientURI(uri.toString());
            mongoClient = new MongoClient(clientURI);
        } else {
            List<ServerAddress> serverAddress = getServerAddress(hostPorts);
            if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
                mongoClient = new MongoClient(serverAddress, options);
            } else {
                if (StringUtils.isBlank(db)) {
                    db = dealSchema(hostPorts);
                }
                MongoCredential credential = MongoCredential.createScramSha1Credential(username, db,
                        password.toCharArray());
                List<MongoCredential> credentials = Lists.newArrayList();
                credentials.add(credential);
                mongoClient = new MongoClient(serverAddress, credentials, options);
            }
        }
        return mongoClient;
    }

    /**
     * 如果没有指定schema，判断hostPort中有没有
     *
     * @param hostPorts
     * @return
     */
    public static String dealSchema(String hostPorts) {
        for (String hostPort : hostPorts.split(HOST_SPLIT_REGEX)) {
            if (hostPort.length() == 0) {
                continue;
            }
            Matcher matcher = HOST_PORT_SCHEMA_PATTERN.matcher(hostPort);
            if (matcher.find()) {
                String schema = matcher.group("schema");
                return schema;
            }
        }
        return null;
    }

    /**
     * 判断指定db中是否存在该collection，大小写敏感
     *
     * @param source
     * @param tableName
     * @param dbName
     * @return
     */
    public static Boolean isTableExistsInDatabase(ISourceDTO source, String tableName, String dbName) {
        MongoClient client = getClient((MongoSourceDTO) source);
        MongoDatabase database = client.getDatabase(dbName);
        MongoIterable<String> names = database.listCollectionNames();
        for (String next : names) {
            if (StringUtils.equals(tableName, next)) {
                return true;
            }
        }
        return false;
    }
}
