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

package com.dtstack.batch.controller.batch;

import com.dtstack.batch.mapstruct.vo.TaskMapstructTransfer;
import com.dtstack.batch.service.task.impl.BatchTaskService;
import com.dtstack.batch.vo.BatchTaskBatchVO;
import com.dtstack.batch.vo.TaskResourceParam;
import com.dtstack.batch.web.task.vo.query.*;
import com.dtstack.batch.web.task.vo.result.*;
import com.dtstack.engine.common.lang.coc.APITemplate;
import com.dtstack.engine.common.lang.web.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Api(value = "任务管理", tags = {"任务管理"})
@RestController
@RequestMapping(value = "/api/rdos/batch/batchTask")
public class BatchTaskController {

    @Autowired
    private BatchTaskService taskService;


    @PostMapping(value = "cloneTask")
    @ApiOperation("任务克隆")
    public R<BatchTaskResultVO> cloneTask(@RequestBody BatchTaskCloneTaskVO infoVO) {
        return new APITemplate<BatchTaskResultVO>() {
            @Override
            protected BatchTaskResultVO process() {
                return TaskMapstructTransfer.INSTANCE.BatchTaskToResultVO(taskService.cloneTask(infoVO.getProjectId(), infoVO.getUserId(),
                        infoVO.getTaskId(), infoVO.getTaskName(), infoVO.getTaskDesc(), infoVO.getNodePid()));
            }
        }.execute();
    }

    @PostMapping(value = "globalSearch")
    @ApiOperation("数据开发-任务全局搜索")
    public R<List<Map<String, Object>>> globalSearch(@RequestBody BatchTaskInfoVO infoVO) {
        return new APITemplate<List<Map<String, Object>>>() {
            @Override
            protected List<Map<String, Object>> process() {
                return taskService.globalSearch(infoVO.getTaskName());
            }
        }.execute();
    }

    @PostMapping(value = "getTaskById")
    @ApiOperation("数据开发-根据任务id，查询详情")
    public R<BatchTaskGetTaskByIdResultVO> getTaskById(@RequestBody BatchScheduleTaskVO batchScheduleTaskVO) {
        return new APITemplate<BatchTaskGetTaskByIdResultVO>() {
            @Override
            protected BatchTaskGetTaskByIdResultVO process() {
                BatchTaskBatchVO batchTaskBatchVO =  taskService.getTaskById(TaskMapstructTransfer.INSTANCE.BatchScheduleTaskVToScheduleTaskVO(batchScheduleTaskVO));
                return TaskMapstructTransfer.INSTANCE.BatchTaskBatchVOToBatchTaskGetTaskByIdResultVO(batchTaskBatchVO);
            }
        }.execute();
    }

    @PostMapping(value = "getTasksByProjectId")
    @ApiOperation("数据开发-根据项目id获取已提交任务列表")
    public R<List<BatchTaskResultVO> > getTasksByProjectId(@RequestBody BatchTaskGetTaskVO vo) {
        return new APITemplate<List<BatchTaskResultVO> >() {
            @Override
            protected List<BatchTaskResultVO>  process() {
                return TaskMapstructTransfer.INSTANCE.BatchTaskListToBatchTaskResultVOList(taskService.getTasksByTenantId(vo.getTenantId(), vo.getTaskName()));
            }
        }.execute();
    }

    @PostMapping(value = "queryTaskByType")
    @ApiOperation("查询工作流任务")
    public R<List<BatchTaskResultVO>> queryTaskByType(@RequestBody BatchTaskQueryTaskByTypeVO vo) {
        return new APITemplate<List<BatchTaskResultVO>>() {
            @Override
            protected List<BatchTaskResultVO>  process() {
                return TaskMapstructTransfer.INSTANCE.BatchTaskListToBatchTaskResultVOList(taskService.queryTaskByType(vo.getProjectId(),
                        vo.getTaskName(), vo.getTaskType()));
            }
        }.execute();
    }


    @PostMapping(value = "getTasksByName")
    @ApiOperation("根据项目id,任务名 获取任务列表")
    public R<List<BatchTaskResultVO>> getTasksByName(@RequestBody BatchTaskGetTasksByNameVO infoVO) {
        return new APITemplate<List<BatchTaskResultVO>>() {
            @Override
            protected List<BatchTaskResultVO>   process() {
                return TaskMapstructTransfer.INSTANCE.BatchTaskListToBatchTaskResultVOList(taskService.getTasksByName(infoVO.getProjectId(),
                        infoVO.getName()));
            }
        }.execute();
    }

    @PostMapping(value = "getDependencyTask")
    @ApiOperation("获取依赖任务")
    public R<List<Map<String, Object>>> getDependencyTask(@RequestBody BatchTaskGetDependencyTaskVO infoVO) {
        return new APITemplate<List<Map<String, Object>>>() {
            @Override
            protected List<Map<String, Object>>  process() {
                return taskService.getDependencyTask(infoVO.getProjectId(), infoVO.getTaskId(), infoVO.getName(), infoVO.getSearchProjectId());
            }
        }.execute();
    }

    @PostMapping(value = "checkIsLoop")
    @ApiOperation("检查task与依赖的task是否有构成有向环")
    public R<BatchTaskResultVO> checkIsLoop(@RequestBody BatchTaskCheckIsLoopVO infoVO) {
        return new APITemplate<BatchTaskResultVO>() {
            @Override
            protected BatchTaskResultVO process() {
                return TaskMapstructTransfer.INSTANCE.BatchTaskToResultVO(taskService.checkIsLoop(infoVO.getTaskId(), infoVO.getDependencyTaskId()));
            }
        }.execute();
    }

    @PostMapping(value = "queryCatalogueTasks")
    @ApiOperation("关键字搜索")
    public R<TaskCatalogueResultVO> queryCatalogueTasks(@RequestBody BatchTaskQueryCatalogueTasksVO infoVO) {
        return new APITemplate<TaskCatalogueResultVO>() {
            @Override
            protected TaskCatalogueResultVO process() {
                return TaskMapstructTransfer.INSTANCE.TaskCatalogueVOToResultVO(taskService.queryCatalogueTasks(infoVO.getProjectId(),
                        infoVO.getName()));
            }
        }.execute();
    }

    @PostMapping(value = "checkAndPublishTask")
    @ApiOperation("任务发布权限判断、发布")
    public R<Void> checkAndPublishTask(@RequestBody BatchTaskCheckAndPublishTaskVO detailVO) {
        return new APITemplate<Void>() {
            @Override
            protected Void process() {
                taskService.checkAndPublishTask(detailVO.getTenantId(), detailVO.getId(), detailVO.getUserId(),
                        detailVO.getPublishDesc(), detailVO.getIsRoot());
                return null;
            }
        }.execute();
    }

    @PostMapping(value = "publishTask")
    @ApiOperation("任务发布")
    public R<BatchTaskPublishTaskResultVO> publishTask(@RequestBody BatchTaskPublishTaskVO detailVO) {
        return new APITemplate<BatchTaskPublishTaskResultVO>() {
            @Override
            protected BatchTaskPublishTaskResultVO process() {
                return TaskMapstructTransfer.INSTANCE.TaskCheckResultVOToBatchTaskPublishTaskResultVO(taskService.publishTask(detailVO.getTenantId(),
                        detailVO.getId(), detailVO.getUserId(), detailVO.getPublishDesc(), detailVO.getIsRoot(), detailVO.getIgnoreCheck()));
            }
        }.execute();
    }

    @PostMapping(value = "getTaskVersionRecord")
    @ApiOperation("获取任务版本")
    public R<List<BatchTaskVersionDetailResultVO>> getTaskVersionRecord(@RequestBody BatchTaskGetTaskVersionRecordVO detailVO) {
        return new APITemplate<List<BatchTaskVersionDetailResultVO>>() {
            @Override
            protected List<BatchTaskVersionDetailResultVO> process() {
                return TaskMapstructTransfer.INSTANCE.BatchTaskVersionDetailListToResultVOList(taskService.getTaskVersionRecord(
                        detailVO.getTaskId(),
                        detailVO.getPageSize(), detailVO.getPageNo()));
            }
        }.execute();
    }

    @PostMapping(value = "taskVersionScheduleConf")
    @ApiOperation("获取任务版本列表")
    public R<BatchTaskVersionDetailResultVO> taskVersionScheduleConf(@RequestBody BatchTaskTaskVersionScheduleConfVO detailVO) {
        return new APITemplate<BatchTaskVersionDetailResultVO>() {
            @Override
            protected BatchTaskVersionDetailResultVO process() {
                return TaskMapstructTransfer.INSTANCE.BatchTaskVersionDetailToResultVO(taskService.taskVersionScheduleConf(
                        detailVO.getVersionId()));
            }
        }.execute();
    }

    @PostMapping(value = "getJsonTemplate")
    @ApiOperation("获取任务json模板，过滤账号密码")
    public R<String> getJsonTemplate(@RequestBody BatchTaskResourceParamVO paramVO) {
        return new APITemplate<String>() {
            @Override
            protected String process() {
                TaskResourceParam taskResourceParam = TaskMapstructTransfer.INSTANCE.TaskResourceParamVOToTaskResourceParam(paramVO);
                return taskService.getJsonTemplate(taskResourceParam);
            }
        }.execute();
    }

    @PostMapping(value = "addOrUpdateTask")
    @ApiOperation("数据开发-新建/更新 任务")
    public R<TaskCatalogueResultVO> addOrUpdateTask(@RequestBody BatchTaskResourceParamVO paramVO) {
        return new APITemplate<TaskCatalogueResultVO>() {
            @Override
            protected TaskCatalogueResultVO process() {
                TaskResourceParam taskResourceParam = TaskMapstructTransfer.INSTANCE.TaskResourceParamVOToTaskResourceParam(paramVO);
                return TaskMapstructTransfer.INSTANCE.TaskCatalogueVOToResultVO(taskService.addOrUpdateTask(taskResourceParam));
            }
        }.execute();
    }

    @PostMapping(value = "guideToTemplate")
    @ApiOperation("向导模式转模版")
    public R<TaskCatalogueResultVO> guideToTemplate(@RequestBody BatchTaskResourceParamVO paramVO) {
        return new APITemplate<TaskCatalogueResultVO>() {
            @Override
            protected TaskCatalogueResultVO process() {
                TaskResourceParam taskResourceParam = TaskMapstructTransfer.INSTANCE.TaskResourceParamVOToTaskResourceParam(paramVO);
                return TaskMapstructTransfer.INSTANCE.TaskCatalogueVOToResultVO(taskService.guideToTemplate(taskResourceParam));
            }
        }.execute();
    }

    @PostMapping(value = "renameTask")
    @ApiOperation("强制任务重命名（不校验taskVersion、lockVersion）")
    public R<Void> renameTask(@RequestBody BatchTaskRenameTaskVO detailVO) {
        return new APITemplate<Void>() {
            @Override
            protected Void process() {
                 taskService.renameTask(detailVO.getTaskId(), detailVO.getTaskName(), detailVO.getProjectId());
                 return null;
            }
        }.execute();
    }

    @PostMapping(value = "getChildTasks")
    @ApiOperation("获取子任务")
    public R<List<BatchGetChildTasksResultVO>> getChildTasks(@RequestBody BatchTaskGetChildTasksVO tasksVO) {
        return new APITemplate<List<BatchGetChildTasksResultVO>>() {
            @Override
            protected List<BatchGetChildTasksResultVO> process() {
                return TaskMapstructTransfer.INSTANCE.notDeleteTaskVOsToBatchGetChildTasksResultVOs(taskService.getChildTasks(tasksVO.getTaskId()));
            }
        }.execute();
    }

    @PostMapping(value = "deleteTask")
    @ApiOperation("删除任务")
    public R<Long> deleteTask(@RequestBody BatchTaskDeleteTaskVO detailVO) {
        return new APITemplate<Long>() {
            @Override
            protected Long process() {
                return taskService.deleteTask(detailVO.getTaskId(), detailVO.getTenantId(), detailVO.getUserId(), detailVO.getSqlText());
            }
        }.execute();
    }

    @PostMapping(value = "frozenTask")
    @ApiOperation("冻结任务")
    public R<Void> frozenTask(@RequestBody BatchTaskFrozenTaskVO detailVO) {
        return new APITemplate<Void>() {
            @Override
            protected Void process() {
                 taskService.frozenTask(detailVO.getTaskIdList(), detailVO.getScheduleStatus(), detailVO.getUserId(), detailVO.getTenantId(), detailVO.getIsRoot());
                 return null;
            }
        }.execute();
    }

    @PostMapping(value = "getAllTaskList")
    @ApiOperation("获取所有需要需要生成调度的task")
    public R<List<BatchTaskResultVO>> getAllTaskList() {
        return new APITemplate<List<BatchTaskResultVO>>() {
            @Override
            protected List<BatchTaskResultVO> process() {
                return TaskMapstructTransfer.INSTANCE.BatchTaskListToBatchTaskResultVOList(taskService.getAllTaskList());
            }
        }.execute();
    }

    @PostMapping(value = "getSupportJobTypes")
    @ApiOperation("根据支持的引擎类型返回")
    public R<List<BatchTaskGetSupportJobTypesResultVO>> getSupportJobTypes(@RequestBody(required = false) BatchTaskGetSupportJobTypesVO detailVO) {
        return new APITemplate<List<BatchTaskGetSupportJobTypesResultVO>>() {
            @Override
            protected List<BatchTaskGetSupportJobTypesResultVO>  process() {
                return taskService.getSupportJobTypes(detailVO.getTenantId());
            }
        }.execute();
    }

    @PostMapping(value = "forceUpdate")
    @ApiOperation("覆盖更新")
    public R<TaskCatalogueResultVO> forceUpdate(@RequestBody BatchTaskResourceParamVO paramVO) {
        return new APITemplate<TaskCatalogueResultVO>() {
            @Override
            protected TaskCatalogueResultVO process() {
                TaskResourceParam taskResource = TaskMapstructTransfer.INSTANCE.TaskResourceParamVOToTaskResourceParam(paramVO);
                return TaskMapstructTransfer.INSTANCE.TaskCatalogueVOToResultVO(taskService.forceUpdate(taskResource));
            }
        }.execute();
    }

    @PostMapping(value = "getSysParams")
    @ApiOperation("获取所有系统参数")
    public R<Collection<BatchSysParameterResultVO>> getSysParams() {
        return new APITemplate<Collection<BatchSysParameterResultVO>>() {
            @Override
            protected Collection<BatchSysParameterResultVO> process() {
                return TaskMapstructTransfer.INSTANCE.BatchSysParameterCollectionToBatchSysParameterResultVOCollection(taskService.getSysParams());
            }
        }.execute();
    }

    @PostMapping(value = "checkName")
    @ApiOperation("新增离线任务/脚本/资源/自定义脚本，校验名称")
    public R<Void> checkName(@RequestBody BatchTaskCheckNameVO detailVO) {
        return new APITemplate<Void>() {
            @Override
            protected Void process() {
                taskService.checkName(detailVO.getName(), detailVO.getType(), detailVO.getPid(), detailVO.getIsFile(),  detailVO.getProjectId());
                return null;
            }
        }.execute();
    }

    @PostMapping(value = "setOwnerUser")
    @ApiOperation("设置修改负责人")
    public R<Void> setOwnerUser(@RequestBody BatchTaskSetOwnerUserVO detailVO) {
        return new APITemplate<Void>() {
            @Override
            protected Void process() {
                taskService.setOwnerUser(detailVO.getOwnerUserId(), detailVO.getTaskId());
                return null;
            }
        }.execute();
    }

    @PostMapping(value = "getByName")
    @ApiOperation("根据名称查询任务")
    public R<BatchTaskResultVO> getByName(@RequestBody BatchTaskGetByNameVO detailVO) {
        return new APITemplate<BatchTaskResultVO>() {
            @Override
            protected BatchTaskResultVO process() {
                return TaskMapstructTransfer.INSTANCE.BatchTaskToResultVO(taskService.getByName(detailVO.getName(), detailVO.getProjectId()));
            }
        }.execute();
    }

    @PostMapping(value = "allProductGlobalSearch")
    @ApiOperation("所有产品的已提交任务查询")
    public R<List<ScheduleTaskShadeResultVO>> allProductGlobalSearch(@RequestBody AllProductGlobalSearchVO allProductGlobalSearchVO) {
        return new APITemplate<List<ScheduleTaskShadeResultVO>>() {
            @Override
            protected List<ScheduleTaskShadeResultVO> process() {
                return TaskMapstructTransfer.INSTANCE.scheduleTaskShadeTypeVOsToBatchTaskResultVOs(taskService.allProductGlobalSearch(allProductGlobalSearchVO));
            }
        }.execute();
    }


    @PostMapping(value = "getComponentVersionByTaskType")
    @ApiOperation("获取组件版本号")
    public R<List<BatchTaskGetComponentVersionResultVO>> getComponentVersionByTaskType(@RequestBody BatchTaskGetComponentVersionVO getComponentVersionVO) {
        return new APITemplate<List<BatchTaskGetComponentVersionResultVO>>() {
            @Override
            protected List<BatchTaskGetComponentVersionResultVO> process() {
                return taskService.getComponentVersionByTaskType(getComponentVersionVO.getDtuicTenantId(), getComponentVersionVO.getTaskType());
            }
        }.execute();
    }

}
