import { styleStringGenerator, loop } from '../utils';
import { IRelationTree, EnumTableType } from '../types';

describe('utils test for relation view:', () => {
  it('module impoert:', () => {
    expect(styleStringGenerator).toBeInstanceOf(Function);
    expect(loop).toBeInstanceOf(Function);
  });

  it('style string generator:', () => {
    const mxStyleString = styleStringGenerator('=');
    const input = {
      background: 'red',
      fillColor: 'orange',
      strokeColor: 'blue',
    };
    const mxStyleResult = 'background=red;fillColor=orange;strokeColor=blue;';
    expect(mxStyleString(input)).toBe(mxStyleResult);
    const domStyleString = styleStringGenerator(':');
    const domStyleResult = 'background:red;fillColor:orange;strokeColor:blue;';
    expect(domStyleString(input)).toBe(domStyleResult);
  });

  it('loop:', () => {
    const tableD: IRelationTree = {
      tableName: 'ddd',
      tableAlias: 'alias_d',
      columns: [],
      joinInfo: null,
      _tableType: EnumTableType.PRIMARY,
      children: [],
    };

    const tableB: IRelationTree = {
      tableName: 'bbb',
      tableAlias: 'alias_b',
      columns: [],
      joinInfo: null,
      _tableType: EnumTableType.PRIMARY,
      children: [tableD],
    };

    const tableC: IRelationTree = {
      tableName: 'ccc',
      tableAlias: 'alias_c',
      columns: [],
      joinInfo: null,
      _tableType: EnumTableType.PRIMARY,
      children: [],
    };

    const input: IRelationTree = {
      tableName: 'aaa',
      tableAlias: 'alias_a',
      columns: [],
      joinInfo: null,
      _tableType: EnumTableType.PRIMARY,
      children: [tableB, tableC],
    };

    const list = [];

    loop(input, (n) => list.push(n));

    const result = [input, tableC, tableB, tableD];

    expect(list).toEqual(result);
  });
});
