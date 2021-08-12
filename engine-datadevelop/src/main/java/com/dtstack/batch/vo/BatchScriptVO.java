package com.dtstack.batch.vo;

import com.dtstack.batch.domain.BatchScript;
import com.dtstack.engine.api.domain.User;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/11/9
 */
@Data
public class BatchScriptVO extends BatchScript {

    private static final Logger logger = LoggerFactory.getLogger(BatchScriptVO.class);

    public static BatchScriptVO toVO(BatchScript origin) {
        BatchScriptVO vo = new BatchScriptVO();
        try {
            BeanUtils.copyProperties(origin, vo);
        } catch (Exception e) {
            logger.error("", e);
        }
        return vo;
    }

    private User createUser;
    private User modifyUser;
    private ReadWriteLockVO readWriteLockVO;
    private Integer lockVersion = 0;
    private Long userId = 0L;

    private Boolean forceUpdate = false;

}
