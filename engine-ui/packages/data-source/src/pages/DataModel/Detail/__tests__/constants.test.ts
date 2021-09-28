import { holder } from '../constants';

describe('constantts of Detail:', () => {
  it('holder:', () => {
    const input = 'text';
    const output = holder(input);
    expect(output).toBe('text');
    expect(holder('')).toBe('--');
  });
});
