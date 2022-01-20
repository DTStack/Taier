package com.dtstack.batch.service.user;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dtstack.engine.common.enums.Deleted;
import com.dtstack.engine.domain.User;
import com.dtstack.engine.dto.UserDTO;
import com.dtstack.engine.mapper.UserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public String getUserName(Long userId) {
        User user = userMapper.selectById(userId);
        return null == user ? "" : user.getUserName();
    }

    public Map<Long, User> getUserMap(Collection<Long> userIds) {
        List<User> users = userMapper.selectBatchIds(userIds);
        if(CollectionUtils.isEmpty(users)){
            return new HashMap<>();
        }
        return users.stream().collect(Collectors.toMap(User::getId, u -> u));
    }

    public User getById(Long userId) {
       return userMapper.selectById(userId);
    }


    public List<User> listAll() {
        return userMapper.selectList(Wrappers.lambdaQuery(User.class).eq(User::getIsDeleted, Deleted.NORMAL.getStatus()));
    }

    public User getByUserName(String username) {
        return userMapper.selectOne(Wrappers.lambdaQuery(User.class).eq(User::getUserName, username));
    }

    public UserDTO getUserByDTO(Long userId) {
        if (userId == null) {
            return null;
        }
        User one = getById(userId);
        if (Objects.isNull(one)) {
            return null;
        }
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(one, userDTO);
        return userDTO;
    }
}
