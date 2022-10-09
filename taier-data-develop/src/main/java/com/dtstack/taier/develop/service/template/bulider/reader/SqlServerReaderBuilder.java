package com.dtstack.taier.develop.service.template.bulider.reader;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.datasource.api.base.ClientCache;
import com.dtstack.taier.datasource.api.client.IClient;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.RdbmsSourceDTO;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.common.util.DataSourceUtils;
import com.dtstack.taier.dao.domain.DsInfo;
import com.dtstack.taier.develop.common.template.Reader;
import com.dtstack.taier.develop.dto.devlop.ColumnDTO;
import com.dtstack.taier.develop.dto.devlop.ConnectionDTO;
import com.dtstack.taier.develop.dto.devlop.TaskResourceParam;
import com.dtstack.taier.develop.enums.develop.DAoperators;
import com.dtstack.taier.develop.enums.develop.RdbmsDaType;
import com.dtstack.taier.develop.service.datasource.impl.DsInfoService;
import com.dtstack.taier.develop.service.template.PluginName;
import com.dtstack.taier.develop.service.template.bulider.db.DbBuilder;
import com.dtstack.taier.develop.service.template.bulider.db.SqlServerDbBuilder;
import com.dtstack.taier.develop.service.template.rdbms.RdbmsPollReaderParam;
import com.dtstack.taier.develop.service.template.sqlserver.SqlServerCdcReader;
import com.dtstack.taier.develop.service.template.sqlserver.SqlServerCdcReaderParam;
import com.dtstack.taier.develop.service.template.sqlserver.SqlServerPollReader;
import com.dtstack.taier.develop.utils.JsonUtils;
import com.dtstack.taier.develop.utils.develop.sync.util.ColumnUtil;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.dtstack.taier.common.util.DataSourceUtils.PASSWORD;
import static com.dtstack.taier.common.util.DataSourceUtils.USERNAME;

/**
 * sqlServer builder
 *
 * @author ：wangchuan
 * date：Created in 上午10:53 2021/7/7
 * company: www.dtstack.com
 */
@Component
public class SqlServerReaderBuilder implements DaReaderBuilder {
    @Autowired
    private DsInfoService dataSourceCenterService;

    private static final Map<Integer, DaReaderBuilder> BUILDER_MAP = new HashMap<>();

    @PostConstruct
    private void init() {
        BUILDER_MAP.put(RdbmsDaType.CDC.getCode(), new SqlServerCdcDABuilder(dataSourceCenterService));
        BUILDER_MAP.put(RdbmsDaType.Poll.getCode(), new SqlServerPollReaderBuilder(dataSourceCenterService));
    }

    @Override
    public void setReaderJson(TaskResourceParam param) {
        BUILDER_MAP.get(MapUtils.getInteger(param.getSourceMap(), RDBMS_DA_TYPE, RdbmsDaType.CDC.getCode()))
                .setReaderJson(param);
    }

    @Override
    public Reader daReaderBuild(TaskResourceParam param) throws Exception {
        return BUILDER_MAP.get(MapUtils.getInteger(param.getSourceMap(), RDBMS_DA_TYPE, RdbmsDaType.CDC.getCode()))
                .daReaderBuild(param);
    }

    @Override
    public Map<String, Object> getParserSourceMap(Map<String, Object> sourceMap) {
        return BUILDER_MAP.get(MapUtils.getInteger(sourceMap, RDBMS_DA_TYPE, RdbmsDaType.CDC.getCode()))
                .getParserSourceMap(sourceMap);
    }

    @Override
    public DataSourceType getDataSourceType() {
        return DataSourceType.SQLSERVER_2017_LATER;
    }


    /**
     * sqlServer cdc reader builder
     */
    public static class SqlServerCdcDABuilder implements DaReaderBuilder {

        private DsInfoService dataSourceCenterService;

        public SqlServerCdcDABuilder(DsInfoService dataSourceCenterService) {
            this.dataSourceCenterService = dataSourceCenterService;
        }

        @Override
        public void setReaderJson(TaskResourceParam param) {
            Map<String, Object> sourceMap = param.getSourceMap();
            Long sourceId = Long.parseLong(sourceMap.get("sourceId").toString());
            DsInfo source = dataSourceCenterService.getOneById(sourceId);
            sourceMap.put("source", source);
            //全部表
            Boolean allTable = MapUtils.getBoolean(sourceMap, "allTable");
            if (BooleanUtils.isTrue(allTable)) {
                String schema = MapUtils.getString(sourceMap, "schema");
                DbBuilder dbBuilder = new SqlServerDbBuilder();
                ISourceDTO sourceDTO = dataSourceCenterService.getSourceDTO(source.getId());
                List<String> tableList = dbBuilder.listTablesBySchema(schema, null, sourceDTO, null);
                sourceMap.put("table", tableList);
            }
        }

        @Override
        public Reader daReaderBuild(TaskResourceParam param) throws Exception {
            setReaderJson(param);
            Map<String, Object> sourceMap = param.getSourceMap();
            DsInfo dataSource = (DsInfo) sourceMap.get("source");
            SqlServerCdcReaderParam readerParam = JsonUtils.objectToObject(sourceMap, SqlServerCdcReaderParam.class);
            SqlServerCdcReader reader = new SqlServerCdcReader();

            String schema = readerParam.getSchema();

            JSONObject json = DataSourceUtils.getDataSourceJson(dataSource.getDataJson());
            String jdbc = DataSourceUtils.getJdbcUrl(json);
            String pwd = DataSourceUtils.getJdbcPassword(json);
            String username = DataSourceUtils.getJdbcUsername(json);

            reader.setUrl(jdbc);
            reader.setPassword(pwd);
            reader.setUsername(username);
            reader.setPavingData(readerParam.getPavingData());
            reader.setDatabaseName(getDataBaseName(jdbc));
            reader.setLsn(readerParam.getLsn());
            reader.setPollInterval(readerParam.getPollInterval());

            List<String> tempList = readerParam.getTable();
            tempList = CollectionUtils.isEmpty(tempList) ? new ArrayList<>() : tempList;

            tempList = tempList.stream().map(s -> transferTableName(schema, s)).collect(Collectors.toList());
            reader.setTableList(tempList);

            //解析操作数
            //解析操作参数
            StringJoiner catJoiner = new StringJoiner(",");
            List<Integer> intCats = readerParam.getCat();
            if (CollectionUtils.isNotEmpty(intCats)) {
                for (Integer cat : intCats) {
                    catJoiner.add(DAoperators.getByVal(cat).name());
                }
            }
            reader.setCat(catJoiner.toString());
            reader.setExtralConfig(readerParam.getExtralConfig());
            return reader;
        }

        @Override
        public Map<String, Object> getParserSourceMap(Map<String, Object> sourceMap) {
            try {
                SqlServerCdcReaderParam param = JsonUtils.objectToObject(sourceMap, SqlServerCdcReaderParam.class);
                return JsonUtils.objectToMap(param);
            } catch (Exception e) {
                throw new RdosDefineException(String.format("getParserSourceMap error,Caused by: %s", e.getMessage()), e);
            }
        }

        @Override
        public DataSourceType getDataSourceType() {
            return DataSourceType.SQLSERVER_2017_LATER;
        }

        public static String getDataBaseName(String jdbc) {
            String regex = "jdbc:sqlserver://(?<host>[0-9a-zA-Z\\.-]+):(?<port>\\d+).*((?i)databasename|database)=(?<db>[0-9a-zA-Z_%\\.]+)*";
            Pattern compile = Pattern.compile(regex);
            Matcher matcher = compile.matcher(jdbc);
            if (matcher.find()) {
                return matcher.group("db");
            }
            throw new RdosDefineException("jdbcUrl中必须指定使用的库");
        }

        /**
         * sqlServer中给schema和tableName添加中括号如 [schema].[tableName]
         * 避免schema或者tableName中有.或者其他特殊情况
         *
         * @param schema
         * @param tableName
         * @return
         */
        private String transferTableName(String schema, String tableName) {
            if (!schema.startsWith("[") || !schema.endsWith("]")) {
                schema = String.format("[%s]", schema);
            }
            if (!tableName.startsWith("[") || !tableName.endsWith("]")) {
                tableName = String.format("[%s]", tableName);
            }
            return String.format("%s.%s", schema, tableName);
        }

    }

    /**
     * sqlServer 间隔轮询 reader builder
     */
    public static class SqlServerPollReaderBuilder extends RdbmsReaderBuilder {

        private DsInfoService dataSourceCenterService;

        public SqlServerPollReaderBuilder(DsInfoService dataSourceCenterService) {
            this.dataSourceCenterService = dataSourceCenterService;
        }

        @Override
        public void setReaderJson(TaskResourceParam param) {
            List<Long> sourceIds = new ArrayList<>();

            Map<String, Object> map = param.getSourceMap();
            Long sourceId = Long.parseLong(map.get("sourceId").toString());
            DsInfo source = dataSourceCenterService.getOneById(sourceId);
            map.put("source", source);
            if (Objects.equals(param.getTaskType(), EScheduleJobType.SYNC.getVal())) {
                sourceIds.add(sourceId);
                JSONObject json = JSON.parseObject(source.getDataJson());
                map.put("type", source.getDataTypeCode());
                map.put("password", JsonUtils.getStringDefaultEmpty(json, PASSWORD));
                map.put("username", JsonUtils.getStringDefaultEmpty(json, USERNAME));
                map.put("jdbcUrl", JsonUtils.getStringDefaultEmpty(json, JDBC_URL));
                map.put("sourceIds", sourceIds);
            } else if (Objects.equals(param.getTaskType(), EScheduleJobType.DATA_ACQUISITION.getVal())) {
                //for hive writer
                String tableName = MapUtils.getString(map, "tableName");
                List<String> table = Lists.newArrayList(tableName);
                map.put("table", table);
            }
        }

        @Override
        public Reader daReaderBuild(TaskResourceParam param) throws Exception {
            setReaderJson(param);
            Map<String, Object> sourceMap = param.getSourceMap();
            SqlServerPollReader sqlServerPollReader = new SqlServerPollReader();
            if (Objects.equals(param.getTaskType(), EScheduleJobType.DATA_ACQUISITION.getVal())) {
                IClient client = ClientCache.getClient(DataSourceType.SQLServer.getVal());
                DsInfo dataSource = (DsInfo) sourceMap.get("source");
                RdbmsSourceDTO sourceDTO = (RdbmsSourceDTO) dataSourceCenterService.getSourceDTO(dataSource.getId());
                return daReaderBuild(param,sqlServerPollReader , client, sourceDTO);
            } else if (Objects.equals(param.getTaskType(), EScheduleJobType.SYNC.getVal())) {

                DsInfo dataSource = (DsInfo) sourceMap.get("source");
                JSONObject json = DataSourceUtils.getDataSourceJson(dataSource.getDataJson());

                RdbmsPollReaderParam readerParam = JsonUtils.objectToObject(sourceMap, RdbmsPollReaderParam.class);

                sqlServerPollReader.setPollingInterval(readerParam.getPollingInterval());
                List<ConnectionDTO> connectionDTOList = new ArrayList<>();
                List<String> tables = JSON.parseArray(JSON.toJSONString(readerParam.getTable()), String.class);
                List<String> newTables = new ArrayList<>();

                tables.forEach(table->{
                    String[] split = table.split("\\.");
                    newTables.add(split[1].replace("[","").replace("]",""));
                });
                String[] first = tables.get(0).split("\\.");
                String schema =first[0].replace("[","").replace("]","");

                //设置链接信息
                ConnectionDTO connectionDTO = new ConnectionDTO();
                connectionDTO.setJdbcUrl(Lists.newArrayList(DataSourceUtils.getJdbcUrl(json)));
                connectionDTO.setTable(newTables);
                connectionDTO.setSchema(schema);
                connectionDTOList.add(connectionDTO);

                sqlServerPollReader.setConnection(connectionDTOList);
                sqlServerPollReader.setUsername(DataSourceUtils.getJdbcUsername(json));
                sqlServerPollReader.setPassword(DataSourceUtils.getJdbcPassword(json));

                Map<String, Object> settingMap = param.getSettingMap();
                //下面代码 是为了 拿到断点续传在字段列表的第几位
                if (sourceMap != null && settingMap != null) {
                    Object column = sourceMap.get("column");
                    Integer restoreColumnIndex = 0;
                    if (column != null) {
                        JSONArray colums = JSONArray.parseArray(JSONObject.toJSONString(sourceMap.get("column")));
                        for (int i = 0; i < colums.size(); i++) {
                            if (Objects.equals(colums.getJSONObject(i).getString("key"), settingMap.get("restoreColumnName"))) {
                                restoreColumnIndex = i;
                                break;
                            }
                        }
                    }
                    settingMap.put("restoreColumnIndex", restoreColumnIndex);
                    param.setSettingMap(settingMap);
                }
                sqlServerPollReader.setUsername(sourceMap.get(USERNAME).toString());
                sqlServerPollReader.setPassword(sourceMap.get(PASSWORD).toString());
                sqlServerPollReader.setWhere(readerParam.getWhere());
                sqlServerPollReader.setSplitPk(readerParam.getSplitPK());
                List columns = ColumnUtil.getColumns(readerParam.getColumn(), PluginName.SQLSERVER_POLL_R);
                if (CollectionUtils.isNotEmpty(columns)) {
                    sqlServerPollReader.setColumn(JSONArray.parseArray(JSON.toJSONString(columns), ColumnDTO.class));
                }
                // 增量配置
                sqlServerPollReader.setIncreColumn(Optional.ofNullable(readerParam.getIncreColumn()).orElse(""));
                sqlServerPollReader.setStartLocation("");
                sqlServerPollReader.setExtralConfig(readerParam.getExtralConfig());
                sqlServerPollReader.setPolling(null);
                return sqlServerPollReader;
            }
            return sqlServerPollReader;
        }

        @Override
        public DataSourceType getDataSourceType() {
            return DataSourceType.SQLServer;
        }

    }
}
