import * as React from 'react';

import workbenchAction from '../../consts/workbenchActionType';

export default class TabIcon extends React.Component<any, any> {
    render () {
        const { type } = this.props;

        let iconName = '';
        switch (type) {
            case workbenchAction.OPEN_SQL_QUERY: {
                iconName = 'sql.svg';
                break;
            }
            case workbenchAction.OPEN_TABLE:
            case workbenchAction.OPEN_TABLE_EDITOR:
            case workbenchAction.CREATE_TABLE: {
                iconName = 'table.svg';
                break;
            }
            case workbenchAction.OPEN_DATA_MAP:
            case workbenchAction.CREATE_DATA_MAP: {
                iconName = 'datamap.svg';
                break;
            }
            case workbenchAction.OPEN_DATABASE: {
                iconName = 'database.svg';
                break;
            }
            default:
                iconName = 'file.svg';
        }

        return <img
            className="s-icon"
            style={{
                width: '14px',
                height: '14px',
                position: 'absolute',
                top: '8px',
                left: '10px'
            }}
            src={`/public/analyticsEngine/img/icon/${iconName}`}
        />;
    }
}
