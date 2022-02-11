package com.dtstack.taiga.develop.vo.datasource;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *
 * @author 全阅
 * @Description: 数据源下拉列表类
 * @Date: 2021/3/9 14:15
 */
@Data
@ApiModel("当前租户支持的数据源类型列表")
public class DsTypeListVO {

    @ApiModelProperty(value = "数据源类型", example = "如Mysql, Oracle, Hive")
    private String dataType;

}
