
import { notebookFilesType } from '../../../consts/actionType/filesType';
function expandedKeys (state = [], action) {
    const { type, payload } = action;
    switch (type) {
        case notebookFilesType.UPDATE_EXPANDEDKEYS: {
            return payload;
        }
        case notebookFilesType.REMOVE_EXPANDEDKEYS: {
            return state.filter((key) => {
                return key != payload;
            });
        }
        default: {
            return state;
        }
    }
}

export default expandedKeys;
