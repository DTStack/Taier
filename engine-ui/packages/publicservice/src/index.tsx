import * as React from 'react'
import {render} from 'react-dom'
import { Provider } from 'react-redux';
import Routers from './router'
import "assets/styles/index.scss";
import "assets/styles/antd.less";
import store from './store';
import  '@babel/polyfill'

class App extends React.Component{
    
    constructor(props) {
        super(props)
    }
    render(){
        return(
          <Provider store={store}>
            <Routers />
        </Provider>
        )
    }
}
render(<App/>,document.getElementById('root'))

