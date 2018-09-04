<template>
    <div>
        <Row>
            <Col span="24">
                <Card>
                    <p slot="title">
                        <Icon type="help-buoy"></Icon>
                        二维码统计列表
                    </p>
                    <Row>
                        <Col span="24"  align="right">
                            <Input v-model="searchKey" placeholder="金额" style="width: 200px"/>
                            <span @click="search">
                        <Button type="primary" icon="search">搜索</Button></span>
                        </Col>
                    </Row>
                    <Row class="margin-top-10">
                        <Table :context="self" border :data="qrcodeAmountList" :columns="tableColums" stripe></Table>
                    </Row>
                    <Row class="margin-top-10">
                        <Col span="24" align="right">
                            <Page :total="total" :current="pageNumber" :page-size="pageSize" @on-change="search" show-total show-elevator></Page>
                        </Col>
                    </Row>
                </Card>
            </Col>
        </Row>




    </div>

</template>

<script>
    import {mapState} from 'vuex'
    import consts from '../../../libs/consts'
    const managerBtn = (vm, h, param) => {

        return h('Button', {
            props: {
                type: 'primary',
                size: 'small'
            },
            style: {
                marginRight: '5px',
                marginTop: '2px',
                marginBottom: '2px',
            },
            on: {
                click: () => {
                    vm.manager(param.row)
                }
            }

        }, '管理');
    }




    export default {

        computed: {
            ...mapState({
                'qrcodeAmountList': state => state.qrcodeWx.qrcodeAmountList,
                'totalPage': state => state.qrcodeWx.totalPage,
                'pageNumber': state => state.qrcodeWx.pageNumber,
                'pageSize': state => state.qrcodeWx.pageSize,
                'total': state => state.qrcodeWx.totalRow,
            })
        },
        methods: {



            search(pn) {
                this.$store.dispatch('qrcodeAmount_list', {search: this.searchKey, pn: pn})
            },

        manager(i) {

        },


        },
        mounted() {
            //页面加载时或数据方法
            this.$store.dispatch('qrcodeAmount_list')

        },
        data() {
            return {

                self: this,
                searchKey: '',

                ruleValidate: {

                },


                tableColums: [

                    {
                        title: '二维码交易金额（元）',
                        key: 'amount',
                        align: 'center',
                    },
                    {
                        title: '二维码总数量（个）',
                        key: 'amountCount',
                        align: 'center',
                    },
                    {
                        title: '有效数量（个）',
                        key: 'vailCount',
                        align: 'center',
                    },
                    {
                        title: '未锁定数量（个）',
                        key: 'lockCount',
                        align: 'center',
                    },

                    {
                        title: '操作',
                        key: 'action',
                        width: 150,
                        align: 'center',
                        render: (h, param) => {
                            if (!param.row.dAt) {
                                    return h('div', [
                                        managerBtn(this, h, param),
                                    ]);



                            }
                        }
                    }

                ]
            }
        }
    }
</script>
<style lang="less">
    @import '../../../styles/common.less';
</style>