package com.dtstack.batch.dto;

import lombok.Data;

import java.util.List;

/**
 * 图表元数据类
 * company: www.dtstack.com
 * author: jiangbo
 * create: 2017/10/17
 */
@Data
public class ChartMetaDataDTO {
    private String name;
    private List<Object> data;

    public ChartMetaDataDTO(String name, List<Object> data) {
        this.name = name;
        this.data = data;
    }

}
