package com.dtstack.batch.engine.hdfs.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.dtstack.batch.bo.ExecuteContent;
import com.dtstack.engine.api.domain.BatchDataSource;
import com.dtstack.engine.api.domain.BatchTask;
import com.dtstack.engine.api.domain.ScheduleEngineProject;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.batch.common.exception.RdosDefineException;
import com.dtstack.batch.dao.ProjectEngineDao;
import com.dtstack.batch.domain.*;
import com.dtstack.batch.engine.rdbms.common.HadoopConf;
import com.dtstack.batch.engine.rdbms.common.HdfsOperator;
import com.dtstack.batch.enums.TableRelationType;
import com.dtstack.batch.enums.TaskOperateType;
import com.dtstack.batch.mapping.TaskTypeEngineTypeMapping;
import com.dtstack.batch.service.datasource.impl.BatchDataSourceService;
import com.dtstack.batch.service.impl.BatchSqlExeService;
import com.dtstack.batch.service.job.IBatchJobExeService;
import com.dtstack.batch.service.task.impl.BatchTaskParamService;
import com.dtstack.batch.service.task.impl.BatchTaskResourceShadeService;
import com.dtstack.batch.sync.job.PluginName;
import com.dtstack.batch.sync.job.SourceType;
import com.dtstack.batch.vo.CheckSyntaxResult;
import com.dtstack.batch.vo.ExecuteResultVO;
import com.dtstack.batch.vo.ExecuteSqlParseVO;
import com.dtstack.dtcenter.common.enums.*;
import com.dtstack.dtcenter.common.util.Base64Util;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.dtstack.engine.api.ApiURL;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * hadoop 相关类型Job执行
 * Date: 2019/5/17
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

@Service
public class BatchHadoopJobExeService implements IBatchJobExeService {

    private static final Logger LOG = LoggerFactory.getLogger(BatchHadoopJobExeService.class);

    @Autowired
    private BatchHadoopSelectSqlService batchHadoopSelectSqlService;

    @Autowired
    private BatchDataSourceService batchDataSourceService;

    @Autowired
    private BatchTaskResourceShadeService batchTaskResourceShadeService;

    @Autowired
    private BatchTaskParamService batchTaskParamService;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private BatchSqlExeService batchSqlExeService;

    @Autowired
    private ProjectEngineDao projectEngineDao;

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private static final String JOB_ARGS_TEMPLATE = "-jobid %s -job %s";

    private static final String ADD_FILE_FORMAT = "ADD JAR WITH %s AS %s;";

    private static final String EXT_REF_RESOURCE_ARGS_TMPL = " extRefResource %s ";

    private static final String OPERATE_MODEL = "operateModel";

    private static final String FILES_ARG = "--files";

    private static final String CMD_OPT = "--cmd-opts";


    /**
     * todo 需更新
     */
    private static final List<Integer> ADD_JAR_JOB_TYPE = Arrays.asList(EJobType.SPARK.getVal(), EJobType.SPARK_PYTHON.getVal(), EJobType.HADOOP_MR.getVal());

    private static final Map<Integer, String> PY_VERSION_MAP = new HashMap<>(2);

    static {
        PY_VERSION_MAP.put(2, " 2.x ");
        PY_VERSION_MAP.put(3, " 3.x ");
    }

    @Override
    public Map<String, Object> readyForSyncImmediatelyJob(BatchTask batchTask, Long dtuicTenantId, Boolean isRoot) {


        if (!batchTask.getTaskType().equals(EJobType.SYNC.getVal())) {
            throw new RdosDefineException("只支持同步任务直接运行");
        }

        Map<String, Object> actionParam = new HashMap<>(10);

        try {
            String taskParams = batchTask.getTaskParams();
            List<BatchTaskParam> taskParamsToReplace = batchTaskParamService.getTaskParam(batchTask.getId());

            JSONObject syncJob = JSON.parseObject(Base64Util.baseDecode(batchTask.getSqlText()));
            taskParams = replaceSyncParll(taskParams, parseSyncChannel(syncJob));

            String job = syncJob.getString("job");

            // 向导模式根据job中的sourceId填充数据源信息，保证每次运行取到最新的连接信息
            job = batchDataSourceService.setJobDataSourceInfo(job, dtuicTenantId, syncJob.getIntValue("createModel"));
            // 获取脏数据存储路径
            //todo skip
//            job = batchHadoopDirtyDataService.replaceTablePath(false, job, batchTask.getId(), batchTask.getName(), batchTask.getCreateUserId(), batchTask.getTenantId(), batchTask.getProjectId(), isRoot, actionParam);

            batchTaskParamService.checkParams(batchTaskParamService.checkSyncJobParams(job), taskParamsToReplace);

            String name = "run_sync_task_" + batchTask.getName() + "_" + System.currentTimeMillis();
            String taskExeArgs = String.format(JOB_ARGS_TEMPLATE, name, job);
            actionParam.put("taskSourceId",batchTask.getId());
            actionParam.put("taskType", EJobType.SYNC.getVal());
            actionParam.put("name", name);
            actionParam.put("computeType", batchTask.getComputeType());
            actionParam.put("sqlText", "");
            actionParam.put("taskParams", taskParams);
            actionParam.put("tenantId", dtuicTenantId);
            actionParam.put("sourceType", SourceType.TEMP_QUERY.getType());
            actionParam.put("isFailRetry", false);
            actionParam.put("maxRetryNum", 0);
            actionParam.put("job", job);
            MultiEngineType multiEngineType = TaskTypeEngineTypeMapping.getEngineTypeByTaskType(batchTask.getTaskType());
            actionParam.put("multiEngineType", multiEngineType.getType());
            actionParam.put("taskParamsToReplace", JSON.toJSONString(taskParamsToReplace));
            DataSourceType writerDataSourceType = getSyncJobWriterDataSourceType(job);
            if (Objects.nonNull(writerDataSourceType)) {
                actionParam.put("dataSourceType", writerDataSourceType.getVal());
            }
            if (taskExeArgs != null) {
                actionParam.put("exeArgs", taskExeArgs);
            }

        } catch (Exception e) {
            LOG.error("", e);
            throw new RdosDefineException("创建数据同步job失败:" + e.getMessage());
        }

        return actionParam;
    }

    /**
     * 获取数据同步写入插件的数据源类型
     * 注意：目前只调整Inceptor类型，其他数据源类型没有出现问题，不进行变动
     *
     * @param jobStr
     * @return
     */
    public DataSourceType getSyncJobWriterDataSourceType(String jobStr) {
        JSONObject job = JSONObject.parseObject(jobStr);
        JSONObject jobContent = job.getJSONObject("job");
        JSONObject content = jobContent.getJSONArray("content").getJSONObject(0);
        JSONObject writer = content.getJSONObject("writer");
        String writerName = writer.getString("name");
        switch (writerName) {
            case PluginName.INCEPTOR_W:
                return DataSourceType.INCEPTOR;
            default:
                return null;
        }
    }

    @Override
    public ExecuteResultVO startSqlImmediately(Long userId, Long tenantId,
                                               String uniqueKey, long projectId,
                                               long taskId, String sql,
                                               Boolean isRoot, Long dtuicTenantId,
                                               BatchTask task, String dtToken,
                                               Boolean isEnd,
                                               String jobId) throws Exception {
        ExecuteResultVO result;
        if (EJobType.SPARK_SQL.getVal().equals(task.getTaskType())
                || EJobType.HIVE_SQL.getVal().equals(task.getTaskType())
        ) {
            ExecuteContent content = new ExecuteContent();
            content.setTenantId(tenantId).setProjectId(projectId).setUserId(userId).setSql(sql).setRelationId(taskId)
                    .setRelationType(TableRelationType.TASK.getType()).setDetailType(task.getTaskType())
                    .setRootUser(isRoot).setCheckSyntax(environmentContext.getExplainEnable()).setIsdirtyDataTable(false).setSessionKey(uniqueKey).setEnd(isEnd)
                    .setEngineType(MultiEngineType.HADOOP.getType()).setTableType(ETableType.HIVE.getType()).setPreJobId(jobId);


            result = batchSqlExeService.executeSql(content);
        } else {

            throw new RdosDefineException("不支持" + EJobType.getEJobType(task.getTaskType()).getName() + "类型的任务直接运行");
        }

        return result;
    }

    /**
     * 高级运行-sparkSQl不允许直连spark ThriftServer需要通过引擎执行
     * @param userId
     * @param tenantId
     * @param uniqueKey
     * @param projectId
     * @param taskId
     * @param sqlList
     * @param isRoot
     * @param dtuicTenantId
     * @param task
     * @param dtToken
     * @return
     */
    @Override
    public ExecuteSqlParseVO startSqlSophisticated(Long userId, Long tenantId, String uniqueKey, long projectId, long taskId, List<String> sqlList, Boolean isRoot, Long dtuicTenantId, BatchTask task, String dtToken, String database)throws Exception {
        if (task.getTaskType().equals(EJobType.SPARK_SQL.getVal())){
            ExecuteContent content = new ExecuteContent();
            content.setTenantId(tenantId).setProjectId(projectId).setUserId(userId).setSqlList(sqlList).setRelationId(taskId)
                    .setRelationType(TableRelationType.TASK.getType()).setDetailType(task.getTaskType())
                    .setRootUser(isRoot).setCheckSyntax(environmentContext.getExplainEnable()).setIsdirtyDataTable(false).setSessionKey(uniqueKey)
                    .setEngineType(MultiEngineType.HADOOP.getType()).setTableType(ETableType.HIVE.getType()).setDtuicTenantId(dtuicTenantId).setDatabase(database);
            return batchSqlExeService.batchExeSqlParse(content);
        }
        return new ExecuteSqlParseVO();
    }

    @Override
    public void readyForTaskStartTrigger(Map<String, Object> actionParam, Long dtuicTenantId, ScheduleEngineProject project, BatchTask batchTask, List<BatchTaskParamShade> taskParamsToReplace) throws Exception {

        String sql = batchTask.getSqlText();
        sql = sql == null ? "" : sql;

        String taskParams = batchTask.getTaskParams();

        String taskExeArgs = null;

        List<BatchResource> resourceList = batchTaskResourceShadeService.listResourceByTaskId(batchTask.getId(), ResourceRefType.MAIN_RES.getType(), batchTask.getProjectId());
        final List<BatchResource> extResourceList = batchTaskResourceShadeService.listResourceByTaskId(batchTask.getId(), ResourceRefType.DEPENDENCY_RES.getType(), batchTask.getProjectId());

        if (EJobType.SPARK_SQL.getVal().equals(batchTask.getTaskType()) || EJobType.HIVE_SQL.getVal().equals(batchTask.getTaskType())) {
            sql = String.format("set hive.default.fileformat=%s;\n ",environmentContext.getCreateTableType())+sql;
            batchTaskParamService.checkParams(sql, taskParamsToReplace);
            // 处理多条sql
            CheckSyntaxResult result = batchSqlExeService.processSqlText(dtuicTenantId, batchTask.getTaskType(), sql, batchTask.getCreateUserId(), project.getUicTenantId(),
                    project.getId(), false, Boolean.FALSE, MultiEngineType.HADOOP.getType(), taskParams);
            sql = result.getSql();
            if (EJobType.HIVE_SQL.getVal().equals(batchTask.getTaskType())) {
                sql = sql.replace("\n", "");
            }
        } else if (batchTask.getTaskType().equals(EJobType.SYNC.getVal())) {
            JSONObject syncJob = JSON.parseObject(Base64Util.baseDecode(batchTask.getSqlText()));
            taskParams = replaceSyncParll(taskParams, parseSyncChannel(syncJob));

            String job = syncJob.getString("job");

            // 向导模式根据job中的sourceId填充数据源信息，保证每次运行取到最新的连接信息
            job = batchDataSourceService.setJobDataSourceInfo(job, dtuicTenantId, syncJob.getIntValue("createModel"));

            // 获取脏数据存储路径
            //todo skip dirtyData
//            job = batchHadoopDirtyDataService.replaceTablePath(true, job, batchTask.getId(), batchTask.getName(), batchTask.getCreateUserId(), batchTask.getTenantId(), batchTask.getProjectId(), Boolean.FALSE, actionParam);

            batchTaskParamService.checkParams(batchTaskParamService.checkSyncJobParams(job), taskParamsToReplace);
            actionParam.put("job", job);
            //需要添加额外脏数据信息
            ProjectEngine projectDb = projectEngineDao.getByProjectAndEngineType(project.getId(), MultiEngineType.HADOOP.getType());
            if (Objects.nonNull(projectDb)) {
                actionParam.put("engineIdentity", projectDb.getEngineIdentity());
                try {
                    Object eval = JSONPath.eval(JSON.parseObject(job), "$.job.content[0].writer.parameter.sourceIds[0]");
                    final String sourceId = eval==null?null:eval.toString();
                    if (StringUtils.isNotBlank(sourceId)) {
                        BatchDataSource writeDataSource = batchDataSourceService.getOne(Long.valueOf(sourceId));
                        if (Objects.nonNull(writeDataSource)) {
                            actionParam.put("dataSourceType", writeDataSource.getType());
                        }
                    }
                } catch (Exception e) {
                    LOG.info("get write datasource error {} ", job, e);
                    actionParam.put("dataSourceType", DataSourceType.HIVE.getVal());
                }
            }
        }

        if (EJobType.SPARK_SQL.getVal().equals(batchTask.getTaskType())) {
            //sparkSql已经参数替换过
        } else if (batchTask.getEngineType().equals(EngineType.Learning.getVal())
                || batchTask.getEngineType().equals(EngineType.Shell.getVal())
                || batchTask.getEngineType().equals(EngineType.DtScript.getVal())
                || batchTask.getEngineType().equals(EngineType.Spark.getVal())
                || batchTask.getEngineType().equals(EngineType.Hadoop.getVal())
                || batchTask.getEngineType().equals(EngineType.Python2.getVal())
                || batchTask.getEngineType().equals(EngineType.Python3.getVal())) {
            taskParams = formatLearnTaskParams(batchTask.getTaskParams());
            //替换系统参数
            batchTaskParamService.checkParams(batchTask.getSqlText(), taskParamsToReplace);
            taskExeArgs = buildExeArgs(dtuicTenantId, batchTask.getExeArgs(), batchTask.getTaskType(), batchTask.getEngineType(), batchTask.getName(),
                    batchTask.getSqlText(), resourceList, extResourceList, batchTask.getTenantId(), batchTask.getProjectId(), ApiURL.JOB_ID,
                    batchTask.getCreateUserId(), taskParamsToReplace);
            if (batchTask.getEngineType().equals(EngineType.Spark.getVal())){
                if (CollectionUtils.isNotEmpty(resourceList) && StringUtils.isBlank(resourceList.get(0).getUrl())){
                    LOG.error(String.format("任务= %s 运行时依赖的jar包未找到 taskId= %s,projectId= %s",batchTask.getName(),batchTask.getId(),project.getId()));
                    resourceList = batchTaskResourceShadeService.listResourceByTaskId(batchTask.getId(), ResourceRefType.MAIN_RES.getType(), batchTask.getProjectId());
                    if (CollectionUtils.isEmpty(resourceList)){
                        BatchHadoopJobExeService.LOG.error(String.format("任务= %s 运行时依赖的jar包未找到,再次查询时仍未找到 taskId= %s,projectId= %s",batchTask.getName(),batchTask.getId(),project.getId()));
                        throw new RdosDefineException("spark jar not find");
                    }else {
                        String url = resourceList.get(0).getUrl();
                        LOG.info("任务id:{},名称:{}再次查询获取到的资源url为空",batchTask.getId(),batchTask.getName());
                        if(StringUtils.isEmpty(url)){
                            throw new RdosDefineException("spark任务"+batchTask.getName()+"的资源为空，提交失败");
                        }
                    }
                }
            }
        }

        sql = setAddJarSql(batchTask.getTaskType(), batchTask.getMainClass(), resourceList, sql);

        if (taskExeArgs != null) {
            actionParam.put("exeArgs", taskExeArgs);
        }

        actionParam.put("sqlText", sql);
        actionParam.put("taskParams", taskParams);
    }

    public String setAddJarSql(Integer taskType, String mainClass, List<BatchResource> resourceList, String sql) {
        if (EJobType.SPARK.getVal().equals(taskType)) {
            if (resourceList.size() != 1) {
                //批处理MR必须关联一个资源
                throw new RdosDefineException("batch job ref resource size must be one");
            }
        }
        if (ADD_JAR_JOB_TYPE.contains(taskType)) {
            if (CollectionUtils.isNotEmpty(resourceList)) {
                String url = resourceList.get(0).getUrl();
                LOG.info("资源url为：{}",url);
                String formattedSql = formatAddJarSQL(url, mainClass);
                LOG.info("格式化后的任务sqlText为:{}",formattedSql);
                return formattedSql;
            }else {
                LOG.info("资源为空");
            }
        } else if (taskType.equals(EJobType.SYNC.getVal())) {
            return "";
        }
        LOG.info("addJarSql内容为：",sql);
        return sql;
    }

    private String formatLearnTaskParams(String taskParams) {
        List<String> params = new ArrayList<>();

        for (String param : taskParams.split("\r|\n")) {
            if (StringUtils.isNotEmpty(param.trim()) && !param.trim().startsWith("#")) {
                String[] parts = param.split("=");
                if (parts.length < 2) {
                    continue;
                }
                params.add(param.trim());
            }
        }

        return StringUtils.join(params, "\n");
    }

    private String formatAddJarSQL(String url, String mainClass) {
        return String.format(ADD_FILE_FORMAT, url, mainClass);
    }

    private String generateResource(List<BatchResource> resourceList) {
        if (CollectionUtils.isEmpty(resourceList)) {
            return "";
        }

        List<String> resourceInfo = Lists.newArrayList();
        resourceList.forEach(res -> resourceInfo.add(res.getUrl()));
        return String.join(",", resourceInfo);
    }

    private Integer parseSyncChannel(JSONObject syncJob) {
        //解析出并发度---sync 消耗资源是: 并发度*1
        try {
            JSONObject jobJson = syncJob.getJSONObject("job").getJSONObject("job");
            JSONObject settingJson = jobJson.getJSONObject("setting");
            JSONObject speedJson = settingJson.getJSONObject("speed");
            return speedJson.getInteger("channel");
        } catch (Exception e) {
            LOG.error("", e);
            //默认1
            return 1;
        }

    }

    public String buildExeArgs(Long dtuicTenantId, String exeArgs, Integer taskType, Integer engineType, String taskName, String content,
                               List<BatchResource> resourceList, List<BatchResource> extResource, Long tenantId, Long projectId, String jobId, Long userId,
                               List<BatchTaskParamShade> taskParamsToReplace) {
        JSONObject exeArgsJson = JSON.parseObject(exeArgs);
        if (null == exeArgsJson) {
            return "";
        }
        String componentType = exeArgsJson.getString("componentType");
        //init 执行参数
        setCmdOpt(exeArgsJson, exeArgs, jobId, componentType, userId, projectId, tenantId, dtuicTenantId);
        //init 执行路径
        String fileDir = setFileDir(exeArgsJson, componentType, resourceList, content, taskType, taskName, projectId, tenantId, dtuicTenantId,jobId);

        exeArgsJson = collectArgs(exeArgsJson, taskType, engineType, extResource, resourceList, fileDir, taskParamsToReplace);

        List<String> exeArgsList = new ArrayList<>();
        exeArgsJson.forEach((key, value) -> {
            exeArgsList.add(String.format("%s %s", key, value));
        });

        //处理一下 前后空格，因为如果有空格 engine运行失败
        StringBuffer resultString = new StringBuffer();
        exeArgsList.forEach(bean->{resultString.append(bean.trim()).append(" ");});
        return resultString.toString().trim();
    }

    private void setCmdOpt(JSONObject exeArgsJson, String exeArgs, String jobId, String componentType, Long userId, Long projectId, Long tenantId, Long dtuicTenantId) {
        if (StringUtils.isEmpty(exeArgsJson.getString(CMD_OPT))) {
            exeArgsJson.remove("--cmd-opts");
        }
    }

    private String setFileDir(JSONObject exeArgsJson, String componentType, List<BatchResource> resourceList, String content, Integer taskType, String taskName, Long projectId, Long tenantId, Long dtuicTenantId,
                              String jobId) {
        String fileDir;
        if (exeArgsJson.getInteger(OPERATE_MODEL) == null || TaskOperateType.RESOURCE.getType() == exeArgsJson.getInteger(OPERATE_MODEL)) {
            // 资源模式 获取资源的路径
            fileDir = generateResource(resourceList);
        } else {
            if (ApiURL.JOB_ID.equals(jobId)) {
                // web编辑 模式 需要 到task 替换参数之后 在上传 这里先用占位符
                fileDir = ApiURL.UPLOADPATH;
            } else {
                // 临时运行
                fileDir = uploadSqlTextToHdfs(dtuicTenantId, content, taskType, taskName, tenantId, projectId);
                if(Objects.isNull(resourceList)){
                    resourceList = new ArrayList<>();
                }
                resourceList.add(new BatchResource(fileDir));
            }
        }
        BatchHadoopJobExeService.LOG.info("fileDir:{}",fileDir);
        return fileDir;
    }

    public String uploadSqlTextToHdfs(Long dtuicTenantId, String content, Integer taskType, String taskName, Long tenantId, Long projectId) {
        String hdfsPath = null;
        try {
            // shell任务，创建脚本文件
            String fileName = null;
            if (taskType.equals(EJobType.SHELL.getVal())) {
                fileName = String.format("shell_%s_%s_%s_%s.sh", tenantId, projectId,
                        taskName, System.currentTimeMillis());
            } else if (taskType.equals(EJobType.PYTHON.getVal())) {
                fileName = String.format("python_%s_%s_%s_%s.py", tenantId, projectId,
                        taskName, System.currentTimeMillis());
            } else if (taskType.equals(EJobType.SPARK_PYTHON.getVal())) {
                fileName = String.format("pyspark_%s_%s_%s_%s.py", tenantId, projectId,
                        taskName, System.currentTimeMillis());
            }

            if (fileName != null) {
                hdfsPath = environmentContext.getHdfsBatchPath() + fileName;
                if (taskType.equals(EJobType.SHELL.getVal())) {
                    content = content.replaceAll("\r\n", System.getProperty("line.separator"));
                }
                Map<String, Object> configuration = HadoopConf.getConfiguration(dtuicTenantId);
                Map<String, Object> kerberosConf = HadoopConf.getHadoopKerberosConf(dtuicTenantId);
                HdfsOperator.uploadInputStreamToHdfs(configuration,kerberosConf, content.getBytes(), hdfsPath);
            }
        } catch (Exception e) {
            LOG.error("", e);
            throw new RdosDefineException("Update task to HDFS failure:" + e.getMessage());
        }

        return HadoopConf.getDefaultFs(dtuicTenantId) + hdfsPath;
    }

    private JSONObject collectArgs(JSONObject exeArgsJson, Integer taskType, Integer engineType, List<BatchResource> extResource, List<BatchResource> resourceList, String fileDir,
                                   List<BatchTaskParamShade> taskParamsToReplace) {
        exeArgsJson = rmUnConcerned(exeArgsJson);
        if (EJobType.SPARK.getVal().equals(taskType) ||
                EJobType.SPARK_PYTHON.getVal().equals(taskType) || EJobType.HADOOP_MR.getVal().equals(taskType)) {
            //spark类型参数直接以空格分割
            String opt = exeArgsJson.getString(CMD_OPT);
            //spark remove cmd
            exeArgsJson.put("",opt);
            exeArgsJson.remove(CMD_OPT);
            //添加引用资源
            String extRefDir = generateResource(extResource);
            if (StringUtils.isNotBlank(extRefDir)) {
                exeArgsJson.put(EXT_REF_RESOURCE_ARGS_TMPL, extRefDir);
            }
            //保证addJar有可用资源
            if (CollectionUtils.isEmpty(resourceList)) {
                if (Objects.isNull(resourceList)) {
                    resourceList = new ArrayList<>();
                }
                BatchHadoopJobExeService.LOG.info("根据fileDir创建resource，fileDir:{}",fileDir);
                resourceList.add(new BatchResource(fileDir));
            }
        } else {
            exeArgsJson.put(FILES_ARG, fileDir);
            String opt = exeArgsJson.getString(CMD_OPT);
            if (StringUtils.isNotBlank(opt)) {
                exeArgsJson.put(CMD_OPT, Base64Util.baseEncode(opt));
            }
            setPythonVersion(exeArgsJson);
        }
        return exeArgsJson;
    }

    private JSONObject rmUnConcerned(JSONObject exeArgsJson) {
        JSONObject obj = new JSONObject();
        for (Map.Entry<String, Object> entry : exeArgsJson.entrySet()) {
            if (entry.getKey().startsWith("--")) {
                obj.put(entry.getKey(), entry.getValue());
            }
        }
        return obj;
    }

    public String replaceSyncParll(String taskParams, int parallelism) throws IOException {
        Properties properties = new Properties();
        properties.load(new ByteArrayInputStream(taskParams.getBytes(StandardCharsets.UTF_8)));
        properties.put("mr.job.parallelism", parallelism);
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Object, Object> tmp : properties.entrySet()) {
            sb.append(tmp.getKey())
                    .append(" = ")
                    .append(tmp.getValue())
                    .append(LINE_SEPARATOR);
        }
        return sb.toString();
    }

    /**
     * 1.兼容之前配置
     * like：
     * --app-type python --python-version 2
     * 2.转换深度学习pythonVersion为 2.x\3.x
     */
    private void setPythonVersion(JSONObject exeArgsJson) {
        String appType = exeArgsJson.getString("--app-type");
        if (Objects.equals(appType, "python")) {
            int pyVersion = exeArgsJson.getIntValue("--python-version");
            if (pyVersion == 0) {
                exeArgsJson.put("--app-type", EngineType.Python3.getVal());
            } else {
                exeArgsJson.put("--app-type", EngineType.getByPythonVersion(pyVersion).getEngineName());
            }
        }
    }
}
