import React from 'react';
import { Tabs } from 'antd';

import NotebookSiderBar from './tabPanes/notebook';
import ComponentSiderBar from './tabPanes/component';
import ExperimentSiderBar from './tabPanes/experiment';
const TabPane = Tabs.TabPane;

class SiderBarContainer extends React.Component {
    render () {
        return (
            <div className='sidebar'>
                <Tabs className='c-antd-tabs-sidebar' tabPosition='left'>
                    <TabPane tab="notebook" key="1">
                        <NotebookSiderBar />
                    </TabPane>
                    <TabPane tab="实验" key="2">
                        <ExperimentSiderBar />
                    </TabPane>
                    <TabPane tab="组件" key="3">
                        <ComponentSiderBar />
                    </TabPane>
                    <TabPane tab="模型" key="4">model</TabPane>
                </Tabs>
            </div>
        )
    }
}
export default SiderBarContainer;
