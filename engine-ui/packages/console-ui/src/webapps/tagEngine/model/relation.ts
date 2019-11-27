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
    /**
     * 关联实体
     */
    relationCollection: IRelationEntity[];
}
