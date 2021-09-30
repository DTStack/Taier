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

import React, { useState } from 'react';
import { Input, Icon } from 'antd';

interface IProp {
	placeholder?: string;
	width?: number;
	onSearch: (value: string) => void;
	className?: string;
}
export default function SearchInput(props: IProp, any) {
	const { placeholder = '请求输入搜索内容', width = 200, onSearch, className } = props;

	const [value, setValue] = useState('');
	const IconSearch = () => (
		<span className="iconfont2 iconOutlinedxianxing_Search" data-testid="search-icon"></span>
	);
	return (
		<Input
			className={className}
			allowClear
			value={value}
			onChange={(e) => {
				setValue(e.target.value);
				if (!e.target.value) {
					onSearch(e.target.value);
				}
			}}
			onKeyDown={(e) => {
				if (e.key === 'Enter') {
					onSearch(value);
				}
			}}
			data-testid="input"
			placeholder={placeholder}
			suffix={
				<Icon
					component={IconSearch}
					onClick={() => {
						onSearch(value);
					}}
					style={{ cursor: 'pointer', color: '#999' }}
				/>
			}
			style={{ width: width }}
		/>
	);
}
