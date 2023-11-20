import { act } from 'react-dom/test-utils';
import { cleanup, fireEvent, render } from '@testing-library/react';
import { inputNumber, select } from 'ant-design-testing';

import LifeCycleSelect from '..';

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

        expect(inputNumber.query(container, 0)?.classList.contains('ant-input-number-readonly')).toBeTruthy();
    });

    it('Should trigger onChange event handler', () => {
        const fn = jest.fn();
        const { container } = render(<LifeCycleSelect width={100} value={3} onChange={fn} />);

        act(() => {
            fireEvent.click(inputNumber.queryInput(container)!);
        });

        select.fireSelect(document, 1);

        expect(fn).toBeCalledWith(7);
    });

    it('Should support customize', () => {
        const { container } = render(<LifeCycleSelect width={100} value={3} />);

        act(() => {
            fireEvent.click(inputNumber.queryInput(container)!);
        });

        select.fireSelect(document, 5);

        expect(inputNumber.query(container, 0)?.classList.contains('ant-input-number-readonly')).toBeFalsy();
    });
});
