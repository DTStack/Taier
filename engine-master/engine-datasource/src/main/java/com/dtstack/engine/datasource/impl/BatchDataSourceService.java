//package com.dtstack.batch.service.datasource.impl;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.dtstack.batch.bo.CarbonDataParameter;
//import com.dtstack.batch.bo.CarbonDataTable;
//import com.dtstack.batch.common.enums.ETableType;
//import com.dtstack.batch.common.env.EnvironmentContext;
//import com.dtstack.batch.common.exception.ErrorCode;
//import com.dtstack.batch.common.exception.RdosDefineException;
//import com.dtstack.batch.common.template.Reader;
//import com.dtstack.batch.common.template.Setting;
//import com.dtstack.batch.common.template.Writer;
//import com.dtstack.batch.common.util.JsonUtil;
//import com.dtstack.batch.dao.BatchDataSourceDao;
//import com.dtstack.batch.dao.BatchDataSourceTaskRefDao;
//import com.dtstack.batch.dao.BatchTestProduceDataSourceDao;
//import com.dtstack.batch.dao.NotifyDao;
//import com.dtstack.batch.dao.NotifyRecordDao;
//import com.dtstack.batch.dao.UserDao;
//import com.dtstack.batch.datamask.domain.DataMaskRule;
//import com.dtstack.batch.datamask.util.DataMaskUtil;
//import com.dtstack.batch.domain.BatchDataSource;
//import com.dtstack.batch.domain.BatchSysParameter;
//import com.dtstack.batch.domain.BatchTableInfo;
//import com.dtstack.batch.domain.BatchTask;
//import com.dtstack.batch.domain.BatchTestProduceDataSource;
//import com.dtstack.batch.domain.Notify;
//import com.dtstack.batch.domain.NotifyRecord;
//import com.dtstack.batch.domain.Project;
//import com.dtstack.batch.domain.ProjectEngine;
//import com.dtstack.batch.domain.User;
//import com.dtstack.batch.dto.BatchDataSourceDTO;
//import com.dtstack.batch.dto.BatchDataSourceTaskDto;
//import com.dtstack.batch.engine.rdbms.common.HadoopConf;
//import com.dtstack.batch.engine.rdbms.common.HadoopConfTool;
//import com.dtstack.batch.engine.rdbms.common.HdfsOperator;
//import com.dtstack.batch.engine.rdbms.common.enums.StoredType;
//import com.dtstack.batch.engine.rdbms.service.impl.Engine2DTOService;
//import com.dtstack.batch.enums.AlarmTypeEnum;
//import com.dtstack.batch.enums.CarbonDataConfigType;
//import com.dtstack.batch.enums.CarbonDataPartitionType;
//import com.dtstack.batch.enums.DataSourceDataBaseType;
//import com.dtstack.batch.enums.RDBMSSourceType;
//import com.dtstack.batch.enums.TableLocationType;
//import com.dtstack.batch.enums.TaskCreateModelType;
//import com.dtstack.batch.mapping.SourceTypeEngineTypeMapping;
//import com.dtstack.batch.mapping.TableTypeEngineTypeMapping;
//import com.dtstack.batch.service.datamask.impl.DataMaskColumnInfoService;
//import com.dtstack.batch.service.datasource.helper.DataSourceClientUtils;
//import com.dtstack.batch.service.datasource.helper.EsUtil;
//import com.dtstack.batch.service.datasource.helper.FtpUtil;
//import com.dtstack.batch.service.datasource.helper.HBaseUtil;
//import com.dtstack.batch.service.datasource.helper.KuduDbUtil;
//import com.dtstack.batch.service.datasource.helper.MongoDbUtil;
//import com.dtstack.batch.service.datasource.helper.OdpsUtil;
//import com.dtstack.batch.service.datasource.helper.RedisUtil;
//import com.dtstack.batch.service.impl.BatchDirtyDataService;
//import com.dtstack.batch.service.impl.DictService;
//import com.dtstack.batch.service.impl.ProjectEngineService;
//import com.dtstack.batch.service.impl.ProjectService;
//import com.dtstack.batch.service.impl.RoleUserService;
//import com.dtstack.batch.service.impl.TenantService;
//import com.dtstack.batch.service.impl.UserService;
//import com.dtstack.batch.service.table.impl.BatchTableInfoService;
//import com.dtstack.batch.service.table.impl.BatchTablePermissionService;
//import com.dtstack.batch.service.task.impl.BatchTaskParamService;
//import com.dtstack.batch.service.task.impl.BatchTaskService;
//import com.dtstack.batch.service.testproduct.impl.BatchTestProduceDataSourceService;
//import com.dtstack.batch.sync.format.ColumnType;
//import com.dtstack.batch.sync.format.TypeFormat;
//import com.dtstack.batch.sync.format.writer.HiveWriterFormat;
//import com.dtstack.batch.sync.handler.ImpalaSyncBuilder;
//import com.dtstack.batch.sync.handler.SyncBuilderFactory;
//import com.dtstack.batch.sync.job.JobTemplate;
//import com.dtstack.batch.sync.job.PluginName;
//import com.dtstack.batch.sync.template.CarbonDataReader;
//import com.dtstack.batch.sync.template.CarbonDataWriter;
//import com.dtstack.batch.sync.template.DefaultSetting;
//import com.dtstack.batch.sync.template.EsReader;
//import com.dtstack.batch.sync.template.EsWriter;
//import com.dtstack.batch.sync.template.FtpReader;
//import com.dtstack.batch.sync.template.FtpWriter;
//import com.dtstack.batch.sync.template.HBaseReader;
//import com.dtstack.batch.sync.template.HBaseWriter;
//import com.dtstack.batch.sync.template.HDFSReader;
//import com.dtstack.batch.sync.template.HDFSWriter;
//import com.dtstack.batch.sync.template.HiveReader;
//import com.dtstack.batch.sync.template.HiveWriter;
//import com.dtstack.batch.sync.template.MongoDbReader;
//import com.dtstack.batch.sync.template.MongoDbWriter;
//import com.dtstack.batch.sync.template.OdpsBase;
//import com.dtstack.batch.sync.template.OdpsReader;
//import com.dtstack.batch.sync.template.OdpsWriter;
//import com.dtstack.batch.sync.template.RDBBase;
//import com.dtstack.batch.sync.template.RDBReader;
//import com.dtstack.batch.sync.template.RDBWriter;
//import com.dtstack.batch.sync.template.RedisWriter;
//import com.dtstack.batch.sync.util.HdfsOrcUtil;
//import com.dtstack.batch.vo.BatchTableInfoVO;
//import com.dtstack.batch.vo.BatchTableSearchVO;
//import com.dtstack.batch.vo.DataSourceTypeVO;
//import com.dtstack.batch.vo.DataSourceVO;
//import com.dtstack.batch.vo.TaskResourceParam;
//import com.dtstack.batch.web.pager.PageQuery;
//import com.dtstack.batch.web.pager.PageResult;
//import com.dtstack.dtcenter.common.engine.ConsoleSend;
//import com.dtstack.dtcenter.common.engine.JdbcInfo;
//import com.dtstack.dtcenter.common.enums.AlarmTrigger;
//import com.dtstack.dtcenter.common.enums.AppType;
//import com.dtstack.dtcenter.common.enums.DictType;
//import com.dtstack.dtcenter.common.enums.EBoolean;
//import com.dtstack.dtcenter.common.enums.EComponentType;
//import com.dtstack.dtcenter.common.enums.EDataSourcePermission;
//import com.dtstack.dtcenter.common.enums.EJobType;
//import com.dtstack.dtcenter.common.enums.MultiEngineType;
//import com.dtstack.dtcenter.common.enums.NotifyType;
//import com.dtstack.dtcenter.common.enums.ProjectType;
//import com.dtstack.dtcenter.common.enums.SftpAuthType;
//import com.dtstack.dtcenter.common.enums.Sort;
//import com.dtstack.dtcenter.common.enums.TaskStatus;
//import com.dtstack.dtcenter.common.exception.DtCenterDefException;
//import com.dtstack.dtcenter.common.kerberos.KerberosConfigVerify;
//import com.dtstack.dtcenter.common.sftp.SFTPHandler;
//import com.dtstack.dtcenter.common.thread.RdosThreadFactory;
//import com.dtstack.dtcenter.common.util.AddressUtil;
//import com.dtstack.dtcenter.common.util.Base64Util;
//import com.dtstack.dtcenter.common.util.DataFilter;
//import com.dtstack.dtcenter.common.util.JdbcUrlUtil;
//import com.dtstack.dtcenter.common.util.JsonUtils;
//import com.dtstack.dtcenter.common.util.ParamsCheck;
//import com.dtstack.dtcenter.common.util.PublicUtil;
//import com.dtstack.dtcenter.common.util.UrlInfo;
//import com.dtstack.dtcenter.loader.client.ClientCache;
//import com.dtstack.dtcenter.loader.client.IClient;
//import com.dtstack.dtcenter.loader.client.IKerberos;
//import com.dtstack.dtcenter.loader.dto.ColumnMetaDTO;
//import com.dtstack.dtcenter.loader.dto.SqlQueryDTO;
//import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
//import com.dtstack.dtcenter.loader.source.DataBaseType;
//import com.dtstack.dtcenter.loader.source.DataSourceType;
//import com.dtstack.dtcenter.loader.utils.DBUtil;
//import com.dtstack.engine.api.dto.UserMessageDTO;
//import com.dtstack.engine.api.param.AlarmSendParam;
//import com.dtstack.engine.api.param.NotifyRecordParam;
//import com.dtstack.engine.api.service.ConsoleNotifyApiClient;
//import com.dtstack.sdk.core.common.ApiResponse;
//import com.dtstack.sql.Column;
//import com.dtstack.sql.Table;
//import com.dtstack.sql.Twins;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.google.common.base.Preconditions;
//import com.google.common.collect.Lists;
//import com.google.common.collect.Maps;
//import com.google.common.collect.Sets;
//import com.jcraft.jsch.SftpException;
//import org.apache.commons.collections.CollectionUtils;
//import org.apache.commons.collections.MapUtils;
//import org.apache.commons.lang.BooleanUtils;
//import org.apache.commons.lang.StringUtils;
//import org.codehaus.jackson.JsonGenerationException;
//import org.codehaus.jackson.JsonParseException;
//import org.codehaus.jackson.map.JsonMappingException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.BeanUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.util.Assert;
//
//import java.io.File;
//import java.io.IOException;
//import java.sql.Connection;
//import java.sql.ResultSet;
//import java.sql.ResultSetMetaData;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.sql.Timestamp;
//import java.text.SimpleDateFormat;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Objects;
//import java.util.Optional;
//import java.util.Set;
//import java.util.StringJoiner;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Future;
//import java.util.concurrent.LinkedBlockingQueue;
//import java.util.concurrent.ThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.TimeoutException;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//import java.util.stream.Collectors;
//
///**
// * company: www.dtstack.com
// * author: toutian
// * create: 2017/5/10
// */
//@Service
//public class BatchDataSourceService {
//
//    private static final Logger logger = LoggerFactory.getLogger(BatchDataSourceService.class);
//    private static final Logger job_log = LoggerFactory.getLogger("job");
//
//    /**
//     * FIMXE 暂时将数据源读写权限设置在程序    里面
//     */
//    private static final Map<Integer, Integer> dataSourcePermissionMap = Maps.newHashMap();
//
//    private static final String META_COLUMN_MYSQL_SQL = "select * from %s where 1=1 limit 3";
//    private static final String META_COLUMN_DB2_SQL = "select * from %s where 1=1 fetch first 3 rows only";
//    private static final String META_COLUMN_ORACLE_SQL = "select * from %s where 1=1 and rownum <=3";
//    private static final String META_COLUMN_SQLSERVER_SQL = "select TOP 3 * from %s";
//
//    private static final String META_COLUMN_HIVE_SQL = "select * from %s limit 3";
//    private static final String META_COLUMN_HIVE_SQL_WITH_PARTITION = "select * from %s where %s limit 3";
//
//    public static final String JDBC_URL = "jdbc.url";
//    public static final String JDBC_USERNAME = "jdbc.username";
//    public static final String JDBC_PASSWORD = "jdbc.password";
//    public static final String JDBC_HOSTPORTS = "hostPorts";
//
//    public static final String HDFS_DEFAULTFS = "defaultFS";
//
//    public static final String HADOOP_CONFIG = "hadoopConfig";
//
//    private static final String HBASE_CONFIG = "hbaseConfig";
//
//    public static final String HIVE_PARTITION = "partition";
//
//    public static final String TEMP_TABLE_PREFIX = "select_sql_temp_table_";
//
//    public static final String TEMP_TABLE_PREFIX_FROM_DQ = "temp_data_";
//
//    private static final String NAME_PREFIX = "link_";
//
//    private static final String KEY = "key";
//
//    private static final String TYPE = "type";
//
//    private static final String COLUMN = "column";
//
//    private static final String ExtralConfig = "extralConfig";
//
//    private static final List<String> MYSQL_NUMBERS = Lists.newArrayList("TINYINT", "SMALLINT", "MEDIUMINT", "INT", "BIGINT", "INT UNSIGNED");
//
//    private static final List<String> CLICKHOUSE_NUMBERS = Lists.newArrayList("UINT8", "UINT16", "UINT32", "UINT64", "INT8", "INT16", "INT32", "INT64");
//
//    private static final List<String> ORACLE_NUMBERS = Lists.newArrayList("INT", "SMALLINT", "NUMBER");
//
//    private static final List<String> SQLSERVER_NUMBERS = Lists.newArrayList("INT", "INTEGER", "SMALLINT", "TINYINT", "BIGINT");
//
//    private static final List<String> POSTGRESQL_NUMBERS = Lists.newArrayList("INT2", "INT4", "INT8", "SMALLINT", "INTEGER", "BIGINT", "SMALLSERIAL", "SERIAL", "BIGSERIAL");
//
//    private static final List<String> DB2_NUMBERS = Lists.newArrayList("SMALLINT", "INTEGER", "BIGINT");
//
//    private static final List<String> GBASE_NUMBERS = Lists.newArrayList("SMALLINT", "TINYINT", "INT", "BIGINT", "FLOAT", "DOUBLE", "DECIMAL", "NUMERIC");
//
//    private static final List<String> DMDB_NUMBERS = Lists.newArrayList("INT", "SMALLINT", "BIGINT","NUMBER");
//
//    private static final List<String> GREENPLUM_NUMBERS = Lists.newArrayList("SMALLINT", "INTEGER", "BIGINT");
//
//    private static final List<String> KINGBASE_NUMBERS = Lists.newArrayList("BIGINT", "DOUBLE", "FLOAT", "INT4", "INT8", "FLOAT", "FLOAT8", "NUMERIC");
//
//    private static final Pattern NUMBER_PATTERN = Pattern.compile("NUMBER\\(\\d+\\)");
//
//    private static final Pattern NUMBER_PATTERN2 = Pattern.compile("NUMBER\\((\\d+),([\\d-]+)\\)");
//
//    private static final ObjectMapper objectMapper = new ObjectMapper();
//
//    private static final String DEFAULT_FS_REGEX = "hdfs://.*";
//
//    private static final TypeFormat TYPE_FORMAT = new HiveWriterFormat();
//
//    private static final String NO_PERMISSION = "NO PERMISSION";
//
//    private static final String hdfsCustomConfig = "hdfsCustomConfig";
//
//    private static final String DESC_EXTENDED = "desc extended %s";
//    private static final String DESC_TABLE_TEMPLATE = "desc %s";
//    private static final String COL_NAME = "col_name";
//    private static final String DATA_TYPE = "data_type";
//    private static final String COMMENT = "comment";
//
//    private static final String SEPARATE = "/";
//
//    private static final String KERBEROS_CONFIG = "kerberosConfig";
//    /**
//     * Kerberos 文件上传的时间戳
//     */
//    private static final String KERBEROS_FILE_TIMESTAMP = "kerberosFileTimestamp";
//    private static final String OPEN_KERBEROS = "openKerberos";
//    private static final String KERBEROS_FILE = "kerberosFile";
//
//    public static final Pattern ORACLE_SID = Pattern.compile("(?i)jdbc:oracle:thin:@(?<ip>[0-9a-zA-Z\\.]+):(?<port>[0-9]+):.*");
//
//    public static final Pattern ORACLE_SERVICE = Pattern.compile("(?i)jdbc:oracle:thin:@//(?<ip>[0-9a-zA-Z\\.]+):(?<port>[0-9]+)/.*");
//
//    public static final Pattern TNS_NAME = Pattern.compile("(?i)jdbc:oracle:thin:@\\(.*\\)");
//
//    private static final ExecutorService es = new ThreadPoolExecutor(8, 8, 1L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(1000), new RdosThreadFactory("batchDataSourceService"), new ThreadPoolExecutor.CallerRunsPolicy());
//
//    //如果数据源连接失败，重试次数
//    private static final Integer retryNum = 1;
//
//    private static final Map<Long, Integer> sourceFailedMap = new ConcurrentHashMap<>();
//
//    private static final Integer CANNOT_CONNECT = 0;
//    private static final Integer CONNECT_ACTIVE = 1;
//    private static final String TITLE = "数据源连接告警";
//    private static final String MESSAGE_TEMPLATE = "<%s> 数据源连接异常触发告警\n" +
//            "离线计算模块，因数据源连接异常触发告警（系统每隔10分钟检测一次连通性，连续2次无法连通，触发告警）\n" +
//            "数据源：%s（%s）\n" +
//            "连接信息：%s\n" +
//            "触发告警时间：%s";
//
//    private static final List<String> senderTypeList = Arrays.asList(AlarmTypeEnum.MAIL.getAlertGateSource(), AlarmTypeEnum.SMS.getAlertGateSource());
//
//    static {
//        BatchDataSourceService.dataSourcePermissionMap.put(DataSourceType.MySQL.getVal(), EDataSourcePermission.READ_WRITE.getType());
//        BatchDataSourceService.dataSourcePermissionMap.put(DataSourceType.Oracle.getVal(), EDataSourcePermission.READ_WRITE.getType());
//        BatchDataSourceService.dataSourcePermissionMap.put(DataSourceType.SQLServer.getVal(), EDataSourcePermission.READ_WRITE.getType());
//        BatchDataSourceService.dataSourcePermissionMap.put(DataSourceType.PostgreSQL.getVal(), EDataSourcePermission.READ_WRITE.getType());
//        BatchDataSourceService.dataSourcePermissionMap.put(DataSourceType.RDBMS.getVal(), EDataSourcePermission.READ_WRITE.getType());
//        BatchDataSourceService.dataSourcePermissionMap.put(DataSourceType.HDFS.getVal(), EDataSourcePermission.READ_WRITE.getType());
//        BatchDataSourceService.dataSourcePermissionMap.put(DataSourceType.HIVE.getVal(), EDataSourcePermission.READ_WRITE.getType());
//        BatchDataSourceService.dataSourcePermissionMap.put(DataSourceType.DB2.getVal(), EDataSourcePermission.READ_WRITE.getType());
//        BatchDataSourceService.dataSourcePermissionMap.put(DataSourceType.Clickhouse.getVal(), EDataSourcePermission.READ_WRITE.getType());
//        BatchDataSourceService.dataSourcePermissionMap.put(DataSourceType.HIVE1X.getVal(), EDataSourcePermission.READ_WRITE.getType());
//        BatchDataSourceService.dataSourcePermissionMap.put(DataSourceType.Phoenix.getVal(), EDataSourcePermission.READ_WRITE.getType());
//        BatchDataSourceService.dataSourcePermissionMap.put(DataSourceType.PHOENIX5.getVal(), EDataSourcePermission.READ_WRITE.getType());
//        BatchDataSourceService.dataSourcePermissionMap.put(DataSourceType.TiDB.getVal(), EDataSourcePermission.READ_WRITE.getType());
//        BatchDataSourceService.dataSourcePermissionMap.put(DataSourceType.DMDB.getVal(), EDataSourcePermission.READ_WRITE.getType());
//        BatchDataSourceService.dataSourcePermissionMap.put(DataSourceType.GREENPLUM6.getVal(), EDataSourcePermission.READ_WRITE.getType());
//        BatchDataSourceService.dataSourcePermissionMap.put(DataSourceType.KINGBASE8.getVal(), EDataSourcePermission.READ_WRITE.getType());
//    }
//
//    @Autowired
//    private BatchDataSourceDao batchDataSourceDao;
//
//    @Autowired
//    private BatchTaskService taskService;
//
//    @Autowired
//    private BatchDataSourceTaskRefService dataSourceTaskRefService;
//
//    @Autowired
//    private DictService dictService;
//
//    @Autowired
//    private RoleUserService roleUserService;
//
//    @Autowired
//    private UserDao userDao;
//
//    @Autowired
//    private BatchDirtyDataService batchDirtyDataService;
//
//    @Autowired
//    private BatchTablePermissionService batchTablePermissionService;
//
//    @Autowired
//    private TenantService tenantService;
//
//    @Autowired
//    private ProjectService projectService;
//
//    @Autowired
//    private BatchDataSourceTaskRefDao batchDataSourceTaskRefDao;
//
//    @Autowired
//    private BatchTestProduceDataSourceService batchTestProduceDataSourceService;
//
//    @Autowired
//    private BatchTestProduceDataSourceDao batchTestProduceDataSourceDao;
//
//    @Autowired
//    private BatchTaskParamService batchTaskParamService;
//
//    @Autowired
//    private BatchTaskService batchTaskService;
//
//    @Autowired
//    private BatchTableInfoService batchTableInfoService;
//
//    @Autowired
//    private DataMaskColumnInfoService dataMaskColumnInfoService;
//
//    @Autowired
//    private ProjectEngineService projectEngineService;
//
//    @Autowired
//    private EnvironmentContext environmentContext;
//
//    @Autowired
//    private ConsoleSend consoleSend;
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private NotifyDao notifyDao;
//
//    @Autowired
//    private ConsoleNotifyApiClient consoleNotifyApiClient;
//
//    @Autowired
//    private NotifyRecordDao notifyRecordDao;
//
//    @Autowired
//    private SyncBuilderFactory syncBuilderFactory;
//
//    /**
//     * 判断任务是否可以配置增量标识
//     */
//    public boolean canSetIncreConf(Long taskId) {
//        final BatchTask task = this.batchTaskService.getBatchTaskById(taskId);
//        if (task == null) {
//            throw new RdosDefineException(ErrorCode.DATA_NOT_FIND);
//        }
//
//        if (!EJobType.SYNC.getVal().equals(task.getTaskType())) {
//            return false;
//        }
//
//        // 增量同步任务不能在工作流中运行
//        if (task.getFlowId() != 0) {
//            return false;
//        }
//
//        if (StringUtils.isEmpty(task.getSqlText())) {
//            return true;
//        }
//
//        try {
//            final JSONObject json = JSON.parseObject(Base64Util.baseDecode(task.getSqlText()));
//            this.batchTaskService.checkSyncJobContent(json.getJSONObject("job"), false);
//        } catch (final RdosDefineException e) {
//            return false;
//        }
//
//        return true;
//    }
//
//    @Transactional(rollbackFor = Exception.class)
//    public void deleteByProjectId(Long projectId, Long userId) {
//        batchDataSourceDao.deleteByProjectId(projectId, userId);
//        batchDataSourceTaskRefDao.deleteByProjectId(projectId);
//    }
//
//    public JSONObject trace(final Long taskId) {
//        String sqlText = null;
//        final BatchTask batchTask = this.taskService.getBatchTaskById(taskId);
//
//        if (batchTask == null) {
//            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_TASK);
//        } else {
//            sqlText = batchTask.getSqlText();
//        }
//
//        final String sql = Base64Util.baseDecode(sqlText);
//        if (StringUtils.isBlank(sql)) {
//            return null;
//        }
//
//        final JSONObject sqlJson = JSON.parseObject(sql);
//        JSONObject parserJson = sqlJson.getJSONObject("parser");
//        if (parserJson != null) {
//            parserJson = this.checkTrace(parserJson);
//            parserJson.put("sqlText", sqlJson.getString("job"));
//            parserJson.put("syncMode", sqlJson.get("syncMode"));
//            parserJson.put("taskId", taskId);
//        }
//        return parserJson;
//    }
//
//    private JSONObject checkTrace(final JSONObject jsonObject) {
//        final JSONObject keymap = jsonObject.getJSONObject("keymap");
//        final JSONArray source = keymap.getJSONArray("source");
//        final JSONArray target = keymap.getJSONArray("target");
//        final JSONObject sourceMap = jsonObject.getJSONObject("sourceMap");
//        final Integer fromId = (Integer) sourceMap.get("sourceId");
//        final JSONObject targetMap = jsonObject.getJSONObject("targetMap");
//        final Integer toId = (Integer) targetMap.get("sourceId");
//        final JSONObject sourceType = sourceMap.getJSONObject("type");
//        final List<String> sourceTables = this.getTables(sourceType);
//        final JSONObject targetType = targetMap.getJSONObject("type");
//        final List<String> targetTables = this.getTables(targetType);
//        final BatchDataSource fromDs = this.getOne(fromId.longValue());
//        final BatchDataSource toDs = this.getOne(toId.longValue());
//
//        int fromSourceType = DataSourceType.getSourceType(fromDs.getType()).getVal();
//        int toSourceType = DataSourceType.getSourceType(toDs.getType()).getVal();
//        if (DataSourceType.HBASE.getVal() == fromSourceType || DataSourceType.HBASE.getVal() == toSourceType) {
//            return jsonObject;
//        }
//
//        // 处理分库分表的信息
//        this.addSourceList(sourceMap);
//
//        if (CollectionUtils.isNotEmpty(sourceTables)) {
//            getMetaDataColumns(sourceMap, sourceTables, fromDs);
//        }
//
//        if (CollectionUtils.isNotEmpty(targetTables)) {
//            getMetaDataColumns(targetMap, targetTables, toDs);
//        }
//        //因为下面要对keyMap中target中的字段类型进行更新 所以遍历一次目标map 拿出字段和类型的映射
//        Map<String,String> newTargetColumnTypeMap = targetMap.getJSONArray(COLUMN)
//                .stream().map(column -> (JSONObject)column)
//                .collect(Collectors.toMap(column -> column.getString(BatchDataSourceService.KEY), column -> column.getString(BatchDataSourceService.TYPE)));
//
//
//        final Collection<BatchSysParameter> sysParams = this.batchTaskService.getSysParams();
//
//        final JSONArray newSource = new JSONArray();
//        final JSONArray newTarget = new JSONArray();
//        for (int i = 0; i < source.size(); ++i) {
//            boolean srcTag = true;
//            final JSONArray srcColumns = sourceMap.getJSONArray("column");
//            if (CollectionUtils.isNotEmpty(sourceTables)) {
//                int j = 0;
//                final String srcColName;
//                String colValue = "";
//                if (!(source.get(i) instanceof JSONObject)) {
//                    srcColName = source.getString(i);
//                } else {
//                    //source 可能含有系统变量
//                    srcColName = source.getJSONObject(i).getString("key");
//                    colValue = source.getJSONObject(i).getString("value");
//                }
//
//                //srcColumns 源表中的字段
//                for (; j < srcColumns.size(); ++j) {
//                    final JSONObject srcColumn = srcColumns.getJSONObject(j);
//                    if (srcColumn.getString("key").equals(srcColName)) {
//                        break;
//                    }
//                }
//                boolean isSysParam = false;
//                for (final BatchSysParameter sysParam : sysParams) {
//                    if (sysParam.strIsSysParam(colValue)) {
//                        isSysParam = true;
//                        break;
//                    }
//                }
//                // 没有系统变量 还需要判断是否有自定义变量
//                if(!isSysParam){
//                    isSysParam = StringUtils.isNotBlank(colValue);
//                }
//                //兼容系统变量
//                if (isSysParam) {
//                    boolean hasThisKey = false;
//                    for (int k = 0; k < srcColumns.size(); ++k) {
//                        final JSONObject srcColumn = srcColumns.getJSONObject(k);
//                        if (srcColumn.getString("key").equals(srcColName)) {
//                            hasThisKey = true;
//                            break;
//                        }
//
//                    }
//                    if (!hasThisKey) {
//                        //创建出系统变量colume
//                        final JSONObject jsonColumn = new JSONObject();
//                        jsonColumn.put("key", srcColName);
//                        jsonColumn.put("value", colValue);
//                        jsonColumn.put("type",source.getJSONObject(i).getString("type"));
//                        jsonColumn.put("format",source.getJSONObject(i).getString("format"));
//                        srcColumns.add(jsonColumn);
//                    }
//                }
//                if (j == srcColumns.size() && !isSysParam) {
//                    srcTag = false;
//                }
//            }
//
//            boolean destTag = true;
//            final JSONArray destColumns = targetMap.getJSONArray("column");
//            if (CollectionUtils.isNotEmpty(targetTables)) {
//                int k = 0;
//                final String destColName;
//                if (!(target.get(i) instanceof JSONObject)) {
//                    destColName = target.getString(i);
//                } else {
//                    destColName = target.getJSONObject(i).getString("key");
//                    //更新dest表中字段类型
//                    final String newType = newTargetColumnTypeMap.get(destColName);
//                    if (StringUtils.isNotEmpty(newType)){
//                        target.getJSONObject(i).put("type",newType);
//                    }
//                }
//                for (; k < destColumns.size(); ++k) {
//                    final JSONObject destColumn = destColumns.getJSONObject(k);
//                    if (destColumn.getString("key").equals(destColName)) {
//                        break;
//                    }
//                }
//
//                if (k == destColumns.size()) {
//                    destTag = false;
//                }
//            }
//
//            if (srcTag && destTag) {
//                newSource.add(source.get(i));
//                newTarget.add(target.get(i));
//            }
//        }
//
//        keymap.put("source", newSource);
//        keymap.put("target", newTarget);
//
//        return jsonObject;
//    }
//
//    /**
//     * 刷新sourceMap中的字段信息
//     * 这个方法做了3个事情
//     * 1.拿到sourceMap的中的原字段信息
//     * 2.拿到对应表的 元数据最新字段信息
//     * 3.和原字段信息进行匹配，
//     * 如果原字段中的某个字段 不在最新字段中 那就忽略大小写再匹配一次，如果能匹配到就用原字段信息
//     * 原因是 Hive执行alter语句增加字段会把源信息所有字段变小写  导致前端映射关系丢失 这里做一下处理
//     * @param sourceMap
//     * @param sourceTables
//     * @param fromDs
//     */
//    private void getMetaDataColumns(JSONObject sourceMap, List<String> sourceTables, BatchDataSource fromDs) {
//        JSONArray srcColumns = new JSONArray();
//        List<JSONObject> custColumns = new ArrayList<>();
//        List<String> allOldColumnsName = new ArrayList<>();
//        Map<String,String> newNameToOldName = new HashMap<>();
//        //对原有的字段进行处理 处理方式看方法注释
//        getAllTypeColumnsMap(sourceMap, custColumns, allOldColumnsName, newNameToOldName);
//        //获取原有字段
//        JSONArray sourceColumns = sourceMap.getJSONArray(COLUMN);
//        try {
//            //获取一下schema
//            String schema = sourceMap.getString("schema");
//            List<JSONObject> tableColumns = getTableColumnIncludePart(fromDs, sourceTables.get(0), true, schema);
//            for (JSONObject tableColumn : tableColumns) {
//                String columnName = tableColumn.getString(BatchDataSourceService.KEY);
//                //获取前端需要的真正的字段名称
//                columnName = getRealColumnName(allOldColumnsName, newNameToOldName, columnName);
//
//                String columnType = tableColumn.getString(BatchDataSourceService.TYPE);
//                JSONObject jsonColumn = new JSONObject();
//                jsonColumn.put(KEY, columnName);
//                jsonColumn.put(TYPE, columnType);
//                if (StringUtils.isNotEmpty(tableColumn.getString("isPart"))) {
//                    jsonColumn.put("isPart", tableColumn.get("isPart"));
//                }
//
//                if (!(sourceColumns.get(0) instanceof String)) {
//                    //这个是兼容原来的desc table 出来的结果 因为desc出来的不仅仅是字段名
//                    for (int i = 0; i < sourceColumns.size(); i++) {
//                        final JSONObject item = sourceColumns.getJSONObject(i);
//                        if (item.get(KEY).equals(columnName) && item.containsKey("format")) {
//                            jsonColumn.put("format", item.getString("format"));
//                            break;
//                        }
//                    }
//                }
//                srcColumns.add(jsonColumn);
//            }
//        } catch (Exception ignore) {
//            logger.error("数据同步获取表字段异常 : ", ignore.getMessage(), ignore);
//            srcColumns = sourceColumns;
//        }
//        if (CollectionUtils.isNotEmpty(custColumns)) {
//            srcColumns.addAll(custColumns);
//        }
//        sourceMap.put(COLUMN, srcColumns);
//    }
//
//    /**
//     * 拿到真实的字段名
//     * 判断 如果
//     * @param allOldColumnsName  原有的所有字段的字段名
//     * @param newNameToOldName   key是原有字段名的小写  value是原有字段名
//     * @param columnName  元数据字段名
//     * @return
//     */
//    private String getRealColumnName(List<String> allOldColumnsName, Map<String, String> newNameToOldName, String columnName) {
//        if (allOldColumnsName.contains(columnName)){
//            //认为字段名没有改动 直接返回
//            return columnName;
//        }
//
//        String oldColumnName = newNameToOldName.get(columnName);
//        if (StringUtils.isBlank(oldColumnName)){
//            //认为字段名没有从大写变小写
//            return columnName;
//        }
//        //字段名大写变小写了 所以返回原有字段名 保证前端映射无问题
//        return oldColumnName;
//    }
//
//    /**
//     * 这个方法 是对老数据中的字段做一下处理 会出来3个集合
//     * @param sourceMap 源信息
//     * @param custColumns 用户自定义字段
//     * @param allOldColumnsName  老字段名称集合
//     * @param newNameToOldName  新字段名称和老字段名字对应集合  key：字段名小写  value 原字段名 用处hive增加字段 字段名全小写导致对应关系丢失
//     */
//    private void getAllTypeColumnsMap(JSONObject sourceMap,List<JSONObject> custColumns,List<String> allOldColumnsName,Map<String,String> newNameToOldName ){
//        JSONArray sourceColumns = sourceMap.getJSONArray(COLUMN);
//        if (sourceColumns == null){
//            return;
//        }
//        for (int i = 0; i < sourceColumns.size(); ++i) {
//            JSONObject column = sourceColumns.getJSONObject(i);
//            if (column.containsKey("value")) {
//                custColumns.add(column);
//                continue;
//            }
//            String key = column.getString(KEY);
//            if (StringUtils.isBlank(key)){
//                continue;
//            }
//            allOldColumnsName.add(key);
//            newNameToOldName.put(key.toLowerCase(),key);
//        }
//
//    }
//
//    private List<String> getTables(final Map<String, Object> map) {
//        final List<String> tables = new ArrayList<>();
//        if (map.get("table") instanceof String) {
//            tables.add(map.get("table").toString());
//        } else {
//            final List<String> tableList = (List<String>) map.get("table");
//            if (CollectionUtils.isNotEmpty(tableList)) {
//                tables.addAll((List<String>) map.get("table"));
//            }
//        }
//
//        return tables;
//    }
//
//    private void addSourceList(final JSONObject sourceMap) {
//        if (sourceMap.containsKey("sourceList")) {
//            return;
//        }
//
//        if (sourceMap.getJSONObject("type").getInteger("type") != DataSourceType.MySQL.getVal()) {
//            return;
//        }
//
//        final JSONArray sourceList = new JSONArray();
//        final JSONObject source = new JSONObject();
//        source.put("sourceId", sourceMap.get("sourceId"));
//        source.put("name", sourceMap.getString("name"));
//        source.put("type", sourceMap.getJSONObject("type").getInteger("type"));
//        source.put("tables", Arrays.asList(sourceMap.getJSONObject("type").getString("table")));
//        sourceList.add(source);
//
//        sourceMap.put("sourceList", sourceList);
//    }
//
//    /**
//     * 配置或修改离线任务
//     *
//     * @param isFilter 获取数据同步脚本时候是否进行过滤用户名密码操作
//     * @return
//     * @throws IOException
//     * @throws JsonGenerationException
//     * @throws JsonMappingException
//     * @throws JsonParseException
//     */
//    public String getSyncSql(final TaskResourceParam param, boolean isFilter) {
//        final Map<String, Object> sourceMap = param.getSourceMap();//来源集合
//        final Map<String, Object> targetMap = param.getTargetMap();//目标集合
//        final Map<String, Object> settingMap = param.getSettingMap();//流控、错误集合
//        try {
//            //清空资源和任务的关联关系
//            this.dataSourceTaskRefService.removeRef(param.getId());
//
//            this.setReaderJson(sourceMap, param.getId(), param.getProjectId(), param.getTenantId(), isFilter);
//            this.setWriterJson(targetMap, param.getId(), param.getProjectId(), param.getTenantId(), isFilter);
//            Reader reader = null;
//            Writer writer = null;
//            Setting setting = null;
//
//            final Integer sourceType = Integer.parseInt(sourceMap.get("dataSourceType").toString());
//            final Integer targetType = Integer.parseInt(targetMap.get("dataSourceType").toString());
//
//            if (!this.checkDataSourcePermission(sourceType, EDataSourcePermission.READ.getType())) {
//                throw new RdosDefineException(ErrorCode.SOURCE_CAN_NOT_AS_INPUT);
//            }
//
//            if (!this.checkDataSourcePermission(targetType, EDataSourcePermission.WRITE.getType())) {
//                throw new RdosDefineException(ErrorCode.SOURCE_CAN_NOT_AS_OUTPUT);
//            }
//
//            final List<Long> sourceIds = (List<Long>) sourceMap.get("sourceIds");
//            final List<Long> targetIds = (List<Long>) targetMap.get("sourceIds");
//
//            reader = this.syncReaderBuild(sourceType, sourceMap, sourceIds);
//            writer = this.syncWriterBuild(targetType, targetIds, targetMap, reader);
//
//            this.setDirtyData(settingMap, param);
//            setting = PublicUtil.objectToObject(settingMap, DefaultSetting.class);
//
//            //检查有效性
//            if (writer instanceof HiveWriter) {
//                final HiveWriter hiveWriter = (HiveWriter) writer;
//                if (!hiveWriter.isValid()) {
//                    throw new RdosDefineException(hiveWriter.getErrMsg());
//                }
//            }
//
//            if (param.getCreateModel() == TaskCreateModelType.TEMPLATE.getType()) {  //脚本模式直接返回
//                return this.getJobText(this.putDefaultEmptyValueForReader(sourceType, reader),
//                        this.putDefaultEmptyValueForWriter(targetType, writer), this.putDefaultEmptyValueForSetting(setting));
//            }
//
//            //获得数据同步job.xml的配置
//            final String jobXml = this.getJobText(reader, writer, setting);
//            final String parserXml = this.getParserText(sourceMap, targetMap, settingMap);
//            final JSONObject sql = new JSONObject(3);
//            sql.put("job", jobXml);
//            sql.put("parser", parserXml);
//            sql.put("createModel", TaskCreateModelType.GUIDE.getType());
//
//            this.batchTaskParamService.checkParams(this.batchTaskParamService.checkSyncJobParams(sql.toJSONString()), param.getTaskVariables());
//            return sql.toJSONString();
//        } catch (final Exception e) {
//            BatchDataSourceService.logger.error("{}", e);
//            throw new RdosDefineException("解析同步任务失败: " + e.getMessage(), ErrorCode.SERVER_EXCEPTION);
//        }
//    }
//
//    private Reader syncReaderBuild(final Integer sourceType, final Map<String, Object> sourceMap, final List<Long> sourceIds) throws IOException {
//
//        Reader reader = null;
//        if (Objects.nonNull(RDBMSSourceType.getByDataSourceType(sourceType))
//                && !DataSourceType.HIVE.getVal().equals(sourceType)
//                && !DataSourceType.HIVE1X.getVal().equals(sourceType)
//                && !DataSourceType.CarbonData.getVal().equals(sourceType)
//                && !DataSourceType.IMPALA.getVal().equals(sourceType)) {
//            reader = PublicUtil.objectToObject(sourceMap, RDBReader.class);
//            ((RDBBase) reader).setSourceIds(sourceIds);
//            return reader;
//        }
//
//        if (DataSourceType.HDFS.getVal().equals(sourceType)) {
//            return PublicUtil.objectToObject(sourceMap, HDFSReader.class);
//        }
//
//        if (DataSourceType.HIVE.getVal().equals(sourceType) || DataSourceType.HIVE1X.getVal().equals(sourceType)) {
//            return PublicUtil.objectToObject(sourceMap, HiveReader.class);
//        }
//
//        if (DataSourceType.HBASE.getVal().equals(sourceType)) {
//            return PublicUtil.objectToObject(sourceMap, HBaseReader.class);
//        }
//
//        if (DataSourceType.FTP.getVal().equals(sourceType)) {
//            reader = PublicUtil.objectToObject(sourceMap, FtpReader.class);
//            if (sourceMap.containsKey("isFirstLineHeader") && (Boolean) sourceMap.get("isFirstLineHeader")) {
//                ((FtpReader) reader).setFirstLineHeader(true);
//            } else {
//                ((FtpReader) reader).setFirstLineHeader(false);
//            }
//            return reader;
//        }
//
//        if (DataSourceType.MAXCOMPUTE.getVal().equals(sourceType)) {
//            reader = PublicUtil.objectToObject(sourceMap, OdpsReader.class);
//            ((OdpsBase) reader).setSourceId(sourceIds.get(0));
//            return reader;
//        }
//
//        if (DataSourceType.ES.getVal().equals(sourceType)) {
//            return PublicUtil.objectToObject(sourceMap, EsReader.class);
//        }
//
//        if (DataSourceType.MONGODB.getVal().equals(sourceType)) {
//            return PublicUtil.objectToObject(sourceMap, MongoDbReader.class);
//        }
//
//        if (DataSourceType.CarbonData.getVal().equals(sourceType)) {
//            return PublicUtil.objectToObject(sourceMap, CarbonDataReader.class);
//        }
//
//        if (DataSourceType.Kudu.getVal().equals(sourceType)) {
//            return syncBuilderFactory.getSyncBuilder(DataSourceType.Kudu.getVal()).syncReaderBuild(sourceMap, sourceIds);
//        }
//
//        if (DataSourceType.IMPALA.getVal().equals(sourceType)) {
//            //setSftpConf时，设置的hdfsConfig和sftpConf
//            if (sourceMap.containsKey("hadoopConfig")){
//                Object impalaConfig = sourceMap.get("hadoopConfig");
//                if (impalaConfig instanceof Map){
//                    sourceMap.put("hadoopConfig",impalaConfig);
//                    sourceMap.put("sftpConf",((Map) impalaConfig).get("sftpConf"));
//                }
//            }
//            return syncBuilderFactory.getSyncBuilder(DataSourceType.IMPALA.getVal()).syncReaderBuild(sourceMap, sourceIds);
//        }
//
//
//        throw new RdosDefineException("暂不支持" +DataSourceType.getSourceType(sourceType).name() +"作为数据同步的源");
//    }
//
//    private Writer syncWriterBuild(final Integer targetType, final List<Long> targetIds, final Map<String, Object> targetMap, final Reader reader) throws IOException {
//        Writer writer = null;
//
//        if (Objects.nonNull(RDBMSSourceType.getByDataSourceType(targetType))
//                && !DataSourceType.HIVE.getVal().equals(targetType)
//                && !DataSourceType.HIVE1X.getVal().equals(targetType)
//                && !DataSourceType.IMPALA.getVal().equals(targetType)
//                && !DataSourceType.CarbonData.getVal().equals(targetType)) {
//            writer = PublicUtil.objectToObject(targetMap, RDBWriter.class);
//            ((RDBBase) writer).setSourceIds(targetIds);
//            return writer;
//        }
//
//        if (DataSourceType.HDFS.getVal().equals(targetType)) {
//            return PublicUtil.objectToObject(targetMap, HDFSWriter.class);
//        }
//
//        if (DataSourceType.HIVE.getVal().equals(targetType) || DataSourceType.HIVE1X.getVal().equals(targetType)) {
//            return PublicUtil.objectToObject(targetMap, HiveWriter.class);
//        }
//
//        if (DataSourceType.FTP.getVal().equals(targetType)) {
//            return PublicUtil.objectToObject(targetMap, FtpWriter.class);
//        }
//
//        if (DataSourceType.ES.getVal().equals(targetType)) {
//            return PublicUtil.objectToObject(targetMap, EsWriter.class);
//        }
//
//        if (DataSourceType.HBASE.getVal().equals(targetType)) {
//            targetMap.put("hbaseConfig",targetMap.get("hbaseConfig"));
//            writer = PublicUtil.objectToObject(targetMap, HBaseWriter.class);
//            HBaseWriter hbaseWriter = (HBaseWriter) writer;
//            List<String> sourceColNames = new ArrayList<>();
//            List<Map<String,String>> columnList = (List<Map<String, String>>) targetMap.get("column");
//            for (Map<String,String> column : columnList){
//                if (column.containsKey("key")){
//                    sourceColNames.add(column.get("key"));
//                }
//            }
//            hbaseWriter.setSrcColumns(sourceColNames);
//            return writer;
//        }
//
//        if (DataSourceType.MAXCOMPUTE.getVal().equals(targetType)) {
//            writer = PublicUtil.objectToObject(targetMap, OdpsWriter.class);
//            ((OdpsBase) writer).setSourceId(targetIds.get(0));
//            return writer;
//        }
//
//        if (DataSourceType.REDIS.getVal().equals(targetType)) {
//            return PublicUtil.objectToObject(targetMap, RedisWriter.class);
//        }
//
//        if (DataSourceType.MONGODB.getVal().equals(targetType)) {
//            return PublicUtil.objectToObject(targetMap, MongoDbWriter.class);
//        }
//
//        if (DataSourceType.CarbonData.getVal().equals(targetType)) {
//            return PublicUtil.objectToObject(targetMap, CarbonDataWriter.class);
//        }
//
//        if (DataSourceType.Kudu.getVal().equals(targetType)) {
//            return syncBuilderFactory.getSyncBuilder(DataSourceType.Kudu.getVal()).syncWriterBuild(targetIds, targetMap, reader);
//        }
//
//        if (DataSourceType.IMPALA.getVal().equals(targetType)) {
//            return syncBuilderFactory.getSyncBuilder(DataSourceType.IMPALA.getVal()).syncWriterBuild(targetIds, targetMap, reader);
//        }
//
//        throw new RdosDefineException("暂不支持" +DataSourceType.getSourceType(targetType).name() +"作为数据同步的目标");
//    }
//
//    private void setDirtyData(final Map<String, Object> settingMap, final TaskResourceParam param) {
//        if (param.getCreateModel() == TaskCreateModelType.TEMPLATE.getType()) {
//            settingMap.put("isSaveDirty", 1);
//            final BatchTask task = this.taskService.getBatchTaskById(param.getId());
//            param.setName(task.getName());
//        }
//
//        if (settingMap.containsKey("isSaveDirty")) {
//
//            // 兼容前端的0/1和true/false
//            final String isSaveDirty = settingMap.get("isSaveDirty").toString();
//            boolean isSaveDirtyVal = false;
//            if ("1".equals(isSaveDirty) || "true".equals(isSaveDirty)) {
//                isSaveDirtyVal = true;
//                settingMap.put("isSaveDirty", 1);
//            } else {
//                settingMap.put("isSaveDirty", 0);
//            }
//
//            final Long dtuicTenantId = this.tenantService.getDtuicTenantId(param.getTenantId());
//
//            if (isSaveDirtyVal) {
//                String tableName = "";
//                if (settingMap.containsKey("tableName") && settingMap.get("tableName") != null) {
//                    tableName = settingMap.get("tableName").toString();
//                }
//
//                Integer lifeDay = null;
//                if (settingMap.get("lifeDay") != null) {
//                    lifeDay = Integer.parseInt(settingMap.get("lifeDay").toString());
//                }
//
//                final Map<String, Object> readyParam = batchDirtyDataService.readyForSaveDirtyData(tableName, lifeDay, param.getUserId(), param.getId(), param.getName(), param.getTenantId(), dtuicTenantId,
//                        param.getProjectId(), MultiEngineType.HADOOP.getType());
//
//                settingMap.putAll(readyParam);
//            }
//        } else {
//            settingMap.put("isSaveDirty", 0);
//        }
//    }
//
//    private Reader putDefaultEmptyValueForReader(int sourceType, Reader reader) {
//        if (Objects.nonNull(RDBMSSourceType.getByDataSourceType(sourceType))
//                && DataSourceType.HIVE.getVal() != sourceType
//                && DataSourceType.HIVE1X.getVal() != sourceType
//                && DataSourceType.CarbonData.getVal() != sourceType) {
//            RDBReader rdbReader = (RDBReader) reader;
//            rdbReader.setWhere("");
//            rdbReader.setSplitPK("");
//            return rdbReader;
//        } else if (DataSourceType.ES.getVal() == sourceType) {
//            EsReader esReader = (EsReader) reader;
//            JSONObject obj = new JSONObject();
//            obj.put("col", "");
//            JSONObject query = new JSONObject();
//            query.put("match", obj);
//            esReader.setQuery(query);
//            JSONObject column = new JSONObject();
//            column.put("key", "col1");
//            column.put("type", "string");
//            esReader.getColumn().add(column);
//            return esReader;
//        } else if (DataSourceType.FTP.getVal() == sourceType) {
//            FtpReader ftpReader = (FtpReader) reader;
//            ftpReader.setPath("/");
//            return ftpReader;
//        }
//        return reader;
//    }
//
//    private Writer putDefaultEmptyValueForWriter(int targetType, Writer writer) {
//        if (Objects.nonNull(RDBMSSourceType.getByDataSourceType(targetType))
//                && DataSourceType.HIVE.getVal() != targetType
//                && DataSourceType.HIVE1X.getVal() != targetType
//                && DataSourceType.CarbonData.getVal() != targetType) {
//            RDBWriter rdbWriter = (RDBWriter) writer;
//            rdbWriter.setPostSql("");
//            rdbWriter.setPostSql("");
//            rdbWriter.setSession("");
//            if (DataSourceType.GREENPLUM6.getVal() == targetType){
//                rdbWriter.setWriteMode("insert");
//            }else {
//                rdbWriter.setWriteMode("replace");
//            }
//            return rdbWriter;
//        } else if (DataSourceType.ES.getVal() == targetType) {
//            EsWriter esWriter = (EsWriter) writer;
//            esWriter.setType("");
//            esWriter.setIndex("");
//            JSONObject column = new JSONObject();
//            column.put("key", "col1");
//            column.put("type", "string");
//            JSONObject idColumn = new JSONObject();
//            idColumn.put("index", 0);
//            idColumn.put("type", "int");
//            esWriter.getIdColumn().add(idColumn);
//            return esWriter;
//        }
//        return writer;
//    }
//
//    private Setting putDefaultEmptyValueForSetting(Setting setting) {
//        DefaultSetting defaultSetting = (DefaultSetting) setting;
//        defaultSetting.setSpeed(1.0);
//        defaultSetting.setRecord(0);
//        defaultSetting.setPercentage(0.0);
//        return defaultSetting;
//    }
//
//    /**
//     * 校验数据源可以使用的场景---读写
//     * 如果数据源没有添加到关系里面,默认为true
//     * FIXME 暂时先把对应关系写在程序里面
//     *
//     * @return
//     */
//    private boolean checkDataSourcePermission(int dataSourceType, int targetType) {
//        Integer permission = dataSourcePermissionMap.get(dataSourceType);
//        if (permission == null) {
//            return true;
//        }
//
//        return (permission & targetType) == targetType;
//
//    }
//
//    public String getParserText(final Map<String, Object> sourceMap,
//                                final Map<String, Object> targetMap,
//                                final Map<String, Object> settingMap) throws Exception {
//
//        JSONObject parser = new JSONObject(4);
//        parser.put("sourceMap", getSourceMap(sourceMap));
//        parser.put("targetMap", getTargetMap(targetMap));
//        parser.put("setting", settingMap);
//
//        JSONObject keymap = new JSONObject(2);
//        keymap.put("source", MapUtils.getObject(sourceMap, "column"));
//        keymap.put("target", MapUtils.getObject(targetMap, "column"));
//        parser.put("keymap", keymap);
//
//        return parser.toJSONString();
//    }
//
//    private Map<String, Object> getSourceMap(Map<String, Object> sourceMap) throws Exception {
//        BatchDataSource source = (BatchDataSource) sourceMap.get("source");
//
//        Map<String, Object> typeMap = new HashMap<>(6);
//        typeMap.put("type", source.getType());
//
//        Object obj = JSON.parse(JSON.toJSONString(MapUtils.getObject(sourceMap, "column")));
//        if (Objects.nonNull(RDBMSSourceType.getByDataSourceType(source.getType())) && DataSourceType.IMPALA.getVal() != source.getType()) {
//            if (DataSourceType.HIVE.getVal() == source.getType() || DataSourceType.HIVE1X.getVal() == source.getType()) {
//                typeMap.put("partition", MapUtils.getString(sourceMap, "partition"));
//            }
//
//            if (DataSourceType.HIVE.getVal() != source.getType() && DataSourceType.HIVE1X.getVal() != source.getType()
//                    && DataSourceType.CarbonData.getVal() != source.getType()) {
//                String table = ((List<String>) sourceMap.get("table")).get(0);
//                JSONArray oriCols = (JSONArray) obj;
//                List<JSONObject> dbCols = this.getTableColumn(source, table, Objects.isNull(sourceMap.get("schema")) ? null : sourceMap.get("schema").toString());
//
//                if (oriCols.get(0) instanceof String) {//老版本存在字符串数组
//                    obj = dbCols;
//                } else {
//                    Set<String> keys = new HashSet<>(oriCols.size());
//                    for (int i = 0; i < oriCols.size(); i++) {
//                        keys.add(oriCols.getJSONObject(i).getString("key"));
//                    }
//
//                    List<JSONObject> newCols = new ArrayList<>();
//                    for (JSONObject dbCol : dbCols) {
//                        JSONObject col = null;
//                        for (Object oriCol : oriCols) {
//                            if (((JSONObject) oriCol).getString("key").equals(dbCol.getString("key"))) {
//                                col = (JSONObject) oriCol;
//                                break;
//                            }
//                        }
//
//                        if (col == null) {
//                            col = dbCol;
//                        }
//
//                        newCols.add(col);
//                    }
//
//                    //加上常量字段信息
//                    for (Object oriCol : oriCols) {
//                        if (((JSONObject) oriCol).getString("type").equalsIgnoreCase("string")) {
//                            //去重
//                            if(!keys.contains(((JSONObject) oriCol).getString("key"))){
//                                newCols.add((JSONObject) oriCol);
//                            }
//                        }
//                    }
//                    obj = newCols;
//
//                }
//
//            }
//
//            typeMap.put("where", MapUtils.getString(sourceMap, "where"));
//            typeMap.put("splitPK", MapUtils.getString(sourceMap, "splitPK"));
//            typeMap.put("table", sourceMap.get("table"));
//        } else if (DataSourceType.HDFS.getVal() == source.getType()) {
//            typeMap.put("path", MapUtils.getString(sourceMap, "path"));
//            typeMap.put("fieldDelimiter", MapUtils.getString(sourceMap, "fieldDelimiter"));
//            typeMap.put("fileType", MapUtils.getString(sourceMap, "fileType"));
//            typeMap.put("encoding", MapUtils.getString(sourceMap, "encoding"));
//        } else if (DataSourceType.HBASE.getVal() == source.getType()) {
//            typeMap.put("encoding", MapUtils.getString(sourceMap, "encoding"));
//            typeMap.put("table", MapUtils.getString(sourceMap, "table"));
//            typeMap.put("startRowkey", MapUtils.getString(sourceMap, "startRowkey"));
//            typeMap.put("endRowkey", MapUtils.getString(sourceMap, "endRowkey"));
//            typeMap.put("isBinaryRowkey", MapUtils.getString(sourceMap, "isBinaryRowkey"));
//            typeMap.put("scanCacheSize", MapUtils.getString(sourceMap, "scanCacheSize"));
//            typeMap.put("scanBatchSize", MapUtils.getString(sourceMap, "scanBatchSize"));
//        } else if (DataSourceType.FTP.getVal() == source.getType()) {
//            typeMap.put("encoding", MapUtils.getString(sourceMap, "encoding"));
//            typeMap.put("path", sourceMap.get("path"));
//            typeMap.put("fieldDelimiter", MapUtils.getString(sourceMap, "fieldDelimiter"));
//            typeMap.put("isFirstLineHeader", MapUtils.getBooleanValue(sourceMap, "isFirstLineHeader"));
//        } else if (DataSourceType.MAXCOMPUTE.getVal() == source.getType()) {
//            typeMap.put("table", MapUtils.getString(sourceMap, "table"));
//            typeMap.put("partition", MapUtils.getString(sourceMap, "partition"));
//        } else if (DataSourceType.Kudu.getVal() == source.getType()) {
//            Assert.isTrue(StringUtils.isNotEmpty(MapUtils.getString(sourceMap, "table")), "表名不能为空");
//            String table = MapUtils.getString(sourceMap, "table");
//            typeMap.put("table", table);
//            typeMap.put("where", MapUtils.getString(sourceMap, "where"));
//            obj = this.getTableColumn(source, table, null);
//        } else if (DataSourceType.IMPALA.getVal() == source.getType()) {
//            typeMap.put("table", MapUtils.getString(sourceMap, "table"));
//            typeMap.put(TableLocationType.key(), MapUtils.getString(sourceMap, TableLocationType.key()));
//            Optional.ofNullable(MapUtils.getString(sourceMap, "partition")).ifPresent(s -> typeMap.put("partition", s));
//        }
//
//        Map<String, Object> map = new HashMap<>(4);
//        map.put("sourceId", source.getId());
//        map.put("name", source.getDataName());
//        map.put("column", obj);
//        map.put("type", typeMap);
//        map.put(ExtralConfig, sourceMap.getOrDefault(ExtralConfig, ""));
//
//        if (sourceMap.containsKey("increColumn")) {
//            map.put("increColumn", sourceMap.get("increColumn"));
//        }
//
//        if (sourceMap.containsKey("sourceList")) {
//            map.put("sourceList", sourceMap.get("sourceList"));
//        }
//        if (sourceMap.containsKey("schema")) {
//            map.put("schema", sourceMap.get("schema"));
//        }
//        return map;
//    }
//
//    private Map<String, Object> getTargetMap(Map<String, Object> targetMap) throws Exception {
//        BatchDataSource target = (BatchDataSource) targetMap.get("source");
//
//        Map<String, Object> typeMap = new HashMap<>(6);
//        typeMap.put("type", target.getType());
//
//        Object obj = null;
//        if (Objects.nonNull(RDBMSSourceType.getByDataSourceType(target.getType())) && DataSourceType.IMPALA.getVal() != target.getType()) {
//            Assert.isTrue(StringUtils.isNotEmpty(MapUtils.getString(targetMap, "table")), "表名不能为空");
//            if (DataSourceType.HIVE.getVal() == target.getType() || DataSourceType.HIVE1X.getVal() == target.getType()) {
//                obj = MapUtils.getObject(targetMap, "column");
//                typeMap.put("partition", MapUtils.getString(targetMap, "partition"));
//            } else if (DataSourceType.CarbonData.getVal() == target.getType()) {
//                obj = MapUtils.getObject(targetMap, "column");
//            } else {
//                String schema = (targetMap.containsKey("schema") && targetMap.get("schema") != null) ? targetMap.get("schema").toString() : null;
//                String table = ((List<String>) targetMap.get("table")).get(0);
//                obj = this.getTableColumn(target, table, schema);
//            }
//
//            typeMap.put("writeMode", MapUtils.getString(targetMap, "writeMode"));
//            typeMap.put("table", targetMap.get("table"));
//            typeMap.put("preSql", MapUtils.getString(targetMap, "preSql"));
//            typeMap.put("postSql", MapUtils.getString(targetMap, "postSql"));
//        } else if (DataSourceType.HDFS.getVal() == target.getType()) {
//            obj = MapUtils.getObject(targetMap, "column");
//            typeMap.put("path", MapUtils.getString(targetMap, "path"));
//            typeMap.put("fileName", MapUtils.getString(targetMap, "fileName"));
//            typeMap.put("writeMode", MapUtils.getString(targetMap, "writeMode"));
//            typeMap.put("fieldDelimiter", MapUtils.getString(targetMap, "fieldDelimiter"));
//            typeMap.put("encoding", MapUtils.getString(targetMap, "encoding"));
//            typeMap.put("fileType", MapUtils.getString(targetMap, "fileType"));
//        } else if (DataSourceType.HBASE.getVal() == target.getType()) {
//            Assert.isTrue(StringUtils.isNotEmpty(MapUtils.getString(targetMap, "table")), "表名不能为空");
//            obj = MapUtils.getObject(targetMap, "column");
//            typeMap.put("encoding", MapUtils.getString(targetMap, "encoding"));
//            typeMap.put("table", MapUtils.getString(targetMap, "table"));
//            typeMap.put("nullMode", MapUtils.getString(targetMap, "nullMode"));
//            typeMap.put("writeBufferSize", MapUtils.getString(targetMap, "writeBufferSize"));
//            typeMap.put("rowkey", MapUtils.getString(targetMap, "rowkey"));
//        } else if (DataSourceType.FTP.getVal() == target.getType()) {
//            obj = MapUtils.getObject(targetMap, "column");
//            typeMap.put("encoding", MapUtils.getString(targetMap, "encoding"));
//            typeMap.put("path", MapUtils.getString(targetMap, "path"));
//            typeMap.put("writeMode", MapUtils.getString(targetMap, "writeMode"));
//            typeMap.put("fieldDelimiter", MapUtils.getString(targetMap, "fieldDelimiter"));
//        } else if (DataSourceType.MAXCOMPUTE.getVal() == target.getType()) {
//            Assert.isTrue(StringUtils.isNotEmpty(MapUtils.getString(targetMap, "table")), "表名不能为空");
//            obj = MapUtils.getObject(targetMap, "column");
//            typeMap.put("table", MapUtils.getString(targetMap, "table"));
//            typeMap.put("partition", MapUtils.getString(targetMap, "partition"));
//            typeMap.put("writeMode", MapUtils.getString(targetMap, "writeMode"));
//        } else if (DataSourceType.Kudu.getVal() == target.getType()) {
//            Assert.isTrue(StringUtils.isNotEmpty(MapUtils.getString(targetMap, "table")), "表名不能为空");
//            String table = MapUtils.getString(targetMap, "table");
//            typeMap.put("writeMode", MapUtils.getString(targetMap, "writeMode"));
//            typeMap.put("table", table);
//            obj = this.getTableColumn(target, table, null);
//        } else if (DataSourceType.IMPALA.getVal() == target.getType()) {
//            typeMap.put("table", MapUtils.getString(targetMap, "table"));
//            typeMap.put(TableLocationType.key(), MapUtils.getString(targetMap, TableLocationType.key()));
//            Optional.ofNullable(MapUtils.getString(targetMap, "partition")).ifPresent(s -> typeMap.put("partition", s));
//            Optional.ofNullable(MapUtils.getString(targetMap, "writeMode")).ifPresent(s -> typeMap.put("writeMode", s));
//            obj = MapUtils.getObject(targetMap, "column");
//        }
//
//        Map<String, Object> map = new HashMap<>(4);
//        map.put("sourceId", target.getId());
//        map.put("name", target.getDataName());
//        map.put("column", obj);
//        map.put("type", typeMap);
//        map.put(ExtralConfig, targetMap.getOrDefault(ExtralConfig, ""));
//        if (targetMap.containsKey("schema")) {
//            map.put("schema", targetMap.get("schema"));
//        }
//        map.put(BatchDataSourceService.ExtralConfig, targetMap.getOrDefault(BatchDataSourceService.ExtralConfig, ""));
//
//        return map;
//    }
//
//    /**
//     * 设置write属性
//     *
//     * @param map
//     * @param taskId
//     * @param projectId
//     * @param tenantId
//     * @param isFilter 是否过滤账号密码
//     * @throws Exception
//     */
//    public void setWriterJson(Map<String, Object> map, Long taskId, Long projectId, Long tenantId, boolean isFilter) throws Exception {
//        if (map.get("sourceId") == null) {
//            throw new RdosDefineException(ErrorCode.DATA_SOURCE_NOT_SET);
//        }
//
//        Long sourceId = Long.parseLong(map.get("sourceId").toString());
//        BatchDataSource source = batchDataSourceDao.getOne(sourceId);
//        if (source == null) {
//            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_DATA_SOURCE);
//        }
//        Map<String,Object> kerberos = fillKerberosConfig(sourceId);
//        Long dtuicTenantId = tenantService.getDtuicTenantId(tenantId);
//        map.put("sourceIds", Arrays.asList(sourceId));
//        map.put("source", source);
//
//        JSONObject json = JSON.parseObject(Base64Util.baseDecode(source.getDataJson()));
//        map.put("dataSourceType", source.getType());
//        Integer sourceType = source.getType();
//
//        if (Objects.nonNull(RDBMSSourceType.getByDataSourceType(sourceType))
//                && !DataSourceType.HIVE.getVal().equals(sourceType)
//                && !DataSourceType.HIVE1X.getVal().equals(sourceType)
//                && !DataSourceType.IMPALA.getVal().equals(sourceType)
//                && !DataSourceType.CarbonData.getVal().equals(sourceType)) {
//            DataBaseType dataBaseType = DataSourceDataBaseType.getBaseTypeBySourceType(sourceType);
//            map.put("type", dataBaseType);
//            map.put("password", JsonUtil.getStringDefaultEmpty(json, JDBC_PASSWORD));
//            map.put("username", JsonUtil.getStringDefaultEmpty(json, JDBC_USERNAME));
//            map.put("jdbcUrl", JsonUtil.getStringDefaultEmpty(json, JDBC_URL));
//            processTable(map);
//        } else if (DataSourceType.HIVE.getVal().equals(sourceType) || DataSourceType.HIVE1X.getVal().equals(sourceType)) {
//            DataBaseType dataBaseType = DataSourceDataBaseType.getBaseTypeBySourceType(sourceType);
//            map.put("type", dataBaseType);
//            map.put("password", JsonUtil.getStringDefaultEmpty(json, JDBC_PASSWORD));
//            map.put("username", JsonUtil.getStringDefaultEmpty(json, JDBC_USERNAME));
//            map.put("jdbcUrl", JsonUtil.getStringDefaultEmpty(json, JDBC_URL));
//            map.put("partition", map.get(HIVE_PARTITION));
//            map.put("defaultFS", JsonUtil.getStringDefaultEmpty(json, HDFS_DEFAULTFS));
//            this.checkLastHadoopConfig(map,dtuicTenantId,json,source.getIsDefault() == 1);
//            setSftpConfig(sourceId, json, source.getIsDefault(), dtuicTenantId, map, "hadoopConfig",EComponentType.HDFS);
//        } else if (DataSourceType.HDFS.getVal().equals(sourceType)) {
//            map.put("defaultFS", JsonUtil.getStringDefaultEmpty(json, HDFS_DEFAULTFS));
//            this.checkLastHadoopConfig(map,dtuicTenantId,json,source.getIsDefault() == 1);
//            setSftpConfig(sourceId, json, source.getIsDefault(), dtuicTenantId, map, "hadoopConfig",EComponentType.HDFS);
//        } else if (DataSourceType.HBASE.getVal().equals(sourceType)) {
//            String jsonStr = json.getString(HBASE_CONFIG);
//            Map jsonMap = new HashMap();
//            if (StringUtils.isNotEmpty(jsonStr)){
//                jsonMap = objectMapper.readValue(jsonStr,Map.class);
//            }
//            map.put("hbaseConfig", jsonMap);
//            setSftpConfig(sourceId, json, source.getIsDefault(), dtuicTenantId, map, "hbaseConfig",EComponentType.HDFS);
//        } else if (DataSourceType.FTP.getVal().equals(sourceType)) {
//            map.putAll(json);
//        } else if (DataSourceType.MAXCOMPUTE.getVal().equals(sourceType)) {
//            map.put("accessId", json.get("accessId"));
//            map.put("accessKey", json.get("accessKey"));
//            map.put("project", json.get("project"));
//            map.put("endPoint", json.get("endPoint"));
//        } else if ((DataSourceType.ES.getVal().equals(sourceType))) {
//            map.put("address", json.get("address"));
//            map.put("username", JsonUtil.getStringDefaultEmpty(json, "username"));
//            map.put("password", JsonUtil.getStringDefaultEmpty(json, "password"));
//        } else if (DataSourceType.REDIS.getVal().equals(sourceType)) {
//            map.put("type", "string");
//            map.put("hostPort", JsonUtil.getStringDefaultEmpty(json, "hostPort"));
//            map.put("database", json.getIntValue("database"));
//            map.put("password", JsonUtil.getStringDefaultEmpty(json, "password"));
//        } else if (DataSourceType.MONGODB.getVal().equals(sourceType)) {
//            map.put(JDBC_HOSTPORTS, JsonUtil.getStringDefaultEmpty(json, JDBC_HOSTPORTS));
//            map.put("username", JsonUtil.getStringDefaultEmpty(json, "username"));
//            map.put("database", JsonUtil.getStringDefaultEmpty(json, "database"));
//            map.put("password", JsonUtil.getStringDefaultEmpty(json, "password"));
//        } else if (DataSourceType.CarbonData.getVal().equals(sourceType)) {
//            map.put("password", JsonUtil.getStringDefaultEmpty(json, JDBC_PASSWORD));
//            map.put("username", JsonUtil.getStringDefaultEmpty(json, JDBC_USERNAME));
//            map.put("jdbcUrl", JsonUtil.getStringDefaultEmpty(json, JDBC_URL));
//            map.put("partition", map.get(HIVE_PARTITION));
//            String jdbcUrl = JsonUtil.getStringDefaultEmpty(json, JDBC_URL);
//            String table = (String) map.get("table");
//            map.put("path", getPath(DataSourceType.CarbonData, table, jdbcUrl, JsonUtil.getStringDefaultEmpty(json, JDBC_USERNAME), JsonUtil.getStringDefaultEmpty(json, JDBC_PASSWORD), fillKerberosConfig(sourceId)));
//            map.put("database", jdbcUrl.substring(jdbcUrl.lastIndexOf("/") + 1));
//            this.setHadoopConfigToReaderAndWriter(map, json);
//            map.put("column", getCarbonDataColumnMap(map));
//        } else if (DataSourceType.Kudu.getVal().equals(sourceType)) {
//            syncBuilderFactory.getSyncBuilder(DataSourceType.Kudu.getVal()).setWriterJson(map, json,kerberos);
//            setSftpConfig(sourceId, json, source.getIsDefault(), dtuicTenantId, map, "hadoopConfig", null);
//        } else if (DataSourceType.IMPALA.getVal().equals(sourceType)) {
//            syncBuilderFactory.getSyncBuilder(DataSourceType.IMPALA.getVal()).setWriterJson(map, json,kerberos);
//            setSftpConfig(sourceId, json, source.getIsDefault(), dtuicTenantId, map, "hadoopConfig",EComponentType.IMPALA_SQL);
//        }
//
//        if (isFilter) {
//            map.remove("username");
//            map.remove("password");
//            map.remove("accessKey");
//        }
//
//        if (taskId != null) {
//            //插入资源和任务的关联关系
//            dataSourceTaskRefService.addRef(sourceId, taskId, projectId, tenantId);
//        }
//    }
//
//    /**
//     * defaultFS and hadoopConfig中
//     *
//     * @param map
//     * @param json
//     */
//    private void setHadoopConfigToReaderAndWriter(Map<String, Object> map, JSONObject json) {
//        String hadoopConfig = JsonUtil.getStringDefaultEmpty(json, HADOOP_CONFIG);
//        JSONObject config = new JSONObject();
//        if (StringUtils.isNotBlank(hadoopConfig)) {
//            config = JSON.parseObject(hadoopConfig);
//        }
//        map.put("hadoopConfig", config);
//        map.put("defaultFS", JsonUtil.getStringDefaultEmpty(json, HDFS_DEFAULTFS));
//    }
//
//    /**
//     * meta数据源需要从从console获取最新配置
//     * @param map
//     * @param dtuicTenantId
//     * @param json
//     * @param isDefault
//     */
//    private void checkLastHadoopConfig(Map<String, Object> map, Long dtuicTenantId, JSONObject json,boolean isDefault) {
//        //拿取最新配置
//        if (isDefault){
//            String consoleHadoopConfig = this.getConsoleHadoopConfig(dtuicTenantId);
//            if (StringUtils.isNotBlank(consoleHadoopConfig)) {
//                map.put("hadoopConfig", JSON.parse(consoleHadoopConfig));
//            } else {
//                String hadoopConfig = JsonUtil.getStringDefaultEmpty(json, HADOOP_CONFIG);
//                if (StringUtils.isNotBlank(hadoopConfig)) {
//                    map.put("hadoopConfig", JSON.parse(hadoopConfig));
//                }
//            }
//        }else {
//            String hadoopConfig = JsonUtil.getStringDefaultEmpty(json, HADOOP_CONFIG);
//            if (StringUtils.isNotBlank(hadoopConfig)) {
//                map.put("hadoopConfig", JSON.parse(hadoopConfig));
//            }
//        }
//    }
//
//    private List<Map<String, Object>> getCarbonDataColumnMap(Map<String, Object> map) throws Exception {
//        List<Map<String, Object>> columnList = (List<Map<String, Object>>) map.get("column");
//        //carbonData标准分区需要每个分区字段都有连线
//        BatchDataSource source = (BatchDataSource) map.get("source");
//        String table = (String) map.get("table");
//        Boolean isNativeHive = isNativeHive(source.getId(), table, source.getTenantId());
//        if (!isNativeHive) {
//            List<String> carbonDataPartCols = getCarbonDataPartCols(source, table);
//
//            List<Object> keyList = columnList.stream().map(colMap -> {
//                return colMap.get("key");
//            }).collect(Collectors.toList());
//
//            if (!keyList.containsAll(carbonDataPartCols)) {
//                throw new RdosDefineException("CarbonData数据源的分区字段必须被选中");
//            }
//        }
//
//        for (Map<String, Object> colMap : columnList) {
//            colMap.put("isPart", colMap.getOrDefault("isPart", false));
//        }
//        return columnList;
//    }
//
//    private List<String> getCarbonDataPartCols(BatchDataSource source, String table) throws Exception {
//        List<JSONObject> tableColumn = getTableColumn(source, table, null);
//        List<String> partCols = new ArrayList<>();
//        for (JSONObject json : tableColumn) {
//            Boolean isPart = json.getBoolean("isPart");
//            if (BooleanUtils.isTrue(isPart)) {
//                partCols.add(JsonUtil.getStringDefaultEmpty(json, KEY));
//            }
//        }
//        return partCols;
//    }
//
//    /**
//     * 解析数据源连接信息
//     *
//     * @param map       不允许为空
//     * @param taskId
//     * @param projectId
//     * @param isFilter 是否过滤数据源账号密码信息
//     */
//    public void setReaderJson(Map<String, Object> map, Long taskId, Long projectId, Long tenantId, boolean isFilter) throws Exception {
//        List<Long> sourceIds = new ArrayList<>();
//        if (map == null){
//            throw new RdosDefineException("传入信息有误");
//        }
//
//        if (map != null && !map.containsKey("sourceId")) {
//            throw new RdosDefineException(ErrorCode.DATA_SOURCE_NOT_SET);
//        }
//        Long dataSourceId = MapUtils.getLong(map, "sourceId", 0L);
//        BatchDataSource source = batchDataSourceDao.getOne(dataSourceId);
//        if (source == null) {
//            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_DATA_SOURCE);
//        }
//        Integer sourceType = source.getType();
//        map.put("type",sourceType);
//        Long dtuicTenantId = tenantService.getDtuicTenantId(tenantId);
//        // 包含 sourceList 为分库分表读取,兼容原来的单表读取逻辑
//        if ((DataSourceType.MySQL.getVal().equals(sourceType) || DataSourceType.TiDB.getVal().equals(sourceType)) && map.containsKey("sourceList")) {
//            List<Object> sourceList = (List<Object>) map.get("sourceList");
//            JSONArray connections = new JSONArray();
//            for (Object dataSource : sourceList) {
//                Map<String, Object> sourceMap = (Map<String, Object>) dataSource;
//                Long sourceId = Long.parseLong(sourceMap.get("sourceId").toString());
//                BatchDataSource batchDataSource = batchDataSourceDao.getOne(sourceId);
//                if (batchDataSource == null) {
//                    throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_DATA_SOURCE);
//                }
//
//                JSONObject json = JSON.parseObject(Base64Util.baseDecode(batchDataSource.getDataJson()));
//                JSONObject conn = new JSONObject();
//                if (!isFilter) {
//                    conn.put("username", JsonUtil.getStringDefaultEmpty(json, JDBC_USERNAME));
//                    conn.put("password", JsonUtil.getStringDefaultEmpty(json, JDBC_PASSWORD));
//                }
//                conn.put("jdbcUrl", Collections.singletonList(JsonUtil.getStringDefaultEmpty(json, JDBC_URL)));
//
//                if (sourceMap.get("tables") instanceof String) {
//                    conn.put("table", Collections.singletonList(sourceMap.get("tables")));
//                } else {
//                    conn.put("table", sourceMap.get("tables"));
//                }
//
//                conn.put("type", batchDataSource.getType());
//                conn.put("sourceId", sourceId);
//
//                connections.add(conn);
//                sourceIds.add(sourceId);
//
//                sourceMap.put("name", batchDataSource.getDataName());
//                if (map.get("source") == null) {
//                    map.put("source", batchDataSource);
//                }
//                if (map.get("datasourceType") == null) {
//                    map.put("dataSourceType", batchDataSource.getType());
//                }
//            }
//
//            Map<String, Object> sourceMap = (Map<String, Object>) sourceList.get(0);
//            DataBaseType dataBaseType = DataSourceDataBaseType.getBaseTypeBySourceType(sourceType);
//            map.put("sourceId", sourceMap.get("sourceId"));
//            map.put("name", sourceMap.get("name"));
//            map.put("type", dataBaseType);
//            map.put("connections", connections);
//            processTable(map);
//        } else {
//            sourceIds.add(dataSourceId);
//            Long sourceId = source.getId();
//            map.put("source", source);
//            map.put("dataSourceType", source.getType());
//            JSONObject json = JSON.parseObject(Base64Util.baseDecode(source.getDataJson()));
//
//            if (Objects.nonNull(RDBMSSourceType.getByDataSourceType(sourceType))
//                    && !DataSourceType.HIVE.getVal().equals(sourceType)
//                    && !DataSourceType.HIVE1X.getVal().equals(sourceType)
//                    && !DataSourceType.IMPALA.getVal().equals(sourceType)
//                    && !DataSourceType.CarbonData.getVal().equals(sourceType)) {
//                DataBaseType dataBaseType = DataSourceDataBaseType.getBaseTypeBySourceType(sourceType);
//                map.put("type", dataBaseType);
//                map.put("password", JsonUtil.getStringDefaultEmpty(json, JDBC_PASSWORD));
//                map.put("username", JsonUtil.getStringDefaultEmpty(json, JDBC_USERNAME));
//                map.put("jdbcUrl", JsonUtil.getStringDefaultEmpty(json, JDBC_URL));
//                processTable(map);
//            } else if (DataSourceType.HIVE.getVal().equals(sourceType) || DataSourceType.HIVE1X.getVal() == sourceType) {
//                DataBaseType dataBaseType = DataSourceDataBaseType.getBaseTypeBySourceType(sourceType);
//                map.put("type", dataBaseType);
//                map.put("password", JsonUtil.getStringDefaultEmpty(json, JDBC_PASSWORD));
//                map.put("username", JsonUtil.getStringDefaultEmpty(json, JDBC_USERNAME));
//                map.put("jdbcUrl", JsonUtil.getStringDefaultEmpty(json, JDBC_URL));
//                map.put("partition", map.get(HIVE_PARTITION));
//                map.put("defaultFS", JsonUtil.getStringDefaultEmpty(json, HDFS_DEFAULTFS));
//                this.checkLastHadoopConfig(map, dtuicTenantId, json,source.getIsDefault() == 1);
//                setSftpConfig(sourceId, json, source.getIsDefault(), dtuicTenantId, map, "hadoopConfig",EComponentType.HDFS);
//            } else if (DataSourceType.HDFS.getVal().equals(sourceType)) {
//                map.put("defaultFS", JsonUtil.getStringDefaultEmpty(json, HDFS_DEFAULTFS));
//                this.checkLastHadoopConfig(map, dtuicTenantId, json,source.getIsDefault() == 1);
//                setSftpConfig(sourceId, json, source.getIsDefault(), dtuicTenantId, map, "hadoopConfig",EComponentType.HDFS);
//            } else if (DataSourceType.HBASE.getVal().equals(sourceType)) {
//                String jsonStr = json.getString(HBASE_CONFIG);
//                Map jsonMap = new HashMap();
//                if (StringUtils.isNotEmpty(jsonStr)){
//                    jsonMap = objectMapper.readValue(jsonStr,Map.class);
//                }
//                map.put("hbaseConfig", jsonMap);
//                setSftpConfig(sourceId, json, source.getIsDefault(), dtuicTenantId, map, "hbaseConfig",null);
//            } else if (DataSourceType.FTP.getVal().equals(sourceType)) {
//                map.putAll(json);
//            } else if (DataSourceType.MAXCOMPUTE.getVal().equals(sourceType)) {
//                map.put("accessId", json.get("accessId"));
//                map.put("accessKey", json.get("accessKey"));
//                map.put("project", json.get("project"));
//                map.put("endPoint", json.get("endPoint"));
//            } else if ((DataSourceType.ES.getVal().equals(sourceType))) {
//                map.put("address", json.get("address"));
//            } else if (DataSourceType.REDIS.getVal().equals(sourceType)) {
//                map.put("type", "string");
//                map.put("hostPort", JsonUtil.getStringDefaultEmpty(json, "hostPort"));
//                map.put("database", json.getIntValue("database"));
//                map.put("password", JsonUtil.getStringDefaultEmpty(json, "password"));
//            } else if (DataSourceType.MONGODB.getVal().equals(sourceType)) {
//                map.put(JDBC_HOSTPORTS, JsonUtil.getStringDefaultEmpty(json, JDBC_HOSTPORTS));
//                map.put("username", JsonUtil.getStringDefaultEmpty(json, "username"));
//                map.put("database", JsonUtil.getStringDefaultEmpty(json, "database"));
//                map.put("password", JsonUtil.getStringDefaultEmpty(json, "password"));
//            } else if (DataSourceType.CarbonData.getVal().equals(sourceType)) {
//                String jdbcUrl = JsonUtil.getStringDefaultEmpty(json, JDBC_URL);
//                String table = (String) map.get("table");
//                map.put("path", getPath(DataSourceType.CarbonData, table, jdbcUrl, JsonUtil.getStringDefaultEmpty(json, JDBC_USERNAME), JsonUtil.getStringDefaultEmpty(json, JDBC_PASSWORD), fillKerberosConfig(sourceId)));
//                map.put("filter", map.get("where"));
//                map.put("database", jdbcUrl.substring(jdbcUrl.lastIndexOf("/") + 1));
//                this.setHadoopConfigToReaderAndWriter(map, json);
//                map.put("column", getCarbonDataColumnMap(map));
//            } else if (DataSourceType.Kudu.getVal().equals(sourceType)) {
//                syncBuilderFactory.getSyncBuilder(DataSourceType.Kudu.getVal()).setReaderJson(map, json,fillKerberosConfig(sourceId));
//                setSftpConfig(sourceId, json, source.getIsDefault(), dtuicTenantId, map, "hadoopConfig", null);
//            } else if (DataSourceType.IMPALA.getVal().equals(sourceType)) {
//                syncBuilderFactory.getSyncBuilder(DataSourceType.IMPALA.getVal()).setReaderJson(map, json,fillKerberosConfig(sourceId));
//                setSftpConfig(sourceId, json, source.getIsDefault(), dtuicTenantId, map, "hadoopConfig",EComponentType.IMPALA_SQL);
//            }
//        }
//
//        // isFilter为true表示过滤数据源信息，移除相关属性
//        if (isFilter) {
//            map.remove("username");
//            map.remove("password");
//            map.remove("accessKey");
//        }
//
//        map.put("sourceIds", sourceIds);
//
//        if (taskId != null) {
//            for (Long sourceId : sourceIds) {
//                //插入资源和任务的关联关系
//                dataSourceTaskRefService.addRef(sourceId, taskId, projectId, tenantId);
//            }
//        }
//    }
//
//    private void setSftpConfig(Long sourceId, JSONObject json, Integer isDefault, Long dtuicTenantId, Map<String, Object> map, String confKey, final EComponentType componentType) {
//        setSftpConfig(sourceId, json, isDefault, dtuicTenantId, map, confKey, true);
//    }
//
//    /**
//     * 添加ftp地址
//     * @param sourceId
//     * @param json
//     * @param isDefault
//     * @param dtuicTenantId
//     * @param map
//     * @param confKey
//     */
//    private void setSftpConfig(Long sourceId, JSONObject json, Integer isDefault, Long dtuicTenantId, Map<String, Object> map, String confKey, boolean downloadKerberos) {
//        JSONObject kerberosConfig = json.getJSONObject(KERBEROS_CONFIG);
//        if (MapUtils.isNotEmpty(kerberosConfig)) {
//            Map<String, String> sftpMap = getSftpMap(dtuicTenantId);
//            Map<String, Object> conf = null;
//            Object confObj = map.get(confKey);
//            if (confObj instanceof String) {
//                conf = JSON.parseObject(confObj.toString());
//            } else if (confObj instanceof Map) {
//                conf = (Map<String, Object>) confObj;
//            }
//            conf = Optional.ofNullable(conf).orElse(new HashMap<>());
//            //flinkx参数
//            conf.putAll(kerberosConfig);
//            conf.put("sftpConf", sftpMap);
//            String remoteDir;
//            String principalFile;
//            if (1 == isDefault) {
//                EComponentType byConfName = EComponentType.getByConfName(confKey.substring(0,confKey.length()-2));
//                remoteDir = consoleSend.getSftpDir(dtuicTenantId, byConfName.getTypeCode());
//                Map<String, Object> hadoop = consoleSend.getHdfs(dtuicTenantId);
//                //这个地方用hdfs的pricipalFile 因为要操作hdfs 不要使用默认数据源的 那个是hive的 不一样
//                Map<String,Object> kerberConfig = (Map<String, Object>) hadoop.getOrDefault("kerberosConfig", new HashMap<>());
//
//                principalFile = kerberConfig.getOrDefault("principalFile", "").toString();
//                if (hadoop.containsKey("principal")) {
//                    conf.put("principal", hadoop.get("principal"));
//                }
//
//            } else {
//                Object remotePath =  conf.get("remotePath");
//                if (Objects.nonNull(remotePath)){
//                    remoteDir = remotePath.toString();
//                }else {
//                    remoteDir = sftpMap.get("path") + SEPARATE + getSourceKey(sourceId);
//                }
//                principalFile = conf.getOrDefault("principalFile", "").toString();
//
//            }
//            if (StringUtils.isNotEmpty(principalFile)){
//                conf.put("principalFile", getFileName(principalFile));
//            }
//            conf.put("remoteDir", remoteDir);
//            map.put(confKey, conf);
//
//            if (downloadKerberos) {
//                //hiveBase中连接数据库需要kerberosConfig
//                Map<String, Object> kerberosConfigReplaced = fillKerberosConfig(sourceId);
//                map.put("kerberosConfig", kerberosConfigReplaced);
//            }
//
//            String krb5Conf = conf.getOrDefault("java.security.krb5.conf", "").toString();
//            if (StringUtils.isNotEmpty(krb5Conf)){
//                conf.put("java.security.krb5.conf", getFileName(krb5Conf));
//            }
//            // 开启kerberos认证需要的参数
//            conf.put(HadoopConfTool.IS_HADOOP_AUTHORIZATION, "true");
//            conf.put(HadoopConfTool.HADOOP_AUTH_TYPE, "kerberos");
//        }
//    }
//
//    private String getFileName(final String path){
//        if (StringUtils.isEmpty(path)){
//            return path;
//        }
//        final String[] split = path.split(File.separator);
//        return split[split.length-1];
//    }
//
//    /**
//     * 获取carbondata数据源中表的hdfs路径
//     *
//     * @param dataSourceType
//     * @param table
//     * @param jdbcUrl
//     * @param userName
//     * @param password
//     * @return
//     */
//    public String getPath(DataSourceType dataSourceType, String table, String jdbcUrl, String userName, String password, Map<String, Object> kerberosConfig) {
//        Connection conn = null;
//        try {
//            ISourceDTO sourceDTO = DataSourceClientUtils.getSourceDTO(dataSourceType, jdbcUrl, userName, password, kerberosConfig);
//            conn = DataSourceClientUtils.getClient(dataSourceType.getVal()).getCon(sourceDTO);
//            List<Map<String, Object>> mapList = DBUtil.executeQuery(conn, "desc extended " + table,false);
//            for (Map<String, Object> map : mapList) {
//                String col_name = (String) map.get("col_name");
//                if (col_name.contains("Path")) {
//                    return ((String) map.get("data_type")).trim();
//                }
//            }
//        } catch (Exception e) {
//            logger.error("", e);
//        } finally {
//            DBUtil.closeDBResources(null, null, conn);
//        }
//        return null;
//    }
//
//    private void processTable(Map<String, Object> map) {
//        Object table = map.get("table");
//        List<String> tables = new ArrayList<>();
//        if (table instanceof String) {
//            tables.add(table.toString());
//        } else {
//            tables.addAll((List<String>) table);
//        }
//
//        map.put("table", tables);
//    }
//
//    /**
//     * @author toutian
//     */
//    private String getJobText(final Reader reader,
//                              final Writer writer,
//                              final Setting setting) {
//
//        return new JobTemplate() {
//            @Override
//            public Reader newReader() {
//                return reader;
//            }
//
//            @Override
//            public Writer newWrite() {
//                return writer;
//            }
//
//            @Override
//            public Setting newSetting() {
//                return setting;
//            }
//        }.toJobJsonString();
//    }
//
//    /**
//     * 数据同步-向导模式-获得项目下所有数据源
//     *
//     * @param projectId 项目id
//     * @return
//     */
//    public List<DataSourceVO> list(Long projectId) {
//
//        List<BatchDataSource> list = batchDataSourceDao.listByProjectId(projectId);
//
//        List<DataSourceVO> vos = new ArrayList<>(list.size());
//        list.forEach(source -> {
//            int count = dataSourceTaskRefService.getSourceRefCount(source.getId());
//            DataSourceVO vo = DataSourceVO.toVO(source, count);
//            parseModifyUser(vo);
//            parseDataJsonForView(vo);
//            vos.add(vo);
//        });
//
//        return vos;
//    }
//
//    /**
//     * 数据同步-获取表的底层存储信息
//     * 目前用于impala 判断底层表是kudu 还是hbase
//     *
//     * @return
//     * @throws SQLException
//     */
//    public JSONObject tableLocation(Long sourceId, String tableName) {
//
//        BatchDataSource source = batchDataSourceDao.getOne(sourceId);
//        if (source == null) {
//            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_DATA_SOURCE);
//        }
//
//        String dataJson = Base64Util.baseDecode(source.getDataJson());
//        JSONObject dataSource = JSON.parseObject(dataJson);
//        JSONObject result = new JSONObject();
//        if (DataSourceType.IMPALA.getVal() != source.getType()) {
//            return result;
//        }
//        //目前此接口仅用于impala
//        Map<String, Object> kerberos = fillKerberosConfig(sourceId);
//        ImpalaSyncBuilder impalaSyncBuildHandler = new ImpalaSyncBuilder();
//        return impalaSyncBuildHandler.tableLocation(dataSource, tableName,kerberos);
//    }
//
//    /**
//     * 数据同步-获得数据库中相关的表信息
//     *
//     * @param projectId 项目id
//     * @param sourceId  数据源id
//     * @param isSys     是否系统用户
//     * @param tenantId 租户id
//     * @param dtuicTenantId uic租户id
//     * @param schema 查询的schema
//     * @param name 模糊查询表名
//     * @param isAll 是否获取所有表
//     * @return
//     * @throws SQLException
//     */
//    public List<String> tablelist(Long projectId, Long sourceId, boolean isSys, Long tenantId, Long dtuicTenantId, String schema, String name, Boolean isAll) {
//
//        BatchDataSource source = batchDataSourceDao.getOne(sourceId);
//        if (source == null) {
//            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_DATA_SOURCE);
//        }
//
//        String dataJson = Base64Util.baseDecode(source.getDataJson());
//        JSONObject json = JSON.parseObject(dataJson);
//
//        if (Objects.nonNull(RDBMSSourceType.getByDataSourceType(source.getType()))) {  //RDBMS
//            DataSourceType dataSourceType = DataSourceType.getSourceType(source.getType());
//            String dataSource = "";
//            if (DataSourceType.LIBRA.getVal() == source.getType()) {
//                ProjectEngine projectDb = projectEngineService.getProjectDb(source.getProjectId(), MultiEngineType.LIBRA.getType());
//                if (null != projectDb) {
//                    dataSource = projectDb.getEngineIdentity();
//                }
//            }
//            if (DataSourceType.KINGBASE8.getVal() == source.getType() || DataSourceType.PostgreSQL.getVal() == source.getType()) {
//                dataSource = schema;
//            }
//
//            List<String> tables;
//            // oracle和mysql改为后端搜索
//            if (DataSourceType.Oracle.getVal() == source.getType() || DataSourceType.MySQL.getVal() == source.getType()) {
//                // 限制表名返回条数
//                Integer limitNum = BooleanUtils.isNotTrue(isAll) ? environmentContext.getTableLimit() : null;
//                tables = DataSourceClientUtils.tableSearch(dataSourceType,  JsonUtil.getStringDefaultEmpty(json, JDBC_URL),
//                        JsonUtil.getStringDefaultEmpty(json, JDBC_USERNAME), JsonUtil.getStringDefaultEmpty(json, JDBC_PASSWORD), true, schema, name, limitNum, fillKerberosConfig(sourceId));
//            } else {
//                tables = DataSourceClientUtils.getTableList(dataSourceType, JsonUtil.getStringDefaultEmpty(json, JDBC_URL),
//                        JsonUtil.getStringDefaultEmpty(json, JDBC_USERNAME), JsonUtil.getStringDefaultEmpty(json, JDBC_PASSWORD), isSys, true, dataSource, fillKerberosConfig(sourceId));
//            }
//
//            if (source.getType().equals(DataSourceType.HIVE.getVal()) || source.getType().equals(DataSourceType.CarbonData.getVal())
//                    || source.getType().equals(DataSourceType.HIVE1X.getVal())) {
//
//                BatchTableSearchVO searchVO = new BatchTableSearchVO();
//                searchVO.setProjectId(projectId);
//                searchVO.setTenantId(tenantId);
//
//                PageResult<List<BatchTableInfoVO>> listPageResult = null;
//                try {
//                    listPageResult = batchTableInfoService.queryDirtyDataTable(searchVO);
//                } catch (Exception e) {
//                    logger.error("", e);
//                }
//                if (null != listPageResult && CollectionUtils.isNotEmpty(listPageResult.getData())) {
//                    List<String> dirtyName = listPageResult.getData().stream().map(BatchTableInfoVO::getTableName).collect(Collectors.toList());
//                    //过滤脏数据表
//                    tables.removeAll(dirtyName);
//                }
//                // 过滤掉临时表和藏数据表
//                tables.removeIf(tableName -> (tableName.startsWith(TEMP_TABLE_PREFIX)
//                        || tableName.startsWith(TEMP_TABLE_PREFIX_FROM_DQ)));
//
//            }
//            return tables;
//        } else if (source.getType() == DataSourceType.MAXCOMPUTE.getVal()) {
//            Map<String, String> properties;
//            try {
//                properties = objectMapper.readValue(json.toString(), Map.class);
//                return OdpsUtil.tableList(properties);
//            } catch (Exception e) {
//                throw new RdosDefineException("获取表列表失败");
//            }
//        } else if (source.getType() == DataSourceType.HBASE.getVal()) {
//            json.put(KERBEROS_CONFIG, new JSONObject(fillKerberosConfig(sourceId)));
//            return HBaseUtil.getTableList(json);
//        } else if (DataSourceType.Kudu.getVal() == source.getType()) {
//            return KuduDbUtil.getTableList(JsonUtil.getStringDefaultEmpty(json, JDBC_HOSTPORTS), fillKerberosConfig(sourceId));
//        }
//
//        throw new RdosDefineException(ErrorCode.CAN_NOT_FITABLE_SOURCE_TYPE);
//    }
//
//
//    /**
//     * 数据同步-获得获取HBASE表中所有列簇
//     *
//     * @param sourceId  数据源id
//     * @param tableName 表名
//     * @return
//     * @throws SQLException
//     */
//    public List<String> columnfamily(Long sourceId,String tableName) {
//        BatchDataSource source = getOne(sourceId);
//        Map<String, Object> kerberosConfig = fillKerberosConfig(sourceId);
//        String dataJson = Base64Util.baseDecode(source.getDataJson());
//        JSONObject json = JSON.parseObject(dataJson);
//        return HBaseUtil.getColumnFamilyList(json, tableName, kerberosConfig);
//    }
//
//    /**
//     * 数据同步-获得表中字段与类型信息
//     *
//     * @param sourceId  数据源id
//     * @param tableName 表名
//     * @return
//     * @throws SQLException
//     */
//    public List<JSONObject> tablecolumn(Long projectId, Long userId, Long sourceId, String tableName, Boolean isIncludePart, String schema) {
//
//        final BatchDataSource source = this.batchDataSourceDao.getOne(sourceId);
//        final StringBuffer newTableName = new StringBuffer();
//        if (DataSourceType.SQLServer.getVal() == source.getType() && StringUtils.isNotBlank(tableName)){
//            if (tableName.indexOf("[") == -1){
//                final String[] tableNames = tableName.split("\\.");
//                for (final String name : tableNames) {
//                    newTableName.append("[").append(name).append("]").append(".");
//                }
//                tableName = newTableName.substring(0,newTableName.length()-1);
//            }
//        }
//        return getTableColumnIncludePart(source, tableName,isIncludePart, schema);
//    }
//
//
//    public Set<String> getHivePartitions(Long sourceId, String tableName) {
//
//        BatchDataSource source = batchDataSourceDao.getOne(sourceId);
//        String dataJson = Base64Util.baseDecode(source.getDataJson());
//        JSONObject json = JSON.parseObject(dataJson);
//        String jdbcUrl = JsonUtil.getStringDefaultEmpty(json, JDBC_URL);
//        String username = JsonUtil.getStringDefaultEmpty(json, JDBC_USERNAME);
//        String password = JsonUtil.getStringDefaultEmpty(json, JDBC_PASSWORD);
//        Map<String, Object> kerberosConfig = this.fillKerberosConfig(sourceId);
//
//        ISourceDTO sourceDTO = DataSourceClientUtils.getSourceDTO(DataSourceType.getSourceType(source.getType()), jdbcUrl, username, password, kerberosConfig);
//        IClient iClient = ClientCache.getClient(source.getType());
//        List<ColumnMetaDTO> partitionColumn = iClient.getPartitionColumn(sourceDTO, SqlQueryDTO.builder().tableName(tableName).build());
//
//        Set<String> partitionNameSet = Sets.newHashSet();
//        //格式化分区信息 与hive保持一致
//        if (CollectionUtils.isNotEmpty(partitionColumn)){
//            StringJoiner tempJoiner = new StringJoiner("=/","","=");
//            for (ColumnMetaDTO column : partitionColumn) {
//                tempJoiner.add(column.getKey());
//            }
//            partitionNameSet.add(tempJoiner.toString());
//        }
//        return partitionNameSet;
//    }
//
//    /**
//     * 获取可以作为增量标识的字段
//     */
//    public List<JSONObject> getIncreColumn(Long sourceId, Object table, String schema) {
//        List<JSONObject> increColumn = new ArrayList<>();
//
//        String tableName;
//        if (table instanceof String) {
//            tableName = String.valueOf(table);
//        } else if (table instanceof List) {
//            List tableList = (List) table;
//            if (CollectionUtils.isEmpty(tableList)) {
//                return new ArrayList<>();
//            }
//            tableName = String.valueOf(tableList.get(0));
//        } else {
//            throw new RdosDefineException(ErrorCode.INVALID_PARAMETERS);
//        }
//
//        BatchDataSource source = batchDataSourceDao.getOne(sourceId);
//        List<JSONObject> allColumn = getTableColumn(source, tableName, schema);
//        for (JSONObject col : allColumn) {
//            if (ColumnType.isIncreType(col.getString("type"))) {
//                increColumn.add(col);
//            } else if (source.getType() == DataSourceType.Oracle.getVal()) {
//                increColumn.add(col);
//            } else if (source.getType() == DataSourceType.SQLServer.getVal()
//                    && ColumnType.NVARCHAR.equals(ColumnType.fromString(col.getString("key")))) {
//                increColumn.add(col);
//            }
//        }
//
//        return increColumn;
//    }
//
//    /**
//     * 获取表所属字段 不包括分区字段
//     * @param source
//     * @param tableName
//     * @return
//     * @throws Exception
//     */
//    private List<JSONObject> getTableColumn(BatchDataSource source, String tableName, String schema) {
//        try {
//            return this.getTableColumnIncludePart(source,tableName,false, schema);
//        } catch (final Exception e) {
//            throw new RdosDefineException("获取表字段异常", e);
//        }
//
//    }
//
//    /**
//     * 查询表所属字段 可以选择是否需要分区字段
//     * @param source
//     * @param tableName
//     * @param part 是否需要分区字段
//     * @return
//     * @throws Exception
//     */
//    private List<JSONObject> getTableColumnIncludePart(BatchDataSource source, String tableName, Boolean part, String schema)  {
//        try {
//            if (source == null) {
//                throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_DATA_SOURCE);
//            }
//            if (part ==null){
//                part = false;
//            }
//
//            Map<String, Object> kerberosConfig = fillKerberosConfig(source.getId());
//            DataSourceType sourceType = DataSourceType.getSourceType(source.getType());
//            if (sourceType == DataSourceType.HDFS) {
//
//                String dataJson = Base64Util.baseDecode(source.getDataJson());
//                JSONObject json = JSON.parseObject(dataJson);
//                JSONObject hadoopConfig = json.getJSONObject(HADOOP_CONFIG);
//                hadoopConfig.putAll(kerberosConfig);
//                String defaultFS = JsonUtil.getStringDefaultEmpty(json, HDFS_DEFAULTFS);
//                return this.convertToJSONList(HdfsOrcUtil.getColumnList(tableName, defaultFS, hadoopConfig.toString(),kerberosConfig));
//            }
//
//            if (sourceType == DataSourceType.MAXCOMPUTE) {
//                String dataJson = Base64Util.baseDecode(source.getDataJson());
//                JSONObject json = JSON.parseObject(dataJson);
//                Map<String, String> properties = objectMapper.readValue(json.toString(), Map.class);
//
//                return convertToJSONList(OdpsUtil.getTableColumns(properties, tableName));
//            }
//
//            if (sourceType == DataSourceType.Kudu) {
//                String dataJson = Base64Util.baseDecode(source.getDataJson());
//                JSONObject json = JSON.parseObject(dataJson);
//                String hostPorts = JsonUtil.getStringDefaultEmpty(json, JDBC_HOSTPORTS);
//                return KuduDbUtil.getTableColumns(hostPorts, tableName, kerberosConfig);
//            }
//
//            //RDMS, including HADOOP carbondata
//            String dataJson = Base64Util.baseDecode(source.getDataJson());
//            JSONObject json = JSON.parseObject(dataJson);
//            if (sourceType.equals(DataSourceType.CarbonData)) {
//                return DataSourceClientUtils.getColumnMetaData(sourceType, JsonUtil.getStringDefaultEmpty(json, JDBC_URL),
//                        JsonUtil.getStringDefaultEmpty(json, JDBC_USERNAME), JsonUtil.getStringDefaultEmpty(json, JDBC_PASSWORD), tableName, kerberosConfig, schema);
//            }
//            if (sourceType.equals(DataSourceType.HIVE)){
//                return DataSourceClientUtils.getColumnIsIncloudPartition(sourceType, JsonUtil.getStringDefaultEmpty(json, JDBC_URL),
//                        JsonUtil.getStringDefaultEmpty(json, JDBC_USERNAME), JsonUtil.getStringDefaultEmpty(json, JDBC_PASSWORD), tableName, kerberosConfig,part);
//            }
//            return DataSourceClientUtils.getColumnMetaData(sourceType, JsonUtil.getStringDefaultEmpty(json, JDBC_URL),
//                    JsonUtil.getStringDefaultEmpty(json, JDBC_USERNAME), JsonUtil.getStringDefaultEmpty(json, JDBC_PASSWORD), tableName, kerberosConfig, schema);
//        } catch (DtCenterDefException e) {
//            throw e;
//        } catch (Exception e) {
//            throw new RdosDefineException(ErrorCode.GET_COLUMN_ERROR, e);
//        }
//
//    }
//
//    /**
//     * 数据同步-获得预览数据，默认展示3条
//     *
//     * @param projectId 项目id
//     * @param userId    用户id
//     * @param sourceId  数据源id
//     * @param tableName 表名
//     * @return
//     * @author toutian
//     */
//    public JSONObject preview(Long projectId, Long userId, Long sourceId, String tableName, String partition,
//                              Long tenantId, Long dtuicTenantId, Boolean isRoot, String schema) {
//
//        final BatchDataSource source = this.batchDataSourceDao.getOne(sourceId);
//        final StringBuffer newTableName = new StringBuffer();
//        if (DataSourceType.SQLServer.getVal() == source.getType() && StringUtils.isNotBlank(tableName)){
//            if (tableName.indexOf("[") == -1){
//                final String[] tableNames = tableName.split("\\.");
//                for (final String name : tableNames) {
//                    newTableName.append("[").append(name).append("]").append(".");
//                }
//                tableName = newTableName.substring(0,newTableName.length()-1);
//            }
//        }
//
//        if (source == null) {
//            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_DATA_SOURCE);
//        }
//        DataBaseType dataBaseType = DataSourceDataBaseType.getBaseTypeBySourceType(source.getType());
//        DataSourceType sourceType = DataSourceType.getSourceType(source.getType());
//        String dataJson = Base64Util.baseDecode(source.getDataJson());
//        JSONObject json = JSON.parseObject(dataJson);
//
//        List<String> columnList = new ArrayList<String>();
//        List<List<String>> dataList = new ArrayList<List<String>>();
//
//        Statement statement = null;
//        ResultSet rs = null;
//        String querySql = "";
//        if (DataSourceType.MAXCOMPUTE.getVal() == source.getType()) {
//            try {
//                Map properties = objectMapper.readValue(json.toString(), Map.class);
//                List<ColumnMetaDTO> columns = OdpsUtil.getColumns(properties, tableName);
//                if (CollectionUtils.isNotEmpty(columns)) {
//                    for (ColumnMetaDTO columnMetaDTO : columns) {
//                        columnList.add(columnMetaDTO.getKey());
//                    }
//                }
//                dataList = OdpsUtil.getTablePreview(properties, tableName);
//            } catch (Exception e) {
//                logger.error("{}", e);
//                throw new RdosDefineException(String.format("%s获取预览数据失败", source.getDataName()), e);
//            }
//        } else if (DataSourceType.Kudu.getVal() == source.getType()) {
//            try {
//                List<JSONObject> columnJson = KuduDbUtil.getTableColumns(JsonUtil.getStringDefaultEmpty(json, JDBC_HOSTPORTS), tableName, fillKerberosConfig(sourceId));
//                for (JSONObject column : columnJson) {
//                    columnList.add(column.getString("key"));
//                }
//                List<List<Object>> objects = KuduDbUtil.getTablePreview(JsonUtil.getStringDefaultEmpty(json, JDBC_HOSTPORTS), tableName, fillKerberosConfig(sourceId));
//                for (List<Object> object : objects) {
//                    List<String> data = new ArrayList<>();
//                    for (Object o : object) {
//                        data.add(o.toString());
//                    }
//                    dataList.add(data);
//                }
//            } catch (Exception e) {
//                throw new RdosDefineException(String.format("%s获取预览数据失败", source.getDataName()), e);
//            }
//        } else if (DataSourceType.KINGBASE8.getVal() == source.getType() || DataSourceType.PostgreSQL.getVal() == source.getType() || DataSourceType.Oracle.getVal() == source.getType()) {
//            try {
//                List<List<Object>> objects = DataSourceClientUtils.getTablePreview(sourceType,JsonUtil.getStringDefaultEmpty(json, JDBC_URL),
//                        JsonUtil.getStringDefaultEmpty(json, JDBC_USERNAME), JsonUtil.getStringDefaultEmpty(json, JDBC_PASSWORD), tableName, fillKerberosConfig(sourceId), schema, 3);
//                List<List<String>> columns = objects.stream().map(o -> JSON.parseArray(JSON.toJSONString(o), String.class)).collect(Collectors.toList());
//                columnList.addAll(columns.get(0));
//                if (columns.size() > 1) {
//                    dataList.addAll(columns.subList(1, columns.size()));
//                }
//            } catch (Exception e) {
//                throw new RdosDefineException(String.format("%s获取预览数据失败", source.getDataName()), e);
//            }
//        } else {
//            Map<String, Object> kerberosConfig = fillKerberosConfig(sourceId);
//            ISourceDTO sourceDTO = DataSourceClientUtils.getSourceDTO(sourceType, JsonUtil.getStringDefaultEmpty(json, JDBC_URL),
//                    JsonUtil.getStringDefaultEmpty(json, JDBC_USERNAME), JsonUtil.getStringDefaultEmpty(json, JDBC_PASSWORD), kerberosConfig);
//            Connection conn = DataSourceClientUtils.getClient(sourceType).getCon(sourceDTO);
//            try {
//                statement = conn.createStatement();
//
//                String sql = null;
//                if (dataBaseType == DataBaseType.MySql || dataBaseType == DataBaseType.PostgreSQL ||
//                        dataBaseType == DataBaseType.GBase8a || dataBaseType == DataBaseType.LIBRA || dataBaseType == DataBaseType.Clickhouse
//                        || dataBaseType == DataBaseType.Polardb_For_MySQL || dataBaseType == DataBaseType.Phoenix
//                        || dataBaseType == DataBaseType.Phoenix5 || dataBaseType == DataBaseType.TiDB || dataBaseType == DataBaseType.Greenplum6) {
//                    sql = META_COLUMN_MYSQL_SQL;
//                } else if (dataBaseType == DataBaseType.DB2) {
//                    sql = META_COLUMN_DB2_SQL;
//                } else if (dataBaseType == DataBaseType.SQLServer) {
//                    sql = META_COLUMN_SQLSERVER_SQL;
//                } else if (dataBaseType == DataBaseType.HIVE || dataBaseType == DataBaseType.CarbonData || dataBaseType.equals(DataBaseType.HIVE1X) || dataBaseType.equals(DataBaseType.Impala)) {
//                    if (StringUtils.isNotBlank(partition) && !partition.contains("$")) {
//                        sql = META_COLUMN_HIVE_SQL_WITH_PARTITION;
//                    } else {
//                        sql = META_COLUMN_HIVE_SQL;
//                    }
//                }
//
//                if (StringUtils.isBlank(partition)) {
//                    querySql = String.format(sql, tableName);
//                } else if (!partition.contains("$")) {
//                    String[] parts = partition.split("/");
//                    List<String> partList = new ArrayList<>();
//                    for (String part : parts) {
//                        String[] pair = part.split("=");
//                        partList.add(pair[0] + "='" + pair[1] + "'");
//                    }
//                    querySql = String.format(sql, tableName, StringUtils.join(partList, " and "));
//                }
//                logger.info("querySql:{}", querySql);
//                rs = statement.executeQuery(querySql);
//                ResultSetMetaData rsMetaData = rs.getMetaData();
//                int columnCount = rsMetaData.getColumnCount();
//                for (int i = 0, len = columnCount; i < len; i++) {
//                    columnList.add(rsMetaData.getColumnName(i + 1));
//                }
//
//                while (rs.next()) {
//                    List<String> rowData = new ArrayList<>(columnCount);
//                    for (int i = 1; i <= columnCount; i++) {
//                        String value;
//                        if ((value = rs.getString(i)) == null) {
//                            value = "";
//                        }
//                        rowData.add(value);
//                    }
//                    dataList.add(rowData);
//                }
//
//                boolean needMask = !roleUserService.isAdmin(userId, projectId, isRoot);
//                if (DataSourceType.HIVE.getVal() == source.getType() || DataSourceType.HIVE1X.getVal() == source.getType()) {
//                    List<String> colM = mask(source.getProjectId(), tenantId, tableName, columnList, dataList, ETableType.HIVE.getType(), needMask);
//                    columnList = colM;
//                } else if (DataSourceType.LIBRA.getVal() == source.getType()) {
//                    List<String> colM = mask(source.getProjectId(), tenantId, tableName, columnList, dataList, ETableType.LIBRA.getType(), needMask);
//                    checkPermissionColumn(columnList, dataList, dtuicTenantId, JsonUtil.getStringDefaultEmpty(json, JDBC_URL), userId, tableName, tenantId, ETableType.LIBRA.getType());
//                    columnList = colM;
//                }
//            } catch (SQLException e) {
//                logger.error("datasource preview end with error.", e);
//                throw new RdosDefineException(String.format("%s获取预览数据失败", source.getDataName()), e);
//            } finally {
//                DBUtil.closeDBResources(rs, statement, conn);
//            }
//        }
//
//        JSONObject preview = new JSONObject(2);
//        preview.put("columnList", columnList);
//        preview.put("dataList", dataList);
//
//        return preview;
//    }
//
//    /**
//     * DTSTACK.time_test 格式化为  DTSTACK."time_test"
//     * @param tableName
//     * @return
//     */
//    private String formatOracleTableName(String tableName) {
//        if (StringUtils.isNotBlank(tableName) && tableName.contains(".")) {
//            String[] split = tableName.split("\\.");
//            if (split.length > 1) {
//                return String.format("%s.%s", split[0], DataSourceClientUtils.transferTableName(split[1],DataSourceType.Oracle));
//            }
//        }
//        return DataSourceClientUtils.transferTableName(tableName, DataSourceType.Oracle);
//    }
//
//
//    private List<String> mask(Long projectId, Long tenantId, String tableName, List<String> columnList, List<List<String>> dataList, Integer tableType, boolean needMask) {
//        BatchTableInfo table = batchTableInfoService.getTableInfoByTableName(tableName, tenantId, projectId, tableType);
//        if (table == null) {
//            logger.warn("table [{}] in project [{}] not found.", tableName, projectId);
//            return columnList;
//        }
//        List<Integer> index = Lists.newArrayList();
//        List<String> colM = Lists.newArrayList();
//        Map<String, List<DataMaskRule>> colRules = dataMaskColumnInfoService.getRelatedRulesByTableId(table.getId());
//        if (colRules.isEmpty()) {
//            return columnList;
//        }
//        for (int i = 0; i < columnList.size(); i++) {
//            String col = columnList.get(i).toLowerCase();
//            if (colRules.containsKey(col)) {
//                colM.add(col + DataMaskUtil.SIGN_FOR_COLUMNS_NEED_MASK);
//                index.add(i);
//            } else {
//                colM.add(col);
//            }
//        }
//
//        // 如果不需要脱敏，则直接返回
//        if (!needMask) {
//            return colM;
//        }
//
//        for (List<String> data : dataList) {
//            for (Integer in : index) {
//                String source = data.get(in);
//                List<DataMaskRule> ru = colRules.get(columnList.get(in).toLowerCase());
//                data.set(in, DataMaskUtil.mask(source, dataMaskColumnInfoService.generateMaskRule(ru, source)));
//            }
//        }
//        return colM;
//    }
//
//
//    /**
//     * @param columnList
//     * @param dataList
//     * @param dtuicTenantId
//     * @param jdbcUrl
//     * @param userId
//     * @param tableName
//     * @param tenantId
//     */
//    private void checkPermissionColumn(List<String> columnList, List<List<String>> dataList, Long dtuicTenantId, String jdbcUrl, Long userId, String tableName, Long tenantId, Integer tableType) {
//        try {
//            Long dtUicUserId = null;
//            User user = userService.getUser(userId);
//            if (user != null){
//                dtuicTenantId = user.getDtuicUserId();
//            }
//            JdbcInfo jdbcInfo = Engine2DTOService.getJdbcInfo(dtuicTenantId, dtUicUserId, DataSourceType.Spark.getVal());
//            UrlInfo sysUrlInfo = JdbcUrlUtil.getUrlInfo(jdbcInfo.getJdbcUrl());
//            UrlInfo urlInfo = JdbcUrlUtil.getUrlInfo(jdbcUrl);
//            boolean needCheckPermission = false;
//            needCheckPermission = AddressUtil.checkServiceIsSame(sysUrlInfo.getHost(), sysUrlInfo.getPort(), urlInfo.getHost(), urlInfo.getPort());
//            if (needCheckPermission) {
//                String db = jdbcUrl.substring(jdbcUrl.lastIndexOf("/") + 1);
//                Object permissionColumns = batchTablePermissionService.getPermissionColumns(userId, tableName, tenantId, db, true, tableType);
//
//                List<Integer> colIndex = new ArrayList<>();
//                for (int i = 0; i < columnList.size(); i++) {
//                    if (Boolean.TRUE.equals(permissionColumns)) {
//                        //全部字段权限
//                        colIndex.add(i);
//                    } else if (permissionColumns != null && permissionColumns instanceof List) {
//                        //部分字段权限
//                        List<String> perColLists = (List<String>) permissionColumns;
//                        if (perColLists.size() > 0) {
//                            for (String permissionColumn : perColLists) {
//                                if (columnList.get(i).equalsIgnoreCase(permissionColumn)) {
//                                    colIndex.add(i);
//                                }
//                            }
//                        }
//                    }
//                }
//                for (List<String> data : dataList) {
//                    for (int i = 0; i < data.size(); i++) {
//                        if (!colIndex.contains(i)) {
//                            data.set(i, NO_PERMISSION);
//                        }
//                    }
//                }
//            }
//        } catch (RdosDefineException e) {
//            logger.warn("{}", e);
//        } catch (Exception e) {
//            throw new RdosDefineException("检查字段权限异常", e);
//        }
//    }
//
//    /**
//     * 数据源-得到某一数据源详情
//     *
//     * @author toutian
//     */
//    public DataSourceVO getBySourceId(Long sourceId) {
//
//        BatchDataSource source = batchDataSourceDao.getOne(sourceId);
//        if (source == null) {
//            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_DATA_SOURCE);
//        }
//        int count = dataSourceTaskRefService.getSourceRefCount(source.getId());
//        DataSourceVO vo = DataSourceVO.toVO(source, count);
//
//        BatchTestProduceDataSource sourceSource = batchTestProduceDataSourceDao.getBySourceIdOrLinkSourceId(sourceId);
//        if (sourceSource != null) {
//            Long linkSourceId;
//            if (sourceSource.getTestDataSourceId().equals(sourceId)) {
//                linkSourceId = sourceSource.getProduceDataSourceId();
//            } else {
//                linkSourceId = sourceSource.getTestDataSourceId();
//            }
//            BatchDataSource linkSource = batchDataSourceDao.getOne(linkSourceId);
//
//            vo.setLinkSourceId(linkSource.getId());
//            vo.setLinkSourceName(linkSource.getDataName());
//        }
//
//        parseModifyUser(vo);
//        parseDataJson(vo);
//        return vo;
//    }
//
//    private void parseModifyUser(DataSourceVO vo) {
//        long modifyUserId = vo.getModifyUserId();
//        User modifyUser = userDao.getOne(modifyUserId);
//        vo.setModifyUser(modifyUser);
//    }
//
//    /**
//     * 得到数据源类型
//     *
//     * @author toutian
//     */
//    public List<DataSourceTypeVO> getTypes(Long projectId, Long userId) {
//        return dictService.getDictByType(DictType.DATA_SOURCE.getValue()).stream().map(dict -> {
//            DataSourceType sourceType = DataSourceType.getSourceType(dict.getDictValue());
//            return DataSourceTypeVO.toVO(sourceType);
//        }).sorted(Comparator.comparingInt(DataSourceTypeVO::getOrder)).collect(Collectors.toList());
//    }
//
//    /**
//     * 数据源 - 条件查询
//     *
//     * @author toutian
//     */
//    public PageResult<List<DataSourceVO>> pageQuery(Integer type, String name, Long tenantId, Long projectId, Integer currentPage, Integer pageSize) {
//
//
//        BatchDataSourceDTO batchDataSourceDTO = new BatchDataSourceDTO();
//        batchDataSourceDTO.setTenantId(tenantId);
//        batchDataSourceDTO.setProjectId(projectId);
//
//        if (StringUtils.isNotBlank(name)) {
//            batchDataSourceDTO.setFuzzName(name);
//        }
//
//        if (type != null) {
//            batchDataSourceDTO.setType(type);
//        }
//
//
//        PageQuery<BatchDataSourceDTO> pageQuery = new PageQuery<BatchDataSourceDTO>(currentPage, pageSize, "gmt_modified", Sort.DESC.name());
//        pageQuery.setModel(batchDataSourceDTO);
//
//        List<BatchDataSource> batchDataSources = batchDataSourceDao.generalQuery(pageQuery);
//
//        List<Long> sourceIds = new ArrayList<>();
//        Map<Long, Long> sourceSourceMap = new HashMap<>();
//        batchDataSources.forEach(source -> {
//            BatchTestProduceDataSource sourceSource = batchTestProduceDataSourceDao.getBySourceIdOrLinkSourceId(source.getId());
//            if (sourceSource != null) {
//                sourceSourceMap.put(sourceSource.getTestDataSourceId(), sourceSource.getProduceDataSourceId());
//                sourceSourceMap.put(sourceSource.getProduceDataSourceId(), sourceSource.getTestDataSourceId());
//                sourceIds.add(sourceSource.getTestDataSourceId());
//                sourceIds.add(sourceSource.getProduceDataSourceId());
//            }
//        });
//
//        List<BatchDataSource> sources = batchDataSourceDao.listByIds(sourceIds);
//        Map<Long, BatchDataSource> sourceIdMap = new HashMap<>();
//        sources.forEach(source -> {
//            sourceIdMap.put(source.getId(), source);
//        });
//
//        int count = batchDataSourceDao.generalCount(batchDataSourceDTO);
//        List<DataSourceVO> vos = new ArrayList<>(batchDataSources.size());
//        if (count > 0) {
//            batchDataSources.forEach(source -> {
//                int refCount = dataSourceTaskRefService.getSourceRefCount(source.getId());
//                DataSourceVO vo = DataSourceVO.toVO(source, refCount);
//
//                // 密码脱敏
//                DataFilter.passwordFilter(vo.getDataJson());
//
//                BatchDataSource linkSource = sourceIdMap.get(sourceSourceMap.get(source.getId()));
//                if (linkSource != null) {
//                    vo.setLinkSourceId(linkSource.getId());
//                    vo.setLinkSourceName(linkSource.getDataName());
//                }
//
//                parseDataJsonForView(vo);
//                parseModifyUser(vo);
//                vos.add(vo);
//            });
//        }
//
//        PageResult<List<DataSourceVO>> pageResult = new PageResult<List<DataSourceVO>>(vos, count, pageQuery);
//        return pageResult;
//
//    }
//
//    public List<DataSourceVO> getAnalysisSource(Long tenantId, Long projectId) {
//        PageResult<List<DataSourceVO>> result = pageQuery(DataSourceType.CarbonData.getVal(), null, tenantId, projectId, 1, 200);
//        return result.getData();
//    }
//
//    /**
//     * 数据源-删除数据源
//     *
//     * @param sourceId  数据源id
//     * @param projectId 项目id
//     * @param userId    userId
//     * @return
//     * @author toutian
//     */
//    public Long deleteSource(Long sourceId, Long projectId, Long userId) {
//
//        BatchDataSource source = batchDataSourceDao.getOne(sourceId);
//        if (source == null) {
//            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_DATA_SOURCE);
//        }
//
//        if (1 == source.getIsDefault()) {
//            throw new RdosDefineException(ErrorCode.CAN_NOT_MODIFY_DEFAULT_DATA_SOURCE);
//        }
//
//        //判断资源是否被引用---被引用资源无法被删除
//        int count = dataSourceTaskRefService.getSourceRefCount(sourceId);
//        if (count > 0) {
//            throw new RdosDefineException(ErrorCode.CAN_NOT_MODIFY_ACTIVE_SOURCE);
//        }
//
//        batchDataSourceDao.deleteById(sourceId, Timestamp.valueOf(LocalDateTime.now()), projectId, userId);
//
//        // 删除数据源之间的关联
//        batchTestProduceDataSourceService.deleteSourceSource(source.getTenantId(), sourceId);
//
//        return sourceId;
//    }
//
//    /**
//     * 新增默认数据源
//     *  添加console配置的kerberos配置
//     * @param dataSourceVO
//     * @param userId
//     * @param dtuicTenantId
//     * @return
//     */
//    public DataSourceVO addDefaultSourceWithKerberos(DataSourceVO dataSourceVO,
//                                                     Long userId, Long dtuicTenantId) throws SftpException, IOException {
//        // 先保存一次数据源信息，不允许保存为 Default 数据源，获取数据源 ID
//        BatchDataSource tmpDatasource = dataSourceVO.toEntity();
//        dataSourceVO.setId(insertSource(tmpDatasource, userId).getId());
//
//        JSONObject dataJson = dataSourceVO.getDataJson();
//        String localKerberosConf = getLocalKerberosConf(dataSourceVO.getId());
//        JdbcInfo hive = consoleSend.getSparkThrift(dtuicTenantId);
//        String fakeResourceName = null;
//        String fakeResourcePath = null;
//        if (MapUtils.isNotEmpty(hive.getKerberosConfig())) {
//            fakeResourceName = "";
//            fakeResourcePath = "";
//            setKerberosFile(fakeResourcePath, fakeResourceName, dataJson);
//            Map<String, String> sftpMap = getSftpMap(dtuicTenantId);
//            String clusterFtpDir = consoleSend.getSftpDir(dtuicTenantId, EComponentType.SPARK_THRIFT.getTypeCode());
//            Preconditions.checkNotNull(clusterFtpDir, "clusterFtpDir can not be null");
//            //下载集群统一的kerberos配置
//            String sourceKey = clusterFtpDir.replaceAll(sftpMap.get("path"),"");
//            KerberosConfigVerify.downloadKerberosFromSftp(sourceKey, localKerberosConf, sftpMap, dataJson.getTimestamp(KERBEROS_FILE_TIMESTAMP));
//            addKerberosParams(dataSourceVO, hive);
//        }
//
//        dataSourceVO.setLocalKerberosConf(localKerberosConf);
//        DataSourceVO result = addOrUpdateSource(dataSourceVO, userId, null, dtuicTenantId, true);
//        //上传文件至sftp
//        uploadToSftp(fakeResourcePath, fakeResourceName, localKerberosConf, dataSourceVO, result, dtuicTenantId);
//        return result;
//    }
//
//    private void addKerberosParams(DataSourceVO dataSourceVO, JdbcInfo hive) {
//        //flinkx login kerberos 只需要remoteDir\sftpConf\principalFile和其他hadoopConfig即可
//        JSONObject hiveKerberosConfig = hive.getKerberosConfig();
//        String keytabPath = hiveKerberosConfig.getString(HadoopConfTool.KEYTAB_PATH);
//        Preconditions.checkState(StringUtils.isNotEmpty(keytabPath), "keytab path can not be null");
//        String name = new File(keytabPath).getName();
//        Preconditions.checkState(StringUtils.isNotEmpty(name), "keytab name can not be null");
//
//        hiveKerberosConfig.fluentPut("hive.server2.authentication", "KERBEROS").fluentPut(HadoopConfTool.PRINCIPAL_FILE, name);
//        dataSourceVO.setKerberosConfig(hiveKerberosConfig);
//    }
//
//    @Transactional(rollbackFor = Exception.class)
//    public DataSourceVO addOrUpdateSourceWithKerberos(DataSourceVO dataSourceVO, String tmpPath, String originalFilename, Long userId,
//                                                      Boolean isCopyToProduceProject, Long dtuicTenantId) {
//        Map<String, Object> confMap = new HashMap<>();
//        JSONObject dataJson = JSON.parseObject(dataSourceVO.getDataJsonString());
//        String localKerberosConf = getLocalKerberosConf(dataSourceVO.getId());
//        IKerberos kerberos = ClientCache.getKerberos(dataSourceVO.getType());
//
//        if (StringUtils.isNotEmpty(tmpPath)) {
//            try {
//                confMap.putAll(kerberos.parseKerberosFromUpload(tmpPath, localKerberosConf));
//            } catch (IOException e) {
//                throw new RdosDefineException(String.format("解析 Kerberos Zip 文件异常 %s", e.getMessage()), e);
//            }
//            setKerberosFile(tmpPath, originalFilename, dataJson);
//        } else {
//            downloadKerberosFromSftp(dataSourceVO.getId(), localKerberosConf, dtuicTenantId, dataJson.getTimestamp(KERBEROS_FILE_TIMESTAMP));
//            JSONObject kerberosConfig = getOriginKerberosConfig(dataSourceVO.getId(), dataJson);
//            updateDataSourcePrincipalFilePath(dataSourceVO, kerberosConfig,"RDOS_0");
//            confMap.putAll(kerberosConfig);
//        }
//        // 保存principal参数信息
//        savePrincipal(dataJson, confMap);
//        dataSourceVO.setKerberosConfig(confMap);
//        dataSourceVO.setDataJson(dataJson);
//        dataSourceVO.setLocalKerberosConf(localKerberosConf);
//        DataSourceVO result = addOrUpdateSource(dataSourceVO, userId, isCopyToProduceProject, dtuicTenantId);
//        uploadToSftp(tmpPath, originalFilename, localKerberosConf, dataSourceVO, result, dtuicTenantId);
//
//        return result;
//    }
//
//    /**
//     * 保存前端入参principal信息
//     * @param dataJson
//     * @param confMap
//     */
//    private void savePrincipal(JSONObject dataJson, Map<String, Object> confMap) {
//        String principal = dataJson.getString(com.dtstack.dtcenter.loader.kerberos.HadoopConfTool.PRINCIPAL);
//        // 前端入参 . 使用下划线替代
//        String hbaseMasterPrincipal = dataJson.getString("hbase_master_kerberos_principal");
//        String hbaseRegionPrincipal = dataJson.getString("hbase_regionserver_kerberos_principal");
//        if (StringUtils.isNotBlank(principal)) {
//            logger.info("前端入参数principal:{}", principal);
//            confMap.put(com.dtstack.dtcenter.loader.kerberos.HadoopConfTool.PRINCIPAL, principal);
//        }
//        if (StringUtils.isNotBlank(hbaseMasterPrincipal)) {
//            logger.info("前端入参数hbase.master.kerberos.principal{}", hbaseMasterPrincipal);
//            confMap.put(com.dtstack.dtcenter.loader.kerberos.HadoopConfTool.HBASE_MASTER_PRINCIPAL, hbaseMasterPrincipal);
//        }
//        if (StringUtils.isNotBlank(hbaseRegionPrincipal)) {
//            logger.info("前端入参数hbase.regionserver.kerberos.principal{}", hbaseRegionPrincipal);
//            confMap.put(com.dtstack.dtcenter.loader.kerberos.HadoopConfTool.HBASE_REGION_PRINCIPAL, hbaseRegionPrincipal);
//        }
//    }
//
//
//    /**
//     * 第一次添加数据源更新 principalFile
//     * @param dataSourceVO
//     * @param checkString
//     */
//    private void updateDataSourcePrincipalFilePath(DataSourceVO dataSourceVO, JSONObject kerberosConfig, String checkString) {
//        //第一次添加开启kerberosConfig的数据源 需要将kerberosConfig 的principalFile  (/opt/dtstack/DTApp/Batch/kerberosConf/RDOS_0/zhangsan.keytab")
//        //替换插入mysql的之后的数据源id
//        if (Objects.nonNull(kerberosConfig) && StringUtils.isNotBlank(checkString)) {
//            String filePath = (String) kerberosConfig.get(HadoopConfTool.PRINCIPAL_FILE);
//            String krb5Config = (String) kerberosConfig.get(HadoopConfTool.KEY_JAVA_SECURITY_KRB5_CONF);
//            if (StringUtils.isNotBlank(filePath) && filePath.contains(checkString)) {
//                kerberosConfig.put(HadoopConfTool.PRINCIPAL_FILE, filePath.replace(checkString, "RDOS_" + dataSourceVO.getId()));
//            }
//            if (StringUtils.isNotBlank(krb5Config) && filePath.contains(checkString)) {
//                kerberosConfig.put(HadoopConfTool.KEY_JAVA_SECURITY_KRB5_CONF, filePath.replace(checkString, "RDOS_" + dataSourceVO.getId()));
//            }
//        }
//    }
//
//    /**
//     * 需要从数据库中获取保存过的kerberos配置
//     *
//     * @param sourceId
//     * @param dataJson
//     * @return
//     */
//    private JSONObject getOriginKerberosConfig(Long sourceId, JSONObject dataJson) {
//        BatchDataSource source = getOne(sourceId);
//        JSONObject originDataJson = JSON.parseObject(Base64Util.baseDecode(source.getDataJson()));
//        JSONObject kerberosConfig = originDataJson.getJSONObject(KERBEROS_CONFIG);
//        dataJson.put(OPEN_KERBEROS, originDataJson.get(OPEN_KERBEROS));
//        dataJson.put(KERBEROS_FILE, originDataJson.getJSONObject(KERBEROS_FILE));
//        if (kerberosConfig == null) {
//            throw new RdosDefineException("kerberos配置缺失");
//        }
//        return kerberosConfig;
//    }
//
//    private void uploadToSftp(String tmpPath, String originalFilename, String localKerberosConf, DataSourceVO dataSourceVO, DataSourceVO result, Long dtuicTenantId) {
//        if (tmpPath != null) {
//            //上传配置文件
//            Map<String, String> sftpMap = getSftpMap(dtuicTenantId);
//            File localKerberosConfDir = new File(localKerberosConf);
//            if (dataSourceVO.getId() == 0) {
//                File newConfDir = new File(localKerberosConfDir.getParent() + SEPARATE + getSourceKey(result.getId()));
//                localKerberosConfDir.renameTo(newConfDir);
//                localKerberosConfDir = newConfDir;
//            }
//
//            uploadDirFinal(sftpMap, sftpMap.get("path"), localKerberosConfDir.getPath(), result.getId());
//        }
//    }
//
//    private void setKerberosFile(String tmpPath, String originalFilename, JSONObject dataJson) {
//        Map<String,String> kerberosFile = new HashMap<>();
//        kerberosFile.put("name", originalFilename);
//        kerberosFile.put("modifyTime", Timestamp.valueOf(LocalDateTime.now()).toString());
//        dataJson.put("kerberosFile", kerberosFile);
//        dataJson.put(KERBEROS_FILE_TIMESTAMP, new Timestamp(System.currentTimeMillis()));
//        dataJson.put("openKerberos", true);
//    }
//
//    /**
//     * 数据源-新建/更新 数据源
//     *
//     * @author toutian
//     */
//    @Transactional(rollbackFor = Exception.class)
//    public DataSourceVO addOrUpdateSource(DataSourceVO dataSourceVO, Long userId, Boolean isCopyToProduceProject, Long dtuicTenantId) {
//        return addOrUpdateSource(dataSourceVO, userId, isCopyToProduceProject, dtuicTenantId, false);
//    }
//
//    /**
//     * 新增或者修改数据源信息
//     *
//     * @param dataSourceVO 数据源信息
//     * @param userId 用户 ID
//     * @param isCopyToProduceProject 是否保存到生产项目
//     * @param isSaveDefault 是否校验保存默认数据源
//     * @return
//     */
//    private DataSourceVO addOrUpdateSource(DataSourceVO dataSourceVO, Long userId, Boolean isCopyToProduceProject, Long dtuicTenantId, boolean isSaveDefault) {
//        boolean isConnect;
//        try {
//            Map<String, Object> kerberosConfig = MapUtils.isNotEmpty(dataSourceVO.getKerberosConfig()) ? handleKerberos(dataSourceVO.getType(), dataSourceVO.getKerberosConfig(), dataSourceVO.getLocalKerberosConf()) : null;
//            isConnect = this.checkConnectionWithConf(dataSourceVO, kerberosConfig);
//        } catch (RdosDefineException exception) {
//            throw new RdosDefineException("不能添加连接失败的数据源", ErrorCode.CONF_ERROR);
//        }
//        if (!isConnect) {
//            throw new RdosDefineException("不能添加连接失败的数据源", ErrorCode.CONF_ERROR);
//        }
//
//        if (isCopyToProduceProject == null) {
//            isCopyToProduceProject = false;
//        }
//
//        Project project = projectService.getProjectById(dataSourceVO.getProjectId());
//        if (isCopyToProduceProject) {
//            if (ProjectType.GENERAL.getType().equals(project.getProjectType())) {
//                throw new RdosDefineException("此项目还未进行绑定，无法复制数据源");
//            } else if (ProjectType.PRODUCE.getType().equals(project.getProjectType())) {
//                throw new RdosDefineException("此项目为生产项目，不能复制数据源");
//            }
//        }
//
//        JSONObject json = dataSourceVO.getDataJson();
//        JSONObject originJson = JSON.parseObject(json.toJSONString());
//        JSONObject kerberosJson = JSON.parseObject(json.toJSONString());
//
//        // 字段转换
//        json = colMap(json, dataSourceVO.getKerberosConfig(), dataSourceVO.getType(), dtuicTenantId);
//
//        dataSourceVO.setDataJson(json);
//        dataSourceVO.setModifyUserId(userId);
//        dataSourceVO.setGmtModified(Timestamp.valueOf(LocalDateTime.now()));
//
//        BatchDataSource source = dataSourceVO.toEntity();
//        if (source.getId() > 0) {
//            BatchDataSource sourceDB = batchDataSourceDao.getOne(dataSourceVO.getId());
//            if (sourceDB == null) {
//                throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_DATA_SOURCE);
//            }
//
//            if (!isSaveDefault && source.getIsDefault() != null && EBoolean.TRUE.getValue() == source.getIsDefault()) {
//                throw new RdosDefineException(ErrorCode.CAN_NOT_MODIFY_DEFAULT_DATA_SOURCE);
//            }
//
//            sourceDB = new BatchDataSource();
//            PublicUtil.copyPropertiesIgnoreNull(source, sourceDB);
//            sourceDB.setLinkState(1);
//            batchDataSourceDao.update(sourceDB);
//        } else {
//            insertSource(source, userId);
//            //替换kerberosConfig中 文件的路径
//            replaceKerberosConfigPath(dataSourceVO, dtuicTenantId, kerberosJson, source);
//            // 拷贝数据源到生产项目
//            if (isCopyToProduceProject) {
//                if (ProjectType.TEST.getType().equals(project.getProjectType())) {
//                    BatchDataSource produceDatasource = batchDataSourceDao.getDataSourceByName(dataSourceVO.getDataName(), project.getProduceProjectId());
//                    if (produceDatasource != null) {
//                        dataSourceVO.setDataName(NAME_PREFIX + dataSourceVO.getDataName());
//                    }
//
//                    dataSourceVO.setDataJson(originJson);
//                    dataSourceVO.setProjectId(project.getProduceProjectId());
//                    Project tempProject = projectService.getProjectById(project.getProduceProjectId());
//                    dataSourceVO.setTenantId(tempProject.getTenantId());
//                    DataSourceVO dataSourceCopyVO = addOrUpdateSource(dataSourceVO, userId, false, tempProject.getTenantId());
//                    batchTestProduceDataSourceService.addSourceSource(project.getTenantId(), source.getId(), dataSourceCopyVO.getId());
//                }
//            }
//        }
//        return DataSourceVO.toVO(source, source.getActive());
//    }
//
//    /**
//     * 新增数据源信息
//     *
//     * @param source
//     * @param userId
//     */
//    private BatchDataSource insertSource(BatchDataSource source, Long userId) {
//        // 如果 ID 不为空，则直接返回
//        if (source.getId() > 0) {
//            return source;
//        }
//
//        // 校验名称是否已经被占用
//        BatchDataSource tmpSource = batchDataSourceDao.getDataSourceByName(source.getDataName(), source.getProjectId());
//        if (tmpSource != null) {
//            throw new RdosDefineException(ErrorCode.DATA_SOURCE_NAME_ALREADY_EXISTS);
//        }
//
//        source.setActive(0);
//        source.setLinkState(1);
//        source.setCreateUserId(userId);
//        source.setModifyUserId(userId);
//        source.setGmtCreate(Timestamp.valueOf(LocalDateTime.now()));
//        source.setGmtModified(Timestamp.valueOf(LocalDateTime.now()));
//        source.setIsDefault(Optional.ofNullable(source.getIsDefault()).orElse(0));
//        batchDataSourceDao.insert(source);
//        return source;
//    }
//
//    /**
//     * 替换kerberosConfig中 文件的路径
//     * @param dataSourceVO
//     * @param dtuicTenantId
//     * @param json
//     * @param source
//     */
//    private void replaceKerberosConfigPath(DataSourceVO dataSourceVO, Long dtuicTenantId, JSONObject json, BatchDataSource source) {
//        if (dataSourceVO.getKerberosConfig() != null && !dataSourceVO.getKerberosConfig().isEmpty() ) {
//            Map<String, Object> kerberosConfig = dataSourceVO.getKerberosConfig();
//            String localKerberosConf = getLocalKerberosConf(source.getId());
//            if (kerberosConfig.containsKey(HadoopConfTool.KEY_JAVA_SECURITY_KRB5_CONF)) {
//                String krb5Path = kerberosConfig.get(HadoopConfTool.KEY_JAVA_SECURITY_KRB5_CONF).toString();
//                // 替换相对路径为绝对路径
//                kerberosConfig.put(HadoopConfTool.KEY_JAVA_SECURITY_KRB5_CONF, krb5Path.replace("RDOS_0", "RDOS_" + source.getId()).replace(localKerberosConf + File.separator, ""));
//            }
//            if (kerberosConfig.containsKey(HadoopConfTool.PRINCIPAL_FILE)) {
//                String principal = dataSourceVO.getKerberosConfig().get(HadoopConfTool.PRINCIPAL_FILE).toString();
//                kerberosConfig.put(HadoopConfTool.PRINCIPAL_FILE, principal.replace("RDOS_0", "RDOS_" + source.getId()).replace(localKerberosConf + File.separator, ""));
//            }
//        }
//        BatchDataSource batchDataSource = new BatchDataSource();
//        batchDataSource.setId(source.getId());
//        json = colMap(json, dataSourceVO.getKerberosConfig(), dataSourceVO.getType(), dtuicTenantId);
//        dataSourceVO.setDataJson(json);
//        batchDataSource.setDataJson(Base64Util.baseEncode(dataSourceVO.getDataJson().toJSONString()));
//        batchDataSourceDao.update(batchDataSource);
//    }
//
//    /**
//     * 关联数据源
//     */
//    @Transactional(rollbackFor = Exception.class)
//    public void linkDataSource(Long tenantId, Long projectId, Long sourceId,Long linkSourceId) {
//
//        Project project = projectService.getProjectById(projectId);
//        if (ProjectType.GENERAL.getType().equals(project.getProjectType())) {
//            throw new RdosDefineException("此项目没有绑定任何项目，无法配置数据源映射");
//        }
//
//        BatchDataSource source = batchDataSourceDao.getOne(sourceId);
//        if (source == null) {
//            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_DATA_SOURCE);
//        }
//
//        BatchDataSource linkSource = batchDataSourceDao.getOne(linkSourceId);
//        if (linkSource == null) {
//            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_DATA_SOURCE);
//        }
//
//        if (!source.getType().equals(linkSource.getType())) {
//            throw new RdosDefineException("数据源类型不一致");
//        }
//
//        Long testSourceId;
//        Long produceSourceId;
//        if (ProjectType.TEST.getType().equals(project.getProjectType())) {
//            BatchTestProduceDataSource testProduceDataSource = batchTestProduceDataSourceDao.getByProduceSourceId(linkSourceId);
//            if (testProduceDataSource != null) {
//                if (testProduceDataSource.getTestDataSourceId().longValue() == sourceId) {
//                    return;
//                }
//                throw new RdosDefineException("数据源:" + linkSource.getDataName() + " 已关联了其它数据源");
//            }
//            testSourceId = sourceId;
//            produceSourceId = linkSourceId;
//        } else {
//            BatchTestProduceDataSource testProduceDataSource = batchTestProduceDataSourceDao.getByTestSourceId(linkSourceId);
//            if (testProduceDataSource != null) {
//                if (testProduceDataSource.getProduceDataSourceId().longValue() == sourceId) {
//                    return;
//                }
//                throw new RdosDefineException("数据源:" + linkSource.getDataName() + " 已关联了其它数据源");
//            }
//            testSourceId = linkSourceId;
//            produceSourceId = sourceId;
//        }
//
//        // 先删掉已有的关联
//        batchTestProduceDataSourceDao.deleteByTestSourceId(testSourceId);
//        batchTestProduceDataSourceDao.deleteByProduceSourceId(produceSourceId);
//
//        batchTestProduceDataSourceService.addSourceSource(tenantId, testSourceId, produceSourceId);
//    }
//
//    public Boolean checkConnectionWithKerberos(DataSourceVO source, String tmpPath, String originalFilename, Long dtuicTenantId) {
//        Map<String, Object> confMap = new HashMap<>();
//        JSONObject dataJson = JSON.parseObject(source.getDataJsonString());
//        String localKerberosConf = getLocalKerberosConf(source.getId());
//        IKerberos kerberos = ClientCache.getKerberos(source.getType());
//        if (StringUtils.isNotEmpty(tmpPath)) {
//            try {
//                confMap = new HashMap<>(kerberos.parseKerberosFromUpload(tmpPath, localKerberosConf));
//            } catch (IOException e) {
//                throw new RdosDefineException(String.format("解析 Kerberos Zip 文件异常 %s", e.getMessage()), e);
//            }
//        } else {
//            Timestamp kerberosFileTime = null;
//            BatchDataSource batchDataSource = batchDataSourceDao.getOne(source.getId());
//            if (batchDataSource != null) {
//                kerberosFileTime = JSON.parseObject(Base64Util.baseDecode(batchDataSource.getDataJson())).getTimestamp(KERBEROS_FILE_TIMESTAMP);
//            }
//
//            downloadKerberosFromSftp(source.getId(), localKerberosConf, dtuicTenantId, kerberosFileTime);
//            JSONObject kerberosConfig = getOriginKerberosConfig(source.getId(), dataJson);
//            confMap.putAll(kerberosConfig);
//        }
//        savePrincipal(dataJson, confMap);
//
//        // 前端入参，里面主要是传前端选择principal、hbase master principal、hbase region server principal
//        Map<String, Object> kerberosConfig = handleKerberos(source.getType(), confMap, localKerberosConf);
//        try {
//            source.setDataJson(objectMapper.readValue(source.getDataJsonString(), JSONObject.class));
//        } catch (Exception e) {
//            throw new RdosDefineException("获取Kerberos连接异常", e);
//        }
//        return checkConnectionWithConf(source, kerberosConfig);
//    }
//
//    /**
//     * 从上传的keytab文件中解析出principal账号，在前端进行选择
//     *
//     * @param tmpPath 文件上传路径
//     * @return principal集合
//     */
//    public List<String> listPrincipalWithKeytab(DataSourceVO source, String tmpPath, String originalFilename) {
//        String localKerberosConf = getLocalKerberosConf(source.getId());
//        IKerberos kerberos = ClientCache.getKerberos(source.getType());
//        Map<String, Object> kerberosConfig;
//        try {
//            kerberosConfig = kerberos.parseKerberosFromUpload(tmpPath, localKerberosConf);
//        } catch (IOException e) {
//            throw new RdosDefineException(String.format("解析 Kerberos Zip 文件异常 %s", e.getMessage()), e);
//        }
//        kerberos.prepareKerberosForConnect(kerberosConfig, localKerberosConf);
//        return kerberos.getPrincipals(kerberosConfig);
//    }
//
//    /**
//     * 下载检查kerberos配置
//     *
//     * @param sourceId
//     * @return 返回该数据源的完整kerberos配置
//     */
//    public Map<String, Object> fillKerberosConfig(Long sourceId) {
//        BatchDataSource source = getOne(sourceId);
//        Long dtuicTenantId = tenantService.getDtuicTenantId(source.getTenantId());
//        JSONObject dataJson = JSON.parseObject(Base64Util.baseDecode(source.getDataJson()));
//        JSONObject kerberosConfig = dataJson.getJSONObject(KERBEROS_CONFIG);
//        if (MapUtils.isNotEmpty(kerberosConfig)) {
//            String localKerberosConf = getLocalKerberosConf(sourceId);
//            downloadKerberosFromSftp(sourceId, localKerberosConf, dtuicTenantId, dataJson.getTimestamp(KERBEROS_FILE_TIMESTAMP));
//            return handleKerberos(source.getType(), kerberosConfig, localKerberosConf);
//        }
//        return new HashMap<>();
//    }
//
//
//    /**
//     * 数据源-
//     *
//     * @author toutian
//     */
//    public Boolean checkConnection(DataSourceVO source) {
//        return checkConnectionWithConf(source, null);
//    }
//
//    public Boolean checkConnectionWithConf(DataSourceVO source, Map<String, Object> confMap) {
//        JSONObject json = source.getDataJson();
//        String hadoopConfig = StringUtils.isNotBlank(json.getString(HADOOP_CONFIG)) ? json.getString(HADOOP_CONFIG) : null;
//        if (StringUtils.isNotEmpty(hadoopConfig) &&
//                (DataSourceType.HDFS.getVal().equals(source.getType())
//                        || DataSourceType.HIVE.getVal().equals(source.getType())
//                        || DataSourceType.HIVE1X.getVal().equals(source.getType())
//                        || DataSourceType.IMPALA.getVal().equals(source.getType())
//                        || DataSourceType.CarbonData.getVal().equals(source.getType()))) {
//            HdfsConfigChecker checker = new HdfsConfigChecker(hadoopConfig);
//            //检查高可用配置
//            checker.checkConf();
//        }
//        boolean isLegalUrl = this.checkUrlPattern(source);
//        if (!isLegalUrl) {
//            throw new RdosDefineException("数据库URL错误");
//        }
//        try {
//            if (Objects.nonNull(RDBMSSourceType.getByDataSourceType(source.getType()))) {
//                DataSourceType dataSourceType = DataSourceType.getSourceType(source.getType());
//                Connection conn = null;
//                String jdbcUrl = json.get("jdbcUrl").toString();
//                String username = json.containsKey("username") ? json.getString("username") : "";
//                String password = json.containsKey("password") ? Optional.ofNullable(json.get("password")).orElse("").toString() : "";
//                try {
//                    ISourceDTO sourceDTO = DataSourceClientUtils.getSourceDTO(dataSourceType, jdbcUrl, username, password, confMap);
//                    conn = DataSourceClientUtils.getClient(dataSourceType).getCon(sourceDTO);
//                } finally {
//                    DBUtil.closeDBResources(null, null, conn);
//                }
//                return true;
//            } else if (DataSourceType.HDFS.getVal().equals(source.getType())) {
//                String defaultFS = json.get(HDFS_DEFAULTFS).toString();
//                if (!defaultFS.matches(DEFAULT_FS_REGEX)) {
//                    throw new RdosDefineException(ErrorCode.ERROR_DEFAULT_FS_FORMAT);
//                }
//                if (json.containsKey(HADOOP_CONFIG) && StringUtils.isNotEmpty(json.getString(HADOOP_CONFIG))) {
//                    hadoopConfig = json.get(HADOOP_CONFIG).toString();
//                }
//                return HdfsOperator.checkConnection(PublicUtil.strToMap(hadoopConfig),confMap,defaultFS);
//            } else if (DataSourceType.HBASE.getVal().equals(source.getType())) {
//                //kerberos认证
//                JSONObject hbaseConfig = HBaseUtil.buildConnectionConfig(json);
//                json.put(HBASE_CONFIG, hbaseConfig.toJSONString());
//                return HBaseUtil.checkConnection(json, confMap);
//            } else if (DataSourceType.FTP.getVal().equals(source.getType())) {
//                json = colMap(json, source.getType(), null);
//                Map<String, Object> properties = objectMapper.readValue(json.toString(), Map.class);
//                addSftpRsaDefaultPath(properties);
//                return FtpUtil.checkConnection(properties);
//            }
//            if (DataSourceType.MAXCOMPUTE.getVal().equals(source.getType())) {
//                json = colMap(json, source.getType(), null);
//                Map<String, String> properties = objectMapper.readValue(json.toString(), Map.class);
//                return OdpsUtil.checkConnection(properties);
//            }
//            if (DataSourceType.ES.getVal().equals(source.getType())) {
//                json = colMap(json, source.getType(), null);
//                return EsUtil.checkConnection(JsonUtil.getStringDefaultEmpty(json, "address"),
//                        JsonUtil.getStringDefaultEmpty(json, "username"),JsonUtil.getStringDefaultEmpty(json, "password"));
//            }
//            if (DataSourceType.MONGODB.getVal().equals(source.getType())) {
//                return MongoDbUtil.checkConnection(json);
//            }
//            if (DataSourceType.REDIS.getVal().equals(source.getType())) {
//                return RedisUtil.checkConnection(json);
//            }
//            if (DataSourceType.Kudu.getVal().equals(source.getType())) {
//                return KuduDbUtil.checkConnection(json, confMap);
//            }
//        } catch (Throwable e) {
//            logger.error("{}", e);
//            if (e instanceof DtCenterDefException) {
//                throw (DtCenterDefException) e;
//            } else {
//                throw new RdosDefineException(ErrorCode.TEST_CONN_FAIL);
//            }
//        }
//        return false;
//    }
//
//    private void addSftpRsaDefaultPath(Map<String, Object> properties) {
//        String protocol = (String) properties.get("protocol");
//        String authType = Optional.ofNullable(properties.get(SFTPHandler.KEY_AUTHENTICATION)).orElse("").toString();
//        String rsaPath = Optional.ofNullable(properties.get(SFTPHandler.KEY_RSA)).orElse("").toString();
//        String username = (String) properties.get("username");
//        if ("sftp".equalsIgnoreCase(protocol)) {
//            if (SftpAuthType.RSA.getType().toString().equals(authType) && StringUtils.isBlank(rsaPath) && StringUtils.isNotBlank(username)) {
//                //添加默认sftp私钥路径
//                rsaPath = String.format(SFTPHandler.DEFAULT_RSA_PATH_TEMPLATE, username);
//                properties.put(SFTPHandler.KEY_RSA, rsaPath);
//            }
//        }
//    }
//
//    private Boolean checkUrlPattern(DataSourceVO source) {
//        try {
//            String url = source.getDataJson().getString("jdbcUrl");
//            Pattern pattern = Pattern.compile("^\\d{1,5}/");
//            if (source.getType().intValue() == DataSourceType.MySQL.getVal()) {
//                String[] strs = url.split("//");
//                if (!strs[0].trim().equals("jdbc:mysql:")) {
//                    return false;
//                }
//                strs = strs[1].split(":");
//                return pattern.matcher(strs[1]).find(0);
//            }
//            if (source.getType().intValue() == DataSourceType.DB2.getVal()) {
//                String[] strs = url.split("//");
//                if (!strs[0].trim().equals("jdbc:db2:")) {
//                    return false;
//                }
//                strs = strs[1].split(":");
//                return pattern.matcher(strs[1]).find(0);
//            }
//            if (source.getType().intValue() == DataSourceType.PostgreSQL.getVal()) {
//                String[] strs = url.split("//");
//                if (!strs[0].trim().equals("jdbc:postgresql:")) {
//                    return false;
//                }
//                strs = strs[1].split(":");
//                return pattern.matcher(strs[1]).find(0);
//            }
//            if (source.getType().intValue() == DataSourceType.Oracle.getVal()) {
//                Matcher sidMatcher = ORACLE_SID.matcher(url);
//                Matcher serviceMatcher = ORACLE_SERVICE.matcher(url);
//                Matcher tnsMatcher = TNS_NAME.matcher(url);
//                return sidMatcher.find() || serviceMatcher.find() || tnsMatcher.find();
//            }
//            if (source.getType().intValue() == DataSourceType.SQLServer.getVal()) {
//                String[] strs = url.split("//");
//                if (!strs[0].startsWith("jdbc:") || !strs[0].endsWith(":sqlserver:")) {
//                    return false;
//                }
//                strs = strs[1].split(":");
//                pattern = Pattern.compile("^\\d{1,5};");
//                return pattern.matcher(strs[1]).find(0);
//            }
//            if (source.getType().intValue() == DataSourceType.GBase_8a.getVal()) {
//                return url.startsWith("jdbc:gbase://");
//            }
//            if (source.getType().intValue() == DataSourceType.Clickhouse.getVal()){
//                return url.startsWith("jdbc:clickhouse://");
//            }
//        } catch (Exception e) {
//            if(e instanceof RdosDefineException){
//                throw e;
//            }
//            logger.warn("checkUrlPattern {}", e);
//            return false;
//        }
//        return true;
//    }
//
//    /**
//     * 对外展示的接口 不展示source的密码
//     *
//     * @param source
//     */
//    public JSONObject parseDataJsonForView(DataSourceVO source) {
//        parseDataJson(source);
//        JSONObject dataJson = source.getDataJson();
//        dataJson.remove(JDBC_PASSWORD);
//        dataJson.remove("password");
//        dataJson.remove("pass");
//        if (source.getType().equals(DataSourceType.CarbonData.getVal())) {
//            //默认配置不显示
//            String config = (String) dataJson.get(hdfsCustomConfig);
//            if (CarbonDataConfigType.DEFAULT.getName().equals(config)) {
//                dataJson.remove(HDFS_DEFAULTFS);
//                dataJson.remove(HADOOP_CONFIG);
//            }
//        }
//        return dataJson;
//    }
//
//    /**
//     * @author toutian
//     */
//    private void parseDataJson(DataSourceVO source) {
//        JSONObject json = source.getDataJson();
//        if (Objects.nonNull(RDBMSSourceType.getByDataSourceType(source.getType()))) {
//            json.put("password", JsonUtil.getStringDefaultEmpty(json, JDBC_PASSWORD));
//            json.put("username", JsonUtil.getStringDefaultEmpty(json, JDBC_USERNAME));
//            json.put("jdbcUrl", JsonUtil.getStringDefaultEmpty(json, JDBC_URL));
//            json.remove(JDBC_URL);
//            json.remove(JDBC_PASSWORD);
//            json.remove(JDBC_USERNAME);
//        } else if (DataSourceType.HBASE.getVal() == source.getType()) {
//            JSONObject hbaseConfig = json.getJSONObject(HBASE_CONFIG);
//            if (null != hbaseConfig) {
//                json.put("hbase_quorum", hbaseConfig.getString("hbase.zookeeper.quorum"));
//                hbaseConfig.remove("hbase.zookeeper.quorum");
//                JSONObject hbaseOtherConfig = new JSONObject();
//                for (String key : hbaseConfig.keySet()) {
//                    hbaseOtherConfig.put(key, hbaseConfig.getString(key));
//                }
//                json.put("hbase_other", hbaseOtherConfig);
//            }
//        }
//        source.setDataJson(json);
//    }
//
//    private JSONObject colMap(JSONObject json, Integer type, Long dtuicTenantId) {
//        return colMap(json, null, type, dtuicTenantId);
//    }
//
//    //TODO
//    private JSONObject colMap(JSONObject json, Map<String, Object> kerberosConfig, Integer type, Long dtuicTenantId) {
//        if (Objects.nonNull(RDBMSSourceType.getByDataSourceType(type))) {
//            json.put(JDBC_URL, json.get("jdbcUrl"));
//            json.put(JDBC_PASSWORD, json.get("password"));
//            json.put(JDBC_USERNAME, json.get("username"));
//
//            if (type.equals(DataSourceType.CarbonData.getVal())) {
//                String carbonDataConfig = (String) json.get(hdfsCustomConfig);
//                if (CarbonDataConfigType.DEFAULT.getName().equalsIgnoreCase(carbonDataConfig)) {
//                    json.put("defaultFS", HadoopConf.getDefaultFs(dtuicTenantId));
//                    JSONObject hdpConfig = createHadoopConfigObject(dtuicTenantId);
//                    if (!hdpConfig.isEmpty()) {
//                        json.put("hadoopConfig", hdpConfig.toJSONString());
//                    }
//
//                }
//            }
//            json.remove("jdbcUrl");
//            json.remove("password");
//            json.remove("username");
//        } else if (DataSourceType.FTP.getVal() == type) {
//            addSftpRsaDefaultPath(json);
//        }
//
//        if (kerberosConfig != null) {
//            json.put(KERBEROS_CONFIG, kerberosConfig);
//        }
//
//        return json;
//    }
//
//
//    public BatchDataSource getOne(Long sourceId) {
//        BatchDataSource source = batchDataSourceDao.getOne(sourceId);
//        if (source == null) {
//            throw new RdosDefineException("sourceId=" + sourceId + ":" + ErrorCode.CAN_NOT_FIND_DATA_SOURCE.getDescription());
//        }
//        return source;
//    }
//
//
//    /**
//     * 获取使用该数据源的任务的列表
//     *
//     * @param sourceId
//     * @param pageSize
//     * @param currentPage
//     * @return
//     */
//    public PageResult<List<JSONObject> > getSourceTaskRef(Long sourceId, Integer pageSize, Integer currentPage, String taskName) {
//        Long dataSourceId = ParamsCheck.checkNotNull(sourceId);
//        Integer queryPageSize = pageSize == null ? 10 : pageSize;
//        List<JSONObject> data = new ArrayList<>();
//        Integer count = batchDataSourceTaskRefDao.countBySourceId(dataSourceId, taskName);
//        BatchDataSourceTaskDto queryDto = new BatchDataSourceTaskDto();
//        queryDto.setSourceId(sourceId);
//        queryDto.setTaskName(taskName);
//        PageQuery<BatchDataSourceTaskDto> pageQuery = new PageQuery(queryDto);
//        pageQuery.setPage(currentPage);
//        pageQuery.setPageSize(queryPageSize);
//        List<BatchTask> batchTasks = batchDataSourceTaskRefDao.pageQueryBySourceId(pageQuery);
//        if (CollectionUtils.isNotEmpty(batchTasks)) {
//            for (BatchTask batchTask : batchTasks) {
//                JSONObject item = new JSONObject();
//                item.put("id", batchTask.getId());
//                item.put("name", batchTask.getName());
//                data.add(item);
//            }
//        }
//        return new PageResult(data, count, new PageQuery<>(currentPage, pageSize));
//    }
//
//    public String setJobDataSourceInfo(String jobStr, Long dtUicTenentId, Integer createModel) {
//        JSONObject job = JSONObject.parseObject(jobStr);
//        JSONObject jobContent = job.getJSONObject("job");
//        JSONObject content = jobContent.getJSONArray("content").getJSONObject(0);
//        setPluginDataSourceInfo(content.getJSONObject("reader"), dtUicTenentId, createModel);
//        setPluginDataSourceInfo(content.getJSONObject("writer"), dtUicTenentId, createModel);
//        return job.toJSONString();
//    }
//
//    /**
//     * 获取hadoopconfig最新配置
//     * @param dtUicTenantId
//     * @return
//     */
//    private String getConsoleHadoopConfig(Long dtUicTenantId){
//        if(null == dtUicTenantId){
//            return null;
//        }
//        String enginePluginInfo = consoleSend.getEnginePluginInfo(dtUicTenantId, MultiEngineType.HADOOP.getType());
//        if(StringUtils.isBlank(enginePluginInfo)){
//            return null;
//        }
//        JSONObject jsonObject = JSON.parseObject(enginePluginInfo);
//        return jsonObject.getString(EComponentType.HDFS.getTypeCode() + "");
//    }
//
//    /**
//     * 根据模式 判断是否要覆盖数据源信息
//     * 脚本模式 空缺了再覆盖  向导模式 默认覆盖
//     */
//    private void replaceDataSourceInfoByCreateModel(JSONObject jdbcInfo, String key, Object values, Integer createModel){
//        Boolean isReplace = TaskCreateModelType.TEMPLATE.getType().equals(createModel) && jdbcInfo.containsKey(key);
//        if (isReplace) {
//            return;
//        }
//        jdbcInfo.put(key,values);
//
//    }
//
//    private void setPluginDataSourceInfo(JSONObject plugin, Long dtUicTenentId, Integer createModel) {
//        String pluginName = plugin.getString("name");
//        JSONObject param = plugin.getJSONObject("parameter");
//        if (PluginName.MySQLD_R.equals(pluginName)) {
//            JSONArray connections = param.getJSONArray("connection");
//            for (int i = 0; i < connections.size(); i++) {
//                JSONObject conn = connections.getJSONObject(i);
//                if (!conn.containsKey("sourceId")) {
//                    continue;
//                }
//
//                BatchDataSource source = getOne(conn.getLong("sourceId"));
//                JSONObject json = JSONObject.parseObject(Base64Util.baseDecode(source.getDataJson()));
//                replaceDataSourceInfoByCreateModel(conn,"username",JsonUtil.getStringDefaultEmpty(json, JDBC_USERNAME),createModel);
//                replaceDataSourceInfoByCreateModel(conn,"password",JsonUtil.getStringDefaultEmpty(json, JDBC_PASSWORD),createModel);
//                replaceDataSourceInfoByCreateModel(conn,"jdbcUrl",Arrays.asList(JsonUtil.getStringDefaultEmpty(json, JDBC_URL)),createModel);
//            }
//        } else {
//            if (!param.containsKey("sourceIds")) {
//                return;
//            }
//
//            List<Long> sourceIds = param.getJSONArray("sourceIds").toJavaList(Long.class);
//            if (CollectionUtils.isEmpty(sourceIds)) {
//                return;
//            }
//
//            BatchDataSource source = getOne(sourceIds.get(0));
//
//            JSONObject json = JSON.parseObject(Base64Util.baseDecode(source.getDataJson()));
//            Integer sourceType = source.getType();
//
//            if (Objects.nonNull(RDBMSSourceType.getByDataSourceType(sourceType))
//                    && DataSourceType.HIVE.getVal() != sourceType
//                    && DataSourceType.HIVE1X.getVal() != sourceType
//                    && DataSourceType.IMPALA.getVal() != sourceType) {
//                replaceDataSourceInfoByCreateModel(param,"username",JsonUtil.getStringDefaultEmpty(json, JDBC_USERNAME),createModel);
//                replaceDataSourceInfoByCreateModel(param,"password",JsonUtil.getStringDefaultEmpty(json, JDBC_PASSWORD),createModel);
//                JSONObject conn = param.getJSONArray("connection").getJSONObject(0);
//                if (conn.get("jdbcUrl") instanceof String) {
//                    replaceDataSourceInfoByCreateModel(conn,"jdbcUrl",JsonUtil.getStringDefaultEmpty(json, JDBC_URL),createModel);
//                } else {
//                    replaceDataSourceInfoByCreateModel(conn,"jdbcUrl",Arrays.asList(JsonUtil.getStringDefaultEmpty(json, JDBC_URL)),createModel);
//                }
//            } else if (DataSourceType.HIVE.getVal().equals(sourceType) || DataSourceType.HDFS.getVal().equals(sourceType) ||DataSourceType.HIVE1X.getVal().equals(sourceType)) {
//                if (DataSourceType.HIVE.getVal().equals(sourceType) || DataSourceType.HIVE1X.getVal().equals(sourceType)) {
//                    if (param.containsKey("connection")) {
//                        JSONObject conn = param.getJSONArray("connection").getJSONObject(0);
//                        replaceDataSourceInfoByCreateModel(conn,"jdbcUrl",JsonUtil.getStringDefaultEmpty(json, JDBC_URL),createModel);
//                    }
//                }
//                //非meta数据源从高可用配置中取hadoopConf
//                if (0 == source.getIsDefault()){
//                    replaceDataSourceInfoByCreateModel(param,"defaultFS",JsonUtil.getStringDefaultEmpty(json, HDFS_DEFAULTFS),createModel);
//                    String hadoopConfig = JsonUtil.getStringDefaultEmpty(json, HADOOP_CONFIG);
//                    if (StringUtils.isNotBlank(hadoopConfig)) {
//                        replaceDataSourceInfoByCreateModel(param,"hadoopConfig",JSONObject.parse(hadoopConfig),createModel);
//                    }
//                }else {
//                    //meta数据源从console取配置
//                    //拿取最新配置
//                    String consoleHadoopConfig = this.getConsoleHadoopConfig(dtUicTenentId);
//                    if (StringUtils.isNotBlank(consoleHadoopConfig)) {
//                        //替换新path 页面运行fix
//                        JSONArray connections = param.getJSONArray("connection");
//                        if (DataSourceType.HIVE.getVal().equals(sourceType) && Objects.nonNull(connections)){
//                            JSONObject conn = connections.getJSONObject(0);
//                            String hiveUrl = conn.getString("jdbcUrl");
//                            String hiveTable = conn.getJSONArray("table").get(0).toString();
//                            Map<String, Object> kerberosConfig = fillKerberosConfig(source.getId());
//                            String hiveTablePath = getHiveTablePath(DataSourceType.HIVE.getVal(), hiveTable, hiveUrl, JsonUtil.getStringDefaultEmpty(json, JDBC_USERNAME), JsonUtil.getStringDefaultEmpty(json, JDBC_PASSWORD), kerberosConfig);
//                            if (StringUtils.isNotEmpty(hiveTablePath)){
//                                replaceDataSourceInfoByCreateModel(param,"path", hiveTablePath.trim(), createModel);
//                            }
//                        }
//                        replaceDataSourceInfoByCreateModel(param,"hadoopConfig",JSONObject.parse(consoleHadoopConfig),createModel);
//                        JSONObject hadoopConfJson = JSONObject.parseObject(consoleHadoopConfig);
//                        String defaultFs = JsonUtil.getStringDefaultEmpty(hadoopConfJson, "fs.defaultFS");
//                        //替换defaultFs
//                        replaceDataSourceInfoByCreateModel(param,"defaultFS",defaultFs,createModel);
//                    } else {
//                        String hadoopConfig = JsonUtil.getStringDefaultEmpty(json, HADOOP_CONFIG);
//                        if (StringUtils.isNotBlank(hadoopConfig)) {
//                            replaceDataSourceInfoByCreateModel(param, "hadoopConfig", JSONObject.parse(hadoopConfig), createModel);
//                        }
//                    }
//                }
//                setSftpConfig(source.getId(), json, source.getIsDefault(), dtUicTenentId, param, "hadoopConfig", false);
//            } else if (DataSourceType.HBASE.getVal().equals(sourceType)) {
//                String jsonStr = json.getString(HBASE_CONFIG);
//                Map jsonMap = new HashMap();
//                if (StringUtils.isNotEmpty(jsonStr)){
//                    try {
//                        jsonMap = objectMapper.readValue(jsonStr,Map.class);
//                    } catch (IOException e) {
//                        logger.error("", e);
//                    }
//                }
//                replaceDataSourceInfoByCreateModel(param,"hbaseConfig",jsonMap,createModel);
//                if (TaskCreateModelType.GUIDE.getType().equals(createModel)) {
//                    setSftpConfig(source.getId(), json, source.getIsDefault(), dtUicTenentId, param, "hbaseConfig", EComponentType.HDFS);
//                }
//            } else if (DataSourceType.FTP.getVal().equals(sourceType)) {
//                if (json != null){
//                    json.entrySet().forEach(bean->{
//                        replaceDataSourceInfoByCreateModel(param,bean.getKey(),bean.getValue(),createModel);
//                    });
//                }
//            } else if (DataSourceType.MAXCOMPUTE.getVal() == sourceType) {
//                replaceDataSourceInfoByCreateModel(param,"accessId",json.get("accessId"),createModel);
//                replaceDataSourceInfoByCreateModel(param,"accessKey",json.get("accessKey"),createModel);
//                replaceDataSourceInfoByCreateModel(param,"project",json.get("project"),createModel);
//                replaceDataSourceInfoByCreateModel(param,"endPoint",json.get("endPoint"),createModel);
//            } else if ((DataSourceType.ES.getVal() == sourceType)) {
//                replaceDataSourceInfoByCreateModel(param,"address",json.get("address"),createModel);
//            } else if (DataSourceType.REDIS.getVal() == sourceType) {
//                replaceDataSourceInfoByCreateModel(param,"hostPort",JsonUtil.getStringDefaultEmpty(json, "hostPort"),createModel);
//                replaceDataSourceInfoByCreateModel(param,"database",json.getIntValue("database"),createModel);
//                replaceDataSourceInfoByCreateModel(param,"password",JsonUtil.getStringDefaultEmpty(json, "password"),createModel);
//            } else if (DataSourceType.MONGODB.getVal() == sourceType) {
//                replaceDataSourceInfoByCreateModel(param,JDBC_HOSTPORTS,JsonUtil.getStringDefaultEmpty(json, JDBC_HOSTPORTS),createModel);
//                replaceDataSourceInfoByCreateModel(param,"username",JsonUtil.getStringDefaultEmpty(json, "username"),createModel);
//                replaceDataSourceInfoByCreateModel(param,"database",JsonUtil.getStringDefaultEmpty(json, "database"),createModel);
//                replaceDataSourceInfoByCreateModel(param,"password",JsonUtil.getStringDefaultEmpty(json, "password"),createModel);
//            } else if (DataSourceType.Kudu.getVal() == sourceType) {
//                replaceDataSourceInfoByCreateModel(param,"masterAddresses",JsonUtil.getStringDefaultEmpty(json, JDBC_HOSTPORTS),createModel);
//                replaceDataSourceInfoByCreateModel(param,"others",JsonUtil.getStringDefaultEmpty(json, "others"),createModel);
//            } else if (DataSourceType.IMPALA.getVal() == sourceType) {
//                String tableLocation =  param.getString(TableLocationType.key());
//                replaceDataSourceInfoByCreateModel(param,"dataSourceType",DataSourceType.IMPALA.getVal(),createModel);
//                String hadoopConfig = JsonUtil.getStringDefaultEmpty(json, HADOOP_CONFIG);
//                if (StringUtils.isNotBlank(hadoopConfig)) {
//                    replaceDataSourceInfoByCreateModel(param,"hadoopConfig",JSONObject.parse(hadoopConfig),createModel);
//                }
//                if (TableLocationType.HIVE.getValue().equals(tableLocation)) {
//                    replaceDataSourceInfoByCreateModel(param,"username",JsonUtil.getStringDefaultEmpty(json, JDBC_USERNAME),createModel);
//                    replaceDataSourceInfoByCreateModel(param,"password",JsonUtil.getStringDefaultEmpty(json, JDBC_PASSWORD),createModel);
//                    replaceDataSourceInfoByCreateModel(param,"defaultFS",JsonUtil.getStringDefaultEmpty(json, HDFS_DEFAULTFS),createModel);
//                    if (param.containsKey("connection")) {
//                        JSONObject conn = param.getJSONArray("connection").getJSONObject(0);
//                        replaceDataSourceInfoByCreateModel(conn,"jdbcUrl",JsonUtil.getStringDefaultEmpty(json, JDBC_URL),createModel);
//                    }
//                }
//
//            }
//        }
//    }
//
//    private String getHiveTablePath(int sourceType, String table, String jdbcUrl, String username, String password, Map<String, Object> kerberosConfig) {
//        DataSourceType dataSourceType = DataSourceType.getSourceType(sourceType);
//        ISourceDTO sourceDTO = DataSourceClientUtils.getSourceDTO(dataSourceType, jdbcUrl, username, password, kerberosConfig);
//        com.dtstack.dtcenter.loader.dto.Table tableInfo = DataSourceClientUtils.getClient(sourceType).getTable(sourceDTO, SqlQueryDTO.builder().tableName(table).build());
//        return tableInfo.getPath();
//    }
//
//    /**
//     * 返回切分键需要的列名
//     * <p>
//     * 只支持关系型数据库 mysql\oracle\sqlserver\postgresql  的整型数据类型
//     * 也不支持其他数据库。
//     * 如果指定了不支持的类型，则忽略切分键功能，使用单通道进行同步。
//     *
//     * @param projectId
//     * @param userId
//     * @param sourceId
//     * @param tableName
//     * @return
//     */
//    public Set<JSONObject> columnForSyncopate(Long projectId, Long userId, Long sourceId, String tableName, String schema) {
//
//        BatchDataSource source = batchDataSourceDao.getOne(sourceId);
//        if (source != null) {
//            if (Objects.isNull(RDBMSSourceType.getByDataSourceType(source.getType()))) {
//                logger.error("切分键只支关系型数据库");
//                throw new RdosDefineException("切分键只支持关系型数据库");
//            }
//        }
//        if (StringUtils.isEmpty(tableName)) {
//            return new HashSet<>();
//        }
//        final StringBuffer newTableName = new StringBuffer();
//        if (DataSourceType.SQLServer.getVal() == source.getType() && StringUtils.isNotBlank(tableName)){
//            if (tableName.indexOf("[") == -1){
//                final String[] tableNames = tableName.split("\\.");
//                for (final String name : tableNames) {
//                    newTableName.append("[").append(name).append("]").append(".");
//                }
//                tableName = newTableName.substring(0,newTableName.length()-1);
//            }
//        }
//        final List<JSONObject> tablecolumn = this.getTableColumn(source, tableName, schema);
//        if (CollectionUtils.isNotEmpty(tablecolumn)) {
//            List<String> numbers;
//            if (source.getType().equals(DataSourceType.MySQL.getVal()) || source.getType().equals(DataSourceType.Polardb_For_MySQL.getVal()) || source.getType().equals(DataSourceType.TiDB.getVal())) {
//                numbers = MYSQL_NUMBERS;
//            } else if (source.getType().equals(DataSourceType.Oracle.getVal())) {
//                numbers = ORACLE_NUMBERS;
//            } else if (source.getType().equals(DataSourceType.SQLServer.getVal())) {
//                numbers = SQLSERVER_NUMBERS;
//            } else if (source.getType().equals(DataSourceType.PostgreSQL.getVal())) {
//                numbers = POSTGRESQL_NUMBERS;
//            } else if (source.getType().equals(DataSourceType.DB2.getVal())) {
//                numbers = DB2_NUMBERS;
//            } else if (source.getType().equals(DataSourceType.GBase_8a.getVal())) {
//                numbers = GBASE_NUMBERS;
//            } else if (source.getType().equals(DataSourceType.Clickhouse.getVal())){
//                numbers = CLICKHOUSE_NUMBERS;
//            } else if (source.getType().equals(DataSourceType.DMDB.getVal())){
//                numbers = DMDB_NUMBERS;
//            } else if (source.getType().equals(DataSourceType.GREENPLUM6.getVal())){
//                numbers = GREENPLUM_NUMBERS;
//            } else if (source.getType().equals(DataSourceType.KINGBASE8.getVal())){
//                numbers = KINGBASE_NUMBERS;
//            } else {
//                throw new RdosDefineException("切分键只支持关系型数据库");
//            }
//            Map<JSONObject, String> twinsMap = new LinkedHashMap<>(tablecolumn.size()+1);
//            for (JSONObject twins : tablecolumn) {
//                twinsMap.put(twins, twins.getString(TYPE));
//            }
//
//
//            Iterator<Map.Entry<JSONObject, String>> iterator = twinsMap.entrySet().iterator();
//            while (iterator.hasNext()) {
//                String type = getSimpleType(iterator.next().getValue());
//                if (numbers.contains(type.toUpperCase())) {
//                    continue;
//                }
//                if (source.getType().equals(DataSourceType.Oracle.getVal())) {
//                    if (type.equalsIgnoreCase("number")) {
//                        continue;
//                    }
//
//                    Matcher numberMatcher1 = NUMBER_PATTERN.matcher(type);
//                    Matcher numberMatcher2 = NUMBER_PATTERN2.matcher(type);
//                    if (numberMatcher1.matches()) {
//                        continue;
//                    } else if (numberMatcher2.matches()) {
//                        int floatLength = Integer.parseInt(numberMatcher2.group(2));
//                        if (floatLength <= 0) {
//                            continue;
//                        }
//                    }
//                }
//                iterator.remove();
//            }
//            //为oracle加上默认切分键
//            if (source.getType().equals(DataSourceType.Oracle.getVal())) {
//                JSONObject keySet = new JSONObject();
//                keySet.put("type", "NUMBER(38,0)");
//                keySet.put("key", "ROW_NUMBER()");
//                keySet.put("comment", "");
//                twinsMap.put(keySet, "NUMBER(38,0)");
//            }
//            return twinsMap.keySet();
//        }
//        return Sets.newHashSet();
//    }
//
//    private String getSimpleType(String type) {
//        type = type.toUpperCase();
//        String[] split = type.split(" ");
//        if (split != null && split.length > 1) {
//            //提取例如"INT UNSIGNED"情况下的字段类型
//            type = split[0];
//        }
//        return type;
//    }
//
//    /**
//     * 获取绑定项目下的数据源
//     */
//    public JSONObject getDataSourceInBingProject(Long tenantId, Long projectId, Long dataSourceId) {
//        Project project = projectService.getProjectById(projectId);
//        BatchDataSource currentSource = batchDataSourceDao.getOne(dataSourceId);
//        if (currentSource == null) {
//            throw new RdosDefineException("数据源不存在，ID:" + dataSourceId);
//        }
//
//        JSONObject result = new JSONObject();
//        JSONObject current = new JSONObject();
//        current.put("id", currentSource.getId());
//        current.put("dataName", currentSource.getDataName());
//        current.put("type", currentSource.getType());
//        current.put("info", "jdbcUrl:xxxx");
//        result.put("currentSource", current);
//
//        result.put("linkSource", null);
//        BatchTestProduceDataSource sourceSource = batchTestProduceDataSourceDao.getBySourceIdOrLinkSourceId(dataSourceId);
//        if (sourceSource != null) {
//            Long linkSourceId = sourceSource.getTestDataSourceId().equals(dataSourceId) ? sourceSource.getProduceDataSourceId() : sourceSource.getTestDataSourceId();
//            BatchDataSource linkSource = batchDataSourceDao.getOne(linkSourceId);
//            if (linkSource != null) {
//                JSONObject link = new JSONObject();
//                link.put("id", linkSource.getId());
//                link.put("dataName", linkSource.getDataName());
//                link.put("type", linkSource.getType());
//                result.put("linkSource", link);
//            }
//        }
//        List<Long> inUseDataSources = batchTestProduceDataSourceDao.getHasBeenUseDataSources(projectId);
//        List<BatchDataSource> batchDataSources = batchDataSourceDao.listByProjectId(project.getProduceProjectId());
//        JSONArray linkProjectSources = new JSONArray();
//        for (BatchDataSource batchDataSource : batchDataSources) {
//            if (inUseDataSources.contains(batchDataSource.getId())) {
//                continue;
//            }
//            JSONObject source = new JSONObject();
//            source.put("id", batchDataSource.getId());
//            source.put("dataName", batchDataSource.getDataName());
//            source.put("type", batchDataSource.getType());
//            linkProjectSources.add(source);
//        }
//
//        result.put("linkProjectSources", linkProjectSources);
//        return result;
//    }
//
//
//    private List<JSONObject> convertToJSONList(List<Twins<String, String>> twinsList) {
//        List<JSONObject> list = new ArrayList<>(twinsList.size());
//        if (CollectionUtils.isNotEmpty(twinsList)) {
//            for (Twins twins : twinsList) {
//                list.add(convertToJSON(twins));
//            }
//        }
//        return list;
//    }
//
//    private JSONObject convertToJSON(Twins twins) {
//        JSONObject json = new JSONObject(2);
//        json.put(KEY, twins.getKey());
//        json.put(TYPE, twins.getType());
//        return json;
//    }
//
//    /**
//     * 将用户提供的字段类型转成hive类型
//     *
//     * @param map
//     * @return
//     */
//    public JSONObject convertToHiveColumns(Map<String, String> map) {
//        if (map != null && map.size() > 0) {
//            JSONObject json = new JSONObject(map.size(), true);
//            Set<String> keySet = map.keySet();
//            for (String key : keySet) {
//                json.put(key, TYPE_FORMAT.formatToString(map.get(key)));
//            }
//            return json;
//        }
//        return null;
//    }
//
//    /**
//     * 判断carbondata数据表是否是标准分区表
//     *
//     * @param sourceId
//     * @param tableName
//     * @param tenantId
//     * @return
//     */
//    public Boolean isNativeHive(Long sourceId, String tableName, Long tenantId) {
//        BatchDataSource source = getOne(sourceId);
//        DataSourceType carbonDataSourceType = DataSourceType.CarbonData;
//        if (source.getType().equals(carbonDataSourceType.getVal())) {
//            JSONObject dataJson = JSON.parseObject(Base64Util.baseDecode(source.getDataJson()));
//            List<Map<String, Object>> maps = null;
//            Connection conn = null;
//            try {
//                ISourceDTO sourceDTO = DataSourceClientUtils.getSourceDTO(carbonDataSourceType, JsonUtil.getStringDefaultEmpty(dataJson, JDBC_URL),
//                        JsonUtil.getStringDefaultEmpty(dataJson, JDBC_USERNAME), JsonUtil.getStringDefaultEmpty(dataJson, JDBC_PASSWORD), fillKerberosConfig(sourceId));
//                conn = DataSourceClientUtils.getClient(carbonDataSourceType).getCon(sourceDTO);
//
//                maps = DBUtil.executeQuery(conn, "desc extended " + tableName,false);
//            } finally {
//                DBUtil.closeDBResources(null, null, conn);
//            }
//            String partitionType = null;
//            for (Map<String, Object> map : maps) {
//                String colName = (String) map.get("col_name");
//                if (colName.contains("Partition Type")) {
//                    partitionType = (String) map.get("data_type");
//                }
//            }
//            if (partitionType != null) {
//                return partitionType.contains(CarbonDataPartitionType.NATIVE_HIVE.name());
//            }
//            return false;
//        } else {
//            throw new RdosDefineException("数据源类型不匹配");
//        }
//    }
//
//    public CarbonDataTable getCarbonDataTable(String jdbcUrl, String userName, String password, String tableName) throws Exception {
//        List<Map<String, Object>> maps;
//        Connection conn = null;
//        DataSourceType dataSourceType = DataSourceType.CarbonData;
//        try {
//            ISourceDTO sourceDTO = DataSourceClientUtils.getSourceDTO(dataSourceType, jdbcUrl, userName, password, null);
//            conn = DataSourceClientUtils.getClient(dataSourceType).getCon(sourceDTO);
//
//            maps = DBUtil.executeQuery(conn, String.format(DESC_EXTENDED, tableName),false);
//        } finally {
//            DBUtil.closeDBResources(null, null, conn);
//        }
//
//        CarbonDataTable table = new CarbonDataTable();
//        Iterator<Map<String, Object>> iterator = maps.iterator();
//
//        boolean colTurn = true;
//        boolean partTurn = false;
//        while (iterator.hasNext()) {
//            Map<String, Object> map = iterator.next();
//            String col_name = ((String) map.get(COL_NAME)).trim();
//            String data_type = ((String) map.get(DATA_TYPE)).trim();
//            String comment = ((String) map.get(COMMENT)).trim();
//            if (colTurn) {
//                if (StringUtils.isBlank(col_name)) {
//                    iterator.remove();
//                    continue;
//                }
//                if (col_name.contains("#")) {
//                    iterator.remove();
//                    colTurn = false;
//                    continue;
//                }
//                List<JSONObject> colList = table.getColList() == null ? new ArrayList<>() : table.getColList();
//                JSONObject col = new JSONObject();
//                col.put(COL_NAME, col_name);
//                col.put(DATA_TYPE, data_type);
//                col.put(COMMENT, comment);
//                colList.add(col);
//                table.setColList(colList);
//
//            } else if (col_name.contains("#Partition")) {
//                partTurn = true;
//            } else if (partTurn) {
//                if (col_name.contains("#") || StringUtils.isBlank(col_name)) {
//                    iterator.remove();
//                    continue;
//                } else if (col_name.contains(CarbonDataParameter.PartitionType.getName())) {
//                    table.getClass().getMethod(CarbonDataParameter.PartitionType.getMethod(), String.class).invoke(table, data_type.trim());
//                } else {
//                    List<JSONObject> colList = table.getPartList() == null ? new ArrayList<>() : table.getPartList();
//                    JSONObject col = new JSONObject();
//                    col.put(COL_NAME, col_name);
//                    col.put(DATA_TYPE, data_type);
//                    col.put(COMMENT, comment);
//                    colList.add(col);
//                    table.setPartList(colList);
//                }
//            } else {
//                for (CarbonDataParameter param : CarbonDataParameter.values()) {
//                    if (col_name.contains(param.getName())) {
//                        table.getClass().getMethod(param.getMethod(), String.class).invoke(table, data_type.trim());
//                    }
//                }
//            }
//
//            iterator.remove();
//        }
//        return table;
//    }
//
//    public Table getOrcTableInfoForCarbonData(String jdbcUrl, String userName, String password, String tableName, Map<String, Object> kerberosConfig) {
//        DataSourceType dataSourceType = DataSourceType.CarbonData;
//        ISourceDTO sourceDTO = DataSourceClientUtils.getSourceDTO(dataSourceType, jdbcUrl, userName, password, kerberosConfig);
//        com.dtstack.dtcenter.loader.dto.Table table = DataSourceClientUtils.getClient(dataSourceType).getTable(sourceDTO, SqlQueryDTO.builder().tableName(tableName).build());
//        List<ColumnMetaDTO> columnMetaDTOS = table.getColumns();
//        List<Column> columns = new ArrayList<>();
//        List<Column> part = new ArrayList<>();
//        if (CollectionUtils.isNotEmpty(columnMetaDTOS)) {
//            for (int i = 0; i < columnMetaDTOS.size(); i++) {
//                ColumnMetaDTO bean = columnMetaDTOS.get(i);
//                Column column = new Column();
//                column.setTable(tableName);
//                column.setAlias(bean.getKey());
//                column.setName(bean.getKey());
//                column.setType(bean.getType());
//                column.setIndex(i);
//                columns.add(column);
//                if (bean.getPart()){
//                    part.add(column);
//                }
//            }
//        }
//        Table baseInfo = new Table();
//        BeanUtils.copyProperties(table, baseInfo);
//        baseInfo.setColumns(columns);
//        baseInfo.setPartitions(part);
//        baseInfo.setStoreType(StoredType.ORC.name());
//        return baseInfo;
//    }
//
//    public List<String> getProjectHiveSourceTables(Long projectId, Integer tableType, Long tenantId) {
//        BatchDataSource datasourceHadoop = batchDataSourceDao.getDefaultDataSource(projectId, DataSourceType.HIVE.getVal());
//        BatchDataSource datasourceLibra = batchDataSourceDao.getDefaultDataSource(projectId, DataSourceType.LIBRA.getVal());
//        if (datasourceLibra == null && null == datasourceHadoop) {
//            throw new RdosDefineException("项目关联的数据源不存在!", ErrorCode.DATA_NOT_FIND);
//        }
//        List<String> tables = new ArrayList<>();
//        if (null != datasourceHadoop) {
//            tables.addAll(this.tablelist(null,  datasourceHadoop.getId(), false, tenantId,null, null, null, null));
//        }
//
//        if (null != datasourceLibra) {
//            tables.addAll(this.tablelist(null,  datasourceLibra.getId(), false, tenantId,null, null, null, null));
//        }
//        return tables;
//    }
//
//
//    public List<JSONObject> getProjectDefaultSourceTableColumns(Integer tableType, Long projectId, Long tableId, Long tenantId) {
//        Integer sourceType = SourceTypeEngineTypeMapping.getSourceTypeByEngineType(TableTypeEngineTypeMapping.getEngineTypeByTableType(tableType).getType());
//        BatchDataSource dataSource = getDefaultDataSource(sourceType, projectId);
//        BatchTableInfo table = batchTableInfoService.getOne(tableId, tenantId);
//        return this.getTableColumn(dataSource, table.getTableName(), null);
//    }
//
//    /**
//     * 查询数据源列表
//     * For sdk purpose
//     *
//     * @param type      数据源类型，可以为空
//     * @param name      数据源名称，可以为空，支持模糊查询
//     * @param tenantId
//     * @param projectId
//     * @param limit     条数限制，可以为空（默认100条）
//     * @return
//     */
//    public List<DataSourceVO> queryDataSourceList(Integer type, String name, Long tenantId, Long projectId, Integer limit) {
//        checkParamNullTenantProject(tenantId, projectId);
//
//        BatchDataSourceDTO batchDataSourceDTO = new BatchDataSourceDTO();
//        batchDataSourceDTO.setTenantId(tenantId);
//        batchDataSourceDTO.setProjectId(projectId);
//
//        if (StringUtils.isNotBlank(name)) {
//            batchDataSourceDTO.setFuzzName(name);
//        }
//
//        if (type != null) {
//            batchDataSourceDTO.setType(type);
//        }
//
//
//        PageQuery<BatchDataSourceDTO> pageQuery = new PageQuery<BatchDataSourceDTO>(1, Optional.ofNullable(limit).orElse(100), "gmt_modified", Sort.DESC.name());
//        pageQuery.setModel(batchDataSourceDTO);
//
//        List<BatchDataSource> batchDataSources = batchDataSourceDao.generalQuery(pageQuery);
//
//        List<DataSourceVO> vos = new ArrayList<>(batchDataSources.size());
//        batchDataSources.forEach(source -> {
//
//            DataSourceVO vo = new DataSourceVO();
//            BeanUtils.copyProperties(source, vo);
//            vo.setDataJson(JSON.parseObject(Base64Util.baseDecode(source.getDataJson())));
//            parseDataJsonForView(vo);
//            vos.add(vo);
//        });
//
//        return vos;
//    }
//
//
//    /**
//     * 获取指定数据源密码
//     * For SDK purpose
//     *
//     * @param sourceId
//     * @param tenantId
//     * @param projectId
//     * @return
//     */
//    public String getDataSourcePassword(Long sourceId, Long tenantId, Long projectId) {
//        checkParamNullTenantProject(tenantId, projectId);
//        BatchDataSource source = getOne(sourceId);
//        JSONObject dataJson = JSON.parseObject(Base64Util.baseDecode(source.getDataJson()));
//        String password = JsonUtil.getStringDefaultEmpty(dataJson, JDBC_PASSWORD);
//        return Optional.ofNullable(password).orElse("");
//    }
//
//    private void checkParamNullTenantProject(Long tenantId, Long projectId) {
//        if (tenantId == null) {
//            throw new RdosDefineException("tenantId标识不能为空");
//        }
//        if (projectId == null) {
//            throw new RdosDefineException("projectId标识不能为空");
//        }
//    }
//
//    public void checkPermission() {
//    }
//
//
//    public BatchDataSource getByName(String name, Long projectId) {
//        BatchDataSource datasource = batchDataSourceDao.getDataSourceByName(name, projectId);
//        if (datasource == null) {
//            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_DATA_SOURCE);
//        }
//        return datasource;
//    }
//
//    public BatchDataSource getDefaultDataSource(Integer sourceType, Long projectId) {
//        BatchDataSource datasource = batchDataSourceDao.getDefaultDataSource(projectId, sourceType);
//        if (datasource == null) {
//            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_DATA_SOURCE);
//        }
//        return datasource;
//    }
//
//
//    public BatchDataSource getDefaultDataSourceByEngineType(Integer engineType, Long projectId) {
//        Integer sourceType = SourceTypeEngineTypeMapping.getSourceTypeByEngineType(engineType);
//        BatchDataSource datasource = batchDataSourceDao.getDefaultDataSource(projectId, sourceType);
//        if (datasource == null) {
//            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_DATA_SOURCE);
//        }
//        return datasource;
//    }
//
//    public BatchDataSource getBeanByProjectIdAndDbTypeAndDbName(Long projectId,Integer dataSourceType, String dataSourceName) {
//        BatchDataSource datasource = batchDataSourceDao.getBeanByProjectIdAndDbTypeAndDbName(projectId, dataSourceType, dataSourceName);
//        if (datasource == null) {
//            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_DATA_SOURCE);
//        }
//        return datasource;
//    }
//
//    public Long getDefaultDataSourceByTableType(final Integer tableType, final Long projectId) {
//        final MultiEngineType engineTypeByTableType = TableTypeEngineTypeMapping.getEngineTypeByTableType(tableType);
//        if (null != engineTypeByTableType) {
//            Integer sourceType = SourceTypeEngineTypeMapping.getSourceTypeByEngineType(engineTypeByTableType);
//            BatchDataSource datasource = batchDataSourceDao.getDefaultDataSource(projectId, sourceType);
//            if (datasource != null) {
//                return datasource.getId();
//            }
//        }
//        return -1L;
//    }
//
//    public JSONObject createHadoopConfigObject(Long dtuicTenantId) {
//        JSONObject hadoop = new JSONObject();
//        Map<String,Object> config = HadoopConf.getConfiguration(dtuicTenantId);
//        String nameServices = config.getOrDefault("dfs.nameservices","").toString();
//        if (org.apache.commons.lang3.StringUtils.isNotBlank(nameServices)) {
//            hadoop.put("dfs.nameservices", nameServices);
//            String nameNodes = config.getOrDefault(String.format("dfs.ha.namenodes.%s", nameServices),"").toString();
//            if (org.apache.commons.lang3.StringUtils.isNotBlank(nameNodes)) {
//                hadoop.put(String.format("dfs.ha.namenodes.%s", nameServices), nameNodes);
//                for (String nameNode : nameNodes.split(",")) {
//                    String key = String.format("dfs.namenode.rpc-address.%s.%s", nameServices, nameNode);
//                    hadoop.put(key, config.get(key));
//                }
//            }
//            String failoverKey = String.format("dfs.client.failover.proxy.provider.%s", nameServices);
//            hadoop.put(failoverKey, config.get(failoverKey));
//        }
//
//        return hadoop;
//    }
//
//    private String getLocalKerberosConf(Long sourceId) {
//        String key = getSourceKey(sourceId);
//        return environmentContext.getKerberosLocalPath() + File.separator + key;
//    }
//
//    private String getSourceKey(Long sourceId) {
//        return AppType.RDOS.name() + "_" + Optional.ofNullable(sourceId).orElse(0L);
//    }
//
//    private void downloadKerberosFromSftp(Long sourceId, String localKerberosConf, Long dtuicTenantId, Timestamp kerberosFileTimestamp) {
//        //需要读取配置文件
//        Map<String, String> sftpMap = getSftpMap(dtuicTenantId);
//        try {
//            KerberosConfigVerify.downloadKerberosFromSftp(getSourceKey(sourceId), localKerberosConf, sftpMap, kerberosFileTimestamp);
//        } catch (Exception e) {
//            //允许下载失败
//            logger.info("download kerberosFile failed {}", e);
//        }
//    }
//
//
//    public void uploadDirFinal(Map<String, String> configMap, String dstDir, String srcDir, Long sourceId) {
//        SFTPHandler handler = SFTPHandler.getInstance(configMap);
//        try {
//            KerberosConfigVerify.uploadLockFile(srcDir, dstDir + SEPARATE + getSourceKey(sourceId), handler);
//            handler.uploadDir(dstDir, srcDir);
//        } catch (Exception e) {
//            logger.error("{}", e);
//        } finally {
//            if (handler != null) {
//                handler.close();
//            }
//        }
//    }
//
//    public Map<String, String> getSftpMap(Long dtuicTenantId) {
//        Map<String, String> map = new HashMap<>();
//        String cluster = consoleSend.getCluster(dtuicTenantId);
//        JSONObject clusterObj = JSON.parseObject(cluster);
//        JSONObject sftpConfig = clusterObj.getJSONObject(EComponentType.SFTP.getConfName());
//        if (Objects.isNull(sftpConfig)) {
//            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_SFTP);
//        } else {
//            for (String key : sftpConfig.keySet()) {
//                map.put(key, sftpConfig.getString(key));
//            }
//        }
//        return map;
//    }
//
//    /**
//     * kerberos配置预处理、替换相对路径为绝对路径等操作
//     *
//     * @param sourceType
//     * @param kerberosMap
//     * @param localKerberosConf
//     * @return
//     */
//    private Map<String, Object> handleKerberos (Integer sourceType, Map<String, Object> kerberosMap, String localKerberosConf) {
//        IKerberos kerberos = ClientCache.getKerberos(sourceType);
//        HashMap<String, Object> tmpKerberosConfig = new HashMap<>(kerberosMap);
//        try {
//            kerberos.prepareKerberosForConnect(tmpKerberosConfig, localKerberosConf);
//        } catch (Exception e) {
//            logger.error("common-loader中kerberos配置文件处理失败！", e);
//            throw new RdosDefineException("common-loader中kerberos配置文件处理失败", e);
//        }
//        return tmpKerberosConfig;
//    }
//
//    /**
//     * 根据tableType 获取默认数据源信息
//     * @param projectId
//     * @param tableType
//     * @return
//     */
//    public BatchDataSource getSourceByTableType(Long projectId,Integer tableType){
//
//        DataSourceType sourceType =  TableTypeEngineTypeMapping.getDataSourceTypeByTableType(tableType);
//        if (sourceType == null){
//            return null;
//        }
//
//        BatchDataSource defaultDataSource = batchDataSourceDao.getDefaultDataSource(projectId, sourceType.getVal());
//
//        return defaultDataSource;
//    }
//
//    /**
//     * 检查hive、hdfs、impala数据源高可用配置
//     */
//    public static class HdfsConfigChecker{
//
//        private static final String DFS_NAMESERVICES = "dfs.nameservices";
//        /**
//         * 格式 dfs.ha.namenodes.${nameservice}
//         */
//        private static final String DFS_HA_NAMENODES_PATTERN = "dfs.ha.namenodes.%s";
//        /**
//         * 格式 dfs.namenode.rpc-address.${nameservice}.${namenode}
//         */
//        private static final String DFS_NAMENODE_RPC_ADDRESS_PATTERN = "dfs.namenode.rpc-address.%s.%s";
//        /**
//         * 格式 dfs.client.failover.proxy.provider.${nameservice}
//         */
//        private static final String DFS_CLIENT_FAILOVER_PROXY_PROVIDER_PATTERN = "dfs.client.failover.proxy.provider.%s";
//
//        private String nameServices;
//
//        private List<String> nameNodeList;
//
//        private List<String> nameNodeRpcList;
//
//        private String provider;
//
//        private JSONObject originConf;
//
//        public HdfsConfigChecker(JSONObject hadoopConf){
//            this.originConf = hadoopConf;
//        }
//
//        public HdfsConfigChecker(String hadoopConf){
//            JSONObject jsonObject = JSONObject.parseObject(hadoopConf);
//            this.originConf = jsonObject;
//        }
//        public void checkConf() {
//            String nameservices = originConf.getString(DFS_NAMESERVICES);
//            if (StringUtils.isEmpty(nameservices)){
//                throw new RdosDefineException("配置项"+DFS_NAMESERVICES+"不能为空");
//            }
//            this.nameServices = nameservices;
//            String haNamenodeKey = String.format(DFS_HA_NAMENODES_PATTERN,nameservices);
//            String haNamenodeValues = originConf.getString(haNamenodeKey);
//            if (StringUtils.isEmpty(haNamenodeValues)){
//                throw new RdosDefineException("配置项"+haNamenodeKey+"不能为空");
//            }
//            String[] split = haNamenodeValues.split(",");
//            this.nameNodeList = Lists.newArrayList(split);
//            this.nameNodeRpcList = new ArrayList<>();
//            for (String namenode:this.nameNodeList){
//                String rpcKey = String.format(DFS_NAMENODE_RPC_ADDRESS_PATTERN, nameservices, namenode);
//                String rpcValue = originConf.getString(rpcKey);
//                if (StringUtils.isEmpty(rpcValue)){
//                    throw new RdosDefineException("配置项"+rpcKey+"不能为空");
//                }
//                nameNodeRpcList.add(rpcValue);
//            }
//            String providerKey = String.format(DFS_CLIENT_FAILOVER_PROXY_PROVIDER_PATTERN,nameservices);
//            String providerValue = originConf.getString(providerKey);
//            if (StringUtils.isEmpty(providerValue)){
//                this.provider = providerValue;
//            }
//        }
//
//        public String getNameServices() {
//            return nameServices;
//        }
//
//        public List<String> getNameNodeList() {
//            return nameNodeList;
//        }
//
//        public List<String> getNameNodeRpcList() {
//            return nameNodeRpcList;
//        }
//
//        public String getProvider() {
//            return provider;
//        }
//
//        public JSONObject getOriginConf() {
//            return originConf;
//        }
//    }
//
//    //一次最多加载数量
//    private static final Integer MAX_LOAD = 200;
//
//    public void viewState() throws InterruptedException {
//        for (int start = 0; ; start++) {
//            final List<BatchDataSource> batchDataSources = this.batchDataSourceDao.listAll(start * BatchDataSourceService.MAX_LOAD, BatchDataSourceService.MAX_LOAD);
//            if (CollectionUtils.isEmpty(batchDataSources)) {
//                break;
//            }
//            this.batchViewState(batchDataSources);
//        }
//
//    }
//
//    private void batchViewState(final List<BatchDataSource> batchDataSources) throws InterruptedException {
//        if (CollectionUtils.isNotEmpty(batchDataSources)) {
//            CountDownLatch countDownLatch = new CountDownLatch(batchDataSources.size());
//            for (final BatchDataSource batchDataSource : batchDataSources) {
//                final Future<?> submit = BatchDataSourceService.es.submit(() -> {
//                    this.treatSource(batchDataSource, countDownLatch);
//                });
//                BatchDataSourceService.es.submit(() -> {
//                    try {
//                        submit.get(60, TimeUnit.SECONDS);
//                    } catch (InterruptedException e) {
//                        job_log.error("", e);
//                    } catch (ExecutionException e) {
//                        job_log.error("", e);
//                    } catch (TimeoutException e) {
//                        if (!submit.isDone() && !submit.isCancelled()) {
//                            //结束超长时间等待的checkConn
//                            submit.cancel(true);
//                            job_log.error("结束超长时间等待的checkConn, dataName={}, id={}", batchDataSource.getDataName(), batchDataSource.getId());
//                        }
//                    }
//                });
//            }
//            countDownLatch.await();
//        }
//    }
//
//    private void treatSource(final BatchDataSource batchDataSource, final CountDownLatch countDownLatch) {
//        try {
//            //达到该次数，则不告警
//            int nonAlarm = retryNum + 1;
//            job_log.info(batchDataSource.getDataName());
//
//            DataSourceVO dataSourceVO = new DataSourceVO();
//            JSONObject dataJson = JSON.parseObject(Base64Util.baseDecode(batchDataSource.getDataJson()));
//            dataSourceVO.setDataJson(dataJson);
//            dataSourceVO.setType(batchDataSource.getType());
//            final Map<String, Object> confMap = getKerberosConf(batchDataSource.getId(), batchDataSource.getTenantId(), dataJson);
//            parseDataJson(dataSourceVO);
//            Boolean check = false;
//            try {
//                check = this.checkConnectionWithConf(dataSourceVO, confMap);
//            } catch (Exception e) {
//                String tenantName = tenantService.getTenantNameById(batchDataSource.getTenantId());
//                String projectName = projectService.getProjectNameById(batchDataSource.getProjectId());
//                String message = String.format("{tenantId:%s , tenantName:%s , projectId:%s , projectName:%s , dataSourceName:%s}", batchDataSource.getTenantId(), tenantName, batchDataSource.getProjectId(), projectName, batchDataSource.getDataName());
//                job_log.error("数据源连接异常，[{}]。", message, e);
//            }
//            if (BooleanUtils.isFalse(check)) {
//                Integer num = BatchDataSourceService.sourceFailedMap.get(batchDataSource.getId());
//                if (num == null || num < BatchDataSourceService.retryNum) {
//                    num = Optional.ofNullable(num).orElse(0);
//                    BatchDataSourceService.sourceFailedMap.put(batchDataSource.getId(), ++num);
//                } else if (num == nonAlarm) {
//                    updateSourceState(batchDataSource, check);
//                    return;
//                } else {
//                    BatchDataSourceService.sourceFailedMap.put(batchDataSource.getId(), nonAlarm);
//                    this.sendAlarm(batchDataSource);
//                }
//            } else {
//                BatchDataSourceService.sourceFailedMap.put(batchDataSource.getId(), 0);
//            }
//            updateSourceState(batchDataSource, check);
//        } catch (Exception e) {
//            job_log.error("fail to checkConnection. {}", e);
//        } finally {
//            countDownLatch.countDown();
//        }
//    }
//
//    /**
//     * 更新数据源连接状态
//     * @param batchDataSource
//     * @param check
//     */
//    private void updateSourceState(BatchDataSource batchDataSource, boolean check){
//        final Integer linkState = check ? BatchDataSourceService.CONNECT_ACTIVE : BatchDataSourceService.CANNOT_CONNECT;
//        if (!linkState.equals(batchDataSource.getLinkState())) {
//            batchDataSource.setLinkState(linkState);
//            this.batchDataSourceDao.update(batchDataSource);
//        }
//    }
//
//    private Map<String, Object> getKerberosConf(Long id, Long tenantId, JSONObject json) {
//        BatchDataSource source = batchDataSourceDao.getOne(id);
//        Map<String, Object> confMap = new HashMap<>();
//        Long dtuicTenantId = tenantService.getDtuicTenantId(tenantId);
//        String localKerberosConf = getLocalKerberosConf(id);
//        JSONObject kerberosConfig = json.getJSONObject("kerberosConfig");
//        if (MapUtils.isNotEmpty(kerberosConfig)) {
//            downloadKerberosFromSftp(id, localKerberosConf, dtuicTenantId, json.getTimestamp(KERBEROS_FILE_TIMESTAMP));
//            confMap = handleKerberos(source.getType(), kerberosConfig, localKerberosConf);
//        }
//        return confMap;
//    }
//
//    private void sendAlarm(final BatchDataSource source) {
//        /**
//         * 插入一条通知记录
//         */
//        final Notify notify = this.getNotify(source.getProjectId(), source.getTenantId());
//
//        final NotifyRecordParam param = new NotifyRecordParam();
//        param.setTenantId(notify.getTenantId());
//        param.setAppType(AppType.RDOS.getType());
//        param.setProjectId(notify.getProjectId());
//        param.setContent(this.buildAlarmContent(source, new Date()));
//        param.setStatus(TaskStatus.FAILED.getStatus());
//
//
//        final ApiResponse<Long> longApiResponse = this.consoleNotifyApiClient.generateContent(param);
//
//        final Long contentId = null == longApiResponse ? 0L : longApiResponse.getData();
//        final Timestamp nowTime = Timestamp.valueOf(LocalDateTime.now());
//
//        final NotifyRecord notifyRecord = new NotifyRecord();
//        notifyRecord.setTenantId(source.getTenantId());
//        notifyRecord.setProjectId(source.getProjectId());
//        notifyRecord.setContentId(contentId);
//        notifyRecord.setCycTime(nowTime.toString());
//        notifyRecord.setNotifyId(notify.getId());
//        notifyRecord.setStatus(TaskStatus.FAILED.getStatus());
//        this.notifyRecordDao.insert(notifyRecord);
//
//        this.sendAlarm(notifyRecord, contentId, notify.getProjectId(), notify.getTenantId());
//    }
//
//    private void sendAlarm(final NotifyRecord notifyRecord, final Long contentId, final Long projectId, final Long tenantId) {
//        final List<User> userList = this.userDao.listUserAdmin(projectId, tenantId);
//
//        final List<UserMessageDTO> receivers = new ArrayList<>();
//        for (final User user : userList) {
//            final UserMessageDTO userDTO = new UserMessageDTO();
//            userDTO.setEmail(user.getEmail());
//            userDTO.setTelephone(user.getPhoneNumber());
//            userDTO.setUserId(user.getId());
//            userDTO.setUsername(user.getUserName());
//            receivers.add(userDTO);
//        }
//
//        final AlarmSendParam param = new AlarmSendParam();
//        param.setReceivers(receivers);
//        param.setTenantId(tenantId);
//        param.setProjectId(projectId);
//        param.setAppType(AppType.RDOS.getType());
//        param.setContentId(contentId);
//        param.setAlertGateSources(BatchDataSourceService.senderTypeList);
//        param.setTitle(BatchDataSourceService.TITLE);
//
//        this.consoleNotifyApiClient.sendAlarmNew(param);
//    }
//
//    private Notify getNotify(final Long projectId, final Long tenantId) {
//        Notify notify = this.notifyDao.getSystem(tenantId, projectId);
//        if (notify == null) {
//            notify = new Notify();
//            notify.setId(-projectId);
//            notify.setStatus(0);
//            notify.setBizType(NotifyType.BATCH.getType());
//            notify.setRelationId(0L);
//            notify.setTenantId(tenantId);
//            notify.setProjectId(projectId);
//            notify.setTriggerType(AlarmTrigger.FAILED.getTrigger());
//            notify.setCreateUserId(0L);
//            notify.setName(BatchDataSourceService.TITLE);
//            notify.setWebhook("");
//            this.notifyDao.insert(notify);
//        }
//        return notify;
//    }
//
//    private String buildAlarmContent(final BatchDataSource batchDataSource, final Date date) {
//        JSONObject dataJson = JSON.parseObject(Base64Util.baseDecode(batchDataSource.getDataJson()));
//        DataSourceVO dataSourceVO = new DataSourceVO();
//        dataSourceVO.setDataJson(dataJson);
//        dataSourceVO.setType(batchDataSource.getType());
//        String json = JsonUtils.formatJSON(parseDataJsonForView(dataSourceVO));
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//        final String contentStr = String.format(BatchDataSourceService.MESSAGE_TEMPLATE, this.environmentContext.getAlarmTitle(), batchDataSource.getDataName(),
//                DataSourceType.getSourceType(batchDataSource.getType()).name(), json, sdf.format(date));
//        return contentStr;
//    }
//
//    /**
//     * 获取所有schema
//     * @param sourceId 数据源id
//     * @return
//     */
//    public List<String> getAllSchemas(Long sourceId) {
//        BatchDataSource source = batchDataSourceDao.getOne(sourceId);
//        if (source == null) {
//            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_DATA_SOURCE);
//        }
//        String dataJson = Base64Util.baseDecode(source.getDataJson());
//        JSONObject json = JSON.parseObject(dataJson);
//        DataSourceType dataSourceType = DataSourceType.getSourceType(source.getType());
//        return DataSourceClientUtils.getAllDatabase(dataSourceType, JsonUtil.getStringDefaultEmpty(json, JDBC_URL),
//                JsonUtil.getStringDefaultEmpty(json, JDBC_USERNAME), JsonUtil.getStringDefaultEmpty(json, JDBC_PASSWORD), null, null);
//    }
//
//}
