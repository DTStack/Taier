import * as React from 'react'
import { Button, Popconfirm, Checkbox, Radio,
    Row, Col } from 'antd'
import * as _ from 'lodash'

import ModifyCompsModal from '../modifyModal'
import { isSourceTab } from '../../help'
import { CONFIG_BUTTON_TYPE } from '../../const'

const CheckboxGroup = Checkbox.Group;
const RadioGroup = Radio.Group;

interface IProps {
    activeKey: number;
    comps: any[];
    popVisible: boolean;
    handleConfirm: Function;
    handlePopVisible: Function;
}

interface IState {
    visible: boolean;
    addComps: any[];
    deleteComps: any[];
    initialValues: any[];
}

export default class ComponentButton extends React.Component<IProps, IState> {
    state: IState = {
        visible: false,
        addComps: [],
        deleteComps: [],
        initialValues: []
    }

    componentDidMount () {
        this.setState({
            initialValues: this.getInitialValues()
        })
    }

    componentDidUpdate (preProps: any) {
        const { comps, popVisible } = this.props
        if ((preProps.comps != comps) || (preProps.popVisible != popVisible && popVisible)) {
            this.setState({
                initialValues: this.getInitialValues()
            })
        }
    }

    getInitialValues = () => {
        const { comps = [] } = this.props
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
            deleteComps, addComps, initialValues: value
        })
    }

    handleRadioValues = (e: any) => {
        const initialValues = this.getInitialValues()

        // 和初始值取不一致时，新增为选中组件，删除已有组件，相同时同步value值
        if (!_.isEqual(initialValues[0], e.target.value)) {
            const deleteComps = initialValues
            let addComps = []
            addComps.push(e.target.value)
            this.setState({
                deleteComps, addComps, initialValues: [e.target.value]
            })
        } else {
            this.setState({
                initialValues: [e.target.value]
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
        const { initialValues } = this.state

        if (isSourceTab(activeKey)) {
            return (<>
                {this.renderTitle()}
                <RadioGroup
                    className="c-componentButton__content"
                    defaultValue={initialValues[0]}
                    value={initialValues[0]}
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
                value={initialValues}
                defaultValue={initialValues}
                onChange={this.handleCheckValues}
            >
                <Row>
                    {CONFIG_BUTTON_TYPE[activeKey].map((item: any) => {
                        return <Col key={`${item.code}`}>
                            <Checkbox value={item.code}>{item.componentName}</Checkbox>
                        </Col>
                    })}
                </Row>
            </CheckboxGroup>
        </>)
    }

    handleConfirm = () => {
        const { addComps, deleteComps } = this.state
        this.props.handlePopVisible(false)
        if (deleteComps.length > 0) {
            this.setState({
                visible: true
            })
        } else {
            this.props.handleConfirm(addComps, deleteComps)
        }
    }

    handleCancel = () => {
        this.setState({
            addComps: [],
            deleteComps: [],
            visible: false
        })
        this.props.handlePopVisible(false)
    }

    render () {
        const { deleteComps, addComps, visible } = this.state

        return (
            <>
                <Popconfirm
                    icon={null}
                    placement="topRight"
                    title={this.renderContent()}
                    onConfirm={this.handleConfirm}
                    onCancel={this.handleCancel}
                >
                    <Button className="c-editCluster__componentButton" onClick={() => this.props.handlePopVisible()}>
                        <i className="iconfont iconzujianpeizhi" style={{ marginRight: 2 }} />
                        组件配置
                    </Button>
                </Popconfirm>
                <ModifyCompsModal
                    visible={visible}
                    addComps={addComps}
                    deleteComps={deleteComps}
                    onCancel={this.handleCancel}
                    onOk={() => {
                        this.setState({
                            visible: false
                        })
                        this.props.handleConfirm(addComps, deleteComps)
                    }}
                />
            </>
        )
    }
}
