package com.dtstack.batch.enums;

/**
 * @author chener
 * @Classname ResourceType
 * @Description 用于发布时，关联资源表 rdos_batch_test_produce_resource 中类型支持函数
 * @Date 2020/5/28 15:33
 * @Created chener@dtstack.com
 */
public enum ResourceType {
    /**
     * 资源
     */
    RESOURCE(0),
    /**
     * 函数
     */
    FUNCTION(1),
    ;

    private int type;

    public int getType() {
        return type;
    }

    ResourceType(int typeCode) {
        this.type = typeCode;
    }
    public static ResourceType getByTypeCode(int code){
        for (ResourceType type:values()){
            if (type.type == code){
                return type;
            }
        }
        return null;
    }
}
