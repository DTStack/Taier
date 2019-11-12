import * as React from 'react'
import { Menu } from 'antd'
import { Link } from 'react-router'
import SideBarData from '../../consts/sideBarData';
require('public/iconfont/iconfont.js');

const { SubMenu } = Menu;
export default class Sidebar extends React.Component<any, any> {
    constructor (props: any) {
        super(props)
        this.state = {
            current: '',
            menuData: []
        }
    }

    componentDidMount () {
        const { location } = this.props;
        const pathName = location.pathname;
        if (SideBarData.hasOwnProperty(pathName)) {
            this.updateSelected(pathName)
        }
    }
    componentDidUpdate (preProps: any) {
        const { location } = this.props;
        const pathName = location.pathname;
        const prePathName = preProps.location.pathname;
        if ((pathName != prePathName) && SideBarData.hasOwnProperty(pathName)) {
            this.updateSelected(pathName)
        }
    }
    updateSelected = (pathName: any) => {
        this.setState({
            menuData: SideBarData[pathName],
            current: SideBarData[pathName] && SideBarData[pathName].length ? SideBarData[pathName][0]['url'] : ''
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
                <SubMenu key={item.url} title={
                    <span>
                        <svg className="icon-svg" aria-hidden="true">
                            <use xlinkHref={item.icon}></use>
                        </svg>
                        <span>{item.name}</span>
                    </span>
                }
                >
                    {this.renderMenu(item.children)}
                </SubMenu>
            ) : (
                <Menu.Item key={item.url}>
                    <Link to={item.url}>
                        <svg className="icon-svg" aria-hidden="true">
                            <use xlinkHref={item.icon}></use>
                        </svg>
                        <span className="hide-text">{item.name}</span>
                    </Link>
                </Menu.Item>
            )
        })
    }
    render () {
        const { menuData } = this.state;
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
