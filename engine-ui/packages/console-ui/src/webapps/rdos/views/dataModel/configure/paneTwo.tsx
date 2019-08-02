import * as React from 'react';
import { connect } from 'react-redux';

import {
    Table, Card,
    Button, Popconfirm
} from 'antd';

import utils from 'utils';

import SubjectDomainModal from './paneTwoModal';
import BasePane from './basePane';

@(connect((state: any) => {
    return {
        project: state.project
    }
}) as any)
class SubjectDomain extends BasePane {
    constructor(props: any) {
        super(props);
    }

    componentDidMount () {
        this.setState({
            params: Object.assign(this.state.params, {
                type: 2 // 主题域
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
            title: '主题域名称',
            dataIndex: 'name',
            key: 'name'
        }, {
            title: '主题域说明',
            dataIndex: 'modelDesc',
            key: 'modelDesc'
        }, {
            title: '主题域前缀',
            dataIndex: 'prefix',
            key: 'prefix'
        }, {
            title: '最近修改人',
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
                        <a onClick={() => { this.initEdit(record) }}>修改</a>
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
                            onClick={this.initAdd}
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
                <SubjectDomainModal
                    data={modalData}
                    handOk={this.update}
                    handCancel={() => this.setState({ modalVisible: false })}
                    visible={modalVisible}
                />
            </div>
        )
    }
}
export default SubjectDomain;
