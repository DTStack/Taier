import * as React from 'react';
import { Table, Card, Modal, Select, DatePicker, Tooltip } from 'antd';

import GoBack from 'main/components/go-back';
import { TAG_STATUS } from '../../../consts';
import TCApi from '../../../api/tagConfig';

const Option = Select.Option;
const RangePicker = DatePicker.RangePicker;

export default class TagLog extends React.Component<any, any> {
    state: any = {
        loading: false,
        queryParams: {
            tagId: this.props.routeParams.id,
            currentPage: 1,
            pageSize: 20
        },
        logList: {}
    }

    componentDidMount () {
        this.getTagLogData(this.state.queryParams);
    }

    // 获取日志数据
    getTagLogData = (params: any) => {
        this.setState({ loading: true });

        TCApi.queryTagLogInfo(params).then((res: any) => {
            if (res.code === 1) {
                this.setState({
                    loading: false,
                    logList: res.data
                });
            }
        });
    }

    // table设置
    initColumns = () => {
        return [{
            title: '标签名称',
            dataIndex: 'tagName',
            key: 'tagName',
            width: '15%'
        }, {
            title: '更新时间',
            dataIndex: 'gmtModifiedFormat',
            key: 'gmtModifiedFormat',
            width: '15%'
        }, {
            title: '状态',
            dataIndex: 'status',
            key: 'status',
            width: '15%',
            render: (text: any) => {
                return TAG_STATUS[text];
            }
        }, {
            title: '日志内容',
            dataIndex: 'log',
            key: 'log',
            width: '45%',
            render: (text: any) => {
                return <Tooltip overlayClassName="m-tooltip" placement="bottom" title={text} arrowPointAtCenter>
                    <div className="ellipsis-td">{text}</div>
                </Tooltip>
            }
        }, {
            title: '操作',
            key: 'operation',
            render: (text: any, record: any) => {
                return <a onClick={this.showInfo.bind(this, record)}>
                    查看详情
                </a>
            }
        }];
    }

    showInfo = (record: any) => {
        Modal.info({
            title: `${record.tagName} 日志内容`,
            maskClosable: true,
            width: '40%',
            content: (
                <div style={{ maxHeight: 400, overflowY: 'auto', whiteSpace: 'pre-line' }}>
                    {record.log}
                </div>
            )
        });
    }

    // 更新时间筛选
    onUpdateTimeChange = (date: any, dateString: any) => {
        let queryParams: any = {
            ...this.state.queryParams,
            currentPage: 1,
            startTime: date[0] ? date[0].startOf('day').valueOf() : undefined,
            endTime: date[1] ? date[1].startOf('day').valueOf() : undefined
        };

        this.getTagLogData(queryParams);
        this.setState({ queryParams });
    }

    disabledDate = (current: any) => {
        return current && current.valueOf() > Date.now();
    }

    // 状态筛选
    onStatueChange = (value: any) => {
        let queryParams: any = {
            ...this.state.queryParams,
            currentPage: 1,
            tagStatus: value || undefined
        };

        this.getTagLogData(queryParams);
        this.setState({ queryParams });
    }

    render () {
        const { queryParams, loading, logList } = this.state;

        const cardTitle = (
            <div className="flex font-12">
                <div className="flex" style={{ alignItems: 'center' }}>
                    更新时间：
                    <RangePicker
                        format="YYYY-MM-DD"
                        style={{ width: 250 }}
                        placeholder={['更新开始时间', '更新结束时间']}
                        disabledDate={this.disabledDate}
                        onChange={this.onUpdateTimeChange}
                    />
                </div>

                <div className="m-l-8">
                    状态：
                    <Select
                        allowClear
                        style={{ width: 150 }}
                        placeholder="选择状态"
                        onChange={this.onStatueChange}>
                        <Option key="3">更新完成</Option>
                        <Option key="4">更新失败</Option>
                    </Select>
                </div>
            </div>
        )

        const pagination: any = {
            current: queryParams.currentPage,
            pageSize: queryParams.pageSize,
            total: logList.totalCount
        };

        return (
            <div>
                <h1 className="box-title">
                    <GoBack /> 标签日志更新历史
                </h1>

                <div className="box-2 m-card shadow">
                    <Card
                        title={cardTitle}
                        extra={false}
                        noHovering
                        bordered={false}
                    >
                        <Table
                            rowKey="id"
                            className="m-table fixed-table"
                            columns={this.initColumns()}
                            loading={loading}
                            pagination={pagination}
                            dataSource={logList.data}
                            onChange={this.onTableChange}
                        />
                    </Card>
                </div>
            </div>
        )
    }
}
