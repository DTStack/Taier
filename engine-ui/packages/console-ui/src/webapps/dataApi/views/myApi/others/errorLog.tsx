import * as React from 'react';
import { Table, Modal, Row, Col } from 'antd'
import utils from 'utils'

import ErrorDistributed from '../../../components/errorDistributed';

const errorType: any = {
    1: '禁用',
    2: '未认证',
    3: '参数错误',
    4: '超时',
    5: '超出限制',
    6: '其他'
}
class ErrorLog extends React.Component<any, any> {
    state: any = {
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
        const { showRecord = {}, dateType } = this.props;
        const { apiId } = showRecord;
        this.getErrorInfo(apiId, dateType);
    }
    // eslint-disable-next-line
	UNSAFE_componentWillReceiveProps(nextProps: any) {
        const { showRecord: nextShowRecord = {}, dateType: nextDateType } = nextProps;
        const { showRecord = {}, dateType } = this.props;
        const { apiId } = showRecord;
        const { apiId: nextApiId } = nextShowRecord;

        if (apiId !== nextApiId || dateType !== nextDateType) {
            this.setState({
                apiId: nextProps.showRecord.apiId,
                pageIndex: 1
                // total:0
            },
            () => {
                if (nextProps.slidePaneShow) {
                    this.getErrorInfo(nextApiId, nextDateType);
                }
            })
        }
    }
    getErrorInfo (apiId: any, dateType: any) {
        if (!apiId) {
            return;
        }

        this.props.getApiCallErrorInfo(apiId, dateType)
            .then(
                (res: any) => {
                    if (res) {
                        let dic: any = {}
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
                (res: any) => {
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
            render (text: any) {
                return utils.formatDateTime(text)
            }

        }, {
            title: '错误类型',
            dataIndex: 'bizType',
            key: 'bizType',
            render (text: any) {
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
            render: (text: any, record: any) => {
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
    onTableChange = (page: any, filter: any, sorter: any) => {
        this.setState({
            pageIndex: page.current,
            filter: filter
        },
        () => {
            const { showRecord = {}, dateType } = this.props;
            const { apiId } = showRecord;
            this.getErrorInfo(apiId, dateType);
        });
    }
    lookAllErrorText (text: any) {
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
    getErrorPercent (key: any) {
        return (this.state.error[key] && this.state.error[key].percent) || 0;
    }
    getErrorCount (key: any) {
        return (this.state.error[key] && this.state.error[key].count) || 0;
    }
    render () {
        const { recordInfoList, loading } = this.state;

        return (
            <div style={{ padding: '10px 30px' }}>
                <Row>
                    <Col span={16} style={{ paddingRight: '20px' }}>
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
                            loading={loading}
                            pagination={this.getPagination()}
                            dataSource={this.getSource()}
                            onChange={this.onTableChange}
                            scroll={{ y: 180 }}
                        />
                    </Col>
                    <Col span={8} >
                        <ErrorDistributed chartData={recordInfoList} mode="mini" />
                    </Col>
                </Row>
            </div>
        )
    }
}
export default ErrorLog;
