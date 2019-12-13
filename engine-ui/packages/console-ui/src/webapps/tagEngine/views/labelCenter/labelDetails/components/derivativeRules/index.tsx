import * as React from 'react';
import { Row, Col } from 'antd';
import TagValues from '../tagValues/index';
import shortid from 'shortid';
import PanelSelect from '../panelSelect';
import { API } from '../../../../../api/apiMap';
import './style.scss';

interface IProps {
    tagId: string | number;
    entityId: string | number;
}
interface IState {
    data: any;
    tags: any[];
    atomTagList: any[];
    select: string;
    entityName: string;
    tagConfigData: any;
}

function wrapVal (value: string | number) {
    return value || '-';
}
class DerivativeRules extends React.PureComponent<IProps, IState> {
    state: IState = {
        data: {},
        tags: [],
        atomTagList: [],
        select: '',
        entityName: '',
        tagConfigData: {}
    }
    componentDidMount () {
        const { tagId } = this.props;
        if (tagId) {
            this.getDeriveTagVO(tagId);
        }
    }
    getDeriveTagVO = (tagId) => {
        API.getDeriveTagVO({ tagId }).then(res => {
            const { code, data } = res;
            if (code === 1) {
                const { entityName, tags } = data;
                let newtags = tags.map(item => {
                    return {
                        tagValueId: item.tagValueId,
                        label: item.tagValue,
                        value: shortid(),
                        valid: true,
                        params: JSON.parse(item.param)
                    }
                })
                if (newtags && newtags.length) {
                    let { params } = newtags[0];
                    const { children = [] } = params;
                    children.forEach(item => {
                        this.getAtomTagList(item.entityId)
                    })
                }
                this.setState({
                    tags: newtags,
                    entityName,
                    select: newtags.length ? newtags[0].value : ''
                })
                this.setState({
                    data: data
                })
            }
        })
    }
    getAtomTagList = (entityId) => { // 获取原子标签列表
        const { tagConfigData } = this.state;
        API.getAtomTagList({
            entityId
        }).then(res => {
            const { code, data } = res;
            if (code === 1) {
                if (data && data.length) {
                    this.setState({
                        tagConfigData: Object.assign({}, tagConfigData, { [entityId]: data })
                    })
                }
            }
        })
    }
    onSelect = (value) => {
        this.setState({
            select: value
        })
    }
    render () {
        const { tags, select, entityName, tagConfigData } = this.state;
        const currentTag = select ? tags.find(item => item.value == select) : '';
        const treeData = currentTag ? currentTag.params : '';
        return (
            <div className="derivativeRules">
                <Row className="info_item" type="flex"><Col className="label">所属实体： </Col><Col>{wrapVal(entityName)}</Col></Row>
                <Row className="info_item" type="flex"><Col className="label">标签值：</Col> <Col><TagValues select={select} config={{}} value={tags} onSelect={this.onSelect} /></Col> </Row>
                <Row className="info_item" type="flex">
                    <Col className="label">标签值规则： </Col>
                    <Col>
                        <PanelSelect tagConfigData={tagConfigData} treeData={treeData}/>
                    </Col>
                </Row>
            </div>
        )
    }
}

export default DerivativeRules;
