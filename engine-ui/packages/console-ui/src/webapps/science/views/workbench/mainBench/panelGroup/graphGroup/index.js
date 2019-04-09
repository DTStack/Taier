import React from 'react';
import { connect } from 'react-redux';
import { Tabs } from 'antd';

import PanelGroup from '../index';
import GraphPanel from '../graphPanel';

const TabPane = Tabs.TabPane;

@connect(state => {
    return {
        tabs: state.experiment.localTabs || [{
            id: 1,
            name: '2',
            sqlText: '3'
        }, {
            id: 2,
            name: '3',
            sqlText: '3'
        }],
        currentTabIndex: state.experiment.currentTabIndex
    }
})
class GraphGroup extends React.Component {
    switchTab (key) {
        console.log(key)
    }
    render () {
        const { tabs = [] } = this.props;
        return (
            <PanelGroup
                switchTab={this.switchTab.bind(this)}
                currentTabIndex={1}
            >
                {tabs.map((tab) => {
                    return (
                        <TabPane
                            tab={tab.name}
                            key={tab.id}
                        >
                            <GraphPanel currentTab={tab.id} data={tab} />
                        </TabPane>
                    )
                })}
            </PanelGroup>
        )
    }
}
export default GraphGroup;
