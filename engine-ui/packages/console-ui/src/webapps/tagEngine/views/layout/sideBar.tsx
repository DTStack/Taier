import * as React from 'react'
import { Menu } from 'antd'
import { Link } from 'react-router'

const { SubMenu } = Menu;
export default class Sidebar extends React.Component<any, any> {
    constructor (props: any) {
        super(props)
        this.state = {
            current: ''
        }
    }
    componentDidMount () {
        const { location, menuData } = this.props;
        const pathName = location.pathname;
        const currentRouter = menuData.filter(item => item.routers.includes(pathName));
        if (currentRouter[0]) {
            this.updateSelected(currentRouter[0].permissionUrl)
        } else {
            this.updateSelected(menuData[0] ? menuData[0].permissionUrl : '')
        }
    }
    componentDidUpdate (preProps) {
        const { location, menuData } = this.props;
        const pathName = location.pathname;
        const prePathName = preProps.location.pathname;
        if (prePathName != pathName) {
            const currentRouter = menuData.filter(item => item.routers.includes(pathName));
            if (currentRouter[0]) {
                this.updateSelected(currentRouter[0].permissionUrl)
            } else {
                this.updateSelected(menuData[0] ? menuData[0].permissionUrl : '')
            }
        }
    }
    updateSelected = (pathName: any) => {
        this.setState({
            current: pathName
        })
    }

    handleClick = (e: any) => {
        this.setState({
            current: e.key
        });
    }
    renderMenu = (data: any) => {
        return data.map((item: any) => {
            return item.children && item.children.length > 0 ? (
                <SubMenu key={item.permissionUrl} title={
                    <span>
                        <i className={`iconfont icon-svg ${item.permissionIcon}`} />
                        <span>{item.permissionName}</span>
                    </span>
                }
                >
                    {this.renderMenu(item.children)}
                </SubMenu>
            ) : (
                <Menu.Item key={item.permissionUrl}>
                    <Link to={item.permissionUrl}>
                        <i className={`iconfont icon-svg ${item.permissionIcon}`} />
                        <span className="hide-text">{item.permissionName}</span>
                    </Link>
                </Menu.Item>
            )
        })
    }
    render () {
        const { menuData } = this.props;
        return (
            <div className="sidebar m-ant-menu">
                <Menu
                    onClick={this.handleClick}
                    style={{ height: '100%' }}
                    selectedKeys={[this.state.current]}
                    defaultSelectedKeys={[this.state.current]}
                    mode={this.props.mode}
                >
                    {
                        this.renderMenu(menuData)
                    }
                </Menu>
            </div>
        )
    }
}
