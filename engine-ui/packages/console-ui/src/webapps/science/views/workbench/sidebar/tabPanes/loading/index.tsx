import * as React from 'react';
import { Spin } from 'antd';

class SiderBarLoading extends React.Component<any, any> {
    render () {
        return <div style={{ textAlign: 'center', paddingTop: '10px' }}>
            <Spin />
        </div>
    }
}
export default SiderBarLoading;
