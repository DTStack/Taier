package com.dtstack.taier.datasource.plugin.rdbms;

import com.dtstack.taier.datasource.plugin.common.DtClassThreadFactory;
import com.dtstack.taier.datasource.plugin.common.exception.ErrorCode;
import com.dtstack.taier.datasource.plugin.common.exception.IErrorPattern;
import com.dtstack.taier.datasource.plugin.common.service.ErrorAdapterImpl;
import com.dtstack.taier.datasource.plugin.common.service.IErrorAdapter;
import com.dtstack.taier.datasource.plugin.common.utils.DBUtil;
import com.dtstack.taier.datasource.plugin.common.utils.MD5Util;
import com.dtstack.taier.datasource.plugin.common.utils.PropertiesUtil;
import com.dtstack.taier.datasource.plugin.common.utils.ReflectUtil;
import com.dtstack.taier.datasource.plugin.common.utils.SqlFormatUtil;
import com.dtstack.taier.datasource.api.dto.SqlQueryDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.RdbmsSourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 通用 IClient 客户端接口
 *
 * @author ：nanqi
 * date：Created in 下午3:38 2020/01/17
 * company: www.dtstack.com
 */
@Slf4j
public class ConnFactory {

    protected String driverName = null;

    // 错误匹配规则类，需要各个数据源去实现该规则接口并在创建连接工厂时初始化该成员变量
    protected IErrorPattern errorPattern = null;

    // 异常适配器
    protected final IErrorAdapter errorAdapter = new ErrorAdapterImpl();

    protected String testSql;

    private static final ConcurrentHashMap<String, Object> HIKARI_DATA_SOURCES = new ConcurrentHashMap<>();

    private AtomicBoolean isFirstLoaded = new AtomicBoolean(true);

    private static final String CP_POOL_KEY = "url:%s,username:%s,password:%s,properties:%s";

    /**
     * 线程池 - 用于部分数据源获取连接超时处理
     */
    protected static ExecutorService executor = new ThreadPoolExecutor(5, 10, 1L, TimeUnit.MINUTES, new LinkedBlockingQueue<>(1000), new DtClassThreadFactory("connFactory"));

    protected void init() throws ClassNotFoundException {
        // 减少加锁开销
        if (!isFirstLoaded.get()) {
            return;
        }

        synchronized (ConnFactory.class) {
            if (isFirstLoaded.get()) {
                Class.forName(driverName);
                isFirstLoaded.set(false);
            }
        }
    }

    /**
     * 获取连接，对抛出异常进行统一处理，统一入口
     *
     * @param sourceDTO 数据源信息
     * @return jdbc connection
     * @throws Exception 异常信息
     */
    public Connection getConn(ISourceDTO sourceDTO) throws Exception {
        if (sourceDTO == null) {
            throw new SourceException("source is null");
        }
        try {
            RdbmsSourceDTO rdbmsSourceDTO = (RdbmsSourceDTO) sourceDTO;
            // 先判断 RdbmsSourceDTO 中有没有 connection
            //
            boolean isStart = rdbmsSourceDTO.getPoolConfig() != null;
            Connection connection = isStart && MapUtils.isEmpty(rdbmsSourceDTO.getKerberosConfig()) ?
                    getCpConn(rdbmsSourceDTO) : getSimpleConn(rdbmsSourceDTO);

            return setSchema(connection, rdbmsSourceDTO.getSchema());
        } catch (Exception e) {
            // 对异常进行统一处理
            throw new SourceException(errorAdapter.connAdapter(e.getMessage(), errorPattern), e);
        }
    }

    /**
     * 从连接池获取连接
     *
     * @param source 数据源信息
     * @return jdbc connection
     * @throws Exception 异常信息
     */
    private Connection getCpConn(ISourceDTO source) throws Exception {
        RdbmsSourceDTO rdbmsSourceDTO = (RdbmsSourceDTO) source;
        String poolKey = getPrimaryKey(rdbmsSourceDTO);
        log.info("datasource connected(Hikari), url : {}, userName : {}, kerberosConfig : {}", rdbmsSourceDTO.getUrl(), rdbmsSourceDTO.getUsername(), rdbmsSourceDTO.getKerberosConfig());
        HikariDataSource hikariData = (HikariDataSource) HIKARI_DATA_SOURCES.get(poolKey);
        if (hikariData == null) {
            synchronized (ConnFactory.class) {
                hikariData = (HikariDataSource) HIKARI_DATA_SOURCES.get(poolKey);
                if (hikariData == null) {
                    hikariData = transHikari(source);
                    HIKARI_DATA_SOURCES.put(poolKey, hikariData);
                }
            }
        }

        return hikariData.getConnection();
    }

    /**
     * 获取普通连接
     *
     * @param source 数据源信息
     * @return jdbc connection
     * @throws Exception 异常信息
     */
    protected Connection getSimpleConn(ISourceDTO source) throws Exception {
        RdbmsSourceDTO rdbmsSourceDTO = (RdbmsSourceDTO) source;
        init();
        DriverManager.setLoginTimeout(30);
        log.info("datasource connected, url : {}, userName : {}, kerberosConfig : {}", rdbmsSourceDTO.getUrl(), rdbmsSourceDTO.getUsername(), rdbmsSourceDTO.getKerberosConfig());
        return DriverManager.getConnection(rdbmsSourceDTO.getUrl(), PropertiesUtil.convertToProp(rdbmsSourceDTO));
    }

    public Boolean testConn(ISourceDTO source) {
        Connection conn = null;
        Statement statement = null;
        try {
            conn = getConn(source);
            if (StringUtils.isBlank(testSql)) {
                conn.isValid(5);
            } else {
                statement = conn.createStatement();
                statement.execute((testSql));
            }
            return true;
        } catch (Exception e) {
            throw new SourceException(e.getMessage(), e);
        } finally {
            DBUtil.closeDBResources(null, statement, conn);
        }
    }

    /**
     * 测试连通性, 不抛异常、不关闭连接
     *
     * @param connection jdbc 连接
     * @return 是否成功
     */
    public boolean testConnection(Connection connection) {
        Statement statement = null;
        try {
            if (StringUtils.isBlank(testSql)) {
                connection.isValid(5);
            } else {
                statement = connection.createStatement();
                statement.execute((testSql));
            }
            return true;
        } catch (Exception e) {
            log.error("test connection error.", e);
            return false;
        } finally {
            DBUtil.closeDBResources(null, statement, null);
        }
    }

    /**
     * sourceDTO 转化为 HikariDataSource
     *
     * @param source 数据源信息
     * @return HikariDataSource
     */
    protected HikariDataSource transHikari(ISourceDTO source) {
        RdbmsSourceDTO rdbmsSourceDTO = (RdbmsSourceDTO) source;
        HikariDataSource hikariData = new HikariDataSource();

        // 设置 driverClassName
        String driverClassName = getDriverClassName(source);
        hikariData.setDriverClassName(driverClassName);
        hikariData.setUsername(rdbmsSourceDTO.getUsername());
        hikariData.setPassword(rdbmsSourceDTO.getPassword());
        hikariData.setJdbcUrl(rdbmsSourceDTO.getUrl());
        hikariData.setConnectionInitSql(testSql);

        hikariData.setConnectionTestQuery(testSql);
        hikariData.setConnectionTimeout(rdbmsSourceDTO.getPoolConfig().getConnectionTimeout());
        hikariData.setIdleTimeout(rdbmsSourceDTO.getPoolConfig().getIdleTimeout());
        hikariData.setMaxLifetime(rdbmsSourceDTO.getPoolConfig().getMaxLifetime());
        hikariData.setMaximumPoolSize(rdbmsSourceDTO.getPoolConfig().getMaximumPoolSize());
        hikariData.setMinimumIdle(rdbmsSourceDTO.getPoolConfig().getMinimumIdle());
        hikariData.setReadOnly(rdbmsSourceDTO.getPoolConfig().getReadOnly());

        // 设置自定义参数
        Properties properties = PropertiesUtil.convertToProp(rdbmsSourceDTO);
        for (Object key : properties.keySet()) {
            hikariData.addDataSourceProperty(key.toString(), properties.get(key));
        }
        return hikariData;
    }

    protected String getDriverClassName(ISourceDTO source) {
        return driverName;
    }

    /**
     * 根据数据源信息获取唯一 key
     *
     * @param source 数据源信息
     * @return 数据源唯一 key
     */
    protected String getPrimaryKey(ISourceDTO source) {
        RdbmsSourceDTO rdbmsSourceDTO = (RdbmsSourceDTO) source;
        String properties = ReflectUtil.fieldExists(RdbmsSourceDTO.class, "properties") ? rdbmsSourceDTO.getProperties() : "";
        return MD5Util.getMd5String(String.format(CP_POOL_KEY, rdbmsSourceDTO.getUrl(), rdbmsSourceDTO.getUsername(), rdbmsSourceDTO.getPassword(), properties));
    }

    /**
     * 给 connection 设置 schema
     *
     * @param conn   jdbc connection
     * @param schema schema 信息
     * @return 设置 schema 后的 jdbc connection
     */
    public Connection setSchema(Connection conn, String schema) {
        if (StringUtils.isBlank(schema)) {
            return conn;
        }
        try {
            conn.setSchema(schema);
        } catch (Throwable e) {
            log.warn(String.format("setting schema exception : %s", e.getMessage()), e);
        }
        return conn;
    }

    /**
     * 多条 sql 拆分
     *
     * @param queryDTO 查询条件
     * @return 拆分后的 sql
     */
    protected List<String> buildSqlList(SqlQueryDTO queryDTO) {
        return SqlFormatUtil.splitIgnoreQuota(queryDTO.getSql(), ';');
    }

    protected boolean supportProcedure(String sql) {
        String[] sqls = sql.trim().split("\\s+", 2);
        if (sqls.length >= 2) {
            return "BEGIN".equalsIgnoreCase(sqls[0]);
        }
        return false;
    }

    protected String getCreateProcHeader(String procName) {
        throw new SourceException(ErrorCode.NOT_SUPPORT.getDesc());
    }

    protected String getCreateProcTail() {
        return "";
    }

    protected boolean supportTransaction() {
        return true;
    }

    protected boolean supportSelectSql() {
        return false;
    }

    protected String getCallProc(String procName) {
        return String.format("call \"%s\"()", procName);
    }

    protected String getDropProc(String procName) {
        return String.format("DROP PROCEDURE \"%s\"", procName);
    }
}
