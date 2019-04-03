import modalAction from '../../consts/modalActionType';
import { CATALOGUE_TYPE } from '../../consts';
import API from '../../api';

/**
 * 更新Modal对象
 * @param {Object} value 包含Modal对象的类型和数据
 */
export const updateModal = (value) => {
    return { type: modalAction.UPDATE_MODAL, data: value }
}

/**
 * 关闭页面Modal
 */
export const resetModal = () => {
    return { type: modalAction.RESET_MODAL }
}

/**
 * 加载左侧树形目录数据
 */
export const loadCatalogue = function (data, fileType) {
    return async (dispatch) => {
        let res = {};
        switch (fileType) {
            case CATALOGUE_TYPE.TABLE: { // 获取表下的DataMap
                res = await API.getDataMapsByTable({
                    tableId: data.id,
                    databaseId: data.databaseId
                });
                res.data = res.data && res.data.map(item => {
                    item.type = CATALOGUE_TYPE.DATA_MAP;
                    return item;
                });
                data.type = fileType;
                break;
            }
            case CATALOGUE_TYPE.DATA_BASE: {
                res = await API.getTablesByDB({
                    databaseId: data.id
                });
                res.data = res.data && res.data.map(item => {
                    item.type = CATALOGUE_TYPE.TABLE;
                    item.children = [];
                    return item;
                });
                data.type = fileType;
                break;
            }
        }

        // if (res.code === 1) {
        //     data.children = res.data;
        //     dispatch({
        //         type: workbenchAction.LOAD_CATALOGUE_DATA,
        //         payload: data
        //     })
        // }
    }
}
