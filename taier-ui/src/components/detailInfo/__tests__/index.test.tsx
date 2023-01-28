import { CATALOGUE_TYPE } from '@/constant';
import { dataSourceService } from '@/services';
import { render } from '@testing-library/react';
import DetailInfo, { DetailInfoModal } from '..';

jest.mock('@/context', () => {
	const react = jest.requireActual('react');
	return react.createContext({
		supportJobTypes: [{ key: 1, value: 'spark' }],
	});
});

jest.mock('@/services', () => {
	return {
		dataSourceService: {
			getDataSource: jest.fn(),
		},
	};
});

describe('Test DetailInfo Component', () => {
	it('Should match snapshots', () => {
		expect(
			render(
				<DetailInfoModal
					type={CATALOGUE_TYPE.TASK}
					title="任务详情"
					visible
					loading={false}
				/>,
			).asFragment(),
		).toMatchSnapshot();
	});

	describe('Test DetailInfo Component', () => {
		it('Should match snapshots', () => {
			(dataSourceService.getDataSource as jest.Mock).mockReset().mockImplementation(() => [
				{
					dataInfoId: 1,
					dataName: 'dataName',
					dataType: 1,
				},
			]);

			expect(
				render(
					<DetailInfo
						type={CATALOGUE_TYPE.TASK}
						data={{
							name: 'test',
							taskType: 1,
							datasourceId: 1,
							componentVersion: '1.12',
							gmtCreate: new Date('2022-01-01').valueOf(),
							gmtModified: new Date('2022-01-01').valueOf(),
							taskDesc: 'test',
						}}
					/>,
				).asFragment(),
			).toMatchSnapshot();

			expect(
				render(
					<DetailInfo
						type={CATALOGUE_TYPE.FUNCTION}
						data={{
							name: 'test',
							className: 'className',
							sqlText: 'sqlText',
							purpose: 'purpose',
							commandFormate: 'commandFormate',
							paramDesc: 'paramDesc',
							gmtCreate: new Date('2022-01-01').valueOf(),
							gmtModified: new Date('2022-01-01').valueOf(),
						}}
					/>,
				).asFragment(),
			).toMatchSnapshot();

			expect(
				render(
					<DetailInfo
						type={CATALOGUE_TYPE.RESOURCE}
						data={{
							resourceName: 'test',
							resourceDesc: 'resourceDesc',
							url: 'url',
							createUser: {
								userName: 'userName',
							},
							gmtCreate: new Date('2022-01-01').valueOf(),
							gmtModified: new Date('2022-01-01').valueOf(),
						}}
					/>,
				).asFragment(),
			).toMatchSnapshot();

			expect(
				render(
					<DetailInfo
						type="dataSource"
						data={{
							dataInfoId: 17,
							dataName: 'mysql_test',
							dataType: 'MySQL',
							dataVersion: null,
							dataDesc: '',
							linkJson: JSON.stringify({
								jdbcUrl: 'jdbc:mysql://',
								username: 'dtstack',
							}),
							status: 1,
							isMeta: 0,
							gmtModified: 1667358042000,
							isImport: null,
							schemaName: '',
							dataTypeCode: 1,
						}}
					/>,
				).asFragment(),
			).toMatchSnapshot();

			expect(
				render(
					<DetailInfo
						type="taskJob"
						data={{
							taskName: 'test',
							jobId: 'jobId',
							taskType: 1,
							status: 1,
							taskPeriodId: 0,
							cycTime: 1667358042000,
						}}
					/>,
				).asFragment(),
			).toMatchSnapshot();
		});
	});
});
