import React, { Component } from 'react';
import { connect } from 'react-redux';
import { debounce } from 'lodash';
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
        tableData: [], // 父级传递参数
        openStatusLoading: false, // 开关切换loading
        queryParams: {
            currentPage: 1,
            pageSize: 20,
            configId: ''
        },
        total: 0,
        loading: false,
        checkAll: false,
        editRecord: [], // 单击开关
        selectedRowKeys: [],
        openApply: undefined, // 批量开启还是关闭
        // 按项目名，表名，开关状态搜索
        pjId: undefined,
        tableName: undefined,
        enable: '',
        dataSource: [],
        relatedProject: [] // 项目列表
    }
    /** componentDidMount () {
        const currentDesensitization = this.props.tableData;
        if (currentDesensitization) {
            this.loadTableRelation({
                currentPage: 1,
                pageSize: 20,
                configId: currentDesensitization.id
            })
            this.getRelatedPorjects({
                configId: currentDesensitization.id
            })
        }
    }
    */
    /* eslint-disable-next-line */
    componentWillReceiveProps (nextProps) {
        const currentDesensitization = this.props.tableData;
        const { tabKey, tableData } = nextProps;
        if (currentDesensitization.id != tableData.id) {
            this.setState({
                queryParams: Object.assign(this.state.queryParams, { configId: tableData.id })
            }, () => {
                this.search() // 加载表关系
                this.getRelatedPorjects({ configId: tableData.id }) // 加载项目列表
            })
        }
        if (tabKey && this.props.tabKey !== tabKey && tabKey === 'tableRelation') {
            this.search()
            this.getRelatedPorjects({ configId: tableData.id })
        }
    }
    /**
     * 加载表关系
     * @param {Object} queryParams 搜索筛选条件
     */
    search = () => {
        const { queryParams, pjId, tableName, enable } = this.state;
        if (pjId) {
            queryParams.pjId = pjId
        }
        if (tableName) {
            queryParams.tableName = tableName
        }
        if (enable) {
            queryParams.state = enable
        }
        this.loadTableRelation(queryParams)
    }
    loadTableRelation = (params) => {
        this.setState({
            loading: true
        })
        ajax.viewTableRelation(params).then(res => {
            if (res.code === 1) {
                this.setState({
                    dataSource: res.data.data,
                    total: res.data.totalCount,
                    loading: false
                }, () => {
                    this.props.handleClickTable(this.state.dataSource[0] ? this.state.dataSource[0] : {}) // 解决第一次切换tab栏
                })
            } else {
                this.setState({
                    loading: false,
                    dataSource: []
                })
            }
        })
    }
    /**
     * 获取关联项目
     */
    getRelatedPorjects = (params) => {
        ajax.getRelatedPorjects(params).then(res => {
            if (res.code === 1) {
                this.setState({
                    relatedProject: res.data
                })
            }
        })
    }
    projectsOptions () {
        const { relatedProject } = this.state;
        return relatedProject.map((item, index) => {
            return <Option
                key={item.pjId}
                value={`${item.pjId}`}
            >
                {item.projectName}
            </Option>
        })
    }
    /**
     * 切换开关按钮
     */
    changeOpenStatus = (checked, record) => {
        const enable = checked === 0 ? 1 : 0; // 开状态
        this.debounceOperaSwitch({ ids: [record.id], enable })
    }
    /**
     * 批量按钮
     */
    batchOpera = (openApply) => {
        const text = openApply === 0 ? '只能查看脱敏后的数据是否确认开启' : '可以查看原始数据是否确认关闭';
        const { selectedRowKeys } = this.state;
        if (selectedRowKeys.length > 0) {
            confirm({
                title: '开启/关闭脱敏',
                content: `开启脱敏后，数据开发、运维、访客角色的用户${text}？`,
                okText: openApply === 0 ? '开启脱敏' : '关闭脱敏',
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
    /**
     * 调用更新按钮接口
     */
    operaSwitch = (params) => {
        ajax.updateOpenStatus(params).then(res => {
            this.setState({
                openStatusLoading: true
            })
            if (res.code === 1) {
                this.setState({
                    selectedRowKeys: [],
                    checkAll: false,
                    openStatusLoading: false
                })
                message.success('状态切换成功!');
                this.debounceSearch();
            } else {
                this.setState({
                    openStatusLoading: false
                })
            }
        })
    }
    debounceOperaSwitch = debounce(this.operaSwitch, 300, { 'maxWait': 2000 })
    debounceSearch = debounce(this.search, 300, { 'maxWait': 2000 })
    /**
     * 改变project
     */
    changeProject = (value) => {
        this.setState({
            queryParams: Object.assign(this.state.queryParams, {
                pjId: value,
                currentPage: 1
            })
        }, this.search)
    }
    /**
     * 表名字段名搜索
     */
    changeName = (e) => {
        this.setState({
            queryParams: Object.assign(this.state.queryParams, {
                tableName: e.target.value,
                currentPage: 1
            })
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
                    <Button type="primary" size="small" onClick={this.batchOpera.bind(this, 0)}>批量开启</Button>&nbsp;
                    <Button type="primary" size="small" onClick={this.batchOpera.bind(this, 1)}>批量关闭</Button>&nbsp;
                </div>
            </div>
        )
    }
    onSelectChange = (selectedRowKeys) => {
        const checkAll = selectedRowKeys.length === this.state.dataSource.length;
        this.setState({
            selectedRowKeys,
            checkAll
        })
    }
    onCheckAllChange = (e) => {
        const { dataSource } = this.state;
        let selectedRowKeys = [];
        if (e.target.checked) {
            selectedRowKeys = dataSource.map(item => item.id)
        }
        this.setState({
            checkAll: e.target.checked,
            selectedRowKeys
        })
    }
    // table change
    handleTableChange = (pagination, filters) => {
        const queryParams = Object.assign(this.state.queryParams, {
            currentPage: pagination.current,
            enable: filters.enable
        })
        this.setState({
            queryParams,
            selectedRowKeys: [],
            checkAll: false
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
                dataIndex: 'columnName',
                render: (text, record) => {
                    return text
                }
            },
            {
                title: '项目名称',
                width: 140,
                dataIndex: 'projectName',
                render: (text, record) => {
                    return text
                }
            },
            {
                title: '项目显示名称',
                width: 140,
                dataIndex: 'projectAlia',
                render: (text, record) => {
                    return text
                }
            },
            {
                title: '最近修改人',
                width: 170,
                dataIndex: 'modifyUserName',
                render: (text, record) => {
                    return text
                }
            },
            {
                title: '最近修改时间',
                width: 170,
                dataIndex: 'gmtModified',
                render: (text, record) => {
                    return moment(text).format('YYYY-MM-DD HH:mm:ss')
                }
            },
            {
                title: '启用状态',
                width: 100,
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
                            onChange={() => this.changeOpenStatus(text, record)}
                        />
                    )
                }
            },
            {
                title: '操作',
                width: 80,
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
        const { dataSource, selectedRowKeys, loading, total, queryParams } = this.state;
        const rowSelection = {
            selectedRowKeys,
            onChange: this.onSelectChange
        }
        const pagination = {
            current: queryParams.currentPage,
            pageSize: queryParams.pageSize,
            total
        }
        return (
            <div className='m-card'>
                <Card
                    noHovering
                    bordered={false}
                    loading={false}
                    title={
                        <div style={{ marginTop: '10px' }}>
                            <Select
                                allowClear
                                placeholder='项目名称'
                                style={{ width: '150px', marginRight: '20px' }}
                                onChange={this.changeProject}
                            >
                                {this.projectsOptions()}
                            </Select>
                            <Search
                                placeholder="按表名搜索"
                                style={{ width: '200px' }}
                                onChange={this.changeName}
                                onSearch={this.search}
                            />
                        </div>
                    }
                >
                    <Table
                        className="m-table-fix m-table"
                        rowKey="id"
                        loading={loading}
                        columns={columns}
                        dataSource={dataSource}
                        rowSelection={rowSelection}
                        onChange={this.handleTableChange}
                        pagination={pagination}
                        footer={dataSource && dataSource.length > 0 ? this.tableFooter : ''}
                    />
                </Card>
            </div>
        )
    }
}
export default TableRelation;
