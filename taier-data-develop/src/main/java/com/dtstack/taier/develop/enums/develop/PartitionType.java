package com.dtstack.taier.develop.enums.develop;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 15:34 2019-07-24
 * @Description：分区粒度
 */
public enum PartitionType {

    /**
     * 分区类型 小时
     */
    HOUR(0, "HOUR"),

    /**
     * 分区类型 天
     */
    DAY(1, "DAY");

    /**
     * 分区类型
     */
    int type;

    /**
     * 名称
     */
    String name;

    PartitionType(int type, String name){
        this.type = type;
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public static PartitionType fromTypeValue(int type){
        for(PartitionType partitionType : PartitionType.values()){
            if(type == partitionType.type){
                return partitionType;
            }
        }
        return null;
    }
}
