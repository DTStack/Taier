import React from 'react';
import { connect } from 'react-redux';
import { Row, Col, Tabs} from 'antd';


const TabPane = Tabs.TabPane;



function TaskInfo(props,version) {
    const taskInfo = version;

    function versionInfo(info,version){
        const title = version ;
         return (
            <Col >
                <div>{title}</div>
                <div className="box-padding">
                    <Row>
                        <Col span="10" className="txt-right">任务名称：</Col>
                        <Col span="14">
                            {taskInfo}
                        </Col>
                    </Row>
                    <Row>
                        <Col span="10" className="txt-right">任务类型：</Col>
                        <Col span="14">
                        {}
                        </Col>
                    </Row>
                    <Row>
                        <Col span="10" className="txt-right">创建人员：</Col>
                        <Col span="14">{taskInfo}</Col>
                    </Row>
                    <Row>
                        <Col span="10" className="txt-right">创建时间：</Col>
                        <Col span="14">
                            {}
                        </Col>
                    </Row>
                    <Row>
                        <Col span="10" className="txt-right">最近修改人员：</Col>
                        <Col span="14">{taskInfo}</Col>
                    </Row>
                    <Row>
                        <Col span="10" className="txt-right">最近修改时间：</Col>
                        <Col span="14">{}</Col>
                    </Row>
                    <Row>
                        <Col span="10" className="txt-right">描述：</Col>
                        <Col span="14" style={{
                                lineHeight: '20px',
                                padding: '10 0'
                            }}>{taskInfo}
                        </Col>
                    </Row>
                </div>
        </Col >
         )
    }    

    return (
        <Row  gutter={16} className="diff-params">
           {versionInfo('当前版本','当前版本')}
           {versionInfo('历史版本','历史版本')}
        </Row>
    )
}

class DiffParams extends React.Component {

    constructor(props) {
        super(props);
    }

    callback = (key) => {
        console.log(key);
      }

    render() {
        const { tabData } = this.props;
        return <div className="m-taksdetail">
            <Tabs onChange={this.callback} type="card">
                <TabPane tab="Tab 1" key="config">{TaskInfo(1111)}</TabPane>
                <TabPane tab="Tab 2" key="params">Content of Tab Pane 2</TabPane>
            </Tabs>
        </div>
    }
}

export default connect((state) => {
    return {};
}, dispatch => {
    return {
       
    }
})(DiffParams);