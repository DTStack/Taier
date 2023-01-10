import molecule from '@dtinsight/molecule';
import EditorActionBarService from '../editorActionBarService';
import { taskRenderService } from '..';

jest.mock(
	'@/services/executeService',
	() =>
		class {
			cbStore: any = {};
			onStartRun = (cb: any) => {
				this.cbStore.onStartRun = cb;
			};
			onEndRun = (cb: any) => {
				this.cbStore.onEndRun = cb;
			};
			onStopTab = (cb: any) => {
				this.cbStore.onStopTab = cb;
			};
			emit = (name: any, ...args: any[]) => {
				this.cbStore[name](...args);
			};
		},
);

jest.mock('@/services', () => ({
	taskRenderService: { renderEditorActions: jest.fn() },
}));

describe('The editor actionBar service', () => {
	beforeEach(() => {
		(molecule.editor.getState as jest.Mock).mockReset();
		(molecule.editor.getDefaultActions as jest.Mock).mockReset();
		(taskRenderService.renderEditorActions as jest.Mock).mockReset();
		(molecule.editor.updateActions as jest.Mock).mockReset();
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

	it('Should listen execute service', () => {
		(molecule.editor.getState as jest.Mock).mockImplementation(() => ({
			current: {
				activeTab: '1',
			},
		}));
		const service = new EditorActionBarService();
		expect(service.getState().runningTab.size).toBe(0);

		// @ts-ignore
		service.executeService.emit('onStartRun', 1);

		expect(service.getState().runningTab.has(1)).toBeTruthy();
		// Change play action into running and enable stop action
		expect((molecule.editor.updateActions as jest.Mock).mock.calls[0][0]).toMatchSnapshot();

		// @ts-ignore
		service.executeService.emit('onEndRun', 1);
		expect(service.getState().runningTab.size).toBe(0);
		// Reset play action and disable stop action
		expect((molecule.editor.updateActions as jest.Mock).mock.calls[1][0]).toMatchSnapshot();

		// @ts-ignore
		service.executeService.emit('onStartRun', 1);
		// @ts-ignore
		service.executeService.emit('onStopTab', 1);
		// onStopTab is same with onEndRun
		expect(service.getState().runningTab.size).toBe(0);
		expect((molecule.editor.updateActions as jest.Mock).mock.calls[3][0]).toMatchSnapshot();
	});
});
