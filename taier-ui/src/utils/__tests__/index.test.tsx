import { TASK_STATUS } from '@/constant';
import moment from 'moment';
import {
	checkExist,
	convertObjToNamePath,
	convertParams,
	convertToObj,
	convertToStr,
	copyText,
	createSeries,
	deleteCookie,
	filterComments,
	filterSql,
	formatDateTime,
	formJsonValidator,
	getColumnsByColumnsText,
	getCookie,
	getPlus,
	getTenantId,
	getTodayTime,
	getUserId,
	getVertexStyle,
	isValidFormatType,
	pickByTruly,
	prettierJSONstring,
	queryParse,
	randomId,
	removePopUpMenu,
	removeToolTips,
	renderCharacterByCode,
	replaceStrFormIndexArr,
	splitByKey,
	splitSql,
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
		taskRenderService: { openTask: jest.fn() },
	};
});

describe('utils/index', () => {
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
		expect(formatDateTime(new Date('2022-6-1').valueOf())).toBe('2022-06-01 00:00:00');
		expect(formatDateTime(new Date('2022-6-1'))).toBe('2022-06-01 00:00:00');
	});

	it('Should Get the Existension', () => {
		expect(checkExist(undefined)).toBeFalsy();
		expect(checkExist(null)).toBeFalsy();
		expect(checkExist('')).toBeFalsy();
		expect(checkExist({})).toBeTruthy();
	});

	it('Should Validate the JSON', async () => {
		await expect(formJsonValidator(null, JSON.stringify({}))).resolves.not.toThrowError();
		await expect(formJsonValidator(null, 'test')).rejects.toThrow(
			'请检查JSON格式，确认无中英文符号混用！',
		);
		await expect(formJsonValidator(null, 'true')).rejects.toThrow('请填写正确的JSON');
	});

	it('Should Filter Comments In SQL', () => {
		const sql = 'show tables;-- name test';
		expect(filterComments(sql)).toBe('show tables; ');
	});

	it('Should Replace String By Index Array', () => {
		expect(replaceStrFormIndexArr('test', 'abc', [{ begin: 0, end: 2 }])).toBe('abct');
		expect(replaceStrFormIndexArr('test', 'abc', [])).toBe('test');
	});

	it('Should Split Sqls', () => {
		expect(splitSql('show tables;')).toEqual(['show tables']);
		expect(splitSql('show tables;select * from tableA;')).toEqual([
			'show tables',
			'select * from tableA',
		]);
	});

	it('Should Filter Comments And Remove Spaces', () => {
		const sql = 'show tables;-- name test';
		expect(filterSql(sql)).toEqual(['show tables']);
	});

	it('Should Query Hash in URL', () => {
		const url = 'http://test.com/?test=task&test2=abc';
		expect(queryParse(url)).toEqual({ test: 'task', test2: 'abc' });

		expect(queryParse('')).toEqual({});
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

	it('Should Convert to Object', () => {
		const rawObj = { 'a.b.c': 1, 'a.b.a': 2 };
		expect(convertToObj(rawObj)).toEqual({
			a: {
				b: {
					c: 1,
					a: 2,
				},
			},
		});
	});

	it('Should Inverse to Object', () => {
		const rawObj = {
			a: {
				b: {
					c: 1,
					a: 2,
				},
			},
		};
		expect(convertToStr(rawObj)).toEqual({ 'a.b.c': 1, 'a.b.a': 2 });
	});

	it('Should Remove Tooltips from Document', () => {
		jest.useFakeTimers();
		const dom = document.createElement('div');
		dom.classList.add('mxTooltip');
		dom.style.visibility = 'visibility';
		document.body.appendChild(dom);
		expect(dom.style.visibility).toBe('visibility');

		removeToolTips();

		jest.advanceTimersByTime(500);

		expect(dom.style.visibility).toBe('hidden');
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
			'whiteSpace=wrap;fillColor=#e6e9f2;strokeColor=#5b6da6;',
		);

		expect(getVertexStyle(TASK_STATUS.COMPUTING)).toBe(
			'whiteSpace=wrap;fillColor=#fffbe6;strokeColor=#fdb313;',
		);

		expect(getVertexStyle(TASK_STATUS.FINISHED)).toBe(
			'whiteSpace=wrap;fillColor=#f5ffe6;strokeColor=#12bc6a;',
		);

		expect(getVertexStyle(TASK_STATUS.RUNNING)).toBe(
			'whiteSpace=wrap;fillColor=#e6f6ff;strokeColor=#3f87ff;',
		);

		expect(getVertexStyle(TASK_STATUS.PARENT_FAILD)).toBe(
			'whiteSpace=wrap;fillColor=#fff1f0;strokeColor=#fe615c;',
		);

		expect(getVertexStyle(99999)).toBe(
			'whiteSpace=wrap;fillColor=#F3F3F3;strokeColor=#D4D4D4;',
		);
	});

	it('Should prettier JSON string', () => {
		expect(
			prettierJSONstring(JSON.stringify({ a: { b: 1 }, c: JSON.stringify({ a: 1 }) })),
		).toBe('{\n  "a": {\n    "b": 1\n  },\n  "c": {\n    "a": 1\n  }\n}');

		expect(prettierJSONstring(';;')).toBe(';;');
	});

	it('Should have a randomId with 13 length', () => {
		expect(typeof randomId()).toBe('number');
		expect(randomId().toString().length).toBe(13);
	});

	it('Should support copyText', () => {
		const mockFn = jest.fn();
		Object.defineProperty(navigator, 'clipboard', {
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

	it('Should convert params into values', () => {
		expect(convertParams({ sourceId: '{{ form#a.b }}' }, { a: { b: 1 } })).toEqual({
			sourceId: 1,
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
			},
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
});
