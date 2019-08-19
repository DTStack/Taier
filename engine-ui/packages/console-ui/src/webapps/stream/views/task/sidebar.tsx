import * as React from 'react'

import { hashHistory } from 'react-router';
import { connect } from 'react-redux';

import RealTimeTabPanel from './realtimeTab'

class Sidebar extends React.Component<any, any> {
    constructor (props: any) {
        super(props)
    }

    render () {
        return (
            <div className="sidebar">
                <RealTimeTabPanel />
            </div>
        )
    }

    switchTaskPanel (key: any) {
        hashHistory.push(`/${key}/task`);
    }
}

export default connect((state: any) => {
    return {
        routing: state.routing
    }
})(Sidebar);
