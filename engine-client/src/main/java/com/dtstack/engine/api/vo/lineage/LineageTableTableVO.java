package com.dtstack.engine.api.vo.lineage;

/**
 * @author chener
 * @Classname LineageTableTableVO
 * @Description TODO
 * @Date 2020/10/30 10:09
 * @Created chener@dtstack.com
 */
public class LineageTableTableVO {

    private Long tenantId;

    private Integer appType;

    private Long inputTableId;

    private Long resultTableId;

    private Integer lineageSource;

    private LineageTableVO inputTableInfo;

    private LineageTableTableVO resultTableInfo;
}
