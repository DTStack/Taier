import * as React from 'react'
import { Card, Input, Select, Table, Button, Checkbox, Modal, message, Cascader } from 'antd'
import { connect } from 'react-redux';

import ApiSlidePane from './apiDetail/apiSlide';
import { apiMarketActions } from '../../actions/apiMarket';
import { apiManageActions } from '../../actions/apiManage';
import { dataSourceActions } from '../../actions/dataSource';
import { EXCHANGE_ADMIN_API_STATUS, API_SYSTEM_STATUS, API_TYPE } from '../../consts'

const Search = Input.Search;
const Option = Select.Option;
const confirm = Modal.confirm;
const mapStateToProps = (state: any) => {
    const { user, apiMarket, apiManage, dataSource, project } = state;
    return { apiMarket, apiManage, user, dataSource, project }
};

const mapDispatchToProps = (dispatch: any) => ({
    getSecurityList () {
        return dispatch(apiManageActions.getSecuritySimpleList());
    },
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
        searchName: this.props.location.state && this.props.location.state.apiName,
        filter: {},
        sortedInfo: {},
        showRecord: {},
        changeMan: null,
        showModify: false,
        showCreate: false,
        loading: false,
        cascaderData: []// 存储级联选择框数据
    }
    componentDidMount () {
        this.initList();
    }

    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps (nextProps: any) {
        const project = nextProps.project
        const oldProj = this.props.project
        if (oldProj && project && oldProj.id !== project.id) {
            this.initList();
        }
    }

    initList = () => {
        this.props.getCatalogue(0);
        this.props.getSecurityList();
        this.getAllApi()
            .then((res: any) => {
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
        const { user } = this.props;
        const userId = user.id;
        const sortType: any = {
            'gmtModified': 'gmt_modified'
        }
        const orderType: any = {
            'ascend': 'asc',
            'descend': 'desc'
        }
        const {
            searchName,
            type1,
            type2,
            filter,
            dataSource,
            dataSourceType,
            showModify,
            showCreate,
            sortedInfo,
            pageIndex,
            pageSize
        } = this.state;
        let params: any = {};
        params.apiName = searchName;// 查询名
        params.pid = type1;// 一级目录
        params.cid = type2;// 二级目录
        params.status = filter.status;
        params.apiType = filter.apiType && filter.apiType[0];
        params.dataSourceType = dataSourceType && parseInt(dataSourceType);// 数据源类型
        params.dataSourceId = dataSource && parseInt(dataSource);// 数据源
        params.modifyUserId = showModify ? userId : null;// 修改人id
        params.createUserId = showCreate ? userId : null;// 创建人人id
        params.orderBy = sortType[sortedInfo.columnKey];
        params.sort = orderType[sortedInfo.order];
        params.currentPage = pageIndex;
        params.pageSize = pageSize;
        this.setState({
            loading: true
        })
        return this.props.getAllApiList(params)
            .then(
                (res: any) => {
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

    handleSearch (value: any) {
        this.setState({
            searchName: value,
            pageIndex: 1
        }, () => {
            this.getAllApi();
        }
        )
    }
    // 表格换页/排序
    onTableChange = (page: any, filter: any, sorter: any) => {
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
    openDetail (record: any) {
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
            render: (text: any, record: any) => {
                return <a onClick={this.openDetail.bind(this, record)} >{text}</a>
            }
        }, {
            title: '状态',
            dataIndex: 'status',
            key: 'status',
            render: (text: any, record: any) => {
                const dic: any = {
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
            title: 'API类型',
            dataIndex: 'apiType',
            key: 'apiType',
            render: (text: any, record: any) => {
                return <span>{text == API_TYPE.NORMAL ? '生成API' : '注册API'}</span>
            },
            filters: [{
                text: '生成API',
                value: API_TYPE.NORMAL
            }, {
                text: '注册API',
                value: API_TYPE.REGISTER
            }],
            filterMultiple: false
        },
        {
            title: '数据源',
            dataIndex: 'dataSourceType',
            key: 'dataSourceType',
            render (text: any, record: any) {
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
            render: (text: any, record: any) => {
                if (API_SYSTEM_STATUS.SUCCESS == record.status) {
                    return <a onClick={this.closeApi.bind(this, record.id)}>禁用</a>
                }
                return (
                    <div>
                        <a onClick={this.openApi.bind(this, record.id)}>发布</a>
                        <span className="ant-divider" ></span>
                        <a onClick={this.editApi.bind(this, record.id, record.apiType)}>编辑</a>
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
    editApi (id: any, apiType: any) {
        this.props.router.push({
            pathname: '/api/manage/newApi',
            query: {
                apiId: id,
                isRegister: API_TYPE.REGISTER == apiType ? true : undefined
            }
        })
    }
    openApi (apiId: any) {
        confirm({
            title: '确认发布?',
            content: '确认发布api',
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
    closeApi (apiId: any) {
        confirm({
            title: '确认禁用?',
            content: '确认禁用api',
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
                            if (res && res.code == 1) {
                                this.getAllApi();
                                message.success('禁用成功')
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
            content: '确认删除api',
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
    dataSourceTypeChange (key: any) {
        this.setState({
            dataSourceType: key,
            dataSource: undefined
        }, () => {
            this.getDataSource();
            this.getAllApi();
        })
    }
    // 数据源改变
    dataSourceChange (key: any) {
        this.setState({
            dataSource: key
        }, () => {
            this.getAllApi();
        })
    }
    getCascaderData () { // 处理级联选择数据
        const cascaderData: any = [];
        const { apiCatalogue } = this.props.apiMarket;
        if (apiCatalogue.length > 0) {
            apiCatalogue.map((v: any) => {
                const option: any = {};
                option.value = option.label = v.catalogueName;
                option.pid = v.id
                if (v.childCatalogue.length > 0) {
                    option.children = [];
                    v.childCatalogue.map((v: any) => {
                        if (v.api) {
                            return;
                        }
                        const childOpt: any = {};
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
    cascaderOnChange (value: any, data: any) { // API类型改变
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
                    style={{ width: 150 }}
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
                <Checkbox
                    className="m-l-8"
                    onChange={this.onCheckBoxChange.bind(this, 'showCreate')}
                >
                    我创建的
                </Checkbox>
                <div className="m-l-8">
                    <Checkbox onChange={this.onCheckBoxChange.bind(this, 'showModify')}>我修改的</Checkbox>
                </div>
            </div>
        )
    }
    onCheckBoxChange (type: string, e: React.ChangeEvent<HTMLInputElement>) {
        this.setState({
            [type]: e.target.checked
        }, () => {
            this.getAllApi();
        })
    }
    openApiType () {
        this.props.router.push('/api/manage/apiType');
    }
    newApi () {
        this.props.router.push('/api/manage/newApi');
    }
    registerApi () {
        this.props.router.push({
            pathname: '/api/manage/newApi',
            query: {
                isRegister: true
            }
        });
    }
    getCardExtra () {
        return (
            <div>
                <Button type="primary" style={{ marginRight: '8px' }} onClick={this.newApi.bind(this)}>生成API</Button>
                <Button type="primary" style={{ marginRight: '8px' }} onClick={this.registerApi.bind(this)}>注册API</Button>
                <Button onClick={this.openApiType.bind(this)} type="primary">类目管理</Button>
            </div>
        )
    }
    render () {
        const { slidePaneShow, showRecord } = this.state

        return (
            <div className="api-management">
                <div style={{ margin: '0px' }} className="m-card box-2">
                    <ApiSlidePane
                        showRecord={showRecord}
                        slidePaneShow={slidePaneShow}
                        closeSlidePane={this.closeSlidePane.bind(this)}
                    />
                    <Card
                        style={{ minHeight: 'calc(100% - 40px)' }}
                        noHovering
                        title={this.getCardTitle()}
                        extra={this.getCardExtra()}
                    >
                        <Table
                            rowClassName={
                                (record: any, index: any) => {
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
