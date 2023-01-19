import { act, fireEvent } from '@testing-library/react';
import type { render } from '@testing-library/react';

export function fireConfirmOnModal(getByTestId: any) {
	fireEvent.click(getByTestId('antd-mock-Modal-confirm'));
}

export function fireInputChange(dom: HTMLElement, value: string) {
	fireEvent.change(dom, { target: { value } });
}

export function toggleOpen(container: ReturnType<typeof render>['container']): void {
	fireEvent.mouseDown(container.querySelector('.ant-select-selector')!);
	act(() => {
		jest.runAllTimers();
	});
}

export function selectItem(index: number = 0, wrapperIndex = 0) {
	fireEvent.click(
		document.body
			.querySelectorAll('div.ant-select-dropdown')
			.item(wrapperIndex)
			.querySelectorAll('div.ant-select-item-option-content')
			.item(index),
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
