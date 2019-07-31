import * as React from 'react'
import { Icon } from 'antd';
import { Link } from 'react-router';

class HaveNoApi extends React.Component<any, any> {
    render () {
        return (
            <div className="box-mid">
                <Icon type="shop" style={{ fontSize: 100, color: '#94A8C6' }} />
                <br/>
                <span className="color-666 font-14 margin-t14 block">您还没有订购任何Api，您可以进入 <Link to="/api/market">Api市场</Link> 进行订购</span>
            </div>
        )
    }
}

export default HaveNoApi;
