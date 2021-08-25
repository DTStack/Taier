package com.dtstack.batch.mapstruct.vo;

import com.dtstack.batch.domain.Project;
import com.dtstack.engine.api.domain.User;
import com.dtstack.engine.api.vo.tenant.TenantUsersVO;
import com.dtstack.batch.vo.*;
import com.dtstack.batch.web.pager.PageResult;
import com.dtstack.batch.web.project.vo.query.BatchProjectAddNewEngineVO;
import com.dtstack.batch.web.project.vo.query.BatchProjectCreateVO;
import com.dtstack.batch.web.project.vo.result.*;
import com.dtstack.batch.web.role.vo.result.BatchUserRoleResultVO;
import com.dtstack.batch.web.table.vo.result.BatchTableTypeResultVO;
import com.dtstack.batch.web.user.vo.result.BatchUserBaseResultVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 19:55 2020/12/28
 * @Description：项目类型转换
 */
@Mapper
public interface ProjectMapstructTransfer {
    ProjectMapstructTransfer INSTANCE = Mappers.getMapper(ProjectMapstructTransfer.class);

    /**
     * ProjectAddNewEngineVO -> ProjectEngineVO
     *
     * @param engineVO
     * @return
     */
    ProjectEngineVO newEngineVoToEngineVo(BatchProjectAddNewEngineVO engineVO);

    /**
     * ProjectCreateVO -> ProjectVO
     *
     * @param addVO
     * @return
     */
    ProjectVO newCreateVoToCreateVo(BatchProjectCreateVO addVO);

    /**
     * Project -> BatchProjectResultVO
     *
     * @param project
     * @return
     */
    BatchProjectResultVO projectToBatchProjectResultVO(Project project);

    /**
     * List<Project> -> List<BatchProjectResultVO>
     *
     * @param project
     * @return
     */
    List<BatchProjectResultVO> listProjectToBatchProjectResultListVO(List<Project> project);

    /**
     * List<User> -> List<BatchUserBaseResultVO>
     *
     * @param userList
     * @return
     */
    List<BatchUserBaseResultVO> userListToBatchUserBaseResultVOList(List<User> userList);

    /**
     * PageResult<List<ProjectVO>> -> PageResult<List<BatchQueryProjectResultVO>>
     *
     * @param listPageResult
     * @return
     */
    PageResult<List<BatchQueryProjectResultVO>> projectVOToBatchQueryProjectResultVOPageResult(PageResult<List<ProjectVO>> listPageResult);

    /**
     * ProjectVO -> BatchQueryProjectResultVO
     *
     * @param projectVO
     * @return
     */
    BatchQueryProjectResultVO projectVOToBatchQueryProjectResultVO(ProjectVO projectVO);


    /**
     * List<TenantProjectVO> -> List<BatchTenantProjectResultVO>
     *
     * @param tenantProjectVOList
     * @return
     */
    List<BatchTenantProjectResultVO> tenantProjectVOListToBatchTenantProjectResultVOList(List<TenantProjectVO> tenantProjectVOList);

    /**
     * HomePageVo -> BatchHomePageResultVo
     *
     * @param homePages
     * @return
     */
    BatchHomePageResultVO newEngineVoToBatchHomePageResultVO(HomePageVo homePages);

    /**
     * ProjectDelCheckResultVo -> BatchProjectDelCheckResultVO
     *
     * @param projectDelCheckResultVo
     * @return
     */
    BatchProjectDelCheckResultVO projectDelCheckResultVoToBatchProjectDelCheckResultVO(ProjectDelCheckResultVo projectDelCheckResultVo);

    /**
     * PageResult<List<UserRoleVO>> -> PageResult<List<BatchUserRoleResultVO>>
     *
     * @param pageResult
     * @return
     */
    PageResult<List<BatchUserRoleResultVO>> userRoleVOPageResultToBatchUserRoleResultVOPageResult(PageResult<List<UserRoleVO>> pageResult);

    /**
     * List<BatchTableTypeVo> -> List<BatchTableTypeResultVO>
     *
     * @param batchTableTypeVoList
     * @return
     */
    List<BatchTableTypeResultVO> batchTableTypeVoListToBatchTableTypeResultVOList(List<BatchTableTypeVo> batchTableTypeVoList);

    /**
     * PageResult<List<BatchGetProjectListResultVO>> -> PageResult<List<ProjectOverviewVO>>
     *
     * @param projectDTOPageResult
     * @return
     */
    PageResult<List<BatchGetProjectListResultVO>> projectDTOPageResultToBatchGetProjectListResultVOPageResult(PageResult<List<ProjectOverviewVO>> projectDTOPageResult);

    /**
     * Map<Long, Project>  -> Map<Long, BatchProjectResultVO>
     *
     * @param projectMap
     * @return
     */
    Map<Long, BatchProjectResultVO> projectMapToBatchProjectResultVOMap(Map<Long, Project> projectMap);

    /**
     * PageResult<List<UserRoleVO>> -> PageResult<List<BatchUserRoleResultVO>>
     * @param listPageResult
     * @return
     */
    PageResult<List<BatchUserRoleResultVO>> getProjectTaskUsersPageResultToBatchUserRoleResultVOPageResult(PageResult<List<UserRoleVO>> listPageResult);

    /**
     * TenantUsersVO -> BatchProjectGetUicNotInProjectResultVO
     * @param tenantUsersVO
     * @return
     */
    @Mappings({
            @Mapping(source = "id", target = "userId"),
            @Mapping(source = "root", target = "appRoot"),
            @Mapping(source = "admin", target = "tenantAdmin"),
            @Mapping(source = "username", target = "userName")
    })
    BatchProjectGetUicNotInProjectResultVO tenantUsersVOToBatchProjectGetUicNotInProjectResultVO(TenantUsersVO tenantUsersVO);

    /**
     * List<TenantUsersVO> -> List<BatchProjectGetUicNotInProjectResultVO>
     * @param tenantUsersVOList
     * @return
     */
    List<BatchProjectGetUicNotInProjectResultVO> tenantUsersVOListToResultVO(List<TenantUsersVO> tenantUsersVOList);

}
