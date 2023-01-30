import { render } from '@testing-library/react';
import config from './fixtures/config';

describe('Test Chart Component', () => {
    beforeAll(() => {
        jest.useFakeTimers('modern').setSystemTime(new Date('2020-01-01'));
    });

    afterAll(() => {
        jest.useRealTimers();
    });

    it('Should match snapshot', () => {
        import('../index').then((Chart) => {
            const { asFragment } = render(<Chart.default option={config} width={110} height="110px" />);

            expect(asFragment()).toMatchSnapshot();
        });
    });
});
