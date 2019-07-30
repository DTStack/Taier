import * as React from 'react';
import { connect } from 'react-redux';
import { Input, Button, Card, Popconfirm, Table, message } from 'antd';
import moment from 'moment';

import DataSourceForm from './editModal';
import { dataSourceFilter } from '../../consts';
import { dataSourceActions } from '../../actions/dataSource';
import DSApi from '../../api/dataSource';
import '../../styles/views/dataSource.scss';

const Search = Input.Search
const mapStateToProps = (state: any) => {
    const { dataSource } = state;
    return { dataSource }
}
const mapDispatchToProps = (dispatch: any) => ({
    getDataSources(params: any) {
        dispatch(dataSourceActions.getDataSources(params));
    }
})

@(connect(mapStateToProps, mapDispatchToProps) as any)
class DataSource extends React.Component<any, any> {
    state: any = {
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
        }
    }

    componentDidMount () {
        this.props.getDataSources(this.state.params);
    }

    searchDataSources = (name: any) => {
        let params = {
            ...this.state.params,
            /* eslint-disable-next-line */
            name: name ? name : undefined,
            currentPage: 1
        };
        this.props.getDataSources(params);
        this.setState({ params });
    }

    editDataSource = (sourceFormData: any, formObj: any) => {
        const { title, status, source, params } = this.state;
        if (status === 'edit') {
            DSApi.updateDataSource({
                ...source, ...sourceFormData
            }).then((res: any) => {
                if (res.code === 1) {
                    message.success(`${title}成功！`);
                    this.props.getDataSources(params);
                }
            });
        } else {
            DSApi.addDataSource(sourceFormData).then((res: any) => {
                if (res.code === 1) {
                    message.success(`${title}成功！`);
                    this.props.getDataSources(params);
                }
            });
        }

        formObj.resetFields();
        this.setState({ visible: false, source: {} });
    }

    remove = (record: any) => {
        if (record.active === 1) {
            message.info('此数据源已在任务中被引用，无法删除!')
            return;
        }

        DSApi.deleteDataSource({ sourceId: record.id }).then((res: any) => {
            if (res.code === 1) {
                message.success('移除数据源成功！');
                this.props.getDataSources(this.state.params);
            }
        });
    }

    handleTableChange = (page: any, filters: any) => {
        let active = filters.active;
        let type = filters.type;
        let params = {
            ...this.state.params,
            currentPage: page.current,
            active: active ? active[0] : undefined,
            type: type ? type[0] : undefined
        };
        this.props.getDataSources(params);
        this.setState({ params });
    }
    initEdit = (source: any) => {
        this.setState({
            visible: true,
            title: '编辑数据源',
            status: 'edit',
            source
        });
    }
    initColumns = () => {
        const cols: any = [{
            title: '数据源名称',
            dataIndex: 'dataName',
            key: 'dataName',
            width: '18%',
            render: (text: any) => <div className="ellipsis-td" title={text}>{text}</div>
        }, {
            title: '类型',
            dataIndex: 'type',
            key: 'type',
            filters: dataSourceFilter,
            filterMultiple: false,
            width: '10%',
            render(text: any, record: any) {
                return record.sourceTypeValue;
            }
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
            render: (text: any) => moment(text).format('YYYY-MM-DD HH:mm:ss'),
            width: '15%'
        }, {
            title: '状态',
            dataIndex: 'active',
            key: 'active',
            filters: [{
                text: '未启用',
                value: 0
            }, {
                text: '使用中',
                value: 1
            }],
            filterMultiple: false,
            render: (text: any, record: any) => {
                return record.active === 1 ? '使用中' : '未启用'
            },
            width: '10%'
        }, {
            title: '操作',
            width: '10%',
            render: (text: any, record: any) => {
                return (
                    <span>
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
        return cols;
    }

    render () {
        const { visible, title, status, source, params } = this.state;
        const { sourceQuery, loading } = this.props.dataSource;

        const pagination = {
            current: params.currentPage,
            pageSize: params.pageSize,
            total: sourceQuery.totalCount
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
                        title: '添加数据源'
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
                    handCancel={() => { this.setState({ visible: false, source: {} }) }}
                />
            </div>
        )
    }
}

export default DataSource;
