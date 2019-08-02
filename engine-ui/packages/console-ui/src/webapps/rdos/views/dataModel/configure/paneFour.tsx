import * as React from 'react';
import { connect } from 'react-redux';

import {
    Table, Card,
    Button, Popconfirm
} from 'antd';

import utils from 'utils';

import BasePane from './basePane';
import IncrementDefineModal from './paneFourModal';

@(connect((state: any) => {
    return {
        project: state.project
    }
}) as any)
class IncrementDefine extends BasePane {
    componentDidMount () {
        this.setState({
            params: Object.assign(this.state.params, {
                type: 4 // 增量定义
            })
        }, this.loadData)
    }

    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps(nextProps: any) {
        const project = nextProps.project
        const oldProj = this.props.project
        if (oldProj && project && oldProj.id !== project.id) {
            this.loadData();
        }
    }

    initColumns = () => {
        return [{
            title: '增量定义',
            dataIndex: 'name',
            key: 'name'
        }, {
            title: '增量方式标识',
            dataIndex: 'prefix',
            key: 'prefix'
        }, {
            title: '增量描述',
            dataIndex: 'modelDesc',
            key: 'modelDesc'
        }, {
            title: '最后修改人',
            dataIndex: 'userName',
            key: 'userName'
        }, {
            title: '最后修改时间',
            dataIndex: 'gmtModified',
            key: 'gmtModified',
            render: (text: any) => utils.formatDateTime(text)
        }, {
            title: '操作',
            key: 'operation',
            render: (record: any) => {
                return (
                    <div key={record.id}>
                        <a onClick={() => { this.initEdit(record) }}>编辑</a>
                        <span className="ant-divider" />
                        <Popconfirm
                            title="确定删除此条记录吗?"
                            onConfirm={() => { this.delete(record) }}
                            okText="是" cancelText="否"
                        >
                            <a>删除</a>
                        </Popconfirm>
                    </div>
                )
            }
        }]
    }

    render () {
        const { loading, table, modalVisible, modalData } = this.state

        const pagination: any = {
            total: table.totalCount,
            defaultPageSize: 10
        };

        return (
            <div className="m-card">
                <Card
                    noHovering
                    bordered={false}
                    loading={false}
                    title=""
                    extra={
                        <Button
                            style={{ marginTop: '10px' }}
                            type="primary"
                            onClick={() => { this.setState({ modalVisible: true }) }}
                        >
                            新建
                        </Button>
                    }
                >
                    <Table
                        rowKey="id"
                        className="dt-ant-table dt-ant-table--border"
                        style={{ marginTop: '1px' }}
                        pagination={pagination}
                        loading={loading}
                        columns={this.initColumns()}
                        onChange={this.handleTableChange}
                        dataSource={table.data || []}
                    />
                </Card>
                <IncrementDefineModal
                    data={modalData}
                    handOk={this.update}
                    handCancel={() => this.setState({ modalVisible: false, modalData: '' })}
                    visible={modalVisible}
                />
            </div>
        )
    }
}
export default IncrementDefine;
