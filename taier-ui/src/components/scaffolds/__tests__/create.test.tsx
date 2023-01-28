import api from '@/api';
import { selectItem, toggleOpen } from '@/tests/utils';
import { cleanup, fireEvent, render, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import { Form, Input } from 'antd';
import type { NamePath } from 'antd/lib/form/interface';
import { act } from 'react-dom/test-utils';
import scaffolds from '../create';
import resourceManagerTree from '@/services/resourceManagerService';
import { dataSourceService, taskRenderService } from '@/services';
import molecule from '@dtinsight/molecule';

jest.useFakeTimers();
jest.mock('@/api');
jest.mock('@/services', () => ({
	taskRenderService: {
		getState: jest.fn(),
	},
	dataSourceService: {
		getDataSource: jest.fn(),
		onUpdateState: jest.fn(),
		removeOnUpdateState: jest.fn(),
	},
}));
jest.mock('../../folderPicker', () => ({ value, onChange }: any) => {
	return <Input value={value} onChange={onChange} data-testid="mockFolderPicker" />;
});
jest.mock('@/services/resourceManagerService', () => {
	return {
		checkNotDir: jest.fn(),
	};
});

function FormContainer({ children }: any) {
	return <Form>{children}</Form>;
}

function FormItem({ name, initialValue }: { name: NamePath; initialValue: any }) {
	return (
		<Form.Item name={name} label="mockFormItem" initialValue={initialValue}>
			<Input />
		</Form.Item>
	);
}

describe('Test Create Scaffolds', () => {
	beforeEach(() => {
		cleanup();
		document.body.innerHTML = '';
	});

	describe('Test CreateModel Component', () => {
		it('Should match snapshot', () => {
			expect(
				render(
					<FormContainer>
						<scaffolds.createModel />
					</FormContainer>,
				).asFragment(),
			).toMatchSnapshot();
		});
	});

	describe('Test SyncModel Component', () => {
		it('Should match snapshot', () => {
			expect(
				render(
					<FormContainer>
						<scaffolds.syncModel />
					</FormContainer>,
				).asFragment(),
			).toMatchSnapshot();
		});
	});

	describe('Test ComponentVersion Component', () => {
		it('Should match snapshot', () => {
			expect(
				render(
					<FormContainer>
						<scaffolds.componentVersion />
					</FormContainer>,
				).asFragment(),
			).toMatchSnapshot();
		});

		it('Should get version list from request', async () => {
			(api.getComponentVersionByTaskType as jest.Mock).mockReset().mockResolvedValue({
				code: 1,
				data: [
					{
						componentName: 'a',
						componentVersion: 1.1,
					},
				],
			});

			const { container, getByText } = render(
				<FormContainer>
					<FormItem name="taskType" initialValue={undefined} />
					<scaffolds.componentVersion />
				</FormContainer>,
			);

			await act(async () => {
				fireEvent.change(container.querySelector('input#taskType')!, {
					target: { value: 1 },
				});
			});

			expect(api.getComponentVersionByTaskType).toBeCalledWith({ taskType: '1' });

			toggleOpen(container);

			expect(getByText('a-1.1')).toBeInTheDocument();
		});

		it('Should reset componentVersion field', async () => {
			(api.getComponentVersionByTaskType as jest.Mock).mockReset().mockResolvedValue({
				code: 1,
				data: [
					{
						componentName: 'a',
						componentVersion: 1.2,
						default: true,
					},
				],
			});

			const { container } = render(
				<FormContainer>
					<FormItem name="taskType" initialValue={undefined} />
					<FormItem name="componentVersion" initialValue={1.1} />
					<scaffolds.componentVersion />
				</FormContainer>,
			);

			await act(async () => {
				fireEvent.change(container.querySelector('input#taskType')!, {
					target: { value: 1 },
				});
			});

			expect(container.querySelector<HTMLInputElement>('input#componentVersion')!.value).toBe(
				'1.2',
			);
		});
	});

	describe('Test Resource Component', () => {
		it('Should match snapshot', () => {
			expect(
				render(
					<FormContainer>
						<scaffolds.resourceIdList />
					</FormContainer>,
				).asFragment(),
			).toMatchSnapshot();
		});

		it('Should support validator', async () => {
			(resourceManagerTree.checkNotDir as jest.Mock)
				.mockReset()
				.mockRejectedValue(new Error('error message'));

			const { getByTestId, getByText } = render(
				<FormContainer>
					<scaffolds.resourceIdList />
				</FormContainer>,
			);

			await act(async () => {
				fireEvent.change(getByTestId('mockFolderPicker'), { target: { value: 1 } });
			});

			await waitFor(() => {
				expect(getByText('error message')).toBeInTheDocument();
			});
		});
	});

	describe('Test MainClass Component', () => {
		it('Should match snapshot', () => {
			expect(
				render(
					<FormContainer>
						<scaffolds.mainClass />
					</FormContainer>,
				).asFragment(),
			).toMatchSnapshot();
		});
	});

	describe('Test ExeArgs Component', () => {
		it('Should match snapshot', () => {
			expect(
				render(
					<FormContainer>
						<scaffolds.exeArgs />
					</FormContainer>,
				).asFragment(),
			).toMatchSnapshot();
		});
	});

	describe('Test PythonVersion Component', () => {
		it('Should match snapshot', () => {
			expect(
				render(
					<FormContainer>
						<scaffolds.pythonVersion />
					</FormContainer>,
				).asFragment(),
			).toMatchSnapshot();
		});
	});

	describe('Test DataSource Component', () => {
		beforeEach(() => {
			(taskRenderService.getState as jest.Mock).mockReset().mockImplementation(() => ({
				supportTaskList: [],
			}));
			(dataSourceService.getDataSource as jest.Mock).mockReset().mockImplementation(() => []);

			(molecule.sidebar.setActive as jest.Mock).mockReset();
			(molecule.activityBar.setActive as jest.Mock).mockReset();
		});

		it('Should match snapshot', () => {
			expect(
				render(
					<FormContainer>
						<scaffolds.datasource />
					</FormContainer>,
				).asFragment(),
			).toMatchSnapshot();
		});

		it('Should get dataSource list from dataSourceService', async () => {
			(dataSourceService.getDataSource as jest.Mock).mockReset().mockImplementation(() => [
				{
					dataTypeCode: 1,
					dataName: 'a',
					dataType: 1,
					dataInfoId: 1,
				},
			]);
			(taskRenderService.getState as jest.Mock).mockReset().mockImplementation(() => ({
				supportTaskList: [
					{
						key: 1,
						taskProperties: {
							dataTypeCodes: [1],
						},
					},
				],
			}));

			const { container } = render(
				<FormContainer>
					<FormItem name="taskType" initialValue={1} />
					<scaffolds.datasource />
				</FormContainer>,
			);

			toggleOpen(container);

			await waitFor(() => {
				expect(
					container.querySelectorAll('div.ant-select-item-option-content').length,
				).toBe(1);

				expect(
					container.querySelectorAll('div.ant-select-item-option-content')[0].textContent,
				).toBe('a(1)');
			});
		});

		it('Should support render empty', () => {
			(taskRenderService.getState as jest.Mock).mockReset().mockImplementation(() => ({
				supportTaskList: [
					{
						key: 1,
						value: 'xxx',
					},
				],
			}));

			const { container } = render(
				<FormContainer>
					<FormItem name="taskType" initialValue={1} />
					<scaffolds.datasource />
				</FormContainer>,
			);

			toggleOpen(container);

			expect(container.querySelector('.ant-empty-description')?.textContent).toBe(
				'未找到xxx所支持的对应数据源，请先至数据源中心配置',
			);

			fireEvent.click(container.querySelector('button')!);

			expect(molecule.sidebar.setActive).toBeCalledWith('dataSource');
			expect(molecule.activityBar.setActive).toBeCalledWith('dataSource');
		});

		it('Should reset current field when taskType changed', async () => {
			(dataSourceService.getDataSource as jest.Mock).mockReset().mockImplementation(() => [
				{
					dataTypeCode: 1,
					dataName: 'a',
					dataType: 1,
					dataInfoId: 1,
				},
			]);
			(taskRenderService.getState as jest.Mock).mockReset().mockImplementation(() => ({
				supportTaskList: [
					{
						key: 1,
						taskProperties: {
							dataTypeCodes: [1],
						},
					},
				],
			}));
			const { container } = render(
				<FormContainer>
					<FormItem name="taskType" initialValue={1} />
					<scaffolds.datasource />
				</FormContainer>,
			);

			toggleOpen(container);
			selectItem(0);

			await waitFor(() => {
				expect(
					container.querySelector<HTMLInputElement>('.ant-select-selection-item')?.title,
				).toBe('a(1)');
			});

			await act(async () => {
				fireEvent.change(container.querySelector('input#taskType')!, {
					target: { value: 2 },
				});
			});

			await waitFor(() => {
				expect(
					container.querySelector<HTMLInputElement>('.ant-select-selection-item'),
				).toBeNull();
			});
		});
	});

	describe('Test QueueConfig Component', () => {
		beforeEach(() => {
			(api.getResourceByTenant as jest.Mock).mockReset().mockResolvedValue({
				code: 1,
				data: {
					queues: [{ queueName: 'a' }],
				},
			});
		});

		it('Should match snapshot', async () => {
			await act(async () => {
				expect(
					render(
						<FormContainer>
							<scaffolds.queue />
						</FormContainer>,
					).asFragment(),
				).toMatchSnapshot();
			});
		});

		it('Should render options by request data', async () => {
			await act(async () => {
				render(
					<FormContainer>
						<scaffolds.queue />
					</FormContainer>,
				);
			});

			toggleOpen(document.body);

			await waitFor(() => {
				expect(document.querySelectorAll('div.ant-select-item-option-content').length).toBe(
					1,
				);

				expect(
					document.querySelectorAll('div.ant-select-item-option-content')[0].textContent,
				).toBe('a');
			});
		});
	});
});
