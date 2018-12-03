import React, { Component } from 'react'

import {
    Row
} from 'antd';

import { hashHistory } from 'react-router';
import { connect } from 'react-redux';

import RealTimeTabPanel from './realtimeTab'

class Sidebar extends Component {
    constructor (props) {
        super(props)
    }

    render () {
        return (
            <div className="sidebar">
                <Row>
                    <RealTimeTabPanel />
                </Row>
            </div>
        )
    }

    switchTaskPanel (key) {
        hashHistory.push(`/${key}/task`);
    }
}

export default connect(state => {
    return {
        routing: state.routing
    }
})(Sidebar);
