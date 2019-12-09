import * as React from 'react';
import { Row, Switch } from 'antd';

import { IGroup } from '../../../../model/group';

interface IProps {
    data: IGroup;
    onEnableAPI: () => void;
}

function wrapVal (value: any) {
    return value || '-';
}

const GroupBasicInfo: React.SFC<IProps> = function (props: IProps) {
    const { data = {}, onEnableAPI } = props;
    return (
        <div className="c-basicInfo">
            <Row className="c-basicInfo__row"><span>群组ID: </span><span>{wrapVal(data.groupId)}</span></Row>
            <Row className="c-basicInfo__row"><span>创建类型: </span><span>{wrapVal(data.groupType)}</span></Row>
            <Row className="c-basicInfo__row"><span >数据量: </span><span>{wrapVal(data.groupDataCount)}</span></Row>
            <Row className="c-basicInfo__row"><span >群组描述: </span><span>{wrapVal(data.groupDesc)}</span></Row>
            <Row className="c-basicInfo__row"><span >创建人: </span><span>{wrapVal(data.createBy)}</span></Row>
            <Row className="c-basicInfo__row">
                <span >开启API: </span>
                <span>
                    <Switch checkedChildren="开" unCheckedChildren="关" onChange={onEnableAPI} checked={data.isOpen}/>
                </span>
            </Row>
            <Row className="c-basicInfo__row"><span >创建时间: </span><span>{wrapVal(data.createAt)}</span></Row>
        </div>
    )
}

export default GroupBasicInfo;
