import { keyMapActions as ACTION_TYPE } from '../../consts/keyMapActions';

// keyMap模块
export const keyMapActions = (dispatch: any) => {
    return {
        addLinkedKeys: (params: any) => {
            dispatch({
                type: ACTION_TYPE.ADD_LINKED_KEYS,
                payload: params
            });
        },
        delLinkedKeys: (params: any) => {
            dispatch({
                type: ACTION_TYPE.DEL_LINKED_KEYS,
                payload: params
            });
        },
        setEditMap: (params: any) => {
            dispatch({
                type: ACTION_TYPE.SET_EDIT_MAP,
                payload: params
            });
        },
        setRowMap: (params: any) => {
            dispatch({
                type: ACTION_TYPE.SET_ROW_MAP,
                payload: params
            });
        },
        setNameMap: (params: any) => {
            dispatch({
                type: ACTION_TYPE.SET_NAME_MAP,
                payload: params
            });
        },
        resetLinkedKeys: () => {
            dispatch({
                type: ACTION_TYPE.RESET_LINKED_KEYS
            });
        },
        removeSourceKeyRow (source: any, index: any) {
            dispatch({
                type: ACTION_TYPE.REMOVE_KEYMAP,
                payload: { source }
            });
        },
        editKeyMapTarget (params: any) {
            dispatch({
                type: ACTION_TYPE.EDIT_KEYMAP_TARGET,
                payload: params
            });
        },
        removeTargetKeyRow (target: any, index: any) {
            dispatch({
                type: ACTION_TYPE.REMOVE_KEYMAP,
                payload: { target }
            });
        }
    }
};
