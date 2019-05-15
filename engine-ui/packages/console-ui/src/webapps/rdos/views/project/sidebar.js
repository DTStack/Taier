import React, { Component } from 'react'
import { Menu } from 'antd'
import { Link } from 'react-router'
require('../iconfont/iconfont');
export default class Sidebar extends Component {
    constructor (props) {
        super(props)
        this.state = {
            current: 'config'
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
            const current = routes[3].path || 'config'
            this.setState({ current })
        }
    }

    handleClick = (e) => {
        this.setState({
            current: e.key
        });
    }

    render () {
        const props = this.props
        const base = `/project/${props.params.pid}`
        return (
            <div className="sidebar m-ant-menu">
                <Menu
                    onClick={this.handleClick}
                    style={{ width: 200, height: '100%' }}
                    selectedKeys={[this.state.current]}
                    defaultSelectedKeys={[this.state.current]}
                    mode="inline"
                >
                    <Menu.Item key="config">
                        <Link to={`${base}/config`}>
                            <svg className="icon-svg" aria-hidden="true">
                                <use xlinkHref="#icon-project_set"></use>
                            </svg>
                            <span>项目配置</span>
                        </Link>
                    </Menu.Item>
                    <Menu.Item key="engine">
                        <Link to={`${base}/config`}>
                            <svg className="icon-svg" aria-hidden="true">
                                <use xlinkHref="#icon-project_set"></use>
                            </svg>
                            <span>计算引擎</span>
                        </Link>
                    </Menu.Item>
                    <Menu.Item key="member">
                        <Link to={`${base}/member`}>
                            <svg className="icon-svg" aria-hidden="true">
                                <use xlinkHref="#icon-chengyuan"></use>
                            </svg>
                            <span>项目成员管理</span>
                        </Link>
                    </Menu.Item>
                    <Menu.Item key="role">
                        <Link to={`${base}/role`}>
                            <svg className="icon-svg" aria-hidden="true">
                                <use xlinkHref="#icon-jiaoseguanli"></use>
                            </svg>
                            <span>角色管理</span>
                        </Link>
                    </Menu.Item>
                </Menu>
            </div>
        )
    }
}
