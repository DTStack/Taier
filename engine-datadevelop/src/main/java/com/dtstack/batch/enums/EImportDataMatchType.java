package com.dtstack.batch.enums;

/**
 * 导入数据的匹配类型
 * Date: 2017/9/12
 * Company: www.dtstack.com
 * @author xuchao
 */

public enum EImportDataMatchType {

    /**
     * 根据位置
     */
    BY_POS(0),

    /**
     * 根据名称
     */
    BY_NAME(1);

    Integer type;

    EImportDataMatchType(Integer type){
        this.type = type;
    }

    public Integer getType() {
        return type;
    }
}
