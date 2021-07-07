package com.dtstack.batch.mapstruct.vo;

import com.dtstack.batch.domain.ReadWriteLock;
import com.dtstack.batch.vo.ReadWriteLockVO;
import com.dtstack.batch.web.task.vo.result.ReadWriteLockGetLockResultVO;
import com.dtstack.batch.web.task.vo.result.ReadWriteLockResultVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BatchReadWriteLockMapstructTransfer {

    BatchReadWriteLockMapstructTransfer INSTANCE = Mappers.getMapper(BatchReadWriteLockMapstructTransfer.class);

    /**
     * ReadWriteLock -> ReadWriteLockResultVO
     * @param readWriteLock
     * @return
     */
    ReadWriteLockResultVO  ReadWriteLockToResultVO(ReadWriteLock readWriteLock);

    /**
     * ReadWriteLockVO -> ReadWriteLockGetLockResultVO
     * @param readWriteLockVO
     * @return
     */
    @Mapping(source = "isGetLock", target = "getLock")
    ReadWriteLockGetLockResultVO  ReadWriteLockVOToReadWriteLockGetLockResultVO(ReadWriteLockVO readWriteLockVO);

}
