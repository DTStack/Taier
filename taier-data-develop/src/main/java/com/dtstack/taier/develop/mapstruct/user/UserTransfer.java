package com.dtstack.taier.develop.mapstruct.user;

import com.dtstack.taier.dao.domain.User;
import com.dtstack.taier.develop.vo.user.UserVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface UserTransfer {

    UserTransfer INSTANCE = Mappers.getMapper(UserTransfer.class);

    List<UserVO> toVo(List<User> user);
}
