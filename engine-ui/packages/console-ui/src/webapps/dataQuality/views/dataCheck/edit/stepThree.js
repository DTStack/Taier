import React, { Component } from 'react';
import { Table, Checkbox, TimePicker, Form, InputNumber, Button, Switch, Select, message, Row, Col } from 'antd';
import { select, selectAll, mouse } from 'd3-selection';
import { connect } from 'react-redux';
import { keyMapActions } from '../../../actions/dataSource/keyMapActions';
import API from '../../../api/dataSource';

const FormItem = Form.Item;

const mapState = state => {
    const { keymap } = state;
    return { keymap };
};

@connect(mapState, keyMapActions)
export default class StepThree extends Component {
    constructor(props) {
        super(props);
        this.state = {
            selectedIds: [],
            selectedRows: [],
            selectedIds1: [],
            selectedRows1: [],
            diffRule: [],
            h: 40,
            w: 230,
            W: 450,
            H: 0,
            padding: 10,
            originColumn: [],
            targetColumn: []
        }
    }

    componentDidMount() {
        this.initTableData()
        this.$canvas = select(this.canvas);
        this.$activeLine = select('#activeLine');
        // this.setState({
        //     W: this.getCanvasW1(),
        //     H: this.getCanvasH()
        // })
        this.drawSvg();
        this.listenResize();
        // this.loadColumnFamily();
        this.initData();
        console.log(this,'this')
    }

    componentDidUpdate() {
        this.$canvas.selectAll('.dl, .dr, .lines').remove();
        this.drawSvg();
        
    }

	componentWillReceiveProps(nextProps) {
		if (nextProps.keymap.source.length != this.props.keymap.source.length) {
			const { origin, target } = this.props.dataCheck.params;
			this.props.changeParams({
	            origin: { ...origin, column: nextProps.keymap.source },
	            target: { ...target, column: nextProps.keymap.target }
	        });
		}
	}

    componentWillUnmount() {
        window.removeEventListener('resize', this.resize, false);
    }

    listenResize() {
        if(window.addEventListener) {
            window.addEventListener('resize', this.resize, false);
        }
    }

    resize = () => {
        this.setState({
            W: this.getCanvasW1(),
            H: this.getCanvasH()
        });
    }

	initData = () => {
		const { origin, target } = this.props.dataCheck.params;

		API.getDataSourcesColumn({
			sourceId: origin.dataSourceId,
			tableName: origin.table
		}).then((res) => {
			if (res.code === 1) {
				this.setState({
					originColumn: res.data
				});
				this.resize();
			}
		});

		API.getDataSourcesColumn({
			sourceId: target.dataSourceId,
			tableName: target.table
		}).then((res) => {
			if (res.code === 1) {
				this.setState({
					targetColumn: res.data
				});
			}
		});

	}

    drawSvg() {
        this.renderDags();
        this.renderLines();
        this.bindEvents();
    }

    renderDags() {
        const { w, h, W, padding, originColumn, targetColumn } = this.state;

        this.$canvas.append('g')
            .attr('class', 'dl')
            .selectAll('g')
            .data(originColumn)
            .enter()
            .append('g')
            .attr('class', 'col-dag-l')
            .append('circle')
            .attr('class', 'dag-circle')
            .attr('cx', (d, i) => 15)
            .attr('cy', (d, i) => h * (i + 1.5))
            .attr('r', 5)
            .attr('stroke-width', 2)
            .attr('stroke', '#fff')
            .attr('fill', 'rgba(0, 157, 126, 0.5)');

        this.$canvas.append('g')
            .attr('class', 'dr')
            .selectAll('g')
            .data(targetColumn)
            .enter()
            .append('g')
            .attr('class', 'col-dag-r')
            .append('circle')
            .attr('class', 'dag-circle')
            .attr('cx', (d, i) => { return W - 15 })
            .attr('cy', (d, i) => h * (i + 1.5))
            .attr('r', 5)
            .attr('stroke-width', 2)
            .attr('stroke', '#fff')
            .attr('fill', 'rgba(0, 157, 126, 0.5)');
    }

    renderLines() {
        const { w, h, W, padding } = this.state;
        const { source, target } = this.props.keymap;
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
            isMatch = columnItem.key === keymapItem;
            
            return isMatch;
        };

        const matchKeymapToColumn_t = (columnItem, keymapItem) => {
            let isMatch = false;
            isMatch = columnItem.key === keymapItem;
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
                    sx = 15;
                    sy = (i + 1.5) * h;

                    $dagR.each((dr, j) => {
                        /**
                         * target[ii] 类型：
                         * if(targetSrcType === 1, 2, 3) string
                         * if( === 6)  obj{ key, type }
                         */
                        if(matchKeymapToColumn_t(dr, target[ii])) {
                            ex = W - 15;
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
            .attr("stroke","rgba(0, 157, 126, 0.5)")
            .attr("stroke-width", 2)
            .attr("marker-end","url(#arrow)");
    }

    bindEvents() {
        const { w, h, W, padding } = this.state;
        const { addLinkedKeys, delLinkedKeys } = this.props;

        const $line = this.$activeLine;
        const $dagL = selectAll('.col-dag-l');

        let isMouseDown = false;
        let sourceKey_obj, targetKey_obj;

        $dagL.on('mousedown', (d, i, nodes) => {
        	console.log(d,i,nodes)
            sourceKey_obj = d;
            isMouseDown = true;

            let sx = 15, sy = (i + 1.5) * h;
            $line.attr('x1', sx)
                .attr('y1', sy)
                .attr('x2', sx)
                .attr('y2', sy);
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
                const threholdX = W - 15;

                if (ex < threholdX) {
                	this.resetActiveLine();
                } else {
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
                    source: sourceKey_obj.key,
                    target: targetKey_obj.key
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
                    .attr('stroke', 'rgba(0, 157, 126, 0.8)')
            })
            .on('mouseout', (d, i, nodes) => {
                select(nodes[i])
                    .select('line')
                    .attr('stroke-width', 2)
                    .attr('stroke', 'rgba(0, 157, 126, 0.5)')
            })
            .on('click', (d, i, nodes) => {
                delLinkedKeys({
                    source: d.dl,
                    target: d.dr,
                });
            });
    }

    resetActiveLine() {
        this.$activeLine.attr('x1', -10)
            .attr('y1', -10)
            .attr('x2', -10)
            .attr('y2', -10);
    }

    initColumns = () => {
        return [
            {
                title: '差异特征',
                dataIndex: 'setting',
                key: 'setting',
                render: (text, record) => {
                    return this.getSettingItem(text);
                },
                width: '95%',
            }, 
        ]
    }

    initTableData = () => {
        let diffRule = [
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
        this.setState({ diffRule });
    }

    getSettingItem = (key) => {
        const { getFieldDecorator } = this.props.form;
        switch (key) {
            case 'diverseNum':
                return (
                    <div>
                        记录数差异，对比左右表的总记录数，差距小于
                        {
                            getFieldDecorator('diverseNum', {
                                rules: [{ required: true, message: '不能为空' }],
                            })(
                                <InputNumber min={0} step={1} />
                            )
                        }
                        %时候，计为成功匹配
                    </div>
                )
                break;
            case 'diverseRatio':
                return (
                    <div>
                        数值差异百分比，对比左右表的数值型数据时，差距百分比小于
                        {
                            getFieldDecorator('diverseRatio', {
                                rules: [{ required: true, message: '不能为空' }],
                            })(
                                <InputNumber min={0} max={100} step={1} />
                            )
                        }
                        %时候，计为成功匹配
                    </div>
                )
                break;
            case 'diverseAbsolute':
                return (
                    <div>
                        数值差异绝对值，对比左右表的数值型数据时，差距绝对值小于
                        {
                            getFieldDecorator('diverseAbsolute', {
                                rules: [{ required: true, message: '不能为空' }],
                            })(
                                <InputNumber min={0} step={1} />
                            )
                        }
                        时候，计为成功匹配
                    </div>
                )
                break;
            case 'decimalRetain':
                return (
                    <div>
                        数值对比忽略小数点，忽略小数点后
                        {
                            getFieldDecorator('decimalRetain', {
                                rules: [{ required: true, message: '不能为空' }],
                            })(
                                <InputNumber min={0} step={1} />
                            )
                        }
                        位
                    </div>
                )
                break;
            case 'matchCase':
                return (
                    <p>字符不区分大小写，对比左右表的字符串型数据时，不区分大小写</p>
                )
                break;
            case 'matchNull':
                return (
                    <p>空值与NULL等价，对比左右表的数据时，认为空值与NULL值是相等的</p>
                )
                break;
            default:
                break;
        }
    }

	scrollText = (value) => {
	    return <input className="cell-input" defaultValue={value} />
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

    getCanvasW1() {
        const canvas = document.querySelector('.m-keymapcanvas')
        const newW = canvas.getBoundingClientRect().width;
        console.log(newW)
        return newW
    }

    getCanvasH() {
        const leftTable = document.querySelector('.keymap-table')
        const newH = leftTable.getBoundingClientRect().height;
        console.log(newH)
        return newH
    }

	initTableColumns = () => {
		return [
            {
                title: '字段名称',
                dataIndex: 'key',
                key: 'key',
                width: '50%'
            }, 
            {
            	title: '类型',
                dataIndex: 'type',
                key: 'type',
                width: '50%'
            }
        ]
	}

    prev = () => {
        this.props.navToStep(1);
    }

    next = () => {
    	const { params } = this.props.dataCheck;
    	const { keymap } = this.props;
    	const { selectedIds } = this.state;

    	if (!keymap.source.length) {
    		message.error('请连接要比对的字段')
    		return;
    	} else {
    		if (selectedIds.length) {

		        this.props.form.validateFields(selectedIds, (err, values) => {
		            console.log(err,values)
		            if(!err) {
		                this.props.changeParams({
		                	setting: { ...params.setting, ...values }
		                });
		                this.props.navToStep(3);
		            } else {
		            	message.error('请填写已选中的差异设置');
		            }
		        });
    		} else {
    			message.error('请填写差异设置');
    		}
    	}

    }

    render() {
        const { selectedIds, diffRule, w, h, W, H, padding, originColumn, targetColumn } = this.state;

        const keymapSelection = {
            selectedRowKeys: selectedIds,
            onChange: (selectedIds, selectedRows) => {
            	console.log(selectedIds,selectedRows)
                this.setState({
                    selectedIds1: selectedIds,
                    selectedRows1: selectedRows
                });
            }
        };

        const settingRowSelection = {
            selectedRowKeys: selectedIds,
            onChange: (selectedIds, selectedRows) => {
            	let abb = {}
            	selectedIds.forEach((item) => {
            		abb[item] = this.props.form.getFieldValue(item)
            	});
            	console.log(abb)
          //   	this.props.changeParams({
		        //     setting: {

		        //     }
		        // });
            	console.log(selectedIds,selectedRows)
                this.setState({
                    selectedIds: selectedIds,
                    selectedRows: selectedRows
                });
            }
        };

        return (
        	<div>
                <div className="steps-content">
                    <p style={{ fontSize: 14, color: '#ccc', textAlign: 'center' }}>
		                您要配置来源表与目标表的字段映射关系，通过连线将待同步的字段左右相连，也可以通过同行映射、同名映射批量完成映射
		            </p>
		            <Row>
		                <Col offset={2} span={6}>
		                	<Table
				                className="keymap-table m-table"
				                bordered
				                columns={this.initTableColumns()}
				                rowSelection={keymapSelection}
				                rowKey={record => record.key}
				                dataSource={originColumn}
				                pagination={false}
				            />
				        </Col>
				        <Col span={8} style={{ margin: '0 -15px', zIndex: 99 }}>
				            <svg
	                            ref={ el => this.canvas = el }
	                            width='100%'
	                            height={ H }
	                            className="pa m-keymapcanvas"
	                            // style={{ left: w , top: padding, margin: '0 -15px' }}
	                        >
	                            <defs>
	                                <marker id="arrow" markerUnits="strokeWidth" markerWidth="12" markerHeight="12" viewBox="0 0 12 12" refX="6" refY="6" orient="auto" >
	                                    <path d="M2,3 L9,6 L2,9 L2,6 L2,3" style={{ fill: 'rgba(0, 157, 126, 0.5)' }}></path>
	                                </marker>
	                            </defs>
	                            <g>
	                                <line id="activeLine"
	                                    x1="-10" y1="-10" x2="-10" y2="-10"
	                                    stroke="rgba(0, 157, 126, 0.5)"
	                                    strokeWidth="2"
	                                    markerEnd="url(#arrow)"
	                                />
	                            </g>
	                        </svg>
	                    </Col>
	                   	<Col span={6}>
	                        <Table
				                className="keymap-table m-table"
				                bordered
				                columns={this.initTableColumns()}
				                rowSelection={keymapSelection}
				                rowKey={record => record.key}
				                dataSource={targetColumn}
				                pagination={false}
				            />
		                </Col>
		                
		            </Row>
		            <div className="txt-center">
	                    <Button>
	                    	同行映射
	                    </Button>
	                    <Button>
	                    	同名映射
	                    </Button>
		            </div>
		            <Table
		                className="m-table diffrule-table"
		                showHeader={false}
		                columns={this.initColumns()}
		                rowSelection={settingRowSelection}
		                rowKey={record => record.setting}
		                dataSource={diffRule}
		                pagination={false}
		            />
	           </div>
                <div className="steps-action">
                    <Button onClick={this.prev}>上一步</Button>
                    <Button className="m-l-8" type="primary" onClick={this.next}>下一步</Button>
                </div>
            </div>
        
        )
    }
}
StepThree = Form.create()(StepThree);