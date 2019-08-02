
import { notebookFilesType } from '../../../consts/actionType/filesType';
function expandedKeys (state: any[] = [], action: any) {
    const { type, payload } = action;
    switch (type) {
        case notebookFilesType.UPDATE_EXPANDEDKEYS: {
            return payload;
        }
        case notebookFilesType.REMOVE_EXPANDEDKEYS: {
            return state.filter((key: any) => {
                return key != payload;
            });
        }
        default: {
            return state;
        }
    }
}

export default expandedKeys;
