import { cleanup, render } from '@testing-library/react';

import Fullscreen from '..';

describe('Test FullScreen Component', () => {
    beforeEach(() => {
        document.body.innerHTML = '';
        cleanup();
    });

    it('Should match snapshot', () => {
        expect(render(<Fullscreen />).asFragment()).toMatchSnapshot();
    });
});
