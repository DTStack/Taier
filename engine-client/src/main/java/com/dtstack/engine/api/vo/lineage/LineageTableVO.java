package com.dtstack.engine.api.vo.lineage;

/**
 * @author chener
 * @Classname LineageTableInfoVO
 * @Description TODO
 * @Date 2020/10/30 10:25
 * @Created chener@dtstack.com
 */
public class LineageTableVO {

    private Long tableId;

    private Long tenantId;

    private String tableName;

    private Long dataSourceId;

    private LineageDataSourceVO dataSourceVO;
}
