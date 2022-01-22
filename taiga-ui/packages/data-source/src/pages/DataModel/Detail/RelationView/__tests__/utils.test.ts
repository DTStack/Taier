/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
