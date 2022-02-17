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

import { useEffect, useState } from 'react';
import { Modal, Button, Spin } from 'antd';
import moment from 'moment';
import ajax from '../../api';
import type { IFunctionProps } from '@/interface';
import classNames from 'classnames';
import { getContainer } from '../resourceManager/resModal';

interface IFnViewModalProps {
	visible: boolean;
	fnId: number | null;
	closeModal: () => void;
}

export default function FnViewModal({ visible, fnId, closeModal }: IFnViewModalProps) {
	const [loading, setLoading] = useState(false);
	const [data, setData] = useState<IFunctionProps | undefined>(undefined);

	const getFnDetail = (id: number) => {
		setLoading(true);
		ajax.getOfflineFn({
			functionId: id,
		})
			.then((res) => {
				if (res.code === 1) {
					setData(res.data);
				}
			})
			.finally(() => {
				setLoading(false);
			});
	};

	const renderContent = () => {
		if (loading) return <Spin />;
		if (!data) return '系统异常';

		return (
			<table
				className={classNames(
					'ant-table',
					'border',
					'border-ddd',
					'border-solid',
					'w-full',
				)}
			>
				<tbody className="ant-table-tbody">
					<tr>
						<td className="w-1/5">函数名称</td>
						<td className='break-all' title={data.name}>{data.name}</td>
					</tr>
					{data.className && (
						<tr>
							<td>类名</td>
							<td className='break-all' title={data.className}>{data.className}</td>
						</tr>
					)}
					{data.sqlText && (
						<tr>
							<td>SQL</td>
							<td className='break-all' title={data.sqlText || '/'}>{data.sqlText || '/'}</td>
						</tr>
					)}
					<tr>
						<td>用途</td>
						<td className='break-all' title={data.purpose}>{data.purpose || '/'}</td>
					</tr>
					<tr>
						<td>命令格式</td>
						<td className='break-all'>{data.commandFormate || '/'}</td>
					</tr>
					<tr>
						<td>参数说明</td>
						<td className='break-all'>{data.paramDesc || '/'}</td>
					</tr>
					<tr>
						<td>创建</td>
						<td>{moment(data.gmtCreate).format('YYYY-MM-DD hh:mm:ss')}</td>
					</tr>
					<tr>
						<td>最后修改</td>
						<td>{moment(data.gmtModified).format('YYYY-MM-DD hh:mm:ss')}</td>
					</tr>
				</tbody>
			</table>
		);
	};

	useEffect(() => {
		if (fnId || fnId === 0) {
			getFnDetail(fnId);
		}
	}, [fnId]);

	const title = data?.type === 2 ? '存储过程详情' : '函数详情';

	return (
		<div id="JS_fnView_modal">
			<Modal
				title={title}
				visible={visible}
				onCancel={closeModal}
				key={fnId}
				width={550}
				footer={[
					<Button size="large" onClick={closeModal} key="cancel">
						关闭
					</Button>,
				]}
				getContainer={() => getContainer('JS_fnView_modal')}
			>
				{renderContent()}
			</Modal>
		</div>
	);
}
