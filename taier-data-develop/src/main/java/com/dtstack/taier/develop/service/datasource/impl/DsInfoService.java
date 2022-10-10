package com.dtstack.taier.develop.service.datasource.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtstack.taier.common.constant.FormNames;
import com.dtstack.taier.common.enums.DataSourceTypeEnum;
import com.dtstack.taier.common.exception.DtCenterDefException;
import com.dtstack.taier.common.exception.ErrorCode;
import com.dtstack.taier.common.exception.PubSvcDefineException;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.common.thread.RdosThreadFactory;
import com.dtstack.taier.common.util.DataSourceUtils;
import com.dtstack.taier.dao.domain.DsInfo;
import com.dtstack.taier.dao.domain.po.DaoPageParam;
import com.dtstack.taier.dao.domain.po.DsListBO;
import com.dtstack.taier.dao.domain.po.DsListQuery;
import com.dtstack.taier.dao.mapper.DsInfoMapper;
import com.dtstack.taier.dao.pager.PageResult;
import com.dtstack.taier.datasource.api.base.ClientCache;
import com.dtstack.taier.datasource.api.client.IClient;
import com.dtstack.taier.datasource.api.dto.ColumnMetaDTO;
import com.dtstack.taier.datasource.api.dto.SqlQueryDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.Mysql5SourceDTO;
import com.dtstack.taier.datasource.api.dto.source.OracleSourceDTO;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import com.dtstack.taier.develop.bo.datasource.DsListParam;
import com.dtstack.taier.develop.datasource.convert.load.SourceLoaderService;
import com.dtstack.taier.develop.enums.develop.PatternType;
import com.dtstack.taier.develop.mapstruct.datasource.DsListTransfer;
import com.dtstack.taier.develop.service.template.bulider.db.DbBuilder;
import com.dtstack.taier.develop.service.template.bulider.db.DbBuilderFactory;
import com.dtstack.taier.develop.vo.datasource.BinLogFileVO;
import com.dtstack.taier.develop.vo.datasource.DsInfoVO;
import com.dtstack.taier.develop.vo.datasource.DsListVO;
import com.google.common.collect.Lists;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import static com.dtstack.taier.develop.service.template.bulider.reader.DaReaderBuilder.JDBC_URL;

/**
 * @author 全阅
 * @Description:
 * @Date: 2021/3/10
 */
@Service
public class DsInfoService  extends ServiceImpl<DsInfoMapper, DsInfo> {

    @Autowired
    private DsInfoMapper dsInfoMapper;

    @Autowired
    private DbBuilderFactory dbBuilderFactory;

    @Autowired
    private KerberosService kerberosService;

    @Autowired
    private SourceLoaderService sourceLoaderService;

    // 数据源是否是默认数据源
    private static final String DECIMAL_COLUMN = "%s(%s,%s)";
    private static final String SHOW_ORACLE_BINLOG_SQL = "SELECT * FROM (SELECT FIRST_CHANGE#,FIRST_TIME FROM v$log UNION SELECT FIRST_CHANGE#,FIRST_TIME FROM v$archived_log)tmp ORDER BY tmp.FIRST_TIME desc";
    private static final String SHOW_BINLOG_SQL = "show binary logs";
    private static final Integer LIMIT_COUNT = 100;
    public static final String KERBEROS_PATH = "kerberosDir";


    private static final int MAX_POOL_SIZE = 8;
    private static final int QUEUE_CAPACITY = 16;
    private static final int KEEP_ALIVE = 60;
    private static final Integer TOPIC_MESSAGE_LENGTH = 5;

    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(1, MAX_POOL_SIZE,
            KEEP_ALIVE, TimeUnit.SECONDS, new LinkedBlockingQueue<>(QUEUE_CAPACITY),
            new RdosThreadFactory("kafka-consumer"), new ThreadPoolExecutor.DiscardOldestPolicy());
    /**
     * 数据源列表分页
     *
     * @param dsListParam
     * @return
     */
    public PageResult<List<DsListVO>> dsPage(DsListParam dsListParam) {

        DsListQuery listQuery = DsListTransfer.INSTANCE.toInfoQuery(dsListParam);
        listQuery.turn();
        Integer total = this.baseMapper.countDsPage(listQuery);
        if (total == 0) {
            return new PageResult<>(0,0,0,0,new ArrayList<>());
        }
        List<DsListBO> dsListBOList = baseMapper.queryDsPage(listQuery);
        if (CollectionUtils.isEmpty(dsListBOList)) {
            return new PageResult<>(0,0,0,0,new ArrayList<>());
        }
        List<DsListVO> dsListVOS = new ArrayList<>();
        for (DsListBO dsListBO : dsListBOList) {
            DsListVO dsListVO = DsListTransfer.INSTANCE.toInfoVO(dsListBO);
            String linkJson = dsListVO.getLinkJson();
            JSONObject linkData = DataSourceUtils.getDataSourceJson(linkJson);
            linkData.put("schemaName",dsListVO.getSchemaName());
            dsListVO.setLinkJson(DataSourceUtils.getEncodeDataSource(linkData,true));
            dsListVOS.add(dsListVO);
        }
        return new PageResult<>(dsListParam.getCurrentPage(),dsListParam.getPageSize(),total,dsListVOS);
    }

    /**
     * 根据数据源Id获取数据源详情
     *
     * @param dataInfoId
     * @return
     */
    public DsInfo dsInfoDetail(Long dataInfoId) {
        DsInfo dsInfo = lambdaQuery().eq(DsInfo::getId, dataInfoId).one();
        String dataJson = dsInfo.getDataJson();
        JSONObject dataSourceJson = DataSourceUtils.getDataSourceJson(dataJson);
        if(DataSourceUtils.judgeOpenKerberos(dataJson) && null == dataSourceJson.getString(FormNames.PRINCIPAL)){
            JSONObject kerberosConfig = dataSourceJson.getJSONObject(FormNames.KERBEROS_CONFIG);
            dataSourceJson.put(FormNames.PRINCIPAL,kerberosConfig.getString(FormNames.PRINCIPAL));
        }
        if(DataSourceUtils.judgeOpenKerberos(dsInfo.getDataJson()) && dsInfo.getDataType().equals(DataSourceTypeEnum.KAFKA.getDataType())){
            //kafka开启了kerberos认证
            dataSourceJson.put(FormNames.AUTHENTICATION,FormNames.KERBROS);
        }
        return dsInfo;
    }

    /**
     * 删除一条数据源信息
     *
     * @param dataInfoId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean delDsInfo(Long dataInfoId) {
        DsInfo dsInfo = this.getOneById(dataInfoId);
        if (Objects.equals(dsInfo.getIsMeta(), 1)) {
            throw new PubSvcDefineException(ErrorCode.CAN_NOT_DEL_META_DS);
        }
        return this.getBaseMapper().deleteById(dataInfoId) > 0;
    }
    /**
     * 特殊表名处理
     *
     * @param tableName
     * @param dtType
     * @param schema
     * @return
     */
    private String dealSpecialTableName(String tableName, int dtType, String schema) {
        //针对oracle数据源做特殊处理
        if (dtType == DataSourceType.Oracle.getVal()) {
            return String.format("\"%s\".\"%s\"", schema, tableName);
        }
        Matcher matcher = PatternType.SPECIAL_TABLE_PATTERN.getVal().matcher(tableName);

        // TODO 临时处理sqlServer2017 schema、tableName，后面插件化要优化支持传schema
        if (DataSourceType.SQLSERVER_2017_LATER.getVal().equals(dtType)) {
            if (StringUtils.isBlank(schema)) {
                return tableName;
            }
            tableName = String.format("[%s].[%s]", schema, tableName);
        }
        if (!matcher.find()) {
            return tableName;
        }

        if (DataSourceType.MySQL.getVal() == dtType || DataSourceType.MySQL8.getVal() == dtType) {
            return "`" + tableName + "`";
        }
        if (StringUtils.isBlank(schema)) {
            return String.format("%s.%s", schema, tableName);
        }
        return tableName;
    }

    /**
     * 为 Flinkx 特殊处理字段精度
     * 如果数据库字段返回结果存在typeName 则直接用 typename
     *
     * @param columns
     */
    private List<JSONObject> doChangeColumnTypeForFlinkx(List<JSONObject> columns) {
        if (columns == null || columns.size() == 0) {
            return columns;
        }

        columns.forEach(column -> {
            if ("decimal".equalsIgnoreCase(column.getString("type"))) {
                Integer precision = column.getInteger("precision");
                Integer scale = column.getInteger("scale");
                String columnStr = String.format(DECIMAL_COLUMN, "decimal", precision, scale);
                column.put("type", columnStr);
            }
        });
        return columns;
    }

    /**
     * 自动建表增加特殊字段
     *
     * @param tablesColumn
     * @return
     */
    public JSONObject dealTablesColumnAutoCreate(JSONObject tablesColumn) {
        JSONObject backJson = new JSONObject();
        for (String tableName : tablesColumn.keySet()) {
            JSONArray tableColumn = tablesColumn.getJSONArray(tableName);
            if (tableColumn.isEmpty()) {
                continue;
            }

            // 添加特殊字段
            JSONObject specialClumn = new JSONObject();
            specialClumn.put("key", "type");
            specialClumn.put("type", "varchar");
            specialClumn.put("comment", "");
            tableColumn.add(specialClumn);
            specialClumn = new JSONObject();
            specialClumn.put("key", "schema");
            specialClumn.put("type", "varchar");
            specialClumn.put("comment", "");
            tableColumn.add(specialClumn);
            specialClumn = new JSONObject();
            specialClumn.put("key", "table");
            specialClumn.put("type", "varchar");
            specialClumn.put("comment", "");
            tableColumn.add(specialClumn);
            specialClumn = new JSONObject();
            specialClumn.put("key", "ts");
            specialClumn.put("type", "bigint");
            specialClumn.put("comment", "");
            tableColumn.add(specialClumn);
            backJson.put(tableName, tableColumn);
        }
        return backJson;
    }

    /**
     * 添加sftp配置和remoteDir
     */
    public void setFtpConf(Map<String, Object> map, DsInfo source, Long dtuicTenantId, String confKey) {
        JSONObject kerberosConfig = DataSourceUtils.getOriginKerberosConfig(source.getDataJson(), false);
        if (MapUtils.isNotEmpty(kerberosConfig)) {
            Map<String, String> sftpMap = kerberosService.getSftpMap(dtuicTenantId);
            Map<String, Object> conf = null;
            Object confObj = map.get(confKey);
            if (confObj instanceof String) {
                conf = JSONObject.parseObject(confObj.toString());
            } else if (confObj instanceof Map) {
                conf = (Map<String, Object>) confObj;
            }

            conf = Optional.ofNullable(conf).orElse(new HashMap<>());
            conf.putAll(kerberosConfig);
            conf.put("sftpConf", sftpMap);
            DsInfo dsServiceInfoDTO = getOneById(source.getId());
            JSONObject originKerberosConfig = DataSourceUtils.getOriginKerberosConfig(dsServiceInfoDTO.getDataJson(), true);
            String remoteDir = originKerberosConfig.getString(KERBEROS_PATH);
            conf.put("remoteDir", KerberosService.getSftpPath(sftpMap, remoteDir));
            if ("hadoopConfig".equals(confKey)) {
                // HDFS 开启 kerberos 认证，FlinkX 需要添加参数 hadoop.security.authorization 和 hadoop.security.authentication
                conf.put("hadoop.security.authorization", true);
                conf.put("hadoop.security.authentication", "Kerberos");
            }
            map.put(confKey, conf);
        }
    }
    public String getDBFromJdbc(String jdbcUrl) {
        String[] split = jdbcUrl.split("/|\\?|;");
        if (split.length >= 3) {
            String dbName = split[3];
            return dbName.endsWith(";") ? dbName.substring(0, dbName.length() - 1) : dbName;
        }
        return null;
    }
    public String getDBFromJdbc(Long sourceId) {
        DsInfo dsServiceInfoDTO = getOneById(sourceId);
        JSONObject json = JSONObject.parseObject(dsServiceInfoDTO.getDataJson());
        return getDBFromJdbc(json.getString(JDBC_URL));
    }
    /**
     * 处理 表字段平铺处理
     *
     * @param tablesColumn
     * @return
     */
    public JSONObject dealTablesColumnPavingData(JSONObject tablesColumn) {
        JSONObject backJson = new JSONObject();
        for (String tableName : tablesColumn.keySet()) {
            JSONArray tableColumn = tablesColumn.getJSONArray(tableName);
            JSONArray backTableColumn = new JSONArray();
            if (tableColumn.isEmpty()) {
                continue;
            }
            for (int i = 0, len = tableColumn.size(); i < len; i++) {
                JSONObject beforeColumn = (JSONObject) tableColumn.get(i);
                JSONObject afterColumn = (JSONObject) beforeColumn.clone();
                beforeColumn.put("key", "before_" + beforeColumn.getString("key"));
                backTableColumn.add(beforeColumn);
                afterColumn.put("key", "after_" + afterColumn.getString("key"));
                backTableColumn.add(afterColumn);
            }

            backJson.put(tableName, backTableColumn);
        }
        return backJson;
    }


    /**
     * 获取数据源特定表的字段属性，如果表为空，则找全部表
     *
     * @param source
     * @param tableName
     * @param isPartition
     * @return JSONObject Left:TableName Right: Columns [Left:ColumnName Right:ColumnTypeName]
     */
    public JSONObject getTablesColumn(DsInfo source, Object tableName, boolean isPartition, String schema) {
        JSONObject columnMetaData = new JSONObject();
        try {
            if (source == null || tableName == null) {
                throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_DATA_SOURCE);
            }
            ISourceDTO sourceDTO = sourceLoaderService.buildSourceDTO(source.getId());
            if (isPartition) {
                JSONObject tableNameStr = new JSONObject().fluentPutAll((Map) tableName);
                for (String groupName : tableNameStr.keySet()) {
                    groupName = groupName.trim();
                    JSONArray tables = tableNameStr.getJSONArray(groupName);
                    if (null == tables || tables.size() < 1) {
                        continue;
                    }
                    String tName = dealSpecialTableName(tables.get(0).toString().trim(), source.getDataTypeCode(), schema);
                    SqlQueryDTO sqlQueryDTO = SqlQueryDTO.builder().tableName(tName).build();
                    sqlQueryDTO.setFilterPartitionColumns(true);
                    List<ColumnMetaDTO> columnMetaDTOList = ClientCache.getClient(source.getDataTypeCode()).getColumnMetaData(sourceDTO, sqlQueryDTO);
                    List<JSONObject> list = new ArrayList<>();
                    if (org.apache.commons.collections.CollectionUtils.isNotEmpty(columnMetaDTOList)) {
                        for (ColumnMetaDTO columnMetaDTO : columnMetaDTOList) {
                            list.add(JSON.parseObject(JSON.toJSONString(columnMetaDTO)));
                        }
                    }
                    columnMetaData.put(groupName.trim(), doChangeColumnTypeForFlinkx(list));
                }
                return columnMetaData;
            }
            if (tableName instanceof JSONArray) {
                tableName = JSONArray.parseArray(JSON.toJSONString(tableName), String.class);
            }
            String tableNameStr = tableName.toString();
            if (tableNameStr.startsWith("[")) {
                tableNameStr = tableNameStr.substring(1, tableNameStr.length() - 1);
            }
            String[] tablesName = tableNameStr.split(",");
            for (String singleTablesName : tablesName) {
                String tName = dealSpecialTableName(singleTablesName.trim(),  source.getDataTypeCode(), schema);
                SqlQueryDTO sqlQueryDTO = SqlQueryDTO.builder().tableName(tName).build();
                sqlQueryDTO.setFilterPartitionColumns(true);
                List<ColumnMetaDTO> columnMetaDTOList = ClientCache.getClient(source.getDataTypeCode()).getColumnMetaData(sourceDTO, sqlQueryDTO);
                List<JSONObject> list = new ArrayList<>();
                if (org.apache.commons.collections.CollectionUtils.isNotEmpty(columnMetaDTOList)) {
                    for (ColumnMetaDTO columnMetaDTO : columnMetaDTOList) {
                        list.add(JSON.parseObject(JSON.toJSONString(columnMetaDTO)));
                    }
                }
                columnMetaData.put(singleTablesName.trim(), doChangeColumnTypeForFlinkx(list));
            }
            return columnMetaData;
        } catch (Exception e) {
            throw new RdosDefineException(String.format("getTablesColumn 异常 tableName=%s,Caused by: %s", tableName, e.getMessage()), e);
        }
    }

    public List<String> tableList(Long sourceId, String tableNamePattern, boolean isAll) {
        try {
            ISourceDTO sourceDTO = sourceLoaderService.buildSourceDTO(sourceId);
            SqlQueryDTO queryDTO = SqlQueryDTO.builder().tableNamePattern(tableNamePattern).build();
            if (!isAll) {
                queryDTO.setLimit(LIMIT_COUNT);
            }
            List<String> tables = ClientCache.getClient(sourceDTO.getSourceType()).getTableList(sourceDTO, queryDTO);
            // 对表名按照字典表排序
            if (CollectionUtils.isNotEmpty(tables)) {
                tables.sort(String::compareTo);
            }
            return tables;
        } catch (Exception e) {
            throw new RdosDefineException("数据源出错，错误信息：" + e.getMessage(), e);
        }
    }

    public ISourceDTO getSourceDTO(Long sourceId) {
        return sourceLoaderService.buildSourceDTO(sourceId);
    }

    public List<BinLogFileVO> getBinLogListBySource(Long sourceId, Boolean isAll, String journalName) {
        DsInfo dsServiceInfoDTO = getOneById(sourceId);
        JSONObject dataJson = DataSourceUtils.getDataSourceJson(dsServiceInfoDTO.getDataJson());
        String jdbcUrl = DataSourceUtils.getJdbcUrl(dataJson);
        String userName = DataSourceUtils.getJdbcUsername(dataJson);
        String password = DataSourceUtils.getJdbcPassword(dataJson);
        List<BinLogFileVO> binLogFileVOS = new ArrayList<>();
        Integer type = dsServiceInfoDTO.getDataTypeCode();
        if (DataSourceType.Oracle.getVal().equals(type)) {
            //暂时只支持oracle
            List<Map<String, Object>> mapList = null;
            IClient client = ClientCache.getClient(type);
            OracleSourceDTO oracleSourceDTO = OracleSourceDTO
                    .builder()
                    .url(jdbcUrl)
                    .username(userName)
                    .password(password)
                    .build();
            try {
                mapList = client.executeQuery(oracleSourceDTO, SqlQueryDTO.builder().sql(SHOW_ORACLE_BINLOG_SQL).build());
            } catch (Exception e) {
                throw new RdosDefineException(String.format("获取数据库log文件失败,Caused by: %s", e.getMessage()), e);
            }
            for (Map<String, Object> map : mapList) {
                if (StringUtils.isNotBlank(MapUtils.getString(map, "FIRST_TIME")) && StringUtils.isNotBlank(MapUtils.getString(map, "FIRST_CHANGE#"))) {
                    BinLogFileVO binLogFileVO = new BinLogFileVO();
                    binLogFileVO.setJournalName(map.get("FIRST_TIME").toString());
                    binLogFileVO.setScn(map.get("FIRST_CHANGE#").toString());
                    binLogFileVOS.add(binLogFileVO);
                }
            }
        } else {
            //其他的走原来逻辑
            List<String> binLogList = getBinLogList(jdbcUrl, userName, password);
            Optional.ofNullable(binLogList).ifPresent(strings -> strings.forEach(s -> {
                BinLogFileVO binLogFileVO = new BinLogFileVO();
                binLogFileVO.setJournalName(s);
                binLogFileVOS.add(binLogFileVO);
            }));
        }
        // 获取全部binlog列表
        if (BooleanUtils.isTrue(isAll)) {
            return binLogFileVOS;
        }
        // 返回最多100条binlog列表
        if (StringUtils.isBlank(journalName)) {
            return binLogFileVOS.stream().limit(100).collect(Collectors.toList());
        }
        // 根据日志名称模糊查询最多100条binlog列表
        return binLogFileVOS.stream()
                .filter(binLogFileVO -> StringUtils.containsIgnoreCase(binLogFileVO.getJournalName(), journalName))
                .limit(100)
                .collect(Collectors.toList());
    }


    public List<String> getBinLogList(String jdbcUrl, String userName, String password) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        IClient client = ClientCache.getClient(DataSourceType.MySQL.getVal());
        Mysql5SourceDTO sourceDTO = Mysql5SourceDTO
                .builder()
                .url(jdbcUrl)
                .username(userName)
                .password(password)
                .build();
        try {
            mapList = client.executeQuery(sourceDTO, SqlQueryDTO.builder().sql(SHOW_BINLOG_SQL).build());
        } catch (Exception e) {
            if (e.getCause() != null) {
                if (e.getCause() instanceof SQLException) {
                    SQLException cause = (SQLException) e.getCause();
                    if ("HY000".equalsIgnoreCase(cause.getSQLState())) {
                        throw new RdosDefineException("数据库未开启binlog日志");
                    }
                }
            }
        }
        List<String> binLogList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(mapList)) {
            for (Map<String, Object> map : mapList) {
                binLogList.add((String) map.get("Log_name"));
            }
        }
        return binLogList;
    }
    /**
     * 通过数据源主键id获取特定数据源
     * @param dsInfoId
     * @return
     */
    public DsInfo getOneById(Long dsInfoId) {
        DsInfo dataSource = this.getById(dsInfoId);
        if (Objects.isNull(dataSource) || Objects.isNull(dataSource.getId())) {
            throw new PubSvcDefineException(ErrorCode.CAN_NOT_FIND_DATA_SOURCE);
        }
        //查询引入表判断是否是迁移的数据源
        JSONObject dataSourceJson = DataSourceUtils.getDataSourceJson(dataSource.getDataJson());
        dataSource.setDataJson(dataSourceJson.toJSONString());
        return dataSource;
    }


    /**
     * 判断当前数据源新增或者编辑是否有重名
     * @param dsInfo
     * @return
     */
    public Boolean checkDataNameDup(DsInfo dsInfo) {
        List<DsInfo> dsInfoList = this.lambdaQuery().eq(DsInfo::getDataName, dsInfo.getDataName())
                .eq(DsInfo::getTenantId, dsInfo.getTenantId())
                .ne(Objects.nonNull(dsInfo.getId()), DsInfo::getId, dsInfo.getId()).list();
        return CollectionUtils.isNotEmpty(dsInfoList);
    }

    /**
     * 根据租户查询数据源列表
     * @param tenantId
     * @return
     */
    public List<DsInfoVO> queryByTenantId(Long tenantId) {
        List<DsInfo> dsInfos = dsInfoMapper.queryByTenantId(tenantId);
        return DsListTransfer.INSTANCE.toDsInfoVOS(dsInfos);
    }

    /**
     * 查询数据源基本信息
     *
     * @param type
     * @param tenantId
     * @return
     */
    public List<DsInfoVO> listDataSourceBaseInfo(Integer type, Long tenantId) {
        DsListQuery query = new DsListQuery();
        query.setTenantId(tenantId);
        query.setDataTypeCode(type);
        List<DsListBO> dataSourceList = dsInfoMapper.queryDsPage(query);
        List<DsInfoVO> resultList = Lists.newArrayList();
        for (DsListBO dsListBO : dataSourceList) {
            DsInfoVO dsListVO = DsListTransfer.INSTANCE.toDsInfoVO(dsListBO);
            resultList.add(dsListVO);
        }
        return resultList;
    }

    /**
     * 获取当前数据源的所有 schema
     *
     * @param sourceId         数据源id
     * @param schema           schema
     * @param tableNamePattern 模糊查询
     * @return schema 集合
     */
    public List<String> listTablesBySchema(Long sourceId, String schema, String tableNamePattern) {

        if (StringUtils.isBlank(schema)) {
            return tableList(sourceId, tableNamePattern, false);
        }
        ISourceDTO sourceDTO = sourceLoaderService.buildSourceDTO(sourceId, schema);
        DbBuilder dbBuilder = dbBuilderFactory.getDbBuilder(sourceDTO.getSourceType());
        List<String> tables = dbBuilder.listTablesBySchema(schema, tableNamePattern, sourceDTO, null);
        // 按照字典表排序
        if (CollectionUtils.isNotEmpty(tables)) {
            tables.sort(String::compareTo);
        }
        return tables;
    }


    public JSONObject pollPreview(Long sourceId, String tableName, String schema) {
        DsInfo dataSource = this.getById(sourceId);
        ISourceDTO sourceDTO = sourceLoaderService.buildSourceDTO(dataSource.getId(), schema);
        DbBuilder dbBuilder = dbBuilderFactory.getDbBuilder(sourceDTO.getSourceType());
        return dbBuilder.pollPreview(tableName, sourceDTO);
    }

    public List<String> getTopicData(Long sourceId, String topic, String previewModel) {
        List<String> kafkaTopicData = getKafkaTopicData( sourceId, previewModel, topic);
        if (kafkaTopicData != null && kafkaTopicData.size() > TOPIC_MESSAGE_LENGTH) {
            kafkaTopicData = kafkaTopicData.subList(0, TOPIC_MESSAGE_LENGTH);
        }
        return kafkaTopicData;
    }

    public List<String> getKafkaTopicData(Long dtCenterSourceId, String previewModel, String topic) {
        List<Object> records = new ArrayList<>();
        Future<List<Object>> future = executor.submit(() -> {
            ISourceDTO sourceDTO = sourceLoaderService.buildSourceDTO(dtCenterSourceId);
            SqlQueryDTO sqlQueryDTO = SqlQueryDTO.builder().tableName(topic).build();
            List<List<Object>> preview;
            try {
                preview = ClientCache.getKafka(DataSourceType.KAFKA.getVal()).getPreview(sourceDTO, sqlQueryDTO, previewModel);
            } catch (Exception e) {
                throw new DtCenterDefException(String.format("Kafka 预览异常,Caused by: %s", e.getMessage()), e);
            }
            return preview.get(0);
        });
        try {
            records = future.get(5000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            future.cancel(true);
        }
        return records.stream().map(Object::toString).collect(Collectors.toList());
    }


    public List<DsListVO> total(DsListParam dsListParam) {
        dsListParam.setCurrentPage(1);
        dsListParam.setPageSize(DaoPageParam.MAX_PAGE_SIZE);
        PageResult<List<DsListVO>> listPageResult = dsPage(dsListParam);
        return listPageResult.getData();
    }
}
