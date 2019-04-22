import React, { Component } from 'react';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import { get } from 'lodash'
import CommonEditor from '../../../../../components/commonEditor';

import reqUrls from '../../../../../consts/reqUrls';
import { siderBarType } from '../../../../../consts';

import workbenchActions from '../../../../../actions/workbenchActions';
import * as editorActions from '../../../../../actions/editorActions';
import * as runTaskActions from '../../../../../actions/runTaskActions';
import * as experimentActions from '../../../../../actions/experimentActions';
import commActions from '../../../../../actions';
import { Tabs } from 'antd';
import GraphContainer from './graphContainer';
import Description from './description';
import Params from './params/index';

@connect(
    state => {
        const { workbench, editor } = state;
        return {
            editor,
            workbench
        };
    },
    dispatch => {
        const actionsOne = bindActionCreators(workbenchActions, dispatch);
        const actionsTwo = bindActionCreators(editorActions, dispatch);
        const actionsThree = bindActionCreators(commActions, dispatch);
        const actionsFour = bindActionCreators(runTaskActions, dispatch);
        const actionsFive = bindActionCreators(experimentActions, dispatch);
        return Object.assign(actionsOne, actionsTwo, actionsThree, actionsFour, actionsFive);
    }
)
class GraphPanel extends Component {
    componentDidMount () {
        const { data, currentTab } = this.props;
        this.props.getTaskData(data, currentTab);
    }

    removeConsoleTab = targetKey => {
        const { currentTab } = this.props;
        this.props.removeRes(currentTab, parseInt(targetKey, 10));
    };

    closeConsole = () => {
        const { currentTab } = this.props;
        this.props.resetConsole(currentTab, siderBarType.experiment);
    };
    execConfirm = () => {
        const { data } = this.props;
        this.props.execExperiment(data);
    }
    changeSiderbar = (key, source) => {
        this.SiderBarRef.onTabClick(key, source)
    }
    renderSiderbarItems () {
        return [
            <Tabs.TabPane
                tab='组件参数'
                key='params'
            >
                <Params />
            </Tabs.TabPane>,
            <Tabs.TabPane
                tab='组件说明'
                key='description'
            >
                <Description />
            </Tabs.TabPane>,
            <Tabs.TabPane
                tab='调度周期'
                key='key2'
            >
                1232
            </Tabs.TabPane>
        ]
    }
    render () {
        const { editor, data, currentTab } = this.props;

        const currentTabId = data && data.id;

        const consoleData = editor.console[siderBarType.experiment];
        const resultData = consoleData[currentTabId] && consoleData[currentTabId].data
            ? consoleData[currentTabId].data
            : [];
        const consoleActivekey = get(consoleData[currentTabId], 'activeKey', null);

        const toolbarOpts = {
            enable: true,
            enableRun: true,
            disableEdit: true,
            isRunning: editor.running.indexOf(currentTabId) > -1,
            onRun: this.execConfirm,
            onStop: this.stopSQL
        };
        const consoleOpts = {
            data: resultData,
            onConsoleClose: this.closeConsole,
            onRemoveTab: this.removeConsoleTab,
            downloadUri: reqUrls.DOWNLOAD_SQL_RESULT,
            tabOptions: {
                activeKey: consoleActivekey,
                onChange: this.changeConsoleTab

            }
        };
        return (
            <CommonEditor
                console={consoleOpts}
                toolbar={toolbarOpts}
                siderBarItems={this.renderSiderbarItems()}
                SiderBarRef={(SiderBarRef) => this.SiderBarRef = SiderBarRef}
            >
                <GraphContainer isRunning={toolbarOpts.isRunning} changeSiderbar={this.changeSiderbar} currentTab={currentTab} data={data} />
            </CommonEditor>
        );
    }
}

export default GraphPanel;
