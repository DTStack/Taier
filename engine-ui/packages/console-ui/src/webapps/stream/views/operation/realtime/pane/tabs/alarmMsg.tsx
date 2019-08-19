import * as React from 'react'

import ConfigList from './alarm/config';
import History from './alarm/history';

class AlarmMsg extends React.Component<any, any> {
    state: any = {
    }

    componentDidMount () {
        console.log('AlarmMsg')
    }
    render () {
        return (
            <div style={{ padding: '0px 21px 20px 21px' }}>
                <ConfigList data={this.props.data} />
                <History data={this.props.data} />
            </div>
        )
    }
}

export default AlarmMsg;
