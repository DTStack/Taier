import { fireEvent, render } from '@testing-library/react';
import Circle from '..';

describe('Test Circle Component', () => {
    it('Should match snapshots', () => {
        expect(render(<Circle type="running" />).asFragment()).toMatchSnapshot('running');
        expect(render(<Circle type="finished" />).asFragment()).toMatchSnapshot('finished');
        expect(render(<Circle type="stopped" />).asFragment()).toMatchSnapshot('stopped');
        expect(render(<Circle type="frozen" />).asFragment()).toMatchSnapshot('frozen');
        expect(render(<Circle type="fail" />).asFragment()).toMatchSnapshot('fail');
        expect(render(<Circle type="submitting" />).asFragment()).toMatchSnapshot('submitting');
        expect(render(<Circle type="restarting" />).asFragment()).toMatchSnapshot('restarting');
        expect(render(<Circle type="waitSubmit" />).asFragment()).toMatchSnapshot('waitSubmit');
    });

    it('Should call onClick', () => {
        const fn = jest.fn();
        const { getByTestId } = render(<Circle type="running" data-testid="test" onClick={fn} />);
        fireEvent.click(getByTestId('test'));

        expect(fn).toBeCalledTimes(1);
    });
});
