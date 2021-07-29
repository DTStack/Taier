package com.dtstack.batch.dao;

import com.dtstack.batch.domain.RoleUser;
import com.dtstack.batch.web.pager.PageQuery;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author sishu.yss
 */
public interface RoleUserDao {
    List<RoleUser> listByUserIdAndTenantIdWithOutNoProject(@Param("userId") Long userId, @Param("tenantId") Long tenantId);

    List<RoleUser> listRoleUserIsAdminByUserId(@Param("userId") long userId, @Param("tenantId") Long tenantId);

    List<RoleUser> listByUserIdProjectId(@Param("userId") Long userId, @Param("projectId") Long projectId);

    /**
     * 根据用户id projectId tenantId 查询对应的角色信息
     * @param userId
     * @param projectId
     * @param tenantId
     * @return
     */
    List<RoleUser> listByUserIdProjectIdTenantId(@Param("userId") Long userId, @Param("projectId") Long projectId, @Param("tenantId")Long tenantId);

    List<RoleUser> listByProjectId(@Param("projectId") Long projectId);

    List<RoleUser> listRoleUserIsAdminByUserIdProjectId(@Param("userId") long userId, @Param("projectId") Long projectId);

    List<RoleUser> listRoleUserIsAdminByProjectId( @Param("projectId") Long projectId);

    Integer deleteByUserIdAndProjectId(@Param("userId") long userId, @Param("modifyUserId") Long modifyUserId, @Param("projectId") Long projectId);

    /**
     * 删除 固定用户 固定项目 固定租户下的 用户角色信息
     * @param userId
     * @param tenantId
     * @param projectId
     * @return
     */
    Integer deleteByUserIdAndProjectIdAndTenantId(@Param("userId") Long userId, @Param("tenantId") Long tenantId, @Param("projectId") Long projectId);

    /**
     * 删除 固定用户 固定租户 固定 角色集合的记录
     * @param userId
     * @param tenantId
     * @param roleIds
     * @return
     */
    Integer deleteByUserIdAndTenantIdAndRoleIds(@Param("userId") Long userId, @Param("tenantId") Long tenantId, @Param("roleIds") List<Long> roleIds);

    List<RoleUser> listByUserIdAndTenantId(@Param("userId") long userId, @Param("tenantId") long tenantId);

    RoleUser getByRoleIdUserIdProjectIdWithoutNoProject(@Param("roleId") Long roleId, @Param("userId") Long userId, @Param("projectId") Long projectId);

    /**
     * 移除项目下该用户对应的角色
     *
     * @param existRoleIds 角色ID
     * @param userId       用户ID
     * @param projectId    项目ID
     * @return
     */
    Integer deleteByRoleIdsAndUserIdAndProjectId(@Param("roleIds") List<Long> existRoleIds, @Param("userId") Long userId, @Param("projectId") Long projectId, @Param("modifyUserId") Long modifyUserId);

    Integer deleteByRoleIdAndProjectId(@Param("roleId") Long roleId, @Param("projectId") Long projectId, @Param("modifyUserId") Long modifyUserId);

    RoleUser getNoProjectByUserId(@Param("userId") Long userId, @Param("tenantId") Long tenantId);

    List<RoleUser> listByTenantIdAndProjectIdAndUserName(@Param("tenantId") Long tenantId, @Param("projectId") Long projectId);

    List<RoleUser> listByRoleId(@Param("roleId") Long roleId);

    Integer insert(RoleUser roleUser);

    /**
     * 批量添加用户角色信息
     *
     * @param roleUser
     * @return
     */
    Integer batchInsert(@Param("roleUserList") List<RoleUser> roleUser);

    RoleUser getOne(long id);

    List<RoleUser> listByTenantId(@Param("tenantId") long tenantId);

    List<RoleUser> listByTenantIdWithOutNoProject(@Param("tenantId") Long tenantId);

    RoleUser getProjectOwnerByProjectId(@Param("projectId") Long projectId);

    List<RoleUser> listByUserIdANdRoleValues(@Param("userId") Long userId, @Param("roleValues") List<Integer> roleValues, @Param("tenantId") Long tenantId);

    List<Integer> listRoleValueByUserIdAndProjectId(@Param("userId") Long userId, @Param("tenantId") Long tenantId, @Param("projectId") Long projectId);

    Integer deleteTenantAdminRoles(@Param("tenantId") Long tenantId, @Param("modifyUserId") Long modifyUserId);

    Integer isDelByUserIdAndProjectId(@Param("userId") Long userId, @Param("projectId") Long projectId);

    RoleUser getLastTimeByUserIdAndProjectId(@Param("userId") Long userId, @Param("projectId") Long projectId);

    List<RoleUser> getReplyUserList(@Param("userId") Long userId, @Param("projectId") Long projectId, @Param("gmtModifiedStart") Timestamp gmtModifiedStart, @Param("gmtModifiedEnd") Timestamp gmtModifiedEnd);

    Integer replyDelUser(@Param("userId") Long userId, @Param("projectId") Long projectId, @Param("gmtModifiedStart") Timestamp gmtModifiedStart, @Param("gmtModifiedEnd") Timestamp gmtModifiedEnd);

    /**
     * 获取该用户某一roleId下的角色
     * @param roleIds
     * @param userId
     * @return
     */
    List<RoleUser> listByRoleIdsAndUserId(@Param("roleIds") List<Long> roleIds, @Param("userId") Long userId);

    Integer deleteByProjectId(@Param("projectId") Long projectId, @Param("modifyUserId") Long modifyUserId);

    /**
     * 根据特定租户下 用户 id 和角色 id 分页查询
     *
     * @param tenantId
     * @param roleIds
     * @param userIds
     * @param pageQuery
     * @return
     */
    List<Long> listUserIdByRoleAndUsers(@Param("tenantId") Long tenantId, @Param("projectId") Long projectId, @Param("roleIds") List<Long> roleIds, @Param("userIds") List<Long> userIds, @Param("pageQuery") PageQuery pageQuery);

    /**
     * 根据特定租户下 用户 id 和角色 id 统计数量
     *
     * @param tenantId
     * @param projectId
     * @param roleIds
     * @param userIds
     * @return
     */
    Integer countByRoleAndUsers(@Param("tenantId") Long tenantId, @Param("projectId") Long projectId, @Param("roleIds") List<Long> roleIds, @Param("userIds") List<Long> userIds);

    List<RoleUser> listRoleByUserIdsAndProjectIds(@Param("userIds") List<Long> userIds, @Param("projectId") Long projectId);

    /**
     * 获取项目下的用户
     *
     * @param projectId
     * @return
     */
    List<Long> listUsersByProjectId(@Param("projectId") Long projectId);

}
