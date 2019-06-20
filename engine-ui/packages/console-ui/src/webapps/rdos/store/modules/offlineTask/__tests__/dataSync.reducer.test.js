// dataSync-redux.test.js 数据同步测试

import {
    keymap,
    sourceMap,
    targetMap
} from '../dataSync';
import {
    keyMapAction,
    dataSyncAction,
    sourceMapAction
} from '../actionType';

describe('keymap reducer', () => {
    let initalState = { source: [], target: [] };
    const payload = {
        source: { format: '', type: 'string', value: 'test', key: 'aaa' },
        target: { type: 'string', key: 'ip' }
    }
    test('ADD_LINKED_KEYS', async () => {
        initalState = keymap(initalState, {
            type: keyMapAction.ADD_LINKED_KEYS,
            payload: payload
        })
        expect(initalState.source).toMatchObject([payload.source]);
        expect(initalState.target).toMatchObject([payload.target]);
    })
    test('EDIT_KEYMAP_TARGET', async () => {
        const editTarget = Object.assign({}, payload.target, {
            type: 'number'
        })
        const editState = keymap(initalState, {
            type: keyMapAction.EDIT_KEYMAP_TARGET,
            payload: editTarget
        })
        expect(initalState.target).toMatchObject(editState.target);
    })
    test('DEL_LINKED_KEYS', async () => {
        initalState = keymap(initalState, {
            type: keyMapAction.DEL_LINKED_KEYS,
            payload: payload
        })
        expect(initalState.source).not.toMatchObject([payload.source]);
        expect(initalState.target).not.toMatchObject([payload.target]);
    })
})

describe('sourceMap reducer', () => {
    let initalState = { sourceList: [] };
    const payload = {
        key: 'testSourceMapKey'
    }
    const expectedValue = {
        name: null,
        sourceId: null,
        type: null,
        tables: [],
        key: payload.key
    };

    test('DATA_SOURCE_ADD', async () => {
        initalState = sourceMap(initalState, {
            type: sourceMapAction.DATA_SOURCE_ADD,
            key: payload.key
        })
        expect(initalState.sourceList).toMatchObject([expectedValue]);
    })
})

describe('targetMap reducer', () => {
    let initalState = {};
    const payload = {
        targetMap: {
            sourceId: 'targetMapNameSourceId',
            name: 'targetMapName',
            type: 5
        }
    }
    test('INIT_JOBDATA', async () => {
        initalState = targetMap(initalState, {
            type: dataSyncAction.INIT_JOBDATA,
            payload: payload
        })
        expect(initalState).toMatchObject(payload.targetMap);
    })
})
