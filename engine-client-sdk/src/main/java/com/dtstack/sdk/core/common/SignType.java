package com.dtstack.sdk.core.common;

/**
 * @author hejiaolong
 * @Date: 2018/12/18 10:10
 * @Description:
 */
public enum SignType {
    /**
     * 默认验签方式
     */
    DEFAULT,
    ;

    public static SignType getTypeByKeyword(String keyword){
        if (keyword == null || keyword.length() == 0 ){
            return null;
        }
        for (SignType type:values()){
            if (type.name().equalsIgnoreCase(keyword)){
                return type;
            }
        }
        return null;
    }

}
