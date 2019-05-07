import React from 'react';
import { Tabs } from 'antd';
class EditorSiderBar extends React.Component {
    state = {
        activeKey: null
    }
    componentDidMount () {
        this.props.onRef(this);
    }

    onTabClick (key, source = false) {
        if (source) {
            this.setState({
                activeKey: this.state.activeKey ? this.state.activeKey : key
            })
        } else {
            const { activeKey } = this.state;
            if (key == activeKey) {
                this.setState({
                    activeKey: null
                })
            } else {
                this.setState({
                    activeKey: key
                })
            }
        }
    }
    render () {
        const { activeKey } = this.state;
        return (
            <Tabs
                className={`c-panel-siderbar ${activeKey ? 'c-panel-siderbar--open' : ''}`}
                tabPosition='right'
                activeKey={activeKey}
                onTabClick={this.onTabClick.bind(this)}
            >
                {this.props.children}
            </Tabs>
        )
    }
}
export default EditorSiderBar;
