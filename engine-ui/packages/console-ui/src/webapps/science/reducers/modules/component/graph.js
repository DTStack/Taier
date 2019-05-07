import componentActionType from '../../../consts/componentActionType';
export function graph (state = {}, action) {
    const { type, payload } = action;
    switch (type) {
        case componentActionType.SAVE_GRAPH: {
            return payload;
        }
        default: {
            return state;
        }
    }
}

export function selectedCell (state = {}, action) {
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
