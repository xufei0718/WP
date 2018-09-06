<template>
    <div>
        <Row>
            <Col span="24">
                <Card>
                    <p slot="title">
                        <Icon type="help-buoy"></Icon>
                        交易列表
                    </p>
                    <Row>

                        <Col span="24" align="right">
                            <Input v-model="searchKey" placeholder="请输入交易流水号\商户编号" style="width: 200px"/>
                            <span @click="search">
                        <Button type="primary" icon="search">搜索</Button></span>
                        </Col>
                    </Row>
                    <Row class="margin-top-10">
                        <Table :context="self" border :data="tradeLogList" :columns="tableColums" stripe></Table>
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


    export default {

        computed: {
            ...mapState({
                'tradeLogList': state => state.tradeLog.tradeLogList,
                'totalPage': state => state.tradeLog.totalPage,
                'pageNumber': state => state.tradeLog.pageNumber,
                'pageSize': state => state.tradeLog.pageSize,
                'total': state => state.tradeLog.totalRow,
                'tradeLog': state => state.tradeLog.tradeLog,

            })
        },
        methods: {

            vChange(b) {
                if (!b) {
                    this.$refs['formValidate'].resetFields()
                    this.modalLoading = false;

                }
            },
            search(pn) {
                this.$store.dispatch('tradeLog_list', {search: this.searchKey, pn: pn})
            },


        },
        mounted() {
            //页面加载时或数据方法
            this.$store.dispatch('tradeLog_list')

        },
        data() {
            return {

                self: this,
                searchKey: '',

                isAdd: true,


                tableColums: [

                    {
                        title: '交易流水号',
                        key: 'tradeNo',
                        align: 'center',
                    },
                    {
                        title: '商户编号',
                        key: 'tradeMerNo',
                        align: 'center',
                    },
                    {
                        title: '商户名称',
                        key: 'tradeMerName',
                        align: 'center',
                    },
                    {
                        title: '交易金额',
                        key: 'tradeAmount',
                        align: 'center',
                    },
                    {
                        title: '实付金额',
                        key: 'tradeRealAmount',
                        align: 'center',
                    },
                    {
                        title: '收款微信账号',
                        key: 'tradeWxAcct',
                        align: 'center',
                    },
                    {
                        title: '二维码文件名',
                        key: 'tradeQrcodeImg',
                        align: 'center',
                    },
                    {
                        title: '交易状态',
                        key: 'tradeStatus',
                        width: 160,
                        align: 'center',
                        render: (h, param) => {
                            if (param.row.tradeStatus == '0') {
                                return h('Tag', {
                                    props: {
                                        type: 'dot',
                                        color: 'green'
                                    },
                                }, param.row.tradeStatusTxt)
                            } else if (param.row.tradeStatus == '1') {
                                return h('Tag', {
                                    props: {
                                        type: 'dot',
                                        color: 'blue'
                                    },
                                }, param.row.tradeStatusTxt)
                            } else if (param.row.tradeStatus == '2') {
                                return h('Tag', {
                                    props: {
                                        type: 'dot',
                                        color: 'red'
                                    },
                                }, param.row.tradeStatusTxt)
                            }
                        }
                    },
                    {
                        title: '交易时间',
                        key: 'tradeTimeTxt',
                        align: 'center',
                        width: 150,
                    },


                ]
            }
        }
    }
</script>
<style lang="less">
    @import '../../../styles/common.less';
</style>