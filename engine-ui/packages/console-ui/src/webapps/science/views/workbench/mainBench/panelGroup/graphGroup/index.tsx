import * as React from 'react';
import { bindActionCreators } from 'redux';
import { connect } from 'react-redux';
import { Tabs, Icon } from 'antd';

import PanelGroup from '../index';
import GraphPanel from '../graphPanel';

import * as tabActions from '../../../../../actions/base/tab';
import * as commActions from '../../../../../actions/base';
import * as componentActions from '../../../../../actions/componentActions';
import { siderBarType } from '../../../../../consts';
import DefaultExperimentView from '../../default/defaultExperimentView';
import { checkAndcloseTabs } from '../../../../../actions/base/helper';

const TabPane = Tabs.TabPane;

@(connect((state: any) as any) => {
    return {
        tabs: state.experiment.localTabs || [],
        currentTabIndex: state.experiment.currentTabIndex
    }
}, (dispatch: any) => {
    const actions = bindActionCreators({
        ...tabActions,
        getSysParams: commActions.getSysParams,
        saveSelectedCell: componentActions.saveSelectedCell
    }, dispatch);
    return actions;
})
class GraphGroup extends React.Component<any, any> {
    state: any = {
        loading: true
    }
    switchTab(key: any) {
        this.props.saveSelectedCell({});
        this.props.setCurrentTab(siderBarType.experiment, key);
    }
    async componentDidMount () {
        await this.props.getSysParams();
        this.setState({
            loading: false
        })
    }
    async closeTabs(type: any) {
        const { tabs = [], currentTabIndex } = this.props;
        switch (type) {
            case 'ALL': {
                let isChecked = await checkAndcloseTabs(tabs, tabs.map((tab: any) => {
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
    async closeTab(tabId: any) {
        const { tabs = [], currentTabIndex } = this.props;
        this.props.closeTab(siderBarType.experiment, parseInt(tabId), tabs, currentTabIndex);
    }
    render () {
        const { loading } = this.props;
        const { tabs = [], currentTabIndex } = this.props;
        return !tabs || !tabs.length || loading ? (
            <DefaultExperimentView />
        ) : (
            <PanelGroup
                className="experiment"
                switchTab={this.switchTab.bind(this)}
                closeTabs={this.closeTabs.bind(this)}
                closeTab={this.closeTab.bind(this)}
                currentTabIndex={currentTabIndex}
                renderOutsideTabs={() => {
                    return <GraphPanel key={currentTabIndex} currentTab={currentTabIndex} data={tabs.find((o: any) => o.id == currentTabIndex) || {}} />
                }}
            >
                {tabs.map((tab: any) => {
                    return (
                        <TabPane
                            style={{ height: '0px' }}
                            tab={(
                                <span className={tab.isDirty ? 'c-group-tabs__tab--dirty' : ''}>
                                    <Icon className='c-group-tabs__icon' type="usb" />
                                    {tab.name}
                                </span>
                            )}
                            key={`${tab.id}`}
                        />
                    )
                })}
            </PanelGroup>
        )
    }
}
export default GraphGroup;
