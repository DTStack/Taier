package com.dtstack.batch.mapstruct.user;

import com.dtstack.batch.vo.user.UserVO;
import com.dtstack.engine.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface UserTransfer {

    UserTransfer INSTANCE = Mappers.getMapper(UserTransfer.class);

    List<UserVO> toVo(List<User> user);
}
