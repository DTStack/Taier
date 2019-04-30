import modalAction from '../../consts/modalActionType';
import commonActionType from '../../consts/commonActionType';
import { modalType } from '../../consts';

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

export function openNewNotebook (data) {
    return updateModal({
        visibleModal: modalType.newNotebook,
        modalData: data
    })
}

export function openNewExperiment (data) {
    return updateModal({
        visibleModal: modalType.newExperiment,
        modalData: data
    })
}

export function changeSiderBar (id) {
    return {
        type: commonActionType.CHANGE_SIDERBAR_KEY,
        payload: id
    }
}
