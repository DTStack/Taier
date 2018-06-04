import React from 'react';

import {
    Button, Tooltip, Spin, Icon,
} from 'antd'

import TableRelation from './tableRelation';
import ColumnRelation from './columnRelation';

export default class TableRelationContainer extends React.Component {

    state = {
        showTableRelation: true,
    }

    onShow = (flag) => {
        this.setState({
            showTableRelation: flag,
        })
    }

    render() {
        const { showTableRelation } = this.state;
        return (
            <div className="table-ralation" 
                style = {{ position: 'relative', height: '650px' }}
            >
                { showTableRelation ? 
                    <TableRelation 
                        onShowColumn={() => this.onShow(false) }
                        {...this.props}
                    /> 
                    :
                    <ColumnRelation 
                        onShowTable={() => this.onShow(true)} 
                        {...this.props}
                    /> 
                }
            </div>
        )
    }
}
