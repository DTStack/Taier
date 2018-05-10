import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router';
import { Table, Card, Modal, Form, Button, Input, Select, Menu, Dropdown, Icon, Cascader, Popconfirm, message } from 'antd';

import { tagConfigActions } from '../../actions/tagConfig';
import { apiMarketActions } from '../../actions/apiMarket';
import { dataSourceActions } from '../../actions/dataSource';
import { formItemLayout, TAG_STATUS } from '../../consts';
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
        tagList: {},
        editData: {},
        catalogue2Data: []
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
            render: (text) => {
                return TAG_STATUS[text];
            },
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
                                onConfirm={this.removeTag.bind(this, record.id)}
                            >
                                <a>删除</a>
                            </Popconfirm>
                        </Menu.Item>
                    </Menu>
                )

                return (
                    <div>
                        <a onClick={() => {this.editBaseInfo(record)}}>
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

    // 编辑标签基本信息
    editBaseInfo = (record) => {
        let editData = {
            ...record, 
            catalogueId: this.getCatalogueArray(record.catalogueId)
        }
        console.log(editData)
        this.openModal();
        this.setState({ editData });
    }

    // 删除标签
    removeTag = (id) => {
        const { queryParams } = this.state;

        if (id) {
            TCApi.deleteTag({ tagId: id }).then((res) => {
                if (res.code === 1) {
                    message.success('删除成功！');
                    this.getRegisteredTagData(queryParams);
                }
            });
        }
    }

    // 取消编辑
    cancel = () => {
        this.closeModal();
        this.setState({ editData: {} });
    }

    openModal = () => {
        this.setState({ visible: true });
    }

    closeModal = () => {
        this.setState({ visible: false });
    }

    // 保存标签基本信息
    saveRegisterTag = () => {
        const { form } = this.props;
        const { queryParams, editData } = this.state;

        form.validateFields((err, values) => {
            console.log(err,values)
            let api, params, msg;

            if(!err) {
                values.catalogueId = [...values.catalogueId].pop();

                if (editData.id) {
                    api = TCApi.updateTagBaseInfo;
                    params = {...values, id: editData.id};
                    msg = '更新成功';
                } else {
                    api = TCApi.addRegisterTag;
                    params = values;
                    msg = '新增成功';
                }

                api(params).then((res) => {
                    if (res.code === 1) {
                        message.success(msg);
                        this.closeModal();
                        this.setState({ editData: {} });
                        
                        form.resetFields();
                        this.getRegisteredTagData(queryParams);
                    }
                });
            }
        });
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

    // 数据列下拉框
    renderTableColumn = (data) => {
        return data.map((item) => {
            return <Option 
                key={item.key} 
                value={item.key}>
                {item.key}
            </Option>
        });
    }

    // 识别列类型下拉框
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
        let queryParams = {
            ...this.state.queryParams, 
            currentPage: 1,
            name: name ? name : undefined
        };

        this.getRegisteredTagData(queryParams);
        this.setState({ queryParams });
    }

    // 一级分类
    onFirstCatalogueChange = (id) => {
        let queryParams = {
            ...this.state.queryParams, 
            currentPage: 1,
            pid: id ? id : undefined
        };

        if (id) {
            this.getSecondCatalogue(id)
        } else {
            this.setState({ catalogue2Data: [] });
        }

        this.getRegisteredTagData(queryParams);
        this.setState({ queryParams });
    }

    // 二级分类
    onSecondCatalogueChange = (id) => {
        let queryParams = {
            ...this.state.queryParams, 
            currentPage: 1,
            cid: id ? id : undefined
        };

        this.getRegisteredTagData(queryParams);
        this.setState({ queryParams });
    }
    
    // 类目下拉框数据初始化
    initCatagoryOption = (data) => {
        if (data.some(item => item.api === true)) {
            return [];
        } else {
            return data.map((item) => {
                return {
                    value: item.id,
                    label: item.catalogueName,
                    children: this.initCatagoryOption(item.childCatalogue)
                }
            });
        }
    }

    // 获取已选取的类目array
    getCatalogueArray = (value) => {
        const { apiCatalogue } = this.props.apiMarket;
        let arr = [];

        const flat = (data) => {
            for (let i = 0; i < data.length; i++) {
                if (data[i].api) {
                    return
                }
                // 匹配节点
                if (data[i].id === value) {
                    arr.push(data[i].id);
                    return data[i].id;
                }
                // 若子节点含有对应的值，父节点入队
                if (flat(data[i].childCatalogue)) {
                    arr.push(data[i].id);
                    return data[i].id;
                }
            }
        }

        flat(apiCatalogue);
        return arr.reverse();
    }

    // 获取二级分类数据
    getSecondCatalogue = (id) => {
        const { apiCatalogue } = this.props.apiMarket;

        let child = apiCatalogue.filter(item => item.id == id)[0].childCatalogue;
        child = child.some(item => item.api) ? [] : child;

        this.setState({ catalogue2Data: child });
    }

    render() {
        const { form, dataSource, tagConfig, apiMarket } = this.props;
        const { getFieldDecorator } = form;
        const { apiCatalogue } = apiMarket;
        const { identifyColumn } = tagConfig;
        const { sourceList, sourceTable, sourceColumn } = dataSource;
        const { queryParams, visible, selectedIds, loading, tagList, editData, catalogue2Data } = this.state;

        const cardTitle = (
            <div className="flex font-12">
                <Search
                    placeholder="标签名称"
                    onSearch={this.onTagNameSearch}
                    style={{ width: 200, margin: '10px 0' }}
                />

                <div className="m-l-8">
                    标签分类：
                    <Select 
                        allowClear
                        showSearch
                        style={{ width: 150 }}
                        placeholder="选择标签分类"
                        optionFilterProp="title"
                        onChange={this.onFirstCatalogueChange}>
                        {
                            apiCatalogue.map(item => {
                                return <Option 
                                    key={item.id} 
                                    value={item.id.toString()}
                                    title={item.catalogueName}>
                                    {item.catalogueName}
                                </Option>
                            })
                        }
                    </Select>
                </div>

                <div className="m-l-8">
                    二级分类：
                    <Select
                        allowClear 
                        showSearch
                        style={{ width: 150 }}
                        optionFilterProp="title"
                        placeholder="选择二级分类"
                        onChange={this.onSecondCatalogueChange}>
                        {
                            catalogue2Data.map(item => {
                                return <Option 
                                    key={item.id} 
                                    value={item.id.toString()}
                                    title={item.catalogueName}>
                                    {item.catalogueName}
                                </Option>
                            })
                        }
                    </Select>
                </div>
            </div>
        )

        const cardExtra = (
            <div>
                <Button 
                    type="primary" 
                    style={{ margin: 10 }}>
                    <Link to="dl/tagConfig/identify">识别列配置</Link>
                </Button>
                <Button 
                    type="primary" 
                    onClick={this.openModal}>
                    注册标签
                </Button>
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
                    onCancel={this.cancel}
                >
                    <Form>
                        <FormItem {...formItemLayout} label="标签名称">
                            {
                                getFieldDecorator('name', {
                                    rules: [{ 
                                        required: true, 
                                        message: '标签名称不可为空' 
                                    }], 
                                    initialValue: editData.name
                                })(
                                    <Input />
                                )
                            }
                        </FormItem>
                        <FormItem {...formItemLayout} label="标签描述">
                            {
                                getFieldDecorator('tagDesc', {
                                    rules: [], 
                                    initialValue: editData.tagDesc
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
                                    initialValue: editData.catalogueId
                                })(
                                    <Cascader 
                                        showSearch 
                                        popupClassName="noheight" 
                                        options={this.initCatagoryOption(apiCatalogue)} 
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
                                    initialValue: editData.tagRange
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
                                    initialValue: editData.dataSourceId ? editData.dataSourceId.toString() : undefined
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
                                    initialValue: editData.originTable
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
                                    initialValue: editData.originColumn
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
                                    initialValue: editData.identityColumn
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
                                    initialValue: editData.identityId ? editData.identityId.toString() : undefined
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