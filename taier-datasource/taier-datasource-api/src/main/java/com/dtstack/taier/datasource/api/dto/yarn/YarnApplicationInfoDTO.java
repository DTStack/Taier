package com.dtstack.taier.datasource.api.dto.yarn;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * yarn app info DTO
 *
 * @author ：wangchuan
 * date：Created in 上午9:37 2022/3/17
 * company: www.dtstack.com
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class YarnApplicationInfoDTO {

    /**
     * 任务名称
     */
    private String name;

    /**
     * 任务applicationId
     */
    private String applicationId;

    /**
     * 任务状态
     */
    private YarnApplicationStatus status;

    /**
     * 任务开始时间
     */
    private Date startTime;

    /**
     * 任务结束时间
     */
    private Date finishTime;
}
