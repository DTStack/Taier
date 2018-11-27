import React from 'react';
import {
    Card, Table, Form, Select,
    DatePicker, Input, Radio, Pagination,
    Button, Icon, Checkbox
} from 'antd';
import moment from 'moment';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux'
import utils from 'utils';
import { cloneDeep } from 'lodash';

import Api from '../../../../api'
import { publishType, TASK_TYPE, RESOURCE_TYPE_MAP, PROJECT_TYPE } from '../../../../comm/const'
import { RDOS_ROLE } from 'main/consts';
import { getTaskTypes } from '../../../../store/modules/offlineTask/comm';
import AddLinkModal from './addLinkModal'
import PublishModal from '../publish/publishModal'

const { RangePicker } = DatePicker;
const FormItem = Form.Item;
const RadioGroup = Radio.Group;
const Option = Select.Option;
const Search = Input.Search;

@connect(state => {
    return {
        project: state.project,
        taskTypes: {
            offline: state.offlineTask.comm.taskTypes
        },
        user: state.user
    }
}, dispatch => {
    return bindActionCreators({
        getTaskTypes
    }, dispatch);
})
class PackageCreate extends React.Component {
    state = {
        addLinkVisible: false,
        createModalVisible: false,
        tableParams: {
            filter: {},
            sorter: {},
            pagination: {
                current: 1,
                pageSize: 20,
                total: 0
            }
        },
        pagination: {
            current: 1,
            pageSize: 8,
            total: 0
        },
        listType: publishType.TASK,
        modifyUser: undefined,
        publishName: undefined,
        modifyDate: [moment().subtract(30, 'days'), new moment()],
        packageList: [],
        selectedRowKeys: [],
        selectedRows: [],
        addLinkModalData: {},
        users: []
    }

    componentDidMount () {
        this.initComponent();
    }
    initComponent () {
        this.setState({
            selectedRowKeys: [],
            selectedRows: [],
            addLinkModalData: {}
        })
        this.props.getTaskTypes();
        this.getTaskList();
        this.getUsers();
    }
    componentWillReceiveProps (nextProps) {
        const { project = {} } = nextProps;
        const { project: old_project = {} } = this.props;
        if (old_project.id != project.id && project.projectType == PROJECT_TYPE.TEST) {
            setTimeout(() => {
                this.initComponent();
            }, 100)
        }
    }
    getUsers () {
        const { project } = this.props;
        if (!project.id) {
            return;
        }
        Api.getProjectUsers({
            projectId: project.id,
            currentPage: 1,
            pageSize: 99999
        }).then((res) => {
            if (res.code === 1) {
                this.setState({ users: res.data.data })
            }
        })
    }
    getTaskList () {
        const { mode } = this.props;
        const {
            listType, tableParams,
            modifyDate, modifyUser, publishName
        } = this.state;
        const pagination = tableParams.pagination;
        const sorter = tableParams.sorter;

        let extParams = {};
        if (sorter && sorter.columnKey) {
            extParams.sort = (sorter.order === 'descend') ? 'desc' : 'asc';
        }
        extParams['startTime'] = (modifyDate && modifyDate.length) ? modifyDate[0].valueOf() : null;
        extParams['endTime'] = (modifyDate && modifyDate.length) ? modifyDate[1].valueOf() : null;
        switch (listType) {
        case publishType.TASK: {
            extParams['taskName'] = publishName;
            extParams['taskModifyUserId'] = modifyUser;
            break;
        }
        case publishType.FUNCTION: {
            extParams['name'] = publishName;
            extParams['functionModifyUserId'] = modifyUser;
            break;
        }
        case publishType.RESOURCE: {
            extParams['resourceName'] = publishName;
            extParams['resourceModifyUserId'] = modifyUser;
            break;
        }
        case publishType.TABLE: {
            extParams['tableName'] = publishName;
            extParams['tableModifyUserId'] = modifyUser;
            break;
        }
        }
        Api.getRePublishList({
            pageSize: pagination.pageSize,
            pageIndex: pagination.current,
            ...extParams
        }, mode, listType)
            .then(
                (res) => {
                    if (res.code == 1) {
                        this.setState({
                            packageList: res.data.data || [],
                            tableParams: {
                                ...tableParams,
                                pagination: {
                                    ...tableParams.pagination,
                                    total: res.data.totalCount
                                }
                            }
                        })
                    }
                }
            )
    }
    changeRightPage (page, pageSize) {
        this.setState({
            pagination: {
                current: page,
                pageSize: pageSize
            }
        })
    }
    reloadRightPage () {
        const { pagination, selectedRows } = this.state;
        const { current, pageSize } = pagination;
        const count = selectedRows.length;
        if (count <= (current - 1) * pageSize) {
            this.setState({
                pagination: {
                    current: Math.max(Math.ceil(count / pageSize), 1),
                    pageSize,
                    total: 0
                }
            })
        }
    }
    onTableChange (pagination, filters, sorter) {
        this.setState({
            tableParams: {
                pagination,
                filters,
                sorter
            }
        }, this.getTaskList)
    }
    showCreateModal () {
        this.setState({
            createModalVisible: true
        })
    }
    hideCreateModal (doJump) {
        this.setState({
            createModalVisible: false
        })
        if (doJump && typeof doJump == 'boolean') {
            this.props.changeTab('publish');
            this.setState({
                selectedRowKeys: [],
                selectedRows: [],
                addLinkModalData: {}
            })
        }
    }
    showAddLink (record) {
        this.setState({
            addLinkModalData: record,
            addLinkVisible: true
        })
    }
    initColumns () {
        const { listType, users } = this.state;
        const { taskTypes, user } = this.props;
        const offlineTaskTypes = taskTypes.offline;
        const offlineTaskTypesMap = new Map(offlineTaskTypes.map((item) => { return [item.key, item.value] }));
        /**
         * 这边判断该用户是否具有打包权限
         * 目前只有访客无打包权限
         */
        const mine = users.find((userItem) => { return userItem.userId == user.id }) || {};
        const myRoles = mine.roles || [];
        let havePermission = false
        for (let i = 0; i < myRoles.length; i++) {
            let role = myRoles[i];
            if (role.roleValue != RDOS_ROLE.VISITOR) {
                havePermission = true;
                break;
            }
        }
        const addButtonCreate = (record) => {
            return (this.isSelect(record)
                ? <a disabled={!havePermission} onClick={this.removeItem.bind(this, listType, record.id)} style={{ color: '#888' }}>取消</a>
                : <a disabled={!havePermission} onClick={this.addNewItem.bind(this, listType, [record], [])}>添加</a>);
        }
        const publishButtonCreate = (record) => {
            return (
                <a
                    disabled={!havePermission}
                    onClick={
                        () => {
                            this.clearSelect();
                            setTimeout(
                                () => {
                                    this.addNewItem(listType, [record], []);
                                    this.showCreateModal();
                                })
                        }
                    }>打包</a>
            )
        };

        switch (listType) {
        case publishType.TASK: {
            return [{
                title: '名称',
                dataIndex: 'taskName',
                render (text, record) {
                    let extText = '';
                    let extStyle = {};
                    if (record.isDeleted) {
                        extText = '[已删除]'
                        extStyle['color'] = '#999'
                    }
                    return <span style={extStyle}>
                        {`${extText}${text}(${offlineTaskTypesMap.get(record.taskType)})`}
                    </span>
                }
            }, {
                title: '负责人',
                dataIndex: 'chargeUser',
                width: '130px'
            }, {
                title: '修改人',
                dataIndex: 'modifyUser',
                width: '130px'
            }, {
                title: '修改时间',
                dataIndex: 'modifyTime',
                sorter: true,
                render (text) {
                    return utils.formatDateTime(text)
                },
                width: '140px'
            }, {
                title: '备注',
                dataIndex: 'taskDesc',
                width: '140px'
            }, {
                title: '操作',
                dataIndex: 'deal',
                width: '180px',
                render: (n, record) => {
                    return <span>
                        {addButtonCreate(record)}
                        <span className="ant-divider"></span>
                        <a disabled={!havePermission} onClick={this.showAddLink.bind(this, record)}>添加关联</a>
                        <span className="ant-divider"></span>
                        {publishButtonCreate(record)}
                    </span>
                }
            }]
        }
        case publishType.RESOURCE: {
            return [{
                title: '名称',
                dataIndex: 'resourceName',
                render (text, record) {
                    return `${text}(${RESOURCE_TYPE_MAP[record.resourceType]})`
                }
            }, {
                title: '创建人',
                dataIndex: 'createUser',
                render (createUser) {
                    return createUser.userName
                },
                width: '150px'
            }, {
                title: '修改人',
                dataIndex: 'modifyUser',
                render (n, record) {
                    return record.createUser.userName
                },
                width: '150px'
            }, {
                title: '修改时间',
                dataIndex: 'gmtModified',
                sorter: true,
                render (text) {
                    return utils.formatDateTime(text)
                },
                width: '160px'
            }, {
                title: '操作',
                dataIndex: 'deal',
                width: '170px',
                render: (n, record) => {
                    return <span>
                        {addButtonCreate(record)}
                        <span className="ant-divider"></span>
                        {publishButtonCreate(record)}
                    </span>
                }
            }]
        }
        case publishType.FUNCTION: {
            return [{
                title: '名称',
                dataIndex: 'name'
            }, {
                title: '创建人',
                dataIndex: 'createUser',
                render (createUser) {
                    return createUser.userName
                },
                width: '150px'
            }, {
                title: '修改人',
                dataIndex: 'modifyUser',
                render (modifyUser) {
                    return modifyUser.userName
                },
                width: '150px'
            }, {
                title: '修改时间',
                dataIndex: 'gmtModified',
                sorter: true,
                render (text) {
                    return utils.formatDateTime(text)
                },
                width: '160px'
            }, {
                title: '操作',
                dataIndex: 'deal',
                width: '170px',
                render: (n, record) => {
                    return <span>
                        {addButtonCreate(record)}
                        <span className="ant-divider"></span>
                        {publishButtonCreate(record)}
                    </span>
                }
            }]
        }
        case publishType.TABLE: {
            return [{
                title: '名称',
                dataIndex: 'tableName'

            }, {
                title: '负责人',
                dataIndex: 'chargeUser',
                width: '150px'
            }, {
                title: '修改人',
                dataIndex: 'modifyUser',
                width: '150px'
            }, {
                title: '修改时间',
                dataIndex: 'modifyTime',
                sorter: true,
                render (text) {
                    return utils.formatDateTime(text)
                },
                width: '160px'
            }, {
                title: '操作',
                dataIndex: 'deal',
                width: '170px',
                render: (n, record) => {
                    return <span>
                        {addButtonCreate(record)}
                        <span className="ant-divider"></span>
                        {publishButtonCreate(record)}
                    </span>
                }
            }]
        }
        }
    }

    disabledDate (currentDate) {
        const now = new moment();
        if (currentDate > now) {
            return true
        }
        return false;
    }

    dateChange (key, dates) {
        this.setState({
            [key]: dates
        }, this.getTaskList)
    }

    selectChange (key, value) {
        if (key == 'listType') {
            value = value.target.value
            this.setState({
                tableParams: {
                    filter: {},
                    sorter: {},
                    pagination: {
                        current: 1,
                        pageSize: 20,
                        total: 0
                    }
                },
                packageList: []
            })
        }
        this.setState({
            [key]: value
        }, this.getTaskList)
    }
    itemCreate (listType, row) {
        const baseItem = {
            itemId: row.id,
            itemType: listType,
            publishParamJson: {
                updateEnvParam: false
            },
            data: row,
            modifyTime: row.modifyTime
        }
        switch (listType) {
        case publishType.TASK: {
            baseItem.itemName = row.taskName;
            baseItem.createUser = row.createUser;
            baseItem.modifyUser = row.modifyUser;
            baseItem.chargeUser = row.chargeUser;
            baseItem.itemInnerType = row.taskType;
            break;
        }
        case publishType.RESOURCE: {
            baseItem.itemName = row.resourceName;
            baseItem.createUser = row.createUser.userName;
            baseItem.modifyUser = row.modifyUser.userName;
            baseItem.chargeUser = row.chargeUser;
            baseItem.itemInnerType = row.resourceType;
            baseItem.modifyTime = row.gmtModified;
            break;
        }
        case publishType.FUNCTION: {
            baseItem.itemName = row.name;
            baseItem.createUser = row.createUser.userName;
            baseItem.modifyUser = row.modifyUser.userName;
            baseItem.chargeUser = row.chargeUser;
            baseItem.itemInnerType = row.type;
            baseItem.modifyTime = row.gmtModified;
            break;
        }
        case publishType.TABLE: {
            baseItem.itemName = row.tableName;
            baseItem.createUser = row.createUser;
            baseItem.modifyUser = row.modifyUser;
            baseItem.chargeUser = row.chargeUser;
            break;
        }
        }
        return baseItem;
    }
    isSelect (record) {
        const { selectedRows, listType } = this.state;
        const keys = selectedRows.filter(
            (item) => {
                return item.itemType == listType;
            }
        ).map(
            (item) => {
                return item.itemId
            }
        )
        return keys.includes(record.id)
    }
    clearSelect () {
        this.setState({
            selectedRows: []
        }, this.reloadRightPage)
    }
    addNewItem (listType, newItems, packageList) {
        packageList = packageList || [];
        let { selectedRows } = this.state;
        let addArr = [];
        const keys = selectedRows.filter(
            (item) => {
                return item.itemType == listType;
            }
        ).map(
            (item) => {
                return item.itemId
            }
        )
        for (let i = 0; i < newItems.length; i++) {
            let item = newItems[i];
            if (!keys.includes(item.id)) {
                addArr.push(this.itemCreate(listType, item))
            }
        }
        const newItemKeys = newItems.map((item) => { return item.id });
        const packageListKeys = packageList.map((item) => { return item.id });
        if (packageListKeys.length) {
            selectedRows = selectedRows.filter(
                (row) => {
                    const inPackageList = packageListKeys.includes(row.itemId);
                    const inNewItem = newItemKeys.includes(row.itemId);
                    return inNewItem || !inPackageList;
                }
            )
        }
        this.setState({
            selectedRows: selectedRows.concat(addArr)
        }, this.reloadRightPage)
    }
    removeItem (listType, id) {
        const { selectedRows } = this.state;
        let newRows = [];
        newRows = selectedRows.filter(
            (item) => {
                return !(item.itemType == listType && item.itemId == id);
            }
        )
        this.setState({
            selectedRows: newRows
        }, this.reloadRightPage)
    }
    changeEnv (listType, id, e) {
        const { selectedRows } = this.state;
        let newRows = cloneDeep(selectedRows);
        newRows = selectedRows.map(
            (item) => {
                if (item.itemType == listType && item.itemId == id) {
                    item.publishParamJson.updateEnvParam = e.target.checked;
                }
                return item;
            }
        )
        this.setState({
            selectedRows: newRows
        })
    }
    rowSelection () {
        const { selectedRows, listType, packageList } = this.state;
        return {
            onChange: (selectedRowKeys, selectedRows) => {
                this.addNewItem(listType, selectedRows, packageList)
            },
            selectedRowKeys: selectedRows.map(
                (row) => {
                    return row.itemType == listType ? row.itemId : null
                }
            ).filter(Boolean)
        }
    }
    renderRightItem () {
        const { selectedRows, listType, pagination } = this.state;
        const { current, pageSize } = pagination;
        const { taskTypes } = this.props;
        const offlineTaskTypes = taskTypes.offline;
        const offlineTaskTypesMap = new Map(offlineTaskTypes.map((item) => { return [item.key, item.value] }));
        return selectedRows.filter(
            (item, index) => {
                return (index + 1) <= pageSize * current && (index + 1) > pageSize * (current - 1);
            }
        ).map(
            (row) => {
                const showEnvCheckbox = row.itemType == publishType.TASK && row.data.taskType != TASK_TYPE.SYNC;
                let nameText;
                let extMsg = '';
                switch (row.itemType) {
                case publishType.TASK: {
                    nameText = '任务'
                    extMsg = `(${offlineTaskTypesMap.get(row.data.taskType)})`
                    break;
                }
                case publishType.FUNCTION: {
                    nameText = '函数'
                    break;
                }
                case publishType.RESOURCE: {
                    nameText = '资源'
                    extMsg = `(${RESOURCE_TYPE_MAP[row.data.resourceType]})`
                    break;
                }
                case publishType.TABLE: {
                    nameText = '建表'
                    break;
                }
                }
                return <div key={`${row.itemType}%${row.itemId}`} className="item">
                    <Icon className="close" type="close" onClick={this.removeItem.bind(this, row.itemType, row.itemId)} />
                    <p><span className="item-title">{nameText}：</span>{row.itemName} {extMsg}</p>
                    {row.chargeUser && (
                        <p><span className="item-title">负责人：</span>{row.chargeUser}</p>
                    )}
                    <p><span className="item-title">修改人：</span>{row.modifyUser}</p>
                    {showEnvCheckbox && <Checkbox onChange={this.changeEnv.bind(this, row.itemType, row.itemId)} checked={row.publishParamJson.updateEnvParam} >更新环境参数</Checkbox>}
                </div>
            }
        )
    }
    render () {
        const {
            packageList, tableParams, addLinkVisible, createModalVisible,
            pagination, selectedRows,
            listType, modifyUser, publishName, modifyDate,
            addLinkModalData, users
        } = this.state;
        const { mode, project } = this.props;
        return (
            <div className="package-create-box">
                <div className="table-box">
                    <div className="table-header">
                        <div className="header-item">
                            <span className="title">修改人：</span>
                            <Select allowClear className="item" size="default" value={modifyUser} onChange={this.selectChange.bind(this, 'modifyUser')} >
                                {users.map(
                                    (user) => {
                                        return <Option key={user.userId} value={user.userId}>{user.user.userName}</Option>
                                    }
                                )}
                            </Select>
                        </div>
                        <div className="header-item">
                            <span className="title">对象类型：</span>
                            <RadioGroup value={listType} onChange={this.selectChange.bind(this, 'listType')}>
                                <Radio value={publishType.TASK}>任务</Radio>
                                <Radio value={publishType.RESOURCE}>资源</Radio>
                                <Radio value={publishType.FUNCTION}>函数</Radio>
                                {mode == 'offline' && <Radio value={publishType.TABLE}>表</Radio>}
                            </RadioGroup>
                        </div>
                        <div className="header-item">
                            <span className="title">修改日期：</span>
                            <RangePicker value={modifyDate} className="item" onChange={this.dateChange.bind(this, 'modifyDate')} disabledDate={this.disabledDate} size="default" />
                        </div>
                        <div className="header-item">
                            <span className="title">发布对象：</span>
                            <Search
                                value={publishName}
                                className="item"
                                size="default"
                                placeholder="搜索发布对象名"
                                onChange={(e) => { this.setState({ publishName: e.target.value }) }}
                                onSearch={this.selectChange.bind(this, 'publishName')}
                            />
                        </div>
                    </div>
                    <Table
                        rowKey="id"
                        className="m-table select-all-table"
                        columns={this.initColumns()}
                        pagination={tableParams.pagination}
                        dataSource={packageList}
                        onChange={this.onTableChange.bind(this)}
                        rowSelection={this.rowSelection()}
                        scroll={{ y: 600 }}
                    />
                </div>
                <div className="tool-box">
                    <div className="box-border">
                        <div className="title">
                            发布到目标项目：{project.produceProject}
                        </div>
                        <div className="tool-top">
                            待打包对象 <span className="publish-num">{selectedRows.length}</span>
                            <Button disabled={selectedRows.length == 0} onClick={this.showCreateModal.bind(this)} type="primary" className="pack">打包</Button>
                        </div>
                        <div className="main">
                            {this.renderRightItem()}
                        </div>
                        <div className="tool-bottom">
                            <Button onClick={this.clearSelect.bind(this)} className="clear" size="small">清空</Button>
                            <div className="pagn">
                                <Pagination
                                    simple
                                    onChange={this.changeRightPage.bind(this)}
                                    size="small"
                                    {...pagination}
                                    total={selectedRows.length} />
                            </div>
                        </div>
                    </div>
                </div>
                <AddLinkModal
                    addNewItem={this.addNewItem.bind(this)}
                    selectedRows={selectedRows}
                    onCancel={() => { this.setState({ addLinkVisible: false }) }}
                    onOk={() => { this.setState({ addLinkVisible: false }) }}
                    mode={mode}
                    visible={addLinkVisible}
                    data={addLinkModalData}
                />
                <PublishModal
                    isPublish={false}
                    visible={createModalVisible}
                    onCancel={this.hideCreateModal.bind(this, false)}
                    onOk={this.hideCreateModal.bind(this, true)}
                    data={{
                        items: selectedRows
                    }}
                />
            </div>
        )
    }
}

export default PackageCreate;
