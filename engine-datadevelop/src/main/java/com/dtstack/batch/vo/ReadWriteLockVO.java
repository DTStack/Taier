package com.dtstack.batch.vo;

import com.dtstack.batch.domain.ReadWriteLock;
import lombok.Data;
import org.springframework.beans.BeanUtils;
@Data
public class ReadWriteLockVO extends ReadWriteLock {

    private String lastKeepLockUserName;    //上一个持有锁的用户名

    private Integer result = 0;  //检查结果

    private Boolean isGetLock = false;      //是否持有锁

    public static ReadWriteLockVO toVO(ReadWriteLock readWriteLock) {
        ReadWriteLockVO readWriteLockVO = new ReadWriteLockVO();
        BeanUtils.copyProperties(readWriteLock,readWriteLockVO);
        return readWriteLockVO;
    }

    public ReadWriteLockVO() {
    }

    public boolean isGetLock() {
        return isGetLock;
    }

    public void setIsGetLock(boolean getLock) {
        isGetLock = getLock;
    }
}
