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
    entityName: string;
    dataSourceId?: number;
    dataSourceTable?: string;
    entityPrimaryKey?: string;
    entityDesc?: string;
    tagParamList?: any[];
}
