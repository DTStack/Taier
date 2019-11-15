import * as React from 'react';
import { connect } from 'react-redux';
import { isNumber, isObject, isNaN, get } from 'lodash'
import {
    Button, Row, Col,
    Input, Tooltip,
    message, Icon, Modal
} from 'antd';
import { select, selectAll, mouse } from 'd3-selection';
import scrollText from 'widgets/scrollText';
import Resize from 'widgets/resize';
import {
    debounceEventHander
} from 'funcs'

import {
    keyMapActions
} from '../../../../../store/modules/offlineTask/offlineAction';
import utils from 'utils';
import Api from '../../../../../api';

import {
    hdfsFieldTypes,
    hbaseFieldTypes,
    DATA_SOURCE
} from '../../../../../comm/const';

import { isHdfsType, isFtpType } from '../../../../../comm';

import KeyMapModal, { isValidFormatType } from './keymapModal'
import BatchModal from './batchModal'
import ConstModal from './constModal'
const DefaultRowKey: any = { // HBase默认行健
    cf: '-',
    key: 'rowkey',
    type: 'STRING'
};

/**
 * 判断字段是否匹配
 * @param {*} source
 * @param {*} target
 */
function isFieldMatch (source: any, target: any) {
    /**
     * TODO 目前从接口返回的keymap字段与sourceMap, targetMap中不一致
     */
    if (isObject(source as any) && isObject(target as any)) {
        const sourceVal = source.key || source.index;
        const tagetVal = target.key || target.index;
        return sourceVal === tagetVal;
    } else if (isObject(source as any) && !isObject(target as any)) {
        const sourceVal = source.key || source.index
        return sourceVal === target
    } else if (!isObject(source as any) && isObject(target as any)) {
        const targetVal = target.key || target.index
        return source === targetVal
    } else {
        return source === target
    }
}
const rowkeyTxt =
`变量写法：
$(colFamily:colName)：列族、列名
支持md5函数，使用时必须以md5开头
支持多字段拼接，例如：md5($(cf1:userName)_$(cf:userAge))
`;
class Keymap extends React.Component<any, any> {
    constructor (props: any) {
        super(props);

        this.state = {
            h: 40, // 字段一行高度
            w: 230, // 字段的宽度
            W: 450, // step容器大小
            padding: 10, // 绘制拖拽点左右边距
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
            }, // hdfs添加行modal
            batchModal: { // 批量导入目标字段
                visible: false,
                batchText: ''
            },
            batchSourceModal: { // 批量导入源表字段
                visible: false,
                batchText: ''
            },
            sourceColumnFamily: '', // 源表列族
            targetColumnFamily: '', // 目标列族
            isFocus: false
            // isSuccess: true // 用于判定rowkey是否符合期望格式
        };
    }
    $canvas: any;
    canvas: any;
    $activeLine: any;
    container: any;
    /**
     * 获取step容器的大小，最小为450，其他情况为panel大小的5/6;
     */
    getCanvasW () {
        let w = 450;
        const canvas = document.querySelector('.steps-content')
        if (canvas) {
            const newW = canvas.getBoundingClientRect().width / 6 * 4;
            if (newW > w) w = newW;
        }
        return w;
    }

    componentDidMount () {
        // const { targetMap } = this.props;
        // this.setState({
        //     isSuccess: this.isOkRowkey(targetMap.type.rowkey)
        // });
        /**
         * step容器
         */
        this.$canvas = select(this.canvas);
        /**
         * 拖动的线
         */
        this.$activeLine = select('#activeLine');
        /**
         * 设置step容器大小
         */
        this.setState({
            W: this.getCanvasW()
        })
        /**
         * 开始画
         */
        this.drawSvg();
        this.loadColumnFamily();
        this.initData();
    }

    componentDidUpdate () {
        this.$canvas.selectAll('.dl, .dr, .lines').remove();
        this.drawSvg();
    }

    resize = () => {
        this.setState({
            W: this.getCanvasW()
        })
    }

    drawSvg () {
        this.renderDags();
        this.renderLines();
        this.bindEvents();
    }

    /**
     * 绘制字段旁边的拖拽点
     */
    renderDags () {
        const { w, h, W, padding } = this.state;
        const { targetCol, sourceCol } = this.props;

        this.$canvas.append('g')
            .attr('class', 'dl')
            .selectAll('g')
            .data(sourceCol)
            .enter()
            .append('g')
            .attr('class', 'col-dag-l')
            .append('circle')
            .attr('class', 'dag-circle')
            .attr('cx', (d: any, i: any) => padding)
            .attr('cy', (d: any, i: any) => h * (i + 1.5))
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
            /**
             * W-w*2代表绘制区域的宽度
             */
            .attr('cx', (d: any, i: any) => { return (W - w * 2 - padding) })
            .attr('cy', (d: any, i: any) => h * (i + 1.5))
            .attr('r', 5)
            .attr('stroke-width', 2)
            .attr('stroke', '#fff')
            .attr('fill', '#2491F7');
    }

    renderLines () {
        const { w, h, W, padding } = this.state;
        const {
            keymap
        } = this.props;
        const { source, target } = keymap;
        const $dagL = selectAll('.col-dag-l');
        const $dagR = selectAll('.col-dag-r');
        const posArr: any = [];

        /**
         * 左侧source中连接起点对象keymap.source[i] 匹配到 所有字段对象中的一个 sourceMap.column[i]
         * @param {*} columnItem
         * @param {*} keymapItem
         * @return {boolean} isMatch
         */
        const matchKeymapToSourceColumn = (columnItem: any, keymapItem: any) => {
            return isFieldMatch(columnItem, keymapItem);
        };

        const matchKeymapToTargetColumn = (columnItem: any, keymapItem: any) => {
            return isFieldMatch(columnItem, keymapItem);
        }

        /**
         * source中的元素 keyObj 类型：
         * if(sourceSrcType === 1, 2, 3) string
         * if( === 6) { index, type }
         */
        source.forEach((keyObj: any, ii: any) => {
            $dagL.each((dl: any, i: any) => {
                let sx: any, sy: any, ex: any, ey: any;

                if (matchKeymapToSourceColumn(dl, keyObj)) {
                    sx = padding;
                    sy = (i + 1.5) * h;

                    $dagR.each((dr: any, j: any) => {
                        /**
                         * target[ii] 类型：
                         * if(targetSrcType === 1, 2, 3) string
                         * if( === 6)  obj{ key, type }
                         */
                        if (matchKeymapToTargetColumn(dr, target[ii])) {
                            ex = W - w * 2 - padding;
                            ey = (j + 1.5) * h;

                            posArr.push({
                                s: { x: sx, y: sy },
                                e: { x: ex, y: ey },
                                dl: keyObj,
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
            .attr('x1', (d: any) => d.s.x)
            .attr('y1', (d: any) => d.s.y)
            .attr('x2', (d: any) => d.e.x)
            .attr('y2', (d: any) => d.e.y)
            .attr('stroke', '#2491F7')
            .attr('stroke-width', 2)
            .attr('marker-end', 'url(#arrow)');
    }

    bindEvents () {
        const { w, h, W, padding } = this.state;
        const {
            addLinkedKeys, delLinkedKeys,
            sourceSrcType, sourceFileType,
            targetSrcType, targetFileType
        } = this.props;

        const $line = this.$activeLine;
        const $dagL = selectAll('.col-dag-l');

        let isMouseDown = false;
        /**
         *阿珍
         */
        let sourceKeyObj: any;
        /**
         * 阿强
         */
        let targetKeyObj: any;

        $dagL.on('mousedown', (d: any, i: any, nodes: any) => {
            let sx = padding; let sy = (i + 1.5) * h;
            $line.attr('x1', sx)
                .attr('y1', sy)
                .attr('x2', sx)
                .attr('y2', sy);

            sourceKeyObj = d;
            isMouseDown = true;
        });

        this.$canvas.on('mousemove', () => {
            if (isMouseDown) {
                const xy = mouse(this.$canvas.node());
                $line.attr('x2', xy[0]).attr('y2', xy[1]);
            }
        }).on('mouseup', () => {
            if (isMouseDown) {
                const xy = mouse(this.$canvas.node());
                const [ex, ey] = xy;
                const threholdX = W - w * 2 - padding;

                if (ex < threholdX) this.resetActiveLine();
                else {
                    const tidx = Math.floor(ey / h) - 1;
                    const $dagR = selectAll('.col-dag-r');

                    $dagR.each((d: any, i: any) => {
                        if (i === tidx) {
                            targetKeyObj = d;
                        }
                    });
                }
            }
            /**
             * 阿珍爱上了阿强
             */
            if (sourceKeyObj && targetKeyObj) {
                /**
                 * 存储连线
                 */
                addLinkedKeys({
                    source: sourceKeyObj,
                    target: targetKeyObj
                });
                this.resetActiveLine();
            }

            isMouseDown = false;
        });

        this.$canvas.selectAll('.mapline')
            .on('mouseover', (d: any, i: any, nodes: any) => {
                select(nodes[i])
                    .select('line')
                    .attr('stroke-width', 3)
                    .attr('stroke', '#2491F7')
            })
            .on('mouseout', (d: any, i: any, nodes: any) => {
                select(nodes[i])
                    .select('line')
                    .attr('stroke-width', 2)
                    .attr('stroke', '#2491F7')
            })
            .on('click', (d: any, i: any, nodes: any) => {
                delLinkedKeys({
                    source: d.dl,
                    target: d.dr,
                    sourceSrcType,
                    sourceFileType,
                    targetSrcType,
                    targetFileType
                });
            });
    }

    resetActiveLine () {
        this.$activeLine.attr('x1', -10)
            .attr('y1', -10)
            .attr('x2', -10)
            .attr('y2', -10);
    }

    initData = () => {
        const { sourceMap, sourceSrcType, addSourceKeyRow } = this.props;
        if (sourceSrcType === DATA_SOURCE.HBASE && sourceMap) {
            if (!sourceMap.column || sourceMap.column.length === 0) {
                addSourceKeyRow(DefaultRowKey)
            }
        }
    }

    loadColumnFamily = () => {
        const {
            sourceMap, targetMap,
            sourceSrcType, targetSrcType
        } = this.props;
        const ctx = this
        const getColumnFamily = (params: any, succCall: any) => {
            Api.getHBaseColumnFamily(params).then((res: any) => {
                if (res.code === 1) {
                    succCall(res.data)
                }
            })
        }
        if (sourceSrcType === DATA_SOURCE.HBASE && sourceMap) {
            getColumnFamily({
                sourceId: sourceMap.sourceId,
                tableName: sourceMap.type.table
            }, (data: any) => {
                ctx.setState({
                    sourceColumnFamily: data
                })
            })
        }

        if (targetSrcType === DATA_SOURCE.HBASE && targetMap) {
            getColumnFamily({
                sourceId: targetMap.sourceId,
                tableName: targetMap.type.table
            }, (data: any) => {
                ctx.setState({
                    targetColumnFamily: data
                })
            })
        }
    }

    debounceRowkeyChange = debounceEventHander(this.hbaseRowKeyChange.bind(this), 50, { 'maxWait': 2000 })

    hbaseRowKeyChange (e: any) {
        const targetVal = e.target.value || '';
        const value = utils.trim(targetVal);
        const { handleTargetMapChange } = this.props;
        if (value) {
            handleTargetMapChange({ rowkey: value });
            // this.setState({
            //     isSuccess: this.isOkRowkey(value)
            // });
        }
    }

    /* eslint-disable no-useless-escape */
    isOkRowkey = (val: any) => {
        const obase = '([\\w\\n]+\\.[\\w\\n]+)'
        const base = `\\$\\(${obase}\\)`;
        const regexBaseVar = `${base}(?:_${base})*`;
        const testRegExp = new RegExp(`^${regexBaseVar}$ | ^md5\\(${regexBaseVar}\\)$`, 'ig');
        return testRegExp.test(val);
    }

    handleBlurRowkeyCheck = (e: any) => {
        this.setState({
            isFocus: false
        });
    }

    renderSource () {
        const { w, h, padding } = this.state;

        const {
            sourceCol,
            sourceSrcType, sourceFileType,
            removeSourceKeyRow, readonly
        } = this.props;

        const colStyle: any = { left: padding, top: padding, width: w, height: h }

        const renderTableRow = (sourceType?: any, col?: any, i?: any) => {
            const removeOption = <div className="remove-cell"
                onClick={() => removeSourceKeyRow(col, i)}>
                <Tooltip title="删除当前列">
                    <Icon type="minus" />
                </Tooltip>
            </div>

            const editOption = <div className="edit-cell" onClick={
                () => { this.initEditKeyRow(true, sourceType, null, col, i) }
            }>
                <Tooltip title="编辑当前列">
                    <Icon type="edit" />
                </Tooltip>
            </div>

            const cellOperation = (remove: any, edit: any) => <div>
                {remove}
                {edit}
            </div>
            const typeValue = col ? col.value ? `常量(${col.value})` : `${col.type ? col.type.toUpperCase() : ''}${col.format ? `(${col.format})` : ''}` : '';
            const type = col ? scrollText(typeValue) : '类型';

            switch (sourceType) {
                case DATA_SOURCE.HDFS: {
                    const name: any = col ? scrollText(col.index !== undefined ? col.index : col.value ? `'${col.key}'` : col.key) : '索引位';
                    return <div>
                        <div className="cell" >{name}</div>
                        <div className="cell" title={typeValue}>{type}</div>
                        {
                            sourceFileType !== 'orc' ? <div className="cell">
                                { col ? cellOperation(removeOption, editOption) : '操作' }
                            </div> : ''
                        }
                    </div>
                }
                case DATA_SOURCE.HBASE: {
                    const name: any = col ? scrollText(col.value ? `'${col.key}'` : col.key) : '列名/行健';
                    const cf = col ? col.cf : '列族';

                    // 仅允许常量删除操作
                    const opt = col && col.key === 'rowkey' ? cellOperation(null, editOption)
                        : cellOperation(removeOption, editOption);
                    return <div className="four-cells">
                        <div className="cell" title={cf}>{ cf || '-' }</div>
                        <div className="cell" >{ name }</div>
                        <div className="cell" title={typeValue}>{ type }</div>
                        <div className="cell">
                            { col ? opt : '操作' }
                        </div>
                    </div>
                }
                case DATA_SOURCE.MAXCOMPUTE:
                case DATA_SOURCE.HIVE_1:
                case DATA_SOURCE.HIVE_2: {
                    const name: any = col ? scrollText(col.value ? `'${col.key}'` : col.key) : '字段名称';
                    // 仅允许常量删除操作
                    const opt = col && col.value ? cellOperation(removeOption, editOption)
                        : cellOperation(null, editOption);
                    return <div>
                        <div className="cell" >{name}</div>
                        <div className="cell" title={typeValue}>{ type }</div>
                        <div className="cell">
                            {col ? opt : '操作'}
                        </div>
                    </div>
                }
                case DATA_SOURCE.FTP: {
                    const name: any = col ? scrollText(col.index !== undefined ? col.index : col.value ? `'${col.key}'` : col.key) : '字段序号';
                    return <div>
                        <div className="cell" >{name}</div>
                        <div className="cell" title={typeValue}>{ type }</div>
                        <div className="cell">
                            {col ? cellOperation(removeOption, editOption) : '操作'}
                        </div>
                    </div>
                }
                default: {
                    const canFormat = isValidFormatType(col && col.type);
                    const opt = <div>{col && col.value ? removeOption : '' }{canFormat ? editOption : ''}</div>;
                    const name: any = col ? scrollText(col.value ? `'${col.key}'` : col.key) : '字段名称';
                    return <div>
                        <div className="cell">
                            { name }
                        </div>
                        <div className="cell" title={typeValue}>{ type }</div>
                        <div className="cell">
                            {col ? opt : '操作'}
                        </div>
                    </div>
                }
            }
        }

        const renderTableFooter = (sourceType: any) => {
            if (!readonly) {
                let footerContent = null;
                const btnAddConst = (<span className="col-plugin" onClick={
                    () => {
                        this.setState({ visibleConst: true })
                    }}>
                        +添加常量
                </span>);
                switch (sourceType) {
                    case DATA_SOURCE.HBASE:
                        footerContent = <span>
                            <span className="col-plugin" onClick={this.initAddKeyRow.bind(this, true, sourceFileType)}>
                                    +添加字段
                            </span>
                                &nbsp;
                            <span className="col-plugin" onClick={this.importSourceFields}>
                                    +文本模式
                            </span>
                        </span>;
                        break;
                    case DATA_SOURCE.HDFS: {
                        footerContent = sourceFileType !== 'orc'
                            ? <span>
                                <span className="col-plugin" onClick={ this.initAddKeyRow.bind(this, true, sourceFileType) }>
                                +添加字段
                                </span>
                            &nbsp;
                                <span className="col-plugin" onClick={this.importSourceFields}>
                                +文本模式
                                </span>
                            </span> : null;
                        break;
                    }
                    case DATA_SOURCE.FTP: {
                        footerContent = <span>
                            <span className="col-plugin" onClick={ this.initAddKeyRow.bind(this, true, sourceFileType) }>
                                +添加字段
                            </span>
                            &nbsp;
                            <span className="col-plugin" onClick={this.importSourceFields}>
                                +文本模式
                            </span>
                        </span>;
                        break;
                    }
                    default: {
                        footerContent = null; break;
                    }
                }
                return (
                    <div className="m-col pa"
                        style={{
                            left: padding,
                            top: padding + (h * (sourceCol.length + 1)),
                            width: w,
                            height: h,
                            zIndex: 2
                        }}
                    >
                        { footerContent }
                        { btnAddConst }
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
                { sourceCol.map((col: any, i: any) => {
                    return <div
                        style={{
                            width: w,
                            height: h,
                            left: padding,
                            top: padding + (h * (i + 1))
                        }}
                        className="m-col pa" key={ `sourceLeft-${i}`} >
                        { renderTableRow(sourceSrcType, col, i) }
                    </div>
                }) }
                { renderTableFooter(sourceSrcType) }
            </div>
        )
    }

    renderTarget () {
        const { w, h, W, padding } = this.state;
        const {
            targetSrcType,
            targetFileType, removeTargetKeyRow,
            targetCol, sourceCol, readonly
        } = this.props;

        const colStyle: any = {
            left: W - (padding + w),
            top: padding,
            width: w,
            height: h
        }

        const renderTableRow = (targetType?: any, col?: any, i?: any) => {
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
            switch (targetType) {
                case DATA_SOURCE.HDFS: {
                    const name: any = col ? scrollText(col.key) : '字段名称';
                    const type = col ? col.type.toUpperCase() : '类型';
                    return <div>
                        <div className="cell">{name}</div>
                        <div className="cell" title={type}>{type}</div>
                        <div className="cell">
                            { col ? operations : '操作' }
                        </div>
                    </div>
                }
                case DATA_SOURCE.HBASE: {
                    const name = col ? col.cf : '列族';
                    const column: any = col ? scrollText(col.key) : '列名';
                    const type: any = col ? scrollText(col.type.toUpperCase()) : '类型';
                    return <div className="four-cells">
                        <div className="cell" >{name}</div>
                        <div className="cell" title={column}>{column}</div>
                        <div className="cell" title={type}>{type}</div>
                        <div className="cell">{ col ? operations : '操作' }</div>
                    </div>
                }
                case DATA_SOURCE.FTP: {
                    const column: any = col ? scrollText(col.key) : '字段名称';
                    const type = col ? col.type.toUpperCase() : '类型';
                    return <div>
                        <div className="cell" title={column}>{column}</div>
                        <div className="cell" title={type}>{type}</div>
                        <div className="cell">
                            { col ? operations : '操作' }
                        </div>
                    </div>
                }
                default: {
                    const typeText = col ? `${col.type.toUpperCase()}${col.isPart ? `(分区字段)` : ''}` : '类型';
                    const fieldName: any = col ? scrollText(col.key) : '字段名称';
                    return <div>
                        <div className="cell" title={fieldName}>{fieldName}</div>
                        <div className="cell" title={typeText}>{ typeText }</div>
                    </div>
                }
            }
        }

        const renderTableFooter = (targetType: any) => {
            if (!readonly) {
                let footerContent = null;
                switch (targetType) {
                    case DATA_SOURCE.HBASE:
                        footerContent = <div>
                            <span className="col-plugin" onClick={this.initAddKeyRow.bind(this, false, targetFileType)}>
                                +添加字段
                            </span>
                            &nbsp;
                            <span className="col-plugin" onClick={this.importFields}>
                                +文本模式
                            </span>
                        </div>;
                        break;
                    case DATA_SOURCE.HDFS: {
                        footerContent = <div>
                            <span className="col-plugin" onClick={ this.initAddKeyRow.bind(this, false, targetFileType) }>
                                +添加字段
                            </span>
                            &nbsp;
                            <span className="col-plugin" onClick={this.importFields}>
                                +文本模式
                            </span>
                        </div>;
                        break;
                    }
                    case DATA_SOURCE.FTP: {
                        footerContent = <div>
                            <span className="col-plugin" onClick={ this.initAddKeyRow.bind(this, false, targetFileType) }>
                                +添加字段
                            </span>
                            &nbsp;
                            <span className="col-plugin" onClick={this.importFields}>
                                +文本模式
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
                    width: w,
                    height: h,
                    zIndex: 2
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
            { targetCol.map((col: any, i: any) => {
                return <div key={ `targetRight-${i}` } className="m-col pa"
                    style={{
                        width: w,
                        height: h,
                        top: padding + (h * (i + 1)),
                        left: W - (padding + w)
                    }}>
                    { renderTableRow(targetSrcType, col, i) }
                </div>
            }) }
            { renderTableFooter(targetSrcType) }
        </div>
    }

    keymapHelpModal = () => {
        Modal.info({
            title: '快速配置字段映射',
            content: (
                <div>
                    <img style={{
                        width: '100%',
                        height: 400
                    }} src="/public/rdos/img/keymap_help.jpg" />
                </div>
            ),
            width: 850,
            okText: '了解'
        });
    }

    renderKeyModal = () => {
        const {
            keyModal, keyModalVisible,
            sourceColumnFamily, targetColumnFamily
        } = this.state;

        const { operation, isReader } = keyModal;

        const {
            sourceSrcType, targetSrcType
        } = this.props;

        const dataType = keyModal.isReader ? sourceSrcType : targetSrcType;

        let onOk = this.doAddkeyRow;
        let title = '添加HDFS字段';
        if (operation === 'add') {
            if (dataType === DATA_SOURCE.HBASE) {
                title = '添加HBase字段';
            } else if (dataType === DATA_SOURCE.FTP) {
                title = '添加FTP字段';
            }
        } else if (operation === 'edit') {
            onOk = this.doEditKeyRow;
            title = '修改HDFS字段';
            if (dataType === DATA_SOURCE.HBASE) {
                title = '修改HBase字段';
            } else if (dataType === DATA_SOURCE.FTP) {
                title = '添加FTP字段';
            } else {
                title = '修改字段';
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

    getBatIntVal = (type: any, typeCol: any) => {
        const isNotHBase = type !== DATA_SOURCE.HBASE;
        let initialVal = '';
        if (isNotHBase) {
            typeCol && typeCol.forEach((item: any) => {
                const field = utils.checkExist(item.index) ? item.index : utils.checkExist(item.key) ? item.key : undefined;
                if (field !== undefined) initialVal += `${field}:${item.type},\n`;
            })
        } else {
            typeCol && typeCol.forEach((item: any) => {
                const field = utils.checkExist(item.key) ? item.key : undefined;
                if (field !== undefined) initialVal += `${item.cf || '-'}:${field}:${item.type},\n`;
            })
        }
        return initialVal;
    }

    renderBatchModal = () => {
        const {
            batchModal, batchSourceModal,
            sourceColumnFamily, targetColumnFamily
        } = this.state;
        const {
            sourceSrcType, targetSrcType, sourceCol, targetCol
        } = this.props;

        let sPlaceholder, sDesc, tPlaceholder, tDesc;
        switch (sourceSrcType) {
            case DATA_SOURCE.FTP:
            case DATA_SOURCE.HDFS: {
                sPlaceholder = '0: STRING,\n1: INTEGER,...'
                sDesc = 'index: type, index: type'
                break;
            }
            case DATA_SOURCE.HBASE: {
                sPlaceholder = 'cf1: field1: STRING,\ncf1: field2: INTEGER,...'
                sDesc = 'columnFamily: fieldName: type,'
                break;
            }
        }

        switch (targetSrcType) {
            case DATA_SOURCE.FTP:
            case DATA_SOURCE.HDFS: {
                tPlaceholder = 'field1: STRING,\nfield2: INTEGER,...'
                tDesc = 'fieldName: type, fieldName: type'
                break;
            }
            case DATA_SOURCE.HBASE: {
                tPlaceholder = 'cf1: field1: STRING,\ncf2: field2: INTEGER,...'
                tDesc = 'columnFamily: fieldName: type,'
                break;
            }
        }

        return [
            <BatchModal
                key="sourceBatchAdd"
                title="批量添加源表字段"
                desc={sDesc}
                columnFamily={sourceColumnFamily}
                sourceType={sourceSrcType}
                columns={sourceCol}
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
                columns={targetCol}
                placeholder={tPlaceholder}
                sourceType={targetSrcType}
                visible={batchModal.visible}
                value={batchModal.batchText}
                onOk={this.doBatchImport}
                onCancel={this.hideBatchImportModal}
                onChange={this.batchTextChange}
            />
        ]
    }

    render () {
        const {
            w, h, W, padding, visibleConst
        } = this.state;

        const {
            keymap,
            sourceSrcType,
            targetSrcType,
            addSourceKeyRow,
            navtoStep, targetCol, sourceCol,
            targetMap
        } = this.props;
        const { isFocus } = this.state;
        const H = h * (Math.max(targetCol.length, sourceCol.length) + 1);
        const noKeyMapping = !keymap.source || keymap.source.length === 0;
        const focusSty: any = {
            width: isFocus ? '285px' : '185px',
            verticalAlign: isFocus ? 'text-top' : 'middle'
            // border: !isSuccess ? '1px solid #f04134' : '1px solid #dddddd'
        }
        const rowBtnType: any = this.state.rowMap ? 'primary' : 'default';
        const nameBtnType: any = this.state.nameMap ? 'primary' : 'default'
        return <Resize onResize={this.resize}>
            <div style={{ margin: '0 20px' }}>
                <p style={{ fontSize: 12, color: '#ccc', marginTop: '-20px', textAlign: 'center' }}>
                    您要配置来源表与目标表的字段映射关系，通过连线将待同步的字段左右相连，也可以通过同行映射、同名映射批量完成映射
                    &nbsp;{ noKeyMapping ? <a onClick={this.keymapHelpModal}>如何快速配置字段映射？</a> : '' }
                </p>
                { targetSrcType === DATA_SOURCE.HBASE ? (

                    <Row>
                        <Col span={21} style={{ textAlign: 'center' }}>
                            <div className="m-keymapbox"
                                style={{
                                    width: W + 200,
                                    height: h - 18,
                                    marginTop: 18,
                                    zIndex: 3,
                                    display: 'inline-block'
                                }}
                            >
                                <div className="pa"
                                    style={{
                                        left: W - (padding + w) + 100
                                    }}
                                >
                                    <span>rowkey</span>: <Input
                                        autoFocus={ isFocus }
                                        type={ isFocus ? 'textarea' : 'text' }
                                        style={ focusSty }
                                        defaultValue={get(targetMap, 'type.rowkey', '')}
                                        placeholder={ rowkeyTxt }
                                        onChange={ this.debounceRowkeyChange }
                                        onFocus={() => { this.setState({ isFocus: true }) }}
                                        onBlur={ this.handleBlurRowkeyCheck }
                                        autosize={{ minRows: 4, maxRows: 12 }}
                                    />
                                </div>
                            </div>
                        </Col>
                    </Row>
                ) : null}
                <Row>
                    <Col span={21} style={{ textAlign: 'center' }}>
                        <div className="m-keymapbox"
                            ref={ (el: any) => this.container = el }
                            style={{
                                width: W,
                                minHeight: H + 20,
                                display: 'inline-block'
                            }}
                        >
                            { this.renderSource() }
                            { this.renderTarget() }
                            <svg
                                ref={ (el: any) => this.canvas = el }
                                width={ W > w * 2 ? W - w * 2 : 0 }
                                height={ H }
                                className="pa m-keymapcanvas"
                                style={{ left: w, top: padding }}
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
                    <Col span={3}>
                        {!this.props.readonly ? <div className="m-buttons">
                            <Button
                                type={rowBtnType}
                                onClick={ this.setRowMap.bind(this) }
                            >{ this.state.rowMap ? '取消同行映射' : '同行映射'}</Button>
                            <br/>
                            <Button
                                disabled={ isHdfsType(sourceSrcType) || isFtpType(sourceSrcType)}
                                type={nameBtnType}
                                onClick={ this.setNameMap.bind(this) }
                            >{ this.state.nameMap ? '取消同名映射' : '同名映射' }
                            </Button>
                            <br/>
                            {
                                isHdfsType(targetSrcType) || isFtpType(targetSrcType)
                                    ? <>
                                        <Button
                                            onClick={() => { this.copySourceCols(sourceCol) }}>
                                            拷贝源字段
                                        </Button>
                                        <br />
                                    </>
                                    : ''
                            }
                            {
                                isHdfsType(sourceSrcType) || isFtpType(sourceSrcType)
                                    ? <Button
                                        onClick={() => { this.copyTargetCols(targetCol) }}>
                                    拷贝目标字段
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
                    onCancel={() => { this.setState({ visibleConst: false }) }}
                />
                {!this.props.readonly && <div className="steps-action" style={{ marginTop: 80 }}>
                    <Button style={{ marginRight: 8 }} onClick={() => this.prev(navtoStep)}>上一步</Button>
                    <Button type="primary" onClick={() => this.next(navtoStep)}>下一步</Button>
                </div>}
            </div>
        </Resize>
    }

    prev (cb: any) {
        // eslint-disable-next-line
        cb.call(null, 1);
    }

    next (cb: any) {
        const { keymap, targetSrcType, targetCol, isNativeHive } = this.props;
        const { source, target } = keymap;

        if (source.length === 0 && target.length === 0) {
            message.error('尚未配置数据同步的字段映射！');
            return;
        }

        // if (!this.state.isSuccess) {
        //     message.error('rowkey格式有误！');
        //     return;
        // }

        const isCarbonDataCheckPartition = () => {
            if (targetSrcType === DATA_SOURCE.CARBONDATA && !isNativeHive) {
                const hasPartiton = targetCol.find((col: any) => col.isPart);
                const keymapPartition = target.find((col: any) => col.isPart);
                if (hasPartiton && !keymapPartition) {
                    message.error('目标字段中的分区字段必须添加映射！');
                    return false;
                }
            }
            return true;
        }

        // 如果Carbondata作为目标数据源，且目标字段中有分区字段，分区字段必须添加映射，否则error
        if (!isCarbonDataCheckPartition()) return false; ;

        // 针对Hbase增加keyrow检验项
        if (targetSrcType === DATA_SOURCE.HBASE) {
            if (!this.checkHBaseRowKey()) {
                return;
            }
        }
        // eslint-disable-next-line
        cb.call(null, 3);
    }

    checkHBaseRowKey = (e?: any) => {
        const { keymap, targetMap, sourceSrcType } = this.props
        const { type } = targetMap
        const source = keymap.source

        if (type && type.rowkey) {
            const arr: any = []
            if (sourceSrcType === DATA_SOURCE.HBASE) {
                let regx = /([\w]+:[\w]+)/g
                const arr = type.rowkey.match(regx) ? type.rowkey.match(regx) : '';
                for (let i = 0; i < arr.length; i++) {
                    const val = arr[i].split(':')
                    if (val && val.length === 2) {
                        const res = source.find((item: any) => item.cf === val[0] && item.key === val[1])
                        if (!res) {
                            message.error('目标表rowkey在源表中并不存在！')
                            return false;
                        }
                    }
                }
            } else {
                // 正则提取
                let regx = /\$\(([\w]+)\)/g; let temp: any;
                while ((temp = regx.exec(type.rowkey)) !== null) {
                    arr.push(temp[1])
                }
                // 验证字段
                for (let i = 0; i < arr.length; i++) {
                    let res = ''
                    if (isObject(source[0])) {
                        res = source.find((item: any) => {
                            const name = item.key !== undefined ? item.key : item.index
                            return `${name}` === `${arr[i]}` ? item : undefined
                        })
                    } else {
                        res = source.find((item: any) => item === arr[i])
                    }
                    if (!res) {
                        message.error(`目标表rowkey: ${arr[i]}在源表中并不存在！！`)
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

    /**
     * 拷贝源表字段
     */
    copySourceCols = (sourceKeyRow: any) => {
        const {
            targetSrcType,
            addBatchTargetKeyRow
        } = this.props;

        if (isHdfsType(targetSrcType)) {
            const serverParams: any = {};
            sourceKeyRow.forEach((item: any) => {
                serverParams[item.key || item.index] = item.type
            })
            Api.convertToHiveColumns({
                columns: serverParams
            }).then(
                (res: any) => {
                    if (res.code == 1) {
                        const params: any = [];
                        Object.getOwnPropertyNames(res.data).forEach((key: any) => {
                            params.push({
                                key: key,
                                type: res.data[key]
                            })
                        });
                        addBatchTargetKeyRow(params)
                    }
                }
            )
        } else if (sourceKeyRow && sourceKeyRow.length > 0) {
            const params = sourceKeyRow.map((item: any) => {
                return {
                    key: item.key || item.index,
                    type: 'STRING'
                }
            })
            addBatchTargetKeyRow(params)
        }
    }

    /**
     * 拷贝目标表字段
     */
    copyTargetCols = (targetKeyRow: any) => {
        const { addBatchSourceKeyRow } = this.props;
        if (targetKeyRow && targetKeyRow.length > 0) {
            const params = targetKeyRow.map((item: any, index: any) => {
                return {
                    key: index,
                    type: 'STRING'
                }
            })
            addBatchSourceKeyRow(params);
        }
    }

    batchTextChange = (e: any) => {
        this.setState({
            batchModal: {
                visible: true,
                batchText: e.target.value
            }
        })
    }

    batchSourceTextChange = (e: any) => {
        this.setState({
            batchSourceModal: {
                visible: true,
                batchText: e.target.value
            }
        })
    }

    doBatchImport = () => {
        const { batchModal } = this.state
        const { batchText } = batchModal
        const {
            targetSrcType,
            replaceBatchTargetKeyRow
        } = this.props

        const str = utils.trim(batchText)
        const arr = str.split(',')

        const params: any = []

        switch (targetSrcType) {
            case DATA_SOURCE.FTP:
            case DATA_SOURCE.HDFS: {
                for (let i = 0; i < arr.length; i++) {
                    const item = arr[i]
                    if (!item) continue;
                    const map = item.split(':')
                    const key = utils.trim(map[0])
                    const type = utils.trim(map[1].toUpperCase());
                    if (hdfsFieldTypes.includes(type)) {
                        params.push({
                            key,
                            type
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
                    if (!hbaseFieldTypes.includes(type)) {
                        message.error(`字段${name}的数据类型错误！`)
                        return;
                    }
                    params.push({
                        cf: cf,
                        key: name,
                        type
                    });
                }
                break;
            }
        }
        replaceBatchTargetKeyRow(params);
        this.hideBatchImportModal()
    }

    hideBatchImportModal = () => {
        this.setState({
            batchModal: {
                visible: false,
                batchText: ''
            }
        });
    }

    hideBatchSourceModal = () => {
        this.setState({
            batchSourceModal: {
                visible: false,
                batchText: ''
            }
        });
    }

    // 批量导入字段
    importFields = () => {
        this.setState({
            batchModal: {
                visible: true,
                batchText: this.getBatIntVal(this.props.targetSrcType, this.props.targetCol)
            }
        })
    }

    importSourceFields = () => {
        this.setState({
            batchSourceModal: {
                visible: true,
                batchText: this.getBatIntVal(this.props.sourceSrcType, this.props.sourceCol)
            }
        })
    }

    doBatchAddSourceFields = () => {
        const { batchSourceModal } = this.state
        const { batchText } = batchSourceModal
        const {
            sourceSrcType,
            replaceBatchSourceKeyRow
        } = this.props;

        if (!batchText) {
            this.hideBatchSourceModal();
            return;
        }

        const arr = batchText.split(',');
        const params: any = []
        switch (sourceSrcType) {
            case DATA_SOURCE.FTP:
            case DATA_SOURCE.HDFS: {
                for (let i = 0; i < arr.length; i++) {
                    const item = arr[i].replace(/\n/, '');
                    if (!item) continue;
                    const map = item.split(':');
                    if (map.length < 1) { break; };
                    const key = parseInt(utils.trim(map[0]), 10);
                    const type = map[1] ? utils.trim(map[1]).toUpperCase() : null;
                    if (!isNaN(key) && isNumber(key)) {
                        if (hdfsFieldTypes.includes(type)) {
                            if (!params.find((pa: any) => pa.key === key)) {
                                params.push({
                                    key: key,
                                    type
                                })
                            }
                        } else {
                            message.error(`索引 ${key} 的数据类型错误！`)
                            return
                        }
                    } else {
                        message.error(`索引名称 ${key} 应该为整数数字！`)
                        return
                    }
                }
                break;
            }
            case DATA_SOURCE.HBASE: {
                for (let i = 0; i < arr.length; i++) {
                    const item = arr[i].replace(/\n/, '');
                    if (!item) continue;

                    const map = item.split(':');
                    if (map.length < 2) { break; };
                    const cf = utils.trim(map[0]);
                    const name = utils.trim(map[1]);
                    const type = utils.trim(map[2]);
                    if (!hbaseFieldTypes.includes(type)) {
                        message.error(`字段${name}的数据类型错误！`)
                        return;
                    }
                    params.push({
                        cf: cf,
                        key: name,
                        type
                    });
                }
                break;
            }
        }

        replaceBatchSourceKeyRow(params);
        this.hideBatchSourceModal();
    }

    /**
     * @description 同行映射
     * @memberof Keymap
     */
    setRowMap () {
        const { rowMap, nameMap } = this.state;
        const {
            targetCol, sourceCol
        } = this.props;

        if (!rowMap) {
            this.props.setRowMap({
                sourceCol: sourceCol, // convertColumn2Keymap_s(sourceCol),
                targetCol: targetCol // convertColumn2Keymap_t(targetCol)
            });
        } else {
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
    setNameMap () {
        const { nameMap, rowMap } = this.state;
        const { targetCol, sourceCol, sourceSrcType, targetSrcType } = this.props;

        if (!nameMap) {
            this.props.setNameMap({ sourceCol, targetCol, sourceSrcType, targetSrcType });
        } else {
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
    initAddKeyRow (isReader: any, fileType: any) {
        this.setState({
            keyModalVisible: true,
            keyModal: {
                operation: 'add',
                isReader,
                fileType
            }
        });
    }

    initEditKeyRow = (isReader: any, fileType: any, sourceCol: any, field: any, index: any) => {
        this.setState({
            keyModalVisible: true,
            keyModal: {
                operation: 'edit',
                isReader,
                fileType,
                editField: field,
                position: index,
                source: sourceCol
            }
        });
    }

    /**
     * @description dispatch action
     * @param {boolean} isReader 添加到srouce/target
     * @memberof Keymap
     */
    doAddkeyRow = (formData: any) => {
        const { isReader } = this.state.keyModal
        const { addSourceKeyRow, addTargetKeyRow } = this.props;
        if (formData) {
            if (isReader) {
                addSourceKeyRow(formData);
            } else {
                addTargetKeyRow(formData);
            }
            this.hideKeyModal();
        }
    }

    /**
     * 编辑KeyRow
     * @param {*} isReader
     */
    doEditKeyRow = (formData: any) => {
        const {
            editSourceKeyRow,
            editTargetKeyRow,
            editKeyMapTarget,
            removeKeyMap,
            editKeyMapSource
        } = this.props;
        const { keyModal } = this.state;
        const { isReader, position, editField } = keyModal;
        if (formData) {
            if (isReader) {
                editSourceKeyRow({
                    index: position,
                    value: formData
                });
                editKeyMapSource({
                    old: editField,
                    replace: formData
                })
                /**
                 * 如果字段的名称发生了改变，则KeyMap中的关系需要
                 * 重新连接，否则则保存当前的映射关系
                */
                if (editField.key !== formData.key) {
                    removeKeyMap({
                        source: editField
                    })
                }
            } else {
                editTargetKeyRow({
                    index: position,
                    value: formData
                });
                editKeyMapTarget({
                    old: editField,
                    replace: formData
                })
                /**
                 * 如果字段的名称发生了改变，则KeyMap中的关系需要
                 * 重新连接，否则则保存当前的映射关系
                */
                if (editField.key !== formData.key) {
                    removeKeyMap({
                        target: editField
                    })
                }
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
                source: ''
            }
        });
    }
}

const mapState = (state: any) => {
    const { dataSync } = state.offlineTask;
    return {
        targetMap: dataSync.targetMap,
        targetCol: dataSync.targetMap.column || [],
        targetSrcType: dataSync.targetMap.type && dataSync.targetMap.type.type, // 目标数据源类型
        targetFileType: dataSync.sourceMap.type && dataSync.targetMap.type.fileType,
        isNativeHive: dataSync.targetMap.isNativeHive,
        sourceCol: dataSync.sourceMap.column || [],
        sourceSrcType: dataSync.sourceMap.type && dataSync.sourceMap.type.type, // 源头数据源类型
        sourceFileType: dataSync.sourceMap.type && dataSync.sourceMap.type.fileType,
        keymap: dataSync.keymap
    };
};

export default connect(mapState, keyMapActions)(Keymap);
