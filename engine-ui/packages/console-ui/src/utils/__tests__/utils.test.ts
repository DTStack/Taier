import utils from '../index';

describe('utils.convertBytes', () => {
    test(`convert byte to unit B`, () => {
        const byte = 10.24;
        const expected = `10.24 B`;
        const newVal = utils.convertBytes(byte);
        expect(newVal).toEqual(expected);
    })
    test(`convert byte to unit KB`, () => {
        const byte = 1024;
        const expected = `1 KB`;
        const newVal = utils.convertBytes(byte);
        expect(newVal).toEqual(expected);
    })
    test(`convert byte to unit MB`, () => {
        const byte = 10241024;
        const expected = `9.77 MB`;
        const newVal = utils.convertBytes(byte);
        expect(newVal).toEqual(expected);
    })
    test(`convert byte to unit GB`, () => {
        const byte = 3029021814;
        const expected = `2.82 GB`;
        const newVal = utils.convertBytes(byte);
        expect(newVal).toEqual(expected);
    })
    test(`convert byte to unit TB`, () => {
        const byte = 1184380146909; // 1024 * 5;
        const expected = `1.08 TB`;
        const newVal = utils.convertBytes(byte);
        expect(newVal).toEqual(expected);
    })
    test(`convert byte to unit PB`, () => {
        const byte = 1125899906842624;
        const expected = `1 PB`;
        const newVal = utils.convertBytes(byte);
        expect(newVal).toEqual(expected);
    })
});
