import * as React from 'react'
import moment from 'moment'
import CopyToClipboard from 'react-copy-to-clipboard'
import { DateTime } from '@dtinsight/dt-utils'
import { Table, DatePicker, TimePicker, message } from 'antd'
import type { IStreamTaskProps } from '@/interface';
import { disableRangeCreater } from '@/utils';
import SvgIcon from '@/components/svgIcon'

const Api = {} as any

interface IProps {
    data: IStreamTaskProps | undefined;
    tabKey: string;
}

interface IState {
    day?: moment.Moment | null;
    beginTime?: moment.Moment | null;
    endTime?: moment.Moment | null;
    list?: [];
    totalSize?: string;
    loading?: boolean;
}

class CheckPoint extends React.Component<IProps, IState> {
    state: IState = {
        day: moment(),
        beginTime: moment('00:00:00', 'HH:mm:ss'),
        endTime: moment('23:59:59', 'HH:mm:ss'),
        list: [],
        totalSize: '',
        loading: false
    }

    componentDidMount () {
        this.getList();
    }

    initPage () {
        this.setState({
            day: moment(),
            beginTime: moment('00:00:00', 'HH:mm:ss'),
            endTime: moment('23:59:59', 'HH:mm:ss')
        })
    }

    UNSAFE_componentWillReceiveProps (nextProps: IProps) {
        const { data, tabKey } = this.props;
        const { data: nextData, tabKey: nextTabKey } = nextProps;
        // 从其他 tab 回来时，重新请求
        const reFocusTab = tabKey !== 'checkpoint' && nextTabKey === 'checkpoint'
        if (data?.id != nextData?.id || reFocusTab) {
            this.initPage();
            this.getList(nextData);
        }
    }

    getList (data?: IStreamTaskProps | undefined) {
        let { day, beginTime, endTime } = this.state;
        data = data || this.props.data;

        this.setState({ list: [] })

        if (!data || !data.id || !day || !beginTime || !endTime) {
            return;
        }

        const startTime = moment(day.format('YYYY MM DD') + ' ' + beginTime.format('HH:mm:ss'), 'YYYY MM DD HH:mm:ss').valueOf();
        const handleEndTime = moment(day.format('YYYY MM DD') + ' ' + endTime.format('HH:mm:ss'), 'YYYY MM DD HH:mm:ss').valueOf();

        this.setState({ loading: true })

        Api.getCheckPointList({
            taskId: data.id,
            startTime,
            endTime: handleEndTime
        }).then(
            (res: any) => {
                if (res.code == 1) {
                    this.setState({
                        list: res.data.checkpointList,
                        totalSize: res.data.totalSize
                    })
                }
                this.setState({
                    loading: false
                })
            }
        )
    }
    
    initCheckPointColumns () {
        const copyIconStyle: React.CSSProperties = {
            marginLeft: 8,
            cursor: 'pointer',
            fontSize: 14,
            color: '#337dff'
        }

        const copyOk = () => {
            message.success('复制成功！')
        }

        return [{
            title: 'StartTime',
            dataIndex: 'time',
            width: '190px',
            render (time: string) {
                return DateTime.formatDateTime(time);
            }
        }, {
            title: '存储大小',
            dataIndex: 'storeSize',
            width: '120px'
        }, {
            title: '存储路径',
            dataIndex: 'storePath',
            width: '600px',
            render: (path: string) => (
                <>
                    {path}
                    <CopyToClipboard key="jobIdCopy" text={path} onCopy={copyOk}>
                        <SvgIcon
                            linkHref="iconicon_copy"
                            style={copyIconStyle}
                        />
                    </CopyToClipboard>
                </>
            )
        }, {
            title: '持续时间',
            dataIndex: 'duration',
            width: '130px',
            render (text: any) {
                return `${text}ms`
            }
        }]
    }

    changeDate (date: moment.Moment | null) {
        this.setState({
            day: date
        }, this.getList.bind(this))
    }

    changeTime (type: 'beginTime' | 'endTime' , date: moment.Moment | null) {
        this.setState({
            [type]: date
        }, this.getList.bind(this))
    }

    disabledDate = (current: moment.Moment) => {
        const now = moment('23:59:59', 'HH:mm:ss')
        return current > now && current != now;
    }

    getTableTitle = () => {
        const { day, beginTime, endTime, totalSize } = this.state;
        const headerStyle: React.CSSProperties = {
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            padding: '16px 0px'
        }

        return (
            <div style={headerStyle}>
                <div>
                    开始日期：<DatePicker
                        onChange={this.changeDate.bind(this)}
                        style={{ width: '180px', marginRight: 20 }}
                        value={day}
                        disabledDate={this.disabledDate}
                        allowClear={false}
                        placeholder='请选择日期'
                    />
                    开始时间：<TimePicker
                        style={{ width: '180px', marginRight: 20 }}
                        allowClear={false}
                        onChange={this.changeTime.bind(this, 'beginTime')}
                        value={beginTime}
                        placeholder='开始时间'
                        disabledHours={() => {
                            return disableRangeCreater(beginTime, endTime, 'hour')
                        }}
                        disabledMinutes={() => {
                            return disableRangeCreater(beginTime, endTime, 'minute')
                        }}
                        disabledSeconds={() => {
                            return disableRangeCreater(beginTime, endTime, 'second')
                        }}
                    />
                    截止时间：<TimePicker
                        style={{ width: '180px' }}
                        allowClear={false}
                        onChange={this.changeTime.bind(this, 'endTime')}
                        value={endTime}
                        placeholder='结束时间'
                        disabledHours={() => {
                            return disableRangeCreater(beginTime, endTime, 'hour', true)
                        }}
                        disabledMinutes={() => {
                            return disableRangeCreater(beginTime, endTime, 'minute', true)
                        }}
                        disabledSeconds={() => {
                            return disableRangeCreater(beginTime, endTime, 'second', true)
                        }}
                    />
                </div>
                <div>
                    <span>总存储大小：{totalSize ? <span style={{ color: '#337dff' }}>{totalSize}</span> : '-'}</span>
                </div>
            </div>
        )
    }

    render () {
        const { list } = this.state;
        const PAGE_SIZE = 20;
        return (
            <div style={{ padding: '0px 20px 20px 24px' }}>
                {this.getTableTitle()}
                <Table
                    rowKey={(_, index: any) => {
                        return index
                    }}
                    className="dt-table-fixed-base dt-table-border"
                    style={{ height: 'calc(100vh - 334px)', boxShadow: 'unset' }}
                    size="middle"
                    columns={this.initCheckPointColumns()}
                    dataSource={list}
                    pagination={{
                        pageSize: PAGE_SIZE,
                        size: 'small',
                        showTotal: (total: number) => <>
                            共<span style={{ color: '#3F87FF' }}>{total}</span>条数据，每页显示{PAGE_SIZE}条
                        </>
                    } as any}
                />
            </div>
        )
    }
}

export default CheckPoint;
