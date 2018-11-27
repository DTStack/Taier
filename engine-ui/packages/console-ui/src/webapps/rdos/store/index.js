import { syncHistoryWithStore } from 'react-router-redux'
import { hashHistory } from 'react-router'

const configureStore = process.env.NODE_ENV === 'production'
    ? require('./config.prod') : require('./config.dev')

const store = configureStore.default();
const history = syncHistoryWithStore(hashHistory, store);

export {
    store,
    history
}
