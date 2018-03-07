import { keyMapActions } from '../../../consts/keyMapActions';
import { cloneDeep, isEqual } from 'lodash';

const initialState = {
    source: [],
    target: [],
}

export default function keymap(state = initialState, action) {
    switch(action.type) {
        
        case keyMapActions.ADD_LINKED_KEYS: {
            const map = action.payload;
            const clone = cloneDeep(state);
            const { source, target } = clone;

            const checkExist = (arr, item) => {
                let bl = false;

                for(let o of arr) {
                    if(isEqual(o, item)) {
                        bl = true;
                        break;
                    }
                }

                return bl;
            }

            if(checkExist(source, map.source)) {
                return state;
            }
            else if(checkExist(target, map.target)) {
                return state;
            }
            else{
                clone.source = [...clone.source, map.source];
                clone.target = [...clone.target, map.target];
            }

            return clone;
        }

        case keyMapActions.DEL_LINKED_KEYS: {
            const map = action.payload;
            const clone = cloneDeep(state);
            const { source, target } = clone;
            const mapSource = map.source;
            const mapTarget = map.target;
            const newSource = source.filter(key_obj => !isEqual(key_obj, mapSource));
            const newTarget = target.filter(key_obj => !isEqual(key_obj, mapTarget));

            clone.source = newSource;
            clone.target = newTarget;

            return clone;
        }

        case keyMapActions.SET_ROW_MAP: {
            const { targetCol, sourceCol } = action.payload;
            let source = [], target = [];

            sourceCol.forEach((o, i) => {
                if(targetCol[i]) {
                    source.push(o);
                    target.push(targetCol[i]);
                }
            });

            return {source, target};
        }

        case keyMapActions.SET_NAME_MAP: {
            let { targetCol, sourceCol, targetSrcType } = action.payload;
            let source = [], target = [];

            let targetNameCol = targetCol.map(o => o.key);
            sourceCol.forEach((o, i) => {
                let name = o.key;
                let idx = targetNameCol.indexOf(name);

                if( idx !== -1) {
                    source.push(name);
                    target.push(DATA_TYPE_ARRAY.indexOf(+targetSrcType) !== -1? name: targetCol[idx]);
                }
            });

            return {source, target};
        }

        case keyMapActions.EDIT_KEYMAP_TARGET: {
            const map = action.payload;
            const { old, replace } = map
            const clone = cloneDeep(state);
            if (map) {
                const index = clone.target.findIndex((item) => isEqual(item, old))
                if (index > 0) {
                    clone.target[index] = replace;
                    return clone;
                }
            }
            return state;
        }

        // 移除
        case keyMapActions.REMOVE_KEYMAP: {
            const map = action.payload;
            const { source, target } = map
            const clone = cloneDeep(state);
            if (source) {
                const index = clone.source.findIndex((item) => isEqual(item, source))
                if (index > 0) {
                    clone.source.splice(index, 1)
                    clone.target.splice(index, 1)
                    return clone;
                }
            } else if (target) {
                const index = clone.target.findIndex((item) => isEqual(item, target))
                if (index > 0) {
                    clone.source.splice(index, 1)
                    clone.target.splice(index, 1)
                    return clone;
                }
            }
            return state;
        }

        case keyMapActions.RESET_LINKED_KEYS:
            return { source: [], target: [] };

        default: return state;
    }
};