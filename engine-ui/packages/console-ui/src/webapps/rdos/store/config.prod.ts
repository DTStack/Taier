// configureStore production
import { createStore, applyMiddleware } from 'redux'
import thunkMiddleware from 'redux-thunk'
import rootReducer from './reducers'

export default function configureStore (initialState: any) {
    const stroe = createStore(
        rootReducer,
        initialState,
        applyMiddleware(thunkMiddleware)
    );

    return stroe;
}
