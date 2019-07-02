import configureMockStore from 'redux-mock-store'; // eslint-disable-line
import thunk from 'redux-thunk';
import api from '../../../../api';
import {
    browserAction
} from '../actionTypes'

import * as browserActions from '../browser';

jest.mock('../../../../api');
const middlewares = [thunk];
const mockStore = configureMockStore(middlewares)

describe('browser actions', () => {
    beforeEach(() => {
        // 重置store
        jest.resetAllMocks();
        store.clearActions();
    })
    let store = mockStore({});
    const testTask = { id: 10, name: 'test_task' };
    const id = 1; // 请求id
    const pages = [{ id: 1, name: 'test' }, { id: 2, name: 'test' }];
    const existPage = pages.find((page) => {
        return page.id == id
    })
    test('openPage action', async () => {
        const response = { code: 1, data: testTask };
        api.getTasks.mockResolvedValue(response).mockResolvedValueOnce({ ...response, code: 0 })
            .mockResolvedValueOnce({ ...response, code: 1 })
        const expectedActions = [{
            type: browserAction.UPDATE_PAGE,
            data: existPage
        }, {
            type: browserAction.SET_CURRENT_PAGE,
            data: existPage
        }];
        // 当前page在pages已存在
        const pagesFunc = () => {
            return new Promise((resolve, reject) => {
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
        const expectedActions = [{
            type: browserAction.UPDATE_PAGE,
            data: testTask
        }, {
            type: browserAction.SET_CURRENT_PAGE,
            data: testTask
        }, {
            type: browserAction.NEW_PAGE,
            data: testTask
        }];
        store.dispatch(browserActions.newPage(testTask))
        const nextActions = store.getActions();
        expect(nextActions).toEqual(expectedActions)
    })

    test('setCurrentPage', () => {
        const expectedActions = [{
            type: browserAction.UPDATE_PAGE,
            data: testTask
        }, {
            type: browserAction.SET_CURRENT_PAGE,
            data: testTask
        }];
        store.dispatch(browserActions.setCurrentPage(testTask))
        const nextActions = store.getActions();
        expect(nextActions).toEqual(expectedActions)
    })
    test('closePage', () => {
        let currentPage = { id: 1, name: 'test_task' }
        const index = pages.findIndex((item) => {
            return id === item.id
        })
        const newNode = pages[index + 1] || pages[index - 1]
        const expectedActions = [{
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
        store.dispatch(browserActions.closePage(id, pages, currentPage))
        let nextActions = store.getActions();
        expect(nextActions).toEqual(expectedActions)
        // currentId != id
        currentPage = { id: 4, name: 'test_task' }
        store.clearActions(); // 清空上次actions
        store.dispatch(browserActions.closePage(id, pages, currentPage))
        nextActions = store.getActions();
        expect(nextActions).toEqual([{
            type: browserAction.CLOSE_PAGE,
            data: index
        }])
    })
})
