/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.engine.sparkk8s;

import com.dtstack.engine.common.JarFileInfo;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.client.AbstractClient;
import com.dtstack.engine.common.enums.ComputeType;
import com.dtstack.engine.common.enums.EJobType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.common.util.DtStringUtil;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.sparkk8s.config.SparkK8sConfig;
import com.dtstack.engine.sparkk8s.executor.MrSubmiter;
import com.dtstack.engine.sparkk8s.executor.SqlSubmiter;
import com.dtstack.engine.sparkk8s.parser.AddJarOperator;
import com.dtstack.engine.sparkk8s.resourceinfo.SparkK8sResourceInfo;
import com.dtstack.engine.sparkk8s.utils.SparkConfigUtil;
import com.google.common.collect.Lists;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.apache.spark.deploy.k8s.ExtendConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

/**
 * Date: 2020/6/18
 * Company: www.dtstack.com
 * @author maqi
 */
public class SparkK8sClient extends AbstractClient {
    private static final Logger LOG = LoggerFactory.getLogger(SparkK8sClient.class);

    private String hdfsConfPath = "";

    private Properties sparkDefaultProp;

    private SparkK8sConfig sparkK8sConfig;

    private volatile KubernetesClient k8sClient;

    @Override
    public void init(Properties prop) throws Exception {
        this.sparkDefaultProp = prop;
        this.sparkK8sConfig = PublicUtil.jsonStrToObject(PublicUtil.objToString(prop), SparkK8sConfig.class);
        this.hdfsConfPath = SparkConfigUtil.downloadHdfsAndHiveConf(sparkK8sConfig);

        String k8sConfigPath = SparkConfigUtil.downloadK8sConfig(sparkK8sConfig);
        sparkDefaultProp.setProperty(ExtendConfig.KUBERNETES_KUBE_CONFIG_KEY(),k8sConfigPath);

        k8sClient = getK8sClient();
    }

    @Override
    protected JobResult processSubmitJobWithType(JobClient jobClient) {
        EJobType jobType = jobClient.getJobType();
        JobResult jobResult = null;
        if (EJobType.MR.equals(jobType)) {
            jobResult = new MrSubmiter(jobClient, sparkK8sConfig, sparkDefaultProp).submit();
        } else if (EJobType.SQL.equals(jobType)) {
            jobResult = submitSqlJob(jobClient);
        }
        return jobResult;
    }

    private JobResult submitSqlJob(JobClient jobClient) {
        ComputeType computeType = jobClient.getComputeType();
        if (computeType == null) {
            throw new RdosDefineException("need to set compute type.");
        }
        switch (computeType) {
            case BATCH:
                return new SqlSubmiter(jobClient, sparkK8sConfig, sparkDefaultProp, hdfsConfPath).submit();
            default:
                //do nothing
        }
        throw new RdosDefineException("not support for compute type :" + computeType);
    }



    @Override
    public JobResult cancelJob(JobIdentifier jobIdentifier) {
        try {
            Optional<Pod> jobPod = getJobPod(jobIdentifier.getApplicationId());
            Boolean delete = false;
            if (jobPod.isPresent()) {
                Pod pod = jobPod.get();
                delete = getK8sClient().pods().delete(pod);
            }
            return delete ? JobResult.createSuccessResult(jobIdentifier.getApplicationId()) : JobResult.createErrorResult("delete pod failed!");
        } catch (Exception e) {
            LOG.error("", e);
            return JobResult.createErrorResult(e.getMessage());
        }
    }

    @Override
    public RdosTaskStatus getJobStatus(JobIdentifier jobIdentifier) throws IOException {
        Optional<Pod> jobPod = getJobPod(jobIdentifier.getEngineJobId());
        if (jobPod.isPresent()) {
            String phase = jobPod.get().getStatus().getPhase().toLowerCase();
            switch (phase) {
                case "pending":
                    return RdosTaskStatus.SCHEDULED;
                case "running":
                    return RdosTaskStatus.RUNNING;
                case "unknown":
                    return RdosTaskStatus.NOTFOUND;
                case "succeeded":
                    return RdosTaskStatus.FINISHED;
                case "failed":
                    return RdosTaskStatus.FAILED;
                default:
                    throw new RdosDefineException("Unsupported application state");
            }
        }
        return RdosTaskStatus.NOTFOUND;
    }

    private Optional<Pod> getJobPod(String selectorId) {
        List<Pod> items = getK8sClient().pods()
                .withLabel("spark-app-selector", selectorId)
                .list()
                .getItems();

        if (items.size() > 0) {
            return Optional.of(items.get(0));
        }
        return Optional.empty();
    }


    @Override
    public void beforeSubmitFunc(JobClient jobClient) {
        try {
            SparkK8sConfig sparkK8sConfig = PublicUtil.jsonStrToObject(jobClient.getPluginInfo(), SparkK8sConfig.class);
            String k8sConfigPath = SparkConfigUtil.downloadK8sConfig(sparkK8sConfig);
            sparkDefaultProp.setProperty(ExtendConfig.KUBERNETES_KUBE_CONFIG_KEY(), k8sConfigPath);
        } catch (IOException e) {
            throw new RuntimeException("k8s config file download fail");
        }

        String sql = jobClient.getSql();
        List<String> sqlArr = DtStringUtil.splitIgnoreQuota(sql, ';');
        if(sqlArr.size() == 0){
            return;
        }

        List<String> sqlList = Lists.newArrayList(sqlArr);
        Iterator<String> sqlItera = sqlList.iterator();

        while (sqlItera.hasNext()){
            String tmpSql = sqlItera.next();
            if(AddJarOperator.verific(tmpSql)){
                sqlItera.remove();
                JarFileInfo jarFileInfo = AddJarOperator.parseSql(tmpSql);

                if(jobClient.getJobType() == EJobType.SQL){
                    //SQL当前不允许提交jar包,自定义函数已经在web端处理了。
                }else{
                    //非sql任务只允许提交一个附件包
                    jobClient.setCoreJarInfo(jarFileInfo);
                    break;
                }
            }
        }
        jobClient.setSql(String.join(";", sqlList));
    }

    @Override
    public boolean judgeSlots(JobClient jobClient) {
        try {
            SparkK8sResourceInfo sparkResourceInfo = new SparkK8sResourceInfo();
            return sparkResourceInfo.judgeSlots(k8sClient, 2);
        } catch (Exception e) {
            LOG.error("judgeSlots error:{}", e);
            return false;
        }
    }

    @Override
    public String getJobMaster(JobIdentifier jobIdentifier) {
        KubernetesClient k8sClient = getK8sClient();
        if (!Objects.isNull(k8sClient)) {
            return k8sClient.getMasterUrl().toString();
        }
        return "";
    }

    public KubernetesClient getK8sClient() {
        try {
            if (k8sClient == null) {
                synchronized (this) {
                    if (k8sClient == null) {
                        String kubeConfigFilePath = sparkDefaultProp.getProperty(ExtendConfig.KUBERNETES_KUBE_CONFIG_KEY());
                        String nameSpace = sparkK8sConfig.getNameSpace();

                        Config config = SparkConfigUtil.getK8sConfig(kubeConfigFilePath);
                        config.setNamespace(nameSpace);
                        this.k8sClient = new DefaultKubernetesClient(config);
                    }
                }
            } else {
                k8sClient.getMasterUrl();
            }
        } catch (Throwable e) {
            LOG.error("getK8sClient error:{}", e);
            synchronized (this) {
                if (k8sClient != null) {
                    try {
                        //判断下是否可用
                        k8sClient.getMasterUrl();
                    } catch (Throwable e1) {
                        LOG.error("getYarnClient error:{}", e1);
                        k8sClient = null;
                    }
                }

                if (k8sClient == null) {
                    String kubeConfigFilePath = (String) sparkDefaultProp.get(ExtendConfig.KUBERNETES_KUBE_CONFIG_KEY());
                    Config config = SparkConfigUtil.getK8sConfig(kubeConfigFilePath);
                    this.k8sClient = new DefaultKubernetesClient(config);
                }
            }
        }
        return k8sClient;
    }

}
