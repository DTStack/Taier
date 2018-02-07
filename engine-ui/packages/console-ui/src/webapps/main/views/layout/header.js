import React, { Component } from 'react'
import { Navigator, Logo }  from '../../components/nav';

class Header extends Component {

    constructor(props) {
        super(props)
        this.state = {}
    }

    componentDidMount() {
        this.updateSelected()
    }

    // 控制项目下拉菜单的显示
    componentWillReceiveProps() {
        this.updateSelected()
    }

    handleClick = (e) => {
        const props = e.item.props
        const { router, dispatch } = this.props
        this.setState({ current: e.key });
    }

    clickUserMenu = (obj) => {
        if (obj.key === 'logout') {
            Api.logout();
        }
    }

    goIndex = () => {
        const { router } = this.props
        this.setState({ current: 'overview' })
        router.push('/')
    }

    updateSelected() {
        let pathname = this.props.router.location.pathname
        const routes = pathname ? pathname.split('/') : []
        let path = routes.length > 0 && routes[1] !== '' ? routes[1] : 'overview'
        if (path && (path.indexOf('task') > -1 || path.indexOf('offline') > -1)) {
            this.setState({
                devPath: pathname,
            })
            path = 'realtime'
        }
        if (path !== this.state.current) {
            this.setState({
                current: path,
            })
        }
        return path
    }

    initUserDropMenu = () => {
        return (
            <Menu onClick={this.clickUserMenu}>
                <Menu.Item key="ucenter">
                    <a href={UIC_URL_TARGET}>用户中心</a>
                </Menu.Item>
                <Menu.Item key="logout">
                    退出登录
                </Menu.Item>
            </Menu>
        )
    }

    render() {
        const { user, apps } = this.props;
        
        const logo = <Logo linkTo="/" img={'public/main/img/logo.png'}/>

        return <Navigator 
            logo={logo}
            menuItems={apps}
            {...this.props}
        />
    }
}
export default Header

