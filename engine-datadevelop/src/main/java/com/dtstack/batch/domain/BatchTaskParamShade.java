package com.dtstack.batch.domain;


import lombok.Data;

/**
 * Reason:
 * Date: 2017/6/7
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
 */
@Data
public class BatchTaskParamShade extends BaseEntity {

    private Long taskId;

    private Integer type;

    private String paramName;

    private String paramCommand;

}
