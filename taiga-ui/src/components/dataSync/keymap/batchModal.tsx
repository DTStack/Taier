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

import * as React from 'react';
import { Modal, Input } from 'antd';
import {
	DATA_SOURCE_ENUM,
	HDFS_FIELD_TYPES,
	HBASE_FIELD_TYPES,
} from '@/constant';

const renderTypes = (sourceType: number) => {
	const types =
		sourceType === DATA_SOURCE_ENUM.HBASE
			? HBASE_FIELD_TYPES
			: HDFS_FIELD_TYPES;
	const typeItems = types?.map((type: any) => <b key={type}>{type}, </b>);
	return <span style={{ wordBreak: 'break-all' }}>{typeItems}</span>;
};

export default function BatchModal(props: any) {
	const {
		title,
		desc,
		visible,
		onOk,
		placeholder,
		value,
		sourceType,
		onCancel,
		onChange,
		columnFamily,
	} = props;
	const rowsFix = { rows: 6 };
	const isNotHBase = sourceType !== DATA_SOURCE_ENUM.HBASE;
	return (
		<Modal
			title={title}
			onOk={onOk}
			onCancel={onCancel}
			maskClosable={false}
			visible={visible}
		>
			<div>
				{isNotHBase
					? '批量导入的语法格式（index 从 0 开始）：'
					: '批量添加的语法格式:'}
				<b style={{ color: 'rgb(255, 102, 0)' }}>
					{desc &&
					Object.prototype.toString.call(desc)?.slice(8, -1) ===
						'String'
						? desc
								.split(',')
								.map((item: any) => <p key={item}>{item}</p>)
						: { desc }}
				</b>
				<p>
					常用数据类型（type)：
					<span style={{ color: 'rgb(255, 102, 0)' }}>
						{renderTypes(sourceType)}
					</span>
				</p>
				{columnFamily ? (
					<p>
						已有列族：
						<span style={{ color: 'rgb(255, 102, 0)' }}>
							{columnFamily?.map((col: any) => `${col},`)}
						</span>
					</p>
				) : (
					''
				)}
			</div>
			<br />
			<Input.TextArea
				{...rowsFix}
				value={value}
				onChange={onChange}
				placeholder={placeholder}
			/>
		</Modal>
	);
}
