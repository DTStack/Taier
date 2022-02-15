import { useEffect, useState } from 'react';
import { Badge, Button, message, Modal, Tag } from 'antd';
import moment from 'moment';
import molecule from '@dtinsight/molecule';
import { ActionBar, Icon, Menu, useContextView } from '@dtinsight/molecule/esm/components';
import { Content, Header } from '@dtinsight/molecule/esm/workbench/sidebar';
import { connect } from '@dtinsight/molecule/esm/react';
import dataSourceService from '@/services/dataSourceService';
import { API } from '@/api/dataSource';
import { LoadingOutlined, ExclamationCircleOutlined } from '@ant-design/icons';
import Base64 from 'base-64';
import { getEventPosition } from '@dtinsight/molecule/esm/common/dom';
import { DATA_SOURCE_ENUM, CREATE_DATASOURCE_PREFIX } from '@/constant';
import LinkInfoCell from './linkInfoCell';
import Search from './search';
import Add from './add';
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

export interface IDataSourceProps {
	dataInfoId: number;
	dataType: string;
	// 0 for false, 1 for true
	isMeta: number;
	appNames: string;
	dataDesc: string;
	dataName: string;
	dataVersion: string;
	gmtModified: string;
	isImport: number;
	schemaName: string;
	status: number;
	linkJson: string | null;
	type?: DATA_SOURCE_ENUM;
}

const { builtInExplorerHeaderToolbar } = molecule.builtin.getModules();
const HEADER_BAR = builtInExplorerHeaderToolbar;
HEADER_BAR.contextMenu.push({
	id: 'add',
	name: '新增数据源',
});

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
		const { data, success, message: resMessage } = await API.dataSourcepage(requestParams);
		if (success) {
			const { currentPage, totalCount } = data;
			setParams({
				currentPage, // 当前页码
				pageSize: 20, // 分页个数
			});
			const nextData: IDataSourceProps[] = ((data.data as IDataSourceProps[]) || []).map(
				(ele) => {
					const sourceType = ele.dataType.toUpperCase() as keyof typeof DATA_SOURCE_ENUM;
					const type = DATA_SOURCE_ENUM[sourceType];
					const canConvertLinkJson =
						ele.linkJson && !ele.linkJson.includes('{') && !ele.linkJson.includes('}');

					return {
						...ele,
						type,
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
		} else {
			message.error(resMessage);
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
				if (molecule.editor.isOpened(CREATE_DATASOURCE_PREFIX)) {
					message.error('请先保存或关闭新增数据源');
					const groupId = molecule.editor.getGroupIdByTab(CREATE_DATASOURCE_PREFIX)!;
					molecule.editor.setActive(groupId, CREATE_DATASOURCE_PREFIX);
				} else {
					molecule.editor.open({
						id: CREATE_DATASOURCE_PREFIX,
						name: '编辑数据源',
						renderPane: <Add record={record} />,
						breadcrumb: [
							{
								id: 'root',
								name: '数据源中心',
							},
							{
								id: CREATE_DATASOURCE_PREFIX,
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
		if (molecule.editor.isOpened(CREATE_DATASOURCE_PREFIX)) {
			const groupId = molecule.editor.getGroupIdByTab(CREATE_DATASOURCE_PREFIX)!;
			molecule.editor.setActive(groupId, CREATE_DATASOURCE_PREFIX);
		} else {
			molecule.editor.open({
				id: CREATE_DATASOURCE_PREFIX,
				name: '新增数据源',
				renderPane: <Add onSubmit={handleSubmitDataSource} />,
				breadcrumb: [
					{
						id: 'root',
						name: '数据源中心',
					},
					{
						id: CREATE_DATASOURCE_PREFIX,
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
					<ActionBar data={[HEADER_BAR]} onContextMenuClick={handleHeaderBarClick} />
				}
			/>
			<Content>
				<Search onSearch={handleSearch} />
				<div tabIndex={0} className="datasource-content">
					<ul className="datasource-list">
						{dataSources.map((item) => (
							<li
								key={item.dataInfoId}
								className="datasource-record"
								onClick={() => handleOpenDetail(item)}
								onContextMenu={(e) => handleContextmenu(e, item)}
							>
								<Icon type="symbol-field" />
								<div className='datasource-title'>
									{item.isMeta === 0 ? (
										<span title={item.dataName}>{item.dataName}</span>
									) : (
										<>
											<span title={item.dataName}>{item.dataName}</span>
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
						<table
							className="ant-table ant-table-bordered bd-top bd-left"
							style={{ width: '100%' }}
						>
							<tbody className="ant-table-tbody">
								<tr>
									<td {...{ width: '20%' }}>数据源名称</td>
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
									<td>最近修改时间</td>
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
