import React, { Component } from 'react';
import { Tabs } from 'antd';

import FieldCheck from './fieldCheck';
import ModelCheck from './modelCheck';

const TabPane = Tabs.TabPane;

export default class DMCheckCenter extends Component {

    render() {
        return (
            <div className="box-1 m-tabs">
                <Tabs animated={false} style={{height: 'auto'}}>
                    <TabPane tab="模型检测" key="1">
                        <ModelCheck />
                    </TabPane>
                    <TabPane tab="字段检测" key="2">
                        <FieldCheck />
                    </TabPane>
                </Tabs>
            </div>
        )
    }
}
