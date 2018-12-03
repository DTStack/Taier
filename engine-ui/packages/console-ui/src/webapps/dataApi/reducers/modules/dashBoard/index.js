import { dashBoardActionType } from '../../../consts/dashBoardActionType';
import { cloneDeep } from 'lodash';

const initialState = {
    loading: false,
    approvalWarning: true,
    topRecords: [],
    alarmTrend: [],
    alarmSum: {},
    usage: {},
    userDate: '1',
    adminDate: '1',
    approvedMsgCount: 0, // 审批信息
    marketOverview: {// 市场概览
        '1': {
            callInfo: {// 调用情况
                callCount: 0, // 调用总数
                failPercent: 0, // 失败率
                callTopAPI: '', // 调用最多api
                infoList: [// 调用信息列表

                ]
            },
            callCountTop: [], // 调用次数排行
            callFailTop: [], // 调用失败率排行
            failInfoList: [], // 错误信息列表
            topCallFunc: []// 调用方法排行
        },
        '7': {
            callInfo: {// 调用情况
                callCount: 0, // 调用总数
                failPercent: 0, // 失败率
                callTopAPI: '', // 调用最多api
                infoList: [// 调用信息列表

                ]
            },
            callCountTop: [], // 调用次数排行
            callFailTop: [], // 调用失败率排行
            failInfoList: [], // 错误信息列表
            topCallFunc: []// 调用方法排行
        },
        '30': {
            callInfo: {// 调用情况
                callCount: 0, // 调用总数
                failPercent: 0, // 失败率
                callTopAPI: '', // 调用最多api
                infoList: [// 调用信息列表

                ]
            },
            callCountTop: [], // 调用次数排行
            callFailTop: [], // 调用失败率排行
            failInfoList: [], // 错误信息列表
            topCallFunc: []// 调用方法排行
        }

    },
    userOverview: {// 用户调用概览
        '1': {
            callInfo: {// 调用情况
                callCount: 0, // 调用总数
                failPercent: 0, // 失败率
                callTopAPI: '', // 调用最多api
                infoList: [// 调用信息列表
                ]
            },
            callCountTop: [], // 调用次数排行
            callFailTop: [], // 调用失败率排行
            approvalInfo: {// 审批情况
                approvingNum: 0,
                approvedNum: 0,
                stoppedNum: 0
            }
        },
        '7': {
            callInfo: {// 调用情况
                callCount: 0, // 调用总数
                failPercent: 0, // 失败率
                callTopAPI: '', // 调用最多api
                infoList: [// 调用信息列表
                ]
            },
            callCountTop: [], // 调用次数排行
            callFailTop: [], // 调用失败率排行
            approvalInfo: {// 审批情况
                approvingNum: 0,
                approvedNum: 0,
                stoppedNum: 0
            }
        },
        '30': {
            callInfo: {// 调用情况
                callCount: 0, // 调用总数
                failPercent: 0, // 失败率
                callTopAPI: '', // 调用最多api
                infoList: [// 调用信息列表
                ]
            },
            callCountTop: [], // 调用次数排行
            callFailTop: [], // 调用失败率排行
            approvalInfo: {// 审批情况
                approvingNum: 0,
                approvedNum: 0,
                stoppedNum: 0
            }
        }

    }
}

export default function dashBoard (state = initialState, action) {
    const { type, payload, date } = action;
    switch (type) {
        case dashBoardActionType.GET_USER_CALL_INFO: {
            const clone = cloneDeep(state);
            clone.userOverview[date].callInfo = payload;
            return clone;
        }
        case dashBoardActionType.GET_API_FAIL_RANK: {
            const clone = cloneDeep(state);
            clone.userOverview[date].callFailTop = payload;
            return clone;
        }
        case dashBoardActionType.GET_USER_API_CALL_RANK: {
            const clone = cloneDeep(state);
            clone.userOverview[date].callCountTop = payload;
            return clone;
        }
        case dashBoardActionType.GET_USER_API_SUB_INFO: {
            const clone = cloneDeep(state);
            clone.userOverview[date].approvalInfo = payload;
            return clone;
        }
        case dashBoardActionType.GET_MARKET_API_FAIL_RANK: {
            const clone = cloneDeep(state);
            clone.marketOverview[date].callFailTop = payload;
            return clone;
        }
        case dashBoardActionType.GET_MARKET_CALL_INFO: {
            const clone = cloneDeep(state);
            clone.marketOverview[date].callInfo = payload;
            return clone;
        }
        case dashBoardActionType.GET_MARKET_API_CALL_RANK: {
            const clone = cloneDeep(state);
            clone.marketOverview[date].callCountTop = payload;
            return clone;
        }
        case dashBoardActionType.GET_MARKET_API_ERROR_INFO: {
            const clone = cloneDeep(state);
            clone.marketOverview[date].failInfoList = payload;
            return clone;
        }
        case dashBoardActionType.GET_MARKET_TOP_CALL_FUNC: {
            const clone = cloneDeep(state);
            clone.marketOverview[date].topCallFunc = payload;
            return clone;
        }
        case dashBoardActionType.GET_MARKET_API_APPLY_INFO: {
            const clone = cloneDeep(state);
            clone.approvedMsgCount = payload;
            return clone;
        }

        case dashBoardActionType.CHANGE_LOADING: {
            const clone = cloneDeep(state);
            const { loading } = clone;
            clone.loading = !loading;
            return clone;
        }
        case dashBoardActionType.GET_TOP_RECORD: {
            const clone = cloneDeep(state);
            clone.topRecords = payload;
            return clone;
        }

        case dashBoardActionType.GET_ALARM_SUM: {
            const clone = cloneDeep(state);
            clone.alarmSum = payload;
            return clone;
        }

        case dashBoardActionType.GET_ALARM_TREND: {
            const clone = cloneDeep(state);
            clone.alarmTrend = payload;
            return clone;
        }

        case dashBoardActionType.GET_USAGE: {
            const clone = cloneDeep(state);
            clone.usage = payload;
            return clone;
        }
        case dashBoardActionType.CHOOSE_USER_DATE: {
            const clone = cloneDeep(state);
            clone.userDate = payload;
            return clone;
        }
        case dashBoardActionType.CHOOSE_ADMIN_DATE: {
            const clone = cloneDeep(state);
            clone.adminDate = payload;
            return clone;
        }
        case dashBoardActionType.CLOSE_APPROVAL_WARNING: {
            const clone = cloneDeep(state);
            clone.approvalWarning = false;
            return clone;
        }

        default:
            return state;
    }
}
