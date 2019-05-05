import componentActionType from '../../consts/componentActionType';
import { isEqual } from 'lodash'
export function saveSelectedCell (payload, cb) {
    return (dispatch, getState) => {
        const cell = getState().component.selectedCell;
        if (!isEqual(cell, payload)) {
            dispatch({
                type: componentActionType.SAVE_SELECTED_CELL,
                payload
            })
            cb && cb();
        }
    }
}
export function saveGraph (payload) {
    return {
        type: componentActionType.SAVE_GRAPH,
        payload
    }
}
