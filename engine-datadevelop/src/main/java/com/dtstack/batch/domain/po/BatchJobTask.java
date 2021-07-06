package com.dtstack.batch.domain.po;


import com.dtstack.batch.domain.BatchTaskShade;
import lombok.Data;

import java.sql.Timestamp;

/**
 * @author jiangbo
 * @date 2018/6/21 16:49
 */
@Data
public class BatchJobTask extends BatchTaskShade {

    private Timestamp execStartTime;

    private String engineLog;
}
