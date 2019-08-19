import { ruleConfigActionType } from '../../consts/ruleConfigActionType';
import { hashHistory } from 'react-router';
import { message } from 'antd';
import API from '../../api/ruleConfig';

export const ruleConfigActions: any = {
    getMonitorLists (params: any) {
        return (dispatch: any) => {
            dispatch({
                type: ruleConfigActionType.CHANGE_LOADING
            });
            return API.getMonitorLists(params).then((res: any) => {
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
    getRuleFunction (params: any) {
        return (dispatch: any) => {
            API.getRuleFunction(params).then((res: any) => {
                if (res.code === 1) {
                    dispatch({
                        type: ruleConfigActionType.GET_RULE_FUNCTION,
                        payload: res.data
                    });
                }
            });
        }
    },
    getTableColumn (params: any) {
        return (dispatch: any) => {
            API.getTableColumn(params).then((res: any) => {
                if (res.code === 1) {
                    dispatch({
                        type: ruleConfigActionType.GET_MONITOR_TABLE_COLUMN,
                        payload: res.data
                    });
                }
            });
        }
    },
    addMonitor (params: any) {
        return (dispatch: any) => {
            API.addMonitor(params).then((res: any) => {
                if (res.code === 1) {
                    message.success('添加成功！');
                    setTimeout(() => {
                        hashHistory.push('/dq/rule');
                    }, 1000);
                }
            });
        }
    },
    updateMonitor (params: any) {
        return (dispatch: any) => {
            API.updateMonitor(params).then((res: any) => {
                if (res.code === 1) {
                    message.success('更新成功！');
                }
            });
        }
    },
    getMonitorRule (params: any) {
        return (dispatch: any) => {
            API.getMonitorRule(params).then((res: any) => {
                if (res.code === 1) {
                    dispatch({
                        type: ruleConfigActionType.GET_MONITOR_RULE,
                        payload: res.data
                    });
                }
            });
        }
    },
    changeMonitorStatus (params: any) {
        return (dispatch: any) => {
            API.changeMonitorStatus(params).then((res: any) => {
                if (res.code === 1) {
                    message.success('操作成功！');
                }
            });
        }
    },
    getMonitorDetail (params: any) {
        return (dispatch: any) => {
            API.getMonitorDetail(params).then((res: any) => {
                if (res.code === 1) {
                    dispatch({
                        type: ruleConfigActionType.GET_MONITOR_DETAIL,
                        payload: res.data
                    });
                }
            });
        }
    },
    getRemoteTrigger (params: any) {
        return (dispatch: any) => {
            API.getRemoteTrigger(params).then((res: any) => {
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
