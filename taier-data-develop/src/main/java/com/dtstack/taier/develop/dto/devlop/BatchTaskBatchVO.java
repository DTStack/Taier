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

package com.dtstack.taier.develop.dto.devlop;

import com.dtstack.taier.dao.domain.DevelopResource;
import com.dtstack.taier.dao.domain.Task;
import com.dtstack.taier.dao.dto.DevelopTaskVersionDetailDTO;
import com.dtstack.taier.develop.parser.ESchedulePeriodType;
import com.dtstack.taier.develop.parser.ScheduleCron;
import com.dtstack.taier.develop.parser.ScheduleFactory;
import com.dtstack.taier.scheduler.vo.ScheduleTaskVO;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/9 0009.
 */
public class BatchTaskBatchVO extends ScheduleTaskVO {

    private static final Logger LOG = LoggerFactory.getLogger(BatchTaskBatchVO.class);

    public BatchTaskBatchVO(ScheduleTaskVO task) {
        this.setComputeType(task.getComputeType());
        this.setCreateUserId(task.getCreateUserId());
        this.setModifyUserId(task.getModifyUserId());
        this.setUserId(task.getUserId());
        this.setName(task.getName());
        this.setNodePid(task.getNodePid());
        this.setScheduleConf(task.getScheduleConf());
        this.setScheduleStatus(task.getScheduleStatus());
        this.setSqlText(task.getSqlText());
        this.setTaskParams(task.getTaskParams());
        this.setTaskType(task.getTaskType());
        this.setGmtCreate(task.getGmtCreate());
        this.setGmtModified(task.getGmtModified());
        this.setId(task.getId());
        this.setIsDeleted(task.getIsDeleted());
        this.setTenantId(task.getTenantId());
        this.setTaskDesc(task.getTaskDesc());
        this.setFlowId(task.getFlowId());
        this.setTaskVOS(task.getTaskVOS());
        this.setComponentVersion(task.getComponentVersion());
        this.setMainClass(task.getMainClass());
        init();
    }






    private void init() {

        if (StringUtils.isNotBlank(this.getScheduleConf())) {
            try {
                ScheduleCron cron = ScheduleFactory.parseFromJson(this.getScheduleConf());
                this.cron = cron.getCronStr();
                this.taskPeriodId = cron.getPeriodType();
                if (ESchedulePeriodType.MIN.getVal() == cron.getPeriodType()) {
                    taskPeriodType = "分钟任务";
                } else if (ESchedulePeriodType.HOUR.getVal() == cron.getPeriodType()) {
                    taskPeriodType = "小时任务";
                } else if (ESchedulePeriodType.DAY.getVal() == cron.getPeriodType()) {
                    taskPeriodType = "天任务";
                } else if (ESchedulePeriodType.WEEK.getVal() == cron.getPeriodType()) {
                    taskPeriodType = "周任务";
                } else if (ESchedulePeriodType.MONTH.getVal() == cron.getPeriodType()) {
                    taskPeriodType = "月任务";
                } else if (ESchedulePeriodType.CRON.getVal() == cron.getPeriodType()) {
                    taskPeriodType = "自定义任务";
                }
            } catch (Exception e) {
                LOG.error("", e);
            }
        }
    }

    public void parsePeriodType() {
        if (StringUtils.isNotBlank(this.getScheduleConf())) {
            try {
                ScheduleCron cron = ScheduleFactory.parseFromJson(this.getScheduleConf());
                this.setPeriodType(cron.getPeriodType());
            } catch (Exception e) {
                LOG.error("", e);
            }
        }
    }

    public ScheduleTaskVO toVO(Task task) {
        ScheduleTaskVO ScheduleTaskVO = new ScheduleTaskVO();
        try {
            BeanUtils.copyProperties(task, ScheduleTaskVO);
        } catch (Exception e) {
            LOG.error("", e);
        }
        return ScheduleTaskVO;
    }

    public ScheduleTaskVO toVO(Task task, ScheduleTaskVO ScheduleTaskVO) {
        try {
            BeanUtils.copyProperties(task, ScheduleTaskVO);
        } catch (Exception e) {
            LOG.error("", e);
        }
        return ScheduleTaskVO;
    }

    public BatchTaskBatchVO() {
    }

    private Integer taskPeriodId;
    private String taskPeriodType;
    private String nodePName;
    private Long userId;
    private Integer lockVersion;
    private List<Map> taskVariables;

    private Long dataSourceId;

    private ScheduleTaskVO subNodes;

    private List<ScheduleTaskVO> relatedTasks;

    private String tenantName;

    /**
     * 0-向导模式，1-脚本模式
     */
    private Integer createModel = 0;

    /**
     * 操作模式 0-资源模式，1-编辑模式
     */
    private Integer operateModel = 0;

    /**
     * 输入数据文件的路径
     */
    private String input;

    /**
     * 输出模型的路径
     */
    private String output;

    /**
     * 脚本的命令行参数
     */
    private String options;

    private String flowName;

    /**
     * 同步模式
     */
    private Integer syncModel = 0;

    private String increColumn;

    /**
     * 提交状态
     * 未提交：0
     * 已已经：1
     */
    private Integer submitStatus;

    private Long taskId;

    @Override
    public Long getTaskId() {
        return taskId;
    }

    /**
     * 'task版本'
     */
    private Integer version;

    public Integer getVersion() {
        return version;
    }

    private List<ScheduleTaskVO> taskVOS;

    private List<ScheduleTaskVO> subTaskVOS;

    private List<DevelopResource> resourceList;

    private List<DevelopResource> refResourceList;

    private List<DevelopTaskVersionDetailDTO> taskVersions;


    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }


    @Override
    public String getTenantName() {
        return tenantName;
    }

    @Override
    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    @Override
    public String getIncreColumn() {
        return increColumn;
    }

    @Override
    public void setIncreColumn(String increColumn) {
        this.increColumn = increColumn;
    }

    @Override
    public int getSyncModel() {
        return syncModel;
    }

    @Override
    public void setSyncModel(int syncModel) {
        this.syncModel = syncModel;
    }

    @Override
    public int getOperateModel() {
        return operateModel;
    }

    @Override
    public void setOperateModel(int operateModel) {
        this.operateModel = operateModel;
    }

    @Override
    public String getInput() {
        return input;
    }

    @Override
    public void setInput(String input) {
        this.input = input;
    }

    @Override
    public String getOutput() {
        return output;
    }

    @Override
    public void setOutput(String output) {
        this.output = output;
    }

    @Override
    public String getOptions() {
        return options;
    }

    @Override
    public void setOptions(String options) {
        this.options = options;
    }

    @Override
    public Integer getTaskPeriodId() {
        return taskPeriodId;
    }

    @Override
    public void setTaskPeriodId(Integer taskPeriodId) {
        this.taskPeriodId = taskPeriodId;
    }

    @Override
    public String getTaskPeriodType() {
        return taskPeriodType;
    }

    @Override
    public void setTaskPeriodType(String taskPeriodType) {
        this.taskPeriodType = taskPeriodType;
    }

    @Override
    public List<ScheduleTaskVO> getTaskVOS() {
        return taskVOS;
    }

    private String cron;

    @Override
    public ScheduleTaskVO setTaskVOS(List<ScheduleTaskVO> taskVOS) {
        this.taskVOS = taskVOS;
        return this;
    }

    public List<DevelopResource> getResourceList() {
        return resourceList;
    }

    public void setResourceList(List<DevelopResource> resourceList) {
        this.resourceList = resourceList;
    }

    @Override
    public String getNodePName() {
        return nodePName;
    }

    @Override
    public void setNodePName(String nodePName) {
        this.nodePName = nodePName;
    }

    @Override
    public String getCron() {
        return cron;
    }

    @Override
    public void setCron(String cron) {
        this.cron = cron;
    }

    public List<DevelopTaskVersionDetailDTO> getTaskVersions() {
        return taskVersions;
    }

    public void setTaskVersions(List<DevelopTaskVersionDetailDTO> taskVersions) {
        this.taskVersions = taskVersions;
    }

    @Override
    public Integer getLockVersion() {
        return lockVersion;
    }

    @Override
    public void setLockVersion(Integer lockVersion) {
        this.lockVersion = lockVersion;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public List<Map> getTaskVariables() {
        return taskVariables;
    }

    @Override
    public void setTaskVariables(List<Map> taskVariables) {
        this.taskVariables = taskVariables;
    }

    @Override
    public List<ScheduleTaskVO> getSubTaskVOS() {
        return subTaskVOS;
    }

    @Override
    public void setSubTaskVOS(List<ScheduleTaskVO> subTaskVOS) {
        this.subTaskVOS = subTaskVOS;
    }

    @Override
    public int getCreateModel() {
        return createModel;
    }

    @Override
    public void setCreateModel(int createModel) {
        this.createModel = createModel;
    }

    @Override
    public String getFlowName() {
        return flowName;
    }

    @Override
    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    @Override
    public ScheduleTaskVO getSubNodes() {
        return subNodes;
    }

    @Override
    public void setSubNodes(ScheduleTaskVO subNodes) {
        this.subNodes = subNodes;
    }

    @Override
    public List<ScheduleTaskVO> getRelatedTasks() {
        return relatedTasks;
    }

    @Override
    public void setRelatedTasks(List<ScheduleTaskVO> relatedTasks) {
        this.relatedTasks = relatedTasks;
    }

    public List<DevelopResource> getRefResourceList() {
        return refResourceList;
    }

    public void setRefResourceList(List<DevelopResource> refResourceList) {
        this.refResourceList = refResourceList;
    }

    @Override
    public Long getDataSourceId() {
        return dataSourceId;
    }

    @Override
    public void setDataSourceId(Long dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    public Integer getSubmitStatus() {
        return submitStatus;
    }

    public void setSubmitStatus(Integer submitStatus) {
        this.submitStatus = submitStatus;
    }
}
