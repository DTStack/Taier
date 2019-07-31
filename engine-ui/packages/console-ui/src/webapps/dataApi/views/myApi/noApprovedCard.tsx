import * as React from 'react';
import { Card, Table, Input } from 'antd'
import moment from 'moment';

import SlidePane from './approvedSlidePane';
import utils from 'utils'
const sortType: any = {
    'applyTime': 'gmt_modified'
}
const orderType: any = {
    'ascend': 'asc',
    'descend': 'desc'
}
const Search = Input.Search;
class NoApprovedCard extends React.Component<any, any> {
    state: any = {
        pageIndex: 1,
        slidePaneShow: false,
        sortedInfo: {},
        loading: false,
        showRecord: {},
        apiName: utils.getParameterByName('apiName')
    }
    getApplyingList (callback: any) {
        this.setState({
            loading: true
        })
        this.props.getApplyingList(this.state.pageIndex, sortType[this.state.sortedInfo.columnKey], orderType[this.state.sortedInfo.order], this.state.apiName)
            .then(
                () => {
                    this.setState({
                        loading: false
                    })
                    if (callback) {
                        callback();
                    }
                }
            );
    }
    componentDidMount () {
        this.getApplyingList(
            () => {
                this.openCard(this.props.apiId);
            }
        );
    }
    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps(nextProps: any) {
        if (this.props.nowView != nextProps.nowView && nextProps.nowView == 'notApproved') {
            this.getApplyingList();
        }

        if (this.props.apiId != nextProps.apiId && nextProps.apiId) {
            this.openCard(nextProps.apiId);
        }
    }
    openCard (apiId: any) {
        const res = this.getSource();
        if (res) {
            for (let i in res) {
                let item = res[i];

                if (apiId == item.apiId) {
                    this.openApprovedState(item);
                    break;
                }
            }
        }
    }
    // 表格换页/排序
    onTableChange = (page: any, filter: any, sorter: any) => {
        console.log(sorter)
        this.setState({
            pageIndex: page.current,
            sortedInfo: sorter
        }, () => {
            this.getApplyingList();
        });
    }
    openApprovedState (record: any) {
        this.setState({
            slidePaneShow: true,
            showRecord: record || {}
        })
    }
    initColumns () {
        return [{
            title: 'API名称',
            dataIndex: 'apiName',
            key: 'apiName',
            render: (text: any, record: any) => {
                return <a onClick={this.openApprovedState.bind(this, record)} >{text}</a>
            }
        }, {
            title: '申请次数',
            dataIndex: 'callLimit',
            key: 'callLimit',
            width: '100px',
            render (text: any) {
                if (text == -1) {
                    return '无限制'
                }
                return text;
            }
        }, {
            title: '申请周期',
            dataIndex: 'applyDateRange',
            key: 'applyDateRange',
            render (text: any, record: any) {
                if (!record.beginTime || !record.endTime) {
                    return '无时间限制';
                }
                return <span>{moment(record.beginTime).format('YYYY-MM-DD')} ~ {moment(record.endTime).format('YYYY-MM-DD')}</span>
            }
        }, {
            title: '申请说明',
            dataIndex: 'applyContent',
            key: 'applyContent',
            width: '300px'

        }, {
            title: '申请时间',
            dataIndex: 'applyTime',
            key: 'applyTime',
            sorter: true,
            render (text: any) {
                return utils.formatDateTime(text);
            }
        }]
    }
    getSource () {
        return this.props.mine.apiList.applyingList.data;
    }
    getTotal () {
        return (this.props.mine.apiList.applyingList && this.props.mine.apiList.applyingList.totalCount) || 0
    }
    getPagination () {
        return {
            current: this.state.pageIndex,
            pageSize: 20,
            total: this.getTotal()
        }
    }
    closeSlidePane () {
        this.setState({
            slidePaneShow: false,
            showRecord: {}
        })
    }
    handleApiSearch (key: any) {
        this.setState({
            apiName: key,
            pageIndex: 1
        }, () => {
            this.getApplyingList();
        })
    }
    render () {
        const { apiName } = this.state;
        return (
            <div>
                <SlidePane
                    {...this.props}
                    slidePaneShow={this.state.slidePaneShow}
                    showRecord={this.state.showRecord}
                    closeSlidePane={this.closeSlidePane.bind(this)}
                ></SlidePane>
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
                        rowKey="id"
                        rowClassName={
                            (record: any, index: any) => {
                                if (this.state.showRecord.apiId == record.apiId) {
                                    return 'row-select'
                                } else {
                                    return '';
                                }
                            }
                        }
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
export default NoApprovedCard;
