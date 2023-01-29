import { act, fireEvent, render } from '@testing-library/react';
import '@testing-library/jest-dom';
import CustomDrawer, { updateDrawer } from '..';
import { history } from 'umi';
import { removePopUpMenu } from '@/utils';

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

        fireEvent.click(document.body.querySelector('.ant-drawer-mask')!);

        expect(history.push).toBeCalledWith({ query: {} });
        expect(removePopUpMenu).toBeCalledTimes(1);
    });
});
