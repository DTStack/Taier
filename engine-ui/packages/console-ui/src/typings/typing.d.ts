declare module 'typing' {

    /**
     * 数组或者字符串类型
     */
    export type numOrStr = number | string;

    /**
     * 响应结果
     */
    export interface Response<D = any, R = any> {
        /**
         * 状态码, 1 成功， 0 失败
         */
        code: number;
        message?: string;
        result?: R;
        data?: D;
        space?: number;
    }

    /**
     * 分页类型
     */
    export interface Pagination {
        current?: number;
        total: number;
        pageSize?: number;
    }

}
