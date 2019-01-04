import React, { Component } from 'react';
import { connect } from 'react-redux';
import moment from 'moment';
import ajax from '../../../api/dataManage';
import { Input, Spin, Table, Button, Card, Popconfirm, message, Tabs } from 'antd';
import '../../../styles/pages/dataManage.scss';
import SlidePane from 'widgets/slidePane'
import AddDesensitization from './addDesensitization';
import TableRelation from './tableRelation';
import BloodRelation from './bloodRelation';
const Search = Input.Search;
const TabPane = Tabs.TabPane;
@connect(state => {
    return {
        projects: state.projects,
        user: state.user
    }
}, null)

class DesensitizationMange extends Component {
    state = {
        cardLoading: false,
        addVisible: false,
        visibleSlidePane: false, // 查看脱敏明细
        selectedId: '', // 选中脱敏
        nowView: 'tableRelation', // 默认选中表关系
        table: [], // 表数据
        editModalKey: null,
        queryParams: {
            currentPage: 1,
            pageSize: 20,
            name: undefined
        },
        tableId: undefined // 点击查看血缘表Id
        // mock
        // dataSource: [
        //     {
        //         key: '1',
        //         name: '身份证号脱敏',
        //         relatedNum: 12,
        //         ruleName: '身份证号',
        //         modifyUserName: 'admin@dtstack.com',
        //         gmtModified: '2018-01-01 12:12:12',
        //         opear: '删除'
        //     }
        // ]
    }
    /* eslint-disable */
    componentDidMount () {
        this.search();
    }
    search = () => {
        this.setState({
            cardLoading: true
        })
        const { queryParams } = this.state;
        ajax.searchDesensitization(queryParams).then(res => {
            if (res.code === 1) {
                this.setState({
                    table: res.data,
                    cardLoading: false
                })
            } else {
                this.setState({
                    cardLoading: false
                })
            }
        })
    }
    changeName = (e) => {
        const { queryParams } = this.state;
        this.setState({
            queryParams: Object.assign(queryParams, { name: e.target.value })
        })
    }
    handleTableChange = (pagination, filters, sorter) => {
        const queryParams = Object.assign(this.state.queryParams, { currentPage: pagination.current })
        this.setState({
            queryParams
        }, this.search)
    }
    // 添加脱敏
    addDesensitization = (desensitization) => {
        ajax.addDesensitization(desensitization).then(res => {
            if (res.code === 1) {
                this.setState({
                    addVisible: false
                })
                message.success('添加成功!');
                this.search();
            }
        })
    }
    // 删除脱敏
    delete = (record) => {
        ajax.delDesensitization({
            id: record.id
        }).then(res => {
            if (res.code === 1) {
                message.success('删除成功!');
                this.search()
            }
        })
    }
    // 打开面板
    showDesensitization = (record) => {
        this.setState({
            visibleSlidePane: true,
            selectedId: record
        })
    }
    closeSlidePane = () => {
        this.setState({
            visibleSlidePane: false,
            selectedId: null
        })
    }
    // 面板切换
    onTabChange = (tabKey) => {
        this.setState({
            nowView: tabKey
        })
    }
    // 获取点击具体查看血缘
    handleClickTable = (tableId) => {
        this.setState({
            tableId
        })
    }
    initialColumns = () => {
        return [
            {
                title: '脱敏名称',
                width: 140,
                dataIndex: 'name',
                render: (text, record) => {
                    return (
                        <a onClick={() => { this.showDesensitization(record) }}>身份证号脱敏</a>
                    )
                }
            },
            {
                title: '关联表数量',
                width: 140,
                dataIndex: 'relatedNum'
            },
            {
                title: '脱敏规则',
                width: 140,
                dataIndex: 'ruleName'
            },
            {
                title: '最近修改人',
                width: 200,
                dataIndex: 'modifyUserName'
            },
            {
                title: '最近修改时间',
                width: 200,
                dataIndex: 'gmtModified',
                render (text, record) {
                    return moment(text).format('YYYY-MM-DD HH:mm:ss')
                }
            },
            {
                title: '操作',
                width: 140,
                dataIndex: 'opera',
                render: (text, record) => {
                    return (
                        <Popconfirm
                            title="确定删除此条脱敏吗?"
                            okText="是"
                            cancelText="否"
                            onConfirm={() => { this.delete(record) }}
                        >
                            <a>删除</a>
                        </Popconfirm>
                    )
                }
            }
        ]
    }
    render () {
        const columns = this.initialColumns();
        const { cardLoading, table, editModalKey, addVisible, visibleSlidePane, selectedId, nowView, tableId } = this.state;
        return (
            <div className='box-1 m-card'>
                <Card
                    noHovering
                    bordered={false}
                    loading={false}
                    title={
                        <Search
                            placeholder='按脱敏名称搜索'
                            style={{ width: '200px', marginTop: '10px' }}
                            onChange={this.changeName}
                            onSearch={this.search}
                        />
                    }
                    extra={
                        <Button
                            type='primary'
                            style={{ marginTop: '10px' }}
                            onClick={() => { this.setState({ addVisible: true, editModalKey: Math.random() }) }}
                        >
                            添加脱敏
                        </Button>
                    }
                >
                    <Spin tip="正在加载中..." spinning={cardLoading}>
                        <Table
                            className="m-table"
                            columns={columns}
                            dataSource={table}
                            onChange={this.handleTableChange.bind(this)}
                        />
                    </Spin>
                    <SlidePane
                        className="m-tabs bd-top bd-right m-slide-pane"
                        onClose={this.closeSlidePane}
                        visible={visibleSlidePane}
                        style={{ right: '0px', width: '75%', height: '100%', minHeight: '600px' }}
                    >
                        <Tabs animated={false} onChange={this.onTabChange} activeKey={nowView}>
                            <TabPane tab="表关系" key="tableRelation">
                                <TableRelation
                                    // reload={this.search}
                                    tabKey={nowView}
                                    visibleSlidePane={visibleSlidePane}
                                    onTabChange={this.onTabChange}
                                    handleClickTable={this.handleClickTable}
                                    // goToTaskDev={this.props.goToTaskDev}
                                    // clickPatchData={this.clickPatchData}
                                    tableData={selectedId}
                                />
                            </TabPane>
                            <TabPane tab="血缘关系" key="bloodRelation">
                                <BloodRelation
                                    tabKey={nowView}
                                    visibleSlidePane={visibleSlidePane}
                                    tableId={tableId}
                                    tableData={selectedId}
                                />
                            </TabPane>
                        </Tabs>
                    </SlidePane>
                </Card>
                <AddDesensitization
                    key={editModalKey}
                    visible={addVisible}
                    onCancel={() => { this.setState({ addVisible: false }) }}
                    onOk={this.addDesensitization}
                />
            </div>
        )
    }
}
export default DesensitizationMange;
