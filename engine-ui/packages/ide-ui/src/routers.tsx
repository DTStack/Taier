import { Route, hashHistory } from 'react-router'
import { getItem } from './utils/local'
import { USER_NAME } from './consts'
import IDE from './ide/workbench'
import Login from './uic'

export function isLogin () {
    const pid = getItem(USER_NAME)
    if (pid) {
        hashHistory.push('/')
    }
}

export function isLogout () {
    const pid = getItem(USER_NAME)
    if (!pid) {
        hashHistory.push('/login')
    }
}

export default (
    <>
        <Route path="/login" component={Login} onEnter={isLogin}></Route>
        <Route path="/" component={IDE} onEnter={isLogout}></Route>
    </>
)
