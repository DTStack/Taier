import React, { Component } from 'react'

import {
    Row, Tabs
} from 'antd';

import { hashHistory } from 'react-router';
import { connect } from 'react-redux';
import OfflineTabPanel from './offlineTab'

class Sidebar extends Component {
    constructor (props) {
        super(props)
    }

    render () {
        return (
            <div className="sidebar">
                <Row>
                    <OfflineTabPanel/>
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
