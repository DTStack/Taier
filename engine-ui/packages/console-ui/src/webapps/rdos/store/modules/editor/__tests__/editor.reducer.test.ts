import {
    console,
    selection,
    running,
    options,
    showRightExtraPane,
    syntaxPane
} from '../index';
import { editorAction } from '../actionTypes';

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
        let defaultData: any = { theme: 'vs' };

        let state = options(undefined, {})
        /**
         * 验证初始化的值
         */
        expect(state).toEqual(defaultData);
        let testData: any = {
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
    test('syntaxPane', () => {
        let defaultData: any = { selected: undefined, html: '' };
        let state = syntaxPane(undefined, {})
        expect(state).toEqual(defaultData);
        state = syntaxPane(state, {
            type: editorAction.UPDATE_SYNTAX_PANE,
            data: {
                html: 'test'
            }
        })
        expect(state).toEqual({
            ...defaultData,
            html: 'test'
        });
        state = syntaxPane(state, {
            type: editorAction.UPDATE_SYNTAX_PANE
        })
        expect(state).toEqual({
            ...defaultData,
            html: 'test'
        });
    });
    test('console', () => {
        let state = console(undefined, {});
        expect(state).toEqual({});
        /**
         * 初始化tab
         */
        state = console(state, {
            type: editorAction.GET_TAB,
            key: 'test'
        })
        expect(state).toEqual({
            test: { log: '', results: [] }
        });
        state = console(state, {
            type: editorAction.SET_TAB,
            data: { data: { log: '11', results: [] }, key: 'test' }
        })
        expect(state).toEqual({
            test: { log: '11', results: [] }
        });
        state = console(state, {
            type: editorAction.SET_CONSOLE_LOG,
            key: 'test',
            data: '123'
        })
        expect(state).toEqual({
            test: { log: '123', results: [], showRes: false }
        });
        state = console(state, {
            type: editorAction.APPEND_CONSOLE_LOG,
            key: 'test',
            data: '123'
        })
        expect(state).toEqual({
            test: { log: '123 \n123', results: [], showRes: false }
        });
        state = console(state, {
            type: editorAction.UPDATE_RESULTS,
            key: 'test',
            data: {
                list: ['1']
            }
        })
        expect(state).toEqual({
            test: {
                log: '123 \n123',
                results: [{
                    list: ['1'],
                    id: 1
                }],
                showRes: true
            }
        });
        state = console(state, {
            type: editorAction.UPDATE_RESULTS,
            key: 'test'
        })
        expect(state).toEqual({
            test: {
                log: '123 \n123',
                results: [{
                    list: ['1'],
                    id: 1
                }],
                showRes: false
            }
        });
        state = console(state, {
            type: editorAction.UPDATE_RESULTS,
            key: 'test',
            data: {
                list: ['12']
            }
        })
        expect(state).toEqual({
            test: {
                log: '123 \n123',
                results: [{
                    list: ['1'],
                    id: 1
                }, {
                    list: ['12'],
                    id: 2
                }],
                showRes: true
            }
        });
        state = console(state, {
            type: editorAction.DELETE_RESULT,
            key: 'test',
            data: 0
        })
        expect(state).toEqual({
            test: {
                log: '123 \n123',
                results: [{
                    list: ['12'],
                    id: 2
                }],
                showRes: true
            }
        });
        state = console(state, {
            type: editorAction.UPDATE_RESULTS,
            key: 'test',
            data: {
                list: ['123']
            }
        })
        expect(state).toEqual({
            test: {
                log: '123 \n123',
                results: [{
                    list: ['12'],
                    id: 2
                }, {
                    list: ['123'],
                    id: 3
                }],
                showRes: true
            }
        });
        state = console(state, {
            type: editorAction.RESET_CONSOLE,
            key: 'test'
        })
        expect(state).toEqual({
            test: {
                log: '',
                results: []
            }
        });
    })
});
