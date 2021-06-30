package com.dtstack.engine.master.jobdealer.resource;

import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.enums.ComputeType;
import com.dtstack.engine.common.enums.EDeployType;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.enums.EComponentType;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
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
    private static final String STANDALONE = "standalone";

    @Override
    public ComputeResourceType getComputeResourceType(JobClient jobClient) {

        long tenantId = jobClient.getTenantId();
        EComponentType componentType = resourceComponents.computeIfAbsent(tenantId,
                e -> getResourceEComponentType(jobClient));

        Properties properties = jobClient.getConfProperties();
        ComputeType computeType = jobClient.getComputeType();
        String modeStr = properties.getProperty(FLINK_TASK_RUN_MODE_KEY);
        if (EComponentType.FLINK.getTypeCode().equals(componentType.getTypeCode())) {
            //flink on standalone
            return ComputeResourceType.FlinkOnStandalone;
        } else if (EComponentType.YARN.getTypeCode().equals(componentType.getTypeCode())) {
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
            } else if (STANDALONE.equals(modeStr)) {
                return ComputeResourceType.FlinkOnStandalone;
            }
        } else if (EComponentType.KUBERNETES.getTypeCode().equals(componentType.getTypeCode())) {
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
            }else if(STANDALONE.equals(modeStr)){
                return ComputeResourceType.FlinkOnStandalone;
            }
        }

        throw new RdosDefineException("not support mode: " + modeStr);
    }

    public EComponentType getResourceEComponentType(JobClient jobClient) {
        long dtUicTenantId = jobClient.getTenantId();

        Long clusterId = engineTenantDao.getClusterIdByTenantId(dtUicTenantId);
        Component flinkComponent = componentService.getComponentByClusterId(clusterId, EComponentType.FLINK.getTypeCode(), null);
        if (null != flinkComponent) {
            if (EDeployType.STANDALONE.getType() == flinkComponent.getDeployType()) {
                return EComponentType.FLINK;
            } else if (EDeployType.YARN.getType() == flinkComponent.getDeployType()) {
                return EComponentType.YARN;
            }
        }
        Component kubernetesComponent = componentService.getComponentByClusterId(clusterId, EComponentType.KUBERNETES.getTypeCode(), null);
        if (null != kubernetesComponent) {
            return EComponentType.KUBERNETES;
        }
        throw new RdosDefineException("No found resource EComponentType");
    }
}
