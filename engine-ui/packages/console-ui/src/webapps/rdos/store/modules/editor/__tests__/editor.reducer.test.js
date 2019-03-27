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
    let initState = {
        'console': {},
        'selection': '',
        'running': [],
        'options': { 'theme': 'vs' },
        'showRightExtraPane': '',
        'syntaxPane': {
            'html': ''
        }
    };
    test('showRightExtraPane', () => {
        let state = showRightExtraPane(initState.showRightExtraPane, {
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
        let localData = localDb.get(KEY_EDITOR_OPTIONS);
        /**
         * 验证是否存入了localStorage
         */
        expect(state).toEqual(localData);
    })
});
