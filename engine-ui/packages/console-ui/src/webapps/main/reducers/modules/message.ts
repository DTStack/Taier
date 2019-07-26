import { assign } from 'lodash';

import msgActions from 'main/consts/msgActions'

const initalMsg = {
    currentPage: 1,
    msgType: '1'
}

export function msgList (state = initalMsg, action) {
    switch (action.type) {
        case msgActions.UPDATE_MSG: {
            if (action.data !== null) {
                return assign({}, state, action.data)
            }

            return state;
        }
        default:
            return state
    }
}
