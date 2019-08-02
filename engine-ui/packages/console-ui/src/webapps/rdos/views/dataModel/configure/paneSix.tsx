import * as React from 'react';
import { connect } from 'react-redux';
import { isEmpty } from 'lodash';

import {
    Table, Select, Form, Card,
    Input, Button, Popconfirm
} from 'antd';

import utils from 'utils';

import BasePane from './basePane';
import Api from '../../../api/dataModel';
import AtomIndexDefineModal from './paneSixModal';
import { IndexType } from '../../../components/display';

const Option = Select.Option;
const FormItem = Form.Item;

class AtomIndexDefine extends BasePane {
    componentDidMount () {
        this.setState({
            params: Object.assign(this.state.params, {
                type: 1 // 原子指标
            })
        }, this.loadData)
    }
    searchInput = React.createRef();
    // eslint-disable-next-line
	UNSAFE_componentWillReceiveProps(nextProps: any) {
        const project = nextProps.project
        const oldProj = this.props.project
        if (oldProj && project && oldProj.id !== project.id) {
            this.loadData();
        }
    }

    loadData = () => {
        const { params } = this.state;
        this.setState({
            loading: true
        });
        Api.getModelIndexs(params).then((res: any) => {
            if (res.code === 1) {
                this.setState({
                    table: res.data
                })
            }
            this.setState({
                loading: false
            })
        });
    }

    changeParams = (field: any, value: any) => {
        let params = Object.assign(this.state.params);
        if (field) {
            params[field] = value;
        }
        this.setState({
            params
        }, this.loadData)
    }

    changeSearchName = (e: any) => {
        this.setState({
            params: Object.assign(this.state.params, {
                columnNameZh: e.target.value,
                currentPage: 1
            })
        })
    }

    update = (formData: any) => {
        const { modalData } = this.state;
        const isEdit = modalData && !isEmpty(modalData);
        const succCallback = (res: any) => {
            if (res.code === 1) {
                this.loadData();
                this.setState({
                    modalData: null,
                    modalVisible: false
                });
            }
        }
        if (isEdit) {
            Api.updateModelIndex(formData).then(succCallback)
        } else {
            Api.addModelIndex(formData).then(succCallback)
        }
    }

    delete = (data: any) => {
        Api.deleteModelIndex({
            ids: [data.id]
        }).then((res: any) => {
            if (res.code === 1) {
                this.loadData();
            }
        })
    }

    initColumns = () => {
        return [{
            title: '指标类型',
            dataIndex: 'columnType',
            key: 'columnType',
            render: (type: any) => <IndexType value={type} />
        }, {
            title: '原子指标名称',
            dataIndex: 'columnNameZh',
            key: 'columnNameZh'
        }, {
            title: '原子指标命名',
            dataIndex: 'columnName',
            key: 'columnName'
        }, {
            title: '数据类型',
            dataIndex: 'dataType',
            key: 'dataType'
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
                    title={
                        <Form
                            className="m-form-inline"
                            layout="inline"
                        >
                            <FormItem label="">
                                <Input.Search
                                    placeholder="按指标名称搜索"
                                    style={{ width: 200 }}
                                    size="default"
                                    onChange={this.changeSearchName}
                                    onSearch={this.loadData}
                                    ref={(el: any) => this.searchInput = el}
                                />
                            </FormItem>
                            <FormItem label="指标类型">
                                <Select
                                    allowClear
                                    style={{ width: 126 }}
                                    placeholder="选择指标类型"
                                    onChange={(value: any) => this.changeParams('columnType', value)}
                                >
                                    <Option value="1">原子指标</Option>
                                    <Option value="2">修饰词</Option>
                                </Select>
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
                        onChange={(pagination: any) => this.changeParams('currentPage', pagination.current)}
                        dataSource={table.data || []}
                    />
                </Card>
                <AtomIndexDefineModal
                    data={modalData}
                    handOk={this.update}
                    handCancel={() => this.setState({ modalVisible: false })}
                    visible={modalVisible}
                />
            </div>
        )
    }
}

export default connect((state: any) => {
    return {
        project: state.project
    }
})(AtomIndexDefine);

export { AtomIndexDefine };
