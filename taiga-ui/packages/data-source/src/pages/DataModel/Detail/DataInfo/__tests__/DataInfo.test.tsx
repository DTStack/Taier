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

import React from 'react';
import DataInfo from '../';
import { render, cleanup } from '@testing-library/react';

const mockRelationList = [
  {
    leftSchema: '0328001_xiaohe',
    leftTable: 'dim_jack',
    leftTableAlias: 't0',
    joinType: 1,
    schema: '0328001_xiaohe',
    table: 'bds_order_text',
    tableAlias: 't1',
    partition: true,
    updateType: 1,
    joinPairs: [
      {
        leftValue: {
          schema: '0328001_xiaohe',
          tableName: 'dim_jack',
          tableAlias: 't0',
          columnName: 'col186',
          columnType: 'string',
          columnComment: 'HELLO',
          dimension: false,
          metric: false,
          partition: false,
        },
        rightValue: {
          schema: '0328001_xiaohe',
          tableName: 'bds_order_text',
          tableAlias: 't1',
          columnName: 'order_header_id',
          columnType: 'string',
          columnComment: '订单头id',
          dimension: false,
          metric: false,
          partition: false,
        },
      },
    ],
  },
];

const mockColumnsList = [
  {
    schema: '0328001_xiaohe',
    tableName: 'dim_jack',
    tableAlias: 't0',
    columnName: 'col57',
    columnType: 'string',
    columnComment: 'HELLO',
    dimension: true,
    metric: false,
    partition: false,
  },
  {
    schema: '0328001_xiaohe',
    tableName: 'dim_jack',
    tableAlias: 't0',
    columnName: 'col185',
    columnType: 'string',
    columnComment: 'HELLO',
    dimension: true,
    metric: false,
    partition: false,
  },
  {
    schema: '0328001_xiaohe',
    tableName: 'dim_jack',
    tableAlias: 't0',
    columnName: 'col313',
    columnType: 'string',
    columnComment: 'HELLO',
    dimension: false,
    metric: true,
    partition: false,
  },
];

describe('component DataInfo:', () => {
  beforeEach(() => cleanup());
  it('module import:', () => {
    expect(DataInfo).toBeInstanceOf(Function);
  });

  it('normal render:', () => {
    const dimensionList = mockColumnsList.filter((item) => item.dimension);
    const metricList = mockColumnsList.filter((item) => item.metric);
    const wrapper = render(
      <DataInfo
        relationTableList={mockRelationList}
        dimensionList={dimensionList}
        metricList={metricList}
      />
    );
    expect(wrapper.getByText('关联表')).not.toBeNull();
    expect(wrapper.getByText('度量')).not.toBeNull();
    expect(wrapper.getByText('维度')).not.toBeNull();
    expect(wrapper.getAllByRole('table').length).toBe(3);
    expect(wrapper.getByText('共1张表')).not.toBeNull();
    expect(wrapper.getByText('共2个字段')).not.toBeNull();
    expect(wrapper.getByText('共1个字段')).not.toBeNull();
  });

  it('none metric render:', () => {
    const wrapper = render(
      <DataInfo relationTableList={[]} dimensionList={[]} metricList={[]} />
    );
    expect(wrapper.getByText('关联表')).not.toBeNull();
    expect(wrapper.getByText('维度')).not.toBeNull();
    expect(wrapper.queryByText('度量')).toBeNull();
    expect(wrapper.getAllByRole('table').length).toBe(2);
    expect(wrapper.getByText('共0张表')).not.toBeNull();
    expect(wrapper.getAllByText('共0个字段').length).toBe(1);
  });
});
