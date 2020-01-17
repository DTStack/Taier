package com.dtstack.task.server.vo;

import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
public class ChartDataVO {

    /**
     * x轴数据
     */
    protected ChartMetaDataVO x;

    /**
     * 类型，对应图上的多条线
     */
    protected ChartMetaDataVO type;

    /**
     * y轴数据
     */
    protected List<ChartMetaDataVO> y;

    public ChartMetaDataVO getX() {
        return x;
    }

    public void setX(ChartMetaDataVO x) {
        this.x = x;
    }

    public ChartMetaDataVO getType() {
        return type;
    }

    public void setType(ChartMetaDataVO type) {
        this.type = type;
    }

    public List<ChartMetaDataVO> getY() {
        return y;
    }

    public void setY(List<ChartMetaDataVO> y) {
        this.y = y;
    }
}
