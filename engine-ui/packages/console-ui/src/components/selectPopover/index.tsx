import * as React from 'react';
import { Button, Popover, Checkbox,
    Row, Col, Radio } from 'antd';
import {
    SOURCE_COMPONENTS, STORE_COMPONENTS, COMPUTE_COMPONENTS, COMMON_COMPONENTS, TABS_TITLE_KEY } from '../../consts';
const CheckboxGroup = Checkbox.Group;
const RadioGroup = Radio.Group;

export default class SelectPopver extends React.Component<any, any> {
    setCheckboxCompData = (value: any) => {
        const { setCheckboxCompData } = this.props;
        setCheckboxCompData(value);
    }
    setRadioCompData = (e: any) => {
        const { setRadioCompData } = this.props;
        setRadioCompData(e);
    }
    componentBtn = () => {
        let content: any;
        const { compTypeKey, defaultValue, handleCanclePopover, modifyTabCompData } = this.props;
        // console.log('defaultValue------selectValue', defaultValue, selectValue)
        const popoverFooter = (
            <div style={{ marginTop: 37, float: 'right' }}>
                <Row>
                    <Col>
                        <Button size="small" onClick={handleCanclePopover}>取消</Button>
                        <Button size="small" type="primary" style={{ marginLeft: 8 }} onClick={() => modifyTabCompData()}>确认</Button>
                    </Col>
                </Row>
            </div>
        )
        switch (compTypeKey) {
            case TABS_TITLE_KEY.COMMON:
                content = (
                    <CheckboxGroup style={{ width: '100%' }} onChange={this.setCheckboxCompData} defaultValue={defaultValue}>
                        <Row>
                            {COMMON_COMPONENTS.map((item: any) => {
                                return <Col key={`${item.componentTypeCode}`}><Checkbox value={item.componentTypeCode}>{item.componentName}</Checkbox></Col>
                            })}
                        </Row>
                        {popoverFooter}
                    </CheckboxGroup>
                )
                break;
            case TABS_TITLE_KEY.SOURCE:
                content = (
                    <RadioGroup style={{ width: '100%' }} onChange={this.setRadioCompData} defaultValue={defaultValue[0]}>
                        <Row>
                            {SOURCE_COMPONENTS.map((item: any) => {
                                return <Col key={`${item.componentTypeCode}`}><Radio value={item.componentTypeCode}>{item.componentName}</Radio></Col>
                            })}
                        </Row>
                        {popoverFooter}
                    </RadioGroup>
                )
                break;
            case TABS_TITLE_KEY.STORE:
                content = (
                    <CheckboxGroup style={{ width: '100%' }} onChange={this.setCheckboxCompData} defaultValue={defaultValue}>
                        <Row>
                            {STORE_COMPONENTS.map((item: any) => {
                                return <Col key={`${item.componentTypeCode}`}><Checkbox value={item.componentTypeCode}>{item.componentName}</Checkbox></Col>
                            })}
                        </Row>
                        {popoverFooter}
                    </CheckboxGroup>
                )
                break;
            case TABS_TITLE_KEY.COMPUTE:
                content = (
                    <CheckboxGroup style={{ width: '100%' }} onChange={this.setCheckboxCompData} defaultValue={defaultValue}>
                        <Row>
                            {COMPUTE_COMPONENTS.map((item: any) => {
                                return <Col key={`${item.componentTypeCode}`}><Checkbox value={item.componentTypeCode}>{item.componentName}</Checkbox></Col>
                            })}
                        </Row>
                        {popoverFooter}
                    </CheckboxGroup>
                )
                break;
            default:
                content = ''
                break;
        }
        return content;
    }
    render () {
        const { popoverVisible, setPopverVisible } = this.props;
        const content = this.componentBtn();
        return (
            <Popover
                title="组件配置"
                placement="topRight"
                trigger="click"
                visible={popoverVisible}
                content={content}
                style={{ width: 240 }}
            >
                <Button className="c-editCluster__componentButton" onClick={() => setPopverVisible()}><i className="iconfont iconzujianpeizhi" style={{ marginRight: 2 }}></i>组件配置</Button>
            </Popover>
        )
    }
}
