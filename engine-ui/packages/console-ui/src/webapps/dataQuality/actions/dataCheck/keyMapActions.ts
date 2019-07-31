import { keyMapActionType } from '../../consts/keyMapActionType';

// keyMap模块
export const keyMapActions = (dispatch: any) => {
    return {
        addLinkedKeys: (params: any) => {
            dispatch({
                type: keyMapActionType.ADD_LINKED_KEYS,
                payload: params
            });
        },
        delLinkedKeys: (params: any) => {
            dispatch({
                type: keyMapActionType.DEL_LINKED_KEYS,
                payload: params
            });
        },
        setEditMap: (params: any) => {
            dispatch({
                type: keyMapActionType.SET_EDIT_MAP,
                payload: params
            });
        },
        setRowMap: (params: any) => {
            dispatch({
                type: keyMapActionType.SET_ROW_MAP,
                payload: params
            });
        },
        setNameMap: (params: any) => {
            dispatch({
                type: keyMapActionType.SET_NAME_MAP,
                payload: params
            });
        },
        resetLinkedKeys: () => {
            dispatch({
                type: keyMapActionType.RESET_LINKED_KEYS
            });
        }
    }
};
