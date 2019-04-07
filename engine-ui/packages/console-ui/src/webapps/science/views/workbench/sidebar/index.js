import React from 'react';
import { Tabs } from 'antd';
import { connect } from 'react-redux';

import NotebookSiderBar from './tabPanes/notebook';
import ComponentSiderBar from './tabPanes/component';
import ExperimentSiderBar from './tabPanes/experiment';
import commonActionType from '../../../consts/commonActionType';
import { siderBarType } from '../../../consts'
const TabPane = Tabs.TabPane;
@connect((state) => {
    return {
        siderBarKey: state.common.siderBarKey
    }
}, (dispatch) => {
    return {
        changeSiderBar (key) {
            dispatch({
                type: commonActionType.CHANGE_SIDERBAR_KEY,
                payload: key
            })
        }
    }
})
class SiderBarContainer extends React.Component {
    render () {
        const { changeSiderBar, siderBarKey } = this.props;
        return (
            <Tabs activeKey={siderBarKey} onChange={changeSiderBar} className={`c-antd-tabs-sidebar ${siderBarKey == siderBarType.model ? 'u-pane--hidden' : ''}`} tabPosition='left'>
                <TabPane tab="notebook" key="notebook">
                    <NotebookSiderBar />
                </TabPane>
                <TabPane tab="实验" key="experiment">
                    <ExperimentSiderBar />
                </TabPane>
                <TabPane tab="组件" key="component">
                    <ComponentSiderBar />
                </TabPane>
                <TabPane tab="模型" key="model">model</TabPane>
            </Tabs>
        )
    }
}
export default SiderBarContainer;
