import * as React from 'react'
import { Menu } from 'antd'
import { Link } from 'react-router'
require('public/iconfont/iconfont.js');
const { SubMenu } = Menu;
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
        const routes = this.props.router.routes
        if (routes.length > 3) {
            const current = routes[3].path || 'config'
            this.setState({ current })
        }
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
        const menuData = [{
            icon: '#icon-project_set',
            name: '数据源管理',
            url: '/database'
        }, {
            icon: '#icon-project_set',
            name: '实体管理',
            url: '/entityManage'
        }, {
            icon: '#icon-project_set',
            name: '关系管理',
            url: '/relationManage'
        }, {
            icon: '#icon-project_set',
            name: '字典管理',
            url: '/dictionaryManage'
        }]
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
