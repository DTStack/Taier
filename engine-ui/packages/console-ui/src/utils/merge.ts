import _ from 'lodash';

function mergeDeep (object1: any, object2: any) {
    if (object1 == null || object2 == null) {
        return object2;
    } else if (!_.isPlainObject(object1) || !_.isPlainObject(object2)) {
        return object2;
    } else if (object1 === object2) {
        return object2;
    } else {
        if ('_isMergeAtom' in object2) {
            const isMergeAtom = object2._isMergeAtom;
            delete object2._isMergeAtom;

            if (isMergeAtom) {
                return object2;
            }
        }
        const obj = {
            ...object1
        };
        _.forEach(object2, (value, key) => {
            if (key in object1) {
                obj[key] = mergeDeep(object1[key], value);
            } else {
                obj[key] = value;
            }
        });

        return obj;
    }
}

export default {
    mergeDeep
}
