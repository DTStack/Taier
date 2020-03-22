package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.common.cache.ConsoleCache;
import com.dtstack.dtcenter.common.enums.EComponentType;
import com.dtstack.dtcenter.common.enums.MultiEngineType;
import com.dtstack.dtcenter.common.login.DtUicUserConnect;
import com.dtstack.dtcenter.common.login.domain.UserTenant;
import com.dtstack.dtcenter.common.pager.PageQuery;
import com.dtstack.dtcenter.common.pager.PageResult;
import com.dtstack.dtcenter.common.pager.Sort;
import com.dtstack.engine.common.exception.EngineAssert;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.dao.*;
import com.dtstack.engine.domain.*;
import com.dtstack.engine.domain.Queue;
import com.dtstack.engine.master.env.EnvironmentContext;
import com.dtstack.engine.vo.ClusterVO;
import com.dtstack.engine.vo.EngineTenantVO;
import com.dtstack.engine.vo.TestConnectionVO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.ibatis.annotations.Param;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/7/16
 */
@Service
public class TenantService {

    private static Logger LOGGER = LoggerFactory.getLogger(TenantService.class);

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final ThreadLocal<Map<Long, UserTenant>> threadLocalUicTenant = new ThreadLocal<>();

    @Autowired
    private EnvironmentContext env;

    @Autowired
    private TenantDao tenantDao;

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
    private ClusterService clusterService;

    @Autowired
    private ComponentService componentService;

    public PageResult<List<EngineTenantVO>> pageQuery(@Param("clusterId") Long clusterId,
                                                      @Param("engineType") Integer engineType,
                                                      @Param("tenantName") String tenantName,
                                                      @Param("pageSize") int pageSize,
                                                      @Param("currentPage") int currentPage){
        Cluster cluster = clusterDao.getOne(clusterId);
        if(cluster == null){
            throw new RdosDefineException("集群不存在", ErrorCode.DATA_NOT_FIND);
        }

        Engine engine = engineDao.getByClusterIdAndEngineType(clusterId, engineType);
        if(engine == null){
            throw new RdosDefineException("引擎不存在", ErrorCode.DATA_NOT_FIND);
        }

        tenantName = tenantName == null ? "" : tenantName;
        PageQuery query = new PageQuery(currentPage, pageSize, "gmt_modified", Sort.DESC.name());
        int count = engineTenantDao.generalCount(engine.getId(), tenantName);
        if (count == 0){
            return PageResult.EMPTY_PAGE_RESULT;
        }

        List<EngineTenantVO> engineTenantVOS = engineTenantDao.generalQuery(query, engine.getId(), tenantName);
        fillQueue(engineTenantVOS);

        return new PageResult<>(engineTenantVOS, count, query);
    }

    /**
     * 获取处于统一集群的全部tenant
     *
     * @param dtuicTenantId
     * @param engineType
     * @return
     */
    public List<EngineTenantVO> listEngineTenant(@Param("dtuicTenantId") Long dtuicTenantId,
                                                 @Param("engineType") Integer engineType) {
        EngineTenant engineTenant = engineTenantDao.getByTenantIdAndEngineType(dtuicTenantId, engineType);
        List<EngineTenantVO> engineTenantVOS = engineTenantDao.listEngineTenant(engineTenant.getEngineId());
        fillQueue(engineTenantVOS);
        return engineTenantVOS;
    }

    private void fillQueue(List<EngineTenantVO> engineTenantVOS){
        List<Long> queueIds = new ArrayList<>();
        for (EngineTenantVO engineTenantVO : engineTenantVOS) {
            if(engineTenantVO.getQueueId() != null){
                queueIds.add(engineTenantVO.getQueueId());
            }
        }

        Map<Long, Queue> queueMap = new HashMap<>();
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
            engineTenantVO.setMaxCapacity(queue.getMaxCapacity());
            engineTenantVO.setMinCapacity(queue.getCapacity());
        }
    }

    public List listTenant(@Param("dtToken") String dtToken) {
        List<UserTenant> tenantList = postTenantList(dtToken);
        if (CollectionUtils.isEmpty(tenantList)) {
            return tenantList;
        }

        updateTenantLocalCache(tenantList);

        List<Long> hasClusterTenantIds = tenantDao.listAllDtUicTenantIds();
        if (hasClusterTenantIds.isEmpty()) {
            return tenantList;
        }

        Iterator it = tenantList.iterator();
        while (it.hasNext()) {
            UserTenant tenant = (UserTenant) it.next();
            if (hasClusterTenantIds.contains(tenant.getTenantId())) {
                it.remove();
            }
        }

        return tenantList;
    }

    private List<UserTenant> postTenantList(String dtToken) {
        String dtUicUrl = env.getDtUicUrl();
        return DtUicUserConnect.getUserTenants(dtUicUrl, dtToken, "");
    }

    @Transactional(rollbackFor = Exception.class)
    public void bindingTenant(@Param("tenantId") Long dtUicTenantId, @Param("clusterId") Long clusterId,
                              @Param("queueId") Long queueId, @Param("dtToken") String dtToken) throws Exception {
        Cluster cluster = clusterDao.getOne(clusterId);
        EngineAssert.assertTrue(cluster != null, "集群不存在", ErrorCode.DATA_NOT_FIND);

        checkTenantBindStatus(dtUicTenantId);
        checkClusterCanUse(clusterId);

        Tenant tenant = getTenant(dtUicTenantId, dtToken);

        List<Engine> engineList = engineDao.listByClusterId(clusterId);
        Engine hadoopEngine = addEngineTenant(tenant.getId(), engineList);

        if(queueId != null && hadoopEngine != null){
            updateTenantQueue(tenant.getId(), dtUicTenantId, hadoopEngine.getId(), queueId);
        }

        threadLocalUicTenant.remove();
    }

    private void checkTenantBindStatus(Long dtUicTenantId) {
        List<Long> engineIdList = engineTenantDao.listEngineIdByTenantId(dtUicTenantId);
        if (CollectionUtils.isNotEmpty(engineIdList)) {
            throw new RdosDefineException("该租户已经被绑定");
        }
    }

    private void checkClusterCanUse(Long clusterId) throws Exception {
        ClusterVO clusterVO = clusterService.getCluster(clusterId, true);
        JSONObject jsonObject = clusterService.buildClusterConfig(clusterVO);
        TestConnectionVO testConnectionVO = componentService.testConnections(jsonObject.toJSONString(), clusterId, null);
        boolean canUse = true;
        StringBuilder msg = new StringBuilder();
        msg.append("此集群不可用,测试连通性为通过：\n");
        for (TestConnectionVO.ComponentTestResult testResult : testConnectionVO.getTestResults()) {
            if(!testResult.getResult()){
                canUse = false;
                EComponentType componentType = EComponentType.getByCode(testResult.getComponentTypeCode());
                msg.append("组件:").append(componentType.getName()).append(" ").append(testResult.getErrorMsg()).append("\n");
            }
        }

        if(!canUse){
            throw new RdosDefineException(msg.toString());
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

        tenant = addTenant(dtUicTenantId, dtToken);
        return tenant;
    }

    private Tenant addTenant(Long dtUicTenantId, String dtToken){
        Map<Long, UserTenant> uicIdTenantMap = threadLocalUicTenant.get();
        if(uicIdTenantMap == null || !uicIdTenantMap.containsKey(dtUicTenantId)){
            List list = postTenantList(dtToken);
            uicIdTenantMap = updateTenantLocalCache(list);
        }

        UserTenant userTenant = uicIdTenantMap.get(dtUicTenantId);
        String tenantName = userTenant.getTenantName();
        String tenantDesc = userTenant.getTenantDesc();

        Tenant tenant = new Tenant();
        tenant.setTenantName(tenantName);
        tenant.setTenantDesc(tenantDesc);
        tenant.setDtUicTenantId(dtUicTenantId);
        tenantDao.insert(tenant);

        return tenant;
    }

    private Map<Long, UserTenant> updateTenantLocalCache(List<UserTenant> list){
        Map<Long, UserTenant> map = new HashMap<>(list.size());
        for (UserTenant ut : list) {
            map.put(ut.getTenantId(),ut);
        }

        threadLocalUicTenant.set(map);
        return map;
    }

    private void updateTenantQueue(Long tenantId, Long dtUicTenantId, Long engineId, Long queueId){
        Integer childCount = queueDao.countByParentQueueId(queueId);
        if (childCount != 0) {
            throw new RdosDefineException("所选队列存在子队列，选择正确的子队列", ErrorCode.DATA_NOT_FIND);
        }

        int result = engineTenantDao.updateQueueId(tenantId, engineId, queueId);
        if(result == 0){
            throw new RdosDefineException("更新引擎队列失败");
        }

        consoleCache.publishRemoveMessage(dtUicTenantId.toString());
    }

    /**
     * 绑定/切换队列
     */
    @Transactional(rollbackFor = Exception.class)
    public void bindingQueue(@Param("queueId") Long queueId,
                             @Param("tenantId") Long dtUicTenantId) {
        Queue queue = queueDao.getOne(queueId);
        if (queue == null) {
            throw new RdosDefineException("队列不存在", ErrorCode.DATA_NOT_FIND);
        }

        Long tenantId = tenantDao.getIdByDtUicTenantId(dtUicTenantId);
        if(tenantId == null){
            throw new RdosDefineException("租户不存在", ErrorCode.DATA_NOT_FIND);
        }

        updateTenantQueue(tenantId, dtUicTenantId, queue.getEngineId(), queueId);
    }
}
