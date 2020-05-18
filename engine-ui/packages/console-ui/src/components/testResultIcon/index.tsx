import * as React from 'react';
import { Icon, Tooltip, Modal } from 'antd';
import { COMPONENT_TYPE_VALUE } from '../../consts'
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
            content: `${engine.errorMsg}`
        })
    }
    matchCompTest (testResult: any) {
        switch (testResult.result) {
            case TEST_STATUS.SUCCESS: {
                return <Icon className='success-icon' type="check-circle" />
            }
            case TEST_STATUS.FAIL: {
                return <Tooltip
                    title={
                        <a
                            style={{ color: '#fff' }}
                            onClick={ this.showDetailErrMessage.bind(this, testResult)}
                        >
                            {testResult.errorMsg}
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
        const { componentData, testStatus } = this.props;
        const { componentTypeCode } = componentData;
        const { flinkTestResult,
            sparkThriftTestResult,
            carbonTestResult,
            sparkTestResult,
            dtYarnShellTestResult,
            learningTestResult,
            hdfsTestResult,
            yarnTestResult,
            hiveServerTestResult,
            libraSqlTestResult,
            tidbSqlTestResult,
            impalaSqlTestResult,
            sftpTestResult } = testStatus;
        let testCompResult: any = {}
        switch (componentTypeCode) {
            case COMPONENT_TYPE_VALUE.FLINK: {
                testCompResult = flinkTestResult;
                break;
            }
            case COMPONENT_TYPE_VALUE.SPARKTHRIFTSERVER: { // hive <=> Spark Thrift Server
                testCompResult = sparkThriftTestResult;
                break;
            }
            case COMPONENT_TYPE_VALUE.CARBONDATA: {
                testCompResult = carbonTestResult;
                break;
            }
            case COMPONENT_TYPE_VALUE.IMPALASQL: {
                testCompResult = impalaSqlTestResult;
                break;
            }
            case COMPONENT_TYPE_VALUE.SPARK: {
                testCompResult = sparkTestResult;
                break;
            }
            case COMPONENT_TYPE_VALUE.DTYARNSHELL: {
                testCompResult = dtYarnShellTestResult;
                break;
            }
            case COMPONENT_TYPE_VALUE.LEARNING: {
                testCompResult = learningTestResult;
                break;
            }
            case COMPONENT_TYPE_VALUE.HIVESERVER: {
                testCompResult = hiveServerTestResult;
                break;
            }
            case COMPONENT_TYPE_VALUE.HDFS: {
                testCompResult = hdfsTestResult;
                break;
            }
            case COMPONENT_TYPE_VALUE.YARN: {
                testCompResult = yarnTestResult;
                break;
            }
            case COMPONENT_TYPE_VALUE.LIBRA_SQL: {
                testCompResult = libraSqlTestResult;
                break;
            }
            case COMPONENT_TYPE_VALUE.TIDB_SQL: {
                testCompResult = tidbSqlTestResult;
                break;
            }
            case COMPONENT_TYPE_VALUE.SFTP: {
                testCompResult = sftpTestResult;
                break;
            }
            default: {
                return null
            }
        }
        return this.matchCompTest(testCompResult);
    }
}
