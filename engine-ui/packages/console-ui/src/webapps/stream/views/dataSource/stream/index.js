import React, { Component } from 'react';
import { connect } from 'react-redux';
import { cloneDeep } from 'lodash';
import {
    Input, Button,
    Table, message, Card, Icon, Tooltip, Popconfirm
} from 'antd';

import { Circle } from 'widgets/circle';
import Api from '../../../api';
import DataSourceForm from '../form';
import DataSourceTaskListModal from '../dataSourceTaskListModal'
import { ExtTableCell } from '../extDataSourceMsg'

const Search = Input.Search

class DataSourceManaStream extends Component {
    state = {
        dataSource: {
            data: []
        },
        loading: false,
        title: '新增数据源',
        status: 'add',
        source: {},
        sourceTypes: []
    }

    componentDidMount () {
        this.loadDataSources({
            pageSize: 10,
            currentPage: 1
        })
        this.getSourceTypes();
    }

    // eslint-disable-next-line
	UNSAFE_componentWillReceiveProps (nextProps) {
        const project = nextProps.project
        const oldProj = this.props.project
        if (oldProj.id !== 0 && project && oldProj.id !== project.id) {
            this.loadDataSources()
            this.getSourceTypes();
        }
    }

    getSourceTypes () {
        Api.getDataSourceTypes().then((res) => {
            if (res.code == 1) {
                this.setState({
                    sourceTypes: res.data
                })
            }
        })
    }

    loadDataSources = (params) => {
        const ctx = this
        this.setState({ loading: true })
        const reqParams = Object.assign({
            pageSize: 10,
            currentPage: 1
        }, params)
        Api.streamQueryDataSource(reqParams).then((res) => {
            this.setState({
                loading: false
            })
            if (res.code === 1) {
                ctx.setState({ dataSource: res.data })
            }
        })
    }

    searchDataSources = (query) => {
        this.loadDataSources({
            name: query
        })
    }

    addOrUpdateDataSource = (sourceFormData, formObj, callBack) => {
        const ctx = this
        const { title, status, source } = this.state
        let reqSource = sourceFormData
        if (status === 'edit') { // 编辑数据
            reqSource = Object.assign(source, sourceFormData)
        }
        Api.streamSaveDataSource(reqSource).then((res) => {
            if (res.code === 1) {
                formObj.resetFields()
                message.success(`${title}成功！`)
                ctx.setState({
                    visible: false
                })
                ctx.loadDataSources()
                callBack();
            }
        })
    }
    openDataSourceModal = () => {
        Api.checkDataSourcePermission().then(res => {
            if (res.code === 1) {
                this.setState({
                    visible: true,
                    source: {},
                    status: 'add',
                    title: '添加数据源'
                })
            }
        })
    }
    remove = (source) => {
        const ctx = this
        if (source.active === 1) {
            message.info('此数据源已在任务中被引用，无法删除!')
            return;
        }
        Api.streamDeleteDataSource({ sourceId: source.id }).then((res) => {
            if (res.code === 1) {
                message.success('移除数据源成功！')
                ctx.loadDataSources()
            }
        })
    }

    testConnection = (source) => { // 测试数据源连通性
        Api.streamTestDataSourceConnection(source).then((res) => {
            if (res.code === 1 && res.data) {
                message.success('数据源连接正常！')
            } else if (res.code === 1 && !res.data) {
                message.error('数据源连接异常')
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
        Api.checkDataSourcePermission().then(res => {
            this.setState({
                visible: true,
                title: '编辑数据源',
                status: 'edit',
                source: cloneDeep(source)
            })
        })
    }
    getSourceType (type) {
        const { sourceTypes } = this.state;
        const source = sourceTypes.find((source) => {
            return source.value == type
        })
        return source ? source.name : '';
    }
    initColumns = () => {
        const text = '系统每隔10分钟会尝试连接一次数据源，如果无法连通，则会显示连接失败的状态。数据源连接失败会导致同步任务执行失败。';
        const { sourceTypes } = this.state;

        return [{
            title: '数据源名称',
            dataIndex: 'dataName',
            key: 'dataName'
        }, {
            title: '类型',
            dataIndex: 'type',
            key: 'type',
            width: '100px',
            render: (text, record) => {
                return this.getSourceType(text)
            },
            filters: sourceTypes.map((source) => {
                return { ...source, text: source.name }
            }),
            filterMultiple: false
        },
        {
            title: '描述',
            dataIndex: 'dataDesc',
            key: 'dataDesc'
        },
        {
            title: '连接信息',
            dataIndex: 'ext',
            key: 'ext',
            render: (empty, record) => {
                return <ExtTableCell sourceData={record} />
            }
        },
        // {
        //     title: '最近修改人',
        //     dataIndex: 'modifyUserId',
        //     key: 'modifyUserId',
        //     width: '120px',
        //     render: (text, record) => {
        //         return record.modifyUser ? record.modifyUser.userName : ''
        //     }
        // }, {
        //     title: '最近修改时间',
        //     dataIndex: 'gmtModified',
        //     key: 'gmtModified',
        //     width: '120px',
        //     render: text => utils.formatDateTime(text),
        // },
        {
            title: '应用状态',
            dataIndex: 'active',
            key: 'active',
            width: '70px',
            render: (active, record) => {
                return active === 1 ? <DataSourceTaskListModal type="stream" dataSource={record}>使用中</DataSourceTaskListModal> : '未使用'
            }
        },
        {
            title: <Tooltip placement="top" title={text} arrowPointAtCenter>
                <span>连接状态 &nbsp;
                    <Icon type="question-circle-o" />
                </span>
            </Tooltip>,
            dataIndex: 'linkState',
            key: 'linkState',
            width: '100px',
            render: (text, record) => {
                return record.linkState === 1
                    ? <span><Circle style={{ background: '#00A755' }} /> 正常</span>
                    : <span><Circle style={{ background: '#EF5350' }} /> 连接失败</span>
            }
        },
        {
            title: '操作',
            width: '150px',
            key: 'operation',
            render: (text, record) => {
                // active  '0：未启用，1：使用中'。  只有为0时，可以修改
                return (
                    <span key={record.id}>
                        {/* {
                            record.type === DATA_SOURCE.MYSQL
                            &&
                            <span>
                                <a onClick={this.openSyncModal.bind(this, record)}>
                                    同步历史
                                </a>
                                <span className="ant-divider" />
                                <Link to={`database/stream/db-sync/${record.id}/${record.dataName}`}>
                                    整库同步
                                </Link>
                                <span className="ant-divider" />
                            </span>
                        } */}
                        <a onClick={() => { this.initEdit(record) }}>
                            编辑
                        </a>
                        <span className="ant-divider" />
                        {
                            record.active === 1
                                ? <span style={{ color: '#ccc' }}>删除</span>
                                : (
                                    <Popconfirm
                                        title="确定删除此数据源？"
                                        okText="确定" cancelText="取消"
                                        onConfirm={() => { this.remove(record) }}
                                    >
                                        <a>删除</a>
                                    </Popconfirm>
                                )
                        }
                    </span>
                )
            }
        }]
    }

    openSyncModal = (record) => {
        this.setState({
            syncModalVisible: true,
            source: record
        });
    }

    closeSyncModal = () => {
        this.setState({
            syncModalVisible: false,
            source: {}
        });
    }

    render () {
        const { source, dataSource, sourceTypes } = this.state
        const pagination = {
            total: dataSource.totalCount,
            defaultPageSize: 10
        };
        const title = (
            <div>
                <Search
                    placeholder="数据源名称"
                    style={{ width: 200, padding: 0 }}
                    onSearch={this.searchDataSources}
                />&nbsp;&nbsp;
            </div>
        )
        const extra = (
            <Button
                type="primary"
                style={{ marginTop: 10 }}
                className="right"
                onClick={this.openDataSourceModal}
            >新增数据源</Button>
        )

        console.log('dataSource.data', dataSource.data);
        return (
            <div>
                <div className="shadow rdos-data-source">
                    <Card
                        title={title}
                        extra={extra}
                        noHovering
                        bordered={false}
                    >
                        <Table
                            rowKey="id"
                            className="m-table full-screen-table-47"
                            pagination={pagination}
                            onChange={this.handleTableChange}
                            loading={this.state.loading}
                            columns={this.initColumns()}
                            dataSource={dataSource.data}
                        />
                    </Card>
                </div>

                <DataSourceForm
                    title={this.state.title}
                    visible={this.state.visible}
                    status={this.state.status}
                    handOk={this.addOrUpdateDataSource}
                    testConnection={this.testConnection}
                    sourceData={source}
                    sourceTypes={sourceTypes}
                    showUserNameWarning={true}
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
})(DataSourceManaStream)
