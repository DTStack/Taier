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
import { Modal, Button, Spin, message } from 'antd';
import CopyToClipboard from 'react-copy-to-clipboard';
import ajax from '../../api';
import { getContainer } from './resModal';
import { formatDateTime } from '@/utils';
import classNames from 'classnames';

interface IResViewModalProps {
	visible: boolean;
	closeModal: () => void;
	resId?: string;
}

export default function ResViewModal({ visible, resId, closeModal }: IResViewModalProps) {
	const [loading, setLoading] = useState(true);
	const [data, setData] = useState<Record<string, any> | null>(null);

	const getResDetail = () => {
		ajax.getOfflineRes({
			resourceId: resId,
		}).then((res) => {
			if (res.code === 1) {
				setLoading(false);
				setData(res.data);
			}
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
						<td className="w-1/5">资源名称</td>
						<td>{data.resourceName}</td>
					</tr>
					<tr>
						<td>资源描述</td>
						<td>{data.resourceDesc}</td>
					</tr>
					<tr>
						<td>存储路径</td>
						<td>
							{data.url}
							<CopyToClipboard
								key="copy"
								text={data.url}
								onCopy={() => message.success('复制成功！')}
							>
								<a style={{ marginLeft: 4 }}>复制</a>
							</CopyToClipboard>
						</td>
					</tr>
					<tr>
						<td>创建</td>
						<td>
							{data.createUser.userName} 于 {formatDateTime(data.gmtCreate)}
						</td>
					</tr>
					<tr>
						<td>修改时间</td>
						<td>{formatDateTime(data.gmtModified)}</td>
					</tr>
				</tbody>
			</table>
		);
	};

	useEffect(() => {
		if (resId) {
			getResDetail();
		}
	}, [resId]);

	return (
		<div id="JS_resView_modal">
			<Modal
				title="资源详情"
				visible={visible}
				onCancel={closeModal}
				key={resId}
				width={550}
				footer={[
					<Button size="large" onClick={closeModal} key="cancel">
						关闭
					</Button>,
				]}
				getContainer={() => getContainer('JS_resView_modal')}
			>
				{renderContent()}
			</Modal>
		</div>
	);
}
