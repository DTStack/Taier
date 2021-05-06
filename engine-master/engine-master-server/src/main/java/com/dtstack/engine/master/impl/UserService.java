package com.dtstack.engine.master.impl;

import com.dtstack.engine.api.domain.User;
import com.dtstack.engine.api.dto.ScheduleTaskForFillDataDTO;
import com.dtstack.engine.api.dto.UserDTO;
import com.dtstack.engine.api.vo.ScheduleFillDataJobPreViewVO;
import com.dtstack.engine.api.vo.ScheduleTaskVO;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.dao.UserDao;
import com.dtstack.engine.master.router.login.DtUicUserConnect;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    public void fullUser(List<ScheduleTaskVO> vos) {
        if (CollectionUtils.isEmpty(vos)) {
            return;
        }

        List<Long> userIds = vos.stream().map(ScheduleTaskVO::getUserId).collect(Collectors.toList());
        List<Long> createUserIds = vos.stream().map(ScheduleTaskVO::getCreateUserId).collect(Collectors.toList());
        List<Long> modifyUserIds = vos.stream().map(ScheduleTaskVO::getModifyUserId).collect(Collectors.toList());

        Set<Long> userSetIds = new HashSet<>();
        userSetIds.addAll(createUserIds);
        userSetIds.addAll(modifyUserIds);
        userSetIds.addAll(userIds);

        List<User> userDb = userDao.getByDtUicUserIds(userSetIds);

        // 查询出库里面没有用用户
        List<User> users = findUser(userSetIds, userDb);

        userDb.addAll(users);
        Map<Long, List<User>> userMaps = userDb.stream().collect(Collectors.groupingBy(User::getDtuicUserId));
        for (ScheduleTaskVO vo : vos) {
            User user = userMaps.get(vo.getUserId()).get(0);
            User createUser = userMaps.get(vo.getCreateUserId()).get(0);
            User modifyUser = userMaps.get(vo.getModifyUserId()).get(0);

            vo.setOwnerUser(buildUserDTO(user));
            vo.setCreateUser(buildUserDTO(createUser));
            vo.setModifyUser(buildUserDTO(modifyUser));
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

    }

    public void fullFillDataJobUserName(List<ScheduleFillDataJobPreViewVO> resultContent) {
        Set<Long> userId = resultContent.stream().map(ScheduleFillDataJobPreViewVO::getDutyUserId).collect(Collectors.toSet());
        List<User> userDb = userDao.getByDtUicUserIds(userId);

        List<User> users = findUser(userId, userDb);
        userDb.addAll(users);
        Map<Long, List<User>> userMaps = userDb.stream().collect(Collectors.groupingBy(User::getDtuicUserId));


        for (ScheduleFillDataJobPreViewVO viewVO : resultContent) {
            User user = userMaps.get(viewVO.getDutyUserId()).get(0);

            if (user != null) {
                viewVO.setDutyUserName(user.getUserName());
            }

        }
    }

    public void fullScheduleTaskForFillDataDTO(List<ScheduleTaskForFillDataDTO> scheduleTaskForFillDataDTOS) {
        try {
            if (CollectionUtils.isEmpty(scheduleTaskForFillDataDTOS)) {
                return;
            }

            List<Long> userIds = scheduleTaskForFillDataDTOS.stream().map(ScheduleTaskForFillDataDTO::getOwnerUserId).collect(Collectors.toList());
            List<Long> createUserIds = scheduleTaskForFillDataDTOS.stream().map(ScheduleTaskForFillDataDTO::getCreateUserId).collect(Collectors.toList());

            Set<Long> userSetIds = new HashSet<>();
            userSetIds.addAll(createUserIds);
            userSetIds.addAll(userIds);

            List<User> userDb = userDao.getByDtUicUserIds(userSetIds);

            // 查询出库里面没有用用户
            List<User> users = findUser(userSetIds, userDb);

            userDb.addAll(users);
            Map<Long, List<User>> userMaps = userDb.stream().collect(Collectors.groupingBy(User::getDtuicUserId));
            for (ScheduleTaskForFillDataDTO vo : scheduleTaskForFillDataDTOS) {
                User user = userMaps.get(vo.getOwnerUserId()).get(0);
                User createUser = userMaps.get(vo.getCreateUserId()).get(0);

                vo.setOwnerUser(buildUserDTO(user));
                vo.setCreateUser(buildUserDTO(createUser));
            }

        } catch (Exception e) {
            LOGGER.error("",e);
        }

    }
}
