package com.dtstack.batch.vo;

import lombok.Data;

@Data
public class HiveTableCatalogueVO extends CatalogueVO {
    /**
     * 项目mingc
     */
    private String project;

    /**
     * 项目别名
     */
    private String projectAlias;

    /**
     * 负责人
     */
    private String chargeUser;

    /**
     * 描述
     */
    private String tableDesc;

    /**
     * 生命周期，单位：day
     */
    private Integer lifeDay;
}
