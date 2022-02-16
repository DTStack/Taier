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

package com.dtstack.taier.develop.dto.devlop;

import com.dtstack.taier.dao.domain.BatchReadWriteLock;
import org.springframework.beans.BeanUtils;

public class ReadWriteLockVO extends BatchReadWriteLock {

    private String lastKeepLockUserName;    //上一个持有锁的用户名

    private Integer result = 0;  //检查结果

    public static ReadWriteLockVO toVO(BatchReadWriteLock readWriteLock) {
        ReadWriteLockVO readWriteLockVO = new ReadWriteLockVO();
        BeanUtils.copyProperties(readWriteLock,readWriteLockVO);
        return readWriteLockVO;
    }

    public ReadWriteLockVO() {
    }

    public String getLastKeepLockUserName() {
        return lastKeepLockUserName;
    }

    public void setLastKeepLockUserName(String lastKeepLockUserName) {
        this.lastKeepLockUserName = lastKeepLockUserName;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }
}
