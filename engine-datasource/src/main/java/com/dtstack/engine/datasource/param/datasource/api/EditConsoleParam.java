package com.dtstack.engine.datasource.param.datasource.api;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.datasource.param.PubSvcBaseParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
@Data
@ApiModel("控制台修改数据源信息参数")
public class EditConsoleParam extends PubSvcBaseParam implements Serializable {

    @ApiModelProperty(value = "租户dtuic id数组", required = true)
    private List<Long> dsDtuicTenantIdList;

    @ApiModelProperty(value = "数据源类型type", required = true)
    private Integer type;

    @ApiModelProperty(value = "修改的jdbcUrl")
    private String jdbcUrl;

    @ApiModelProperty(value = "修改的用户名")
    private String username;

    @ApiModelProperty(value = "修改的密码")
    private String password;

    @ApiModelProperty(value = "kerberos配置文件参数")
    private JSONObject kerberosConfig;

    @ApiModelProperty(value = "高可用配置")
    private JSONObject hdfsConfig;

    @ApiModelProperty(value = "sftp配置文件参数")
    private JSONObject sftpConf;

    @ApiModelProperty("数据源版本 (预留字段, 后续改造)")
    private String dataVersion;



}
