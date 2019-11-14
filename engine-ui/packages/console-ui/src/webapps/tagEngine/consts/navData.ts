const navData: any = [
    {
        'permissionName': '实体管理',
        'permissionIcon': '',
        'authCode': '',
        'permissionUrl': '/entityManage',
        'routers': ['/database', '/entityManage', '/relationManage', '/dictionaryManage'], // 实体相关路由
        'children': [
            {
                'permissionIcon': '#icon-project_set',
                'permissionName': '数据源管理',
                'permissionUrl': '/database',
                'routers': []
            },
            {
                'permissionIcon': '#icon-project_set',
                'permissionName': '实体管理',
                'permissionUrl': '/entityManage',
                'routers': []
            },
            {
                'permissionIcon': '#icon-project_set',
                'permissionName': '关系管理',
                'permissionUrl': '/relationManage',
                'routers': []
            },
            {
                'permissionIcon': '#icon-project_set',
                'permissionName': '字典管理',
                'permissionUrl': '/dictionaryManage',
                'routers': []
            }
        ]
    },
    {
        'permissionName': '标签中心',
        'permissionIcon': '',
        'authCode': '',
        'permissionUrl': '/labelCenter',
        'routers': ['/labelCenter', '/labelDirectory'],
        'children': [
            {
                'permissionIcon': '#icon-project_set',
                'permissionName': '标签管理',
                'permissionUrl': '/labelCenter',
                'routers': []
            },
            {
                'permissionIcon': '#icon-project_set',
                'permissionName': '标签目录',
                'permissionUrl': '/labelDirectory',
                'routers': []
            }
        ]
    },
    {
        'permissionName': '群组分析',
        'permissionIcon': '',
        'authCode': '',
        'permissionUrl': '/groupAnalyse',
        'routers': ['/groupAnalyse'],
        'children': [
            {
                'permissionIcon': '#icon-project_set',
                'permissionName': '群组管理',
                'permissionUrl': '/groupAnalyse',
                'routers': []
            }
        ]
    }];
export default navData;
