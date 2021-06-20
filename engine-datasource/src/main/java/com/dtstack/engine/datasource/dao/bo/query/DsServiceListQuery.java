package com.dtstack.engine.datasource.dao.bo.query;

import com.dtstack.engine.datasource.dao.bo.DaoPageParam;
import lombok.Data;

import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
@Data
public class DsServiceListQuery extends DaoPageParam {

    private Integer appType;

    private List<String> dataTypeList;

    private String search;

    /**
     * 租户主键id
     */
    private Long dsTenantId;

    /**
     * 租户 dtuic id
     */
    private Long dsDtuicTenantId;

    /**项目id**/
    private Long projectId;

    /**数据源类型编码列表**/
    private List<Integer> dataTypeCodeList;

}
