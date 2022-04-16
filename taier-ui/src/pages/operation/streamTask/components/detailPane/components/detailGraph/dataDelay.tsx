import * as React from 'react';

import { Table } from 'antd';
// import Api from '../../../../../api'
import DetailTable from './delay/detailTable';

interface Props {
	data: any;
	tabKey: string;
}

const Api = {
	getDelayList: () => Promise.resolve({
		code: 1,
		message: null,
		data: [],
		space: 0,
		version: null,
		success: true,
	}),
};

class DataDelay extends React.Component<Props, any> {
	state = {
		delayList: [],
		loading: false,
		detailVisible: false,
		detailRecord: {} as any,
	};

	componentDidMount() {
		this.getDelayList();
	}

	// eslint-disable-next-line
	UNSAFE_componentWillReceiveProps(nextProps: Props) {
		const { data = {}, tabKey } = this.props;
		const { data: nextData = {}, tabKey: nextTabKey } = nextProps;
		// 从其他 tab 回来时，重新请求
		const reFocusTab = tabKey !== 'dataDelay' && nextTabKey === 'dataDelay';
		if (data.id != nextData.id || reFocusTab) {
			this.getDelayList(nextData);
		}
	}

	getDelayList(data?: any) {
		data = data || this.props.data;
		this.setState({ delayList: [] });
		if (!data) {
			return;
		}
		this.setState({ loading: true, detailVisible: false });

		Api.getDelayList({ taskId: data.id }).then((res: any) => {
			if (res.code == 1) {
				this.setState({ delayList: res.data });
			}
			this.setState({ loading: false });
		});
	}

	initDelayListColumns() {
		return [
			{
				title: 'Topic名称',
				dataIndex: 'topicName',
				render: (text: string, record) => {
					return (
						<a
							onClick={() => {
								this.showDetail(record);
							}}
						>
							{text}
						</a>
					);
				},
			},
			{ title: '总延迟消息数', dataIndex: 'totalDelayCount' },
			{ title: '分区总数', dataIndex: 'partCount' },
		];
	}

	showDetail = (record: any) => {
		this.setState({
			detailRecord: record,
			detailVisible: true,
		});
	};

	closeDetail() {
		this.setState({
			detailRecord: {},
			detailVisible: false,
		});
	}

	render() {
		const { loading, delayList, detailVisible, detailRecord } = this.state;
		const { data = {} } = this.props;
		const paginations = {
			pageSize: 10,
			size: 'small',
			total: delayList.length,
			showTotal: (total) => (
				<span>
					共<span style={{ color: '#3F87FF' }}>{total}</span>条数据，每页显示10条
				</span>
			),
		};
		return !detailVisible ? (
			<Table
				rowKey="topicName"
				className="dt-table-border"
				style={{ boxShadow: 'unset' }}
				columns={this.initDelayListColumns()}
				dataSource={delayList}
				pagination={paginations}
				loading={loading}
			/>
		) : (
			<DetailTable
				closeDetail={this.closeDetail.bind(this)}
				taskId={data.id}
				topicName={detailRecord.topicName}
			/>
		);
	}
}

export default DataDelay;
