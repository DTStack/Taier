import {
    taskTreeAction,
    resTreeAction,
    sparkCustomFnTreeAction,
    sparkSysFnTreeActon,
} from './actionTypes';

export const updateCatalogueData = (dispatch: any, data: any, dataType: any) => {
    let action: any = null
    switch (dataType) {
        case 'task': 
            action = taskTreeAction
            break;
        case 'resource': 
            action = resTreeAction;
            break;
        case 'sparkCustomFunction':
            action = sparkCustomFnTreeAction;
            break;
        case 'sparkSysFunction': 
            action = sparkSysFnTreeActon;
            break;
        default: 
            action = taskTreeAction;
            break;
    }
    dispatch({
        type: action.LOAD_FOLDER_CONTENT,
        payload: data
    })
}