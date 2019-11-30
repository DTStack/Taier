import { INode } from '../components/relationGraph';

export interface IRelationEntity<T = any> extends INode {
    name?: string;
    relationId?: number;
}

export interface IRelation {
    id?: number;
    relationName?: string;
    relationDesc?: string;
    dataSourceId?: number;
    createAt?: Date | number | string;
    updateAt?: Date | number | string;
    createBy?: string;
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
