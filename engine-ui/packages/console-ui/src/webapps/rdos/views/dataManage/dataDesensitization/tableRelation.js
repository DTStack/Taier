import React, { Component } from 'react';
import { Input, Select, Card, Table, Checkbox, Switch, message } from 'antd';
import ajax from '../../../api/dataManage';
const Search = Input.Search;
class TableRelation extends Component {
    state = {
        tableData: [], // 表数据
        openStatusLoading: false, // 开关切换loading
        queryParams: {
            pageIndex: 1,
            pageSize: 20,
            id: '' // 脱敏id
        },
        // 按项目名，表名，字段名，开关状态搜索
        projectName: undefined,
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
                admin: 'admin@dtstack.com',
                time: '2018-01-01 12:12:12',
                status: 1, // 1为开，0为关
                deal: '查看血缘'
            }
        ]
    }
    componentDidMount () {
        const currentDesensitization = this.props.tabData;
        if (currentDesensitization) {
        }
    }
    /* eslint-disable-next-line */
    componentWillReceiveProps (nextProps) {
        const currentDesensitization = this.props.tabData;
        if (currentDesensitization.id != nextProps.tabData.id) {
            this.setState({
                queryParams: Object.assign(this.state.queryParams, { id: nextProps.tabData.id })
            }, () => {
                // this.search() // 加载表关系
            })
        }
    }
    // 加载表关系
    search = () => {
        const { queryParams, projectName, tableName, columnsName, openStatus } = this.state;
        if (projectName) {
            queryParams.projectName = projectName
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
    changeOpenStatus = (checked) => {
        this.setState({
            openStatusLoading: true
        })
        ajax.updateOpenStatus({
            status: checked ? 0 : 1
        }).then(res => {
            this.setState({
                openStatusLoading: false
            })
            if (res.code === 1) {
                message.success('状态切换成功');
                // this.loadTableRelation();
            }
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

    initColumns = () => {
        return [
            {
                title: '表',
                width: 120,
                dataIndex: 'tableName',
                render: (text, record) => {
                    return (
                        <Checkbox>{text}</Checkbox>
                    )
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
                dataIndex: 'admin',
                render: (text, record) => {
                    return text
                }
            },
            {
                title: '最近修改时间',
                width: 150,
                dataIndex: 'time',
                render: (text, record) => {
                    return text
                }
            },
            {
                title: '启用状态',
                width: 140,
                dataIndex: 'status',
                filters: [
                    { text: '开', value: '1' },
                    { text: '关', value: '0' }
                ],
                render: (text, record) => {
                    const isChecked = text === 1;
                    const { openStatusLoading } = this.state;
                    return (
                        <Switch
                            checkedChildren="开"
                            unCheckedChildren="关"
                            disabled={openStatusLoading}
                            checked={isChecked}
                            onChange={this.changeOpenStatus.bind(this)}
                        />
                    )
                }
            },
            {
                title: '操作',
                width: 100,
                dataIndex: 'deal',
                render: (text, record) => {
                    return (
                        <a>查看血缘</a>
                    )
                }
            }
        ]
    }
    render () {
        const columns = this.initColumns();
        const { dataSource } = this.state;
        return (
            <div className='m-card'>
                <Card
                    noHovering
                    bordered={false}
                    loading={false}
                    title={
                        <div style={{ marginTop: '10px' }}>
                            <Select
                                mode="multiple"
                                allowClear
                                placeholder='项目名称'
                                style={{ width: '200px', marginRight: '20px' }}
                            />
                            <Search
                                placeholder="按表名、字段名搜索"
                                style={{ width: '200px' }}
                            />
                        </div>
                    }
                >
                    <Table
                        className="m-table"
                        columns={columns}
                        dataSource={dataSource}
                        onChange={this.handleTableChange}
                    />
                </Card>
            </div>
        )
    }
}
export default TableRelation;
