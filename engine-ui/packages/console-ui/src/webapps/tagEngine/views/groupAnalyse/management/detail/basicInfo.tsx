import * as React from 'react';
import { Row, Switch, Col, message } from 'antd';
import CopyToClipboard from 'react-copy-to-clipboard';

import { IGroup } from '../../../../model/group';

interface IProps {
    data: IGroup;
    onEnableAPI: () => void;
}

function wrapVal (value: any) {
    return value || '-';
}
function copyOk () {
    message.success('复制成功！');
}
const GroupBasicInfo: React.SFC<IProps> = function (props: IProps) {
    const { data, onEnableAPI } = props;
    const style1 = { fontSize: '14px' };
    console.log('data:', data)
    return (
        <div className="c-basicInfo">
            <Row className="c-basicInfo__row" style={style1}><Col span={2} className="basicInfo_col" >群组ID: </Col ><Col>{wrapVal(data.groupId)}</Col></Row>
            <Row className="c-basicInfo__row" style={style1}><Col span={2} className="basicInfo_col">数据量: </Col><Col>{wrapVal(data.groupDataCount)}</Col></Row>
            <Row className="c-basicInfo__row" style={style1}><Col span={2} className="basicInfo_col">群组描述: </Col><Col>{wrapVal(data.groupDesc)}</Col></Row>
            <Row className="c-basicInfo__row" style={style1}><Col span={2} className="basicInfo_col">创建人: </Col><Col>{wrapVal(data.createBy)}</Col></Row>
            <Row className="c-basicInfo__row" style={style1}><Col span={2} className="basicInfo_col">创建时间: </Col><Col>{wrapVal(data.createAt)}</Col></Row>
            <Row className="c-basicInfo__row" style={style1}>
                <Col span={2} className="basicInfo_col">开启API: </Col>
                <Col>
                    <Switch onChange={onEnableAPI} checked={data.isOpen} />
                </Col>
            </Row>
            {
                data.isOpen ? (<Row className="c-basicInfo__row" style={style1}>
                    <Col span={2} className="basicInfo_col">调用URL: </Col>
                    <Col span={5}>{wrapVal(data.apiUrl)}</Col>
                    <Col span={2}>
                        <CopyToClipboard key="copy" text={data.apiUrl}
                            onCopy={copyOk}>
                            <a >复制</a>
                        </CopyToClipboard>
                    </Col>
                </Row>) : ''
            }

            <Row><Col span={2} style={{ fontSize: '16px', color: '#909090', padding: '5px 0' }}>群组规则</Col></Row>
            <Row className="c-basicInfo__row" style={style1}><Col span={2} className="basicInfo_col">群组类型: </Col><Col>{wrapVal(data.groupType)}</Col></Row>
        </div>
    )
}

export default GroupBasicInfo;
