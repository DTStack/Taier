package com.dtstack.engine.master.component;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.common.enums.DataBaseType;
import com.dtstack.dtcenter.common.hadoop.HadoopConfTool;
import com.dtstack.dtcenter.common.util.DBUtil;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.enums.KerberosKey;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.authentication.util.KerberosUtil;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author jiangbo
 * @date 2019/5/31
 */
public class JDBCComponent extends BaseComponent {

    private DataBaseType dataBaseType;

    private final static String HIVE_DEFAULT_DB = "default";

    private final static String KEY_JDBC_URL = "jdbcUrl";
    private final static String KEY_USERNAME = "username";
    private final static String KEY_PASSWORD = "password";

    public JDBCComponent(Map<String, Object> allConfig) {
        super(allConfig);
    }

    public JDBCComponent(Map<String, Object> allConfig, DataBaseType dataBaseType) {
        this(allConfig);
        this.dataBaseType = dataBaseType;
    }

    @Override
    public void testConnection() throws Exception {
        String jdbcUrl = MapUtils.getString(allConfig, KEY_JDBC_URL);
        String username = MapUtils.getString(allConfig, KEY_USERNAME);
        String password = MapUtils.getString(allConfig, KEY_PASSWORD);
        String principal = MapUtils.getString(allConfig, KerberosKey.PRINCIPAL.getKey());
        String keytabPath = MapUtils.getString(allConfig, KerberosKey.KEYTAB.getKey());
        JSONObject hdfsConfig = JSONObject.parseObject(MapUtils.getString(allConfig, KerberosKey.HDFS_CONFIG.getKey()));

        if (DataBaseType.HIVE.equals(dataBaseType) || DataBaseType.CarbonData.equals(dataBaseType)) {
            jdbcUrl = String.format(jdbcUrl, HIVE_DEFAULT_DB);
        }

        if (StringUtils.isEmpty(jdbcUrl)) {
            throw new RdosDefineException(dataBaseType.getTypeName() + "数据源的jdbcUrl为空");
        }

        try {
            if (StringUtils.isNotEmpty(principal)) {
                Configuration configuration = new Configuration();
                hdfsConfig.entrySet().stream().forEach(entry -> configuration.set(entry.getKey(), entry.getValue().toString()));
                loginKerberos(configuration, principal, keytabPath, null);
                jdbcUrl = concatHiveJdbcUrl(allConfig, jdbcUrl, principal);
            }
        } catch (Exception e) {
            LOG.error("{}", e);
            throw new RdosDefineException("kerberos校验失败, Message:" + e.getMessage());
        }

        Connection conn = null;
        try {
            conn = DBUtil.getConnection(dataBaseType, jdbcUrl, username, password, null);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    @Override
    public String getJsonString() {
        processJdbcUrl();
        return MapUtils.isEmpty(allConfig) ? "{}" : JSONObject.toJSONString(allConfig);
    }

    private void processJdbcUrl(){
        String jdbcUrl = MapUtils.getString(allConfig, KEY_JDBC_URL);
        if(DataBaseType.LIBRA.equals(dataBaseType)){
            jdbcUrl = processLibraJdbcUrl(jdbcUrl);
        } else if(DataBaseType.HIVE.equals(dataBaseType) || DataBaseType.CarbonData.equals(dataBaseType)){
            jdbcUrl = processHiveJdbcUrl(jdbcUrl);
        }

        allConfig.put(KEY_JDBC_URL, jdbcUrl);
    }

    /**
     * 去掉连接中的schema参数:currentSchema=schemaName，具体schema在应用中填充
     */
    private String processLibraJdbcUrl(String jdbcUrl){
        // jdbc:postgresql://172.16.8.190:54321/test?currentSchema=dtstack
        if(!jdbcUrl.contains("?")){
            return jdbcUrl;
        }

        List<String> params = new ArrayList<>();

        String jdbcUri = jdbcUrl.split("\\?")[0];
        String paramStr = jdbcUrl.split("\\?")[1];
        String[] paramStrArray = paramStr.split("&");
        for (String param : paramStrArray) {
            if(!param.startsWith("currentSchema=")){
                params.add(param);
            }
        }

        return jdbcUri + "?" + StringUtils.join(params, "&");
    }

    private String processHiveJdbcUrl(String jdbcUrl){
        return jdbcUrl;
    }


    public static String concatHiveJdbcUrl(Map<String, Object> conf, String jdbcUrl, String principal) throws Exception {

        if (jdbcUrl.contains(";principal=")) {
            return jdbcUrl;
        }

        if (principal == null) {
            String host = MapUtils.getString(conf, HadoopConfTool.HIVE_BIND_HOST);
            if (host != null) {
                UserGroupInformation loginUser = UserGroupInformation.getLoginUser();
                principal = String.format("%s/%s@%s", loginUser.getShortUserName(), host, KerberosUtil.getDefaultRealm());
            }
        }

        if (StringUtils.isNotEmpty(principal) && !jdbcUrl.contains(";principal=")) {
            jdbcUrl = jdbcUrl + ";principal=" + principal;
        }

        return jdbcUrl;
    }
}
