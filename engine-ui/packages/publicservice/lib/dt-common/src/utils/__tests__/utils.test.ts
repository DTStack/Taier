import utils from '../index';

describe('utils', () => {
  const date = new Date('Wed Jul 29 2020 14:00:38');
  test(`formatDate as YYYY-MM-DD`, () => {
    const expected = '2020-07-29';
    const newVal = utils.formatDate(date);
    expect(newVal).toEqual(expected);
  });

  test(`formatDateTime as YYYY-MM-DD HH:mm:ss`, () => {
    const expected = '2020-07-29 14:00:38';
    const newVal = utils.formatDateTime(date);
    expect(newVal).toEqual(expected);
  });

  test(`formatDateHours as YYYY-MM-DD HH:mm`, () => {
    const expected = '2020-07-29 14:00';
    const newVal = utils.formatDateHours(date);
    expect(newVal).toEqual(expected);
  });

  test(`formatDayHours as MM-DD HH:mm`, () => {
    const expected = '07-29 14:00';
    const newVal = utils.formatDayHours(date);
    expect(newVal).toEqual(expected);
  });
  test(`formatHours as HH:mm`, () => {
    const expected = '14:00';
    const newVal = utils.formatHours(date);
    expect(newVal).toEqual(expected);
  });

  test(`formatMinute as HH:mm:ss`, () => {
    const expected = '14:00:38';
    const newVal = utils.formatMinute(date);
    expect(newVal).toEqual(expected);
  });
});
