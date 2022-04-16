import React, { useState, useEffect } from 'react';
// import Api from '@/api'
import { Breadcrumb, Table } from 'antd';
import { ReloadOutlined } from '@ant-design/icons';
import Chart from './detailGraph';
import moment from 'moment';
import GraphTimePicker from '@/components/graphTime/graphTimePicker';
import GraphTimeRange from '@/components/graphTime/graphTimeRange';

interface Props {
	taskId: number;
	topicName: string;
	closeDetail: () => void;
}

interface TableData {
	currentLocation: number;
	delayCount: number;
	partitionId: string;
	totalDelayCount: number;
}

export default function DetailTable(props: Props) {
	const { taskId, topicName, closeDetail } = props;

	const [tableData, setTableData] = useState<TableData[]>([]);
	const [timespan, setTimespan] = useState('10m');
	const [endTime, setEndTime] = useState(moment());
	const [loading, setLoading] = useState(false);

	useEffect(() => {
		getData();
	}, []);

	function getData() {
		if (loading) {
			return;
		}
		setLoading(true);
		Api.getTopicDetail({
			taskId: taskId,
			topicName: topicName,
		})
			.then((res) => {
				if (res.code === 1) {
					setTableData(res.data);
				}
			})
			.finally(() => {
				setLoading(false);
			});
	}

	function refresh() {
		setEndTime(moment());
		getData();
	}

	const columns = [
		{ title: '分区 ID', dataIndex: 'partitionId' },
		{ title: '延迟消息数（条）', dataIndex: 'delayCount' },
		{ title: '总消息数', dataIndex: 'totalDelayCount' },
		{ title: '当前消费位置', dataIndex: 'currentLocation' },
	];

	const pagination = {
		size: 'small',
		pageSize: 20,
		showTotal: (total) => (
			<>
				共<span style={{ color: '#3F87FF' }}>{total}</span>条数据
			</>
		),
	};

	function expandedRowRender(record: TableData) {
		return (
			<Chart
				taskId={taskId}
				partitionId={record.partitionId}
				topicName={topicName}
				timespan={timespan}
				end={endTime.valueOf()}
			/>
		);
	}

	return (
		<>
			<div style={{ display: 'flex', alignItems: 'center', marginBottom: '16px' }}>
				<Breadcrumb style={{ marginRight: 'auto' }}>
					<Breadcrumb.Item onClick={closeDetail}>
						<a>数据延迟</a>
					</Breadcrumb.Item>
					<Breadcrumb.Item>{topicName}</Breadcrumb.Item>
				</Breadcrumb>
				<GraphTimeRange
					value={timespan}
					onRangeChange={(s) => setTimespan(s)}
					onInputChange={(s) => setTimespan(s)}
				/>
				<GraphTimePicker
					style={{ marginLeft: 12 }}
					value={endTime}
					timeRange={timespan}
					onChange={(e) => setEndTime(e)}
				/>
				<ReloadOutlined
					style={{ color: '#666', marginLeft: 12, cursor: 'pointer' }}
					onClick={refresh}
				/>
			</div>
			<div className="dt-table-fixed-base">
				<Table
					dataSource={tableData}
					rowKey="partitionId"
					loading={loading}
					className="dt-table-border"
					style={{ height: 'calc(100vh - 335px)', boxShadow: 'none' }}
					scroll={{ y: true }}
					size="middle"
					columns={columns}
					pagination={pagination}
					expandedRowRender={expandedRowRender}
				/>
			</div>
		</>
	);
}
