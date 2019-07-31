import * as React from 'react';
import { Row, Col, Collapse } from 'antd';

import utils from 'utils';

const Panel = Collapse.Panel;

export default class ScriptDetail extends React.Component<any, any> {
    constructor (props: any) {
        super(props);
    }

    render () {
        const scriptInfo = this.props.tabData
        return <div className="m-taksdetail">
            <Collapse bordered={false} defaultActiveKey={['1', '2']}>
                <Panel key="1" header="脚本属性">
                    <Row className="task-info">
                        <Row>
                            <Col span="10" className="txt-right">脚本名称：</Col>
                            <Col span="14">
                                {scriptInfo.name}
                            </Col>
                        </Row>
                        <Row>
                            <Col span="10" className="txt-right">创建人员：</Col>
                            <Col span="14">{scriptInfo.createUser.userName}</Col>
                        </Row>
                        <Row>
                            <Col span="10" className="txt-right">创建时间：</Col>
                            <Col span="14">
                                {utils.formatDateTime(scriptInfo.gmtCreate)}
                            </Col>
                        </Row>
                        <Row>
                            <Col span="10" className="txt-right">最近修改人员：</Col>
                            <Col span="14">{scriptInfo.modifyUser.userName}</Col>
                        </Row>
                        <Row>
                            <Col span="10" className="txt-right">最近修改时间：</Col>
                            <Col span="14">{utils.formatDateTime(scriptInfo.gmtModified)}</Col>
                        </Row>
                        <Row>
                            <Col span="10" className="txt-right">描述：</Col>
                            <Col span="14" style={{
                                lineHeight: '20px',
                                padding: '10 0'
                            }}>{scriptInfo.scriptDesc}</Col>
                        </Row>
                    </Row>
                </Panel>
            </Collapse>
        </div>
    }
}
