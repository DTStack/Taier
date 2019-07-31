import * as React from 'react';

class TableManage extends React.Component<any, any> {
    constructor (props: any) {
        super(props);
    }

    render () {
        return <div className="g-tablemanage">
            { this.props.children }
        </div>
    }
}

export default TableManage;
