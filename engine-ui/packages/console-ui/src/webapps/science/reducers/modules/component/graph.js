import componentActionType from '../../../consts/componentActionType';
function graph (state = {}, action) {
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

export default graph
