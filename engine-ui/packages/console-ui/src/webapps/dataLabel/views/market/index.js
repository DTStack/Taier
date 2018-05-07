import React, { Component } from 'react'
import { connect } from "react-redux";
import { Link } from "react-router";
import { Card, Input, Checkbox, Select, DatePicker, Table, Modal, Form } from "antd";

import { apiMarketActions } from '../../actions/apiMarket';
import utils from "utils";
import ApplyBox from "./applyBox";


const FormItem = Form.Item;
const TextArea = Input.TextArea
const Option = Select.Option;
const Search = Input.Search;
let modal;

const mapStateToProps = state => {
    const { user, apiMarket } = state;
    return { apiMarket, user }
};

const mapDispatchToProps = dispatch => ({
    getCatalogue(pid) {
        dispatch(apiMarketActions.getCatalogue(pid));
    },
    getApiMarketList(params) {
        return dispatch(apiMarketActions.getApiMarketList(params));
    }
});

@connect(mapStateToProps, mapDispatchToProps)
class APIMarket extends Component {

    state = {
        searchValue: "",
        pageIndex: 1,
        loading: true,
        applyBox: false,
        apply:{
            apiId:"",
            apiName:"",
            desc:""
        },
        type1: undefined,
        type2: undefined,
        apiName: "",
        pageSize: 20,
        total: 0,
        sorter:{}
    }
    getMarketApi() {
        this.setState({
            loading: true
        })
        const dic={
            updateTime:"gmt_modified"
        }
        const orderType = {
            "ascend": 'asc',
            "descend": 'desc'
        }
        
        this.props.getApiMarketList({
            apiName: this.state.searchValue,
            pid: this.state.type1 || -1,
            cid: this.state.type2 || -1,
            currentPage: this.state.pageIndex,
            pageSize: this.state.pageSize,
            orderBy:dic[this.state.sorter.columnKey],
            sort:orderType[this.state.sorter.order]
        }).then((res) => {
            console.log("apigetOver");

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
    componentDidMount() {
        this.props.getCatalogue(0);
        this.getMarketApi();
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
                if(item_child[i].api){
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
    onSourceChange(key) {
        this.setState({
            type1: key,
            type2: undefined
        }, () => {
            this.getMarketApi();
        })

    }
    onUserSourceChange(key) {
        this.setState({
            type2: key
        }, () => {
            this.getMarketApi();
        })

    }
    handleSearch(value) {
        
        this.setState({
            searchValue: value,
            pageIndex:1
        }, () => {
            this.getMarketApi();
        }
        )
    }
    getDealType(type) {
        const dic = {
            "complete": "查看使用情况",
            "nothing": "申请",
            "applying": "查看审批进度"
        }
        return dic[type || 'nothing']
    }
    deal(record) {
        const method = this['deal' + record.deal]
        if (method) {
            method.call(this, record);
        }
    }
    dealcomplete(record) {
        this.props.router.push("/dl/mine/approved?apiId="+record.key);    
        console.log("dealcomplete", record);
    }
    dealnothing(record) {
        this.setState({
            applyBox: true,
            apply:{
                apiId:record.key,
                apiName:record.apiName,
                desc:record.description
            }
        })
        console.log("dealnothing", record);
    }
    dealapplying(record) {
        this.props.router.push("/dl/mine?apiId="+record.key);
        console.log("dealapplying", record);
    }
    // 表格换页/排序
    onTableChange = (page, filter, sorter) => {
        this.setState({
            pageIndex: page.current,
            sorter:sorter

        }, () => {
            this.getMarketApi();
        });
    }
    openDetail(text) {
        return function () {
            window.open(`${location.origin + location.pathname}#/dl/market/detail/${text}?isHideBack=true`)
        }

    }
    initColumns() {

        return [{
            title: 'API名称',
            dataIndex: 'apiName',
            key: 'apiName',
            render: (text, record) => {
                return <a onClick={this.openDetail(record.key)} >{text}</a>
            }
        }, {
            title: '值域',
            dataIndex: 'valueRange',
            key: 'valueRange',
            width: 100
        }, {
            title: '覆盖数',
            dataIndex: 'overCount',
            key: 'overCount',
            width: 100
        }, {
            title: '昨日调用次数',
            dataIndex: 'callCount',
            key: 'callCount',
        }, {
            title: '更新时间',
            dataIndex: 'updateTime',
            key: 'updateTime',
            render(time){
                return utils.formatDateTime(time);
            },
            sorter:true
        }, {
            width: 80,
            title: '预览',
            dataIndex: 'id',
            key: 'id',
        }, {
            width: 80,
            title: '操作',
            dataIndex: 'deal',
            render: (text, record) => {
                return <a onClick={this.deal.bind(this, record)}>{this.getDealType(record.deal)}</a>
            }
        }]
    }
    getSource() {
        const errorDic = {
            4: "complete",
            3: "complete",
            2: "nothing",
            1: "complete",
            0: "applying",
            "-1":"nothing",
        }
        const apiList = this.props.apiMarket.apiList;
        let arr = [];
        for (let i = 0; i < apiList.length; i++) {
            arr.push({
                key: apiList[i].id,
                apiName: apiList[i].name,
                description: apiList[i].apiDesc,
                callCount: apiList[i].invokeTotal,
                updateTime: apiList[i].gmtModified,
                deal: errorDic[apiList[i].applyStatus]
            })
        }
        return arr;
    }
    getPagination() {
        return {
            current: this.state.pageIndex,
            pageSize: this.state.pageSize,
            total: this.state.total,
        }
    }
    getCardTitle() {

        return (
            <div className="flex font-12">
                <Search
                    placeholder="输入API名称搜索"
                    style={{ width: 150, margin: '10px 0' }}
                    onSearch={this.handleSearch.bind(this)}
                />
                <div className="m-l-8">
                    API分类：
                    <Select value={this.state.type1} allowClear onChange={this.onSourceChange.bind(this)} style={{ width: 120 }}>
                        {
                            this.renderSourceType(0, true)
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
            </div>
        )
    }
   
    jumpToMine() {
        modal.destroy();
        this.props.router.push("/api/mine");

    }
    showApplySuccessModal() {
        modal = Modal.success({
            title: '申请提交成功',
            content: (
                <span>您可以在 <a onClick={this.jumpToMine.bind(this)}>我的API</a> 中查看审批进度</span>
            ),
            okText: "确定"
        });
    }
    handleOk() {

        this.setState({
            applyBox: false
        });
        this.showApplySuccessModal();
    }
    handleCancel() {
        this.setState({
            applyBox: false
        })
    }

    render() {
        const { children } = this.props
        return (
            <div className="api-market">
                <ApplyBox show={this.state.applyBox}
                    successCallBack={this.handleOk.bind(this)}
                    cancelCallback={this.handleCancel.bind(this)}
                    apiId={this.state.apply.apiId}
                    apiName={this.state.apply.apiName}
                    desc={this.state.apply.desc}
                    getMarketApi={this.getMarketApi.bind(this)}
                ></ApplyBox>
                <div className="margin-0-20 m-card box-1">
                    <Card
                        noHovering
                        title={this.getCardTitle()}
                    >
                        <Table
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

export default APIMarket
