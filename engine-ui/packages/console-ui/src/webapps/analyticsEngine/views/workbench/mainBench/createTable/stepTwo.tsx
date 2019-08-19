import * as React from 'react';
import { Input, Table, Select, Icon, Button, Row, Checkbox } from 'antd';
import HelpDoc, { relativeStyle } from '../../../../components/helpDoc';

const Option = Select.Option;

// const columns: any = [];
// const partitions: any = [];
const fieldType: any = [
    {
        name: 'SMALLINT',
        value: 'SMALLINT'
    },
    {
        name: 'INT/INTEGER',
        value: 'INT'
    },
    {
        name: 'BIGINT',
        value: 'BIGINT'
    },
    {
        name: 'DOUBLE',
        value: 'DOUBLE'
    },
    {
        name: 'TIMESTAMP',
        value: 'TIMESTAMP'
    },
    {
        name: 'DATE',
        value: 'DATE'
    },
    {
        name: 'STRING',
        value: 'STRING'
    },
    {
        name: 'BOOLEAN',
        value: 'BOOLEAN'
    },
    {
        name: 'DECIMAL',
        value: 'DECIMAL'
    }
];
const partitionMode: any = [
    {
        name: '标准',
        value: 0
    },
    {
        name: 'Hash',
        value: 1
    },
    {
        name: 'Range',
        value: 2
    },
    {
        name: 'List',
        value: 3
    }
];
// string、char、varchar、timestamp、date
const bucketType: any = ['STRING', 'CHAR', 'VARCHAR', 'TIMESTAMP', 'DATE'];
const sortColumn: any = [
    'STRING',
    'DATE',
    'TIMESTAMP',
    'SMALLINT',
    'INT',
    'BIGINT',
    'BOOLEAN',
    'CHAR',
    'VARCHAR'
];
const canotBeInvert: any = ['SMALLINT', 'INT', 'BIGINT', 'DOUBLE', 'DECIMAL'];
const decimalPrecision: any = [
    1,
    2,
    3,
    4,
    5,
    6,
    7,
    8,
    9,
    10,
    11,
    12,
    13,
    14,
    15,
    16,
    17,
    18,
    19,
    20,
    21,
    22,
    23,
    24,
    25,
    26,
    27,
    28,
    29,
    30,
    31,
    32,
    33,
    34,
    35,
    36,
    37,
    38
];
const decimalScale: any = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9];
export default class StepTwo extends React.Component<any, any> {
    constructor (props: any) {
        super(props);
        this.state = {
            columns: [],
            partitions: {},
            bucketInfo: {}
        };
    }

    componentDidMount () {
        const {
            columns = [],
            partitions,
            bucketInfo
        } = this.props.tabData.tableItem;

        this.setState({
            columns: columns,
            partitions: partitions,
            bucketInfo: bucketInfo
        });
    }
    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps(nextProps: any) {
        const {
            columns = [],
            partitions,
            bucketInfo
        } = nextProps.tabData.tableItem;
        // bucketInfo.bucketNumber = bucketInfo.bucketNumber;

        this.setState({
            columns: columns,
            partitions: partitions,
            bucketInfo: bucketInfo
        });
    }
    // eslint-disable-next-line
    UNSAFE_componentWillUpdate() {
        console.log(this.state.columns);
        console.log(this.state.partitions);
    }

    next = () => {
        this.props.handleNextStep();
    };

    addNewLine = (flag: any) => {
        let { columns, partitions, bucketInfo } = this.state;
        let _fid = 0;
        if (flag === 1) {
            columns.map((o: any) => {
                if (o._fid > _fid) {
                    _fid = o._fid;
                }
            });
            columns[columns.length] = {
                _fid: _fid + 1,
                name: '',
                type: '',
                invert: 1,
                dictionary: 0,
                sortColumn: 0,
                comment: ''
            };
            this.setState({
                columns: columns
            });
        } else if (flag === 2) {
            partitions.columns.map((o: any) => {
                if (o._fid > _fid) {
                    _fid = o._fid;
                }
            });
            partitions.columns[partitions.columns.length] = {
                _fid: _fid + 1,
                name: '',
                type: '',
                comment: ''
            };
            this.setState({
                partitions
            });
        } else if (flag === 3) {
            bucketInfo.infos.map((o: any) => {
                if (o._fid > _fid) {
                    _fid = o._fid;
                }
            });
            bucketInfo.infos[bucketInfo.infos.length] = {
                _fid: _fid + 1,
                name: '',
                type: '',
                comment: ''
            };
            this.setState({
                bucketInfo
            });
        }
    };

    handleNameChange = (e: any, record: any) => {
        let { columns } = this.state;
        record.name = e.target.value;
        console.log(columns);
        this.saveDataToStorage();
    };
    handleSelectChange = (e: any, record: any) => {
        record.type = e;
        if (e === 'TIMESTAMP' || e === 'DATE') {
            this.handleDictionary({ target: { checked: false } }, record);
        } else if (e === 'DECIMAL' || e === 'DOUBLE') {
            this.handleSortColumn({ target: { checked: false } }, record);
        } else if (canotBeInvert.indexOf(e) !== -1) {
            this.handleInvert({ target: { checked: false } }, record);
        }
        if (bucketType.indexOf(e) > -1) {
            this.handleBucketChange({ target: { checked: false } }, record);
        }
        if (sortColumn.indexOf(e) > -1) {
            this.handleSortColumn({ target: { checked: true } }, record);
        }
        this.saveDataToStorage();
    };
    handleCommentChange = (e: any, record: any) => {
        record.comment = e.target.value;
        this.saveDataToStorage();
    };

    remove = (record: any, flag: any) => {
        let { columns, partitions, bucketInfo } = this.state;

        flag === 1
            ? columns.splice(columns.indexOf(record), 1)
            : flag === 2
                ? partitions.columns.splice(partitions.columns.indexOf(record), 1)
                : bucketInfo.infos.splice(bucketInfo.infos.indexOf(record), 1);

        this.setState({
            columns: columns,
            partitions,
            bucketInfo
        });
        this.saveDataToStorage();
    };

    move = (record: any, flag: any, type: any) => {
        // type 1上移 2下移
        // let mid: any = {};
        let { columns, partitions, bucketInfo } = this.state;
        let list =
            flag === 1
                ? columns
                : flag === 2
                    ? partitions.columns
                    : bucketInfo.infos;
        console.log(type);
        console.log(list.indexOf(record));
        console.log(list.length);

        if (
            (type === 1 && list.indexOf(record) === 0) ||
            (type === 2 && list.indexOf(record) === list.length - 1)
        ) {
            return;
        }

        let x = list.indexOf(record);
        let y =
            type === 1 ? list.indexOf(record) - 1 : list.indexOf(record) + 1;

        let midId = list[y]._fid;
        let midItem = list[x];

        list[y]._fid = -1;

        list[x] = list[y]; // fid=-1
        list[y] = midItem;

        list[x]._fid = midId;

        console.log(list);

        if (flag === 1) {
            this.setState({
                columns: list
            });
        } else if (flag === 2) {
            this.setState({
                partitions
            });
        } else {
            this.setState({
                bucketInfo
            });
        }

        this.saveDataToStorage();
    };
    /**
     * 保存输入的值
     */
    saveDataToStorage = () => {
        const { columns, partitions, bucketInfo } = this.state;
        this.props.saveNewTableData([
            {
                key: 'columns',
                value: columns
            },
            {
                key: 'partitions',
                value: partitions
            },
            {
                key: 'bucketInfo',
                value: bucketInfo
            }
        ]);
    };

    handleInvert = (e: any, record: any) => {
        record.invert = e.target.checked ? 1 : 0;
        this.saveDataToStorage();
    };

    handleDictionary = (e: any, record: any) => {
        console.log(e);
        record.dictionary = e.target.checked ? 1 : 0;
        this.saveDataToStorage();
    };

    handleSortColumn = (e: any, record: any) => {
        record.sortColumn = e.target.checked ? 1 : 0;
        this.saveDataToStorage();
    };

    handleBucketChange = (e: any, record: any) => {
        // console.log(e)
        // record.flagIndex = e;
        // const {columns} = this.state;
        // e = columns[e];
        // record.name = e.name;
        // record.type = e.type;
        record.isBucket = e.target.checked;
        this.saveDataToStorage();
    };

    handleDECIMALSelectChange = (e: any, record: any, flag: any) => {
        if (flag === 1) {
            record.precision = e;
        } else {
            record.scale = e;
        }
        this.saveDataToStorage();
    };

    handlePartitionModeChange = (e: any) => {
        console.log(e);
        let { partitions } = this.state;
        partitions.partitionType = e;
        partitions.columns = [];
        this.setState(
            {
                partitions
            },
            () => {
                if (e !== 0) {
                    partitions.columns.push({
                        _fid: 0,
                        name: '',
                        type: '',
                        comment: ''
                    });
                }
                this.setState(
                    {
                        partitions
                    },
                    () => {
                        console.log(this.state.partitions);
                        this.saveDataToStorage();
                    }
                );
            }
        );
        // this.saveDataToStorage();
    };
    handlePartitionParamChange = (e: any) => {
        let { partitions } = this.state;
        partitions.partConfig = e.target.value;
        this.saveDataToStorage();
    };
    handleBarrelDataParamCahnge = (e: any) => {
        let { bucketInfo } = this.state;
        bucketInfo.bucketNumber = e.target.value;
        // this.setState({
        //   bucketInfo
        // })
        this.saveDataToStorage();
    };
    getBucketNumber = () => {
        console.log(this.state.bucketInfo.bucketNumber);
        return this.state.bucketInfo.bucketNumber;
    };
    getTableCol = (flag: any) => {
        let col: any = [
            {
                title: '字段名',
                dataIndex: 'name',
                render: (text: any, record: any) => (
                    <Input
                        style={{ width: 159 }}
                        defaultValue={text}
                        onChange={(e: any) => this.handleNameChange(e, record)}
                    />
                )
            },
            {
                title: '字段类型',
                dataIndex: 'type',
                render: (text: any, record: any) => (
                    <Select
                        style={{ width: 159 }}
                        defaultValue={text || undefined}
                        onChange={(e: any) => this.handleSelectChange(e, record)}
                    >
                        {fieldType.map((o: any) => {
                            return (
                                <Option key={o.value} value={o.value}>
                                    {o.name}
                                </Option>
                            );
                        })}
                    </Select>
                )
            },
            {
                title: '注释',
                dataIndex: 'comment',
                render: (text: any, record: any) => (
                    <Input
                        style={{ width: 159 }}
                        defaultValue={text}
                        onChange={(e: any) => this.handleCommentChange(e, record)}
                    />
                )
            },
            {
                title: '操作',
                dataIndex: 'action',
                width: '150px',
                render: (text: any, record: any) => (
                    <span className="action-span">
                        <a
                            href="javascript:;"
                            onClick={() => this.move(record, flag, 1)}
                        >
                            上移
                        </a>
                        <span className="line" />
                        <a
                            href="javascript:;"
                            onClick={() => this.move(record, flag, 2)}
                        >
                            下移
                        </a>
                        <span className="line" />
                        <a
                            href="javascript:;"
                            onClick={() => this.remove(record, flag)}
                        >
                            删除
                        </a>
                    </span>
                )
            }
        ];

        let colNoaction: any = [
            {
                title: '字段名',
                dataIndex: 'name',
                render: (text: any, record: any) => (
                    <Input
                        style={{ width: 159 }}
                        defaultValue={text}
                        onChange={(e: any) => this.handleNameChange(e, record)}
                    />
                )
            },
            {
                title: '字段类型',
                dataIndex: 'type',
                render: (text: any, record: any) => (
                    <Select
                        style={{ width: 159 }}
                        defaultValue={text || undefined}
                        onChange={(e: any) => this.handleSelectChange(e, record)}
                    >
                        {fieldType.map((o: any) => {
                            return (
                                <Option key={o.value} value={o.value}>
                                    {o.name}
                                </Option>
                            );
                        })}
                    </Select>
                )
            },
            {
                title: '注释',
                dataIndex: 'comment',
                render: (text: any, record: any) => (
                    <Input
                        style={{ width: 159 }}
                        defaultValue={text}
                        onChange={(e: any) => this.handleCommentChange(e, record)}
                    />
                )
            }
        ];
        let colBucket: any = [
            {
                title: '字段名',
                dataIndex: 'name',
                render: (text: any, record: any) => (
                    <Select
                        defaultValue={record.flagIndex}
                        style={{ width: 159 }}
                        onChange={(e: any) => this.handleBucketChange(e, record)}
                    >
                        {this.state.columns.map((o: any) => {
                            if (o.name && bucketType.indexOf(o.type) !== -1) {
                                return (
                                    <Option
                                        key={o._fid}
                                        value={this.state.columns.indexOf(o)}
                                    >
                                        {o.name}
                                    </Option>
                                );
                            }
                        })}
                    </Select>
                )
            },
            {
                title: '字段类型',
                dataIndex: 'type',
                render: (text: any, record: any) =>
                    text ? (
                        <span
                            style={{
                                fontSize: 12,
                                width: 159,
                                display: 'block'
                            }}
                        >
                            {text}
                        </span>
                    ) : (
                        '-'
                    )
            },
            {
                title: '注释',
                dataIndex: 'comment',
                render: (text: any, record: any) => (
                    <Input
                        style={{ width: 159 }}
                        defaultValue={text}
                        onChange={(e: any) => this.handleCommentChange(e, record)}
                    />
                )
            },
            {
                title: '操作',
                dataIndex: 'action',
                width: '150px',
                render: (text: any, record: any) => (
                    <span className="action-span">
                        <a
                            href="javascript:;"
                            onClick={() => this.move(record, flag, 1)}
                        >
                            上移
                        </a>
                        <span className="line" />
                        <a
                            href="javascript:;"
                            onClick={() => this.move(record, flag, 2)}
                        >
                            下移
                        </a>
                        <span className="line" />
                        <a
                            href="javascript:;"
                            onClick={() => this.remove(record, flag)}
                        >
                            删除
                        </a>
                    </span>
                )
            }
        ];

        let colField: any = [
            {
                title: '字段名',
                dataIndex: 'name',
                render: (text: any, record: any) => (
                    <Input
                        autoFocus
                        defaultValue={text}
                        onChange={(e: any) => this.handleNameChange(e, record)}
                    />
                )
            },
            {
                title: '字段类型',
                dataIndex: 'type',
                render: (text: any, record: any) => (
                    <span>
                        <Select
                            style={{
                                width: record.type === 'DECIMAL' ? 90 : 140,
                                marginRight: 5
                            }}
                            defaultValue={text || undefined}
                            onChange={(e: any) => this.handleSelectChange(e, record)}
                        >
                            {fieldType.map((o: any) => {
                                return (
                                    <Option key={o.value} value={o.value}>
                                        {o.name}
                                    </Option>
                                );
                            })}
                        </Select>

                        {record.type === 'DECIMAL' && (
                            <span>
                                <Select
                                    style={{ width: 50, marginRight: 5 }}
                                    defaultValue={
                                        record.precision
                                            ? record.precision
                                            : undefined
                                    }
                                    onChange={(e: any) =>
                                        this.handleDECIMALSelectChange(
                                            e,
                                            record,
                                            1
                                        )
                                    }
                                >
                                    {decimalPrecision.map((o: any) => {
                                        return (
                                            <Option key={o} value={o}>
                                                {o}
                                            </Option>
                                        );
                                    })}
                                </Select>
                                <Select
                                    style={{ width: 50, marginRight: 5 }}
                                    defaultValue={
                                        record.scale ? record.scale : undefined
                                    }
                                    onChange={(e: any) =>
                                        this.handleDECIMALSelectChange(
                                            e,
                                            record,
                                            2
                                        )
                                    }
                                >
                                    {decimalScale.map((o: any) => {
                                        return (
                                            <Option key={o} value={o}>
                                                {o}
                                            </Option>
                                        );
                                    })}
                                </Select>

                                <HelpDoc
                                    style={relativeStyle}
                                    doc="decimalType"
                                />
                            </span>
                        )}
                    </span>
                )
            },
            {
                title: '倒排索引',
                dataIndex: 'invert',
                render: (text: any, record: any) => (
                    <Checkbox
                        disabled={canotBeInvert.indexOf(record.type) !== -1}
                        checked={record.invert || false}
                        onChange={(e: any) => this.handleInvert(e, record)}
                    />
                )
            },
            {
                title: '字典编码',
                dataIndex: 'dictionary',
                render: (text: any, record: any) => (
                    <Checkbox
                        disabled={
                            record.type === 'TIMESTAMP' ||
                            record.type === 'DATE'
                        }
                        checked={record.dictionary || false}
                        onChange={(e: any) => this.handleDictionary(e, record)}
                    />
                )
            },
            {
                title: '多维索引',
                dataIndex: 'sortColumn',
                render: (text: any, record: any) => (
                    <Checkbox
                        disabled={
                            record.type === 'DOUBLE' ||
                            record.type === 'DECIMAL'
                        }
                        checked={record.sortColumn || false}
                        onChange={(e: any) => this.handleSortColumn(e, record)}
                    />
                )
            },
            {
                title: '是否分桶',
                dataIndex: 'isBucket',
                render: (text: any, record: any) => (
                    <Checkbox
                        disabled={bucketType.indexOf(record.type) === -1}
                        checked={record.isBucket || false}
                        onChange={(e: any) => this.handleBucketChange(e, record)}
                    />
                )
            },
            {
                title: '注释',
                dataIndex: 'comment',
                render: (text: any, record: any) => (
                    <Input
                        style={{ width: 159 }}
                        defaultValue={text}
                        onChange={(e: any) => this.handleCommentChange(e, record)}
                    />
                )
            },
            {
                title: '操作',
                dataIndex: 'action',
                width: '150px',
                render: (text: any, record: any) => (
                    <span className="action-span">
                        <a
                            href="javascript:;"
                            onClick={() => this.move(record, flag, 1)}
                        >
                            上移
                        </a>
                        <span className="line" />
                        <a
                            href="javascript:;"
                            onClick={() => this.move(record, flag, 2)}
                        >
                            下移
                        </a>
                        <span className="line" />
                        <a
                            href="javascript:;"
                            onClick={() => this.remove(record, flag)}
                        >
                            删除
                        </a>
                    </span>
                )
            }
        ];

        return flag === 1
            ? colField
            : flag === 2
                ? col
                : flag === 4
                    ? colBucket
                    : colNoaction;
    };

    render () {
        const { columns, partitions, bucketInfo } = this.state;
        console.log(columns);
        return (
            <Row className="step-two-container step-container" id="table-panel" {...{ id: 'table-panel' }}>
                <div className="table-panel" id="field_panel">
                    <span className="title">字段信息</span>
                    <Table
                        columns={this.getTableCol(1)}
                        dataSource={columns}
                        rowKey="_fid"
                        pagination={false}
                        size="small"
                    />
                    <a
                        className="btn"
                        href="javascript:;"
                        onClick={() => this.addNewLine(1)}
                    >
                        <Icon className="icon" type="plus-circle-o" />
                        添加字段
                    </a>
                </div>
                <div
                    className="table-panel"
                    style={{ marginBottom: 40 }}
                    id="parti_panel"
                >
                    <div className="area-title-container">
                        <span className="title">分区信息</span>
                    </div>
                    <div style={{ marginBottom: 10 }}>
                        <span>分区模式：</span>
                        <Select
                            getPopupContainer={(e: any) => e.parentNode}
                            style={{ width: 150 }}
                            value={partitions.partitionType}
                            onChange={this.handlePartitionModeChange}
                        >
                            {partitionMode.map((o: any) => {
                                return (
                                    <Option key={o.value} value={o.value}>
                                        {o.name}
                                    </Option>
                                );
                            })}
                        </Select>
                        <HelpDoc
                            style={{ ...relativeStyle, marginLeft: 5 }}
                            doc="partitionTip"
                        />
                    </div>
                    {partitions.partitionType === 1 ? (
                        <div
                            className="partitionParam-box"
                            style={{ marginBottom: 10 }}
                        >
                            <span>分区数量：</span>
                            <Input
                                defaultValue={partitions.partConfig}
                                style={{ width: 200, marginRight: 4 }}
                                placeholder="1-1000之间的正整数"
                                onChange={this.handlePartitionParamChange}
                            />
                            个
                        </div>
                    ) : partitions.partitionType === 2 ? (
                        <div
                            className="partitionParam-box"
                            style={{ marginBottom: 10, display: 'flex' }}
                        >
                            <span>范围：</span>
                            <Input.TextArea
                                defaultValue={partitions.partConfig}
                                style={{ height: 50, width: 300 }}
                                placeholder="多个范围之间用英文逗号间隔"
                                onChange={this.handlePartitionParamChange}
                            />
                        </div>
                    ) : (
                        partitions.partitionType === 3 && (
                            <div
                                className="partitionParam-box"
                                style={{ marginBottom: 10, display: 'flex' }}
                            >
                                <span>分区名称：</span>
                                <Input.TextArea
                                    defaultValue={partitions.partConfig}
                                    style={{ height: 50, width: 300 }}
                                    placeholder="多个分区名用英文逗号间隔"
                                    onChange={this.handlePartitionParamChange}
                                />
                            </div>
                        )
                    )}
                    <Table
                        columns={
                            partitions.partitionType === 0
                                ? this.getTableCol(2)
                                : this.getTableCol(3)
                        }
                        dataSource={partitions.columns || []}
                        rowKey="_fid"
                        pagination={false}
                        size="small"
                    />

                    {partitions.partitionType === 0 && (
                        <a
                            className="btn"
                            href="javascript:;"
                            onClick={() => this.addNewLine(2)}
                        >
                            <Icon className="icon" type="plus-circle-o" />
                            添加分区字段
                        </a>
                    )}
                </div>
                <div className="table-panel" id="bucket_panel">
                    <div className="area-title-container">
                        <span className="title">分桶信息</span>
                    </div>
                    <div style={{ marginBottom: 10 }}>
                        <span>分桶数量：</span>
                        <Input
                            style={{ width: 150, marginRight: 4 }}
                            value={bucketInfo.bucketNumber}
                            placeholder="1-1000之间的正整数"
                            onChange={this.handleBarrelDataParamCahnge}
                        />
                        个
                    </div>
                    {/* <Table
          columns={this.getTableCol(4)}
          dataSource={bucketInfo.infos || []}
          rowKey="_fid"
          pagination={false}
          size="small"
          ></Table>
          <a className="btn" href="javascript:;" onClick={()=>this.addNewLine(3)}><Icon className="icon" type="plus-circle-o" />添加分桶字段</a> */}
                </div>

                <div className="nav-btn-box">
                    <Button
                        onClick={this.props.handleLastStep}
                        style={{ width: 90 }}
                    >
                        上一步
                    </Button>
                    <Button
                        type="primary"
                        onClick={this.props.handleSave}
                        style={{ width: 90 }}
                    >
                        下一步
                    </Button>
                </div>
            </Row>
        );
    }
}
