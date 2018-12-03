import React, { Component } from 'react'
import { connect } from 'react-redux';
import { Link } from 'react-router';
import { Card, Input, Select, Table, Modal } from 'antd';

import { apiMarketActions } from '../../actions/apiMarket';
import utils from 'utils';
import ApplyBox from './applyBox';

const Option = Select.Option;
const Search = Input.Search;
let modal;
const mapStateToProps = state => {
    const { user, apiMarket } = state;
    return { apiMarket, user }
};

const mapDispatchToProps = dispatch => ({
    getCatalogue (pid) {
        dispatch(apiMarketActions.getCatalogue(pid));
    },
    getApiMarketList (params) {
        return dispatch(apiMarketActions.getApiMarketList(params));
    }
});

@connect(mapStateToProps, mapDispatchToProps)
class APIMarket extends Component {
    state = {
        searchValue: '',
        pageIndex: 1,
        loading: true,
        applyBox: false,
        apply: {
            apiId: '',
            tagName: '',
            tagDesc: ''
        },
        type1: undefined,
        type2: undefined,
        apiName: '',
        pageSize: 20,
        total: 0,
        sorter: {}
    }
    getMarketApi () {
        this.setState({
            loading: true
        })
        const dic = {
            updateTime: 'gmt_modified'
        }
        const orderType = {
            'ascend': 'asc',
            'descend': 'desc'
        }

        this.props.getApiMarketList({
            name: this.state.searchValue,
            pid: this.state.type1 || -1,
            cid: this.state.type2 || -1,
            currentPage: this.state.pageIndex,
            pageSize: this.state.pageSize,
            orderBy: dic[this.state.sorter.columnKey],
            sort: orderType[this.state.sorter.order]
        }).then((res) => {
            console.log('apigetOver');

            this.setState({
                loading: false,
                total: res.data.totalCount
            })
        }).catch((e) => {
            this.setState({
                loading: false
            })
        })
    }
    componentDidMount () {
        this.props.getCatalogue(0);
        this.getMarketApi();
    }
    renderSourceType (id, root) {
        function arrToOptions (arr) {
            if (!arr || arr.length < 1) {
                return null;
            }
            return arr.map(
                (item) => {
                    return <Option key={item.id}>{item.name}</Option>
                }
            )
        }
        let arr = [];
        // 获取子节点
        const items = this.props.apiMarket.apiCatalogue;

        // 一级目录
        if (root) {
            if (!items) {
                return null;
            }
            for (let i = 0; i < items.length; i++) {
                arr.push({
                    id: items[i].id,
                    name: items[i].catalogueName
                })
            }

            return arrToOptions(arr);
        } else { // 二级目录
            if (!items) {
                return null;
            }
            let itemChild;// 二级目录
            // 查找二级目录
            for (let i = 0; i < items.length; i++) {
                if (items[i].id == id) {
                    itemChild = items[i].childCatalogue;
                    break;
                }
            }
            // 找不到，则返回null
            if (!itemChild) {
                return null;
            }

            for (let i = 0; i < itemChild.length; i++) {
                if (itemChild[i].api) {
                    continue;
                }
                arr.push({
                    id: itemChild[i].id,
                    name: itemChild[i].catalogueName
                })
            }
            return arrToOptions(arr);
        }
    }
    onSourceChange (key) {
        this.setState({
            type1: key,
            type2: undefined
        }, () => {
            this.getMarketApi();
        })
    }
    onUserSourceChange (key) {
        this.setState({
            type2: key
        }, () => {
            this.getMarketApi();
        })
    }
    handleSearch (value) {
        this.setState({
            searchValue: value,
            pageIndex: 1
        }, () => {
            this.getMarketApi();
        })
    }
    getDealType (type) {
        const dic = {
            'complete': '查看使用情况',
            'nothing': '申请',
            'applying': '查看审批进度'
        }
        return dic[type || 'nothing']
    }

    doApply (record) {
        this.setState({
            applyBox: true,
            apply: {
                name: record.name,
                apiId: record.apiId,
                tagDesc: record.tagDesc
            }
        })
        console.log('dealnothing', record);
    }

    // 表格换页/排序
    onTableChange = (page, filter, sorter) => {
        this.setState({
            pageIndex: page.current,
            sorter: sorter

        }, () => {
            this.getMarketApi();
        });
    }

    openDetail = (id) => {
        console.log(id)
        window.open(`dataLabel.html#/dl/market/detail/${id}?isHideBack=true`);
    }

    initColumns () {
        return [{
            title: '标签名称',
            dataIndex: 'name',
            key: 'name',
            width: '20%',
            render: (text, record) => {
                return <Link to={`/dl/market/detail/${record.apiId}`}>{text}</Link>
                // <a onClick={this.openDetail.bind(this, record.id)}>{text}</a>
            }
        }, {
            title: '值域',
            dataIndex: 'tagRange',
            key: 'tagRange',
            width: '15%'
        },
        // {
        //     title: '覆盖数',
        //     dataIndex: 'overlayNum',
        //     key: 'overlayNum',
        //     width: '15%',
        // },
        {
            title: '昨日调用次数',
            dataIndex: 'totalYd',
            key: 'totalYd',
            width: '15%'
        }, {
            title: '更新时间',
            dataIndex: 'gmtModified',
            key: 'gmtModified',
            width: '20%',
            render (time) {
                return utils.formatDateTime(time);
            },
            sorter: true
        }, {
            title: '操作',
            dataIndex: 'applyStatus',
            width: '15%',
            render: (status, record) => {
                switch (status) {
                    case 0: {
                        return <Link to={`/dl/mine?apiId=${record.id}`}>查看审批进度</Link>;
                    }
                    case 1:
                    case 3:
                    case 4: {
                        return <Link to={`/dl/mine/approved?apiId=${record.id}`}>查看使用情况</Link>;
                    }
                    case -1:
                    case 2: {
                        return <a onClick={this.doApply.bind(this, record)}>申请</a>;
                    }
                    default: return '';
                }
            }
        }]
    }

    getPagination () {
        return {
            current: this.state.pageIndex,
            pageSize: this.state.pageSize,
            total: this.state.total
        }
    }

    getCardTitle () {
        return (
            <div className="flex font-12">
                <Search
                    placeholder="输入标签名称搜索"
                    style={{ width: 150, margin: '10px 0' }}
                    onSearch={this.handleSearch.bind(this)}
                />
                <div className="m-l-8">
                    标签分类：
                    <Select
                        placeholder="选择标签分类"
                        value={this.state.type1}
                        allowClear
                        onChange={this.onSourceChange.bind(this)} style={{ width: 120 }}
                    >
                        {
                            this.renderSourceType(0, true)
                        }
                    </Select>
                </div>
                <div className="m-l-8">
                    二级分类：
                    <Select
                        placeholder="选择二级分类"
                        value={this.state.type2}
                        allowClear
                        onChange={this.onUserSourceChange.bind(this)}
                        style={{ width: 150 }}
                    >
                        {
                            this.renderSourceType(this.state.type1, false)
                        }
                    </Select>
                </div>
            </div>
        )
    }

    jumpToMine () {
        modal.destroy();
        this.props.router.push('/dl/mine');
    }

    showApplySuccessModal () {
        modal = Modal.success({
            title: '申请提交成功',
            content: (
                <span>您可以在 <a onClick={this.jumpToMine.bind(this)}>我的标签</a> 中查看审批进度</span>
            ),
            okText: '确定'
        });
    }
    handleOk () {
        this.setState({
            applyBox: false
        });
        this.showApplySuccessModal();
    }
    handleCancel () {
        this.setState({
            applyBox: false
        })
    }

    render () {
        const { apiMarket } = this.props;
        return (
            <div className="api-market">
                <ApplyBox show={this.state.applyBox}
                    successCallBack={this.handleOk.bind(this)}
                    cancelCallback={this.handleCancel.bind(this)}
                    apiId={this.state.apply.apiId}
                    name={this.state.apply.name}
                    desc={this.state.apply.tagDesc}
                    getMarketApi={this.getMarketApi.bind(this)}
                ></ApplyBox>
                <div className="m-card box-1">
                    <Card
                        noHovering
                        title={this.getCardTitle()}
                    >
                        <Table
                            className="m-table monitor-table"
                            columns={this.initColumns()}
                            loading={this.state.loading}
                            pagination={this.getPagination()}
                            dataSource={apiMarket.apiList || []}
                            onChange={this.onTableChange}
                        />
                    </Card>
                </div>
            </div>
        )
    }
}

export default APIMarket
