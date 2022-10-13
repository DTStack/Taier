package com.dtstack.taier.develop.vo.develop.result;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author zhiChen
 * @date 2021/1/7 20:13
 * @see TaskCheckpointTransfer
 */
public class GetCheckpointListResultVO {

    @ApiModelProperty(value = "间隔符", example = "_")
    private static String SPLIT = "_";

    @ApiModelProperty(value = "ID", example = "rdos_stream_task_checkpoint的记录id + checkpoint内容的具体id")
    private String id;

    @ApiModelProperty(value = "时间", example = "213")
    private Long time;

    /**
     * 其实应该服务器重新查询,太麻烦了暂时直接让客户端回传
     */
    @ApiModelProperty(value = "外部路径", example = "213")
    private String externalPath;

}
