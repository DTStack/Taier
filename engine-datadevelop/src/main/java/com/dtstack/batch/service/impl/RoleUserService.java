package com.dtstack.batch.service.impl;

import com.dtstack.batch.common.exception.ErrorCode;
import com.dtstack.batch.common.exception.RdosDefineException;
import com.dtstack.batch.dao.RoleDao;
import com.dtstack.batch.dao.RoleUserDao;
import com.dtstack.batch.dao.po.TaskOwnerAndProjectPO;
import com.dtstack.batch.domain.Role;
import com.dtstack.batch.domain.RoleUser;
import com.dtstack.engine.api.domain.Tenant;
import com.dtstack.engine.api.domain.User;
import com.dtstack.batch.service.auth.IAuthService;
import com.dtstack.batch.service.task.impl.BatchTaskService;
import com.dtstack.batch.service.uic.impl.UicUserApiClient;
import com.dtstack.batch.service.uic.impl.domain.UICUserVO;
import com.dtstack.batch.vo.UserRolePermissionVO;
import com.dtstack.batch.vo.UserRoleVO;
import com.dtstack.batch.web.pager.PageQuery;
import com.dtstack.batch.web.pager.PageResult;
import com.dtstack.batch.web.user.vo.query.BatchRoleUserAddNewMapVO;
import com.dtstack.dtcenter.common.annotation.SecurityAudit;
import com.dtstack.dtcenter.common.console.SecurityResult;
import com.dtstack.dtcenter.common.enums.ActionType;
import com.dtstack.dtcenter.common.enums.EntityStatus;
import com.dtstack.dtcenter.common.enums.RoleValue;
import com.dtstack.engine.master.impl.TenantService;
import com.dtstack.engine.master.impl.UserService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;


/**
 * @author sishu.yss
 */

@Service
public class RoleUserService {

    private static final Logger logger = LoggerFactory.getLogger(RoleUserService.class);

    @Autowired
    private RoleUserDao roleUserDao;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserService userService;

    @Autowired
    private IAuthService authService;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private BatchTaskService batchTaskService;

    @Autowired
    private UicUserApiClient uicUserApiClient;

    private static final String CANT_REMOVE_PROJECT_USER_MSG = "无法移除用户: 此用户下任务: %s个,表: %s个, 告警接收: %s个";

    private static List<String> PERMISSION_CODE_NEED_REMOVE_IN_PRODUCE_ENV = Arrays.asList(
            "datadevelop_batch_taskmanager_edit",
            "datadevelop_batch_taskmanager_publish",
            "batchintegration_batch_edit",
            "batchintegration_batch_query",
            "datamodel_manager_edit",
            "datamanager_tablemanager_edit",
            "datamanager_tablemanager_editcharge",
            "datamanager_catalogue_edit",
            "datamanager_permissionmanager_edit",
            "datadevelop_batch_functionmanager",
            "datadevelop_batch_resourcemanager",
            "datadevelop_batch_scriptmanager_edit"
    );

    private static List<String> MAINTENANCE_PERMISSION_CODE = Arrays.asList(
            "maintenance_batchtaskmanager_filldata",
            "maintenance_batch_taskop",
            "maintenance_batch_scheduleop",
            "maintenance_alarm_custom_batch_edit"
    );

    /**
     * 添加用户默认角色
     *
     * @param roleIdList   角色列表
     * @param targetUserId 用户
     * @param tenantId     租户ID
     * @param projectId    项目ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void addDefaultRoleUserList(List<Long> roleIdList, Long targetUserId, Long tenantId, Long projectId) {
        List<RoleUser> roleUserList = new ArrayList<>();
        for (Long roleId : roleIdList) {
            RoleUser dbRoleUser = new RoleUser();
            dbRoleUser.setRoleId(roleId);
            dbRoleUser.setUserId(targetUserId);
            dbRoleUser.setProjectId(projectId);
            dbRoleUser.setTenantId(tenantId);
            dbRoleUser.setCreateUserId(targetUserId);
            roleUserList.add(dbRoleUser);
        }
        if (CollectionUtils.isNotEmpty(roleUserList)) {
            roleUserDao.batchInsert(roleUserList);
            authService.clearCache(targetUserId, projectId, tenantId);
        }
    }

    /**
     * 添加用户信息
     *
     * @param roleIds
     * @param userId
     * @param tenantId
     * @param projectId
     * @param targetUsers
     * @param isRoot
     * @return
     */
    @SecurityAudit(actionType = ActionType.ADD_USER,orderedKeys= {"newUser"})
    @Transactional(rollbackFor = Exception.class)
    public SecurityResult<Map<Long, List<RoleUser>>> addRoleUserNew(List<Long> roleIds, Long userId, Long tenantId, Long projectId, List<BatchRoleUserAddNewMapVO> targetUsers, Boolean isRoot) {
        if (CollectionUtils.isEmpty(roleIds)) {
            throw new RdosDefineException(ErrorCode.ROLE_IDS_CANNOT_BE_EMPTY);
        }
        if (CollectionUtils.isEmpty(targetUsers)) {
            throw new RdosDefineException("添加用户不能为空");
        }

        //校验当前操作用户与被分配的角色id
        isRoleInfoModifyAvailable(userId, projectId, roleIds, isRoot);

        List<Long> toAddRoleList = Lists.newArrayList();
        List<BatchRoleUserAddNewMapVO> toAddUserAndRole = Lists.newArrayList();
        List<String> newUsers = new ArrayList<>();

        List<Long> dtUicUserIds = targetUsers.stream().map(BatchRoleUserAddNewMapVO::getUserId).collect(Collectors.toList());
        List<User> userList =  userService.getUserInUicUserIds(dtUicUserIds);
        Map<Long, User> userMap = userList.stream().collect(Collectors.toMap(User::getDtuicUserId, Function.identity(), (key1, key2) -> key2));

        // 区分已经添加过的用户 和 未添加过的用户
        for (BatchRoleUserAddNewMapVO uicUserVO : targetUsers) {
            // 如果没有添加过该用户，则需要先添加该用户信息
            if (!userMap.containsKey(uicUserVO.getUserId())) {
                toAddUserAndRole.add(uicUserVO);
            } else {
                User user = userMap.get(uicUserVO.getUserId());
                toAddRoleList.add(user.getId());
                newUsers.add(user.getUserName());
            }
        }

        // 保存用户信息
        if (CollectionUtils.isNotEmpty(toAddUserAndRole)) {
            List<Long> addUicUserIdList = addUicUser(toAddUserAndRole);
            toAddRoleList.addAll(addUicUserIdList);
        }

        // 添加用户角色信息
        Map<Long, List<RoleUser>> map = addUserRolesById(toAddRoleList, roleIds, projectId, tenantId, userId);

        SecurityResult<Map<Long, List<RoleUser>>> securityResult = new SecurityResult<>();
        securityResult.setResult(map);
        String newUserStr = StringUtils.join(newUsers, ",");
        securityResult.addSecurityData("newUser", newUserStr);
        securityResult.setOperatorId(userId);
        //FIXME sessionUtil中添加,使用用户名考虑从缓存中获取
        securityResult.setOperator(userService.getUserName(userId));
        securityResult.setTenantId(tenantId);
        return securityResult;
    }


    /**
     * 添加新的用户信息
     *
     * @param addNewMapVOList
     * @return
     */
    public List<Long> addUicUser(List<BatchRoleUserAddNewMapVO> addNewMapVOList){
        // 添加离线不存在的uic用户信息
        List<Long> uicUsers = new ArrayList<>();
        addNewMapVOList.forEach(uicUserVo ->{
            User user = new User();
            user.setUserName(uicUserVo.getUserName());
            //取账号为邮箱
            user.setEmail(user.getUserName());
            user.setPhoneNumber(user.getPhoneNumber());
            user.setDtuicUserId(uicUserVo.getUserId());
            //用户状态0：正常，1：禁用
            user.setStatus(BooleanUtils.isTrue(uicUserVo.getActive()) ? EntityStatus.normal.getStatus() : EntityStatus.disable.getStatus());
            uicUsers.add(userService.addUser(user));
        });
        return uicUsers;
    }

    /**
     *
     * sdk调用
     * @param roleValues
     * @param userId
     * @param tenantId
     * @param projectId
     * @param targetUsers
     * @param isRoot
     * @return
     */
    @SecurityAudit(actionType = ActionType.ADD_USER,orderedKeys= {"newUser"})
    public SecurityResult<Map<Long, List<RoleUser>>> addRoleUserNewFromSdk(List<Integer> roleValues, long userId, long tenantId, long projectId, List<BatchRoleUserAddNewMapVO> targetUsers, Boolean isRoot) {
        List<Role> roles = roleDao.getByProjectIdAndRoleValues(projectId, roleValues);
        if (CollectionUtils.isEmpty(roles)) {
            SecurityResult<Map<Long, List<RoleUser>>> result = new SecurityResult<>();
            result.setResult(Maps.newHashMap());
            result.setIgnoreLog(true);
            return result;
        }
        List<Long> roleIds = roles.stream().map(Role::getId).distinct().collect(Collectors.toList());
        return addRoleUserNew(roleIds, userId, tenantId, projectId, targetUsers, isRoot);
    }

    /**
     * 添加用户角色信息
     *
     * @param targetUserIds 用户列表
     * @param roleIds       角色列表
     * @param projectId     项目ID
     * @param tenantId      租户ID
     * @return
     */
    private Map<Long, List<RoleUser>> addUserRolesById(List<Long> targetUserIds, List<Long> roleIds, Long projectId, Long tenantId, Long operatorUserId){
        Tenant tenant = tenantService.getTenantById(tenantId);
        if (Objects.isNull(tenant)) {
            throw new RdosDefineException(String.format("获取租户信息失败， tenantId: %s", tenantId));
        }
        Map<Long, List<RoleUser>> mapRoleUser = new HashMap<>(targetUserIds.size());
        Map<Long, User> userMap = buildUserMap(targetUserIds);
        Map<Integer, Role> roleMap = buildRoleMap(projectId);
        for (Long targetUserId : targetUserIds) {
            User user = (User) MapUtils.getObject(userMap, targetUserId);
            if (Objects.isNull(user)) {
                throw new RdosDefineException(String.format("获取用户信息失败， userId: %s", targetUserId));
            }
            List<Long> userRoleIds = new ArrayList<>(roleIds);
            // 添加该用户的一些uic角色
            userRoleIds.addAll(addUserDefaultRole(user.getDtuicUserId(), tenant.getDtuicTenantId(), roleMap));
            List<RoleUser> listRoleUser = new ArrayList<RoleUser>();
            for (Long roleId : userRoleIds) {
                //判断当前角色+用户是不是已经存在
                RoleUser dbTarget = roleUserDao.getByRoleIdUserIdProjectIdWithoutNoProject(roleId, targetUserId, projectId);
                // 如果角色已经存在，那么直接跳过
                if (Objects.nonNull(dbTarget)) {
                    continue;
                }
                RoleUser dbRoleUser = new RoleUser();
                dbRoleUser.setRoleId(roleId);
                dbRoleUser.setUserId(targetUserId);
                dbRoleUser.setProjectId(projectId);
                dbRoleUser.setTenantId(tenantId);
                dbRoleUser.setCreateUserId(operatorUserId);
                roleUserDao.insert(dbRoleUser);
                dbRoleUser = roleUserDao.getOne(dbRoleUser.getId());
                listRoleUser.add(dbRoleUser);
            }
            mapRoleUser.put(targetUserId, listRoleUser);

            //清二级缓存
            authService.clearCache(targetUserId, projectId, tenantId);
        }
        return mapRoleUser;
    }

    /**
     * 构建本项目下用户对应关系
     *
     * @param userIds
     * @return key: userId value:用户信息
     */
    private Map<Long, User> buildUserMap(List<Long> userIds){
        if(CollectionUtils.isEmpty(userIds)){
            return Maps.newHashMap();
        }
        List<User> userList = userService.listByIds(userIds);
        Map<Long, User> userMap = userList.stream().collect(Collectors.toMap(User::getId, Function.identity(), (key1, key2) -> key2));
        return userMap;
    }

    /**
     * 构建本项目下角色对应关系
     *
     * @param projectId
     * @return
     */
    private Map<Integer, Role> buildRoleMap(Long projectId){
        // 得到角色映射关系
        List<Integer> roleValueList =  Lists.newArrayList(
                RoleValue.APPADMIN.getRoleValue(), RoleValue.TEANTOWNER.getRoleValue(),
                RoleValue.TEANTADMIN.getRoleValue());
        List<Role> roleList = roleService.getByProjectIdAndRoleValues(projectId, roleValueList);
        Map<Integer, Role> roleValueRoleMap = roleList.stream().collect(Collectors.toMap(Role::getRoleValue, Function.identity(), (key1, key2) -> key2));
        return roleValueRoleMap;
    }

    /**
     * 给这个用户添加uic那边的默认角色（超级管理员、租户所有者、租户管理员）
     *
     * @param uicUserId
     * @param uicTenantId
     * @param roleMap
     * @return
     */
    private List<Long> addUserDefaultRole(Long uicUserId, Long uicTenantId, Map<Integer, Role> roleMap){
        List<Long> roleList = new ArrayList<>();
        UICUserVO uicUserVO = uicUserApiClient.getByTenantId(uicUserId, uicTenantId);
        // 判断是否是超级管理员
        if (BooleanUtils.isTrue(uicUserVO.isRoot())) {
            Role role = (Role) MapUtils.getObject(roleMap, RoleValue.APPADMIN.getRoleValue());
            if (Objects.nonNull(role)) {
                roleList.add(role.getId());
            }
        }
        // 判断是否是租户所有者
        if (BooleanUtils.isTrue(uicUserVO.isTenantOwner())) {
            Role role = (Role) MapUtils.getObject(roleMap, RoleValue.TEANTOWNER.getRoleValue());
            if (Objects.nonNull(role)) {
                roleList.add(role.getId());
            }
        }
        // 判断是否是租户管理员
        if (BooleanUtils.isTrue(uicUserVO.isTenantAdmin())) {
            Role role = (Role) MapUtils.getObject(roleMap, RoleValue.TEANTADMIN.getRoleValue());
            if (Objects.nonNull(role)) {
                roleList.add(role.getId());
            }
        }
        return roleList;
    }


    /**
     * 修改成员角色
     */
    @Transactional(rollbackFor = Exception.class)
    @SecurityAudit(actionType = ActionType.CHANGE_MODE,orderedKeys = {"user","rolesBefore","rolesAfter"})
    public SecurityResult<List<RoleUser>> updateUserRole(Long userId, Long targetUserId, List<Long> roleIds, Long tenantId, Long projectId, Boolean isRoot) {
        // 校验参数
        if (Objects.nonNull(userId) && userId.equals(targetUserId)) {
            throw new RdosDefineException("用户不可修改自身角色", ErrorCode.PERMISSION_LIMIT);
        }

        User targetUser = userService.getById(targetUserId);
        if (targetUser == null) {
            throw new RdosDefineException(ErrorCode.USER_NOT_FIND);
        }
        this.isRoleInfoModifyAvailable(userId, projectId, roleIds, isRoot);

        Tenant tenant = tenantService.getTenantById(tenantId);
        if (Objects.isNull(tenant)) {
            throw new RdosDefineException(String.format("获取租户信息失败， tenantId: %s", tenantId));
        }
        // 添加一些该用户的角色
        Map<Integer, Role> roleMap =  buildRoleMap(projectId);
        roleIds.addAll(addUserDefaultRole(targetUser.getDtuicUserId(), tenant.getDtuicTenantId(), roleMap));

        /**
         * 修改用户角色：增量更新
         */
        List<RoleUser> roleUsers = roleUserDao.listByUserIdProjectId(targetUserId, projectId);
        //修改前的角色
        List<String> rolesBefore = new ArrayList<>();
        //修改后的角色
        List<String> rolesAfter = new ArrayList<>();
        List<Long> existRoleIds = roleUsers.stream().map(RoleUser::getRoleId).collect(Collectors.toList());
        for (Long roleId : roleIds) {
            rolesAfter.add(roleService.getRoleById(roleId).getRoleName());
            if (existRoleIds.contains(roleId)) {
                rolesBefore.add(roleService.getRoleById(roleId).getRoleName());
                existRoleIds.remove(roleId);
            } else {
                RoleUser dbRoleUser = new RoleUser();
                dbRoleUser.setRoleId(roleId);
                dbRoleUser.setUserId(targetUserId);
                dbRoleUser.setProjectId(projectId);
                dbRoleUser.setTenantId(tenantId);
                dbRoleUser.setCreateUserId(userId);
                roleUserDao.insert(dbRoleUser);
            }
        }
        if (CollectionUtils.isNotEmpty(existRoleIds)) {
           roleUserDao.deleteByRoleIdsAndUserIdAndProjectId(existRoleIds, targetUserId, projectId, userId);
        }

        //清二级缓存
        authService.clearCache(targetUserId, projectId, tenantId);
        List<RoleUser> roleUsersResult = roleUserDao.listByUserIdProjectId(targetUserId, projectId);
        SecurityResult<List<RoleUser>> result = new SecurityResult<>();
        result.setResult(roleUsersResult);
        result.setTenantId(tenantId);
        result.setOperatorId(userId);
        result.setOperator(userService.getUserName(userId));
        result.addSecurityData("user",targetUser.getUserName())
                .addSecurityData("rolesBefore", StringUtils.join(rolesBefore,","))
                .addSecurityData("rolesAfter",StringUtils.join(rolesAfter,","));
        return result;
    }

    /**
     * 处理项目增加角色（uic回调事件）
     *
     * @param projectIdList
     * @param roleValueList
     * @param userId
     * @param tenantId
     */
    private void processProjectAddRoleByEvent(Set<Long> projectIdList, List<Integer> roleValueList, Long userId, Long tenantId){
        List<RoleUser> roleUserList ;
        Map<Long, RoleUser> roleUserMap ;
        List<RoleUser> addRoleUserList = new ArrayList<>();
        for(Long projectId : projectIdList){
            roleUserList = roleUserDao.listByUserIdProjectId(projectId, userId);
            roleUserMap = roleUserList.stream().collect(Collectors.toMap(RoleUser::getRoleId, Function.identity(), (key1, key2) -> key2));
            List<Role> projectRoleList = roleService.getByProjectIdAndRoleValues(projectId, roleValueList);
            for(Role role : projectRoleList){
                // 判断角色是否已经存在，如果存在则跳过，不存在则新增
                if(!roleUserMap.containsKey(role.getId())){
                    RoleUser roleUser = new RoleUser();
                    roleUser.setRoleId(role.getId());
                    roleUser.setUserId(userId);
                    roleUser.setProjectId(projectId);
                    roleUser.setTenantId(tenantId);
                    addRoleUserList.add(roleUser);
                }
            }
            if (CollectionUtils.isNotEmpty(addRoleUserList)) {
                roleUserDao.batchInsert(addRoleUserList);
                authService.clearCache(userId, projectId, tenantId);
            }
        }
    }

    /**
     * 处理项目移除角色（uic回调事件）
     *
     * @param projectIdList 项目 ID 列表
     * @param roleValueList 角色值列表
     * @param userId        用户 ID
     */
    private void processProjectRemoveRoleByEvent(Set<Long> projectIdList, List<Integer> roleValueList, Long userId, Long tenantId){
        // 如果是移除角色，则直接移除，不进行判断
        // 因为如果一个用户在项目中至少会有一个访客的角色，不用考虑移除角色导致用户在该项目角色为空问题，直接暴力移除
        for (Long projectId : projectIdList) {
            List<Role> projectRoleList = roleService.getByProjectIdAndRoleValues(projectId, roleValueList);
            if (CollectionUtils.isEmpty(projectIdList)) {
                continue;
            }
            List<Long> roleIdList = projectRoleList.stream().map(Role::getId).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(roleIdList)) {
                roleUserDao.deleteByRoleIdsAndUserIdAndProjectId(roleIdList, userId, projectId, userId);
                authService.clearCache(userId, projectId, tenantId);
            }
        }
    }


    /**
     * sdk调用
     * @param userId
     * @param targetUicUserId
     * @param roleValues
     * @param tenantId
     * @param projectId
     * @param isRoot
     * @return
     */
    @SecurityAudit(actionType = ActionType.CHANGE_MODE,orderedKeys = {"user","rolesBefore","rolesAfter"})
    public SecurityResult<List<RoleUser>> updateUserRoleFromSdk(long userId, long targetUicUserId, List<Integer> roleValues, long tenantId, long projectId, Boolean isRoot) {
        List<Role> roles = roleDao.getByProjectIdAndRoleValues(projectId, roleValues);
        if (CollectionUtils.isEmpty(roles)) {
            SecurityResult<List<RoleUser>> result = new SecurityResult<>();
            result.setIgnoreLog(true);
            result.setResult(Lists.newArrayList());
            return result;
        }
        User user = userService.getByDtUicUserId(targetUicUserId);
        List<Long> roleIds = roles.stream().map(Role::getId).distinct().collect(Collectors.toList());
        return updateUserRole(userId, user.getId(), roleIds, tenantId, projectId, isRoot);
    }

    /**
     * 获取租户内所有成员角色信息
     *
     * @param userId
     * @param tenantId
     * @return
     */
    public List<RoleUser> getRoleUserByUserId(Long userId, Long tenantId) {
        return roleUserDao.listByUserIdAndTenantIdWithOutNoProject(userId, tenantId);
    }

    @SecurityAudit(actionType = ActionType.REMOVE_USER,orderedKeys = "removedUser")
    public SecurityResult<Integer> removeRoleUserFromProject(long userId, long targetUserId, long projectId, Long tenantId, Boolean isRoot) {
        checkRemovePermission(isRoot, targetUserId, userId, projectId);
        Integer delete = roleUserDao.deleteByUserIdAndProjectId(targetUserId, userId, projectId);
        if (delete > 0) {
            //清二级缓存
            authService.clearCache(targetUserId, projectId, tenantId);
        }
        SecurityResult<Integer> result = new SecurityResult<>();
        result.setTenantId(tenantId);
        result.setOperatorId(userId);
        result.setOperator(userService.getUserName(userId));
        result.setResult(delete);
        result.addSecurityData("removedUser", userService.getUserName(targetUserId));
        return result;
    }

    /**
     * 模糊查询出用户以及在该项目下的权限
     *
     * @param projectId
     * @param name
     * @param oldOwnerUserId
     * @return
     */
    public List<UserRolePermissionVO> getUsersAndPermission(Long projectId, String name, Long oldOwnerUserId) {

        // 查询出该项目下所有的用户
        List<Long> userIds = listUsersByProjectId(projectId);
        if (CollectionUtils.isEmpty(userIds)){
            return Collections.emptyList();
        }
        // 模糊查询出符合条件的用户(限制50条)
        List<User> users = userService.getUsersByUserNameAndUserIds(userIds, name);

        return getUserRolePermissionVOS(projectId, oldOwnerUserId, users);
    }

    /**
     * 获取该项目下面的模糊查询的用户角色权限信息
     *
     * @param projectId
     * @param oldOwnerUserId
     * @param users
     * @return
     */
    private List<UserRolePermissionVO> getUserRolePermissionVOS(Long projectId, Long oldOwnerUserId, List<User> users) {
        if (CollectionUtils.isEmpty(users)) {
            return Collections.emptyList();
        }
        List<Long> userIds = users.stream().map(User::getId).collect(Collectors.toList());
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(User::getId, user -> user));

        // 删除被移除人
        if (Objects.nonNull(oldOwnerUserId)) {
            userIds.remove(oldOwnerUserId);
        }
        // 获取此项目下的角色值
        List<Role> roles = roleService.getByProjectIdAndRoleValues(projectId, null);
        Map<Long, Role> roleMap = roles.stream().collect(Collectors.toMap(Role::getId, role -> role));

        // 查询该项目下的用户下的所有权限 Map:key--userId, value--List<Long> roleIds
        List<RoleUser> roleUsers = listRoleByUserIdsAndProjectIds(userIds, projectId);
        Map<Long, List<Long>> roleUserMap = new HashMap<>();
        for (RoleUser roleUser : roleUsers) {
            if (roleUserMap.containsKey(roleUser.getUserId())) {
                roleUserMap.get(roleUser.getUserId()).add(roleUser.getRoleId());
            }else {
                roleUserMap.put(roleUser.getUserId(), Lists.newArrayList(roleUser.getRoleId()));
            }
        }

        List<UserRolePermissionVO> userRoleVOS = new ArrayList<>();
        for (Long userId : userIds) {
            UserRolePermissionVO userRoleVO = new UserRolePermissionVO();
            // 如果该用户权限有且只有访客
            if (roleUserMap.get(userId).size() == 1 && RoleValue.MEMBER.getRoleValue() == roleMap.get(roleUserMap.get(userId).get(0)).getRoleValue()) {
                userRoleVO.setIsCustomer(true);
            }else {
                userRoleVO.setIsCustomer(false);
            }
            userRoleVO.setUserName(userMap.get(userId).getUserName());
            userRoleVO.setUserId(userId);
            userRoleVOS.add(userRoleVO);
        }
        return userRoleVOS;
    }

    /**
     * 通过userIds和项目Id查询权限
     *
     * @param userIds
     * @param projectId
     * @return
     */
    private List<RoleUser> listRoleByUserIdsAndProjectIds(List<Long> userIds, Long projectId) {
        if (CollectionUtils.isEmpty(userIds)) {
            return Lists.newArrayList();
        }
        return roleUserDao.listRoleByUserIdsAndProjectIds(userIds, projectId);
    }

    /**
     * 获取此项目下的用户
     *
     * @param projectId
     * @return
     */
    public List<Long> listUsersByProjectId(Long projectId) {
        return roleUserDao.listUsersByProjectId(projectId);
    }

    /**
     * 资源交接
     *
     * @param oldOwnerUserId
     * @param newOwnerUserId
     * @param projectId
     * @param tenantId
     */
    @Transactional(rollbackFor = Exception.class)
    public void handoverOwner(Long oldOwnerUserId, Long newOwnerUserId,
                              Long projectId, Long tenantId, Long userId,
                              Boolean isRoot) {

        if (Objects.isNull(oldOwnerUserId) || Objects.isNull(newOwnerUserId)) {
            throw new RdosDefineException("必须选择移交人");
        }
        // 更新任务责任人
        batchTaskService.updateTaskOwnerUser(oldOwnerUserId, newOwnerUserId, projectId);
        // 调用删除接口
        removeRoleUserFromProject(userId, oldOwnerUserId, projectId, tenantId, isRoot);
    }

    /**
     * 判断是否可以移除
     *
     * @param isRoot       是否超级管理员
     * @param targetUserId 被移除的用户ID
     * @param userId       当前用户ID
     * @param projectId    项目ID
     */
    private void checkRemovePermission(Boolean isRoot, Long targetUserId, Long userId, Long projectId) {
        if (Objects.equals(targetUserId, userId)) {
            throw new RdosDefineException("("+ErrorCode.CAN_NOT_REMOVE_PROJECT_USER.getDescription()+")", ErrorCode.CAN_NOT_REMOVE_PROJECT_USER);
        }
        // 项目所有者及其以上用户不能移除
        if (isProjectOwner(targetUserId, projectId)) {
            logger.warn("remove target user {} is the owner of project {}, can't remove!", userId, projectId);
            throw new RdosDefineException("平台管理员/租户所有者/租户管理员/项目所有者用户不能被移除", ErrorCode.CAN_NOT_REMOVE_PROJECT_USER);
        }
        // 移除的用户角色是项目管理员，只有项目所有者及其以上权限才可移除
        if (isAdmin(targetUserId, projectId)) {
            if (!isProjectOwner(userId, projectId, isRoot)) {
                throw new RdosDefineException("只有平台管理员/租户所有者/租户管理员/项目所有者才能移除项目管理员", ErrorCode.CAN_NOT_REMOVE_PROJECT_USER);
            }
        }
        if (!isAdmin(userId, projectId, isRoot)) {
            throw new RdosDefineException("无此权限", ErrorCode.CAN_NOT_REMOVE_PROJECT_USER);
        }
    }

    @SecurityAudit(actionType = ActionType.REMOVE_USER,orderedKeys = "removedUser")
    public SecurityResult<Integer> removeRoleUserFromSdk(long userId, long targetUserId, long projectId, Long tenantId, Boolean isRoot) {
        User user = userService.getByDtUicUserId(targetUserId);
        return removeRoleUserFromProject(userId, user.getId(), projectId, tenantId, isRoot);
    }

    /**
     * 获取特点项目所有用户，过滤掉访客
     *
     * @param projectId
     * @param tenantId
     * @param userId
     * @param name
     * @param pageQuery
     * @return
     */
    public PageResult<List<UserRoleVO>> getUserRoleVOSWithoutMember(Long projectId, Long tenantId, Long userId, String name, PageQuery pageQuery) {
        List<Integer> roleValues = Arrays.stream(RoleValue.values()).filter(roleValue ->
                RoleValue.MEMBER.getRoleValue() != roleValue.getRoleValue()).map(RoleValue::getRoleValue).collect(Collectors.toList());
        return getUserRoles(projectId, tenantId, userId, name, roleValues, pageQuery);
    }

    /**
     * 获取特定项目所有用户
     *
     * @param projectId
     * @param tenantId
     * @param userId
     * @param name
     * @param pageQuery
     * @return
     */
    public PageResult<List<UserRoleVO>> getUserAllRoles(Long projectId, Long tenantId, Long userId, String name, PageQuery pageQuery) {
        List<Integer> roleValues = Arrays.stream(RoleValue.values()).map(RoleValue::getRoleValue).collect(Collectors.toList());
        return getUserRoles(projectId, tenantId, userId, name, roleValues, pageQuery);
    }

    /**
     * 获取指定角色值 特定项目下的角色用户信息
     *
     * @param projectId
     * @param tenantId
     * @param userId
     * @param name
     * @param roleValues
     * @param pageQuery
     * @return
     */
    private PageResult<List<UserRoleVO>> getUserRoles(Long projectId, Long tenantId, Long userId, String name, List<Integer> roleValues, PageQuery pageQuery) {
        // 根据名称过滤用户信息
        List<User> users = userService.getUsersByUserName(name);
        if (CollectionUtils.isEmpty(users)) {
            return PageResult.EMPTY_PAGE_RESULT;
        }

        // 根据角色值获取指定角色信息
        List<Role> roles = roleService.getByProjectIdAndRoleValues(projectId, roleValues);
        List<Long> roleIds = roles.stream().map(Role::getId).collect(Collectors.toList());

        // 查询出所有符合条件的数量
        Integer size = countByRoleAndUsers(tenantId, projectId, roleIds, users.stream().map(User::getId).collect(Collectors.toList()));

        // 去用户角色表分页查询出所有符合条件的用户信息
        List<Long> userIds = listUserIdByRoleAndUsers(tenantId, projectId, roleIds, users.stream().map(User::getId).collect(Collectors.toList()), pageQuery);

        // 填充当前项目下的用户信息
        return new PageResult(getUserRoleVOS(projectId, userId, userIds, users), size, pageQuery);
    }

    /**
     * 获取该项目下面的所有的用户角色信息
     *
     * @param projectId
     * @param userIds
     * @return
     */
    private List<UserRoleVO> getUserRoleVOS(Long projectId, Long curUserId, List<Long> userIds, List<User> users) {
        if (CollectionUtils.isEmpty(userIds)) {
            return Collections.emptyList();
        }

        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(User::getId, user -> user));

        List<UserRoleVO> userRoleVOS = new ArrayList<>();
        for (Long userId : userIds) {
            UserRoleVO userRoleVO = new UserRoleVO();
            List<RoleUser> roleUsers = listByUserIdProjectId(userId, projectId);
            userRoleVO.setUserId(userId);
            userRoleVO.setUser(userMap.get(userId));
            userRoleVO.setRoles(roleUsers.stream().map(RoleUser::getRole).collect(Collectors.toList()));
            userRoleVO.setIsSelf(userId.equals(curUserId) ? 1 : 0);
            userRoleVO.setGmtCreate(roleUsers.get(0) == null ? new Timestamp(System.currentTimeMillis()) : roleUsers.get(0).getGmtCreate());
            userRoleVOS.add(userRoleVO);
        }
        return userRoleVOS;
    }

    /**
     * 根据用户和项目找出所有的用户角色信息
     *
     * @param userId
     * @param projectId
     * @return
     */
    private List<RoleUser> listByUserIdProjectId(Long userId, Long projectId) {
        return roleUserDao.listByUserIdProjectId(userId, projectId);
    }

    /**
     * 根据角色和用户 id 统计数量
     *
     * @param roleIds
     * @param userIds
     */
    private Integer countByRoleAndUsers(Long tenantId, Long projectId, List<Long> roleIds, List<Long> userIds) {
        return roleUserDao.countByRoleAndUsers(tenantId, projectId, roleIds, userIds);
    }

    /**
     * 获取角色为管理员及以上的RoleUser
     *
     * @param userId
     * @param tenantId
     * @return
     */
    public List<RoleUser> getRoleUserIsAdmin(Long userId, Long tenantId) {
        return roleUserDao.listRoleUserIsAdminByUserId(userId, tenantId);
    }

    /**
     * 根据用户和角色 id 获取所有的用户信息
     *
     * @param roleIds
     * @param userIds
     * @param pageQuery
     * @return
     */
    public List<Long> listUserIdByRoleAndUsers(Long tenantId, Long projectId, List<Long> roleIds, List<Long> userIds, PageQuery pageQuery) {
        return roleUserDao.listUserIdByRoleAndUsers(tenantId, projectId, roleIds, userIds, pageQuery);
    }

    public boolean isProjectOwner(long userId, long projectId, Boolean isRoot) {
        return BooleanUtils.isTrue(isRoot) || isProjectOwner(userId, projectId);
    }

    /**
     * 判断该用户角色是否是项目所有者角色以上（平台管理员、租户所有者、租户管理员、项目所有者）
     *
     * @param userId
     * @param projectId
     * @return
     */
    public boolean isProjectOwner(Long userId, Long projectId) {
        List<RoleUser> roleUsers = roleUserDao.listByUserIdProjectId(userId, projectId);
        if (CollectionUtils.isEmpty(roleUsers)) {
            return false;
        }
        for (RoleUser roleUser : roleUsers) {
            if (RoleValue.getPriority(roleUser.getRole().getRoleValue()) <= RoleValue.getPriority(RoleValue.PROJECTOWNER.getRoleValue())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断该用户角色是否是项目所有者角色以上（平台管理员、租户所有者、租户管理员、项目所有者、项目管理员）
     *
     * @param userId
     * @param projectId
     * @param isRoot
     * @return
     */
    public boolean isAdmin(long userId, long projectId, Boolean isRoot) {
        return BooleanUtils.isTrue(isRoot) || isAdmin(userId, projectId);
    }

    /**
     * 判断该用户角色是否是项目所有者角色以上（平台管理员、租户所有者、租户管理员、项目所有者、项目管理员）
     *
     * @param userId
     * @param projectId
     * @return
     */
    public boolean isAdmin(long userId, long projectId) {
        List<RoleUser> roleUsers = roleUserDao.listRoleUserIsAdminByUserIdProjectId(userId, projectId);
        return CollectionUtils.isNotEmpty(roleUsers);
    }

    /**
     * 判断当前是否为租户所有者
     *
     * @param userId
     * @param tenantId
     * @return
     */
    public boolean isTenantOwner(long userId, long tenantId) {
        List<RoleUser> roleUsers = roleUserDao.listByUserIdAndTenantId(userId, tenantId);
        for (RoleUser roleUser : roleUsers) {
            if (roleUser.getRole().getRoleValue() == RoleValue.TEANTOWNER.getRoleValue()){
                return true;
            }
        }
        return false;
    }

    public List<RoleUser> getAllRoleUserByUserId(long userId, long tenantId) {
        return roleUserDao.listByUserIdAndTenantId(userId, tenantId);
    }

    public List<User> getProjectAdminUser(long projectId, boolean isRoot, Long userId){
        List<RoleUser> roleUsers = roleUserDao.listRoleUserIsAdminByProjectId(projectId);
        Map<Long, User> userMap = Maps.newHashMap();
        List<User> adminUserList = Lists.newArrayList();
        for(RoleUser roleUser : roleUsers){
            if(!userMap.containsKey(roleUser.getUser().getId())){
                userMap.put(roleUser.getUser().getId(), roleUser.getUser());
            }
        }
        if (isRoot && !userMap.containsKey(userId)) {
            User rootUser = userService.getById(userId);
            adminUserList.add(rootUser);
        }
        adminUserList.addAll(userMap.values());
        return adminUserList;
    }

    /**
     * 权限信息校验，要嘛可行，要嘛报错
     *
     * @param userId
     * @param projectId
     * @param roleIds
     * @return
     */
    private boolean isRoleInfoModifyAvailable(Long userId, Long projectId, List<Long> roleIds, Boolean isRoot) {
        if (BooleanUtils.isTrue(isRoot)) {
            return true;
        }
        for (Long roleId : roleIds) {
            //判断角色Id是否可用
            Role role = roleService.getRoleById(roleId);
            if (role == null) {
                throw new RdosDefineException(ErrorCode.ROLE_NOT_FOUND);
            }
            //参数校验，角色值不能小于租户、项目角色值，项目所有者只能是项目创建者，不能赋权
            if (role.getRoleValue() == RoleValue.PROJECTOWNER.getRoleValue()) {
                throw new RdosDefineException("项目所有者只能是项目创建者，不能赋权", ErrorCode.INVALID_PARAMETERS);
            }
            //只有项目所有者及其以上可以创建管理员
            if (role.getRoleValue() == RoleValue.PROJECTADMIN.getRoleValue() && !isProjectOwner(userId, projectId)) {
                throw new RdosDefineException("只有项目所有者及其以上角色可以创建管理员", ErrorCode.USER_IS_NOT_PROJECT_OWNER);
            }
            //只要有项目管理员可以添加非管理角色
            if (!isAdmin(userId, projectId, isRoot)) {
                throw new RdosDefineException("需要项目管理员及以上角色才能添加非管理角色", ErrorCode.USER_IS_NOT_PROJECT_ADMIN);
            }
        }
        return true;
    }

    /**
     * 获取用户在某项目最高的角色
     *
     * （若不存在，则返回访客角色）
     *
     * @param userId
     * @param projectId
     * @param tenantId
     * @return
     */
    public Integer getMaxRoleValue(Long userId, Long projectId, Long tenantId) {
        List<Integer> roleValues = roleUserDao.listRoleValueByUserIdAndProjectId(userId, tenantId, projectId);
        return roleValues.stream().sorted((r1, r2) -> {
            return RoleValue.getPriority(r1) - RoleValue.getPriority(r2);
        }).findFirst().orElse(RoleValue.CUSTOM.getRoleValue());
    }

    public void checkUserRole(Long createUserId, int roleValue, String errMsg, Long projectId, Long tenantId, Boolean isRoot) {
        if (BooleanUtils.isNotTrue(isRoot)) {
            checkUserRole(createUserId, roleValue, errMsg, projectId, tenantId);
        }
    }

    /**
     * check用户权限是否足够
     *
     * 数据开发以上角色才可以（包括跨项目任务）
     *
     * @param createUserId
     * @param errMsg 报错信息
     * @param roleValue 小于该roleValue则报错
     *
     */
    public void checkUserRole(Long createUserId, int roleValue, String errMsg, Long projectId, Long tenantId) {
        Integer maxRoleValue = getMaxRoleValue(createUserId, projectId, tenantId);
        if (RoleValue.getPriority(maxRoleValue) > RoleValue.getPriority(roleValue)) {
            throw new RdosDefineException(errMsg);
        }
    }


    public List<RoleUser> getRoleUserInTenant(Long tenantId) {
        return roleUserDao.listByTenantIdWithOutNoProject(tenantId);
    }

    public RoleUser getProjectOwner(Long projectId) {
        return roleUserDao.getProjectOwnerByProjectId(projectId);
    }

    public List<RoleUser> getRoleUserByRoleValues(Long userId, List<Integer> roleValues, Long tenantId) {
        return roleUserDao.listByUserIdANdRoleValues(userId, roleValues, tenantId);
    }

    public List<Integer> listRoleValueByUserIdAndProjectId(Long userId, Long tenantId, Long projectId) {
        return roleUserDao.listRoleValueByUserIdAndProjectId(userId, tenantId, projectId);
    }

    /**
     * 校验用户在该项目中是否有相应权限
     *
     * @param userId
     * @param projectId
     * @param usefulRoles 权限列表
     * @param isRoot
     * @return
     */
    public boolean getAimPermission(Long userId, Long projectId, List<Integer> usefulRoles, Boolean isRoot) {
        List<RoleUser> aimPermissions = roleUserDao.listByUserIdProjectId(userId, projectId);
        boolean userPermission = BooleanUtils.isTrue(isRoot);
        if (!userPermission) {
            if (CollectionUtils.isNotEmpty(aimPermissions)) {
                for (RoleUser roleUser : aimPermissions) {
                    if (usefulRoles.contains(roleUser.getRole().getRoleValue())) {
                        userPermission = true;
                        break;
                    }
                }
            }
        }
        return userPermission;
    }


    /**
     * 更新租户所有者
     * 逻辑是
     * 1.删除旧用户 相同租户下  所有项目下 角色为租户所有者的记录。
     * 2.新增该租户下 所有项目下 新用户的租户所有者的角色  删除project_id = -1 角色为访客的记录
     */
    public void changeTenantOwner(Long dtUicOldUserId, Long dtUicNewUserId, Long dtTenantId) {
        Tenant tenant = tenantService.getByDtUicTenantId(dtTenantId);
        if (tenant == null){
            logger.info("该租户 {} 不存在",dtTenantId);
            return;
        }
        // 获取该租户下对应的租户所有者角色
        List<Role> roles = roleService.listByTenantIdAndRoleValue(tenant.getId(), Lists.newArrayList(RoleValue.TEANTOWNER.getRoleValue()));
        // 获取该租户下所有项目的租户所有者的roleId
        List<Long> tenantOwnerRoleIds = roles.stream().map(Role::getId).collect(Collectors.toList());

        User oldUser = userService.getByDtUicUserId(dtUicOldUserId);
        if (oldUser != null){
            //旧角色不为null 才需要删除旧记录
            roleUserDao.deleteByUserIdAndTenantIdAndRoleIds(oldUser.getId(), tenant.getId(), tenantOwnerRoleIds);
            //兼顾历史数据
            // 删除projectId = -1的历史角色
            roleUserDao.deleteByUserIdAndProjectIdAndTenantId(oldUser.getId(), tenant.getId(), -1L);
            authService.clearCache(oldUser.getId(), -1L, tenant.getId());
        }

        User newUser = userService.getByDtUicUserId(dtUicNewUserId);
        if (newUser != null){
            //删除 租户下的默认角色
            roleUserDao.deleteByUserIdAndProjectIdAndTenantId(newUser.getId(), tenant.getId(), -1L);
            //新增 每个项目的 租户所有者的角色
            for (Role role : roles) {
                if (role.getProjectId() == -1 ){
                    continue;
                }
                addDefaultRoleUserList(Lists.newArrayList(role.getId()), newUser.getId(), tenant.getId(), role.getProjectId());
                //同时刷新旧用户的权限信息  放在这里是为了少一次遍历
                if (oldUser != null) {
                    authService.clearCache(oldUser.getId(), role.getProjectId(), tenant.getId());
                }
            }
            return;
        }
    }

    /**
     * 真正操作 用户的角色的信息是否删除
     * @param taskOwnerAndProjectId
     */
    private void operateRoleUser(List<TaskOwnerAndProjectPO> taskOwnerAndProjectId) {
        if (CollectionUtils.isEmpty(taskOwnerAndProjectId)){
            return;
        }
        for (TaskOwnerAndProjectPO vo : taskOwnerAndProjectId){
            Integer delByUserIdAndProjectId = roleUserDao.isDelByUserIdAndProjectId(vo.getOwnerId(), vo.getProjectId());
            if (delByUserIdAndProjectId == 0){
                RoleUser roleUser = roleUserDao.getLastTimeByUserIdAndProjectId(vo.getOwnerId(), vo.getProjectId());
                if (roleUser != null && roleUser.getUserId() != null) {
                    List<RoleUser> replyUserList = roleUserDao.getReplyUserList(vo.getOwnerId(), vo.getProjectId(), new Timestamp(roleUser.getGmtModified().getTime() - 60000), roleUser.getGmtModified());
                    for (RoleUser r : replyUserList) {
                        logger.info(String.format("即将恢复的用户角色信息: %s",r.toString()));
                    }
                    roleUserDao.replyDelUser(vo.getOwnerId(), vo.getProjectId(), new Timestamp(roleUser.getGmtModified().getTime() - 60000), roleUser.getGmtModified());
                }
            }
        }
    }

}
