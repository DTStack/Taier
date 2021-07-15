package com.dtstack.batch.web.project.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 引擎信息
 *
 * @author Ruomu[ruomu@dtstack.com]
 * @Data 2021/1/8 11:43
 */
@Data
@ApiModel("引擎信息返回信息")
public class BatchEngineInfoResultVO {

    @ApiModelProperty(value = "jdbc URL", example = "jdbc:hive2://****")
    private String jdbcURL;

    @ApiModelProperty(value = "默认 FS", example = "hdfs://***")
    private String defaultFS;

    @ApiModelProperty(value = "jdbc URL", example = "jdbc:hive2://****")
    private String userName;

    @ApiModelProperty(value = "引擎类型", example = "GREENPLUM")
    private String engineTypeEnum;

    @ApiModelProperty(value = "引擎类型", example = "Greenplum")
    private String engineType;
}
