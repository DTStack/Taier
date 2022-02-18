/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.taier.yarn;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.pluginapi.pojo.ClusterResource;
import com.dtstack.taier.pluginapi.pojo.ComponentTestResult;
import com.dtstack.taier.pluginapi.pojo.ParamAction;
import com.dtstack.taier.base.util.HadoopConfTool;
import com.dtstack.taier.base.util.KerberosUtils;
import com.dtstack.taier.pluginapi.JobClient;
import com.dtstack.taier.pluginapi.JobIdentifier;
import com.dtstack.taier.pluginapi.client.AbstractClient;
import com.dtstack.taier.pluginapi.enums.TaskStatus;
import com.dtstack.taier.pluginapi.exception.ExceptionUtil;
import com.dtstack.taier.pluginapi.exception.PluginDefineException;
import com.dtstack.taier.pluginapi.http.PoolHttpClient;
import com.dtstack.taier.pluginapi.pojo.JobResult;
import com.dtstack.taier.pluginapi.util.MD5Util;
import com.dtstack.taier.pluginapi.util.PublicUtil;
import com.dtstack.taier.yarn.constrant.ConfigConstrant;
import com.dtstack.taier.yarn.util.HadoopConf;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.records.NodeReport;
import org.apache.hadoop.yarn.api.records.NodeState;
import org.apache.hadoop.yarn.api.records.QueueInfo;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class DtYarnClient extends AbstractClient {

    private static final Logger LOG = LoggerFactory.getLogger(DtYarnClient.class);
    private static final String YARN_RM_WEB_KEY_PREFIX = "yarn.resourcemanager.webapp.address.";
    private static final String YARN_SCHEDULER_FORMAT = "http://%s/ws/v1/cluster/scheduler";

    private Config config;
    private YarnConfiguration configuration;

    @Override
    public void init(Properties prop) throws Exception {
        String configStr = PublicUtil.objToString(prop);
        config = PublicUtil.jsonStrToObject(configStr, Config.class);
        configuration =  this.initYarnConf(config.getYarnConf());
    }
    private YarnConfiguration initYarnConf(Map<String, Object> conf){
        if(null == conf){
            return null;
        }

        YarnConfiguration configuration = new YarnConfiguration();

        conf.keySet().forEach(key ->{
            Object value = conf.get(key);
            if (value instanceof String){
                configuration.set(key, (String) value);
            } else if (value instanceof Boolean){
                configuration.setBoolean(key, (boolean) value);
            }
        });
        HadoopConfTool.setDefaultYarnConf(configuration, conf);
        return configuration;
    }

    @Override
    public JobResult cancelJob(JobIdentifier jobIdentifier) {
        return null;
    }

    @Override
    public TaskStatus getJobStatus(JobIdentifier jobIdentifier) throws IOException {
        return null;
    }

    @Override
    public String getJobMaster(JobIdentifier jobIdentifier) {
        return null;
    }


    @Override
    protected JobResult processSubmitJobWithType(JobClient jobClient) {
        return null;
    }

    /**
     * 测试联通性 yarn需要返回集群队列信息
     * @param pluginInfo
     * @return
     */
    @Override
    public ComponentTestResult testConnect(String pluginInfo) {
        ComponentTestResult testResult = new ComponentTestResult();
        testResult.setResult(false);
        try {
            Config allConfig = PublicUtil.jsonStrToObject(pluginInfo, Config.class);
            Configuration configuration =  this.initYarnConf(allConfig.getYarnConf());
            return KerberosUtils.login(allConfig, () -> testYarnConnect(testResult, allConfig),configuration);
        } catch (Exception e) {
            LOG.error("test yarn connect error", e);
            testResult.setErrorMsg(ExceptionUtil.getErrorMessage(e));
        }
        return testResult;
    }

    private ComponentTestResult testYarnConnect(ComponentTestResult testResult, Config allConfig) {
        try {
            HadoopConf hadoopConf = new HadoopConf();
            hadoopConf.initYarnConf(allConfig.getYarnConf());
            YarnClient testYarnClient = YarnClient.createYarnClient();
            testYarnClient.init(hadoopConf.getYarnConfiguration());
            testYarnClient.start();

            List<NodeReport> nodes = testYarnClient.getNodeReports(NodeState.RUNNING);
            int totalMemory = 0;
            int totalCores = 0;
            for (NodeReport rep : nodes) {
                totalMemory += rep.getCapability().getMemory();
                totalCores += rep.getCapability().getVirtualCores();
            }

            boolean isFullPath = hadoopConf.getYarnConfiguration().getBoolean(ConfigConstrant.IS_FULL_PATH_KEY, false);
            String rootQueueName = isFullPath? getRootQueueName(testYarnClient) : "";
            List<ComponentTestResult.QueueDescription> descriptions = getQueueDescription(rootQueueName, testYarnClient.getRootQueueInfos(), isFullPath);
            testResult.setClusterResourceDescription(new ComponentTestResult.ClusterResourceDescription(nodes.size(), totalMemory, totalCores, descriptions));
        } catch (Exception e) {
            LOG.error("test yarn connect error", e);
            throw new PluginDefineException(e);
        }
        testResult.setResult(true);
        return testResult;
    }

    private String getRootQueueName(YarnClient yarnClient) throws Exception {
        String webAddress = getYarnWebAddress(yarnClient);
        String schedulerUrl = String.format(YARN_SCHEDULER_FORMAT, webAddress);
        String schedulerInfoMsg = getDataFromYarnRest(yarnClient.getConfig(), schedulerUrl);
        JSONObject schedulerInfo = JSONObject.parseObject(schedulerInfoMsg);

        String rootQueueName = "root";
        JSONObject schedulerJson = schedulerInfo.getJSONObject("scheduler");
        if (schedulerJson.containsKey("schedulerInfo")) {
            JSONObject schedulerInfoJson = schedulerJson.getJSONObject("schedulerInfo");
            String schedulerType = schedulerInfoJson.getString("type");
            if (StringUtils.equalsIgnoreCase(schedulerType, ConfigConstrant.CAPACITYSCHEDULER_TPYE)) {
                rootQueueName = schedulerInfoJson.getString("queueName");
            }
            if (StringUtils.equalsIgnoreCase(schedulerType, ConfigConstrant.FAIRSCHEDULER_TPYE)) {
                JSONObject rootQueueJson = schedulerInfoJson.getJSONObject("rootQueue");
                rootQueueName = rootQueueJson == null ? rootQueueName : rootQueueJson.getString("queueName");
            }
        }
        return rootQueueName;
    }

    private List<ComponentTestResult.QueueDescription> getQueueDescription(String parentPath, List<QueueInfo> queueInfos, boolean isFullPath) {
        List<ComponentTestResult.QueueDescription> descriptions = new ArrayList<>(queueInfos.size());
        parentPath = StringUtils.isBlank(parentPath) ? "" : parentPath + ".";
        for (QueueInfo queueInfo : queueInfos) {
            String queuePath = queueInfo.getQueueName().startsWith(parentPath) ? queueInfo.getQueueName() : parentPath + queueInfo.getQueueName();
            ComponentTestResult.QueueDescription queueDescription = new ComponentTestResult.QueueDescription();
            queueDescription.setQueueName(queueInfo.getQueueName());
            if (isFullPath) {
                queueDescription.setQueueName(queuePath);
            }
            queueDescription.setCapacity(String.valueOf(queueInfo.getCapacity()));
            queueDescription.setMaximumCapacity(String.valueOf(queueInfo.getMaximumCapacity()));
            queueDescription.setQueueState(queueInfo.getQueueState().name());
            queueDescription.setQueuePath(queuePath);
            if (CollectionUtils.isNotEmpty(queueInfo.getChildQueues())) {
                List<ComponentTestResult.QueueDescription> childQueues = getQueueDescription(queuePath, queueInfo.getChildQueues(), isFullPath);
                queueDescription.setChildQueues(childQueues);
            }
            descriptions.add(queueDescription);
        }
        return descriptions;
    }


    @Override
    public ClusterResource getClusterResource() {
        ClusterResource clusterResource = new ClusterResource();
        try {

            KerberosUtils.login(config, () -> {
                YarnClient resourceClient = null;
                try {
                    resourceClient = YarnClient.createYarnClient();
                    resourceClient.init(configuration);
                    resourceClient.start();
                    List<NodeReport> nodes = resourceClient.getNodeReports(NodeState.RUNNING);
                    List<ClusterResource.NodeDescription> clusterNodes = new ArrayList<>();

                    Integer totalMem = 0;
                    Integer totalCores = 0;
                    Integer usedMem = 0;
                    Integer usedCores = 0;

                    for (NodeReport rep : nodes) {
                        ClusterResource.NodeDescription node = new ClusterResource.NodeDescription();
                        String nodeName = rep.getHttpAddress().split(":")[0];
                        node.setNodeName(nodeName);
                        node.setMemory(rep.getCapability().getMemory());
                        node.setUsedMemory(rep.getUsed().getMemory());
                        node.setUsedVirtualCores(rep.getUsed().getVirtualCores());
                        node.setVirtualCores(rep.getCapability().getVirtualCores());
                        clusterNodes.add(node);

                        // 计算集群资源总量和使用量
                        Resource capability = rep.getCapability();
                        Resource used = rep.getUsed();
                        totalMem += capability.getMemory();
                        totalCores += capability.getVirtualCores();
                        usedMem += used.getMemory();
                        usedCores += used.getVirtualCores();
                    }

                    ClusterResource.ResourceMetrics metrics = createResourceMetrics(
                            totalMem, usedMem, totalCores, usedCores);

                    clusterResource.setNodes(clusterNodes);
                    String webAddress = getYarnWebAddress(resourceClient);
                    String schedulerUrl = String.format(YARN_SCHEDULER_FORMAT, webAddress);
                    String schedulerInfoMsg = PoolHttpClient.get(schedulerUrl, null);
                    JSONObject schedulerInfo = JSONObject.parseObject(schedulerInfoMsg);
                    if(schedulerInfo.containsKey("scheduler")){
                        clusterResource.setScheduleInfo(schedulerInfo.getJSONObject("scheduler").getJSONObject("schedulerInfo"));
                    }
                    clusterResource.setQueues(getQueueResource(resourceClient));
                    clusterResource.setResourceMetrics(metrics);

                } catch (Exception e) {
                    LOG.error("close reource error ", e);
                } finally {
                    if (null != resourceClient) {
                        try {
                            resourceClient.close();
                        } catch (IOException e) {
                            LOG.error("close reource error ", e);
                        }
                    }
                }
                return clusterResource;
            }, configuration);

        } catch (Exception e) {
            throw new PluginDefineException(e.getMessage());
        }
        return clusterResource;
    }

    private ClusterResource.ResourceMetrics createResourceMetrics(
            Integer totalMem, Integer usedMem, Integer totalCores, Integer usedCores) {

        ClusterResource.ResourceMetrics metrics = new ClusterResource.ResourceMetrics();

        metrics.setTotalCores(totalCores);
        metrics.setUsedCores(usedCores);

        Double totalMemDouble = totalMem / (1024 * 1.0);
        Double totalMemNew = retainDecimal(2, totalMemDouble);
        metrics.setTotalMem(totalMemNew);

        Double usedMemDouble = usedMem / (1024 * 1.0);
        Double usedMemNew = retainDecimal(2, usedMemDouble);
        metrics.setUsedMem(usedMemNew);

        Double memRateDouble = usedMem / (totalMem * 1.0) * 100;
        Double memRate = retainDecimal(2, memRateDouble);
        metrics.setMemRate(memRate);

        Double coresRateDouble = usedCores / (totalCores * 1.0) * 100;
        Double coresRate = retainDecimal(2, coresRateDouble);
        metrics.setCoresRate(coresRate);
        return metrics;
    }

    private Double retainDecimal(Integer position, Double decimal) {
        BigDecimal retain = new BigDecimal(decimal);
        return retain.setScale(position, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    private List<JSONObject> getQueueResource(YarnClient yarnClient) throws Exception {
        String webAddress = getYarnWebAddress(yarnClient);
        String schedulerUrl = String.format(YARN_SCHEDULER_FORMAT, webAddress);
        String schedulerInfoMsg = getDataFromYarnRest(yarnClient.getConfig(), schedulerUrl);
        JSONObject schedulerInfo = JSONObject.parseObject(schedulerInfoMsg);

        JSONObject schedulerJson = schedulerInfo.getJSONObject("scheduler");
        if (!schedulerJson.containsKey("schedulerInfo")) {
            LOG.error("get yarn queueInfo error! Miss schedulerInfo field");
            return null;
        }
        JSONObject schedulerInfoJson = schedulerJson.getJSONObject("schedulerInfo");
        if (!schedulerInfoJson.containsKey("queues")) {
            LOG.error("get yarn queueInfo error! Miss queues field");
            return null;
        }
        JSONObject queuesJson = schedulerInfoJson.getJSONObject("queues");
        List<JSONObject> modifyQueueInfos = modifyQueueInfo(null, queuesJson);
        return modifyQueueInfos;
    }

    private List<JSONObject> modifyQueueInfo(String parentName, JSONObject queueInfos) {
        List<JSONObject> queues = new ArrayList<>();
        if (!queueInfos.containsKey("queue")) {
            return null;
        }

        for (Object ob : queueInfos.getJSONArray("queue")) {
            JSONObject queueInfo = (JSONObject)ob;
            String queueName = queueInfo.getString("queueName");
            parentName = StringUtils.isBlank(parentName) ? "" : parentName + ".";
            String queueNewName = parentName + queueName;

            if (queueInfo.containsKey("queues")) {
                List<JSONObject> childQueues = modifyQueueInfo(queueNewName, queueInfo.getJSONObject("queues"));
                if (childQueues != null) {
                    queues.addAll(childQueues);
                }
            }

            queueInfo.put("queueName", queueNewName);
            if (!queueInfo.containsKey("queues")) {
                fillUser(queueInfo);
                retainCapacity(queueInfo);
                queues.add(queueInfo);
            }
        }
        return queues;
    }

    private void retainCapacity(JSONObject queueInfo) {
        Double capacity = queueInfo.getDouble("capacity");
        queueInfo.put("capacity", retainDecimal(2, capacity));

        Double usedCapacity = queueInfo.getDouble("usedCapacity");
        queueInfo.put("usedCapacity", retainDecimal(2, usedCapacity));

        Double maxCapacity = queueInfo.getDouble("maxCapacity");
        queueInfo.put("maxCapacity", retainDecimal(2, maxCapacity));

    }

    private void fillUser(JSONObject queueInfo) {
        boolean existUser = false;
        JSONObject queueUsers = queueInfo.getJSONObject("users");
        if (queueUsers == null) {
            existUser = false;
        } else {
            JSONArray users = queueUsers.getJSONArray("user");
            existUser = users == null ? false : true;
        }

        if (!existUser) {
            JSONObject userJSONObject = new JSONObject();
            userJSONObject.put("username", "admin");
            userJSONObject.put("resourcesUsed", queueInfo.getJSONObject("resourcesUsed"));
            userJSONObject.put("AMResourceUsed", queueInfo.getJSONObject("usedAMResource"));
            userJSONObject.put("userResourceLimit", queueInfo.getJSONObject("userAMResourceLimit"));
            userJSONObject.put("maxResource", queueInfo.getJSONObject("userAMResourceLimit"));
            userJSONObject.put("maxAMResource", queueInfo.getJSONObject("userAMResourceLimit"));
            List<JSONObject> users = new ArrayList<>();
            users.add(userJSONObject);
            queueInfo.put("users", users);
        } else {
            JSONArray users = queueUsers.getJSONArray("user");
            for (Object user : users) {
                JSONObject userJSONObject = (JSONObject)user;
                userJSONObject.put("maxResource", userJSONObject.getJSONObject("userResourceLimit"));
                userJSONObject.put("maxAMResource", userJSONObject.getJSONObject("userResourceLimit"));
            }
            queueInfo.put("users", users);
        }
    }

    private String getYarnWebAddress(YarnClient yarnClient) throws Exception {
        Field rmClientField = yarnClient.getClass().getDeclaredField("rmClient");
        rmClientField.setAccessible(true);
        Object rmClient = rmClientField.get(yarnClient);

        Field hField = rmClient.getClass().getSuperclass().getDeclaredField("h");
        hField.setAccessible(true);
        //获取指定对象中此字段的值
        Object h = hField.get(rmClient);
        Object currentProxy = null;
        try {
            Field currentProxyField = h.getClass().getDeclaredField("currentProxy");
            currentProxyField.setAccessible(true);
            currentProxy = currentProxyField.get(h);
        } catch (Exception e) {
            //兼容Hadoop 2.7.3 2.6.4.91-3
            LOG.warn("get currentProxy error: ", e);
            Field proxyDescriptorField = h.getClass().getDeclaredField("proxyDescriptor");
            proxyDescriptorField.setAccessible(true);
            Object proxyDescriptor = proxyDescriptorField.get(h);
            Field currentProxyField = proxyDescriptor.getClass().getDeclaredField("proxyInfo");
            currentProxyField.setAccessible(true);
            currentProxy = currentProxyField.get(proxyDescriptor);
        }

        Field proxyInfoField = currentProxy.getClass().getDeclaredField("proxyInfo");
        proxyInfoField.setAccessible(true);
        String proxyInfoKey = (String) proxyInfoField.get(currentProxy);

        YarnConfiguration config = (YarnConfiguration) yarnClient.getConfig();
        String key = YARN_RM_WEB_KEY_PREFIX + proxyInfoKey;
        String webAddress = config.get(key);

        if (webAddress == null) {
            webAddress = config.get("yarn.resourcemanager.webapp.address");
        }
        return webAddress;
    }

    public String getDataFromYarnRest(Configuration yarnConfig, String url) throws Exception {
        String token = yarnConfig.get(ConfigConstrant.HTTP_AUTHENTICATION_TOKEN_KEY);
        Header[] headers = {};
        if (StringUtils.isNotEmpty(token)) {
            String authKey = "Authorization";
            String authValue = String.format("Bearer %s", token);
            headers = new Header[]{new BasicHeader(authKey, authValue)};
        }
        return PoolHttpClient.get(url, ConfigConstrant.HTTP_MAX_RETRY, headers);
    }

    public static void main(String[] args) throws Exception {

        System.setProperty("HADOOP_USER_NAME", "admin");

        // input params json file path
        String filePath = args[0];
        File paramsFile = new File(filePath);
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(paramsFile)));
        String request = reader.readLine();
        Map params =  PublicUtil.jsonStrToObject(request, Map.class);
        ParamAction paramAction = PublicUtil.mapToObject(params, ParamAction.class);
        JobClient jobClient = new JobClient(paramAction);

        String pluginInfo = jobClient.getPluginInfo();
        Properties properties = PublicUtil.jsonStrToObject(pluginInfo, Properties.class);
        String md5plugin = MD5Util.getMd5String(pluginInfo);
        properties.setProperty("md5sum", md5plugin);

        DtYarnClient client = new DtYarnClient();
        client.init(properties);

        ClusterResource clusterResource = client.getClusterResource();

        LOG.info("submit success!");
        LOG.info(clusterResource.toString());
        System.exit(0);
    }

}
