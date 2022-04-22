package com.dtstack.taier.develop.enums.develop;

/**
 * 时间单位枚举
 */
public enum TimeUnit {

    /**
     * 秒
     */
    SECOND(0),

    /**
     * 分
     */
    MINUTE(1);

    private Integer type;

    public Integer getType() {
        return type;
    }

    TimeUnit(Integer type) {
        this.type = type;
    }

    /**
     * 分，秒转化毫秒
     *
     * @return
     */
    public static Long transformMillisecond(Long time, Integer timeUnit) {
        if (TimeUnit.SECOND.getType().equals(timeUnit)) {
            time = time * 1000;
        } else if (TimeUnit.MINUTE.getType().equals(timeUnit)) {
            time = time * 1000 * 60;
        }
        return time;
    }
}
