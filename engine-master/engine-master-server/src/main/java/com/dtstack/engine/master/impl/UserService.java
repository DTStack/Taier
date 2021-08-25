package com.dtstack.engine.master.impl;

import com.dtstack.dtcenter.common.enums.EntityStatus;
import com.dtstack.engine.api.domain.User;
import com.dtstack.engine.api.dto.ScheduleTaskForFillDataDTO;
import com.dtstack.engine.api.dto.UserDTO;
import com.dtstack.engine.api.vo.ScheduleFillDataJobPreViewVO;
import com.dtstack.engine.api.vo.ScheduleJobVO;
import com.dtstack.engine.api.vo.ScheduleTaskVO;
import com.dtstack.engine.api.vo.tenant.TenantUsersVO;
import com.dtstack.engine.api.vo.user.UserVO;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.dao.UserDao;
import com.dtstack.engine.master.router.login.DtUicUserConnect;
import com.google.common.collect.Lists;
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

@Service
public class UserService {

    private final static Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserDao userDao;

    @Autowired
    private DtUicUserConnect dtUicUserConnect;

    @Autowired
    private EnvironmentContext environmentContext;


    /**
     * 封装user
     *
     * @param vos
     */
    public void fillUser(List<ScheduleTaskVO> vos) {
        if (CollectionUtils.isEmpty(vos)) {
            return;
        }

        List<Long> userIds = vos.stream().map(ScheduleTaskVO::getOwnerUserId).collect(Collectors.toList());
        List<Long> createUserIds = vos.stream().map(ScheduleTaskVO::getCreateUserId).collect(Collectors.toList());
        List<Long> modifyUserIds = vos.stream().map(ScheduleTaskVO::getModifyUserId).collect(Collectors.toList());

        Set<Long> userSetIds = new HashSet<>();
        userSetIds.addAll(createUserIds);
        userSetIds.addAll(modifyUserIds);
        userSetIds.addAll(userIds);

        if (CollectionUtils.isNotEmpty(userSetIds)) {
            List<User> userDb = userDao.getByDtUicUserIds(userSetIds);

            // 查询出库里面没有用用户
            List<User> users = findUser(userSetIds, userDb);

            userDb.addAll(users);
            Map<Long, List<User>> userMaps = userDb.stream().collect(Collectors.groupingBy(User::getDtuicUserId));
            for (ScheduleTaskVO vo : vos) {
                User user = userMaps.get(vo.getOwnerUserId()) != null ? userMaps.get(vo.getOwnerUserId()).get(0) : null;
                User createUser = userMaps.get(vo.getCreateUserId()) != null ? userMaps.get(vo.getCreateUserId()).get(0) : null;
                User modifyUser = userMaps.get(vo.getModifyUserId()) != null ? userMaps.get(vo.getModifyUserId()).get(0) : null;

                vo.setOwnerUser(buildUserDTO(user));
                vo.setCreateUser(buildUserDTO(createUser));
                vo.setModifyUser(buildUserDTO(modifyUser));
            }
        }

    }

    private List<User> findUser(Set<Long> userSetIds, List<User> userDb) {
        List<Long> dbUserId = userDb.stream().map(User::getDtuicUserId).collect(Collectors.toList());
        List<Long> userUicId = userSetIds.stream().filter(userId -> !dbUserId.contains(userId)).collect(Collectors.toList());

        // 去uic查询这些用户
        List<User> users = Lists.newArrayList();
        try {
            users = dtUicUserConnect.getUserByUserIds(environmentContext.getSdkToken(), environmentContext.getDtUicUrl(), userUicId);
            // 保存用户
            saveUser(users);
        } catch (Exception e) {
            LOGGER.error("add user error:",e);
        }
        return users;
    }

    private UserDTO buildUserDTO(User user) {
        UserDTO userDTO = null;
        if (user != null) {
            userDTO = new UserDTO();
            userDTO.setDtuicUserId(user.getDtuicUserId());
            userDTO.setUserName(user.getUserName());
            userDTO.setEmail(user.getEmail());
            userDTO.setPhoneNumber(user.getPhoneNumber());
        }
        return userDTO;
    }

    private void saveUser(List<User> users) {
        if (CollectionUtils.isNotEmpty(users)) {
            if (users.size() > environmentContext.getListChildTaskLimit()) {
                List<List<User>> partition = Lists.partition(users, environmentContext.getListChildTaskLimit());
                for (List<User> userList : partition) {
                    userDao.insertBatch(userList);
                }
            } else {
                userDao.insertBatch(users);
            }
        }
    }

    public void fillFillDataJobUserName(List<ScheduleFillDataJobPreViewVO> resultContent) {
        Set<Long> userId = resultContent.stream().map(ScheduleFillDataJobPreViewVO::getDutyUserId).collect(Collectors.toSet());
        if (CollectionUtils.isNotEmpty(userId)) {
            List<User> userDb = userDao.getByDtUicUserIds(userId);

            List<User> users = findUser(userId, userDb);
            userDb.addAll(users);
            Map<Long, List<User>> userMaps = userDb.stream().collect(Collectors.groupingBy(User::getDtuicUserId));


            for (ScheduleFillDataJobPreViewVO viewVO : resultContent) {
                User user = userMaps.get(viewVO.getDutyUserId()) != null ? userMaps.get(viewVO.getDutyUserId()).get(0) : null;

                if (user != null) {
                    viewVO.setDutyUserName(user.getUserName());
                }

            }
        }

    }

    public void fillScheduleTaskForFillDataDTO(List<ScheduleTaskForFillDataDTO> scheduleTaskForFillDataDTOS) {
        try {
            if (CollectionUtils.isEmpty(scheduleTaskForFillDataDTOS)) {
                return;
            }

            List<Long> userIds = scheduleTaskForFillDataDTOS.stream().map(ScheduleTaskForFillDataDTO::getOwnerUserId).collect(Collectors.toList());
            List<Long> createUserIds = scheduleTaskForFillDataDTOS.stream().map(ScheduleTaskForFillDataDTO::getCreateUserId).collect(Collectors.toList());

            Set<Long> userSetIds = new HashSet<>();
            userSetIds.addAll(createUserIds);
            userSetIds.addAll(userIds);

            if (CollectionUtils.isNotEmpty(userIds)) {
                List<User> userDb = userDao.getByDtUicUserIds(userSetIds);

                // 查询出库里面没有用用户
                List<User> users = findUser(userSetIds, userDb);

                userDb.addAll(users);
                Map<Long, List<User>> userMaps = userDb.stream().collect(Collectors.groupingBy(User::getDtuicUserId));
                for (ScheduleTaskForFillDataDTO vo : scheduleTaskForFillDataDTOS) {
                    User user = userMaps.get(vo.getOwnerUserId()) != null ? userMaps.get(vo.getOwnerUserId()).get(0) : null;
                    User createUser = userMaps.get(vo.getCreateUserId()) != null ? userMaps.get(vo.getCreateUserId()).get(0) : null;

                    vo.setOwnerUser(buildUserDTO(user));
                    vo.setCreateUser(buildUserDTO(createUser));
                }
            }

        } catch (Exception e) {
            LOGGER.error("",e);
        }

    }

    public void fillScheduleJobVO(List<ScheduleJobVO> jobVOS) {
        try {
            if (CollectionUtils.isEmpty(jobVOS)) {
                return;
            }
            List<ScheduleTaskVO> taskVOS = jobVOS.stream().map(ScheduleJobVO::getBatchTask).collect(Collectors.toList());
            fillUser(taskVOS);
        } catch (Exception e) {
            LOGGER.error("",e);
        }

    }

    public List<UserVO> findAllUser() {
        List<User> userDb = userDao.getAllUser();
        List<UserVO> userVOS = Lists.newArrayList();
        for (User user : userDb) {
            UserVO vo = new UserVO();
            vo.setUserName(user.getUserName());
            vo.setDtuicUserId(user.getDtuicUserId());
            vo.setEmail(user.getEmail());
            userVOS.add(vo);
        }
        return userVOS;
    }

    public User getById(Long userId) {
        if (userId == null) {
            return null;
        }
        return userDao.getByUserId(userId);
    }

    public List<User> listByIds(Collection<Long> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return Collections.EMPTY_LIST;
        }
        return userDao.listByIds(userIds);
    }

    public List<User> listByNotInIdsAndName(List<Long> userInIds, String name){
        return userDao.listByNotInIdsAndName(userInIds, name);
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
        List<User> existUserList = userDao.getByDtUicUserIds(dtuicUserIdList);
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
     * 根据id和name模糊查询
     *
     * @param userIds
     * @param userName
     * @return
     */
    public List<User> getUsersByUserNameAndUserIds(List<Long> userIds, String userName) {
        return userDao.getUsersByUserNameAndUserIds(userIds, userName);
    }

    public Map<Long, User> getUserMap(Collection<Long> userIds) {
        Map<Long, User> idUserMap = new HashMap<>();
        List<User> users = userDao.listByIds(userIds);
        for (User user : users) {
            idUserMap.put(user.getId(), user);
        }
        return idUserMap;
    }

    public String getUserName(Long userId) {
        User user = userDao.getByUserId(userId);
        if (user != null) {
            return user.getUserName();
        }
        return null;
    }

    public UserDTO getUserByDTO(Long userId) {
        if (userId == null) {
            return null;
        }
        User one = userDao.getByUserId(userId);
        if (Objects.isNull(one)) {
            return null;
        }
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(one, userDTO);
        return userDTO;
    }

    public User getByDtUicUserId(Long dtuicUserId) {
        return userDao.getByDtUicUserId(dtuicUserId);
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
        return userDao.getByDtUicUserIds(dtUicUserIds);
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

    /**
     * 根据用户名模糊查询用户信息
     *
     * @param userName
     * @return
     */
    public List<User> getUsersByUserName(String userName) {
        return userDao.getUsersByUserName(userName);
    }
}
