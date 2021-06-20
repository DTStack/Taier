package com.dtstack.pubsvc.dao.bo.query;

import com.dtstack.pubsvc.dao.bo.DaoPageParam;
import lombok.Data;

import java.util.List;

/**
 *
 * @description:
 * @author: liuxx
 * @date: 2021/3/18
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
