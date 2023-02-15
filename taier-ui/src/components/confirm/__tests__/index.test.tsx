import type { CatalogueDataProps } from '@/interface';
import { act, cleanup, fireEvent, render } from '@testing-library/react';
import Confirm, { confirm } from '..';

describe('Test Confirm Component', () => {
    beforeEach(() => {
        cleanup();
        document.body.innerHTML = '';
    });

    describe('Test Inner Confirm', () => {
        it('Should match snapshot', () => {
            const { asFragment } = render(<Confirm open tab={{ name: 'test' } as CatalogueDataProps} />);
            expect(asFragment()).toMatchSnapshot();
        });

        it('Should focus on save button', () => {
            const { container } = render(<Confirm open tab={{ name: 'test' } as CatalogueDataProps} />);

            expect(document.activeElement).toEqual(container.querySelectorAll('button').item(0));
        });
    });

    it('Should call confirm with function', () => {
        const saveFn = jest.fn();
        const unSaveFn = jest.fn();
        const cancelFn = jest.fn();

        act(() => {
            confirm({
                tab: { name: 'test' } as CatalogueDataProps,
                onSave: saveFn,
                onUnSave: unSaveFn,
                onCancel: cancelFn,
            });
        });

        expect(document.body).toMatchSnapshot();

        const buttons = document.body.querySelectorAll('button');

        act(() => {
            fireEvent.click(buttons[0]);
        });
        expect(saveFn).toBeCalledTimes(1);

        act(() => {
            confirm({
                tab: { name: 'test' } as CatalogueDataProps,
                onSave: saveFn,
                onUnSave: unSaveFn,
                onCancel: cancelFn,
            });
        });
        act(() => {
            fireEvent.click(buttons[1]);
        });
        expect(unSaveFn).toBeCalledTimes(1);

        act(() => {
            confirm({
                tab: { name: 'test' } as CatalogueDataProps,
                onSave: saveFn,
                onUnSave: unSaveFn,
                onCancel: cancelFn,
            });
        });
        act(() => {
            fireEvent.click(buttons[2]);
        });
        expect(cancelFn).toBeCalledTimes(1);
    });
});
