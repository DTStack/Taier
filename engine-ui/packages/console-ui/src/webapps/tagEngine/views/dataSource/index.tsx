import * as React from 'react';
import { connect } from 'react-redux';
import { cloneDeep, pickBy } from 'lodash';
import {
    Input, Button,
    Table, message, Card, Icon, Tooltip, Popconfirm
} from 'antd';

import { Circle } from 'widgets/circle';
import Api from '../../../api/dataSource';
import DataSourceForm from '../form';
import DataSourceTaskListModal from '../dataSourceTaskListModal'
import { ExtTableCell } from '../extDataSourceMsg'

const Search = Input.Search

class DataSourceManaStream extends React.Component<any, any> {
    state: any = {
        dataSource: {
            data: []
        },
        sourceTypes: [],
        title: '新增数据源',
        loading: false,
        status: 'add',
        currentPage: 1,
        pageSize: 10,
        source: {},
        name: '',
        type: undefined
    }

    componentDidMount () {
        this.loadDataSources()
        this.getSourceTypes();
    }

    // eslint-disable-next-line
	UNSAFE_componentWillReceiveProps(nextProps: any) {
        const project = nextProps.project
        const oldProj = this.props.project
        if (oldProj.id !== 0 && project && oldProj.id !== project.id) {
            this.loadDataSources()
            this.getSourceTypes();
        }
    }

    getSourceTypes () {
        Api.getDataSourceTypes().then((res: any) => {
            if (res.code == 1) {
                this.setState({
                    sourceTypes: res.data
                })
            }
        })
    }

    loadDataSources = () => {
        const { pageSize, currentPage, name, type } = this.state;
        this.setState({ loading: true })
        const reqParams: any = {
            pageSize,
            currentPage,
            name,
            type
        }
        Api.streamQueryDataSource(reqParams).then((res: any) => {
            this.setState({
                loading: false
            })
            if (res.code === 1) {
                this.setState({ dataSource: res.data })
            }
        })
    }

    searchDataSources = (query: any) => {
        this.setState({
            name: query,
            currentPage: 1
        }, this.loadDataSources)
    }

    addOrUpdateDataSource = (sourceFormData: any, formObj: any, callBack: any) => {
        const ctx = this
        const { title, status, source } = this.state
        let reqSource = sourceFormData
        if (status === 'edit') { // 编辑数据
            reqSource = Object.assign(cloneDeep(source), sourceFormData)
        }
        if (reqSource.dataJson.openKerberos) {
            reqSource.dataJsonString = JSON.stringify(reqSource.dataJson)
            console.log(reqSource)
            delete reqSource.modifyUser;
            delete reqSource.dataJson;
            reqSource = pickBy(reqSource, (item, key) => { // 过滤掉空字符串和值为null的属性，并且过滤掉编辑时的kerberos字段
                if (key === 'kerberosFile' && (!item.type)) {
                    return false
                }
                return item != null
            })
            Api.streamSaveDataSourceWithKerberos(reqSource).then((res: any) => {
                if (res.code === 1) {
                    message.success(`${title}成功！`)
                    ctx.setState({
                        visible: false
                    })
                    formObj.resetFields()
                    ctx.loadDataSources()
                    callBack();
                }
            })
        } else {
            console.log(reqSource)
            Api.streamSaveDataSource(reqSource).then((res: any) => {
                if (res.code === 1) {
                    message.success(`${title}成功！`)
                    ctx.setState({
                        visible: false
                    })
                    formObj.resetFields()
                    ctx.loadDataSources()
                    callBack();
                }
            })
        }
    }
    openDataSourceModal = () => {
        Api.checkDataSourcePermission().then((res: any) => {
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
    remove = (source: any) => {
        const ctx = this
        if (source.active === 1) {
            message.info('此数据源已在任务中被引用，无法删除!')
            return;
        }
        Api.streamDeleteDataSource({ sourceId: source.id }).then((res: any) => {
            if (res.code === 1) {
                message.success('移除数据源成功！')
                ctx.loadDataSources()
            }
        })
    }

    testConnection = (formSource: any) => { // 测试数据源连通性
        const { source } = this.state;
        formSource.id = source.id;
        console.log(formSource, source)
        if (formSource.dataJson.openKerberos) {
            formSource.dataJsonString = JSON.stringify(formSource.dataJson)
            delete formSource.dataJson;
            formSource = pickBy(formSource, (item, key) => { // 过滤掉空字符串和值为null的属性，并且过滤掉编辑时的kerberos字段
                if (key === 'kerberosFile' && (!item.type)) {
                    return false
                }
                return item != null
            })
            Api.streamTestDataSourceConnectionWithKerberos(formSource).then((res: any) => {
                if (res.code === 1 && res.data) {
                    message.success('数据源连接正常！')
                } else if (res.code === 1 && !res.data) {
                    message.error('数据源连接异常')
                }
            })
        } else {
            Api.streamTestDataSourceConnection(formSource).then((res: any) => {
                if (res.code === 1 && res.data) {
                    message.success('数据源连接正常！')
                } else if (res.code === 1 && !res.data) {
                    message.error('数据源连接异常')
                }
            })
        }
    }

    handleTableChange = (pagination: any, filters: any) => {
        this.setState({
            currentPage: pagination.current,
            type: filters.type && filters.type[0]
        }, this.loadDataSources);
    }

    initEdit = (source: any) => {
        Api.checkDataSourcePermission().then((res: any) => {
            if (res.code === 1) {
                this.setState({
                    visible: true,
                    title: '编辑数据源',
                    status: 'edit',
                    source: cloneDeep(source)
                })
            }
        })
    }
    getSourceType (type: any) {
        const { sourceTypes } = this.state;
        const source = sourceTypes.find((source: any) => {
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
            render: (text: any, record: any) => {
                return this.getSourceType(text)
            },
            filters: sourceTypes.map((source: any) => {
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
            render: (empty: any, record: any) => {
                return <ExtTableCell sourceData={record} />
            }
        },
        // {
        //     title: '最近修改人',
        //     dataIndex: 'modifyUserId',
        //     key: 'modifyUserId',
        //     width: '120px',
        //     render: (text: any, record: any) => {
        //         return record.modifyUser ? record.modifyUser.userName : ''
        //     }
        // }, {
        //     title: '最近修改时间',
        //     dataIndex: 'gmtModified',
        //     key: 'gmtModified',
        //     width: '120px',
        //     render: (text: any) => utils.formatDateTime(text),
        // },
        {
            title: '应用状态',
            dataIndex: 'active',
            key: 'active',
            width: '70px',
            render: (active: any, record: any) => {
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
            render: (text: any, record: any) => {
                return record.linkState === 1
                    ? <span><Circle style={{ background: '#00A755' }} /> 正常</span>
                    : <span><Circle style={{ background: '#EF5350' }} /> 连接失败</span>
            }
        },
        {
            title: '操作',
            width: '150px',
            key: 'operation',
            render: (text: any, record: any) => {
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

    openSyncModal = (record: any) => {
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
        const { source, dataSource, sourceTypes, currentPage, pageSize } = this.state
        const pagination: any = {
            total: dataSource.totalCount,
            pageSize: pageSize,
            current: currentPage
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
        return (
            <div>
                <div className="shadow rdos-data-source">
                    <Card
                        title={title}
                        extra={extra}
                        noHovering
                        bordered={false}
                        className="noBorderBottom"
                    >
                        <Table
                            rowKey="id"
                            className="dt-ant-table dt-ant-table--border full-screen-table-47"
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
class WrapDataSourceMana extends React.Component<any, any> {
    render () {
        const { project } = this.props;
        return (
            <DataSourceManaStream key={project.id} {...this.props} />
        )
    }
}
export default connect((state: any) => {
    return {
        project: state.project,
        sourceTypes: state.dataSource.sourceTypes
    }
})(WrapDataSourceMana)
