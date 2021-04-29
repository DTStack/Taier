package com.dtstack.engine.master.impl;

import com.dtstack.engine.api.domain.User;
import com.dtstack.engine.api.dto.UserDTO;
import com.dtstack.engine.api.vo.ScheduleTaskVO;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.dao.UserDao;
import com.dtstack.engine.master.router.login.DtUicUserConnect;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

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
        List<Long> dbUserId = userDb.stream().map(User::getDtuicUserId).collect(Collectors.toList());
        List<Long> userUicId = userSetIds.stream().filter(userId -> !dbUserId.contains(userId)).collect(Collectors.toList());

        // 去uic查询这些用户
        List<User> users = dtUicUserConnect.getUserByUserIds(environmentContext.getSdkToken(), environmentContext.getDtUicUrl(), userUicId);

        // 保存用户
        saveUser(users);

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
}
