import { act, render } from '@testing-library/react';
import { drawer } from 'ant-design-testing';
import { history } from 'umi';
import '@testing-library/jest-dom';

import { removePopUpMenu } from '@/utils';
import CustomDrawer, { updateDrawer } from '..';

jest.mock('umi', () => ({
    history: {
        push: jest.fn(),
    },
}));

jest.mock('@/utils', () => ({
    removePopUpMenu: jest.fn(),
}));

describe('Test CustomDrawer Component', () => {
    beforeEach(() => {
        jest.useFakeTimers();
    });

    afterEach(() => {
        jest.useRealTimers();
    });

    it('Should match snapshot', () => {
        act(() => render(<CustomDrawer id="test" open />));
        expect(document.body).toMatchSnapshot();
    });

    it('Should support forceRender', () => {
        const { getByTestId } = render(<CustomDrawer id="test" />);

        act(() =>
            updateDrawer({
                id: 'test',
                visible: true,
                renderContent() {
                    return <div data-testid="content" />;
                },
            })
        );

        expect(getByTestId('content')).toBeInTheDocument();

        drawer.fireClose(document);

        expect(history.push).toBeCalledWith({ query: {} });
        expect(removePopUpMenu).toBeCalledTimes(1);
    });
});
