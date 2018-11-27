import { isEmpty, cloneDeep } from 'lodash';
import { keyMapActions as ACTION_TYPE } from '../../consts/keyMapActions';
import API from '../../api/dataSource';

// keyMap模块
export const keyMapActions = (dispatch) => {
    return {
        addLinkedKeys: (params) => {
            dispatch({
                type: ACTION_TYPE.ADD_LINKED_KEYS,
                payload: params
            });
        },
        delLinkedKeys: (params) => {
            dispatch({
                type: ACTION_TYPE.DEL_LINKED_KEYS,
                payload: params
            });
        },
        setEditMap: (params) => {
            dispatch({
                type: ACTION_TYPE.SET_EDIT_MAP,
                payload: params
            });
        },
        setRowMap: (params) => {
            dispatch({
                type: ACTION_TYPE.SET_ROW_MAP,
                payload: params
            });
        },
        setNameMap: (params) => {
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
        removeSourceKeyRow (source, index) {
            dispatch({
                type: ACTION_TYPE.REMOVE_KEYMAP,
                payload: { source }
            });
        },
        editKeyMapTarget (params) {
            dispatch({
                type: ACTION_TYPE.EDIT_KEYMAP_TARGET,
                payload: params
            });
        },
        removeTargetKeyRow (target, index) {
            dispatch({
                type: ACTION_TYPE.REMOVE_KEYMAP,
                payload: { target }
            });
        }
    }
};
