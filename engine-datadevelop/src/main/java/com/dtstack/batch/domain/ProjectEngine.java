package com.dtstack.batch.domain;

import lombok.Data;

/**
 * 项目引擎类型关联表
 * Date: 2019/6/1
 * Company: www.dtstack.com
 * @author xuchao
 */
@Data
public class ProjectEngine extends BaseEntity {

    private Long projectId;

    private Long tenantId;

    private Integer engineType;

    private Integer status;

    private String engineIdentity;

    /**
     * 创建人id
     */
    private Long createUserId;

    /**
     * 修改人id
     */
    private Long modifyUserId;

}
