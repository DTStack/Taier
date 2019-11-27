export interface ISorter {
    /**
     * 排序字段
     */
    fields?: string;
    /**
     * 是否升序
     */
    asc?: boolean;
}

export interface IReqParams {
    /**
     * 搜索字符串
     */
    search?: string;
    /**
     * 当前页
     */
    current?: number;
    /**
     * 分页大小
     */
    size?: number;
    /**
     * 排序，默认降序
     */
    orders?: ISorter;
};

/**
 * 请求响应数据接口
 */
export interface IResponse<T = any> {
    code: number;
    message: string;
    space: number;
    data: T;
};
