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

import React, { useCallback } from 'react';
import './style';
import { IModelDetail } from '../../types';
import { holder } from '../constants';
import { EnumSize } from '../types';

interface IPropsHTable {
	detail: Partial<IModelDetail>;
	size?: EnumSize;
}

const HTable = (props: IPropsHTable) => {
	const { detail, size = EnumSize.LARGE } = props;

	const getSize = useCallback((size: EnumSize) => {
		switch (size) {
			case EnumSize.LARGE:
				return '750px';
			case EnumSize.SMALL:
				return '510px';
		}
	}, []);

	return (
		<table className="h-table" data-testid="h-table">
			<tbody>
				<tr>
					<td className="label border-top border-left">模型名称</td>
					<td className="value border-top">{holder(detail.modelName)}</td>
					<td className="label border-top">数据源</td>
					<td className="value border-right border-top">{holder(detail.dsName)}</td>
				</tr>
				<tr>
					<td className="label border-left">创建人</td>
					<td className="value">{holder(detail.creator)}</td>
					<td className="label">创建时间</td>
					<td className="value border-right">{holder(detail.createTime)}</td>
				</tr>
				<tr>
					<td className="label border-left">分区字段（日期）</td>
					<td className="value">
						{holder(detail.modelPartition?.datePartitionColumn?.columnName)}
					</td>
					<td className="label">分区字段（时间）</td>
					<td className="value border-right">
						{holder(detail.modelPartition?.timePartitionColumn?.columnName)}
					</td>
				</tr>
				<tr>
					<td className="label border-left border-bottom">备注</td>
					<td className="value border-bottom border-right" colSpan={3}>
						<div
							className="h-table-remark-inner"
							style={{ maxWidth: getSize(size) }}
							title={detail.remark}
						>
							{holder(detail.remark)}
						</div>
					</td>
				</tr>
			</tbody>
		</table>
	);
};

export default HTable;
