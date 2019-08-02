import configureMockStore from 'redux-mock-store'; // eslint-disable-line
import thunk from 'redux-thunk';
import api from '../../../../api';
import {
    browserAction
} from '../actionTypes'

import * as browserActions from '../browser';

jest.mock('../../../../api');
const middlewares: any = [thunk];
const mockStore = configureMockStore(middlewares)

describe('browser actions', () => {
    beforeEach(() => {
        // 重置store
        jest.resetAllMocks();
        store.clearActions();
    })
    let store = mockStore({});
    const testTask: any = { id: 10, name: 'test_task' };
    const id = 1; // 请求id
    const pages: any = [{ id: 1, name: 'test' }, { id: 2, name: 'test' }];
    const existPage = pages.find((page: any) => {
        return page.id == id
    })
    test('openPage action', async () => {
        const response: any = { code: 1, data: testTask };
        (api.getTasks as any).mockResolvedValue(response).mockResolvedValueOnce({ ...response, code: 0 })
            .mockResolvedValueOnce({ ...response, code: 1 })
        const expectedActions: any = [{
            type: browserAction.UPDATE_PAGE,
            data: existPage
        }, {
            type: browserAction.SET_CURRENT_PAGE,
            data: existPage
        }];
        // 当前page在pages已存在
        const pagesFunc = () => {
            return new Promise((resolve: any, reject: any) => {
                browserActions.openPage({ id })(store.dispatch, () => {
                    return {
                        realtimeTask: { pages: pages }
                    }
                })
                resolve(expect(store.getActions()).toEqual(expectedActions))
            })
        }
        pagesFunc()
    })

    test('newPage', async () => {
        const expectedActions: any = [{
            type: browserAction.NEW_PAGE,
            data: testTask
        }, {
            type: browserAction.UPDATE_PAGE,
            data: testTask
        }, {
            type: browserAction.SET_CURRENT_PAGE,
            data: testTask
        }];
        store.dispatch((browserActions as any).newPage(testTask))
        const nextActions = store.getActions();
        expect(nextActions).toEqual(expectedActions)
    })
    test('setCurrentPage', () => {
        const expectedActions: any = [{
            type: browserAction.UPDATE_PAGE,
            data: testTask
        }, {
            type: browserAction.SET_CURRENT_PAGE,
            data: testTask
        }];
        store.dispatch((browserActions as any).setCurrentPage(testTask))
        const nextActions = store.getActions();
        expect(nextActions).toEqual(expectedActions)
    })
    test('closePage', () => {
        let currentPage: any = { id: 1, name: 'test_task' }
        const index = pages.findIndex((item: any) => {
            return id === item.id
        })
        const newNode = pages[index + 1] || pages[index - 1]
        const expectedActions: any = [{
            type: browserAction.UPDATE_PAGE,
            data: newNode
        }, {
            type: browserAction.SET_CURRENT_PAGE,
            data: newNode
        }, {
            type: browserAction.CLOSE_PAGE,
            data: index
        }];
        // currentID = id
        store.dispatch((browserActions as any).closePage(id, pages, currentPage))
        let nextActions = store.getActions();
        expect(nextActions).toEqual(expectedActions)
        // currentId != id
        currentPage = { id: 4, name: 'test_task' }
        store.clearActions(); // 清空上次actions
        store.dispatch((browserActions as any).closePage(id, pages, currentPage))
        nextActions = store.getActions();
        expect(nextActions).toEqual([{
            type: browserAction.CLOSE_PAGE,
            data: index
        }])
    })
    test('test single Actions', () => { // 这里统一 test 传入page 同步 actions
        let curPage: any = { id: 1, name: 'test_task' }
        let expectedActions: any = [{
            type: browserAction.CLOSE_OTHERS,
            data: curPage
        }]
        // closeOtherPages
        store.dispatch(browserActions.closeOtherPages(curPage))
        let nextActions = store.getActions();
        expect(nextActions).toEqual(expectedActions)
        // updatePage
        expectedActions = [{
            type: browserAction.UPDATE_PAGE,
            data: curPage
        }]
        store.clearActions();
        store.dispatch(browserActions.updatePage(curPage))
        nextActions = store.getActions();
        expect(nextActions).toEqual(expectedActions)
        // updateCurrentPage
        expectedActions = [{
            type: browserAction.UPDATE_CURRENT_PAGE,
            data: curPage
        }]
        store.clearActions();
        store.dispatch(browserActions.updateCurrentPage(curPage))
        nextActions = store.getActions();
        expect(nextActions).toEqual(expectedActions)
    })
    test('set default page input output dimension data', () => { // 初始化page源表，结果表，维表
        const actionTypes = (type: any) => {
            let expectedActions: any;
            let defaultVal: any = {}
            expectedActions = [{
                type: browserAction[type],
                data: defaultVal
            }]
            return expectedActions
        }
        store.clearActions();
        store.dispatch(browserActions.setInputData({}))
        let nextActions = store.getActions();
        expect(nextActions).toEqual(actionTypes('SET_INPUT_DATA'))
        store.clearActions();
        store.dispatch(browserActions.setOutputData({}))
        nextActions = store.getActions();
        expect(nextActions).toEqual(actionTypes('SET_OUTPUT_DATA'))
        store.clearActions();
        store.dispatch(browserActions.setDimensionData({}))
        nextActions = store.getActions();
        expect(nextActions).toEqual(actionTypes('SET_DIMESION_DATA'))
    })

    test('close input output dimension data', () => {
        const expectedActions = (type: any) => {
            let actionsCreator: any;
            actionsCreator = [{
                type: browserAction[type],
                data: id
            }]
            return actionsCreator
        }
        // clearCurrent
        store.dispatch(browserActions.closeCurrentInputData(id))
        expect(store.getActions()).toEqual(expectedActions('CLEAR_CURRENT_INPUT_DATA'))
        store.clearActions();
        store.dispatch(browserActions.closeCurrentOutputData(id))
        expect(store.getActions()).toEqual(expectedActions('CLEAR_CURRENT_OUTPUT_DATA'))
        store.clearActions();
        store.dispatch(browserActions.closeCurrentDimensionData(id))
        expect(store.getActions()).toEqual(expectedActions('CLEAR_CURRENT_DIMESION_DATA'))
        store.clearActions();
        // closeOther
        store.dispatch(browserActions.closeOtherInputData(id))
        expect(store.getActions()).toEqual(expectedActions('CLEAR_OTHER_INPUT_DATA'))
        store.clearActions();
        store.dispatch(browserActions.closeOtherOutputData(id))
        expect(store.getActions()).toEqual(expectedActions('CLEAR_OTHER_OUTPUT_DATA'))
        store.clearActions();
        store.dispatch(browserActions.closeOtherDimensionData(id))
        expect(store.getActions()).toEqual(expectedActions('CLEAR_OTHER_DIMESION_DATA'))
    })
})
