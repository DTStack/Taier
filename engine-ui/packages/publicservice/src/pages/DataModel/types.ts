export interface IModelData {
  id: number;
  modelName: string;
  modelEnName: string;
  dataSourceType: number;
  datasourceUrl?: string;
  modelStatus: number;
  creator: string;
  createTime: string;
  updateTime: string;
  remark: string;
}

// 1 -> 增量; 2 -> 全量;
enum UPDATA_TYPE {
  INCREACEMENT_IPDATE = 1,
  FULL_UPDATE = 2,
}

interface JoinKeyItem {
  schema: string;
  tableName: string;
  columnName: string;
  columnType: string;
  columnComment: string;
}

// 关联键信息
export interface JoinKey {
  leftValue: JoinKeyItem;
  rightValue: JoinKeyItem;
}

// 表关联方式
export enum JoinType {
  LEFT_JOIN = 1,
  RIGHT_JOIN = 2,
  INNER_JOIN = 3,
  // OUTTER_JOIN,
}

export interface FieldColumn {
  id?: number;
  columnComment: string;
  columnName: string;
  columnType: string;
  schema: string;
  tableName: string;
  dimension?: boolean;
  metric?: boolean;
}

// 表关联信息
export interface TableJoinInfo {
  id?: number;
  leftTable: string;
  joinType: JoinType;
  schema: string;
  table: string;
  tableAlias: string;
  updateType: UPDATA_TYPE;
  joinPairs: JoinKey[];
}
export interface IModelDetail {
  id: number;
  modelName: string;
  modelEnName: string;
  dsId: number; // 数据源id
  dsType: 1 | 2; // 1 -> Presto; 2 -> Impala;
  dsTypeName: string;
  dsUrl: string;
  remark: string;
  schema: string;
  tableName: string;
  updateType: UPDATA_TYPE;
  joinList: TableJoinInfo[];
  columns: Partial<FieldColumn>[];
  creator: string;
  // TODO: any类型
  modelPartition: any;
  createTime?: string;
  dsName?: string;
  step?: number; // 编辑步数step
}
