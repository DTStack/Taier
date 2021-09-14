import React from 'react';
import ReactDOM from 'react-dom';
import { hashHistory } from 'react-router';
import 'dt-react-component/lib/style/index.css';

import App from './views/App';
import '@/styles/task/task.scss';

ReactDOM.render(
    <React.StrictMode>
        <App history={hashHistory} />
    </React.StrictMode>,
    document.getElementById('root')
);
