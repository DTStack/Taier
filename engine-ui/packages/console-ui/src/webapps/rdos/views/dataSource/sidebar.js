import React, { Component } from 'react'
import { Menu } from 'antd'
import { Link } from 'react-router'

export default class Sidebar extends Component {

    constructor(props) {
        super(props)
        this.state = {
            current: 'offline',
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
        console.log("routes",routes);
        
        if (routes.length > 3) {
            let current = routes[3].path;
            console.log('current',current);
            
            if (current) {
                current = current.split('/')[0];
            }
            
            this.setState({ current: current||"offline" })
        }
    }

    handleClick = (e) => {
        this.setState({
            current: e.key,
        });
    }
 
    render() {
        const props = this.props
        console.log('this.state.current',this.state.current);
        
        return (
            <div className="sidebar m-ant-menu">
                <Menu
                  onClick={this.handleClick}
                  style={{ height: '100%' }}
                  selectedKeys={[this.state.current]}
                  defaultSelectedKeys={['offline']}
                  mode='inline'
                >
                    <Menu.Item key="offline">
                        <Link to={`/database/offline`}>离线数据源</Link>
                    </Menu.Item>
                    <Menu.Item key="stream">
                        <Link to={`/database/stream`}>实时数据源</Link>
                    </Menu.Item>
                </Menu>
            </div>
        )
    }
}
