import * as React from 'react';
import { Row } from 'antd';

import utils from 'utils';

interface IProps {
    data: any;
}

function wrapVal (value: string | number) {
    return value || '-';
}

const GroupBasicInfo: React.SFC<IProps> = function (props: IProps) {
    const { data = {} } = props;
    return (
        <div className="c-basicInfo">
            <Row className="c-basicInfo__row"><span>群组ID: </span><span>{wrapVal(data.id)}</span></Row>
            <Row className="c-basicInfo__row"><span >数据量: </span><span>{wrapVal(data.storeSize)}</span></Row>
            <Row className="c-basicInfo__row"><span >群组描述: </span><span>{wrapVal(data.description)}</span></Row>
            <Row className="c-basicInfo__row"><span >创建人: </span><span>{wrapVal(data.creator)}</span></Row>
            <Row className="c-basicInfo__row"><span >创建时间: </span><span>{wrapVal(utils.formatDateTime(data.createTime))}</span></Row>
        </div>
    )
}

export default GroupBasicInfo;
