import React from 'react';
import { connect } from 'react-redux';

import {
    Table, Card,
    Button, Popconfirm
} from 'antd';

import utils from 'utils';

import ModelLevelModal from './paneOneModal';
import BasePane from './basePane';

@connect((state) => {
    return {
        project: state.project
    }
})
class ModelLevel extends BasePane {
    constructor (props) {
        super(props);
    }

    componentDidMount () {
        this.setState({
            params: Object.assign(this.state.params, {
                type: 1 // 模型层级
            })
        }, this.loadData)
    }

    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps (nextProps) {
        const project = nextProps.project
        const oldProj = this.props.project
        if (oldProj && project && oldProj.id !== project.id) {
            this.loadData();
        }
    }

    initColumns = () => {
        return [{
            title: '层级编号',
            dataIndex: 'id',
            key: 'id'
        }, {
            title: '层级名称',
            dataIndex: 'name',
            key: 'name'
        }, {
            title: '层级说明',
            dataIndex: 'modelDesc',
            key: 'modelDesc'
        }, {
            title: '层级前缀',
            dataIndex: 'prefix',
            key: 'prefix'
        }, {
            title: '生命周期',
            dataIndex: 'lifeDay',
            key: 'lifeDay'
        }, {
            title: '是否记入层级依赖',
            dataIndex: 'depend',
            key: 'depend',
            render: depend => depend === 1 ? '是' : '否'
        }, {
            title: '最近修改人',
            dataIndex: 'userName',
            key: 'userName'
        }, {
            title: '最近修改时间',
            dataIndex: 'gmtModified',
            key: 'gmtModified',
            render: text => utils.formatDateTime(text)
        }, {
            title: '操作',
            key: 'operation',
            render: (record) => {
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

        const pagination = {
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
                            onClick={this.initAdd}
                        >
                            新建
                        </Button>
                    }
                >
                    <Table
                        rowKey="id"
                        className="m-table"
                        style={{ marginTop: '1px' }}
                        pagination={pagination}
                        loading={loading}
                        columns={this.initColumns()}
                        onChange={this.handleTableChange}
                        dataSource={table.data || []}
                    />
                </Card>
                <ModelLevelModal
                    key={`modelLevel-${modalData.id}`}
                    data={modalData}
                    handOk={this.update}
                    handCancel={() => this.setState({ modalVisible: false })}
                    visible={modalVisible}
                />
            </div>
        )
    }
}

export default ModelLevel;
