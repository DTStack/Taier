package com.dtstack.engine.datasource.param.datasource.api;

import com.dtstack.engine.datasource.param.PubSvcBaseParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
@Data
@ApiModel("第三方创建或迁移数据源参数类")
public class CreateDsParam extends PubSvcBaseParam {

    @ApiModelProperty(value = "来源产品type", example = "1-离线, 2-数据质量", required = true)
    private Integer appType;

    @ApiModelProperty(value = "数据源type", notes = "映射 com.dtstack.pubsvc.common.enums.datasource.DataSourceTypeEnum val值", required = true)
    private Integer type;

    @ApiModelProperty(value = "数据源名称", required = true)
    private String dataName;

    @ApiModelProperty("数据源简介")
    private String dataDesc;

    @ApiModelProperty(value = "数据源表单填写数据JsonString, 默认以Base64密文传输", required = true)
    private String dataJson;

//    @ApiModelProperty("数据源表单填写数据Json参数")
//    private JSONObject dataJson;

    @ApiModelProperty(value = "是否为默认数据源", required = true)
    private Integer isMeta;

    @ApiModelProperty("连接状态 0-连接失败 1-成功")
    private Integer status;

    @ApiModelProperty(value = "创建数据源的租户主键id", required = true)
    private Long dsTenantId;

    @ApiModelProperty(value = "创建数据源的dtuic 租户id", required = true)
    private Long dsDtuicTenantId;

    @ApiModelProperty("创建时间")
    private Date gmtCreate;

    @ApiModelProperty("修改时间")
    private Date gmtModified;

    @ApiModelProperty("创建用户Id")
    private Long createUserId;

    @ApiModelProperty("修改用户Id")
    private Long modifyUserId;

}
