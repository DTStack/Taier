package com.dtstack.taier.datasource.plugin.mongo.pool;

import com.dtstack.taier.datasource.api.pool.PoolConfig;
import com.dtstack.taier.datasource.plugin.mongo.MongoDBUtils;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.MongoSourceDTO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @company: www.dtstack.com
 * @Author ：wangchuan
 * @Date ：Created in 下午5:28 2020/8/3
 * @Description：池化管理类
 */
@Slf4j
public class MongoManager {
    private volatile static MongoManager manager;

    private volatile Map<String, MongoClient> sourcePool = Maps.newConcurrentMap();

    private static final String MONGO_KEY = "hostPorts:%s,username:%s,password:%s,schema:%s";

    private static Pattern USER_PWD_PATTERN = Pattern.compile("(?<username>(.*)):(?<password>(.*))@(?<else>(.*))");

    public static final int TIME_OUT = 5 * 1000;

    private MongoManager() {}

    public static MongoManager getInstance() {
        if (null == manager) {
            synchronized (MongoManager.class) {
                if (null == manager) {
                    manager = new MongoManager();
                }
            }
        }
        return manager;
    }

    public MongoClient getConnection(ISourceDTO source) {
        MongoSourceDTO mongoSourceDTO = (MongoSourceDTO) source;
        String key = getPrimaryKey(mongoSourceDTO).intern();
        MongoClient mongoClient = sourcePool.get(key);
        if (mongoClient == null) {
            synchronized (MongoManager.class) {
                mongoClient = sourcePool.get(key);
                if (mongoClient == null) {
                    mongoClient = initSource(source);
                    sourcePool.putIfAbsent(key, mongoClient);
                }
            }
        }

        return mongoClient;
    }

    public MongoClient initSource(ISourceDTO source) {
        MongoSourceDTO mongoSourceDTO = (MongoSourceDTO) source;
        String hostPorts = mongoSourceDTO.getHostPort();
        String username = mongoSourceDTO.getUsername();
        String password = mongoSourceDTO.getPassword();
        String schema = mongoSourceDTO.getSchema();
        PoolConfig poolConfig = mongoSourceDTO.getPoolConfig();
        MongoClient mongoClient = null;
        hostPorts = hostPorts.trim();
        MongoClientOptions options = MongoClientOptions.builder()
                .serverSelectionTimeout(TIME_OUT)
                .connectTimeout(poolConfig.getConnectionTimeout().intValue())
                .maxWaitTime(poolConfig.getConnectionTimeout().intValue())
                .minConnectionsPerHost(poolConfig.getMinimumIdle())
                .connectionsPerHost(poolConfig.getMaximumPoolSize())
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
            List<ServerAddress> serverAddress = MongoDBUtils.getServerAddress(hostPorts);
            if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
                mongoClient = new MongoClient(serverAddress, options);
            } else {
                if (StringUtils.isBlank(schema)){
                    schema = MongoDBUtils.dealSchema(hostPorts);
                }
                MongoCredential credential = MongoCredential.createScramSha1Credential(username, schema,
                        password.toCharArray());
                List<MongoCredential> credentials = Lists.newArrayList();
                credentials.add(credential);
                mongoClient = new MongoClient(serverAddress, credentials, options);
            }
        }
        return mongoClient;

    }

    private static String getPrimaryKey(ISourceDTO sourceDTO) {
        MongoSourceDTO mongoSourceDTO = (MongoSourceDTO) sourceDTO;
        return String.format(MONGO_KEY, mongoSourceDTO.getHostPort(), mongoSourceDTO.getUsername(), mongoSourceDTO.getPassword(), mongoSourceDTO.getSchema());
    }
}
