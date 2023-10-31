import { useEffect, useState } from 'react';
import { Table } from 'antd';
import type { ColumnType } from 'antd/lib/table';

import DetailTable from './delay/detailTable';

interface IDataDelay {
    data: any;
    tabKey: string;
}

export default function DataDelay({ data }: IDataDelay) {
    const [detailVisible, setDetailVisible] = useState(false);
    const [detailRecord, setDetailRecord] = useState<any | undefined>(undefined);
    const [delayList, setDelayList] = useState<any[]>([]);
    const [loading, setLoading] = useState(false);

    const getDelayList = () => {
        // TODO：需要后端配合迁移接口
        setDelayList([]);
        setLoading(false);
        /* setLoading(true);
		setDetailVisible(false);

		api.getDelayList({ taskId: data.id })
			.then((res) => {
				if (res.code == 1) {
					setDelayList(res.data || []);
				}
			})
			.finally(() => {
				setLoading(false);
			}); */
    };

    useEffect(() => {
        getDelayList();
    }, [data.id]);

    const columns: ColumnType<any>[] = [
        {
            title: 'Topic名称',
            dataIndex: 'topicName',
            render: (text: string, record) => {
                return (
                    <a
                        onClick={() => {
                            setDetailRecord(record);
                            setDetailVisible(true);
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

    return !detailVisible ? (
        <Table
            rowKey="topicName"
            bordered
            columns={columns}
            dataSource={delayList}
            pagination={{
                pageSize: 10,
                size: 'small',
                total: delayList.length,
                showTotal: (total) => (
                    <span>
                        共<span style={{ color: '#3F87FF' }}>{total}</span>条数据，每页显示10条
                    </span>
                ),
            }}
            loading={loading}
        />
    ) : (
        <DetailTable
            closeDetail={() => {
                setDetailRecord(undefined);
                setDetailVisible(false);
            }}
            taskId={data.id}
            topicName={detailRecord?.topicName}
        />
    );
}
