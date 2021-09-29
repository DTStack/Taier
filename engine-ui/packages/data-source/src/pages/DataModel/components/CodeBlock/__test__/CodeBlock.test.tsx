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
import CodeBlock from '../';
import { render, cleanup } from '@testing-library/react';

describe('component CodeBlock:', () => {
  beforeEach(() => cleanup());

  it('module import:', () => {
    expect(CodeBlock).toBeInstanceOf(Function);
  });

  it('empty sql render:', () => {
    const sql = '';
    const wrapper = render(<CodeBlock code={sql} />);
    expect(wrapper.getByText('暂无SQL信息')).not.toBeNull();
    expect(wrapper.getByRole('pre')).toBeNull();
  });

  it('none empty sql render:', () => {
    const sql = 'select name, age from table t1 where id=1';
    const wrapper = render(<CodeBlock code={sql} />);
    expect(wrapper.getByText('暂无SQL信息')).toBeNull();
    expect(wrapper.getByRole('pre')).not.toBeNull();
  });
});
