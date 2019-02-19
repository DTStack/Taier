// api.test.js
const API = require('../index');
// import API from '../index';

test('getLoginedUser', async () => {
    const res = await API.getLoginedUser();
    expect(res.code).toBe(0);
})
