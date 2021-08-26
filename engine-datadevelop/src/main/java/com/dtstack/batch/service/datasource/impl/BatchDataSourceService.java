package com.dtstack.batch.service.datasource.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.batch.common.enums.ETableType;
import com.dtstack.engine.api.domain.BatchDataSource;
import com.dtstack.engine.api.domain.BatchTask;
import com.dtstack.engine.api.domain.ScheduleEngineProject;
import com.dtstack.engine.api.domain.User;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.batch.common.exception.ErrorCode;
import com.dtstack.batch.common.exception.RdosDefineException;
import com.dtstack.batch.common.template.Reader;
import com.dtstack.batch.common.template.Setting;
import com.dtstack.batch.common.template.Writer;
import com.dtstack.batch.common.util.JsonUtil;
import com.dtstack.batch.dao.BatchDataSourceCenterDao;
import com.dtstack.batch.domain.*;
import com.dtstack.batch.dto.BatchDataSourceTaskDto;
import com.dtstack.batch.engine.rdbms.common.HadoopConfTool;
import com.dtstack.batch.engine.rdbms.common.enums.StoredType;
import com.dtstack.batch.engine.rdbms.service.impl.Engine2DTOService;
import com.dtstack.batch.enums.*;
import com.dtstack.batch.mapping.TableTypeEngineTypeMapping;
import com.dtstack.batch.schedule.JobParamReplace;
import com.dtstack.batch.service.impl.*;
import com.dtstack.batch.service.task.impl.BatchTaskParamService;
import com.dtstack.batch.service.task.impl.BatchTaskService;
import com.dtstack.batch.sync.format.ColumnType;
import com.dtstack.batch.sync.format.TypeFormat;
import com.dtstack.batch.sync.format.writer.HiveWriterFormat;
import com.dtstack.batch.sync.handler.ImpalaSyncBuilder;
import com.dtstack.batch.sync.handler.SyncBuilderFactory;
import com.dtstack.batch.sync.job.JobTemplate;
import com.dtstack.batch.sync.job.PluginName;
import com.dtstack.batch.sync.template.*;
import com.dtstack.batch.utils.TableOperateUtils;
import com.dtstack.batch.vo.*;
import com.dtstack.batch.web.datasource.vo.query.*;
import com.dtstack.batch.web.datasource.vo.result.BatchDataSourceAllowImportResultVO;
import com.dtstack.batch.web.datasource.vo.result.BatchDataSourceHaveImportResultVO;
import com.dtstack.batch.web.pager.PageQuery;
import com.dtstack.batch.web.pager.PageResult;
import com.dtstack.dtcenter.common.engine.JdbcInfo;
import com.dtstack.dtcenter.common.enums.*;
import com.dtstack.dtcenter.common.exception.DtCenterDefException;
import com.dtstack.dtcenter.common.kerberos.KerberosConfigVerify;
import com.dtstack.dtcenter.common.util.*;
import com.dtstack.dtcenter.loader.client.ClientCache;
import com.dtstack.dtcenter.loader.client.IClient;
import com.dtstack.dtcenter.loader.client.IKerberos;
import com.dtstack.dtcenter.loader.dto.ColumnMetaDTO;
import com.dtstack.dtcenter.loader.dto.SqlQueryDTO;
import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.dtstack.dtcenter.loader.source.DataBaseType;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.dtstack.dtcenter.loader.utils.DBUtil;
import com.dtstack.engine.api.dto.DataSourceDTO;
import com.dtstack.engine.api.pojo.lineage.Column;
import com.dtstack.engine.api.pojo.lineage.Table;
import com.dtstack.engine.datasource.facade.datasource.ApiServiceFacade;
import com.dtstack.engine.datasource.param.datasource.api.CreateDsParam;
import com.dtstack.engine.datasource.param.datasource.api.DsServiceListParam;
import com.dtstack.engine.datasource.param.datasource.api.ProductImportParam;
import com.dtstack.engine.datasource.param.datasource.api.RollDsParam;
import com.dtstack.engine.datasource.vo.datasource.api.DsServiceInfoVO;
import com.dtstack.engine.datasource.vo.datasource.api.DsServiceListVO;
import com.dtstack.engine.datasource.vo.datasource.api.DsShiftReturnVO;
import com.dtstack.engine.master.impl.ClusterService;
import com.dtstack.engine.master.impl.ComponentService;
import com.dtstack.engine.master.impl.ProjectService;
import com.dtstack.engine.master.impl.TenantService;
import com.dtstack.engine.master.impl.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/5/10
 */
@Service
public class BatchDataSourceService {

    private static final Logger logger = LoggerFactory.getLogger(BatchDataSourceService.class);

    /**
     * FIMXE 暂时将数据源读写权限设置在程序    里面
     */
    private static final Map<Integer, Integer> DATASOURCE_PERMISSION_MAP = Maps.newHashMap();

    public static final String JDBC_URL = "jdbcUrl";
    public static final String JDBC_USERNAME = "username";
    public static final String JDBC_PASSWORD = "password";
    public static final String JDBC_HOSTPORTS = "hostPorts";
    public static final String SECRET_KEY = "secretKey";

    public static final String HDFS_DEFAULTFS = "defaultFS";

    public static final String HADOOP_CONFIG = "hadoopConfig";

    public static String HIVE_METASTORE_URIS = "hiveMetastoreUris";

    private static final String HBASE_CONFIG = "hbaseConfig";

    public static final String HIVE_PARTITION = "partition";

    public static final String TEMP_TABLE_PREFIX = "select_sql_temp_table_";

    public static final String TEMP_TABLE_PREFIX_FROM_DQ = "temp_data_";

    private static final String KEY = "key";

    private static final String TYPE = "type";

    private static final String COLUMN = "column";

    private static final String EXTRAL_CONFIG = "extralConfig";

    private static final List<String> MYSQL_NUMBERS = Lists.newArrayList("TINYINT", "SMALLINT", "MEDIUMINT", "INT", "BIGINT", "INT UNSIGNED");

    private static final List<String> CLICKHOUSE_NUMBERS = Lists.newArrayList("UINT8", "UINT16", "UINT32", "UINT64", "INT8", "INT16", "INT32", "INT64");

    private static final List<String> ORACLE_NUMBERS = Lists.newArrayList("INT", "SMALLINT", "NUMBER");

    private static final List<String> SQLSERVER_NUMBERS = Lists.newArrayList("INT", "INTEGER", "SMALLINT", "TINYINT", "BIGINT");

    private static final List<String> POSTGRESQL_NUMBERS = Lists.newArrayList("INT2", "INT4", "INT8", "SMALLINT", "INTEGER", "BIGINT", "SMALLSERIAL", "SERIAL", "BIGSERIAL");

    private static final List<String> DB2_NUMBERS = Lists.newArrayList("SMALLINT", "INTEGER", "BIGINT");

    private static final List<String> GBASE_NUMBERS = Lists.newArrayList("SMALLINT", "TINYINT", "INT", "BIGINT", "FLOAT", "DOUBLE", "DECIMAL", "NUMERIC");

    private static final List<String> DMDB_NUMBERS = Lists.newArrayList("INT", "SMALLINT", "BIGINT","NUMBER");

    private static final List<String> GREENPLUM_NUMBERS = Lists.newArrayList("SMALLINT", "INTEGER", "BIGINT");

    private static final List<String> KINGBASE_NUMBERS = Lists.newArrayList("BIGINT", "DOUBLE", "FLOAT", "INT4", "INT8", "FLOAT", "FLOAT8", "NUMERIC");

    private static final List<String> INFLUXDB_NUMBERS = Lists.newArrayList("INTEGER");

    private static final Pattern NUMBER_PATTERN = Pattern.compile("NUMBER\\(\\d+\\)");

    private static final Pattern NUMBER_PATTERN2 = Pattern.compile("NUMBER\\((\\d+),([\\d-]+)\\)");

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final TypeFormat TYPE_FORMAT = new HiveWriterFormat();

    private static final String NO_PERMISSION = "NO PERMISSION";

    private static final String hdfsCustomConfig = "hdfsCustomConfig";

    private static final String KERBEROS_CONFIG = "kerberosConfig";

    /**
     * kerberos认证文件在 ftp上的相对路径
     */
    private static final String KERBEROS_DIR = "kerberosDir";

    /**
     * Kerberos 文件上传的时间戳
     */
    private static final String KERBEROS_FILE_TIMESTAMP = "kerberosFileTimestamp";


    static {
        BatchDataSourceService.DATASOURCE_PERMISSION_MAP.put(DataSourceType.MySQL.getVal(), EDataSourcePermission.READ_WRITE.getType());
        BatchDataSourceService.DATASOURCE_PERMISSION_MAP.put(DataSourceType.Oracle.getVal(), EDataSourcePermission.READ_WRITE.getType());
        BatchDataSourceService.DATASOURCE_PERMISSION_MAP.put(DataSourceType.SQLServer.getVal(), EDataSourcePermission.READ_WRITE.getType());
        BatchDataSourceService.DATASOURCE_PERMISSION_MAP.put(DataSourceType.PostgreSQL.getVal(), EDataSourcePermission.READ_WRITE.getType());
        BatchDataSourceService.DATASOURCE_PERMISSION_MAP.put(DataSourceType.RDBMS.getVal(), EDataSourcePermission.READ_WRITE.getType());
        BatchDataSourceService.DATASOURCE_PERMISSION_MAP.put(DataSourceType.HDFS.getVal(), EDataSourcePermission.READ_WRITE.getType());
        BatchDataSourceService.DATASOURCE_PERMISSION_MAP.put(DataSourceType.HIVE.getVal(), EDataSourcePermission.READ_WRITE.getType());
        BatchDataSourceService.DATASOURCE_PERMISSION_MAP.put(DataSourceType.DB2.getVal(), EDataSourcePermission.READ_WRITE.getType());
        BatchDataSourceService.DATASOURCE_PERMISSION_MAP.put(DataSourceType.Clickhouse.getVal(), EDataSourcePermission.READ_WRITE.getType());
        BatchDataSourceService.DATASOURCE_PERMISSION_MAP.put(DataSourceType.HIVE1X.getVal(), EDataSourcePermission.READ_WRITE.getType());
        BatchDataSourceService.DATASOURCE_PERMISSION_MAP.put(DataSourceType.HIVE3X.getVal(), EDataSourcePermission.READ_WRITE.getType());
        BatchDataSourceService.DATASOURCE_PERMISSION_MAP.put(DataSourceType.Phoenix.getVal(), EDataSourcePermission.READ_WRITE.getType());
        BatchDataSourceService.DATASOURCE_PERMISSION_MAP.put(DataSourceType.PHOENIX5.getVal(), EDataSourcePermission.READ_WRITE.getType());
        BatchDataSourceService.DATASOURCE_PERMISSION_MAP.put(DataSourceType.TiDB.getVal(), EDataSourcePermission.READ_WRITE.getType());
        BatchDataSourceService.DATASOURCE_PERMISSION_MAP.put(DataSourceType.DMDB.getVal(), EDataSourcePermission.READ_WRITE.getType());
        BatchDataSourceService.DATASOURCE_PERMISSION_MAP.put(DataSourceType.GREENPLUM6.getVal(), EDataSourcePermission.READ_WRITE.getType());
        BatchDataSourceService.DATASOURCE_PERMISSION_MAP.put(DataSourceType.KINGBASE8.getVal(), EDataSourcePermission.READ_WRITE.getType());
        BatchDataSourceService.DATASOURCE_PERMISSION_MAP.put(DataSourceType.INCEPTOR.getVal(), EDataSourcePermission.WRITE.getType());
    }

    @Resource(name = "batchJobParamReplace")
    private JobParamReplace jobParamReplace;

    @Autowired
    private BatchDataSourceCenterDao batchDataSourceCenterDao;

    @Autowired
    private BatchTaskService taskService;

    @Autowired
    private BatchDataSourceTaskRefService dataSourceTaskRefService;

    @Autowired
    private DictService dictService;

    @Autowired
    private UserService userService;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private BatchDataSourceTaskRefService batchDataSourceTaskRefService;

    @Autowired
    private BatchTaskParamService batchTaskParamService;

    @Autowired
    private BatchTaskService batchTaskService;

    @Autowired
    private ProjectEngineService projectEngineService;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private SyncBuilderFactory syncBuilderFactory;

    @Autowired
    private ApiServiceFacade apiServiceFacade;

    @Autowired
    private ComponentService componentService;

    @Autowired
    private ClusterService clusterService;

    // 数据同步-模版导入 writer 不需要添加默认值的数据源类型
    private static final Set<Integer> notPutValueFoeWriterSourceTypeSet = Sets.newHashSet(DataSourceType.HIVE.getVal(), DataSourceType.HIVE3X.getVal(),
            DataSourceType.HIVE1X.getVal(), DataSourceType.CarbonData.getVal(), DataSourceType.INCEPTOR.getVal(), DataSourceType.SparkThrift2_1.getVal());

    /**
     * 判断任务是否可以配置增量标识
     */
    public boolean canSetIncreConf(Long taskId) {
        final BatchTask task = this.batchTaskService.getBatchTaskById(taskId);
        if (task == null) {
            throw new RdosDefineException(ErrorCode.DATA_NOT_FIND);
        }

        if (!EJobType.SYNC.getVal().equals(task.getTaskType())) {
            return false;
        }

        // 增量同步任务不能在工作流中运行
        if (task.getFlowId() != 0) {
            return false;
        }

        if (StringUtils.isEmpty(task.getSqlText())) {
            return true;
        }

        try {
            final JSONObject json = JSON.parseObject(Base64Util.baseDecode(task.getSqlText()));
            this.batchTaskService.checkSyncJobContent(json.getJSONObject("job"), false);
        } catch (final RdosDefineException e) {
            return false;
        }

        return true;
    }

    /**
     * 根据项目id 删除项目下的数据源
     * @param projectId
     * @param userId
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteByProjectId(Long tenantId, Long projectId, Long userId) {
        cancelImportDataSourceByProject(tenantId, projectId, userId);
        batchDataSourceTaskRefService.deleteByProjectId(projectId);
    }

    public JSONObject trace(final Long taskId) {
        String sqlText = null;
        final BatchTask batchTask = this.taskService.getBatchTaskById(taskId);

        if (batchTask == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_TASK);
        } else {
            sqlText = batchTask.getSqlText();
        }

        final String sql = Base64Util.baseDecode(sqlText);
        if (StringUtils.isBlank(sql)) {
            return null;
        }

        final JSONObject sqlJson = JSON.parseObject(sql);
        JSONObject parserJson = sqlJson.getJSONObject("parser");
        if (parserJson != null) {
            parserJson = this.checkTrace(parserJson);
            parserJson.put("sqlText", sqlJson.getString("job"));
            parserJson.put("syncMode", sqlJson.get("syncMode"));
            parserJson.put("taskId", taskId);
        }
        return parserJson;
    }

    private JSONObject checkTrace(final JSONObject jsonObject) {
        final JSONObject keymap = jsonObject.getJSONObject("keymap");
        final JSONArray source = keymap.getJSONArray("source");
        final JSONArray target = keymap.getJSONArray("target");
        final JSONObject sourceMap = jsonObject.getJSONObject("sourceMap");
        final Integer fromId = (Integer) sourceMap.get("sourceId");
        final JSONObject targetMap = jsonObject.getJSONObject("targetMap");
        final Integer toId = (Integer) targetMap.get("sourceId");
        final JSONObject sourceType = sourceMap.getJSONObject("type");
        final List<String> sourceTables = this.getTables(sourceType);
        final JSONObject targetType = targetMap.getJSONObject("type");
        final List<String> targetTables = this.getTables(targetType);
        final BatchDataSource fromDs = this.getOne(fromId.longValue());
        final BatchDataSource toDs = this.getOne(toId.longValue());

        int fromSourceType = DataSourceType.getSourceType(fromDs.getType()).getVal();
        int toSourceType = DataSourceType.getSourceType(toDs.getType()).getVal();
        if (DataSourceType.HBASE.getVal() == fromSourceType || DataSourceType.HBASE.getVal() == toSourceType) {
            return jsonObject;
        }

        // 处理分库分表的信息
        this.addSourceList(sourceMap);

        if (CollectionUtils.isNotEmpty(sourceTables)) {
            getMetaDataColumns(sourceMap, sourceTables, fromDs);
        }

        if (CollectionUtils.isNotEmpty(targetTables)) {
            getMetaDataColumns(targetMap, targetTables, toDs);
        }
        //因为下面要对keyMap中target中的字段类型进行更新 所以遍历一次目标map 拿出字段和类型的映射
        Map<String,String> newTargetColumnTypeMap = targetMap.getJSONArray(COLUMN)
                .stream().map(column -> (JSONObject)column)
                .collect(Collectors.toMap(column -> column.getString(BatchDataSourceService.KEY), column -> column.getString(BatchDataSourceService.TYPE)));


        final Collection<BatchSysParameter> sysParams = this.batchTaskService.getSysParams();

        final JSONArray newSource = new JSONArray();
        final JSONArray newTarget = new JSONArray();
        for (int i = 0; i < source.size(); ++i) {
            boolean srcTag = true;
            final JSONArray srcColumns = sourceMap.getJSONArray("column");
            if (CollectionUtils.isNotEmpty(sourceTables)) {
                int j = 0;
                final String srcColName;
                String colValue = "";
                if (!(source.get(i) instanceof JSONObject)) {
                    srcColName = source.getString(i);
                } else {
                    //source 可能含有系统变量
                    srcColName = source.getJSONObject(i).getString("key");
                    colValue = source.getJSONObject(i).getString("value");
                }

                //srcColumns 源表中的字段
                for (; j < srcColumns.size(); ++j) {
                    final JSONObject srcColumn = srcColumns.getJSONObject(j);
                    if (srcColumn.getString("key").equals(srcColName)) {
                        break;
                    }
                }
                boolean isSysParam = false;
                for (final BatchSysParameter sysParam : sysParams) {
                    if (sysParam.strIsSysParam(colValue)) {
                        isSysParam = true;
                        break;
                    }
                }
                // 没有系统变量 还需要判断是否有自定义变量
                if(!isSysParam){
                    isSysParam = StringUtils.isNotBlank(colValue);
                }
                //兼容系统变量
                if (isSysParam) {
                    boolean hasThisKey = false;
                    for (int k = 0; k < srcColumns.size(); ++k) {
                        final JSONObject srcColumn = srcColumns.getJSONObject(k);
                        if (srcColumn.getString("key").equals(srcColName)) {
                            hasThisKey = true;
                            break;
                        }

                    }
                    if (!hasThisKey) {
                        //创建出系统变量colume
                        final JSONObject jsonColumn = new JSONObject();
                        jsonColumn.put("key", srcColName);
                        jsonColumn.put("value", colValue);
                        jsonColumn.put("type",source.getJSONObject(i).getString("type"));
                        jsonColumn.put("format",source.getJSONObject(i).getString("format"));
                        srcColumns.add(jsonColumn);
                    }
                }
                if (j == srcColumns.size() && !isSysParam) {
                    srcTag = false;
                }
            }

            boolean destTag = true;
            final JSONArray destColumns = targetMap.getJSONArray("column");
            if (CollectionUtils.isNotEmpty(targetTables)) {
                int k = 0;
                final String destColName;
                if (!(target.get(i) instanceof JSONObject)) {
                    destColName = target.getString(i);
                } else {
                    destColName = target.getJSONObject(i).getString("key");
                    //更新dest表中字段类型
                    final String newType = newTargetColumnTypeMap.get(destColName);
                    if (StringUtils.isNotEmpty(newType)){
                        target.getJSONObject(i).put("type",newType);
                    }
                }
                for (; k < destColumns.size(); ++k) {
                    final JSONObject destColumn = destColumns.getJSONObject(k);
                    if (destColumn.getString("key").equals(destColName)) {
                        break;
                    }
                }

                if (k == destColumns.size()) {
                    destTag = false;
                }
            }

            if (srcTag && destTag) {
                newSource.add(source.get(i));
                newTarget.add(target.get(i));
            }
        }

        keymap.put("source", newSource);
        keymap.put("target", newTarget);

        return jsonObject;
    }

    /**
     * 刷新sourceMap中的字段信息
     * 这个方法做了3个事情
     * 1.拿到sourceMap的中的原字段信息
     * 2.拿到对应表的 元数据最新字段信息
     * 3.和原字段信息进行匹配，
     * 如果原字段中的某个字段 不在最新字段中 那就忽略大小写再匹配一次，如果能匹配到就用原字段信息
     * 原因是 Hive执行alter语句增加字段会把源信息所有字段变小写  导致前端映射关系丢失 这里做一下处理
     * @param sourceMap
     * @param sourceTables
     * @param fromDs
     */
    private void getMetaDataColumns(JSONObject sourceMap, List<String> sourceTables, BatchDataSource fromDs) {
        JSONArray srcColumns = new JSONArray();
        List<JSONObject> custColumns = new ArrayList<>();
        List<String> allOldColumnsName = new ArrayList<>();
        Map<String,String> newNameToOldName = new HashMap<>();
        //对原有的字段进行处理 处理方式看方法注释
        getAllTypeColumnsMap(sourceMap, custColumns, allOldColumnsName, newNameToOldName);
        //获取原有字段
        JSONArray sourceColumns = sourceMap.getJSONArray(COLUMN);
        try {
            //获取一下schema
            String schema = sourceMap.getString("schema");
            List<JSONObject> tableColumns = getTableColumnIncludePart(fromDs, sourceTables.get(0), true, schema);
            for (JSONObject tableColumn : tableColumns) {
                String columnName = tableColumn.getString(BatchDataSourceService.KEY);
                //获取前端需要的真正的字段名称
                columnName = getRealColumnName(allOldColumnsName, newNameToOldName, columnName);

                String columnType = tableColumn.getString(BatchDataSourceService.TYPE);
                JSONObject jsonColumn = new JSONObject();
                jsonColumn.put(KEY, columnName);
                jsonColumn.put(TYPE, columnType);
                if (StringUtils.isNotEmpty(tableColumn.getString("isPart"))) {
                    jsonColumn.put("isPart", tableColumn.get("isPart"));
                }

                if (!(sourceColumns.get(0) instanceof String)) {
                    //这个是兼容原来的desc table 出来的结果 因为desc出来的不仅仅是字段名
                    for (int i = 0; i < sourceColumns.size(); i++) {
                        final JSONObject item = sourceColumns.getJSONObject(i);
                        if (item.get(KEY).equals(columnName) && item.containsKey("format")) {
                            jsonColumn.put("format", item.getString("format"));
                            break;
                        }
                    }
                }
                srcColumns.add(jsonColumn);
            }
        } catch (Exception ignore) {
            logger.error("数据同步获取表字段异常 : ", ignore.getMessage(), ignore);
            srcColumns = sourceColumns;
        }
        if (CollectionUtils.isNotEmpty(custColumns)) {
            srcColumns.addAll(custColumns);
        }
        sourceMap.put(COLUMN, srcColumns);
    }

    /**
     * 拿到真实的字段名
     * 判断 如果
     * @param allOldColumnsName  原有的所有字段的字段名
     * @param newNameToOldName   key是原有字段名的小写  value是原有字段名
     * @param columnName  元数据字段名
     * @return
     */
    private String getRealColumnName(List<String> allOldColumnsName, Map<String, String> newNameToOldName, String columnName) {
        if (allOldColumnsName.contains(columnName)){
            //认为字段名没有改动 直接返回
            return columnName;
        }

        String oldColumnName = newNameToOldName.get(columnName);
        if (StringUtils.isBlank(oldColumnName)){
            //认为字段名没有从大写变小写
            return columnName;
        }
        //字段名大写变小写了 所以返回原有字段名 保证前端映射无问题
        return oldColumnName;
    }

    /**
     * 这个方法 是对老数据中的字段做一下处理 会出来3个集合
     * @param sourceMap 源信息
     * @param custColumns 用户自定义字段
     * @param allOldColumnsName  老字段名称集合
     * @param newNameToOldName  新字段名称和老字段名字对应集合  key：字段名小写  value 原字段名 用处hive增加字段 字段名全小写导致对应关系丢失
     */
    private void getAllTypeColumnsMap(JSONObject sourceMap,List<JSONObject> custColumns,List<String> allOldColumnsName,Map<String,String> newNameToOldName ){
        JSONArray sourceColumns = sourceMap.getJSONArray(COLUMN);
        if (sourceColumns == null){
            return;
        }
        for (int i = 0; i < sourceColumns.size(); ++i) {
            JSONObject column = sourceColumns.getJSONObject(i);
            if (column.containsKey("value")) {
                custColumns.add(column);
                continue;
            }
            String key = column.getString(KEY);
            if (StringUtils.isBlank(key)){
                continue;
            }
            allOldColumnsName.add(key);
            newNameToOldName.put(key.toLowerCase(),key);
        }

    }

    private List<String> getTables(final Map<String, Object> map) {
        final List<String> tables = new ArrayList<>();
        if (map.get("table") instanceof String) {
            tables.add(map.get("table").toString());
        } else {
            final List<String> tableList = (List<String>) map.get("table");
            if (CollectionUtils.isNotEmpty(tableList)) {
                tables.addAll((List<String>) map.get("table"));
            }
        }

        return tables;
    }

    private void addSourceList(final JSONObject sourceMap) {
        if (sourceMap.containsKey("sourceList")) {
            return;
        }

        if (!DataSourceType.MySQL.getVal().equals(sourceMap.getJSONObject("type").getInteger("type"))) {
            return;
        }

        final JSONArray sourceList = new JSONArray();
        final JSONObject source = new JSONObject();
        source.put("sourceId", sourceMap.get("sourceId"));
        source.put("name", sourceMap.getString("name"));
        source.put("type", sourceMap.getJSONObject("type").getInteger("type"));
        source.put("tables", Arrays.asList(sourceMap.getJSONObject("type").getString("table")));
        sourceList.add(source);

        sourceMap.put("sourceList", sourceList);
    }

    /**
     * 配置或修改离线任务
     *
     * @param isFilter 获取数据同步脚本时候是否进行过滤用户名密码操作
     * @return
     * @throws IOException
     */
    public String getSyncSql(final TaskResourceParam param, boolean isFilter) {
        final Map<String, Object> sourceMap = param.getSourceMap();//来源集合
        final Map<String, Object> targetMap = param.getTargetMap();//目标集合
        final Map<String, Object> settingMap = param.getSettingMap();//流控、错误集合
        try {
            //清空资源和任务的关联关系
            this.dataSourceTaskRefService.removeRef(param.getId());

            this.setReaderJson(sourceMap, param.getId(), param.getProjectId(), param.getTenantId(), isFilter);
            this.setWriterJson(targetMap, param.getId(), param.getProjectId(), param.getTenantId(), isFilter);
            Reader reader = null;
            Writer writer = null;
            Setting setting = null;

            final Integer sourceType = Integer.parseInt(sourceMap.get("dataSourceType").toString());
            final Integer targetType = Integer.parseInt(targetMap.get("dataSourceType").toString());

            if (!this.checkDataSourcePermission(sourceType, EDataSourcePermission.READ.getType())) {
                throw new RdosDefineException(ErrorCode.SOURCE_CAN_NOT_AS_INPUT);
            }

            if (!this.checkDataSourcePermission(targetType, EDataSourcePermission.WRITE.getType())) {
                throw new RdosDefineException(ErrorCode.SOURCE_CAN_NOT_AS_OUTPUT);
            }

            final List<Long> sourceIds = (List<Long>) sourceMap.get("sourceIds");
            final List<Long> targetIds = (List<Long>) targetMap.get("sourceIds");

            reader = this.syncReaderBuild(sourceType, sourceMap, sourceIds);
            writer = this.syncWriterBuild(targetType, targetIds, targetMap, reader);

            this.setDirtyData(settingMap, param);
            setting = PublicUtil.objectToObject(settingMap, DefaultSetting.class);

            //检查有效性
            if (writer instanceof HiveWriter) {
                final HiveWriter hiveWriter = (HiveWriter) writer;
                if (!hiveWriter.isValid()) {
                    throw new RdosDefineException(hiveWriter.getErrMsg());
                }
            }

            if (param.getCreateModel() == TaskCreateModelType.TEMPLATE.getType()) {  //脚本模式直接返回
                return this.getJobText(this.putDefaultEmptyValueForReader(sourceType, reader),
                        this.putDefaultEmptyValueForWriter(targetType, writer), this.putDefaultEmptyValueForSetting(setting));
            }

            //获得数据同步job.xml的配置
            final String jobXml = this.getJobText(reader, writer, setting);
            final String parserXml = this.getParserText(sourceMap, targetMap, settingMap);
            final JSONObject sql = new JSONObject(3);
            sql.put("job", jobXml);
            sql.put("parser", parserXml);
            sql.put("createModel", TaskCreateModelType.GUIDE.getType());

            this.batchTaskParamService.checkParams(this.batchTaskParamService.checkSyncJobParams(sql.toJSONString()), param.getTaskVariables());
            return sql.toJSONString();
        } catch (final Exception e) {
            BatchDataSourceService.logger.error("{}", e);
            throw new RdosDefineException("解析同步任务失败: " + e.getMessage(), ErrorCode.SERVER_EXCEPTION);
        }
    }

    private Reader syncReaderBuild(final Integer sourceType, final Map<String, Object> sourceMap, final List<Long> sourceIds) throws IOException {

        Reader reader = null;
        if (Objects.nonNull(RDBMSSourceType.getByDataSourceType(sourceType))
                && !DataSourceType.HIVE.getVal().equals(sourceType)
                && !DataSourceType.HIVE1X.getVal().equals(sourceType)
                && !DataSourceType.HIVE3X.getVal().equals(sourceType)
                && !DataSourceType.CarbonData.getVal().equals(sourceType)
                && !DataSourceType.IMPALA.getVal().equals(sourceType)
                && !DataSourceType.SparkThrift2_1.getVal().equals(sourceType)) {
            reader = PublicUtil.objectToObject(sourceMap, RDBReader.class);
            ((RDBBase) reader).setSourceIds(sourceIds);
            return reader;
        }

        if (DataSourceType.HDFS.getVal().equals(sourceType)) {
            return PublicUtil.objectToObject(sourceMap, HDFSReader.class);
        }

        if (DataSourceType.HIVE.getVal().equals(sourceType) || DataSourceType.HIVE3X.getVal().equals(sourceType) || DataSourceType.HIVE1X.getVal().equals(sourceType) || DataSourceType.SparkThrift2_1.getVal().equals(sourceType)) {
            return PublicUtil.objectToObject(sourceMap, HiveReader.class);
        }

        if (DataSourceType.HBASE.getVal().equals(sourceType)) {
            return PublicUtil.objectToObject(sourceMap, HBaseReader.class);
        }

        if (DataSourceType.FTP.getVal().equals(sourceType)) {
            reader = PublicUtil.objectToObject(sourceMap, FtpReader.class);
            if (sourceMap.containsKey("isFirstLineHeader") && (Boolean) sourceMap.get("isFirstLineHeader")) {
                ((FtpReader) reader).setFirstLineHeader(true);
            } else {
                ((FtpReader) reader).setFirstLineHeader(false);
            }
            return reader;
        }

        if (DataSourceType.MAXCOMPUTE.getVal().equals(sourceType)) {
            reader = PublicUtil.objectToObject(sourceMap, OdpsReader.class);
            ((OdpsBase) reader).setSourceId(sourceIds.get(0));
            return reader;
        }

        if (DataSourceType.ES.getVal().equals(sourceType)) {
            return PublicUtil.objectToObject(sourceMap, EsReader.class);
        }

        if (DataSourceType.MONGODB.getVal().equals(sourceType)) {
            return PublicUtil.objectToObject(sourceMap, MongoDbReader.class);
        }

        if (DataSourceType.CarbonData.getVal().equals(sourceType)) {
            return PublicUtil.objectToObject(sourceMap, CarbonDataReader.class);
        }

        if (DataSourceType.Kudu.getVal().equals(sourceType)) {
            return syncBuilderFactory.getSyncBuilder(DataSourceType.Kudu.getVal()).syncReaderBuild(sourceMap, sourceIds);
        }

        if (DataSourceType.INFLUXDB.getVal().equals(sourceType)) {
            return PublicUtil.objectToObject(sourceMap, InfluxDBReader.class);
        }

        if (DataSourceType.IMPALA.getVal().equals(sourceType)) {
            //setSftpConf时，设置的hdfsConfig和sftpConf
            if (sourceMap.containsKey(HADOOP_CONFIG)){
                Object impalaConfig = sourceMap.get(HADOOP_CONFIG);
                if (impalaConfig instanceof Map){
                    sourceMap.put(HADOOP_CONFIG,impalaConfig);
                    sourceMap.put("sftpConf",((Map) impalaConfig).get("sftpConf"));
                }
            }
            return syncBuilderFactory.getSyncBuilder(DataSourceType.IMPALA.getVal()).syncReaderBuild(sourceMap, sourceIds);
        }

        if (DataSourceType.AWS_S3.getVal().equals(sourceType)) {
            return PublicUtil.objectToObject(sourceMap, AwsS3Reader.class);
        }

        throw new RdosDefineException("暂不支持" + DataSourceType.getSourceType(sourceType).name() +"作为数据同步的源");
    }

    private Writer syncWriterBuild(final Integer targetType, final List<Long> targetIds, final Map<String, Object> targetMap, final Reader reader) throws IOException {
        Writer writer = null;

        if (Objects.nonNull(RDBMSSourceType.getByDataSourceType(targetType))
                && !DataSourceType.HIVE.getVal().equals(targetType)
                && !DataSourceType.HIVE1X.getVal().equals(targetType)
                && !DataSourceType.HIVE3X.getVal().equals(targetType)
                && !DataSourceType.IMPALA.getVal().equals(targetType)
                && !DataSourceType.CarbonData.getVal().equals(targetType)
                && !DataSourceType.SparkThrift2_1.getVal().equals(targetType)
                && !DataSourceType.INCEPTOR.getVal().equals(targetType)) {
            writer = PublicUtil.objectToObject(targetMap, RDBWriter.class);
            ((RDBBase) writer).setSourceIds(targetIds);
            return writer;
        }

        if (DataSourceType.HDFS.getVal().equals(targetType)) {
            return PublicUtil.objectToObject(targetMap, HDFSWriter.class);
        }

        if (DataSourceType.HIVE.getVal().equals(targetType) || DataSourceType.HIVE3X.getVal().equals(targetType) || DataSourceType.HIVE1X.getVal().equals(targetType) || DataSourceType.SparkThrift2_1.getVal().equals(targetType)) {
            return PublicUtil.objectToObject(targetMap, HiveWriter.class);
        }

        if (DataSourceType.FTP.getVal().equals(targetType)) {
            return PublicUtil.objectToObject(targetMap, FtpWriter.class);
        }

        if (DataSourceType.ES.getVal().equals(targetType)) {
            return PublicUtil.objectToObject(targetMap, EsWriter.class);
        }

        if (DataSourceType.HBASE.getVal().equals(targetType)) {
            targetMap.put("hbaseConfig",targetMap.get("hbaseConfig"));
            writer = PublicUtil.objectToObject(targetMap, HBaseWriter.class);
            HBaseWriter hbaseWriter = (HBaseWriter) writer;
            List<String> sourceColNames = new ArrayList<>();
            List<Map<String,String>> columnList = (List<Map<String, String>>) targetMap.get("column");
            for (Map<String,String> column : columnList){
                if (column.containsKey("key")){
                    sourceColNames.add(column.get("key"));
                }
            }
            hbaseWriter.setSrcColumns(sourceColNames);
            return writer;
        }

        if (DataSourceType.MAXCOMPUTE.getVal().equals(targetType)) {
            writer = PublicUtil.objectToObject(targetMap, OdpsWriter.class);
            ((OdpsBase) writer).setSourceId(targetIds.get(0));
            return writer;
        }

        if (DataSourceType.REDIS.getVal().equals(targetType)) {
            return PublicUtil.objectToObject(targetMap, RedisWriter.class);
        }

        if (DataSourceType.MONGODB.getVal().equals(targetType)) {
            return PublicUtil.objectToObject(targetMap, MongoDbWriter.class);
        }

        if (DataSourceType.CarbonData.getVal().equals(targetType)) {
            return PublicUtil.objectToObject(targetMap, CarbonDataWriter.class);
        }

        if (DataSourceType.Kudu.getVal().equals(targetType)) {
            return syncBuilderFactory.getSyncBuilder(DataSourceType.Kudu.getVal()).syncWriterBuild(targetIds, targetMap, reader);
        }

        if (DataSourceType.IMPALA.getVal().equals(targetType)) {
            return syncBuilderFactory.getSyncBuilder(DataSourceType.IMPALA.getVal()).syncWriterBuild(targetIds, targetMap, reader);
        }

        if (DataSourceType.AWS_S3.getVal().equals(targetType)) {
            return PublicUtil.objectToObject(targetMap, AwsS3Writer.class);
        }

        if (DataSourceType.INCEPTOR.getVal().equals(targetType)) {
            return PublicUtil.objectToObject(targetMap, InceptorWriter.class);
        }

        throw new RdosDefineException("暂不支持" + DataSourceType.getSourceType(targetType).name() +"作为数据同步的目标");
    }

    private void setDirtyData(final Map<String, Object> settingMap, final TaskResourceParam param) {
        if (param.getCreateModel() == TaskCreateModelType.TEMPLATE.getType()) {
            settingMap.put("isSaveDirty", 1);
            final BatchTask task = this.taskService.getBatchTaskById(param.getId());
            param.setName(task.getName());
        }

        if (settingMap.containsKey("isSaveDirty")) {

            // 兼容前端的0/1和true/false
            final String isSaveDirty = settingMap.get("isSaveDirty").toString();
            boolean isSaveDirtyVal = false;
            if ("1".equals(isSaveDirty) || "true".equals(isSaveDirty)) {
                isSaveDirtyVal = true;
                settingMap.put("isSaveDirty", 1);
            } else {
                settingMap.put("isSaveDirty", 0);
            }
        } else {
            settingMap.put("isSaveDirty", 0);
        }
    }

    /**
     * 向导模式，填充reader的默认信息
     * @param sourceType
     * @param reader
     * @return
     */
    private Reader putDefaultEmptyValueForReader(int sourceType, Reader reader) {
        if (Objects.nonNull(RDBMSSourceType.getByDataSourceType(sourceType))
                && DataSourceType.HIVE.getVal() != sourceType
                && DataSourceType.HIVE1X.getVal() != sourceType
                && DataSourceType.HIVE3X.getVal() != sourceType
                && DataSourceType.SparkThrift2_1.getVal() != sourceType
                && DataSourceType.CarbonData.getVal() != sourceType) {
            RDBReader rdbReader = (RDBReader) reader;
            rdbReader.setWhere("");
            rdbReader.setSplitPK("");
            return rdbReader;
        } else if (DataSourceType.ES.getVal() == sourceType) {
            EsReader esReader = (EsReader) reader;
            JSONObject obj = new JSONObject();
            obj.put("col", "");
            JSONObject query = new JSONObject();
            query.put("match", obj);
            esReader.setQuery(query);
            JSONObject column = new JSONObject();
            column.put("key", "col1");
            column.put("type", "string");
            esReader.getColumn().add(column);
            return esReader;
        } else if (DataSourceType.FTP.getVal() == sourceType) {
            FtpReader ftpReader = (FtpReader) reader;
            ftpReader.setPath("/");
            return ftpReader;
        } else if (DataSourceType.INFLUXDB.getVal().equals(sourceType)) {
            InfluxDBReader influxDBReader = (InfluxDBReader) reader;
            influxDBReader.setWhere("");
            influxDBReader.setSplitPK("");
            return influxDBReader;
        }
        return reader;
    }

    private Writer putDefaultEmptyValueForWriter(int targetType, Writer writer) {
        if (Objects.nonNull(RDBMSSourceType.getByDataSourceType(targetType))
                && !notPutValueFoeWriterSourceTypeSet.contains(targetType)){
            RDBWriter rdbWriter = (RDBWriter) writer;
            rdbWriter.setPostSql("");
            rdbWriter.setPostSql("");
            rdbWriter.setSession("");
            if (DataSourceType.GREENPLUM6.getVal() == targetType){
                rdbWriter.setWriteMode("insert");
            }else {
                rdbWriter.setWriteMode("replace");
            }
            return rdbWriter;
        } else if (DataSourceType.ES.getVal() == targetType) {
            EsWriter esWriter = (EsWriter) writer;
            esWriter.setType("");
            esWriter.setIndex("");
            JSONObject column = new JSONObject();
            column.put("key", "col1");
            column.put("type", "string");
            JSONObject idColumn = new JSONObject();
            idColumn.put("index", 0);
            idColumn.put("type", "int");
            esWriter.getIdColumn().add(idColumn);
            return esWriter;
        }
        return writer;
    }

    private Setting putDefaultEmptyValueForSetting(Setting setting) {
        DefaultSetting defaultSetting = (DefaultSetting) setting;
        defaultSetting.setSpeed(1.0);
        defaultSetting.setRecord(0);
        defaultSetting.setPercentage(0.0);
        return defaultSetting;
    }

    /**
     * 校验数据源可以使用的场景---读写
     * 如果数据源没有添加到关系里面,默认为true
     * FIXME 暂时先把对应关系写在程序里面
     *
     * @return
     */
    private boolean checkDataSourcePermission(int dataSourceType, int targetType) {
        Integer permission = DATASOURCE_PERMISSION_MAP.get(dataSourceType);
        if (permission == null) {
            return true;
        }

        return (permission & targetType) == targetType;

    }

    public String getParserText(final Map<String, Object> sourceMap,
                                final Map<String, Object> targetMap,
                                final Map<String, Object> settingMap) throws Exception {

        JSONObject parser = new JSONObject(4);
        parser.put("sourceMap", getSourceMap(sourceMap));
        parser.put("targetMap", getTargetMap(targetMap));
        parser.put("setting", settingMap);

        JSONObject keymap = new JSONObject(2);
        keymap.put("source", MapUtils.getObject(sourceMap, "column"));
        keymap.put("target", MapUtils.getObject(targetMap, "column"));
        parser.put("keymap", keymap);

        return parser.toJSONString();
    }

    private Map<String, Object> getSourceMap(Map<String, Object> sourceMap) {
        BatchDataSource source = (BatchDataSource) sourceMap.get("source");

        Map<String, Object> typeMap = new HashMap<>(6);
        typeMap.put("type", source.getType());

        Object obj = JSON.parse(JSON.toJSONString(MapUtils.getObject(sourceMap, "column")));
        if (Objects.nonNull(RDBMSSourceType.getByDataSourceType(source.getType())) && !DataSourceType.IMPALA.getVal().equals(source.getType())) {
            if (DataSourceType.HIVE.getVal().equals(source.getType()) || DataSourceType.HIVE3X.getVal().equals(source.getType()) || DataSourceType.HIVE1X.getVal().equals(source.getType()) || DataSourceType.SparkThrift2_1.getVal().equals(source.getType())) {
                typeMap.put("partition", MapUtils.getString(sourceMap, "partition"));
            }

            if (!DataSourceType.HIVE.getVal().equals(source.getType()) && !DataSourceType.HIVE3X.getVal().equals(source.getType()) && !DataSourceType.HIVE1X.getVal().equals(source.getType())
                    && !DataSourceType.CarbonData.getVal().equals(source.getType()) && !DataSourceType.SparkThrift2_1.getVal().equals(source.getType())) {
                String table = ((List<String>) sourceMap.get("table")).get(0);
                JSONArray oriCols = (JSONArray) obj;
                List<JSONObject> dbCols = this.getTableColumn(source, table, Objects.isNull(sourceMap.get("schema")) ? null : sourceMap.get("schema").toString());

                if (oriCols.get(0) instanceof String) {//老版本存在字符串数组
                    obj = dbCols;
                } else {
                    Set<String> keys = new HashSet<>(oriCols.size());
                    for (int i = 0; i < oriCols.size(); i++) {
                        keys.add(oriCols.getJSONObject(i).getString("key"));
                    }

                    List<JSONObject> newCols = new ArrayList<>();
                    for (JSONObject dbCol : dbCols) {
                        JSONObject col = null;
                        for (Object oriCol : oriCols) {
                            if (((JSONObject) oriCol).getString("key").equals(dbCol.getString("key"))) {
                                col = (JSONObject) oriCol;
                                break;
                            }
                        }

                        if (col == null) {
                            col = dbCol;
                        }

                        newCols.add(col);
                    }

                    //加上常量字段信息
                    for (Object oriCol : oriCols) {
                        if ("string".equalsIgnoreCase(((JSONObject) oriCol).getString("type"))) {
                            //去重
                            if(!keys.contains(((JSONObject) oriCol).getString("key"))){
                                newCols.add((JSONObject) oriCol);
                            }
                        }
                    }
                    obj = newCols;
                }
            }

            typeMap.put("where", MapUtils.getString(sourceMap, "where"));
            typeMap.put("splitPK", MapUtils.getString(sourceMap, "splitPK"));
            typeMap.put("table", sourceMap.get("table"));
        } else if (DataSourceType.HDFS.getVal().equals(source.getType())) {
            typeMap.put("path", MapUtils.getString(sourceMap, "path"));
            typeMap.put("fieldDelimiter", MapUtils.getString(sourceMap, "fieldDelimiter"));
            typeMap.put("fileType", MapUtils.getString(sourceMap, "fileType"));
            typeMap.put("encoding", MapUtils.getString(sourceMap, "encoding"));
        } else if (DataSourceType.HBASE.getVal().equals(source.getType())) {
            typeMap.put("encoding", MapUtils.getString(sourceMap, "encoding"));
            typeMap.put("table", MapUtils.getString(sourceMap, "table"));
            typeMap.put("startRowkey", MapUtils.getString(sourceMap, "startRowkey"));
            typeMap.put("endRowkey", MapUtils.getString(sourceMap, "endRowkey"));
            typeMap.put("isBinaryRowkey", MapUtils.getString(sourceMap, "isBinaryRowkey"));
            typeMap.put("scanCacheSize", MapUtils.getString(sourceMap, "scanCacheSize"));
            typeMap.put("scanBatchSize", MapUtils.getString(sourceMap, "scanBatchSize"));
        } else if (DataSourceType.FTP.getVal().equals(source.getType())) {
            typeMap.put("encoding", MapUtils.getString(sourceMap, "encoding"));
            typeMap.put("path", sourceMap.get("path"));
            typeMap.put("fieldDelimiter", MapUtils.getString(sourceMap, "fieldDelimiter"));
            typeMap.put("isFirstLineHeader", MapUtils.getBooleanValue(sourceMap, "isFirstLineHeader"));
        } else if (DataSourceType.MAXCOMPUTE.getVal().equals(source.getType())) {
            typeMap.put("table", MapUtils.getString(sourceMap, "table"));
            typeMap.put("partition", MapUtils.getString(sourceMap, "partition"));
        } else if (DataSourceType.Kudu.getVal().equals(source.getType())) {
            Assert.isTrue(StringUtils.isNotEmpty(MapUtils.getString(sourceMap, "table")), "表名不能为空");
            String table = MapUtils.getString(sourceMap, "table");
            typeMap.put("table", table);
            typeMap.put("where", MapUtils.getString(sourceMap, "where"));
            obj = this.getTableColumn(source, table, null);
        } else if (DataSourceType.IMPALA.getVal().equals(source.getType())) {
            typeMap.put("table", MapUtils.getString(sourceMap, "table"));
            typeMap.put(TableLocationType.key(), MapUtils.getString(sourceMap, TableLocationType.key()));
            Optional.ofNullable(MapUtils.getString(sourceMap, "partition")).ifPresent(s -> typeMap.put("partition", s));
        } else if (DataSourceType.AWS_S3.getVal().equals(source.getType())) {
            typeMap.put("bucket", MapUtils.getString(sourceMap, "bucket"));
            typeMap.put("objects", MapUtils.getObject(sourceMap, "objects"));
            typeMap.put("fieldDelimiter", MapUtils.getString(sourceMap, "fieldDelimiter"));
            typeMap.put("encoding", MapUtils.getString(sourceMap, "encoding"));
            typeMap.put("isFirstLineHeader", MapUtils.getBoolean(sourceMap, "isFirstLineHeader"));
        } else if (DataSourceType.INFLUXDB.getVal().equals(source.getType())) {
            typeMap.put("customSql", MapUtils.getString(sourceMap, "customSql"));
            typeMap.put("format", MapUtils.getString(sourceMap, "format"));
            typeMap.put("where", MapUtils.getString(sourceMap, "where"));
            typeMap.put("splitPK", MapUtils.getString(sourceMap, "splitPK"));
            typeMap.put("table", MapUtils.getObject(sourceMap, "table"));
            typeMap.put("schema", MapUtils.getString(sourceMap, "schema"));
        }

        Map<String, Object> map = new HashMap<>(4);
        map.put("sourceId", source.getId());
        map.put("name", source.getDataName());
        map.put("column", obj);
        map.put("type", typeMap);
        map.put(EXTRAL_CONFIG, sourceMap.getOrDefault(EXTRAL_CONFIG, ""));

        if (sourceMap.containsKey("increColumn")) {
            map.put("increColumn", sourceMap.get("increColumn"));
        }

        if (sourceMap.containsKey("sourceList")) {
            map.put("sourceList", sourceMap.get("sourceList"));
        }
        if (sourceMap.containsKey("schema")) {
            map.put("schema", sourceMap.get("schema"));
        }
        return map;
    }

    private Map<String, Object> getTargetMap(Map<String, Object> targetMap) throws Exception {
        BatchDataSource target = (BatchDataSource) targetMap.get("source");

        Map<String, Object> typeMap = new HashMap<>(6);
        typeMap.put("type", target.getType());

        Object obj = null;
        if (Objects.nonNull(RDBMSSourceType.getByDataSourceType(target.getType())) && !DataSourceType.IMPALA.getVal().equals(target.getType())) {
            Assert.isTrue(StringUtils.isNotEmpty(MapUtils.getString(targetMap, "table")), "表名不能为空");
            if (DataSourceType.HIVE.getVal().equals(target.getType()) || DataSourceType.HIVE3X.getVal().equals(target.getType())
                    || DataSourceType.HIVE1X.getVal().equals(target.getType()) || DataSourceType.SparkThrift2_1.getVal().equals(target.getType())
                    || DataSourceType.INCEPTOR.getVal().equals(target.getType())) {
                obj = MapUtils.getObject(targetMap, "column");
                typeMap.put("partition", MapUtils.getString(targetMap, "partition"));
            } else if (DataSourceType.CarbonData.getVal().equals(target.getType())) {
                obj = MapUtils.getObject(targetMap, "column");
            } else {
                String schema = (targetMap.containsKey("schema") && targetMap.get("schema") != null) ? targetMap.get("schema").toString() : null;
                String table = ((List<String>) targetMap.get("table")).get(0);
                obj = this.getTableColumn(target, table, schema);
            }

            typeMap.put("writeMode", MapUtils.getString(targetMap, "writeMode"));
            typeMap.put("table", targetMap.get("table"));
            typeMap.put("preSql", MapUtils.getString(targetMap, "preSql"));
            typeMap.put("postSql", MapUtils.getString(targetMap, "postSql"));
        } else if (DataSourceType.HDFS.getVal().equals(target.getType())) {
            obj = MapUtils.getObject(targetMap, "column");
            typeMap.put("path", MapUtils.getString(targetMap, "path"));
            typeMap.put("fileName", MapUtils.getString(targetMap, "fileName"));
            typeMap.put("writeMode", MapUtils.getString(targetMap, "writeMode"));
            typeMap.put("fieldDelimiter", MapUtils.getString(targetMap, "fieldDelimiter"));
            typeMap.put("encoding", MapUtils.getString(targetMap, "encoding"));
            typeMap.put("fileType", MapUtils.getString(targetMap, "fileType"));
        } else if (DataSourceType.HBASE.getVal().equals(target.getType())) {
            Assert.isTrue(StringUtils.isNotEmpty(MapUtils.getString(targetMap, "table")), "表名不能为空");
            obj = MapUtils.getObject(targetMap, "column");
            typeMap.put("encoding", MapUtils.getString(targetMap, "encoding"));
            typeMap.put("table", MapUtils.getString(targetMap, "table"));
            typeMap.put("nullMode", MapUtils.getString(targetMap, "nullMode"));
            typeMap.put("writeBufferSize", MapUtils.getString(targetMap, "writeBufferSize"));
            typeMap.put("rowkey", MapUtils.getString(targetMap, "rowkey"));
        } else if (DataSourceType.FTP.getVal().equals(target.getType())) {
            obj = MapUtils.getObject(targetMap, "column");
            typeMap.put("encoding", MapUtils.getString(targetMap, "encoding"));
            typeMap.put("ftpFileName", MapUtils.getString(targetMap, "ftpFileName"));
            typeMap.put("path", MapUtils.getString(targetMap, "path"));
            typeMap.put("writeMode", MapUtils.getString(targetMap, "writeMode"));
            typeMap.put("fieldDelimiter", MapUtils.getString(targetMap, "fieldDelimiter"));
        } else if (DataSourceType.MAXCOMPUTE.getVal().equals(target.getType())) {
            Assert.isTrue(StringUtils.isNotEmpty(MapUtils.getString(targetMap, "table")), "表名不能为空");
            obj = MapUtils.getObject(targetMap, "column");
            typeMap.put("table", MapUtils.getString(targetMap, "table"));
            typeMap.put("partition", MapUtils.getString(targetMap, "partition"));
            typeMap.put("writeMode", MapUtils.getString(targetMap, "writeMode"));
        } else if (DataSourceType.Kudu.getVal().equals(target.getType())) {
            Assert.isTrue(StringUtils.isNotEmpty(MapUtils.getString(targetMap, "table")), "表名不能为空");
            String table = MapUtils.getString(targetMap, "table");
            typeMap.put("writeMode", MapUtils.getString(targetMap, "writeMode"));
            typeMap.put("table", table);
            obj = this.getTableColumn(target, table, null);
        } else if (DataSourceType.IMPALA.getVal().equals(target.getType())) {
            typeMap.put("table", MapUtils.getString(targetMap, "table"));
            typeMap.put(TableLocationType.key(), MapUtils.getString(targetMap, TableLocationType.key()));
            Optional.ofNullable(MapUtils.getString(targetMap, "partition")).ifPresent(s -> typeMap.put("partition", s));
            Optional.ofNullable(MapUtils.getString(targetMap, "writeMode")).ifPresent(s -> typeMap.put("writeMode", s));
            obj = MapUtils.getObject(targetMap, "column");
        } else if (DataSourceType.AWS_S3.getVal().equals(target.getType())) {
            obj = MapUtils.getObject(targetMap, "column");
            typeMap.put("bucket", MapUtils.getString(targetMap, "bucket"));
            typeMap.put("object", MapUtils.getString(targetMap, "object"));
            typeMap.put("writeMode", MapUtils.getString(targetMap, "writeMode"));
            typeMap.put("fieldDelimiter", MapUtils.getString(targetMap, "fieldDelimiter"));
            typeMap.put("encoding", MapUtils.getString(targetMap, "encoding"));
        }

        Map<String, Object> map = new HashMap<>(4);
        map.put("sourceId", target.getId());
        map.put("name", target.getDataName());
        map.put("column", obj);
        map.put("type", typeMap);
        map.put(EXTRAL_CONFIG, targetMap.getOrDefault(EXTRAL_CONFIG, ""));
        if (targetMap.containsKey("schema")) {
            map.put("schema", targetMap.get("schema"));
        }
        map.put(BatchDataSourceService.EXTRAL_CONFIG, targetMap.getOrDefault(BatchDataSourceService.EXTRAL_CONFIG, ""));

        return map;
    }

    /**
     * 设置write属性
     *
     * @param map
     * @param taskId
     * @param projectId
     * @param tenantId
     * @param isFilter 是否过滤账号密码
     * @throws Exception
     */
    public void setWriterJson(Map<String, Object> map, Long taskId, Long projectId, Long tenantId, boolean isFilter) throws Exception {
        if (map.get("sourceId") == null) {
            throw new RdosDefineException(ErrorCode.DATA_SOURCE_NOT_SET);
        }

        Long sourceId = Long.parseLong(map.get("sourceId").toString());
        BatchDataSource source = getOne(sourceId);
        Map<String,Object> kerberos = fillKerberosConfig(sourceId);
        Long dtuicTenantId = tenantService.getDtuicTenantId(tenantId);
        map.put("sourceIds", Arrays.asList(sourceId));
        map.put("source", source);

        JSONObject json = JSON.parseObject(source.getDataJson());
        map.put("dataSourceType", source.getType());
        Integer sourceType = source.getType();
        // 根据jdbc信息 替换map中的信息
        replaceJdbcInfoByDataJsonToMap(map, sourceId, source, dtuicTenantId, json, sourceType);

        if (DataSourceType.Kudu.getVal().equals(sourceType)) {
            syncBuilderFactory.getSyncBuilder(DataSourceType.Kudu.getVal()).setWriterJson(map, json,kerberos);
            setSftpConfig(sourceId, json,dtuicTenantId, map, HADOOP_CONFIG);
        }

        if (DataSourceType.IMPALA.getVal().equals(sourceType)) {
            syncBuilderFactory.getSyncBuilder(DataSourceType.IMPALA.getVal()).setWriterJson(map, json,kerberos);
            setSftpConfig(sourceId, json, dtuicTenantId, map, HADOOP_CONFIG);
        }

        if (isFilter) {
            map.remove("username");
            map.remove("password");

            //S3数据源不需要移除 accessKey
            if(!DataSourceType.AWS_S3.getVal().equals(sourceType)){
                map.remove("accessKey");
            }
        }

        if (taskId != null) {
            //插入资源和任务的关联关系
            dataSourceTaskRefService.addRef(sourceId, taskId, projectId, tenantId);
        }
    }

    /**
     * 根据dataJson 替换map中 jdbc信息
     *
     * @param map
     * @param sourceId
     * @param source
     * @param dtuicTenantId
     * @param json
     * @param sourceType
     * @throws Exception
     */
    private void replaceJdbcInfoByDataJsonToMap(Map<String, Object> map, Long sourceId, BatchDataSource source, Long dtuicTenantId, JSONObject json, Integer sourceType) throws Exception {
        if (Objects.nonNull(RDBMSSourceType.getByDataSourceType(sourceType))
                && !DataSourceType.HIVE.getVal().equals(sourceType)
                && !DataSourceType.HIVE3X.getVal().equals(sourceType)
                && !DataSourceType.HIVE1X.getVal().equals(sourceType)
                && !DataSourceType.SparkThrift2_1.getVal().equals(sourceType)
                && !DataSourceType.IMPALA.getVal().equals(sourceType)
                && !DataSourceType.CarbonData.getVal().equals(sourceType)
                && !DataSourceType.INCEPTOR.getVal().equals(sourceType)) {
            DataBaseType dataBaseType = DataSourceDataBaseType.getBaseTypeBySourceType(sourceType);
            map.put("type", dataBaseType);
            map.put("password", JsonUtil.getStringDefaultEmpty(json, JDBC_PASSWORD));
            map.put("username", JsonUtil.getStringDefaultEmpty(json, JDBC_USERNAME));
            map.put("jdbcUrl", JsonUtil.getStringDefaultEmpty(json, JDBC_URL));
            processTable(map);
        } else if (DataSourceType.HIVE.getVal().equals(sourceType) || DataSourceType.HIVE3X.getVal().equals(sourceType) || DataSourceType.HIVE1X.getVal().equals(sourceType) || DataSourceType.SparkThrift2_1.getVal().equals(sourceType)) {
            DataBaseType dataBaseType = DataSourceDataBaseType.getBaseTypeBySourceType(sourceType);
            map.put("isDefaultSource", source.getIsDefault() == 1 ? true : false);
            map.put("type", dataBaseType);
            map.put("password", JsonUtil.getStringDefaultEmpty(json, JDBC_PASSWORD));
            map.put("username", JsonUtil.getStringDefaultEmpty(json, JDBC_USERNAME));
            map.put("jdbcUrl", JsonUtil.getStringDefaultEmpty(json, JDBC_URL));
            map.put("partition", map.get(HIVE_PARTITION));
            map.put("defaultFS", JsonUtil.getStringDefaultEmpty(json, HDFS_DEFAULTFS));
            this.checkLastHadoopConfig(map, json);
            setSftpConfig(sourceId, json, dtuicTenantId, map, HADOOP_CONFIG);
        } else if (DataSourceType.HDFS.getVal().equals(sourceType)) {
            map.put("defaultFS", JsonUtil.getStringDefaultEmpty(json, HDFS_DEFAULTFS));
            this.checkLastHadoopConfig(map,json);
            setSftpConfig(sourceId, json, dtuicTenantId, map, HADOOP_CONFIG);
        } else if (DataSourceType.HBASE.getVal().equals(sourceType)) {
            String jsonStr = json.getString(HBASE_CONFIG);
            Map jsonMap = new HashMap();
            if (StringUtils.isNotEmpty(jsonStr)){
                jsonMap = objectMapper.readValue(jsonStr,Map.class);
            }
            map.put("hbaseConfig", jsonMap);
            setSftpConfig(sourceId, json, dtuicTenantId, map, "hbaseConfig");
        } else if (DataSourceType.FTP.getVal().equals(sourceType)) {
            map.putAll(json);
        } else if (DataSourceType.MAXCOMPUTE.getVal().equals(sourceType)) {
            map.put("accessId", json.get("accessId"));
            map.put("accessKey", json.get("accessKey"));
            map.put("project", json.get("project"));
            map.put("endPoint", json.get("endPoint"));
        } else if ((DataSourceType.ES.getVal().equals(sourceType))) {
            map.put("address", json.get("address"));
            map.put("username", JsonUtil.getStringDefaultEmpty(json, "username"));
            map.put("password", JsonUtil.getStringDefaultEmpty(json, "password"));
        } else if (DataSourceType.REDIS.getVal().equals(sourceType)) {
            map.put("type", "string");
            map.put("hostPort", JsonUtil.getStringDefaultEmpty(json, "hostPort"));
            map.put("database", json.getIntValue("database"));
            map.put("password", JsonUtil.getStringDefaultEmpty(json, "password"));
        } else if (DataSourceType.MONGODB.getVal().equals(sourceType)) {
            map.put(JDBC_HOSTPORTS, JsonUtil.getStringDefaultEmpty(json, JDBC_HOSTPORTS));
            map.put("username", JsonUtil.getStringDefaultEmpty(json, "username"));
            map.put("database", JsonUtil.getStringDefaultEmpty(json, "database"));
            map.put("password", JsonUtil.getStringDefaultEmpty(json, "password"));
        } else if (DataSourceType.CarbonData.getVal().equals(sourceType)) {
            map.put("password", JsonUtil.getStringDefaultEmpty(json, JDBC_PASSWORD));
            map.put("username", JsonUtil.getStringDefaultEmpty(json, JDBC_USERNAME));
            map.put("jdbcUrl", JsonUtil.getStringDefaultEmpty(json, JDBC_URL));
            map.put("partition", map.get(HIVE_PARTITION));
            String table = (String) map.get("table");
            String jdbcUrl = JsonUtil.getStringDefaultEmpty(json, JDBC_URL);
            map.put("path", getPath(DataSourceType.CarbonData, table, json, fillKerberosConfig(sourceId)));
            map.put("database", jdbcUrl.substring(jdbcUrl.lastIndexOf("/") + 1));
            this.setHadoopConfigToReaderAndWriter(map, json);
            map.put("column", getCarbonDataColumnMap(map));
        } else if (DataSourceType.AWS_S3.getVal().equals(sourceType)) {
            map.put("accessKey", JsonUtil.getStringDefaultEmpty(json, "accessKey"));
            map.put("secretKey", JsonUtil.getStringDefaultEmpty(json, "secretKey"));
            map.put("region", JsonUtil.getStringDefaultEmpty(json, "region"));
        } else if (DataSourceType.INCEPTOR.getVal().equals(sourceType)) {
            DataBaseType dataBaseType = DataSourceDataBaseType.getBaseTypeBySourceType(sourceType);
            map.put("type", dataBaseType);
            map.put("password", JsonUtil.getStringDefaultEmpty(json, JDBC_PASSWORD));
            map.put("username", JsonUtil.getStringDefaultEmpty(json, JDBC_USERNAME));
            map.put("jdbcUrl", JsonUtil.getStringDefaultEmpty(json, JDBC_URL));
            map.put("partition", map.get(HIVE_PARTITION));
            map.put("defaultFS", JsonUtil.getStringDefaultEmpty(json, HDFS_DEFAULTFS));
            map.put("hiveMetastoreUris", JsonUtil.getStringDefaultEmpty(json, HIVE_METASTORE_URIS));
            checkLastHadoopConfig(map, json);
            setSftpConfig(sourceId, json, dtuicTenantId, map, "hadoopConfig");
        } else if (DataSourceType.INFLUXDB.getVal().equals(sourceType)) {
            map.put("username", JsonUtil.getStringDefaultEmpty(json, "username"));
            map.put("password", JsonUtil.getStringDefaultEmpty(json, "password"));
            map.put("url", JsonUtil.getStringDefaultEmpty(json, "url"));
        }
    }

    /**
     * defaultFS and hadoopConfig中
     *
     * @param map
     * @param json
     */
    private void setHadoopConfigToReaderAndWriter(Map<String, Object> map, JSONObject json) {
        String hadoopConfig = JsonUtil.getStringDefaultEmpty(json, HADOOP_CONFIG);
        JSONObject config = new JSONObject();
        if (StringUtils.isNotBlank(hadoopConfig)) {
            config = JSON.parseObject(hadoopConfig);
        }
        map.put(HADOOP_CONFIG, config);
        map.put("defaultFS", JsonUtil.getStringDefaultEmpty(json, HDFS_DEFAULTFS));
    }

    /**
     * 获取最新的hadoopConfig 进行替换
     * @param map
     * @param json
     */
    private void checkLastHadoopConfig(Map<String, Object> map, JSONObject json) {
        //拿取最新配置
        String hadoopConfig = JsonUtil.getStringDefaultEmpty(json, HADOOP_CONFIG);
        if (StringUtils.isNotBlank(hadoopConfig)) {
            map.put(HADOOP_CONFIG, JSON.parse(hadoopConfig));
        }
    }

    private List<Map<String, Object>> getCarbonDataColumnMap(Map<String, Object> map) throws Exception {
        List<Map<String, Object>> columnList = (List<Map<String, Object>>) map.get("column");
        //carbonData标准分区需要每个分区字段都有连线
        BatchDataSource source = (BatchDataSource) map.get("source");
        String table = (String) map.get("table");
        Boolean isNativeHive = isNativeHive(source.getId(), table, source.getTenantId());
        if (!isNativeHive) {
            List<String> carbonDataPartCols = getCarbonDataPartCols(source, table);

            List<Object> keyList = columnList.stream().map(colMap -> {
                return colMap.get("key");
            }).collect(Collectors.toList());

            if (!keyList.containsAll(carbonDataPartCols)) {
                throw new RdosDefineException("CarbonData数据源的分区字段必须被选中");
            }
        }

        for (Map<String, Object> colMap : columnList) {
            colMap.put("isPart", colMap.getOrDefault("isPart", false));
        }
        return columnList;
    }

    private List<String> getCarbonDataPartCols(BatchDataSource source, String table) throws Exception {
        List<JSONObject> tableColumn = getTableColumn(source, table, null);
        List<String> partCols = new ArrayList<>();
        for (JSONObject json : tableColumn) {
            Boolean isPart = json.getBoolean("isPart");
            if (BooleanUtils.isTrue(isPart)) {
                partCols.add(JsonUtil.getStringDefaultEmpty(json, KEY));
            }
        }
        return partCols;
    }

    /**
     * 解析数据源连接信息
     *
     * @param map       不允许为空
     * @param taskId
     * @param projectId
     * @param isFilter 是否过滤数据源账号密码信息
     */
    public void setReaderJson(Map<String, Object> map, Long taskId, Long projectId, Long tenantId, boolean isFilter) throws Exception {
        List<Long> sourceIds = new ArrayList<>();
        if (map == null){
            throw new RdosDefineException("传入信息有误");
        }

        if (map != null && !map.containsKey("sourceId")) {
            throw new RdosDefineException(ErrorCode.DATA_SOURCE_NOT_SET);
        }
        Long dataSourceId = MapUtils.getLong(map, "sourceId", 0L);
        BatchDataSource source = getOne(dataSourceId);
        Integer sourceType = source.getType();
        map.put("type",sourceType);
        Long dtuicTenantId = tenantService.getDtuicTenantId(tenantId);
        // 包含 sourceList 为分库分表读取,兼容原来的单表读取逻辑
        if ((DataSourceType.MySQL.getVal().equals(sourceType) || DataSourceType.TiDB.getVal().equals(sourceType)) && map.containsKey("sourceList")) {
            List<Object> sourceList = (List<Object>) map.get("sourceList");
            JSONArray connections = new JSONArray();
            for (Object dataSource : sourceList) {
                Map<String, Object> sourceMap = (Map<String, Object>) dataSource;
                Long sourceId = Long.parseLong(sourceMap.get("sourceId").toString());
                BatchDataSource batchDataSource = getOne(sourceId);

                JSONObject json = JSON.parseObject(batchDataSource.getDataJson());
                JSONObject conn = new JSONObject();
                if (!isFilter) {
                    conn.put("username", JsonUtil.getStringDefaultEmpty(json, JDBC_USERNAME));
                    conn.put("password", JsonUtil.getStringDefaultEmpty(json, JDBC_PASSWORD));
                }
                conn.put("jdbcUrl", Collections.singletonList(JsonUtil.getStringDefaultEmpty(json, JDBC_URL)));

                if (sourceMap.get("tables") instanceof String) {
                    conn.put("table", Collections.singletonList(sourceMap.get("tables")));
                } else {
                    conn.put("table", sourceMap.get("tables"));
                }

                conn.put("type", batchDataSource.getType());
                conn.put("sourceId", sourceId);

                connections.add(conn);
                sourceIds.add(sourceId);

                sourceMap.put("name", batchDataSource.getDataName());
                if (map.get("source") == null) {
                    map.put("source", batchDataSource);
                }
                if (map.get("datasourceType") == null) {
                    map.put("dataSourceType", batchDataSource.getType());
                }
            }

            Map<String, Object> sourceMap = (Map<String, Object>) sourceList.get(0);
            DataBaseType dataBaseType = DataSourceDataBaseType.getBaseTypeBySourceType(sourceType);
            map.put("sourceId", sourceMap.get("sourceId"));
            map.put("name", sourceMap.get("name"));
            map.put("type", dataBaseType);
            map.put("connections", connections);
            processTable(map);
        } else {
            sourceIds.add(dataSourceId);
            Long sourceId = source.getId();
            map.put("source", source);
            map.put("dataSourceType", source.getType());
            JSONObject json = JSON.parseObject(source.getDataJson());
            // 根据jdbc信息 替换map中的信息
            replaceJdbcInfoByDataJsonToMap(map, sourceId, source, dtuicTenantId, json, sourceType);
           if (DataSourceType.Kudu.getVal().equals(sourceType)) {
                syncBuilderFactory.getSyncBuilder(DataSourceType.Kudu.getVal()).setReaderJson(map, json,fillKerberosConfig(sourceId));
                setSftpConfig(sourceId, json, dtuicTenantId, map, "hadoopConfig");
            }
           if (DataSourceType.IMPALA.getVal().equals(sourceType)) {
                syncBuilderFactory.getSyncBuilder(DataSourceType.IMPALA.getVal()).setReaderJson(map, json,fillKerberosConfig(sourceId));
                setSftpConfig(sourceId, json, dtuicTenantId, map, "hadoopConfig");
            }
        }

        // isFilter为true表示过滤数据源信息，移除相关属性
        if (isFilter) {
            map.remove("username");
            map.remove("password");

            //S3数据源不需要移除 accessKey
            if(!DataSourceType.AWS_S3.getVal().equals(sourceType)){
                map.remove("accessKey");
            }
        }

        map.put("sourceIds", sourceIds);

        if (taskId != null) {
            for (Long sourceId : sourceIds) {
                //插入资源和任务的关联关系
                dataSourceTaskRefService.addRef(sourceId, taskId, projectId, tenantId);
            }
        }
    }

    private void setSftpConfig(Long sourceId, JSONObject json, Long dtuicTenantId, Map<String, Object> map, String confKey) {
        setSftpConfig(sourceId, json, dtuicTenantId, map, confKey, true);
    }

    /**
     * 添加ftp地址
     * @param sourceId
     * @param json
     * @param dtuicTenantId
     * @param map
     * @param confKey
     */
    private void setSftpConfig(Long sourceId, JSONObject json, Long dtuicTenantId, Map<String, Object> map, String confKey, boolean downloadKerberos) {
        JSONObject kerberosConfig = json.getJSONObject(KERBEROS_CONFIG);
        if (MapUtils.isNotEmpty(kerberosConfig)) {
            Map<String, String> sftpMap = getSftpMap(dtuicTenantId);
            Map<String, Object> conf = null;
            Object confObj = map.get(confKey);
            if (confObj instanceof String) {
                conf = JSON.parseObject(confObj.toString());
            } else if (confObj instanceof Map) {
                conf = (Map<String, Object>) confObj;
            }
            conf = Optional.ofNullable(conf).orElse(new HashMap<>());
            //flinkx参数
            conf.putAll(kerberosConfig);
            conf.put("sftpConf", sftpMap);
            //替换remotePath 就是ftp上kerberos的相对路径和principalFile
            String remoteDir = sftpMap.get("path") + File.separator + kerberosConfig.getString("kerberosDir");
            String principalFile = conf.getOrDefault("principalFile", "").toString();;
            if (StringUtils.isNotEmpty(principalFile)){
                conf.put("principalFile", getFileName(principalFile));
            }
            conf.put("remoteDir", remoteDir);
            map.put(confKey, conf);

            if (downloadKerberos) {
                //hiveBase中连接数据库需要kerberosConfig
                Map<String, Object> kerberosConfigReplaced = fillKerberosConfig(sourceId);
                map.put("kerberosConfig", kerberosConfigReplaced);
            }

            String krb5Conf = conf.getOrDefault("java.security.krb5.conf", "").toString();
            if (StringUtils.isNotEmpty(krb5Conf)){
                conf.put("java.security.krb5.conf", getFileName(krb5Conf));
            }
            // 开启kerberos认证需要的参数
            conf.put(HadoopConfTool.IS_HADOOP_AUTHORIZATION, "true");
            conf.put(HadoopConfTool.HADOOP_AUTH_TYPE, "kerberos");
        }
    }

    private String getFileName(final String path){
        if (StringUtils.isEmpty(path)){
            return path;
        }
        final String[] split = path.split(File.separator);
        return split[split.length-1];
    }

    /**
     * 获取carbondata数据源中表的hdfs路径
     *
     * @param dataSourceType
     * @param table
     * @param dataJson
     * @return
     */
    public String getPath(DataSourceType dataSourceType, String table, JSONObject dataJson, Map<String, Object> kerberosConfig) {
        Connection conn = null;
        try {
            ISourceDTO sourceDTO = SourceDTOType.getSourceDTO(dataJson, dataSourceType.getVal(), kerberosConfig);
            IClient iClient = ClientCache.getClient(dataSourceType.getVal());
            conn = iClient.getCon(sourceDTO);
            List<Map<String, Object>> mapList = DBUtil.executeQuery(conn, "desc extended " + table,false);
            for (Map<String, Object> map : mapList) {
                String col_name = (String) map.get("col_name");
                if (col_name.contains("Path")) {
                    return ((String) map.get("data_type")).trim();
                }
            }
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            DBUtil.closeDBResources(null, null, conn);
        }
        return null;
    }

    private void processTable(Map<String, Object> map) {
        Object table = map.get("table");
        List<String> tables = new ArrayList<>();
        if (table instanceof String) {
            tables.add(table.toString());
        } else {
            tables.addAll((List<String>) table);
        }

        map.put("table", tables);
    }

    /**
     * @author toutian
     */
    private String getJobText(final Reader reader,
                              final Writer writer,
                              final Setting setting) {

        return new JobTemplate() {
            @Override
            public Reader newReader() {
                return reader;
            }

            @Override
            public Writer newWrite() {
                return writer;
            }

            @Override
            public Setting newSetting() {
                return setting;
            }
        }.toJobJsonString();
    }

    /**
     * 数据同步-向导模式-获得项目下所有数据源
     *
     * @param projectId 项目id
     * @return
     */
    public List<DataSourceVO> list(Long projectId) {
        List<BatchDataSource> list = getDataSourceByProjectId(projectId);
        List<DataSourceVO> vos = new ArrayList<>();
        list.forEach(source -> {
            int count = dataSourceTaskRefService.getSourceRefCount(source.getId());
            DataSourceVO vo = DataSourceVO.toVO(source, count);
            parseModifyUser(vo);
            parseDataJsonForView(vo);
            vos.add(vo);
        });

        return vos;
    }

    /**
     * 获取传入的projectId 下所有的数据源信息
     *
     * @param projectId
     * @return
     */
    public List<BatchDataSource> getDataSourceByProjectId(Long projectId){
        List<BatchDataSourceCenter> dataSourceCenterList = batchDataSourceCenterDao.getInfoIdsByProject(projectId);
        if (CollectionUtils.isEmpty(dataSourceCenterList)){
            return Lists.newArrayList();
        }
        return getSourceListByDataSourceCenter(dataSourceCenterList);
    }

    /**
     * 根据DataSourceCenter 转化为DataSource
     *
     * @param dataSourceCenterList
     * @return
     */
    public List<BatchDataSource> getSourceListByDataSourceCenter(List<BatchDataSourceCenter> dataSourceCenterList){
        List<BatchDataSource> list = new ArrayList<>();
        if(CollectionUtils.isEmpty(dataSourceCenterList)){
            return list;
        }

        Set<Long> infoIdSet = dataSourceCenterList.stream().map(BatchDataSourceCenter::getDtCenterSourceId).collect(Collectors.toSet());
        //去数据源中心获取数据源的详细信息
        List<DsServiceInfoVO> dsInfoListByIdList = apiServiceFacade.getDsInfoListByIdList(new ArrayList<>(infoIdSet));
        if (CollectionUtils.isEmpty(dsInfoListByIdList)) {
            return Lists.newArrayList();
        }

        //将DsServiceInfoDTOList 转换成map
        Map<Long, DsServiceInfoVO> dsServiceInfoDTOMap = dsInfoListByIdList.stream().collect(Collectors.toMap(DsServiceInfoVO::getDataInfoId, Function.identity(), (v1, v2) -> v2));

        //遍历本地的list，然后去根据数据源的id从map中获取数据源连接信息
        //注意，由于数据源中心同一个数据源可以被离线多个项目引入，所以这里本地list可能比数据源的list多，所以一定是遍历本地list
        dataSourceCenterList.forEach(dataSourceCenter -> {
            DsServiceInfoVO dsServiceInfo = dsServiceInfoDTOMap.get(dataSourceCenter.getDtCenterSourceId());
            list.add(convertDsServiceInfoDTOToDataSource(dataSourceCenter, dsServiceInfo));
        });
        return list;
    }

    /**
     * 数据同步-获取表的底层存储信息
     * 目前用于impala 判断底层表是kudu 还是hbase
     *
     * @return
     * @throws SQLException
     */
    public JSONObject tableLocation(Long sourceId, String tableName) {

        BatchDataSource source = getOne(sourceId);

        String dataJson = source.getDataJson();
        JSONObject dataSource = JSON.parseObject(dataJson);
        JSONObject result = new JSONObject();
        if (!DataSourceType.IMPALA.getVal().equals(source.getType())) {
            return result;
        }
        //目前此接口仅用于impala
        Map<String, Object> kerberos = fillKerberosConfig(sourceId);
        ImpalaSyncBuilder impalaSyncBuildHandler = new ImpalaSyncBuilder();
        return impalaSyncBuildHandler.tableLocation(dataSource, tableName,kerberos);
    }

    /**
     * 数据同步-获得数据库中相关的表信息
     *
     * @param projectId 项目id
     * @param sourceId  数据源id
     * @param tenantId 租户id
     * @param schema 查询的schema
     * @param name 模糊查询表名
     * @param isAll 是否获取所有表
     * @param isRead 是否读取类型
     * @return
     * @throws SQLException
     */
    public List<String> tablelist(Long projectId, Long sourceId,Long tenantId,String schema, String name, Boolean isAll, Boolean isRead) {
        List<String> tables = new ArrayList<>();
        BatchDataSource source = getOne(sourceId);
        String dataJson = source.getDataJson();
        JSONObject json = JSON.parseObject(dataJson);
        //返回条数
        Integer limitNum = BooleanUtils.isNotTrue(isAll) ? environmentContext.getTableLimit() : null;
        //查询的db
        String dataSource = schema;

        IClient client = ClientCache.getClient(source.getType());
        ISourceDTO sourceDTO = SourceDTOType.getSourceDTO(json, source.getType(), fillKerberosConfig(source.getId()));
        SqlQueryDTO sqlQueryDTO = SqlQueryDTO.builder().tableNamePattern(name).limit(limitNum).build();
        if (Objects.nonNull(RDBMSSourceType.getByDataSourceType(source.getType()))) {  //RDBMS
            if (DataSourceType.LIBRA.getVal().equals(source.getType())) {
                ProjectEngine projectDb = projectEngineService.getProjectDb(source.getProjectId(), MultiEngineType.LIBRA.getType());
                if (null != projectDb) {
                    dataSource = projectDb.getEngineIdentity();
                }
            }
        }
        sqlQueryDTO.setView(true);
        sqlQueryDTO.setSchema(dataSource);
        //如果是hive类型的数据源  过滤脏数据表 和 临时表
        tables = client.getTableList(sourceDTO, sqlQueryDTO);
        return tables;
    }

    /**
     * 数据同步-获得表中字段与类型信息
     *
     * @param sourceId  数据源id
     * @param tableName 表名
     * @return
     * @throws SQLException
     */
    public List<JSONObject> tablecolumn(Long projectId, Long userId, Long sourceId, String tableName, Boolean isIncludePart, String schema) {

        final BatchDataSource source = this.getOne(sourceId);
        final StringBuffer newTableName = new StringBuffer();
        if (DataSourceType.SQLServer.getVal().equals(source.getType()) && StringUtils.isNotBlank(tableName)){
            if (tableName.indexOf("[") == -1){
                final String[] tableNames = tableName.split("\\.");
                for (final String name : tableNames) {
                    newTableName.append("[").append(name).append("]").append(".");
                }
                tableName = newTableName.substring(0,newTableName.length()-1);
            }
        }
        return getTableColumnIncludePart(source, tableName,isIncludePart, schema);
    }


    public Set<String> getHivePartitions(Long sourceId, String tableName) {

        BatchDataSource source = getOne(sourceId);
        JSONObject json = JSON.parseObject(source.getDataJson());
        Map<String, Object> kerberosConfig = this.fillKerberosConfig(sourceId);

        ISourceDTO sourceDTO = SourceDTOType.getSourceDTO(json, source.getType(), kerberosConfig);
        IClient iClient = ClientCache.getClient(source.getType());
        List<ColumnMetaDTO> partitionColumn = iClient.getPartitionColumn(sourceDTO, SqlQueryDTO.builder().tableName(tableName).build());

        Set<String> partitionNameSet = Sets.newHashSet();
        //格式化分区信息 与hive保持一致
        if (CollectionUtils.isNotEmpty(partitionColumn)){
            StringJoiner tempJoiner = new StringJoiner("=/","","=");
            for (ColumnMetaDTO column : partitionColumn) {
                tempJoiner.add(column.getKey());
            }
            partitionNameSet.add(tempJoiner.toString());
        }
        return partitionNameSet;
    }

    /**
     * 获取可以作为增量标识的字段
     */
    public List<JSONObject> getIncreColumn(Long sourceId, Object table, String schema) {
        List<JSONObject> increColumn = new ArrayList<>();

        String tableName;
        if (table instanceof String) {
            tableName = String.valueOf(table);
        } else if (table instanceof List) {
            List tableList = (List) table;
            if (CollectionUtils.isEmpty(tableList)) {
                return new ArrayList<>();
            }
            tableName = String.valueOf(tableList.get(0));
        } else {
            throw new RdosDefineException(ErrorCode.INVALID_PARAMETERS);
        }

        BatchDataSource source = getOne(sourceId);
        List<JSONObject> allColumn = getTableColumn(source, tableName, schema);
        for (JSONObject col : allColumn) {
            if (ColumnType.isIncreType(col.getString("type"))) {
                increColumn.add(col);
            } else if (DataSourceType.Oracle.getVal().equals(source.getType())) {
                increColumn.add(col);
            } else if (DataSourceType.SQLServer.getVal().equals(source.getType())
                    && ColumnType.NVARCHAR.equals(ColumnType.fromString(col.getString("key")))) {
                increColumn.add(col);
            }
        }

        return increColumn;
    }

    /**
     * 获取表所属字段 不包括分区字段
     * @param source
     * @param tableName
     * @return
     * @throws Exception
     */
    private List<JSONObject> getTableColumn(BatchDataSource source, String tableName, String schema) {
        try {
            return this.getTableColumnIncludePart(source,tableName,false, schema);
        } catch (final Exception e) {
            throw new RdosDefineException("获取表字段异常", e);
        }

    }

    /**
     * 查询表所属字段 可以选择是否需要分区字段
     * @param source
     * @param tableName
     * @param part 是否需要分区字段
     * @return
     * @throws Exception
     */
    private List<JSONObject> getTableColumnIncludePart(BatchDataSource source, String tableName, Boolean part, String schema)  {
        try {
            if (source == null) {
                throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_DATA_SOURCE);
            }
            if (part ==null){
                part = false;
            }
            JSONObject dataJson = JSONObject.parseObject(source.getDataJson());
            Map<String, Object> kerberosConfig = fillKerberosConfig(source.getId());
            IClient iClient = ClientCache.getClient(source.getType());
            SqlQueryDTO sqlQueryDTO = SqlQueryDTO.builder()
                    .tableName(tableName)
                    .schema(schema)
                    .filterPartitionColumns(part)
                    .build();
            ISourceDTO iSourceDTO = SourceDTOType.getSourceDTO(dataJson, source.getType(), kerberosConfig);
            List<ColumnMetaDTO> columnMetaData = iClient.getColumnMetaData(iSourceDTO, sqlQueryDTO);
            List<JSONObject> list = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(columnMetaData)) {
                for (ColumnMetaDTO columnMetaDTO : columnMetaData) {
                    JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(columnMetaDTO));
                    jsonObject.put("isPart",columnMetaDTO.getPart());
                    list.add(jsonObject);
                }
            }
            return list;
        } catch (DtCenterDefException e) {
            throw e;
        } catch (Exception e) {
            throw new RdosDefineException(ErrorCode.GET_COLUMN_ERROR, e);
        }

    }

    /**
     * 数据同步-获得预览数据，默认展示3条
     *
     * @param projectId 项目id
     * @param userId    用户id
     * @param sourceId  数据源id
     * @param tableName 表名
     * @return
     * @author toutian
     */
    public JSONObject preview(Long projectId, Long userId, Long sourceId, String tableName, String partition,
                              Long tenantId, Long dtuicTenantId, Boolean isRoot, String schema) {

        BatchDataSource source = getOne(sourceId);
        StringBuffer newTableName = new StringBuffer();
        if (DataSourceType.SQLServer.getVal().equals(source.getType()) && StringUtils.isNotBlank(tableName)){
            if (tableName.indexOf("[") == -1){
                final String[] tableNames = tableName.split("\\.");
                for (final String name : tableNames) {
                    newTableName.append("[").append(name).append("]").append(".");
                }
                tableName = newTableName.substring(0,newTableName.length()-1);
            }
        }
        String dataJson = source.getDataJson();
        JSONObject json = JSON.parseObject(dataJson);
        //获取字段信息
        List<String> columnList = new ArrayList<String>();
        //获取数据
        List<List<String>> dataList = new ArrayList<List<String>>();
        try {
            Map<String, Object> kerberosConfig = fillKerberosConfig(source.getId());
            List<JSONObject> columnJson = getTableColumn(source, tableName, schema);
            if (CollectionUtils.isNotEmpty(columnJson)) {
                for (JSONObject columnMetaDTO : columnJson) {
                    columnList.add(columnMetaDTO.getString("key"));
                }
            }
            IClient iClient = ClientCache.getClient(source.getType());
            ISourceDTO iSourceDTO = SourceDTOType.getSourceDTO(json, source.getType(), kerberosConfig);
            SqlQueryDTO sqlQueryDTO = SqlQueryDTO.builder().schema(schema).tableName(tableName).previewNum(3).build();
            dataList = iClient.getPreview(iSourceDTO, sqlQueryDTO);
            if (DataSourceType.getRDBMS().contains(source.getType())) {
                //因为会把字段名也会返回 所以要去除第一行
                dataList = dataList.subList(1, dataList.size());
            }
        } catch (Exception e) {
            logger.error("datasource preview end with error.", e);
            throw new RdosDefineException(String.format("%s获取预览数据失败", source.getDataName()), e);
        }

        JSONObject preview = new JSONObject(2);
        preview.put("columnList", columnList);
        preview.put("dataList", dataList);

        return preview;
    }

    /**
     * @param columnList
     * @param dataList
     * @param dtuicTenantId
     * @param jdbcUrl
     * @param userId
     * @param tableName
     * @param tenantId
     */
    private void checkPermissionColumn(List<String> columnList, List<List<String>> dataList, Long dtuicTenantId, String jdbcUrl, Long userId, String tableName, Long tenantId, Integer tableType) {
    }

    /**
     * 数据源-得到某一数据源详情
     *
     * @author toutian
     */
    public DataSourceVO getBySourceId(Long sourceId) {

        BatchDataSource source = getOne(sourceId);
        int count = dataSourceTaskRefService.getSourceRefCount(source.getId());
        DataSourceVO vo = DataSourceVO.toVO(source, count);

        parseModifyUser(vo);
        parseDataJson(vo);
        return vo;
    }

    private void parseModifyUser(DataSourceVO vo) {
        long modifyUserId = vo.getModifyUserId();
        User modifyUser = userService.getById(modifyUserId);
        vo.setModifyUser(modifyUser);
    }

    /**
     * 得到数据源类型
     *
     * @author toutian
     */
    public List<DataSourceTypeVO> getTypes() {
        return dictService.getDictByType(DictType.DATA_SOURCE.getValue()).stream().map(dict -> {
            DataSourceType sourceType = DataSourceType.getSourceType(dict.getDictValue());
            return DataSourceTypeVO.toVO(sourceType);
        }).sorted(Comparator.comparingInt(DataSourceTypeVO::getOrder)).collect(Collectors.toList());
    }

    /**
     * 数据源 - 条件查询
     *
     * @author toutian
     */
    public PageResult<List<DataSourceVO>> pageQuery(List<Integer> types, String name, Long tenantId, Long projectId, Integer currentPage, Integer pageSize) {
        return new PageResult();

    }

    public List<DataSourceVO> getAnalysisSource(Long tenantId, Long projectId) {
        PageResult<List<DataSourceVO>> result = pageQuery(Lists.newArrayList(DataSourceType.CarbonData.getVal()), null, tenantId, projectId, 1, 200);
        return result.getData();
    }


    /**
     * 根据dataSourceType获取EComponentType
     * @return
     */
    public Integer getEComponentTypeByDataSourceType(Integer dataSourceType){
        if(DataSourceType.SparkThrift2_1.getVal().equals(dataSourceType)){
            return EComponentType.SPARK_THRIFT.getTypeCode();
        }else if(DataSourceType.IMPALA.getVal().equals(dataSourceType)){
            return EComponentType.IMPALA_SQL.getTypeCode();
        }else if(DataSourceType.HIVE1X.getVal().equals(dataSourceType) || DataSourceType.HIVE3X.getVal().equals(dataSourceType) || DataSourceType.HIVE.getVal().equals(dataSourceType)){
            return EComponentType.HIVE_SERVER.getTypeCode();
        }
        throw new RdosDefineException("not find 'Hadoop' Component!");
    }

    /**
     * 关联数据源
     */
    @Transactional(rollbackFor = Exception.class)
    public void linkDataSource(Long tenantId, Long projectId, Long sourceId,Long linkSourceId) {

        BatchDataSource source = getOne(sourceId);
        BatchDataSource linkSource = getOne(linkSourceId);

        if (!source.getType().equals(linkSource.getType())) {
            throw new RdosDefineException("数据源类型不一致");
        }
    }

    /**
     * 下载检查kerberos配置
     *
     * @param sourceId
     * @return 返回该数据源的完整kerberos配置
     */
    public Map<String, Object> fillKerberosConfig(Long sourceId) {
        BatchDataSource source = getOne(sourceId);
        Long dtuicTenantId = tenantService.getDtuicTenantId(source.getTenantId());
        JSONObject dataJson = JSON.parseObject(source.getDataJson());
        JSONObject kerberosConfig = dataJson.getJSONObject(KERBEROS_CONFIG);
        if (MapUtils.isNotEmpty(kerberosConfig)) {
            String localKerberosConf = getLocalKerberosConf(sourceId);
            downloadKerberosFromSftp(kerberosConfig.getString(KERBEROS_DIR), localKerberosConf, dtuicTenantId, dataJson.getTimestamp(KERBEROS_FILE_TIMESTAMP));
            return handleKerberos(source.getType(), kerberosConfig, localKerberosConf);
        }
        return new HashMap<>();
    }

    /**
     * 对外展示的接口 不展示source的密码
     *
     * @param source
     */
    public JSONObject parseDataJsonForView(DataSourceVO source) {
        parseDataJson(source);
        JSONObject dataJson = source.getDataJson();
        dataJson.remove(JDBC_PASSWORD);
        dataJson.remove("password");
        dataJson.remove("pass");
        if (source.getType().equals(DataSourceType.CarbonData.getVal())) {
            //默认配置不显示
            String config = (String) dataJson.get(hdfsCustomConfig);
            if (CarbonDataConfigType.DEFAULT.getName().equals(config)) {
                dataJson.remove(HDFS_DEFAULTFS);
                dataJson.remove(HADOOP_CONFIG);
            }
        }
        if (DataSourceType.AWS_S3.getVal().equals(source.getType())) {
            dataJson.remove(SECRET_KEY);
        }
        return dataJson;
    }

    /**
     * @author toutian
     */
    private void parseDataJson(DataSourceVO source) {
        JSONObject json = source.getDataJson();
        if (Objects.nonNull(RDBMSSourceType.getByDataSourceType(source.getType()))) {
            json.put("password", JsonUtil.getStringDefaultEmpty(json, JDBC_PASSWORD));
            json.put("username", JsonUtil.getStringDefaultEmpty(json, JDBC_USERNAME));
            json.put("jdbcUrl", JsonUtil.getStringDefaultEmpty(json, JDBC_URL));
        } else if (DataSourceType.HBASE.getVal().equals(source.getType())) {
            JSONObject hbaseConfig = json.getJSONObject(HBASE_CONFIG);
            if (null != hbaseConfig) {
                json.put("hbase_quorum", hbaseConfig.getString("hbase.zookeeper.quorum"));
                hbaseConfig.remove("hbase.zookeeper.quorum");
                JSONObject hbaseOtherConfig = new JSONObject();
                for (String key : hbaseConfig.keySet()) {
                    hbaseOtherConfig.put(key, hbaseConfig.getString(key));
                }
                json.put("hbase_other", hbaseOtherConfig);
            }
        }
        source.setDataJson(json);
    }

    /**
     * 根据id获取数据源，如果为空则抛出异常
     * @param sourceId
     * @return
     */
    public BatchDataSource getOne(Long sourceId) {
        BatchDataSourceCenter centerSource = batchDataSourceCenterDao.getSourceCenterByCenterId(sourceId);
        if(centerSource == null){
            throw new RdosDefineException("sourceId=" + sourceId + ":" + ErrorCode.CAN_NOT_FIND_DATA_SOURCE.getDescription());
        }
        DsServiceInfoVO dataSourceInfo = apiServiceFacade.getDsInfoById(centerSource.getDtCenterSourceId());
        if(dataSourceInfo == null){
            throw new RdosDefineException("sourceId=" + sourceId + ":" + ErrorCode.CAN_NOT_FIND_DATA_SOURCE.getDescription());
        }

        BatchDataSource source = convertDsServiceInfoDTOToDataSource(centerSource, dataSourceInfo);

        return source;
    }

    /**
     * DsServiceInfoDTO 转化为BatchDataSource
     *
     * @param centerSource
     * @param dataSourceInfo
     * @return
     */
    private BatchDataSource convertDsServiceInfoDTOToDataSource(BatchDataSourceCenter centerSource, DsServiceInfoVO dataSourceInfo) {
        BatchDataSource source = new BatchDataSource();
        BeanUtils.copyProperties(centerSource, source);
        source.setDataJson(dataSourceInfo.getDataJson());
        source.setType(dataSourceInfo.getType());
        source.setLinkState(dataSourceInfo.getStatus());
        source.setDataName(dataSourceInfo.getDataName());
        source.setDataDesc(dataSourceInfo.getDataDesc());
        source.setCreateUserId(centerSource.getCreateUserId());
        source.setModifyUserId(centerSource.getModifyUserId());

        Long dtuicTenantId = tenantService.getDtuicTenantId(source.getTenantId());
        source.setDtuicTenantId(dtuicTenantId);

        Integer sourceRefCount = dataSourceTaskRefService.getSourceRefCount(centerSource.getId());

        if(sourceRefCount > 0 ){
            source.setActive(1);
        } else {
            source.setActive(0);
        }
        return source;
    }

    /**
     * DsServiceInfoDTO 转化为BatchDataSource
     *
     * @param centerSource
     * @param dataSourceInfo
     * @return
     */
    private BatchDataSource convertDsServiceListDTOToDataSource(BatchDataSourceCenter centerSource, DsServiceListVO dataSourceInfo) {
        DsServiceInfoVO dsServiceInfoDTO = new DsServiceInfoVO();
        BeanUtils.copyProperties(dataSourceInfo, dsServiceInfoDTO);
        return convertDsServiceInfoDTOToDataSource(centerSource, dsServiceInfoDTO);
    }
    /**
     * 根据sourceId 获取 对应的ISourceDTO
     *
     * @param sourceId
     * @return
     */
    public ISourceDTO getSourceDTOBySourcdId(Long sourceId) {
        BatchDataSource one = getOne(sourceId);
        Map<String, Object> map = fillKerberosConfig(sourceId);
        JSONObject dataJson = JSONObject.parseObject(one.getDataJson());
        ISourceDTO sourceDTO = SourceDTOType.getSourceDTO(dataJson, one.getType(), map);
        return sourceDTO;
    }


    /**
     * 获取使用该数据源的任务的列表
     *
     * @param sourceId
     * @param pageSize
     * @param currentPage
     * @return
     */
    public PageResult<List<JSONObject> > getSourceTaskRef(Long sourceId, Integer pageSize, Integer currentPage, String taskName) {
        Long dataSourceId = ParamsCheck.checkNotNull(sourceId);
        Integer queryPageSize = pageSize == null ? 10 : pageSize;
        List<JSONObject> data = new ArrayList<>();
        Integer count = batchDataSourceTaskRefService.countBySourceId(dataSourceId, taskName);
        BatchDataSourceTaskDto queryDto = new BatchDataSourceTaskDto();
        queryDto.setSourceId(sourceId);
        queryDto.setTaskName(taskName);
        PageQuery<BatchDataSourceTaskDto> pageQuery = new PageQuery(queryDto);
        pageQuery.setPage(currentPage);
        pageQuery.setPageSize(queryPageSize);
        List<BatchTask> batchTasks = batchDataSourceTaskRefService.pageQueryBySourceId(pageQuery);
        if (CollectionUtils.isNotEmpty(batchTasks)) {
            for (BatchTask batchTask : batchTasks) {
                JSONObject item = new JSONObject();
                item.put("id", batchTask.getId());
                item.put("name", batchTask.getName());
                data.add(item);
            }
        }
        return new PageResult(data, count, new PageQuery<>(currentPage, pageSize));
    }

    public String setJobDataSourceInfo(String jobStr, Long dtUicTenentId, Integer createModel) {
        JSONObject job = JSONObject.parseObject(jobStr);
        JSONObject jobContent = job.getJSONObject("job");
        JSONObject content = jobContent.getJSONArray("content").getJSONObject(0);
        setPluginDataSourceInfo(content.getJSONObject("reader"), dtUicTenentId, createModel);
        setPluginDataSourceInfo(content.getJSONObject("writer"), dtUicTenentId, createModel);
        return job.toJSONString();
    }

    /**
     * 获取hadoopconfig最新配置
     * @param dtUicTenantId
     * @return
     */
    private String getConsoleHadoopConfig(Long dtUicTenantId){
        if(null == dtUicTenantId){
            return null;
        }
        String enginePluginInfo = Engine2DTOService.getEnginePluginInfo(dtUicTenantId, MultiEngineType.HADOOP.getType());
        if(StringUtils.isBlank(enginePluginInfo)){
            return null;
        }
        JSONObject jsonObject = JSON.parseObject(enginePluginInfo);
        return jsonObject.getString(EComponentType.HDFS.getTypeCode() + "");
    }

    /**
     * 根据模式 判断是否要覆盖数据源信息
     * 脚本模式 空缺了再覆盖  向导模式 默认覆盖
     */
    private void replaceDataSourceInfoByCreateModel(JSONObject jdbcInfo, String key, Object values, Integer createModel){
        Boolean isReplace = TaskCreateModelType.TEMPLATE.getType().equals(createModel) && jdbcInfo.containsKey(key);
        if (isReplace) {
            return;
        }
        jdbcInfo.put(key,values);
    }

    private void setPluginDataSourceInfo(JSONObject plugin, Long dtUicTenentId, Integer createModel) {
        String pluginName = plugin.getString("name");
        JSONObject param = plugin.getJSONObject("parameter");
        if (PluginName.MySQLD_R.equals(pluginName)) {
            JSONArray connections = param.getJSONArray("connection");
            for (int i = 0; i < connections.size(); i++) {
                JSONObject conn = connections.getJSONObject(i);
                if (!conn.containsKey("sourceId")) {
                    continue;
                }

                BatchDataSource source = getOne(conn.getLong("sourceId"));
                JSONObject json = JSONObject.parseObject(source.getDataJson());
                replaceDataSourceInfoByCreateModel(conn,"username",JsonUtil.getStringDefaultEmpty(json, JDBC_USERNAME),createModel);
                replaceDataSourceInfoByCreateModel(conn,"password",JsonUtil.getStringDefaultEmpty(json, JDBC_PASSWORD),createModel);
                replaceDataSourceInfoByCreateModel(conn,"jdbcUrl",Arrays.asList(JsonUtil.getStringDefaultEmpty(json, JDBC_URL)),createModel);
            }
        } else {
            if (!param.containsKey("sourceIds")) {
                return;
            }

            List<Long> sourceIds = param.getJSONArray("sourceIds").toJavaList(Long.class);
            if (CollectionUtils.isEmpty(sourceIds)) {
                return;
            }

            BatchDataSource source = getOne(sourceIds.get(0));

            JSONObject json = JSON.parseObject(source.getDataJson());
            Integer sourceType = source.getType();

            if (Objects.nonNull(RDBMSSourceType.getByDataSourceType(sourceType))
                    && !DataSourceType.HIVE.getVal().equals(sourceType)
                    && !DataSourceType.HIVE3X.getVal().equals(sourceType)
                    && !DataSourceType.HIVE1X.getVal().equals(sourceType)
                    && !DataSourceType.IMPALA.getVal().equals(sourceType)
                    && !DataSourceType.SparkThrift2_1.getVal().equals(sourceType)
                    && !DataSourceType.INCEPTOR.getVal().equals(sourceType)) {
                replaceDataSourceInfoByCreateModel(param,"username",JsonUtil.getStringDefaultEmpty(json, JDBC_USERNAME),createModel);
                replaceDataSourceInfoByCreateModel(param,"password",JsonUtil.getStringDefaultEmpty(json, JDBC_PASSWORD),createModel);
                JSONObject conn = param.getJSONArray("connection").getJSONObject(0);
                if (conn.get("jdbcUrl") instanceof String) {
                    replaceDataSourceInfoByCreateModel(conn,"jdbcUrl",JsonUtil.getStringDefaultEmpty(json, JDBC_URL),createModel);
                } else {
                    replaceDataSourceInfoByCreateModel(conn,"jdbcUrl",Arrays.asList(JsonUtil.getStringDefaultEmpty(json, JDBC_URL)),createModel);
                }
            } else if (DataSourceType.HIVE.getVal().equals(sourceType) || DataSourceType.HDFS.getVal().equals(sourceType)
                    || DataSourceType.HIVE1X.getVal().equals(sourceType) || DataSourceType.HIVE3X.getVal().equals(sourceType) || DataSourceType.SparkThrift2_1.getVal().equals(sourceType)) {
                if (DataSourceType.HIVE.getVal().equals(sourceType) || DataSourceType.HIVE3X.getVal().equals(sourceType) || DataSourceType.HIVE1X.getVal().equals(sourceType) || DataSourceType.SparkThrift2_1.getVal().equals(sourceType)) {
                    if (param.containsKey("connection")) {
                        JSONObject conn = param.getJSONArray("connection").getJSONObject(0);
                        replaceDataSourceInfoByCreateModel(conn,JDBC_URL, JsonUtil.getStringDefaultEmpty(json, JDBC_URL),createModel);
                    }
                }
                //非meta数据源从高可用配置中取hadoopConf
                if (0 == source.getIsDefault()){
                    replaceDataSourceInfoByCreateModel(param,"defaultFS",JsonUtil.getStringDefaultEmpty(json, HDFS_DEFAULTFS),createModel);
                    String hadoopConfig = JsonUtil.getStringDefaultEmpty(json, HADOOP_CONFIG);
                    if (StringUtils.isNotBlank(hadoopConfig)) {
                        replaceDataSourceInfoByCreateModel(param,HADOOP_CONFIG,JSONObject.parse(hadoopConfig),createModel);
                    }
                }else {
                    //meta数据源从console取配置
                    //拿取最新配置
                    String consoleHadoopConfig = this.getConsoleHadoopConfig(dtUicTenentId);
                    if (StringUtils.isNotBlank(consoleHadoopConfig)) {
                        //替换新path 页面运行fix
                        JSONArray connections = param.getJSONArray("connection");
                        if ((DataSourceType.HIVE.getVal().equals(sourceType) || DataSourceType.HIVE1X.getVal().equals(sourceType) || DataSourceType.HIVE3X.getVal().equals(sourceType) || DataSourceType.SparkThrift2_1.getVal().equals(sourceType)) && Objects.nonNull(connections)){
                            JSONObject conn = connections.getJSONObject(0);
                            String hiveTable = conn.getJSONArray("table").get(0).toString();
                            Map<String, Object> kerberosConfig = fillKerberosConfig(source.getId());
                            String hiveTablePath = getHiveTablePath(sourceType, hiveTable, json, kerberosConfig);
                            if (StringUtils.isNotEmpty(hiveTablePath)){
                                replaceDataSourceInfoByCreateModel(param,"path", hiveTablePath.trim(), createModel);
                            }
                        }
                        replaceDataSourceInfoByCreateModel(param,HADOOP_CONFIG,JSONObject.parse(consoleHadoopConfig),createModel);
                        JSONObject hadoopConfJson = JSONObject.parseObject(consoleHadoopConfig);
                        String defaultFs = JsonUtil.getStringDefaultEmpty(hadoopConfJson, "fs.defaultFS");
                        //替换defaultFs
                        replaceDataSourceInfoByCreateModel(param,"defaultFS",defaultFs,createModel);
                    } else {
                        String hadoopConfig = JsonUtil.getStringDefaultEmpty(json, HADOOP_CONFIG);
                        if (StringUtils.isNotBlank(hadoopConfig)) {
                            replaceDataSourceInfoByCreateModel(param, HADOOP_CONFIG, JSONObject.parse(hadoopConfig), createModel);
                        }
                    }
                }
                setSftpConfig(source.getId(), json, dtUicTenentId, param, HADOOP_CONFIG, false);
            } else if (DataSourceType.HBASE.getVal().equals(sourceType)) {
                String jsonStr = json.getString(HBASE_CONFIG);
                Map jsonMap = new HashMap();
                if (StringUtils.isNotEmpty(jsonStr)){
                    try {
                        jsonMap = objectMapper.readValue(jsonStr,Map.class);
                    } catch (IOException e) {
                        logger.error("", e);
                    }
                }
                replaceDataSourceInfoByCreateModel(param,"hbaseConfig",jsonMap,createModel);
                if (TaskCreateModelType.GUIDE.getType().equals(createModel)) {
                    setSftpConfig(source.getId(), json, dtUicTenentId, param, "hbaseConfig");
                }
            } else if (DataSourceType.FTP.getVal().equals(sourceType)) {
                if (json != null){
                    json.entrySet().forEach(bean->{
                        replaceDataSourceInfoByCreateModel(param,bean.getKey(),bean.getValue(),createModel);
                    });
                }
            } else if (DataSourceType.MAXCOMPUTE.getVal().equals(sourceType)) {
                replaceDataSourceInfoByCreateModel(param,"accessId",json.get("accessId"),createModel);
                replaceDataSourceInfoByCreateModel(param,"accessKey",json.get("accessKey"),createModel);
                replaceDataSourceInfoByCreateModel(param,"project",json.get("project"),createModel);
                replaceDataSourceInfoByCreateModel(param,"endPoint",json.get("endPoint"),createModel);
            } else if ((DataSourceType.ES.getVal().equals(sourceType))) {
                replaceDataSourceInfoByCreateModel(param,"address",json.get("address"),createModel);
            } else if (DataSourceType.REDIS.getVal().equals(sourceType)) {
                replaceDataSourceInfoByCreateModel(param,"hostPort",JsonUtil.getStringDefaultEmpty(json, "hostPort"),createModel);
                replaceDataSourceInfoByCreateModel(param,"database",json.getIntValue("database"),createModel);
                replaceDataSourceInfoByCreateModel(param,"password",JsonUtil.getStringDefaultEmpty(json, "password"),createModel);
            } else if (DataSourceType.MONGODB.getVal().equals(sourceType)) {
                replaceDataSourceInfoByCreateModel(param,JDBC_HOSTPORTS,JsonUtil.getStringDefaultEmpty(json, JDBC_HOSTPORTS),createModel);
                replaceDataSourceInfoByCreateModel(param,"username",JsonUtil.getStringDefaultEmpty(json, "username"),createModel);
                replaceDataSourceInfoByCreateModel(param,"database",JsonUtil.getStringDefaultEmpty(json, "database"),createModel);
                replaceDataSourceInfoByCreateModel(param,"password",JsonUtil.getStringDefaultEmpty(json, "password"),createModel);
            } else if (DataSourceType.Kudu.getVal().equals(sourceType)) {
                replaceDataSourceInfoByCreateModel(param,"masterAddresses",JsonUtil.getStringDefaultEmpty(json, JDBC_HOSTPORTS),createModel);
                replaceDataSourceInfoByCreateModel(param,"others",JsonUtil.getStringDefaultEmpty(json, "others"),createModel);
            } else if (DataSourceType.IMPALA.getVal().equals(sourceType)) {
                String tableLocation =  param.getString(TableLocationType.key());
                replaceDataSourceInfoByCreateModel(param,"dataSourceType", DataSourceType.IMPALA.getVal(),createModel);
                String hadoopConfig = JsonUtil.getStringDefaultEmpty(json, HADOOP_CONFIG);
                if (StringUtils.isNotBlank(hadoopConfig)) {
                    replaceDataSourceInfoByCreateModel(param,HADOOP_CONFIG,JSONObject.parse(hadoopConfig),createModel);
                }
                if (TableLocationType.HIVE.getValue().equals(tableLocation)) {
                    replaceDataSourceInfoByCreateModel(param,"username",JsonUtil.getStringDefaultEmpty(json, JDBC_USERNAME),createModel);
                    replaceDataSourceInfoByCreateModel(param,"password",JsonUtil.getStringDefaultEmpty(json, JDBC_PASSWORD),createModel);
                    replaceDataSourceInfoByCreateModel(param,"defaultFS",JsonUtil.getStringDefaultEmpty(json, HDFS_DEFAULTFS),createModel);
                    if (param.containsKey("connection")) {
                        JSONObject conn = param.getJSONArray("connection").getJSONObject(0);
                        replaceDataSourceInfoByCreateModel(conn,"jdbcUrl",JsonUtil.getStringDefaultEmpty(json, JDBC_URL),createModel);
                    }
                }
            } else if (DataSourceType.INCEPTOR.getVal().equals(sourceType)) {
                replaceInceptorDataSource(param, json, createModel, source, dtUicTenentId);
            } else if (DataSourceType.INFLUXDB.getVal().equals(sourceType)) {
                replaceDataSourceInfoByCreateModel(param, "username", JsonUtil.getStringDefaultEmpty(json, "username"), createModel);
                replaceDataSourceInfoByCreateModel(param, "password", JsonUtil.getStringDefaultEmpty(json, "password"), createModel);
                if (param.containsKey("connection")) {
                    JSONObject conn = param.getJSONArray("connection").getJSONObject(0);
                    String url = JsonUtil.getStringDefaultEmpty(json, "url");
                    replaceDataSourceInfoByCreateModel(conn, "url", Lists.newArrayList(url), createModel);
                    replaceDataSourceInfoByCreateModel(conn, "measurement", conn.getJSONArray("table"), createModel);
                    replaceDataSourceInfoByCreateModel(conn, "database", conn.getString("schema"), createModel);

                }
            }
        }
    }

    /**
     * 替换Inceptor 相关的数据源信息
     *
     * @param param
     * @param json
     * @param createModel
     * @param source
     * @param dtUicTenentId
     */
    public void replaceInceptorDataSource(JSONObject param, JSONObject json, Integer createModel, BatchDataSource source,
                                          Long dtUicTenentId){
        if (param.containsKey("connection")) {
            JSONObject conn = param.getJSONArray("connection").getJSONObject(0);
            replaceDataSourceInfoByCreateModel(conn,"jdbcUrl",JsonUtil.getStringDefaultEmpty(json, JDBC_URL),createModel);
        }

        replaceDataSourceInfoByCreateModel(param,HDFS_DEFAULTFS,JsonUtil.getStringDefaultEmpty(json, HDFS_DEFAULTFS),createModel);
        replaceDataSourceInfoByCreateModel(param,HIVE_METASTORE_URIS,JsonUtil.getStringDefaultEmpty(json, HIVE_METASTORE_URIS),createModel);
        String hadoopConfig = JsonUtil.getStringDefaultEmpty(json, HADOOP_CONFIG);
        JSONObject hadoopConfigJson = new JSONObject();
        if (StringUtils.isNotBlank(hadoopConfig)) {
            hadoopConfigJson.putAll(JSONObject.parseObject(hadoopConfig));
        }
        hadoopConfigJson.put(HIVE_METASTORE_URIS, JsonUtil.getStringDefaultEmpty(json, HIVE_METASTORE_URIS));
        replaceDataSourceInfoByCreateModel(param,HADOOP_CONFIG, hadoopConfigJson, createModel);

        // 替换表相关的信息
        JSONArray connections = param.getJSONArray("connection");
        JSONObject conn = connections.getJSONObject(0);
        String hiveTableName = conn.getJSONArray("table").get(0).toString();
        Map<String, Object> kerberosConfig = fillKerberosConfig(source.getId());
        com.dtstack.dtcenter.loader.dto.Table tableInfo = getTableInfo(DataSourceType.INCEPTOR.getVal(), hiveTableName, json, kerberosConfig);

        replaceDataSourceInfoByCreateModel(param,"path", tableInfo.getPath().trim(), createModel);
        replaceDataSourceInfoByCreateModel(param,"schema", tableInfo.getDb(), createModel);
        replaceDataSourceInfoByCreateModel(param,"table", hiveTableName, createModel);
        replaceDataSourceInfoByCreateModel(param,"isTransaction", tableInfo.getIsTransTable(), createModel);

        setSftpConfig(source.getId(), json, dtUicTenentId, param, HADOOP_CONFIG, false);
    }

    /**
     * 获取table location
     *
     * @param sourceType
     * @param table
     * @param dataJson
     * @param kerberosConfig
     * @return
     */
    private String getHiveTablePath(Integer sourceType, String table, JSONObject dataJson, Map<String, Object> kerberosConfig) {
        com.dtstack.dtcenter.loader.dto.Table tableInfo = getTableInfo(sourceType, table, dataJson, kerberosConfig);
        return tableInfo.getPath();
    }

    /**
     * 获取表信息
     *
     * @param sourceType
     * @param table
     * @param dataJson
     * @param kerberosConfig
     * @return
     */
    private  com.dtstack.dtcenter.loader.dto.Table  getTableInfo(Integer sourceType, String table, JSONObject dataJson, Map<String, Object> kerberosConfig){
        IClient client = ClientCache.getClient(sourceType);
        ISourceDTO sourceDTO = SourceDTOType.getSourceDTO(dataJson, sourceType, kerberosConfig);

        com.dtstack.dtcenter.loader.dto.Table tableInfo = client.getTable(sourceDTO, SqlQueryDTO.builder().tableName(table).build());
        return tableInfo;
    }

    /**
     * 返回切分键需要的列名
     * <p>
     * 只支持关系型数据库 mysql\oracle\sqlserver\postgresql  的整型数据类型
     * 也不支持其他数据库。
     * 如果指定了不支持的类型，则忽略切分键功能，使用单通道进行同步。
     *
     * @param projectId
     * @param userId
     * @param sourceId
     * @param tableName
     * @return
     */
    public Set<JSONObject> columnForSyncopate(Long projectId, Long userId, Long sourceId, String tableName, String schema) {

        BatchDataSource source = getOne(sourceId);
        if (Objects.isNull(RDBMSSourceType.getByDataSourceType(source.getType())) && !DataSourceType.INFLUXDB.getVal().equals(source.getType())) {
            logger.error("切分键只支关系型数据库");
            throw new RdosDefineException("切分键只支持关系型数据库");
        }
        if (StringUtils.isEmpty(tableName)) {
            return new HashSet<>();
        }
        final StringBuffer newTableName = new StringBuffer();
        if (DataSourceType.SQLServer.getVal().equals(source.getType()) && StringUtils.isNotBlank(tableName)){
            if (tableName.indexOf("[") == -1){
                final String[] tableNames = tableName.split("\\.");
                for (final String name : tableNames) {
                    newTableName.append("[").append(name).append("]").append(".");
                }
                tableName = newTableName.substring(0,newTableName.length()-1);
            }
        }
        final List<JSONObject> tablecolumn = this.getTableColumn(source, tableName, schema);
        if (CollectionUtils.isNotEmpty(tablecolumn)) {
            List<String> numbers;
            if (DataSourceType.MySQL.getVal().equals(source.getType()) || DataSourceType.Polardb_For_MySQL.getVal().equals(source.getType()) || DataSourceType.TiDB.getVal().equals(source.getType())) {
                numbers = MYSQL_NUMBERS;
            } else if (DataSourceType.Oracle.getVal().equals(source.getType())) {
                numbers = ORACLE_NUMBERS;
            } else if (DataSourceType.SQLServer.getVal().equals(source.getType())) {
                numbers = SQLSERVER_NUMBERS;
            } else if (DataSourceType.PostgreSQL.getVal().equals(source.getType())
                    || DataSourceType.ADB_FOR_PG.getVal().equals(source.getType())) {
                numbers = POSTGRESQL_NUMBERS;
            } else if (DataSourceType.DB2.getVal().equals(source.getType())) {
                numbers = DB2_NUMBERS;
            } else if (DataSourceType.GBase_8a.getVal().equals(source.getType())) {
                numbers = GBASE_NUMBERS;
            } else if (DataSourceType.Clickhouse.getVal().equals(source.getType())) {
                numbers = CLICKHOUSE_NUMBERS;
            } else if (DataSourceType.DMDB.getVal().equals(source.getType())) {
                numbers = DMDB_NUMBERS;
            } else if (DataSourceType.GREENPLUM6.getVal().equals(source.getType())) {
                numbers = GREENPLUM_NUMBERS;
            } else if (DataSourceType.KINGBASE8.getVal().equals(source.getType())) {
                numbers = KINGBASE_NUMBERS;
            } else if (DataSourceType.INFLUXDB.getVal().equals(source.getType())) {
                numbers = INFLUXDB_NUMBERS;
            } else {
                throw new RdosDefineException("切分键只支持关系型数据库");
            }
            Map<JSONObject, String> twinsMap = new LinkedHashMap<>(tablecolumn.size()+1);
            for (JSONObject twins : tablecolumn) {
                twinsMap.put(twins, twins.getString(TYPE));
            }


            Iterator<Map.Entry<JSONObject, String>> iterator = twinsMap.entrySet().iterator();
            while (iterator.hasNext()) {
                String type = getSimpleType(iterator.next().getValue());
                if (numbers.contains(type.toUpperCase())) {
                    continue;
                }
                if (source.getType().equals(DataSourceType.Oracle.getVal())) {
                    if ("number".equalsIgnoreCase(type)) {
                        continue;
                    }

                    Matcher numberMatcher1 = NUMBER_PATTERN.matcher(type);
                    Matcher numberMatcher2 = NUMBER_PATTERN2.matcher(type);
                    if (numberMatcher1.matches()) {
                        continue;
                    } else if (numberMatcher2.matches()) {
                        int floatLength = Integer.parseInt(numberMatcher2.group(2));
                        if (floatLength <= 0) {
                            continue;
                        }
                    }
                }
                iterator.remove();
            }
            //为oracle加上默认切分键
            if (source.getType().equals(DataSourceType.Oracle.getVal())) {
                JSONObject keySet = new JSONObject();
                keySet.put("type", "NUMBER(38,0)");
                keySet.put("key", "ROW_NUMBER()");
                keySet.put("comment", "");
                twinsMap.put(keySet, "NUMBER(38,0)");
            }
            return twinsMap.keySet();
        }
        return Sets.newHashSet();
    }

    private String getSimpleType(String type) {
        type = type.toUpperCase();
        String[] split = type.split(" ");
        if (split != null && split.length > 1) {
            //提取例如"INT UNSIGNED"情况下的字段类型
            type = split[0];
        }
        return type;
    }

    /**
     * 获取绑定项目下的数据源
     */
    public JSONObject getDataSourceInBingProject(Long tenantId, Long projectId, Long dataSourceId) {
        Project project = projectService.getProjectById(projectId);
        BatchDataSource currentSource = getOne(dataSourceId);

        JSONObject result = new JSONObject();
        JSONObject current = new JSONObject();
        current.put("id", currentSource.getId());
        current.put("dataName", currentSource.getDataName());
        current.put("type", currentSource.getType());
        current.put("info", "jdbcUrl:xxxx");
        result.put("currentSource", current);

        result.put("linkSource", null);
//        BatchTestProduceDataSource sourceSource = batchTestProduceDataSourceDao.getBySourceIdOrLinkSourceId(dataSourceId);
//        if (sourceSource != null) {
//            Long linkSourceId = sourceSource.getTestDataSourceId().equals(dataSourceId) ? sourceSource.getProduceDataSourceId() : sourceSource.getTestDataSourceId();
//            BatchDataSource linkSource = getOneOrNull(linkSourceId);
//            if (linkSource != null) {
//                JSONObject link = new JSONObject();
//                link.put("id", linkSource.getId());
//                link.put("dataName", linkSource.getDataName());
//                link.put("type", linkSource.getType());
//                result.put("linkSource", link);
//            }
//        }
//        List<Long> inUseDataSources = batchTestProduceDataSourceDao.getHasBeenUseDataSources(projectId);
//        List<BatchDataSource> batchDataSources = getDataSourceByProjectId(project.getProduceProjectId());
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
        return result;
    }

    /**
     * 将用户提供的字段类型转成hive类型
     *
     * @param map
     * @return
     */
    public JSONObject convertToHiveColumns(Map<String, String> map) {
        if (map != null && map.size() > 0) {
            JSONObject json = new JSONObject(map.size(), true);
            Set<String> keySet = map.keySet();
            for (String key : keySet) {
                json.put(key, TYPE_FORMAT.formatToString(map.get(key)));
            }
            return json;
        }
        return new JSONObject();
    }

    /**
     * 判断carbondata数据表是否是标准分区表
     *
     * @param sourceId
     * @param tableName
     * @param tenantId
     * @return
     */
    public Boolean isNativeHive(Long sourceId, String tableName, Long tenantId) {
        BatchDataSource source = getOne(sourceId);
        DataSourceType carbonDataSourceType = DataSourceType.CarbonData;
        if (source.getType().equals(carbonDataSourceType.getVal())) {
            JSONObject dataJson = JSON.parseObject(source.getDataJson());
            List<Map<String, Object>> maps = null;
            Connection conn = null;
            try {
                ISourceDTO sourceDTO = SourceDTOType.getSourceDTO(dataJson, source.getType(), fillKerberosConfig(sourceId));
                IClient client = ClientCache.getClient(carbonDataSourceType.getVal());
                conn = client.getCon(sourceDTO);

                maps = DBUtil.executeQuery(conn, "desc extended " + tableName,false);
            } finally {
                DBUtil.closeDBResources(null, null, conn);
            }
            String partitionType = null;
            for (Map<String, Object> map : maps) {
                String colName = (String) map.get("col_name");
                if (colName.contains("Partition Type")) {
                    partitionType = (String) map.get("data_type");
                }
            }
            if (partitionType != null) {
                return partitionType.contains(CarbonDataPartitionType.NATIVE_HIVE.name());
            }
            return false;
        } else {
            throw new RdosDefineException("数据源类型不匹配");
        }
    }

    public Table getOrcTableInfoForCarbonData(JSONObject dataJson, String tableName, Map<String, Object> kerberosConfig) {
        DataSourceType dataSourceType = DataSourceType.CarbonData;
        IClient client = ClientCache.getClient(dataSourceType.getVal());
        ISourceDTO sourceDTO = SourceDTOType.getSourceDTO(dataJson, dataSourceType.getVal(), kerberosConfig);
        com.dtstack.dtcenter.loader.dto.Table table = client.getTable(sourceDTO, SqlQueryDTO.builder().tableName(tableName).build());
        List<ColumnMetaDTO> columnMetaDTOS = table.getColumns();
        List<Column> columns = new ArrayList<>();
        List<Column> part = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(columnMetaDTOS)) {
            for (int i = 0; i < columnMetaDTOS.size(); i++) {
                ColumnMetaDTO bean = columnMetaDTOS.get(i);
                Column column = new Column();
                column.setTable(tableName);
                column.setAlias(bean.getKey());
                column.setName(bean.getKey());
                column.setType(bean.getType());
                column.setIndex(i);
                columns.add(column);
                if (bean.getPart()){
                    part.add(column);
                }
            }
        }
        Table baseInfo = new Table();
        BeanUtils.copyProperties(table, baseInfo);
        baseInfo.setColumns(columns);
        baseInfo.setPartitions(part);
        baseInfo.setStoreType(StoredType.ORC.getValue());
        return baseInfo;
    }

    public List<String> getProjectHiveSourceTables(Long projectId, Integer tableType, Long tenantId) {
        BatchDataSource datasourceHadoop = getHadoopDefaultDataSourceByProjectIdWithoutError(projectId);
        BatchDataSource datasourceLibra = getDefaultDataSourceOrNull(projectId, DataSourceType.LIBRA.getVal());
        if (datasourceLibra == null && null == datasourceHadoop) {
            throw new RdosDefineException("项目关联的数据源不存在!", ErrorCode.DATA_NOT_FIND);
        }
        List<String> tables = new ArrayList<>();
        if (null != datasourceHadoop) {
            tables.addAll(this.tablelist(null,  datasourceHadoop.getId(), tenantId, null, null, null, false));
        }

        if (null != datasourceLibra) {
            tables.addAll(this.tablelist(null,  datasourceLibra.getId(), tenantId, null, null, null, false));
        }
        return tables;
    }

    /**
     * 获取指定数据源密码
     * For SDK purpose
     *
     * @param sourceId
     * @param tenantId
     * @param projectId
     * @return
     */
    public String getDataSourcePassword(Long sourceId, Long tenantId, Long projectId) {
        checkParamNullTenantProject(tenantId, projectId);
        BatchDataSource source = getOne(sourceId);
        JSONObject dataJson = JSON.parseObject(source.getDataJson());
        String password = JsonUtil.getStringDefaultEmpty(dataJson, JDBC_PASSWORD);
        return Optional.ofNullable(password).orElse("");
    }

    private void checkParamNullTenantProject(Long tenantId, Long projectId) {
        if (tenantId == null) {
            throw new RdosDefineException("tenantId标识不能为空");
        }
        if (projectId == null) {
            throw new RdosDefineException("projectId标识不能为空");
        }
    }

    public void checkPermission() {
    }


    public BatchDataSource getByName(String name, Long projectId) {
        BatchDataSourceHaveImportVO vo = new BatchDataSourceHaveImportVO();
        vo.setProjectId(projectId);
        vo.setDataName(name);
        com.dtstack.dtcenter.common.pager.PageResult<List<DsServiceListVO>> listPageResult = this.queryHaveImportedDataSource(vo);
        List<DsServiceListVO> data = listPageResult.getData();
        if(CollectionUtils.isNotEmpty(data)){
            DsServiceListVO dsServiceList = data.get(0);
            BatchDataSourceCenter dataSourceCenter = batchDataSourceCenterDao.getDataSourceCenterByInfoId(projectId, dsServiceList.getDataInfoId());
            return convertDsServiceListDTOToDataSource(dataSourceCenter, dsServiceList);
        }
        throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_DATA_SOURCE);
    }

    public BatchDataSource getDefaultDataSource(Long projectId, Integer sourceType) {
        List<BatchDataSource> dataSourceList = getDefaultListByProjectId(projectId);
        for (BatchDataSource dataSource : dataSourceList){
            if(sourceType != null && sourceType.equals(dataSource.getType())){
                return dataSource;
            }
        }
        throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_DATA_SOURCE);
    }

    /**
     * 如果没有找到数据源，则返回null
     * @param projectId
     * @param sourceType
     * @return
     */
    public BatchDataSource getDefaultDataSourceOrNull(Long projectId, Integer sourceType) {
        List<BatchDataSource> dataSourceList = getDefaultListByProjectId(projectId);
        for (BatchDataSource dataSource : dataSourceList){
            if(sourceType != null && sourceType.equals(dataSource.getType())){
                return dataSource;
            }
        }
        return null;
    }

    /**
     * 根据多个类型查询，只返回一个结果，主要用于查询Hadoop的默认数据源（Hive、SparkThrift、Impala）
     * @param projectId
     * @param sourceTypes
     * @return
     */
    public BatchDataSource getDefaultDataSource(Long projectId, List<Integer> sourceTypes) {
        List<BatchDataSource> dataSourceList = getDefaultListByProjectId(projectId);
        for (BatchDataSource dataSource : dataSourceList){
            if(CollectionUtils.isNotEmpty(sourceTypes) && sourceTypes.contains(dataSource.getType())){
                return dataSource;
            }
        }
        throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_DATA_SOURCE);
    }

    /**
     * 获取对应默认的数据源信息
     *
     * @param engineType
     * @param projectId
     * @return
     */
    public BatchDataSource getDefaultDataSourceByEngineType(Integer engineType, Long projectId) {
        DataSourceType sourceType = this.getDataSourceTypeByEngineType(engineType, projectId);
        return getDefaultDataSource(projectId, sourceType.getVal());
    }

    public BatchDataSource getBeanByProjectIdAndDbTypeAndDbName(Long projectId, Integer dataSourceType, String dataSourceName) {
        BatchDataSourceHaveImportVO vo = new BatchDataSourceHaveImportVO();
        vo.setProjectId(projectId);
        vo.setDataName(dataSourceName);
        vo.setDataTypeCodeList(Lists.newArrayList(dataSourceType));
        com.dtstack.dtcenter.common.pager.PageResult<List<DsServiceListVO>> listPageResult = this.queryHaveImportedDataSource(vo);
        List<DsServiceListVO> data = listPageResult.getData();
        if(CollectionUtils.isNotEmpty(data)){
            DsServiceListVO dsServiceList = data.get(0);
            BatchDataSourceCenter dataSourceCenter = batchDataSourceCenterDao.getDataSourceCenterByInfoId(projectId, dsServiceList.getDataInfoId());
            return convertDsServiceListDTOToDataSource(dataSourceCenter, dsServiceList);
        }
        throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_DATA_SOURCE);
    }

    public Long getDefaultDataSourceByTableType(final Integer tableType, final Long projectId) {
        final MultiEngineType engineTypeByTableType = TableTypeEngineTypeMapping.getEngineTypeByTableType(tableType);
        if (null != engineTypeByTableType) {
            DataSourceType sourceType = this.getDataSourceTypeByEngineType(engineTypeByTableType.getType(), projectId);
            BatchDataSource datasource = getDefaultDataSourceOrNull(projectId, sourceType.getVal());
            if (datasource != null) {
                return datasource.getId();
            }
        }
        return -1L;
    }

    private String getLocalKerberosConf(Long sourceId) {
        String key = getSourceKey(sourceId);
        return environmentContext.getKerberosLocalPath() + File.separator + key;
    }

    private String getSourceKey(Long sourceId) {
        return AppType.RDOS.name() + "_" + Optional.ofNullable(sourceId).orElse(0L);
    }

    private void downloadKerberosFromSftp(String kerberosFile, String localKerberosConf, Long dtuicTenantId, Timestamp kerberosFileTimestamp) {
        //需要读取配置文件
        Map<String, String> sftpMap = getSftpMap(dtuicTenantId);
        try {
            KerberosConfigVerify.downloadKerberosFromSftp(kerberosFile, localKerberosConf, sftpMap, kerberosFileTimestamp);
        } catch (Exception e) {
            //允许下载失败
            logger.info("download kerberosFile failed {}", e);
        }
    }

    public Map<String, String> getSftpMap(Long dtuicTenantId) {
        Map<String, String> map = new HashMap<>();
        String cluster = clusterService.clusterInfo(dtuicTenantId);
        JSONObject clusterObj = JSON.parseObject(cluster);
        JSONObject sftpConfig = clusterObj.getJSONObject(EComponentType.SFTP.getConfName());
        if (Objects.isNull(sftpConfig)) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_SFTP);
        } else {
            for (String key : sftpConfig.keySet()) {
                map.put(key, sftpConfig.getString(key));
            }
        }
        return map;
    }

    /**
     * kerberos配置预处理、替换相对路径为绝对路径等操作
     *
     * @param sourceType
     * @param kerberosMap
     * @param localKerberosConf
     * @return
     */
    private Map<String, Object> handleKerberos (Integer sourceType, Map<String, Object> kerberosMap, String localKerberosConf) {
        IKerberos kerberos = ClientCache.getKerberos(sourceType);
        HashMap<String, Object> tmpKerberosConfig = new HashMap<>(kerberosMap);
        try {
            kerberos.prepareKerberosForConnect(tmpKerberosConfig, localKerberosConf);
        } catch (Exception e) {
            logger.error("common-loader中kerberos配置文件处理失败！", e);
            throw new RdosDefineException("common-loader中kerberos配置文件处理失败", e);
        }
        return tmpKerberosConfig;
    }

    /**
     * 根据tableType 获取默认数据源信息
     * @param projectId
     * @param tableType
     * @return
     */
    public BatchDataSource getSourceByTableType(Long projectId, Integer tableType){
        MultiEngineType engineType = TableTypeEngineTypeMapping.getEngineTypeByTableType(tableType);
        BatchDataSource defaultDataSource = getDefaultDataSourceByEngineType(engineType.getType(), projectId);
        return defaultDataSource;
    }

    //一次最多加载数量
    private static final Integer MAX_LOAD = 200;

    /**
     * 获取所有schema
     * @param sourceId 数据源id
     * @return
     */
    public List<String> getAllSchemas(Long sourceId, String schema) {
        BatchDataSource source = getOne(sourceId);
        String dataJson = source.getDataJson();
        JSONObject json = JSON.parseObject(dataJson);
        ISourceDTO sourceDTO = SourceDTOType.getSourceDTO(json, source.getType(), fillKerberosConfig(sourceId));
        IClient client = ClientCache.getClient(source.getType());
        return client.getAllDatabases(sourceDTO, SqlQueryDTO.builder().schema(schema).build());
    }

    /**
     * 获取项目下的数据源信息
     *
     * @param projectId 项目ID
     * @return
     */
    public List<BatchDataSource> getDefaultListByProjectId(Long projectId){
        List<BatchDataSource> batchDataSourceList = Lists.newArrayList();

        //先从本地查询项目下所有的默认数据源
        List<BatchDataSourceCenter> defaultDataSourceCenterList = batchDataSourceCenterDao.getDefaultDataSourceCenterByProjectId(projectId);
        if(CollectionUtils.isEmpty(defaultDataSourceCenterList)){
            return batchDataSourceList;
        }
        Map<Long, BatchDataSourceCenter> defaultDataSourceCenterMap = defaultDataSourceCenterList.stream().collect(Collectors.toMap(BatchDataSourceCenter::getDtCenterSourceId, Function.identity(), (key1, key2) -> key1));

        if (MapUtils.isEmpty(defaultDataSourceCenterMap)) {
            return Lists.newArrayList();
        }

        //然后根据id去数据源中心获取详细信息
        List<DsServiceInfoVO> data = apiServiceFacade.getDsInfoListByIdList(new ArrayList<>(defaultDataSourceCenterMap.keySet()));
        if(CollectionUtils.isNotEmpty(data)){
            for (DsServiceInfoVO infoDTO : data){
                //转换成BatchDataSource
                BatchDataSourceCenter dataSourceCenter = defaultDataSourceCenterMap.get(infoDTO.getDataInfoId());
                BatchDataSource batchDataSource = convertDsServiceInfoDTOToDataSource(dataSourceCenter, infoDTO);
                batchDataSourceList.add(batchDataSource);
            }
        }
        return batchDataSourceList;
    }

    /**
     * 默认数据源对应的engineType
     * @param sourceType
     * @return
     */
    private MultiEngineType getEngineTypeByDefaultDataSourceType(Integer sourceType) {
        MultiEngineType engineType = null;
        DataSourceType dataSourceType = DataSourceType.getSourceType(sourceType);
        if (dataSourceType == DataSourceType.HIVE || dataSourceType == DataSourceType.HIVE1X || dataSourceType == DataSourceType.HIVE3X
                || dataSourceType == DataSourceType.SparkThrift2_1 || dataSourceType == DataSourceType.IMPALA
                || dataSourceType == DataSourceType.INCEPTOR) {
            engineType = MultiEngineType.HADOOP;
        } else if (dataSourceType == DataSourceType.GREENPLUM6) {
            engineType = MultiEngineType.GREENPLUM;
        } else if (dataSourceType == DataSourceType.LIBRA) {
            engineType = MultiEngineType.LIBRA;
        } else if (dataSourceType == DataSourceType.Oracle) {
            engineType = MultiEngineType.ORACLE;
        } else if (dataSourceType == DataSourceType.TiDB) {
            engineType = MultiEngineType.TIDB;
        }
        return engineType;
    }

    /**
     * 获取Hadoop引擎新建的默认数据源类型
     *
     * @param projectId
     * @return
     */
    public DataSourceType getHadoopDefaultDataSourceByProjectId(Long projectId){
        List<Integer> typeList = listHadoopDataSourceType();
        BatchDataSource batchDataSource = getDefaultDataSource(projectId, typeList);
        return DataSourceType.getSourceType(batchDataSource.getType());
    }

    /**
     * 获取Hadoop引擎新建的默认数据源类型
     *
     * @param projectId
     * @return
     */
    public BatchDataSource getHadoopDefaultDataSourceByProjectIdWithoutError(Long projectId) {
        List<Integer> typeList = listHadoopDataSourceType();
        try {
            return getDefaultDataSource(projectId, typeList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据engineType获取dataSourceType
     * @param engineType
     * @param projectId
     * @return
     */
    public DataSourceType getDataSourceTypeByEngineType(Integer engineType, Long projectId) {
        if (MultiEngineType.HADOOP.getType() == engineType) {
            return getHadoopDefaultDataSourceByProjectId(projectId);
        }
        if (MultiEngineType.LIBRA.getType() == engineType) {
            return DataSourceType.LIBRA;
        }
        if(MultiEngineType.TIDB.getType() == engineType){
            return DataSourceType.TiDB;
        }
        if(MultiEngineType.ORACLE.getType() == engineType){
            return DataSourceType.Oracle;
        }
        if(MultiEngineType.GREENPLUM.getType() == engineType){
            return DataSourceType.GREENPLUM6;
        }
        if (MultiEngineType.ANALYTICDB_FOR_PG.getType() == engineType) {
            return DataSourceType.ADB_FOR_PG;
        }
        throw new RdosDefineException("not funod default dataSourceType!");
    }

    /**
     * 查询hadoop对应的console配置的数据源信息
     * @param defaultDataSourceDTO
     * @return
     */
    private List<DataSourceDTO> listHadoopDataSourceDTOs(DataSourceDTO defaultDataSourceDTO) {
        List<DataSourceDTO> dataSourceDTOS = Lists.newArrayList(defaultDataSourceDTO);
        List<Integer> hadoopDataSourceTypes = listHadoopDataSourceType();
        hadoopDataSourceTypes.remove(defaultDataSourceDTO.getSourceType());
        for (Integer dataSourceType : hadoopDataSourceTypes) {
            if ((dataSourceType.equals(DataSourceType.HIVE1X.getVal()) || dataSourceType.equals(DataSourceType.HIVE.getVal()) || dataSourceType.equals(DataSourceType.HIVE3X.getVal()))
                    && dataSourceDTOS.stream().anyMatch(dataSourceDTO -> DataSourceType.HIVE1X.getVal().equals(dataSourceDTO.getSourceType())
                    || DataSourceType.HIVE.getVal().equals(dataSourceDTO.getSourceType()) || DataSourceType.HIVE3X.getVal().equals(dataSourceDTO.getSourceType()))) {
                continue;
            }
            JdbcInfo jdbcInfo = null;
            try {
                jdbcInfo = Engine2DTOService.getJdbcInfo(defaultDataSourceDTO.getDtUicTenantId(), null, ETableType.getDatasourceType(dataSourceType));
                if (jdbcInfo == null || StringUtils.isEmpty(jdbcInfo.getJdbcUrl())) {
                    continue;
                }
            } catch (Exception e) {
                logger.error(String.format("获取console连接信息报错，dtUicTenantId:%d, dataSourceType:%d", defaultDataSourceDTO.getDtUicTenantId(), dataSourceType));
                continue;
            }
            DataSourceDTO dataSourceDTO = new DataSourceDTO();
            dataSourceDTO.setSourceName(String.format("%s_%s", defaultDataSourceDTO.getSourceName(), DataSourceType.getSourceType(dataSourceType).getName()));
            dataSourceDTO.setSchemaName(defaultDataSourceDTO.getSchemaName());
            Map<String, String> dataJsonMap = Maps.newHashMap();
            dataJsonMap.put("jdbcUrl", Engine2DTOService.buildUrlWithDb(jdbcInfo.getJdbcUrl(), defaultDataSourceDTO.getSchemaName()));
            dataJsonMap.put("password", jdbcInfo.getPassword());
            dataJsonMap.put("username", jdbcInfo.getUsername());
            dataSourceDTO.setDataJson(JSONObject.toJSONString(dataJsonMap));
            if (jdbcInfo.getKerberosConfig() != null) {
                dataSourceDTO.setKerberosConf(JSON.toJSONString(jdbcInfo.getKerberosConfig()));
            }
            dataSourceDTO.setSourceType(dataSourceType);
            dataSourceDTO.setAppType(AppType.RDOS.getType());
            dataSourceDTO.setDtUicTenantId(defaultDataSourceDTO.getDtUicTenantId());
            dataSourceDTO.setProjectId(defaultDataSourceDTO.getProjectId());
            dataSourceDTO.setIsDefault(0);
            dataSourceDTOS.add(dataSourceDTO);
        }
        return dataSourceDTOS;
    }

    /**
     * 获取hadoop引擎对应的数据源
     * @return
     */
    private static List<Integer> listHadoopDataSourceType() {
        return Lists.newArrayList(DataSourceType.SparkThrift2_1.getVal(), DataSourceType.HIVE.getVal(),  DataSourceType.HIVE3X.getVal(),
                DataSourceType.HIVE1X.getVal(), DataSourceType.IMPALA.getVal());
    }

    /**
     * 根据数据源信息获取对应的表信息
     *
     * @param sourceId
     * @param schema
     * @param tableName
     * @return
     */
    public BatchTableMetaInfoDTO getTableInfoByDataSource(Long sourceId, String schema, String tableName){
        ISourceDTO sourceDTO = getSourceDTOByDataSourceId(sourceId);
        return TableOperateUtils.getTableMetaInfo(sourceDTO, tableName, schema);
    }

    /**
     * 根据数据源的ID构建ISourceDTO
     *
     * @param sourceId
     * @return
     */
    private ISourceDTO getSourceDTOByDataSourceId(Long sourceId){
        BatchDataSource batchDataSource = getOne(sourceId);
        String dataJson = batchDataSource.getDataJson();
        JSONObject json = JSON.parseObject(dataJson);
        Map<String, Object> kerberosConfig = fillKerberosConfig(sourceId);
        return SourceDTOType.getSourceDTO(json, batchDataSource.getType(), kerberosConfig);
    }

    /**
     * 获取数据对应的版本
     *
     * @param sourceId
     * @return
     */
    public String getDataSourceVersion(Long sourceId){
        ISourceDTO iSourceDTO = getSourceDTOByDataSourceId(sourceId);
        IClient iClient = ClientCache.getClient(iSourceDTO.getSourceType());
        return iClient.getVersion(iSourceDTO);
    }

    /**
     * 根据sourceId taskParamList 正则表达式 获取 匹配的记录
     *
     * @param taskParamList 自定义参数
     * @param sourceId ftp数据源id
     * @param regexStr  路径+正则表达式
     * @return
     */
    public FtpRegexVO ftpRegexPre(List<BatchTaskParamVO> taskParamList, Long sourceId, String regexStr){
        List<BatchTaskParam> list = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(taskParamList)){
            for (BatchTaskParamVO vo : taskParamList) {
                BatchTaskParam taskParam = new BatchTaskParam();
                BeanUtils.copyProperties(vo, taskParam);
                list.add(taskParam);
            }
        }
        String ruleFilePath = jobParamReplace.paramReplace(regexStr, list);
        String filePath = ruleFilePath.substring(0, ruleFilePath.lastIndexOf("/")+1);
        //处理只传入正则表达式的情况
        filePath = StringUtils.isNotEmpty(filePath) ? filePath : "/";
        regexStr = ruleFilePath.substring(ruleFilePath.lastIndexOf("/")+1);
        IClient iClient = ClientCache.getClient(DataSourceType.FTP.getVal());
        ISourceDTO sourceDTO = getSourceDTOBySourcdId(sourceId);
        List<String> fileList = iClient.listFileNames(sourceDTO, filePath, true, true, 101, regexStr);
        FtpRegexVO vo = new FtpRegexVO();
        vo.setFileNameList(fileList);
        vo.setNumber(fileList.size());
        return vo;
    }
    /**
     * 引入数据源
     * @param vo
     */
    @Transactional
    public void importDataSource(BatchDataSourceImportVO vo){
        if(CollectionUtils.isEmpty(vo.getDtCenterSourceIds())){
            throw new RdosDefineException("引入的数据源不能为空！");
        }
        BatchDataSourceCenter batchDataSourceCenter = new BatchDataSourceCenter();
        batchDataSourceCenter.setTenantId(vo.getTenantId());
        batchDataSourceCenter.setProjectId(vo.getProjectId());
        batchDataSourceCenter.setCreateUserId(vo.getUserId());
        batchDataSourceCenter.setIsDefault(0);

        List<BatchDataSourceCenter> dataSourceCenterList = batchDataSourceCenterDao.getDataSourceCenterByInfoIds(vo.getProjectId(), vo.getDtCenterSourceIds());
        if(CollectionUtils.isNotEmpty(dataSourceCenterList)){
            throw new RdosDefineException("数据源中心引入数据源失败，存在已经引入过的数据源！");
        }
        for(Long dtCenterSourceId : vo.getDtCenterSourceIds()){
            batchDataSourceCenter.setDtCenterSourceId(dtCenterSourceId);
            batchDataSourceCenterDao.insertDataSource(batchDataSourceCenter);
        }

        ProductImportParam appImportParam = new ProductImportParam();
        appImportParam.setAppType(AppType.RDOS.getType());
        appImportParam.setDataInfoIdList(vo.getDtCenterSourceIds());
        appImportParam.setProjectId(vo.getProjectId());
        Long dtuicTenantId = tenantService.getDtuicTenantId(vo.getTenantId());
        appImportParam.setDtUicTenantId(dtuicTenantId);
        try {
            Boolean data = apiServiceFacade.productImportDs(appImportParam);
            if(!BooleanUtils.isTrue(data)){
                throw new RdosDefineException("数据源中心引入数据源失败，请重试！");
            }
        } catch (Exception e) {
            throw new RdosDefineException("数据源中心引入数据源失败，请重试！" + e.getMessage());
        }
    }

    /**
     * 取消引入数据源
     * @param vo
     */
    @Transactional
    public void cancelImportDataSource(BatchDataSourceCancelImportVO vo){
        BatchDataSourceCenter dataSourceCenter = batchDataSourceCenterDao.getDataSourceCenterById(vo.getSourceId());

        if(dataSourceCenter == null || dataSourceCenter.getIsDefault() == 1){
            throw new RdosDefineException("meta数据源不允许取消引入！");
        }
        batchDataSourceCenterDao.deleteById(vo.getSourceId(), vo.getUserId());

        ProductImportParam appImportParam = new ProductImportParam();
        appImportParam.setAppType(AppType.RDOS.getType());
        appImportParam.setDataInfoIdList(Lists.newArrayList(dataSourceCenter.getDtCenterSourceId()));
        appImportParam.setProjectId(vo.getProjectId());
        Long dtuicTenantId = tenantService.getDtuicTenantId(vo.getTenantId());
        appImportParam.setDtUicTenantId(dtuicTenantId);
        try {
            Boolean data = apiServiceFacade.productCancelDs(appImportParam);
            if(!BooleanUtils.isTrue(data)){
                throw new RdosDefineException("数据源中心取消引入数据源失败，请重试！");
            }
        } catch (Exception e) {
            throw new RdosDefineException("数据源中心取消引入数据源失败，请重试！" + e.getMessage());
        }
    }

    /**
     * 根据项目id 删除数据源
     * @param projectId
     * @param userId
     */
    @Transactional
    public void cancelImportDataSourceByProject(Long tenantId, Long projectId, Long userId){
        List<BatchDataSourceCenter> dataSourceCenterList = batchDataSourceCenterDao.getInfoIdsByProject(projectId);
        if(CollectionUtils.isEmpty(dataSourceCenterList)){
            return;
        }
        Set<Long> infoIdSet = dataSourceCenterList.stream().map(BatchDataSourceCenter::getDtCenterSourceId).collect(Collectors.toSet());

        batchDataSourceCenterDao.deleteByProjectId(projectId, userId);

        ProductImportParam appImportParam = new ProductImportParam();
        appImportParam.setAppType(AppType.RDOS.getType());
        appImportParam.setDataInfoIdList(new ArrayList<>(infoIdSet));
        appImportParam.setProjectId(projectId);
        Long dtuicTenantId = tenantService.getDtuicTenantId(tenantId);
        appImportParam.setDtUicTenantId(dtuicTenantId);
        try {
            Boolean data = apiServiceFacade.productCancelDs(appImportParam);
            if(!BooleanUtils.isTrue(data)){
                throw new RdosDefineException("数据源中心取消引入数据源失败，请重试！");
            }
        } catch (Exception e) {
            throw new RdosDefineException("数据源中心取消引入数据源失败，请重试！" + e.getMessage());
        }
    }

    /**
     * 创建项目失败时，删除本地数据源引入信息，并回滚数据源中心的meta数据源
     * @param projectId
     * @param userId
     */
    @Transactional
    public void callbackPubDataSourceByProject(Long tenantId, Long projectId, Long userId){
        try {
            List<BatchDataSourceCenter> dataSourceCenterList = batchDataSourceCenterDao.getInfoIdsByProject(projectId);
            if(CollectionUtils.isEmpty(dataSourceCenterList)){
                return;
            }
            Set<Long> infoIdSet = dataSourceCenterList.stream().map(BatchDataSourceCenter::getDtCenterSourceId).collect(Collectors.toSet());

            batchDataSourceCenterDao.deleteByProjectId(projectId, userId);

            Long dtuicTenantId = tenantService.getDtuicTenantId(tenantId);
            for(Long dataInfoId : infoIdSet){
                RollDsParam rollDsParam = new RollDsParam();
                rollDsParam.setAppType(AppType.RDOS.getType());
                rollDsParam.setDsTenantId(tenantId);
                rollDsParam.setDsDtuicTenantId(dtuicTenantId);
                rollDsParam.setDataInfoId(dataInfoId);
                Boolean data = apiServiceFacade.rollDsInfoById(rollDsParam);
                if(!BooleanUtils.isTrue(data)){
                    logger.error("数据源中心取消引入数据源失败，请重试！");
                }
            }
        } catch (Exception e) {
            logger.error("数据源中心取消引入数据源失败，请重试！" + e.getMessage());
        }
    }

    /**
     * 查询可以引入的数据源列表
     * @param vo
     */
    public PageResult<List<BatchDataSourceAllowImportResultVO>> queryAllowImportDataSource(BatchDataSourceAllowImportVO vo){
        DsServiceListParam dsServiceListParam = new DsServiceListParam();
        BeanUtils.copyProperties(vo, dsServiceListParam);
        dsServiceListParam.setAppType(AppType.RDOS.getType());
        dsServiceListParam.setDsDtuicTenantId(vo.getDtuicTenantId());
        com.dtstack.dtcenter.common.pager.PageResult<List<DsServiceListVO>> pageResult = apiServiceFacade.importDsPage(dsServiceListParam);

        List<DsServiceListVO> dsServiceListVOList = pageResult.getData();
        List<BatchDataSourceAllowImportResultVO> resultVOList = Lists.newArrayList();
        for (DsServiceListVO dsServiceListVO : dsServiceListVOList){
            BatchDataSourceAllowImportResultVO resultVO = new BatchDataSourceAllowImportResultVO();
            BeanUtils.copyProperties(dsServiceListVO, resultVO);
            resultVOList.add(resultVO);
        }
        PageResult<List<BatchDataSourceAllowImportResultVO>> pageResultVO = new PageResult<>();
        BeanUtils.copyProperties(pageResult, pageResultVO);
        pageResultVO.setData(resultVOList);
        return pageResultVO;
    }


    /**
     * 创建meta数据源
     * @return
     */
    public void createMateDataSource(Long dtuicTenantId, Long tenantId, Long projectId, Long userId, String dataJson, String dataName, Integer dataType, String dataDesc, String schemaName){
        CreateDsParam createDsParam = new CreateDsParam();
        createDsParam.setAppType(AppType.RDOS.getType());
        createDsParam.setCreateUserId(userId);
        createDsParam.setDataJson(dataJson);
        createDsParam.setDataName(dataName);
        createDsParam.setDsDtuicTenantId(dtuicTenantId);
        createDsParam.setIsMeta(1);
        createDsParam.setType(dataType);
        createDsParam.setProjectId(projectId);
        createDsParam.setDsTenantId(tenantId);
        createDsParam.setDataDesc(StringUtils.isNotEmpty(dataDesc) ? dataDesc : "");
        createDsParam.setSchemaName(schemaName);

        DsShiftReturnVO data = apiServiceFacade.createMetaDs(createDsParam);
        if(data == null){
            throw new RdosDefineException("数据源中心创建默认数据源失败！");
        }
        Long dataInfoId = data.getDataInfoId();
        BatchDataSourceCenter batchDataSourceCenter = new BatchDataSourceCenter();
        batchDataSourceCenter.setTenantId(tenantId);
        batchDataSourceCenter.setProjectId(projectId);
        batchDataSourceCenter.setIsDefault(1);
        batchDataSourceCenter.setDtCenterSourceId(dataInfoId);
        batchDataSourceCenter.setCreateUserId(userId);
        batchDataSourceCenterDao.insertDataSource(batchDataSourceCenter);

    }

    /**
     * 查询已经引入的数据源信息
     * @param vo
     * @return
     */
    private com.dtstack.dtcenter.common.pager.PageResult<List<DsServiceListVO>> queryHaveImportedDataSource(BatchDataSourceHaveImportVO vo){
        DsServiceListParam listParam = new DsServiceListParam();
        listParam.setDsDtuicTenantId(vo.getDtuicTenantId());
        if(vo.getDtuicTenantId() == null){
            if(vo.getProjectId() == null){
                throw new RdosDefineException("查询数据源信息必须传uic租户id");
            }
            Project project = projectService.getProjectById(vo.getProjectId());
            Long dtuicTenantId = tenantService.getDtuicTenantId(project.getTenantId());
            listParam.setDsDtuicTenantId(dtuicTenantId);
        }

        listParam.setAppType(AppType.RDOS.getType());
        listParam.setDataTypeCodeList(vo.getDataTypeCodeList());
        listParam.setProjectId(vo.getProjectId());
        listParam.setSearch(vo.getSearch());
        listParam.setPageSize(vo.getPageSize());
        listParam.setCurrentPage(vo.getCurrentPage());
        listParam.setDataName(vo.getDataName());
        com.dtstack.dtcenter.common.pager.PageResult<List<DsServiceListVO>> data = apiServiceFacade.appDsPage(listParam);
        return data;
    }

    /**
     * 根据tenantId 获取本租户下所有的数据源信息
     *
     * @param tenantId
     * @return
     */
    public List<BatchDataSource> listByTenantId(Long tenantId){
        List<BatchDataSourceCenter> dataSourceCenterList = batchDataSourceCenterDao.getListByTenantId(tenantId);
        if (CollectionUtils.isEmpty(dataSourceCenterList)) {
            return Lists.newArrayList();
        }
       return getSourceListByDataSourceCenter(dataSourceCenterList);
    }

    /**
     * 页面查询已经引入的数据源信息
     * @param vo
     * @return
     */
    public PageResult<List<BatchDataSourceHaveImportResultVO>> queryHaveImportedDataSourceView(BatchDataSourceHaveImportVO vo){
        com.dtstack.dtcenter.common.pager.PageResult<List<DsServiceListVO>> pageResult = queryHaveImportedDataSource(vo);
        List<DsServiceListVO> dsServiceListDTOList = pageResult.getData();

        PageResult<List<BatchDataSourceHaveImportResultVO>> pageResultVO = new PageResult<>();
        if(CollectionUtils.isEmpty(dsServiceListDTOList)){
            BeanUtils.copyProperties(pageResult, pageResultVO);
            pageResultVO.setData(Lists.newArrayList());
            return pageResultVO;
        }
        //查询projectEngine表 看一下有没有hadoop引擎
        ProjectEngine hadoopEngine = projectEngineService.getProjectDb(vo.getProjectId(), MultiEngineType.HADOOP.getType());
        List<Long> infoIdList = dsServiceListDTOList.stream().map(DsServiceListVO::getDataInfoId).collect(Collectors.toList());
        List<BatchDataSourceCenter> dataSourceCenterList = batchDataSourceCenterDao.getDataSourceCenterByInfoIds(vo.getProjectId(), infoIdList);
        Map<Long, BatchDataSourceCenter> infoIdMappingCenterMap = dataSourceCenterList.stream().collect(Collectors.toMap(BatchDataSourceCenter::getDtCenterSourceId, Function.identity(), (key1, key2) -> key1));

        List<BatchDataSourceHaveImportResultVO> resultVOList = Lists.newArrayList();
        for (DsServiceListVO dsServiceListVO : dsServiceListDTOList){
            BatchDataSourceHaveImportResultVO resultVO = new BatchDataSourceHaveImportResultVO();
            BeanUtils.copyProperties(dsServiceListVO, resultVO);

            BatchDataSourceCenter batchDataSourceCenter = infoIdMappingCenterMap.get(dsServiceListVO.getDataInfoId());
            if(batchDataSourceCenter == null){
                throw new RdosDefineException("数据源中心引入的数据源，未在离线中找到！名称：" + dsServiceListVO.getDataName());
            }
            resultVO.setId(batchDataSourceCenter.getId());
            resultVO.setIsDefault(batchDataSourceCenter.getIsDefault());

            Integer sourceRefCount = batchDataSourceTaskRefService.getSourceRefCount(batchDataSourceCenter.getId());
            if(sourceRefCount > 0){
                resultVO.setActive(1);
            } else {
                resultVO.setActive(0);
            }

//            //查询数据源映射信息
//            BatchTestProduceDataSource batchTestProduceDataSource = batchTestProduceDataSourceDao.getBySourceIdOrLinkSourceId(batchDataSourceCenter.getId());
//            resultVO.setLinkStatus(0);
//            if(batchTestProduceDataSource != null){
//                resultVO.setLinkStatus(1);
//                if(resultVO.getId().equals(batchTestProduceDataSource.getProduceDataSourceId())){
//                    resultVO.setLinkSourceId(batchTestProduceDataSource.getTestDataSourceId());
//                } else{
//                    resultVO.setLinkSourceId(batchTestProduceDataSource.getProduceDataSourceId());
//                }
//            }
//
//            resultVO.setHasHadoopEngine(!Objects.isNull(hadoopEngine));
            resultVOList.add(resultVO);
        }
        BeanUtils.copyProperties(pageResult, pageResultVO);
        pageResultVO.setData(resultVOList);
        return pageResultVO;
    }

    /**
     * 根据离线数据源id 查询 数据源中心的id
     * @param sourceId
     * @return
     */
    public Long getInfoIdByCenterId(Long sourceId) {
        return batchDataSourceCenterDao.getInfoIdByCenterId(sourceId);
    }

    /**
     * 根据数据源中心id获取默认数据源对应的projectId
     * @param dataInfoId
     * @return
     */
    public BatchDataSourceCenter getDefaultDataSourceCenterByDataInfoId(Long dataInfoId) {
        BatchDataSourceCenter batchDataSourceCenter = batchDataSourceCenterDao.getDefaultDataSourceCenterByDataInfoId(dataInfoId);
        if (batchDataSourceCenter == null) {
            throw new RdosDefineException("查询不到对应的默认数据源，dataInfoId：" + dataInfoId);
        }
        return batchDataSourceCenter;
    }

}
