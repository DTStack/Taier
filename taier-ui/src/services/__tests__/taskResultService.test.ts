import taskResultService, { createLinkMark, createLog, createTitle } from '../taskResultService';

describe('The TaskResultService', () => {
	it('Should filled with default value', () => {
		expect(taskResultService.getState()).toEqual({
			logs: {},
			results: {},
		});
	});

	it('Should support set and clear results', () => {
		taskResultService.setResult('a-b', ['test']);
		expect(taskResultService.getState().results).toEqual({
			'a-b': ['test'],
		});

		taskResultService.clearResult('a-b');
		expect(taskResultService.getState().results).toEqual({});
	});

	it('Should support append and clear logs', () => {
		taskResultService.appendLogs('a', '-- -- --');
		expect(taskResultService.getState().logs).toEqual({
			a: '-- -- --\n',
		});

		taskResultService.appendLogs('a', 'spark');
		expect(taskResultService.getState().logs).toEqual({
			a: '-- -- --\nspark\n',
		});

		taskResultService.clearLogs('a');
		expect(taskResultService.getState().logs).toEqual({
			a: '',
		});
	});

	it('create Log', () => {
		const original = Date.now;
		Date.now = jest.fn(() => new Date('2023-01-01T12:00:00.000').getTime());

		expect(createLog('log successful', 'success')).toBe('[12:00:00] <success> log successful');
		expect(createLog('info message')).toBe('[12:00:00] <info> info message');

		Date.now = original;
	});

	it('createTitle', () => {
		expect(createTitle()).toBe('============================');
		expect(createTitle('title')).toBe('===========title===========');
	});

	it('createLinkMark', () => {
		expect(createLinkMark({ href: '/test' })).toBe('http://localhost/test');
	});
});
