import React, { Component } from 'react';
import { bindActionCreators } from 'redux';
import { connect } from "react-redux";

import {
    Tabs, Menu, Dropdown, Icon
} from 'antd';

import TabIcon from '../../../components/tab-icon';
import * as workbenchActions from '../../../actions/workbenchActions';

import BenchContent from './benchContent';
import CreateDBModal from './database/create';
import CreateTableDDLModal from './tableDetail/ddlModal';

const TabPane = Tabs.TabPane;

@connect(
    state => {
        const { workbench, modal } = state;
        return {
            workbench,
            modal,
        };
    }, 
    dispatch => {
        const actions = bindActionCreators(workbenchActions, dispatch);
        return actions;
    }
)
class MainBench extends Component {

    renderTabs = (tabs) => {

        if (tabs && tabs.length > 0) {
            return tabs.map((tab) => {

                let title = (<span>
                    <TabIcon type={tab.actionType} />
                    <span className="tab-ellipsis" title={tabs}>
                        {tab.name}
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
        const { closeTabs, workbench, switchTab } = this.props;
        const { tabs, currentTab } = workbench.mainBench;
        return (
            <Dropdown overlay={
                <Menu style={{ marginRight: 2, maxHeight:"500px", overflowY:"auto" }}
                >
                    <Menu.Item  key="OHTERS">
                    <a onClick={() => closeTabs("OHTERS")}>关闭其他</a>
                    </Menu.Item>
                    <Menu.Item key="ALL">
                    <a onClick={() => closeTabs("ALL")} >关闭所有</a>
                    </Menu.Item>
                    <Menu.Divider />
                    {tabs.map((tab)=>{
                        return <Menu.Item key={tab.id} >
                        <a 
                            onClick={switchTab.bind(currentTab)}
                            style={tab.id == currentTab ? { color:"#2491F7" } : {} }
                        >
                            {tab.name}
                        </a>
                        </Menu.Item>
                    })}
                </Menu>
            }>
                <Icon type="bars" size="" style={{ margin: '7 5 0 0', fontSize: 18, }} />
            </Dropdown>
        )
    }

    render() {
        const { closeTab, switchTab, workbench } = this.props;
        const { tabs, currentTab, currentStep } = workbench.mainBench;
        return (
            <div className="m-mainbench">
                 <Tabs
                    hideAdd
                    onTabClick={switchTab.bind(currentTab)}
                    activeKey={`${currentTab}`}
                    type="editable-card"
                    onEdit={(tabId) => closeTab(tabId)}
                    tabBarExtraContent={this.renderTabBarExtraContent()}
                >
                    {this.renderTabs(tabs)}
                </Tabs>
                <CreateDBModal {...this.props} />
                <CreateTableDDLModal {...this.props} />
            </div>
        )
    }
}

export default MainBench