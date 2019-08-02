import * as React from 'react'
import { Menu } from 'antd'
import { Link } from 'react-router'
import '../../styles/pages/dataManage.scss'
const SubMenu = Menu.SubMenu;
export default class Sidebar extends React.Component<any, any> {
    constructor (props: any) {
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
        console.log(routes);
        if (routes.length > 3) {
            let current = routes[3].path;

            if (current) {
                current = current.split('/')[0];
            }
            this.setState({ current: current || 'table' })
        }
    }

    handleClick = (e: any) => {
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
                    style={{ height: '100%' }}
                    selectedKeys={[this.state.current]}
                    defaultOpenKeys={['desensitization']}
                    defaultSelectedKeys={[this.state.current]}
                    mode={this.props.mode}
                    className="ele-padd_small"
                >
                    <Menu.Item key="assets">
                        <Link to={`${base}/assets`}>
                            <img src="/public/rdos/img/icon/icon-dataasset.svg" className='sidebar__icon' />
                            <span className="hide-text">数据资产</span>
                        </Link>
                    </Menu.Item>
                    <Menu.Item key="search">
                        <Link to={`${base}/search`}>
                            <img src="/public/rdos/img/icon/icon-searchdata.svg" className='sidebar__icon' />
                            <span className="hide-text">查找数据</span>
                        </Link>
                    </Menu.Item>
                    <Menu.Item key="table">
                        <Link to={`${base}/table`}>
                            <img src="/public/rdos/img/icon/icon-datasheet.svg" className='sidebar__icon' /><span className="hide-text">数据表管理</span>
                        </Link>
                    </Menu.Item>
                    <Menu.Item key="auth">
                        <Link to={`${base}/auth`}>
                            <img src="/public/rdos/img/icon/icon-access.svg" className='sidebar__icon' /><span className="hide-text">权限管理</span>
                        </Link>
                    </Menu.Item>
                    <Menu.Item key="catalogue">
                        <Link to={`${base}/catalogue`}>
                            <img src="/public/rdos/img/icon/icon-classity.svg" className='sidebar__icon' /><span className="hide-text">数据类目</span>
                        </Link>
                    </Menu.Item>
                    <SubMenu key="desensitization" style={{ height: '42px' }} title={<span><img src="/public/rdos/img/icon/icon-tuomin.svg" className='sidebar__icon' /><span className="hide-text">数据脱敏</span></span>}>
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
