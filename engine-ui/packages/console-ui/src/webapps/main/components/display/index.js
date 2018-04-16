import { MY_APPS } from '../../consts'
export function DatabaseType(props) {
    const value = props.value
    switch (value) {
    case 1:
        return <span>MySQL</span>
    case 2:
        return <span>Oracle</span>
    case 3:
        return <span>SQLServer</span>
    case 6:
        return <span>HDFS</span>
    case 7:
        return <span>Hive</span>
    case 8:
        return <span>HBASE</span>
    case 9:
        return <span>FTP</span>
    default:
        return <span>其他</span>
    }
}

export function AppName(app) {
    switch(app) {
        case MY_APPS.RDOS: 
            return '开发套件'
        case MY_APPS.DATA_QUALITY: 
            return '数据质量'
        case MY_APPS.API: 
            return 'API管理'
        case MY_APPS.LABEL: 
            return '标签管理'
        case MY_APPS.DATA_MAP: 
            return '数据地图'
        case MY_APPS.META_DATA: 
            return '元数据管理'
        default: return '';
    }
}

export function MsgTypeDesc(app, type) {
    switch(app) {
        case MY_APPS.RDOS: {
            switch(type) {
                default: return '-';
            }
        }
        case MY_APPS.DATA_QUALITY: {
            switch(type) {
                case 1: 
                    return '监控任务运行失败'
                case 2: 
                    return '监控任务不通过'
                case 3: 
                    return '逐行校验任务取消'
                case 4: 
                    return '逐行校验任务失败'
               case 5: 
                    return '逐行校验任务成功'
                default: return '-';
            }
        }
        case MY_APPS.API: {
            switch(type) {
                default: return '-';
            }
        }
        default: return '';
    }
}

