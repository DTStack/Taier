import { toggleOpen, selectItem } from '@/tests/utils';
import type { render } from '@testing-library/react';
import { fireEvent } from '@testing-library/react';

export function fillFormContent(getAllByTestId: ReturnType<typeof render>['getAllByTestId']) {
	toggleOpen();
	selectItem(1);

	const radios = document.querySelector('#udfType')?.querySelectorAll('input') || [];
	const folderPickers = getAllByTestId('mockFolderPicker');

	fireEvent.click(radios[0]);
	fireEvent.change(document.querySelector('input#name')!, { target: { value: 'mock' } });
	fireEvent.change(document.querySelector('input#className')!, {
		target: { value: 'com.dtstack' },
	});
	fireEvent.change(folderPickers[0], { target: { value: 1 } });
	fireEvent.change(document.querySelector('input#purpose')!, {
		target: { value: 'forTest' },
	});
	fireEvent.change(document.querySelector('input#commandFormate')!, {
		target: { value: 'main' },
	});
	fireEvent.change(document.querySelector('textarea#paramDesc')!, {
		target: { value: 'test' },
	});
	fireEvent.change(folderPickers[1], { target: { value: 1 } });
}
