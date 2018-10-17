import React, { Component } from 'react';
import { bindActionCreators } from 'redux';
import { connect } from "react-redux";

import {
    Row, Col, Button, message, Input, Form,
    Tabs, Menu, Dropdown, Icon, Modal, Tooltip
} from 'antd';

import TabIcon from '../../../components/tab-icon';
import * as workbenchActions from '../../../actions/workbenchActions';
import BenchContent from './benchContent';

const TabPane = Tabs.TabPane;

@connect(
    state => {
        const { workbench } = state;
        return {
            workbench,
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
                        style={{ height: '0px' }}
                        tab={title}
                        key={tab.id}
                    >
                        <BenchContent tabData={tab}/>
                    </TabPane>
                );
            });
        }
        return []
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
                >
                    {this.renderTabs(tabs)}
                </Tabs>
            </div>
        )
    }
}

export default MainBench