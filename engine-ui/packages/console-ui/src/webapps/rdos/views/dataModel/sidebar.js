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
        if (routes.length > 3) {
            let current = routes[3].path;

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
        const base = `/data-model`
        return (
            <div className="sidebar m-ant-menu">
                <Menu
                  onClick={this.handleClick}
                  style={{ width: 200, height: '100%' }}
                  selectedKeys={[this.state.current]}
                  defaultSelectedKeys={[this.state.current]}
                  mode="inline"
                >
                    <Menu.Item key="overview">
                        <Link to={`${base}/overview`}>
                            <Icon type="pie-chart" />总览
                        </Link>
                    </Menu.Item>
                    <Menu.Item key="check">
                        <Link to={`${base}/check`}>
                            <Icon type="filter" />检测中心
                        </Link>
                    </Menu.Item>
                    <Menu.Item key="list">
                        <Link to={`${base}/list`}>
                            <Icon type="api" />模型设计
                        </Link>
                    </Menu.Item>
                    <Menu.Item key="config">
                        <Link to={`${base}/config`}>
                            <Icon type="tool" />配置中心
                        </Link>
                    </Menu.Item>
                </Menu>
            </div>
        )
    }
}
