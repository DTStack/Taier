package com.dtstack.batch.dto;

import com.dtstack.batch.domain.BaseEntity;
import lombok.Data;

/**
 * @Auther: 尘二(chener @ dtstack.com)
 * @Date: 2018/12/12 10:47
 * @Description:
 */
@Data
public class BatchDataSourceTaskDto extends BaseEntity {

    private Long sourceId;

    private String taskName;
}
