import React from 'react'
import ReactDOM from 'react-dom'
import { hashHistory } from 'react-router'

import App from './App'

ReactDOM.render(
    <React.StrictMode>
        <App history={hashHistory} />
    </React.StrictMode>,
    document.getElementById('root')
)
