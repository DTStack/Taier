import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { Tabs, Menu, Table } from 'antd'
import { Link } from 'react-router'

import { tabBarStyle } from '../../consts'

import DataSouceApi from 'dataQuality/api/dataSource'

const TabPane = Tabs.TabPane;
const MenuItem = Menu.Item;

class MessageList extends Component {

    state = {
        active: 'all',
        data: '',
    }

    componentDidMount() {
        this.loadMsg();
    }

    loadMsg = () => {
        DataSouceApi.getDataSources().then(res => {
            this.setState({
                data: res.data,
            })
        })
    }

    onPaneChange = (key) => {
        this.setState({
            active: key,
        })
    }

    renderPane = () => {
        const { apps } = this.props;
        const { data } = this.state;
        const menuItem = []
        let defaultKey = '';

        if (apps && apps.length > 0) {
            defaultKey = apps[0].id
            for (var i = 0; i < apps.length; i++) {
                const app = apps[i];
                if (app.enable) {
                    menuItem.push(
                        <MenuItem app={app} key={app.id}>{app.name}</MenuItem>
                    )
                }
            }
        }

        const colms = [{
            title: '标题与内容',
            dataIndex: 'dataName',
            key: 'name',
            render(text, record) {
                return <Link to={`message/detail/${record.id}`}>{text}</Link>
            },
        }, {
            title: '发送时间',
            dataIndex: 'age',
            key: 'age',
        }, {
            title: '类型',
            dataIndex: 'address',
            key: 'address',
        }]

        const rowSelection = {
            onChange: (selectedRowKeys, selectedRows) => {
                console.log(`selectedRowKeys: ${selectedRowKeys}`, 'selectedRows: ', selectedRows);
            },
            getCheckboxProps: record => ({
                disabled: record.name === 'Disabled User', // Column configuration not to be checked
            }),
        };

        return (
            <div className="m-panel">
                <Menu 
                    defaultOpenKeys={[defaultKey]}
                    className="left">
                    {menuItem}
                </Menu>
                <main className="right panel-content">
                    <Table 
                        rowKey="id"
                        className="m-table"
                        columns={colms} 
                        dataSource={ data ? data.data : [] } 
                        rowSelection={rowSelection} 
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
                    tabBarStyle={tabBarStyle} 
                    activeKey={this.state.active} 
                    onChange={this.onPaneChange}
                >
                    <TabPane tab="全部消息" key="all"> {paneContent} </TabPane>
                    <TabPane tab="未读消息" key="unread"> {paneContent} </TabPane>
                    <TabPane tab="已读消息" key="read"> {paneContent} </TabPane>
                </Tabs>
            </div>
        )
    }
}

export default MessageList