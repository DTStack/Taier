import React, { PureComponent } from 'react'
import { connect } from 'react-redux';
import { Card, Table, Button, Input, Modal } from 'antd';
import '../../styles/views/source/index.scss';

import { dataSourceFilter } from '../../comm/const.js'
import Api from '../../api'

import Edit from './edit';
import DataImport from './dataImport'
import UploaderProgressBar from '../../components/uploader-progress';

const confirm = Modal.confirm;
const Search = Input.Search;
@connect(state => {
    return {
        uploader: state.dataManage.uploader
    }
})
class Source extends PureComponent {
    state = {
        loading: false,
        data: [],
        pagination: {
            current: 1,
            total: 0,
            pageSize: 15
        },
        params: {
            search: '',
            filter: ''
        },
        editRecord: {},
        visible: false
    }
    componentDidMount () {
        this.getTableData();
    }
    handleTableChange = (pagination, filters, sorter) => {
        const params = Object.assign({}, this.state.params);
        params.filter = filters.type.length ? filters.type[0] : '';
        this.setState({
            params,
            pagination: {
                ...this.state.pagination,
                current: pagination.current
            }
        }, this.getTableData);
    }
    handleEdit = (record) => {
        this.setState({
            visible: true,
            editRecord: record
        });
    }
    handleDelete = (record) => {
        confirm({
            title: '请确认是否删除？',
            onOk: () => {
                // TODO
                console.log('record', record)
            }
        });
    }
    handleCancel = () => {
        this.setState({
            visible: false,
            editRecord: {}
        });
    }
    handleUpload = () => {
        const upload = document.getElementById('JS_importFile')
        if (upload) {
            upload.click();
        }
    }
    getTableData = async () => {
        this.setState({
            loading: true
        })
        const { pagination, params } = this.state;
        // TODO
        let res = await Api.comm.listDataSource({
            currentPage: pagination.current,
            pageSize: pagination.pageSize,
            name: params.search || undefined,
            type: params.filter || undefined
        });
        if (res && res.code == 1) {
            this.setState({
                data: res.data.data,
                pagination: {
                    ...pagination,
                    total: res.data.totalCount
                }
            });
        }
        this.setState({
            loading: false
        })
    }
    initCol = () => {
        // TODO
        return [{
            title: '数据源名称',
            dataIndex: 'name',
            key: 'name',
            width: '150px'
        }, {
            title: '类型',
            dataIndex: 'type',
            key: 'type',
            width: '100px',
            filters: dataSourceFilter,
            filterMultiple: false
        }, {
            title: '描述信息',
            dataIndex: 'dataSourceDesc',
            key: 'dataSourceDesc',
            width: '200px'
        }, {
            title: 'HDFS地址',
            dataIndex: 'address',
            key: 'address'
        }, {
            title: 'HDFS具体路径',
            dataIndex: 'location',
            key: 'location'
        }, {
            title: '操作',
            dataIndex: 'o',
            key: 'o',
            render: (text, record) => {
                const type = record.type;
                return (
                    <span>
                        {type == 'hive' && (
                            <a href="javascript:void(0)" onClick={() => this.handleEdit(record)}>编辑</a>
                        )}
                    </span>
                )
            }
        }]
    }
    handleSearch = (value) => {
        this.setState({
            params: {
                ...this.state.params,
                search: value
            },
            pagination: {
                ...this.state.pagination,
                current: 1
            }
        }, this.getTableData)
    }
    render () {
        const { loading, data, pagination, visible, editRecord } = this.state;
        const { uploader } = this.props;
        return (
            <div className="inner-container source">
                <div className="source-title">数据源</div>
                <Card
                    noHovering
                    bordered={false}
                    title={
                        <Search
                            onSearch={this.handleSearch}
                            placeholder='按数据源名称搜索'
                            style={{ width: 267, height: 30 }} />
                    }
                    extra={
                        <>
                            <UploaderProgressBar key={uploader.status} uploader={uploader} />
                            <Button type="primary" className="upload-button" onClick={() => this.handleUpload()}>上传数据</Button>
                        </>
                    }
                >
                    <Table
                        rowKey="id"
                        className='border-table dt-ant-table'
                        loading={loading}
                        columns={this.initCol()}
                        onChange={this.handleTableChange}
                        dataSource={data}
                        pagination={pagination}
                    />
                </Card>
                <DataImport
                    onOk={this.getTableData}
                />
                <Edit
                    record={editRecord}
                    onOk={this.getTableData}
                    onCancel={this.handleCancel}
                    visible={visible} />
            </div>
        )
    }
}
export default Source;
