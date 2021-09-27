package com.dtstack.batch.domain;

import com.dtstack.engine.domain.TenantProjectEntity;
import lombok.Data;

/**
 * @author sanyue
 */
@Data
public class NotifyRecord extends TenantProjectEntity {
    /**
     * 通知id
     */
    private Long notifyId;
    /**
     * 内容文本
     */
    private Long contentId;
    /**
     * 任务状态
     */
    private Integer status;
    /**
     * 批处理调度的时间
     */
    private String cycTime;

}
