package com.dtstack.taier.common.enums;

/**
 * @author: 小北(xiaobei @ dtstack.com)
 * @description:
 * @create: 2021-12-15 23:41
 **/
public enum ResourceType {

    /**
     * 其他
     */
    OTHER(0),
    /**
     * 资源类型jar
     */
    JAR(1),
    /**
     * 资源类型python
     */
    PYTHON(2),

    /**
     * zip
     */
    ZIP(3),

    /**
     * egg
     */
    EGG(4);

    private int type;

    ResourceType(int type){
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
