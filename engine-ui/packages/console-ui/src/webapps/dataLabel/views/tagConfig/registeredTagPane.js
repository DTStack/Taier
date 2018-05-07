import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router';
import { Table, Card, Modal, Form, Button, Input, Select, Popconfirm } from 'antd';

import { tagConfigActions } from '../../actions/tagConfig';
import { formItemLayout } from '../../consts';

const Search = Input.Search;
const Option = Select.Option;
const FormItem = Form.Item;
const TextArea = Input.TextArea;

const mapStateToProps = state => {
    const { tagConfig } = state;
    return { tagConfig }
}

const mapDispatchToProps = dispatch => ({
    getRegisteredTagList(params) {
        dispatch(tagConfigActions.getRegisteredTagList(params));
    },
})

@connect(mapStateToProps, mapDispatchToProps)
export default class RegisteredTagPane extends Component {

    state = {
        visible: false,
        selectedIds: [],
        queryParams: {
            currentPage: 1,
            pageSize: 20
        }
    }

    componentDidMount() {
        this.props.getRegisteredTagList(this.state.queryParams);
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
            title: '标签名称',
            dataIndex: 'name',
            key: 'name',
            // width: '10%'
        }, {
            title: '标签描述',
            dataIndex: 'des',
            key: 'des',
            // width: '12%'
        }, {
            title: '标签类目',
            dataIndex: 'type',
            key: 'type',
            // width: '8%',
        }, {
            title: '值域',
            dataIndex: 'valueRange',
            key: 'valueRange',
            // width: '12%'
        }, {
            title: '目标数据库',
            dataIndex: 'db',
            key: 'db',
            // width: '10%'
        }, {
            title: '识别列ID',
            dataIndex: 'configureID',
            key: 'configureID',
            // width: '8%'
        }, {
            title: '识别列类型',
            dataIndex: 'configureType',
            key: 'configureType',
            // width: '8%'
        }, {
            title: '来源表',
            dataIndex: 'table',
            key: 'table',
            // width: '10%'
        }, {
            title: '来源列',
            dataIndex: 'column',
            key: 'column',
            // width: '10%'
        }, {
            title: '状态',
            dataIndex: 'status',
            key: 'status',
            // width: '10%',
        }, {
            title: '操作',
            // width: '10%',
            render: (text, record) => {
                const menu = (
                    <Menu>
                        <Menu.Item key="1">
                            <a>发布</a>
                        </Menu.Item>
                        <Menu.Item key="2">
                            <Popconfirm
                                title="确定删除此标签？"
                                okText="确定" cancelText="取消"
                                onConfirm={() => {this.removeTag(record)}}
                            >
                                <a>删除</a>
                            </Popconfirm>
                        </Menu.Item>
                    </Menu>
                )

                return (
                    <div>
                        <a onClick={() => {this.editTag(record)}}>
                            编辑
                        </a>
                        <span className="ant-divider" />
                        <Dropdown overlay={menu} trigger={['click']}>
                            <a className="ant-dropdown-link">
                                更多 
                                <Icon type="down" />
                            </a>
                        </Dropdown>
                    </div>
                )
            }
        }]
    }

    saveTag = () => {
        console.log('save')
    }

    onUserSourceChange = (value) => {
        console.log(value)
    }

    render() {
        const { visible, selectedIds } = this.state;
        const { getFieldDecorator } = this.props.form;

        const cardTitle = (
            <div className="flex font-12">
                <Search
                    placeholder="标签名称"
                    onSearch={this.onTagSearch}
                    style={{ width: 200, margin: '10px 0' }}
                />

                <div className="m-l-8">
                    标签分类：
                    <Select 
                        allowClear
                        showSearch
                        style={{ width: 150 }}
                        // placeholder="选择数据源类型"
                        onChange={this.onSourceChange}>
                        <Option key={"1"} value={"1"}>标签1</Option>
                        <Option key={"2"} value={"2"}>标签2</Option>
                    </Select>
                </div>

                <div className="m-l-8">
                    二级分类：
                    <Select
                        allowClear 
                        showSearch
                        style={{ width: 150 }}
                        // optionFilterProp="title"
                        // placeholder="选择数据源"
                        onChange={this.onUserSourceChange}>
                        <Option key={"1"} value={"1"}>标签1</Option>
                        <Option key={"2"} value={"2"}>标签2</Option>
                    </Select>
                </div>
            </div>
        )

        const cardExtra = (
            <div>
                <Button type="primary" style={{ margin: 10 }}><Link to="dl/tagConfig/identify">识别列配置</Link></Button>
                <Button type="primary" onClick={this.openModal}>注册标签</Button>
            </div>
        )

        const rowSelection = {
            selectedRowKeys: selectedIds,
            onChange: (selectedIds) => {
                this.setState({ selectedIds });
            },
        };

        return (
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
                    // loading={loading}
                    rowSelection={rowSelection}
                    pagination={false}
                    dataSource={[]}
                    onChange={this.onTableChange}
                />

                <Modal
                    title="注册标签"
                    wrapClassName="resolveTagModal"
                    width={'50%'}
                    visible={visible}
                    maskClosable={false}
                    okText="保存"
                    cancelText="取消"
                    onOk={this.saveTag}
                    onCancel={this.closeModal}
                >
                    <Form>
                        <FormItem {...formItemLayout} label="标签名称">
                            {
                                getFieldDecorator('name', {
                                    rules: [{ 
                                        required: true, 
                                        message: '标签名称不可为空' 
                                    }], 
                                    // initialValue: name
                                })(
                                    <Input />
                                )
                            }
                        </FormItem>
                        <FormItem {...formItemLayout} label="标签描述">
                            {
                                getFieldDecorator('des', {
                                    rules: [], 
                                    // initialValue: des
                                })(
                                    <TextArea 
                                        placeholder="标签描述" 
                                        // className="trigger-remarks" 
                                        autosize={{ minRows: 2, maxRows: 6 }} 
                                        // onChange={this.onRemarkChange} 
                                    />
                                )
                            }
                        </FormItem>
                        <FormItem {...formItemLayout} label="标签类目">
                            {
                                getFieldDecorator('type', {
                                    rules: [{ 
                                        required: true, 
                                        message: '标签类目不可为空' 
                                    }], 
                                    // initialValue: type
                                })(
                                    <Select
                                        showSearch
                                        // style={{ width: 150 }}
                                        // optionFilterProp="title"
                                        // placeholder="选择数据源"
                                        onChange={this.onUserSourceChange}>
                                        <Option key={"1"} value={"1"}>标签1</Option>
                                        <Option key={"2"} value={"2"}>标签2</Option>
                                    </Select>
                                )
                            }
                        </FormItem>
                        <FormItem {...formItemLayout} label="值域">
                            {
                                getFieldDecorator('range', {
                                    rules: [{ 
                                        required: true, 
                                        message: '标签名称不可为空' 
                                    }], 
                                    // initialValue: range
                                })(
                                    <TextArea 
                                        placeholder="值域" 
                                        // className="trigger-remarks" 
                                        autosize={{ minRows: 2, maxRows: 6 }} 
                                        onChange={this.onRemarkChange} 
                                    />
                                )
                            }
                        </FormItem>
                        <FormItem {...formItemLayout} label="目标数据库">
                            {
                                getFieldDecorator('db', {
                                    rules: [{ 
                                        required: true, 
                                        message: '标签名称不可为空' 
                                    }], 
                                    // initialValue: db
                                })(
                                    <Select
                                        showSearch
                                        // style={{ width: 150 }}
                                        // optionFilterProp="title"
                                        placeholder="选择目标数据库"
                                        onChange={this.onUserSourceChange}>
                                        <Option key={"1"} value={"1"}>标签1</Option>
                                        <Option key={"2"} value={"2"}>标签2</Option>
                                    </Select>
                                )
                            }
                        </FormItem>
                        <FormItem {...formItemLayout} label="来源表">
                            {
                                getFieldDecorator('table', {
                                    rules: [{ 
                                        required: true, 
                                        message: '标签名称不可为空' 
                                    }], 
                                    // initialValue: table
                                })(
                                    <Select
                                        showSearch
                                        // style={{ width: 150 }}
                                        // optionFilterProp="title"
                                        placeholder="选择来源表"
                                        onChange={this.onUserSourceChange}>
                                        <Option key={"1"} value={"1"}>标签1</Option>
                                        <Option key={"2"} value={"2"}>标签2</Option>
                                    </Select>
                                )
                            }
                        </FormItem>
                        <FormItem {...formItemLayout} label="标签所在列">
                            {
                                getFieldDecorator('column', {
                                    rules: [{ 
                                        required: true, 
                                        message: '标签名称不可为空' 
                                    }], 
                                    // initialValue: column
                                })(
                                    <Select
                                        showSearch
                                        // style={{ width: 150 }}
                                        // optionFilterProp="title"
                                        placeholder="选择标签所在列"
                                        onChange={this.onUserSourceChange}>
                                        <Option key={"1"} value={"1"}>标签1</Option>
                                        <Option key={"2"} value={"2"}>标签2</Option>
                                    </Select>
                                )
                            }
                        </FormItem>
                        <FormItem {...formItemLayout} label="识别列ID">
                            {
                                getFieldDecorator('configureID', {
                                    rules: [{ 
                                        required: true, 
                                        message: '标签名称不可为空' 
                                    }], 
                                    // initialValue: configureID
                                })(
                                    <Select
                                        showSearch
                                        // style={{ width: 150 }}
                                        // optionFilterProp="title"
                                        placeholder="选择识别列ID"
                                        onChange={this.onUserSourceChange}>
                                        <Option key={"1"} value={"1"}>标签1</Option>
                                        <Option key={"2"} value={"2"}>标签2</Option>
                                    </Select>
                                )
                            }
                        </FormItem>
                        <FormItem {...formItemLayout} label="识别列类型">
                            {
                                getFieldDecorator('configureType', {
                                    rules: [{ 
                                        required: true, 
                                        message: '标签名称不可为空' 
                                    }], 
                                    // initialValue: configureType
                                })(
                                    <Select
                                        showSearch
                                        // style={{ width: 150 }}
                                        // optionFilterProp="title"
                                        placeholder="选择识别列类型"
                                        onChange={this.onUserSourceChange}>
                                        <Option key={"1"} value={"1"}>标签1</Option>
                                        <Option key={"2"} value={"2"}>标签2</Option>
                                    </Select>
                                )
                            }
                        </FormItem>
                    </Form>
                </Modal>
            </Card>
        )
    }
}
RegisteredTagPane = Form.create()(RegisteredTagPane);