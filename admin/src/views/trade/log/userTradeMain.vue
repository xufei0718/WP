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
                        <Col span="8" align="left">
                            <span style="font-size: 18px;  vertical-align: middle; padding-left: 10px"> 商户账户余额：{{merAmount}} 元</span>
                        </Col>
                        <Col span="16" align="right">
                            <Input v-model="searchAmount" placeholder="请输入实付金额" style="width: 200px"/>
                            <DatePicker @on-change="setSearchDate"  type="daterange" placement="bottom-end" placeholder="请选择时间范围" style="width: 200px"></DatePicker>

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

        <Modal v-model="uploadModal" @on-visible-change="vChange" :mask-closable="false">
            <p slot="header">
                <Icon type="information-circled"></Icon>
                <span>{{modalTitle}}</span>
            </p>
            <Form ref="formValidate" :label-width="150" :model="tradeLog" :rules="ruleValidate">
                    <FormItem label="交易成功凭证" prop="tradeImgName">

                         <Upload
                                 ref="upload"
                                 :show-upload-list="false"
                                 :on-success="handleSuccessCard"
                                 :format="['jpg','jpeg','png']"
                                 :max-size="4096"
                                 :on-format-error="handleFormatError"
                                 :on-exceeded-size="handleMaxSize"
                                 type="drag"
                                 :action="updateAction"
                                 :data="{tradeNo:tradeLog.tradeNo}"
                                 style="width:300px; line-height: 0px;">

                             <div style="line-height: 32px;">点击交易成功凭证</div>

                             <img :src="urlTradeImg" v-show="showTradeImg" width="300">

                         </Upload>
                     </FormItem>



            </Form>
            <div slot="footer">
                <Button type="success" :loading="modalLoading" @click="saveTrade">更正</Button>
                <Button type="error" @click="uploadModal=false">关闭</Button>
            </div>
        </Modal>

    </div>

</template>

<script>
    import {mapState} from 'vuex'
    import consts from '../../../libs/consts'
    const uploadBtn = (vm, h, param) => {

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
                    vm.uploadTradeImg(param.row)
                }
            }

        }, '修改交易结果');
    }

    export default {

        computed: {
            ...mapState({
                'tradeLogList': state => state.tradeLog.tradeLogList,
                'totalPage': state => state.tradeLog.totalPage,
                'pageNumber': state => state.tradeLog.pageNumber,
                'pageSize': state => state.tradeLog.pageSize,
                'total': state => state.tradeLog.totalRow,
                'tradeLog': state => state.tradeLog.tradeLog,
                'merAmount': state => state.tradeLog.merAmount,

            })
        },
        methods: {
            uploadTradeImg(tradeLog) {
                this.modalTitle = "更正交易结果"
                this.isAdd = false

                let vm = this
                vm.$store.commit('tradeLog_reset', tradeLog)
                vm.uploadModal = true

            },
            saveTrade() {
                let vm = this;
                this.modalLoading = true;
                this.$refs['formValidate'].validate((valid) => {
                    if (valid) {
                        this.$store.dispatch('tradeLog_saveTrade').then((res) => {
                            if (res && res == 'success') {
                                vm.uploadModal = false;
                                vm.search();
                            } else {
                                this.modalLoading = false;
                            }
                        })
                    } else {
                        this.modalLoading = false;
                    }
                })
            },
            vChange(b) {
                if (!b) {

                    this.$refs['formValidate'].resetFields()
                    this.modalLoading = false;
                    this.showTradeImg = false;
                    this.urlTradeImg = '';

                }
            },
            search(pn) {
                this.$store.dispatch('tradeLog_list', {search: this.searchKey, searchDate: this.searchDate, searchWxAcct: this.searchWxAcct,searchAmount:this.searchAmount, pn: pn})
            },
            setSearchDate(str){
                this.searchDate= str
            },
            handleSuccessCard(res, file) {

                this.tradeLog.tradeImgName = res.tradeImgName;
                this.urlTradeImg = consts.devLocation + "/cmn/act07?tradeImgName=" + res.tradeImgName;
                this.showTradeImg = true;
                this.modalLoading = false;

            },
            handleFormatError(file) {
                this.$Notice.warning({
                    title: '文件格式不正确',
                    desc: '文件 ' + file.name + ' 格式不正确，请上传 jpg 或 png 格式的图片。'
                });
            },
            handleMaxSize(file) {
                this.$Notice.warning({
                    title: '超出文件大小限制',
                    desc: '文件 ' + file.name + ' 太大，不能超过 4M。'
                });
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
                searchDate:[],
                searchWxAcct:'',
                searchAmount:'',
                isAdd: true,
                urlTradeImg:'',
                showTradeImg:false,
                modalLoading:false,
                uploadModal:false,
                modalTitle:'',
                updateAction:consts.env + '/tt00/upTradeImg',

                ruleValidate: {
                    tradeImgName: [
                        {required: true, message: '交易凭证不能为空', trigger: 'blur'},
                    ],
                },

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
                        title: '交易时间',
                        key: 'tradeTimeTxt',
                        align: 'center',
                        width: 150,
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

                ]
            }
        }
    }
</script>
<style lang="less">
    @import '../../../styles/common.less';
</style>