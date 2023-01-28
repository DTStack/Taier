import { act, cleanup, fireEvent, waitFor } from '@testing-library/react';
import LintTo from '..';
import '@testing-library/jest-dom';
import { sleep } from '@/tests/utils';
import { mouse } from 'd3-selection';

jest.mock('d3-selection', () => {
	const original = jest.requireActual('d3-selection');
	return {
		...original,
		mouse: jest.fn(),
	};
});

const createLineTo = (
	isLined: boolean = false,
	onLineChanged = jest.fn(),
	onLineClick = jest.fn(),
) => {
	const div = document.createElement('div');
	document.body.appendChild(div);

	const line = new LintTo(div, {
		rowKey: 'id',
		onRenderColumns() {
			return [{ dataIndex: 'name', key: 'name' }];
		},
		onDragStart() {
			return true;
		},
		onDrop() {
			return true;
		},
		onLineChanged,
		onLineClick,
	});

	line.setSourceData([{ id: 1, name: 'source' }]);
	line.setTargetData([{ id: 1, name: 'target' }]);

	if (isLined) {
		line.setLine([
			{
				from: { id: 1, name: 'source' },
				to: { id: 1, name: 'target' },
			},
		]);
	}

	act(() => {
		line.render();
	});

	return div;
};

describe('Test LineTo Class Component', () => {
	beforeEach(() => {
		cleanup();
		document.body.innerHTML = '';
	});

	it('Should match snapshot', () => {
		const div = createLineTo();

		expect(div).toMatchSnapshot();
	});

	it('Should render drag pointer', async () => {
		createLineTo();

		const svg = document.querySelector('svg.taier__lintTo__svg');

		await waitFor(() => {
			const source = svg?.querySelector('.taier__lintTo__sourcePoints');
			const target = svg?.querySelector('.taier__lintTo__targetPoints');
			expect(source).toBeInTheDocument();
			expect(target).toBeInTheDocument();

			expect(source?.querySelectorAll('.taier__lintTo__point').length).toBe(1);
			expect(target?.querySelectorAll('.taier__lintTo__point').length).toBe(1);
		});
	});

	it('Should render lines', async () => {
		createLineTo(true);

		const svg = document.querySelector('svg.taier__lintTo__svg');

		await waitFor(() => {
			const lines = svg?.querySelector('.taier__lintTo__lines');
			expect(lines).toBeInTheDocument();

			expect(lines?.querySelectorAll('.taier__lintTo__line').length).toBe(1);
		});
	});

	it('Should support bind Events', async () => {
		(mouse as jest.Mock).mockReset().mockImplementation(() => [-10, 0]);

		const fn = jest.fn();
		createLineTo(false, fn);

		await sleep(300);

		const svg = document.querySelector('svg.taier__lintTo__svg')!;
		const sourcePointer = svg
			?.querySelector('.taier__lintTo__sourcePoints')
			?.querySelector('.taier__lintTo__point');

		expect(sourcePointer).toBeInTheDocument();

		fireEvent.mouseDown(sourcePointer!);

		expect(svg?.querySelector('.taier__lintTo__previewer__line')).toBeInTheDocument();

		fireEvent.mouseMove(svg);
		fireEvent.mouseUp(svg);

		expect(fn).toBeCalledWith({ id: 1, name: 'source' }, { id: 1, name: 'target' });
	});

	it('Should support remove line', async () => {
		const fn = jest.fn();
		createLineTo(true, undefined, fn);

		const svg = document.querySelector('svg.taier__lintTo__svg');

		await waitFor(() => {
			const lines = svg?.querySelector('.taier__lintTo__lines');
			expect(lines?.querySelectorAll('.taier__lintTo__line').length).toBe(1);
		});

		const line = svg?.querySelector('.taier__lintTo__lines')?.querySelector('g');

		fireEvent.mouseOver(line!);

		expect(
			svg?.querySelector<HTMLDivElement>('.taier__lintTo__tooltip__content')?.style.display,
		).toBe('block');
		jest.useFakeTimers();

		fireEvent.mouseOut(line!);
		jest.advanceTimersByTime(500);
		expect(
			svg
				?.querySelector<HTMLDivElement>('.taier__lintTo__tooltip__content')
				?.getAttribute('style'),
		).toBe(null);

		fireEvent.click(line!);

		expect(fn).toBeCalledWith({ id: 1, name: 'source' }, { id: 1, name: 'target' });
	});
});
