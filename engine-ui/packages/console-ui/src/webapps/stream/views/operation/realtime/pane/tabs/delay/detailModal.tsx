import * as React from 'react'
import utils from 'utils'

import { Table, Modal, Button } from 'antd'
import Api from '../../../../../../api'
import DetailGraph from './detailGraph';

class DelayDetailModal extends React.Component<any, any> {
    state: any = {
        pagination: {
            total: 0,
            pageSize: 5,
            current: 1
        },
        delayList: [],
        loading: false,
        detailVisible: false,
        detailRecord: {},
        sorter: {},
        expandedRowKeys: []
    }

    componentDidMount () {
        this.getDelayList();
    }
    // eslint-disable-next-line
	UNSAFE_componentWillReceiveProps(nextProps: any) {
        const { taskId, topicName } = this.props;
        const { taskId: nextTaskId, topicName: nextTopicName } = nextProps;
        if (taskId != nextTaskId || topicName != nextTopicName) {
            this.initPage();
            this.getDelayList(nextTaskId, nextTopicName);
        }
    }
    initPage () {
        this.setState({
            pagination: {
                ...this.state.pagination,
                total: 0,
                current: 1
            }
        })
    }
    getDelayList (taskId: any, topicName: any) {
        taskId = typeof taskId == 'undefined' ? this.props.taskId : taskId;
        topicName = typeof topicName == 'undefined' ? this.props.topicName : topicName;
        if (!taskId || !topicName) {
            return;
        }

        const { pagination, sorter } = this.state;

        this.setState({
            delayList: []
        })

        let extParams: any = {};
        /**
         * 排序字段
         */
        extParams.orderBy = sorter.columnKey;
        extParams.sort = utils.exchangeOrder(sorter.order);

        this.setState({
            loading: true
        })

        Api.getTopicDetail({
            taskId,
            topicName,
            currentPage: pagination.current,
            pageSize: pagination.pageSize,
            ...extParams
        }).then(
            (res: any) => {
                if (res.code == 1) {
                    this.setState({
                        delayList: res.data,
                        pagination: {
                            ...pagination,
                            total: res.data.length
                        }
                    })
                }
                this.setState({
                    loading: false
                })
            }
        )
    }
    initDelayListColumns () {
        return [{
            title: '分区ID',
            dataIndex: 'partitionId',
            width: 130
        }, {
            title: '延迟消息数（条）',
            dataIndex: 'delayCount',
            width: 130,
            sorter: (a: any, b: any) => {
                return a - b
            }
        }, {
            title: '总消息数',
            dataIndex: 'totalDelayCount',
            width: 110,
            sorter: (a: any, b: any) => {
                return a - b
            }
        }, {
            title: '当前消费位置',
            dataIndex: 'currentLocation',
            width: 110
        }, {
            title: '操作',
            dataIndex: 'deal',
            width: '100px',
            render: (text: any, record: any) => {
                const { expandedRowKeys } = this.state;
                if (expandedRowKeys[0] == record.partitionId) {
                    return <a onClick={this.onExpand.bind(this, false, record)}>关闭详情</a>
                } else {
                    return <a onClick={this.onExpand.bind(this, true, record)}>查看详情</a>
                }
            }
        }]
    }
    showDetail (record: any) {
        this.setState({
            expandedRowKeys: [record.partitionId]
        })
    }
    closeDetail () {
        this.setState({
            detailRecord: {},
            detailVisible: false
        })
    }
    onTableChange (page: any, filters: any, sorter: any) {
        const { pagination } = this.state;
        this.setState({
            pagination: {
                ...pagination,
                current: page.current
            },
            sorter: sorter,
            expandedRowKeys: []
        })
    }
    expandedRowRender (record: any) {
        const { taskId, topicName } = this.props;
        const { partitionId } = record;
        return <DetailGraph taskId={taskId} partitionId={partitionId} topicName={topicName} />
    }
    onExpand (expanded: any, record: any) {
        if (expanded) {
            this.setState({
                expandedRowKeys: [record.partitionId]
            })
        } else {
            this.setState({
                expandedRowKeys: []
            })
        }
    }
    render () {
        const { delayList, pagination, loading, expandedRowKeys } = this.state;
        return (
            <Modal
                title="数据延迟（最近24小时）"
                visible={this.props.visible}
                onCancel={this.props.closeDetail}
                width={700}
                footer={(
                    <Button onClick={this.props.closeDetail}>关闭</Button>
                )}
            >
                <Table
                    rowKey="partitionId"
                    className="m-table"
                    columns={this.initDelayListColumns()}
                    dataSource={delayList}
                    pagination={pagination}
                    loading={loading}
                    expandedRowRender={this.expandedRowRender.bind(this)}
                    onChange={this.onTableChange.bind(this)}
                    expandedRowKeys={expandedRowKeys}
                    onExpand={this.onExpand.bind(this)}
                />
            </Modal>
        )
    }
}

export default DelayDetailModal;
