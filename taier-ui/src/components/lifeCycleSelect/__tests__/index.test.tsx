import { cleanup, fireEvent, render } from '@testing-library/react';
import { act } from 'react-dom/test-utils';
import LifeCycleSelect from '..';
import { select } from 'ant-design-testing';

describe('Test LifeCycleSelect Component', () => {
    beforeEach(() => {
        cleanup();
        document.body.innerHTML = '';
    });
    it('Should match snapshot', () => {
        expect(render(<LifeCycleSelect width={100} />).asFragment()).toMatchSnapshot();
    });

    it('Should readOnly', () => {
        const { container } = render(<LifeCycleSelect width={100} value={3} />);

        expect(
            container.querySelector('.ant-input-number')?.classList.contains('ant-input-number-readonly')
        ).toBeTruthy();
    });

    it('Should trigger onChange event handler', () => {
        const fn = jest.fn();
        const { container } = render(<LifeCycleSelect width={100} value={3} onChange={fn} />);

        act(() => {
            fireEvent.click(container.querySelector('input')!);
        });

        select.fireSelect(document.body, 1);

        expect(fn).toBeCalledWith(7);
    });

    it('Should support customize', () => {
        const { container } = render(<LifeCycleSelect width={100} value={3} />);

        act(() => {
            fireEvent.click(container.querySelector('input')!);
        });

        select.fireSelect(document.body, 5);

        expect(
            container.querySelector('.ant-input-number')?.classList.contains('ant-input-number-readonly')
        ).toBeFalsy();
    });
});
