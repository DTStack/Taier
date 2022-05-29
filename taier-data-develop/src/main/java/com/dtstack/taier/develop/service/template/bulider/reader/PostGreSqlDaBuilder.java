package com.dtstack.taier.develop.service.template.bulider.reader;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.loader.client.ClientCache;
import com.dtstack.dtcenter.loader.client.IClient;
import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.dtstack.dtcenter.loader.dto.source.PostgresqlSourceDTO;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.common.util.DataSourceUtils;
import com.dtstack.taier.dao.domain.DsInfo;
import com.dtstack.taier.develop.common.template.Reader;
import com.dtstack.taier.develop.dto.devlop.ColumnDTO;
import com.dtstack.taier.develop.dto.devlop.ConnectionDTO;
import com.dtstack.taier.develop.dto.devlop.TaskResourceParam;
import com.dtstack.taier.develop.enums.develop.CollectType;
import com.dtstack.taier.develop.enums.develop.DAoperators;
import com.dtstack.taier.develop.enums.develop.RdbmsDaType;
import com.dtstack.taier.develop.enums.develop.SlotConfigEnum;
import com.dtstack.taier.develop.service.datasource.impl.DsInfoService;
import com.dtstack.taier.develop.service.template.PluginName;
import com.dtstack.taier.develop.service.template.bulider.db.DbBuilder;
import com.dtstack.taier.develop.service.template.bulider.db.PostgreSQLDbBuilder;
import com.dtstack.taier.develop.service.template.oracle.OraclePollReader;
import com.dtstack.taier.develop.service.template.oracle.OraclePollReaderParam;
import com.dtstack.taier.develop.service.template.postgresql.PostGreSqlCdcReader;
import com.dtstack.taier.develop.service.template.postgresql.PostGreSqlCdcReaderParam;
import com.dtstack.taier.develop.service.template.postgresql.PostGreSqlPollReader;
import com.dtstack.taier.develop.service.template.rdbms.RdbmsPollReaderParam;
import com.dtstack.taier.develop.utils.JsonUtils;
import com.dtstack.taier.develop.utils.develop.sync.util.ColumnUtil;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import static com.dtstack.taier.common.util.DataSourceUtils.PASSWORD;
import static com.dtstack.taier.common.util.DataSourceUtils.USERNAME;

/**
 * @author huoyun
 * @date 2021/4/13 2:00 下午
 * @company: www.dtstack.com
 */
@Component
public class PostGreSqlDaBuilder implements DaReaderBuilder {

    private static Map<Integer, DaReaderBuilder> builderMap = new HashMap<>();
    @Autowired
    private DsInfoService dataSourceCenterService;

    @PostConstruct
    private void init() {
        if (dataSourceCenterService == null) {
            throw new RdosDefineException("streamDataSourceService should not be null");
        }
        builderMap.put(RdbmsDaType.CDC.getCode(), new PostGreSqlDaCDCBuilder(dataSourceCenterService));
        builderMap.put(RdbmsDaType.Poll.getCode(), new PostGreSqlDaPollInnerBuilder(dataSourceCenterService));
    }

    @Override
    public void setReaderJson(TaskResourceParam param) {
        builderMap.get(MapUtils.getInteger(param.getSourceMap(), RDBMS_DA_TYPE, RdbmsDaType.Poll.getCode()))
                .setReaderJson(param);
    }

    @Override
    public Reader daReaderBuild(TaskResourceParam param) throws Exception {
        return builderMap.get(MapUtils.getInteger(param.getSourceMap(), RDBMS_DA_TYPE, RdbmsDaType.Poll.getCode()))
                .daReaderBuild(param);
    }

    @Override
    public Map<String, Object> getParserSourceMap(Map<String, Object> sourceMap) {
        return builderMap.get(MapUtils.getInteger(sourceMap, RDBMS_DA_TYPE, RdbmsDaType.Poll.getCode()))
                .getParserSourceMap(sourceMap);
    }

    @Override
    public DataSourceType getDataSourceType() {
        return DataSourceType.PostgreSQL;
    }


    /**
     * Poll方式内联实现
     */
    public static class PostGreSqlDaPollInnerBuilder implements DaReaderBuilder {
        private DsInfoService dataSourceCenterService;

        public PostGreSqlDaPollInnerBuilder(DsInfoService dataSourceCenterService) {
            this.dataSourceCenterService = dataSourceCenterService;
        }

        @Override
        public void setReaderJson(TaskResourceParam param) {
            Map<String, Object> map = param.getSourceMap();

            Long sourceId = Long.parseLong(map.get("sourceId").toString());
            DsInfo source = dataSourceCenterService.getOneById(sourceId);
            map.put("source", source);
            map.put("sourceIds", Arrays.asList(sourceId));
            map.put("type", source.getDataTypeCode());
            map.put("dataName", source.getDataName());

            //for hive writer
            //String tableName = MapUtils.getString(map, "tableName");
            // List<String> table = Lists.newArrayList(tableName);
            //map.put("table", table);
        }


        @Override
        public Reader daReaderBuild(TaskResourceParam param) throws Exception {
            setReaderJson(param);
            Map<String, Object> sourceMap = param.getSourceMap();
            Map<String, Object> clone = new HashMap<>(sourceMap);
            List<ConnectionDTO> connectionDTOList = new ArrayList<>();
            String tableName = MapUtils.getString(sourceMap, "tableName");
            if (StringUtils.isEmpty(tableName) || "null".equals(tableName)) {
                tableName = MapUtils.getString(sourceMap, "table");
            }
            String schema = MapUtils.getString(sourceMap, "schema");

            //设置链接信息
            DsInfo dataSource = (DsInfo) clone.get("source");
            JSONObject json = JSONObject.parseObject(dataSource.getDataJson());
            ConnectionDTO connectionDTO = new ConnectionDTO();
            connectionDTO.setJdbcUrl(Lists.newArrayList(json.getString(JDBC_URL)));
            connectionDTO.setSchema(schema);
            connectionDTO.setTable(Lists.newArrayList(tableName));
            connectionDTOList.add(connectionDTO);

            if (Objects.equals(param.getTaskType(), EScheduleJobType.SYNC.getVal())) {
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
                RdbmsPollReaderParam readerParam = JsonUtils.objectToObject(param.getSourceMap(), RdbmsPollReaderParam.class);
                PostGreSqlPollReader reader = new PostGreSqlPollReader();
                reader.setUsername(json.getString(USERNAME));
                reader.setPassword(json.getString(PASSWORD));
                reader.setWhere(readerParam.getWhere());
                reader.setSplitPk(readerParam.getSplitPK());
                List columns = ColumnUtil.getColumns(readerParam.getColumn(), PluginName.PostgreSQL_R);
                if (CollectionUtils.isNotEmpty(columns)) {
                    reader.setColumn(JSONArray.parseArray(JSON.toJSONString(columns), ColumnDTO.class));
                }
                // 增量配置
                reader.setIncreColumn(Optional.ofNullable(readerParam.getIncreColumn()).orElse(""));
                reader.setStartLocation("");
                reader.setConnection(connectionDTOList);
                reader.setExtralConfig(readerParam.getExtralConfig());
                reader.setSourceIds(readerParam.getSourceIds());
                reader.setPolling(null);
                return reader;
            }
            return new OraclePollReader();
        }

        @Override
        public Map<String, Object> getParserSourceMap(Map<String, Object> sourceMap) {
            try {
                OraclePollReaderParam param = JsonUtils.objectToObject(sourceMap, OraclePollReaderParam.class);
                return JsonUtils.objectToMap(param);
            } catch (Exception e) {
                throw new RdosDefineException("getParserSourceMap error", e);
            }
        }

        @Override
        public DataSourceType getDataSourceType() {
            return DataSourceType.PostgreSQL;
        }
    }

    /**
     * Poll方式内联实现
     */
    public static class PostGreSqlDaCDCBuilder implements DaReaderBuilder {
        private DsInfoService dataSourceCenterService;

        public PostGreSqlDaCDCBuilder(DsInfoService dataSourceCenterService) {
            this.dataSourceCenterService = dataSourceCenterService;
        }

        @Override
        public void setReaderJson(TaskResourceParam param) {
            Map<String, Object> sourceMap = param.getSourceMap();
            Long sourceId = Long.parseLong(sourceMap.get("sourceId").toString());
            DsInfo source = dataSourceCenterService.getOneById(sourceId);
            sourceMap.put("source",source);
            //全部表
            Boolean allTable = MapUtils.getBoolean(sourceMap, "allTable");
            if (BooleanUtils.isTrue(allTable)) {
                String schema = MapUtils.getString(sourceMap, "schema");
                ISourceDTO sourceDTO = dataSourceCenterService.getSourceDTO(source.getId());
                DbBuilder dbBuilder = new PostgreSQLDbBuilder();
                List<String> tableList = dbBuilder.listTablesBySchema(schema, null, sourceDTO, null);
                sourceMap.put("table", tableList);
            }
        }


        /**
         * 获取当前的database
         * @param info
         * @return
         */
        public String getPostGreDataBase(JSONObject info) {
            String jdbcUrl = DataSourceUtils.getJdbcUrl(info);
            String password = DataSourceUtils.getJdbcPassword(info);
            String userName = DataSourceUtils.getJdbcUsername(info);
            IClient client = ClientCache.getClient(DataSourceType.PostgreSQL.getVal());
            PostgresqlSourceDTO postgresqlSourceDTO = PostgresqlSourceDTO
                    .builder()
                    .url(jdbcUrl)
                    .username(userName)
                    .password(password)
                    .build();
            return client.getCurrentDatabase(postgresqlSourceDTO);
        }

        @Override
        public Reader daReaderBuild(TaskResourceParam param) throws Exception {
            setReaderJson(param);
            Map<String, Object> sourceMap = param.getSourceMap();
            DsInfo dataSource = (DsInfo) sourceMap.get("source");
            PostGreSqlCdcReaderParam readerParam = JsonUtils.objectToObject(sourceMap, PostGreSqlCdcReaderParam.class);
            PostGreSqlCdcReader reader = new PostGreSqlCdcReader();

            String schema = readerParam.getSchema();

            JSONObject json = DataSourceUtils.getDataSourceJson(dataSource.getDataJson());
            String jdbc = DataSourceUtils.getJdbcUrl(json);
            String pwd = DataSourceUtils.getJdbcPassword(json);
            String username = DataSourceUtils.getJdbcUsername(json);

            //设置链接信息
            reader.setUrl(jdbc);
            reader.setPassword(pwd);
            reader.setUsername(username);
            reader.setPavingData(readerParam.getPavingData());
            reader.setDatabaseName(getPostGreDataBase(json));
            if (CollectType.LSN.getCollectType().equals(readerParam.getCollectType())) {
                reader.setLsn(readerParam.getLsn());
            }
            reader.setStatusInterval(readerParam.getStatusInterval());
            //选择已有的slot
            if (SlotConfigEnum.USE_EXISTED.getSlotConfig().equals(readerParam.getSlotConfig())) {
                reader.setSlotName(readerParam.getSlotName());
            }
            //创建slot
            else if (SlotConfigEnum.CREATE_SLOT.getSlotConfig().equals(readerParam.getSlotConfig())) {
                reader.setTemporary(readerParam.getTemporary());
                reader.setAllowCreateSlot(Boolean.TRUE);
            }

            List<String> tempTableList = readerParam.getTable();
            tempTableList = CollectionUtils.isEmpty(tempTableList) ? new ArrayList<>() : tempTableList;

            tempTableList = tempTableList.stream().map(table -> transferTableName(schema, table)).collect(Collectors.toList());
            reader.setTableList(tempTableList);

            //解析操作参数
            StringJoiner catJoiner = new StringJoiner(",");
            List<Integer> intCats = readerParam.getCat();
            if (CollectionUtils.isNotEmpty(intCats)) {
                for (Integer cat : intCats) {
                    catJoiner.add(DAoperators.getByVal(cat).name());
                }
            }
            reader.setCat(catJoiner.toString());

            return reader;
        }

        /**
         * schema.tableName
         *
         * @param schema
         * @param tableName
         * @return
         */
        private String transferTableName(String schema, String tableName) {
            return String.format("%s.%s", schema, tableName);
        }

        @Override
        public Map<String, Object> getParserSourceMap(Map<String, Object> sourceMap) {
            try {
                PostGreSqlCdcReaderParam param = JsonUtils.objectToObject(sourceMap, PostGreSqlCdcReaderParam.class);
                return JsonUtils.objectToMap(param);
            } catch (Exception e) {
                throw new RdosDefineException(String.format("getParserSourceMap error,Caused by: %s", e.getMessage()), e);
            }
        }

        @Override
        public DataSourceType getDataSourceType() {
            return DataSourceType.PostgreSQL;
        }
    }

}
