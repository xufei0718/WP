import Cookies from 'js-cookie';
import kit from '../../libs/kit';

const tradeLog = {
    state: {
        tradeLogList:[],
        totalPage:0,
        pageNumber:1,
        pageSize:15,
        totalRow:0,
        tradeLog:{},
        isOper:false,

    },
    mutations: {
        set_tradeLog_list(state,page){
            state.tradeLogList=page.page.list
            state.totalPage=page.page.totalPage
            state.pageSize=page.page.pageSize
            state.pageNumber=page.page.pageNumber
            state.totalRow=page.page.totalRow

        },



    },
    actions:{
        tradeLog_list:function ({ commit,state },param) {
            if(param&&!param.pn){
                param.pn=state.pageNumber;
            }

            this._vm.$axios.post('/tt00/list',param).then((res)=>{
                commit('set_tradeLog_list',res)
            });
        },


    }
};

export default tradeLog;
