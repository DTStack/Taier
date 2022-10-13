import dataSourceService from '../dataSourceService';

describe('Test dataSource service', () => {
	it('Should init with data', () => {
		expect(dataSourceService.getState()).toEqual({dataSource: [], current: 1, filters: {}});
	});
});
