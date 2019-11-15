const navData: any = [
    {
        'permissionName': '实体管理',
        'permissionIcon': '',
        'authCode': '',
        'permissionUrl': '/entityManage',
        'routers': ['/database', '/entityManage', '/relationManage', '/dictionaryManage', '/entityManage/detail', '/entityManage/edit', '/relationManage', '/dictionaryManage', '/dictionaryManage/detail', '/dictionaryManage/edit'], // 相关路由
        'children': [
            {
                'permissionIcon': '#icon-project_set',
                'permissionName': '实体管理',
                'permissionUrl': '/entityManage',
                'routers': ['/entityManage', '/entityManage/detail', '/entityManage/edit']
            },
            {
                'permissionIcon': '#icon-project_set',
                'permissionName': '数据源管理',
                'permissionUrl': '/database',
                'routers': ['/database']
            },
            {
                'permissionIcon': '#icon-project_set',
                'permissionName': '关系管理',
                'permissionUrl': '/relationManage',
                'routers': ['/relationManage']
            },
            {
                'permissionIcon': '#icon-project_set',
                'permissionName': '字典管理',
                'permissionUrl': '/dictionaryManage',
                'routers': ['/dictionaryManage', '/dictionaryManage/detail', '/dictionaryManage/edit']
            }
        ]
    },
    {
        'permissionName': '标签中心',
        'permissionIcon': '',
        'authCode': '',
        'permissionUrl': '/labelCenter',
        'routers': ['/labelCenter', '/labelDirectory', '/createLabel', '/atomicLabelDetails', '/derivativeLabelDetails'],
        'children': [
            {
                'permissionIcon': '#icon-project_set',
                'permissionName': '标签管理',
                'permissionUrl': '/labelCenter',
                'routers': ['/labelCenter', '/createLabel', '/atomicLabelDetails', '/derivativeLabelDetails']
            },
            {
                'permissionIcon': '#icon-project_set',
                'permissionName': '标签目录',
                'permissionUrl': '/labelDirectory',
                'routers': ['/labelDirectory']
            }
        ]
    },
    {
        'permissionName': '群组分析',
        'permissionIcon': '',
        'authCode': '',
        'permissionUrl': '/groupAnalyse',
        'routers': ['/groupAnalyse', '/groupAnalyse/upload', '/groupAnalyse/detail'],
        'children': [
            {
                'permissionIcon': '#icon-project_set',
                'permissionName': '群组管理',
                'permissionUrl': '/groupAnalyse',
                'routers': ['/groupAnalyse', '/groupAnalyse/upload', '/groupAnalyse/detail']
            }
        ]
    }];
export default navData;
