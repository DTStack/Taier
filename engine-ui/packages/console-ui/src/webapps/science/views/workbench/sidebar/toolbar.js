import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Tooltip, Icon } from 'antd';

@connect(state => {
    return {
        licenseApps: state.licenseApps
    }
})
class Toolbar extends Component {
    render () {
        const { toolbarItems = [] } = this.props;
        return (
            <div className="toolbar txt-right">
                {toolbarItems.map((item) => {
                    return <Tooltip key={item.title} title={item.title}>
                        <Icon
                            className="btn-icon"
                            type={item.type}
                            onClick={item.onClick}
                        />
                    </Tooltip>
                })}
            </div>
        )
    }
}

export default Toolbar
