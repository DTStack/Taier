export enum EnumFormItemType {
  INPUT = 'INPUT',
  SELECT = 'SELECT',
  TEXT_AREA = 'TEXT_AREA',
  RELATION_LIST = 'RELATION_LIST',
}

export interface IFormItem {
  key: string;
  type: EnumFormItemType;
  label: string;
  placeholder?: string;
  rules?: any[];
  options?: any[];
  ext?: Object;
}
