declare const utils:{
    /**
     * 转换排序字段
     * @param order 
     */
    exchangeOrder(order?:'ascend'|'descend'):'asc'|'desc'|void;
    /**
     * 多函数排序，匹配到0为止
     * @param sortArr 要排序的数组
     * @param compareFunctions 比较函数
     */
    sortByCompareFunctions(sortArr:Array<any>,...compareFunctions:Function[]):void;
    /**
     * json格式化
     * @param text 格式化内容
     * @param space 格式化占位符数量
     */
    jsonFormat(text:string,space?:number):string|null;
}
export default utils;