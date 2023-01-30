import { TASK_STATUS } from '@/constant';
import { taskRenderService } from '@/services';
import '@testing-library/jest-dom';
import moment from 'moment';
import { history } from 'umi';
import {
    checkExist,
    convertObjToNamePath,
    convertParams,
    copyText,
    createElement,
    createSeries,
    createSQLProposals,
    deleteCookie,
    disableRangeCreater,
    filterComments,
    filterSql,
    formatDateTime,
    getColumnsByColumnsText,
    getCookie,
    getPlus,
    getTenantId,
    getTodayTime,
    getUserId,
    getVertexStyle,
    goToTaskDev,
    isValidFormatType,
    pickByTruly,
    prettierJSONstring,
    randomId,
    removePopUpMenu,
    renderCharacterByCode,
    replaceStrFormIndexArr,
    splitByKey,
    splitSql,
    toArray,
    utf16to8,
    utf8to16,
    visit,
} from '..';

jest.mock('@/components/customDrawer', () => {
    return {
        updateDrawer: jest.fn(),
    };
});

jest.mock('umi', () => {
    return {
        history: { push: jest.fn() },
    };
});

jest.mock('@/services', () => {
    return {
        taskRenderService: {
            openTask: jest.fn(),
        },
    };
});

describe('utils/index', () => {
    beforeEach(() => {
        jest.clearAllMocks();
        document.body.innerHTML = '';
    });

    it('Should Get the time', () => {
        const today = getTodayTime();
        expect(today).toHaveLength(2);
        expect(today[0]).toBeInstanceOf(moment);
        expect(today[1]).toBeInstanceOf(moment);
        expect(today[0].format('HH:mm:ss')).toBe('00:00:00');
        expect(today[1].format('HH:mm:ss')).toBe('23:59:59');
    });

    it('Should Get the Cookie', () => {
        document.cookie = 'test=abc;';
        document.cookie = 'test2=zxc;';
        expect(getCookie('test')).toBe('abc');
        expect(getCookie('test2')).toBe('zxc');
        expect(getCookie('test3')).toBe(null);
    });

    it('Should Delete the Cookie', () => {
        expect(getCookie('test')).toBe('abc');

        deleteCookie('test');
        expect(getCookie('test')).toBe(null);
    });

    it('Should Format the DateTime', () => {
        expect(formatDateTime('2022-06-01')).toBe('2022-06-01 00:00:00');
        expect(formatDateTime(new Date('2022-06-01').valueOf())).toBe('2022-06-01 00:00:00');
        expect(formatDateTime(new Date('2022-06-01'))).toBe('2022-06-01 00:00:00');
    });

    it('Should Get the Existension', () => {
        expect(checkExist(undefined)).toBeFalsy();
        expect(checkExist(null)).toBeFalsy();
        expect(checkExist('')).toBeFalsy();
        expect(checkExist({})).toBeTruthy();
    });

    it('Should Filter Comments In SQL', () => {
        expect(filterComments('show tables;-- name test')).toBe('show tables; ');
        expect(filterComments('-- select test from A where id = "taier";')).toBe(' ');
        expect(filterComments('-- name test\nshow tables;-- name test')).toBe(' \nshow tables; ');
        expect(filterComments('/* select test from A where id = "taier"; */')).toBe(' ');
        expect(filterComments('select test from A where id = "taier";-- name test')).toBe(
            'select test from A where id = "taier"; '
        );

        // Error
        expect(filterComments('show tables";-- name test')).toBe('show tables";-- name test');
        // Error
        expect(filterComments('show tables/*;-- name test')).toBe('show tables/*;-- name test');
    });

    it('Should Replace String By Index Array', () => {
        expect(replaceStrFormIndexArr('test', 'abc', [{ begin: 0, end: 2 }])).toBe('abct');
        expect(replaceStrFormIndexArr('test', 'abc', [])).toBe('test');
    });

    it('Should Split Sqls', () => {
        expect(splitSql('')).toEqual('');
        expect(splitSql('show tables')).toEqual(['show tables']);
        expect(splitSql('show tables;')).toEqual(['show tables']);
        expect(splitSql('show tables;select * from tableA where id = "1";')).toEqual([
            'show tables',
            'select * from tableA where id = "1"',
        ]);
    });

    it('Should Filter Comments And Remove Spaces', () => {
        const sql = 'show tables;-- name test';
        expect(filterSql(sql)).toEqual(['show tables']);
    });

    it('Should Get Tenant Id From Cookie', () => {
        document.cookie = 'tenantId=1;';
        expect(getTenantId()).toBe('1');
        deleteCookie('tenantId');
    });

    it('Should Get User Id From Cookie', () => {
        document.cookie = 'userId=1;';
        expect(getUserId()).toBe('1');
        deleteCookie('userId');
    });

    it('Should Remove Popup Menu from Document', () => {
        jest.useFakeTimers();
        const dom = document.createElement('div');
        dom.classList.add('mxPopupMenu');
        dom.style.visibility = 'visibility';
        document.body.appendChild(dom);
        expect(dom.style.visibility).toBe('visibility');

        removePopUpMenu();

        jest.advanceTimersByTime(500);

        expect(dom.style.visibility).toBe('hidden');
    });

    it('Should render different status', () => {
        expect(getVertexStyle(TASK_STATUS.AUTO_CANCEL)).toBe(
            'whiteSpace=wrap;fillColor=var(--badge-cancel-background);strokeColor=var(--badge-cancel-border);'
        );

        expect(getVertexStyle(TASK_STATUS.COMPUTING)).toBe(
            'whiteSpace=wrap;fillColor=var(--badge-pending-background);strokeColor=var(--badge-pending-border);'
        );

        expect(getVertexStyle(TASK_STATUS.FINISHED)).toBe(
            'whiteSpace=wrap;fillColor=var(--badge-finished-background);strokeColor=var(--badge-finished-border);'
        );

        expect(getVertexStyle(TASK_STATUS.RUNNING)).toBe(
            'whiteSpace=wrap;fillColor=var(--badge-running-background);strokeColor=var(--badge-running-border);'
        );

        expect(getVertexStyle(TASK_STATUS.PARENT_FAILD)).toBe(
            'whiteSpace=wrap;fillColor=var(--badge-failed-background);strokeColor=var(--badge-failed-border);'
        );

        // default
        expect(getVertexStyle(99999)).toBe(
            'whiteSpace=wrap;fillColor=var(--badge-common-background);strokeColor=var(--badge-common-border);'
        );
    });

    it('Should prettier JSON string', () => {
        expect(prettierJSONstring(JSON.stringify({ a: { b: 1 }, c: JSON.stringify({ a: 1 }) }))).toBe(
            '{\n  "a": {\n    "b": 1\n  },\n  "c": {\n    "a": 1\n  }\n}'
        );

        expect(prettierJSONstring(';;')).toBe(';;');
    });

    it('Should have a randomId with 13 length', () => {
        expect(typeof randomId()).toBe('number');
        expect(randomId().toString().length).toBe(13);
    });

    it('Should support copyText', () => {
        const mockFn = jest.fn();
        Object.defineProperty(navigator, 'clipboard', {
            configurable: true,
            value: { writeText: mockFn },
        });

        copyText('test');
        expect(mockFn).toBeCalled();
        expect(mockFn.mock.calls[0][0]).toBe('test');

        Reflect.deleteProperty(navigator, 'clipboard');

        // Expect call execCommand without navigator.clipboard.writeText
        mockFn.mockClear();
        Object.defineProperty(document, 'execCommand', {
            value: mockFn,
        });
        copyText('test');
        expect(mockFn).toBeCalled();
        Reflect.deleteProperty(document, 'execCommand');
    });

    it('Should have a series of number', () => {
        expect(createSeries(5)).toEqual([1, 2, 3, 4, 5]);
    });

    it('Should parse columns', () => {
        expect(getColumnsByColumnsText('id int')).toEqual([{ field: 'id', type: 'int' }]);
        expect(getColumnsByColumnsText('id int\nage int\n\n\n\n')).toEqual([
            { field: 'id', type: 'int' },
            { field: 'age', type: 'int' },
        ]);
    });

    it('Should render Character', () => {
        expect(renderCharacterByCode(8)).toBe('⌫');
    });

    it('Should get an Array', () => {
        expect(toArray('test')).toEqual(['test']);
        expect(toArray(['test'])).toEqual(['test']);
    });

    it('Should convert params into values', () => {
        expect(
            convertParams({ sourceId: '{{ form#a.b }}', targetId: '{{ form#a.b#toArray }}' }, { a: { b: 1 } })
        ).toEqual({
            sourceId: 1,
            targetId: [1],
        });
    });

    it('Should get an calculated value', () => {
        expect(getPlus({ a: { b: [{ c: 1, value: 100 }] } }, '{{a.b#find.c}}', 100)).toBe(1);

        expect(getPlus({ a: { b: 2 } }, '{{a.b}}')).toBe(2);
    });

    it("Should convert to form's name path", () => {
        expect(convertObjToNamePath({ a: { b: 1 } })).toEqual([['a', 'b'], 1]);
    });

    it('Should support to visit tree nodes by condition', () => {
        const tree = {
            type: 1,
            value: 1,
            children: [
                {
                    type: 'number',
                    value: 1,
                },
                {
                    type: 'number',
                    value: 1,
                },
                {
                    type: 'number',
                    value: 1,
                },
                {
                    type: 'object',
                    value: 2,
                    children: [
                        {
                            type: 'object',
                            value: 1,
                            children: [
                                {
                                    type: 'number',
                                    value: 2,
                                },
                            ],
                        },
                    ],
                },
            ],
        };
        visit(
            tree,
            (item) => item.value === 1,
            (item) => {
                // eslint-disable-next-line no-param-reassign
                item.visited = true;
            }
        );

        expect(tree).toEqual({
            type: 1,
            value: 1,
            children: [
                {
                    type: 'number',
                    value: 1,
                    visited: true,
                },
                {
                    type: 'number',
                    value: 1,
                    visited: true,
                },
                {
                    type: 'number',
                    value: 1,
                    visited: true,
                },
                {
                    type: 'object',
                    value: 2,
                    children: [
                        {
                            type: 'object',
                            value: 1,
                            visited: true,
                            children: [
                                {
                                    type: 'number',
                                    value: 2,
                                },
                            ],
                        },
                    ],
                },
            ],
        });
    });

    it('Should filter the falsy value in object', () => {
        expect(pickByTruly({ a: 0, b: '', c: undefined, d: null })).toEqual({ a: 0, b: '' });
    });

    it('Should pick up object by keys', () => {
        expect(splitByKey({ a: 1, b: 2, c: 3 }, ['a', 'b'])).toEqual({
            obj1: {
                a: 1,
                b: 2,
            },
            obj2: {
                c: 3,
            },
        });
    });

    it('Should distinguish the validation of formate type', () => {
        expect(isValidFormatType('')).toBe(false);
        expect(isValidFormatType('string')).toBe(true);
        expect(isValidFormatType('STRING')).toBe(true);
        expect(isValidFormatType('varChar')).toBe(true);
        expect(isValidFormatType('varChar2')).toBe(true);
        expect(isValidFormatType('abc')).toBe(false);
    });

    it('Should have completion for sql', () => {
        window.fetch = jest
            .fn()
            .mockResolvedValueOnce({
                // eslint-disable-next-line global-require
                json: () => Promise.resolve(require('../../../public/assets/keywords.json')),
            })
            .mockResolvedValueOnce({
                // eslint-disable-next-line global-require
                json: () => Promise.resolve(require('../../../public/assets/functions.json')),
            });
        const completions = createSQLProposals({
            startLineNumber: 0,
            startColumn: 0,
            endLineNumber: 0,
            endColumn: 0,
        });

        expect(completions).resolves.toMatchSnapshot();
    });

    it('disableRangeCreater', () => {
        expect(disableRangeCreater(null, null, 'hour')).toEqual([]);

        // [0,1,2,3,4,...,23]
        const hoursRange = new Array(24).fill(1).map((_, idx) => idx);
        // [0,1,2,3,4,...,59]
        const minutesRange = new Array(60).fill(1).map((_, idx) => idx);
        // [0,1,2,3,4,...,59]
        const secondsRange = new Array(60).fill(1).map((_, idx) => idx);

        // Range span a day
        expect(
            disableRangeCreater(moment('2013-02-08', 'YYYY-DD-MM'), moment('2013-02-09', 'YYYY-DD-MM'), 'hour')
        ).toEqual(hoursRange.slice(1));

        expect(
            disableRangeCreater(
                moment('2013-02-08 09:30', 'YYYY-DD-MM HH:mm'),
                moment('2013-02-08 10:30', 'YYYY-DD-MM HH:mm'),
                'hour'
            )
        ).toEqual(hoursRange.slice(11));

        expect(
            disableRangeCreater(
                moment('2013-02-08 09:30', 'YYYY-DD-MM HH:mm'),
                moment('2013-02-08 10:30', 'YYYY-DD-MM HH:mm'),
                'hour',
                true
            )
        ).toEqual(hoursRange.slice(0, 9));

        // Returns [] since different hour
        expect(
            disableRangeCreater(
                moment('2013-02-08 09:30', 'YYYY-DD-MM HH:mm'),
                moment('2013-02-08 10:35', 'YYYY-DD-MM HH:mm'),
                'minute'
            )
        ).toEqual([]);

        expect(
            disableRangeCreater(
                moment('2013-02-08 09:30', 'YYYY-DD-MM HH:mm'),
                moment('2013-02-08 9:35', 'YYYY-DD-MM HH:mm'),
                'minute'
            )
        ).toEqual(minutesRange.slice(36));

        // Returns [] since different hour
        expect(
            disableRangeCreater(
                moment('2013-02-08 09:30:26', 'YYYY-DD-MM HH:mm:ss'),
                moment('2013-02-08 10:30:26', 'YYYY-DD-MM HH:mm:ss'),
                'second'
            )
        ).toEqual([]);

        // Returns [] since different minute
        expect(
            disableRangeCreater(
                moment('2013-02-08 09:30:26', 'YYYY-DD-MM HH:mm:ss'),
                moment('2013-02-08 09:31:26', 'YYYY-DD-MM HH:mm:ss'),
                'second'
            )
        ).toEqual([]);

        expect(
            disableRangeCreater(
                moment('2013-02-08 09:30:26', 'YYYY-DD-MM HH:mm:ss'),
                moment('2013-02-08 09:30:50', 'YYYY-DD-MM HH:mm:ss'),
                'second'
            )
        ).toEqual(secondsRange.slice(51));
    });

    it('Should convert utf-8 to utf-16', () => {
        expect(utf8to16('test')).toBe('test');
        expect(utf8to16('@Àäåæçèé')).toBe('@$妧詀');
        expect(utf8to16(1 as any)).toBe(1);
    });

    it('Should convert utf-16 to utf-8', () => {
        expect(utf16to8(1 as any)).toBe(1);
        expect(utf16to8('Àäåæçèé')).toBe('Àäåæçèé');
        expect(utf16to8('a¡ऄ')).toBe('aÂ¡à¤');
    });

    it('Should support to goToTaskDev', () => {
        goToTaskDev({ id: 1 });

        expect(taskRenderService.openTask).toBeCalledWith({ id: '1' });
        expect(history.push).toBeCalledWith({ query: {} });
    });

    it('Should create a dom avoid duplicated element', () => {
        expect(document.body.childElementCount).toBe(0);

        createElement({});
        expect(document.body.childElementCount).toBe(1);

        createElement({ className: 'test' });
        expect(document.querySelector('.test')).toBeInTheDocument();
    });
});
