export interface IDataSource {
    /**
     * 数据源 ID
     */
    id: number;
    /**
     * 数据源类型
     */
    type: number;
    /**
     * 数据JSON
     */
    dataJSON: object;
    /**
     * 数据源名称
     */
    dataName: string;
    /**
     * 数据描述
     */
    dataDesc: string;
    /**
     * 应用状态
     */
    active: number;
    /**
     * 链接状态
     */
    linkState: number;
};
