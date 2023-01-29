import { cleanup, fireEvent, render } from '@testing-library/react';
import SlidePane from '..';

describe('Test SlidePane Component', () => {
    beforeEach(() => {
        cleanup();
    });

    it('Should match snapshot', () => {
        const { asFragment } = render(
            <SlidePane visible>
                <div>test</div>
            </SlidePane>
        );

        expect(asFragment()).toMatchSnapshot();
    });

    it('Should disabled when visible is false', () => {
        const { container } = render(
            <SlidePane visible={false}>
                <div>test</div>
            </SlidePane>
        );

        expect(container.querySelector<HTMLDivElement>('.dtc-slide-pane')?.style.pointerEvents).toBe('none');
    });

    it('Should support close pane by ESC', () => {
        const fn = jest.fn();
        const { container } = render(
            <SlidePane visible={false} onClose={fn}>
                <div>test</div>
            </SlidePane>
        );

        fireEvent.keyDown(container.querySelector('.dtc-slide-pane')!, { key: 'Escape' });

        expect(fn).toBeCalled();
    });
});
