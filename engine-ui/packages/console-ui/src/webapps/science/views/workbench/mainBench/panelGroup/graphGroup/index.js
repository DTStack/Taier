import React from 'react';
import { bindActionCreators } from 'redux';
import { connect } from 'react-redux';
import { Tabs } from 'antd';

import PanelGroup from '../index';
import GraphPanel from '../graphPanel';

import * as tabActions from '../../../../../actions/base/tab';
import { siderBarType } from '../../../../../consts';

const TabPane = Tabs.TabPane;

@connect(state => {
    return {
        tabs: state.experiment.localTabs || [],
        currentTabIndex: state.experiment.currentTabIndex
    }
}, (dispatch) => {
    const actions = bindActionCreators(tabActions, dispatch);
    return actions;
})
class GraphGroup extends React.Component {
    switchTab (key) {
        this.props.setCurrentTab(siderBarType.experiment, key);
    }
    closeTabs (type) {
        const { currentTabIndex } = this.props;
        switch (type) {
            case 'ALL': {
                this.props.deleteAllTab(siderBarType.experiment);
                break;
            }
            case 'OHTERS': {
                this.props.deleteOtherTab(siderBarType.experiment, currentTabIndex);
                break;
            }
        }
    }
    render () {
        const { tabs = [] } = this.props;
        return (
            <PanelGroup
                switchTab={this.switchTab.bind(this)}
                closeTabs={this.closeTabs.bind(this)}
                currentTabIndex={1}
            >
                {tabs.map((tab) => {
                    return (
                        <TabPane
                            className={tab.isDirty ? 'c-group-tabs__tab--dirty' : ''}
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
