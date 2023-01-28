import { cleanup, fireEvent, render } from '@testing-library/react';
import moment from 'moment';
import GraphTimePicker from '../graphTimePicker';

function openPicker(container: HTMLElement, index = 0) {
	const input = container.querySelectorAll('input')[index];
	fireEvent.mouseDown(input);
	fireEvent.focus(input);
}

function findCell(text: string | number, index = 0) {
	const table = document.querySelectorAll('table')[index];

	const array = Array.from(table.querySelectorAll('td'));
	for (let j = 0; j < array.length; j += 1) {
		const td = array[j];
		if (td.textContent === String(text) && td.className.includes('-in-view')) {
			return td;
		}
	}
}

function selectCell(text: string | number, index = 0) {
	const td = findCell(text, index);
	fireEvent.click(td!);

	return td;
}

function confirmOK() {
	fireEvent.click(document.querySelector('.ant-picker-ok > *')!);
}

describe('Test GraphTimePicker Component', () => {
	beforeEach(() => {
		cleanup();
		document.body.innerHTML = '';
	});

	it('Should match snapshot', () => {
		const { asFragment } = render(
			<GraphTimePicker timeRange="10m" value={moment(new Date('2022-01-20'))} />,
		);
		expect(asFragment()).toMatchSnapshot();
	});

	it('Should call onChange event handler', async () => {
		const fn = jest.fn();
		const { container } = render(
			<GraphTimePicker
				timeRange="10m"
				value={moment(new Date('2022-01-20'))}
				onChange={fn}
			/>,
		);

		expect(fn).not.toBeCalled();
		openPicker(container);
		selectCell(3);
		confirmOK();

		// Once for onChange, another for onChange
		expect(fn).toBeCalledTimes(2);
		expect(
			(fn.mock.calls[0][0] as moment.Moment).isSame(moment(new Date('2022-01-03'))),
		).toBeTruthy();
	});

	it('Should disabled the day after today and time', () => {
		const original = Date.now;
		Date.now = jest.fn(() => new Date('2022-01-21T12:33:37.000').valueOf());

		const { container } = render(
			<GraphTimePicker timeRange="10m" value={moment(new Date('2022-01-21T12:33:37.000'))} />,
		);

		openPicker(container);

		const dates = Array.from(document.querySelector('table')?.querySelectorAll('td') || []);
		const idx = dates.findIndex((d) => d.textContent === '21');

		// Expect the days after today(today is 2022-01-21) are all disabled
		expect(
			dates.slice(idx + 1).every((d) => d.classList.contains('ant-picker-cell-disabled')),
		).toBeTruthy();

		const hours = Array.from(
			document
				.querySelectorAll('ul.ant-picker-time-panel-column')[0]
				?.querySelectorAll('li') || [],
		);
		// Expect the hours after now are all disabled
		expect(
			hours
				.slice(13)
				.every((h) => h.classList.contains('ant-picker-time-panel-cell-disabled')),
		).toBeTruthy();

		const minutes = Array.from(
			document
				.querySelectorAll('ul.ant-picker-time-panel-column')[1]
				?.querySelectorAll('li') || [],
		);
		expect(
			minutes
				.slice(34)
				.every((m) => m.classList.contains('ant-picker-time-panel-cell-disabled')),
		).toBeTruthy();

		const seconds = Array.from(
			document
				.querySelectorAll('ul.ant-picker-time-panel-column')[2]
				?.querySelectorAll('li') || [],
		);
		expect(
			seconds
				.slice(38)
				.every((s) => s.classList.contains('ant-picker-time-panel-cell-disabled')),
		).toBeTruthy();

		Date.now = original;
	});

	it('Should support to change value via button', () => {
		const fn = jest.fn();
		const { container } = render(
			<GraphTimePicker
				timeRange="10m"
				value={moment(new Date('2022-01-21T12:33:37.000'))}
				onChange={fn}
			/>,
		);

		fireEvent.click(container.querySelector('.btn-prev')!);
		expect(fn).toBeCalledTimes(1);
		expect((fn.mock.calls[0][0] as moment.Moment).format('YYYY-MM-DD hh:mm:ss')).toBe(
			'2022-01-21 12:23:37',
		);

		fireEvent.click(container.querySelector('.btn-next')!);
		expect(fn).toBeCalledTimes(2);
		expect((fn.mock.calls[1][0] as moment.Moment).format('YYYY-MM-DD hh:mm:ss')).toBe(
			'2022-01-21 12:43:37',
		);
	});
});
