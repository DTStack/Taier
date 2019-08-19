import componentActionType from '../../../consts/componentActionType';
export function graph (state: any = {}, action: any) {
    const { type, payload } = action;
    switch (type) {
        case componentActionType.SAVE_GRAPH: {
            state[payload.currentTab] = payload.graph;
            return Object.assign({}, state);
        }
        default: {
            return state;
        }
    }
}

export function selectedCell (state: any = {}, action: any) {
    const { type, payload } = action;
    switch (type) {
        case componentActionType.SAVE_SELECTED_CELL: {
            return payload;
        }
        default: {
            return state;
        }
    }
}
