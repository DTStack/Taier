import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router';
import { Table, Card, Modal, Form, Button, Input, Select, Menu, Dropdown, Icon, Cascader, Popconfirm, message } from 'antd';

import { tagConfigActions } from '../../actions/tagConfig';
import { apiMarketActions } from '../../actions/apiMarket';
import { dataSourceActions } from '../../actions/dataSource';
import { formItemLayout } from '../../consts';
import TCApi from '../../api/tagConfig';

const Search = Input.Search;
const Option = Select.Option;
const FormItem = Form.Item;
const TextArea = Input.TextArea;

const mapStateToProps = state => {
    const { tagConfig, dataSource, apiMarket } = state;
    return { tagConfig, dataSource, apiMarket }
}

const mapDispatchToProps = dispatch => ({
    getRegisteredTagList(params) {
        dispatch(tagConfigActions.getRegisteredTagList(params));
    },
    getDataSourcesTable(params) {
        dispatch(dataSourceActions.getDataSourcesTable(params));
    },
    resetDataSourcesTable() {
        dispatch(dataSourceActions.resetDataSourcesTable());
    },
    getDataSourcesColumn(params) {
        dispatch(dataSourceActions.getDataSourcesColumn(params));
    },
    getCatalogue(pid) {
        dispatch(apiMarketActions.getCatalogue(pid));
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
        },
        tagList: {}
    }

    componentDidMount() {
        // this.props.getRegisteredTagList(this.state.queryParams);
        this.getRegisteredTagData(this.state.queryParams);
    }

    getRegisteredTagData = (params) => {
        this.setState({ loading: true });
        TCApi.queryRegisteredTag(params).then((res) => {
            if (res.code === 1) {
                this.setState({ 
                    loading: false,
                    tagList: res.data 
                });
            }
        });
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
            dataIndex: 'tagDesc',
            key: 'tagDesc',
            // width: '12%'
        }, {
            title: '标签类目',
            dataIndex: 'catalogueName',
            key: 'catalogueName',
            // width: '8%',
        }, {
            title: '值域',
            dataIndex: 'tagRange',
            key: 'tagRange',
            // width: '12%'
        }, {
            title: '目标数据库',
            dataIndex: 'dataSourceName',
            key: 'dataSourceName',
            // width: '10%'
        }, {
            title: '识别列ID',
            dataIndex: 'identityColumn',
            key: 'identityColumn',
            // width: '8%'
        }, {
            title: '识别列类型',
            dataIndex: 'identityName',
            key: 'identityName',
            // width: '8%'
        }, {
            title: '来源表',
            dataIndex: 'originTable',
            key: 'originTable',
            // width: '10%'
        }, {
            title: '来源列',
            dataIndex: 'originColumn',
            key: 'originColumn',
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

    openModal = () => {
        this.setState({ visible: true });
    }

    closeModal = () => {
        this.setState({ visible: false });
    }

    saveRegisterTag = () => {
        const { form } = this.props;
        const { queryParams } = this.state;

        form.validateFields((err, values) => {
            console.log(err,values)
            
            if(!err) {
                values.catalogueId = values.catalogueId.pop();
                TCApi.addRegisterTag(values).then((res) => {
                    if (res.code === 1) {
                        message.success('添加成功！');
                        this.closeModal();
                        this.getRegisteredTagData(queryParams);
                    }
                });
            }
        });
    }

    saveTag = () => {
        console.log('save')
    }

    // 数据源下拉框
    renderUserSource = (data) => {
        return data.map((source) => {
            let title = `${source.dataName}（${source.sourceTypeValue}）`;
            return (
                <Option 
                    key={source.id} 
                    value={source.id.toString()}
                    title={title}>
                    {title}
                </Option>
            )
        });
    }

    // 数据表下拉框
    renderSourceTable = (data) => {
        return data.map((tableName) => {
            return <Option 
                key={tableName} 
                value={tableName}>
                {tableName}
            </Option>
        });
    }

    renderTableColumn = (data) => {
        return data.map((item) => {
            return <Option 
                key={item.key} 
                value={item.key}>
                {item.key}
            </Option>
        });
    }

    renderIdentifyColumn = (data) => {
        return data.map((item) => {
            return (
                <Option 
                    key={item.id} 
                    value={item.id.toString()}
                    title={item.name}>
                    {item.name}
                </Option>
            )
        });
    }

    onSourceChange = (id) => {
        console.log(id)
        this.props.resetDataSourcesTable();
        this.props.getDataSourcesTable({ sourceId: id });
    }

    onSourceTableChange = (name) => {
        console.log(name)
        const { form } = this.props;
        this.props.getDataSourcesColumn({ 
            sourceId: form.getFieldValue("dataSourceId"), 
            tableName: name
        });
    }

    // TagName
    onTagNameSearch = (name) => {
        let params = {
            ...this.state.params, 
            pageSize: 1,
            name: name ? name : undefined
        };

        this.props.getRuleTagList(params);
        this.setState({ params });
    }

    getCatagoryOption = () => {
        const tree = this.props.apiMarket.apiCatalogue;

        function exchangeTree(data) {
            let arr = []
            if (!data||data.length<1) {
                return null;
            }
            
            
            for (let i = 0; i < data.length; i++) {
            
                let item = data[i];
                
                if(item.api){
                    return null;
                }
                arr.push({
                    value: item.id,
                    label: item.catalogueName,
                    children: exchangeTree(item.childCatalogue)
                })
            }
            return arr;
        }

        return exchangeTree(tree);

    }

    render() {
        const { form, dataSource, tagConfig } = this.props;
        const { getFieldDecorator } = form;
        const { sourceList, sourceTable, sourceColumn } = dataSource;
        const { identifyColumn } = tagConfig;
        const { visible, selectedIds, loading, tagList, queryParams } = this.state;

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
                        onChange={this.onssSourceChange}>
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

        const pagination = {
            current: queryParams.currentPage,
            pageSize: queryParams.pageSize,
            total: tagList.totalCount
        }

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
                    loading={loading}
                    rowSelection={rowSelection}
                    pagination={pagination}
                    dataSource={tagList.data}
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
                    onOk={this.saveRegisterTag}
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
                                getFieldDecorator('tagDesc', {
                                    rules: [], 
                                    // initialValue: tagDesc
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
                                getFieldDecorator('catalogueId', {
                                    rules: [{ 
                                        required: true, 
                                        message: '标签类目不可为空' 
                                    }], 
                                    // initialValue: catalogueId
                                })(
                                    <Cascader 
                                        showSearch 
                                        popupClassName="noheight" 
                                        options={this.getCatagoryOption()} 
                                        placeholder="请选择分组" 
                                    />
                                )
                            }
                        </FormItem>
                        <FormItem {...formItemLayout} label="值域">
                            {
                                getFieldDecorator('tagRange', {
                                    rules: [{ 
                                        required: true, 
                                        message: '标签名称不可为空' 
                                    }], 
                                    // initialValue: tagRange
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
                                getFieldDecorator('dataSourceId', {
                                    rules: [{ 
                                        required: true, 
                                        message: '标签名称不可为空' 
                                    }], 
                                    // initialValue: dataSourceId
                                })(
                                    <Select
                                        showSearch
                                        // style={{ width: 150 }}
                                        // optionFilterProp="title"
                                        placeholder="选择目标数据库"
                                        onChange={this.onSourceChange}>
                                        {
                                            this.renderUserSource(sourceList)
                                        }
                                    </Select>
                                )
                            }
                        </FormItem>
                        <FormItem {...formItemLayout} label="来源表">
                            {
                                getFieldDecorator('originTable', {
                                    rules: [{ 
                                        required: true, 
                                        message: '标签名称不可为空' 
                                    }], 
                                    // initialValue: originTable
                                })(
                                    <Select
                                        showSearch
                                        // style={{ width: 150 }}
                                        // optionFilterProp="title"
                                        placeholder="选择来源表"
                                        onChange={this.onSourceTableChange}>
                                        {
                                            this.renderSourceTable(sourceTable)
                                        }
                                    </Select>
                                )
                            }
                        </FormItem>
                        <FormItem {...formItemLayout} label="来源列">
                            {
                                getFieldDecorator('originColumn', {
                                    rules: [{ 
                                        required: true, 
                                        message: '不可为空' 
                                    }], 
                                    // initialValue: originColumn
                                })(
                                    <Select
                                        showSearch
                                        // style={{ width: 150 }}
                                        // optionFilterProp="title"
                                        placeholder="选择来源列"
                                        onChange={this.onUserSourceChange}>
                                        {
                                            this.renderTableColumn(sourceColumn)
                                        }
                                    </Select>
                                )
                            }
                        </FormItem>
                        <FormItem {...formItemLayout} label="识别列ID">
                            {
                                getFieldDecorator('identityColumn', {
                                    rules: [{ 
                                        required: true, 
                                        message: '标签名称不可为空' 
                                    }], 
                                    // initialValue: identityColumn
                                })(
                                    <Select
                                        showSearch
                                        // style={{ width: 150 }}
                                        // optionFilterProp="title"
                                        placeholder="选择识别列ID"
                                        onChange={this.onUserSourceChange}>
                                        {
                                            this.renderTableColumn(sourceColumn)
                                        }
                                    </Select>
                                )
                            }
                        </FormItem>
                        <FormItem {...formItemLayout} label="识别列类型">
                            {
                                getFieldDecorator('identityId', {
                                    rules: [{ 
                                        required: true, 
                                        message: '标签名称不可为空' 
                                    }], 
                                    // initialValue: identityId
                                })(
                                    <Select
                                        showSearch
                                        // style={{ width: 150 }}
                                        // optionFilterProp="title"
                                        placeholder="选择识别列类型"
                                        onChange={this.onUserSourceChange}>
                                        {
                                            this.renderIdentifyColumn(identifyColumn)
                                        }
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