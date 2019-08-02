import * as React from 'react';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import { get, isEmpty, debounce } from 'lodash'
import { Tabs, Button, Icon, Tooltip } from 'antd';

import CommonEditor from '../../../../../components/commonEditor';

import reqUrls from '../../../../../consts/reqUrls';
import MyIcon from '../../../../../components/icon';
import { siderBarType } from '../../../../../consts';

import workbenchActions from '../../../../../actions/workbenchActions';
import * as editorActions from '../../../../../actions/editorActions';
import * as runTaskActions from '../../../../../actions/runTaskActions';
import * as experimentActions from '../../../../../actions/experimentActions';
import { changeContent } from '../../../../../actions/experimentActions/runExperimentActions';
import PublishButtons from '../../../../../components/publishButtons';
import commActions from '../../../../../actions';
import GraphContainer from './graphContainer';
import Description from './description';
import Params from './params/index';
import SchedulingConfig from '../../../../../components/schedulingConfig';

@(connect(
    (state: any) => {
        const { workbench, editor } = state;
        return {
            editor,
            workbench,
            selectedCell: state.component.selectedCell
        };
    },
    (dispatch: any) => {
        return {
            ...bindActionCreators(workbenchActions, dispatch),
            ...bindActionCreators(editorActions, dispatch),
            ...bindActionCreators(commActions, dispatch),
            ...bindActionCreators(runTaskActions, dispatch),
            ...bindActionCreators(experimentActions, dispatch),
            ...bindActionCreators({ changeContent }, dispatch)
        }
    }
) as any)
class GraphPanel extends React.Component<any, any> {
    constructor (props: any) {
        super(props);
    }
    SiderBarRef: any;
    GraphInstance: any;
    GraphContainer: any;
    removeConsoleTab = (targetKey: any) => {
        const { currentTab } = this.props;
        this.props.removeRes(currentTab, parseInt(targetKey, 10));
    };

    closeConsole = () => {
        const { currentTab } = this.props;
        this.props.resetConsole(currentTab, siderBarType.experiment);
    };
    /* 运行 */
    execConfirm = () => {
        const { data, currentTab } = this.props;
        this.props.getRunTaskList(data, data.id, 0, currentTab);
    }
    /* 停止 */
    stopTask = () => {
        const { data } = this.props;
        this.props.stopRunningTask(data);
    }
    changeSiderbar = (key: any, source: any) => {
        this.SiderBarRef.onTabClick(key, source)
    }
    saveTab = () => {
        const { data } = this.props;
        this.props.saveExperiment(data);
    }
    changeContent (key: any, value: any) {
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
                tab={<span><Icon className='c-panel__siderbar__title__icon' type="bars" />组件参数</span>}
                key='params'
                disabled={isEmpty(selectedCell)}
            >
                <Params data={data.detailData} />
            </Tabs.TabPane>,
            <Tabs.TabPane
                tab={<span><Icon className='c-panel__siderbar__title__icon' type="question-circle-o" />组件说明</span>}
                key='description'
                disabled={isEmpty(selectedCell)}
            >
                <Description />
            </Tabs.TabPane>,
            <Tabs.TabPane
                tab={<span><Icon className='c-panel__siderbar__title__icon' type="rocket" />调度周期</span>}
                key='scheduleConf'
            >
                <SchedulingConfig
                    formData={Object.assign(JSON.parse(data.scheduleConf || '{}'), { scheduleStatus: data.scheduleStatus })}
                    onChange={(newFormData: any) => {
                        this.changeContent('scheduleStatus', newFormData.scheduleStatus);
                        this.debounceChangeContent('scheduleConf', JSON.stringify(newFormData));
                    }}
                />
            </Tabs.TabPane>
        ]
    }
    renderRightCustomButtons = () => {
        const btnStyle: any = { padding: '0 10px' };
        return (
            <React.Fragment>
                <Tooltip placement="bottom" title="布局">
                    <Button style={btnStyle}>
                        <MyIcon type="flowchart" onClick={this.layout}/>
                    </Button>
                </Tooltip>
                <Tooltip placement="bottom" title="放大">
                    <Button style={btnStyle}>
                        <MyIcon onClick={this.zoomIn} type="zoom-in"/>
                    </Button>
                </Tooltip>
                <Tooltip placement="bottom" title="缩小">
                    <Button style={btnStyle}>
                        <MyIcon onClick={this.zoomOut} type="zoom-out"/>
                    </Button>
                </Tooltip>
                <Tooltip placement="bottom" title="搜索节点">
                    <Button
                        icon="search"
                        style={{ fontSize: '17px', paddingRight: 0 }}
                        onClick={this.showSearchNode}>
                    </Button>
                </Tooltip>
            </React.Fragment>
        )
    }
    handleRenderToolbarOptions = () => {
        return <Button
            icon="save"
            {...{ title: '保存' }}
            onClick={this.saveTab}
        >
            保存
        </Button>
    }
    renderPublishButton () {
        const { data } = this.props;
        return <PublishButtons
            data={data}
            name='实验'
            disabled={data.isDirty}
            onSubmit={(values: any) => {
                return this.props.submitExperiment({
                    ...data,
                    ...values
                })
            }}
            onSubmitModel={(values: any) => {
                return this.props.submitExperimentModel({
                    taskId: data.id,
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
        const running = get(editor, 'running', []);
        const toolbarOpts: any = {
            enable: true,
            enableRun: true,
            disableEdit: true,
            customToobar: this.handleRenderToolbarOptions(),
            leftCustomButton: this.renderPublishButton(),
            rightCustomButton: this.renderRightCustomButtons(),
            isRunning: running.findIndex((o: any) => o == currentTabId) > -1,
            onRun: this.execConfirm,
            onStop: this.stopTask
        };
        const consoleOpts: any = {
            data: resultData,
            onConsoleClose: this.closeConsole,
            onRemoveTab: this.removeConsoleTab,
            downloadUri: reqUrls.DOWNLOAD_SQL_RESULT,
            tabOptions: {
                activeKey: consoleActivekey

            }
        };
        return (
            <CommonEditor
                console={consoleOpts}
                toolbar={toolbarOpts}
                siderBarItems={this.renderSiderbarItems()}
                SiderBarRef={(SiderBarRef: any) => this.SiderBarRef = SiderBarRef}
            >
                <GraphContainer
                    onRefGraph={(graph: any) => { this.GraphInstance = graph; }}
                    onRef={(graphContainer: any) => {
                        this.GraphContainer = graphContainer;
                    }}
                    isRunning={toolbarOpts.isRunning}
                    changeSiderbar={this.changeSiderbar}
                    currentTab={currentTab}
                    data={data} />
            </CommonEditor>
        );
    }
    layout = () => {
        this.GraphInstance.layout();
    }

    zoomIn = () => {
        this.GraphInstance.zoomIn();
    }

    zoomOut = () => {
        this.GraphInstance.zoomOut();
    }

    showSearchNode = () => {
        this.GraphContainer.initShowSearch();
    }
}

export default GraphPanel;
