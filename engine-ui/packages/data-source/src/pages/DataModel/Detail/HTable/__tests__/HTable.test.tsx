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
import HTable from '../index';
import { cleanup, render } from '@testing-library/react';
import { IModelDetail } from 'pages/DataModel/types';

const detail: Partial<IModelDetail> = {
  modelName: 'model name',
  dsName: 'data source name',
  creator: 'admin@dtstack.com',
  createTime: '2020-01-01 12:00:00',
  modelPartition: {
    datePartitionColumn: {
      columnName: 'date',
    },
    timePartitionColumn: {
      columnName: 'time',
    },
  },
  remark: 'remark',
};

describe('component HTable:', () => {
  it('module import:', () => {
    expect(HTable).toBeInstanceOf(Function);
  });

  it('render with empty detail data:', () => {
    const wrapper = render(<HTable detail={{}} />);
    const ele = wrapper.getByTestId('h-table');
    expect(ele.getElementsByTagName('tr').length).toBe(4);
    expect(wrapper.getAllByText('--').length).toBe(7);
  });

  it('render with non-empty detail data:', () => {
    const wrapper = render(<HTable detail={detail} />);
    wrapper.getByText(detail.modelName);
    wrapper.getByText(detail.dsName);
    wrapper.getByText(detail.creator);
    wrapper.getByText(detail.createTime);
    wrapper.getByText(detail.modelPartition.datePartitionColumn.columnName);
    wrapper.getByText(detail.modelPartition.timePartitionColumn.columnName);
    wrapper.getByText(detail.remark);
  });

  afterEach(() => {
    cleanup();
  });
});
