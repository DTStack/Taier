import React, { Component } from 'react';
import { connect } from 'react-redux';

import { 
    Row, Tabs, Icon, 
    Popover, Tooltip 
} from 'antd';


import FolderTree from './folderTree'

@connect(state => {
    return {
        routing: state.routing
    }
})
class Sidebar extends Component {

    constructor(props) {
        super(props)
    }

    render() {
        return (
            <div className="sidebar">
                <header></header>
                <FolderTree />
            </div>
        )
    }
}

export default Sidebar;
