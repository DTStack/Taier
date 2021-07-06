package com.dtstack.batch.service.impl;

import com.dtstack.batch.common.exception.ErrorCode;
import com.dtstack.batch.common.exception.RdosDefineException;
import com.dtstack.batch.dao.RoleUserDao;
import com.dtstack.batch.dao.UserDao;
import com.dtstack.batch.domain.RoleUser;
import com.dtstack.batch.domain.User;
import com.dtstack.batch.mapstruct.vo.UserMapstructTransfer;
import com.dtstack.batch.vo.UserVO;
import com.dtstack.batch.web.user.vo.result.BatchGetUserByIdResultVO;
import com.dtstack.batch.web.user.vo.result.BatchUserGetUsersInTenantVO;
import com.dtstack.dtcenter.common.enums.EntityStatus;
import com.dtstack.dtcenter.common.enums.RoleValue;
import com.dtstack.dtcenter.common.login.SessionUtil;
import com.dtstack.engine.api.dto.UserDTO;
import com.dtstack.uic.domain.vo.TenantUsersVO;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author sishu.yss
 */
@Service
public class UserService {

    private static Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleUserDao roleUserDao;

    @Autowired
    private RoleUserService roleUserService;

    private static final String IS_CHECK_DDL_KEY = "isCheckDDL";

    /**
     * 获取租户下的用户
     */
    public List<BatchUserGetUsersInTenantVO> getUsersInTenant(Long tenantId) {
        List<BatchUserGetUsersInTenantVO> list = new ArrayList<>();

        List<RoleUser> roleUsers = roleUserDao.listByTenantId(tenantId);
        Set<Long> userIds = Sets.newHashSet();
        roleUsers.forEach(r -> userIds.add(r.getUserId()));

        List<User> users = userDao.listByIds(userIds);
        if (CollectionUtils.isNotEmpty(users)) {
            BatchUserGetUsersInTenantVO getUsersInTenantVO;
            for (User user : users) {
                getUsersInTenantVO = new BatchUserGetUsersInTenantVO();
                getUsersInTenantVO.setUserId(user.getId());
                getUsersInTenantVO.setUserName(user.getUserName());
                list.add(getUsersInTenantVO);
            }
        }
        return list;
    }

    /**
     * 添加用户信息
     *
     * @param user
     * @return
     */
    public Long addUser(User user){
        userDao.insert(user);
        return user.getId();
    }

    public User addOrUpdate(User user) {
        if (user.getId() > 0) {
            userDao.update(user);
        } else {
            Integer insert = userDao.insert(user);
            if (insert != null && insert == 0) {
                user = userDao.getByDtUicUserId(user.getDtuicUserId());
            }
        }

        return user;
    }

    public User getUserByDtUicUserId(long userId) {
        return userDao.getByDtUicUserId(userId);
    }

    /**
     * 根据userIds获取User信息
     *
     * @param userIds
     * @return
     * @author toutian
     */
    public List<User> getUserInIds(List<Long> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return Collections.EMPTY_LIST;
        }
        return userDao.listByIds(userIds);
    }

    /**
     * 根据dtuicUserId 获取 User 信息
     *
     * @param dtUicUserIds
     * @return
     */
    public List<User> getUserInUicUserIds(List<Long> dtUicUserIds) {
        if (CollectionUtils.isEmpty(dtUicUserIds)) {
            return Collections.EMPTY_LIST;
        }
        return userDao.listByDtuicUserIds(dtUicUserIds);
    }

    public BatchGetUserByIdResultVO getUserById(Long tenantId, long userId, String dtToken) {
        try {
            // 获取ddl检查设置
            Integer isCheckDDL = SessionUtil.getValue(dtToken, IS_CHECK_DDL_KEY, Integer.class);
            if (isCheckDDL == null) {
                isCheckDDL = 0;
            }

            User user = userDao.getOne(userId);
            if (user == null) {
                throw new RdosDefineException(ErrorCode.USER_NOT_FIND);
            }

            BatchGetUserByIdResultVO userResultVO = UserMapstructTransfer.INSTANCE.UserToBatchGetUserByIdResultVO(userDao.getOne(userId));
            userResultVO.setIsCheckDDL(isCheckDDL);

            List<RoleUser> roleUsers = roleUserService.getRoleUserIsAdmin(userId, tenantId);
            if (CollectionUtils.isEmpty(roleUsers)) {
                userResultVO.setIsAdminAbove(0);
            } else {
                if (roleUsers.size() == 1 && roleUsers.get(0).getRole().getRoleValue() == RoleValue.MEMBER.getRoleValue()) {
                    userResultVO.setIsAdminAbove(0);
                } else {
                    userResultVO.setIsAdminAbove(1);
                    for (RoleUser roleUser : roleUsers) {
                        if (roleUser.getRole().getRoleValue() == RoleValue.TEANTOWNER.getRoleValue()) {
                            userResultVO.setIsAdminAbove(2);
                        }
                    }
                }
            }
            boolean isRoot = SessionUtil.getUser(dtToken, UserVO.class).getIsRootOnly();
            userResultVO.setIsRoot(isRoot);
            boolean isTenantOwner = roleUserService.isTenantOwner(userId, tenantId);
            userResultVO.setIsTenantOwner(isTenantOwner);
            return userResultVO;
        } catch (Exception e) {
            logger.error("", e);
            throw new RdosDefineException(ErrorCode.GET_USER_ERROR, e);
        }
    }

    public User getUser(Long userId) {
        if (userId == null) {
            return null;
        }
        return userDao.getOne(userId);
    }


    public UserDTO getUserByDTO(Long userId) {
        if (userId == null) {
            return null;
        }
        User one = userDao.getOne(userId);
        if (Objects.isNull(one)) {
            return null;
        }
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(one, userDTO);
        return userDTO;
    }

    public String getUserName(Long userId) {
        User user = userDao.getOne(userId);
        if (user != null) {
            return user.getUserName();
        }
        return null;
    }

    public List<User> getUserByUserName(String userName) {
        List<User> userList = userDao.getUserByUserName(userName);
        return userList;
    }

    /**
     * 根据用户名模糊查询用户信息
     *
     * @param userName
     * @return
     */
    public List<User> getUsersByUserName(String userName) {
        return userDao.getUsersByUserName(userName);
    }

    public Map<Long, User> getUserMap(Collection<Long> userIds) {
        Map<Long, User> idUserMap = new HashMap<>();
        List<User> users = userDao.listByIds(userIds);
        for (User user : users) {
            idUserMap.put(user.getId(), user);
        }
        return idUserMap;
    }

    public Map<Long, User> getUserMapRangeAll(Collection<Long> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return Maps.newHashMap();
        }
        List<User> users = userDao.listUsersIncludeDeletedByIds(userIds);
        return users.stream()
                .collect(Collectors.toMap(r -> r.getId(), r -> r, (v1, v2) -> v2));
    }

    /**
     * 根据id和name模糊查询
     *
     * @param userIds
     * @param userName
     * @return
     */
    public List<User> getUsersByUserNameAndUserIds(List<Long> userIds, String userName) {
        return userDao.getUsersByUserNameAndUserIds(userIds, userName);
    }

    /**
     * 处理该租户下uic用户信息，如果该用户不存在，则新增
     *
     * @param tenantUsersVOList
     * @return
     */
    public List<User> dealTenantUicUserList(List<TenantUsersVO> tenantUsersVOList){
        List<User> userList = new ArrayList<>();

        List<Long> dtuicUserIdList = tenantUsersVOList.stream().map(TenantUsersVO::getId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(dtuicUserIdList)) {
            return userList;
        }
        // 获取到现在存在的用户信息
        List<User> existUserList = userDao.listByDtuicUserIds(dtuicUserIdList);
        userList.addAll(existUserList);
        Map<Long, User> userMap = existUserList.stream().collect(Collectors.toMap(User::getDtuicUserId, Function.identity(), (key1, key2) -> key2));

        List<User> addUserList = new ArrayList<>();
        for (TenantUsersVO tenantUsersVO : tenantUsersVOList) {
            // 判断用户是否存在，如果不存在，则需要增加
            if (!userMap.containsKey(tenantUsersVO.getId())) {
                User user = new User();
                user.setUserName(tenantUsersVO.getUsername());
                //取账号为邮箱
                user.setEmail(tenantUsersVO.getUsername());
                user.setPhoneNumber(tenantUsersVO.getPhone());
                user.setDtuicUserId(tenantUsersVO.getId());
                //用户状态0：正常，1：禁用
                user.setStatus(BooleanUtils.isTrue(tenantUsersVO.getActive()) ? EntityStatus.normal.getStatus() : EntityStatus.disable.getStatus());
                addUserList.add(user);
            }
        }

        if (CollectionUtils.isNotEmpty(addUserList)) {
            userDao.batchInsert(addUserList);
            userList.addAll(addUserList);
        }
        return userList;
    }

    /**
     * 根据ID 获取 user信息
     * @param id
     * @return
     */
    public User getOne(Long id){
        return userDao.getOne(id);
    }

}
