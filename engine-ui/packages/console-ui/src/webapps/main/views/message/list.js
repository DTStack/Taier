import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { assign } from 'lodash'
import {
    Tabs, Menu, Table,
    Checkbox, Button, message
} from 'antd'

import { Link } from 'react-router'

import utils from 'utils'
import Api from '../../api'
import MsgStatus from './msgStatus'
import { MsgTypeDesc } from '../../components/display'

const TabPane = Tabs.TabPane;
const MenuItem = Menu.Item;

class MessageList extends Component {
    state = {

        selectedApp: '',
        table: {
            data: []
        },

        selectedRowKeys: [],
        selectedRows: [],
        selectedAll: false
    }

    componentDidMount () {
        const { apps } = this.props;
        const initialApp = utils.getParameterByName('app');
        const defaultApp = apps.find(app => app.default)

        if (defaultApp) {
            this.setState({
                selectedApp: initialApp || defaultApp.id
            }, this.loadMsg)
        }
    }

    loadMsg = (params) => {
        const { msgList } = this.props;
        const { selectedApp } = this.state;

        const reqParams = assign({
            currentPage: msgList.currentPage,
            pageSize: 10,
            mode: msgList.msgType
        }, params);

        Api.getMessage(selectedApp, reqParams).then(res => {
            this.setState({
                table: res.data
            })
        })
    }

    getUnreadRows = () => {
        const { selectedRows } = this.state;
        const ids = []

        selectedRows.forEach(item => {
            if (item.readStatus === 0) { // 获取未读数据
                ids.push(item.id)
            }
        })

        return ids
    }

    resetRowKeys = () => {
        this.setState({
            selectedRowKeys: [],
            selectedRows: [],
            selectedAll: false
        })
    }

    markAsRead = () => {
        const { selectedApp, selectedRowKeys } = this.state;

        const unReadRows = this.getUnreadRows()

        if (this.selectedNotNull(unReadRows)) {
            Api.markAsRead(selectedApp, {
                notifyRecordIds: unReadRows
            }).then(res => {
                if (res.code === 1) {
                    this.resetRowKeys();
                    this.loadMsg();
                }
            })
        }
    }

    markAsAllRead = () => {
        const { selectedApp, selectedRowKeys } = this.state;

        const unReadRows = this.getUnreadRows()

        Api.markAsAllRead(selectedApp, {
            notifyRecordIds: unReadRows
        }).then(res => {
            if (res.code === 1) {
                this.resetRowKeys();
                this.loadMsg();
            }
        })
    }

    deleteMsg = () => {
        const { selectedApp, selectedRowKeys } = this.state;

        if (this.selectedNotNull(selectedRowKeys)) {
            Api.deleteMsgs(selectedApp, {
                notifyRecordIds: selectedRowKeys
            }).then(res => {
                if (res.code === 1) {
                    this.loadMsg()
                    this.setState({
                        selectedAll: false,
                        selectedRowKeys: []
                    })
                }
            })
        }
    }

    handleTableChange = (pagination, filters) => {
        this.props.updateMsg({
            currentPage: pagination.current
        })
        this.setState({
            selectedRowKeys: [],
            selectedAll: false
        }, this.loadMsg)
    }

    selectedNotNull (selected) {
        if (!selected || selected.length <= 0) {
            message.error('请选择要操作的消息！')

            return false
        }

        return true
    }

    onPaneChange = (key) => {
        this.props.updateMsg({
            currentPage: 1,
            msgType: key
        });
        this.loadMsg({
            mode: key
        });
    }

    onAppSelect = ({ key }) => {
        this.props.updateMsg({
            currentPage: 1
        });
        this.setState({
            selectedApp: key,
            selectedRowKeys: []
        }, this.loadMsg)
    }

    onCheckAllChange = (e) => {
        const selectedRowKeys = []
        const selectedRows = []

        if (e.target.checked) {
            const data = this.state.table.data

            for (let i = 0; i < data.length; i++) {
                const item = data[i]

                selectedRowKeys.push(item.id)
                selectedRows.push(item)
            }
        }

        this.setState({
            selectedRowKeys,
            selectedRows,
            selectedAll: e.target.checked
        })
    }

    tableFooter = (currentPageData) => {
        const { msgList } = this.props
        const { selectedAll, table } = this.state
        const disabled = !table || !table.data || (table.data.length === 0);

        return (
            <tr className="ant-table-row  ant-table-row-level-0">
                <td style={{ padding: '0 24px' }}>
                    <Checkbox
                        checked={selectedAll}
                        disabled={disabled}
                        onChange={this.onCheckAllChange}></Checkbox>
                </td>
                <td>
                    {
                        <Button
                            size="small"
                            type="primary"
                            disabled={disabled}
                            onClick={this.deleteMsg}>
                            删除
                        </Button>
                    }
                    {
                        msgList.msgType !== '3' && <span>
                            <Button
                                size="small"
                                type="primary"
                                disabled={disabled}
                                onClick={this.markAsRead}>
                                标为已读
                            </Button>
                            <Button
                                disabled={disabled}
                                size="small"
                                type="primary"
                                onClick={this.markAsAllRead}>
                                全部已读
                            </Button>
                        </span>
                    }
                </td>
            </tr>
        )
    }

    renderPane = () => {
        const { apps, msgList } = this.props;
        const { table, selectedApp, selectedRowKeys } = this.state;
        const menuItem = []

        if (apps && apps.length > 0) {
            for (let i = 0; i < apps.length; i++) {
                const app = apps[i];

                if (app.enable && app.id !== 'main' && !app.disableExt && !app.disableMessage) {
                    menuItem.push(
                        <MenuItem app={app} key={app.id}>{app.name}</MenuItem>
                    )
                }
            }
        }

        const colms = [{
            title: '标题与内容',
            dataIndex: 'content',
            key: 'content',
            render (text, record) {
                return <Link to={`message/detail/${record.id}?app=${selectedApp}`}>
                    <MsgStatus value={record.readStatus} /> {text}
                </Link>
            }
        }, {
            width: 100,
            title: '状态',
            dataIndex: 'readStatus',
            key: 'readStatus',
            render (status) {
                let display = '未读'

                if (status === 1) { // 已读
                    display = '已读'
                }

                return display
            }
        }, {
            width: 150,
            title: '发送时间',
            dataIndex: 'gmtCreate',
            key: 'gmtCreate',
            render (text) {
                return utils.formatDateTime(text)
            }
        }, {
            width: 120,
            title: '类型描述',
            dataIndex: 'status',
            key: 'status',
            render (type) {
                return MsgTypeDesc(selectedApp, type)
            }
        }]

        const rowSelection = {
            selectedRowKeys,
            onChange: (selectedRowKeys, selectedRows) => {
                this.setState({
                    selectedRowKeys,
                    selectedRows
                })
            }
        };

        const pagination = {
            total: table && table.totalCount,
            defaultPageSize: 10,
            current: msgList.currentPage
        };

        return (
            <div className="m-panel" style={{ overflowY: 'auto', height: 'calc(100% - 80px)' }}>
                <Menu
                    selectedKeys={[selectedApp]}
                    onSelect={this.onAppSelect}
                    className="left">
                    {menuItem}
                </Menu>
                <div className="right panel-content">
                    <Table
                        rowKey="id"
                        className="m-table"
                        columns={colms}
                        dataSource={table.data || []}
                        rowSelection={rowSelection}
                        onChange={this.handleTableChange}
                        pagination={pagination}
                        footer={this.tableFooter}
                    // scroll={{ y: 400 }}
                    />
                </div>
            </div>
        )
    }

    render () {
        const { msgList } = this.props;

        const paneContent = this.renderPane();

        return (
            <div className="box-1 m-tabs" style={{ height: 'calc(100% - 40px)' }}>
                <Tabs
                    animated={false}
                    // style={{ overflowY: 'auto' }}
                    activeKey={msgList.msgType}
                    onChange={this.onPaneChange}
                >
                    <TabPane tab="全部消息" key="1"> {paneContent} </TabPane>
                    <TabPane tab="未读消息" key="2"> {paneContent} </TabPane>
                    <TabPane tab="已读消息" key="3"> {paneContent} </TabPane>
                </Tabs>
            </div>
        )
    }
}

export default MessageList
