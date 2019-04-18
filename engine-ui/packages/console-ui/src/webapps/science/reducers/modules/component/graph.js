import componentActionType from '../../../consts/componentActionType';
export function task (state = [], action) {
    const { type, payload } = action;
    switch (type) {
        case componentActionType.GET_TASK_DATA: {
            return payload;
        }
        case componentActionType.UPDATE_TASK_DATA: {
            for (let index = 0; index < payload.length; index++) {
                const element = payload[index];
                const object = state.find(o => o.id == element.id);
                if (object) {
                    object.x = element.x;
                    object.y = element.y;
                    object.data = element.data;
                }
            }
            return state;
        }
        case componentActionType.ADD_JOBID : {
            for (const key in payload) {
                if (payload.hasOwnProperty(key)) {
                    const element = payload[key];
                    const object = state.find(o => o.data.id == key);
                    object.data.jobId = element;
                }
            }
            return [].concat(state);
        }
        case componentActionType.CHANGE_TASK_STATUS: {
            for (const key in payload) {
                if (payload.hasOwnProperty(key)) {
                    const element = payload[key];
                    const object = state.find(o => o.data.id == key);
                    object.data.status = element;
                }
            }
            return [].concat(state);
        }
        default: {
            return state;
        }
    }
}
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
