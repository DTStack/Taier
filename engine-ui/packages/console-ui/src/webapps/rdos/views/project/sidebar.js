import React, { Component } from 'react'
import { Menu } from 'antd'
import { Link } from 'react-router'

export default class Sidebar extends Component {

    constructor(props) {
        super(props)
        this.state = {
            current: 'config',
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
            const current = routes[2].path || 'config'
            this.setState({ current })
        }
    }

    handleClick = (e) => {
        this.setState({
            current: e.key,
        });
    }

    render() {
        const props = this.props
        const base = `/project/${props.params.pid}`
        return (
            <div className="sidebar my-ant-menu">
                <Menu
                  onClick={this.handleClick}
                  style={{ width: 200, height: '100%' }}
                  selectedKeys={[this.state.current]}
                  defaultSelectedKeys={[this.state.current]}
                  mode="inline"
                >
                    <Menu.Item key="config">
                        <Link to={`${base}/config`}>项目配置</Link>
                    </Menu.Item>
                    <Menu.Item key="member">
                        <Link to={`${base}/member`}>项目成员管理</Link>
                    </Menu.Item>
                    <Menu.Item key="role">
                        <Link to={`${base}/role`}>角色管理</Link>
                    </Menu.Item>
                </Menu>
            </div>
        )
    }
}
