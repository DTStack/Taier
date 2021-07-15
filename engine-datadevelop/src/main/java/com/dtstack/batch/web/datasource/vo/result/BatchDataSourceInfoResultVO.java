package com.dtstack.batch.web.datasource.vo.result;

import com.dtstack.batch.web.table.vo.result.BatchTableColumnVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("数据同步-数据源 表信息")
public class BatchDataSourceInfoResultVO {

    @ApiModelProperty(value = "表所在数据库")
    private String db;

    @ApiModelProperty(value = "所有者")
    private String owner;

    @ApiModelProperty(value = "创建时间")
    private String createdTime;

    @ApiModelProperty(value = "最近访问时间")
    private String lastAccess;

    @ApiModelProperty(value = "创建者")
    private String createdBy;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "描述")
    private String comment;

    @ApiModelProperty(value = "分隔符，hive表属性")
    private String delim;

    @ApiModelProperty(value = "存储格式，hive表属性")
    private String storeType;

    @ApiModelProperty(value = "表路径")
    private String path;

    @ApiModelProperty(value = "表类型:EXTERNAL-外部表，MANAGED-内部表")
    private String externalOrManaged;

    @ApiModelProperty(value = "非分区字段")
    private List<BatchTableColumnVO> columns;

    @ApiModelProperty(value = "分区字段")
    private List<BatchTableColumnVO> partColumns;

    @ApiModelProperty(value = "是否是事务表")
    private Boolean isTransTable = false;

    @ApiModelProperty(value = " 是否是视图")
    private Boolean isView = false;

}
