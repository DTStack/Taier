package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.domain.*;
import com.dtstack.engine.common.pager.PageQuery;
import com.dtstack.engine.common.pager.PageResult;
import com.dtstack.engine.domain.Queue;
import com.dtstack.engine.domain.po.EngineTenantPO;
import com.dtstack.engine.master.impl.pojo.ComponentMultiTestResult;
import com.dtstack.engine.master.mapstruct.EngineTenantStruct;
import com.dtstack.engine.master.vo.EngineTenantVO;
import com.dtstack.engine.master.vo.tenant.TenantResourceVO;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.MultiEngineType;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.EngineAssert;
import com.dtstack.engine.pluginapi.exception.ErrorCode;
import com.dtstack.engine.pluginapi.exception.RdosDefineException;
import com.dtstack.engine.dao.*;
import com.dtstack.engine.master.router.cache.ConsoleCache;
import com.dtstack.engine.pluginapi.enums.EScheduleJobType;
import com.dtstack.engine.common.enums.Sort;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/7/16
 */
@Service
public class TenantService {

    private static Logger LOGGER = LoggerFactory.getLogger(TenantService.class);

    @Autowired
    private EnvironmentContext env;

    @Autowired
    private TenantDao tenantDao;

    @Autowired
    private TenantResourceDao tenantResourceDao;

    @Autowired
    private QueueDao queueDao;

    @Autowired
    private ConsoleCache consoleCache;

    @Autowired
    private EngineTenantDao engineTenantDao;

    @Autowired
    private EngineDao engineDao;

    @Autowired
    private ClusterDao clusterDao;

    @Autowired
    private ComponentService componentService;

    @Autowired
    private EngineTenantStruct engineTenantStruct;

    public PageResult<List<EngineTenantVO>> pageQuery( Long clusterId,
                                                       Integer engineType,
                                                       String tenantName,
                                                       int pageSize,
                                                       int currentPage){
        Cluster cluster = clusterDao.getOne(clusterId);
        if(cluster == null){
            throw new RdosDefineException("Cluster does not exist", ErrorCode.DATA_NOT_FIND);
        }

        Engine engine = engineDao.getByClusterIdAndEngineType(clusterId, engineType);
        if(engine == null){
            throw new RdosDefineException("Engine does not exist", ErrorCode.DATA_NOT_FIND);
        }

        tenantName = tenantName == null ? "" : tenantName;
        PageQuery query = new PageQuery(currentPage, pageSize, "gmt_modified", Sort.DESC.name());
        int count = engineTenantDao.generalCount(engine.getId(), tenantName);
        if (count == 0){
            return PageResult.EMPTY_PAGE_RESULT;
        }

        List<EngineTenantPO> engineTenantPOs = engineTenantDao.generalQuery(query, engine.getId(), tenantName);
        List<EngineTenantVO> engineTenantVOs = engineTenantStruct.toEngineTenantVOs(engineTenantPOs);
        fillQueue(engineTenantVOs);

        return new PageResult<>(engineTenantVOs, count, query);
    }

    /**
     * 获取处于统一集群的全部tenant
     *
     * @param dtuicTenantId
     * @param engineType
     * @return
     */
    public List<EngineTenantVO> listEngineTenant( Long dtuicTenantId,
                                                  Integer engineType) {
        EngineTenant engineTenant = engineTenantDao.getByTenantIdAndEngineType(dtuicTenantId, engineType);
        if(null == engineTenant){
            return Collections.EMPTY_LIST;
        }
        List<EngineTenantPO> engineTenantPOs = engineTenantDao.listEngineTenant(engineTenant.getEngineId());

        if(CollectionUtils.isEmpty(engineTenantPOs)){
            return Collections.EMPTY_LIST;
        }

        List<EngineTenantVO> engineTenantVOs = engineTenantStruct.toEngineTenantVOs(engineTenantPOs);
        fillQueue(engineTenantVOs);
        return engineTenantVOs;
    }

    private void fillQueue(List<EngineTenantVO> engineTenantVOS){

        List<Long> queueIds = engineTenantVOS.stream().filter(v -> v.getQueueId() != null).map(EngineTenantVO::getQueueId).collect(Collectors.toList());
        Map<Long, Queue> queueMap = new HashMap<>(16);
        List<Queue> queueList = queueDao.listByIds(queueIds);
        for (Queue queue : queueList) {
            queueMap.put(queue.getId(), queue);
        }
        for (EngineTenantVO engineTenantVO : engineTenantVOS) {
            if(engineTenantVO.getQueueId() == null){
                continue;
            }
            Queue queue = queueMap.get(engineTenantVO.getQueueId());
            if(queue == null){
                continue;
            }
            engineTenantVO.setQueue(queue.getQueuePath());
            engineTenantVO.setMaxCapacity(NumberUtils.toDouble(queue.getMaxCapacity(),0) * 100 + "%");
            engineTenantVO.setMinCapacity(NumberUtils.toDouble(queue.getCapacity(),0) * 100 + "%");
        }
    }


    public List<Tenant> listTenant() {

        List<Tenant> tenants = tenantDao.listAllDtUicTenantIds();
        if (tenants.isEmpty()) {
            return Lists.newArrayList();
        }
        return tenants;
    }

    @Transactional(rollbackFor = Exception.class)
    public void bindingTenant( Long dtUicTenantId,  Long clusterId,
                               Long queueId,  String dtToken,String namespace) throws Exception {
        Cluster cluster = clusterDao.getOne(clusterId);
        EngineAssert.assertTrue(cluster != null, "Cluster does not exist", ErrorCode.DATA_NOT_FIND);

        Tenant tenant = getTenant(dtUicTenantId, dtToken);
        checkTenantBindStatus(tenant.getId());
        checkClusterCanUse(cluster.getClusterName());


        List<Engine> engineList = engineDao.listByClusterId(clusterId);
        Engine hadoopEngine = addEngineTenant(tenant.getId(), engineList);
        if(null == hadoopEngine){
            return;
        }
        if (StringUtils.isNotBlank(namespace)) {
            //k8s
            componentService.addOrUpdateNamespaces(cluster.getId(), namespace, null, dtUicTenantId);
        } else if (queueId != null) {
            //hadoop
            updateTenantQueue(tenant.getId(), dtUicTenantId, hadoopEngine.getId(), queueId);
        }
    }

    private void checkTenantBindStatus(Long tenantId) {
        List<Long> engineIdList = engineTenantDao.listEngineIdByTenantId(tenantId);
        if (CollectionUtils.isNotEmpty(engineIdList)) {
            throw new RdosDefineException("The tenant has been bound");
        }
    }


    public void checkClusterCanUse(String clusterName) throws Exception {
        List<ComponentMultiTestResult> testConnectionVO = componentService.testConnects(clusterName);
        boolean canUse = true;
        StringBuilder msg = new StringBuilder();
        msg.append("此集群不可用,测试连通性为通过：\n");
        for (ComponentMultiTestResult testResult : testConnectionVO) {
            EComponentType componentType = EComponentType.getByCode(testResult.getComponentTypeCode());
            if(!noNeedCheck(componentType) && !testResult.getResult()){
                canUse = false;
                msg.append("组件:").append(componentType.getName()).append(" ").append(JSON.toJSONString(testResult.getErrorMsg())).append("\n");
            }
        }

        if(!canUse){
            throw new RdosDefineException(msg.toString());
        }
    }

    private Boolean noNeedCheck(EComponentType componentType) {
        switch (componentType) {
            case LIBRA_SQL:
            case IMPALA_SQL:
            case TIDB_SQL:
            case SPARK_THRIFT:
            case CARBON_DATA:
            case SFTP: return true;
            default: return false;
        }
    }

    private Engine addEngineTenant(Long tenantId, List<Engine> engineList){
        Engine hadoopEngine = null;
        for (Engine engine : engineList) {
            if(MultiEngineType.HADOOP.getType() == engine.getEngineType()){
                hadoopEngine = engine;
            }

            EngineTenant et = new EngineTenant();
            et.setTenantId(tenantId);
            et.setEngineId(engine.getId());

            engineTenantDao.insert(et);
        }

        return hadoopEngine;
    }

    private Tenant getTenant(Long dtUicTenantId, String dtToken){
        Tenant tenant = tenantDao.getByDtUicTenantId(dtUicTenantId);
        if(tenant != null){
            return tenant;
        }
        return tenant;
    }

    public Tenant getByDtUicTenantId(Long dtUicTenantId) {
        return tenantDao.getByDtUicTenantId(dtUicTenantId);
    }

    public Long getDtuicTenantId(Long id) {
        Tenant tenant = tenantDao.getOne(id);
        if (tenant != null) {
            return tenant.getDtUicTenantId();
        }
        return null;
    }

    public Tenant getTenantById(Long id) {
        return tenantDao.getOne(id);
    }

    public void updateTenantQueue(Long tenantId, Long dtUicTenantId, Long engineId, Long queueId){
        Integer childCount = queueDao.countByParentQueueId(queueId);
        if (childCount != 0) {
            throw new RdosDefineException("The selected queue has sub-queues, and the correct sub-queues are selected", ErrorCode.DATA_NOT_FIND);
        }

        LOGGER.info("switch queue, tenantId:{} queueId:{} engineId:{}",tenantId,queueId,engineId);
        int result = engineTenantDao.updateQueueId(tenantId, engineId, queueId);
        if(result == 0){
            throw new RdosDefineException("The update engine queue failed");
        }
        //缓存刷新
        consoleCache.publishRemoveMessage(dtUicTenantId.toString());
    }

    /**
     * 绑定/切换队列
     */
    @Transactional(rollbackFor = Exception.class)
    public void bindingQueue( Long queueId,
                              Long dtUicTenantId,String taskTypeResourceJson) {
        Queue queue = queueDao.getOne(queueId);
        if (queue == null) {
            throw new RdosDefineException("Queue does not exist", ErrorCode.DATA_NOT_FIND);
        }

        Long tenantId = tenantDao.getIdByDtUicTenantId(dtUicTenantId);
        if(tenantId == null){
            throw new RdosDefineException("Tenant does not exist", ErrorCode.DATA_NOT_FIND);
        }
        try {
            //修改租户各个任务资源限制
            updateTenantTaskResource(tenantId,dtUicTenantId,taskTypeResourceJson);
            LOGGER.info("switch queue, tenantId:{} queueId:{} queueName:{} engineId:{}",tenantId,queueId,queue.getQueueName(),queue.getEngineId());
            updateTenantQueue(tenantId, dtUicTenantId, queue.getEngineId(), queueId);
        } catch (Exception e) {
            LOGGER.error("", e);
            throw new RdosDefineException("Failed to switch queue");
        }
    }

    /**
    * @author zyd
    * @Description 修改租户的任务资源限制
    * @Date 11:14 上午 2020/10/15
    * @Param [tenantId, dtUicTenantId, taskTypeResourceMap]
    * @retrun void
    **/
    @Transactional(rollbackFor = Exception.class)
    public void updateTenantTaskResource(Long tenantId, Long dtUicTenantId, String taskTypeResourceJson) {

        //先删除原来的资源限制
        tenantResourceDao.delete(tenantId,dtUicTenantId);
        //再插入新添加的任务资源限制
        if(StringUtils.isBlank(taskTypeResourceJson)){
            return;
        }
        JSONArray jsonArray = JSON.parseArray(taskTypeResourceJson);
        TenantResource tenantResource = new TenantResource();
        tenantResource.setTenantId(tenantId.intValue());
        tenantResource.setDtUicTenantId(dtUicTenantId.intValue());
        for (Object obj : jsonArray) {
            JSONObject jsonObj = (JSONObject) obj;
            Integer taskType = jsonObj.getInteger("taskType");
            tenantResource.setTaskType(taskType);
            EScheduleJobType eJobType = EScheduleJobType.getEJobType(taskType);
            if(null == eJobType){
                throw new RdosDefineException("Incoming task type is wrong");
            }else{
                tenantResource.setEngineType(eJobType.getName());
            }
            tenantResource.setResourceLimit(jsonObj.getString("resourceParams"));
            tenantResourceDao.insert(tenantResource);
        }
    }


    /**
    * @author zyd
    * @Description 根据租户id查询租户设置的任务资源限制信息
    * @Date 5:26 下午 2020/10/15
    * @Param [dtUicTenantId]
    * @retrun java.util.List<com.dtstack.engine.master.vo.tenant.TenantResourceVO>
    **/
    public List<TenantResourceVO> queryTaskResourceLimits(Long dtUicTenantId) {

        try {
            List<TenantResource> tenantResources = tenantResourceDao.selectByUicTenantId(dtUicTenantId);
            return convertTenantResourceToVO(tenantResources);
        } catch (Exception e) {
            LOGGER.error("", e);
            throw new RdosDefineException("Query failed");
        }
    }

    private List<TenantResourceVO> convertTenantResourceToVO(List<TenantResource> tenantResources) throws Exception {

        List<TenantResourceVO> tenantResourceVos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(tenantResources)) {
            for (TenantResource tenantResource : tenantResources) {
                TenantResourceVO tenantResourceVO = new TenantResourceVO();
                BeanUtils.copyProperties(tenantResource, tenantResourceVO);
                String resourceLimit = tenantResource.getResourceLimit();
                if(StringUtils.isNotEmpty(resourceLimit)) {
                    Map<String, Object> objectMap = JSONObject.parseObject(resourceLimit);
                    tenantResourceVO.setResourceLimit(objectMap);
                }
                tenantResourceVos.add(tenantResourceVO);
            }
        }
        return tenantResourceVos;
    }

    /**
    * @author zyd
    * @Description 根据租户id和taskType获取资源限制信息
    * @Date 9:56 上午 2020/10/16
    * @Param [dtUicTenantId, taskType]
    * @retrun java.lang.String
    **/
    public String queryResourceLimitByTenantIdAndTaskType(Long dtUicTenantId, Integer taskType) {

        TenantResource tenantResource = null;
        try {
            tenantResource = tenantResourceDao.selectByUicTenantIdAndTaskType(dtUicTenantId, taskType);
        } catch (Exception e) {
            LOGGER.error("", e);
            throw new RdosDefineException("Failed to find resource limit");
        }
        if(null != tenantResource){
            return tenantResource.getResourceLimit();
        }else{
            return "";
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public void deleteTenantId(Long dtUicTenantId) {
        Long consoleTenantId = tenantDao.getIdByDtUicTenantId(dtUicTenantId);
        if (null != consoleTenantId) {
            LOGGER.info("delete tenant {} dtUicTenantId {} ", consoleTenantId, dtUicTenantId);
            tenantDao.delete(dtUicTenantId);
            engineTenantDao.deleteTenantId(consoleTenantId);
        }
    }


    public void updateTenantInfo(Long dtUicTenantId, String tenantName, String tenantDesc) {
        Tenant tenant = new Tenant();
        tenant.setDtUicTenantId(dtUicTenantId);
        tenant.setTenantName(tenantName);
        tenant.setTenantDesc(tenantDesc);
        tenantDao.updateByDtUicTenantId(tenant);
    }


    /**
     * 根据TenantIds获取到DtUicTenantIds
     * @param tenantIds
     * @return
     */
    public List<Long> listDtUicTenantByTenantIds(List<Long> tenantIds) {
        if (CollectionUtils.isEmpty(tenantIds)) {
            return Collections.EMPTY_LIST;
        }
        return tenantDao.getDtUicTenantIdListByIds(tenantIds);
    }

    public List<Tenant> listByDtUicTenantIds(List<Long> dtUicTenantIds) {
        if (CollectionUtils.isEmpty(dtUicTenantIds)) {
            return Collections.EMPTY_LIST;
        }
        return tenantDao.getByDtUicTenantIds(dtUicTenantIds);
    }

    /**
     *  根据tenantId集合获取对应租户集合
     *
     * @param tenantIds
     * @return
     */
    public List<Tenant> listByTenantIds(List<Long> tenantIds) {
        if (CollectionUtils.isEmpty(tenantIds)) {
            return Collections.EMPTY_LIST;
        }
        return tenantDao.listDtuicTenantIdByTenantId(tenantIds);
    }

}
