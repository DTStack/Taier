import React, { useEffect, useState } from 'react';
import { Pagination, Table, TablePaginationConfig } from 'antd';
import { ColumnsType, FilterValue, SorterResult } from 'antd/lib/table/interface';
import moment from 'moment';

import stream from '@/api';
import reqStream from '@/api/request';

interface HistoryLogProps {
    id: number;
    jobId: string | number;
    isShow: boolean;
}

interface IDataSource {
    id: number;
    execStartTime: string;
    execEndTime: string;
    applicationId: number;
}

function HistoryLog(props: HistoryLogProps) {
    const { id, isShow, jobId } = props;
    const [tableData, setTableData] = useState([]);
    const [pageInfo, setPageInfo] = useState({ current: 1, total: 0, pageSize: 20 });

    useEffect(() => {
        if (isShow) {
            getTableData();
        }
    }, [id, isShow]);

    function getTableData(page = 1, pageSize = 20, sort?: string, orderBy?: React.Key) {
        stream.getHistoryLog({ taskId: props.id, currentPage: page, pageSize, sort, orderBy }).then((res) => {
            if (res.code === 1) {
                setTableData(res.data?.data || []);
                setPageInfo({ current: page, pageSize, total: res.data?.totalCount || 0 });
            }
        });
    }

    function sorter(
        pagination: TablePaginationConfig,
        filters: Record<string, FilterValue | null>,
        sorter: SorterResult<any> | SorterResult<any>[]
    ) {
        if (Array.isArray(sorter)) return;
        const obj = {
            ascend: 'asc',
            descend: 'desc',
        };
        getTableData(pageInfo.current, pageInfo.pageSize, obj[sorter.order!], sorter.columnKey);
    }

    const columns: ColumnsType<IDataSource> = [
        { title: 'Application ID', dataIndex: 'applicationId', sorter: true, width: '370px' },
        {
            title: '开始时间',
            dataIndex: 'execStartTime',
            sorter: true,
            width: '280px',
            render: (text) => (text ? moment(text).format('YYYY-MM-DD HH:mm:ss') : '-'),
        },
        {
            title: '结束时间',
            dataIndex: 'execEndTime',
            sorter: true,
            width: '280px',
            render: (text) => (text ? moment(text).format('YYYY-MM-DD HH:mm:ss') : '-'),
        },
        {
            title: '操作',
            width: '140px',
            render: (text, record) => {
                const href =
                    reqStream.DOWNLOAD_HISTORY_LOG + '?applicationId=' + record.applicationId + '&jobId=' + jobId;
                return (
                    <a download href={href}>
                        下载
                    </a>
                );
            },
        },
    ];

    const pagination = () => (
        <Pagination
            {...pageInfo}
            size="small"
            pageSizeOptions={['20', '50', '100']}
            showSizeChanger
            showTotal={(total) => (
                <>
                    共<span style={{ color: '#3F87FF' }}>{total}</span>条数据
                </>
            )}
            onChange={getTableData}
            onShowSizeChange={getTableData}
        />
    );

    return (
        <div className="dt-table-fixed-contain-footer" style={{ padding: '0 20px' }}>
            <Table
                dataSource={tableData}
                columns={columns}
                rowKey="id"
                className="dt-table-border dt-table-fixed-overflowx-auto"
                style={{ height: 'calc(100vh - 310px)', boxShadow: 'none' }}
                size="middle"
                pagination={false}
                footer={pagination}
                onChange={sorter}
            />
        </div>
    );
}

export default HistoryLog;
