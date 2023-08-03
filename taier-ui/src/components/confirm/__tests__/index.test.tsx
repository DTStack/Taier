import type { CatalogueDataProps } from '@/interface';
import { act, cleanup, render, waitFor } from '@testing-library/react';
import Confirm, { confirm } from '..';
import { button } from 'ant-design-testing';
import { $ } from '@/tests/utils';

describe('Test Confirm Component', () => {
    beforeEach(() => {
        cleanup();
        document.body.innerHTML = '';
    });

    describe('Test Inner Confirm', () => {
        it('Should match snapshot', () => {
            render(<Confirm open tab={{ name: 'test' } as CatalogueDataProps} />);
            expect(document.body).toMatchSnapshot();
        });

        it('Should focus on save button', () => {
            act(() => {
                render(<Confirm open tab={{ name: 'test' } as CatalogueDataProps} />);
            });

            waitFor(() => {
                expect(document.activeElement).toEqual(
                    button.query($<HTMLElement>('.taier__confirm__btnGroups')!, 0)
                );
            });
        });
    });

    describe('Should call confirm with function', () => {
        it('Should call saveFn', () => {
            const saveFn = jest.fn();
            act(() => {
                confirm({
                    tab: { name: 'test' } as CatalogueDataProps,
                    onSave: saveFn,
                });
            });

            act(() => {
                button.fireClick(
                    button.query($<HTMLElement>('.taier__confirm__btnGroups')!, 0)!
                );
            });
            expect(saveFn).toBeCalledTimes(1);
        });

        it('Should call unSaveFn', () => {
            const unSaveFn = jest.fn();
            act(() => {
                confirm({
                    tab: { name: 'test' } as CatalogueDataProps,
                    onUnSave: unSaveFn,
                });
            });

            act(() => {
                button.fireClick(
                    button.query($<HTMLElement>('.taier__confirm__btnGroups')!, 1)!
                );
            });
            expect(unSaveFn).toBeCalledTimes(1);
        });

        it('Should call cancelFn', () => {
            const cancelFn = jest.fn();
            act(() => {
                confirm({
                    tab: { name: 'test' } as CatalogueDataProps,
                    onCancel: cancelFn,
                });
            });

            act(() => {
                button.fireClick(
                    button.query($<HTMLElement>('.taier__confirm__btnGroups')!, 2)!
                );
            });
            expect(cancelFn).toBeCalledTimes(1);
        });
    });
});
