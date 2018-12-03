import React, { Component } from 'react';
import { Table, Modal, Row, Col } from 'antd'
import { connect } from 'react-redux';

import utils from 'utils'
import { apiManageActions } from '../../../../actions/apiManage';
import { mineActions } from '../../../../actions/mine';
import AdminErrorDistributed from '../../../../components/errorDistributed';

const errorType = {
    1: 'disable',
    2: 'unauthorize',
    3: 'paramerror',
    4: 'timeout',
    5: 'outlimit',
    6: 'other'
}
const errorExchange = {
    disable: '禁用',
    unauthorize: '未认证',
    paramerror: '参数错误',
    timeout: '超时',
    outlimit: '超过限制',
    other: '其他'

}
const mapStateToProps = state => {
    return {}
};

const mapDispatchToProps = dispatch => ({
    getApiCallErrorInfo (id, date) {
        return dispatch(apiManageActions.getApiCallErrorInfo({
            apiId: id,
            time: date
        }));
    },
    queryApiCallLog (id, currentPage, bizType, date) {
        return dispatch(mineActions.queryApiCallLog({
            apiId: id,
            currentPage: currentPage,
            bizType: bizType,
            useAdmin: true,
            time: date
        }));
    }
});

@connect(mapStateToProps, mapDispatchToProps)
class ManageErrorLog extends Component {
    state = {
        error: {

        },

        data: [],
        loading: false,
        pageIndex: 1,
        total: 0,
        filter: {},
        recordInfoList: []
    }
    componentDidMount () {
        const { apiId, dateType } = this.props;
        this.getErrorInfo(apiId, dateType);
    }
    // eslint-disable-next-line
	UNSAFE_componentWillReceiveProps (nextProps) {
        const { apiId, dateType } = this.props;
        const { apiId: nextApiId, dateType: nextDateType } = nextProps;
        if (apiId != nextApiId || dateType != nextDateType) {
            this.setState({
                pageIndex: 1
            }, () => {
                this.getErrorInfo(nextApiId, nextDateType);
            })
        }
    }
    getErrorInfo (apiId, dateType) {
        if (!apiId) {
            return;
        }
        this.props.getApiCallErrorInfo(apiId, dateType)
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
                            error: dic,
                            recordInfoList: res.data.recordInfoList
                        })
                    }
                }
            )
        this.props.queryApiCallLog(apiId, this.state.pageIndex, this.state.filter.bizType && this.state.filter.bizType[0], dateType)
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
    initColumns () {
        return [{
            title: '调用时间',
            dataIndex: 'invokeTime',
            key: 'invokeTime',
            render (text) {
                return utils.formatDateTime(text)
            }

        }, {
            title: '调用用户',
            dataIndex: 'userName',
            key: 'userName'

        }, {
            title: '错误类型',
            dataIndex: 'bizType',
            key: 'bizType',
            width: '100px',
            render (text) {
                return errorExchange[errorType[text]]
            },
            filters: [
                { text: '参数错误', value: '3' },
                { text: '禁用', value: '1' },
                { text: '未认证', value: '2' },
                { text: '超时', value: '4' },
                { text: '超过限制', value: '5' },
                { text: '其他', value: '6' }
            ],
            filterMultiple: false
        }, {
            title: '错误日志',
            dataIndex: 'content',
            key: 'content',
            width: '250px'

        }, {
            title: '操作',
            dataIndex: '',
            key: 'deal',
            width: '100px',
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
            pageSize: 10,
            total: this.state.total
        }
    }
    getSource () {
        return this.state.data
    }
    // 表格换页/排序
    onTableChange = (page, filter, sorter) => {
        this.setState({
            pageIndex: page.current,
            filter: filter
        },
        () => {
            const { apiId, dateType } = this.props;
            this.getErrorInfo(apiId, dateType);
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
        const { recordInfoList } = this.state;
        const data = this.getSource();
        let className = 'm-table monitor-table table-p-l20'
        if (data.length < 3) {
            className += ' mini-filter'
        }
        return (
            <div>
                <Row>
                    <Col span={16} style={{ paddingRight: '20px' }}>
                        <p style={{ lineHeight: '1', padding: '14px 0px 14px 8px' }} className="child-span-padding-r20">
                            <span>参数错误: {this.getErrorPercent('paramerror')}% ({this.getErrorCount('paramerror')}次)</span>
                            <span>禁用: {this.getErrorPercent('disable')}% ({this.getErrorCount('disable')}次)</span>
                            <span>未认证: {this.getErrorPercent('unauthorize')}% ({this.getErrorCount('unauthorize')}次)</span>
                            <span>超时: {this.getErrorPercent('timeout')}% ({this.getErrorCount('timeout')}次)</span>
                            <span>超过限制: {this.getErrorPercent('outlimit')}% ({this.getErrorCount('outlimit')}次)</span>
                            <span>其他: {this.getErrorPercent('other')}% ({this.getErrorCount('other')}次)</span>
                        </p>
                        <Table
                            rowKey="id"
                            className={className}
                            columns={this.initColumns()}
                            loading={this.state.loading}
                            pagination={this.getPagination()}
                            dataSource={data}
                            scroll={{ y: 180 }}
                            onChange={this.onTableChange}
                        />
                    </Col>
                    <Col span={8} >
                        <AdminErrorDistributed chartData={recordInfoList} mode="mini" />
                    </Col>
                </Row>
            </div>
        )
    }
}
export default ManageErrorLog;
