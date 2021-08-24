package com.dtstack.engine.rdbs.hive;

import com.dtstack.engine.api.pojo.ClusterResource;
import com.dtstack.engine.api.pojo.ParamAction;
import com.dtstack.engine.api.pojo.lineage.Column;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.MD5Util;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.rdbs.common.AbstractRdbsClient;
import com.dtstack.engine.rdbs.common.executor.AbstractConnFactory;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/** @author dtstack tiezhu 2021/4/20 星期二 */
public class HiveClient extends AbstractRdbsClient {
    public HiveClient() {
        this.dbType = "hive";
    }

    private static final Logger LOG = LoggerFactory.getLogger(HiveClient.class);

    @Override
    protected AbstractConnFactory getConnFactory() {
        return new HiveConnFactory();
    }

    @Override
    public List<Column> getAllColumns(String tableName, String schemaName, String dbName) {

        List<List<Object>> result = new ArrayList<>();
        ResultSet res = null;
        try (Connection conn = connFactory.getConn();
                Statement statement = conn.createStatement()) {
            String descSql = "desc " + tableName;
            if (StringUtils.isNotEmpty(dbName)) {
                statement.execute("use " + dbName);
            }
            if (statement.execute(descSql)) {
                res = statement.getResultSet();
                int columns = res.getMetaData().getColumnCount();
                List<Object> columnName = Lists.newArrayList();
                int timeStamp = 0;
                SimpleDateFormat dateFormat = null;
                for (int i = 1; i <= columns; i++) {
                    String name = res.getMetaData().getColumnName(i);
                    if (name.contains(".")) {
                        name = name.split("\\.")[1];
                    }
                    if ("current_timestamp()".equalsIgnoreCase(name)) {
                        // current_timestamp() 需要转为 时间
                        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                        timeStamp = i;
                    }
                    columnName.add(name);
                }
                result.add(columnName);
                while (res.next()) {
                    List<Object> objects = Lists.newArrayList();
                    for (int i = 1; i <= columns; i++) {
                        if (i == timeStamp) {
                            if (null != dateFormat) {
                                objects.add(dateFormat.format(res.getObject(i)));
                                continue;
                            }
                        }
                        objects.add(res.getObject(i));
                    }
                    result.add(objects);
                }
            }
        } catch (Exception e) {
            throw new RdosDefineException("getColumnsList exception");
        } finally {
            if (null != res) {
                try {
                    res.close();
                } catch (SQLException e) {
                    LOG.error("close result exception");
                }
            }
        }
        return parseColumnInfo(result, tableName);
    }

    private List<Column> parseColumnInfo(List<List<Object>> result, String tableName) {

        List<Column> columns = new ArrayList<>();
        List<Column> part = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(result)) {
            result.remove(0);
            boolean isPart = false;
            Column column;
            for (List<Object> objects : result) {
                if (objects.get(0) == null
                        || StringUtils.isEmpty(objects.get(0).toString().trim())
                        || objects.get(0).toString().contains("# Partition Information")) {
                    continue;
                }

                column = new Column();
                column.setTable(tableName);
                column.setName(String.valueOf(objects.get(0)));
                column.setType(String.valueOf(objects.get(1)));
                if (objects.get(2) != null) {
                    column.setComment(String.valueOf(objects.get(2)));
                }

                if (!isPart) {
                    if (String.valueOf(objects.get(0)).contains("# col_name")) {
                        isPart = true;
                        continue;
                    }

                    column.setIndex(columns.size());
                    columns.add(column);
                } else {
                    column.setIndex(part.size());
                    part.add(column);
                }
            }

            if (part.size() > 0) {
                columns = columns.subList(0, columns.size() - part.size());
            }
        }
        return columns;
    }

    public static void main(String[] args) throws IOException {

        FileInputStream fileInputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;

        try {
            System.setProperty("HADOOP_USER_NAME", "admin");

            // input params json file path
            String filePath = args[0];
            File paramsFile = new File(filePath);
            fileInputStream = new FileInputStream(paramsFile);
            inputStreamReader = new InputStreamReader(fileInputStream);
            reader = new BufferedReader(inputStreamReader);
            String request = reader.readLine();
            Map params = PublicUtil.jsonStrToObject(request, Map.class);
            ParamAction paramAction = PublicUtil.mapToObject(params, ParamAction.class);
            JobClient jobClient = new JobClient(paramAction);

            String pluginInfo = jobClient.getPluginInfo();
            Properties properties = PublicUtil.jsonStrToObject(pluginInfo, Properties.class);
            String md5plugin = MD5Util.getMd5String(pluginInfo);
            properties.setProperty("md5sum", md5plugin);

            HiveClient client = new HiveClient();
            client.init(properties);

            ClusterResource clusterResource = client.getClusterResource();

            LOG.info("submit success!");
            LOG.info(clusterResource.toString());
            System.exit(0);
        } catch (Exception e) {
            LOG.error("submit error!", e);
        } finally {
            if (reader != null) {
                reader.close();
                inputStreamReader.close();
                fileInputStream.close();
            }
        }
    }
}
