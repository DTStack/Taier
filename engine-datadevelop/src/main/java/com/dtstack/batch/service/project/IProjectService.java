package com.dtstack.batch.service.project;

import com.dtstack.batch.vo.ProjectEngineVO;

import java.util.List;

/**
 * 项目相关接口
 * Date: 2019/4/25
 * Company: www.dtstack.com
 * @author xuchao
 */
public interface IProjectService {

    Integer NORMAL_TABLE = 0;

    /**
     *TODO 是否应该返回更明确的创建信息和失败信息
     * 创建项目
     * @param projectId
     * @param projectName
     * @param projectDesc
     * @param userId
     * @param tenantId
     * @param dtuicTenantId
     * @param projectEngineVO
     * @return
     * @throws Exception
     */
    int createProject(Long projectId, String projectName, String projectDesc, Long userId, Long tenantId,
                      Long dtuicTenantId, ProjectEngineVO projectEngineVO) throws Exception;

    /**
     * 获取已经存在的database
     * @return
     */
    List<String> getRetainDB(Long dtuicTenantId,Long userId) throws Exception;

    List<String> getDBTableList(Long dtuicTenantId, Long userId, String dbName, Long projectId) throws Exception;
}
