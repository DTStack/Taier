import { cleanup, renderHook } from '@testing-library/react';

import useSize from '../useSize';

describe('Test useSize hook', () => {
    beforeEach(() => {
        cleanup();
        document.body.innerHTML = '';

        Object.defineProperty(global, 'ResizeObserver', {
            writable: true,
            value: jest.fn().mockImplementation((fn) => ({
                observe: jest.fn(() => 'Mocking works'),
                unobserve: jest.fn(),
                disconnect: jest.fn(),
                trigger: jest.fn((entries: any) => fn(entries)),
            })),
        });
    });
    it('Should filled with default value', () => {
        const dom = document.createElement('div');
        dom.style.width = '0px';
        dom.style.height = '0px';
        document.body.appendChild(dom);

        const { result } = renderHook(() => useSize(dom));

        expect(result.current).toEqual({ width: 0, height: 0 });
    });
});
