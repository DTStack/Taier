import componentActionType from '../../consts/componentActionType';
import { isEqual } from 'lodash'
export function saveSelectedCell (payload: any, cb: any) {
    return (dispatch: any, getState: any) => {
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
export function saveGraph (payload: any) {
    return (dispatch: any, getState: any) => {
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
