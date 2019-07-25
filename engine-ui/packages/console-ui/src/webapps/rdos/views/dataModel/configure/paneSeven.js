import React from 'react';
import { connect } from 'react-redux';

import {
    Table, Form, Card,
    Input, Button, Popconfirm,
    Tooltip
} from 'antd';

import utils from 'utils';

import { AtomIndexDefine } from './paneSix';
import DeriveIndexModal from './paneSevenModal';

const FormItem = Form.Item;

@connect((state) => {
    return {
        project: state.project
    }
})
class DeriveIndexDefine extends AtomIndexDefine {
    componentDidMount () {
        this.setState({
            params: Object.assign(this.state.params, {
                type: 2 // 原子指标
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

    characterProcess = (text = '', maxWidth = '300px') => {
        const style = {
            overflow: 'hidden',
            maxWidth,
            textOverflow: 'ellipsis',
            whiteSpace: 'nowrap'
        }
        const content = (
            <Tooltip title={text} >
                <div style={style}>{text}</div>
            </Tooltip>
        )

        return content
    }

    initColumns = () => {
        return [{
            title: '衍生指标名称',
            dataIndex: 'columnNameZh',
            key: 'columnNameZh'
        }, {
            title: '指标命名',
            dataIndex: 'columnName',
            key: 'columnName'
        }, {
            title: '数据类型',
            dataIndex: 'dataType',
            key: 'dataType'
        }, {
            title: '指标口径',
            width: '400px',
            dataIndex: 'modelDesc',
            key: 'modelDesc',
            render: text => this.characterProcess(text)
        }, {
            title: '最后修改人',
            dataIndex: 'userName',
            key: 'userName'
        }, {
            title: '最后修改时间',
            dataIndex: 'gmtModified',
            key: 'gmtModified',
            render: text => utils.formatDateTime(text)
        }, {
            title: '操作',
            key: 'operation',
            render: (record) => {
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
        const { loading, table = {}, modalVisible, modalData } = this.state;

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
                    title={
                        <Form
                            className="m-form-inline"
                            layout="inline"
                        >
                            <FormItem label="">
                                <Input.Search
                                    placeholder="按指标名称搜索"
                                    style={{ width: '200px' }}
                                    size="default"
                                    onChange={this.changeSearchName}
                                    onSearch={this.loadData}
                                    ref={el => this.searchInput = el}
                                />
                            </FormItem>
                        </Form>
                    }

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
                        pagination={pagination}
                        loading={loading}
                        columns={this.initColumns()}
                        onChange={(pagination) => this.changeParams('currentPage', pagination.current)}
                        dataSource={table.data || []}
                    />
                </Card>
                <DeriveIndexModal
                    data={modalData}
                    handOk={this.update}
                    handCancel={() => this.setState({ modalVisible: false })}
                    visible={modalVisible}
                />
            </div>
        )
    }
}
export default DeriveIndexDefine;
