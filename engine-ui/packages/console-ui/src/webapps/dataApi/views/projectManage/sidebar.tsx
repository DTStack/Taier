import * as React from 'react'
import { Menu } from 'antd'
import { Link } from 'react-router'
require('public/iconfont/iconfont.js');
export default class Sidebar extends React.Component<any, any> {
    constructor (props: any) {
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
        const routes = this.props.router.routes;
        if (routes.length > 3) {
            const isRoleDynaRouter: boolean = routes[3].path == 'role/edit/:roleId';
            const current = isRoleDynaRouter ? 'role' : routes[3].path || 'config';
            this.setState({ current })
        }
    }

    handleClick = (e: any) => {
        this.setState({
            current: e.key
        });
    }

    render () {
        const props = this.props;
        const base = `/api/project/${props.params.pid}`;
        return (
            <div className="sidebar m-ant-menu">
                <Menu
                    onClick={this.handleClick}
                    style={{ height: '100%' }}
                    selectedKeys={[this.state.current]}
                    defaultSelectedKeys={[this.state.current]}
                    mode={this.props.mode}
                >

                    <Menu.Item key="config">
                        <Link to={`${base}/config`}>
                            <svg className="icon-svg" aria-hidden="true">
                                <use xlinkHref="#icon-project_set"></use>
                            </svg>
                            <span className="hide-text">项目配置</span>
                        </Link>
                    </Menu.Item>
                    <Menu.Item key="member">
                        <Link to={`${base}/member`}>
                            <svg className="icon-svg" aria-hidden="true">
                                <use xlinkHref="#icon-chengyuan"></use>
                            </svg>
                            <span className="hide-text">项目成员管理</span>
                        </Link>
                    </Menu.Item>
                    <Menu.Item key="role">
                        <Link to={`${base}/role`}>
                            <svg className="icon-svg" aria-hidden="true">
                                <use xlinkHref="#icon-jiaoseguanli"></use>
                            </svg>
                            <span className="hide-text">角色管理</span>
                        </Link>
                    </Menu.Item>
                </Menu>
            </div>
        )
    }
}
