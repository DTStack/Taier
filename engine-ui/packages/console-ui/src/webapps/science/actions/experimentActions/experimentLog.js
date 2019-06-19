import { siderBarType, consoleKey } from '../../consts';
import editorAction from '../../consts/editorActionType';
import { setOutput, output, outputRes } from '../editorActions';

function changeConsoleKey (tabId, activeKey) {
    return {
        type: editorAction.CHANGE_TABS_KEY,
        payload: {
            tabId,
            activeKey,
            siderType: siderBarType.experiment
        }
    }
}

function showExperimentLog (tabId) {
    return changeConsoleKey(tabId, consoleKey);
}

function setExperimentLog (tabId, log) {
    return setOutput(tabId, log, consoleKey, siderBarType.experiment, {
        name: '日志',
        disableClose: true
    });
}

function appendExperimentLog (tabId, log) {
    return output(tabId, log, consoleKey, siderBarType.experiment);
}

function setExperimentResult (tabId, jobId, data) {
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
