package com.dtstack.batch.vo;

import lombok.Data;

/**
 * @author yuebai
 * @date 2019-06-12
 */
@Data
public class BatchTableTypeVo {
    private Integer value;

    private String name;

    public BatchTableTypeVo(Integer value, String name) {
        this.value = value;
        this.name = name;
    }
}
