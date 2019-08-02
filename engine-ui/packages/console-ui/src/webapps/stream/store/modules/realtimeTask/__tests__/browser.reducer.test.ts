import {
    browserAction
} from '../actionTypes';
import { pages, currentPage, inputData, outputData, dimensionData } from '../browser'; /* eslint-disable-line */
describe('broswer reducer', () => {
    let initValue: any = [];
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
        let data: any = { id: 1, name: 'test' };
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
        let data: any = { id: 2, name: 'test_2' };
        const defaultVal: any = [{
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
    test('set default page input output dimension data ', () => {
        const getInitVal = (type: any) => {
            let initVal: any;
            switch (type) {
                case 'SET_INPUT_DATA': {
                    initVal = {
                        taskId: 1,
                        source: {
                            tabTemplate: [],
                            panelColumn: [],
                            panelActiveKey: [],
                            popoverVisible: [],
                            checkFormParams: [],
                            timeColumoption: [],
                            originOptionType: [],
                            topicOptionType: []
                        }
                    }
                    break;
                }
                case 'SET_OUTPUT_DATA': {
                    initVal = {
                        taskId: 1,
                        sink: {
                            tabTemplate: [], // 模版存储,所有输出源(记录个数)
                            panelActiveKey: [], // 输出源是打开或关闭状态
                            popoverVisible: [], // 删除显示按钮状态
                            panelColumn: [], // 存储数据
                            checkFormParams: [], // 存储要检查的参数from
                            originOptionType: [], // 数据源选择数据
                            tableOptionType: [], // 表选择数据
                            topicOptionType: [], // topic 列表
                            tableColumnOptionType: []// 表字段选择的类型
                        }
                    }
                    break;
                }
                case 'SET_DIMESION_DATA': {
                    initVal = {
                        taskId: 1,
                        side: {
                            tabTemplate: [],
                            panelColumn: [],
                            panelActiveKey: [],
                            popoverVisible: [],
                            checkFormParams: [],
                            originOptionType: [],
                            tableOptionType: [],
                            tableColumnOptionType: []
                        }
                    }
                    break;
                }
                default:
                    return initVal = {}
            }
            return initVal
        }
        const getActionCreator = (type: any) => {
            let actionsCreator: any;
            actionsCreator = {
                type: browserAction[type],
                data: getInitVal(type)
            }
            return actionsCreator
        }
        let page: any = [{ id: 1, name: 'test' }, { id: 2, name: 'test' }]
        let newState = pages(page, getActionCreator('SET_INPUT_DATA'))
        expect(newState).toEqual([{ id: 1, name: 'test', source: [] }, { id: 2, name: 'test' }])
        newState = pages(page, getActionCreator('SET_OUTPUT_DATA'))
        expect(newState).toEqual([{ id: 1, name: 'test', sink: [] }, { id: 2, name: 'test' }])
        newState = pages(page, getActionCreator('SET_DIMESION_DATA'))
        expect(newState).toEqual([{ id: 1, name: 'test', side: [] }, { id: 2, name: 'test' }])
    })
    test('CLOSE_PAGE', () => {
        let page: any = [{ id: 1, name: 'test' }, { id: 2, name: 'test' }];
        let index = 0;
        let newState = pages(page, {
            type: browserAction.CLOSE_PAGE,
            data: index
        })
        expect(newState).toEqual([{ id: 2, name: 'test' }])
    })
    test('CLOSE_OTHERS', () => {
        let page: any = [{ id: 1, name: 'test' }, { id: 2, name: 'test' }];
        let currentPage: any = { id: 2, name: 'test' }
        let newState = pages(page, {
            type: browserAction.CLOSE_OTHERS,
            data: currentPage
        })
        expect(newState).toEqual([{ id: 2, name: 'test' }])
    })
    test('close current or other or all input output dimension data reducer', () => {
        const defaultVal: any = {
            1: {
                panelColumn: []
            },
            2: {
                panelColumn: []
            }
        };
        const id = 1;
        let newState = inputData(defaultVal, {
            type: browserAction.CLEAR_CURRENT_INPUT_DATA,
            data: id
        })
        expect(newState).toEqual({ 2: { panelColumn: [] } })
        newState = inputData(defaultVal, {
            type: browserAction.CLEAR_OTHER_INPUT_DATA,
            data: id
        })
        expect(newState).toEqual({ 1: { panelColumn: [] } })
        newState = inputData(defaultVal, {
            type: browserAction.CLEAR_ALL_INPUT_DATA
        })
        expect(newState).toEqual({})
        // output
        newState = outputData(defaultVal, {
            type: browserAction.CLEAR_CURRENT_OUTPUT_DATA,
            data: id
        })
        expect(newState).toEqual({ 2: { panelColumn: [] } })
        newState = outputData(defaultVal, {
            type: browserAction.CLEAR_OTHER_OUTPUT_DATA,
            data: id
        })
        expect(newState).toEqual({ 1: { panelColumn: [] } })
        newState = outputData(defaultVal, {
            type: browserAction.CLEAR_ALL_OUTPUT_DATA
        })
        expect(newState).toEqual({})
        // dimension
        newState = dimensionData(defaultVal, {
            type: browserAction.CLEAR_CURRENT_DIMESION_DATA,
            data: id
        })
        expect(newState).toEqual({ 2: { panelColumn: [] } })
        newState = dimensionData(defaultVal, {
            type: browserAction.CLEAR_OTHER_DIMESION_DATA,
            data: id
        })
        expect(newState).toEqual({ 1: { panelColumn: [] } })
        newState = dimensionData(defaultVal, {
            type: browserAction.CLEAR_ALL_DIMESION_DATA
        })
        expect(newState).toEqual({})
    })
})
