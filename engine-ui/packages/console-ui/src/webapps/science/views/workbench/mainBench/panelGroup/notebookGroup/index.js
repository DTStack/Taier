import React from 'react';
import { bindActionCreators } from 'redux';
import { connect } from 'react-redux';
import { Tabs, Icon } from 'antd';

import PanelGroup from '../index';
import EditorPanel from '../editorPanel';
import DefaultNotebookView from '../../default/defaultNoteBookView';

import * as tabActions from '../../../../../actions/base/tab';
import { siderBarType } from '../../../../../consts';
import * as commActions from '../../../../../actions/base';
import { checkAndcloseTabs } from '../helper';

const TabPane = Tabs.TabPane;

@connect(state => {
    return {
        tabs: state.notebook.localTabs || [],
        currentTabIndex: state.notebook.currentTabIndex
    }
}, (dispatch) => {
    return {
        ...bindActionCreators(commActions, dispatch),
        ...bindActionCreators(tabActions, dispatch)
    };
})
class NoteBookGroup extends React.Component {
    componentDidMount () {
        this.props.getSysParams();
    }
    switchTab (key) {
        this.props.setCurrentTab(siderBarType.notebook, key)
    }
    async closeTabs (type) {
        const { tabs = [], currentTabIndex } = this.props;
        switch (type) {
            case 'ALL': {
                let isChecked = await checkAndcloseTabs(tabs, tabs.map((tab) => {
                    return tab.id
                }));
                if (isChecked) {
                    this.props.deleteAllTab(siderBarType.notebook);
                }
                break;
            }
            case 'OHTERS': {
                let isChecked = await checkAndcloseTabs(tabs, tabs.map((tab) => {
                    return tab.id
                }).filter((id) => {
                    return id != currentTabIndex
                }));
                if (isChecked) {
                    this.props.deleteOtherTab(siderBarType.notebook, currentTabIndex);
                }
                break;
            }
        }
    }
    async closeTab (tabId) {
        const { tabs = [], currentTabIndex } = this.props;
        let isChecked = await checkAndcloseTabs(tabs, [tabId]);
        if (isChecked) {
            if (currentTabIndex == tabId && tabs.length > 1) {
                this.switchTab(tabs.filter((tab) => {
                    return tab.id != currentTabIndex
                }).pop().id);
            }
            this.props.deleteTab(siderBarType.notebook, tabId);
        }
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
                                    tab={(
                                        <span className={tab.isDirty ? 'c-group-tabs__tab--dirty' : ''}>
                                            <Icon className='c-group-tabs__icon' type="book" />
                                            {tab.name}
                                        </span>
                                    )}
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
