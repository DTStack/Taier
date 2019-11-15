import * as React from 'react';
import { Row, Col } from 'antd';

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
        <div>
            <Row><Col span={2}>群组ID: </Col><Col>{wrapVal(data.id)}</Col></Row>
            <Row><Col span={2}>数据量: </Col><Col>{wrapVal(data.storeSize)}</Col></Row>
            <Row><Col span={2}>群组描述: </Col><Col>{wrapVal(data.description)}</Col></Row>
            <Row><Col span={2}>创建人: </Col><Col>{wrapVal(data.creator)}</Col></Row>
            <Row><Col span={2}>创建时间: </Col><Col>{wrapVal(utils.formatDateTime(data.createTime))}</Col></Row>

        </div>
    )
}

export default GroupBasicInfo;
