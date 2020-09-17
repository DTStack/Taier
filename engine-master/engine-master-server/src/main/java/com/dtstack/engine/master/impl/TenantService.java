package com.dtstack.engine.master.impl;

import com.dtstack.engine.api.domain.*;
import com.dtstack.engine.api.pager.PageQuery;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.pojo.ComponentTestResult;
import com.dtstack.engine.api.vo.ClusterVO;
import com.dtstack.engine.api.vo.EngineTenantVO;
import com.dtstack.engine.api.vo.tenant.TenantAdminVO;
import com.dtstack.engine.api.vo.tenant.UserTenantVO;
import com.dtstack.engine.common.exception.EngineAssert;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.dao.*;
import com.dtstack.engine.master.enums.EComponentType;
import com.dtstack.engine.master.enums.MultiEngineType;
import com.dtstack.engine.master.env.EnvironmentContext;
import com.dtstack.engine.master.router.cache.ConsoleCache;
import com.dtstack.engine.master.router.login.DtUicUserConnect;
import com.dtstack.engine.master.router.login.domain.TenantAdmin;
import com.dtstack.engine.master.router.login.domain.UserTenant;
import com.dtstack.schedule.common.enums.Sort;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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

    @Autowired
    private QueueService queueService;

    public PageResult<List<EngineTenantVO>> pageQuery( Long clusterId,
                                                       Integer engineType,
                                                       String tenantName,
                                                       int pageSize,
                                                       int currentPage){
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
    public List<EngineTenantVO> listEngineTenant( Long dtuicTenantId,
                                                  Integer engineType) {
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
            engineTenantVO.setMaxCapacity(NumberUtils.toInt(queue.getMaxCapacity(),0) * 100 + "%");
            engineTenantVO.setMinCapacity(NumberUtils.toInt(queue.getCapacity(),0) * 100 + "%");
        }
    }


    public List<UserTenantVO> listTenant(String dtToken) {
        List<UserTenant> tenantList = postTenantList(dtToken);
        if (CollectionUtils.isEmpty(tenantList)) {
            return Lists.newArrayList();
        }

        List<Long> hasClusterTenantIds = tenantDao.listAllDtUicTenantIds();
        if (hasClusterTenantIds.isEmpty()) {
            return Lists.newArrayList();
        }
        tenantList.removeIf(tenant -> hasClusterTenantIds.contains(tenant.getTenantId()));

        return beanConversionVo(tenantList);
    }

    private List<UserTenantVO> beanConversionVo(List<UserTenant> tenantList) {
        List<UserTenantVO> vos = Lists.newArrayList();
        for (UserTenant userTenant : tenantList) {
            UserTenantVO vo = new UserTenantVO();
            BeanUtils.copyProperties(userTenant, vo);
            List<TenantAdmin> adminList = userTenant.getAdminList();
            List<TenantAdminVO> tenantAdminVOS = Lists.newArrayList();
            for (TenantAdmin tenantAdmin : adminList) {
                TenantAdminVO tenantAdminVO = new TenantAdminVO();
                BeanUtils.copyProperties(tenantAdmin, tenantAdminVO);
                tenantAdminVOS.add(tenantAdminVO);
            }
            vo.setAdminList(tenantAdminVOS);
        }
        return vos;
    }

    private List<UserTenant> postTenantList(String dtToken) {
        String dtUicUrl = env.getDtUicUrl();
        //uic对数据量做了限制，可能未查询到租户信息
        return DtUicUserConnect.getUserTenants(dtUicUrl, dtToken, "");
    }


    private UserTenant getTenantByDtUicTenantId(Long dtUicTenantId,String token){
        String dtUicUrl = env.getDtUicUrl();
        return DtUicUserConnect.getTenantByTenantId(dtUicUrl, dtUicTenantId, token);
    }


    @Transactional(rollbackFor = Exception.class)
    public void bindingTenant( Long dtUicTenantId,  Long clusterId,
                               Long queueId,  String dtToken,String namespace) throws Exception {
        Cluster cluster = clusterDao.getOne(clusterId);
        EngineAssert.assertTrue(cluster != null, "集群不存在", ErrorCode.DATA_NOT_FIND);

        Tenant tenant = getTenant(dtUicTenantId, dtToken);
        checkTenantBindStatus(tenant.getId());
        checkClusterCanUse(clusterId);


        List<Engine> engineList = engineDao.listByClusterId(clusterId);
        Engine hadoopEngine = addEngineTenant(tenant.getId(), engineList);
        if(null == hadoopEngine){
            return;
        }
        if(StringUtils.isNotBlank(namespace)){
            //k8s
           queueId = queueService.addNamespaces(hadoopEngine.getId(),namespace);
        }
        if(queueId != null){
            updateTenantQueue(tenant.getId(), dtUicTenantId, hadoopEngine.getId(), queueId);
        }

    }

    private void checkTenantBindStatus(Long tenantId) {
        List<Long> engineIdList = engineTenantDao.listEngineIdByTenantId(tenantId);
        if (CollectionUtils.isNotEmpty(engineIdList)) {
            throw new RdosDefineException("该租户已经被绑定");
        }
    }


    public void checkClusterCanUse(Long clusterId) throws Exception {
        ClusterVO clusterVO = clusterService.getCluster(clusterId, true, true);
        List<ComponentTestResult> testConnectionVO = componentService.testConnects(clusterVO.getClusterName());
        boolean canUse = true;
        StringBuilder msg = new StringBuilder();
        msg.append("此集群不可用,测试连通性为通过：\n");
        for (ComponentTestResult testResult : testConnectionVO) {
            EComponentType componentType = EComponentType.getByCode(testResult.getComponentTypeCode());
            if(!noNeedCheck(componentType) && !testResult.getResult()){
                canUse = false;
                msg.append("组件:").append(componentType.getName()).append(" ").append(testResult.getErrorMsg()).append("\n");
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

        tenant = addTenant(dtUicTenantId, dtToken);
        return tenant;
    }

    @Transactional(rollbackFor = Exception.class)
    public Tenant addTenant(Long dtUicTenantId, String dtToken){
        UserTenant userTenant = getTenantByDtUicTenantId(dtUicTenantId, dtToken);
        if(userTenant == null){
            throw new RdosDefineException("未查询到租户");
        }
        String tenantName = userTenant.getTenantName();
        String tenantDesc = userTenant.getTenantDesc();

        Tenant tenant = new Tenant();
        tenant.setTenantName(tenantName);
        tenant.setTenantDesc(tenantDesc);
        tenant.setDtUicTenantId(dtUicTenantId);
        tenantDao.insert(tenant);

        return tenant;
    }

    public void updateTenantQueue(Long tenantId, Long dtUicTenantId, Long engineId, Long queueId){
        Integer childCount = queueDao.countByParentQueueId(queueId);
        if (childCount != 0) {
            throw new RdosDefineException("所选队列存在子队列，选择正确的子队列", ErrorCode.DATA_NOT_FIND);
        }

        int result = engineTenantDao.updateQueueId(tenantId, engineId, queueId);
        if(result == 0){
            throw new RdosDefineException("更新引擎队列失败");
        }

        //缓存刷新
        consoleCache.publishRemoveMessage(dtUicTenantId.toString());
    }

    /**
     * 绑定/切换队列
     */
    @Transactional(rollbackFor = Exception.class)
    public void bindingQueue( Long queueId,
                              Long dtUicTenantId) {
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
