package com.dtstack.batch.vo;

import com.dtstack.batch.domain.NotifyRecord;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
@Data
public class NotifyRecordVO extends NotifyRecord{

    private static final Logger logger = LoggerFactory.getLogger(NotifyRecordVO.class);

    private Integer readStatus;

    private NotifyVO notifyVO;

    private String content;

    private String gmtCreateFormat;

    public static NotifyRecordVO toVO(NotifyRecord notifyRecord) {
        NotifyRecordVO vo = new NotifyRecordVO();
        try {
            BeanUtils.copyProperties(notifyRecord, vo);
        } catch (Exception e) {
            logger.error("", e);
        }
        return vo;
    }

}
