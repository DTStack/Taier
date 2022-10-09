package com.dtstack.taier.datasource.plugin.redis;

import com.dtstack.taier.datasource.api.enums.RedisMode;
import com.dtstack.taier.datasource.plugin.common.exception.IErrorPattern;
import com.dtstack.taier.datasource.plugin.common.service.ErrorAdapterImpl;
import com.dtstack.taier.datasource.plugin.common.service.IErrorAdapter;
import com.dtstack.taier.datasource.plugin.common.utils.AddressUtil;
import com.dtstack.taier.datasource.api.dto.SqlQueryDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.RedisSourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.api.utils.AssertUtils;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisCommands;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.util.Pool;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 16:38 2020/2/4
 * @Description：Redis 工具类
 */
@Slf4j
public class RedisUtils {
    private static Pattern HOST_PORT_PATTERN = Pattern.compile("(?<host>(.*)):((?<port>\\d+))*");
    private static final int DEFAULT_PORT = 6379;
    private static final int TIME_OUT = 5 * 1000;

    private static final int LIMIT_MAX_KEY = 1000;

    private static final IErrorPattern ERROR_PATTERN = new RedisErrorPattern();

    // 异常适配器
    private static final IErrorAdapter ERROR_ADAPTER = new ErrorAdapterImpl();

    public static boolean checkConnection(ISourceDTO iSource) {
        RedisSourceDTO redisSourceDTO = (RedisSourceDTO) iSource;
        log.info("get Redis connected, host : {}, port : {}", redisSourceDTO.getMaster(), redisSourceDTO.getHostPort());
        RedisMode redisMode = redisSourceDTO.getRedisMode() != null ? redisSourceDTO.getRedisMode() : RedisMode.Standalone;
        try {
            switch (redisMode) {
                case Standalone:
                    return checkConnectionStandalone(redisSourceDTO);
                case Sentinel:
                    return checkRedisConnectionSentinel(redisSourceDTO);
                case Cluster:
                    return checkRedisConnectionCluster(redisSourceDTO);
                default:
                    throw new SourceException("Unsupported mode");
            }
        } catch (Exception e) {
            String errorMsg = e.getMessage();
            if (e instanceof JedisConnectionException && e.getCause() != null) {
                errorMsg = e.getCause().getMessage();
            }
            throw new SourceException(ERROR_ADAPTER.connAdapter(errorMsg, ERROR_PATTERN), e);
        }
    }

    public static List<List<Object>> getPreview(ISourceDTO source, SqlQueryDTO queryDTO) {
        RedisSourceDTO redisSourceDTO = (RedisSourceDTO) source;
        String tableName = queryDTO.getTableName();
        if (StringUtils.isBlank(tableName)) {
            throw new SourceException("preview table name not empty");
        }
        log.info("get Redis connected, host : {}, port : {}", redisSourceDTO.getMaster(), redisSourceDTO.getHostPort());
        RedisMode redisMode = redisSourceDTO.getRedisMode() != null ? redisSourceDTO.getRedisMode() : RedisMode.Standalone;
        List<List<Object>> result = Lists.newArrayList();

        List<Object> redisResult = Lists.newArrayList();
        Map<String, Object> resultMap = Maps.newHashMap();

        List<String> fieldKey;
        List<String> values ;
        if (RedisMode.Standalone.equals(redisMode) || RedisMode.Sentinel.equals(redisMode)) {
            Pool<Jedis> redisPool = null;
            Jedis jedis = null;
            try {
                if (RedisMode.Standalone.equals(redisMode)) {
                    redisPool = getRedisPool(source);
                } else {
                    redisPool = getSentinelPool(source);
                }
                jedis = redisPool.getResource();
                int db = StringUtils.isEmpty(redisSourceDTO.getSchema()) ? 0 : Integer.parseInt(redisSourceDTO.getSchema());
                jedis.select(db);
                Set<String> keys = jedis.hkeys(tableName);
                if (CollectionUtils.isEmpty(keys)) {
                    return result;
                }
                fieldKey = keys.stream().limit(queryDTO.getPreviewNum()).collect(Collectors.toList());
                values = jedis.hmget(tableName, fieldKey.toArray(new String[]{}));
            } finally {
                // 关闭资源
                close(jedis, redisPool);
            }
        }  else {
            JedisCluster redisCluster = null;
            try {
                redisCluster = getRedisCluster(source);
                Set<String> keys = redisCluster.hkeys(tableName);
                if (CollectionUtils.isEmpty(keys)) {
                    return result;
                }
                fieldKey = keys.stream().limit(queryDTO.getPreviewNum()).collect(Collectors.toList());
                values = redisCluster.hmget(tableName, fieldKey.toArray(new String[]{}));
            } finally {
                // 关闭资源
                close(redisCluster);
            }
        }

        if (fieldKey.size() != values.size()) {
            throw new SourceException("get redis hash data exception");
        }
        for (int index = 0; index < values.size(); index++) {
            resultMap.put(fieldKey.get(index), values.get(index));
        }
        redisResult.add(resultMap);
        result.add(redisResult);
        return result;
    }

    /**
     * 查询 string 类型
     * @param jedis
     * @param keys
     * @return
     */
    private static Map<String, Object> getStringRedisValue(JedisCommands jedis, List<String> keys) {
        Map<String, Object> resultMap = new HashMap<>();
        if (CollectionUtils.isEmpty(keys)) {
            return resultMap;
        }
        for (String key : keys) {
            String result = jedis.get(key);
            resultMap.put(key, result);
        }
        return resultMap;
    }

    /**
     * 查询list类型
     * @param jedis
     * @param keys
     * @return
     */
    private static Map<String, Object> getListRedisValue(JedisCommands jedis, List<String> keys, Integer limit) {
        Map<String, Object> resultMap = new HashMap<>();
        if (CollectionUtils.isEmpty(keys)) {
            return resultMap;
        }
        for (String key : keys) {
            List<String> result = jedis.lrange(key, 0, limit);
            result = CollectionUtils.isEmpty(result) ? new ArrayList<>() : result;
            resultMap.put(key, result);
        }
        return resultMap;
    }

    /**
     * 查询set 类型， smembers
     * @param jedis
     * @param keys
     * @return
     */
    private static Map<String, Object> getSetRedisValue(JedisCommands jedis, List<String> keys) {
        Map<String, Object> resultMap = new HashMap<>();
        if (CollectionUtils.isEmpty(keys)) {
            return resultMap;
        }
        ScanParams scanParams = new ScanParams();
        for (String key : keys) {
            String cursor = ScanParams.SCAN_POINTER_START;
            List<String> list = new ArrayList<>();
            do {
                ScanResult<String> scanResult = jedis.sscan(key, cursor, scanParams);
                List<String> result = scanResult.getResult();
                list.addAll(result);
                cursor = scanResult.getStringCursor();
            } while (!ScanParams.SCAN_POINTER_START.equals(cursor));
            resultMap.put(key, list);
        }
        return resultMap;
    }

    /**
     * 查询zset 类型，zrange
     * @param jedis
     * @param keys
     * @return
     */
    private static Map<String, Object> getSortSetRedisValue(JedisCommands jedis, List<String> keys, Integer limit) {
        Map<String, Object> resultMap = new HashMap<>();
        if (CollectionUtils.isEmpty(keys)) {
            return resultMap;
        }
        for (String key : keys) {
            Set<String> result = jedis.zrange(key, 0, limit);
            result = CollectionUtils.isEmpty(result) ? new HashSet<>() : result;
            resultMap.put(key, result);
        }
        return resultMap;
    }


    /**
     * 查询hash 类型的表
     * @param jedis
     * @param keys
     * @return
     */
    private static Map<String, Object> getHashRedisValue(JedisCommands jedis, List<String> keys) {
        Map<String, Object> resultMap = new HashMap<>();
        if (CollectionUtils.isEmpty(keys)) {
            return resultMap;
        }
        ScanParams scanParams = new ScanParams();
        for (String key : keys) {
            String cursor = ScanParams.SCAN_POINTER_START;
            List<Map.Entry<String, String>> list = new ArrayList<>();
            do {
                ScanResult<Map.Entry<String, String>> scanResult = jedis.hscan(key, cursor, scanParams);
                List<Map.Entry<String, String>> tuples = scanResult.getResult();
                if (CollectionUtils.isNotEmpty(tuples)) {
                    list.addAll(tuples);
                }
                resultMap.put(key, list);
                cursor = scanResult.getStringCursor();
            } while (!ScanParams.SCAN_POINTER_START.equals(cursor));
        }
        return resultMap;
    }

    private static boolean checkConnectionStandalone(ISourceDTO source) {
        RedisSourceDTO redisSourceDTO = (RedisSourceDTO) source;
        JedisPool redisPool = null;
        Jedis jedis = null;
        int db = StringUtils.isNotEmpty(redisSourceDTO.getSchema()) ? Integer.parseInt(redisSourceDTO.getSchema()) : 0;
        try {
            redisPool = getRedisPool(source);
            jedis = redisPool.getResource();
            jedis.select(db);
            return true;
        } finally {
            // 关闭资源
            close(jedis, redisPool);
        }
    }

    public static boolean checkRedisConnectionCluster(ISourceDTO source) {
        RedisSourceDTO redisSourceDTO = (RedisSourceDTO) source;
        JedisCluster redisCluster = null;
        try {
            redisCluster = getRedisCluster(source);
            Set<HostAndPort> nodes = getHostAndPorts(redisSourceDTO.getHostPort());
            // redis集群模式不带密码，创建客户端不会主动去连接集群，所以需要用telnet检测
            for (HostAndPort node : nodes) {
                if (!AddressUtil.telnet(node.getHost(), node.getPort())) {
                    return false;
                }
            }
            return true;
        } finally {
            close(redisCluster);
        }
    }

    public static boolean checkRedisConnectionSentinel(ISourceDTO source) {
        RedisSourceDTO redisSourceDTO = (RedisSourceDTO) source;
        int db = StringUtils.isEmpty(redisSourceDTO.getSchema()) ? 0 : Integer.parseInt(redisSourceDTO.getSchema());
        JedisSentinelPool sentinelPool = null;
        Jedis jedis = null;
        try {
            sentinelPool = getSentinelPool(source);
            jedis = sentinelPool.getResource();
            jedis.select(db);
            return true;
        } finally {
            // 关闭资源
            close(jedis, sentinelPool);
        }
    }

    /**
     * 获取redis 哨兵模式连接池
     * @param source 数据源信息
     * @return redis 连接池
     */
    private static JedisSentinelPool getSentinelPool (ISourceDTO source) {
        RedisSourceDTO redisSourceDTO = (RedisSourceDTO) source;
        String masterName = redisSourceDTO.getMaster();
        String hostPorts = redisSourceDTO.getHostPort();
        String password = redisSourceDTO.getPassword();
        Preconditions.checkArgument(StringUtils.isNotBlank(hostPorts), "hostPort not empty");
        Set<HostAndPort> nodes = getHostAndPorts(hostPorts);
        Preconditions.checkArgument(CollectionUtils.isNotEmpty(nodes), "invalid ip and port");

        Set<String> sentinels = nodes.stream().map(hostAndPort -> hostAndPort.getHost() + ":" + hostAndPort.getPort())
                .collect(Collectors.toSet());

        JedisSentinelPool sentinelPool;
        if (StringUtils.isNotBlank(password)) {
            sentinelPool = new JedisSentinelPool(masterName, sentinels, password);
        } else {
            sentinelPool = new JedisSentinelPool(masterName, sentinels);
        }
        return sentinelPool;
    }

    /**
     * 获取redis 单机模式连接池
     * @param source 数据源信息
     * @return redis 连接池
     */
    private static JedisPool getRedisPool (ISourceDTO source) {
        RedisSourceDTO redisSourceDTO = (RedisSourceDTO) source;
        String password = redisSourceDTO.getPassword();
        String hostPort = redisSourceDTO.getHostPort();
        int db = StringUtils.isNotEmpty(redisSourceDTO.getSchema()) ? Integer.parseInt(redisSourceDTO.getSchema()) : 0;
        Matcher matcher = HOST_PORT_PATTERN.matcher(hostPort);

        Preconditions.checkArgument(matcher.find(), "hostPort Format exception");

        String host = matcher.group("host");
        String portStr = matcher.group("port");
        int port = portStr == null ? DEFAULT_PORT : Integer.parseInt(portStr);
        JedisPool pool;
        if (StringUtils.isEmpty(password)) {
            pool = new JedisPool(new GenericObjectPoolConfig(), host, port, TIME_OUT);
        } else {
            pool = new JedisPool(new GenericObjectPoolConfig(), host, port, TIME_OUT, password, db);
        }
        return pool;
    }

    /**
     * 获取redis 集群模式 客户端
     * @param source 数据源信息
     * @return redis客户端
     */
    private static JedisCluster getRedisCluster (ISourceDTO source) {
        RedisSourceDTO redisSourceDTO = (RedisSourceDTO) source;
        String hostPorts = redisSourceDTO.getHostPort();
        String password = redisSourceDTO.getPassword();
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(2);
        poolConfig.setMaxIdle(2);
        poolConfig.setMaxWaitMillis(1000);
        JedisCluster cluster;
        Preconditions.checkArgument(StringUtils.isNotBlank(hostPorts), "hostPort not empty");

        Set<HostAndPort> nodes = getHostAndPorts(hostPorts);

        Preconditions.checkArgument(CollectionUtils.isNotEmpty(nodes), "invalid ip and port");

        if (StringUtils.isNotBlank(password)) {
            cluster = new JedisCluster(nodes, 1000, 1000, 100, password, poolConfig);
        } else {
            cluster = new JedisCluster(nodes, poolConfig);
        }
        return cluster;
    }

    private static Set<HostAndPort> getHostAndPorts(String hostPorts) {
        Set<HostAndPort> nodes = new LinkedHashSet<>();
        String[] split = hostPorts.split(",");
        for (String node : split) {
            Matcher matcher = HOST_PORT_PATTERN.matcher(node);
            if (matcher.find()) {
                String host = matcher.group("host");
                String portStr = matcher.group("port");
                if (StringUtils.isNotBlank(host) && StringUtils.isNotBlank(portStr)) {
                    // 转化为int格式的端口
                    int port = Integer.parseInt(portStr);
                    nodes.add(new HostAndPort(host, port));
                }
            }
        }
        return nodes;
    }

    private static void close(Closeable... closeables) {
        try {
            if (Objects.nonNull(closeables)) {
                for (Closeable closeable : closeables) {
                    if (Objects.nonNull(closeable)) {
                        closeable.close();
                    }
                }
            }
        } catch (Exception e) {
            throw new SourceException(String.format("redis close resource error,%s", e.getMessage()), e);
        }
    }
}
