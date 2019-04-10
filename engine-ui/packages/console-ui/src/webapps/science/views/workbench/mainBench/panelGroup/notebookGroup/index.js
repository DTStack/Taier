import React from 'react';
import { bindActionCreators } from 'redux';
import { connect } from 'react-redux';
import { Tabs } from 'antd';

import PanelGroup from '../index';
import EditorPanel from '../editorPanel';
import DefaultNotebookView from '../../default/defaultNoteBookView';

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
    closeTab (tabId) {
        this.props.deleteTab(siderBarType.notebook, tabId);
    }
    render () {
        const { tabs = [], currentTabIndex } = this.props;
        return !tabs || !tabs.length ? (
            <DefaultNotebookView />
        )
            : (
                <PanelGroup
                    switchTab={this.switchTab.bind(this)}
                    closeTabs={this.closeTabs.bind(this)}
                    closeTab={this.closeTab.bind(this)}
                    currentTabIndex={currentTabIndex}
                >
                    {
                        tabs.map((tab) => {
                            return (
                                <TabPane
                                    tab={<span className={tab.isDirty ? 'c-group-tabs__tab--dirty' : ''}>{tab.name}</span>}
                                    key={tab.id}
                                >
                                    <EditorPanel currentTab={tab.id} data={tab} />
                                </TabPane>
                            )
                        })
                    }
                </PanelGroup>
            )
    }
}
export default NoteBookGroup;
