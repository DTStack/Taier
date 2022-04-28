package com.dtstack.taier.common.enums;


import com.dtstack.taier.common.exception.RdosDefineException;
import com.google.common.collect.Lists;

import java.util.Comparator;
import java.util.List;

/**
 * 时间达到多少往前进位
 *
 * @author ：wangchuan
 * date：Created in 下午2:10 2021/4/19
 * company: www.dtstack.com
 */
public enum ETimeCarry {

    /**
     * 秒
     */
    SECOND("s", 60, 1, "m", 1),

    /**
     * 分钟
     */
    MINUTE("m", 60, 2, "h", 60),

    /**
     * 小时
     */
    HOUR("h", 24, 3, "d", 60 * 60),

    /**
     * 天
     */
    DAY("d", 7, 4, "w", 24 * 60 * 60),

    /**
     * 星期
     */
    WEEK("w", 52, 5, "y", 7 * 24 * 60 * 60),

    /**
     * 年
     */
    YEAR("y", -1, 6, null, 365 * 24 * 60 * 60);

    // 时间类型
    private final String type;

    // 达到多少后往前进位
    private final Integer carry;

    // 时间类型位置标志 s -> m -> h -> d -> w -> y 从小到大
    private final Integer flag;

    // 进位后的时间类型
    private final String carryType;

    // 转换时间单位为秒
    private final Integer convertToSecond;

    ETimeCarry(String type, Integer carry, Integer flag, String carryType, Integer convertToSecond) {
        this.type = type;
        this.carry = carry;
        this.flag = flag;
        this.carryType = carryType;
        this.convertToSecond = convertToSecond;
    }

    public String getType() {
        return type;
    }

    public Integer getCarry() {
        return carry;
    }

    public Integer getFlag() {
        return flag;
    }

    public String getCarryType() {
        return carryType;
    }

    public Integer getConvertToSecond() {
        return convertToSecond;
    }

    public static ETimeCarry getTimeCarryByType(String type) {
        for (ETimeCarry timeCarry : values()) {
            if (timeCarry.type.equals(type)) {
                return timeCarry;
            }
        }
        throw new RdosDefineException(String.format("timeCarry not found ,type: %s", type));
    }

    public static List<ETimeCarry> getSortETimeCarryList() {
        List<ETimeCarry> timeCarryList = Lists.newArrayList(ETimeCarry.values());
        // 按照 flag 从小到大进行排列
        timeCarryList.sort(Comparator.comparingInt(ETimeCarry::getFlag));
        return timeCarryList;
    }
}
