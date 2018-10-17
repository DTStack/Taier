import React, { Component } from 'react';
import { Button, Row } from 'antd';

import DataMapForm from './form';

class CreateDataMap extends Component {
    render () {
        return (
            <div className="pane-wrapper" style={{ padding: '24px 20px 50px 20px' }}>
                <DataMapForm isCreate={true} {...this.props} />
                <Row style={{paddingLeft: 130}}>
                    <Button style={{ width: 90, height: 30 }} type="primary">创建</Button>
                </Row>
            </div>
        )
    }
}

export default CreateDataMap