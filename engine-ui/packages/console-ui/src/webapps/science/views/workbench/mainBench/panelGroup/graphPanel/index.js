import React, { Component } from 'react';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import { get, isEmpty, debounce } from 'lodash'
import CommonEditor from '../../../../../components/commonEditor';

import reqUrls from '../../../../../consts/reqUrls';
import { siderBarType } from '../../../../../consts';

import workbenchActions from '../../../../../actions/workbenchActions';
import * as editorActions from '../../../../../actions/editorActions';
import * as runTaskActions from '../../../../../actions/runTaskActions';
import * as experimentActions from '../../../../../actions/experimentActions';
import { changeContent } from '../../../../../actions/experimentActions/runExperimentActions';
import PublishButtons from '../../../../../components/publishButtons';
import commActions from '../../../../../actions';
import { Tabs, Button } from 'antd';
import GraphContainer from './graphContainer';
import Description from './description';
import Params from './params/index';
import SchedulingConfig from '../../../../../components/schedulingConfig';

@connect(
    state => {
        const { workbench, editor } = state;
        return {
            editor,
            workbench,
            selectedCell: state.component.selectedCell
        };
    },
    dispatch => {
        return {
            ...bindActionCreators(workbenchActions, dispatch),
            ...bindActionCreators(editorActions, dispatch),
            ...bindActionCreators(commActions, dispatch),
            ...bindActionCreators(runTaskActions, dispatch),
            ...bindActionCreators(experimentActions, dispatch),
            ...bindActionCreators({ changeContent }, dispatch)
        }
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
    /* 运行 */
    execConfirm = () => {
        const { data } = this.props;
        this.props.execExperiment(data);
    }
    /* 停止 */
    stopTask = () => {
        // const { data } = this.props;
        // this.props.execExperiment(data);
    }
    changeSiderbar = (key, source) => {
        this.SiderBarRef.onTabClick(key, source)
    }
    saveTab = () => {
        const { data } = this.props;
        this.props.saveExperiment(data);
    }
    changeContent (key, value) {
        const { data } = this.props;
        this.props.changeContent({
            [key]: value
        }, data);
    }
    debounceChangeContent = debounce(this.changeContent, 100, {
        maxWait: 2000
    });
    renderSiderbarItems () {
        const { selectedCell, data } = this.props;
        return [
            <Tabs.TabPane
                tab='组件参数'
                key='params'
                disabled={isEmpty(selectedCell)}
            >
                <Params data={data.detailData} taskId={data.id} />
            </Tabs.TabPane>,
            <Tabs.TabPane
                tab='组件说明'
                key='description'
                disabled={isEmpty(selectedCell)}
            >
                <Description />
            </Tabs.TabPane>,
            <Tabs.TabPane
                tab='调度周期'
                key='scheduleConf'
            >
                <SchedulingConfig
                    formData={JSON.parse(data.scheduleConf)}
                    onChange={(newFormData) => {
                        this.debounceChangeContent('scheduleConf', JSON.stringify(newFormData));
                    }}
                />
            </Tabs.TabPane>
        ]
    }
    handleRenderToolbarOptions = () => {
        return <Button
            icon="save"
            title="保存"
            onClick={this.saveTab}
        >
            保存
        </Button>
    }
    renderPublishButton () {
        const { data } = this.props;
        return <PublishButtons
            data={data}
            name='作业'
            isNotebook={true}
            disabled={data && data.isDirty}
            onSubmit={(values) => {
                return this.props.submitNotebook({
                    ...data,
                    ...values
                })
            }}
            onSubmitModel={(values) => {
                return this.props.submitNotebookModel({
                    id: data && data.id,
                    ...values
                })
            }}
        />
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
            customToobar: this.handleRenderToolbarOptions(),
            leftCustomButton: this.renderPublishButton(),
            isRunning: editor.running.indexOf(currentTabId) > -1,
            onRun: this.execConfirm,
            onStop: this.stopTask
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
