import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Input, Select, Card, Table, Checkbox, Switch, message, Button, Modal } from 'antd';
import ajax from '../../../api/dataManage';
import moment from 'moment';
const Search = Input.Search;
const confirm = Modal.confirm;
const Option = Select.Option;
@connect(state => {
    return {
        projects: state.projects,
        user: state.user
    }
}, null)
class TableRelation extends Component {
    state = {
        tableData: [], // 表数据
        openStatusLoading: false, // 开关切换loading
        queryParams: {
            pageIndex: 1,
            pageSize: 20,
            configId: '',
            projectId: ''
        },
        checkAll: false,
        selectedRowKeys: [],
        openApply: undefined, // 批量开启还是关闭
        // 按项目名，表名，字段名，开关状态搜索
        projectId: undefined,
        tableName: undefined,
        columnsName: undefined,
        openStatus: '',
        // mock
        dataSource: [
            {
                key: '1',
                tableName: 'test1',
                tableColumns: 12,
                projects: 'test',
                projectsName: '测试',
                modifyUserName: 'admin@dtstack.com',
                gmtModified: '2018-01-01 12:12:12',
                status: 1, // 1为开，0为关
                deal: '查看血缘'
            }
        ]
    }
    componentDidMount () {
        const currentDesensitization = this.props.tableData;
        if (currentDesensitization) {
            this.loadTableRelation({
                pageIndex: 1,
                pageSize: 20,
                projectId: currentDesensitization.projectId
            })
        }
    }
    /* eslint-disable-next-line */
    componentWillReceiveProps (nextProps) {
        const currentDesensitization = this.props.tableData;
        if (currentDesensitization.projectId != nextProps.tableData.projectId && currentDesensitization.id != nextProps.tableData.id) {
            this.setState({
                queryParams: Object.assign(this.state.queryParams, { configId: nextProps.tableData.id, projectId: nextProps.tableData.projectId })
            }, () => {
                this.search() // 加载表关系
            })
        }
    }
    // 加载表关系
    search = () => {
        const { queryParams, projectId, tableName, columnsName, openStatus } = this.state;
        if (projectId) {
            queryParams.projectId = projectId
        }
        if (tableName) {
            queryParams.tableName = tableName
        }
        if (columnsName) {
            queryParams.columnsName = columnsName
        }
        if (openStatus) {
            queryParams.state = openStatus
        }
        this.loadTableRelation(queryParams)
    }
    loadTableRelation = (params) => {
        ajax.viewTableRelation(params).then(res => {
            if (res.code === 1) {
                this.setState({
                    tableData: res.data
                })
            }
        })
    }
    // 切换开关
    changeOpenStatus = (checked, record) => {
        const enable = checked === 0; // 开状态
        this.setState({
            openStatusLoading: true
        })
        this.operaSwitch({ ids: [record.id], enable: !enable })
    }
    // 批量按钮
    batchOpera = (openApply) => {
        const text = openApply ? '只能查看脱敏后的数据是否确认开启' : '可以查看原始数据是否确认关闭';
        const { selectedRowKeys } = this.state;
        if (selectedRowKeys > 0) {
            confirm({
                title: '开启/关闭脱敏',
                content: `开启脱敏后，数据开发、运维、访客角色的用户${text}？`,
                okText: openApply ? '开启脱敏' : '关闭脱敏',
                cancelText: '取消',
                onCancel: () => {
                    this.setState({
                        openApply: undefined
                    })
                },
                onOk: () => {
                    this.operaSwitch({ ids: selectedRowKeys, enable: openApply })
                }
            })
        } else {
            message.warning('请勾选要操作的列表')
        }
    }
    // 调用更新按钮接口
    operaSwitch = (params) => {
        ajax.updateOpenStatus(params).then(res => {
            this.setState({
                openStatusLoading: false
            })
            if (res.code === 1) {
                message.success('状态切换成功!');
                this.search();
            }
        })
    }
    // 改变project
    changeProject = (value) => {
        this.setState({
            queryParams: Object.assign(this.state.queryParams, { projectId: value })
        }, this.search)
    }
    // 表名字段名搜索
    changeName = (e) => {
        this.setState({
            queryParams: Object.assign(this.state.queryParams, { tableName: e.target.value })
        })
    }
    tableFooter = (currentPageData) => {
        return (
            <div className="ant-table-row  ant-table-row-level-0">
                <div style={{ padding: '15px 10px 10px 30px', display: 'inline-block' }}>
                    <Checkbox
                        checked={this.state.checkAll}
                        onChange={this.onCheckAllChange}
                    >
                    </Checkbox>
                </div>
                <div style={{ display: 'inline-block', marginLeft: '15px' }}>
                    <Button type="primary" size="small" onClick={this.batchOpera.bind(this, true)}>批量开启</Button>&nbsp;
                    <Button type="primary" size="small" onClick={this.batchOpera.bind(this, false)}>批量关闭</Button>&nbsp;
                </div>
            </div>
        )
    }
    onSelectChange = (selectedRowKeys) => {
        // const checkAll = selectedRowKeys.length === this.state.tableData.data.length;
        this.setState({
            selectedRowKeys
            // checkAll
        })
    }
    onCheckAllChange = (e) => {
        const { tableData } = this.state;
        let selectedRowKeys = [];
        if (e.target.checked) {
            selectedRowKeys = tableData.data.map(item => item.tableId)
        }
        this.setState({
            checkAll: e.target.checked,
            selectedRowKeys
        })
    }
    // table change
    handleTableChange = (pagination, filters) => {
        const queryParams = Object.assign(this.state.queryParams, {
            pageIndex: pagination.current,
            openStatus: filters.status
        })
        this.setState({
            queryParams
        }, this.search)
    }
    // 查看血缘
    viewBlood = (record) => {
        const { onTabChange, handleClickTable } = this.props;
        onTabChange('bloodRelation'); // 切换至血缘关系
        handleClickTable(record);
    }
    initColumns = () => {
        return [
            {
                title: '表',
                width: 120,
                dataIndex: 'tableName',
                render: (text, record) => {
                    return text
                }
            },
            {
                title: '字段',
                width: 140,
                dataIndex: 'tableColumns',
                render: (text, record) => {
                    return text
                }
            },
            {
                title: '项目名称',
                width: 140,
                dataIndex: 'projects',
                render: (text, record) => {
                    return text
                }
            },
            {
                title: '项目显示名称',
                width: 140,
                dataIndex: 'projectsName',
                render: (text, record) => {
                    return text
                }
            },
            {
                title: '最近修改人',
                width: 150,
                dataIndex: 'modifyUserName',
                render: (text, record) => {
                    return text
                }
            },
            {
                title: '最近修改时间',
                width: 150,
                dataIndex: 'gmtModified',
                render: (text, record) => {
                    return moment(text).format('YYYY-MM-DD HH:mm:ss')
                }
            },
            {
                title: '启用状态',
                width: 140,
                dataIndex: 'enable',
                filters: [
                    { text: '开', value: '0' },
                    { text: '关', value: '1' }
                ],
                render: (text, record) => {
                    const isChecked = text === 0; // 开
                    const { openStatusLoading } = this.state;
                    return (
                        <Switch
                            checkedChildren="开"
                            unCheckedChildren="关"
                            disabled={openStatusLoading}
                            checked={isChecked}
                            onChange={this.changeOpenStatus.bind(this, record)}
                        />
                    )
                }
            },
            {
                title: '操作',
                width: 100,
                dataIndex: 'opera',
                render: (text, record) => {
                    return (
                        <a onClick={() => { this.viewBlood(record) }}>查看血缘</a>
                    )
                }
            }
        ]
    }
    render () {
        const columns = this.initColumns();
        const { dataSource, selectedRowKeys } = this.state;
        const rowSelection = {
            selectedRowKeys,
            onChange: this.onSelectChange
        }
        const { projects } = this.props;
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
            <div className='m-card'>
                <Card
                    noHovering
                    bordered={false}
                    loading={false}
                    title={
                        <div style={{ marginTop: '10px' }}>
                            <Select
                                // allowClear
                                // defaultValue={}
                                placeholder='项目名称'
                                style={{ width: '150px', marginRight: '20px' }}
                                onChange={this.changeProject}
                            >
                                {projectsOptions}
                            </Select>
                            <Search
                                placeholder="按表名、字段名搜索"
                                style={{ width: '200px' }}
                                onChange={this.changeName}
                                onSearch={this.search}
                            />
                        </div>
                    }
                >
                    <Table
                        className="m-table-fix m-table"
                        columns={columns}
                        dataSource={dataSource}
                        rowSelection={rowSelection}
                        onChange={this.handleTableChange}
                        footer={this.tableFooter}
                    />
                </Card>
            </div>
        )
    }
}
export default TableRelation;
