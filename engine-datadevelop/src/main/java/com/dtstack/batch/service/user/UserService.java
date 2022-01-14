package com.dtstack.batch.service.user;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dtstack.engine.domain.User;
import com.dtstack.engine.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public List<User> listByIds(List<Long> userIds) {
        return userMapper.selectBatchIds(userIds);
    }

    public User getByUserName(String username) {
        return userMapper.selectOne(Wrappers.lambdaQuery(User.class).eq(User::getUserName, username));
    }
}
