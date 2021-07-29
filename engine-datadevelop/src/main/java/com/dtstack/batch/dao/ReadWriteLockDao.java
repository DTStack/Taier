package com.dtstack.batch.dao;

import com.dtstack.batch.domain.ReadWriteLock;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ReadWriteLockDao {
    ReadWriteLock getOne(@Param("id") long id);

    ReadWriteLock getByLockName(@Param("lockName") String lockName);

    Integer insert(ReadWriteLock readWriteLock);

    Integer updateVersionAndModifyUserId(@Param("id") Long id, @Param("version") Integer version, @Param("modifyUserId")Long modifyUserId);

    ReadWriteLock getByProjectIdAndRelationIdAndType(@Param("projectId") long projectId, @Param("relationId") long relationId, @Param("type") String type);

    List<ReadWriteLock> getLocksByIds(@Param("projectId") long projectId, @Param("type") String type, @Param("relationIds") List<Long> relationIds);

    Integer updateVersionAndModifyUserIdDefinitized(@Param("id") Long id, @Param("userId") Long userId);

    Integer deleteByProjectId(@Param("projectId") Long projectId, @Param("userId") Long userId);
}
