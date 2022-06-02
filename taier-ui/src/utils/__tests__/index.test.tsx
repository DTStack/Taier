import moment from 'moment';
import {
	checkExist,
	convertToObj,
	convertToStr,
	deleteCookie,
	filterComments,
	filterSql,
	formatDateTime,
	formJsonValidator,
	getCookie,
	getTenantId,
	getTodayTime,
	getUserId,
	queryParse,
	removePopUpMenu,
	removeToolTips,
	replaceStrFormIndexArr,
	splitSql,
} from '..';

jest.mock('@/components/customDrawer', () => {
	return {
		updateDrawer: jest.fn(),
	};
});

jest.mock('@/extensions/folderTree', () => {
	return {
		openTaskInTab: jest.fn(),
	};
});

jest.mock('umi', () => {
	return {
		history: { push: jest.fn() },
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
});
