import {
    console,
    selection,
    running,
    options,
    showRightExtraPane,
    syntaxPane,
    KEY_EDITOR_OPTIONS
} from '../index';
import { editorAction } from '../actionTypes';
import localDb from 'utils/localDb';

describe('editor reducer', () => {
    test('showRightExtraPane', () => {
        let state = showRightExtraPane(undefined, {
            type: editorAction.SHOW_RIGHT_PANE,
            data: editorAction.SHOW_TABLE_TIP_PANE
        })
        expect(state).toBe(editorAction.SHOW_TABLE_TIP_PANE);
        state = showRightExtraPane(state, {
            type: editorAction.SHOW_RIGHT_PANE,
            data: editorAction.SHOW_SYNTAX_HELP_PANE
        })
        expect(state).toBe(editorAction.SHOW_SYNTAX_HELP_PANE);
    })
    test('options', () => {
        let defaultData = { theme: 'vs' };

        let state = options(undefined, {})
        /**
         * 验证初始化的值
         */
        expect(state).toEqual(defaultData);
        let testData = {
            theme: 'vs',
            theme2: 'dark'
        }
        state = options(state, {
            type: editorAction.UPDATE_OPTIONS,
            data: testData
        })
        expect(state).toEqual(testData);
        state = options(undefined, {});
        /**
         * 验证本地缓存是否生效
         */
        expect(state).toEqual(testData);
    })
    test('running', () => {
        let state = running(undefined, {
            type: editorAction.ADD_LOADING_TAB,
            data: {
                id: 1
            }
        });
        expect(state).toContain(1);
        state = running(state, {
            type: editorAction.ADD_LOADING_TAB,
            data: {
                id: 2
            }
        })
        expect(state).toContain(1);
        expect(state).toContain(2);
        state = running(state, {
            type: editorAction.REMOVE_LOADING_TAB,
            data: {
                id: 1
            }
        });
        expect(state).toContain(2);
        expect(state).not.toContain(1);
        state = running(state, {
            type: editorAction.REMOVE_ALL_LOAING_TAB
        });
        expect(state).toHaveLength(0)
    })
    test('selection', () => {
        let state = selection(undefined, {})
        expect(state).toEqual('');
        state = selection(state, {
            type: editorAction.SET_SELECTION_CONTENT,
            data: { test: 1 }
        })
        expect(state).toEqual({ test: 1 });
        state = selection(state, {
            type: editorAction.SET_SELECTION_CONTENT
        })
        expect(state).toEqual('');
    })
});
