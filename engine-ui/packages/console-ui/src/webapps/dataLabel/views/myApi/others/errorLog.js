import React, { Component } from 'react';
import { Table, Modal } from 'antd'
import utils from 'utils'
const errorType = {
    1: '禁用',
    2: '未认证',
    3: '参数错误',
    4: '超时',
    5: '超出限制',
    6: '其他'
}
class errorLog extends Component {
    state = {
        error: {

        },

        data: [],
        loading: false,
        pageIndex: 1,
        total: 0,
        filter: {}
    }
    componentDidMount () {
        this.getErrorInfo();
    }
    getErrorInfo (apiId) {
        apiId = apiId || this.props.showRecord.apiId;
        if (!apiId) {
            return;
        }

        this.props.getApiCallErrorInfo(apiId)
            .then(
                (res) => {
                    if (res) {
                        let dic = {}
                        for (let i = 0; i < res.data.recordInfoList.length; i++) {
                            let item = res.data.recordInfoList[i];
                            let key = errorType[item.type];

                            if (key) {
                                dic[key] = {
                                    percent: item.rate,
                                    count: item.callNum
                                }
                            }
                        }
                        this.setState({
                            error: dic
                        })
                    }
                }
            )
        this.props.queryApiCallLog(apiId, this.state.pageIndex, this.state.filter.bizType && this.state.filter.bizType[0])
            .then(
                (res) => {
                    if (res) {
                        this.setState({
                            data: res.data.data,
                            total: res.data.totalCount
                        })
                    }
                }
            )
    }

    componentWillReceiveProps (nextProps) {
        if (
            (this.props.showRecord && this.props.showRecord.apiId !== nextProps.showRecord.apiId)
        ) {
            if (nextProps.slidePaneShow) {
                this.getErrorInfo(nextProps.showRecord.apiId);
            }
        }
    }
    initColumns () {
        return [{
            title: '调用时间',
            dataIndex: 'invokeTime',
            key: 'invokeTime',
            render (text) {
                return utils.formatDateTime(text)
            }

        }, {
            title: '错误类型',
            dataIndex: 'bizType',
            key: 'bizType',
            render (text) {
                return errorType[text]
            },
            filters: [
                { text: '参数错误', value: '3' },
                { text: '禁用', value: '1' },
                { text: '未认证', value: '2' },
                { text: '超时', value: '4' },
                { text: '超出限制', value: '5' },
                { text: '其他', value: '6' }
            ],
            filterMultiple: false
        }, {
            title: '错误日志',
            dataIndex: 'content',
            key: 'content',
            width: '50%'

        }, {
            title: '操作',
            dataIndex: '',
            key: 'deal',
            render: (text, record) => {
                return (
                    <a onClick={this.lookAllErrorText.bind(this, record.content)}>查看全部</a>
                )
            }
        }]
    }
    getPagination () {
        return {
            current: this.state.pageIndex,
            pageSize: 5,
            total: this.state.total
        }
    }
    getSource () {
        return this.state.data;
    }
    // 表格换页/排序
    onTableChange = (page, filter, sorter) => {
        this.setState({
            pageIndex: page.current,
            filter: filter
        },
        () => {
            this.getErrorInfo();
        });
    }
    lookAllErrorText (text) {
        Modal.info({
            title: '错误日志',
            content: (
                <div>
                    <p>{text}</p>

                </div>
            ),
            onOk () { }
        });
    }
    getErrorPercent (key) {
        return this.state.error[key] && this.state.error[key].percent || 0;
    }
    getErrorCount (key) {
        return this.state.error[key] && this.state.error[key].count || 0;
    }
    render () {
        return (
            <div style={{ paddingLeft: 20 }}>
                <p style={{ lineHeight: '30px', paddingLeft: '20px' }} className="child-span-padding-r20">
                    <span>参数错误: {this.getErrorPercent('参数错误')}% ({this.getErrorCount('参数错误')}次)</span>
                    <span>禁用: {this.getErrorPercent('禁用')}% ({this.getErrorCount('禁用')}次)</span>
                    <span>未认证: {this.getErrorPercent('未认证')}% ({this.getErrorCount('未认证')}次)</span>
                    <span>超时: {this.getErrorPercent('超时')}% ({this.getErrorCount('超时')}次)</span>
                    <span>超出限制: {this.getErrorPercent('超出限制')}% ({this.getErrorCount('超出限制')}次)</span>
                    <span>其他: {this.getErrorPercent('其他')}% ({this.getErrorCount('其他')}次)</span>
                </p>
                <Table
                    rowKey="id"
                    className="m-table monitor-table"

                    columns={this.initColumns()}
                    loading={this.state.loading}
                    pagination={this.getPagination()}
                    dataSource={this.getSource()}
                    onChange={this.onTableChange}
                />
            </div>
        )
    }
}
export default errorLog;
