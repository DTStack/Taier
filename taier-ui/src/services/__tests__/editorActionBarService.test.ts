import molecule from '@dtinsight/molecule';
import EditorActionBarService from '../editorActionBarService';
import ExecuteService from '../executeService';
import { taskRenderService } from '..';

jest.mock('@/services', () => ({
	executeService: jest.fn(),
	taskRenderService: { renderEditorActions: jest.fn() },
}));

describe('The editor actionBar service', () => {
	beforeEach(() => {
		(molecule.editor.getState as jest.Mock).mockReset();
		(molecule.editor.getDefaultActions as jest.Mock).mockReset();
		(taskRenderService.renderEditorActions as jest.Mock).mockReset();
	});

	it('Should init with empty set', () => {
		const service = new EditorActionBarService();
		const state = service.getState();
		expect(state.runningTab).toBeInstanceOf(Set);
		expect(state.runningTab.size).toBe(0);
	});

	it("Should support to update editor's actions by current tab", () => {
		(molecule.editor.getState as jest.Mock)
			.mockImplementationOnce(() => ({
				current: {
					id: '1',
					tab: {
						data: {},
					},
				},
			}))
			.mockImplementation(() => ({
				current: {
					id: '1',
				},
			}));
		(taskRenderService.renderEditorActions as jest.Mock).mockImplementation(() => ['test']);
		(molecule.editor.getDefaultActions as jest.Mock).mockImplementation(() => ['actions']);

		const service = new EditorActionBarService();
		service.performSyncTaskActions();

		expect(molecule.editor.updateGroup).toBeCalledWith('1', { actions: ['test', 'actions'] });

		service.performSyncTaskActions();
		expect(molecule.editor.updateGroup).toBeCalledWith('1', { actions: ['actions'] });

		expect(molecule.editor.updateActions).not.toBeCalled();
	});

	it('Should update running task when current tab in running', () => {
		(molecule.editor.getState as jest.Mock).mockImplementation(() => ({
			current: {
				tab: {
					data: {
						id: 1,
					},
				},
			},
		}));
		(taskRenderService.renderEditorActions as jest.Mock).mockImplementation(() => ['test']);
		(molecule.editor.getDefaultActions as jest.Mock).mockImplementation(() => ['actions']);

		const service = new EditorActionBarService();
		service.setState({
			runningTab: new Set([1]),
		});
		service.performSyncTaskActions();
		expect(molecule.editor.updateActions).toBeCalled();
	});
});
