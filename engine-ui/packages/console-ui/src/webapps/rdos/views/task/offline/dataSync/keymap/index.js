import React from 'react';
import { connect } from 'react-redux';
import { isNumber, assign, isEqual, isObject, isNaN } from 'lodash'
import { 
    Button, Row, Col, Modal, 
    Form, Select, Input, Tooltip,
    InputNumber, message, Icon,
} from 'antd';

import { select, selectAll, mouse } from 'd3-selection';
import scrollText from 'widgets/scrollText';

import { 
    keyMapActions 
} from '../../../../../store/modules/offlineTask/offlineAction';
import utils from 'utils';
import Api from '../../../../../api';

import { 
    hdfsFieldTypes, 
    formItemLayoutm, 
    DATA_TYPE_ARRAY, 
    DATA_SOURCE,
} from '../../../../../comm/const';

import KeyMapModal from './keymapModal'
import BatchModal from './batchModal'
import ConstModal from './constModal'

const FormItem = Form.Item;
const Option = Select.Option;

const DefaultRowKey = { // HBase默认行健
    cf: '-',
    key: 'rowkey',
    type: 'STRING',
};

/**
 * 是否为HDFS类型
 * @param {*} type 
 */
function isHdfsType(type) {
    return DATA_SOURCE.HDFS === parseInt(type, 10)
}

/**
 * 是否属于关系型数据源
 * @param {*} type 
 */
function isRDB(type) {
    return DATA_TYPE_ARRAY.indexOf(parseInt(type, 10)) > -1
}

/**
 * 判断字段是否匹配
 * @param {*} source 
 * @param {*} target 
 */
function isFieldMatch(source, target) {
    if (isObject(source) && isObject(target)) {
        return isEqual(source, target);
    } else if(isObject(source) && !isObject(target) ) {
        const sourceVal = source.key || source.index
        return sourceVal === target
    } else if (!isObject(source) && isObject(target)) {
        const targetVal = target.key || target.index
        return source === targetVal
    } else {
        return source === target
    }
}

class Keymap extends React.Component{

    constructor(props) {
        super(props);

        this.state = {
            h: 40,
            w: 230,
            W: 450,
            padding: 10,
            rowMap: false,
            nameMap: false,
            keyModalVisible: false,
            visibleConst: false,
            keyModal: {// 用于字段添加、编辑操作Model
                operation: 'add', // 默认增加操作
                isReader: true,
                editField: '',
                position: -1,
                source: '',
                fileType: 'orc'
            }, //hdfs添加行modal
            batchModal: { // 批量导入目标字段
                visible: false,
                batchText: '',
            },
            batchSourceModal: { // 批量导入源表字段
                visible: false,
                batchText: '',
            },
            sourceColumnFamily: '', // 源表列族
            targetColumnFamily: '', // 目标列族
        };
    }

    getCanvasW() {
        let w = 450;
        const canvas = document.querySelector('.steps-content')
        if (canvas) {
            const newW = canvas.getBoundingClientRect().width/6*5;
            if(newW > w) w = newW;
        }
        return w;
    }

    componentDidMount() {
        this.$canvas = select(this.canvas);
        this.$activeLine = select('#activeLine');
        this.setState({
            W: this.getCanvasW(),
        })
        this.drawSvg();
        this.listenResize();
        this.loadColumnFamily();
        this.initData();
    }

    resize = () => {
        this.setState({
            W: this.getCanvasW()
        })
    }

    componentWillUnmount() {
        window.removeEventListener('resize', this.resize, false);
    }

    listenResize() {
        if(window.addEventListener) {
            window.addEventListener('resize', this.resize, false);
        }
    }

    componentDidUpdate() {
        this.$canvas.selectAll('.dl, .dr, .lines').remove();
        this.drawSvg();
    }

    drawSvg() {
        this.renderDags();
        this.renderLines();
        this.bindEvents();
    }

    renderDags() {
        const { w, h, W, padding } = this.state;
        const { targetCol, sourceCol, keymap } = this.props;

        this.$canvas.append('g')
            .attr('class', 'dl')
            .selectAll('g')
            .data(sourceCol)
            .enter()
            .append('g')
            .attr('class', 'col-dag-l')
            .append('circle')
            .attr('class', 'dag-circle')
            .attr('cx', (d, i) => padding)
            .attr('cy', (d, i) => h * (i + 1.5))
            .attr('r', 5)
            .attr('stroke-width', 2)
            .attr('stroke', '#fff')
            .attr('fill', '#2491F7');

        this.$canvas.append('g')
            .attr('class', 'dr')
            .selectAll('g')
            .data(targetCol)
            .enter()
            .append('g')
            .attr('class', 'col-dag-r')
            .append('circle')
            .attr('class', 'dag-circle')
            .attr('cx', (d, i) => { return (W - w*2 - padding)})
            .attr('cy', (d, i) => h * (i + 1.5))
            .attr('r', 5)
            .attr('stroke-width', 2)
            .attr('stroke', '#fff')
            .attr('fill', '#2491F7');
    }

    renderLines() {
        const { w, h, W, padding } = this.state;
        const { 
            targetCol, sourceCol, keymap, 
            sourceSrcType, sourceFileType, 
            targetSrcType, targetFileType 
        } = this.props;
        const { source, target } = keymap;
        const $dagL = selectAll('.col-dag-l');
        const $dagR = selectAll('.col-dag-r');
        const posArr = [];

        /**
         * 左侧source中连接起点对象keymap.source[i] 匹配到 所有字段对象中的一个 sourceMap.column[i]
         * @param {*} columnItem
         * @param {*} keymapItem
         * @return {boolean} isMatch
         */
        const matchKeymapToColumn_s = (columnItem, keymapItem) => {
            let isMatch = false;
            if (isRDB(sourceSrcType)) {// sql/oracle/sqlserver
                isMatch = columnItem.key === keymapItem;
            } else {
                // isMatch = isEqual(columnItem, keymapItem);
                isMatch = isFieldMatch(columnItem, keymapItem);
            }
            return isMatch;
        };

        const matchKeymapToColumn_t = (columnItem, keymapItem) => {
            let isMatch = false;
            if (isRDB(targetSrcType)) { // sql/oracle/sqlserver/hive
                isMatch = columnItem.key === keymapItem;
            } else {
                // isMatch = isEqual(columnItem, keymapItem);
                isMatch = isFieldMatch(columnItem, keymapItem)
            }
            return isMatch;
        }

        /**
         * source中的元素 key_obj 类型：
         * if(sourceSrcType === 1, 2, 3) string
         * if( === 6) { index, type }
         */
        source.forEach((key_obj, ii) => {
            $dagL.each((dl, i) => {
                let sx, sy, ex, ey;

                if(matchKeymapToColumn_s(dl, key_obj)) {
                    sx = padding;
                    sy = (i + 1.5) * h;

                    $dagR.each((dr, j) => {
                        /**
                         * target[ii] 类型：
                         * if(targetSrcType === 1, 2, 3) string
                         * if( === 6)  obj{ key, type }
                         */
                        if(matchKeymapToColumn_t(dr, target[ii])) {
                            ex = W - w*2 - padding;
                            ey = (j + 1.5) * h;

                            posArr.push({
                                s: {x: sx, y: sy},
                                e: {x: ex, y: ey},
                                dl: key_obj,
                                dr: target[ii]
                            });
                        }
                    });
                }
            })
        });

        const mapline = this.$canvas.append('g').attr('class', 'lines')
            .selectAll('g')
            .data(posArr)
            .enter()
            .append('g')
            .attr('class', 'mapline');

        mapline.append('line')
            .attr("x1", d => d.s.x)
            .attr("y1", d => d.s.y)
            .attr("x2", d => d.e.x)
            .attr("y2", d => d.e.y)
            .attr("stroke","#2491F7")
            .attr("stroke-width", 2)
            .attr("marker-end","url(#arrow)");
    }

    bindEvents() {
        const { w, h, W, padding } = this.state;
        const { 
            addLinkedKeys, delLinkedKeys, 
            sourceSrcType, sourceFileType, 
            targetSrcType, targetFileType 
        } = this.props;

        const $line = this.$activeLine;
        const $dagL = selectAll('.col-dag-l');

        let isMouseDown = false;
        let sourceKey_obj, targetKey_obj;

        $dagL.on('mousedown', (d, i, nodes) => {
            let sx = padding, sy = (i + 1.5) * h;
            $line.attr('x1', sx)
                .attr('y1', sy)
                .attr('x2', sx)
                .attr('y2', sy);

            sourceKey_obj = d;
            isMouseDown = true;
        });

        this.$canvas.on('mousemove', () => {
            if(isMouseDown) {
                const xy = mouse(this.$canvas.node());
                $line.attr('x2', xy[0]).attr('y2', xy[1]);
            }
        }).on('mouseup', () => {
            if(isMouseDown) {
                const xy = mouse(this.$canvas.node());
                const [ex, ey] = xy;
                const threholdX = W - w*2 - padding;

                if(ex < threholdX) this.resetActiveLine();
                else {
                    const tidx = Math.floor(ey / h) - 1;
                    const $dagR = selectAll('.col-dag-r');

                    $dagR.each((d, i) => {
                        if(i === tidx) {
                            targetKey_obj = d;
                        }
                    });
                }
            }

            if(sourceKey_obj && targetKey_obj) {
                addLinkedKeys({
                    source: isRDB(sourceSrcType) ?
                        sourceKey_obj.key : sourceKey_obj,
                    target: isRDB(targetSrcType) ?
                        targetKey_obj.key : targetKey_obj
                });
                this.resetActiveLine();
            }

            isMouseDown = false;
        });

        this.$canvas.selectAll('.mapline')
            .on('mouseover', (d, i, nodes) => {
                select(nodes[i])
                    .select('line')
                    .attr('stroke-width', 3)
                    .attr('stroke', '#2491F7')
            })
            .on('mouseout', (d, i, nodes) => {
                select(nodes[i])
                    .select('line')
                    .attr('stroke-width', 2)
                    .attr('stroke', '#2491F7')
            })
            .on('click', (d, i, nodes) => {
                delLinkedKeys({
                    source: d.dl,
                    target: d.dr,
                    sourceSrcType, sourceFileType, targetSrcType, targetFileType
                });
            });
    }

    resetActiveLine() {
        this.$activeLine.attr('x1', -10)
            .attr('y1', -10)
            .attr('x2', -10)
            .attr('y2', -10);
    }
    
    initData = () => {
        const { sourceMap, sourceSrcType, addSourceKeyRow } = this.props;
        if (sourceSrcType === DATA_SOURCE.HBASE) {
            if (!sourceMap.column || sourceMap.column.length === 0) {
                addSourceKeyRow(DefaultRowKey)
            }
        }
    }

    loadColumnFamily = () => {
        const {
            sourceMap, targetMap,
            sourceSrcType, targetSrcType,
        } = this.props;
        const ctx = this

        const getColumnFamily = (params, succCall) => {
            Api.getHBaseColumnFamily(params).then(res => {
                if (res.code === 1) {
                    succCall(res.data)
                }
            })
        }
        if (sourceSrcType === DATA_SOURCE.HBASE) {
            getColumnFamily({
                sourceId: sourceMap.sourceId,
                tableName: sourceMap.type.table,
            }, (data) => {
                ctx.setState({
                    sourceColumnFamily: data,
                })
            })
        }

        if (targetSrcType === DATA_SOURCE.HBASE) {
            getColumnFamily({
                sourceId: targetMap.sourceId,
                tableName: targetMap.type.table,
            }, (data) => {
                ctx.setState({
                    targetColumnFamily: data,
                })
            })
        }
    }

    hbaseRowKeyChange = (e) => {
        const value = utils.trim(e.target.value)
        const { handleTargetMapChange, targetMap } = this.props
        if (value) {
            targetMap.rowkey = value
            handleTargetMapChange(targetMap)
        }
    }

    renderSource() {
        const { w, h, W, H, padding } = this.state;

        const { 
            targetCol, sourceCol, keymap,
            sourceSrcType, sourceFileType,
            removeSourceKeyRow, readonly,
        } = this.props;

        const colStyle = { left: padding, top: padding, width: w, height: h, }

        const renderTableRow = (sourceType, col, i) => {

            const removeOperation = <div className="remove-cell"
                onClick={() => removeSourceKeyRow(col, i)}>
                <Tooltip title="删除当前列">
                    <Icon type="minus" />
                </Tooltip>
            </div>
            switch(sourceType) {
                case DATA_SOURCE.HDFS: {
                    const name = col ? scrollText(col.index !== undefined ? col.index : `'${col.key}'`) : '索引位' 
                    return <div>
                        <div className="cell" title={name}>{name}</div>
                        <div className="cell">{col ? (col.value ? '常量' : col.type.toUpperCase()) : '类型'}</div>
                        { 
                            sourceFileType !== 'orc' ? <div className="cell">
                            { col ? removeOperation : '操作' }
                            </div> : '' 
                        }
                    </div>
                }
                case DATA_SOURCE.HBASE: {
                    const name = col ? scrollText(col.key) : '列名/行健'
                    const cf = col ? col.cf : '列族'
                    return <div className="four-cells">
                        <div className="cell" title={cf}>{ cf }</div>
                        <div className="cell" title={name}>{ name }</div>
                        <div className="cell">{ col ? col.type.toUpperCase() : '类型' }</div>
                        <div className="cell">
                            { 
                                col ? <div>  
                                    {col.key === 'rowkey' ? '' : removeOperation }
                                    <div className="edit-cell" onClick={
                                            () => { this.initEditKeyRow(true, sourceType, null, col, i) }
                                        }>
                                        <Tooltip title="编辑当前列">
                                            <Icon type="edit" />
                                        </Tooltip>
                                    </div>
                                </div> : '操作'
                            }   
                        </div>
                    </div>
                }
                case DATA_SOURCE.HIVE: {
                    const name = col ? scrollText(col.value ? `'${col.key}'` : col.key) : '字段名称'
                    return <div>
                        <div className="cell">{name}</div>
                        <div className="cell">
                            {col ? (col.value ? '常量' : col.type.toUpperCase()) : '类型'}
                        </div>
                        <div className="cell">
                            {col ? (col.value ? removeOperation : '' ) : '操作'}
                        </div>
                    </div>
                }
                case DATA_SOURCE.FTP: {
                    const name = col ? scrollText(col.index !== undefined ? col.index : `'${col.key}'`) : '字段序号' 
                    return <div>
                        <div className="cell" title={name}>{name}</div>
                        <div className="cell">{col ? (col.value ? '常量' : col.type.toUpperCase()) : '类型'}</div>
                        <div className="cell">
                            {col ? removeOperation : '操作'}
                        </div>
                    </div>
                }
                default: {
                    return <div>
                        <div className="cell" title={name}>
                            {
                                col ? scrollText(col.key) : '字段名称'
                            }
                        </div>
                        <div className="cell">
                            {
                                col ? col.type.toUpperCase() : '类型'
                            }
                        </div>
                    </div>
                }
            }
        }

        const renderTableFooter = (sourceType) => {
            if (!readonly) {
                let footerContent = ''
                const btnAddConst = <span onClick={
                        () => {this.setState({ visibleConst: true })
                    }}>
                        +添加常量
                    </span>
                switch(sourceType) {
                    case DATA_SOURCE.HIVE:
                        footerContent = <div>
                            { btnAddConst }
                        </div>
                        break;
                    case DATA_SOURCE.HBASE:
                        footerContent = <div>
                                <span onClick={this.initAddKeyRow.bind(this, true, sourceFileType)}>
                                    +添加字段
                                </span>
                                &nbsp;
                                <span onClick={this.importSourceFields}>
                                    批量添加
                                </span> 
                            </div>;
                        break;
                    case DATA_SOURCE.HDFS: {
                        footerContent = sourceFileType !== 'orc' ? 
                        <div>
                            <span onClick={ this.initAddKeyRow.bind(this, true, sourceFileType) }>
                                +添加字段
                            </span>
                            &nbsp;
                            { btnAddConst }
                            &nbsp;
                            <span onClick={this.importSourceFields}>
                                批量添加
                            </span>
                        </div> : null;
                        break;
                    }
                    case DATA_SOURCE.FTP: {
                        footerContent = <div>
                            <span onClick={ this.initAddKeyRow.bind(this, true, sourceFileType) }>
                                +添加字段
                            </span>
                            &nbsp;
                            { btnAddConst }
                            &nbsp;
                            <span onClick={this.importSourceFields}>
                                批量添加
                            </span>
                        </div>;
                        break;
                    }
                    default: {
                        footerContent = null; break;
                    }
                }
                return (
                    <div className="m-col footer pa" 
                        style={{
                            left: padding,
                            top: padding + (h * (sourceCol.length + 1)),
                            width: w, height: h,
                            zIndex: 100,
                        }}
                    >
                        { footerContent }
                    </div>
                )
            }
            return ''
        }

        return (
            <div className="sourceLeft">
                <div className="m-col title pa" 
                    style={ colStyle }>
                    { renderTableRow(sourceSrcType) }
                </div>
                { sourceCol.map((col, i) => {
                    return <div
                        style={{
                            width: w, height: h, 
                            left: padding, 
                            top: padding + (h * (i + 1)),
                        }} 
                        className="m-col pa" key={ `sourceLeft-${i}`} >
                        { renderTableRow(sourceSrcType, col, i) }
                    </div>
                }) }
                { renderTableFooter(sourceSrcType) }
            </div>
        )
    }

    renderTarget() {
        const { w, h, W, H, padding } = this.state;
        const { 
            targetCol, sourceCol, keymap, readonly, targetMap,
            targetFileType, removeTargetKeyRow,
            sourceSrcType, targetSrcType, 
        } = this.props;

        const colStyle = {
            left: W - (padding + w),
            top: padding,
            width: w,
            height: h
        }

        const renderTableRow = (targetType, col, i) => {
            const operations = <div>
                <div className="remove-cell" onClick={() => removeTargetKeyRow(col, i)}>
                    <Tooltip title="删除当前列">
                        <Icon type="minus" />
                    </Tooltip>
                </div>
                <div className="edit-cell" onClick={
                        () => { this.initEditKeyRow(false, targetFileType, sourceCol[i], col, i) }
                    }>
                    <Tooltip title="编辑当前列">
                        <Icon type="edit" />
                    </Tooltip>
                </div>
            </div>
            switch(targetType) {
                case DATA_SOURCE.HDFS: {
                    return <div>
                        <div className="cell">{col ? scrollText(col.key) : '字段名称' }</div>
                        <div className="cell">{col ? col.type.toUpperCase() : '类型' }</div>
                        <div className="cell">
                            {   col ? operations : '操作' }
                        </div>
                    </div>
                }
                case DATA_SOURCE.HBASE: {
                    return <div className="four-cells">
                        <div className="cell">{col ? col.cf : '列族' }</div>
                        <div className="cell">{col ? scrollText(col.key) : '列名' }</div>
                        <div className="cell">{col ? col.type.toUpperCase() : '类型' }</div>
                        <div className="cell">{ col ? operations : '操作' }</div>
                    </div>
                }
                case DATA_SOURCE.FTP: {
                    return <div>
                        <div className="cell">{col ? scrollText(col.key) : '字段名称' }</div>
                        <div className="cell">{col ? col.type.toUpperCase() : '类型' }</div>
                        <div className="cell">
                            {   col ? operations : '操作' }
                        </div>
                    </div> 
                }
                default: {
                    return <div>
                        <div className="cell">{col ? scrollText(col.key) : '字段名称'}</div>
                        <div className="cell">{col ? col.type.toUpperCase() : '类型'}</div>
                    </div>
                }
            }
        }

        const renderTableFooter = (targetType) => {
            if (!readonly) {
                let footerContent = ''
                switch(targetType) {
                    case DATA_SOURCE.HBASE:
                        footerContent = <div>
                            <span onClick={this.initAddKeyRow.bind(this, false, targetFileType)}>
                                +添加字段
                            </span>
                            &nbsp;
                            <span onClick={this.importFields}>
                                批量添加
                            </span>
                            <div className="m-col">
                                rowkey: <Input 
                                        style={{ width: '160px' }}
                                        defaultValue={(targetMap.type && targetMap.type.rowkey) || ''}
                                        placeholder={
                                            sourceSrcType === DATA_SOURCE.HBASE ? 
                                            '$(colFamily1:colName1)' : '$(colName)'
                                        }
                                        onChange={this.hbaseRowKeyChange}/>
                            </div>
                        </div>;
                        break;
                    case DATA_SOURCE.HDFS: {
                        footerContent = <div>
                            <span onClick={ this.initAddKeyRow.bind(this, false, targetFileType) }>
                                +添加字段
                            </span>
                            &nbsp;
                            <span onClick={this.importFields}>
                                批量添加
                            </span>
                        </div>;
                        break;
                    }
                    case DATA_SOURCE.FTP: {
                        footerContent = <div>
                            <span onClick={ this.initAddKeyRow.bind(this, false, targetFileType) }>
                                +添加字段
                            </span>
                            &nbsp;
                            <span onClick={this.importFields}>
                                批量添加
                            </span>
                        </div>;
                        break;
                    }
                    default: {
                        footerContent = null; break;
                    }
                }
                return footerContent ? <div className="m-col footer pa" style={{
                        top: padding + (h * (targetCol.length + 1)),
                        left: W - (padding + w),
                        width: w, height: h,
                        zIndex: 100,
                    }}>
                        {footerContent}
                    </div> : ''
            }
            return ''
        }

        return <div className="targetRight">
            <div className="m-col title  pa" style={colStyle}>
               { renderTableRow(targetSrcType) }
            </div>
            { targetCol.map((col, i) => {
                return <div key={ `targetRight-${i}` } className="m-col pa"
                    style={{
                        width: w, height: h,
                        top: padding + (h * (i + 1)),
                        left: W - (padding + w)
                    }}>
                    { renderTableRow(targetSrcType, col, i) }
                </div>
            }) }
            { renderTableFooter(targetSrcType) }
        </div>
    }

    renderKeyModal = () => {

        const { 
            keyModal, keyModalVisible, 
            sourceColumnFamily, targetColumnFamily
        } = this.state;

        const { operation, isReader } = keyModal;

        const { 
            sourceSrcType, targetSrcType, 
        } = this.props;

        const dataType = keyModal.isReader ? sourceSrcType : targetSrcType;
        
        let onOk = this.doAddkeyRow;
        let title = '添加HDFS字段';
        if (operation === 'add') {
            if (dataType === DATA_SOURCE.HBASE) {
                title = '添加HBase字段';
            } else if(dataType === DATA_SOURCE.FTP) {
                title = '添加FTP字段';
            }
        } else if (operation === 'edit') {
            onOk = this.doEditKeyRow;
            title = '修改HDFS字段';
            if (dataType === DATA_SOURCE.HBASE) {
                title = '修改HBase字段';
            } else if(dataType === DATA_SOURCE.FTP) {
                title = '添加FTP字段';
            }
        }

        return <KeyMapModal
            title={ title }
            visible={ keyModalVisible }
            keyModal={ keyModal }
            dataType={ dataType }
            sourceColumnFamily={sourceColumnFamily}
            targetColumnFamily={targetColumnFamily}
            onOk={ onOk }
            onCancel={ this.hideKeyModal }
            key={ isReader ? `source-${dataType}-${sourceSrcType}` : `target-${dataType}-${targetSrcType}` }
        />
    }

    renderBatchModal = () => {
        const {
            batchModal, batchSourceModal, 
            sourceColumnFamily, targetColumnFamily
        } = this.state;
        const {
            sourceSrcType, targetSrcType,
        } = this.props;

        let sPlaceholder, sDesc, tPlaceholder, tDesc
        switch (sourceSrcType) {
            case DATA_SOURCE.FTP:
            case DATA_SOURCE.HDFS: {
                sPlaceholder = '0: STRING, 1: INTEGER,...'
                sDesc = 'index: type, index: type'
                break;
            }
            case DATA_SOURCE.HBASE: {
                sPlaceholder = 'cf1: field1: STRING, cf1: field2: INTEGER,...'
                sDesc = 'columnFamily: fieldName: type,'
                break;
            }
        }

        switch (targetSrcType) {
            case DATA_SOURCE.FTP:
            case DATA_SOURCE.HDFS: {
                tPlaceholder = 'field1: STRING, field2: INTEGER,...'
                tDesc = 'fieldName: type, fieldName: type'
                break;
            }
            case DATA_SOURCE.HBASE: {
                tPlaceholder = sPlaceholder
                tDesc = sDesc
                break;
            }
        }

        return [
            <BatchModal
                key="sourceBatchAdd"
                title="批量添加源表字段"
                desc={sDesc}
                columnFamily={sourceColumnFamily}
                placeholder={sPlaceholder}
                visible={batchSourceModal.visible}
                value={batchSourceModal.batchText}
                onOk={this.doBatchAddSourceFields}
                onCancel={this.hideBatchSourceModal}
                onChange={this.batchSourceTextChange}
            />,
            <BatchModal
                key="targetBatchAdd"
                title="批量添加目标字段"
                desc={tDesc}
                columnFamily={targetColumnFamily}
                placeholder={tPlaceholder}
                visible={batchModal.visible}
                value={batchModal.batchText}
                onOk={this.doBatchImport}
                onCancel={this.hideBatchImportModal}
                onChange={this.batchTextChange}
            />
        ]
    }

    render() {
        const {
            w, h, W, padding, visibleConst,
        } = this.state;

        const {
            targetCol, sourceCol, keymap, 
            sourceSrcType, sourceFileType, 
            navtoStep, targetSrcType, 
            addSourceKeyRow,
        } = this.props;

        const H = h * (Math.max(targetCol.length, sourceCol.length) + 1);
        const { source, target } = keymap;

        return <div>
            <p style={{ fontSize: 12, color: '#ccc', marginTop: -20 }}>
                您要配置来源表与目标表的字段映射关系，通过连线将待同步的字段左右相连，也可以通过同行映射、同名映射批量完成映射
            </p>
            <Row>
                <Col span="20">
                    <div className="m-keymapbox"
                        ref={ el => this.container = el }
                        style={{
                            width: W,
                            minHeight: H + 20
                        }}
                    >
                        { this.renderSource() }
                        { this.renderTarget() }
                        <svg
                            ref={ el => this.canvas = el }
                            width={ W > w*2 ? W - w*2 : 0 }
                            height={ H }
                            className="pa m-keymapcanvas"
                            style={{ left: w , top: padding }}
                        >
                            <defs>
                                <marker id="arrow" markerUnits="strokeWidth" markerWidth="12" markerHeight="12" viewBox="0 0 12 12" refX="6" refY="6" orient="auto" >
                                    <path d="M2,3 L9,6 L2,9 L2,6 L2,3" style={{ fill: '#2491F7' }}></path>
                                </marker>
                            </defs>
                            <g>
                                <line id="activeLine"
                                    x1="-10" y1="-10" x2="-10" y2="-10"
                                    stroke="#2491F7"
                                    strokeWidth="2"
                                    markerEnd="url(#arrow)"
                                />
                            </g>
                        </svg>
                    </div>
                </Col>
                <Col span="4">
                    {!this.props.readonly ? <div className="m-buttons">
                        <Button
                            type={ this.state.rowMap ? 'primary' : 'default' }
                            onClick={ this.setRowMap.bind(this) }
                        >{ this.state.rowMap ? '取消同行映射' : '同行映射'}</Button>
                        <br/>
                        <Button
                            disabled={ isHdfsType(sourceSrcType)}
                            type={ this.state.nameMap ? 'primary' : 'default' }
                            onClick={ this.setNameMap.bind(this) }
                        >{ this.state.nameMap ? '取消同名映射' : '同名映射' }
                        </Button>
                        <br/>
                        {
                            isHdfsType(targetSrcType) ? 
                            <Button 
                                onClick={() => {this.copySourceCols(sourceCol)}}>
                                拷贝源字段
                            </Button>
                            : ''
                        }
                    </div> : null
                    }
                </Col>
            </Row>
            { this.renderKeyModal() }
            { this.renderBatchModal() }
            <ConstModal
                visible={visibleConst}
                onOk={addSourceKeyRow}
                onCancel={() => { this.setState({ visibleConst: false })}}
            />
            {!this.props.readonly && <div className="steps-action" style={{ marginTop: 80 }}>
                <Button style={{ marginRight: 8 }} onClick={() => this.prev(navtoStep)}>上一步</Button>
                <Button type="primary" onClick={() => this.next(navtoStep)}>下一步</Button>
            </div>}
        </div>
    }

    prev(cb) {
        cb.call(null, 1);
    }

    next(cb) {
        const { keymap, targetSrcType } = this.props;
        const { source, target } = keymap;

        if(source.length === 0 && target.length === 0) {
            message.error('尚未配置数据同步的字段映射！');
            return;
        }

        // 针对Hbase增加keyrow检验项
        if (targetSrcType === DATA_SOURCE.HBASE) {
            if (!this.checkHBaseRowKey()) {
                return;
            }
        }

        cb.call(null, 3);
    }

    checkHBaseRowKey = (e) => {
        const { keymap, targetMap, sourceSrcType } = this.props
        const { type } = targetMap
        const source = keymap.source

        if (type && type.rowkey) {
            const arr = []
            if (sourceSrcType === DATA_SOURCE.HBASE) {
                let regx = /([\w]+:[\w]+)/g
                const arr = type.rowkey.match(regx)
                for (let i = 0; i < arr.length; i++) {
                    const val = arr[i].split(':')
                    if (val && val.length === 2) {
                        const res = source.find(item => item.cf === val[0] && item.key === val[1])
                        if (!res) {
                            message.error('目标表rowkey在源表中并不存在！')
                            return false;
                        }
                    }
                }
            } else {
                // 正则提取
                let regx = /\$\(([\w]+)\)/g, temp;
                while ((temp = regx.exec(type.rowkey)) !== null) {
                    arr.push(temp[1])
                }
                // 验证字段
                for (let i = 0; i < arr.length; i++) {
                    let res = ''
                    if (isObject(source[0])) {
                        res = source.find(item => {
                            const name = item.key !== undefined ? item.key : item.index
                            return `${name}` === `${arr[i]}` ? item : undefined
                        })
                    } else {
                        res = source.find(item => item === arr[i])
                    }
                    if (!res) {
                        message.error(`rowkey在已映射的字段中并未找到${arr[i]}！`)
                        return false;
                    }
                }
            }
            return true;
        } else if (!type.rowkey) {
            message.error('目标表rowkey不能为空！')
            return false
        }
    }

    copySourceCols = (sourceKeyRow) => {
        const { addBatchTargetKeyRow } = this.props
        const params = sourceKeyRow && sourceKeyRow.map(item => {
            return {
                key: item.key || item.index,
                type: item.type,
            }
        })
        addBatchTargetKeyRow(params)
    }

    batchTextChange = (e) => {
        const { batchModal } = this.state
        this.setState({
            batchModal: {
                visible: true,
                batchText: e.target.value,
            }
        })
    }

    batchSourceTextChange = (e) => {
        const { batchSourceModal } = this.state
        this.setState({
            batchSourceModal: {
                visible: true,
                batchText: e.target.value,
            }
        })
    }

    doBatchImport = () => {
        const { batchModal } = this.state
        const { batchText } = batchModal
        const { addBatchTargetKeyRow, targetSrcType } = this.props

        const str = utils.trim(batchText)
        const arr = str.split(',')

        const params = []

        switch (targetSrcType) {
            case DATA_SOURCE.FTP:
            case DATA_SOURCE.HDFS: {
                for (let i = 0; i < arr.length; i++ ) {
                    const item = arr[i]
                    if (!item) continue;
                    const map = item.split(':')
                    const key = map[0]
                    const type = map[1]
                    if (hdfsFieldTypes.includes(type)) {
                        params.push({
                            key,
                            type,
                        })
                    } else {
                        message.error(`字段${key}的数据类型错误！`)
                        return
                    }
                }
                break;
            }
            case DATA_SOURCE.HBASE: {
                for (let i = 0; i < arr.length; i++) {
                    const item = arr[i]
                    if (!item) continue;
                    const map = item.split(':')
                    const cf = utils.trim(map[0])
                    const name = utils.trim(map[1])
                    const type = utils.trim(map[2])
                    if (hdfsFieldTypes.includes(type)) {
                        params.push({
                            cf: cf,
                            key: name,
                            type,
                        })
                    } else {
                        message.error(`字段${name}的数据类型错误！`)
                        return
                    }
                }
                break;
            }
        }
        addBatchTargetKeyRow(params)
        this.hideBatchImportModal()
    }

    hideBatchImportModal = () => {
        this.setState({
            batchModal: {
                visible: false,
                batchText: '',
            },
        });
    }

    hideBatchSourceModal = () => {
        this.setState({
            batchSourceModal: {
                visible: false,
                batchText: '',
            },
        });
    }

    // 批量导入字段
    importFields = () => {
        this.setState({
            batchModal: {
                visible: true,
            }
        })
    }

    importSourceFields = () => {
        this.setState({
            batchSourceModal: {
                visible: true,
            }
        })
    }

    doBatchAddSourceFields = () => {
        const { batchSourceModal } = this.state
        const { batchText } = batchSourceModal
        const { addBatchSourceKeyRow, sourceSrcType, sourceCol } = this.props

        const str = utils.trim(batchText)
        const arr = batchText.split(',')

        const params = []

        switch (sourceSrcType) {
            case DATA_SOURCE.FTP:
            case DATA_SOURCE.HDFS: {
                for (let i = 0; i < arr.length; i++ ) {
                    const item = arr[i]
                    if (!item) continue;
                    const map = item.split(':')
                    const index = parseInt(utils.trim(map[0]), 10)
                    const type = utils.trim(map[1])
                    if (!isNaN(index) && isNumber(index)) {
                        if (hdfsFieldTypes.includes(type) ) {
                            if (!params.find(pa => pa.index === index )) {
                                params.push({
                                    index: index,
                                    type,
                                })
                            }
                        } else {
                            message.error(`字段${index}的数据类型错误！`)
                            return
                        }
                    } else {
                        message.error(`索引名称应该为整数数字！`)
                        return
                    }
                }
                break;
            }
            case DATA_SOURCE.HBASE: {

                params.push(DefaultRowKey) // 默认插入行健

                for (let i = 0; i < arr.length; i++) {
                    const item = arr[i]
                    if (!item) continue;
                    const map = item.split(':')

                    const cf = utils.trim(map[0])
                    const name = utils.trim(map[1])
                    const type = utils.trim(map[2])
                    if (hdfsFieldTypes.includes(type)) {
                        params.push({
                            cf: cf,
                            key: name,
                            type,
                        })
                    } else {
                        message.error(`字段${name}的数据类型错误！`)
                        return
                    }
                }
                break;
            }
        }
        addBatchSourceKeyRow(params)
        this.hideBatchSourceModal()
    }

    /**
     * @description 同行映射
     * @memberof Keymap
     */
    setRowMap() {
        const { rowMap, nameMap } = this.state;
        const { 
            targetCol, sourceCol, sourceSrcType, 
            sourceFileType, targetSrcType, targetFileType, 
        } = this.props;

        const convertColumn2Keymap_s = (column) => {
            if (isRDB(sourceSrcType)) {
                column = column.map(o => o.key);
            }
            return column;
        }

        const convertColumn2Keymap_t = (column) => {
            if (isRDB(targetSrcType)) {
                column = column.map(o => o.key);
            }
            return column;
        }

        if(!rowMap) {
            this.props.setRowMap({
                sourceCol: convertColumn2Keymap_s(sourceCol),
                targetCol: convertColumn2Keymap_t(targetCol)
            });
        }
        else{
            this.props.resetLinkedKeys();
        }

        this.setState({
            rowMap: !rowMap,
            nameMap: nameMap ? !nameMap : nameMap
        });
    }

    /**
     * @description 同名映射
     * @memberof Keymap
     */
    setNameMap() {
        const { nameMap, rowMap } = this.state;
        const { targetCol, sourceCol, targetSrcType } = this.props;

        if(!nameMap) {
            this.props.setNameMap({sourceCol, targetCol, targetSrcType});
        }
        else{
            this.props.resetLinkedKeys();
        }

        this.setState({
            nameMap: !nameMap,
            rowMap: rowMap ? !rowMap : rowMap
        });
    }

    /**
     * @description HDFS数据源类型时添加一行字段
     * @param {boolean} isReader 区分来源数据与目标数据
     * @param {string} hdfs 文件类型 orc/text
     * @memberof Keymap
     */
    initAddKeyRow(isReader, fileType) {
        this.setState({
            keyModalVisible: true,
            keyModal: {
                operation: 'add',
                isReader,
                fileType
            }
        });
    }

    initEditKeyRow = (isReader, fileType, sourceCol, field, index) => {
        this.setState({
            keyModalVisible: true,
            keyModal: {
                operation: 'edit',
                isReader,
                fileType,
                editField: field,
                position: index,
                source: sourceCol,
            }
        });
    }

    /**
     * @description dispatch action
     * @param {boolean} isReader 添加到srouce/target
     * @memberof Keymap
     */
    doAddkeyRow = (formData) => {
        const { isReader } = this.state.keyModal
        const { addSourceKeyRow, addTargetKeyRow } = this.props;
        if (formData) {
            if(isReader) {
                addSourceKeyRow(formData);
            }
            else {
                addTargetKeyRow(formData);
            }
            this.hideKeyModal();
        }
    }

    /**
     * 编辑KeyRow
     * @param {*} isReader 
     */
    doEditKeyRow = (formData) => {
        const {
            editSourceKeyRow,
            editTargetKeyRow,
            editKeyMapTarget,
        } = this.props;
        const { keyModal } = this.state;
        const { isReader, position, source, editField } = keyModal;
        if (formData) {

            if(isReader) {
                editSourceKeyRow(formData);
            }
            else {
                editTargetKeyRow({
                    index: position,
                    value: formData,
                });
                editKeyMapTarget({
                    old: editField,
                    replace: formData,
                })
            }
            this.hideKeyModal();
        }
    }

    hideKeyModal = () => {
        this.setState({
            keyModalVisible: false,
            keyModal: {
                operation: 'add',
                editField: '',
                source: '',
            },
        });
    }

}

const mapState = state => {
    const { dataSync } = state.offlineTask;
    return {
        sourceMap: dataSync.sourceMap,
        targetMap: dataSync.targetMap,
        targetCol: dataSync.targetMap.column || [],
        targetSrcType: dataSync.targetMap.type && dataSync.targetMap.type.type,  // 目标数据源类型
        targetFileType: dataSync.sourceMap.type && dataSync.targetMap.type.fileType,
        sourceCol: dataSync.sourceMap.column || [],
        sourceSrcType: dataSync.sourceMap.type && dataSync.sourceMap.type.type, // 源头数据源类型
        sourceFileType: dataSync.sourceMap.type && dataSync.sourceMap.type.fileType,
        keymap: dataSync.keymap,
    };
};

export default connect(mapState, keyMapActions)(Keymap);