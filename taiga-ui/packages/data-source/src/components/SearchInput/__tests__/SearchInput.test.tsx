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
import SearchInput from '../index';
import { cleanup, render, fireEvent } from '@testing-library/react';

describe('test SearchInput', () => {
  afterEach(() => {
    cleanup();
  });

  it('should get search input component', () => {
    let { queryByTestId } = render(
      <SearchInput
        placeholder="请输入数据源名称或描述"
        width={200}
        onSearch={(value) => {
          console.log('value:', value);
        }}
      />
    );
    expect(queryByTestId('input')).not.toBeNull();
  });

  it('should get placeholder property', () => {
    let { getByPlaceholderText } = render(
      <SearchInput
        placeholder="请输入数据源名称或描述"
        width={200}
        onSearch={(value) => {
          console.log('value:', value);
        }}
      />
    );
    expect(getByPlaceholderText('请输入数据源名称或描述')).toBeTruthy();
  });

  it('should get change value', () => {
    const { queryByTestId } = render(
      <SearchInput
        onSearch={(value) => {
          console.log('value-search:', value);
        }}
      />
    );
    fireEvent.change(queryByTestId('input'), {
      target: { value: 'description' },
    });
    const id: any = queryByTestId('input');
    expect(id.value).toBe('description');
  });

  it('should get search event', () => {
    const myMockSearch = jest.fn((value: any) => {
      return { value };
    });
    const { queryByTestId } = render(<SearchInput onSearch={myMockSearch} />);

    fireEvent.change(queryByTestId('input'), {
      target: { value: '123456789' },
    });
    fireEvent.click(queryByTestId('search-icon'));
    expect(myMockSearch.mock.calls.length).toBe(1);
  });
});
