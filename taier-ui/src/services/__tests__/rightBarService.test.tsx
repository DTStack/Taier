import { RightBarKind } from '@/interface';
import molecule from '@dtinsight/molecule';
import { render } from '@testing-library/react';
import { taskRenderService } from '..';
import RightBarService from '../RightBarService';

jest.mock('../', () => {
	return {
		taskRenderService: {
			renderRightBar: jest.fn(),
		},
	};
});

jest.mock('@/pages/rightBar/flinkDimension', () => {
	return () => <div>flinkDimension</div>;
});
jest.mock('@/pages/rightBar/flinkResult', () => {
	return () => <div>flinkResult</div>;
});
jest.mock('@/pages/rightBar/flinkSource', () => {
	return () => <div>flinkSource</div>;
});
jest.mock('@/pages/rightBar/envParams', () => {
	return () => <div>envParams</div>;
});
jest.mock('@/pages/rightBar/schedulingConfig', () => {
	return () => <div>schedulingConfig</div>;
});
jest.mock('@/pages/rightBar/taskInfo', () => {
	return () => <div>taskInfo</div>;
});
jest.mock('@/pages/rightBar/taskParams', () => {
	return () => <div>taskParams</div>;
});
jest.mock('@/pages/rightBar/taskConfig', () => {
	return () => <div>taskConfig</div>;
});

describe('Test RightBarService', () => {
	afterEach(() => {
		(taskRenderService.renderRightBar as jest.Mock).mockReset();
		(molecule.editor.getState as jest.Mock).mockReset();
	});

	it('Should have rightBar text', () => {
		const rightBarService = new RightBarService();
		expect(rightBarService.getTextByKind(RightBarKind.TASK)).toBe('任务属性');
		expect(rightBarService.getTextByKind(RightBarKind.DEPENDENCY)).toBe('调度依赖');
		expect(rightBarService.getTextByKind(RightBarKind.TASK_PARAMS)).toBe('任务参数');
		expect(rightBarService.getTextByKind(RightBarKind.TASK_CONFIG)).toBe('任务配置');
		expect(rightBarService.getTextByKind(RightBarKind.ENV_PARAMS)).toBe('环境参数');
		expect(rightBarService.getTextByKind(RightBarKind.FLINKSQL_SOURCE)).toBe('源表');
		expect(rightBarService.getTextByKind(RightBarKind.FLINKSQL_RESULT)).toBe('结果表');
		expect(rightBarService.getTextByKind(RightBarKind.FLINKSQL_DIMENSION)).toBe('维表');
		expect(rightBarService.getTextByKind(RightBarKind.QUEUE)).toBe('队列管理');
		expect(rightBarService.getTextByKind('')).toBe('未知');
	});

	it('Should have correct component', () => {
		(taskRenderService.renderRightBar as jest.Mock).mockImplementation(() => [
			RightBarKind.TASK,
			RightBarKind.DEPENDENCY,
			RightBarKind.TASK_PARAMS,
			RightBarKind.ENV_PARAMS,
			RightBarKind.TASK_CONFIG,
			RightBarKind.FLINKSQL_SOURCE,
			RightBarKind.FLINKSQL_RESULT,
			RightBarKind.FLINKSQL_DIMENSION,
			'1',
		]);

		(molecule.editor.getState as jest.Mock).mockImplementation(() => ({
			current: { tab: { id: 1 } },
		}));

		const rightBarService = new RightBarService();
		expect(
			render(rightBarService.createContent(RightBarKind.TASK)!).asFragment(),
		).toMatchSnapshot();
		expect(
			render(rightBarService.createContent(RightBarKind.DEPENDENCY)!).asFragment(),
		).toMatchSnapshot();
		expect(
			render(rightBarService.createContent(RightBarKind.TASK_PARAMS)!).asFragment(),
		).toMatchSnapshot();
		expect(
			render(rightBarService.createContent(RightBarKind.ENV_PARAMS)!).asFragment(),
		).toMatchSnapshot();
		expect(
			render(rightBarService.createContent(RightBarKind.TASK_CONFIG)!).asFragment(),
		).toMatchSnapshot();
		expect(
			render(rightBarService.createContent(RightBarKind.FLINKSQL_SOURCE)!).asFragment(),
		).toMatchSnapshot();
		expect(
			render(rightBarService.createContent(RightBarKind.FLINKSQL_RESULT)!).asFragment(),
		).toMatchSnapshot();
		expect(
			render(rightBarService.createContent(RightBarKind.FLINKSQL_DIMENSION)!).asFragment(),
		).toMatchSnapshot();

		expect(rightBarService.createContent('1')).toBe(null);
	});
});
