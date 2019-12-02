import { INode } from '../components/relationGraph';

export interface IRelationEntity<T = any> extends INode {
    /**
     * 实体维度ID
     */
    attrId?: number;
    /**
     * 实体维度名称
     */
    attrName?: string;
    /**
     * 数据源表名
     */
    dataSourceTable?: string;
     /**
     * 实体维度中文名称
     */
    attrNameCN?: string;
}

export interface IRelation {
    id?: number;
    relationName?: string;
    relationDesc?: string;
    dataSourceId?: number | string;
    createAt?: Date | number | string;
    updateAt?: Date | number | string;
    createBy?: string;
    relationJson?: any;
    /**
     * 实体名称
     */
    entityNames?: string;
    /**
     * 使用状况
     */
    usedCount?: number;
    /**
     * 关联实体
     */
    relationCollection?: IRelationEntity[];
}
