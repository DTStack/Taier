package com.dtstack.engine.sparkk8s.resourceinfo;

import com.dtstack.engine.base.resource.EngineResourceInfo;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.pojo.JudgeResult;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 *   Spark依赖kuber-client版本为4.7以上会有兼容问题，导致driver pod无法运行。
 * Date: 2020/6/24
 * Company: www.dtstack.com
 * @author maqi
 */

public class SparkK8sResourceInfo implements EngineResourceInfo {

    private Logger LOG = LoggerFactory.getLogger(getClass());

    private final static String PENDING_PHASE = "Pending";

    private KubernetesClient kubernetesClient;
    private String namespace;
    private Integer allowPendingPodSize;

    public SparkK8sResourceInfo(KubernetesClient kubernetesClient, String namespace, Integer allowPendingPodSize) {
        this.kubernetesClient = kubernetesClient;
        this.namespace = namespace;
        this.allowPendingPodSize = allowPendingPodSize;
    }

    //TODO 通过TOP接口进行k8s资源判断
    @Override
    public JudgeResult judgeSlots(JobClient jobClient) {
        if (allowPendingPodSize > 0) {
            List<Pod> pods = kubernetesClient.pods().list().getItems();
            List<Pod> pendingPods = pods.stream()
                    .filter(p -> PENDING_PHASE.equals(p.getStatus().getPhase()))
                    .collect(Collectors.toList());

            if (pendingPods.size() > allowPendingPodSize) {
                LOG.info("pendingPods-size:{} allowPendingPodSize:{}", pendingPods.size(), allowPendingPodSize);
                return JudgeResult.notOk( "The number of pending pod is greater than " + allowPendingPodSize);
            }
        }
        return JudgeResult.ok();
    }

    public static SparkK8sResourceInfoBuilder SparkK8sResourceInfoBuilder() {
        return new SparkK8sResourceInfoBuilder();
    }

    public static class SparkK8sResourceInfoBuilder {
        private KubernetesClient kubernetesClient;
        private String namespace;
        private Integer allowPendingPodSize;

        public SparkK8sResourceInfoBuilder withKubernetesClient(KubernetesClient kubernetesClient) {
            this.kubernetesClient = kubernetesClient;
            return this;
        }

        public SparkK8sResourceInfoBuilder withNamespace(String namespace) {
            this.namespace = namespace;
            return this;
        }

        public SparkK8sResourceInfoBuilder withAllowPendingPodSize(Integer allowPendingPodSize) {
            this.allowPendingPodSize = allowPendingPodSize;
            return this;
        }

        public SparkK8sResourceInfo build() {
            return new SparkK8sResourceInfo(kubernetesClient, namespace, allowPendingPodSize);
        }
    }

}
