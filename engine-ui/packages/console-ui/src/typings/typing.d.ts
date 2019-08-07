declare module 'typing' {

    /**
     * 数组或者字符串类型
     */
    export type numOrStr = number | string;

    /**
     * 分页类型
     */
    export interface Pagination {
        current?: number;
        total?: number;
        pageSize?: number;
    }

}
