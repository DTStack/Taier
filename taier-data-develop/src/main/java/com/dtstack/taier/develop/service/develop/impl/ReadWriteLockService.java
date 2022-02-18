/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.taier.develop.service.develop.impl;

import com.dtstack.taier.common.Callback;
import com.dtstack.taier.common.enums.ReadWriteLockType;
import com.dtstack.taier.common.enums.TaskLockStatus;
import com.dtstack.taier.common.exception.ErrorCode;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.dao.domain.BatchReadWriteLock;
import com.dtstack.taier.dao.mapper.DevelopReadWriteLockDao;
import com.dtstack.taier.develop.dto.devlop.ReadWriteLockVO;
import com.dtstack.taier.develop.service.user.UserService;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * company: www.dtstack.com
 *
 * @author: toutian
 * create: 2018/1/9
 */
@Service
public class ReadWriteLockService {

    @Autowired
    private DevelopReadWriteLockDao developReadWriteLockDao;

    @Autowired
    private UserService userService;

    private static final String SPLIT = "_";

    private static final int INIT_VERSION = 1;

    /**
     * 获取锁
     * @param tenantId
     * @param userId
     * @param type
     * @param fileId
     * @param subFileIds
     * @return
     */
    public ReadWriteLockVO getLock(Long tenantId, Long userId, String type, Long fileId, List<Long> subFileIds) {
        if (CollectionUtils.isNotEmpty(subFileIds)) {
            //解锁子任务
            subFileIds.forEach(id -> addOrUpdateLock(tenantId, userId, type, id));
        }
        return addOrUpdateLock(tenantId, userId, type, fileId);
    }


    /**
     * 新增、更新 锁信息
     * @param tenantId
     * @param userId
     * @param type
     * @param fileId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ReadWriteLockVO addOrUpdateLock(Long tenantId, Long userId, String type, Long fileId) {
        ReadWriteLockVO readWriteLockVO;
        BatchReadWriteLock readWriteLock = developReadWriteLockDao.getByTenantIdAndRelationIdAndType(tenantId, fileId, type);
        if (readWriteLock == null) {
            readWriteLockVO = this.insert(tenantId, fileId, type, userId);
        } else {
            Integer result = 0;
            result = developReadWriteLockDao.updateVersionAndModifyUserId(readWriteLock.getId(), null, userId);
            readWriteLock = developReadWriteLockDao.getOne(readWriteLock.getId());
            readWriteLockVO = ReadWriteLockVO.toVO(readWriteLock);
            String userName = userService.getUserName(readWriteLock.getModifyUserId());
            readWriteLockVO.setLastKeepLockUserName(userName);
        }
        return readWriteLockVO;
    }

    /**
     * 锁检查，判断是否有其他人也在修改相同的文件
     * @param userId
     * @param tenantId
     * @param relationId
     * @param type
     * @param lockVersion
     * @param relationLocalVersion
     * @param relationVersion
     * @return
     */
    private ReadWriteLockVO checkLock(Long userId, Long tenantId, Long relationId, ReadWriteLockType type, Integer lockVersion, Integer relationLocalVersion, Integer relationVersion) {
        BatchReadWriteLock readWriteLock = developReadWriteLockDao.getByTenantIdAndRelationIdAndType(tenantId, relationId, type.name());
        if (readWriteLock == null) {
            throw new RdosDefineException(ErrorCode.LOCK_IS_NOT_EXISTS);
        }
        Long modifyUserId = readWriteLock.getModifyUserId();
        int version = readWriteLock.getVersion();

        //初始化返回对象
        ReadWriteLockVO readWriteLockVO = new ReadWriteLockVO();
        readWriteLockVO.setGmtModified(readWriteLock.getGmtModified());
        readWriteLockVO.setLastKeepLockUserName(userService.getUserName(modifyUserId));
        //任务版本是否保持一致
        if (!relationLocalVersion.equals(relationVersion)) {
            //表示已经被提交了
            //提示任务版本已经更新不能在提交，是否保存到本地，或取消修改
            readWriteLockVO.setResult(TaskLockStatus.UPDATE_COMPLETED.getVal());
            return readWriteLockVO;
        } else if (modifyUserId.equals(userId) && version == lockVersion) {
            readWriteLockVO.setResult(TaskLockStatus.TO_UPDATE.getVal());
            readWriteLockVO.setVersion(lockVersion);
            return readWriteLockVO;
        } else {//提示其他人也正在编辑，是否取消修改
            readWriteLockVO.setResult(TaskLockStatus.TO_CONFIRM.getVal());
            return readWriteLockVO;
        }
    }


    /**
     * 获取读写锁
     * @param tenantId
     * @param relationId
     * @param type
     * @return
     */
    public BatchReadWriteLock getReadWriteLock(Long tenantId, Long relationId, String type) {
        BatchReadWriteLock readWriteLock = developReadWriteLockDao.getByTenantIdAndRelationIdAndType(tenantId, relationId, type);
        if (readWriteLock == null) {
            throw new RdosDefineException(ErrorCode.LOCK_IS_NOT_EXISTS);
        }
        return readWriteLock;
    }


    /**
     * 构建锁的名称
     * @param tenantId
     * @param taskId
     * @param type
     * @return
     */
    private String uniteName(Long tenantId, Long taskId, String type) {
        StringBuilder builder = new StringBuilder();
        builder.append(tenantId).append(SPLIT).append(taskId).append(SPLIT).append(type);
        return builder.toString();
    }


    /**
     * 获取锁VO对象
     * @param tenantId
     * @param relationId
     * @param type
     * @param userId
     * @param modifyUserId
     * @param gmtModified
     * @return
     */
    public ReadWriteLockVO getDetail(Long tenantId, Long relationId, ReadWriteLockType type, Long userId, Long modifyUserId, Timestamp gmtModified) {
        BatchReadWriteLock readWriteLock = developReadWriteLockDao.getByTenantIdAndRelationIdAndType(tenantId, relationId, type.name());
        if (readWriteLock == null) {
            ReadWriteLockVO readWriteLockVO = new ReadWriteLockVO();
            readWriteLockVO.setLastKeepLockUserName(userService.getUserName(modifyUserId));
            readWriteLockVO.setGmtModified(gmtModified);
            return readWriteLockVO;
        }
        ReadWriteLockVO readWriteLockVO = ReadWriteLockVO.toVO(readWriteLock);
        readWriteLockVO.setLastKeepLockUserName(userService.getUserName(readWriteLockVO.getModifyUserId()));
        return readWriteLockVO;
    }


    /**
     * 根据 锁对象ids 批量获取锁信息
     * @param tenantId
     * @param type
     * @param relationIds
     * @param userId
     * @param names
     * @return
     */
    public Map<Long, ReadWriteLockVO> getLocks(Long tenantId, ReadWriteLockType type, List<Long> relationIds, long userId, Map<Long, String> names) {
        List<BatchReadWriteLock> ls = developReadWriteLockDao.getLocksByIds(tenantId, type.name(), relationIds);
        Map<Long, BatchReadWriteLock> records = ls.stream()
                .collect(Collectors.toMap(r -> r.getRelationId(), r -> r, (v1, v2) -> v2));
        Map<Long, ReadWriteLockVO> result = Maps.newHashMap();
        for (Long id : relationIds) {
            if (records.containsKey(id)) {
                BatchReadWriteLock readWriteLock = records.get(id);
                ReadWriteLockVO readWriteLockVO = ReadWriteLockVO.toVO(readWriteLock);
                readWriteLockVO.setLastKeepLockUserName(getUserNameInMemory(names, readWriteLockVO.getModifyUserId()));
                result.put(id, readWriteLockVO);
            } else {
                ReadWriteLockVO readWriteLockVO = new ReadWriteLockVO();
                readWriteLockVO.setLastKeepLockUserName(null);
                readWriteLockVO.setGmtModified(null);
                result.put(id, readWriteLockVO);
            }
        }
        return result;
    }


    /**
     * 根据用户id从缓存中获取用户名称
     * @param names
     * @param userId
     * @return
     */
    private String getUserNameInMemory(Map<Long, String> names, Long userId) {
        if (names.containsKey(userId)) {
            return names.get(userId);
        } else {
            String name = userService.getUserName(userId);
            names.put(userId, name);
            return name;
        }
    }


    /**
     *
     * @param userId
     * @param type
     * @param relationId
     * @param tenantId
     * @return
     */
    private ReadWriteLockVO forceUpdateLock(Long userId, ReadWriteLockType type, Long relationId, Long tenantId) {
        BatchReadWriteLock readWriteLock = developReadWriteLockDao.getByTenantIdAndRelationIdAndType(tenantId, relationId, type.name());
        if (readWriteLock != null) {
            developReadWriteLockDao.updateVersionAndModifyUserIdDefinitized(readWriteLock.getId(), userId);

            readWriteLock = developReadWriteLockDao.getOne(readWriteLock.getId());
            ReadWriteLockVO readWriteLockVO = ReadWriteLockVO.toVO(readWriteLock);
            String userName = userService.getUserName(readWriteLock.getModifyUserId());
            readWriteLockVO.setLastKeepLockUserName(userName);
            return readWriteLockVO;
        } else {
            throw new RdosDefineException(ErrorCode.LOCK_IS_NOT_EXISTS);
        }

    }

    /**
     * 添加锁对象
     * @param tenantId
     * @param fileId
     * @param type
     * @param userId
     * @return
     */
    private ReadWriteLockVO insert(Long tenantId, Long fileId, String type, Long userId) {
        BatchReadWriteLock readWriteLock = new BatchReadWriteLock();
        readWriteLock.setTenantId(tenantId);
        readWriteLock.setLockName(uniteName(fileId, tenantId, type));
        readWriteLock.setCreateUserId(userId);
        readWriteLock.setModifyUserId(userId);
        readWriteLock.setRelationId(fileId);
        readWriteLock.setType(type);
        developReadWriteLockDao.insert(readWriteLock);
        ReadWriteLockVO readWriteLockVO = ReadWriteLockVO.toVO(readWriteLock);
        readWriteLockVO.setVersion(INIT_VERSION);
        readWriteLockVO.setGmtModified(Timestamp.valueOf(LocalDateTime.now()));
        readWriteLockVO.setLastKeepLockUserName(userService.getUserName(userId));
        return readWriteLockVO;
    }

    /**
     * 利用回调函数实现锁机制
     *
     * @param tenantId
     * @param relationId
     * @param type
     * @param userId
     * @param callback
     * @return
     */
    public ReadWriteLockVO dealWithLock(Long tenantId, Long relationId, ReadWriteLockType type, Long userId,
                                        Callback<Void> callback) {

        ReadWriteLockVO readWriteLockVO = null;
        Integer update = (Integer) callback.submit(null);
        if (update != 1) {
            readWriteLockVO = getLockBasicInfo(tenantId, relationId, type);
            readWriteLockVO.setResult(TaskLockStatus.UPDATE_COMPLETED.getVal());
            return readWriteLockVO;
        } else {
            readWriteLockVO = forceUpdateLock(userId, type, relationId, tenantId);
            readWriteLockVO.setResult(TaskLockStatus.TO_UPDATE.getVal());
            return readWriteLockVO;
        }
    }

    private ReadWriteLockVO getLockBasicInfo(Long tenantId, long relationId, ReadWriteLockType type) {
        BatchReadWriteLock readWriteLock = developReadWriteLockDao.getByTenantIdAndRelationIdAndType(tenantId, relationId, type.name());
        if (readWriteLock == null) {
            throw new RdosDefineException(ErrorCode.LOCK_IS_NOT_EXISTS);
        }
        ReadWriteLockVO readWriteLockVO = new ReadWriteLockVO();
        readWriteLockVO.setGmtModified(readWriteLock.getGmtModified());
        readWriteLockVO.setLastKeepLockUserName(userService.getUserName(readWriteLock.getModifyUserId()));
        return readWriteLockVO;
    }

}
