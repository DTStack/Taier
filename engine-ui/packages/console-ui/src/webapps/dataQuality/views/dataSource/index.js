import React, { Component } from 'react';
import { connect } from 'react-redux';
import { 
    Input, Button, Popconfirm,
    Table, message, Card
 } from 'antd';
import moment from 'moment';

import DataSourceForm from './editModal';
import { formItemLayout, dataSourceFilter } from '../../consts';
import { dataSourceActions } from '../../actions/dataSource';
import DSApi from '../../api/dataSource';
import '../../styles/views/dataSource.scss';

const Search = Input.Search

const mapStateToProps = state => {
    const { dataSource } = state;
    return { dataSource }
}

const mapDispatchToProps = dispatch => ({
    getDataSources(params) {
        dispatch(dataSourceActions.getDataSources(params));
    },
})

@connect(mapStateToProps, mapDispatchToProps)
export default class DataSource extends Component {

    state = {
        visible: false,
        title: '新增数据源',
        status: 'add',
        source: {},
        params: {
            currentPage: 1,
            pageSize: 20,
            name: undefined,
            type: undefined,
            active: undefined
        },
    }

    componentDidMount() {
        this.props.getDataSources(this.state.params);
    }

    searchDataSources = (name) => {
        let params = {...this.state.params, name, currentPage: 1};
       
        this.setState({ params });
        this.props.getDataSources(params);
    }

    editDataSource = (sourceFormData, formObj) => {
        const { title, status, source, params } = this.state;
        
        if (status === 'edit') {
            DSApi.updateDataSource({
                ...source, ...sourceFormData
            }).then((res) => {
                if (res.code === 1) {
                    message.success(`${title}成功！`);
                    this.props.getDataSources(params);
                }
            });
        } else {
            DSApi.addDataSource(sourceFormData).then((res) => {
                if (res.code === 1) {
                    message.success(`${title}成功！`);
                    this.props.getDataSources(params);
                }
            });
        }

        formObj.resetFields();
        this.setState({ visible: false });
    }

    remove = (record) => {
        if (record.active === 1) {
            message.info('此数据源已在任务中被引用，无法删除!')
            return;
        }

        DSApi.deleteDataSource({ sourceId: record.id }).then((res) => {
            if (res.code === 1) {
                message.success('移除数据源成功！');
                this.props.getDataSources(this.state.params);
            }
        });
    }

    testConnection = (params) => { // 测试数据源连通性
        DSApi.testDSConnection(params).then((res) => {
            if (res.code === 1) {
                message.success('数据源连接正常！')
            }
        })
    }

    handleTableChange = (page, filters) => {
        let active = filters.active,
            type   = filters.type;

        let params = {...this.state.params, 
            currentPage: page.current, 
            active: active ? active[0] : undefined,
            type: type ? type[0] : undefined
        };
       
        this.setState({ params });
        this.props.getDataSources(params);
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
        return [{
            title: '数据源名称',
            dataIndex: 'dataName',
            key: 'dataName',
            width: '18%',
            render: (text => <div className="ellipsis-td" title={text}>{text}</div>)
        }, {
            title: '类型',
            dataIndex: 'sourceTypeValue',
            key: 'sourceTypeValue',
            filters: dataSourceFilter,
            filterMultiple: false,
            width: '10%'
        }, {
            title: '描述信息',
            dataIndex: 'dataDesc',
            key: 'dataDesc',
            width: '22%'
        }, {
            title: '最近修改人',
            dataIndex: 'modifyUserName',
            key: 'modifyUserName',
            width: '15%'
        }, {
            title: '最近修改时间',
            dataIndex: 'gmtModified',
            key: 'gmtModified',
            render: text => moment(text).format("YYYY-MM-DD HH:mm:ss"),
            width: '15%'
        }, {
            title: '状态',
            dataIndex: 'active',
            key: 'active',
            filters: [{
                text: '未启用',
                value: 0,
            }, {
                text: '使用中',
                value: 1,
            }],
            filterMultiple: false,
            render: (text, record) => {
                return record.active === 1 ? '使用中' : '未启用'
            },
            width: '10%'
        }, {
            title: '操作',
            width: '10%',
            render: (text, record) => {
                 // active  '0：未启用，1：使用中'。  只有为0时，可以修改
                return (
                    <span>
                        <a onClick={() => {this.initEdit(record)}}>
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

    render() {
        const { visible, title, status, source, params } = this.state;
        const { sourceQuery, loading } = this.props.dataSource;

        const pagination = {
            current: params.currentPage,
            pageSize: params.pageSize,
            total: sourceQuery.totalCount,
        };

        const cardTitle = (
            <Search
                placeholder="数据源名称"
                style={{ width: 200, margin: '10px 0' }}
                onSearch={this.searchDataSources}
            />
        )

        const cardExtra = (
            <Button
                type="primary"
                style={{ margin: '10px 0' }}
                className="right"
                onClick={() => {
                    this.setState({
                        visible: true, 
                        source: {},
                        status: 'add',
                        title: '添加数据源',
                    })
                }}
            >
                新增数据源
            </Button>
        )

        return (
            <div className="source-page">
                <h1 className="box-title">
                    数据源管理
                </h1>

                <div className="box-2 m-card shadow">
                    <Card 
                        noHovering 
                        bordered={false}
                        title={cardTitle} 
                        extra={cardExtra} 
                    >
                        <Table
                            rowKey="id"
                            loading={loading}
                            className="m-table fixed-table"
                            pagination={pagination}
                            columns={this.initColumns()}
                            dataSource={sourceQuery.data}
                            onChange={this.handleTableChange}
                        />
                    </Card>
                </div>

                <DataSourceForm
                    title={title}
                    status={status}
                    visible={visible}
                    sourceData={source}
                    editDataSource={this.editDataSource}
                    testConnection={this.testConnection}
                    handCancel={() => { this.setState({ visible: false }) }}
                />
            </div>
        )
    }
}
