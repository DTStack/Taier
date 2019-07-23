import React, { Component } from 'react';
import ajax from '../../../api/dataManage';
import { connect } from 'react-redux';
import { Input, Spin, Table, Button, Card, Popconfirm, message, Select } from 'antd';
import moment from 'moment';
import AddUpdateRules from './addUpdateRules';
const Search = Input.Search;
const Option = Select.Option;

@connect(state => {
    return {
        projects: state.projects
    }
}, null)

class RuleManage extends Component {
    state = {
        cardLoading: false,
        addVisible: false, // 添加规则
        table: [], // 表数据
        queryParams: {
            currentPage: 1,
            pageSize: 20,
            name: undefined,
            pjId: undefined
        },
        total: 0,
        editModalKey: null,
        status: undefined, // 新增或编辑
        source: {} // 编辑规则信息
    }
    componentDidMount () {
        this.search();
    }
    search = () => {
        this.setState({
            cardLoading: true
        })
        const { queryParams } = this.state;
        ajax.searchRule(queryParams).then(res => {
            if (res.code === 1) {
                this.setState({
                    table: res.data.data,
                    total: res.data.totalCount,
                    cardLoading: false
                })
            } else {
                this.setState({
                    cardLoading: false
                })
            }
        })
    }
    clickAddRule = () => {
        ajax.voidCheckPermission().then(res => {
            if (res.code === 1) {
                this.setState({
                    addVisible: true,
                    status: 'add',
                    source: {},
                    editModalKey: Math.random()
                })
            }
        })
    }
    /**
     * 添加规则
     */
    addRule = (params) => {
        ajax.addRule(params).then(res => {
            if (res.code === 1) {
                this.setState({
                    addVisible: false
                })
                message.success('添加成功!');
                this.search();
            }
        })
    }
    /**
     * 更新规则
     */
    updateRule = (params) => {
        ajax.updateRule(params).then(res => {
            if (res.code === 1) {
                this.setState({
                    addVisible: false
                })
                message.success('更新成功!');
                this.search();
            }
        })
    }
    /**
     * 删除规则
     */
    delete = (record) => {
        ajax.delRule({
            id: record.id,
            projectId: record.projectId
        }).then(res => {
            if (res.code === 1) {
                message.success('删除成功!');
                this.search()
            }
        })
    }
    /**
     * 编辑规则
     */
    editRule = (record) => {
        ajax.voidCheckPermission({ projectId: record.projectId }).then(res => {
            if (res.code === 1) {
                ajax.editRule({
                    id: record.id,
                    projectId: record.projectId
                }).then(res => {
                    if (res.code === 1) {
                        this.setState({
                            source: res.data
                        })
                    }
                })
                this.setState({
                    addVisible: true,
                    status: 'edit',
                    editModalKey: Math.random()
                })
            }
        })
    }
    changeName = (e) => {
        this.setState({
            queryParams: Object.assign(this.state.queryParams, {
                name: e.target.value,
                currentPage: 1
            })
        })
    }
    changeProject = (value) => {
        this.setState({
            queryParams: Object.assign(this.state.queryParams, {
                pjId: value,
                currentPage: 1
            })
        }, this.search)
    }
    handleTableChange = (pagination, filters, sorter) => {
        const queryParams = Object.assign(this.state.queryParams, { currentPage: pagination.current })
        this.setState({
            queryParams
        }, this.search)
    }
    initialColumns = () => {
        return [
            {
                title: '规则名称',
                width: 140,
                dataIndex: 'name'
            },
            {
                title: '项目名称',
                width: 140,
                dataIndex: 'projectName'
            },
            {
                title: '项目显示名称',
                width: 140,
                dataIndex: 'projectAlia'
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
                        <span>
                            <a onClick={() => { this.editRule(record) }}>编辑</a>
                            <span className="ant-divider"></span>
                            <Popconfirm
                                title="确定删除此条规则吗?"
                                okText="是"
                                cancelText="否"
                                onConfirm={() => { this.delete(record) }}
                            >
                                <a>删除</a>
                            </Popconfirm>
                        </span>
                    )
                }
            }
        ]
    }
    render () {
        const columns = this.initialColumns();
        const { table, cardLoading, addVisible, status, source, editModalKey, queryParams, total } = this.state;
        const { projects } = this.props;
        const pagination = {
            current: queryParams.currentPage,
            pageSize: queryParams.pageSize,
            total
        }
        const projectsOptions = projects.map(item => {
            return <Option
                title={item.projectAlias}
                key={item.id}
                name={item.projectAlias}
                value={`${item.id}`}
            >
                {item.projectAlias}
            </Option>
        })
        return (
            <div className='box-1 m-card'>
                <Card
                    noHovering
                    bordered={false}
                    loading={false}
                    title={
                        <div>
                            所属项目：
                            <Select
                                allowClear
                                showSearch
                                style={{ width: '150px', marginRight: '10px' }}
                                placeholder='请选择所属项目'
                                optionFilterProp='children'
                                filterOption={(inputVal, option) => {
                                    return option.props.children.toLowerCase().indexOf(inputVal.toLowerCase()) >= 0
                                }}
                                onChange={this.changeProject}
                            >
                                {projectsOptions}
                            </Select>
                            <Search
                                placeholder='按规则名称搜索'
                                style={{ width: '200px' }}
                                onChange={this.changeName}
                                onSearch={this.search}
                            />
                        </div>
                    }
                    extra={
                        <Button
                            type='primary'
                            style={{ marginTop: '10px' }}
                            onClick={this.clickAddRule}
                        >
                            创建规则
                        </Button>
                    }
                >
                    <Spin tip="正在加载中..." spinning={cardLoading}>
                        <Table
                            className="m-table"
                            columns={columns}
                            dataSource={table}
                            pagination={pagination}
                            onChange={this.handleTableChange.bind(this)}
                        />
                    </Spin>
                </Card>
                <AddUpdateRules
                    key={editModalKey}
                    visible={addVisible}
                    onCancel={() => { this.setState({ addVisible: false, source: {}, status: undefined }) }}
                    onOk={status === 'add' ? this.addRule : this.updateRule}
                    status={status}
                    dataSource={source}
                />
            </div>
        )
    }
}
export default RuleManage;
