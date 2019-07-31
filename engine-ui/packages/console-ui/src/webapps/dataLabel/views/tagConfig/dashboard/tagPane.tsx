import * as React from 'react';
import { connect } from 'react-redux';
import { Link, hashHistory } from 'react-router';
import {
    Table, Card, Icon, Button,
    Input, Select, Menu, message,
    Dropdown, Popconfirm
} from 'antd';

import utils from 'utils';
import EditTagModal from './editTagModal';
import { dataSourceActions } from '../../../actions/dataSource';
import { TAG_STATUS, TAG_PUBLISH_STATUS } from '../../../consts';
import TCApi from '../../../api/tagConfig';

const Search = Input.Search;
const Option = Select.Option;

const mapStateToProps = (state: any) => {
    const { apiMarket } = state;
    return { apiMarket }
}
const mapDispatchToProps = (dispatch: any) => ({
    getDataSourcesTable (params: any) {
        dispatch(dataSourceActions.getDataSourcesTable(params));
    },
    getDataSourcesColumn (params: any) {
        dispatch(dataSourceActions.getDataSourcesColumn(params));
    }
})

@(connect(mapStateToProps, mapDispatchToProps) as any)
class TagPane extends React.Component<any, any> {
    state: any = {
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

    componentDidMount () {
        this.getTagListData(this.state.queryParams);
    }

    // 获取标签列表数据
    getTagListData = (params: any) => {
        this.setState({ loading: true });

        let api = this.props.tagType === 'rule' ? TCApi.queryRuleTag : TCApi.queryRegisteredTag;

        api(params).then((res: any) => {
            if (res.code === 1) {
                this.setState({
                    loading: false,
                    tagList: res.data
                });
            }
        });
    }

    // 规则标签表格设置
    initRuleTagTable = () => {
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
            width: '10%'
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
            title: '更新日期',
            dataIndex: 'gmtModified',
            key: 'gmtModified',
            width: '10%',
            render: (time: any) => {
                return time ? utils.formatDateTime(time) : '--';
            }
        }, {
            title: '状态',
            dataIndex: 'status',
            key: 'status',
            width: 100,
            render: (text: any) => {
                return TAG_STATUS[text];
            }
        }, {
            title: '发布状态',
            dataIndex: 'publishStatus',
            key: 'publishStatus',
            width: 90,
            render: (text: any) => {
                return TAG_PUBLISH_STATUS[text];
            }
        }, {
            title: '操作',
            width: 90,
            render: (text: any, record: any) => {
                const menu = (
                    <Menu>
                        {
                            TAG_STATUS[record.status] != '更新中' &&
                            <Menu.Item key="edit">
                                <a onClick={this.editBaseInfo.bind(this, record)}>
                                    编辑
                                </a>
                            </Menu.Item>
                        }
                        {
                            (TAG_STATUS[record.status] != '更新中' && TAG_PUBLISH_STATUS[record.publishStatus] === '未发布') &&
                            <Menu.Item key="edit2">
                                <Link to={`/dl/tagConfig/ruleTagEdit/${record.id}`}>
                                    配置计算逻辑
                                </Link>
                            </Menu.Item>
                        }
                        {
                            (TAG_STATUS[record.status] == '更新完成' && TAG_PUBLISH_STATUS[record.publishStatus] === '未发布') &&
                            <Menu.Item key="pub">
                                <a onClick={this.checkPublish.bind(this, record.id)}>
                                    发布
                                </a>
                            </Menu.Item>
                        }
                        <Menu.Item key="log">
                            <Link to={`/dl/tagConfig/tagLog/${record.id}`}>
                                查看更新历史
                            </Link>
                        </Menu.Item>
                        <Menu.Item key="del">
                            <Popconfirm
                                title="确定删除此标签？"
                                okText="确定" cancelText="取消"
                                onConfirm={this.removeTag.bind(this, record.id)}>
                                <a>删除</a>
                            </Popconfirm>
                        </Menu.Item>
                    </Menu>
                )
                return (<span id={`tag_${record.id}`}>
                    <Dropdown
                        getPopupContainer={
                            () => document.getElementById(`tag_${record.id}`)
                        }
                        overlay={menu}
                        trigger={['click']}
                    >
                        <Button>操作<Icon type="down" /></Button>
                    </Dropdown>
                </span>
                )
            }
        }];
    }

    // 注册标签表格设置
    initRegisterTagTable = () => {
        return [{
            title: '标签名称',
            dataIndex: 'name',
            key: 'name',
            width: '10%'
        }, {
            title: '标签描述',
            dataIndex: 'tagDesc',
            key: 'tagDesc',
            width: '10%'
        }, {
            title: '标签类目',
            dataIndex: 'catalogueName',
            key: 'catalogueName',
            width: '8%'
        }, {
            title: '值域',
            dataIndex: 'tagRange',
            key: 'tagRange',
            width: 120
        }, {
            title: '目标数据库',
            dataIndex: 'dataSourceName',
            key: 'dataSourceName',
            width: '8%'
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
            title: '来源表',
            dataIndex: 'originTable',
            key: 'originTable',
            width: '8%'
        }, {
            title: '来源列',
            dataIndex: 'originColumn',
            key: 'originColumn',
            width: '8%'
        }, {
            title: '状态',
            dataIndex: 'status',
            key: 'status',
            width: 100,
            render: (text: any) => {
                return TAG_STATUS[text];
            }
        }, {
            title: '发布状态',
            dataIndex: 'publishStatus',
            key: 'publishStatus',
            width: 90,
            render: (text: any) => {
                return TAG_PUBLISH_STATUS[text];
            }
        }, {
            title: '操作',
            width: 110,
            render: (text: any, record: any) => {
                const menu = (
                    <Menu>
                        <Menu.Item key="1">
                            <Link to={`/dl/manage/newApi/${record.id}`}>
                                发布
                            </Link>
                        </Menu.Item>
                        <Menu.Item key="2">
                            <Popconfirm
                                title="确定删除此标签？"
                                okText="确定" cancelText="取消"
                                onConfirm={this.removeTag.bind(this, record.id)}>
                                <a>删除</a>
                            </Popconfirm>
                        </Menu.Item>
                    </Menu>
                )

                const eleId = `tag_${record.id}`
                /* eslint-disable */
                return (
                    <div>
                        <a onClick={() => { this.editBaseInfo(record) }}>
                            编辑
                        </a>
                        <span className="ant-divider" id={eleId} />
                        {
                            TAG_PUBLISH_STATUS[record.publishStatus] == '已发布' ?
                                <Popconfirm
                                    title="确定删除此标签？"
                                    okText="确定" cancelText="取消"
                                    onConfirm={this.removeTag.bind(this, record.id)}>
                                    <a>删除</a>
                                </Popconfirm> :
                                <Dropdown
                                    getPopupContainer={
                                        () => document.getElementById(eleId)
                                    }
                                    overlay={menu}
                                    trigger={['click']}
                                >
                                    <a className="ant-dropdown-link">
                                        更多 <Icon type="down" />
                                    </a>
                                </Dropdown>
                        }
                    </div>
                )
            }
        }]
    }

    // 检查是否可发布
    checkPublish = (id: any) => {
        TCApi.checkPublish({ tagId: id }).then((res: any) => {
            if (res.code === 1 && res.data) {
                hashHistory.push(`/dl/manage/newApi/${id}`);
            }
        })
    }

    // 新增标签
    addTag = (id: any) => {
        this.openModal();
        this.setState({
            editData: {
                type: this.props.tagType === 'rule' ? 2 : 1
            }
        });
    }

    // 编辑标签基本信息
    editBaseInfo = (record: any) => {
        let editData: any = {
            ...record,
            catalogueId: this.getCatalogueArray(record.catalogueId)
        };

        this.openModal();
        this.setState({ editData });

        if (this.props.tagType === 'register') {
            if (TAG_PUBLISH_STATUS[editData.publishStatus] === '未发布') {
                this.props.getDataSourcesTable({
                    sourceId: editData.dataSourceId
                });
                this.props.getDataSourcesColumn({
                    sourceId: editData.dataSourceId,
                    tableName: editData.originTable
                });
            }
        }
    }

    // 删除标签
    removeTag = (id: any) => {
        const { queryParams } = this.state;

        if (id) {
            TCApi.deleteTag({ tagId: id }).then((res: any) => {
                if (res.code === 1) {
                    message.success('删除成功！');
                    this.getTagListData(queryParams);
                }
            });
        }
    }

    // 取消编辑
    cancel = (form: any) => {
        this.closeModal();
        form.resetFields();
    }

    openModal = () => {
        this.setState({ visible: true });
    }

    closeModal = () => {
        this.setState({ visible: false });
    }

    // 保存标签基本信息
    saveTag = (form: any) => {
        const { queryParams, editData } = this.state;

        form.validateFields((err: any, values: any) => {
            console.log(err, values)
            let api, params, msg;
            if (!err) {
                // 取子节点的id
                values.catalogueId = [...values.catalogueId].pop();

                if (editData.id) {
                    api = TCApi.updateTagBaseInfo;
                    params = { ...values, id: editData.id };
                    msg = '更新成功';
                } else {
                    api = this.props.tagType === 'rule' ? TCApi.addRuleTag : TCApi.addRegisterTag;
                    params = values;
                    msg = '新增成功';
                }

                api(params).then((res: any) => {
                    if (res.code === 1) {
                        message.success(msg);

                        this.closeModal();
                        form.resetFields();
                        this.getTagListData(queryParams);
                        this.setState({ editData: {} });
                    }
                });
            }
        });
    }

    // 一级分类筛选
    onFirstCatalogueChange = (id: any) => {
        let queryParams: any = {
            ...this.state.queryParams,
            currentPage: 1,
            pid: id ? id : undefined
        };

        if (id) {
            this.getSecondCatalogue(id)
        } else {
            this.setState({ catalogue2Data: [] });
        }

        this.getTagListData(queryParams);
        this.setState({ queryParams });
    }

    // 获取二级分类数据
    getSecondCatalogue = (id: any) => {
        const { apiCatalogue } = this.props.apiMarket;

        let child = apiCatalogue.filter((item: any) => item.id == id)[0].childCatalogue;
        child = child.some((item: any) => item.api) ? [] : child;

        this.setState({ catalogue2Data: child });
    }

    // 二级分类筛选
    onSecondCatalogueChange = (id: any) => {
        let queryParams: any = {
            ...this.state.queryParams,
            currentPage: 1,
            cid: id ? id : undefined
        };

        this.getTagListData(queryParams);
        this.setState({ queryParams });
    }

    // name筛选
    onTagNameSearch = (name: any) => {
        let queryParams: any = {
            ...this.state.queryParams,
            currentPage: 1,
            name: name ? name : undefined
        };

        this.getTagListData(queryParams);
        this.setState({ queryParams });
    }

    // 获取已选取的类目array
    getCatalogueArray = (value: any) => {
        const { apiCatalogue } = this.props.apiMarket;
        let arr: any = [];

        const flat = (data: any) => {
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
    onTableChange = (page: any, filter: any, sorter: any) => {
        let queryParams: any = {
            ...this.state.queryParams,
            currentPage: page.current,
        };

        this.getTagListData(queryParams);
        this.setState({ queryParams });
    }
    render () {
        const { apiCatalogue } = this.props.apiMarket;
        const { queryParams, visible, loading, tagList, editData, catalogue2Data } = this.state;

        let initColumns = this.props.tagType === 'rule' ? this.initRuleTagTable : this.initRegisterTagTable;

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
                            apiCatalogue.map((item: any) => {
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
                            catalogue2Data.map((item: any) => {
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
                <Link to="dl/tagConfig/identify">
                    <Button
                        type="primary"
                        style={{ margin: 10 }}>
                        识别列配置
                    </Button>
                </Link>
                <Button
                    type="primary"
                    onClick={this.addTag}>
                    新建标签
                </Button>
            </div>
        )

        const pagination: any = {
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
                    columns={initColumns()}
                    loading={loading}
                    pagination={pagination}
                    dataSource={tagList.data}
                    onChange={this.onTableChange}
                />

                <EditTagModal
                    visible={visible}
                    editData={editData}
                    saveTag={this.saveTag}
                    cancel={this.cancel}
                />
            </Card>
        )
    }
}
export default TagPane;
