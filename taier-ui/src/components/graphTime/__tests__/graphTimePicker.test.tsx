import { cleanup, fireEvent, render } from '@testing-library/react';
import { datePicker } from 'ant-design-testing';
import moment from 'moment';

import { $, $$ } from '@/tests/utils';
import GraphTimePicker from '../graphTimePicker';

describe('Test GraphTimePicker Component', () => {
    beforeEach(() => {
        jest.useFakeTimers();
        cleanup();
        document.body.innerHTML = '';
    });

    afterEach(() => {
        jest.useRealTimers();
    });

    it('Should match snapshot', () => {
        const { asFragment } = render(<GraphTimePicker timeRange="10m" value={moment(new Date('2022-01-20'))} />);
        expect(asFragment()).toMatchSnapshot();
    });

    it('Should call onChange event handler', async () => {
        const fn = jest.fn();
        const { container } = render(
            <GraphTimePicker timeRange="10m" value={moment(new Date('2022-01-20'))} onChange={fn} />
        );

        expect(fn).not.toBeCalled();
        datePicker.fireOpen(container);
        datePicker.fireChange(document, '3');
        datePicker.fireOk(document);

        // Once for onChange, another for onChange
        expect(fn).toBeCalledTimes(2);
        expect((fn.mock.calls[0][0] as moment.Moment).isSame(moment(new Date('2022-01-03')))).toBeTruthy();
    });

    it('Should disabled the day after today and time', () => {
        const original = Date.now;
        Date.now = jest.fn(() => new Date('2022-01-21T12:33:37.000').valueOf());

        const { container } = render(
            <GraphTimePicker timeRange="10m" value={moment(new Date('2022-01-21T12:33:37.000'))} />
        );

        datePicker.fireOpen(container);

        const dates = Array.from($('table')?.querySelectorAll('td') || []);
        const idx = dates.findIndex((d) => d.textContent === '21');

        // Expect the days after today(today is 2022-01-21) are all disabled
        expect(dates.slice(idx + 1).every((d) => d.classList.contains('ant-picker-cell-disabled'))).toBeTruthy();

        const [hourCol, minCol, secCol] = $$('ul.ant-picker-time-panel-column');
        const hours = Array.from(hourCol?.querySelectorAll('li') || []);
        // Expect the hours after now are all disabled
        expect(hours.slice(13).every((h) => h.classList.contains('ant-picker-time-panel-cell-disabled'))).toBeTruthy();

        const minutes = Array.from(minCol?.querySelectorAll('li') || []);
        expect(
            minutes.slice(34).every((m) => m.classList.contains('ant-picker-time-panel-cell-disabled'))
        ).toBeTruthy();

        const seconds = Array.from(secCol?.querySelectorAll('li') || []);
        expect(
            seconds.slice(38).every((s) => s.classList.contains('ant-picker-time-panel-cell-disabled'))
        ).toBeTruthy();

        Date.now = original;
    });

    it('Should support to change value via button', () => {
        const fn = jest.fn();
        render(<GraphTimePicker timeRange="10m" value={moment(new Date('2022-01-21T12:33:37.000'))} onChange={fn} />);

        fireEvent.click($('.btn-prev')!);
        expect(fn).toBeCalledTimes(1);
        expect((fn.mock.calls[0][0] as moment.Moment).format('YYYY-MM-DD hh:mm:ss')).toBe('2022-01-21 12:23:37');

        fireEvent.click($('.btn-next')!);
        expect(fn).toBeCalledTimes(2);
        expect((fn.mock.calls[1][0] as moment.Moment).format('YYYY-MM-DD hh:mm:ss')).toBe('2022-01-21 12:43:37');
    });
});
