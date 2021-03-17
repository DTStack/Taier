//分页字段
export interface IPagination {
  current: number;
  size: number;
}
export interface IOther {
  search: string;
  dataType: string[];
  appType: number[];
  isMeta: number;
  status: number[];
}

//列表操作字段
export interface IRecord{
  dataInfoId:number,
  isAuth:number
}
