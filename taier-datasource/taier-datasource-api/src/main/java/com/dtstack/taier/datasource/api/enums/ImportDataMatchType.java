package com.dtstack.taier.datasource.api.enums;

import com.dtstack.taier.datasource.api.exception.SourceException;

import java.util.Objects;

/**
 * 导入数据的匹配类型
 * Date: 2017/9/12
 * Company: www.dtstack.com
 *
 * @author xuchao
 */
public enum ImportDataMatchType {

    /**
     * 根据位置
     */
    BY_POS(0),

    /**
     * 根据名称
     */
    BY_NAME(1);

    Integer type;

    ImportDataMatchType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

    public static ImportDataMatchType getMatchType(Integer type) {
        for (ImportDataMatchType value : ImportDataMatchType.values()) {
            if (Objects.equals(value.getType(), type)) {
                return value;
            }
        }

        throw new SourceException("can't find valid match type");
    }
}
