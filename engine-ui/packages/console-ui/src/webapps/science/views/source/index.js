import React, { PureComponent } from 'react'
import { connect } from 'react-redux';
import { Card, Table, Button, Input, Modal } from 'antd';
import '../../styles/views/source/index.scss';
import { dataSourceFilter } from '../../comm/const.js'
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
            total: 0
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
        const params = Object.assign(this.state.params);
        params.filter = filters.type.length ? filters.type.join(',') : '';
        this.setState({
            params
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
    getTableData = () => {
        // TODO
        this.setState({
            data: [{
                id: 1,
                name: 'test',
                dataTable: true
            }, {
                id: 2,
                dataTable: false
            }]
        });
    }
    initCol = () => {
        // TODO
        return [{
            title: '数据源名称',
            dataIndex: 'name',
            key: 'name'
        }, {
            title: '类型',
            dataIndex: 'type',
            key: 'type',
            filters: dataSourceFilter
        }, {
            title: '描述信息',
            dataIndex: 'description',
            key: 'description'
        }, {
            title: 'HDFS文件区',
            dataIndex: 'hdfsFile',
            key: 'hdfsFile'
        }, {
            title: 'HDFS具体路径',
            dataIndex: 'hdfsLocation',
            key: 'hdfsLocation'
        }, {
            title: '操作',
            dataIndex: 'o',
            key: 'o',
            render: (text, record) => {
                const edit = record.dataTable;
                return (
                    <span>
                        {edit && (
                            <>
                                <a href="javascript:void(0)" onClick={() => this.handleEdit(record)}>编辑</a>
                                <span className="ant-divider"></span>
                            </>
                        )}
                        <a href="javascript:void(0)" onClick={() => this.handleDelete(record)}>删除</a>
                    </span>
                )
            }
        }]
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
                            placeholder='按项目名称、项目显示名搜索'
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
                        className='m-table'
                        loading={loading}
                        columns={this.initCol()}
                        onChange={this.handleTableChange}
                        dataSource={data}
                        pagination={pagination}
                    />
                </Card>
                <DataImport />
                <Edit
                    record={editRecord}
                    onCancel={this.handleCancel}
                    visible={visible} />
            </div>
        )
    }
}
export default Source;
