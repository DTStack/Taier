export enum EnumFormItemType {
  INPUT = 'INPUT',
  SELECT = 'SELECT',
  TEXT_AREA = 'TEXT_AREA',
}

export interface IFormItem {
  key: string;
  type: EnumFormItemType;
  label: string;
  placeholder?: string;
  rules?: any[];
  options?: any[];
}
