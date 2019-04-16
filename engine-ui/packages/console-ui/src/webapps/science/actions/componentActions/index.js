import componentActionType from '../../consts/componentActionType';
export function saveGraph (payload) {
    return {
        type: componentActionType.SAVE_GRAPH,
        payload
    }
}
export function saveSelectedCell (payload) {
    return {
        type: componentActionType.SAVE_SELECTED_CELL,
        payload
    }
}
