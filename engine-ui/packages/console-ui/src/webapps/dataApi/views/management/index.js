import React, { Component } from 'react'
import { Card, Input, Select, Table, Button, Checkbox, Modal, message, Cascader } from 'antd'
import { connect } from 'react-redux';

import ApiSlidePane from './apiDetail/apiSlide';
import { apiMarketActions } from '../../actions/apiMarket';
import { apiManageActions } from '../../actions/apiManage';
import { dataSourceActions } from '../../actions/dataSource';
import { EXCHANGE_ADMIN_API_STATUS, API_SYSTEM_STATUS } from '../../consts'

const Search = Input.Search;
const Option = Select.Option;
const confirm = Modal.confirm;
const mapStateToProps = state => {
    const { user, apiMarket, apiManage, dataSource } = state;
    return { apiMarket, apiManage, user, dataSource }
};

const mapDispatchToProps = dispatch => ({
    getSecurityList () {
        return dispatch(apiManageActions.getSecuritySimpleList());
    },
    getCatalogue (pid) {
        dispatch(apiMarketActions.getCatalogue(pid));
    },
    getAllApiList (params) {
        return dispatch(apiManageActions.getAllApiList(params));
    },
    getDataSourceList (type) {
        return dispatch(apiManageActions.getDataSourceByBaseInfo({ type: type }));
    },
    deleteApi (apiId) {
        return dispatch(apiManageActions.deleteApi({ apiIds: [apiId] }));
    },
    openApi (apiId) {
        return dispatch(apiManageActions.openApi(apiId));
    },
    closeApi (apiId) {
        return dispatch(apiManageActions.closeApi(apiId));
    },
    getDataSourcesType () {
        return dispatch(dataSourceActions.getDataSourcesType());
    }
});

@connect(mapStateToProps, mapDispatchToProps)
class APIMana extends Component {
    state = {
        pageSize: 20,
        pageIndex: 1,
        type1: undefined,
        type2: undefined,
        total: 0,
        dataSourceType: undefined,
        dataSource: undefined,
        searchName: this.props.location.state && this.props.location.state.apiName,
        filter: {},
        sortedInfo: {},
        showRecord: {},
        changeMan: null,
        loading: false,
        cascaderData: []// 存储级联选择框数据
    }
    componentDidMount () {
        this.props.getCatalogue(0);
        this.props.getSecurityList();
        this.getAllApi()
            .then((res) => {
                const apiId = this.props.location.state && this.props.location.state.apiId;
                if (res && apiId) {
                    for (let i in res.data.data) {
                        let item = res.data.data[i];
                        if (apiId == item.id) {
                            this.openDetail(item);
                            break;
                        }
                    }
                }
            });
        this.getDataSource(null);
        this.props.getDataSourcesType();
    }
    exchangeSourceType () {
        let arr = [];
        let dic = {};

        const items = this.props.dataSource.sourceType;
        for (let i in items) {
            let item = items[i];
            dic[item.value] = item.name;
            arr.push({
                text: item.name,
                value: item.value
            })
        }

        return { typeList: arr, typeDic: dic };
    }
    getAllApi () {
        const sortType = {
            'gmtModified': 'gmt_modified'
        }
        const orderType = {
            'ascend': 'asc',
            'descend': 'desc'
        }
        let params = {};
        params.apiName = this.state.searchName;// 查询名
        params.pid = this.state.type1;// 一级目录
        params.cid = this.state.type2;// 二级目录
        params.status = this.state.filter.status;
        params.dataSourceType = this.state.dataSourceType && parseInt(this.state.dataSourceType);// 数据源类型
        params.dataSourceId = this.state.dataSource && parseInt(this.state.dataSource);// 数据源
        params.modifyUserId = this.state.changeMan;// 修改人id
        params.orderBy = sortType[this.state.sortedInfo.columnKey];
        params.sort = orderType[this.state.sortedInfo.order];
        params.currentPage = this.state.pageIndex;
        params.pageSize = this.state.pageSize;
        this.setState({
            loading: true
        })
        return this.props.getAllApiList(params)
            .then(
                (res) => {
                    this.setState({
                        loading: false
                    })
                    if (res) {
                        this.setState({
                            total: res.data.totalCount
                        })
                        return res;
                    }
                }
            );
    }

    handleSearch (value) {
        this.setState({
            searchName: value,
            pageIndex: 1
        }, () => {
            this.getAllApi();
        }
        )
    }
    // 表格换页/排序
    onTableChange = (page, filter, sorter) => {
        this.setState({
            pageIndex: page.current,
            filter: filter,
            sortedInfo: sorter
        }, () => {
            this.getAllApi();
        });
    }
    getPagination () {
        return {
            current: this.state.pageIndex,
            pageSize: 20,
            total: this.state.total
        }
    }
    openDetail (record) {
        this.setState({
            showRecord: record,
            slidePaneShow: true
        })
    }
    closeSlidePane () {
        this.setState({
            showRecord: {},
            slidePaneShow: false
        })
    }
    initColumns () {
        return [{
            title: 'API名称',
            dataIndex: 'name',
            key: 'name',
            render: (text, record) => {
                return <a onClick={this.openDetail.bind(this, record)} >{text}</a>
            }
        }, {
            title: '状态',
            dataIndex: 'status',
            key: 'status',
            render: (text, record) => {
                const dic = {
                    success: '正常',
                    stop: '已禁用',
                    editting: '未发布'
                }
                return <span className={`state-${EXCHANGE_ADMIN_API_STATUS[text]}`}>{dic[EXCHANGE_ADMIN_API_STATUS[text]]}</span>
            },
            filters: [{
                text: '正常',
                value: API_SYSTEM_STATUS.SUCCESS
            }, {
                text: '未发布',
                value: API_SYSTEM_STATUS.EDITTING
            }, {
                text: '已禁用',
                value: API_SYSTEM_STATUS.STOP
            }]
        },
        {
            title: '数据源',
            dataIndex: 'dataSourceType',
            key: 'dataSourceType',
            render (text, record) {
                if (!record.dataSourceType && !record.dataSourceName) {
                    return null;
                }
                return (record.dataSourceType || '无') + ' / ' + (record.dataSourceName || '无');
            }
        }, {
            title: '最近24小时调用',
            dataIndex: 'total1d',
            key: 'total1d',
            width: '150px'
        },
        {
            title: '累计调用',
            dataIndex: 'invokeTotal',
            key: 'invokeTotal',
            width: '150px'
        },
        {
            title: '创建人',
            dataIndex: 'createUser',
            key: 'createUser'
        },
        {
            title: '操作',
            dataIndex: 'deal',
            key: 'deal',
            width: '150px',
            render: (text, record) => {
                if (API_SYSTEM_STATUS.SUCCESS == record.status) {
                    return <a onClick={this.closeApi.bind(this, record.id)}>禁用</a>
                }
                return (
                    <div>
                        <a onClick={this.openApi.bind(this, record.id)}>发布</a>
                        <span className="ant-divider" ></span>
                        <a onClick={this.editApi.bind(this, record.id)}>编辑</a>
                        <span className="ant-divider"></span>
                        <a onClick={this.deleteApi.bind(this, record.id)}>删除</a>
                    </div>
                );
            }
        }]
    }
    getSource () {
        return this.props.apiManage.apiList;
    }
    editApi (id) {
        this.props.router.push('/api/manage/newApi?apiId=' + id);
    }
    openApi (apiId) {
        confirm({
            title: '确认发布?',
            content: '确认发布api',
            onOk: () => {
                this.setState({
                    loading: true
                })
                this.props.openApi(apiId)
                    .then(
                        (res) => {
                            this.setState({
                                loading: false
                            })
                            if (res) {
                                message.success('发布成功')
                                this.getAllApi();
                            }
                        }
                    )
            },
            onCancel () {
                console.log('Cancel');
            }
        });
    }
    closeApi (apiId) {
        confirm({
            title: '确认禁用?',
            content: '确认禁用api',
            onOk: () => {
                this.setState({
                    loading: true
                })
                this.props.closeApi(apiId)
                    .then(
                        (res) => {
                            this.setState({
                                loading: false
                            })
                            message.success('禁用成功')
                            if (res) {
                                this.getAllApi();
                            }
                        }
                    )
            },
            onCancel () {
                console.log('Cancel');
            }
        });
    }
    // 删除api
    deleteApi (apiId) {
        confirm({
            title: '确认删除?',
            content: '确认删除api',
            onOk: () => {
                this.setState({
                    loading: true
                })
                this.props.deleteApi(apiId)
                    .then(
                        (res) => {
                            this.setState({
                                loading: false
                            })
                            if (res) {
                                message.success('删除成功')
                                this.getAllApi();
                            }
                        }
                    )
            },
            onCancel () {
                console.log('Cancel');
            }
        });
    }
    // 获取数据源视图
    gerDataSourceView () {
        const type = this.state.dataSourceList;
        if (!type) {
            return null;
        }
        return type.map(function (item, index) {
            return <Option key={item.id}>{item.name}</Option>
        })
    }
    // 获取数据源
    getDataSource () {
        this.props.getDataSourceList(this.state.dataSourceType)
            .then(
                (res) => {
                    if (res) {
                        const data = res.data;
                        this.setState({
                            dataSourceList: data
                        })
                    }
                }
            )
    }
    // 获取类型视图
    getDataSourceTypeView () {
        const { typeList } = this.exchangeSourceType();

        if (!typeList || typeList.length < 1) {
            return null;
        }
        return typeList.map(function (item) {
            return <Option key={item.value}>{item.text}</Option>
        })
    }
    // 数据源类型改变
    dataSourceTypeChange (key) {
        this.setState({
            dataSourceType: key,
            dataSource: undefined
        }, () => {
            this.getDataSource();
            this.getAllApi();
        })
    }
    // 数据源改变
    dataSourceChange (key) {
        this.setState({
            dataSource: key
        }, () => {
            this.getAllApi();
        })
    }
    changeManCheck (e) {
        let changeMan = null;
        if (e.target.checked) {
            changeMan = this.props.user.id;
        }
        this.setState({
            changeMan: changeMan
        }, () => {
            this.getAllApi();
        })
    }
    getCascaderData () { // 处理级联选择数据
        const cascaderData = [];
        const { apiCatalogue } = this.props.apiMarket;
        if (apiCatalogue.length > 0) {
            apiCatalogue.map(v => {
                const option = {};
                option.value = option.label = v.catalogueName;
                option.pid = v.id
                if (v.childCatalogue.length > 0) {
                    option.children = [];
                    v.childCatalogue.map(v => {
                        if (v.api) {
                            return;
                        }
                        const childOpt = {};
                        childOpt.value = childOpt.label = v.catalogueName;
                        childOpt.cid = v.id;
                        option.children.push(childOpt);
                    })
                    if (option.children.length == 0) {
                        option.children = undefined;
                    }
                }
                cascaderData.push(option)
            })
        }
        console.log(cascaderData);
        return cascaderData;
    }
    cascaderOnChange (value, data) { // API类型改变
        let { type1, type2 } = this.state;
        type1 = data[0] ? data[0].pid : undefined;
        type2 = data[1] ? data[1].cid : undefined;
        this.setState({
            type1,
            type2
        }, () => {
            this.getAllApi();
        })
    }
    getCardTitle () {
        const { searchName } = this.state;
        const cascaderData = this.getCascaderData();
        return (
            <div className="flex font-12">
                <Search
                    placeholder="输入API名称搜索"
                    style={{ width: 150, margin: '10px 0' }}
                    onSearch={this.handleSearch.bind(this)}
                    defaultValue={searchName}
                />
                <div className="m-l-8">
                    API分类：
                    <Cascader placeholder="API分类" options={cascaderData} onChange={this.cascaderOnChange.bind(this)} changeOnSelect expandTrigger="hover" />
                </div>
                <div className="m-l-8">
                    类型：
                    <Select placeholder="类型" value={this.state.dataSourceType} allowClear onChange={this.dataSourceTypeChange.bind(this)} style={{ width: 100 }}>
                        {
                            this.getDataSourceTypeView()
                        }
                    </Select>
                </div>
                <div className="m-l-8">
                    数据源：
                    <Select placeholder="数据源" value={this.state.dataSource} allowClear onChange={this.dataSourceChange.bind(this)} style={{ width: 100 }}>
                        {
                            this.gerDataSourceView()
                        }
                    </Select>
                </div>
                <div className="m-l-8">
                    <Checkbox onChange={this.changeManCheck.bind(this)}>我修改的</Checkbox>
                </div>
            </div>
        )
    }
    openApiType () {
        this.props.router.push('/api/manage/apiType');
    }
    newApi () {
        this.props.router.push('/api/manage/newApi');
    }
    getCardExtra () {
        return (
            <div style={{ paddingTop: '10px' }}>
                <Button onClick={this.openApiType.bind(this)} style={{ marginRight: '8px' }} type="primary">类目管理</Button>
                <Button type="primary" onClick={this.newApi.bind(this)}>生成API</Button>

            </div>
        )
    }
    render () {
        const { slidePaneShow, showRecord } = this.state

        return (
            <div className="api-management">
                <div style={{ marginTop: '20px' }} className="margin-0-20 m-card box-2">
                    <ApiSlidePane showRecord={showRecord} slidePaneShow={slidePaneShow} closeSlidePane={this.closeSlidePane.bind(this)} />
                    <Card

                        noHovering
                        title={this.getCardTitle()}
                        extra={this.getCardExtra()}
                    >
                        <Table
                            rowClassName={
                                (record, index) => {
                                    if (showRecord.id == record.id) {
                                        return 'row-select'
                                    } else {
                                        return '';
                                    }
                                }
                            }
                            rowKey="id"
                            className="m-table monitor-table"
                            columns={this.initColumns()}
                            loading={this.state.loading}
                            pagination={this.getPagination()}
                            dataSource={this.getSource()}
                            onChange={this.onTableChange}
                        />
                    </Card>
                </div>
            </div>
        )
    }
}

export default APIMana
