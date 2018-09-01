<template>
    <div>
        <Row>
            <Col span="24">
                <Card>
                    <p slot="title">
                        <Icon type="help-buoy"></Icon>
                        微信账号列表
                    </p>
                    <Row>
                        <Col span="8">
                            <Button type="primary" icon="person-add" @click="add">新增微信账号</Button>
                        </Col>
                        <Col span="8" offset="8" align="right">
                            <Input v-model="searchKey" placeholder="请输入微信账号" style="width: 200px"/>
                            <span @click="search">
                        <Button type="primary" icon="search">搜索</Button></span>
                        </Col>
                    </Row>
                    <Row class="margin-top-10">
                        <Table :context="self" border :data="qrcodeWxList" :columns="tableColums" stripe></Table>
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

    const delBtn = (vm, h, param) => {
        return h('Poptip', {
            props: {
                confirm: '',
                placement: 'top-end',
                width: 150,
                title: '您确定要删除吗？'
            },
            style: {
                marginRight: '5px',
                marginTop: '2px',
                marginBottom: '2px',
            },
            on: {
                'on-ok': () => {
                    vm.del(param.row.id)
                }
            }
        }, [h('Button', {
            props: {
                type: 'error',
                size: 'small'
            }
        }, '删除')]);
    }
    const editBtn = (vm, h, param) => {
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
                    vm.edit(param.row)
                }
            }
        }, '编辑')
    }
    const stopBtn = (vm, h, param) => {
        return h('Poptip', {
            props: {
                confirm: '',
                placement: 'top-end',
                width: 150,
                title: '您确定要禁用吗？'
            },
            style: {
                marginRight: '5px',
                marginTop: '2px',
                marginBottom: '2px',
            },
            on: {
                'on-ok': () => {
                    vm.stop(param.row.id)
                }
            }
        }, [h('Button', {
            props: {
                type: 'error',
                size: 'small'
            }
        }, '禁用')]);
    }
    const actBtn = (vm, h, param) => {
        return h('Poptip', {
            props: {
                confirm: '',
                placement: 'top-end',
                width: 150,
                title: '您确定要激活吗？'
            },
            style: {
                marginRight: '5px',
                marginTop: '2px',
                marginBottom: '2px',
            },
            on: {
                'on-ok': () => {
                    vm.active(param.row.id)
                }
            }
        }, [h('Button', {
            props: {
                type: 'success',
                size: 'small'
            }
        }, '激活')]);
    }
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
                'qrcodeWxList': state => state.qrcodeWx.qrcodeWxList,
                'totalPage': state => state.qrcodeWx.totalPage,
                'pageNumber': state => state.qrcodeWx.pageNumber,
                'pageSize': state => state.qrcodeWx.pageSize,
                'total': state => state.qrcodeWx.totalRow,
                'qrcodeWx': state => state.qrcodeWx.qrcodeWx,

            })
        },
        methods: {
            add() {
                this.isAdd = true
                this.isEditMerType = false
                this.modalTitle = "新增商户"
                let vm = this;
                vm.merInfoModal = true;
                //点击添加按钮是将对象设置成空
                this.$store.commit('merInfo_reset', {})
            },
            del(i) {
                let vm = this;
                this.$store.dispatch('merInfo_del', {id: i}).then((res) => {
                    setTimeout(vm.search, 1000)
                    //vm.search()
                })
            },
            edit(merInfo) {
                this.modalTitle = "修改商户"
                this.isAdd = false
                this.isEditMerType = true
                let vm = this

                vm.$store.commit('merInfo_reset', merInfo)
                this.urlCard = consts.devLocation + "/cmn/act04?picid=" + merInfo.cardImg;
                this.urlCardF = consts.devLocation + "/cmn/act04?picid=" + merInfo.cardF;
                this.urlCardZ = consts.devLocation + "/cmn/act04?picid=" + merInfo.cardZ;
                this.showImgCard = true;
                this.showImgCardF = true;
                this.showImgCardZ = true;
                vm.merInfoModal = true

            },
            stop(i) {
                let vm = this;
                this.$store.dispatch('merInfo_stop', {id: i}).then((res) => {
                    setTimeout(vm.search, 1000)
                    //vm.search()
                    //this.$store.dispatch('merInfo_list',{search:this.searchKey,pn:pn})
                })
            },
            active(i) {
                let vm = this;
                this.$store.dispatch('merInfo_active', {id: i}).then((res) => {
                    setTimeout(vm.search, 1000)
                    //vm.search()
                    //this.$store.dispatch('merInfo_list',{search:this.searchKey,pn:pn})
                })
            },
            save() {
                let vm = this;
                this.modalLoading = true;
                this.$refs['formValidate'].validate((valid) => {
                    if (valid) {
                        let action = 'save';
                        if (!vm.isAdd)
                            action = 'update';
                        this.$store.dispatch('merInfo_save', action).then((res) => {
                            if (res && res == 'success') {
                                vm.merInfoModal = false;
                                this.$store.dispatch('merInfo_list')
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
                    this.showImgCard = false;
                    this.showImgCardF = false;
                    this.showImgCardZ = false;
                    this.urlCard = '';
                    this.urlCardF = '';
                    this.urlCardZ = '';
                }
            },
            search(pn) {
                this.$store.dispatch('qrcodeWx_list', {search: this.searchKey, pn: pn})
            },
            refresh() {
                this.$store.dispatch('qrcodeWx_list', {search: this.searchKey})
            },
            handleBeforeUpload(res, file) {
                this.modalLoading = true;
            },
            handleSuccessCard(res, file) {
                this.merInfo.cardImg = res.resData;
                this.urlCard = consts.devLocation + "/cmn/act04?picid=" + this.merInfo.cardImg
                this.showImgCard = true;
                this.modalLoading = false;

            },
            handleSuccessCardZ(res, file) {
                this.merInfo.cardZ = res.resData;
                this.urlCardZ = consts.devLocation + "/cmn/act04?picid=" + this.merInfo.cardZ
                this.showImgCardZ = true;
                this.modalLoading = false;

            },
            handleSuccessCardF(res, file) {
                this.merInfo.cardF = res.resData;
                this.urlCardF = consts.devLocation + "/cmn/act04?picid=" + this.merInfo.cardF
                this.showImgCardF = true;
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
        manager(i) {
            let vm = this;
            this.$router.push({
                path: '/qrcode/wxManager/'+i.id,
            })
        },


        },
        mounted() {
            //页面加载时或数据方法
            this.$store.dispatch('qrcodeWx_list')

        },
        data() {
            return {
                merName: '',
                merFee: {},
                merFeeTitle: '手续费管理',
                merFeeModal: false,
                feeModalLoading: false,
                showImgCard: false,
                showImgCardZ: false,
                showImgCardF: false,
                urlCard: '',
                urlCardZ: '',
                urlCardF: '',
                self: this,
                searchKey: '',
                merInfoModal: false,
                isAdd: true,
                isEditMerType: false,
                modalTitle: '新增用户',
                uploadAction: consts.env + '/cmn/act03',
                modalLoading: false,
                merFeeAddTitle: '手续费续存',
                merFeeAddModal: false,
                merFeeAmount: {},
                ruleValidate: {
                    merchantName: [
                        {type: 'string', required: true, message: '商户名称不能为空', trigger: 'blur'},
                        {type: 'string', max: 100, message: '用户名长度不能超过100', trigger: 'blur'}
                    ],
                    merchantType: [
                        {type: 'string', required: true, message: '请选择商户类型', trigger: 'blur'}
                    ],
                    feeCollectType: [
                        {type: 'string', required: true, message: '手续费计算方式', trigger: 'blur'}
                    ],
                    perName: [
                        {required: true, message: '负责人名称不能为空', trigger: 'blur'},
                        {type: 'string', message: '负责人名称长度不能超过50', max: 50, trigger: 'blur'}
                    ],
                    mobile: [
                        {required: true, message: '负责人手机号不能为空', trigger: 'blur'},
                        {type: 'string', message: '请输入11位手机号', len: 11, trigger: 'blur'},
                        {
                            type: 'string',
                            message: '手机号码无效',
                            pattern: /^((13|14|15|17|18)[0-9]{1}\d{8})$/,
                            trigger: 'blur'
                        }
                    ],
                    email: [
                        {type: 'string', required: true, message: 'Email不能为空', trigger: 'blur'},
                        {type: 'email', message: 'Email格式不正确', max: 255, trigger: 'blur'},
                        {type: 'string', message: 'Email长度不能超过255', max: 255, trigger: 'blur'}
                    ],
                    cardID: [
                        {required: true, message: '身份证号不能为空', trigger: 'blur'},
                        {type: 'string', max: 50, message: '身份证号长度不能超过50', trigger: 'blur'}
                    ],
                    address: [
                        {required: true, message: '联系地址不能为空', trigger: 'blur'},
                        {type: 'string', max: 200, message: '联系地址长度不能超过20', trigger: 'blur'}
                    ],

                    bankNo: [
                        {required: true, message: '清算银行卡号不能为空', trigger: 'blur'},
                    ],

                    bankAccountName: [
                        {required: true, message: '清算银行卡户名不能为空', trigger: 'blur'},
                    ],
                    maxTradeAmount: [
                        {
                            type: 'string',
                            message: '最大代扣金额无效',
                            pattern: /^\d+$/,
                            trigger: 'blur'
                        }
                    ],

                    cardImg: [
                        {required: true, message: '手持身份证照片不能为空', trigger: 'blur'}
                    ],
                    cardZ: [
                        {required: true, message: '身份证正面照片不能为空', trigger: 'blur'}
                    ],
                    cardF: [
                        {required: true, message: '身份证反面照片不能为空', trigger: 'blur'}
                    ],


                },
                feeAmountValidate: {
                    merFeeAmount: [
                        {required: true, message: '手续费续存金额不能为空', trigger: 'blur'},
                        {
                            type: 'string',
                            message: '手续费续存金额输入无效',
                            pattern: /^([+]?\d{1,10})(\.\d{1,10})?$/,
                            trigger: 'blur'
                        }
                    ],


                },

                feeValidate: {
                    tradeType: [
                        {type: 'string', required: true, message: '请选择交易类型', trigger: 'change'},
                    ],
                    amountUpper: [
                        {type: 'string', required: true, message: '金额上限不能为空', trigger: 'blur'},
                        {
                            type: 'string',
                            message: '请输入0或整数',
                            pattern: /^\d+$/,
                            trigger: 'blur'
                        }
                    ],
                    feeType: [
                        {type: 'string', required: true, message: '请选择手续费类型', trigger: 'change'},
                    ],
                    amount: [
                        {type: 'string', required: true, message: '手续费金额或比例不能为空', trigger: 'blur'},
                        {
                            type: 'string',
                            message: '手续费金额或比例输入无效',
                            pattern: /^([+]?\d{1,10})(\.\d{1,10})?$/,
                            trigger: 'blur'
                        }
                    ],


                },

                tableColums: [

                    {
                        title: '微信账号',
                        key: 'wxAcct',
                        align: 'center',
                    },

                    {
                        title: '登陆状态',
                        key: 'isLogin',
                        width: 150,
                        align: 'center',
                        render: (h, param) => {
                            if (param.row.isLogin == '0') {
                                return h('Tag', {
                                    props: {
                                        type: 'dot',
                                        color: 'blue'
                                    },
                                }, param.row.isLoginTxt)
                            } else if (param.row.isLogin == '1') {
                                return h('Tag', {
                                    props: {
                                        type: 'dot',
                                        color: 'red'
                                    },
                                }, param.row.isLoginTxt)
                            }
                        }
                    },
                    {
                        title: '创建时间',
                        key: 'catTxt',
                        align: 'center',
                        width: 150,
                    },
                    {
                        title: '操作',
                        key: 'action',
                        width: 180,
                        align: 'center',
                        render: (h, param) => {
                            if (!param.row.dAt) {
                                    return h('div', [
                                       // editBtn(this, h, param),
                                        managerBtn(this, h, param),
                                        //delBtn(this, h, param),
                                        stopBtn(this, h, param),

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