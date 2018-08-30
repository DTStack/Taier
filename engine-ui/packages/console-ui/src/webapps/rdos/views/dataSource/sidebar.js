import React, { Component } from 'react'
import { Menu } from 'antd'
import { Link } from 'react-router'

export default class Sidebar extends Component {

    constructor(props) {
        super(props)
        this.state = {
            current: 'offLineData',
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
            if (current) {
                current = current.split('/')[0];
            }
            
            this.setState({ current: current||"offLineData" })
        }
    }

    handleClick = (e) => {
        this.setState({
            current: e.key,
        });
    }
  
    render() {
        const props = this.props
        return (
            <div className="sidebar m-ant-menu">
                <Menu
                  onClick={this.handleClick}
                  style={{ height: '100%' }}
                  selectedKeys={[this.state.current]}
                  defaultSelectedKeys={['offLineData']}
                  mode='inline'
                >
                    <Menu.Item key="offLineData">
                        <Link to={`/database/offLineData`}>
                            <img className="tabs-icon" src="/public/rdos/img/icon/offline.png" style={{width: 18, position: "relative", top: 4}}/>
                            <span style={{paddingLeft: 5}}>离线数据源</span>
                        </Link>
                    </Menu.Item>
                    <Menu.Item key="streamData">
                        <Link to={`/database/streamData`}>
                            <img className="tabs-icon" src="/public/rdos/img/icon/realtime.png" style={{width: 18, position: "relative", top: 4}}/>
                            <span style={{paddingLeft: 5}}>实时数据源</span>
                        </Link>
                    </Menu.Item>
                </Menu>
            </div>
        )
    }
}
