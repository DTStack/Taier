import React, { Component } from 'react';
import { connect } from 'react-redux';
import { select, selectAll, mouse } from 'd3-selection';
import { isEmpty, isEqual } from 'lodash';
import { Table, Form, InputNumber, Button, message, Row, Col } from 'antd';

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
            selectedSetting: [],
            selectedSource: [],
            selectedTarget: [],
            diverseData: [],
            h: 40,
            w: 230,
            W: 450,
            H: 0,
            padding: 10,
            originColumn: [],
            targetColumn: [],
            rowMap: false,
            nameMap: false
        }
    }

    componentDidMount() {
        this.initDiverseData();
        this.$canvas = select(this.canvas);
        this.$activeLine = select('#activeLine');
        // this.setState({
        //     W: this.getCanvasW(),
        //     H: this.getCanvasH()
        // })
        // this.loadColumnFamily();
        this.drawSvg();
        this.listenResize();
        this.getKeyMapColumnData();
        this.setEditKeymapData();
        this.setKeymapCheck();
        this.setSettingTableFields();
        console.log(this,'this')
    }

    componentDidUpdate() {
        this.$canvas.selectAll('.dl, .dr, .lines').remove();
        this.drawSvg();
        
    }

	componentWillReceiveProps(nextProps) {
		let newKeymap = nextProps.keymap,
			oldKeymap = this.props.keymap;

		// keymap有改变则改变params
		if (newKeymap.source.length != oldKeymap.source.length) {
			const { origin, target } = this.props.editParams;
			this.props.changeParams({
	            origin: { ...origin, column: newKeymap.source },
	            target: { ...target, column: newKeymap.target }
	        });
		}
	}

    componentWillUnmount() {
        window.removeEventListener('resize', this.resize, false);
        this.props.resetLinkedKeys();
    }

	setEditKeymapData = () => {
		const { origin, target } = this.props.editParams;
		if (origin.column && target.column) {
			this.props.setEditMap({
                source: origin.column,
                target: target.column
            });
		}
	}

	setSettingTableFields = () => {
		const { setting } = this.props.editParams;
		const { form } = this.props;

		if (!isEmpty(setting)) {
			let selectedSetting = [],
				fieldsValue = {};

			Object.keys(setting).forEach((item) => {
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
	}

    setKeymapCheck = () => {
    	const { mappedPK } = this.props.editParams;

    	// 填充keymap的checkbox
		if (mappedPK) {
			let sourceCheck = [],
				targetCheck = [];

			for (let [key, value] of Object.entries(mappedPK)) {
				sourceCheck.push(key);
				targetCheck.push(value);
			}

			this.setState({
  				selectedSource: sourceCheck,
  				selectedTarget: targetCheck
  			});
		} 
    }

    listenResize() {
        if (window.addEventListener) {
            window.addEventListener('resize', this.resize, false);
        }
    }

    resize = () => {
        this.setState({
            W: this.getCanvasW(),
            H: this.getCanvasH()
        });
    }

	getKeyMapColumnData = () => {
		const { origin, target } = this.props.editParams;
		const { setEditMap } = this.props;

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
				this.resize();
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
            .attr('fill', '#2491F7');

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
            .attr('fill', '#2491F7');
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
         * if (sourceSrcType === 1, 2, 3) string
         * if ( === 6) { index, type }
         */
        source.forEach((key_obj, ii) => {
            $dagL.each((dl, i) => {
                let sx, sy, ex, ey;

                if (matchKeymapToColumn_s(dl, key_obj)) {
                    sx = 15;
                    sy = (i + 1.5) * h;

                    $dagR.each((dr, j) => {
                        /**
                         * target[ii] 类型：
                         * if (targetSrcType === 1, 2, 3) string
                         * if ( === 6)  obj{ key, type }
                         */
                        if (matchKeymapToColumn_t(dr, target[ii])) {
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
            .attr("stroke","#2491F7")
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
            sourceKey_obj = d;
            isMouseDown = true;

            let sx = 15, sy = (i + 1.5) * h;
            $line.attr('x1', sx)
                .attr('y1', sy)
                .attr('x2', sx)
                .attr('y2', sy);
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
                const threholdX = W - 15;

                if (ex < threholdX) {
                	this.resetActiveLine();
                } else {
                    const tidx = Math.floor(ey / h) - 1;
                    const $dagR = selectAll('.col-dag-r');

                    $dagR.each((d, i) => {
                        if (i === tidx) {
                            targetKey_obj = d;
                        }
                    });
                }
            }

            if (sourceKey_obj && targetKey_obj) {
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
                });
            });
    }

    resetActiveLine() {
        this.$activeLine.attr('x1', -10)
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
                render: (text, record) => {
                    return this.getSettingItem(text);
                },
                width: '95%',
            }, 
        ]
    }

    initDiverseData = () => {
        let diverseData = [
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
        this.setState({ diverseData });
    }



    getSettingItem = (key) => {
    	const { getFieldDecorator } = this.props.form;
    	const { setting } = this.props.editParams;
        switch (key) {
            case 'diverseNum':
                return (
                    <div>
                        记录数差异，对比左右表的总记录数，差距小于
                        {
                            getFieldDecorator('diverseNum', {
                                rules: [{ required: true, message: '不能为空' }],
                                initialValue: setting.diverseNum
                            })(
                                <InputNumber min={1} step={1} />
                            )
                        }
                        %时候，计为成功匹配
                    </div>
                )
            case 'diverseRatio':
                return (
                    <div>
                        数值差异百分比，对比左右表的数值型数据时，差距百分比小于
                        {
                            getFieldDecorator('diverseRatio', {
                                rules: [{ required: true, message: '不能为空' }],
                                initialValue: setting.diverseRatio
                            })(
                                <InputNumber min={1} step={1} />
                            )
                        }
                        %时候，计为成功匹配
                    </div>
                )
            case 'diverseAbsolute':
                return (
                    <div>
                        数值差异绝对值，对比左右表的数值型数据时，差距绝对值小于
                        {
                            getFieldDecorator('diverseAbsolute', {
                                rules: [{ required: true, message: '不能为空' }],
                                initialValue: setting.diverseAbsolute
                            })(
                                <InputNumber min={1} step={1} />
                            )
                        }
                        时候，计为成功匹配
                    </div>
                )
            case 'decimalRetain':
                return (
                    <div>
                        数值对比忽略小数点，忽略小数点后
                        {
                            getFieldDecorator('decimalRetain', {
                                rules: [{ required: true, message: '不能为空' }],
                                initialValue: setting.decimalRetain
                            })(
                                <InputNumber min={1} step={1} />
                            )
                        }
                        位
                    </div>
                )
            case 'matchCase':
                return (
                    <p>字符不区分大小写，对比左右表的字符串型数据时，不区分大小写</p>
                )
            case 'matchNull':
                return (
                    <p>空值与NULL等价，对比左右表的数据时，认为空值与NULL值是相等的</p>
                )
            default:
                break;
        }
    }

	scrollText = (value) => {
	    return <input className="cell-input" defaultValue={value} />
	}

    getCanvasW() {
        return document.querySelector('.keymap-svg').getBoundingClientRect().width;
    }

    getCanvasH() {
        const left = document.querySelectorAll('.keymap-table')[0].getBoundingClientRect().height
        const right = document.querySelectorAll('.keymap-table')[1].getBoundingClientRect().height
        return left > right ? left : right;
    }

	initLeftTable = () => {
		return [
            {
                title: '左侧表字段',
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
        ]
	}

    initRightTable = () => {
        return [
            {
                title: '右侧表字段',
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
        ]
    }

    prev = () => {
        const { currentStep, navToStep } = this.props;
        navToStep(currentStep - 1);
    }

    next = () => {
    	const { editParams, keymap, currentStep, navToStep, form, changeParams } = this.props;
    	const { selectedSetting } = this.state;

    	if (!keymap.source.length) {
    		message.error('请连接要比对的字段');
    		return
    	} else {

    		if (!editParams.mappedPK) {
	    		message.error('请选择逻辑主键');
	    		return
	    	}

	        form.validateFields(selectedSetting, (err, values) => {
	            if (!err) {
	            	Object.keys(values).forEach((item) => {
	            		if (item === 'matchCase' || item === 'matchNull') {
	            			values[item] = true;
	            		}
	            	});
	                changeParams({ setting: values });
	                navToStep(currentStep + 1);
	            } else {
	            	message.error('请填写已选中的差异设置');
	            }
	        });
    	}
    }

    // 同行映射连接
	setRowMap = () => {
		const { rowMap, nameMap, originColumn, targetColumn } = this.state;
		const { origin, target } = this.props.editParams;

        const convertColumn2Keymap = (column) => {
            column = column.map(o => o.key);
            return column;
        }

    	if (!rowMap) {
            this.props.setRowMap({
                sourceCol: convertColumn2Keymap(originColumn),
                targetCol: convertColumn2Keymap(targetColumn)
            });
        } else {
            this.props.resetLinkedKeys();
        }

    	this.setState({
            rowMap: !rowMap,
            nameMap: nameMap ? !nameMap : nameMap
        });
    }

    // 同名映射连接
    setNameMap = () => {
        const { nameMap, rowMap, originColumn, targetColumn } = this.state;
        const { origin, target } = this.props.editParams;

        if (!nameMap) {
            this.props.setNameMap({
                sourceCol: originColumn,
                targetCol: targetColumn
            });
        } else {
            this.props.resetLinkedKeys();
        }

        this.setState({
            nameMap: !nameMap,
            rowMap: rowMap ? !rowMap : rowMap
        });
    }

    render() {
        const { selectedSetting, selectedSource, selectedTarget, diverseData, w, h, W, H, padding, originColumn, targetColumn } = this.state;
        const { source, target } = this.props.keymap;
        const { mappedPK } = this.props.editParams;

        const keymapSourceSelection = {
            selectedRowKeys: selectedSource,
        	getCheckboxProps(record) {
			    return {
			      	disabled: !source.includes(record.key)
			    };
		  	},
		  	onSelect: (record, selected, selectedRow) => {
		  		let sourceCheck = [...selectedSource];
		  		let targetCheck = [...selectedTarget];
	  			let mapPk = {};

		  		if (selected) {
		  			sourceCheck.push(record.key);
	  				targetCheck.push(target[source.indexOf(record.key)]);

		  			this.setState({
		  				selectedSource: sourceCheck,
		  				selectedTarget: targetCheck
		  			});
		  		} else {
		  			sourceCheck.splice(sourceCheck.indexOf(record.key), 1);
		  			targetCheck.splice(targetCheck.indexOf(record.key), 1);

		  			this.setState({
		  				selectedTarget: targetCheck,
		  				selectedSource: sourceCheck,
		  			});
		  		}

		  		// mapperPk
		  		sourceCheck.forEach((s, index) => {
	  				mapPk[s] = targetCheck[index]
	  			});
	  			this.props.changeParams({
	  				mappedPK: mapPk
	  			});
		  	}
        };

        const keymapTargetSelection = {
            selectedRowKeys: selectedTarget,
            getCheckboxProps(record) {
			    return {
			      	disabled: !target.includes(record.key)
			    };
		  	},
		  	onSelect: (record, selected,selectedRow) => {
		  		let sourceCheck = [...selectedSource];
		  		let targetCheck = [...selectedTarget];
		  		let mapPk = {};

		  		if (selected) {
		  			targetCheck.push(record.key);
		  			sourceCheck.push(source[target.indexOf(record.key)]);

		  			this.setState({
		  				selectedSource: sourceCheck,
		  				selectedTarget: targetCheck
		  			});
		  		} else {
		  			targetCheck.splice(targetCheck.indexOf(record.key), 1);
		  			sourceCheck.splice(sourceCheck.indexOf(record.key), 1);

		  			this.setState({
		  				selectedSource: sourceCheck,
		  				selectedTarget: targetCheck
		  			});
		  		}

		  		// mapperPk
		  		sourceCheck.forEach((s, index) => {
	  				mapPk[s] = targetCheck[index]
	  			});

	  			this.props.changeParams({
	  				mappedPK: mapPk
	  			});
		  	}
        };

        const settingRowSelection = {
            selectedRowKeys: selectedSetting,
            onChange: (selectedIds) => {
                this.setState({
                    selectedSetting: selectedIds
                });
            }
        };

        return (
        	<div>
                <div className="steps-content">
                    <p className="keymap-title">
		                您要配置来源表与目标表的字段映射关系，通过连线将待同步的字段左右相连，也可以通过同行映射、同名映射批量完成映射
		            </p>

		            <Row className="keymap-content">
		                <Col offset={2} span={6}>
		                	<Table
				                className="keymap-table m-table"
				                columns={this.initLeftTable()}
				                rowSelection={keymapSourceSelection}
				                rowKey={record => record.key}
				                dataSource={originColumn}
				                pagination={false}
				            />
				        </Col>

				        <Col span={8} className="canvas-content">
				            <svg
	                            ref={ el => this.canvas = el }
	                            width='100%'
	                            height={ H }
	                            className="keymap-svg"
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
	                    </Col>

	                   	<Col span={6}>
	                        <Table
				                className="keymap-table m-table"
				                columns={this.initRightTable()}
				                rowSelection={keymapTargetSelection}
				                rowKey={record => record.key}
				                dataSource={targetColumn}
				                pagination={false}
				            />
		                </Col>

	                	<Col span={2}>
		                	<div className="keymap-action">
		                        <Button
		                            type={this.state.rowMap ? 'primary' : 'default'}
		                            onClick={this.setRowMap}
		                        >
		                        	{this.state.rowMap ? '取消同行映射' : '同行映射'}
		                        </Button>
		                        <br />
		                        <Button
		                            type={this.state.nameMap ? 'primary' : 'default'}
		                            onClick={this.setNameMap}
		                        >
		                        	{ this.state.nameMap ? '取消同名映射' : '同名映射' }
		                        </Button>
	                        </div>
		                </Col>
		            </Row>

		            <Row className="keymap-content">
		                <Col offset={2} span={20}>
				            <Table
				                className="m-table setting-table"
				                showHeader={false}
				                columns={this.initDiverseSetting()}
				                rowSelection={settingRowSelection}
				                rowKey={record => record.setting}
				                dataSource={diverseData}
				                pagination={false}
				            />
		                </Col>
		             </Row>
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