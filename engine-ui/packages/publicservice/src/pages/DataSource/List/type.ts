//分页字段
export interface IPagination {
  currentPage: number;
  pageSize: number;
}
export interface IOther {
  search: string;
  dataTypeList: string[];
  appTypeList: number[];
  isMeta: number;
  status: number[];
}

//列表操作字段
export interface IRecord {
  dataInfoId: number;
  isAuth: number;
}
