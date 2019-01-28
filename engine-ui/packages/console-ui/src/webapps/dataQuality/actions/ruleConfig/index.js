import { ruleConfigActionType } from '../../consts/ruleConfigActionType';
import { hashHistory } from 'react-router';
import { message } from 'antd';
import API from '../../api/ruleConfig';

export const ruleConfigActions = {
    getMonitorLists (params) {
        return dispatch => {
            dispatch({
                type: ruleConfigActionType.CHANGE_LOADING
            });
            return API.getMonitorLists(params).then((res) => {
                dispatch({
                    type: ruleConfigActionType.CHANGE_LOADING
                });
                if (res.code === 1) {
                    dispatch({
                        type: ruleConfigActionType.GET_MONITOR_LIST,
                        payload: res.data
                    });
                    return res;
                }
            });
        }
    },
    getRuleFunction (params) {
        return dispatch => {
            API.getRuleFunction(params).then((res) => {
                if (res.code === 1) {
                    dispatch({
                        type: ruleConfigActionType.GET_RULE_FUNCTION,
                        payload: res.data
                    });
                }
            });
        }
    },
    getTableColumn (params) {
        return dispatch => {
            API.getTableColumn(params).then((res) => {
                if (res.code === 1) {
                    dispatch({
                        type: ruleConfigActionType.GET_MONITOR_TABLE_COLUMN,
                        payload: res.data
                    });
                }
            });
        }
    },
    addMonitor (params) {
        return dispatch => {
            API.addMonitor(params).then((res) => {
                if (res.code === 1) {
                    message.success('添加成功！');
                    setTimeout(() => {
                        hashHistory.push('/dq/rule');
                    }, 1000);
                }
            });
        }
    },
    updateMonitor (params) {
        return dispatch => {
            API.updateMonitor(params).then((res) => {
                if (res.code === 1) {
                    message.success('更新成功！');
                }
            });
        }
    },
    getMonitorRule (params) {
        return dispatch => {
            API.getMonitorRule(params).then((res) => {
                if (res.code === 1) {
                    dispatch({
                        type: ruleConfigActionType.GET_MONITOR_RULE,
                        payload: res.data
                    });
                }
            });
        }
    },
    changeMonitorStatus (params) {
        return dispatch => {
            API.changeMonitorStatus(params).then((res) => {
                if (res.code === 1) {
                    message.success('操作成功！');
                }
            });
        }
    },
    getMonitorDetail (params) {
        return dispatch => {
            API.getMonitorDetail(params).then((res) => {
                if (res.code === 1) {
                    dispatch({
                        type: ruleConfigActionType.GET_MONITOR_DETAIL,
                        payload: res.data
                    });
                }
            });
        }
    },
    getRemoteTrigger (params) {
        return dispatch => {
            API.getRemoteTrigger(params).then((res) => {
                if (res.code === 1) {
                    dispatch({
                        type: ruleConfigActionType.GET_REMOTE_TRIGGER,
                        payload: res.data
                    });
                }
            });
        }
    }
}
