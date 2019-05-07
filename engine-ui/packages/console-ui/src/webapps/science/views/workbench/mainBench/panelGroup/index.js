import React, { Component } from 'react';
import { bindActionCreators } from 'redux';
import { connect } from 'react-redux';

import {
    Tabs, Menu, Dropdown, Icon
} from 'antd';

import TabIcon from '../../../../components/tab-icon';
import workbenchActions from '../../../../actions/workbenchActions';

import BenchContent from '../benchContent';

const TabPane = Tabs.TabPane;

@connect(
    state => {
        const { workbench, user } = state;
        return {
            user,
            workbench
        };
    },
    dispatch => {
        const actions = bindActionCreators(workbenchActions, dispatch);
        return actions;
    }
)
class PanelGroup extends Component {
    renderTabs = (tabs) => {
        if (tabs && tabs.length > 0) {
            return tabs.map((tab) => {
                let title = (<span>
                    <TabIcon type={tab.actionType} />
                    <span className="tab-ellipsis" title={tab.tabName}>
                        {tab.tabName}
                    </span>
                </span>);

                return (
                    <TabPane
                        tab={title}
                        key={tab.id}
                    >
                        <BenchContent tabData={tab} {...this.props}/>
                    </TabPane>
                );
            });
        }
        return []
    }

    renderTabBarExtraContent = () => {
        const { closeTabs } = this.props;
        return (
            <Dropdown overlay={
                <Menu style={{ marginRight: 2, maxHeight: '500px', overflowY: 'auto' }}>
                    <Menu.Item key="OHTERS">
                        <a onClick={() => closeTabs('OHTERS')}>关闭其他</a>
                    </Menu.Item>
                    <Menu.Item key="ALL">
                        <a onClick={() => closeTabs('ALL')} >关闭所有</a>
                    </Menu.Item>
                    <Menu.Divider />
                    {/* {tabs.map((tab) => {
                        return <Menu.Item key={tab.id} >
                            <a
                                onClick={() => this.switchTab(tab.id)}
                                style={tab.id == currentTab ? { color: '#2491F7' } : {} }
                            >
                                {tab.tabName || tab.name}
                            </a>
                        </Menu.Item>
                    })} */}
                </Menu>
            }>
                <Icon type="bars" size="" style={{ margin: '7 5 0 0', fontSize: 18 }} />
            </Dropdown>
        )
    }
    switchTab (key) {
        console.log(key)
    }
    render () {
        const { closeTab, currentTabIndex, children } = this.props;
        return (
            <div style={{ position: 'relative', width: '100%', height: 'calc(100% - 10px)', marginTop: '10px' }}>
                <div className="m-mainbench">
                    <Tabs
                        className='c-group-tabs'
                        hideAdd
                        onTabClick={this.props.switchTab}
                        activeKey={`${currentTabIndex}`}
                        type="editable-card"
                        onEdit={(tabId) => closeTab(tabId)}
                        tabBarExtraContent={this.renderTabBarExtraContent()}
                    >
                        {children}
                    </Tabs>
                </div>
            </div>
        )
    }
}

export default PanelGroup
