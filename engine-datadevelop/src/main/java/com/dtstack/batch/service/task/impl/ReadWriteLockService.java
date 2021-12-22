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

package com.dtstack.batch.service.task.impl;

import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.batch.dao.ReadWriteLockDao;
import com.dtstack.batch.domain.ReadWriteLock;
import com.dtstack.batch.vo.ReadWriteLockVO;
import com.dtstack.engine.common.Callback;
import com.dtstack.engine.common.enums.ReadWriteLockType;
import com.dtstack.engine.common.enums.TaskLockStatus;
import com.dtstack.engine.master.impl.UserService;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
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
    private ReadWriteLockDao readWriteLockDao;

    @Autowired
    private UserService userService;

    private static final String SPLIT = "_";

    private static final int INIT_VERSION = 1;

    /**
     * 获取锁
     */
    public ReadWriteLockVO getLock(Long tenantId, Long userId, String type, Long fileId, long projectId, Integer lockVersion, List<Long> subFileIds) {
        if (CollectionUtils.isNotEmpty(subFileIds)) {
            //解锁子任务
            subFileIds.forEach(id -> addOrUpdateLock(tenantId, userId, type, id, projectId));
        }
        return addOrUpdateLock(tenantId, userId, type, fileId, projectId);
    }

    @Transactional(rollbackFor = Exception.class)
    public ReadWriteLockVO addOrUpdateLock(Long tenantId, Long userId, String type, Long fileId, Long projectId) {
        ReadWriteLockVO readWriteLockVO;
        ReadWriteLock readWriteLock = readWriteLockDao.getByProjectIdAndRelationIdAndType(projectId, fileId, type);
        if (readWriteLock == null) {
            readWriteLockVO = this.insert(tenantId, projectId, fileId, type, userId);
        } else {
            Integer result = 0;
            result = readWriteLockDao.updateVersionAndModifyUserId(readWriteLock.getId(), null, userId);
            readWriteLock = readWriteLockDao.getOne(readWriteLock.getId());
            readWriteLockVO = ReadWriteLockVO.toVO(readWriteLock);
            String userName = userService.getUserName(readWriteLock.getModifyUserId());
            readWriteLockVO.setLastKeepLockUserName(userName);
            if (result != 1) {
                //获取锁失败
                readWriteLockVO.setIsGetLock(false);
            } else {
                readWriteLockVO.setIsGetLock(true);
            }
        }
        return readWriteLockVO;
    }

    /**
     * 检查是否拥有该任务的锁
     */
    private ReadWriteLockVO isGetLock(ReadWriteLockVO lockVO, Long userId) {
        Long modifyUserId = lockVO.getModifyUserId();
        if (modifyUserId.equals(userId)) {
            lockVO.setIsGetLock(true);
        } else {
            lockVO.setIsGetLock(false);
        }
        return lockVO;
    }

    private ReadWriteLockVO checkLock(Long userId, long projectId, long relationId, ReadWriteLockType type, int lockVersion, int relationLocalVersion, int relationVersion) {
        ReadWriteLock readWriteLock = readWriteLockDao.getByProjectIdAndRelationIdAndType(projectId, relationId, type.name());
        if (readWriteLock == null) {
            throw new RdosDefineException(ErrorCode.LOCK_IS_NOT_EXISTS);
        }
        Long modifyUesrId = readWriteLock.getModifyUserId();
        int version = readWriteLock.getVersion();

        //初始化返回对象
        ReadWriteLockVO readWriteLockVO = new ReadWriteLockVO();
        readWriteLockVO.setGmtModified(readWriteLock.getGmtModified());
        readWriteLockVO.setLastKeepLockUserName(userService.getUserName(modifyUesrId));
        //任务版本是否保持一致
        if (relationLocalVersion != relationVersion) {
            //表示已经被提交了
            //提示任务版本已经更新不能在提交，是否保存到本地，或取消修改
            readWriteLockVO.setResult(TaskLockStatus.UPDATE_COMPLETED.getVal());
            return readWriteLockVO;
        } else if (modifyUesrId.equals(userId) && version == lockVersion) {
            readWriteLockVO.setResult(TaskLockStatus.TO_UPDATE.getVal());
            readWriteLockVO.setVersion(lockVersion);
            readWriteLockVO.setIsGetLock(true);
            return readWriteLockVO;
        } else {//提示其他人也正在编辑，是否取消修改
            readWriteLockVO.setResult(TaskLockStatus.TO_CONFIRM.getVal());
            return readWriteLockVO;
        }
    }

    public ReadWriteLock getReadWriteLock(Long projectId, Long relationId, String type) {
        ReadWriteLock readWriteLock = readWriteLockDao.getByProjectIdAndRelationIdAndType(projectId, relationId, type);
        if (readWriteLock == null) {
            throw new RdosDefineException(ErrorCode.LOCK_IS_NOT_EXISTS);
        }
        return readWriteLock;
    }

    private String uniteName(long project, long taskId, String type) {
        StringBuilder builder = new StringBuilder();
        builder.append(project).append(SPLIT).append(taskId).append(SPLIT).append(type);
        return builder.toString();
    }

    public void deleteByProjectId(Long projectId, Long userId) {
        readWriteLockDao.deleteByProjectId(projectId, userId);
    }

    public ReadWriteLockVO getDetail(Long tenantId, Long relationId, ReadWriteLockType type, Long userId, Long modifyUserId, Timestamp gmtModified) {
        ReadWriteLock readWriteLock = readWriteLockDao.getByProjectIdAndRelationIdAndType(tenantId, relationId, type.name());
        if (readWriteLock == null) {
            ReadWriteLockVO readWriteLockVO = new ReadWriteLockVO();
            readWriteLockVO.setLastKeepLockUserName(userService.getUserName(modifyUserId));
            readWriteLockVO.setIsGetLock(false);
            readWriteLockVO.setGmtModified(gmtModified);
            return readWriteLockVO;
        }
        ReadWriteLockVO readWriteLockVO = ReadWriteLockVO.toVO(readWriteLock);
        readWriteLockVO = this.isGetLock(readWriteLockVO, userId);
        readWriteLockVO.setLastKeepLockUserName(userService.getUserName(readWriteLockVO.getModifyUserId()));
        return readWriteLockVO;
    }

    public Map<Long, ReadWriteLockVO> getLocks(long projectId, ReadWriteLockType type, List<Long> relationIds, long userId, Map<Long, String> names) {
        List<ReadWriteLock> ls = readWriteLockDao.getLocksByIds(projectId, type.name(), relationIds);
        Map<Long, ReadWriteLock> records = ls.stream()
                .collect(Collectors.toMap(r -> r.getRelationId(), r -> r, (v1, v2) -> v2));
        Map<Long, ReadWriteLockVO> result = Maps.newHashMap();
        for (Long id : relationIds) {
            if (records.containsKey(id)) {
                ReadWriteLock readWriteLock = records.get(id);
                ReadWriteLockVO readWriteLockVO = ReadWriteLockVO.toVO(readWriteLock);
                readWriteLockVO = this.isGetLock(readWriteLockVO, userId);
                readWriteLockVO.setLastKeepLockUserName(getUserNameInMemory(names, readWriteLockVO.getModifyUserId()));
                result.put(id, readWriteLockVO);
            } else {
                ReadWriteLockVO readWriteLockVO = new ReadWriteLockVO();
                readWriteLockVO.setLastKeepLockUserName(null);
                readWriteLockVO.setIsGetLock(false);
                readWriteLockVO.setGmtModified(null);
                result.put(id, readWriteLockVO);
            }
        }
        return result;
    }

    private String getUserNameInMemory(Map<Long, String> names, Long userId) {
        if (names.containsKey(userId)) {
            return names.get(userId);
        } else {
            String name = userService.getUserName(userId);
            names.put(userId, name);
            return name;
        }
    }

    private ReadWriteLockVO forceUpdateLock(Long userId, ReadWriteLockType type, Long relationId, Long projectId) {
        ReadWriteLock readWriteLock = readWriteLockDao.getByProjectIdAndRelationIdAndType(projectId, relationId, type.name());
        if (readWriteLock != null) {
            readWriteLockDao.updateVersionAndModifyUserIdDefinitized(readWriteLock.getId(), userId);

            readWriteLock = readWriteLockDao.getOne(readWriteLock.getId());
            ReadWriteLockVO readWriteLockVO = ReadWriteLockVO.toVO(readWriteLock);
            String userName = userService.getUserName(readWriteLock.getModifyUserId());
            readWriteLockVO.setLastKeepLockUserName(userName);
            readWriteLockVO.setIsGetLock(true);
            return readWriteLockVO;
        } else {
            throw new RdosDefineException(ErrorCode.LOCK_IS_NOT_EXISTS);
        }

    }

    private ReadWriteLockVO insert(Long tenantId, Long projectId, Long fileId, String type, Long userId) {
        ReadWriteLock readWriteLock = new ReadWriteLock();
        readWriteLock.setTenantId(tenantId);
        readWriteLock.setLockName(uniteName(fileId, projectId, type));
        readWriteLock.setCreateUserId(userId);
        readWriteLock.setModifyUserId(userId);
        readWriteLock.setProjectId(projectId);
        readWriteLock.setRelationId(fileId);
        readWriteLock.setType(type);
        readWriteLockDao.insert(readWriteLock);
        ReadWriteLockVO readWriteLockVO = ReadWriteLockVO.toVO(readWriteLock);
        readWriteLockVO.setVersion(INIT_VERSION);
        readWriteLockVO.setGmtModified(Timestamp.valueOf(LocalDateTime.now()));
        readWriteLockVO.setIsGetLock(true);
        readWriteLockVO.setLastKeepLockUserName(userService.getUserName(userId));
        return readWriteLockVO;
    }

    /**
     * 利用回调函数实现锁机制
     *
     * @param projectId
     * @param relationId
     * @param type
     * @param userId
     * @param lockVersion
     * @param relationLocalVersion
     * @param relationVersion
     * @param callback
     * @param forceUpdate
     * @return
     */
    public ReadWriteLockVO dealWithLock(long projectId, long relationId, ReadWriteLockType type, Long userId,
                                        int lockVersion, int relationLocalVersion, int relationVersion,
                                        Callback<Void> callback, boolean forceUpdate) {

        ReadWriteLockVO readWriteLockVO = null;
        if (forceUpdate) {
            Integer update = (Integer) callback.submit(null);
            if (update != 1) {
                readWriteLockVO = getLockBasicInfo(projectId, relationId, type);
                readWriteLockVO.setResult(TaskLockStatus.UPDATE_COMPLETED.getVal());
                return readWriteLockVO;
            } else {
                readWriteLockVO = forceUpdateLock(userId, type, relationId, projectId);
                readWriteLockVO.setResult(TaskLockStatus.TO_UPDATE.getVal());
                return readWriteLockVO;
            }
        } else {
            readWriteLockVO = checkLock(
                    userId, projectId,
                    relationId, type,
                    lockVersion, relationLocalVersion,
                    relationVersion);
            if (readWriteLockVO.getResult() == TaskLockStatus.TO_UPDATE.getVal()) {
                Integer update = (Integer) callback.submit(null);
                if (update != 1) {
                    readWriteLockVO.setResult(TaskLockStatus.UPDATE_COMPLETED.getVal());
                }
            }
            return readWriteLockVO;
        }
    }

    private ReadWriteLockVO getLockBasicInfo(long projectId, long relationId, ReadWriteLockType type) {
        ReadWriteLock readWriteLock = readWriteLockDao.getByProjectIdAndRelationIdAndType(projectId, relationId, type.name());
        if (readWriteLock == null) {
            throw new RdosDefineException(ErrorCode.LOCK_IS_NOT_EXISTS);
        }
        ReadWriteLockVO readWriteLockVO = new ReadWriteLockVO();
        readWriteLockVO.setGmtModified(readWriteLock.getGmtModified());
        readWriteLockVO.setLastKeepLockUserName(userService.getUserName(readWriteLock.getModifyUserId()));
        return readWriteLockVO;
    }



    /**
     * 使用HashMap做个简单的<UserId,UserName>缓存
     */
    private String getUserNameMapCached(Long userId, HashMap<Long, String> userMap) {
        String userName = userMap.get(userId);
        if (userName == null) {
            userName = userService.getUserName(userId);
            userMap.put(userId, userName);
        }
        return userName;
    }
}
