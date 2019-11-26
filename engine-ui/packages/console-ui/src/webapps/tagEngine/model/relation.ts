import { INode } from '../components/relationGraph';

export interface IRelationEntity<T = any> extends INode {
    name?: string;
    relationId?: number;
}

export interface IRelation {
    id?: number;
    name?: string;
    description?: string;
    /**
     * 关联实体
     */
    relationEntities: IRelationEntity[];
}
