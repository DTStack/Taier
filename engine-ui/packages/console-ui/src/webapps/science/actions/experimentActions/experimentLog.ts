import { siderBarType, consoleKey } from '../../consts';
import editorAction from '../../consts/editorActionType';
import { setOutput, output, outputRes } from '../editorActions';

function changeConsoleKey (tabId: any, activeKey: any) {
    return {
        type: editorAction.CHANGE_TABS_KEY,
        payload: {
            tabId,
            activeKey,
            siderType: siderBarType.experiment
        }
    }
}

function showExperimentLog (tabId: any) {
    return changeConsoleKey(tabId, consoleKey);
}

function setExperimentLog (tabId: any, log: any) {
    return setOutput(tabId, log, consoleKey, siderBarType.experiment, {
        name: '日志',
        disableClose: true
    });
}

function appendExperimentLog (tabId: any, log: any) {
    return output(tabId, log, consoleKey, siderBarType.experiment);
}

function setExperimentResult (tabId: any, jobId: any, data: any) {
    return outputRes(tabId, data, jobId, siderBarType.experiment, {
        name: '执行结果'
    })
}
export default {
    showExperimentLog,
    setExperimentLog,
    appendExperimentLog,
    setExperimentResult
}
