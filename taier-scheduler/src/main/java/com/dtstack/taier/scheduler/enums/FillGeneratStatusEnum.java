package com.dtstack.taier.scheduler.enums;

/**
 * @Auther: dazhi
 * @Date: 2021/9/10 10:49 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public enum FillGeneratStatusEnum {

    /**
     * 补数据生成状态：0默认值，按照原来的接口逻辑走。1 表示正在生成，2 完成生成补数据实例，3生成补数据失败
     */
    DEFAULT_VALUE(0, "默认值，按照原来的接口逻辑走"),
    REALLY_GENERATED(1, "实例正在生成中..."),
    FILL_FINISH(2, "完成生成补数据实例"),
    FILL_FAIL(3, "生成补数据失败"),
    ;

    private final Integer type;

    private final String name;

    FillGeneratStatusEnum(Integer type, String name) {
        this.type = type;
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
