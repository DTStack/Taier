package com.dtstack.engine.datasource.vo.datasource;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.datasource.vo.datasource.domain.User;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
@Data
@ApiModel("数据源视图对象")
public class DataSourceVO {
    /**
     * 数据源 ID
     */
    private Long id = 0L;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * dtuic 租户id
     */
    private Long dtuicTenantId;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 修改时间
     */
    private Date gmtModified;

    /**
     * 租户 ID
     */
    private Long tenantId;

    /**
     * 项目 ID
     */
    private Long projectId;

    /**
     * 数据源名称
     */
    private String dataName;

    /**
     * 数据源描述
     */
    private String dataDesc;

    /**
     * 数据源类型
     */
    private Integer type;

    /**
     * 数据源类型
     */
    private String dataType;

    /**数据源类型编码**/
    private Integer dataTypeCode;

    /**
     * 数据源版本
     */
    private String dataVersion;

    /**
     * 是否被使用
     */
    private Integer active;

    /**
     * 是否有效
     */
    private Integer linkState;

    /**
     * 修改人 ID
     */
    private Long modifyUserId;

    /**
     * 创建人 ID
     */
    private Long createUserId;

    /**
     * 修改人
     */
    private User modifyUser;

    /**
     * 数据源相关信息
     */
    private JSONObject dataJson;

    /**
     * 数据源加密字符
     */
    private String dataJsonString;

    /**
     * Kerberos 信息
     */
    private Map<String, Object> kerberosConfig;

    /**
     * 本地 Kerberos 地址
     */
    private String localKerberosConf;

    /**
     * 授权产品编码 可为空
     */
    private List<Integer> appTypeList;

    /**
     * 数组字符串
     */
    private String appTypeListString;

    /**
     * 是否为默认数据源 0-否 1-是
     */
    private Integer isMeta;

    /**数据库名称**/
    private String schemaName;


}
