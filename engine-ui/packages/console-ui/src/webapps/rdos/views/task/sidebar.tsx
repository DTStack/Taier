import * as React from 'react'

import { hashHistory } from 'react-router';
import { connect } from 'react-redux';
import OfflineTabPanel from './offlineTab'

class Sidebar extends React.Component<any, any> {
    constructor (props: any) {
        super(props)
    }

    render () {
        return (
            <div className="sidebar">
                <OfflineTabPanel/>
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
