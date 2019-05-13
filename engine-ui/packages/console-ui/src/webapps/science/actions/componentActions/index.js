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
    return (dispatch, getState) => {
        const currentTab = getState().experiment.currentTabIndex
        dispatch({
            type: componentActionType.SAVE_GRAPH,
            payload: {
                graph: payload,
                currentTab
            }
        })
    }
}
