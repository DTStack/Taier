import React, { Component } from 'react'
import { connect } from 'react-redux'
import {
    Input, Button, Popconfirm,
    Table, message, Card
} from 'antd'

import utils from 'utils'

import Api from '../../api'
import DataSourceForm from './form'
import { formItemLayout, DataSourceTypeFilter } from '../../comm/const'
import { DatabaseType } from '../../components/status'
import { getSourceTypes } from '../../store/modules/dataSource/sourceTypes'

const Search = Input.Search

class DataSource extends Component {
    state = {
        dataSource: {
            data: []
        },
        visible: false,
        visibleEdit: false,
        loading: false,
        title: '新增数据源',
        status: 'add',
        source: {}
    }

    componentDidMount () {
        this.loadDataSources({
            pageSize: 10,
            currentPage: 1
        })
        // this.props.getSourceTypes();
    }

    componentWillReceiveProps (nextProps) {
        const project = nextProps.project
        const oldProj = this.props.project

        if (oldProj.id !== 0 && project && oldProj.id !== project.id) {
            this.loadDataSources()
        }
    }

    loadDataSources = (params) => {
        const ctx = this

        this.setState({ loading: true })
        const reqParams = Object.assign({
            pageSize: 10,
            currentPage: 1
        }, params)

        Api.queryDataSource(reqParams).then((res) => {
            if (res.code === 1) {
                ctx.setState({ dataSource: res.data, loading: false })
            }
        })
    }

    searchDataSources = (query) => {
        this.loadDataSources({
            name: query
        })
    }

    addOrUpdateDataSource = (sourceFormData, formObj) => {
        const ctx = this
        const { title, status, source } = this.state
        let reqSource = sourceFormData

        if (status === 'edit') { // 编辑数据
            reqSource = Object.assign(source, sourceFormData)
        }

        Api.addOrUpdateSource(reqSource).then((res) => {
            if (res.code === 1) {
                formObj.resetFields()
                message.success(`${title}成功！`)
                ctx.setState({
                    visible: false
                })
                ctx.loadDataSources()
            }
        })
    }

    remove = (source) => {
        const ctx = this

        if (source.active === 1) {
            message.info('此数据源已在任务中被引用，无法删除!')

            return;
        }

        Api.deleteDataSource({ sourceId: source.id }).then((res) => {
            if (res.code === 1) {
                message.success('移除数据源成功！')
                ctx.loadDataSources()
            }
        })
    }

    testConnection = (source) => { // 测试数据源连通性
        const ctx = this

        Api.testDSConnection(source).then((res) => {
            if (res.code === 1) {
                message.success('数据源连接正常！')
            }
        })
    }

    handleTableChange = (pagination, filters) => {
        const params = {}

        if (filters.type) {
            params.type = filters.type[0]
        }

        params.currentPage = pagination.current
        this.setState({ current: pagination.current })
        this.loadDataSources(params)
    }

    initEdit = (source) => {
        this.setState({
            visible: true,
            title: '编辑数据源',
            status: 'edit',
            source
        })
    }

    initColumns = () => {
        return [{
            title: '数据源名称',
            dataIndex: 'dataName',
            key: 'dataName',
            width: 80
        }, {
            title: '类型',
            dataIndex: 'type',
            key: 'type',
            width: 80,
            render: (text, record) => {
                return <DatabaseType value={record.type} />
            },
            filters: DataSourceTypeFilter,
            filterMultiple: false
        },
        {
            title: '描述',
            dataIndex: 'dataDesc',
            key: 'dataDesc',
            width: 100
        }, {
            title: '最近修改人',
            dataIndex: 'modifyUserId',
            key: 'modifyUserId',
            render: (text, record) => {
                return record.modifyUser ? record.modifyUser.userName : ''
            }
        }, {
            title: '最近修改时间',
            dataIndex: 'gmtModified',
            key: 'gmtModified',
            render: text => utils.formatDateTime(text)
        }, {
            title: '状态',
            dataIndex: 'active',
            key: 'active',
            width: 100,
            render: (text, record) => {
                return record.active === 1 ? '使用中' : '未启用'
            }
        }, {
            title: '操作',
            width: 100,
            key: 'operation',
            render: (text, record) => {
                // active  '0：未启用，1：使用中'。  只有为0时，可以修改
                return (
                    <span key={record.id}>
                        <a onClick={() => { this.initEdit(record) }}>
                            编辑
                        </a>
                        <span className="ant-divider" />
                        <Popconfirm
                            title="确定删除此数据源？"
                            okText="确定" cancelText="取消"
                            onConfirm={() => { this.remove(record) }}
                        >
                            <a>删除</a>
                        </Popconfirm>
                    </span>
                )
            }
        }]
    }

    render () {
        const { visible, dataSource } = this.state
        const { project } = this.props
        const pagination = {
            total: dataSource.totalCount,
            defaultPageSize: 10
        };
        const title = (
            <div>
                <Search
                    placeholder="数据源名称"
                    style={{ width: 200 }}
                    onSearch={this.searchDataSources}
                />&nbsp;&nbsp;
            </div>
        )
        const extra = (
            <Button
                type="primary"
                className="right"
                onClick={() => {
                    this.setState({
                        visible: true,
                        source: {},
                        status: 'add',
                        title: '添加数据源'
                    })
                }}
            >新增数据源</Button>
        )

        return (
            <div className="project-member">
                <article className="section">
                    <h1 className="title black" style={{ paddingTop: '0' }}>
                        离线数据源
                    </h1>
                    <Card title={title} extra={extra}>
                        <Table
                            rowKey="id"
                            className="section-border"
                            pagination={pagination}
                            onChange={this.handleTableChange}
                            loading={this.state.loading}
                            columns={this.initColumns()}
                            dataSource={dataSource.data}
                        />
                    </Card>
                </article>
                <DataSourceForm
                    title={this.state.title}
                    visible={this.state.visible}
                    status={this.state.status}
                    handOk={this.addOrUpdateDataSource}
                    testConnection={this.testConnection}
                    sourceData={this.state.source}
                    handCancel={() => { this.setState({ visible: false }) }}
                />
            </div>
        )
    }
}
export default connect((state) => {
    return {
        project: state.project,
        sourceTypes: state.dataSource.sourceTypes
    }
}, dispatch => {
    return {
        getSourceTypes () {
            dispatch(getSourceTypes())
        }
    }
})(DataSource)
