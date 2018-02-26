import React, { Component } from 'react'
import { Menu, Icon } from 'antd'
import { Link } from 'react-router'

export default class Sidebar extends Component {

    constructor(props) {
        super(props)
        this.state = {
            current: 'table',
        }
    }

    componentDidMount() {
        this.updateSelected()
    }

    componentWillReceiveProps() {
        this.updateSelected()
    }

    updateSelected = () => {
        const routes = this.props.router.routes
        if (routes.length > 2) {
            let current = routes[2].path;

            if(current) {
                current = current.split('/')[0];
            }
            this.setState({ current: current || 'table' })
        }
    }

    handleClick = (e) => {
        this.setState({
            current: e.key,
        });
    }

    render() {
        const props = this.props
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
                    <Menu.Item key="table">
                        <Link to={`${base}/table`}>
                            <Icon type="database" />表管理
                        </Link>
                    </Menu.Item>
                    <Menu.Item key="log">
                        <Link to={`${base}/log`}>
                            <Icon type="solution" />操作记录
                        </Link>
                    </Menu.Item>
                    <Menu.Item key="catalogue">
                        <Link to={`${base}/catalogue`}>
                            <Icon type="book" />数据类目
                        </Link>
                    </Menu.Item>
                    <Menu.Item key="dirty-data">
                        <Link to={`${base}/dirty-data`}>
                            <Icon type="book" />脏数据管理
                        </Link>
                    </Menu.Item>
                </Menu>
            </div>
        )
    }
}
