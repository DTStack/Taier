import modalAction from '../../consts/modalActionType';

const defaultModalData = {
    visibleModal: '', // visible modal
    modalData: '' // modal数据
}

const modalReducer = (state = defaultModalData, action) => {
    switch (action.type) {
        case modalAction.UPDATE_MODAL:
            return action.data;
        case modalAction.RESET_MODAL:
            return defaultModalData;
        default:
            return state;
    }
}

export default modalReducer
