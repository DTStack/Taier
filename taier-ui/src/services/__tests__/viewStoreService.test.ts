import { waitFor } from '@testing-library/react';
import viewStoreService from '../viewStoreService';

describe('Test ViewStoreService', () => {
    test('Should support getter and setter', () => {
        viewStoreService.setViewStorage('test', 'test');
        expect(viewStoreService.getViewStorage<string>('test')).toBe('test');

        viewStoreService.setViewStorage('test', (pre) => `${pre}-double`);
        expect(viewStoreService.getViewStorage<string>('test')).toBe('test-double');
    });

    test('Should support clear Map', () => {
        viewStoreService.setViewStorage('test2', 'test');

        expect(viewStoreService.getViewStorage<string>('test')).toBe('test-double');
        expect(viewStoreService.getViewStorage<string>('test2')).toBe('test');

        // Specify an Map to be cleared
        expect(viewStoreService.clearStorage('test2')).toBe(true);
        expect(viewStoreService.getViewStorage<string>('test2')).toBe(undefined);

        // Clean all Map
        expect(viewStoreService.clearStorage()).toBe(true);
        expect(viewStoreService.getViewStorage<string>('test')).toBe(undefined);
    });

    test('Should support emit and subscribe', () => {
        const fn = jest.fn();
        viewStoreService.onStorageChange(fn);

        viewStoreService.emiStorageChange('test');

        waitFor(() => {
            expect(fn).toBeCalledWith('test');
        });
    });
});
