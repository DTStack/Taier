package com.dtstack.batch.dto;

import lombok.Data;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/1/2
 */
@Data
public class BatchParamDTO {

    private Integer type;

    private String paramName;

    private String paramCommand;

    public BatchParamDTO(int type, String paramName, String paramCommand){
        this.type = type;
        this.paramName = paramName;
        this.paramCommand = paramCommand;
    }


}
