import React from 'react';

import {
    Button, Tooltip, Spin, Icon,
} from 'antd';

import TableRelation from './tableRelation';
import ColumnRelation from './columnRelation';

export default class TableRelationContainer extends React.Component {

    render() {
        const { showTableRelation, onShowBloodRelation } = this.props;
        return (
            <div className="table-ralation">
                { showTableRelation ? 
                    <TableRelation 
                        onShowColumn={() => onShowBloodRelation(false) }
                        {...this.props}
                    /> 
                    :
                    <ColumnRelation 
                        onShowTable={() => onShowBloodRelation(true)} 
                        {...this.props}
                    /> 
                }
            </div>
        )
    }
}
