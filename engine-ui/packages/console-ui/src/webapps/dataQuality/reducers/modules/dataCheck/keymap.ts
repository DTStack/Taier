import { keyMapActionType } from '../../../consts/keyMapActionType';
import { cloneDeep } from 'lodash';

const initialState: any = {
    source: [],
    target: []
};

export default function keymap (state = initialState, action: any) {
    switch (action.type) {
        case keyMapActionType.ADD_LINKED_KEYS: {
            const map = action.payload;
            const clone = cloneDeep(state);
            const { source, target } = clone;

            const checkExist = (arr: any, item: any) => {
                return arr.indexOf(item) > -1;
            };

            if (checkExist(source, map.source)) {
                return state;
            } else if (checkExist(target, map.target)) {
                return state;
            } else {
                clone.source = [...clone.source, map.source];
                clone.target = [...clone.target, map.target];
            }

            return clone;
        }

        case keyMapActionType.DEL_LINKED_KEYS: {
            const map = action.payload;
            const clone = cloneDeep(state);
            const { source, target } = clone;

            source.splice(source.indexOf(map.source), 1);
            target.splice(target.indexOf(map.target), 1);

            clone.source = source;
            clone.target = target;

            return clone;
        }

        case keyMapActionType.SET_EDIT_MAP: {
            const map = action.payload;
            const clone = cloneDeep(state);

            clone.source = map.source;
            clone.target = map.target;

            return clone;
        }

        case keyMapActionType.SET_ROW_MAP: {
            const { sourceKeyCol, targetKeyCol } = action.payload;
            let source: any = [];
            let target: any = [];

            sourceKeyCol.forEach((key: any, i: any) => {
                if (targetKeyCol[i]) {
                    source.push(key);
                    target.push(targetKeyCol[i]);
                }
            });

            return { source, target };
        }

        case keyMapActionType.SET_NAME_MAP: {
            const { sourceKeyCol, targetKeyCol } = action.payload;
            let source: any = [];
            let target: any = [];

            sourceKeyCol.forEach((key: any, i: any) => {
                if (targetKeyCol.indexOf(key) > -1) {
                    source.push(key);
                    target.push(key);
                }
            });

            return { source, target };
        }

        case keyMapActionType.RESET_LINKED_KEYS:
            return { source: [], target: [] };

        default:
            return state;
    }
}
