import * as React from 'react'
import { Card, Input, Select, Table, Button, Modal, message } from 'antd'
import { connect } from 'react-redux';
import utils from 'utils';

import { apiMarketActions } from '../../actions/apiMarket';
import { apiManageActions } from '../../actions/apiManage';
import { dataSourceActions } from '../../actions/dataSource';

import { TAG_TYPE, API_OPEN_STATUS } from '../../consts';

import { dataSourceText } from '../../components/display';

const Search = Input.Search;
const Option = Select.Option;
const confirm = Modal.confirm;

const mapStateToProps = (state: any) => {
    const { user, apiMarket, apiManage, dataSource } = state;
    return { user, apiMarket, apiManage, dataSource }
};

const mapDispatchToProps = (dispatch: any) => ({
    getCatalogue (pid: any) {
        dispatch(apiMarketActions.getCatalogue(pid));
    },
    getAllApiList (params: any) {
        return dispatch(apiManageActions.getAllApiList(params));
    },
    getDataSourceList (type: any) {
        return dispatch(apiManageActions.getDataSourceByBaseInfo({ type: type }));
    },
    deleteApi (apiId: any) {
        return dispatch(apiManageActions.deleteApi({ apiIds: [apiId] }));
    },
    openApi (apiId: any) {
        return dispatch(apiManageActions.openApi(apiId));
    },
    closeApi (apiId: any) {
        return dispatch(apiManageActions.closeApi(apiId));
    },
    getDataSourcesType () {
        return dispatch(dataSourceActions.getDataSourcesType());
    }
});

@(connect(mapStateToProps, mapDispatchToProps) as any)
class APIMana extends React.Component<any, any> {
    state: any = {
        pageSize: 20,
        pageIndex: 1,
        type1: undefined,
        type2: undefined,
        total: 0,
        dataSourceType: undefined,
        dataSource: undefined,
        searchName: null,
        filter: {},
        sortedInfo: {},
        changeMan: null,
        loading: false

    }
    componentDidMount () {
        this.props.getCatalogue(0);
        this.getAllApi();
        this.getDataSource();
        this.props.getDataSourcesType();
    }
    exchangeSourceType () {
        let arr: any = [];
        let dic: any = {};

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
        const sortType: any = {
            'gmtModified': 'gmt_modified'
        }
        const orderType: any = {
            'ascend': 'asc',
            'descend': 'desc'
        }
        let params: any = {};
        params.name = this.state.searchName;// 查询名
        params.pid = this.state.type1;// 一级目录
        params.cid = this.state.type2;// 二级目录
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
        this.props.getAllApiList(params)
            .then(
                (res: any) => {
                    this.setState({
                        loading: false
                    })
                    if (res) {
                        this.setState({
                            total: res.data.totalCount
                        })
                    }
                }
            );
    }

    renderSourceType (id: any, root: any) {
        function arrToOptions (arr: any) {
            if (!arr || arr.length < 1) {
                return null;
            }
            return arr.map(
                (item: any) => {
                    return <Option key={item.id}>{item.name}</Option>
                }
            )
        }
        let arr: any = [];
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
    onSourceChange = (key: any) => {
        this.setState({
            type1: key,
            type2: undefined
        }, () => {
            this.getAllApi();
        })
    }
    onUserSourceChange (key: any) {
        this.setState({
            type2: key
        }, () => {
            this.getAllApi();
        })
    }
    handleSearch (value: any) {
        this.setState({
            searchName: value || undefined,
            pageIndex: 1
        }, () => {
            this.getAllApi();
        })
    }
    // 表格换页/排序
    onTableChange = (page: any, filter: any, sorter: any) => {
        this.setState({
            pageIndex: page.current,
            filter: filter,
            sortedInfo: sorter
        },
        () => {
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
    openDetail (text: any) {
        this.props.router.push('/dl/manage/detail/' + text)
    }
    initColumns () {
        return [{
            width: '10%',
            title: '标签名称',
            dataIndex: 'name',
            key: 'name',
            render: (text: any, record: any) => {
                return <a onClick={this.openDetail.bind(this, record.apiId)} >{text}</a>
            }
        }, {
            width: 100,
            title: '类型',
            dataIndex: 'type',
            key: 'type',
            render: (tagType: any, record: any) => {
                return TAG_TYPE[tagType];
            }
        }, {
            width: '10%',
            title: '描述',
            dataIndex: 'tagDesc',
            key: 'tagDesc'
        }, {
            width: '10%',
            title: '数据源',
            dataIndex: 'dataSourceType',
            key: 'dataSourceType',
            render (text: any, record: any) {
                return dataSourceText(record.dataSourceType) + ' / ' + record.dataSourceName
            }
        }, {
            width: '10%',
            title: '最近24小时调用',
            dataIndex: 'total1d',
            key: 'total1d'
        }, {
            width: '8%',
            title: '累计调用',
            dataIndex: 'invokeTotal',
            key: 'invokeTotal'
        }, {
            width: '10%',
            title: '最近修改人',
            dataIndex: 'modifyUser',
            key: 'modifyUser'
        }, {
            width: '10%',
            title: '最近修改时间',
            dataIndex: 'gmtModified',
            key: 'gmtModified',
            sorter: true,
            render (text: any) {
                return utils.formatDateTime(text);
            }
        }, {
            width: '10%',
            title: '创建人',
            dataIndex: 'createUser',
            key: 'createUser'
        }, {
            width: 100,
            title: '启用状态',
            dataIndex: 'apiStatus',
            key: 'apiStatus',
            render: (text: any, record: any) => {
                return API_OPEN_STATUS[text];
            }
        }, {
            width: 120,
            title: '操作',
            dataIndex: 'deal',
            key: 'deal',
            render: (text: any, record: any) => {
                if (record.apiStatus == 0) {
                    return <a onClick={this.closeApi.bind(this, record.apiId)}>禁用</a>
                }

                return (
                    <div>
                        <a onClick={this.openApi.bind(this, record.apiId)}>开启</a>
                        <span className="ant-divider" ></span>
                        <a onClick={this.editApi.bind(this, record.id)}>编辑</a>
                        <span className="ant-divider"></span>
                        <a onClick={this.deleteApi.bind(this, record.apiId)}>删除</a>
                    </div>
                );
            }
        }]
    }
    getSource () {
        return this.props.apiManage.apiList;
    }
    editApi (id: any) {
        this.props.router.push('/dl/manage/editApi/' + id);
    }
    openApi (apiId: any) {
        confirm({
            title: '确认开启?',
            content: '确认开启标签',
            onOk: () => {
                this.setState({
                    loading: true
                })
                this.props.openApi(apiId)
                    .then(
                        (res: any) => {
                            this.setState({
                                loading: false
                            })
                            message.success('开启成功')
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
    closeApi (apiId: any) {
        confirm({
            title: '确认禁用?',
            content: '确认禁用标签',
            onOk: () => {
                this.setState({
                    loading: true
                })
                this.props.closeApi(apiId)
                    .then(
                        (res: any) => {
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
    deleteApi (apiId: any) {
        confirm({
            title: '确认删除?',
            content: '确认删除标签',
            onOk: () => {
                this.setState({
                    loading: true
                })
                this.props.deleteApi(apiId)
                    .then(
                        (res: any) => {
                            this.setState({
                                loading: false
                            })
                            if (res.code === 1) {
                                message.success('删除成功');
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
        return type.map(function (item: any, index: any) {
            return <Option key={item.id}>{item.name}</Option>
        })
    }
    // 获取数据源
    getDataSource () {
        this.props.getDataSourceList(this.state.dataSourceType)
            .then(
                (res: any) => {
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
        return typeList.map(function (item: any) {
            return <Option key={item.value}>{item.text}</Option>
        })
    }
    // 数据源类型改变
    dataSourceTypeChange = (key: any) => {
        this.setState({
            dataSourceType: key,
            dataSource: undefined
        },
        () => {
            this.getDataSource();
            this.getAllApi();
        })
    }
    // 数据源改变
    dataSourceChange (key: any) {
        this.setState({
            dataSource: key
        },
        () => {
            this.getAllApi();
        })
    }
    changeManCheck (e: any) {
        let changeMan = null;
        if (e.target.checked) {
            changeMan = this.props.user.id;
        }
        this.setState({
            changeMan: changeMan
        },
        () => {
            this.getAllApi();
        })
    }

    openApiType () {
        this.props.router.push('/dl/manage/apiType');
    }
    newApi () {
        this.props.router.push('/dl/manage/newApi');
    }

    render () {
        const cardTitle = (
            <div className="flex font-12">
                <Search
                    placeholder="输入标签名称搜索"
                    style={{ width: 200, margin: '10px 0' }}
                    onSearch={this.handleSearch.bind(this)}
                />

                <div className="m-l-8">
                    标签分类：
                    <Select
                        allowClear
                        value={this.state.type1}
                        style={{ width: 150 }}
                        onChange={this.onSourceChange}>
                        {
                            this.renderSourceType(0, true)
                        }
                    </Select>
                </div>

                <div className="m-l-8">
                    类型：
                    <Select
                        allowClear
                        value={this.state.dataSourceType}
                        style={{ width: 150 }}
                        onChange={this.dataSourceTypeChange}>
                        {
                            this.getDataSourceTypeView()
                        }
                    </Select>
                </div>
                {/* <div className="m-l-8">
                    数据源：
                    <Select value={this.state.dataSource} allowClear onChange={this.dataSourceChange.bind(this)} style={{ width: 100 }}>
                        {
                            this.gerDataSourceView()
                        }
                    </Select>
                </div>
                <div className="m-l-8">
                    二级分类：
                    <Select value={this.state.type2} allowClear onChange={this.onUserSourceChange.bind(this)} style={{ width: 150 }}>
                        {
                            this.renderSourceType(this.state.type1, false)
                        }
                    </Select>
                </div>
                <div className="m-l-8">
                    <Checkbox onChange={this.changeManCheck.bind(this)}>我修改的</Checkbox>
                </div> */}
            </div>
        )

        const cardExtra = (
            <div style={{ paddingTop: '10px' }}>
                <Button onClick={this.openApiType.bind(this)} style={{ marginRight: '8px' }} type="primary">类目管理</Button>
                {/* <Button type="primary" onClick={this.newApi.bind(this)}>新建API</Button> */}
            </div>
        )

        return (
            <div className="api-management">
                <div style={{ marginTop: '20px' }} className="margin-0-20 m-card box-2">
                    <Card
                        noHovering
                        title={cardTitle}
                        extra={cardExtra}
                    >
                        <Table
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
