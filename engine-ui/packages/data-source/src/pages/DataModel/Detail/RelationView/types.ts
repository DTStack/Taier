import { FieldColumn, JoinType } from '../../types';
export enum EnumNodeType {
  TABLE_NAME = 'TABLE_NAME',
  COLUMN_NAME = 'COLUMN_NAME',
  PARTITION_COLUMN = 'PARTITION_COLUMN',
}

export interface IRelationTreeJoinItem {
  tableAlias: string;
  tableName: string;
  schema: string;
  columnType: string;
  columnName: string;
}

export interface IRelationTree {
  tableName: string;
  columns: Partial<FieldColumn>[];
  tableAlias: string;
  joinInfo: null | {
    joinType: JoinType;
    joinPairs: {
      leftValue: IRelationTreeJoinItem;
      rightValue: IRelationTreeJoinItem;
    }[];
  };
  children: IRelationTree[];
  _tableType: EnumTableType;
}

export type LoopCallback = (item: IRelationTree) => void;

export enum EnumTableType {
  PRIMARY = 'PRIMARY',
  RELATION = 'RELATION',
}
