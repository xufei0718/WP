import Main from '@/views/Main.vue';

// 不作为Main组件的子页面展示的页面单独写，如下
//isCheck 表示 组件是否需要权限检查
export const loginRouter = {
    path: '/login',
    name: 'login',
    meta: {
        title: '登录'
    },
    component: resolve => { require(['@/views/login.vue'], resolve); }
};

export const page404 = {
    path: '/*',
    name: 'error-404',
    meta: {
        title: '404-页面不存在'
    },
    component: resolve => { require(['@/views/error-page/404.vue'], resolve); }
};

export const page403 = {
    path: '/403',
    meta: {
        title: '403-权限不足'
    },
    name: 'error-403',
    component: resolve => { require(['@//views/error-page/403.vue'], resolve); }
};

export const page500 = {
    path: '/500',
    meta: {
        title: '500-服务端错误'
    },
    name: 'error-500',
    component: resolve => { require(['@/views/error-page/500.vue'], resolve); }
};



export const locking = {
    path: '/locking',
    name: 'locking',
    component: resolve => { require(['@/views/main-components/lockscreen/components/locking-page.vue'], resolve); }
};

// 作为Main组件的子页面展示但是不在左侧菜单显示的路由写在otherRouter里
export const otherRouter = {
    path: '/',
    name: 'otherRouter',
    redirect: '/home',
    isCheck: 'no',
    component: Main,
    children: [
        { path: 'home', title: '控制中心', isCheck: 'no', name: 'home_index', component: resolve => { require(['@/views/home/home.vue'], resolve); } },
        { path: 'ownspace', title: '个人中心', isCheck: 'no', name: 'ownspace_index', component: resolve => { require(['@/views/own-space/own-space.vue'], resolve); } },
        { path: 'merCenter', title: '商户中心', isCheck: 'no', name: 'merCenter', component: resolve => { require(['@/views/own-space/merCenter.vue'], resolve); } },

    ]
};

// 作为Main组件的子页面展示并且在左侧菜单显示的路由写在appRouter里
export const appRouter = [
    {
        path: '/admin',
        icon: 'monitor',
        isCheck: 'yes',
        name: 'admin',
        title: '系统管理',
        component: Main,
        children: [
            { path: 'param', title: '参数管理', isCheck: 'yes', name: 'admin_param', component: resolve => { require(['@/views/admin/param/main.vue'], resolve); } },
            { path: 'user', title: '用户管理', isCheck: 'yes', name: 'admin_user', component: resolve => { require(['@/views/admin/user/main.vue'], resolve); } },
            { path: 'role', title: '角色管理', isCheck: 'yes', name: 'admin_role', component: resolve => { require(['@/views/admin/role/main.vue'], resolve); } },
            { path: 'tax', title: '分类管理', isCheck: 'yes', name: 'admin_tax', component: resolve => { require(['@/views/admin/taxonomy/main.vue'], resolve); } },
        ]
    },

    {
        path: '/mer',
        icon: 'monitor',
        isCheck: 'yes',
        name: 'mer',
        title: '商户管理',
        component: Main,
        children: [
            { path: 'merinfo', title: '商户信息管理', isCheck: 'yes', name: 'merinfo', component: resolve => { require(['@/views/mer/merinfo/main.vue'], resolve); } },

        ]
    },
    {
        path: '/merUser',
        icon: 'monitor',
        isCheck: 'yes',
        name: 'merUser',
        title: '商户交易查询',
        component: Main,
        children: [
            { path: 'tradeLog', title: '商户交易查询', isCheck: 'yes', name: 'tradeLog', component: resolve => { require(['@/views/trade/log/userTradeMain.vue'], resolve); } },

        ]
    },
    {
        path: '/qrcode',
        icon: 'monitor',
        isCheck: 'yes',
        name: 'qrcode',
        title: '微信管理',
        component: Main,
        children: [
            { path: 'wx', title: '微信账号管理', isCheck: 'yes', name: 'wx', component: resolve => { require(['@/views/qrcode/wx/main.vue'], resolve); }},
            { path: 'info', title: '二维码管理', isCheck: 'yes', name: 'info', component: resolve => { require(['@/views/qrcode/info/main.vue'], resolve); } },
            { path: 'wxManager/:id', title: '账号管理', isCheck: 'no', name: 'wxManager', component: resolve => { require(['@/views/qrcode/wx/manager.vue'], resolve); } },
        ]
    },
    {
        path: '/trade',
        icon: 'monitor',
        isCheck: 'yes',
        name: 'trade',
        title: '交易管理',
        component: Main,
        children: [
            { path: 'wx', title: '交易查询', isCheck: 'yes', name: 'log', component: resolve => { require(['@/views/trade/log/main.vue'], resolve); }},


        ]
    },


];

// 所有上面定义的路由都要写在下面的routers里
export const routers = [
    loginRouter,
    otherRouter,
    locking,
    ...appRouter,
    page500,
    page403,
    page404
];
