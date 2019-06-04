import { userActions, clusterActions } from '../../consts/consoleActions'
import { cloneDeep } from 'lodash'
const defaultState = {
    userList: [],
    engineList: [{
        'engineName': 'Libra',
        'engineId': 2
    }, {
        'engineName': 'HADOOP',
        'engineId': 1
    }], // engine列表 （hadoop, libra）
    hadoopComponentList: [],
    libraComponentList: []
}
export default function (state = defaultState, action) {
    switch (action.type) {
        case userActions.SET_USER_LIST: {
            const list = action.data;
            const newState = cloneDeep(state)
            newState.userList = list;
            return newState
        }
        case clusterActions.UPDATE_ENGINE_LIST: {
            const list = action.data;
            const newState = cloneDeep(state);
            newState.engineList = list;
            return newState
        }
        case clusterActions.UPDATE_HADOOP_COMPONENT_LIST: {
            const list = action.data;
            const newState = cloneDeep(state);
            newState.hadoopComponentList = list;
            return newState
        }
        case clusterActions.UPDATE_LIBRA_COMPONENT_LIST: {
            const list = action.data;
            const newState = cloneDeep(state);
            newState.libraComponentList = list;
            return newState
        }
        default:
            return state
    }
}
