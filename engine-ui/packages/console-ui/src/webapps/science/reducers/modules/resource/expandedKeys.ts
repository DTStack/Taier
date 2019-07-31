
import { resourceFilesType } from '../../../consts/actionType/filesType';
function expandedKeys (state = [], action: any) {
    const { type, payload } = action;
    switch (type) {
        case resourceFilesType.UPDATE_EXPANDEDKEYS: {
            return payload;
        }
        case resourceFilesType.REMOVE_EXPANDEDKEYS: {
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
