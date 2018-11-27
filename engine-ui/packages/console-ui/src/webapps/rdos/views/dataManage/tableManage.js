import React from 'react';

class TableManage extends React.Component {
    constructor (props) {
        super(props);
    }

    render () {
        return <div className="g-tablemanage">
            { this.props.children }
        </div>
    }
}

export default TableManage;
