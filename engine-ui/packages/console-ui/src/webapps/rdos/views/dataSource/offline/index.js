import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router';
import { 
    Input, Button, Popconfirm,
    Table, message, Card, Icon, Tooltip
 } from 'antd';

import utils from 'utils';

import { Circle } from 'widgets/circle';
import Api from '../../../api';
import DataSourceForm from '../form';
import DbSyncModal from '../syncModal';
import { DataSourceTypeFilter, DATA_SOURCE } from '../../../comm/const';
import { DatabaseType } from '../../../components/status';
import { getSourceTypes } from '../../../store/modules/dataSource/sourceTypes';
import DataSourceTaskListModal from '../dataSourceTaskListModal';

const Search = Input.Search

class DataSourceMana extends Component {

    state = {
        dataSource: {
            data: [],
        },
        visible: false,
        syncModalVisible: false,
        loading: false,
        title: '新增数据源',
        status: 'add',
        source: {},
    }

    componentDidMount() {
        this.loadDataSources({
            pageSize: 10,
            currentPage: 1,
        })
        this.props.getSourceTypes();
    }

    componentWillReceiveProps(nextProps) {
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
            currentPage: 1,
        }, params)
        Api.queryDataSource(reqParams).then((res) => {
            if (res.code === 1) {
                ctx.setState({ dataSource: res.data, loading: false })
            }
        })
    }

    searchDataSources = (query) => {
        this.loadDataSources({
            name: query,
        })
    }

    addOrUpdateDataSource = (sourceFormData, formObj,callBack) => {
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
                    visible: false,
                })
                ctx.loadDataSources()
                callBack();
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
            if (res.code === 1 && res.data) {
                message.success('数据源连接正常！')
            }else if(res.code===1&& !res.data){
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
        this.setState({
            visible: true,
            title: '编辑数据源',
            status: 'edit',
            source,
        })
    }

    initColumns = () => {
        const text = "系统每隔10分钟会尝试连接一次数据源，如果无法连通，则会显示连接失败的状态。数据源连接失败会导致同步任务执行失败。";
        return [{
            title: '数据源名称',
            dataIndex: 'dataName',
            key: 'dataName',
        }, {
            title: '类型',
            dataIndex: 'type',
            key: 'type',
            render: (text, record) => {
                return <DatabaseType value={record.type} />
            },
            filters: DataSourceTypeFilter,
            filterMultiple: false,
        }, 
        {
            title: '描述',
            dataIndex: 'dataDesc',
            key: 'dataDesc',
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
            render: text => utils.formatDateTime(text),
        }, {
            title: '应用状态',
            dataIndex: 'active',
            key: 'active',
            render: (active,record) => {
                return active === 1 ? <DataSourceTaskListModal type="offline" dataSource={record}>使用中</DataSourceTaskListModal> : '未使用'
            },
        }, 
        {
            title: <Tooltip placement="top" title={text} arrowPointAtCenter>
                        <span>连接状态 &nbsp;
                            <Icon type="question-circle-o" />
                        </span>
                    </Tooltip>,
            dataIndex: 'linkState',
            key: 'linkState',
            render: (linkState) => {
                return linkState === 1 ? 
                    <span><Circle style={{ background: '#00A755' }}/> 正常</span> : 
                    <span><Circle style={{ background: '#EF5350' }}/> 连接失败</span>
            },
        },
         {
            title: <div className="txt-right m-r-8">操作</div>,
            width: '230px',
            className: 'txt-right m-r-8',
            key: 'operation',
            render: (text, record) => {
                 // active  '0：未启用，1：使用中'。  只有为0时，可以修改
                return (
                    <span key={record.id}>
                        {
                            record.type === DATA_SOURCE.MYSQL
                            &&
                            <span>
                                <a onClick={this.openSyncModal.bind(this, record)}>
                                    同步历史
                                </a>
                                <span className="ant-divider" />
                                <Link to={`database/offLineData/db-sync/${record.id}/${record.dataName}`}>
                                    整库同步
                                </Link>
                                <span className="ant-divider" />
                            </span>
                        }
                        <a onClick={() => {this.initEdit(record)}}>
                            编辑
                        </a>
                        <span className="ant-divider" />
                        { 
                            record.active === 1 ?
                                <Popconfirm
                                    title="此数据源已在任务中被引用，无法删除!"
                                    okText="确定" cancelText="取消"
                                    //onConfirm={() => { this.remove(record) }}
                                >

                                    <span style={{color: "#ccc",paddingRight:8}}>删除</span>
                                </Popconfirm> :
                                <Popconfirm
                                    title="确定删除此数据源？"
                                    okText="确定" cancelText="取消"
                                    onConfirm={() => { this.remove(record) }}
                                >
                                    <a style={{paddingRight:8}}>删除</a>
                                </Popconfirm>
                        }
                    </span>
                )
            },
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

    render() {
        const { visible, title , status, source, syncModalVisible, dataSource } = this.state
        const {  sourceTypes } = this.props;
        const pagination = {
            total: dataSource.totalCount,
            defaultPageSize: 10,
        };
        const titles = (
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
                style={{ marginTop: 10 }}
                className="right"
                onClick={() => {
                    this.setState({
                        visible: true, 
                        source: {},
                        status: 'add',
                        title: '添加数据源',
                    })
                }}
            >新增数据源</Button>
        )

        return (
            <div>
                <div className="shadow rdos-data-source">
                    <Card 
                        title={titles} 
                        extra={extra} 
                        noHovering 
                        bordered={false}
                    >
                        <Table
                            className="m-table full-screen-table-47"
                            rowKey="id"
                            pagination={pagination}
                            onChange={this.handleTableChange}
                            loading={this.state.loading}
                            columns={this.initColumns()}
                            dataSource={dataSource.data}
                        />
                    </Card>
                </div>
                
                <DataSourceForm
                    title={title}
                    visible={visible}
                    status={status}
                    handOk={this.addOrUpdateDataSource}
                    testConnection={this.testConnection}
                    sourceData={source}
                    sourceTypes={sourceTypes}
                    handCancel={() => { this.setState({ visible: false }) }}
                />

                <DbSyncModal
                    visible={syncModalVisible}
                    source={source}
                    cancel={this.closeSyncModal}
                />
            </div>
        )
    }
}
export default connect((state) => {
    console.log('connect',state);
    
    return {
        project: state.project,
        sourceTypes: state.dataSource.sourceTypes,
    }
}, dispatch => {
    return {
        getSourceTypes: function() {
            dispatch(getSourceTypes())
        }
    }
})(DataSourceMana)
