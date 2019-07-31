import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';

import api from '../../../../api';

import {
    workbenchActions
} from '../offlineAction'

import {
    workbenchAction
} from '../actionType';

jest.mock('../../../../api');

const middlewares: any = [thunk]
const mockStore = configureMockStore(middlewares)

describe('workbench actions', () => {
    let initialState: any = {
        tabs: [],
        currentTab: undefined,
        isCurrentTabNew: undefined,
        taskCustomParams: []
    };

    test('reloadTaskTab action', () => {
        const task: any = { id: 10, name: 'testTask' };
        const resp: any = { code: 1, data: task };
        api.getOfflineTaskDetail.mockResolvedValue(resp);

        const expectedActions: any = [{
            type: workbenchAction.UPDATE_TASK_TAB,
            payload: resp
        }];

        const store = mockStore(initialState);
        const actions = workbenchActions(store.dispatch);
        actions.reloadTaskTab();

        setTimeout(() => { // 此处 reloadTaskTab 中为异步调用，需要线程等待下
            const allActions = store.getActions();
            expect(allActions).toEqual(expectedActions)
        }, 0)
    })
})
