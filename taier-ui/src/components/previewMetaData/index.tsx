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
import type { ModalProps, TablePaginationConfig } from 'antd';
import { Modal, Table } from 'antd';
import Api from '../../api/index';
import type { ENGINE_SOURCE_TYPE_ENUM } from '@/constant';
import classNames from 'classnames';

const PAGESIZE = 20;

interface IPreviewMetaDataProps {
	visible: boolean;
	onCancel: ModalProps['onCancel'];
	dbName: string;
	engineType: ENGINE_SOURCE_TYPE_ENUM;
}

const COLUMNS = [
	{
		title: '表名',
		key: 'name',
		dataIndex: 'name',
	},
];

/**
 * 预览原数据 Modal
 */
export default function PreviewMetaData({
	visible,
	onCancel,
	dbName,
	engineType,
}: IPreviewMetaDataProps) {
	const [loading, setLoading] = useState(false);
	const [tableData, setTableData] = useState<{ name: string }[]>([]);
	const [total, setTotal] = useState(0);
	const [currentPage, setCurrentPage] = useState(1);

	const getDBTableList = () => {
		setLoading(true);
		Api.getDBTableList({
			dbName,
			engineType,
		})
			.then((res) => {
				if (res.code === 1) {
					setTableData(
						(res.data || []).map((item: string) => ({
							name: item,
						})),
					);
					setTotal(res.data.total);
				}
			})
			.finally(() => {
				setLoading(false);
			});
	};

	const handleTableChange = (pagination: TablePaginationConfig) => {
		setCurrentPage(pagination.current || 1);
	};

	useEffect(() => {
		if (visible) {
			getDBTableList();
		}
	}, [dbName, visible]);

	return (
		<Modal title="预览元数据" visible={visible} onCancel={onCancel} onOk={onCancel}>
			<div className={classNames('pb-10px', 'pl-5px')}>{`数据库名：${dbName}`}</div>
			<Table
				className="dt-ant-table dt-ant-table--border"
				loading={loading}
				rowKey="name"
				columns={COLUMNS}
				onChange={handleTableChange}
				pagination={{
					current: currentPage,
					pageSize: PAGESIZE,
					total,
					showSizeChanger: false,
				}}
				dataSource={tableData}
				scroll={{ y: 350 }}
			/>
		</Modal>
	);
}
