//aop配置文件
var ioc = {
    $aop: {
        type: 'org.nutz.ioc.aop.config.impl.ComboAopConfigration',
        fields: {
            aopConfigrations: [{
                type: 'org.nutz.ioc.aop.config.impl.JsonAopConfigration',
                fields: {
                    //根据路径正则对不同的类配置特定的切面拦截器
                    itemList: [
                        // ['com\\.wonders\\.zxjc\\.','.+','ioc:txNONE']
                        ['com\\.wonders\\.', '(add|save|insert|edit|update|modify|delete|remove|clear).+', 'ioc:txSERIALIZABLE'],
                        ['com\\.wonders\\.', '(get|query|find|fetch|load).+', 'ioc:txREAD_COMMITTED']
                    ]
                }
            }, {
                type: 'org.nutz.ioc.aop.config.impl.AnnotationAopConfigration'
            }]
        }
    },

    txNONE: {
        type: 'org.nutz.aop.interceptor.TransactionInterceptor',
        args: [0]
    },
    txREAD_UNCOMMITTED: {
        type: 'org.nutz.aop.interceptor.TransactionInterceptor',
        args: [1]
    },
    txREAD_COMMITTED: {
        type: 'org.nutz.aop.interceptor.TransactionInterceptor',
        args: [2]
    },
    txREPEATABLE_READ: {
        type: 'org.nutz.aop.interceptor.TransactionInterceptor',
        args: [4]
    },
    txSERIALIZABLE: {
        type: 'org.nutz.aop.interceptor.TransactionInterceptor',
        args: [8]
    },
    //日志切面
    log: {
        type: 'org.nutz.aop.interceptor.LoggingMethodInterceptor'
    }

};