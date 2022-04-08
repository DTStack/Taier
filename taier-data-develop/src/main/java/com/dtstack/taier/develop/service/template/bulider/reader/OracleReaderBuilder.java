package com.dtstack.taier.develop.service.template.bulider.reader;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.loader.client.ClientCache;
import com.dtstack.dtcenter.loader.client.IClient;
import com.dtstack.dtcenter.loader.dto.ColumnMetaDTO;
import com.dtstack.dtcenter.loader.dto.SqlQueryDTO;
import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.dtstack.dtcenter.loader.dto.source.OracleSourceDTO;
import com.dtstack.dtcenter.loader.source.DataSourceType;
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
import com.dtstack.taier.develop.service.template.bulider.db.OracleDbBuilder;
import com.dtstack.taier.develop.service.template.oracle.OracleBinLogReader;
import com.dtstack.taier.develop.service.template.oracle.OracleBinLogReaderParam;
import com.dtstack.taier.develop.service.template.oracle.OraclePollReader;
import com.dtstack.taier.develop.service.template.oracle.OraclePollReaderParam;
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
 * Date: 2020/1/7
 * Company: www.dtstack.com
 * 内联方式实现 方便之后再做拆分
 *
 * @author xiaochen
 */
@Component
public class OracleReaderBuilder implements DaReaderBuilder {
    private static Map<Integer, DaReaderBuilder> builderMap = new HashMap<>();
    @Autowired
    private DsInfoService dataSourceCenterService;

    @PostConstruct
    private void init() {
        if (dataSourceCenterService == null) {
            throw new RdosDefineException("streamDataSourceService should not be null");
        }
        builderMap.put(RdbmsDaType.LOGMINER.getCode(), new OracleDABinlogInnerBuilder(dataSourceCenterService));
        builderMap.put(RdbmsDaType.Poll.getCode(), new OracleDAPollInnerBuilder(dataSourceCenterService));
    }

    @Override
    public void setReaderJson(TaskResourceParam param) {
        builderMap.get(MapUtils.getInteger(param.getSourceMap(), RDBMS_DA_TYPE, RdbmsDaType.LOGMINER.getCode()))
                .setReaderJson(param);
    }

    @Override
    public Reader daReaderBuild(TaskResourceParam param) throws Exception {
        return builderMap.get(MapUtils.getInteger(param.getSourceMap(), RDBMS_DA_TYPE, RdbmsDaType.LOGMINER.getCode()))
                .daReaderBuild(param);
    }

    @Override
    public Map<String, Object> getParserSourceMap(Map<String, Object> sourceMap) {
        return builderMap.get(MapUtils.getInteger(sourceMap, RDBMS_DA_TYPE, RdbmsDaType.LOGMINER.getCode()))
                .getParserSourceMap(sourceMap);
    }

    @Override
    public DataSourceType getDataSourceType() {
        return DataSourceType.Oracle;
    }

    /**
     * Poll方式内联实现
     */
    public static class OracleDAPollInnerBuilder implements DaReaderBuilder {
        private DsInfoService dataSourceCenterService;

        public OracleDAPollInnerBuilder(DsInfoService dataSourceCenterService) {
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

//            //for hive writer
//            String tableName = MapUtils.getString(map, "table");
//            List<String> table = Lists.newArrayList(tableName);
//            map.put("table", table);
        }

        @Override
        public Reader daReaderBuild(TaskResourceParam param) throws Exception {
            setReaderJson(param);
            Map<String, Object> sourceMap = param.getSourceMap();
            Map<String, Object> clone = new HashMap<>(sourceMap);
            List<ConnectionDTO> connectionDTOList = new ArrayList<>();
            String tableName = MapUtils.getString(sourceMap, "table");
            String schema = MapUtils.getString(sourceMap, "schema");
//            String schemaTableName = schema + "." + tableName;
            //设置链接信息
            DsInfo dataSource = (DsInfo) clone.get("source");
            JSONObject json = JSONObject.parseObject(dataSource.getDataJson());
            ConnectionDTO connectionDTO = new ConnectionDTO();
            connectionDTO.setJdbcUrl(Lists.newArrayList(json.getString(JDBC_URL)));
            connectionDTO.setTable((Lists.newArrayList(tableName)));
            connectionDTO.setSchema(schema);
            connectionDTOList.add(connectionDTO);

            if (Objects.equals(param.getTaskType(), EScheduleJobType.DATA_ACQUISITION.getVal())) {
                clone.put("connection", connectionDTOList);
                String username = json.getString(USERNAME);
                String password = json.getString(PASSWORD);
                clone.put("username", username);
                clone.put("password", password);
                //获取表对应的字段
                List<JSONObject> columns = null;
                try {
                    IClient client = ClientCache.getClient(DataSourceType.Oracle.getVal());
                    OracleSourceDTO sourceDTO = OracleSourceDTO
                            .builder()
                            .url(connectionDTO.getJdbcUrl().get(0))
                            .username(username)
                            .password(password)
                            .schema(schema)
                            .build();
                    SqlQueryDTO sqlQueryDTO = SqlQueryDTO.builder().tableName(tableName).build();
                    List<ColumnMetaDTO> columnMetaDTOList = client.getColumnMetaData(sourceDTO, sqlQueryDTO);
                    columns = new ArrayList<>();
                    List<String> fields = new ArrayList<>();
                    if (CollectionUtils.isNotEmpty(columnMetaDTOList)) {
                        for (ColumnMetaDTO columnMetaDTO : columnMetaDTOList) {
                            if (sourceMap.get("tableFields") == null) {
                                columns.add(JSON.parseObject(JSON.toJSONString(columnMetaDTO)));
                                fields.add(columnMetaDTO.getKey());
                            } else {
                                List<String> tableFields = (List<String>) sourceMap.get("tableFields");
                                if (tableFields.contains(columnMetaDTO.getKey())) {
                                    columns.add(JSON.parseObject(JSON.toJSONString(columnMetaDTO)));
                                    fields.add(columnMetaDTO.getKey());
                                }
                            }
                        }
                    }
                    sourceMap.put("tableFields", fields);
                } catch (Exception e) {
                    throw new RdosDefineException("获取" + tableName + "字段信息异常", e);
                }
                String increColumn = MapUtils.getString(sourceMap, "increColumn");
                int index = -1;
                for (int i = 0; i < columns.size(); i++) {
                    JSONObject column = columns.get(i);
                    String key = column.getString("key");
                    column.put("name", key);
                    if (increColumn.equals(key)) {
                        index = i;
                    }
                }

                //回写settingMap
                param.getSettingMap().put("restoreColumnIndex", String.valueOf(index));
                param.getSettingMap().put("restoreColumnName", increColumn);

                clone.put("column", columns);
                OraclePollReader reader = JsonUtils.objectToObject(clone, OraclePollReader.class);
                return reader;
            } else if (Objects.equals(param.getTaskType(), EScheduleJobType.SYNC.getVal())) {
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
                OraclePollReader reader = new OraclePollReader();
                reader.setUsername(json.getString(USERNAME));
                reader.setPassword(json.getString(PASSWORD));
                reader.setWhere(readerParam.getWhere());
                reader.setSplitPk(readerParam.getSplitPK());
                // todo 字段类型不一样
                List columns = ColumnUtil.getColumns(readerParam.getColumn(), PluginName.MySQLD_R);
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
            return DataSourceType.Oracle;
        }
    }

    /**
     * binlog方式内联实现
     */
    public static class OracleDABinlogInnerBuilder implements DaReaderBuilder {
        private DsInfoService dataSourceCenterService;

        public OracleDABinlogInnerBuilder(DsInfoService dataSourceCenterService) {
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
                String pdbName = MapUtils.getString(sourceMap, "pdbName");
                DbBuilder dbBuilder = new OracleDbBuilder();
                ISourceDTO sourceDTO = dataSourceCenterService.getSourceDTO(source.getId());
                List<String> tableList = dbBuilder.listTablesBySchema(schema, null, sourceDTO, pdbName);
                sourceMap.put("table", tableList);
            }
        }

        @Override
        public Reader daReaderBuild(TaskResourceParam param) throws Exception {
            setReaderJson(param);
            Map<String, Object> sourceMap = param.getSourceMap();
            String pdbName = MapUtils.getString(sourceMap, "pdbName");
            //全部表
            String schema = MapUtils.getString(sourceMap, "schema");
            DsInfo dataSource = (DsInfo) sourceMap.get("source");
            JSONObject json = DataSourceUtils.getDataSourceJson(dataSource.getDataJson());
            String jdbc = DataSourceUtils.getJdbcUrl(json);
            String pwd = DataSourceUtils.getJdbcPassword(json);
            String username = DataSourceUtils.getJdbcUsername(json);
            Map<String, Object> clone = new HashMap<>(sourceMap);

            List<String> tableList = (List<String>) clone.get("table");
            List<String> tables = tableList.stream().map(t -> {
                if (StringUtils.isNotBlank(pdbName)) {
                    return String.format("%s.%s.%s", pdbName, schema, t);
                }
                return String.format("%s.%s", schema, t);
            }).collect(Collectors.toList());
            clone.put("table", tables);

            clone.put("password", pwd);
            clone.put("username", username);
            clone.put("jdbcUrl", jdbc);

            //解析操作参数
            StringJoiner catJoiner = new StringJoiner(",");
            List<Integer> intCats = (List<Integer>) clone.get("cat");
            if (CollectionUtils.isNotEmpty(intCats)) {
                for (Integer cat : intCats) {
                    catJoiner.add(DAoperators.getByVal(cat).name());
                }
            }
            clone.put("cat", catJoiner.toString());

            //解析同步方式
            Integer collectType = MapUtils.getInteger(clone, "collectType");
            String readPosition = getReadPosition(collectType);
            clone.put("readPosition", readPosition);

            return JsonUtils.objectToObject(clone, OracleBinLogReader.class);
        }

        @Override
        public Map<String, Object> getParserSourceMap(Map<String, Object> sourceMap) {
            try {
                OracleBinLogReaderParam param = JsonUtils.objectToObject(sourceMap, OracleBinLogReaderParam.class);
                return JsonUtils.objectToMap(param);
            } catch (Exception e) {
                throw new RdosDefineException("getParserSourceMap error", e);
            }
        }

        @Override
        public DataSourceType getDataSourceType() {
            return DataSourceType.Oracle;
        }

        private String getReadPosition(Integer collectType) {
            switch (collectType) {
                case 0:
                    return "current";
                case 2:
                    return "scn";
                case 1://
                default:
                    throw new RdosDefineException("Oracle binlog实时采集暂不支持此采集方式");
            }
        }
    }
}
