import { act } from 'react-dom/test-utils';
import { cleanup, fireEvent, render } from '@testing-library/react';

import api from '@/api';
import GraphTimeRange from '../graphTimeRange';

jest.mock('@/api');

describe('Test GraphTimeRange Component', () => {
    beforeEach(() => {
        cleanup();
        document.body.innerHTML = '';

        // (api.formatTimeSpan as jest.Mock)
        // 	.mockReset()
        // 	.mockResolvedValueOnce({
        // 		code: 1,
        // 		data: {
        // 			correct: true,
        // 			formatResult: '20m',
        // 		},
        // 	})
        // 	.mockRejectedValue({
        // 		code: 1,
        // 		data: {
        // 			correct: false,
        // 		},
        // 	});
    });

    it('Should match snapshot', () => {
        expect(render(<GraphTimeRange value="10m" />).asFragment()).toMatchSnapshot();
    });

    it('Should support get range time', () => {
        const fn = jest.fn();
        const { container } = render(<GraphTimeRange value="10m" onRangeChange={fn} />);

        act(() => {
            fireEvent.click(container.querySelector('.btn-prev')!);
        });

        expect(fn).toBeCalledWith('1m');

        act(() => {
            fireEvent.click(container.querySelector('.btn-next')!);
        });

        expect(fn).toBeCalledWith('30m');
    });

    it('Should not call event handler when in the edge of range', () => {
        const fn = jest.fn();
        const { container, rerender } = render(<GraphTimeRange value="10s" onRangeChange={fn} />);

        act(() => {
            fireEvent.click(container.querySelector('.btn-prev')!);
        });

        expect(fn).not.toBeCalled();

        rerender(<GraphTimeRange value="2y" onRangeChange={fn} />);

        act(() => {
            fireEvent.click(container.querySelector('.btn-next')!);
        });

        expect(fn).not.toBeCalled();
    });

    it.skip('Should support change time by input', () => {
        const fn = jest.fn();
        const { container } = render(<GraphTimeRange value="10s" onRangeChange={fn} />);

        act(() => {
            fireEvent.change(container.querySelector('input')!, { target: { value: '20m' } });
            fireEvent.blur(container.querySelector('input')!);
        });

        expect(api.formatTimeSpan).toBeCalled();
    });
});
