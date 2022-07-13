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
import com.dtstack.taier.develop.vo.develop.query.AllProductGlobalSearchVO;
import com.dtstack.taier.develop.vo.develop.query.DevelopDataSourceIncreColumnVO;
import com.dtstack.taier.develop.vo.develop.query.DevelopFrozenTaskVO;
import com.dtstack.taier.develop.vo.develop.query.DevelopScheduleTaskVO;
import com.dtstack.taier.develop.vo.develop.query.DevelopTaskCheckIsLoopVO;
import com.dtstack.taier.develop.vo.develop.query.DevelopTaskCheckNameVO;
import com.dtstack.taier.develop.vo.develop.query.DevelopTaskDeleteTaskVO;
import com.dtstack.taier.develop.vo.develop.query.DevelopTaskEditVO;
import com.dtstack.taier.develop.vo.develop.query.DevelopTaskGetByNameVO;
import com.dtstack.taier.develop.vo.develop.query.DevelopTaskGetChildTasksVO;
import com.dtstack.taier.develop.vo.develop.query.DevelopTaskGetComponentVersionVO;
import com.dtstack.taier.develop.vo.develop.query.DevelopTaskGetSupportJobTypesVO;
import com.dtstack.taier.develop.vo.develop.query.DevelopTaskGetTaskVersionRecordVO;
import com.dtstack.taier.develop.vo.develop.query.DevelopTaskPublishTaskVO;
import com.dtstack.taier.develop.vo.develop.query.DevelopTaskResourceParamVO;
import com.dtstack.taier.develop.vo.develop.query.DevelopTaskTaskVersionScheduleConfVO;
import com.dtstack.taier.develop.vo.develop.result.DevelopAllProductGlobalReturnVO;
import com.dtstack.taier.develop.vo.develop.result.DevelopGetChildTasksResultVO;
import com.dtstack.taier.develop.vo.develop.result.DevelopSysParameterResultVO;
import com.dtstack.taier.develop.vo.develop.result.DevelopTaskGetComponentVersionResultVO;
import com.dtstack.taier.develop.vo.develop.result.DevelopTaskGetSupportJobTypesResultVO;
import com.dtstack.taier.develop.vo.develop.result.DevelopTaskGetTaskByIdResultVO;
import com.dtstack.taier.develop.vo.develop.result.DevelopTaskPublishTaskResultVO;
import com.dtstack.taier.develop.vo.develop.result.DevelopTaskResultVO;
import com.dtstack.taier.develop.vo.develop.result.DevelopTaskVersionDetailResultVO;
import com.dtstack.taier.develop.vo.develop.result.TaskCatalogueResultVO;
import com.google.common.base.Preconditions;
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
    private DevelopTaskService developTaskService;

    @PostMapping(value = "getTaskById")
    @ApiOperation("数据开发-根据任务id，查询详情")
    public R<DevelopTaskGetTaskByIdResultVO> getTaskById(@RequestBody DevelopScheduleTaskVO developScheduleTaskVO) {
        return new APITemplate<DevelopTaskGetTaskByIdResultVO>() {
            @Override
            protected DevelopTaskGetTaskByIdResultVO process() {
                TaskVO taskById = developTaskService.getTaskById(TaskMapstructTransfer.INSTANCE.DevelopScheduleTaskVToTaskVO(developScheduleTaskVO));
                return TaskMapstructTransfer.INSTANCE.TaskVOToDevelopTaskGetTaskByIdResultVO(taskById);
            }
        }.execute();
    }

    @PostMapping(value = "checkIsLoop")
    @ApiOperation("检查task与依赖的task是否有构成有向环")
    public R<DevelopTaskResultVO> checkIsLoop(@RequestBody DevelopTaskCheckIsLoopVO infoVO) {
        return new APITemplate<DevelopTaskResultVO>() {
            @Override
            protected DevelopTaskResultVO process() {
                return TaskMapstructTransfer.INSTANCE.DevelopTaskToResultVO(developTaskService.checkIsLoop(infoVO.getTaskId(), infoVO.getDependencyTaskId()));
            }
        }.execute();
    }

    @PostMapping(value = "publishTask")
    @ApiOperation("任务发布")
    public R<DevelopTaskPublishTaskResultVO> publishTask(@RequestBody DevelopTaskPublishTaskVO detailVO) {
        return new APITemplate<DevelopTaskPublishTaskResultVO>() {
            @Override
            protected DevelopTaskPublishTaskResultVO process() {
                return TaskMapstructTransfer.INSTANCE.TaskCheckResultVOToDevelopTaskPublishTaskResultVO(developTaskService.publishTask(detailVO.getId(),
                        detailVO.getUserId(), detailVO.getPublishDesc(), detailVO.getComponentVersion()));
            }
        }.execute();
    }

    @PostMapping(value = "getTaskVersionRecord")
    @ApiOperation("获取任务版本")
    public R<List<DevelopTaskVersionDetailResultVO>> getTaskVersionRecord(@RequestBody DevelopTaskGetTaskVersionRecordVO detailVO) {
        return new APITemplate<List<DevelopTaskVersionDetailResultVO>>() {
            @Override
            protected List<DevelopTaskVersionDetailResultVO> process() {
                return TaskMapstructTransfer.INSTANCE.DevelopTaskVersionDetailListToResultVOList(developTaskService.getTaskVersionRecord(
                        detailVO.getTaskId(),
                        detailVO.getPageSize(), detailVO.getPageNo()));
            }
        }.execute();
    }

    @PostMapping(value = "taskVersionScheduleConf")
    @ApiOperation("获取任务版本列表")
    public R<DevelopTaskVersionDetailResultVO> taskVersionScheduleConf(@RequestBody DevelopTaskTaskVersionScheduleConfVO detailVO) {
        return new APITemplate<DevelopTaskVersionDetailResultVO>() {
            @Override
            protected DevelopTaskVersionDetailResultVO process() {
                return TaskMapstructTransfer.INSTANCE.DevelopTaskVersionDetailToResultVO(developTaskService.taskVersionScheduleConf(
                        detailVO.getVersionId()));
            }
        }.execute();
    }

    @PostMapping(value = "addOrUpdateTask")
    @ApiOperation("数据开发-新建/更新 任务")
    public R<TaskCatalogueResultVO> addOrUpdateTask(@RequestBody DevelopTaskResourceParamVO paramVO) {
        return new APITemplate<TaskCatalogueResultVO>() {
            @Override
            protected TaskCatalogueResultVO process() {
                TaskResourceParam taskResourceParam = TaskMapstructTransfer.INSTANCE.TaskResourceParamVOToTaskResourceParam(paramVO);
                return TaskMapstructTransfer.INSTANCE.TaskVOToResultVO(developTaskService.addOrUpdateTaskNew(taskResourceParam));
            }
        }.execute();
    }

    @PostMapping(value = "guideToTemplate")
    @ApiOperation("向导模式转模版")
    public R<TaskCatalogueResultVO> guideToTemplate(@RequestBody DevelopTaskResourceParamVO paramVO) {
        return new APITemplate<TaskCatalogueResultVO>() {
            @Override
            protected TaskCatalogueResultVO process() {
                TaskResourceParam taskResourceParam = TaskMapstructTransfer.INSTANCE.TaskResourceParamVOToTaskResourceParam(paramVO);
                return TaskMapstructTransfer.INSTANCE.TaskCatalogueVOToResultVO(developTaskService.guideToTemplate(taskResourceParam));
            }
        }.execute();
    }

    @PostMapping(value = "getChildTasks")
    @ApiOperation("获取子任务")
    public R<List<DevelopGetChildTasksResultVO>> getChildTasks(@RequestBody DevelopTaskGetChildTasksVO tasksVO) {
        return new APITemplate<List<DevelopGetChildTasksResultVO>>() {
            @Override
            protected List<DevelopGetChildTasksResultVO> process() {
                return TaskMapstructTransfer.INSTANCE.notDeleteTaskVOsToDevelopGetChildTasksResultVOs(developTaskService.getChildTasks(tasksVO.getTaskId()));
            }
        }.execute();
    }

    @PostMapping(value = "deleteTask")
    @ApiOperation("删除任务")
    public R<Long> deleteTask(@RequestBody DevelopTaskDeleteTaskVO detailVO) {
        return new APITemplate<Long>() {
            @Override
            protected Long process() {
                return developTaskService.deleteTask(detailVO.getTaskId(), detailVO.getUserId(), detailVO.getSqlText());
            }
        }.execute();
    }

    @PostMapping(value = "getSysParams")
    @ApiOperation("获取所有系统参数")
    public R<Collection<DevelopSysParameterResultVO>> getSysParams() {
        return new APITemplate<Collection<DevelopSysParameterResultVO>>() {
            @Override
            protected Collection<DevelopSysParameterResultVO> process() {
                return TaskMapstructTransfer.INSTANCE.DevelopSysParameterCollectionToDevelopSysParameterResultVOCollection(developTaskService.getSysParams());
            }
        }.execute();
    }

    @PostMapping(value = "checkName")
    @ApiOperation("新增离线任务/脚本/资源/自定义脚本，校验名称")
    public R<Void> checkName(@RequestBody DevelopTaskCheckNameVO detailVO) {
        return new APITemplate<Void>() {
            @Override
            protected Void process() {
                developTaskService.checkName(detailVO.getName(), detailVO.getType(), detailVO.getPid(), detailVO.getIsFile(), detailVO.getTenantId());
                return null;
            }
        }.execute();
    }

    @PostMapping(value = "getByName")
    @ApiOperation("根据名称查询任务")
    public R<DevelopTaskResultVO> getByName(@RequestBody DevelopTaskGetByNameVO detailVO) {
        return new APITemplate<DevelopTaskResultVO>() {
            @Override
            protected DevelopTaskResultVO process() {
                return TaskMapstructTransfer.INSTANCE.DevelopTaskToResultVO(developTaskService.getByName(detailVO.getName(), detailVO.getTenantId()));
            }
        }.execute();
    }

    @PostMapping(value = "getComponentVersionByTaskType")
    @ApiOperation("获取组件版本号")
    public R<List<DevelopTaskGetComponentVersionResultVO>> getComponentVersionByTaskType(@RequestBody DevelopTaskGetComponentVersionVO getComponentVersionVO) {
        return new APITemplate<List<DevelopTaskGetComponentVersionResultVO>>() {
            @Override
            protected List<DevelopTaskGetComponentVersionResultVO> process() {
                return developTaskService.getComponentVersionByTaskType(getComponentVersionVO.getTenantId(), getComponentVersionVO.getTaskType());
            }
        }.execute();
    }

    @PostMapping(value = "allProductGlobalSearch")
    @ApiOperation("所有产品的已提交任务查询")
    public R<List<DevelopAllProductGlobalReturnVO>> allProductGlobalSearch(@RequestBody AllProductGlobalSearchVO allProductGlobalSearchVO) {
        return new APITemplate<List<DevelopAllProductGlobalReturnVO>>() {
            @Override
            protected List<DevelopAllProductGlobalReturnVO> process() {
                return developTaskService.allProductGlobalSearch(allProductGlobalSearchVO);
            }
        }.execute();
    }

    @PostMapping(value = "frozenTask")
    @ApiOperation("所有产品的已提交任务查询")
    public R<Boolean> frozenTask(@RequestBody DevelopFrozenTaskVO vo) {
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
                developTaskService.frozenTask(vo.getTaskIds(), vo.getScheduleStatus(), vo.getUserId());
                return true;
            }
        }.execute();
    }

    @PostMapping(value = "getSupportJobTypes")
    @ApiOperation("根据支持的引擎类型返回")
    public R<List<DevelopTaskGetSupportJobTypesResultVO>> getSupportJobTypes(@RequestBody(required = false) DevelopTaskGetSupportJobTypesVO detailVO) {
        return new APITemplate<List<DevelopTaskGetSupportJobTypesResultVO>>() {
            @Override
            protected List<DevelopTaskGetSupportJobTypesResultVO>  process() {
                return developTaskService.getSupportJobTypes(detailVO.getTenantId());
            }
        }.execute();
    }

    @PostMapping(value = "getIncreColumn")
    @ApiOperation(value = "获取可以作为增量标识的字段")
    public R<List<JSONObject>> getIncreColumn(@RequestBody(required = false) DevelopDataSourceIncreColumnVO vo) {
        return new APITemplate<List<JSONObject>>() {
            @Override
            protected List<JSONObject> process() {
                return developTaskService.getIncreColumn(vo.getSourceId(), vo.getTableName(), vo.getSchema());
            }
        }.execute();
    }

    @PostMapping(value = "editTask")
    @ApiOperation(value = "编辑任务")
    public R<Void> editTask(@RequestBody DevelopTaskEditVO vo) {
        return new APITemplate<Void>() {
            @Override
            protected void checkParams() throws IllegalArgumentException {
                Preconditions.checkNotNull(vo.getTaskId(), "parameters of taskId not be null.");
                Preconditions.checkNotNull(vo.getName(), "parameters of name not be null.");
                Preconditions.checkNotNull(vo.getCatalogueId(), "parameters of catalogueId not be null.");
            }

            @Override
            protected Void process() {
                developTaskService.editTask(vo.getTaskId(), vo.getName(), vo.getCatalogueId(), vo.getDesc(),
                        vo.getTenantId(), vo.getComponentVersion());
                return null;
            }
        }.execute();
    }

}
