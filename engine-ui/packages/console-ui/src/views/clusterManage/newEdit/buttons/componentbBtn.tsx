import * as React from 'react'
import { Button, Popconfirm, Checkbox, Radio,
    Row, Col } from 'antd'
import * as _ from 'lodash'

import { isSourceTab } from '../help'
import { CONFIG_BUTTON_TYPE } from '../const'

const CheckboxGroup = Checkbox.Group;
const RadioGroup = Radio.Group;

interface IProps {
    activeKey: number;
    comps: any[];
    handleConfirm: Function;
}

interface IState {
    addComps: any[];
    deleteComps: any[];
}

export default class ComponentButton extends React.Component<IProps, IState> {
    state: IState = {
        addComps: [],
        deleteComps: []
    }

    getInitialValues = () => {
        const { comps } = this.props
        return comps.map((comp: any) => comp?.componentTypeCode)
    }

    handleSelectValue = () => {
        const { comps } = this.props
        const selectValues = comps.map((comp) => comp.componentTypeCode)
        return selectValues
    }

    handleCheckValues = (value: any[]) => {
        const { activeKey } = this.props
        const initialValues = this.getInitialValues()
        if (isSourceTab(activeKey)) {
            return
        }
        // 和初始值取两次交集可得删除的组件
        const intersectionArr = _.xor(value, initialValues)
        const deleteComps = _.intersection(intersectionArr, initialValues)
        // 和初始值取一次合集，一次交集可得增加的组件
        const unionArr = _.union(value, initialValues)
        const addComps = _.xor(unionArr, initialValues)
        this.setState({
            deleteComps, addComps
        })
    }

    handleRadioValues = (e: any) => {
        const initialValues = this.getInitialValues()
        // 和初始值取不一致时，新增为选中组件，删除已有组件
        if (!_.isEqual(initialValues[0], e.target.value)) {
            const deleteComps = initialValues
            const addComps = [...e.target.value]
            this.setState({
                deleteComps, addComps
            })
        }
    }

    renderTitle = () => {
        return <div className="c-componentButton__title">
            <span>组件配置</span>
        </div>
    }

    renderContent = () => {
        const { activeKey } = this.props
        const initialValues = this.getInitialValues()

        if (isSourceTab(activeKey)) {
            return (<>
                {this.renderTitle()}
                <RadioGroup
                    className="c-componentButton__content"
                    defaultValue={initialValues[0]}
                    onChange={this.handleRadioValues}
                >
                    <Row>
                        {CONFIG_BUTTON_TYPE[activeKey].map((item: any) => {
                            return <Col key={`${item.code}`}>
                                <Radio value={item.code}>{item.componentName}</Radio>
                            </Col>
                        })}
                    </Row>
                </RadioGroup>
            </>)
        }
        return (<>
            {this.renderTitle()}
            <CheckboxGroup
                className="c-componentButton__content"
                defaultValue={initialValues}
                onChange={this.handleCheckValues}
            >
                <Row>
                    {CONFIG_BUTTON_TYPE[activeKey].map((item: any) => {
                        return <Col key={`${item.code}`}>
                            <Checkbox value={item.code}>{item.componentName}
                            </Checkbox>
                        </Col>
                    })}
                </Row>
            </CheckboxGroup>
        </>)
    }

    handleConfirm = () => {
        const { addComps, deleteComps } = this.state
        this.props.handleConfirm(addComps, deleteComps)
    }

    handleCancel = () => {
        this.setState({
            addComps: [],
            deleteComps: []
        })
    }

    render () {
        return (
            <Popconfirm
                icon={null}
                placement="topRight"
                title={this.renderContent()}
                onConfirm={this.handleConfirm}
                onCancel={this.handleCancel}
            >
                <Button className="c-editCluster__componentButton">
                    <i className="iconfont iconzujianpeizhi" style={{ marginRight: 2 }} />
                    组件配置
                </Button>
            </Popconfirm>
        )
    }
}
