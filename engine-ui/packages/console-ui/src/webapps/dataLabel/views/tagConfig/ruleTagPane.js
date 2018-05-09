import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router';
import { Table, Card, Modal, Form, Button, Input, Select, Menu, Dropdown, Popconfirm, Cascader, Icon, message } from 'antd';

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
    getAllIdentifyColumn(params) {
        dispatch(tagConfigActions.getAllIdentifyColumn(params));
    },
    getDataSourcesList(params) {
        dispatch(dataSourceActions.getDataSourcesList(params));
    },
    getCatalogue(pid) {
        dispatch(apiMarketActions.getCatalogue(pid));
    },
})

@connect(mapStateToProps, mapDispatchToProps)
export default class RuleTagPane extends Component {

    state = {
        visible: false,
        loading: false,
        selectedIds: [],
        queryParams: {
            currentPage: 1,
            pageSize: 20,
            name: undefined,
            catalogueId: undefined,
            pid: undefined,
            cid: undefined,
            identityId: undefined,
            status: undefined,
            dataSourceId: undefined
        },
        tagList: {},
        // params: {
        //     name: undefined
        // }
        editData: {}
    }

    componentDidMount() {
        this.props.getDataSourcesList();
        this.props.getAllIdentifyColumn();
        this.props.getCatalogue(0);

        this.getRuleTagData(this.state.queryParams);
    }

    // 获取标签列表数据
    getRuleTagData = (params) => {
        this.setState({ loading: true });

        TCApi.queryRuleTag(params).then((res) => {
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
            width: '12%'
        }, {
            title: '标签描述',
            dataIndex: 'tagDesc',
            key: 'tagDesc',
            width: '12%'
        }, {
            title: '标签类目',
            dataIndex: 'catalogueName',
            key: 'catalogueName',
            width: '10%',
        }, {
            title: '值域',
            dataIndex: 'tagRange',
            key: 'tagRange',
            width: '10%'
        }, {
            title: '目标数据库',
            dataIndex: 'dataSourceName',
            key: 'dataSourceName',
            width: '10%'
        }, {
            title: '识别列ID',
            dataIndex: 'identityColumn',
            key: 'identityColumn',
            width: '8%'
        }, {
            title: '识别列类型',
            dataIndex: 'identityName',
            key: 'identityName',
            width: '8%'
        }, {
            title: '数据更新日期',
            dataIndex: 'executeTime',
            key: 'executeTime',
            render: (text) => {
                return text ? moment(text).format("YYYY-MM-DD HH:mm:ss") : '--';
            },
            width: '10%'
        }, {
            title: '状态',
            dataIndex: 'status',
            key: 'status',
            width: '8%',
            render: (text) => {
                return TAG_STATUS[text];
            },
        }, {
            title: '操作',
            width: '12%',
            render: (text, record) => {
                const menu = (
                    <Menu>
                        <Menu.Item key="0">
                            <a>查看更新历史</a>
                        </Menu.Item>
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
                        <a onClick={this.editBaseInfo.bind(this, record)}>
                            编辑 
                        </a>
                        <span className="ant-divider" />
                        <Link to={`/dl/tagConfig/ruleTagEdit/${record.id}`}>配置计算逻辑</Link>
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
        const { apiCatalogue } = this.props.apiMarket;

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
                    this.getRuleTagData(queryParams);
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
    saveRuleTag = () => {
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
                    api = TCApi.addRuleTag;
                    params = values;
                    msg = '新增成功';
                }

                api(params).then((res) => {
                    if (res.code === 1) {
                        message.success(msg);
                        this.closeModal();
                        this.setState({ editData: {} });
                        
                        form.resetFields();
                        this.getRuleTagData(queryParams);
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

    onUserSourceChange = (value) => {
        console.log(value)
    }

    onCatagoryIdChange = (value, option) => {
        console.log(value, option)
    }

    // TagName
    onTagNameSearch = (name) => {
        let queryParams = {
            ...this.state.queryParams, 
            currentPage: 1,
            name: name ? name : undefined
        };

        this.getRuleTagData(queryParams);
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
<<<<<<< HEAD
=======

        let arr = [];

        if (this.arrint(apiCatalogue, value)) {
            arr.push(this.arrint(apiCatalogue, value));
            this.arrint(apiCatalogue, value)
        }

        return arr;

    }

    arrint = (data, value) => {
        data.forEach((item) => {
            if (item.id === value) {
                return item.id
            } else {
                return false;
            }
        })
    }


    getInitCatagoryList(value, catagorys) {

        const tree = catagorys || this.props.apiMarket.apiCatalogue;
>>>>>>> a9a6dad57b8674ce5795c6c64b8f5617b86cf153
        let arr = [];

        const flat = (data) => {
            let id;

            data.forEach((item) => {
                if (item.api) {
                    return
                }
                // 匹配节点
                if (item.id === value) {
                    arr.push(item.id);
                    id = item.id;
                }
                // 若子节点含有对应的值，父节点入队
                if (flat(item.childCatalogue)) {
                    arr.push(item.id);
                    id = item.id;
                }
            });

            return id;
        }

        flat(apiCatalogue);

        return arr.reverse();
    }

    render() {
        const { form, tagConfig, dataSource, apiMarket } = this.props;
        const { getFieldDecorator } = form;
        const { sourceList } = dataSource;
        const { apiCatalogue } = apiMarket;
        const { identifyColumn } = tagConfig;
        const { queryParams, visible, selectedIds, loading, tagList, editData } = this.state;

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
                        placeholder="选择二级分类"
                        onChange={this.onUserSourceChange}>
                        <Option key={"1"} value={"1"}>标签1</Option>
                        <Option key={"2"} value={"2"}>标签2</Option>
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
                    新建标签
                </Button>
            </div>
        )

        const rowSelection = {
            selectedRowKeys: selectedIds,
            onChange: (selectedIds) => {
                this.setState({ selectedIds });
            },
        };

        const pagination = {
            current: queryParams.currentPage,
            pageSize: queryParams.pageSize,
            total: tagList.totalCount
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
                    title="新建标签"
                    wrapClassName="ruleTagModal"
                    width={'50%'}
                    visible={visible}
                    maskClosable={false}
                    okText="保存"
                    cancelText="取消"
                    onOk={this.saveRuleTag}
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
                                        onChange={this.onCatagoryIdChange}
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
                        <FormItem {...formItemLayout} label="识别列ID">
                            {
                                getFieldDecorator('identityColumn', {
                                    rules: [{ 
                                        required: true, 
                                        message: '标签名称不可为空' 
                                    }], 
                                    initialValue: editData.identityColumn
                                })(
                                    <Input />
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
RuleTagPane = Form.create()(RuleTagPane);