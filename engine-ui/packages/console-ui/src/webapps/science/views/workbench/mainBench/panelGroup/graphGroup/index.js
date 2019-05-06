import React from 'react';
import { bindActionCreators } from 'redux';
import { connect } from 'react-redux';
import { Tabs, Icon } from 'antd';

import PanelGroup from '../index';
import GraphPanel from '../graphPanel';

import * as tabActions from '../../../../../actions/base/tab';
import { siderBarType } from '../../../../../consts';
import DefaultExperimentView from '../../default/defaultExperimentView';
import { checkAndcloseTabs } from '../helper';

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
    async closeTabs (type) {
        const { tabs = [], currentTabIndex } = this.props;
        switch (type) {
            case 'ALL': {
                let isChecked = await checkAndcloseTabs(tabs, tabs.map((tab) => {
                    return tab.id
                }));
                if (isChecked) {
                    this.props.deleteAllTab(siderBarType.experiment);
                }
                break;
            }
            case 'OHTERS': {
                this.props.deleteOtherTab(siderBarType.experiment, currentTabIndex);
                break;
            }
        }
    }
    async closeTab (tabId) {
        const { tabs = [], currentTabIndex } = this.props;
        let isChecked = await checkAndcloseTabs(tabs, [parseInt(tabId)]);
        if (isChecked) {
            if (currentTabIndex == tabId && tabs.length > 1) {
                this.switchTab(tabs.filter((tab) => {
                    return tab.id != currentTabIndex
                }).pop().id);
            }
            this.props.deleteTab(siderBarType.experiment, tabId);
        }
    }
    render () {
        const { tabs = [], currentTabIndex } = this.props;
        return !tabs || !tabs.length ? (
            <DefaultExperimentView />
        ) : (
            <PanelGroup
                switchTab={this.switchTab.bind(this)}
                closeTabs={this.closeTabs.bind(this)}
                closeTab={this.closeTab.bind(this)}
                currentTabIndex={currentTabIndex}
            >
                {tabs.map((tab) => {
                    return (
                        <TabPane
                            tab={(
                                <span className={tab.isDirty ? 'c-group-tabs__tab--dirty' : ''}>
                                    <Icon className='c-group-tabs__icon' type="usb" />
                                    {tab.name}
                                </span>
                            )}
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
