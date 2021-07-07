package com.dtstack.batch.vo;

import com.dtstack.batch.domain.BatchFunction;
import com.dtstack.batch.domain.User;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/1/4
 */
@Data
public class BatchFunctionVO extends BatchFunction {

    private static final Logger logger = LoggerFactory.getLogger(BatchFunction.class);

    public static BatchFunctionVO toVO(BatchFunction origin) {
        BatchFunctionVO vo = new BatchFunctionVO();
        try {
            BeanUtils.copyProperties(origin, vo);
        } catch (Exception e) {
            logger.error("", e);
        }
        return vo;
    }

    private User createUser;

    private User modifyUser;

    private Long resources;

    @Override
    public String toString() {
        return "BatchFunctionVO{" +
                "functionName=" + getName() +
                ",createUser=" + createUser.getUserName() +
                ", modifyUser=" + modifyUser.getUserName() +
                ", time=" + getGmtModified() +
                '}';
    }
}
