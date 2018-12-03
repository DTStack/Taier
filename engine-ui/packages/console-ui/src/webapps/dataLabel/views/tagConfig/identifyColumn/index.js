import React, { Component } from 'react';
import { Table, Card, Modal, Form, Button, Input, Popconfirm, message } from 'antd';

import GoBack from 'main/components/go-back';
import { formItemLayout } from '../../../consts';
import TCApi from '../../../api/tagConfig';

const FormItem = Form.Item;
const TextArea = Input.TextArea;

class IdentifyColumn extends Component {
    state = {
        visible: false,
        loading: false,
        queryParams: {
            currentPage: 1,
            pageSize: 20
        },
        columnData: {},
        currentData: {}
    }

    componentDidMount () {
        this.getColumnData(this.state.queryParams);
    }

    // 获取识别列数据
    getColumnData = (params) => {
        this.setState({ loading: true });

        TCApi.queryIdentifyColumn(params).then((res) => {
            if (res.code === 1) {
                this.setState({
                    columnData: res.data,
                    loading: false
                });
            }
        });
    }

    // table设置
    initColumns = () => {
        return [{
            title: '识别列类型',
            dataIndex: 'name',
            key: 'name',
            width: '45%'
        }, {
            title: '描述',
            dataIndex: 'identityDesc',
            key: 'identityDesc',
            width: '45%'
        }, {
            title: '操作',
            width: '10%',
            render: (text, record) => {
                return (
                    <div>
                        <a onClick={this.editColumn.bind(this, record)}>
                            编辑
                        </a>
                        <span className="ant-divider" />
                        <Popconfirm
                            title="确定删除此识别列？"
                            okText="确定" cancelText="取消"
                            onConfirm={this.deleteColumn.bind(this, record.id)}>
                            <a>删除</a>
                        </Popconfirm>
                    </div>
                )
            }
        }]
    }

    // 新增识别列
    addColumn = () => {
        this.openModal();
        this.setState({ currentData: {} });
    }

    // 保存识别列
    saveColumn = () => {
        const { form } = this.props;
        const { queryParams, currentData } = this.state;

        form.validateFields((err, values) => {
            console.log(err, values)
            let api, params, msg;

            if (!err) {
                if (currentData.id) {
                    api = TCApi.updateIdentifyColumn;
                    params = { ...values, id: currentData.id };
                    msg = '更新成功';
                } else {
                    api = TCApi.addIdentifyColumn;
                    params = values;
                    msg = '新增成功';
                }

                api(params).then((res) => {
                    if (res.code === 1) {
                        message.success(msg);
                        this.getColumnData(queryParams);
                        this.closeModal();
                        this.setState({ currentData: {} });
                        form.resetFields();
                    }
                });
            }
        });
    }

    // 编辑识别列
    editColumn = (record) => {
        this.openModal();
        this.setState({ currentData: record });
    }

    // 删除
    deleteColumn = (id) => {
        const { queryParams } = this.state;

        if (id) {
            TCApi.deleteIdentifyColumn({ identifyId: id }).then((res) => {
                if (res.code === 1) {
                    message.success('删除成功！');
                    this.getColumnData(queryParams);
                }
            });
        }
    }

    // 取消编辑
    cancel = () => {
        this.closeModal();
        this.props.form.resetFields();
    }

    openModal = () => {
        this.setState({ visible: true });
    }

    closeModal = () => {
        this.setState({ visible: false });
    }

    // 表格换页/排序
    onTableChange = (page, filter, sorter) => {
        let queryParams = {
            ...this.state.queryParams,
            currentPage: page.current
        };

        this.getColumnData(queryParams);
        this.setState({ queryParams });
    }

    render () {
        const { getFieldDecorator } = this.props.form;
        const { visible, queryParams, loading, columnData, currentData } = this.state;

        const cardTitle = (
            <div>
                <GoBack /> 识别列类型配置
            </div>
        )

        const cardExtra = (
            <Button
                type="primary"
                style={{ margin: 10 }}
                onClick={this.addColumn}>
                新建识别列
            </Button>
        )

        const pagination = {
            current: queryParams.currentPage,
            pageSize: queryParams.pageSize,
            total: columnData.totalCount
        };

        return (
            <div className="box-1 m-card shadow">
                <Card
                    title={cardTitle}
                    extra={cardExtra}
                    noHovering
                    bordered={false}
                >
                    <Table
                        rowKey="id"
                        className="m-table"
                        columns={this.initColumns()}
                        loading={loading}
                        pagination={pagination}
                        dataSource={columnData.data}
                        onChange={this.onTableChange}
                    />

                    <Modal
                        title={currentData.id ? '编辑识别列' : '新建识别列'}
                        width={'50%'}
                        visible={visible}
                        maskClosable={false}
                        okText="确定"
                        cancelText="取消"
                        onOk={this.saveColumn}
                        onCancel={this.cancel}
                    >
                        <Form>
                            <FormItem {...formItemLayout} label="类型名称">
                                {
                                    getFieldDecorator('name', {
                                        rules: [{
                                            required: true,
                                            message: '类型名称不可为空'
                                        }, {
                                            max: 20,
                                            message: '最大字数不能超过20'
                                        }, {
                                            pattern: new RegExp(/^([\w|\u4e00-\u9fa5]*)$/),
                                            message: '名称只能以字母，数字，下划线组成'
                                        }],
                                        initialValue: currentData.name
                                    })(
                                        <Input placeholder="请输入类型名称" />
                                    )
                                }
                            </FormItem>
                            <FormItem {...formItemLayout} label="描述">
                                {
                                    getFieldDecorator('identityDesc', {
                                        rules: [{
                                            max: 200,
                                            message: '描述字符不能超过200'
                                        }],
                                        initialValue: currentData.identityDesc
                                    })(
                                        <TextArea
                                            placeholder="类型描述"
                                            autosize={{ minRows: 3, maxRows: 6 }}
                                        />
                                    )
                                }
                            </FormItem>
                        </Form>
                    </Modal>
                </Card>
            </div>
        )
    }
}
export default (Form.create()(IdentifyColumn));
