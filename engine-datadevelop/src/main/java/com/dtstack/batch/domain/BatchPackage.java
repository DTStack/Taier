package com.dtstack.batch.domain;

import com.dtstack.engine.domain.TenantProjectEntity;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class BatchPackage extends TenantProjectEntity {

    private String name;

    private String comment;

    private Long createUserId;

    private Long publishUserId;
    /**
     * 发布状态：0-待发布，1-成功，2-失败
     */
    private Integer status;

    private String log;
    /**
     * 导出0 导入1
     */
    private Integer packageType;

    /**
     * 如果式导入 则为导入的压缩包的path
     */
    private String path;

    /**
     * 用于后期判断zip的导入时间是否过期
     */
    private Timestamp pathTime;

}
