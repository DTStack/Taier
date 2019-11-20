import utils from '../index';

describe('utils.convertBytes', () => {
    test(`convert byte to unit B`, () => {
        const byte = 1024;
        const expected = `1024 B`;
        const newVal = utils.convertBytes(byte, 'B');
        expect(newVal).toEqual(expected);
    })
    test(`convert byte to unit KB`, () => {
        const byte = 1024;
        const expected = `1 KB`;
        const newVal = utils.convertBytes(byte, 'KB');
        expect(newVal).toEqual(expected);
    })
    test(`convert byte to unit MB`, () => {
        const byte = 1024 * 3;
        const expected = `3 MB`;
        const newVal = utils.convertBytes(byte, 'MB');
        expect(newVal).toEqual(expected);
    })
    test(`convert byte to unit GB`, () => {
        const byte = 1024 * 4;
        const expected = `4 GB`;
        const newVal = utils.convertBytes(byte, 'GB');
        expect(newVal).toEqual(expected);
    })
    test(`convert byte to unit TB`, () => {
        const byte = 1184380146909; // 1024 * 5;
        const expected = `1.08 TB`;
        const newVal = utils.convertBytes(byte, 'TB');
        expect(newVal).toEqual(expected);
    })
    test(`convert byte to unit PB`, () => {
        const byte = 1024 * 6;
        const expected = `6 PB`;
        const newVal = utils.convertBytes(byte, 'PB');
        expect(newVal).toEqual(expected);
    })
});