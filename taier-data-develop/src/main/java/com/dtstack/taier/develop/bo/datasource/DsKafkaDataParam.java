package com.dtstack.taier.develop.bo.datasource;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 数据预览结果
 */
@ApiModel("数据预览结果")
public class DsKafkaDataParam {


    @ApiModelProperty(value = "数据预览模式 1.earliest 2.latest")
    private String previewModel;

    @ApiModelProperty(value = "数据源Id")
    private Long sourceId;

    @ApiModelProperty(value = "kafka topic 名称")
    private String topic;

    public String getPreviewModel() {
        return previewModel;
    }

    public void setPreviewModel(String previewModel) {
        this.previewModel = previewModel;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }


}
