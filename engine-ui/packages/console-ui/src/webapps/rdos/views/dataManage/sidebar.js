import React, { Component } from 'react'
import { Menu } from 'antd'
import { Link } from 'react-router'
import '../../styles/pages/dataManage.scss'
const SubMenu = Menu.SubMenu;
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
        console.log('---------------');
        console.log(routes);
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
                    defaultOpenKeys={['desensitization', 'rule-manage']}
                    defaultSelectedKeys={[this.state.current]}
                    mode="inline"
                >
                    <Menu.Item key="assets">
                        <Link to={`${base}/assets`}>
                            <img src="/public/rdos/img/icon/icon-dataasset.svg" className='sidebar__icon' />数据资产
                        </Link>
                    </Menu.Item>
                    <Menu.Item key="search">
                        <Link to={`${base}/search`}>
                            <img src="/public/rdos/img/icon/icon-searchdata.svg" className='sidebar__icon' />查找数据
                        </Link>
                    </Menu.Item>
                    <Menu.Item key="table">
                        <Link to={`${base}/table`}>
                            <img src="/public/rdos/img/icon/icon-datasheet.svg" className='sidebar__icon' />数据表管理
                        </Link>
                    </Menu.Item>
                    <Menu.Item key="auth">
                        <Link to={`${base}/auth`}>
                            <img src="/public/rdos/img/icon/icon-access.svg" className='sidebar__icon' />权限管理
                        </Link>
                    </Menu.Item>
                    <Menu.Item key="catalogue">
                        <Link to={`${base}/catalogue`}>
                            <img src="/public/rdos/img/icon/icon-classity.svg" className='sidebar__icon' />数据类目
                        </Link>
                    </Menu.Item>
                    <SubMenu key="desensitization" title={<span><img src="/public/rdos/img/icon/icon-tuomin.svg" className='sidebar__icon' /><span className="nav-text">数据脱敏</span></span>}>
                        <Menu.Item key="desensitization-manage">
                            <Link to={`${base}/desensitization-manage`}>
                                脱敏管理
                            </Link>
                        </Menu.Item>
                        <Menu.Item key="rule-manage">
                            <Link to={`${base}/rule-manage`}>
                                规则管理
                            </Link>
                        </Menu.Item>
                    </SubMenu>
                </Menu>
            </div>
        )
    }
}
