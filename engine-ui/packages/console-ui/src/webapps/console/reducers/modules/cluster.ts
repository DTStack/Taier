import mc from 'mirror-creator';
import { DEFAULT_COMP_TEST, DEFAULT_COMP_REQUIRED } from '../../consts/index'
const clusterActions = mc([
    'UPDATE_TEST_RESULT',
    'UPDATE_REQUIRED_STATUS'
])

// actions
export const updateTestStatus = (data: any) => {
    return {
        type: clusterActions.UPDATE_TEST_RESULT,
        data: data
    }
}
export const updateRequiredStatus = (data: any) => {
    return {
        type: clusterActions.UPDATE_REQUIRED_STATUS,
        data: data
    }
}

// reducer
export function testStatus (state = DEFAULT_COMP_TEST, action: any) {
    switch (action.type) {
        case clusterActions.UPDATE_TEST_RESULT: {
            const data = action.data;
            return Object.assign({}, state, data)
        }
        default:
            return state
    }
}

export function showRequireStatus (state = DEFAULT_COMP_REQUIRED, action: any) {
    switch (action.type) {
        case clusterActions.UPDATE_REQUIRED_STATUS: {
            const data = action.data;
            return Object.assign({}, state, data)
        }
        default:
            return state
    }
}
