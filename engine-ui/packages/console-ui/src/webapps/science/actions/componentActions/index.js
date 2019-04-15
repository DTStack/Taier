import componentActionType from '../../consts/componentActionType';
export function saveGraph (payload) {
    return {
        type: componentActionType.SAVE_GRAPH,
        payload
    }
}
