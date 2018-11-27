import React, { Component } from 'react';
import { Tabs } from 'antd';
import { connect } from 'react-redux';
import { hashHistory } from 'react-router';

import FieldCheck from './fieldCheck';
import ModelCheck from './modelCheck';

const TabPane = Tabs.TabPane;

class DMCheckCenter extends Component {
    constructor (props) {
        super(props)
        const { currentTab } = this.props.location.query;
        this.state = {
            currentTab: currentTab || '1'
        }
    }

    changeTabs = (value) => {
        const { pathname, query } = this.props.location;

        const pathQuery = { currentTab: value };
        hashHistory.push({
            pathname,
            query: Object.assign(query, pathQuery)
        })
        this.setState({
            currentTab: value
        })
    }

    render () {
        return (
            <div className="box-1 m-tabs data-check">
                <Tabs
                    animated={false}
                    style={{ height: 'auto', minHeight: 'calc(100% - 40px)' }}
                    onChange={this.changeTabs}
                    activeKey={this.state.currentTab}
                >
                    <TabPane tab="模型检测" key="1">
                        <ModelCheck {...this.props} searchQuery = {this.state.searchQuery} />
                    </TabPane>
                    <TabPane tab="字段检测" key="2">
                        <FieldCheck {...this.props} />
                    </TabPane>
                </Tabs>
            </div>
        )
    }
}

export default connect(state => ({
    project: state.project,
    user: state.user
}), null)(DMCheckCenter);
