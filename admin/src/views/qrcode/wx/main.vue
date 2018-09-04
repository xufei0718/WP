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
                        <Col span="12">
                            <Button type="primary" icon="person-add" @click="add">新增微信账号</Button>
                        </Col>
                        <Col span="12" align="right">
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

        <Modal v-model="wxAcctModal" @on-visible-change="vChange" :mask-closable="false">
            <p slot="header">
                <Icon type="information-circled"></Icon>
                <span>{{modalTitle}}</span>
            </p>
            <Form ref="formValidate" :label-width="150" :model="qrcodeWx" :rules="ruleValidate">
                <FormItem label="微信账号" prop="wxAcct" style="margin-top: 20px">
                    <Input v-model="qrcodeWx.wxAcct" placeholder="请输入..." style="width: 300px; "/>
                </FormItem>
            </Form>
            <div slot="footer">
                <Button type="success" :loading="modalLoading" @click="save">保存</Button>
                <Button type="error" @click="wxAcctModal=false">关闭</Button>
            </div>
        </Modal>


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
                this.modalTitle = "新增微信账号"
                this.wxAcctModal = true;
                //点击添加按钮是将对象设置成空
                this.$store.commit('qrcodeWx_reset', {})
            },

            save() {
                let vm = this;
                this.modalLoading = true;
                this.$refs['formValidate'].validate((valid) => {
                    if (valid) {
                        let action = 'save';
                        if (!vm.isAdd)
                            action = 'update';
                        this.$store.dispatch('qrcodeWx_save', action).then((res) => {
                            if (res && res == 'success') {
                                vm.wxAcctModal = false;
                                this.$store.dispatch('qrcodeWx_list')
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

                }
            },
            search(pn) {
                this.$store.dispatch('qrcodeWx_list', {search: this.searchKey, pn: pn})
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

                self: this,
                searchKey: '',
                wxAcctModal: false,
                isAdd: true,
                modalTitle: '新增微信账号',
                modalLoading: false,
                ruleValidate: {
                    wxAcct: [
                        {type: 'string', required: true, message: '微信账号不能为空', trigger: 'blur'},
                        {type: 'string', max: 100, message: '微信账号长度不能超过100', trigger: 'blur'}
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
                        width: 150,
                        align: 'center',
                        render: (h, param) => {
                            if (!param.row.dAt) {
                                    return h('div', [
                                       // editBtn(this, h, param),
                                        managerBtn(this, h, param),
                                        //delBtn(this, h, param),
                                       // stopBtn(this, h, param),

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