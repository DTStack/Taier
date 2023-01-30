import { CATALOGUE_TYPE } from '@/constant';
import { selectItemInTree, toggleOpen } from '@/tests/utils';
import { cleanup, render } from '@testing-library/react';
import React from 'react';
import CustomTreeSelect from '../customTreeSelect';
import treeData from './fixtures/treeData';

jest.useFakeTimers();
jest.mock('@/utils/extensions', () => {
    return {
        fileIcon: () => <svg data-testid="mockFileIcon" />,
    };
});

describe('Test CustomTreeSelect Component', () => {
    beforeEach(() => {
        cleanup();
    });

    it('Should match snapshot', () => {
        const { asFragment } = render(<CustomTreeSelect showFile dataType={CATALOGUE_TYPE.TASK} treeData={treeData} />);
        expect(asFragment()).toMatchSnapshot();
    });

    it('Should trigger onChange event handler', () => {
        const fn = jest.fn();
        const { container } = render(
            <CustomTreeSelect showFile dataType={CATALOGUE_TYPE.TASK} treeData={treeData} onChange={fn} />
        );

        toggleOpen(container);
        selectItemInTree(0);

        expect(fn.mock.calls[0][0]).toBe(31);
        expect(render(fn.mock.calls[0][1]).container.textContent).toBe('任务开发 ');
    });
});
