package com.dtstack.batch.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.batch.common.exception.ErrorCode;
import com.dtstack.batch.common.exception.RdosDefineException;
import com.dtstack.batch.dao.*;
import com.dtstack.batch.domain.*;
import com.dtstack.batch.dto.BatchFunctionDTO;
import com.dtstack.batch.engine.rdbms.common.util.SqlFormatUtil;
import com.dtstack.batch.mapping.TaskTypeEngineTypeMapping;
import com.dtstack.batch.service.table.IFunctionService;
import com.dtstack.batch.service.task.impl.BatchTaskService;
import com.dtstack.batch.vo.BatchFunctionVO;
import com.dtstack.batch.vo.TaskCatalogueVO;
import com.dtstack.batch.web.pager.PageQuery;
import com.dtstack.batch.web.pager.PageResult;
import com.dtstack.dtcenter.common.constant.PatternConstant;
import com.dtstack.dtcenter.common.enums.*;
import com.dtstack.dtcenter.common.exception.DtCenterDefException;
import com.dtstack.dtcenter.common.util.PublicUtil;
import com.dtstack.engine.api.domain.Tenant;
import com.dtstack.engine.api.domain.User;
import com.dtstack.engine.master.impl.TenantService;
import com.dtstack.engine.master.impl.UserService;
import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by jiangbo on 2017/5/8 0008.
 */
@Service
public class BatchFunctionService {

    private static final Logger logger = LoggerFactory.getLogger(BatchFunctionService.class);

    @Autowired
    private BatchFunctionDao batchFunctionDao;

    @Autowired
    private BatchFunctionResourceDao batchResourceFunctionDao;

    @Autowired
    private UserService userService;

    @Autowired
    private BatchResourceService batchResourceService;

    @Autowired
    private BatchTaskService batchTaskService;

    @Autowired
    private ProjectEngineService projectEngineService;

    @Autowired
    private MultiEngineServiceFactory multiEngineServiceFactory;

    @Autowired
    private BatchCatalogueDao batchCatalogueDao;

    @Autowired
    private TenantService tenantService;

    /**
     * 系统函数缓存
     */
    private Cache<String, Map<String, BatchFunction>> systemFunctions = CacheBuilder.newBuilder().expireAfterWrite(30 * (long)60, TimeUnit.SECONDS).maximumSize(10).build();

    private static String system = "systemFunctions";

    // 创建临时函数
    private static final String CREATE_TEMP_FUNCTION = "create temporary function %s as '%s' using jar '%s';";

    // 删除函数打印使用
    private static final String LOGGER_DELETE_FUNCTION = "[id : %s , name : %s]";


    @PostConstruct
    public void init() {
        systemFunctions.put(system, getSystemFunctions());
    }

    /**
     * 根据id获取函数
     */
    public BatchFunctionVO getFunction(Long functionId) {
        BatchFunction batchFunction = batchFunctionDao.getOne(functionId);
        if (Objects.isNull(batchFunction)) {
            return new BatchFunctionVO();
        }
        BatchFunctionVO vo = BatchFunctionVO.toVO(batchFunction);
        BatchFunctionResource resourceFunctionByFunctionId = batchResourceFunctionDao.getResourceFunctionByFunctionId(batchFunction.getId());
        if (Objects.nonNull(resourceFunctionByFunctionId)){
            vo.setResources(resourceFunctionByFunctionId.getResourceId());
        }
        vo.setCreateUser(userService.getById(batchFunction.getCreateUserId()));
        vo.setModifyUser(userService.getById(batchFunction.getModifyUserId()));
        return vo;
    }

    public Map<String, BatchFunction> getSystemFunctions() {
        Map<String, BatchFunction> bfs = systemFunctions.getIfPresent(system);
        if (bfs == null || bfs.size() == 0) {
            List<BatchFunction> bf = batchFunctionDao.listSystemFunction(null);
            bfs = Maps.newConcurrentMap();
            for (BatchFunction bb : bf) {
                bfs.put(bb.getName(), bb);
            }
            systemFunctions.put(system, bfs);
        }
        return bfs;
    }

    /**
     * 默认engineIdentity
     * @param projectId
     * @param engineType
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public String getEngineIdentity(Long projectId, Integer engineType) {
        ProjectEngine projectEngine = projectEngineService.getProjectDb(projectId, engineType);
        Preconditions.checkNotNull(projectEngine, String.format("project %d not support engine %d.", projectId, engineType));
        return projectEngine.getEngineIdentity();
    }

    /**
     * 添加函数
     */
    @Transactional(rollbackFor = Exception.class)
    public TaskCatalogueVO addOrUpdateFunction(BatchFunction batchFunction, String resourceIds, Long dtuicTenantId) {
        if (!PublicUtil.matcher(batchFunction.getName(), PatternConstant.FUNCTIONPATTERN)) {
            throw new RdosDefineException("注意名称只允许存在字母、数字、下划线、横线，hive函数不支持大写字母", ErrorCode.NAME_FORMAT_ERROR);
        }
        if (StringUtils.isEmpty(resourceIds)) {
            throw new RdosDefineException("新增函数必须添加资源", ErrorCode.INVALID_PARAMETERS);
        } else {
            checkResourceType(resourceIds, ResourceType.JAR.getType());
        }
        BatchCatalogue parentNode = batchCatalogueDao.getOne(batchFunction.getNodePid());
        if (null != parentNode && parentNode.getEngineType() > 0 && batchFunction.getEngineType() == null) {
            batchFunction.setEngineType(parentNode.getEngineType());
        }
        try {
			ProjectEngine projectEngine = projectEngineService.getProjectDb(batchFunction.getProjectId(), batchFunction.getEngineType());
			Preconditions.checkNotNull(projectEngine, String.format("project %d not support engine %d.", batchFunction.getProjectId(), batchFunction.getEngineType()));

			// id小于0走新增逻辑
			if (Objects.isNull(batchFunction.getId()) || batchFunction.getId() < 1) {
                //名称重复校验
                batchTaskService.checkName(batchFunction.getName(), CatalogueType.CUSTOM_FUNCTION.name(), null, 1, batchFunction.getProjectId());
                batchFunction.setGmtCreate(Timestamp.valueOf(LocalDateTime.now()));
			}

			batchFunction.setType(FuncType.CUSTOM.getType());
			batchFunction.setGmtModified(Timestamp.valueOf(LocalDateTime.now()));
			addOrUpdate(batchFunction);
            addOrUpdateFunctionResource(batchFunction, Long.valueOf(resourceIds));
			// 添加类目关系
			TaskCatalogueVO taskCatalogueVO = new TaskCatalogueVO();
			taskCatalogueVO.setId(batchFunction.getId());
			taskCatalogueVO.setName(batchFunction.getName());
			taskCatalogueVO.setType("file");
			taskCatalogueVO.setLevel(null);
			taskCatalogueVO.setChildren(null);
			taskCatalogueVO.setParentId(batchFunction.getNodePid());

            String username = userService.getUserName(batchFunction.getCreateUserId());
			taskCatalogueVO.setCreateUser(username);
			return taskCatalogueVO;
		} catch (Exception e) {
            logger.error("addFunction, functions={},resource={},uicTenantId={}", JSONObject.toJSONString(batchFunction), resourceIds, dtuicTenantId);
            logger.error(e.getMessage(), e);
            if (e instanceof RdosDefineException) {
                throw e;
            } else {
               throw new RdosDefineException("添加函数失败" + e.getMessage());
            }
		}
    }

    /**
     * 添加或更新函数资源关联关系
     *
     * @param function
     * @param resourceId
     */
    private void addOrUpdateFunctionResource(BatchFunction function, Long resourceId) {
        BatchFunctionResource batchFunctionResource = new BatchFunctionResource();
        batchFunctionResource.setFunctionId(function.getId());
        batchFunctionResource.setProjectId(function.getProjectId());
        batchFunctionResource.setTenantId(function.getTenantId());
        batchFunctionResource.setResourceId(resourceId);
        batchFunctionResource.setResource_Id(resourceId);
        batchFunctionResource.setIsDeleted(0);
        batchFunctionResource.setGmtModified(new Timestamp(System.currentTimeMillis()));
        BatchFunctionResource resourceFunctionByFunctionId = getResourceFunctionByFunctionId(function.getId());
        if (Objects.isNull(resourceFunctionByFunctionId)) {
            batchFunctionResource.setGmtCreate(new Timestamp(System.currentTimeMillis()));
            batchResourceFunctionDao.insert(batchFunctionResource);
        }else {
            batchResourceFunctionDao.updateByFunctionId(batchFunctionResource);
        }
    }

    /**
     * 根据函数id获取资源函数关系
     * @param functionId
     * @return
     */
    private BatchFunctionResource getResourceFunctionByFunctionId(Long functionId) {
        return batchResourceFunctionDao.getResourceFunctionByFunctionId(functionId);
    }


    @Transactional(rollbackFor = Exception.class)
    public void deleteByProjectId(Long projectId, Long userId) {
        batchFunctionDao.deleteByProjectId(projectId, userId);
        batchResourceFunctionDao.deleteByProjectId(projectId);
    }

    private void checkResourceType(String resources, int resourceType) {
        List<Long> resourceIdList = stringToList(resources);
        List<BatchResource> resourceList = batchResourceService.getResourceList(resourceIdList);
        if (resourceList == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_RESOURCE);
        }
    }

    public BatchFunction addOrUpdate(BatchFunction batchFunction) {
        if (batchFunction.getId() > 0) {
            batchFunctionDao.update(batchFunction);
        } else {
            batchFunctionDao.insert(batchFunction);
        }

        return batchFunction;
    }

    /**
     * 移动函数
     */
    public void moveFunction(Long userId, Long functionId, Long nodePid) {

        BatchFunction bf = batchFunctionDao.getOne(functionId);
        if (bf == null) {
            throw new RdosDefineException(ErrorCode.FUNCTION_CAN_NOT_FIND);
        }
        if (FuncType.SYSTEM.getType().equals(bf.getType())) {
            throw new RdosDefineException(ErrorCode.SYSTEM_FUNCTION_CAN_NOT_MODIFY);
        }
        bf = new BatchFunction();
        bf.setId(functionId);
        bf.setNodePid(nodePid);
        bf.setModifyUserId(userId);
        bf.setGmtModified(Timestamp.valueOf(LocalDateTime.now()));
        addOrUpdate(bf);
    }

    /**
     * 删除函数
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteFunction(Long userId, Long projectId, Long functionId, Long dtuicTenantId) {
        BatchFunction bf = batchFunctionDao.getOne(functionId);
        if (bf == null) {
            throw new RdosDefineException(ErrorCode.FUNCTION_CAN_NOT_FIND);
        }

        if (FuncType.SYSTEM.getType().equals(bf.getType())) {
            throw new RdosDefineException(ErrorCode.SYSTEM_FUNCTION_CAN_NOT_MODIFY);
        }

        Integer engineType = bf.getEngineType();
        try {
            if (engineType.intValue() == MultiEngineType.GREENPLUM.getType()) {
                this.deleteGreenplumFunction(projectId, dtuicTenantId, bf);
            }
        } catch (DtCenterDefException e) {
                throw e;
        } catch (Exception e) {
            throw new RdosDefineException("删除函数出错");
        }
        if (engineType.intValue() != MultiEngineType.GREENPLUM.getType()) {
            batchResourceFunctionDao.deleteByFunctionId(functionId);
        }
        bf = new BatchFunction();
        bf.setId(functionId);
        bf.setIsDeleted(Deleted.DELETED.getStatus());
        bf.setModifyUserId(userId);
        bf.setGmtModified(Timestamp.valueOf(LocalDateTime.now()));
        addOrUpdate(bf);
    }

    /**
     * 删除greenplum存储过程或者函数
     * @param projectId
     * @param dtuicTenantId
     * @param bf
     */
    private void deleteGreenplumFunction(Long projectId, Long dtuicTenantId, BatchFunction bf) throws Exception {
        ProjectEngine projectEngine = projectEngineService.getProjectDb(projectId, bf.getEngineType());
        Preconditions.checkNotNull(projectEngine, String.format("project %d not support engine %d.", projectId, bf.getEngineType()));
        IFunctionService functionService = multiEngineServiceFactory.getFunctionService(bf.getEngineType());
        String name = bf.getName();
        String dropSqlPre = String.format("drop function %s.%s", projectEngine.getEngineIdentity(), name);
        String sqlText = bf.getSqlText();
        String dropSqlSuf = null;
        if (FuncType.CUSTOM.getType() == bf.getType()) {
            dropSqlSuf = sqlText.substring(sqlText.indexOf("("), sqlText.indexOf(")")+1);
        } else if (FuncType.PROCEDURE.getType() == bf.getType()){
            String[] typeSqls = sqlText.substring(sqlText.indexOf("(")+1, sqlText.indexOf(")")).split(",");
            List<String> types = Lists.newArrayList();
            for (String typeSql : typeSqls) {
                typeSql = typeSql.replace("\n", "").replace("\r", "").trim();
                types.add(typeSql.substring(typeSql.lastIndexOf(" ")+1));
            }
            dropSqlSuf = "(" + StringUtils.join(types, ",") + ")";
        }
        bf.setSqlText(dropSqlPre + dropSqlSuf);
        functionService.deleteFunction(dtuicTenantId, projectEngine.getEngineIdentity(), bf.getSqlText(), projectId);
    }

    private List<Long> stringToList(String dataStr) {
        String[] dataStrArray = dataStr.split(",");
        List<Long> dataList = new ArrayList<>();
        for (String d : dataStrArray) {
            dataList.add(Long.valueOf(d));
        }

        return dataList;
    }

    public List<String> getAllFunctionName(Long tenantId, Long projectId, Integer taskType) {
        MultiEngineType engineType = TaskTypeEngineTypeMapping.getEngineTypeByTaskType(taskType);
        List<String> nameList = batchFunctionDao.listNameByProjectId(tenantId, projectId, null == engineType ? null : engineType.getType());
        List<BatchFunction> systemFunction = batchFunctionDao.listSystemFunction(null == engineType ? null : engineType.getType());
        List<String> systemNames = systemFunction.stream().map(BatchFunction::getName).collect(Collectors.toList());
        systemNames.addAll(nameList);
        return systemNames;
    }

    public boolean isEmpty(Long tenantId, Long projectId) {
        return batchFunctionDao.countByProjectIdAndType(tenantId, projectId, FunctionType.USER.getType()) == 0;
    }

    public List<BatchFunction> listProjectFunction(Long projectId, Integer functionType, Integer engineType) {
        return batchFunctionDao.listProjectFunction(projectId, functionType, engineType);
    }

    /**
     * 自定义函数分页查询
     */
    public PageResult<List<BatchFunctionVO>> pageQuery(BatchFunctionDTO functionDTO) {
        PageQuery<BatchFunctionDTO> query = new PageQuery<>(functionDTO.getPageIndex(), functionDTO.getPageSize(), "gmt_modified", functionDTO.getSort());
        query.setModel(functionDTO);

        List<BatchFunctionVO> functionVOS = new ArrayList<>();
        Integer count = batchFunctionDao.generalCount(functionDTO);
        if (count > 0) {
            List<BatchFunction> functions = batchFunctionDao.generalQuery(query);

            List<Long> userIds = new ArrayList<>();
            functions.forEach(f -> {
                userIds.add(f.getCreateUserId());
                userIds.add(f.getModifyUserId());
            });
            List<User> users = userService.listByIds(userIds);

            Map<Long, User> idUserMap = new HashMap<>();
            users.forEach(u -> {
                idUserMap.put(u.getId(), u);
            });

            BatchFunctionVO vo;
            for (BatchFunction function : functions) {
                vo = BatchFunctionVO.toVO(function);
                vo.setCreateUser(idUserMap.get(vo.getCreateUserId()));
                vo.setModifyUser(idUserMap.get(vo.getModifyUserId()));
                functionVOS.add(vo);
            }
        }

        return new PageResult<>(functionVOS, count, query);
    }


    /**
     * 处理并返回 sql 自定义函数的 SQL
     *
     * @param sql
     * @param projectId
     * @return
     */
    public List<String> buildContainFunctions(String sql, Long projectId) {
        String buildContainFunction = buildContainFunction(sql, projectId);
        if (StringUtils.isNotBlank(buildContainFunction)) {
            return Arrays.asList(buildContainFunction.split(";"));
        }
        return Lists.newArrayList();
    }


    /**
     * 判断sql是否包含自定义函数
     *
     * @param sql
     * @param projectId
     * @return
     */
    public String buildContainFunction(String sql, Long projectId) {
        StringBuilder sb = new StringBuilder();
        sql = SqlFormatUtil.formatSql(sql).toLowerCase();
        // sql中的自定义函数
        List<String> sqlFunctionNames = SqlFormatUtil.splitSqlWithoutSemi(sql);
        if (CollectionUtils.isEmpty(sqlFunctionNames)) {
            return StringUtils.EMPTY;
        }
        // 获取此项目下的自定义函数名称
        List<BatchFunction> projectCustomFunctions = listProjectFunction(projectId, FunctionType.USER.getType(), MultiEngineType.HADOOP.getType());
        if (CollectionUtils.isEmpty(projectCustomFunctions)) {
            return StringUtils.EMPTY;
        }
        List<String> customFunctionNames = projectCustomFunctions.stream().map(BatchFunction::getName).collect(Collectors.toList());
        // 循环sql中的函数判断是否是项目中的名称
        for (String sqlFunctionName : sqlFunctionNames) {
            // 如果sql中的函数存在于此项目下
            if (customFunctionNames.contains(sqlFunctionName)) {
                BatchFunction byNameAndProjectId = batchFunctionDao.getByNameAndProjectId(projectId, sqlFunctionName);
                sb.append(createTempUDF(byNameAndProjectId));
            }
        }
        return sb.toString();
    }

    /**
     * 校验是否包含了函数
     *
     * @param sql
     * @param projectId
     * @return
     */
    public boolean validContainSelfFunction(String sql, Long projectId, Integer functionType) {
        if (StringUtils.isBlank(sql) || projectId == null) {
            return false;
        }
        sql = SqlFormatUtil.formatSql(sql).toLowerCase();
        // sql中的自定义函数
        List<String> sqlFunctionNames = SqlFormatUtil.splitSqlWithoutSemi(sql);
        if (CollectionUtils.isEmpty(sqlFunctionNames)) {
            return false;
        }
        // 获取此项目下的自定义函数名称
        List<BatchFunction> projectFunctions = listProjectFunction(projectId, functionType, MultiEngineType.HADOOP.getType());
        if (CollectionUtils.isEmpty(projectFunctions)) {
            return false;
        }
        List<String> projectFunctionNames = projectFunctions.stream().map(BatchFunction::getName).collect(Collectors.toList());
        // 循环sql中的函数判断是否是项目中的名称
        for (String sqlFunctionName : sqlFunctionNames) {
            // 如果sql中的函数存在于此项目下
            if (projectFunctionNames.contains(sqlFunctionName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 创建临时函数
     *
     * @param batchFunction
     * @return
     */
    private String createTempUDF(BatchFunction batchFunction) {
        String funcName = batchFunction.getName();
        String className = batchFunction.getClassName();
        // 获取资源路径
        String resourceURL = batchResourceService.getResourceURLByFunctionId(batchFunction.getId());
        if (StringUtils.isNotBlank(resourceURL)) {
            return String.format(CREATE_TEMP_FUNCTION, funcName, className, resourceURL);
        } else {
            return StringUtils.EMPTY;
        }
    }

    /**
     * Spark UDF 脏数据处理
     */
    public String sparkUdfCleanAndPublish() {
        cleanDirtyDataFunction();
        List<Integer> taskTypes = Lists.newArrayList(EJobType.SPARK_SQL.getVal(), EJobType.HIVE_SQL.getVal());
        return batchTaskService.againPublishTask(taskTypes);
    }


    /**
     * 删除函数原来的脏数据
     */
    public void cleanDirtyDataFunction() {
        IFunctionService functionService;
        // 获取engineType为Hadoop的所有的函数
        List<BatchFunction> batchFunctions = batchFunctionDao.listProjectFunction(null, FunctionType.USER.getType(), MultiEngineType.HADOOP.getType());
        if (CollectionUtils.isEmpty(batchFunctions)) {
            return;
        }
        List<Long> projectIds = batchFunctions.stream().map(BatchFunction::getProjectId).collect(Collectors.toList());
        List<Long> tenantIds = batchFunctions.stream().map(BatchFunction::getTenantId).collect(Collectors.toList());

        // 根据projectIds获取engine_identity
        List<ProjectEngine> dbNames = projectEngineService.listIdentityByProjectIdAndType(projectIds, MultiEngineType.HADOOP.getType());
        // 根据tenant_ids获取dtTenant_id
        List<Tenant> tenants = tenantService.listDtuicTenantIdByTenantId(tenantIds);

        //key: projectId --- value: engineIdentity
        Map<Long, String> dbNameMap = dbNames.stream().collect(Collectors.toMap(ProjectEngine::getProjectId, ProjectEngine::getEngineIdentity, (key1,key2) -> key2));
        //key: tenantId --- value: dtUicTenantId
        Map<Long, Long> dtUicTenantIdMap = tenants.stream().collect(Collectors.toMap(Tenant::getId, Tenant::getDtuicTenantId));
        for (BatchFunction batchFunction : batchFunctions) {
            functionService = multiEngineServiceFactory.getFunctionService(batchFunction.getEngineType());
            try {
                functionService.deleteFunction(dtUicTenantIdMap.get(batchFunction.getTenantId()), dbNameMap.get(batchFunction.getProjectId()), batchFunction.getName(), batchFunction.getProjectId());
                // 打印被执行删除的函数
                logger.info("删除成功的函数：" + String.format(LOGGER_DELETE_FUNCTION, batchFunction.getId(), batchFunction.getName()));
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                logger.error("删除失败的函数：" + String.format(LOGGER_DELETE_FUNCTION, batchFunction.getId(), batchFunction.getName()));
            }
        }
    }
}
