import React, { Component } from 'react';
import { Row, Button } from 'antd';

import DataMapForm from './form';

class DataMap extends Component {
    render () {
        const { isCreate } = this.props;
      
        return (
            <div className="pane-wrapper" style={{ padding: '24px 20px 50px 20px' }}>
                <DataMapForm {...this.props} />
                <Row style={{paddingLeft: 130}}>
                    {
                        isCreate ? 
                            <Button style={{ width: 90, height: 30 }} type="primary">创建</Button>
                        :
                            <Button style={{ width: 90, height: 30, color: 'red' }}>删除</Button>
                    }
                    
                </Row>
            </div>
        )
    }
}

export default DataMap