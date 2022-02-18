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

import { Input, Select } from 'antd';
import { useState } from 'react';
import './index.scss';

interface ILifeCycleSelectProps {
	width: number | string;
	inputWidth: number | string;
	value?: number;
	onChange?: (value: number) => void;
}

const OPTIONS = [3, 7, 30, 90, 365]
	.map((day) => ({
		label: `${day}天`,
		value: day,
	}))
	.concat({ label: '自定义', value: -1 });

export default function LifeCycleSelect({
	width,
	inputWidth,
	value,
	onChange,
}: ILifeCycleSelectProps) {
	const [selectValue, setSelectValue] = useState(-1);

	const handleSelect = (eventValue: number) => {
		setSelectValue(eventValue);
		if (eventValue !== -1) {
			onChange?.(eventValue);
		}
	};

	const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
		const { value: eventValue } = e.target;
		onChange?.(Number(eventValue));
	};

	return (
		<>
			<Select<number>
				value={selectValue}
				style={{ width: width || 200 }}
				placeholder="请选择存储生命周期"
				onSelect={handleSelect}
				options={OPTIONS}
			/>
			{selectValue === -1 ? (
				<Input
					className="dt-life-input"
					value={value}
					style={{ width: inputWidth || 220, marginLeft: 5 }}
					min={0}
					addonAfter="天"
					placeholder="请输入生命周期"
					onChange={handleInputChange}
				/>
			) : null}
		</>
	);
}
