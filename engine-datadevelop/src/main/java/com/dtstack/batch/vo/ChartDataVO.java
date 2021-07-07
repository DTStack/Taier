package com.dtstack.batch.vo;

import com.dtstack.batch.dto.ChartMetaDataDTO;
import lombok.Data;

import java.util.List;

/**
 * company: www.dtstack.com
 * author: jiangbo
 * create: 2017/10/17
 */
@Data
public class ChartDataVO {

    /**
     * x轴数据
     */
    private ChartMetaDataDTO x;

    /**
     * 类型，对应图上的多条线
     */
    private ChartMetaDataDTO type;

    /**
     * y轴数据
     */
    private List<ChartMetaDataDTO> y;

}
