import { act, fireEvent } from '@testing-library/react';
import type { render } from '@testing-library/react';

export const $ = <T extends Element>(selector: string) => {
    return document.querySelector<T>(selector);
};

export const $$ = <T extends Element>(selector: string) => {
    return document.querySelectorAll<T>(selector);
};

export function fireConfirmOnModal(getByTestId: any) {
    fireEvent.click(getByTestId('antd-mock-Modal-confirm'));
}

export function fireInputChange(dom: HTMLElement, value: string) {
    fireEvent.change(dom, { target: { value } });
}

/**
 * Used for open Select's option lists
 */
export function toggleOpen(container?: ReturnType<typeof render>['container']): void {
    fireEvent.mouseDown((container || document).querySelector('.ant-select-selector')!);
    act(() => {
        jest.runAllTimers();
    });
}

export function selectItem(index = 0, wrapperIndex = 0) {
    fireEvent.click(
        document.body
            .querySelectorAll('div.ant-select-dropdown')
            [wrapperIndex].querySelectorAll('div.ant-select-item-option-content')[index]
    );
}

export function selectItemInTree(index = 0, wrapperIndex = 0) {
    fireEvent.click(
        document.body
            .querySelectorAll('div.ant-select-dropdown')
            [wrapperIndex].querySelectorAll('span.ant-select-tree-node-content-wrapper')[index]
    );
}

export async function sleep(delay = 300) {
    return new Promise<void>((resolve) => {
        setTimeout(() => {
            resolve();
        }, delay);
    });
}

export function selectValue(idx = 0, index = 0) {
    const container = document.querySelectorAll('.ant-select-dropdown')[index];

    act(() => {
        fireEvent.click(container.querySelectorAll('.ant-select-item')[idx]);
    });
}

export function showTooltip(ele: HTMLElement) {
    fireEvent.mouseEnter(ele);
}

export async function triggerOkOnConfirm() {
    await act(async () => {
        fireEvent.click(document.querySelector('.ant-modal-confirm-btns')!.querySelectorAll('button')[1]);
    });
}
