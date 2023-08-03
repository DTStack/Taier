import { cleanup, render } from '@testing-library/react';
import ViewDetail from '..';

describe('Test ViewDetail Component', () => {
    beforeEach(() => {
        cleanup();
        document.body.innerHTML = '';
    });

    it('Should match snapshot', () => {
        render(<ViewDetail visible />);

        expect(document.body).toMatchSnapshot();
    });
});
