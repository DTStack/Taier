import React, { Component } from 'react';
import { Button } from 'antd';

import DataMapForm from './form';

class CreateDataMap extends Component {
    render () {
        return (
            <div className="pane-wrapper" style={{ padding: '0px 20px' }}>
                <DataMapForm />
                <Button>创建</Button>
            </div>
        )
    }
}

export default CreateDataMap