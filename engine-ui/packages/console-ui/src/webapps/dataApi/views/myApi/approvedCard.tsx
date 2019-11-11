import * as React from 'react';
import { Card, Table, Modal, message, Input } from 'antd';

import ApplyBox from '../../components/applyBox'
import SlidePane from './approvedSlidePane';
import SlidePaneDisabled from './disabledCardSlidePane'
import SlidePaneDetail from './detailSlidePane'
import utils from 'utils'
import { API_USER_STATUS } from '../../consts';

const confirm = Modal.confirm;
const Search = Input.Search;
const exchangeDic: any = {
    0: 'inhand',
    1: 'success',
    2: 'notPass',
    3: 'stop',
    4: 'disabled',
    5: 'expired'
}

const sortType: any = {
    'applyTime': 'gmt_create'
}
const orderType: any = {
    'ascend': 'asc',
    'descend': 'desc'
}
class ApprovedCard extends React.Component<any, any> {
    state: any = {
        pageIndex: 1,
        slidePaneShowNoApproved: false,
        slidePaneShowDisabled: false,
        slidePaneShowSuccess: false,
        loading: false,
        sortedInfo: {},
        filterInfo: {},
        showRecord: {},
        apiName: utils.getParameterByName('apiName'),
        applyBox: false,
        applyRecord: {},
        applyKey: Math.random()

    }
    getAppliedList () {
        this.setState({
            loading: true
        })
        return this.props.getAppliedList(
            this.state.pageIndex,
            sortType[this.state.sortedInfo.columnKey],
            orderType[this.state.sortedInfo.order],
            this.state.filterInfo.status,
            this.state.apiName
        )

            .then(
                (res: any) => {
                    this.setState({
                        loading: false
                    })
                    return res;
                }
            );
    }
    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps (nextProps: any) {
        const project = nextProps.project
        const oldProj = this.props.project
        if (oldProj && project && oldProj.id !== project.id) {
            this.getAppliedList()
        }
    }
    componentDidMount () {
        this.getAppliedList()
            .then(
                (res: any) => {
                    if (this.props.apiId) {
                        if (res) {
                            for (let i in res.data.data) {
                                let item = res.data.data[i];
                                if (this.props.apiId == item.apiId) {
                                    this.apiClick(item);
                                    break;
                                }
                            }
                        }
                    }
                }
            );
    }
    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps(nextProps: any) {
        if (this.props.nowView != nextProps.nowView && nextProps.nowView == 'approved') {
            this.getAppliedList();
        }
        if (this.props.apiId != nextProps.apiId && nextProps.apiId) {
            const res = this.getSource();
            if (res) {
                for (let i in res.data) {
                    let item = res.data[i];
                    if (nextProps.apiId == item.apiId) {
                        this.dealClick(item);
                        break;
                    }
                }
            }
        }
    }
    // 表格换页/排序
    onTableChange = (page: any, filter: any, sorter: any) => {
        console.log(filter);
        this.setState({
            pageIndex: page.current,
            sortedInfo: sorter,
            filterInfo: filter
        }, () => {
            this.getAppliedList();
        });
    }
    // 关闭pane
    closeSlidePane () {
        this.setState({
            slidePaneShowNoApproved: false,
            slidePaneShowDisabled: false,
            slidePaneShowSuccess: false,
            showRecord: {}
        })
    }
    apiClick (record: any) {
        const method = (this as any)['state' + exchangeDic[record.status]]
        if (method) {
            method.call(this, record);
        }
    }
    statesuccess (record: any) {
        this.setState({
            slidePaneShowSuccess: true,
            slidePaneShowNoApproved: false,
            slidePaneShowDisabled: false,
            showRecord: record || {}

        })
    }
    statenotPass (record: any) {
        this.setState({
            slidePaneShowSuccess: false,
            slidePaneShowNoApproved: true,
            slidePaneShowDisabled: false,
            showRecord: record || {}
        })
    }
    statestop (record: any) {
        this.setState({
            slidePaneShowSuccess: true,
            slidePaneShowNoApproved: false,
            slidePaneShowDisabled: false,
            showRecord: record || {}
        })
    }
    statedisabled (record: any) {
        this.setState({
            slidePaneShowSuccess: false,
            slidePaneShowNoApproved: false,
            slidePaneShowDisabled: true,
            showRecord: record || {}
        })
    }
    stateexpired (record: any) {
        this.setState({
            slidePaneShowSuccess: true,
            slidePaneShowNoApproved: false,
            slidePaneShowDisabled: false,
            showRecord: record || {}

        })
    }
    dealClick (record: any) {
        const method = (this as any)['deal' + exchangeDic[record.status]];
        if (method) {
            method.call(this, record);
        }
    }
    dealsuccess (record: any) {
        confirm({
            title: '确认停止',
            content: '确认停止接口？',
            onOk: () => {
                this.props.updateApplyStatus(record.id, 3)
                    .then(
                        (res: any) => {
                            if (res) {
                                message.success('停止成功')
                            }
                            this.getAppliedList();
                        }
                    )
            },
            onCancel () {
                console.log('Cancel');
            }
        });
    }

    dealstop (record: any) {
        confirm({
            title: '确认开启',
            content: '确认开启接口？',
            onOk: () => {
                this.props.updateApplyStatus(record.id, 1)
                    .then(
                        (res: any) => {
                            if (res) {
                                message.success('开启成功')
                            }
                            this.getAppliedList();
                        }
                    )
            },
            onCancel () {
                console.log('Cancel');
            }
        });
    }

    dealnotPass (record: any) {
        this.setState({
            applyBox: true,
            applyRecord: record,
            applyKey: Math.random()
        })
    }
    dealdisabled (record: any) {
        this.apiClick(record);
    }
    dealexpired (record: any) {
        this.setState({
            applyBox: true,
            applyRecord: record,
            applyKey: Math.random()
        })
    }
    deleteApi (record: any) {

    }
    initColumns () {
        return [{
            title: 'API名称',
            dataIndex: 'apiName',
            key: 'apiName',
            render: (text: any, record: any) => {
                const isDeleted = record.apiDeleted == 1;
                const disabled = record.apiStatus == 1;
                if (isDeleted) {
                    return <a className={'disable-all'} onClick={this.apiClick.bind(this, record)} >{text + '(已删除)'}</a>
                } else if (disabled) {
                    return <a className={'disable-all'} onClick={this.apiClick.bind(this, record)} >{text + '(全平台禁用)'}</a>
                } else {
                    return <a onClick={this.apiClick.bind(this, record)} >{text}</a>
                }
            }
        }, {
            title: '授权状态',
            dataIndex: 'status',
            key: 'status',
            render (text: any) {
                const dic: any = {
                    success: '已通过',
                    disabled: '取消授权',
                    stop: '停用',
                    notPass: '已拒绝',
                    expired: '已过期'

                }
                return <span className={`state-${exchangeDic[text]}`}>{dic[exchangeDic[text]]}</span>
            },
            filters: [
                { text: '已通过', value: '1' },
                { text: '已拒绝', value: '2' },
                { text: '停用', value: '3' },
                { text: '取消授权', value: '4' },
                { text: '已过期', value: '5' }

            ]
        }, {
            title: 'API描述',
            dataIndex: 'apiDesc',
            key: 'apiDesc',
            width: '150px'

        }, {
            title: '最近24小时调用(次)',
            dataIndex: 'recentCallNum',
            key: 'recentCallNum'

        }, {
            title: '累计调用',
            dataIndex: 'totalCallNum',
            key: 'totalCallNum'

        }, {
            title: '订购时间',
            dataIndex: 'applyTime',
            key: 'applyTime',
            sorter: true,
            render (text: any) {
                return utils.formatDateTime(text)
            }
        }, {
            title: '操作',
            dataIndex: 'deal',
            key: 'deal',
            render: (text: any, record: any) => {
                const isDeleted = record.apiDeleted == 1;
                const dic: any = {
                    success: '停用',
                    stop: '启用',
                    notPass: '再次申请',
                    expired: '再次申请',
                    disabled: '申请恢复'
                }
                if (isDeleted) {
                    return null;
                }
                // eslint-disable-next-line
                const deleteButton = record.status == API_USER_STATUS.PASS ? null : (
                    <span><span className="ant-divider" ></span> <a onClick={this.deleteApi.bind(this, record)}>删除</a></span>
                );

                if (dic[exchangeDic[record.status]]) {
                    return <span><a onClick={this.dealClick.bind(this, record)}>{dic[exchangeDic[record.status]]}</a></span>
                }
                return null;
            }
        }]
    }

    getSource () {
        return this.props.mine.apiList.appliedList.data || [];
    }

    getTotal () {
        return (this.props.mine.apiList.appliedList && this.props.mine.apiList.appliedList.totalCount) || 0
    }

    getPagination () {
        return {
            current: this.state.pageIndex,
            pageSize: 20,
            total: this.getTotal()
        }
    }

    handleApiSearch (key: any) {
        this.setState({
            apiName: key,
            pageIndex: 1
        }, () => {
            this.getAppliedList();
        })
    }

    handleOk () {
        this.setState({
            applyBox: false
        });
        this.getAppliedList();
    }

    handleCancel () {
        this.setState({
            applyBox: false
        })
    }

    render () {
        const { applyBox, applyRecord, apiName, applyKey } = this.state;

        return (
            <div>
                <ApplyBox
                    show={applyBox}
                    successCallBack={this.handleOk.bind(this)}
                    cancelCallback={this.handleCancel.bind(this)}
                    apiId={applyRecord.apiId}
                    apiName={applyRecord.apiName}
                    desc={applyRecord.apiDesc}
                    hideJump={true}
                    key={applyKey}
                />
                <SlidePane
                    {...this.props}
                    isApproved={true}
                    showRecord={this.state.showRecord}
                    slidePaneShow={this.state.slidePaneShowNoApproved}
                    closeSlidePane={this.closeSlidePane.bind(this)}
                ></SlidePane>
                <SlidePaneDisabled
                    {...this.props}
                    showRecord={this.state.showRecord}
                    slidePaneShow={this.state.slidePaneShowDisabled}
                    closeSlidePane={this.closeSlidePane.bind(this)}
                >
                </SlidePaneDisabled>
                <SlidePaneDetail
                    style={{ right: '0px' }}
                    {...this.props}
                    showRecord={this.state.showRecord}
                    slidePaneShow={this.state.slidePaneShowSuccess}
                    closeSlidePane={this.closeSlidePane.bind(this)}
                >
                </SlidePaneDetail>
                <Card
                    noHovering
                >
                    <div className="flex font-12">
                        <Search
                            placeholder="输入API名称搜索"
                            style={{ width: 150, margin: '10px 0px', marginLeft: '20px' }}
                            onSearch={this.handleApiSearch.bind(this)}
                            defaultValue={apiName}
                        />
                    </div>
                    <Table
                        rowClassName={
                            (record: any, index: any) => {
                                if (this.state.showRecord.apiId == record.apiId) {
                                    return 'row-select'
                                } else {
                                    return '';
                                }
                            }
                        }
                        rowKey="apiId"
                        className="m-table monitor-table"
                        columns={this.initColumns()}
                        loading={this.state.loading}
                        pagination={this.getPagination()}
                        dataSource={this.getSource()}
                        onChange={this.onTableChange}
                    />
                </Card>
            </div>
        )
    }
}
export default ApprovedCard;
