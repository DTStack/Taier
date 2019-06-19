
import { experimentFilesType } from '../../../consts/actionType/filesType';
function expandedKeys (state = [], action) {
    const { type, payload } = action;
    switch (type) {
        case experimentFilesType.UPDATE_EXPANDEDKEYS: {
            return payload;
        }
        default: {
            return state;
        }
    }
}

export default expandedKeys;
