import * as React from 'react';
import { connect } from 'react-redux';
import { cloneDeep } from 'lodash';
import {
    Input, Button,
    Table, message, Card, Icon, Tooltip
} from 'antd';

import DeleteModal from '../../components/deleteModal';
import { Circle } from 'widgets/circle';
import Api from '../../api/dataSource';
import DataSourceForm from './form';
import DataSourceTaskListModal from './dataSourceTaskListModal'
import { ExtTableCell } from './extDataSourceMsg'
import '../../styles/pages/dataSource.scss';

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
        type: undefined,
        deleteVisible: false,
        deleteItem: {}
    }

    componentDidMount () {
        this.loadDataSources()
        this.getSourceTypes();
    }

    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps (nextProps: any) {
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
        const { pageSize, currentPage, name } = this.state;
        this.setState({ loading: true })
        const reqParams: any = {
            size: pageSize,
            current: currentPage,
            search: name
        }
        Api.getTagDataSourceList(reqParams).then((res: any) => {
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
            Api.tagUpdateeDataSource(reqSource).then((res: any) => {
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
            Api.tagCreateDataSource({
                ...reqSource,
                linkState: 1 // 连通性测试过后才可新增 故链接状态始终为1
            }).then((res: any) => {
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
        this.setState({
            visible: true,
            source: {},
            status: 'add',
            title: '添加数据源'
        })
    }

    testConnection = (formSource: any, callBack) => { // 测试数据源连通性
        Api.tagTestDSConnect({
            ...formSource.dataJson
        }).then((res: any) => {
            if (res.code === 1 && res.data) {
                message.success('数据源连接正常！')
                callBack();
            } else if (res.code === 1 && !res.data) {
                message.error('数据源连接异常')
            }
        })
    }

    handleTableChange = (pagination: any, filters: any) => {
        this.setState({
            currentPage: pagination.current,
            type: filters.type && filters.type[0]
        }, this.loadDataSources);
    }

    initEdit = (source: any) => {
        this.setState({
            visible: true,
            title: '编辑数据源',
            status: 'edit',
            source: cloneDeep(source)
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
        // const { sourceTypes } = this.state;

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
            }
            // filters: sourceTypes.map((source: any) => {
            //     return { ...source, text: source.name }
            // }),
            // filterMultiple: false
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
        {
            title: '应用状态',
            dataIndex: 'active',
            key: 'active',
            width: '120px',
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
            width: '120px',
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
                        <a onClick={() => { this.initEdit(record) }}>
                            编辑
                        </a>
                        <span className="ant-divider" />
                        {
                            record.active === 1
                                ? <span style={{ color: '#ccc' }}>删除</span>
                                : (
                                    <a onClick={this.openDeleteModal.bind(this, record)}>删除</a>
                                )
                        }
                    </span>
                )
            }
        }]
    }

    openDeleteModal = (deleteItem) => {
        this.setState({
            deleteVisible: true,
            deleteItem
        })
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

    handleDeleteModel = (type: string) => {
        const { deleteItem } = this.state;
        if (type == 'ok') {
            Api.tagDeleteDataSource({
                id: deleteItem.id
            }).then((res: any) => {
                if (res.code === 1) {
                    message.success('移除数据源成功！')
                    this.setState({
                        currentPage: 1,
                        deleteVisible: false
                    }, () => {
                        this.loadDataSources();
                    })
                }
            })
        } else {
            this.setState({
                deleteVisible: false
            })
        }
    }

    render () {
        const { source, dataSource, sourceTypes, currentPage, pageSize, deleteVisible } = this.state
        const pagination: any = {
            total: dataSource.totalCount,
            pageSize: pageSize,
            current: currentPage,
            showTotal: () => (
                <div>
                    总共 <a>{dataSource.total}</a> 条数据,每页显示{pageSize}条
                </div>
            )
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
                            className="dt-ant-table--border"
                            pagination={pagination}
                            onChange={this.handleTableChange}
                            loading={this.state.loading}
                            columns={this.initColumns()}
                            dataSource={dataSource.contentList}
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
                <DeleteModal
                    title={'删除数据源'}
                    content={'确定删除此数据源？'}
                    okText={'确定'}
                    visible={deleteVisible}
                    onCancel={this.handleDeleteModel.bind(this, 'cancel')}
                    onOk={this.handleDeleteModel.bind(this, 'ok')}
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
        project: state.project
    }
}
)(WrapDataSourceMana)
