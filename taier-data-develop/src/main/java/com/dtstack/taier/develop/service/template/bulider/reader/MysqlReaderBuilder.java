package com.dtstack.taier.develop.service.template.bulider.reader;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.common.exception.ErrorCode;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.common.util.DataSourceUtils;
import com.dtstack.taier.common.util.PublicUtil;
import com.dtstack.taier.dao.domain.DsInfo;
import com.dtstack.taier.datasource.api.base.ClientCache;
import com.dtstack.taier.datasource.api.client.IClient;
import com.dtstack.taier.datasource.api.dto.ColumnMetaDTO;
import com.dtstack.taier.datasource.api.dto.SqlQueryDTO;
import com.dtstack.taier.datasource.api.dto.source.Mysql5SourceDTO;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import com.dtstack.taier.develop.common.template.Reader;
import com.dtstack.taier.develop.dto.devlop.ColumnDTO;
import com.dtstack.taier.develop.dto.devlop.ConnectionDTO;
import com.dtstack.taier.develop.dto.devlop.TaskResourceParam;
import com.dtstack.taier.develop.enums.develop.DAoperators;
import com.dtstack.taier.develop.enums.develop.PatternType;
import com.dtstack.taier.develop.enums.develop.RdbmsDaType;
import com.dtstack.taier.develop.enums.develop.SyncContentEnum;
import com.dtstack.taier.develop.service.datasource.impl.DsInfoService;
import com.dtstack.taier.develop.service.template.PluginName;
import com.dtstack.taier.develop.service.template.mysql.MysqlBinLogReader;
import com.dtstack.taier.develop.service.template.mysql.MysqlBinLogReaderParam;
import com.dtstack.taier.develop.service.template.mysql.MysqlPollReader;
import com.dtstack.taier.develop.service.template.mysql.MysqlPollReaderParam;
import com.dtstack.taier.develop.utils.JsonUtils;
import com.dtstack.taier.develop.utils.develop.sync.util.ColumnUtil;
import com.dtstack.taier.develop.vo.datasource.BinLogFileVO;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.regex.Matcher;

import static com.dtstack.taier.develop.utils.develop.common.enums.Constant.CREATE_MODEL_TEMPLATE;


/**
 * Date: 2020/2/18
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
@Component
public class MysqlReaderBuilder implements DaReaderBuilder {

    private Map<Integer, DaReaderBuilder> builderMap = new HashMap<>();
    @Autowired
    private DsInfoService dsInfoService;

    @PostConstruct
    private void init() {
        if (dsInfoService == null) {
            throw new RdosDefineException("streamDataSourceService should not be null");
        }
        builderMap.put(RdbmsDaType.Binlog.getCode(), new MysqlBinLogDaBuilder(dsInfoService));
        builderMap.put(RdbmsDaType.Poll.getCode(), new MysqlPollDaBuilder(dsInfoService));
    }

    @Override
    public void setReaderJson(TaskResourceParam param) {
        builderMap.get(MapUtils.getInteger(param.getSourceMap(), RDBMS_DA_TYPE, RdbmsDaType.Binlog.getCode()))
                .setReaderJson(param);
    }

    @Override
    public Reader daReaderBuild(TaskResourceParam param) throws Exception {
        return builderMap.get(MapUtils.getInteger(param.getSourceMap(), RDBMS_DA_TYPE, RdbmsDaType.Binlog.getCode()))
                .daReaderBuild(param);
    }

    @Override
    public Map<String, Object> getParserSourceMap(Map<String, Object> sourceMap) {
        return builderMap.get(MapUtils.getInteger(sourceMap, RDBMS_DA_TYPE, RdbmsDaType.Binlog.getCode()))
                .getParserSourceMap(sourceMap);
    }

    @Override
    public DataSourceType getDataSourceType() {
        return DataSourceType.MySQL;
    }

    public static class MysqlBinLogDaBuilder implements DaReaderBuilder {
        private DsInfoService dsInfoService;

        public MysqlBinLogDaBuilder(DsInfoService dsInfoService) {
            this.dsInfoService = dsInfoService;
        }

        @Override
        public void setReaderJson(TaskResourceParam param) {
            Map<String, Object> map = param.getSourceMap();
            Long sourceId = Long.parseLong(map.get("sourceId").toString());
            DsInfo source = dsInfoService.getOneById(sourceId);
            map.put("source", source);
            map.put("type", source.getDataTypeCode());
            map.put("dataName", source.getDataName());
            Boolean allTable = MapUtils.getBoolean(map, "allTable");
            //hive自动建表，当选择全部表时，需要传全部表名
            if (BooleanUtils.isTrue(allTable)) {
                try {
                    List<String> tableList = dsInfoService.tableList(sourceId, null, true);
                    map.put("table", tableList);//塞入sourceMap hiveWriter中使用
                } catch (Exception e) {
                    throw new RdosDefineException("获取mysql tableList异常", e);
                }
            }

        }

        @Override
        public Reader daReaderBuild(TaskResourceParam param) throws Exception {
            setReaderJson(param);
            Map<String, Object> map = param.getSourceMap();
            DsInfo dataSource = (DsInfo) map.get("source");
            MysqlBinLogReaderParam readerParam = JsonUtils.objectToObject(map, MysqlBinLogReaderParam.class);

            //起始位置设置
            if (readerParam.getTimestamp() != null) {
                readerParam.setJournalName(null);
            }
            if (StringUtils.isNotBlank(readerParam.getJournalName())) {
                List<String> binLogList = new ArrayList<>();
                //检查是否存在binlog
                List<BinLogFileVO> binLogFileVOS = dsInfoService.getBinLogListBySource(dataSource.getId(), true, null);
                Optional.ofNullable(binLogFileVOS).ifPresent(
                        binLogFiles -> binLogFiles.forEach(
                                binLogFileVO -> binLogList.add(binLogFileVO.getJournalName())
                        )
                );
                if (CollectionUtils.isEmpty(binLogList) || !binLogList.contains(readerParam.getJournalName())) {
                    throw new RdosDefineException("采集起点配置失败，采集起点文件名不存在");
                }
                readerParam.setTimestamp(null);
            }

            MysqlBinLogReader mysqlBinLogReader = new MysqlBinLogReader();
            MysqlBinLogReader.Start start = new MysqlBinLogReader.Start();
            start.setJournalName(readerParam.getJournalName());
            start.setTimestamp(readerParam.getTimestamp());
            mysqlBinLogReader.setStart(start);
            mysqlBinLogReader.setExtralConfig(readerParam.getExtralConfig());
            mysqlBinLogReader.setPavingData(readerParam.getPavingData());

            //填充数据源参数
            JSONObject dataJson = DataSourceUtils.getDataSourceJson(dataSource.getDataJson());
            String jdbc = DataSourceUtils.getJdbcUrl(dataJson);
            //校验jdbc格式
            Matcher matcher = PatternType.JDBC_PATTERN.getVal().matcher(jdbc);
            Matcher ipv6Matcher = PatternType.JDBC_IPV6_PATTERN.getVal().matcher(jdbc);
            String host = "";
            String port = "3306";
            String database = "";
            if (matcher.find()) {
                host = matcher.group("host");
                if (null != matcher.group("port")) {
                    port = matcher.group("port");
                }
                database = matcher.group("db");
            } else if (ipv6Matcher.find()) {
                host = ipv6Matcher.group("host");
                if (null != ipv6Matcher.group("port")) {
                    port = ipv6Matcher.group("port");
                }
            } else {
                throw new RdosDefineException("MySql数据源jdbc格式错误", ErrorCode.INVALID_PARAMETERS);
            }
            mysqlBinLogReader.setJdbcUrl(jdbc);
            mysqlBinLogReader.setPassword(DataSourceUtils.getJdbcPassword(dataJson));
            mysqlBinLogReader.setUsername(DataSourceUtils.getJdbcUsername(dataJson));
            mysqlBinLogReader.setHost(host);
            mysqlBinLogReader.setPort(Integer.valueOf(port));

            if (Objects.equals(readerParam.getSyncContent(), SyncContentEnum.DATA_STRUCTURE_SYNC.getType())) {
                String finalDatabase = database;
                List<String> finalTableList = new ArrayList<>();
                readerParam.getTable().forEach(table -> {
                    finalTableList.add(finalDatabase + "." + table);
                });
                mysqlBinLogReader.setTable(finalTableList);
                mysqlBinLogReader.setCat("insert,update,delete,alter,truncate,drop,rename,create,erase,cindex,dindex,gtid,xacommit,xarollback,mheartbeat"); //todo 下期优化可能要去掉
            } else {
                //解析操作参数
                StringJoiner catJoiner = new StringJoiner(",");
                List<Integer> intCats = (List<Integer>) map.get("cat");
                if (CollectionUtils.isNotEmpty(intCats)) {
                    for (Integer cat : intCats) {
                        catJoiner.add(DAoperators.getByVal(cat).name());
                    }
                }
                mysqlBinLogReader.setCat(catJoiner.toString());
                mysqlBinLogReader.setTable(readerParam.getTable());
            }

            return mysqlBinLogReader;
        }

        @Override
        public Map<String, Object> getParserSourceMap(Map<String, Object> sourceMap) {
            try {
                MysqlBinLogReaderParam param = JSONObject.parseObject(JSONObject.toJSONString(sourceMap), MysqlBinLogReaderParam.class, Feature.OrderedField);
                return JSONObject.parseObject(JSONObject.toJSONString(param), Feature.OrderedField);
            } catch (Exception e) {
                throw new RdosDefineException(String.format("getParserSourceMap error,Caused by: %s", e.getMessage()), e);
            }
        }

        @Override
        public DataSourceType getDataSourceType() {
            return DataSourceType.MySQL;
        }

    }


    public static class MysqlPollDaBuilder extends RdbmsReaderBuilder implements DaReaderBuilder {

        private DsInfoService dataSourceCenterService;

        public MysqlPollDaBuilder(DsInfoService dataSourceCenterService) {
            this.dataSourceCenterService = dataSourceCenterService;
        }

        @Override
        public void setReaderJson(TaskResourceParam param) {

            Map<String, Object> map = param.getSourceMap();
            if (Objects.equals(param.getTaskType(), EScheduleJobType.SYNC.getVal())) { // 分库分表
                List<Long> sourceIds = new ArrayList<>();
                List<Object> sourceList = (List<Object>) map.get("sourceList");
                JSONArray connections = new JSONArray();

                // mysql sourceList 为空时，根据 sourceId 去获取连接信息
                if (CREATE_MODEL_TEMPLATE == param.getCreateModel()) {
                    ConnectionDTO connectionDTO = new ConnectionDTO();
                    Long sourceId = Long.parseLong(map.get("sourceId").toString());
                    DsInfo dsServiceInfoDTO = dataSourceCenterService.getOneById(sourceId);
                    setProperties(sourceId, connectionDTO, dsServiceInfoDTO, map, connections, sourceIds);

                    connectionDTO.setTable(Lists.newArrayList());
                } else {
                    for (Object dataSource : sourceList) {
                        ConnectionDTO connectionDTO = new ConnectionDTO();
                        Map<String, Object> sourceMap = (Map<String, Object>) dataSource;
                        Long sourceId = Long.parseLong(sourceMap.get("sourceId").toString());
                        DsInfo dsServiceInfoDTO = dataSourceCenterService.getOneById(sourceId);

                        if (sourceMap.get("tables") instanceof String) {
                            connectionDTO.setTable(Collections.singletonList(sourceMap.get("tables").toString()));
                        } else {
                            connectionDTO.setTable(JSONArray.parseArray(sourceMap.get("tables").toString(), String.class));
                        }
                        setProperties(sourceId, connectionDTO, dsServiceInfoDTO, map, connections, sourceIds);

                        sourceMap.put("name", dsServiceInfoDTO.getDataName());

                    }
                    Map<String, Object> sourceMap = (Map<String, Object>) sourceList.get(0);
                    map.put("sourceId", sourceMap.get("sourceId"));
                    map.put("name", sourceMap.get("name"));
                }

                map.put("type", map.get("type"));
                map.put("connection", connections);
                map.put("sourceIds", sourceIds);
            } else if (Objects.equals(param.getTaskType(), EScheduleJobType.DATA_ACQUISITION.getVal())) {
                Long sourceId = Long.parseLong(map.get("sourceId").toString());
                DsInfo source = dataSourceCenterService.getOneById(sourceId);
                map.put("source", source);
                map.put("type", source.getDataTypeCode());
                map.put("dataName", source.getDataName());

                //for hive writer
                String tableName = MapUtils.getString(map, "tableName");
                List<String> table = Lists.newArrayList(tableName);
                map.put("table", table);
            }
        }

        /**
         * 抽取设置相同参数方法
         *
         * @param sourceId
         * @param connectionDTO
         * @param dsServiceInfoDTO
         * @param map
         * @param connections
         * @param sourceIds
         */
        private void setProperties(Long sourceId, ConnectionDTO connectionDTO, DsInfo dsServiceInfoDTO, Map<String, Object> map, JSONArray connections, List<Long> sourceIds) {

            JSONObject json = JSON.parseObject(dsServiceInfoDTO.getDataJson());
            connectionDTO.setJdbcUrl(Collections.singletonList(JsonUtils.getStringDefaultEmpty(json, JDBC_URL)));
            connectionDTO.setPassword(JsonUtils.getStringDefaultEmpty(json, JDBC_PASSWORD));
            connectionDTO.setUsername(JsonUtils.getStringDefaultEmpty(json, JDBC_USERNAME));
            if (map.get("schema") != null) {
                connectionDTO.setSchema(map.get("schema").toString());
            }
            connectionDTO.setType(dsServiceInfoDTO.getDataTypeCode());
            connectionDTO.setSourceId(sourceId);
            connections.add(connectionDTO);
            sourceIds.add(sourceId);
            map.putIfAbsent("source", dsServiceInfoDTO);
            map.putIfAbsent("dataSourceType", dsServiceInfoDTO.getDataTypeCode());
        }


        @Override
        public Reader daReaderBuild(TaskResourceParam param) throws Exception {
            setReaderJson(param);
            Map<String, Object> sourceMap = param.getSourceMap();
            MysqlPollReaderParam readerParam = PublicUtil.objectToObject(sourceMap, MysqlPollReaderParam.class);
            MysqlPollReader mysqlPollReader = new MysqlPollReader();

            if (Objects.equals(param.getTaskType(), EScheduleJobType.DATA_ACQUISITION.getVal())) {//实时任务
                DsInfo dataSource = (DsInfo) sourceMap.get("source");
                JSONObject json = DataSourceUtils.getDataSourceJson(dataSource.getDataJson());
                mysqlPollReader.setPollingInterval(readerParam.getPollingInterval());
                mysqlPollReader.setStartLocation(readerParam.getStartLocation());
                mysqlPollReader.setUsername(DataSourceUtils.getJdbcUsername(json));
                mysqlPollReader.setPassword(DataSourceUtils.getJdbcPassword(json));
                //校验jdbc格式
                //从mysql连接中获取schema
                String jdbcUrl = DataSourceUtils.getJdbcUrl(json);
                //回写到sourceMap中

                String schema = getMysqlDataBase(json);
                sourceMap.put("schema", schema);

                List<ConnectionDTO> connectionDTOList = new ArrayList<>();
                String tableName = schema + "." + readerParam.getTableName();

                //设置链接信息
                ConnectionDTO connectionDTO = new ConnectionDTO();
                connectionDTO.setJdbcUrl(Lists.newArrayList(jdbcUrl));
                connectionDTO.setTable((Lists.newArrayList(tableName)));
                connectionDTOList.add(connectionDTO);

                mysqlPollReader.setConnection(connectionDTOList);
                //获取表对应的字段
                List<ColumnDTO> columns = null;
                try {
                    IClient client = ClientCache.getClient(DataSourceType.MySQL.getVal());
                    Mysql5SourceDTO sourceDTO = Mysql5SourceDTO
                            .builder()
                            .url(connectionDTO.getJdbcUrl().get(0))
                            .username(mysqlPollReader.getUsername())
                            .password(mysqlPollReader.getPassword())
                            .build();
                    SqlQueryDTO sqlQueryDTO = SqlQueryDTO.builder().tableName(readerParam.getTableName()).build();
                    List<ColumnMetaDTO> columnMetaDTOList = client.getColumnMetaData(sourceDTO, sqlQueryDTO);
                    List<JSONObject> list = new ArrayList<>();
                    List<String> fields = new ArrayList<>();
                    if (CollectionUtils.isNotEmpty(columnMetaDTOList)) {
                        for (ColumnMetaDTO columnMetaDTO : columnMetaDTOList) {
                            if (sourceMap.get("tableFields") == null) {
                                list.add(JSON.parseObject(JSON.toJSONString(columnMetaDTO)));
                                fields.add(columnMetaDTO.getKey());
                            } else {
                                List<String> tableFields = (List<String>) sourceMap.get("tableFields");
                                if (tableFields.contains(columnMetaDTO.getKey())) {
                                    list.add(JSON.parseObject(JSON.toJSONString(columnMetaDTO)));
                                    fields.add(columnMetaDTO.getKey());
                                }
                            }
                        }
                    }
                    sourceMap.put("tableFields", fields);
                    String arrayJson = JSON.toJSONString(list);
                    columns = JSON.parseArray(arrayJson, ColumnDTO.class);
                } catch (Exception e) {
                    throw new RdosDefineException("获取" + readerParam.getTableName() + "字段信息异常" + e.getMessage(), e);
                }
                String increColumn = readerParam.getIncreColumn();
                int index = -1;
                for (int i = 0; i < columns.size(); i++) {
                    ColumnDTO column = columns.get(i);
                    column.setName(column.getKey());
                    if (increColumn.equals(column.getKey())) {
                        index = i;
                    }
                }
                mysqlPollReader.setIncreColumn(increColumn);

                //回写settingMap
                param.getSettingMap().put("restoreColumnIndex", String.valueOf(index));
                param.getSettingMap().put("restoreColumnName", increColumn);
                mysqlPollReader.setColumn(columns);
            } else if (Objects.equals(param.getTaskType(), EScheduleJobType.SYNC.getVal())) {//离线任务
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
                mysqlPollReader.setSplitPk(readerParam.getSplitPK());
                mysqlPollReader.setWhere(readerParam.getWhere());
                List columns = ColumnUtil.getColumns(readerParam.getColumn(), PluginName.MySQLD_R);
                if (CollectionUtils.isNotEmpty(columns)) {
                    mysqlPollReader.setColumn(JSONArray.parseArray(JSON.toJSONString(columns), ColumnDTO.class));
                }
                mysqlPollReader.setIncreColumn(Optional.ofNullable(readerParam.getIncreColumn()).orElse(""));
                mysqlPollReader.setStartLocation("");
                List<ConnectionDTO> connections = readerParam.getConnection();
                if (connections != null && connections.size() > 0) {
                    ConnectionDTO conn = connections.get(0);
//                    this.setJdbcUrl(((List<String>)conn.getJdbcUrl("jdbcUrl")).get(0));
//                    String pass = Objects.isNull(conn.get("password"))?"":conn.get("password").toString();
                    mysqlPollReader.setPassword(conn.getPassword());
                    mysqlPollReader.setUsername(conn.getUsername());
//                    if(conn.get("table") instanceof String){
//                        this.setTable(Arrays.asList((String)conn.get("table")));
//                    } else {
//                        this.setTable((List<String>) conn.get("table"));
//                    }
                }

                mysqlPollReader.setConnection(readerParam.getConnection());
                mysqlPollReader.setSourceIds(readerParam.getSourceIds());
                mysqlPollReader.setExtralConfig(readerParam.getExtralConfig());
                mysqlPollReader.setPolling(null);
            }
            return mysqlPollReader;
        }

        public String getMysqlDataBase(JSONObject info) throws Exception {
            String jdbcUrl = info.getString("jdbcUrl");
            String userName = info.getString("username");
            String password = info.getString("password");
            IClient client = ClientCache.getClient(DataSourceType.MySQL.getVal());
            Mysql5SourceDTO mysql5SourceDTO = Mysql5SourceDTO
                    .builder()
                    .url(jdbcUrl)
                    .username(userName)
                    .password(password)
                    .build();
            return client.getCurrentDatabase(mysql5SourceDTO);
        }

        @Override
        public Map<String, Object> getParserSourceMap(Map<String, Object> sourceMap) {
            try {
                MysqlPollReaderParam param = JsonUtils.objectToObject(sourceMap, MysqlPollReaderParam.class);
                return JsonUtils.objectToMap(param);
            } catch (Exception e) {
                throw new RdosDefineException("getParserSourceMap error", e);
            }
        }

        @Override
        public DataSourceType getDataSourceType() {
            return DataSourceType.MySQL;
        }

    }


}
