import React, { Component } from 'react'
import { Card, Input, Select, DatePicker, Table, Button, Checkbox, Modal, message } from "antd"
import { connect } from "react-redux";
import utils from "utils";

import { apiMarketActions } from '../../actions/apiMarket';
import { apiManageActions } from '../../actions/apiManage';
import { dataSourceActions } from '../../actions/dataSource';

import { 
    dataSourceTypes, TAG_TYPE, TAG_STATUS,
    EXCHANGE_ADMIN_API_STATUS
} from "../../consts";

import { dataSourceText } from '../../components/display';

const Search = Input.Search;
const Option = Select.Option;
const confirm = Modal.confirm;

const mapStateToProps = state => {
    const { user, apiMarket, apiManage, dataSource } = state;
    return { apiMarket, apiManage, user, dataSource }
};

const mapDispatchToProps = dispatch => ({
    getCatalogue(pid) {
        dispatch(apiMarketActions.getCatalogue(pid));
    },
    getAllApiList(params) {
        return dispatch(apiManageActions.getAllApiList(params));
    },
    getDataSourceList(type) {
        return dispatch(apiManageActions.getDataSourceByBaseInfo({ type: type }));
    },
    deleteApi(tagId) {
        return dispatch(apiManageActions.deleteApi({ tagIds: [tagId] }));
    },
    openApi(tagId) {
        return dispatch(apiManageActions.openApi(tagId));
    },
    closeApi(tagId) {
        return dispatch(apiManageActions.closeApi(tagId));
    },
    getDataSourcesType() {
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
        searchName: null,
        filter: {},
        sortedInfo: {},
        changeMan: null,
        loading: false

    }
    componentDidMount() {
        this.props.getCatalogue(0);
        this.getAllApi();
        this.getDataSource(null);
        this.props.getDataSourcesType();
    }
    exchangeSourceType() {
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
    getAllApi() {
        const sortType = {
            "gmtModified": 'gmt_modified'
        }
        const orderType = {
            "ascend": 'asc',
            "descend": 'desc'
        }
        let params = {};
        params.name = this.state.searchName;//查询名
        params.pid = this.state.type1;//一级目录
        params.cid = this.state.type2;//二级目录
        params.dataSourceType = this.state.dataSourceType && parseInt(this.state.dataSourceType);//数据源类型
        params.dataSourceId = this.state.dataSource && parseInt(this.state.dataSource);//数据源
        params.modifyUserId = this.state.changeMan;//修改人id
        params.orderBy = sortType[this.state.sortedInfo.columnKey];
        params.sort = orderType[this.state.sortedInfo.order];
        params.currentPage = this.state.pageIndex;
        params.pageSize = this.state.pageSize;
        this.setState({
            loading: true
        })
        this.props.getAllApiList(params)
            .then(
                (res) => {
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

    renderSourceType(id, root) {
        function arrToOptions(arr) {
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
        //获取子节点
        const items = this.props.apiMarket.apiCatalogue;

        //一级目录
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
        } else {//二级目录

            if (!items) {
                return null;
            }
            let item_child;//二级目录
            //查找二级目录
            for (let i = 0; i < items.length; i++) {
                if (items[i].id == id) {
                    item_child = items[i].childCatalogue;
                    break;
                }
            }
            //找不到，则返回null
            if (!item_child) {
                return null;
            }

            for (let i = 0; i < item_child.length; i++) {
                if (item_child[i].api) {
                    continue;
                }
                arr.push({
                    id: item_child[i].id,
                    name: item_child[i].catalogueName
                })
            }
            return arrToOptions(arr);
        }
        return null;
    }
    onSourceChange = (key) => {
        this.setState({
            type1: key,
            type2: undefined
        }, () => {
            this.getAllApi();
        })

    }
    onUserSourceChange(key) {
        this.setState({
            type2: key
        }, () => {
            this.getAllApi();
        })

    }
    handleSearch(value) {
        this.setState({
            searchName: value,
            pageIndex: 1
        }, () => {
            this.getAllApi();
        })
    }
    // 表格换页/排序
    onTableChange = (page, filter, sorter) => {
        this.setState({
            pageIndex: page.current,
            filter: filter,
            sortedInfo: sorter
        },
            () => {
                this.getAllApi();
            });
    }
    getPagination() {
        return {
            current: this.state.pageIndex,
            pageSize: 20,
            total: this.state.total,
        }
    }
    openDetail(text) {
        this.props.router.push("/dl/manage/detail/" + text)
    }
    initColumns() {

        return [{
            width: 100,
            title: '标签名称',
            dataIndex: 'name',
            key: 'name',
            render: (text, record) => {
                return <a onClick={this.openDetail.bind(this, record.id)} >{text}</a>
            }
        }, {
            width: 80,
            title: '状态',
            dataIndex: 'status',
            key: 'status',
            render: (text, record) => {
                return TAG_STATUS[text];
            }
        }, {
            width: 100,
            title: '类型',
            dataIndex: 'type',
            key: 'type',
            render: (tagType, record) => {
                if (tagType === TAG_TYPE.REGISTER) {
                    return '注册标签';
                } else {
                    return '自定义标签';
                }
            }
        }, {
            title: '描述',
            dataIndex: 'tagDesc',
            key: 'tagDesc',
        }, {
            title: '数据源',
            dataIndex: 'dataSourceType',
            key: 'dataSourceType',
            render(text, record) {
                return dataSourceText(record.dataSourceType) + ' / ' + record.dataSourceName
            }
        }, {
            title: '最近24小时调用',
            dataIndex: 'total1d',
            key: "total1d"

        },
        {
            title: '累计调用',
            dataIndex: 'invokeTotal',
            key: "invokeTotal"
        },
        {
            title: '最近修改人',
            dataIndex: 'modifyUser',
            key: "modifyUser"
        },
        {
            title: '最近修改时间',
            dataIndex: 'gmtModified',
            key: "gmtModified",
            sorter: true,
            render(text) {
                return utils.formatDateTime(text);
            }
        },
        {
            width: 120,
            title: '创建人',
            dataIndex: 'createUser',
            key: "createUser"
        },
        {
            width: 120,
            title: '操作',
            dataIndex: 'deal',
            key: "deal",
            render: (text, record) => {
                // 更新完成3才可禁用
                if (record.status == 3) {
                    return <a onClick={this.closeApi.bind(this, record.id)}>禁用</a>
                }
             
                return (
                    <div>
                        <a onClick={this.openApi.bind(this, record.id)}>开启</a>
                        <span className="ant-divider" ></span>
                        <a onClick={this.editApi.bind(this, record.id)}>编辑</a>
                        <span className="ant-divider"></span>
                        <a onClick={this.deleteApi.bind(this, record.id)}>删除</a>
                    </div>
                );
            }
        }]
    }
    getSource() {
        return this.props.apiManage.apiList;

    }
    editApi(id) {
        this.props.router.push("/dl/manage/editApi/" + id);
    }
    openApi(tagId) {
        confirm({
            title: '确认开启?',
            content: '确认开启标签',
            onOk: () => {
                this.setState({
                    loading: true
                })
                this.props.openApi(tagId)
                    .then(
                        (res) => {
                            this.setState({
                                loading: false
                            })
                            message.success("开启成功")
                            if (res) {
                                this.getAllApi();
                            }
                        }
                    )
            },
            onCancel() {
                console.log('Cancel');
            },
        });
    }
    closeApi(tagId) {
        confirm({
            title: '确认禁用?',
            content: '确认禁用标签',
            onOk: () => {
                this.setState({
                    loading: true
                })
                this.props.closeApi(tagId)
                    .then(
                        (res) => {
                            this.setState({
                                loading: false
                            })
                            message.success("禁用成功")
                            if (res) {
                                this.getAllApi();
                            }
                        }
                    )
            },
            onCancel() {
                console.log('Cancel');
            },
        });
    }
    //删除api
    deleteApi(tagId) {
        confirm({
            title: '确认删除?',
            content: '确认删除标签',
            onOk: () => {
                this.setState({
                    loading: true
                })
                this.props.deleteApi(tagId)
                    .then(
                        (res) => {
                            this.setState({
                                loading: false
                            })
                            message.success("删除成功")
                            if (res) {
                                this.getAllApi();
                            }
                        }
                    )
            },
            onCancel() {
                console.log('Cancel');
            },
        });

    }
    //获取数据源视图
    gerDataSourceView() {
        const type = this.state.dataSourceList;
        if (!type) {
            return null;
        }
        return type.map(function (item, index) {
            return <Option key={item.id}>{item.name}</Option>
        })
    }
    //获取数据源
    getDataSource() {
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
    //获取类型视图
    getDataSourceTypeView() {
        const { typeList, typeDic } = this.exchangeSourceType();


        if (!typeList || typeList.length < 1) {
            return null;
        }
        return typeList.map(function (item) {

            return <Option key={item.value}>{item.text}</Option>
        })
    }
    //数据源类型改变
    dataSourceTypeChange = (key) => {
        this.setState({
            dataSourceType: key,
            dataSource: undefined
        },
            () => {
                this.getDataSource();
                this.getAllApi();
            })
    }
    //数据源改变
    dataSourceChange(key) {
        this.setState({
            dataSource: key
        },
            () => {
                this.getAllApi();
            })
    }
    changeManCheck(e) {
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
    
    openApiType() {
        this.props.router.push("/dl/manage/apiType");
    }
    newApi() {
        this.props.router.push("/dl/manage/newApi");
    }
    
    render() {
        const { children } = this.props;

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
            <div style={{ paddingTop: "10px" }}>
                <Button onClick={this.openApiType.bind(this)} style={{ marginRight: "8px" }} type="primary">类目管理</Button>
                {/* <Button type="primary" onClick={this.newApi.bind(this)}>新建API</Button> */}
            </div>
        )

        return (
            <div className="api-management">
                <div style={{ marginTop: "20px" }} className="margin-0-20 m-card box-2">
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
