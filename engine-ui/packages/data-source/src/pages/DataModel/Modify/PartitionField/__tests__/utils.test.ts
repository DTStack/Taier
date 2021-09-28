import { columnsTreeParser } from '../utils';
import { FieldColumn } from '@/pages/DataModel/types';

describe('utils of partition fields:', () => {
  it('columns tree parser:', () => {
    const mock: Partial<FieldColumn>[] = [
      { tableName: 'a', tableAlias: 'alias_a', columnName: 'columns1' },
      { tableName: 'a', tableAlias: 'alias_a', columnName: 'columns2' },
      { tableName: 'a', tableAlias: 'alias_a', columnName: 'columns3' },
      { tableName: 'a', tableAlias: 'alias_a', columnName: 'columns4' },
      { tableName: 'a', tableAlias: 'alias_a', columnName: 'columns5' },
      { tableName: 'b', tableAlias: 'alias_b', columnName: 'columns1' },
      { tableName: 'b', tableAlias: 'alias_b', columnName: 'columns2' },
      { tableName: 'b', tableAlias: 'alias_b', columnName: 'columns3' },
      { tableName: 'b', tableAlias: 'alias_b', columnName: 'columns4' },
      { tableName: 'b', tableAlias: 'alias_b', columnName: 'columns5' },
      { tableName: 'c', tableAlias: 'alias_c', columnName: 'columns1' },
      { tableName: 'c', tableAlias: 'alias_c', columnName: 'columns2' },
      { tableName: 'c', tableAlias: 'alias_c', columnName: 'columns3' },
      { tableName: 'c', tableAlias: 'alias_c', columnName: 'columns4' },
      { tableName: 'c', tableAlias: 'alias_c', columnName: 'columns5' },
    ];
    const target = {
      'a(alias_a)': [
        { tableName: 'a', tableAlias: 'alias_a', columnName: 'columns1' },
        { tableName: 'a', tableAlias: 'alias_a', columnName: 'columns2' },
        { tableName: 'a', tableAlias: 'alias_a', columnName: 'columns3' },
        { tableName: 'a', tableAlias: 'alias_a', columnName: 'columns4' },
        { tableName: 'a', tableAlias: 'alias_a', columnName: 'columns5' },
      ],
      'b(alias_b)': [
        { tableName: 'b', tableAlias: 'alias_b', columnName: 'columns1' },
        { tableName: 'b', tableAlias: 'alias_b', columnName: 'columns2' },
        { tableName: 'b', tableAlias: 'alias_b', columnName: 'columns3' },
        { tableName: 'b', tableAlias: 'alias_b', columnName: 'columns4' },
        { tableName: 'b', tableAlias: 'alias_b', columnName: 'columns5' },
      ],
      'c(alias_c)': [
        { tableName: 'c', tableAlias: 'alias_c', columnName: 'columns1' },
        { tableName: 'c', tableAlias: 'alias_c', columnName: 'columns2' },
        { tableName: 'c', tableAlias: 'alias_c', columnName: 'columns3' },
        { tableName: 'c', tableAlias: 'alias_c', columnName: 'columns4' },
        { tableName: 'c', tableAlias: 'alias_c', columnName: 'columns5' },
      ],
    };

    const res = columnsTreeParser(mock);
    expect(res).toEqual(target);
  });
});
