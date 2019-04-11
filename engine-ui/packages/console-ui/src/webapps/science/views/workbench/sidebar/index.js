import React from 'react';
import { Tabs } from 'antd';
import { connect } from 'react-redux';

import NotebookSiderBar from './tabPanes/notebook';
import ComponentSiderBar from './tabPanes/component';
import ExperimentSiderBar from './tabPanes/experiment';
import commonActionType from '../../../consts/commonActionType';
import { siderBarType } from '../../../consts'
import { initLoadTreeNode } from '../../../actions/base/fileTree';
const TabPane = Tabs.TabPane;
@connect((state) => {
    return {
        siderBarKey: state.common.siderBarKey,
        files: {
            notebook: state.notebook.files,
            experiment: state.experiment.files,
            component: state.component.files
        }
    }
}, (dispatch) => {
    return {
        changeSiderBar (key) {
            dispatch({
                type: commonActionType.CHANGE_SIDERBAR_KEY,
                payload: key
            })
        },
        initLoadTreeNode (...args) {
            return dispatch(initLoadTreeNode(...args));
        }
    }
})
class SiderBarContainer extends React.Component {
    componentDidMount () {
        const { files } = this.props;
        if (!files.notebook.length && !files.experiment.length && !files.component.length) {
            this.props.initLoadTreeNode();
        }
    }
    render () {
        const { changeSiderBar, siderBarKey } = this.props;
        return (
            <Tabs activeKey={siderBarKey} onChange={changeSiderBar} className={`c-antd-tabs-sidebar ${siderBarKey == siderBarType.model ? 'u-pane--hidden' : ''}`} tabPosition='left'>
                <TabPane tab="notebook" key={siderBarType.notebook}>
                    <NotebookSiderBar />
                </TabPane>
                <TabPane tab="实验" key={siderBarType.experiment}>
                    <ExperimentSiderBar />
                </TabPane>
                <TabPane tab="组件" key={siderBarType.component}>
                    <ComponentSiderBar />
                </TabPane>
                <TabPane tab="模型" key={siderBarType.model}>model</TabPane>
            </Tabs>
        )
    }
}
export default SiderBarContainer;
