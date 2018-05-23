import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router';
import { Table, Card, Modal, Form, Icon,
    Button, Input, Select, Menu, message,
    Dropdown, Popconfirm, Cascader
} from 'antd';
import moment from 'moment';

import { tagConfigActions } from '../../actions/tagConfig';
import { apiMarketActions } from '../../actions/apiMarket';
import { dataSourceActions } from '../../actions/dataSource';
import { formItemLayout, TAG_STATUS, TAG_PUBLISH_STATUS } from '../../consts';
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
    getTagDataSourcesList(params) {
        dispatch(dataSourceActions.getTagDataSourcesList(params));
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
        queryParams: {
            currentPage: 1,
            pageSize: 20
        },
        tagList: {},
        editData: {},
        catalogue2Data: []
    }

    componentDidMount() {
        this.props.getTagDataSourcesList();
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
            width: '10%'
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
            width: 100
        }, {
            title: '目标数据库',
            dataIndex: 'dataSourceName',
            key: 'dataSourceName',
            width: '8%'
        }, {
            title: '来源表',
            dataIndex: 'originTable',
            key: 'originTable',
            width: '8%'
        }, {
            title: '识别列ID',
            dataIndex: 'identityColumn',
            key: 'identityColumn',
            width: 100
        }, {
            title: '识别列类型',
            dataIndex: 'identityName',
            key: 'identityName',
            width: '8%'
        }, {
            title: '数据更新日期',
            dataIndex: 'gmtModified',
            key: 'gmtModified',
            width: '10%',
            render: (text) => {
                return text ? moment(text).format("YYYY-MM-DD HH:mm:ss") : '--';
            }
        }, {
            title: '状态',
            dataIndex: 'status',
            key: 'status',
            width: 100,
            render: (text) => {
                return TAG_STATUS[text];
            }
        }, {
            title: '发布状态',
            dataIndex: 'publishStatus',
            key: 'publishStatus',
            width: 90,
            render: (text) => {
                return TAG_PUBLISH_STATUS[text];
            }
        }, {
            title: '操作',
            width: 90,
            render: (text, record) => {
                const menu = (
                    <Menu>
                        {
                            TAG_STATUS[record.status] != '更新中'
                            &&
                            <Menu.Item key="edit">
                                <a onClick={this.editBaseInfo.bind(this, record)}>
                                    编辑
                                </a>
                            </Menu.Item>
                        }
                        {
                            TAG_STATUS[record.status] != '更新中'
                            &&
                            <Menu.Item key="edit2">
                                <Link to={`/dl/tagConfig/ruleTagEdit/${record.id}`}>
                                    配置计算逻辑
                                </Link>
                            </Menu.Item>
                        }
                        <Menu.Item key="log">
                            <Link to={`/dl/tagConfig/tagLog/${record.id}`}>
                                查看更新历史
                            </Link>
                        </Menu.Item>
                        {
                            (TAG_STATUS[record.status] == '更新完成' && TAG_PUBLISH_STATUS[record.publishStatus] != '已发布')
                            &&
                            <Menu.Item key="pub">
                                <Link to={`/dl/manage/newApi/${record.id}`}>
                                    发布
                                </Link>
                            </Menu.Item>
                        }
                        <Menu.Item key="del">
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
                    <Dropdown overlay={menu} trigger={['click']}>
                        <Button>操作<Icon type="down" /></Button>
                    </Dropdown>
                )
            }
        }];
    }

    // 编辑标签基本信息
    editBaseInfo = (record) => {
        const { apiCatalogue } = this.props.apiMarket;

        let editData = {
            ...record, 
            catalogueId: this.getCatalogueArray(record.catalogueId)
        };

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
        this.props.form.resetFields();
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

    // 一级分类筛选
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

        this.getRuleTagData(queryParams);
        this.setState({ queryParams });
    }

    // 获取二级分类数据
    getSecondCatalogue = (id) => {
        const { apiCatalogue } = this.props.apiMarket;

        let child = apiCatalogue.filter(item => item.id == id)[0].childCatalogue;
        child = child.some(item => item.api) ? [] : child;

        this.setState({ catalogue2Data: child });
    }

    // 二级分类筛选
    onSecondCatalogueChange = (id) => {
        let queryParams = {
            ...this.state.queryParams, 
            currentPage: 1,
            cid: id ? id : undefined
        };

        this.getRuleTagData(queryParams);
        this.setState({ queryParams });
    }

    // name筛选
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

    // 表格换页/排序
    onTableChange = (page, filter, sorter) => {
        let queryParams = {
            ...this.state.queryParams, 
            currentPage: page.current,
        };

        this.getRuleTagData(queryParams);
        this.setState({ queryParams });
    }

    render() {
        const { form, tagConfig, dataSource, apiMarket } = this.props;
        const { getFieldDecorator } = form;
        const { tagSourceList } = dataSource;
        const { apiCatalogue } = apiMarket;
        const { identifyColumn } = tagConfig;
        const { queryParams, visible, loading, tagList, editData, catalogue2Data } = this.state;

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
                    新建标签
                </Button>
            </div>
        )

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
                                    }, { 
                                        max: 20,
                                        message: "最大字数不能超过20" 
                                    }, { 
                                        pattern: new RegExp(/^([\w|\u4e00-\u9fa5]*)$/), 
                                        message: '名称只能以字母，数字，下划线组成' 
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
                                        autosize={{ minRows: 2, maxRows: 6 }} 
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
                                        message: '值域不可为空' 
                                    }], 
                                    initialValue: editData.tagRange
                                })(
                                    <TextArea 
                                        placeholder="值域" 
                                        autosize={{ minRows: 2, maxRows: 6 }} 
                                    />
                                )
                            }
                        </FormItem>
                        <FormItem {...formItemLayout} label="目标数据库">
                            {
                                getFieldDecorator('dataSourceId', {
                                    rules: [{ 
                                        required: true, 
                                        message: '目标数据库不可为空' 
                                    }], 
                                    initialValue: editData.dataSourceId ? editData.dataSourceId.toString() : undefined
                                })(
                                    <Select
                                        showSearch
                                        optionFilterProp="title"
                                        placeholder="选择目标数据库"
                                        disabled={TAG_PUBLISH_STATUS[editData.publishStatus] === '已发布'}
                                        onChange={this.onSourceChange}>
                                        {
                                            tagSourceList.map((source) => {
                                                let title = `${source.dataName}（${source.sourceTypeValue}）`;
                                                return <Option 
                                                    key={source.id} 
                                                    value={source.id.toString()}
                                                    title={title}>
                                                    {title}
                                                </Option>
                                            })
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
                                        message: '来源表不可为空' 
                                    }], 
                                    initialValue: editData.originTable
                                })(
                                    <Input disabled={TAG_PUBLISH_STATUS[editData.publishStatus] === '已发布'} />
                                )
                            }
                        </FormItem>
                        <FormItem {...formItemLayout} label="识别列ID">
                            {
                                getFieldDecorator('identityColumn', {
                                    rules: [{ 
                                        required: true, 
                                        message: '识别列ID不可为空' 
                                    }], 
                                    initialValue: editData.identityColumn
                                })(
                                    <Input disabled={TAG_PUBLISH_STATUS[editData.publishStatus] === '已发布'} />
                                )
                            }
                        </FormItem>
                        <FormItem {...formItemLayout} label="识别列类型">
                            {
                                getFieldDecorator('identityId', {
                                    rules: [{ 
                                        required: true, 
                                        message: '识别列类型不可为空' 
                                    }], 
                                    initialValue: editData.identityId ? editData.identityId.toString() : undefined
                                })(
                                    <Select
                                        showSearch
                                        optionFilterProp="title"
                                        placeholder="选择识别列类型"
                                        disabled={TAG_PUBLISH_STATUS[editData.publishStatus] === '已发布'}>
                                        {
                                            identifyColumn.map((item) => {
                                                return <Option 
                                                    key={item.id} 
                                                    value={item.id.toString()}
                                                    title={item.name}>
                                                    {item.name}
                                                </Option>
                                            })
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