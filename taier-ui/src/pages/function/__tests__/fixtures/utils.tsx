import { UDF_TYPE_VALUES } from '@/constant';
import type { render } from '@testing-library/react';
import { input, radio, select } from 'ant-design-testing';

export function fillFormContent(getAllByTestId: ReturnType<typeof render>['getAllByTestId']) {
    document.body.querySelector('div.ant-select-dropdown')?.remove();
    select.fireOpen(document);
    select.fireSelect(document.body, 1);

    const folderPickers = getAllByTestId('mockFolderPicker');

    radio.fireChange(document, UDF_TYPE_VALUES.UDF);
    input.fireChange(document.querySelector<HTMLInputElement>('input#name')!, 'mock');
    input.fireChange(document.querySelector<HTMLInputElement>('input#className')!, 'com.dtstack');
    input.fireChange(folderPickers[0], 1);
    input.fireChange(document.querySelector<HTMLInputElement>('input#purpose')!, 'forTest');
    input.fireChange(document.querySelector<HTMLInputElement>('input#commandFormate')!, 'main');
    input.textarea.fireChange(document.querySelector<HTMLInputElement>('textarea#paramDesc')!, 'test');
    input.fireChange(folderPickers[1], 1);
}
