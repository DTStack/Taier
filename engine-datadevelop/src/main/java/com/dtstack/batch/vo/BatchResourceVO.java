package com.dtstack.batch.vo;

import com.dtstack.batch.domain.BatchResource;
import com.dtstack.engine.api.domain.User;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/1/4
 */
@Slf4j
@Data
public class BatchResourceVO extends BatchResource {

    public static BatchResourceVO toVO(BatchResource origin) {
        BatchResourceVO vo = new BatchResourceVO();
        try {
            BeanUtils.copyProperties(origin, vo);
        } catch (Exception e) {
            log.error("", e);
        }
        return vo;
    }

    private User createUser;

    private User modifyUser;
}
