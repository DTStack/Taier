package com.dtstack.engine.datasource.vo.datasource;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
@Data
@ApiModel("当前租户支持的数据源类型列表")
public class DsTypeListVO {

    @ApiModelProperty(value = "数据源类型", example = "如Mysql, Oracle, Hive")
    private String dataType;

}
