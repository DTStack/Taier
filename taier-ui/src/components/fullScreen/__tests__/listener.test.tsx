import { fireEvent, render } from '@testing-library/react';
import { act } from 'react-dom/test-utils';
import KeyCombiner from '../listener';

describe('Test KeyCombiner Component', () => {
    it('Should call event handler', () => {
        const fn = jest.fn();
        render(
            <KeyCombiner
                onTrigger={fn}
                keyMap={{
                    70: true,
                    91: true,
                    16: true,
                }}
            />
        );

        act(() => {
            fireEvent.keyDown(window, { type: 'keydown', keyCode: 70 });
            fireEvent.keyDown(window, { type: 'keydown', keyCode: 91 });
            fireEvent.keyDown(window, { type: 'keydown', keyCode: 16 });
        });

        expect(fn).toBeCalledTimes(1);
    });
});
