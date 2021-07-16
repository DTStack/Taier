package com.dtstack.batch.web.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.batch.common.exception.RdosDefineException;
import com.dtstack.batch.domain.Project;
import com.dtstack.batch.domain.User;
import com.dtstack.batch.mapstruct.vo.ProjectMapstructTransfer;
import com.dtstack.batch.service.auth.AuthCode;
import com.dtstack.batch.service.impl.ProjectService;
import com.dtstack.batch.service.multiengine.EngineInfo;
import com.dtstack.batch.vo.*;
import com.dtstack.batch.web.pager.PageResult;
import com.dtstack.batch.web.project.vo.query.*;
import com.dtstack.batch.web.project.vo.result.*;
import com.dtstack.batch.web.role.vo.result.BatchUserRoleResultVO;
import com.dtstack.batch.web.table.vo.result.BatchTableTypeResultVO;
import com.dtstack.batch.web.user.vo.result.BatchUserBaseResultVO;
import dt.insight.plat.lang.coc.template.APITemplate;
import dt.insight.plat.autoconfigure.web.security.permissions.annotation.Security;
import dt.insight.plat.lang.exception.biz.BizException;
import dt.insight.plat.lang.web.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Api(value = "项目管理", tags = {"项目管理"})
@RestController
@RequestMapping(value = "/api/rdos/{common|batch}/project")
public class ProjectController {

    @Autowired
    private ProjectService projectService;


    @ApiOperation(value = "删除项目")
    @PostMapping(value = "deleteProject")
    @Security(code = AuthCode.PROJECT_EDIT)
    public R<Long> deleteProject(@RequestBody BatchProjectBaseVO vo) {

        return new APITemplate<Long>() {
            @Override
            protected Long process() throws BizException {
                return projectService.deleteProject(vo.getIsRoot(), vo.getUserId(), vo.getTenantId(), vo.getProjectId());
            }
        }.execute();
    }

    @ApiOperation(value = "删除项目前置判断")
    @PostMapping(value = "preDeleteProject")
    @Security(code = AuthCode.PROJECT_EDIT)
    public R<BatchProjectDelCheckResultVO> preDeleteProject(@RequestBody BatchProjectBaseVO vo) {
        return new APITemplate<BatchProjectDelCheckResultVO>() {
            @Override
            protected BatchProjectDelCheckResultVO process() throws BizException {
                ProjectDelCheckResultVo projectDelCheckResultVo = projectService.preDeleteProject(vo.getIsRoot(), vo.getUserId(), vo.getTenantId(), vo.getProjectId());
                return ProjectMapstructTransfer.INSTANCE.projectDelCheckResultVoToBatchProjectDelCheckResultVO(projectDelCheckResultVo);
            }
        }.execute();
    }

    @ApiOperation(value = "获得项目详情")
    @PostMapping(value = "getProjectByProjectId")
    @Security(code = AuthCode.PROJECT_CONFIGURE_QUERY)
    public R<BatchQueryProjectResultVO> getProjectByProjectId(@RequestBody BatchProjectBaseVO vo) {
        return new APITemplate<BatchQueryProjectResultVO>() {
            @Override
            protected BatchQueryProjectResultVO process() throws BizException {
                ProjectVO projectByProjectId = projectService.getProjectByProjectId(vo.getProjectId(), vo.getIsRoot(), vo.getUserId());
                return ProjectMapstructTransfer.INSTANCE.projectVOToBatchQueryProjectResultVO(projectByProjectId);
            }
        }.execute();
    }


    @ApiOperation(value = "获取项目在用的引擎信息")
    @PostMapping(value = "getProjectUsedEngineInfo")
    @Security(code = AuthCode.PROJECT_EDIT)
    public R<Map<Integer, BatchEngineInfoResultVO>> getProjectUsedEngineInfo(@RequestBody(required = false) BatchProjectBaseVO vo) {

        return new APITemplate<Map<Integer, BatchEngineInfoResultVO>>() {

            @Override
            protected Map<Integer, BatchEngineInfoResultVO> process() throws BizException {
                Map<Integer, EngineInfo> projectUsedEngineInfo = projectService.getProjectUsedEngineInfo(vo.getTenantId(), vo.getDtuicTenantId(), vo.getProjectId());
                String s = JSONObject.toJSONString(projectUsedEngineInfo);
                Map<Integer, BatchEngineInfoResultVO> map = JSON.parseObject(s,Map.class);
                return map;
            }
        }.execute();
    }

    
    @ApiOperation(value = "获取项目未使用的引擎信息")
    @Security(code = AuthCode.PROJECT_EDIT)
    @PostMapping(value = "getProjectUnusedEngineInfo")
    public R<Map<Integer, BatchEngineInfoResultVO>> getProjectUnusedEngineInfo(@RequestBody(required = false) BatchProjectBaseVO vo) {

        return new APITemplate<Map<Integer, BatchEngineInfoResultVO>>() {
            @Override
            protected Map<Integer, BatchEngineInfoResultVO> process() throws BizException {
                Map<Integer, EngineInfo> projectUnusedEngineInfo = projectService.getProjectUnusedEngineInfo(vo.getTenantId(), vo.getDtuicTenantId(), vo.getProjectId());
                String s = JSONObject.toJSONString(projectUnusedEngineInfo);
                Map<Integer, BatchEngineInfoResultVO> map = JSON.parseObject(s,Map.class);
                return map;
            }
        }.execute();
    }


    @ApiOperation(value = "角色权限改版后，项目成员管理")
    @PostMapping(value = "getProjectUsers")
    @Security(code = AuthCode.PROJECT_MEMBER_QUERY)
    public R<PageResult<List<BatchUserRoleResultVO>>> getProjectUsers(@RequestBody BatchProjectGetProjectUsersVO vo) {

        return new APITemplate<PageResult<List<BatchUserRoleResultVO>>>() {
            @Override
            protected PageResult<List<BatchUserRoleResultVO>> process() throws BizException {
                PageResult<List<UserRoleVO>> projectUsers = projectService.getProjectUsers(vo.getProjectId(), vo.getTenantId(), vo.getUserId(), vo.getName(), vo.getCurrentPage(), vo.getPageSize());
                return ProjectMapstructTransfer.INSTANCE.userRoleVOPageResultToBatchUserRoleResultVOPageResult(projectUsers);
            }
        }.execute();
    }

    @ApiOperation(value = "获取不在项目的UIC用户")
    @PostMapping(value = "getUicUsersNotInProject")
    public R<List<BatchProjectGetUicNotInProjectResultVO>> getUicUsersNotInProject(@RequestBody(required = false) BatchProjectGetUicNotInProjectVO vo) {
        return new APITemplate<List<BatchProjectGetUicNotInProjectResultVO>>() {
            @Override
            protected List<BatchProjectGetUicNotInProjectResultVO> process() throws BizException {
                return projectService.getUicUsersNotInProject(vo.getProjectId(), vo.getDtuicTenantId());
            }
        }.execute();
    }


    @ApiOperation(value = "获取租户支持表类型")
    @PostMapping(value = "getSupportTableType")
    public R<List<BatchTableTypeResultVO>> getSupportTableType(@RequestBody(required = false) BatchProjectBaseVO vo) {

        return new APITemplate<List<BatchTableTypeResultVO>>() {
            @Override
            protected List<BatchTableTypeResultVO> process() throws BizException {
                List<BatchTableTypeVo> supportTableType = projectService.getSupportTableType(vo.getDtuicTenantId());
                return ProjectMapstructTransfer.INSTANCE.batchTableTypeVoListToBatchTableTypeResultVOList(supportTableType);
            }
        }.execute();
    }


    @ApiOperation(value = "获取项目支持表类型")
    @PostMapping(value = "getProjectSupportTableType")
    public R<List<BatchTableTypeResultVO>> getProjectSupportTableType(@RequestBody BatchProjectBaseVO vo) {

        return new APITemplate<List<BatchTableTypeResultVO>>() {
            @Override
            protected List<BatchTableTypeResultVO> process() throws BizException {
                List<BatchTableTypeVo> projectSupportTableType = projectService.getProjectSupportTableType(vo.getProjectId());
                return ProjectMapstructTransfer.INSTANCE.batchTableTypeVoListToBatchTableTypeResultVOList(projectSupportTableType);
            }
        }.execute();
    }


    @ApiOperation(value = "修改项目信息")
    @PostMapping(value = "updateProjectInfo")
    @Security(code = AuthCode.PROJECT_CONFIGURE_EDIT)
    public R<Void> updateProjectInfo(@RequestBody BatchProjectCreateVO vo) {

        return new APITemplate<Void>() {
            @Override
            protected Void process() throws BizException {
                projectService.updateProjectInfo(vo.getProjectId(),vo.getProjectDesc(), vo.getProjectAlias(),vo.getUserId());
                return null;
            }
        }.execute();

    }

    @ApiOperation(value = "获取项目信息")
    @PostMapping(value = "getProjectInfo")
    public R<Map<String, Integer>> getProjectInfo(@RequestBody BatchProjectBaseVO vo) {

        return new APITemplate<Map<String, Integer>>() {
            @Override
            protected Map<String, Integer> process() throws BizException {
                return projectService.getProjectInfo(vo.getUserId(), vo.getTenantId(), vo.getIsRoot());
            }
        }.execute();
    }


    @ApiOperation(value = "控制台顶端-项目下拉列表")
    @PostMapping(value = "getProjects")
    public R<List<BatchProjectResultVO>> getProjects(@RequestBody(required = false) BatchProjectBaseVO vo) {

        return new APITemplate<List<BatchProjectResultVO>>() {
            @Override
            protected List<BatchProjectResultVO> process() throws BizException {
                List<Project> projects = projectService.getProjects(vo.getUserId(), vo.getIsAdmin(), vo.getTenantId(), vo.getIsRoot(), vo.getAppType());
                return ProjectMapstructTransfer.INSTANCE.listProjectToBatchProjectResultListVO(projects);
            }
        }.execute();
    }

    @ApiOperation(value = "获取项目支持引擎类型")
    @PostMapping(value = "getProjectSupportEngineType")
    public R<List<BatchTableTypeResultVO>> getProjectSupportEngineType(@RequestBody(required = false) BatchProjectBaseVO vo) {

        return new APITemplate<List<BatchTableTypeResultVO>>() {
            @Override
            protected List<BatchTableTypeResultVO> process() throws BizException {
                List<BatchTableTypeVo> projectSupportEngineType = projectService.getProjectSupportEngineType(vo.getProjectId());
                return ProjectMapstructTransfer.INSTANCE.batchTableTypeVoListToBatchTableTypeResultVOList(projectSupportEngineType);
            }
        }.execute();
    }


    @ApiOperation(value = "获取所有项目，在筛选下拉框里使用")
    @PostMapping(value = "getAllProjects")
    public R<List<BatchProjectResultVO>> getAllProjects(@RequestBody(required = false) BatchProjectGetAllProjectsVO vo) {

        return new APITemplate<List<BatchProjectResultVO>>() {
            @Override
            protected List<BatchProjectResultVO> process() throws BizException {
                List<Project> allProjects = projectService.getAllProjects(vo.getTenantId(), vo.getTotal(), vo.getUserId(), vo.getIsRoot());
                return ProjectMapstructTransfer.INSTANCE.listProjectToBatchProjectResultListVO(allProjects);
            }
        }.execute();
    }


    @ApiOperation(value = "根据项目名/别名 分页查询")
    @PostMapping(value = "getProjectList")
    public R<PageResult<List<BatchGetProjectListResultVO>>> getProjectList(@RequestBody BatchProjectGetProjectListVO vo) {

        return new APITemplate<PageResult<List<BatchGetProjectListResultVO>>>() {
            @Override
            protected PageResult<List<BatchGetProjectListResultVO>> process() throws BizException {
                PageResult<List<ProjectOverviewVO>> projectList = new PageResult<>();
                try {
                    projectList = projectService.getProjectList(vo.getFuzzyName(), vo.getUserId(), vo.getIsAdmin(), vo.getProjectType(), vo.getTenantId(), vo.getOrderBy(), vo.getSort(), vo.getPage(), vo.getPageSize(), vo.getIsRoot(), vo.getCatalogueId());
                } catch (InterruptedException e) {
                    throw new RdosDefineException(e.getMessage(), e);
                }
                return ProjectMapstructTransfer.INSTANCE.projectDTOPageResultToBatchGetProjectListResultVOPageResult(projectList);
            }
        }.execute();
    }


    @ApiOperation(value = "获取租户下所有的项目")
    @PostMapping(value = "getTenantProjects")
    public R<List<BatchProjectResultVO>> getTenantProjects(@RequestBody(required = false) BatchProjectBaseVO vo) {

        return new APITemplate<List<BatchProjectResultVO>>() {
            @Override
            protected List<BatchProjectResultVO> process() throws BizException {
                List<Project> tenantProjects = projectService.getTenantProjects(vo.getTenantId());
                return ProjectMapstructTransfer.INSTANCE.listProjectToBatchProjectResultListVO(tenantProjects);
            }
        }.execute();
    }


    @ApiOperation(value = "开启或关闭调度")
    @Security(code = AuthCode.TEST_PRODUCE_EDIT_SCHEDULE_STATUS)
    @PostMapping(value = "closeOrOpenSchedule")
    public R<Void> closeOrOpenSchedule(@RequestBody BatchProjectCloseOrOpenScheduleVO vo) {

        return new APITemplate<Void>() {
            @Override
            protected Void process() throws BizException {
                projectService.closeOrOpenSchedule(vo.getTenantId(), vo.getProjectId(), vo.getStatus(), vo.getUserId());
                return null;
            }
        }.execute();
    }

    @ApiOperation(value = "是否开启下载查询结果")
    @Security(code = AuthCode.TEST_PRODUCE_EDIT_SCHEDULE_STATUS)
    @PostMapping(value = "closeOrOpenDownloadSelect")
    public R<Void> closeOrOpenDownloadSelect(@RequestBody BatchProjectCloseOrOpenScheduleVO vo) {

        return new APITemplate<Void>() {
            @Override
            protected Void process() throws BizException {
                projectService.closeOrOpenDownloadSelect(vo.getTenantId(), vo.getProjectId(), vo.getStatus(), vo.getUserId());
                return null;
            }
        }.execute();
    }


    @ApiOperation(value = "项目添加新的引擎类型")
    @PostMapping(value = "addNewEngine")
    public R<Long> addNewEngine(@RequestBody BatchProjectAddNewEngineVO vo) {

        return new APITemplate<Long>() {
            @Override
            protected Long process() throws BizException {
                return projectService.addNewEngine(ProjectMapstructTransfer.INSTANCE.newEngineVoToEngineVo(vo), vo.getUserId(), vo.getDtuicTenantId(), vo.getProjectId());
            }
        }.execute();
    }

    @ApiOperation(value = "获取当前租户下指定引擎类型db的表信息")
    @PostMapping(value = "getDBTableList")
    public R<List<String>> getDBTableList(@RequestBody BatchProjectGetDBTableListVO vo) {

        return new APITemplate<List<String>>() {
            @Override
            protected List<String> process() throws BizException {
                return projectService.getDBTableList(vo.getProjectId(),vo.getDtuicTenantId(), vo.getEngineType(), vo.getDbName(), vo.getUserId());
            }
        }.execute();
    }

    @ApiOperation("成功初始化引擎")
    @PostMapping(value = "hasSuccessInitEngine")
    public R<Integer> hasSuccessInitEngine(@RequestBody BatchProjectHasSuccessInitEngineVO vo) {

        return new APITemplate<Integer>() {
            @Override
            protected Integer process() throws BizException {
                return projectService.hasSuccessInitEngine(vo.getProjectId(), vo.getEngineType());
            }
        }.execute();
    }

    @ApiOperation(value = "获取可创建项目的database")
    @Security(code = AuthCode.PROJECT_EDIT)
    @PostMapping(value = "getRetainDB")
    public R<Map<Integer, List<String>>> getRetainDB(@RequestBody BatchProjectGetRetainDBVO vo) {
        return new APITemplate<Map<Integer, List<String>>>() {
            @Override
            protected  Map<Integer, List<String>> process() throws BizException {
                return projectService.getRetainDB(vo.getDtuicTenantId(), vo.getBackupFilter(), vo.getUserId(), vo.getEngineType());
            }
        }.execute();
    }


    @ApiOperation(value = "创建项目")
    @Security(code = AuthCode.PROJECT_EDIT)
    @PostMapping(value = "createProject")
    public R<Long> createProject(@RequestBody BatchProjectCreateVO vo) {

        return new APITemplate<Long>() {
            @Override
            protected Long process() throws BizException {
                return projectService.createProject(ProjectMapstructTransfer.INSTANCE.newCreateVoToCreateVo(vo), vo.getUserId(), vo.getDtuicTenantId(), vo.getDtToken(), vo.getTenantId());
            }
        }.execute();
    }

    @ApiOperation(value = "获取项目首页的统计信息")
    @PostMapping(value = "getHomePages")
    public R<BatchHomePageResultVO> getHomePages(@RequestBody(required = false) BatchProjectBaseVO vo) {

        return new APITemplate<BatchHomePageResultVO>() {
            @Override
            protected BatchHomePageResultVO process() throws BizException {
                HomePageVo homePages = projectService.getHomePages(vo.getUserId(), vo.getIsAdmin(), vo.getTenantId(), vo.getIsRoot());
                return ProjectMapstructTransfer.INSTANCE.newEngineVoToBatchHomePageResultVO(homePages);
            }
        }.execute();
    }

    @ApiOperation(value = "获取租户下所有的项目")
    @PostMapping(value = "getAllByTenantId")
    public R<List<BatchTenantProjectResultVO>> getAllByTenantId(@RequestBody BatchProjectBaseVO vo) {

        return new APITemplate<List<BatchTenantProjectResultVO>>() {
            @Override
            protected List<BatchTenantProjectResultVO> process() throws BizException {
                List<TenantProjectVO> allByTenantId = projectService.getAllByTenantId(vo.getUserId(), vo.getSearchTenantId(), vo.getDtToken(), vo.getIsAdmin(), vo.getIsRoot());
                return ProjectMapstructTransfer.INSTANCE.tenantProjectVOListToBatchTenantProjectResultVOList(allByTenantId);
            }
        }.execute();
    }

    @ApiOperation(value = "绑定测试环境和生产环境")
    @Security(code = AuthCode.TEST_PRODUCE_BINDING_PROJECT)
    @PostMapping(value = "bindingProject")
    public R<Void> bindingProject(@RequestBody(required = false) BatchProjectBindingProjectVO vo) {

        return new APITemplate<Void>() {
            @Override
            protected Void process() throws BizException {
                projectService.bindingProject(vo.getTenantId(), vo.getProjectId(), vo.getProduceProjectId(), vo.getUserId());
                return null;
            }
        }.execute();
    }


    @ApiOperation(value = "获取待绑定项目列表")
    @PostMapping(value = "getBindingProjects")
    public R<List<BatchProjectResultVO>> getBindingProjects(@RequestBody BatchProjectGetBindingProjectsVO vo) {

        return new APITemplate<List<BatchProjectResultVO>>() {
            @Override
            protected List<BatchProjectResultVO> process() throws BizException {
                List<Project> bindingProjects = projectService.getBindingProjects(vo.getTenantId(), vo.getProjectId(), vo.getProjectAlias(), vo.getTargetTenantId(), vo.getDtToken());
                return ProjectMapstructTransfer.INSTANCE.listProjectToBatchProjectResultListVO(bindingProjects);
            }
        }.execute();
    }

    
    @ApiOperation(value = "获取租户支持引擎类型")
    @PostMapping(value = "getSupportEngineType")
    public R<List<Integer>> getSupportEngineType(@RequestBody(required = false) BatchProjectBaseVO vo) {
        return new APITemplate<List<Integer>>() {
            @Override
            protected List<Integer> process() throws BizException {
                return projectService.getSupportEngineType(vo.getDtuicTenantId());
            }
        }.execute();
    }

    @ApiOperation(value = "获取租户Hadoop数据源的连接引擎名称（HiveServer，SparkThrift，Impala中的一种）")
    @PostMapping(value = "getHadoopMetaDataSourceName")
    public R<String> getHadoopMetaDataSourceName(@RequestBody(required = false) BatchProjectBaseVO vo) {
        return new APITemplate<String>() {
            @Override
            protected String process() throws BizException {
                return projectService.getHadoopMetaDataSourceName(vo.getDtuicTenantId());
            }
        }.execute();
    }

    @ApiOperation(value = "控制台-项目列表")
    @PostMapping(value = "queryProjects")
    public R<PageResult<List<BatchQueryProjectResultVO>>> queryProjects(@RequestBody BatchProjectQueryProjectsVO vo) {

        return new APITemplate<PageResult<List<BatchQueryProjectResultVO>>>() {
            @Override
            protected PageResult<List<BatchQueryProjectResultVO>> process() throws BizException {
                PageResult<List<ProjectVO>> listPageResult = projectService.queryProjects(vo.getUserId(), vo.getTenantId(), vo.getProjectName(), vo.getIsAdmin(), vo.getCurrentPage(), vo.getPageSize(), vo.getIsRoot());
                return ProjectMapstructTransfer.INSTANCE.projectVOToBatchQueryProjectResultVOPageResult(listPageResult);
            }
        }.execute();
    }


    @ApiOperation(value = "获取用户指定角色的项目列表")
    @PostMapping(value = "getProjectsByRoleValues")
    public R<List<BatchProjectResultVO>> getProjectsByRoleValues(@RequestBody BatchProjectUserVO vo) {

        return new APITemplate<List<BatchProjectResultVO>>() {
            @Override
            protected List<BatchProjectResultVO> process() throws BizException {
                List<Project> projectsByRoleValues = projectService.getProjectsByRoleValues(vo.getUserId(), vo.getRoleValues(), vo.getTenantId());
                return ProjectMapstructTransfer.INSTANCE.listProjectToBatchProjectResultListVO(projectsByRoleValues);
            }
        }.execute();
    }


    @ApiOperation(value = "获取除项目外的所有成员")
    @PostMapping(value = "getUsersNotInProject")
    @Security(code = AuthCode.PROJECT_MEMBER_QUERY)
    public R<List<BatchUserBaseResultVO>> getUsersNotInProject(@RequestBody BatchProjectGetUsersNotInProjectVO vo) {

        return new APITemplate<List<BatchUserBaseResultVO>>() {
            @Override
            protected List<BatchUserBaseResultVO> process() throws BizException {
                List<User> usersNotInProject = projectService.getUsersNotInProject(vo.getProjectId(), vo.getName());
                return ProjectMapstructTransfer.INSTANCE.userListToBatchUserBaseResultVOList(usersNotInProject);
            }
        }.execute();
    }


    @ApiOperation(value = "获得用户下的项目")
    @PostMapping(value = "getProjectUserIn")
    public R<List<BatchProjectResultVO>> getProjectUserIn(@RequestBody BatchProjectGetProjectUserInVO vo) {

        return new APITemplate<List<BatchProjectResultVO>>() {
            @Override
            protected List<BatchProjectResultVO> process() throws BizException {
                List<Project> projectUserIn = projectService.getProjectUserIn(vo.getUserId(), vo.getTenantId(), vo.getDefaultProjectId());
                return ProjectMapstructTransfer.INSTANCE.listProjectToBatchProjectResultListVO(projectUserIn);
            }
        }.execute();
    }

    @ApiOperation(value = "通过ID获取项目")
    @PostMapping(value = "getProjectById")
    public R<BatchProjectResultVO> getProjectById(@RequestBody BatchProjectBaseVO vo) {

        return new APITemplate<BatchProjectResultVO>() {
            @Override
            protected BatchProjectResultVO process() throws BizException {
                Project projectById = projectService.getProjectById(vo.getProjectId());
                return ProjectMapstructTransfer.INSTANCE.projectToBatchProjectResultVO(projectById);
            }
        }.execute();
    }

    @ApiOperation(value = "将项目设置为置顶")
    @PostMapping(value = "setSticky")
    public R<Integer> setSticky(@RequestBody BatchProjectSetStickyVO vo) {

        return new APITemplate<Integer>() {
            @Override
            protected Integer process() throws BizException {
                return projectService.setSticky(vo.getAppointProjectId(), vo.getStickStatus(), vo.getUserId(), vo.getTenantId());
            }
        }.execute();
    }

    @ApiOperation(value = "获取新增状态")
    @PostMapping(value = "getCreateStatus")
    public R<Map<Long, BatchProjectResultVO>> getCreateStatus(@RequestBody BatchProjectGetCreateStatusVO vo) {

        return new APITemplate<Map<Long, BatchProjectResultVO>>() {
            @Override
            protected Map<Long, BatchProjectResultVO> process() throws BizException {
                Map<Long, Project> createStatus = projectService.getCreateStatus(vo.getProjectIdList());
                return ProjectMapstructTransfer.INSTANCE.projectMapToBatchProjectResultVOMap(createStatus);
            }
        }.execute();
    }

    @ApiOperation(value = "获取项目任务责任人")
    @PostMapping(value = "getProjectTaskUsers")
    @Security(code = AuthCode.PROJECT_MEMBER_QUERY)
    public R<PageResult<List<BatchUserRoleResultVO>>> getProjectTaskUsers(@RequestBody BatchProjectGetProjectTaskUsersVO vo) {
        return new APITemplate<PageResult<List<BatchUserRoleResultVO>>>() {
            @Override
            protected PageResult<List<BatchUserRoleResultVO>> process() throws BizException {
                PageResult<List<UserRoleVO>> projectTaskUsers = projectService.getProjectTaskUsers(vo.getProjectId(),
                        vo.getTenantId(), vo.getUserId(), vo.getName(), vo.getCurrentPage(), vo.getPageSize());
                return ProjectMapstructTransfer.INSTANCE.getProjectTaskUsersPageResultToBatchUserRoleResultVOPageResult(projectTaskUsers);
            }
        }.execute();
    }


    @ApiOperation(value = "根据名称获取项目信息")
    @PostMapping(value = "getByName")
    public R<BatchProjectResultVO> getByName(@RequestBody BatchProjectGetByNameVO vo) {

        return new APITemplate<BatchProjectResultVO>() {
            @Override
            protected BatchProjectResultVO process() throws BizException {
                Project project = projectService.getByName(vo.getProjectName(), vo.getTenantId());
                return ProjectMapstructTransfer.INSTANCE.projectToBatchProjectResultVO(project);
            }
        }.execute();
    }

    @ApiOperation(value = "获取所有项目")
    @PostMapping(value = "getProjectsByUserAndTenant")
    public R<List<BatchProjectResultVO>> getAllProjects(@RequestBody BatchProjectByUserAndTenantVO vo) {
        return new APITemplate<List<BatchProjectResultVO>>() {
            @Override
            protected List<BatchProjectResultVO> process() throws BizException {
                List<Project> allProjects = projectService.getProjectsByUserAndTenant(vo.getDtuicTenantId(), vo.getTotal(), vo.getDtuicUserId(), vo.getIsRoot());
                return ProjectMapstructTransfer.INSTANCE.listProjectToBatchProjectResultListVO(allProjects);
            }
        }.execute();
    }

}
