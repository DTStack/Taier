package com.dtstack.taier.develop.enums.develop;

/**
 * @author huoyun
 * @date 2021/4/13 2:37 下午
 * @company: www.dtstack.com
 */
public enum SlotConfigEnum {
    /**
     * 选择已有的slot
     */
    USE_EXISTED(1),

    /**
     * 新建slot
     */
    CREATE_SLOT(2);

    SlotConfigEnum(Integer slotConfig) {
        this.slotConfig = slotConfig;
    }

    private Integer slotConfig;

    public Integer getSlotConfig() {
        return slotConfig;
    }
}
