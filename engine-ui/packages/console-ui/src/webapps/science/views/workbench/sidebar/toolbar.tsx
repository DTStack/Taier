import * as React from 'react';
import { connect } from 'react-redux';
import { Tooltip, Icon } from 'antd';

@(connect((state: any) => {
    return {
        licenseApps: state.licenseApps
    }
}) as any)
class Toolbar extends React.Component<any, any> {
    render () {
        const { toolbarItems = [] } = this.props;
        return (
            <div className="toolbar txt-right">
                {toolbarItems.map((item: any) => {
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
