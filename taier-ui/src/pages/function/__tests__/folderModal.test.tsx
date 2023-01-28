import { CATALOGUE_TYPE } from '@/constant';
import { cleanup, fireEvent, render, waitFor } from '@testing-library/react';
import { Input } from 'antd';
import FolderModal from '../folderModal';

jest.mock('../../../components/folderPicker', () => ({ id, value, onChange }: any) => (
	<Input id={id} value={value} onChange={onChange} data-testid="mockFolderPicker" />
));

describe('Test FolderModal Component for function', () => {
	beforeEach(() => {
		cleanup();
	});

	it('Match snapshot', () => {
		expect(
			render(
				<FolderModal
					isModalShow
					defaultData={undefined}
					treeData={{} as any}
					dataType={CATALOGUE_TYPE.FUNCTION}
					toggleCreateFolder={jest.fn()}
					addOfflineCatalogue={jest.fn()}
					editOfflineCatalogue={jest.fn()}
				/>,
			).asFragment(),
		).toMatchSnapshot();
	});

	it('Create folder from root folder', async () => {
		const fn = jest.fn(() => Promise.resolve(true));
		const { container } = render(
			<FolderModal
				isModalShow
				defaultData={undefined}
				treeData={{ id: 1 } as any}
				dataType={CATALOGUE_TYPE.FUNCTION}
				toggleCreateFolder={jest.fn()}
				addOfflineCatalogue={fn}
				editOfflineCatalogue={jest.fn()}
			/>,
		);

		const buttons = container.querySelectorAll<HTMLDivElement>('.ant-btn') || [];

		fireEvent.change(container.querySelector('input#dt_nodeName')!, {
			target: { value: 'test' },
		});

		fireEvent.click(buttons[1]);

		await waitFor(() => {
			expect(fn).toBeCalledWith({ nodeName: 'test', nodePid: 1 });
		});
	});

	it('Create folder from a specific folder', async () => {
		const fn = jest.fn(() => Promise.resolve(true));
		const { container } = render(
			<FolderModal
				isModalShow
				defaultData={{
					parentId: 10,
				}}
				treeData={
					{
						id: 1,
						children: [
							{
								id: 10,
							},
						],
					} as any
				}
				dataType={CATALOGUE_TYPE.FUNCTION}
				toggleCreateFolder={jest.fn()}
				addOfflineCatalogue={fn}
				editOfflineCatalogue={jest.fn()}
			/>,
		);

		const buttons = container.querySelectorAll<HTMLDivElement>('.ant-btn') || [];

		fireEvent.change(container.querySelector('input#dt_nodeName')!, {
			target: { value: 'test' },
		});

		fireEvent.click(buttons[1]);

		await waitFor(() => {
			expect(fn).toBeCalledWith({ nodeName: 'test', nodePid: 10 });
		});
	});

	it('Edit folder', async () => {
		const fn = jest.fn(() => Promise.resolve(true));
		const { container } = render(
			<FolderModal
				isModalShow
				defaultData={{
					id: 10,
					name: 'test',
					parentId: 11,
				}}
				treeData={{} as any}
				dataType={CATALOGUE_TYPE.FUNCTION}
				toggleCreateFolder={jest.fn()}
				addOfflineCatalogue={jest.fn()}
				editOfflineCatalogue={fn}
			/>,
		);

		const buttons = container.querySelectorAll<HTMLDivElement>('.ant-btn') || [];

		fireEvent.change(container.querySelector('input#dt_nodeName')!, {
			target: { value: 'test2' },
		});

		fireEvent.click(buttons[1]);

		await waitFor(() => {
			expect(fn).toBeCalledWith({ id: 10, nodeName: 'test2', nodePid: 11, type: 'folder' });
		});
	});

	it('Close modal via button', () => {
		const fn = jest.fn();
		const { container } = render(
			<FolderModal
				isModalShow
				defaultData={undefined}
				treeData={{} as any}
				dataType={CATALOGUE_TYPE.FUNCTION}
				toggleCreateFolder={fn}
				addOfflineCatalogue={jest.fn()}
				editOfflineCatalogue={jest.fn()}
			/>,
		);
		const buttons = container.querySelectorAll<HTMLDivElement>('.ant-btn') || [];
		fireEvent.click(buttons[0]);

		expect(fn).toBeCalled();
	});
});
