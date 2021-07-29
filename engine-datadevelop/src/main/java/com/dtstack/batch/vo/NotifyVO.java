package com.dtstack.batch.vo;

import com.dtstack.batch.domain.Notify;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.util.List;

@Data
public class NotifyVO {

    private static final Logger logger = LoggerFactory.getLogger(NotifyVO.class);

    private List<Long> receivers;

    private List<Integer> sendTypes;

    public static NotifyVO toVO(Notify notify) {
        NotifyVO vo = new NotifyVO();
        try {
            BeanUtils.copyProperties(notify, vo);
        } catch (Exception e) {
            logger.error("", e);
        }
        return vo;
    }
}
