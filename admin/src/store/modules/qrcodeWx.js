import Cookies from 'js-cookie';
import kit from '../../libs/kit';

const qrcodeWx = {
    state: {
        qrcodeWxList:[],
        qrcodeAmountList:[],
        totalPage:0,
        pageNumber:1,
        pageSize:15,
        totalRow:0,
        qrcodeWx:{},
        isOper:false,
    },
    mutations: {
        set_qrcodeWx_list(state,page){
            state.qrcodeWxList=page.page.list
            state.totalPage=page.page.totalPage
            state.pageSize=page.page.pageSize
            state.pageNumber=page.page.pageNumber
            state.totalRow=page.page.totalRow

        },
        qrcodeWx_reset(state,param){
            if(param) {
                state.qrcodeWx = kit.clone(param)
            }
        },

        wxManager_set(state,param){
            console.info(param)
            if(param) {
                this._vm.$axios.post('/qr00/info',param).then((res)=>{
                    state.qrcodeWx =res.qrcodeWxacct
                });
            }
        },

        set_qrcodeAmount_list(state,page){
            state.qrcodeAmountList=page.page.list
            state.totalPage=page.page.totalPage
            state.pageSize=page.page.pageSize
            state.pageNumber=page.page.pageNumber
            state.totalRow=page.page.totalRow

        },

    },
    actions:{
        qrcodeWx_list:function ({ commit,state },param) {
            if(param&&!param.pn){
                param.pn=state.pageNumber;
            }

            this._vm.$axios.post('/qr00/list',param).then((res)=>{
                commit('set_qrcodeWx_list',res)
            });
        },

        qrcodeWx_save:function ({ commit,state },action) {
            let vm=this._vm;
            let p=kit.clone(state.qrcodeWx)
            return new Promise(function (resolve, reject) {
                vm.$axios.post('/qr00/'+action, p).then((res) => {
                    // if(res.resCode&&res.resCode=='success'){
                    //     commit('user_reset');
                    // }
                    resolve(res.resCode);
                });
            });
        },

        qrcodeAmount_list:function ({ commit,state },param) {
            if(param&&!param.pn){
                param.pn=state.pageNumber;
            }

            this._vm.$axios.post('/qr01/list',param).then((res)=>{
                commit('set_qrcodeAmount_list',res)
            });
        },
    }
};

export default qrcodeWx;
