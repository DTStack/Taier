package com.dtstack.engine.master.resource;

import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.api.domain.Engine;
import com.dtstack.engine.api.vo.ClusterVO;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.enums.ComputeType;
import com.dtstack.engine.common.enums.EngineType;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.dao.ClusterDao;
import com.dtstack.engine.dao.EngineDao;
import com.dtstack.engine.master.enums.EComponentType;
import com.dtstack.engine.master.enums.MultiEngineType;
import com.dtstack.engine.master.impl.ClusterService;
import com.dtstack.engine.master.impl.ComponentService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/2/10
 */
public class FlinkResource extends CommonResource {

    private static final String FLINK_TASK_RUN_MODE_KEY = "flinkTaskRunMode";

    private static Map<Long, EComponentType> resourceComponents = new ConcurrentHashMap<>();

    private static final String SESSION = "session";
    private static final String PER_JOB = "per_job";

    @Override
    public ComputeResourceType getComputeResourceType(JobClient jobClient) {

        long tenantId = jobClient.getTenantId();
        EComponentType componentType = resourceComponents.computeIfAbsent(tenantId,
                e -> getResourceEComponentType(jobClient));

        Properties properties = jobClient.getConfProperties();
        ComputeType computeType = jobClient.getComputeType();
        String modeStr = properties.getProperty(FLINK_TASK_RUN_MODE_KEY);

        if (EComponentType.YARN.getTypeCode() == componentType.getTypeCode()) {
            if (StringUtils.isEmpty(modeStr)) {
                if (ComputeType.STREAM == computeType) {
                    return ComputeResourceType.Yarn;
                } else {
                    return ComputeResourceType.FlinkYarnSession;
                }
            }
            if (SESSION.equalsIgnoreCase(modeStr)) {
                return ComputeResourceType.FlinkYarnSession;
            } else if (PER_JOB.equalsIgnoreCase(modeStr)) {
                return ComputeResourceType.Yarn;
            }
        } else if (EComponentType.KUBERNETES.getTypeCode() == componentType.getTypeCode()) {
            if (StringUtils.isEmpty(modeStr)) {
                if (ComputeType.STREAM == computeType) {
                    return ComputeResourceType.Kubernetes;
                } else {
                    return ComputeResourceType.FlinkKubernetesSession;
                }
            }
            if (SESSION.equalsIgnoreCase(modeStr)) {
                return ComputeResourceType.FlinkKubernetesSession;
            } else if (PER_JOB.equalsIgnoreCase(modeStr)) {
                return ComputeResourceType.Kubernetes;
            }
        }

        throw new RdosDefineException("not support mode: " + modeStr);
    }

    public EComponentType getResourceEComponentType(JobClient jobClient){
        long tenantId = jobClient.getTenantId();

        ClusterVO cluster = clusterService.getClusterByTenant(tenantId);
        if (Objects.isNull(cluster)){
            throw new RdosDefineException("No found cluster by tenantId: " + tenantId);
        }

        Long clusterId = cluster.getClusterId();
        List<Engine> engines = engineDao.listByClusterId(clusterId);
        if (CollectionUtils.isEmpty(engines)){
            throw new RdosDefineException("No found engines by clusterId: " + clusterId);
        }

        Engine hadoopEngine = null;
        for (Engine engine : engines) {
            if (engine.getEngineType() == MultiEngineType.HADOOP.getType()) {
                hadoopEngine = engine;
                break;
            }
        }
        if (Objects.isNull(hadoopEngine)) {
            throw new RdosDefineException("No found hadoopEngine");
        }

        List<Component> componentList = componentService.listComponent(hadoopEngine.getId());
        for (Component component : componentList) {
            if (component.getComponentTypeCode() == EComponentType.KUBERNETES.getTypeCode()) {
                return EComponentType.KUBERNETES;
            } else if (component.getComponentTypeCode() == EComponentType.YARN.getTypeCode()) {
                return EComponentType.YARN;
            }
        }

        throw new RdosDefineException("No found resource EComponentType");
    }
}
