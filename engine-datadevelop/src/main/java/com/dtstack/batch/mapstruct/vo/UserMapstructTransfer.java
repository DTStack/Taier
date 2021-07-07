package com.dtstack.batch.mapstruct.vo;

import com.dtstack.batch.domain.User;
import com.dtstack.batch.web.user.vo.result.BatchGetUserByIdResultVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapstructTransfer {

    UserMapstructTransfer INSTANCE = Mappers.getMapper(UserMapstructTransfer.class);

    /**
     * User -> BatchGetUserByIdResultVO
     *
     * @param user
     * @return
     */
    BatchGetUserByIdResultVO UserToBatchGetUserByIdResultVO(User user);
}
