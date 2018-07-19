import React, { Component } from 'react';
import { Tabs } from 'antd';
import { connect } from 'react-redux';

import FieldCheck from './fieldCheck';
import ModelCheck from './modelCheck';

const TabPane = Tabs.TabPane;

class DMCheckCenter extends Component {

    render() {
        return (
            <div className="box-1 m-tabs data-check">
                <Tabs animated={false} style={{height: 'auto'}}>
                    <TabPane tab="模型检测" key="1">
                        <ModelCheck {...this.props}/>
                    </TabPane>
                    <TabPane tab="字段检测" key="2">
                        <FieldCheck {...this.props}/>
                    </TabPane>
                </Tabs>
            </div>
        )
    }
}

export default connect(state => ({
    project: state.project
}), null)(DMCheckCenter);
