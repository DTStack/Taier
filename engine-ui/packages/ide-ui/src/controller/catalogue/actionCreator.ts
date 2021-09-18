import {
    taskTreeAction,
    resTreeAction,
    functionTreeAction,
} from './actionTypes';

export const updateCatalogueData = (
    dispatch: any,
    data: any,
    dataType: any
) => {
    let action: any = null;
    switch (dataType) {
        case 'task':
            action = taskTreeAction;
            break;
        case 'resource':
            action = resTreeAction;
            break;
        case 'function':
            action = functionTreeAction;
            break;
        default:
            action = taskTreeAction;
            break;
    }
    dispatch({
        type: action.LOAD_FOLDER_CONTENT,
        payload: data,
    });
};
