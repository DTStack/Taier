import * as React from 'react';
import PropTypes from 'prop-types';
import SplitPane from 'react-split-pane';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import { Scrollable } from 'molecule/esm/components';
import DataSync from './dataSync';
import { workbenchActions, getDataSyncReqParams } from '../../controller/dataSync/offlineAction';
import * as editorActions from '../../controller/editor/editorAction';
import { DATA_SYNC_MODE, TASK_TYPE } from '../../comm/const';
import { cloneDeep, assign } from 'lodash';

const propType: any = {
    editor: PropTypes.object,
    toolbar: PropTypes.object,
    console: PropTypes.object,
};
const initialState = {
    changeTab: true,
    size: undefined,
    runTitle: 'Command/Ctrl + R',
};
type Istate = typeof initialState;

@(connect(
    (state: any) => {
        const { workbench, dataSync } = state.dataSync;
        const { currentTab, tabs } = workbench;
        const currentTabData = tabs.filter((tab: any) => {
            return tab.id === currentTab;
        })[0];

        return {
            editor: state.editor,
            project: state.project,
            user: state.user,
            currentTab,
            currentTabData,
            dataSync,
        };
    },
    (dispatch: any) => {
        const taskAc = workbenchActions(dispatch);
        const editorAc = bindActionCreators(editorActions, dispatch);
        const actions = Object.assign(editorAc, taskAc);
        return actions;
    }
) as any)
class DataSyncWorkbench extends React.Component<any, Istate> {
    state = {
        changeTab: true,
        size: undefined,
        runTitle: 'Command/Ctrl + R',
    };

    static propTypes = propType;
    
    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps(nextProps: any) {
        const current = nextProps.currentTabData;
        const old = this.props.currentTabData;
        if (current && current.id !== old.id) {
            this.props.getTab(current.id);
        }
    }

    changeTab = (state: any) => {
        let changeTab = false;
        if (state) {
            changeTab = true;
        } else {
            changeTab = false;
        }

        this.setState({
            changeTab,
        });
    };
    /**
     * @description 拼装接口所需数据格式
     * @param {any} data 数据同步job配置对象
     * @returns {any} result 接口所需数据结构
     * @memberof DataSync
     */

    generateRqtBody () {
        const { currentTabData, dataSync } = this.props;

        // deepClone避免直接mutate store
        let reqBody = cloneDeep(currentTabData);
        // 如果当前任务为数据同步任务
        if (currentTabData.taskType === TASK_TYPE.SYNC) {
            const isIncrementMode = currentTabData.syncModel !== undefined && DATA_SYNC_MODE.INCREMENT === currentTabData.syncModel;
            reqBody = assign(reqBody, getDataSyncReqParams(dataSync));
            if (!isIncrementMode) {
                reqBody.sourceMap.increColumn = undefined; // Delete increColumn
            }
        }
        // 修改task配置时接口要求的标记位
        reqBody.preSave = true;

        // 接口要求上游任务字段名修改为dependencyTasks
        if (reqBody.taskVOS) {
            reqBody.dependencyTasks = reqBody.taskVOS.map((o: any) => o);
            reqBody.taskVOS = null;
        }

        // 删除不必要的字段
        delete reqBody.taskVersions;
        delete reqBody.dataSyncSaved;

        // 数据拼装结果
        return reqBody;
    }


    saveTab (isSave: any, saveMode: any) {
        // 每次保存都意味着当前tab不是第一次打开，重置当前标示
        this.setState({
            changeTab: false
        });
        console.log('******** props', this.props)
        const isButtonSubmit = saveMode == 'popOut';
        this.props.isSaveFInish(false);
        const { saveTab } = this.props;
        console.log('******** props', this.props)

        const saveData = this.generateRqtBody();
        const type = 'task'

        saveTab(saveData, isSave, type, isButtonSubmit);
    }


    render() {
        const { currentTabData } = this.props;

        return (
            <Scrollable>
                <div className="ide-editor">
                    <div style={{ zIndex: 901 }} className="ide-content">
                        <SplitPane
                            split="horizontal"
                            minSize={100}
                            maxSize={-77}
                            primary="first"
                            key={`ide-split-pane`}
                        >
                            <div
                                style={{
                                    width: '100%',
                                    height: '100%',
                                    minHeight: '400px',
                                    position: 'relative',
                                }}
                            >
                                <DataSync saveTab={this.saveTab.bind(this, true)} currentTabData={currentTabData} />
                            </div>
                        </SplitPane>
                    </div>
                </div>
            </Scrollable>
        );
    }
}

export default DataSyncWorkbench;
