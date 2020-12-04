package com.dtstack.engine.lineage.asserts;

/**
 * @Author tengzhen
 * @Description: 资产数据源类型枚举
 * @Date: Created in 4:56 下午 2020/11/26
 */
public enum AssertDataSourceTypeEnum {

    MYSQL(1, "Mysql"),
    ORACLE(2, "Oracle SQL"),
    SQL_SERVER(3, "SqlServer SQL"),
    HIVE2(7,"Hive2Server"),
    HIVE1(27, "Hive1Server"),
    TIDB(31, "TiDB SQL"),
    //手动添加的数据源
    CUSTOM(1000,"custom");

    private int typeCode;

    private String name;

    AssertDataSourceTypeEnum(int typeCode, String name) {
        this.typeCode = typeCode;
        this.name = name;
    }

    public static String getNameByTypeCode(Integer typeCode){

        if(typeCode==null){
            return null;
        }
        for (AssertDataSourceTypeEnum value : AssertDataSourceTypeEnum.values()) {
            if(typeCode.equals(value.typeCode)){
                return value.name;
            }
        }
        return null;
    }
}
