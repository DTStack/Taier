import { CATALOGUE_TYPE, TASK_TYPE_ENUM } from '@/constant';
import { taskRenderService } from '@/services';
import { fileIcon } from '../extensions';

jest.mock('@/services', () => {
	return {
		taskRenderService: {
			renderTaskIcon: jest.fn((type) => type),
		},
	};
});

jest.mock('@/services/taskSaveService', () => {
	return {
		transformTabDataToParams: jest.fn(),
	};
});

describe('utils/extensions', () => {
	it('Should render file icon', () => {
		expect(fileIcon(null, CATALOGUE_TYPE.FUNCTION)).toBe('code');
		expect(fileIcon(null, CATALOGUE_TYPE.RESOURCE)).toMatchSnapshot();
		expect(fileIcon(TASK_TYPE_ENUM.SQL, CATALOGUE_TYPE.TASK)).toBe(TASK_TYPE_ENUM.SQL);

		expect(taskRenderService.renderTaskIcon).toBeCalled();
	});
});
