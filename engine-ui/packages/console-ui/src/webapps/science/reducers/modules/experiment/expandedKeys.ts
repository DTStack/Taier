
import { experimentFilesType } from '../../../consts/actionType/filesType';
function expandedKeys (state: any[] = [], action: any) {
    const { type, payload } = action;
    switch (type) {
        case experimentFilesType.UPDATE_EXPANDEDKEYS: {
            return payload;
        }
        case experimentFilesType.REMOVE_EXPANDEDKEYS: {
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
