import React, { Component } from 'react'
import { Menu, Icon } from 'antd'
import { Link } from 'react-router'

export default class Sidebar extends Component {
    constructor (props) {
        super(props)
        this.state = {
            current: 'table'
        }
    }

    componentDidMount () {
        this.updateSelected()
    }

    // eslint-disable-next-line
	UNSAFE_componentWillReceiveProps () {
        this.updateSelected()
    }

    updateSelected = () => {
        const routes = this.props.router.routes
        if (routes.length > 3) {
            let current = routes[3].path;

            if (current) {
                current = current.split('/')[0];
            }
            this.setState({ current: current || 'table' })
        }
    }

    handleClick = (e) => {
        this.setState({
            current: e.key
        });
    }

    render () {
        const base = `/data-manage`
        return (
            <div className="sidebar m-ant-menu">
                <Menu
                    onClick={this.handleClick}
                    style={{ width: 200, height: '100%' }}
                    selectedKeys={[this.state.current]}
                    defaultSelectedKeys={[this.state.current]}
                    mode="inline"
                >
                    <Menu.Item key="assets">
                        <Link to={`${base}/assets`}>
                            <Icon type="pie-chart" />数据资产
                        </Link>
                    </Menu.Item>
                    <Menu.Item key="search">
                        <Link to={`${base}/search`}>
                            <Icon type="search" />查找数据
                        </Link>
                    </Menu.Item>
                    <Menu.Item key="table">
                        <Link to={`${base}/table`}>
                            <Icon type="database" />数据表管理
                        </Link>
                    </Menu.Item>
                    <Menu.Item key="auth">
                        <Link to={`${base}/auth`}>
                            <Icon type="user" />权限管理
                        </Link>
                    </Menu.Item>
                    <Menu.Item key="catalogue">
                        <Link to={`${base}/catalogue`}>
                            <Icon type="book" />数据类目
                        </Link>
                    </Menu.Item>
                </Menu>
            </div>
        )
    }
}
