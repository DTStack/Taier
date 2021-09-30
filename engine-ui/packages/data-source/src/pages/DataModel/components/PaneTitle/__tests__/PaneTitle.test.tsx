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
import PaneTitle from '../index';
import { cleanup, render } from '@testing-library/react';

describe('component PaneTitle:', () => {
	it('module import:', () => {
		expect(PaneTitle).toBeInstanceOf(Function);
	});

	it('classname:', () => {
		const wrapper = render(<PaneTitle title="title"></PaneTitle>);
		const ele = wrapper.getByTestId('pane-title');
		expect(ele).toHaveClass('pane-title');
		expect(ele.getElementsByClassName('title').length).toBe(1);
	});

	it('title content render:', () => {
		const title = 'title';
		const wrapper = render(<PaneTitle title={title}></PaneTitle>);
		const titleEle = wrapper.getByText(title);
		expect(titleEle).not.toBeNull();
	});

	afterEach(() => {
		cleanup();
	});
});
