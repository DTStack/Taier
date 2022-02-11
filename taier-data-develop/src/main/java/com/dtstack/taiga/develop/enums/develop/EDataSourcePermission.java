package com.dtstack.taiga.develop.enums.develop;

/**
 * 数据源使用类型
 * Date: 2017/9/1
 * Company: www.dtstack.com
 * @ahthor xuchao
 */

public enum  EDataSourcePermission {

    READ(0x001),WRITE(0x010), READ_WRITE(0x011);

    private int type;

    EDataSourcePermission(int type){
        this.type = type;
    }

    public int getType() {
        return type;
    }
}