import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { 
    Tabs, Menu, Table, 
    Checkbox, Button, message 
} from 'antd'

import { Link } from 'react-router'

import utils from 'utils'
import Api from '../../api'
import MsgStatus from './msgStatus'

const TabPane = Tabs.TabPane;
const MenuItem = Menu.Item;

class MessageList extends Component {

    state = {
        active: '1',
        selectedApp: '',
        table: {
            data: [],
        },

        selectedRowKeys: [],
        selectedRows: [],
        selectedAll: false,
    }

    componentDidMount() {
        const { apps } = this.props;
        const defaultApp = apps.find(app => app.default)
        if (defaultApp) {
            this.setState({
                selectedApp: defaultApp.id
            }, this.loadMsg)
        }
    }

    loadMsg = () => {
        const { active, selectedApp } = this.state;
        Api.getMessage(selectedApp, {
            currentPage: 1, 
            pageSize: 20,
            mode: active,
        }).then(res => {
            this.setState({
                table: res.data,
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
            selectedAll: false,
        })
    }

    markAsRead = () => {
        const { active, selectedApp, selectedRowKeys } = this.state;

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
        const { active, selectedApp, selectedRowKeys } = this.state;
        
        const unReadRows = this.getUnreadRows()

        if (this.selectedNotNull(unReadRows)) {

            Api.markAsAllRead(selectedApp, {
                notifyRecordIds: unReadRows
            }).then(res => {
                if (res.code === 1) {
                    this.resetRowKeys();
                    this.loadMsg();
                }
            })
        }
    }

    deleteMsg = () => {
        const { active, selectedApp, selectedRowKeys } = this.state;
        if (this.selectedNotNull(selectedRowKeys)) {
            Api.deleteMsgs(selectedApp, {
                notifyRecordIds: selectedRowKeys
            }).then(res => {
                if (res.code === 1) {
                    this.loadMsg()
                    this.setState({
                        selectedRowKeys: [],
                    })
                }
            })
        }
    }

    selectedNotNull(selected) {
        if (!selected || selected.length <= 0) {
            message.error('请选择要操作的消息！')
            return false
        }
        return true
    }

    onPaneChange = (key) => {
        this.setState({
            active: key,
        }, this.loadMsg)
    }

    onAppSelect = ({ key }) => {
        this.setState({
            selectedApp: key,
            selectedRowKeys: [],
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
            selectedAll: e.target.checked,
        })
    }

    tableFooter = (currentPageData) => {
        const { active, selectedAll } = this.state
        return (
            <tr className="ant-table-row  ant-table-row-level-0">
                <td style={{ padding: '0 24px' }}>
                    <Checkbox 
                        checked={selectedAll}
                        onChange={this.onCheckAllChange}></Checkbox>
                </td>
                <td>
                    <Button 
                        size="small"
                        type="primary" 
                        onClick={this.deleteMsg}>
                        删除
                    </Button>
                    {
                        active !== '3' && <span>
                            <Button 
                                size="small"
                                type="primary" 
                                onClick={this.markAsRead}>
                                标为已读
                            </Button>
                            <Button 
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

        const { apps } = this.props;
        const { table, selectedApp, selectedRowKeys } = this.state;
        const menuItem = []

        if (apps && apps.length > 0) {
            for (var i = 0; i < apps.length; i++) {
                const app = apps[i];
                if (app.enable && app.id !== 'main') {
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
            render(text, record) {
                return <Link to={`message/detail/${record.id}?app=${selectedApp}`}>
                    <MsgStatus value={record.readStatus}/> {text}
                </Link>
            },
        }, {
            title: '状态',
            dataIndex: 'readStatus',
            key: 'readStatus',
            render(status) {
                let display = '未读'
                if (status === 1) {// 已读
                    display = '已读'
                }
                return display
            }
        }, {
            title: '发送时间',
            dataIndex: 'gmtCreate',
            key: 'gmtCreate',
            render(text) {
                return utils.formatDateTime(text)
            }
        }, {
            title: '类型',
            dataIndex: 'triggerType',
            key: 'triggerType',
            render(type) {
                return type
            }
        }]

        const rowSelection = {
            selectedRowKeys,
            onChange: (selectedRowKeys, selectedRows) => {
                this.setState({
                    selectedRowKeys,
                    selectedRows,
                })
            },
        };

        return (
            <div className="m-panel">
                <Menu 
                    selectedKeys={[ selectedApp ]}
                    onSelect={this.onAppSelect}
                    className="left">
                    {menuItem}
                </Menu>
                <main className="right panel-content">
                    <Table 
                        rowKey="id"
                        className="m-table"
                        columns={colms} 
                        dataSource={ table.data || [] } 
                        rowSelection={rowSelection} 
                        footer={this.tableFooter}
                    />
                </main>
            </div>
        )
    }

    render() {

        const paneContent = this.renderPane();

        return (
            <div className="box-1 m-tabs" style={{height: '785px'}}>
                <Tabs 
                    animated={false}
                    activeKey={this.state.active} 
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