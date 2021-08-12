package com.dtstack.batch.domain;

import com.dtstack.engine.api.domain.BaseEntity;
import lombok.Data;

/**
 * Reason:
 * Date: 2017/6/7
 * Company: www.dtstack.com
 * @author xuchao
 */
@Data
public class BatchTaskParam extends BaseEntity {
    public BatchTaskParam() {
    }

    public BatchTaskParam(Integer type, String paramName) {
        this.type = type;
        this.paramName = paramName;
    }

    private Long taskId;

    private Integer type;

    private String paramName;

    private String paramCommand;

}
