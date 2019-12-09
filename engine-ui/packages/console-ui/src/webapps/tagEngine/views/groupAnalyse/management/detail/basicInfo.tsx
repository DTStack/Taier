import * as React from 'react';
import { Row, Switch, Col } from 'antd';

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
            <Row className="c-basicInfo__row" style={{ fontSize: '14px' }}><Col span={2} style={{ textAlign: 'right', paddingRight: '15px' }}>群组ID: </Col ><Col>{wrapVal(data.groupId)}</Col></Row>
            <Row className="c-basicInfo__row" style={{ fontSize: '14px' }}><Col span={2} style={{ textAlign: 'right', paddingRight: '15px' }}>数据量: </Col><Col>{wrapVal(data.groupDataCount)}</Col></Row>
            <Row className="c-basicInfo__row" style={{ fontSize: '14px' }}><Col span={2} style={{ textAlign: 'right', paddingRight: '15px' }}>群组描述: </Col><Col>{wrapVal(data.groupDesc)}</Col></Row>
            <Row className="c-basicInfo__row" style={{ fontSize: '14px' }}><Col span={2} style={{ textAlign: 'right', paddingRight: '15px' }}>创建人: </Col><Col>{wrapVal(data.createBy)}</Col></Row>
            <Row className="c-basicInfo__row" style={{ fontSize: '14px' }}>
                <Col span={2} style={{ textAlign: 'right', paddingRight: '15px' }}>开启API: </Col>
                <Col>
                    <Switch onChange={onEnableAPI} checked={data.isOpen} />
                </Col>
            </Row>
            <Row className="c-basicInfo__row" style={{ fontSize: '14px' }}><Col span={2} style={{ textAlign: 'right', paddingRight: '15px' }}>创建时间: </Col><Col>{wrapVal(data.createAt)}</Col></Row>
            <Row><Col span={2} style={{ fontSize: '16px', color: '#909090', padding: '5px 0' }}>群组规则</Col></Row>
            <Row className="c-basicInfo__row" style={{ fontSize: '14px' }}><Col span={2} style={{ textAlign: 'right', paddingRight: '15px' }}>群组类型: </Col><Col>{wrapVal(data.groupType)}</Col></Row>
        </div>
    )
}

export default GroupBasicInfo;
