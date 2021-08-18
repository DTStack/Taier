import { getStore } from '../comm';

const rootReducer = require('../controller').default;
const { store } = getStore(rootReducer, 'hash');

export default store;
