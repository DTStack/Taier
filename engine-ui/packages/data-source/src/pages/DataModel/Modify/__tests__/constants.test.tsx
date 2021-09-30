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

// import React from 'react';
import { cleanup } from '@testing-library/react';
import { layoutGenerator, joinItemParser } from '../constants';
import { EnumModifyStep } from '../types';

describe('constants of Modify:', () => {
	it('layout generator:', () => {
		const layout1 = {
			labelCol: { span: 3 },
			wrapperCol: { span: 21 },
		};
		const layout2 = {
			labelCol: { span: 5 },
			wrapperCol: { span: 19 },
		};
		expect(layoutGenerator(EnumModifyStep.BASIC_STEP)).toEqual(layout1);
		expect(layoutGenerator(EnumModifyStep.RELATION_TABLE_STEP)).toEqual(layout1);
		expect(layoutGenerator(EnumModifyStep.SETTING_STEP)).toEqual(layout2);
	});

	it('joinParser', () => {
		const input = {
			'relation-key-left_0': 'schema1-table1-col1',
			'relation-key-right_0': 'schema1-table1-col1',
			'relation-key-left_1': 'schema2-table2-col2',
			'relation-key-right_1': 'schema2-table2-col2',
		};
		const res = joinItemParser(input);
		delete res.id;
		expect(res).toEqual({
			joinPairs: [
				{
					leftValue: 'schema1-table1-col1',
					rightValue: 'schema1-table1-col1',
				},
				{
					leftValue: 'schema2-table2-col2',
					rightValue: 'schema2-table2-col2',
				},
			],
		});
	});

	afterEach(() => {
		cleanup();
	});
});
