package com.dtstack.batch.domain;

import lombok.Data;

/**
 * 数据源和任务关联关系表
 * Date: 2017/8/22
 * Company: www.dtstack.com
 * @author xuchao
 */
@Data
public class BatchDataSourceTaskRef extends TenantProjectEntity {

    private Long dataSourceId;

    private Long taskId;

}
