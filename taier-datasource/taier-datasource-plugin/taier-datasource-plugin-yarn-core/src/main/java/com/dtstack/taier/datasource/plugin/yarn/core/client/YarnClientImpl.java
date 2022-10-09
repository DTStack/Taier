package com.dtstack.taier.datasource.plugin.yarn.core.client;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.datasource.plugin.common.nosql.AbsNoSqlClient;
import com.dtstack.taier.datasource.plugin.kerberos.core.util.SecurityUtils;
import com.dtstack.taier.datasource.plugin.yarn.core.util.StateUtil;
import com.dtstack.taier.datasource.plugin.yarn.core.util.YarnConfUtil;
import com.dtstack.taier.datasource.plugin.yarn.core.util.YarnRestUtil;
import com.dtstack.taier.datasource.api.client.IYarn;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.YarnSourceDTO;
import com.dtstack.taier.datasource.api.dto.yarn.YarnApplicationInfoDTO;
import com.dtstack.taier.datasource.api.dto.yarn.YarnApplicationStatus;
import com.dtstack.taier.datasource.api.dto.yarn.YarnResourceDTO;
import com.dtstack.taier.datasource.api.dto.yarn.YarnResourceDescriptionDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.NodeReport;
import org.apache.hadoop.yarn.api.records.NodeState;
import org.apache.hadoop.yarn.api.records.QueueInfo;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * yarn client
 *
 * @author ：wangchuan
 * date：Created in 下午2:08 2022/3/15
 * company: www.dtstack.com
 */
@Slf4j
public class YarnClientImpl extends AbsNoSqlClient implements IYarn {

    private static final String YARN_SCHEDULER_FORMAT = "%s/ws/v1/cluster/scheduler";

    public static final String IS_FULL_PATH_KEY = "yarn.resourcemanager.scheduler.queue.is-full-path";

    public static final String FAIRSCHEDULER_TPYE = "FAIRSCHEDULER";

    public static final String CAPACITYSCHEDULER_TPYE = "CAPACITYSCHEDULER";

    @Override
    public Boolean testCon(ISourceDTO source) {
        return getYarnClient(source, (yarnClient -> {
            try {
                List<NodeReport> nodes = yarnClient.getNodeReports(NodeState.RUNNING);
                if (CollectionUtils.isEmpty(nodes)) {
                    throw new SourceException("Active nodes is empty.");
                }
            } catch (Exception e) {
                throw new SourceException("Active nodes is empty.", e);
            }
            return true;
        }));
    }

    @Override
    public List<YarnApplicationInfoDTO> listApplication(ISourceDTO source, YarnApplicationStatus status, String taskName, String applicationId) {
        return getYarnClient(source, yarnClient -> {

            EnumSet<YarnApplicationState> stateEnumSet = EnumSet.noneOf(YarnApplicationState.class);
            stateEnumSet.add(StateUtil.convertToYarnApplicationState(status));

            List<ApplicationReport> apps = null;
            try {
                apps = yarnClient.getApplications(stateEnumSet).stream().
                        filter(report -> {
                            if (StringUtils.isBlank(applicationId)) {
                                return true;
                            }
                            return StringUtils.equals(report.getApplicationId().toString(), applicationId);
                        })
                        .filter(report -> {
                            if (StringUtils.isBlank(taskName)) {
                                return true;
                            }
                            return StringUtils.endsWith(report.getName(), "_" + taskName);
                        })
                        .collect(Collectors.toList());
            } catch (Exception e) {
                throw new SourceException("listApplication " + applicationId, e);
            }

            List<YarnApplicationInfoDTO> infos = new ArrayList<>();
            if (apps.size() > 0) {
                for (ApplicationReport app : apps) {
                    YarnApplicationInfoDTO info = new YarnApplicationInfoDTO();
                    info.setName(app.getName());
                    info.setApplicationId(app.getApplicationId().toString());
                    info.setStartTime(new Date(app.getStartTime()));
                    info.setFinishTime(new Date(app.getFinishTime()));
                    info.setStatus(StateUtil.convertYarnStatusToStatus(app.getYarnApplicationState()));
                    infos.add(info);
                }
            }
            return infos;
        });
    }

    @Override
    public YarnResourceDescriptionDTO getYarnResourceDescription(ISourceDTO source) {
        return getYarnClient(source, yarnClient -> {
            try {
                List<NodeReport> nodes = yarnClient.getNodeReports(NodeState.RUNNING);
                int totalMemory = 0;
                int totalCores = 0;
                for (NodeReport rep : nodes) {
                    totalMemory += rep.getCapability().getMemory();
                    totalCores += rep.getCapability().getVirtualCores();
                }

                boolean isFullPath = yarnClient.getConfig().getBoolean(IS_FULL_PATH_KEY, false);
                String rootQueueName = isFullPath ? getRootQueueName(source, yarnClient) : "";
                List<YarnResourceDescriptionDTO.QueueDescription> descriptions = getQueueDescription(rootQueueName, yarnClient.getRootQueueInfos(), isFullPath);
                return new YarnResourceDescriptionDTO(nodes.size(), totalMemory, totalCores, descriptions);
            } catch (Exception e) {
                throw new SourceException("getYarnResourceDescription ", e);
            }
        });
    }

    private String getRootQueueName(ISourceDTO sourceDTO, YarnClient yarnClient) {
        String webAddress = YarnConfUtil.getRMWebAddress(yarnClient);
        String schedulerUrl = String.format(YARN_SCHEDULER_FORMAT, webAddress);
        String schedulerInfoMsg = YarnRestUtil.getDataFromYarnRest(sourceDTO, yarnClient.getConfig(), schedulerUrl);
        JSONObject schedulerInfo = JSONObject.parseObject(schedulerInfoMsg);

        String rootQueueName = "root";
        JSONObject schedulerJson = schedulerInfo.getJSONObject("scheduler");
        if (schedulerJson.containsKey("schedulerInfo")) {
            JSONObject schedulerInfoJson = schedulerJson.getJSONObject("schedulerInfo");
            String schedulerType = schedulerInfoJson.getString("type");
            if (StringUtils.equalsIgnoreCase(schedulerType, CAPACITYSCHEDULER_TPYE)) {
                rootQueueName = schedulerInfoJson.getString("queueName");
            }
            if (StringUtils.equalsIgnoreCase(schedulerType, FAIRSCHEDULER_TPYE)) {
                JSONObject rootQueueJson = schedulerInfoJson.getJSONObject("rootQueue");
                rootQueueName = rootQueueJson == null ? rootQueueName : rootQueueJson.getString("queueName");
            }
        }
        return rootQueueName;
    }

    private List<YarnResourceDescriptionDTO.QueueDescription> getQueueDescription(String parentPath, List<QueueInfo> queueInfos, boolean isFullPath) {
        List<YarnResourceDescriptionDTO.QueueDescription> descriptions = new ArrayList<>(queueInfos.size());
        parentPath = StringUtils.isBlank(parentPath) ? "" : parentPath + ".";
        for (QueueInfo queueInfo : queueInfos) {
            String queuePath = queueInfo.getQueueName().startsWith(parentPath) ? queueInfo.getQueueName() : parentPath + queueInfo.getQueueName();
            YarnResourceDescriptionDTO.QueueDescription queueDescription = new YarnResourceDescriptionDTO.QueueDescription();
            queueDescription.setQueueName(queueInfo.getQueueName());
            if (isFullPath) {
                queueDescription.setQueueName(queuePath);
            }
            queueDescription.setCapacity(String.valueOf(Math.abs(queueInfo.getCapacity())));
            queueDescription.setMaximumCapacity(String.valueOf(Math.abs(queueInfo.getMaximumCapacity())));
            queueDescription.setQueueState(queueInfo.getQueueState().name());
            queueDescription.setQueuePath(queuePath);
            if (CollectionUtils.isNotEmpty(queueInfo.getChildQueues())) {
                List<YarnResourceDescriptionDTO.QueueDescription> childQueues = getQueueDescription(queuePath, queueInfo.getChildQueues(), isFullPath);
                queueDescription.setChildQueues(childQueues);
            }
            descriptions.add(queueDescription);
        }
        return descriptions;
    }

    @Override
    public YarnResourceDTO getYarnResource(ISourceDTO source) {
        return getYarnClient(source, yarnClient -> {

            YarnResourceDTO yarnResourceDTO = null;
            try {
                yarnResourceDTO = new YarnResourceDTO();
                List<NodeReport> nodes = yarnClient.getNodeReports(NodeState.RUNNING);
                List<YarnResourceDTO.NodeDescription> clusterNodes = new ArrayList<>();

                int totalMem = 0;
                int totalCores = 0;
                int usedMem = 0;
                int usedCores = 0;

                for (NodeReport rep : nodes) {
                    YarnResourceDTO.NodeDescription node = new YarnResourceDTO.NodeDescription();
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

                // set yarn node info
                yarnResourceDTO.setNodes(clusterNodes);

                YarnResourceDTO.ResourceMetrics metrics = createResourceMetrics(
                        totalMem, usedMem, totalCores, usedCores);

                yarnResourceDTO.setResourceMetrics(metrics);

                yarnResourceDTO.setQueues(getQueueResource(source, yarnClient));

                String webAddress = YarnConfUtil.getRMWebAddress(yarnClient);
                String schedulerUrl = String.format(YARN_SCHEDULER_FORMAT, webAddress);
                String schedulerInfoMsg = YarnRestUtil.getDataFromYarnRest(source, yarnClient.getConfig(), schedulerUrl);

                JSONObject schedulerInfo = JSONObject.parseObject(schedulerInfoMsg);
                if (schedulerInfo != null && schedulerInfo.containsKey("scheduler")) {
                    yarnResourceDTO.setScheduleInfo(schedulerInfo.getJSONObject("scheduler").getJSONObject("schedulerInfo"));
                }
            } catch (Exception e) {
                throw new SourceException("getYarnResource ", e);
            }
            return yarnResourceDTO;
        });
    }


    private YarnResourceDTO.ResourceMetrics createResourceMetrics(
            Integer totalMem, Integer usedMem, Integer totalCores, Integer usedCores) {

        YarnResourceDTO.ResourceMetrics metrics = new YarnResourceDTO.ResourceMetrics();

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

    private List<JSONObject> getQueueResource(ISourceDTO sourceDTO, YarnClient yarnClient) throws Exception {

        List<JSONObject> modifyQueueInfos = null;
        String defaultSchedulerType = "capacityScheduler";
        try {
            String webAddress = YarnConfUtil.getRMWebAddress(yarnClient);
            String schedulerUrl = String.format(YARN_SCHEDULER_FORMAT, webAddress);
            String schedulerInfoMsg = YarnRestUtil.getDataFromYarnRest(sourceDTO, yarnClient.getConfig(), schedulerUrl);
            JSONObject schedulerInfo = JSONObject.parseObject(schedulerInfoMsg);

            JSONObject schedulerJson = schedulerInfo.getJSONObject("scheduler");
            if (!schedulerJson.containsKey("schedulerInfo")) {
                log.error("get yarn queueInfo error! Miss schedulerInfo field");
                return null;
            }
            JSONObject schedulerInfoJson = schedulerJson.getJSONObject("schedulerInfo");

            String type = schedulerInfoJson.getString("type");
            switch (type) {
                case "fairScheduler":
                    modifyQueueInfos = modifyDefaultQueueInfo(yarnClient.getRootQueueInfos(), type);
                    break;
                case "capacityScheduler":
                    JSONObject queuesJson = schedulerInfoJson.getJSONObject("queues");

                    boolean isFullPath = yarnClient.getConfig().getBoolean(IS_FULL_PATH_KEY, false);
                    String rootQueueName = isFullPath ? getRootQueueName(sourceDTO, yarnClient) : "";
                    modifyQueueInfos = modifyQueueInfo(rootQueueName, queuesJson);
                    break;
            }
        } catch (Exception e) {
            log.error("Get Fcheduler Info Error", e);
        }

        if (modifyQueueInfos == null) {
            modifyQueueInfos = modifyDefaultQueueInfo(yarnClient.getRootQueueInfos(), defaultSchedulerType);
        }

        return modifyQueueInfos;
    }

    private List<JSONObject> modifyDefaultQueueInfo(List<QueueInfo> queueInfos, String schedulerType) throws IOException, YarnException {
        List<JSONObject> queues = new ArrayList<>();
        for (QueueInfo queueInfo : queueInfos) {
            JSONObject queue = new JSONObject();

            queue.put("queueName", queueInfo.getQueueName());

            double usedCapacity = (1 - queueInfo.getCurrentCapacity()) * 100.0;
            if ("fairScheduler".equals(schedulerType)) {
                usedCapacity = queueInfo.getCurrentCapacity() * 100.0;
            }
            queue.put("usedCapacity", retainDecimal(2, usedCapacity));

            Double capacity = Math.abs(queueInfo.getCapacity() * 100.0);
            queue.put("capacity", retainDecimal(2, capacity));

            Double maxCapacity = Math.abs(queueInfo.getMaximumCapacity() * 100.0);
            queue.put("maxCapacity", retainDecimal(2, maxCapacity));

            if (CollectionUtils.isNotEmpty(queueInfo.getChildQueues())) {
                List<JSONObject> childQueues = modifyDefaultQueueInfo(queueInfo.getChildQueues(), schedulerType);
                queues.addAll(childQueues);
            }
            queues.add(queue);
        }
        return queues;
    }

    private List<JSONObject> modifyQueueInfo(String parentName, JSONObject queueInfos) {
        List<JSONObject> queues = new ArrayList<>();
        if (!queueInfos.containsKey("queue")) {
            return null;
        }

        parentName = StringUtils.isBlank(parentName) ? "" : parentName + ".";
        for (Object ob : queueInfos.getJSONArray("queue")) {
            JSONObject queueInfo = (JSONObject) ob;
            String queueName = queueInfo.getString("queueName");
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
        boolean existUser;
        JSONObject queueUsers = queueInfo.getJSONObject("users");
        if (queueUsers == null) {
            existUser = false;
        } else {
            JSONArray users = queueUsers.getJSONArray("user");
            existUser = users != null;
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
                JSONObject userJSONObject = (JSONObject) user;
                userJSONObject.put("maxResource", userJSONObject.getJSONObject("userResourceLimit"));
                userJSONObject.put("maxAMResource", userJSONObject.getJSONObject("userResourceLimit"));
            }
            queueInfo.put("users", users);
        }
    }

    /**
     * 获取 yarn client
     *
     * @param source 数据源信息
     * @return yarn client
     */
    public <T> T getYarnClient(ISourceDTO source, Function<YarnClient, T> yarnClientConsumer) {
        YarnSourceDTO yarnSourceDTO = (YarnSourceDTO) source;
        YarnConfiguration configuration = YarnConfUtil.initYarnConfiguration(yarnSourceDTO);

        return SecurityUtils.login(() -> {
            try (YarnClient yarnClient = YarnClient.createYarnClient()) {
                yarnClient.init(configuration);
                yarnClient.start();
                return yarnClientConsumer.apply(yarnClient);
            } catch (Exception e) {
                throw new SourceException(String.format("execute method error : %s", e.getMessage()), e);
            }
        }, configuration, yarnSourceDTO.getKerberosConfig());
    }


}