import {
    browserAction
} from '../actionTypes';
import { pages, currentPage } from '../browser'; /* eslint-disable-line */
describe('broswer reducer', () => {
    let initValue = [];
    test('default', () => {
        expect(pages(undefined, {})).toEqual(initValue);
    })
    test('GET_PAGES', () => {
        let nextState = pages(initValue, {
            type: browserAction.GET_PAGES
        })
        expect(nextState || []).toEqual(initValue)
    })
    test('NEW_PAGE', () => {
        // 打开page在当前state不存在
        let data = { id: 1, name: 'test' };
        let newState = pages(initValue, {
            type: browserAction.NEW_PAGE,
            data
        })
        expect(newState).toContainEqual(data);
        // 打开page在当前state存在
        data = { id: 1, name: 'test' };
        newState = pages(newState, {
            type: browserAction.NEW_PAGE,
            data
        })
        expect(newState).toContainEqual(data);
    })
    test('UPDATE_PAGE', () => {
        let data = { id: 2, name: 'test_2' };
        const defaultVal = [{
            id: 2, name: 'test_2'
        }, {
            id: 1, name: 'test'
        }]
        let newState = pages(defaultVal, {
            type: browserAction.UPDATE_PAGE,
            data
        })
        expect(newState).toEqual(defaultVal)
    })
})
