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
import { Badge, Button, Empty, message, Modal, Tag } from 'antd';
import moment from 'moment';
import Base64 from 'base-64';
import molecule from '@dtinsight/molecule';
import { ActionBar, Menu, useContextView } from '@dtinsight/molecule/esm/components';
import { Content, Header } from '@dtinsight/molecule/esm/workbench/sidebar';
import { connect } from '@dtinsight/molecule/esm/react';
import dataSourceService from '@/services/dataSourceService';
import API from '@/api';
import { LoadingOutlined, ExclamationCircleOutlined } from '@ant-design/icons';
import { getEventPosition } from '@dtinsight/molecule/esm/common/dom';
import { ID_COLLECTIONS } from '@/constant';
import { IDataSourceProps } from '@/interface';
import LinkInfoCell from './linkInfoCell';
import Search from './search';
import Add from './add';
import classNames from 'classnames';
import { DataSourceLinkFailed, DataSourceLinkSuccess } from '@/components/icon';
import './index.scss';

const { confirm } = Modal;

interface IPagination {
	currentPage: number;
	pageSize: number;
}

interface IOther {
	search: string;
	dataTypeList: string[];
	appTypeList: number[];
	isMeta: number;
	status: number[];
}

const DataSourceView = () => {
	const [dataSources, setDataSources] = useState<IDataSourceProps[]>([]);
	const [params, setParams] = useState<IPagination>({
		currentPage: 1, // 当前页码
		pageSize: 20, // 分页个数
	});
	const [other, setOther] = useState<IOther>({
		search: '',
		dataTypeList: [],
		appTypeList: [],
		isMeta: 0,
		status: [],
	});

	const [total, setTotal] = useState<number>(0);

	const [visible, setVisible] = useState<boolean>(false);
	const [detailView, setView] = useState<IDataSourceProps | null>(null);

	const contextView = useContextView();

	// 获取表格数据
	const requestTableData = async (query?: any, appendMode: boolean = false) => {
		const requestParams = {
			...params,
			...other,
			...query,
		};
		if (typeof requestParams.isMeta === 'boolean') {
			requestParams.isMeta = Number(requestParams.isMeta);
		}
		const { data, success } = await API.dataSourcepage(requestParams);
		if (success) {
			const { currentPage, totalCount } = data;
			setParams({
				currentPage, // 当前页码
				pageSize: 20, // 分页个数
			});
			const nextData: IDataSourceProps[] = ((data.data as IDataSourceProps[]) || []).map(
				(ele) => {
					const canConvertLinkJson =
						ele.linkJson && !ele.linkJson.includes('{') && !ele.linkJson.includes('}');

					return {
						...ele,
						linkJson: canConvertLinkJson ? Base64.decode(ele.linkJson!) : ele.linkJson,
					};
				},
			);

			setTotal(totalCount); // 总页数

			if (!appendMode) {
				setDataSources(nextData || []);
			} else {
				setDataSources((sources) => {
					const nextSources = sources.concat();
					nextSources.push(...nextData);
					return nextSources;
				});
			}
		}
	};

	// 搜索事件
	const handleSearch = (value: Record<string, any>) => {
		const data = { ...other, ...value, currentPage: 1 };
		setOther(data);
		requestTableData(data);
	};

	const handleLoadMore = () => {
		requestTableData({ currentPage: params.currentPage + 1 }, true);
	};

	const handleOpenDetail = (record: IDataSourceProps) => {
		setVisible(true);
		setView(record);
	};

	// 删除
	const toDelete = async (record: IDataSourceProps) => {
		const { success, message: msg } = await API.dataSourceDelete({
			dataInfoId: record.dataInfoId,
		});

		if (success) {
			message.success('删除成功');
			requestTableData(); // 更新表格
		} else {
			message.error(`${msg}`);
		}
	};

	const handleMenuClick = (menu: { id: string; name: string }, record: IDataSourceProps) => {
		contextView.hide();
		switch (menu.id) {
			case 'edit':
				if (molecule.editor.isOpened(ID_COLLECTIONS.EDIT_DATASOURCE_PREFIX)) {
					message.warning('请先保存或关闭编辑数据源');
					const groupId = molecule.editor.getGroupIdByTab(
						ID_COLLECTIONS.EDIT_DATASOURCE_PREFIX,
					)!;
					molecule.editor.setActive(groupId, ID_COLLECTIONS.EDIT_DATASOURCE_PREFIX);
				} else {
					molecule.editor.open({
						id: ID_COLLECTIONS.EDIT_DATASOURCE_PREFIX,
						name: '编辑数据源',
						icon: 'edit',
						renderPane: (
							<Add
								key={ID_COLLECTIONS.EDIT_DATASOURCE_PREFIX}
								record={record}
								onSubmit={handleSubmitDataSource}
							/>
						),
						breadcrumb: [
							{
								id: 'root',
								name: '数据源中心',
							},
							{
								id: ID_COLLECTIONS.EDIT_DATASOURCE_PREFIX,
								name: '编辑数据源',
							},
						],
					});
				}
				break;
			case 'delete':
				confirm({
					title: '是否删除此条记录？',
					icon: <ExclamationCircleOutlined />,
					okText: '删除',
					okType: 'danger',
					cancelText: '取消',
					onOk() {
						toDelete(record);
					},
					onCancel() {},
				});
				break;
			default:
				break;
		}
	};

	const handleContextmenu = (
		e: React.MouseEvent<HTMLLIElement, MouseEvent>,
		record: IDataSourceProps,
	) => {
		e.preventDefault();
		e.currentTarget.focus();
		contextView.show(getEventPosition(e), () => (
			<Menu
				role="menu"
				onClick={(_: any, item: any) => handleMenuClick(item, record)}
				data={[
					{
						id: 'edit',
						name: '编辑',
					},
					{
						id: 'delete',
						name: '删除',
					},
				]}
			/>
		));
	};

	const handleCloseModal = () => {
		setVisible(false);
	};

	const handleSubmitDataSource = () => {
		const nextParams = {
			currentPage: 1,
		};
		setParams((p) => ({ ...p, ...nextParams }));
		requestTableData(nextParams);
	};

	const handleHeaderBarClick = () => {
		if (molecule.editor.isOpened(ID_COLLECTIONS.CREATE_DATASOURCE_PREFIX)) {
			message.warning('请先保存或关闭新增数据源');
			const groupId = molecule.editor.getGroupIdByTab(
				ID_COLLECTIONS.CREATE_DATASOURCE_PREFIX,
			)!;
			molecule.editor.setActive(groupId, ID_COLLECTIONS.CREATE_DATASOURCE_PREFIX);
		} else {
			molecule.editor.open({
				id: ID_COLLECTIONS.CREATE_DATASOURCE_PREFIX,
				name: '新增数据源',
				icon: 'server-process',
				renderPane: (
					<Add
						key={ID_COLLECTIONS.CREATE_DATASOURCE_PREFIX}
						onSubmit={handleSubmitDataSource}
					/>
				),
				breadcrumb: [
					{
						id: 'root',
						name: '数据源中心',
					},
					{
						id: ID_COLLECTIONS.CREATE_DATASOURCE_PREFIX,
						name: '新增数据源',
					},
				],
			});
		}
	};

	useEffect(() => {
		requestTableData();
	}, []);

	return (
		<div className="datasource-container">
			<Header
				title="数据源中心"
				toolbar={
					<ActionBar
						data={[
							{
								id: 'add',
								title: '新增数据源',
								icon: 'server-process',
								contextMenu: [],
								onClick: handleHeaderBarClick,
							},
						]}
					/>
				}
			/>
			<Content>
				<Search onSearch={handleSearch} />
				{dataSources.length ? (
					<div tabIndex={0} className="datasource-content">
						<ul className="datasource-list">
							{dataSources.map((item) => (
								<li
									key={item.dataInfoId}
									tabIndex={-1}
									className="datasource-record"
									onClick={() => handleOpenDetail(item)}
									onContextMenu={(e) => handleContextmenu(e, item)}
								>
									{item.status === 0 ? (
										<DataSourceLinkFailed
											style={{ color: '#ed5b56', fontSize: 0 }}
										/>
									) : (
										<DataSourceLinkSuccess
											style={{ color: '#72c140', fontSize: 0 }}
										/>
									)}
									<div className="datasource-title">
										{item.isMeta === 0 ? (
											<>
												<span className="title" title={item.dataName}>
													{item.dataName}({item.dataType}
													{item.dataVersion || ''})
												</span>
												<span className={classNames('desc')}>
													{item.dataDesc || '--'}
												</span>
											</>
										) : (
											<>
												<span className="title" title={item.dataName}>
													{item.dataName}({item.dataType}
													{item.dataVersion || ''})
												</span>
												<Tag>Meta</Tag>
											</>
										)}
									</div>
								</li>
							))}
						</ul>
						{total !== dataSources.length && !!total && (
							<Button block onClick={handleLoadMore}>
								加载更多...
							</Button>
						)}
					</div>
				) : (
					<Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />
				)}
				<Modal
					title="数据源详情"
					visible={visible}
					onCancel={handleCloseModal}
					width={550}
					footer={[
						<Button size="large" onClick={handleCloseModal} key="cancel">
							关闭
						</Button>,
					]}
				>
					{detailView ? (
						<table className={classNames('ant-table', 'datasource-detail')}>
							<tbody className="ant-table-tbody">
								<tr>
									<td className="w-1/5">名称</td>
									<td>
										{detailView.isMeta === 0 ? (
											<span title={detailView.dataName}>
												{detailView.dataName}
											</span>
										) : (
											<>
												<span title={detailView.dataName}>
													{detailView.dataName}
												</span>
												<Tag>Meta</Tag>
											</>
										)}
									</td>
								</tr>
								<tr>
									<td>类型</td>
									<td>
										{detailView.dataType}
										{detailView.dataVersion || ''}
									</td>
								</tr>
								<tr>
									<td>描述</td>
									<td>{detailView.dataDesc || '--'}</td>
								</tr>
								<tr>
									<td>连接信息</td>
									<td>
										<LinkInfoCell sourceData={detailView} />
									</td>
								</tr>
								<tr>
									<td>连接状态</td>
									<td>
										{detailView.status === 0 ? (
											<span>
												<Badge status="error" />
												连接失败
											</span>
										) : (
											<span>
												<Badge status="success" />
												正常
											</span>
										)}
									</td>
								</tr>
								<tr>
									<td>修改时间</td>
									<td>
										{moment(detailView.gmtModified).format(
											'YYYY-MM-DD hh:mm:ss',
										)}
									</td>
								</tr>
							</tbody>
						</table>
					) : (
						<LoadingOutlined />
					)}
				</Modal>
			</Content>
		</div>
	);
};

export default connect(dataSourceService, DataSourceView);
