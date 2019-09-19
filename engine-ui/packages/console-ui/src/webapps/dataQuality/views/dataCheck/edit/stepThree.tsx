import * as React from 'react';
import { connect } from 'react-redux';
import { select, selectAll, mouse, Selection } from 'd3-selection';
import { isEmpty } from 'lodash';
import { Table, Form, InputNumber, Button, message, Row, Col } from 'antd';

import { keyMapActions } from '../../../actions/dataCheck/keyMapActions';
import DSApi from '../../../api/dataSource';

const FormItem = Form.Item;

const mapState = (state: any) => {
    const { keymap } = state;
    return { keymap };
};

@(connect(
    mapState,
    keyMapActions
) as any)
class StepThree extends React.Component<any, any> {
    constructor (props: any) {
        super(props);
        this.state = {
            selectedSetting: [],
            selectedSource: [],
            selectedTarget: [],
            h: 40,
            W: 0,
            H: 0,
            originColumn: [],
            targetColumn: [],
            rowMap: false,
            nameMap: false
        };
    }
    $canvas: Selection<SVGAElement, unknown, null, undefined>;
    canvas: SVGAElement;
    $activeLine: any;
    componentDidMount () {
        this.initDiverseData();
        this.$canvas = select(this.canvas);
        this.$activeLine = select('#activeLine');
        // this.setState({
        //     W: this.getCanvasW(),
        //     H: this.getCanvasH()
        // })
        // this.loadColumnFamily();
        this.listenResize();
        this.getColumnData();
        this.setEditKeymapData();
        this.setKeymapCheckData();
        this.setSettingTableFields();
        this.drawSvg();
    }

    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps(nextProps: any) {
        let newKeymap = nextProps.keymap;

        let oldKeymap = this.props.keymap;

        // keymap有变化
        if (newKeymap.source.length != oldKeymap.source.length) {
            const { origin, target } = this.props.editParams;
            this.props.changeParams({
                origin: { ...origin, column: newKeymap.source },
                target: { ...target, column: newKeymap.target }
            });
        }
    }

    componentDidUpdate () {
        this.$canvas.selectAll('.dl, .dr, .lines').remove();
        this.drawSvg();
    }

    componentWillUnmount () {
        window.removeEventListener('resize', this.resize, false);
        this.props.resetLinkedKeys();
    }

    listenResize () {
        window.addEventListener('resize', this.resize, false);
    }

    /**
     * 获取窗口大小
     */
    resize = () => {
        this.setState({
            W: this.getCanvasW(),
            H: this.getCanvasH()
        });
    };

    /**
     * 获取左右表字段数据
     */
    getColumnData = () => {
        const { origin, target } = this.props.editParams;

        DSApi.getDataSourcesColumn({
            sourceId: origin.dataSourceId,
            tableName: origin.table
        }).then((res: any) => {
            if (res.code === 1) {
                this.setState({
                    originColumn: res.data
                });
                this.resize();
            }
        });

        DSApi.getDataSourcesColumn({
            sourceId: target.dataSourceId,
            tableName: target.table
        }).then((res: any) => {
            if (res.code === 1) {
                this.setState({
                    targetColumn: res.data
                });
                this.resize();
            }
        });
    };

    /**
     * 已连接的字段数据
     */
    setEditKeymapData = () => {
        const { origin, target } = this.props.editParams;

        if (origin.column && target.column) {
            this.props.setEditMap({
                source: origin.column,
                target: target.column
            });
        }
    };

    /**
     * 已填写的差异设置
     */
    setSettingTableFields = () => {
        const { setting } = this.props.editParams;
        const { form } = this.props;

        if (!isEmpty(setting)) {
            let selectedSetting: any = [];

            let fieldsValue: any = {};

            Object.keys(setting).forEach((item: any) => {
                if (setting[item]) {
                    selectedSetting.push(item);
                    if (item != 'matchCase' && item != 'matchNull') {
                        fieldsValue[item] = setting[item];
                    }
                }
            });

            form.setFieldsValue(fieldsValue);
            this.setState({ selectedSetting });
        }
    };

    /**
     * 已勾选的逻辑主键
     */
    setKeymapCheckData = () => {
        const { mappedPK } = this.props.editParams;

        // 填充keymap的checkbox
        if (!isEmpty(mappedPK)) {
            let selectedSource: any = [];

            let selectedTarget: any = [];

            for (let [key, value] of Object.entries(mappedPK)) {
                selectedSource.push(key);
                selectedTarget.push(value);
            }

            this.setState({
                selectedSource,
                selectedTarget
            });
        }
    };

    /**
     * 绘制
     */
    drawSvg () {
        this.renderDags();
        this.renderLines();
        this.bindEvents();
    }

    // 绘制两边小圆点
    renderDags () {
        const { h, W, originColumn, targetColumn } = this.state;

        this.$canvas
            .append('g')
            .attr('class', 'dl')
            .selectAll('g')
            .data(originColumn)
            .enter()
            .append('g')
            .attr('class', 'col-dag-l')
            .append('circle')
            .attr('class', 'dag-circle')
            .attr('cx', (d: any, i: any) => 15)
            .attr('cy', (d: any, i: any) => h * (i + 1.5))
            .attr('r', 5)
            .attr('stroke-width', 2)
            .attr('stroke', '#fff')
            .attr('fill', '#2491F7');

        this.$canvas
            .append('g')
            .attr('class', 'dr')
            .selectAll('g')
            .data(targetColumn)
            .enter()
            .append('g')
            .attr('class', 'col-dag-r')
            .append('circle')
            .attr('class', 'dag-circle')
            .attr('cx', (d: any, i: any) => {
                return W - 15;
            })
            .attr('cy', (d: any, i: any) => h * (i + 1.5))
            .attr('r', 5)
            .attr('stroke-width', 2)
            .attr('stroke', '#fff')
            .attr('fill', '#2491F7');
    }

    // 绘制已连接的线段
    renderLines () {
        const { h, W } = this.state;
        const { source, target } = this.props.keymap;
        const $dagL = selectAll('.col-dag-l');
        const $dagR = selectAll('.col-dag-r');

        let map: any = [];

        // 将连线数据存入map数组
        source.forEach((sourceKey: any, sourceIndex: any) => {
            $dagL.each((dl: any, i: any) => {
                let sx: number, sy: number, ex, ey;

                if (dl.key === sourceKey) {
                    sx = 15;
                    sy = (i + 1.5) * h;

                    $dagR.each((dr: any, j: any) => {
                        if (dr.key === target[sourceIndex]) {
                            ex = W - 15;
                            ey = (j + 1.5) * h;

                            map.push({
                                s: { x: sx, y: sy },
                                e: { x: ex, y: ey },
                                dl: sourceKey,
                                dr: target[sourceIndex]
                            });
                        }
                    });
                }
            });
        });

        const mapline = this.$canvas
            .append('g')
            .attr('class', 'lines')
            .selectAll('g')
            .data(map)
            .enter()
            .append('g')
            .attr('class', 'mapline');

        mapline
            .append('line')
            .attr('x1', (d: any) => d.s.x)
            .attr('y1', (d: any) => d.s.y)
            .attr('x2', (d: any) => d.e.x)
            .attr('y2', (d: any) => d.e.y)
            .attr('stroke', '#2491F7')
            .attr('stroke-width', 2)
            .attr('marker-end', 'url(#arrow)');

        // 加大交互区域
        mapline
            .append('rect')
            .attr('x', (d: any) => d.s.x)
            .attr('y', (d: any) => {
                return d.s.y > d.e.y ? d.s.y - 15 : d.s.y - 10;
            })
            .attr('width', (d: any) => {
                console.log(
                    d,
                    (d.e.x - d.s.x) ^ (2 + (d.e.y - d.s.y)) ^ 2 ^ (1 / 2)
                );
                return Math.pow(
                    Math.pow(d.e.x - d.s.x, 2) + Math.pow(d.e.y - d.s.y, 2),
                    1 / 2
                );
            })
            .attr('transform', (d: any) => {
                return `rotate(
                            ${180 /
                                (Math.PI /
                                    Math.atan(
                                        (d.e.y - d.s.y) / (d.e.x - d.s.x)
                                    ))}
                        )`;
            })
            .attr('transform-origin', (d: any) => {
                const offset = d.s.y > d.e.y ? 15 : 10;
                return d.s.x + ' ' + (d.s.y - offset);
            })
            .attr('height', (d: any) => {
                return d.s.y > d.e.y ? '20px' : '15px';
            })
            .attr('fill', 'transparent');
    }

    // 绑定事件
    bindEvents () {
        const { h, W, selectedSource, selectedTarget } = this.state;

        const $line = this.$activeLine;
        const $dagL = selectAll('.col-dag-l');
        const $dagR = selectAll('.col-dag-r');

        let isMouseDown = false;
        let sourceKeyObj: any, targetKeyObj: any;

        $dagL.on('mousedown', (d: any, i: any, nodes: any) => {
            sourceKeyObj = d;
            isMouseDown = true;

            let sx = 15;

            let sy = (i + 1.5) * h;
            $line
                .attr('x1', sx)
                .attr('y1', sy)
                .attr('x2', sx)
                .attr('y2', sy);
        });

        this.$canvas
            .on('mousemove', () => {
                if (isMouseDown) {
                    let xy = mouse(this.$canvas.node());
                    $line.attr('x2', xy[0]).attr('y2', xy[1]);
                }
            })
            .on('mouseup', () => {
                if (isMouseDown) {
                    let xy = mouse(this.$canvas.node());
                    let [ex, ey] = xy;
                    let threholdX = W - 30;

                    if (ex < threholdX) {
                        this.resetActiveLine();
                    } else {
                        let tidx = Math.floor(ey / h) - 1;

                        $dagR.each((d: any, i: any) => {
                            if (i === tidx) {
                                targetKeyObj = d;
                            }
                        });
                    }
                }

                if (sourceKeyObj && targetKeyObj) {
                    this.props.addLinkedKeys({
                        source: sourceKeyObj.key,
                        target: targetKeyObj.key
                    });

                    this.resetActiveLine();
                }

                isMouseDown = false;
            });

        this.$canvas
            .selectAll('.mapline')
            .on('mouseover', (d: any, i: any, nodes: any) => {
                select(nodes[i])
                    .select('line')
                    .attr('stroke-width', 4)
                    .attr('stroke', '#2491F7');
            })
            .on('mouseout', (d: any, i: any, nodes: any) => {
                select(nodes[i])
                    .select('line')
                    .attr('stroke-width', 2)
                    .attr('stroke', '#2491F7');
            })
            .on('click', (d: any, i: any, nodes: any) => {
                this.props.delLinkedKeys({
                    source: d.dl,
                    target: d.dr
                });

                let sourceCheck: any = [...selectedSource];

                let targetCheck: any = [...selectedTarget];

                let mapPk: any = {};

                sourceCheck = sourceCheck.filter((item: any) => item !== d.dl);
                targetCheck = targetCheck.filter((item: any) => item !== d.dr);

                this.setState({
                    selectedSource: sourceCheck,
                    selectedTarget: targetCheck
                });

                sourceCheck.forEach((s: any, index: any) => {
                    mapPk[s] = targetCheck[index];
                });
                this.props.changeParams({ mappedPK: mapPk });
            });
    }

    resetActiveLine () {
        this.$activeLine
            .attr('x1', -10)
            .attr('y1', -10)
            .attr('x2', -10)
            .attr('y2', -10);
    }

    initDiverseSetting = () => {
        return [
            {
                title: '差异特征',
                dataIndex: 'setting',
                key: 'setting',
                render: (text: any, record: any) => {
                    return this.getSettingItem(text);
                },
                width: '95%'
            }
        ];
    };

    initDiverseData = () => {
        return [
            {
                id: 1,
                setting: 'diverseNum'
            },
            {
                id: 2,
                setting: 'diverseRatio'
            },
            {
                id: 3,
                setting: 'diverseAbsolute'
            },
            {
                id: 4,
                setting: 'decimalRetain'
            },
            {
                id: 5,
                setting: 'matchCase'
            },
            {
                id: 6,
                setting: 'matchNull'
            }
        ];
    };

    getSettingItem = (key: any) => {
        const { getFieldDecorator } = this.props.form;
        const { setting } = this.props.editParams;

        switch (key) {
            case 'diverseNum':
                return (
                    <FormItem style={{ marginBottom: 0 }}>
                        记录数差异，对比左右表的总记录数，差距小于
                        {getFieldDecorator('diverseNum', {
                            rules: [
                                {
                                    required: true,
                                    message: '不能为空'
                                }
                            ],
                            initialValue: setting.diverseNum
                        })(<InputNumber size="default" min={1} step={1} />)}
                        %时候，计为成功匹配
                    </FormItem>
                );
            case 'diverseRatio':
                return (
                    <FormItem style={{ marginBottom: 0 }}>
                        数值差异百分比，对比左右表的数值型数据时，差距百分比小于
                        {getFieldDecorator('diverseRatio', {
                            rules: [
                                {
                                    required: true,
                                    message: '不能为空'
                                }
                            ],
                            initialValue: setting.diverseRatio
                        })(<InputNumber size="default" min={1} step={1} />)}
                        %时候，计为成功匹配
                    </FormItem>
                );
            case 'diverseAbsolute':
                return (
                    <FormItem style={{ marginBottom: 0 }}>
                        数值差异绝对值，对比左右表的数值型数据时，差距绝对值小于
                        {getFieldDecorator('diverseAbsolute', {
                            rules: [
                                {
                                    required: true,
                                    message: '不能为空'
                                }
                            ],
                            initialValue: setting.diverseAbsolute
                        })(<InputNumber size="default" min={1} step={1} />)}
                        时候，计为成功匹配
                    </FormItem>
                );
            case 'decimalRetain':
                return (
                    <FormItem style={{ marginBottom: 0 }}>
                        数值对比忽略小数点，忽略小数点后
                        {getFieldDecorator('decimalRetain', {
                            rules: [
                                {
                                    required: true,
                                    message: '不能为空'
                                }
                            ],
                            initialValue: setting.decimalRetain
                        })(
                            <InputNumber
                                size="default"
                                min={1}
                                step={1}
                                precision={0}
                            />
                        )}
                        位
                    </FormItem>
                );
            case 'matchCase':
                return (
                    <p>
                        字符不区分大小写，对比左右表的字符串型数据时，不区分大小写
                    </p>
                );
            case 'matchNull':
                return (
                    <p>
                        空值与NULL等价，对比左右表的数据时，认为空值与NULL值是相等的
                    </p>
                );
            default:
                break;
        }
    };

    getCanvasW () {
        return document.querySelector('.keymap-svg').getBoundingClientRect()
            .width;
    }

    getCanvasH () {
        const left = document
            .querySelectorAll('.keymap-table')[0]
            .getBoundingClientRect().height;
        const right = document
            .querySelectorAll('.keymap-table')[1]
            .getBoundingClientRect().height;
        return left > right ? left : right;
    }

    keymapTableColumn = (type: any) => {
        return [
            {
                title: type === 'left' ? '左侧表字段' : '右侧表字段',
                dataIndex: 'key',
                key: 'key',
                width: '45%'
            },
            {
                title: '类型',
                dataIndex: 'type',
                key: 'type',
                width: '40%'
            }
        ];
    };

    prev = () => {
        const { currentStep, navToStep } = this.props;
        navToStep(currentStep - 1);
    };

    next = () => {
        const { form, editParams, keymap, currentStep } = this.props;
        const { selectedSetting } = this.state;

        if (!keymap.source.length) {
            message.error('请连接要比对的字段');
        } else {
            if (isEmpty(editParams.mappedPK)) {
                message.error('请选择逻辑主键');
                return;
            }

            Object.keys(form.getFieldsValue()).forEach((item: any) => {
                if (selectedSetting.indexOf(item) < 0) {
                    form.resetFields([item]);
                }
            });

            form.validateFields(selectedSetting, (err: any, values: any) => {
                console.log(err, values);
                if (!err) {
                    Object.keys(values).forEach((item: any) => {
                        if (item === 'matchCase' || item === 'matchNull') {
                            values[item] = true;
                        }
                    });
                    this.props.changeParams({ setting: values });
                    this.props.navToStep(currentStep + 1);
                } else {
                    message.error('请填写已选中的差异设置');
                }
            });
        }
    };

    /**
     * 同行映射连接
     */
    setRowMap = () => {
        const { rowMap, nameMap, originColumn, targetColumn } = this.state;

        if (!rowMap) {
            this.props.setRowMap({
                sourceKeyCol: originColumn.map((item: any) => item.key),
                targetKeyCol: targetColumn.map((item: any) => item.key)
            });
        } else {
            this.props.resetLinkedKeys();
            this.setState({
                selectedSource: [],
                selectedTarget: []
            });
            this.props.changeParams({ mappedPK: {} });
        }

        this.setState({
            rowMap: !rowMap,
            nameMap: nameMap ? !nameMap : nameMap
        });
    };

    /**
     * 同名映射连接
     */
    setNameMap = () => {
        const { nameMap, rowMap, originColumn, targetColumn } = this.state;
        // const { origin, target } = this.props.editParams;

        if (!nameMap) {
            this.props.setNameMap({
                sourceKeyCol: originColumn.map((item: any) => item.key),
                targetKeyCol: targetColumn.map((item: any) => item.key)
            });
        } else {
            this.props.resetLinkedKeys();
            this.setState({
                selectedSource: [],
                selectedTarget: []
            });
            this.props.changeParams({ mappedPK: {} });
        }

        this.setState({
            nameMap: !nameMap,
            rowMap: rowMap ? !rowMap : rowMap
        });
    };

    render () {
        const {
            selectedSetting,
            selectedSource,
            selectedTarget,
            H,
            originColumn,
            targetColumn,
            nameMap,
            rowMap
        } = this.state;
        const { source, target } = this.props.keymap;

        // 左侧表选择配置
        const keymapSourceSelection: any = {
            selectedRowKeys: selectedSource,
            getCheckboxProps (record: any) {
                return {
                    disabled: !source.includes(record.key)
                };
            },
            onSelect: (record: any, selected: any, selectedRow: any) => {
                let sourceCheck: any = [...selectedSource];

                let targetCheck: any = [...selectedTarget];

                let mapPk: any = {};

                if (selected) {
                    sourceCheck.push(record.key);
                    targetCheck.push(target[source.indexOf(record.key)]);
                } else {
                    sourceCheck.splice(sourceCheck.indexOf(record.key), 1);
                    targetCheck.splice(targetCheck.indexOf(record.key), 1);
                }

                this.setState({
                    selectedSource: sourceCheck,
                    selectedTarget: targetCheck
                });

                sourceCheck.forEach((s: any, index: any) => {
                    if (source.includes(s)) {
                        mapPk[s] = targetCheck[index];
                    }
                });
                this.props.changeParams({ mappedPK: mapPk });
            }
        };

        // 右侧表选择配置
        const keymapTargetSelection: any = {
            selectedRowKeys: selectedTarget,
            getCheckboxProps (record: any) {
                return {
                    disabled: !target.includes(record.key)
                };
            },
            onSelect: (record: any, selected: any, selectedRow: any) => {
                let sourceCheck: any = [...selectedSource];

                let targetCheck: any = [...selectedTarget];

                let mapPk: any = {};

                if (selected) {
                    targetCheck.push(record.key);
                    sourceCheck.push(source[target.indexOf(record.key)]);
                } else {
                    targetCheck.splice(targetCheck.indexOf(record.key), 1);
                    sourceCheck.splice(sourceCheck.indexOf(record.key), 1);
                }

                this.setState({
                    selectedSource: sourceCheck,
                    selectedTarget: targetCheck
                });

                sourceCheck.forEach((s: any, index: any) => {
                    if (source.includes(s)) {
                        mapPk[s] = targetCheck[index];
                    }
                });
                this.props.changeParams({ mappedPK: mapPk });
            }
        };

        // 差异比对选择配置
        const settingRowSelection: any = {
            selectedRowKeys: selectedSetting,
            onChange: (selectedIds: any) => {
                this.setState({
                    selectedSetting: selectedIds
                });
            }
        };

        return (
            <div>
                <div className="steps-content">
                    <p className="keymap-title">
                        提示：您要配置来源表与目标表的字段映射关系，通过连线将待同步的字段左右相连，也可以通过同行映射、同名映射批量完成映射，同时您需要添加主键
                    </p>
                    <Row className="keymap-content">
                        <Col span={3}>
                            <div className="keymap-action">
                                <Button
                                    type={rowMap ? 'primary' : null}
                                    onClick={this.setRowMap}
                                >
                                    {rowMap ? '取消同行映射' : '同行映射'}
                                </Button>
                                <br />
                                <Button
                                    type={nameMap ? 'primary' : null}
                                    onClick={this.setNameMap}
                                >
                                    {nameMap ? '取消同名映射' : '同名映射'}
                                </Button>
                            </div>
                        </Col>
                        <Col span={6}>
                            <Table
                                className="keymap-table m-table table-select-text-key"
                                rowKey={(record: any) => record.key}
                                columns={this.keymapTableColumn('left')}
                                dataSource={originColumn}
                                rowSelection={keymapSourceSelection}
                                pagination={false}
                            />
                        </Col>

                        <Col span={8} className="canvas-content">
                            <svg
                                ref={(el: any) => (this.canvas = el)}
                                width="100%"
                                height={H}
                                className="keymap-svg"
                            >
                                <defs>
                                    <marker
                                        id="arrow"
                                        markerUnits="strokeWidth"
                                        markerWidth="12"
                                        markerHeight="12"
                                        viewBox="0 0 12 12"
                                        refX="6"
                                        refY="6"
                                        orient="auto"
                                    >
                                        <path
                                            d="M2,3 L9,6 L2,9 L2,3"
                                            style={{ fill: '#2491F7' }}
                                        />
                                    </marker>
                                </defs>
                                <g>
                                    <line
                                        id="activeLine"
                                        x1="-10"
                                        y1="-10"
                                        x2="-10"
                                        y2="-10"
                                        stroke="#2491F7"
                                        strokeWidth="2"
                                        markerEnd="url(#arrow)"
                                    />
                                </g>
                            </svg>
                        </Col>

                        <Col span={6}>
                            <Table
                                className="keymap-table m-table table-select-text-key"
                                rowKey={(record: any) => record.key}
                                columns={this.keymapTableColumn('right')}
                                dataSource={targetColumn}
                                rowSelection={keymapTargetSelection}
                                pagination={false}
                            />
                        </Col>
                    </Row>

                    <Row className="keymap-content">
                        <Col offset={2} span={20}>
                            <Table
                                className="m-table setting-table"
                                showHeader={false}
                                rowKey={(record: any) => record.setting}
                                columns={this.initDiverseSetting()}
                                dataSource={this.initDiverseData()}
                                rowSelection={settingRowSelection}
                                pagination={false}
                            />
                        </Col>
                    </Row>
                </div>

                <div className="steps-action">
                    <Button onClick={this.prev}>上一步</Button>
                    <Button
                        className="m-l-8"
                        type="primary"
                        onClick={this.next}
                    >
                        下一步
                    </Button>
                </div>
            </div>
        );
    }
}
export default Form.create<any>()(StepThree);
