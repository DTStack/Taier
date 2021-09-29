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
import List from '../index';
import { cleanup, render, fireEvent } from '@testing-library/react';

const mockPush = jest.fn();

const router = {
  push: mockPush,
};

let wrapper, ele;
describe('page DataModelList:', () => {
  beforeEach(() => {
    wrapper = render(<List router={router} />);
    ele = wrapper.getByTestId('data-model-list');
  });
  it('module import:', () => {
    expect(List).toBeInstanceOf(Function);
  });
  it('render item:', () => {
    const header = ele.querySelector('.search-area');
    const table = ele.querySelector('.table-area');
    expect(header).not.toBeNull();
    expect(wrapper.getByPlaceholderText('模型名称/英文名')).not.toBeNull();
    expect(table).not.toBeNull();
    expect(wrapper.getByText('新建模型')).not.toBeNull();
  });

  it('add model button click:', () => {
    expect(mockPush.mock.calls.length).toBe(0);
    fireEvent.click(wrapper.getByText('新建模型'));
    expect(mockPush.mock.calls.length).toBe(1);
  });
  afterEach(() => {
    cleanup();
  });
});
