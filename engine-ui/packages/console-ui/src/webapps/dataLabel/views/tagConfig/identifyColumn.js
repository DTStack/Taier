import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router';
import { Table, Card, Modal, Form, Button, Input, Select, Popconfirm } from 'antd';

import GoBack from 'main/components/go-back';
import { tagConfigActions } from '../../actions/tagConfig';
import { formItemLayout } from '../../consts';

const Option = Select.Option;
const FormItem = Form.Item;
const TextArea = Input.TextArea;

const mapStateToProps = state => {
    const { tagConfig } = state;
    return { tagConfig }
}

const mapDispatchToProps = dispatch => ({
    getRuleTagList(params) {
        dispatch(tagConfigActions.getRuleTagList(params));
    },
})

@connect(mapStateToProps, mapDispatchToProps)
export default class IdentifyColumn extends Component {

    state = {
        visible: false,
        selectedIds: [],
        queryParams: {
            currentPage: 1,
            pageSize: 20
        }
    }

    componentDidMount() {
        this.props.getRuleTagList(this.state.queryParams);
    }

    openModal = () => {
        this.setState({ visible: true });
    }

    closeModal = () => {
        this.setState({ visible: false });
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
            dataIndex: 'des',
            key: 'des',
            width: '45%'
        }, {
            title: '操作',
            width: '10%',
            render: (text, record) => {
                return (
                    <div>
                        <a onClick={() => {this.editTag(record)}}>
                            编辑
                        </a>
                        <span className="ant-divider" />
                        <Popconfirm
                            title="确定删除此识别列？"
                            okText="确定" cancelText="取消"
                            onConfirm={() => {this.removeTag(record)}}
                        >
                            <a>删除</a>
                        </Popconfirm>
                    </div>
                )
            }
        }]
    }

    saveColumn = () => {
        console.log('save')
    }

    onUserSourceChange = (value) => {
        console.log(value)
    }

    render() {
        const { visible, selectedIds } = this.state;
        const { getFieldDecorator } = this.props.form;

        const cardExtra = (
            <Button type="primary" style={{ margin: 10 }} onClick={this.openModal}>新建识别列</Button>
        )

        const rowSelection = {
            selectedRowKeys: selectedIds,
            onChange: (selectedIds) => {
                this.setState({ selectedIds });
            },
        };

        return (
            <div>
                <h1 className="box-title">
                    <GoBack /> 识别列类型配置
                </h1>

                <div className="box-2 m-card shadow">
                    <Card 
                        title={false}
                        extra={cardExtra}
                        noHovering 
                        bordered={false}
                    >
                        <Table 
                            rowKey="id"
                            className="m-table"
                            columns={this.initColumns()} 
                            // loading={loading}
                            rowSelection={false}
                            pagination={false}
                            dataSource={[]}
                            onChange={this.onTableChange}
                        />

                        <Modal
                            title="新建识别列"
                            wrapClassName="identifyColumnModal"
                            width={'50%'}
                            visible={visible}
                            maskClosable={false}
                            okText="确定"
                            cancelText="取消"
                            onOk={this.saveColumn}
                            onCancel={this.closeModal}
                        >
                            <Form>
                                <FormItem {...formItemLayout} label="类型名称">
                                    {
                                        getFieldDecorator('name', {
                                            rules: [{ 
                                                required: true, 
                                                message: '类型名称不可为空' 
                                            }], 
                                            // initialValue: name
                                        })(
                                            <Input />
                                        )
                                    }
                                </FormItem>
                                <FormItem {...formItemLayout} label="描述">
                                    {
                                        getFieldDecorator('des', {
                                            rules: [], 
                                            // initialValue: des
                                        })(
                                            <TextArea 
                                                placeholder="类型描述" 
                                                // className="trigger-remarks" 
                                                autosize={{ minRows: 2, maxRows: 6 }} 
                                                // onChange={this.onRemarkChange} 
                                            />
                                        )
                                    }
                                </FormItem>
                            </Form>
                        </Modal>
                    </Card>
                </div>
            </div>
        )
    }
}
IdentifyColumn = Form.create()(IdentifyColumn);