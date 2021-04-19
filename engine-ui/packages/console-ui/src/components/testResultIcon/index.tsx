import * as React from 'react';
import { Icon, Tooltip, Modal } from 'antd';
import { isArray } from 'lodash';
// import { COMPONENT_TYPE_VALUE } from '../../consts'
const TEST_STATUS: any = {
    SUCCESS: true,
    FAIL: false
}
export default class TestRestIcon extends React.Component<any, any> {
    constructor (props: any) {
        super(props);
        this.state = {}
    }
    // show err message
    showDetailErrMessage (engine: any) {
        Modal.error({
            title: `错误信息`,
            content: `${engine.errorMsg}`,
            zIndex: 1061
        })
    }
    matchCompTest (testResult: any) {
        switch (testResult?.result) {
            case TEST_STATUS.SUCCESS: {
                return <Icon className='success-icon' type="check-circle" />
            }
            case TEST_STATUS.FAIL: {
                return <Tooltip
                    title={
                        <a
                            style={{ color: '#fff', overflow: 'scroll' }}
                            onClick={ this.showDetailErrMessage.bind(this, testResult)}
                        >
                            {
                                isArray(testResult.errorMsg)
                                    ? testResult.errorMsg.map(msg => {
                                        return <span key={msg.hadoopVersion}>{ msg.hadoopVersion ? (msg.hadoopVersion + ':') : '' }{msg.msg}</span>
                                    }) : <span>{testResult.errorMsg}</span>
                            }
                        </a>
                    }
                    placement='right'

                >
                    <Icon className='err-icon' type="close-circle" />
                </Tooltip>
            }
            default: {
                return null
            }
        }
    }
    render () {
        const { testStatus } = this.props;
        return this.matchCompTest(testStatus);
    }
}
