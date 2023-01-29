import { render } from '@testing-library/react';
import SpreadSheet from '..';

describe('Test SpreadSheet Component', () => {
    beforeEach(() => {
        // HotTable will generate randomId by Math.random
        // refer to: https://github.com/handsontable/handsontable/blob/11.1.0/wrappers/react/src/helpers.tsx
        jest.spyOn(global.Math, 'random').mockReturnValue(10);
    });

    it('Should match snapshot', () => {
        const { asFragment } = render(
            <SpreadSheet
                columns={['age', 'name']}
                data={[
                    ['1', 'Bob'],
                    ['2', 'Sam'],
                ]}
            />
        );

        expect(asFragment()).toMatchSnapshot();
    });

    it('Should match snapshot with empty data', () => {
        const { asFragment } = render(<SpreadSheet columns={['age', 'name']} data={[]} />);

        expect(asFragment()).toMatchSnapshot();
    });
});
