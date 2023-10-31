import { render } from '@testing-library/react';
import { omit } from 'lodash';

import actions from '../editorActions';

describe("Test editor's actions", () => {
    Object.keys(actions).forEach((action) => {
        const component = actions[action];

        it(`Match ${component.id} snapshot`, () => {
            if (typeof component.icon === 'string') {
                expect(component).toMatchSnapshot();
            } else {
                expect(
                    render(
                        <>
                            <pre>{JSON.stringify(omit(component, ['icon']))}</pre>
                            {component.icon}
                        </>
                    ).asFragment()
                ).toMatchSnapshot();
            }
        });
    });
});
