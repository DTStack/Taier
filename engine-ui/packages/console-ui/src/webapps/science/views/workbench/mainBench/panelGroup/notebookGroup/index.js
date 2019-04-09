import React from 'react';
import { bindActionCreators } from 'redux';
import { connect } from 'react-redux';
import { Tabs } from 'antd';

import PanelGroup from '../index';
import EditorPanel from '../editorPanel';

import * as tabActions from '../../../../../actions/base/tab';
import { siderBarType } from '../../../../../consts';

const TabPane = Tabs.TabPane;

@connect(state => {
    return {
        tabs: state.notebook.localTabs || [],
        currentTabIndex: state.notebook.currentTabIndex
    }
}, (dispatch) => {
    const actions = bindActionCreators(tabActions, dispatch);
    return actions;
})
class NoteBookGroup extends React.Component {
    switchTab (key) {
        this.props.setCurrentTab(siderBarType.notebook, key)
    }
    closeTabs (type) {
        const { currentTabIndex } = this.props;
        switch (type) {
            case 'ALL': {
                this.props.deleteAllTab(siderBarType.notebook);
                break;
            }
            case 'OHTERS': {
                this.props.deleteOtherTab(siderBarType.notebook, currentTabIndex);
                break;
            }
        }
    }
    render () {
        const { tabs = [], currentTabIndex } = this.props;
        return (
            <PanelGroup
                switchTab={this.switchTab.bind(this)}
                closeTabs={this.closeTabs.bind(this)}
                currentTabIndex={currentTabIndex}
            >
                {tabs.map((tab) => {
                    return (
                        <TabPane
                            tab={tab.name}
                            key={tab.id}
                        >
                            <EditorPanel currentTab={tab.id} data={tab} />
                        </TabPane>
                    )
                })}
            </PanelGroup>
        )
    }
}
export default NoteBookGroup;
