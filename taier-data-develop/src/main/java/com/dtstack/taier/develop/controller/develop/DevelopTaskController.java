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

package com.dtstack.taier.develop.controller.develop;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.enums.EScheduleStatus;
import com.dtstack.taier.common.exception.ErrorCode;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.common.lang.coc.APITemplate;
import com.dtstack.taier.common.lang.web.R;
import com.dtstack.taier.develop.dto.devlop.TaskResourceParam;
import com.dtstack.taier.develop.dto.devlop.TaskVO;
import com.dtstack.taier.develop.mapstruct.vo.TaskMapstructTransfer;
import com.dtstack.taier.develop.service.develop.impl.DevelopTaskService;
import com.dtstack.taier.develop.service.develop.impl.FlinkTaskService;
import com.dtstack.taier.develop.vo.develop.query.*;
import com.dtstack.taier.develop.vo.develop.result.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Api(value = "任务管理", tags = {"任务管理"})
@RestController
@RequestMapping(value = "/task")
public class DevelopTaskController {

    @Autowired
    private DevelopTaskService batchTaskService;
    @Autowired
    private FlinkTaskService flinkTaskService;

    @PostMapping(value = "getTaskById")
    @ApiOperation("数据开发-根据任务id，查询详情")
    public R<BatchTaskGetTaskByIdResultVO> getTaskById(@RequestBody BatchScheduleTaskVO batchScheduleTaskVO) {
        return new APITemplate<BatchTaskGetTaskByIdResultVO>() {
            @Override
            protected BatchTaskGetTaskByIdResultVO process() {
                TaskVO taskById = batchTaskService.getTaskById(TaskMapstructTransfer.INSTANCE.BatchScheduleTaskVToTaskVO(batchScheduleTaskVO));
                return TaskMapstructTransfer.INSTANCE.TaskVOToBatchTaskGetTaskByIdResultVO(taskById);
            }
        }.execute();
    }

    @PostMapping(value = "checkIsLoop")
    @ApiOperation("检查task与依赖的task是否有构成有向环")
    public R<BatchTaskResultVO> checkIsLoop(@RequestBody BatchTaskCheckIsLoopVO infoVO) {
        return new APITemplate<BatchTaskResultVO>() {
            @Override
            protected BatchTaskResultVO process() {
                return TaskMapstructTransfer.INSTANCE.BatchTaskToResultVO(batchTaskService.checkIsLoop(infoVO.getTaskId(), infoVO.getDependencyTaskId()));
            }
        }.execute();
    }

    @PostMapping(value = "publishTask")
    @ApiOperation("任务发布")
    public R<BatchTaskPublishTaskResultVO> publishTask(@RequestBody BatchTaskPublishTaskVO detailVO) {
        return new APITemplate<BatchTaskPublishTaskResultVO>() {
            @Override
            protected BatchTaskPublishTaskResultVO process() {
                return TaskMapstructTransfer.INSTANCE.TaskCheckResultVOToBatchTaskPublishTaskResultVO(batchTaskService.publishTask(detailVO.getId(),
                        detailVO.getUserId(), detailVO.getPublishDesc(), detailVO.getComponentVersion()));
            }
        }.execute();
    }

    @PostMapping(value = "getTaskVersionRecord")
    @ApiOperation("获取任务版本")
    public R<List<BatchTaskVersionDetailResultVO>> getTaskVersionRecord(@RequestBody BatchTaskGetTaskVersionRecordVO detailVO) {
        return new APITemplate<List<BatchTaskVersionDetailResultVO>>() {
            @Override
            protected List<BatchTaskVersionDetailResultVO> process() {
                return TaskMapstructTransfer.INSTANCE.BatchTaskVersionDetailListToResultVOList(batchTaskService.getTaskVersionRecord(
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
                return TaskMapstructTransfer.INSTANCE.BatchTaskVersionDetailToResultVO(batchTaskService.taskVersionScheduleConf(
                        detailVO.getVersionId()));
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
                return TaskMapstructTransfer.INSTANCE.TaskVOToResultVO(batchTaskService.addOrUpdateTask(taskResourceParam));
            }
        }.execute();
    }

    @PostMapping(value = "canSetIncreConf")
    @ApiOperation(value = "判断任务是否可以配置增量标识")
    public R<Boolean> canSetIncreConf(@RequestBody BatchScheduleTaskVO vo) {
        return new APITemplate<Boolean>() {
            @Override
            protected Boolean process() {
                return batchTaskService.canSetIncreConf(vo.getId());
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
                return TaskMapstructTransfer.INSTANCE.TaskCatalogueVOToResultVO(batchTaskService.guideToTemplate(taskResourceParam));
            }
        }.execute();
    }

    @PostMapping(value = "getChildTasks")
    @ApiOperation("获取子任务")
    public R<List<BatchGetChildTasksResultVO>> getChildTasks(@RequestBody BatchTaskGetChildTasksVO tasksVO) {
        return new APITemplate<List<BatchGetChildTasksResultVO>>() {
            @Override
            protected List<BatchGetChildTasksResultVO> process() {
                return TaskMapstructTransfer.INSTANCE.notDeleteTaskVOsToBatchGetChildTasksResultVOs(batchTaskService.getChildTasks(tasksVO.getTaskId()));
            }
        }.execute();
    }

    @PostMapping(value = "deleteTask")
    @ApiOperation("删除任务")
    public R<Long> deleteTask(@RequestBody BatchTaskDeleteTaskVO detailVO) {
        return new APITemplate<Long>() {
            @Override
            protected Long process() {
                return batchTaskService.deleteTask(detailVO.getTaskId(), detailVO.getUserId(), detailVO.getSqlText());
            }
        }.execute();
    }

    @PostMapping(value = "getSysParams")
    @ApiOperation("获取所有系统参数")
    public R<Collection<BatchSysParameterResultVO>> getSysParams() {
        return new APITemplate<Collection<BatchSysParameterResultVO>>() {
            @Override
            protected Collection<BatchSysParameterResultVO> process() {
                return TaskMapstructTransfer.INSTANCE.BatchSysParameterCollectionToBatchSysParameterResultVOCollection(batchTaskService.getSysParams());
            }
        }.execute();
    }

    @PostMapping(value = "checkName")
    @ApiOperation("新增离线任务/脚本/资源/自定义脚本，校验名称")
    public R<Void> checkName(@RequestBody BatchTaskCheckNameVO detailVO) {
        return new APITemplate<Void>() {
            @Override
            protected Void process() {
                batchTaskService.checkName(detailVO.getName(), detailVO.getType(), detailVO.getPid(), detailVO.getIsFile(), detailVO.getTenantId());
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
                return TaskMapstructTransfer.INSTANCE.BatchTaskToResultVO(batchTaskService.getByName(detailVO.getName(), detailVO.getTenantId()));
            }
        }.execute();
    }

    @PostMapping(value = "getComponentVersionByTaskType")
    @ApiOperation("获取组件版本号")
    public R<List<BatchTaskGetComponentVersionResultVO>> getComponentVersionByTaskType(@RequestBody BatchTaskGetComponentVersionVO getComponentVersionVO) {
        return new APITemplate<List<BatchTaskGetComponentVersionResultVO>>() {
            @Override
            protected List<BatchTaskGetComponentVersionResultVO> process() {
                return batchTaskService.getComponentVersionByTaskType(getComponentVersionVO.getTenantId(), getComponentVersionVO.getTaskType());
            }
        }.execute();
    }

    @PostMapping(value = "allProductGlobalSearch")
    @ApiOperation("所有产品的已提交任务查询")
    public R<List<BatchAllProductGlobalReturnVO>> allProductGlobalSearch(@RequestBody AllProductGlobalSearchVO allProductGlobalSearchVO) {
        return new APITemplate<List<BatchAllProductGlobalReturnVO>>() {
            @Override
            protected List<BatchAllProductGlobalReturnVO> process() {
                return batchTaskService.allProductGlobalSearch(allProductGlobalSearchVO);
            }
        }.execute();
    }

    @PostMapping(value = "frozenTask")
    @ApiOperation("所有产品的已提交任务查询")
    public R<Boolean> frozenTask(@RequestBody BatchFrozenTaskVO vo) {
        return new APITemplate<Boolean>() {
            @Override
            protected void checkParams() throws IllegalArgumentException {
                EScheduleStatus targetStatus = EScheduleStatus.getStatus(vo.getScheduleStatus());
                if (Objects.isNull(targetStatus)) {
                    throw new RdosDefineException(ErrorCode.INVALID_PARAMETERS);
                }
                if (CollectionUtils.isEmpty(vo.getTaskIds())) {
                    throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_TASK);
                }
            }

            @Override
            protected Boolean process() {
                batchTaskService.frozenTask(vo.getTaskIds(), vo.getScheduleStatus(), vo.getUserId());
                return true;
            }
        }.execute();
    }

    @PostMapping(value = "getSupportJobTypes")
    @ApiOperation("根据支持的引擎类型返回")
    public R<List<BatchTaskGetSupportJobTypesResultVO>> getSupportJobTypes(@RequestBody(required = false) BatchTaskGetSupportJobTypesVO detailVO) {
        return new APITemplate<List<BatchTaskGetSupportJobTypesResultVO>>() {
            @Override
            protected List<BatchTaskGetSupportJobTypesResultVO>  process() {
                return batchTaskService.getSupportJobTypes(detailVO.getTenantId());
            }
        }.execute();
    }

    @PostMapping(value = "getIncreColumn")
    @ApiOperation(value = "获取可以作为增量标识的字段")
    public R<List<JSONObject>> getIncreColumn(@RequestBody(required = false) BatchDataSourceIncreColumnVO vo) {
        return new APITemplate<List<JSONObject>>() {
            @Override
            protected List<JSONObject> process() {
                return batchTaskService.getIncreColumn(vo.getSourceId(), vo.getTableName(), vo.getSchema());
            }
        }.execute();
    }

}
