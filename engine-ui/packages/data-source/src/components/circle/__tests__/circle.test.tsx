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
import { Circle } from '../index';
import { render, cleanup } from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';
const defaultProps = {
	className: 'test-circle',
	children: <div>circle</div>,
};

let wrapper, wrapper2, element, element2;
describe('test circle', () => {
	beforeEach(() => {
		wrapper = render(<Circle {...defaultProps} data-testid="test1"></Circle>);
		wrapper2 = render(<Circle data-testid="test2" />);
		element = wrapper.getByTestId('test1');
		element2 = wrapper2.getByTestId('test2');
	});
	afterEach(() => {
		cleanup();
	});
	test('should render the correct className in Circle', () => {
		expect(element).toBeInTheDocument();
		expect(element).toHaveClass('circle_default test-circle');
	});
	test('should render the correct children in Circle', () => {
		expect(wrapper.getByText('circle')).toBeInTheDocument();
	});
	test('should render nothing without children', () => {
		expect(element2).toBeEmptyDOMElement();
	});
});
