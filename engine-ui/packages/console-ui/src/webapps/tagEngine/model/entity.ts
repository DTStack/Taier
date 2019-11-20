/**
 * 实体维度
 */
export interface IDimension {
    id: number;
    name: string;
    /**
     * 维度的行下标
     */
    rowIndex?: number;
}

/**
 * 实体
 */
export interface IEntity {
    id: number;
    name: string;
    dataSource?: number;
    table?: number;
    description?: string;
    primaryKey?: string;
}
