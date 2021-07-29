package com.dtstack.batch.dto;

import lombok.Data;

import java.util.List;

/**
 * @author jiangbo
 * @date 2018/7/19 9:44
 */
@Data
public class BatchJobJobDTO {

    private String jobKey;

    private Integer level;

    private List<BatchJobJobDTO> children;

}
