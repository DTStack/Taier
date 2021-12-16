package com.dtstack.engine.common.enums.base;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : hanbeikai
 * Date: 2021/12/15 11:02 下午
 * Description: No Description
 */
public enum AppType {
    /**
     * 离线计算
     */
    RDOS(1),
    /**
     * 数据质量
     */
    DQ(2),
    /**
     * 数据api
     */
    API(3),
    /**
     * 标签引擎
     */
    TAG(4),
    /**
     *
     */
    MAP(5),
    /**
     * 控制台
     */
    CONSOLE(6),
    /**
     * 流计算
     */
    STREAM(7),
    /**
     * 数据科学
     */
    DATASCIENCE(8),
    /**
     * 数据资产
     */
    DATAASSETS(9),
    /**
     * 业务中心
     */
    BIZCENTER(10),
    /**
     * 消息中心
     */
    MESSAGECENTER(11),
    /**
     * 数据同步
     */
    DATASYNC(12)
    ;

    private final int type;

    AppType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public static AppType getValue(int value) {
        AppType[] values = AppType.values();
        for (AppType appType : values) {
            if (appType.getType() == value) {
                return appType;
            }
        }
        return null;
    }
}
