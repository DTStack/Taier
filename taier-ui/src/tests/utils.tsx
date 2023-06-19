import { act, fireEvent } from '@testing-library/react';

export const $ = <T extends Element>(selector: string) => {
    return document.querySelector<T>(selector);
};

export const $$ = <T extends Element>(selector: string) => {
    return document.querySelectorAll<T>(selector);
};

export function fireConfirmOnModal(getByTestId: any) {
    fireEvent.click(getByTestId('antd-mock-Modal-confirm'));
}

export async function sleep(delay = 300) {
    return new Promise<void>((resolve) => {
        setTimeout(() => {
            resolve();
        }, delay);
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
