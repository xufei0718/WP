<template>
    <div>
        <Row>
            <Col span="24">
                <Card>
                    <p slot="title">
                        <Icon type="help-buoy"></Icon>
                        微信账号管理页面
                    </p>
                    <Row style="padding: 10px 20px 20px 20px">
                        <Col span="24">

                            <span style="font-size: 24px;  vertical-align: middle; padding-right: 50px"> 微信账号：{{qrcodeWx.wxAcct}}</span>
                            <span style="font-size: 15px;  vertical-align: bottom; padding-right: 50px"> 登录状态：{{qrcodeWx.isLoginTxt}}</span>

                            <Poptip
                                    confirm
                                    placement="bottom"
                                    @on-ok="del(wxID)"
                                    title="删除微信账号，将删除所有该账号二维码图片，确认删除吗?"
                            >
                                <Button type="error" icon="ios-trash" size="large" @click="">删除账号</Button>
                            </Poptip>

                            <Button type="success" icon="refresh" size="large" @click="ret">返回</Button>
                        </Col>

                    </Row>
                    <Row>
                        <Col span="12">
                            <Card style="margin-right: 5px ;height: 400px; ">
                                <p slot="title">上传支付二维码图片 <span style="font-size: 14px !important; font-weight: normal !important;  "> （已有二维码图片数量：{{qrCount}}）</span></p>
                                <div class="demo-spin-col">
                                    <p align="center">
                                        <Upload
                                                :on-success="handleSuccessZip"
                                                :on-error="handleErrorZip"
                                                :format="['zip']"
                                                :max-size="20480"
                                                :show-upload-list="false"
                                                :on-format-error="handleFormatError"
                                                :on-exceeded-size="handleMaxSize"
                                                :before-upload="handleBeforeUpload"
                                                :action="uploadAction"
                                                :data="{id:wxID}">
                                            <Button type="ghost" icon="ios-cloud-upload-outline">点击图片文件压缩包（.zip）</Button>
                                        </Upload>

                                        <Spin fix v-if="spinShow">
                                            <Icon type="load-c" size=30 class="demo-spin-icon-load"></Icon>
                                            <div style="font-size: 14px">文件正在处理，请耐心等待...</div>
                                        </Spin>


                                        <Alert :type="isResMsgType" v-if="isResMsg" show-icon style="width: 400px ; margin-top: 20px">
                                            <div align="left"> {{uploadResMsgTitle}}</div>
                                            <span slot="desc">{{uploadResMsg}}</span>
                                        </Alert>


                                    </p>
                                </div>
                            </Card>
                        </Col>
                        <Col span="12">
                            <Card style="margin-left: 5px; height: 400px">
                                <p slot="title">微信登陆</p>
                                <p align="center">
                                    <Button v-if="loginBtnShow" style="margin-top: 20px" type="primary" icon="person" size="large" @click="login">微信登录</Button>
                                    <Button v-if="loginInfoShow" style="margin-top: 20px" type="error" icon="person" size="large" @click="logout">微信状态重置（非异常状况不可使用此功能）</Button>
                                </p>
                                <p align="center">
                                    <span v-if="loginImgShow" style="margin-top: 30px">扫描下方二维码登录微信</span>
                                    <br/>
                                    <img v-if="loginImgShow" style="margin-top: 10px" id="images" :src="imgData" width="200px"/>

                                    <Alert type="success" v-if="loginInfoShow" show-icon style="width: 400px ; margin-top: 20px">
                                        <div align="left"> 微信已登录</div>
                                        <span slot="desc">此微信已登录，可以正常使用。</span>
                                    </Alert>


                                </p>

                            </Card>
                        </Col>
                    </Row>

                </Card>
            </Col>
        </Row>
        <Row>
            <Col span="24">

            </Col>
        </Row>


    </div>

</template>
<style>
    .demo-spin-icon-load {
        animation: ani-demo-spin 1s linear infinite;
    }

    @keyframes ani-demo-spin {
        from {
            transform: rotate(0deg);
        }
        50% {
            transform: rotate(180deg);
        }
        to {
            transform: rotate(360deg);
        }
    }

    .demo-spin-col {
        height: 300px;
        position: relative;
        padding: 20px;
    }
</style>
<script>


    import {mapState} from 'vuex'
    import consts from '../../../libs/consts'
    import Button from "iview/src/components/button/button";

    export default {
        components: {Button},
        computed: {
            ...mapState({
                'qrcodeWx': state => state.qrcodeWx.qrcodeWx,
                'loginBtnShow': state => state.qrcodeWx.loginBtnShow,
                'loginInfoShow': state => state.qrcodeWx.loginInfoShow,
                'qrCount': state => state.qrcodeWx.qrCount,


            })
        },
        methods: {

            del(i) {
                let vm = this;
                this.$store.dispatch('qrcodeWx_del', {id: i}).then((res) => {
                    //setTimeout(vm.search, 1000)
                    vm.ret();
                })
            },
            ret() {
                this.$router.push({path: '/qrcode/wx'})
            },
            login() {
                let vm = this;
                this.$store.dispatch('qrcodeWx_login', {wxAcct: vm.qrcodeWx.wxAcct}).then((res) => {
                    if (res.code && res.code == 'success') {
                        vm.$store.commit('wxManager_updateIsLoginShow', false)
                        vm.cTime = setInterval(() => {
                            this.getLoginImg();
                        }, 1000)
                    }else{
                        this.$Message.error('微信服务连接失败，请检查微信登陆服务器！');
                    }

                })
            },
            logout() {
                let vm = this;
                this.$store.dispatch('qrcodeWx_logout', {wxAcct: vm.qrcodeWx.wxAcct}).then((res) => {
                    if (res.resCode && res.resCode == 'success') {
                        console.info("微信退出成功")
                        vm.$store.commit('wxManager_set', {id: vm.wxID})
                    }
                    
                })
            },
            getLoginImg() {
                console.info("执行轮询图片")
                let vm = this;
                if (!vm.queryLoginStatus()) {

                    this.$store.dispatch('qrcodeWx_getLoginImg', {wxAcct: vm.qrcodeWx.wxAcct}).then((res) => {
                        if (res.imgData !== "") {
                            vm.imgData = "data:image/gif;base64," + res.imgData;
                            vm.loginImgShow = true;
                            clearInterval(vm.cTime);
                            vm.dTime = setInterval(() => {
                                this.queryLoginStatus();
                            }, 1000)
                        }


                    })
                }
            },
            queryLoginStatus() {
                console.info("查询状态")
                let vm = this;
                this.$store.dispatch('qrcodeWx_queryLoginStatus', {wxID: vm.qrcodeWx.id}).then((res) => {
                    if (res.isLogin === "0") {
                        clearInterval(vm.dTime);
                        this.$store.commit('wxManager_set', {id: vm.qrcodeWx.id})
                        vm.loginImgShow = false;
                        vm.imgData = "";
                        return true
                    } else {
                        return false
                    }
                })
            },
            handleBeforeUpload(res, file) {
                this.modalLoading = true;
                this.isResMsg = false;
                this.spinShow = true;
            },
            handleSuccessZip(res, file) {

                this.isResMsgType = 'success'
                this.uploadResMsgTitle = '文件处理成功'
                this.uploadResMsg = '文件 ' + file.name + ' 已处理，共记录 ' + res.fileCount + ' 个文件。'
                this.isResMsg = true;
                this.spinShow = false;
            },
            handleErrorZip(res, file) {

                this.isResMsgType = 'error'
                this.uploadResMsgTitle = '文件处理失败'
                this.uploadResMsg = '共处理 ' + res.fileCount + ' 个文件。'
                this.isResMsg = true;
                this.spinShow = false;
            },
            handleFormatError(file) {
                this.isResMsgType = 'error'
                this.uploadResMsgTitle = '文件格式不正确'
                this.uploadResMsg = '文件 ' + file.name + ' 格式不正确，请上传 zip 格式的压缩包。'
                this.isResMsg = true;
                this.spinShow = false;
            },
            handleMaxSize(file) {

                this.isResMsgType = 'error'
                this.uploadResMsgTitle = '超出文件大小限制'
                this.uploadResMsg = '文件 ' + file.name + ' 太大，不能超过 20M。'
                this.isResMsg = true;
                this.spinShow = false;
            },


        },
        mounted() {
            //页面加载时或数据方法
            let vm = this;
            this.uploadResMsg = ''
            this.wxID = this.$route.params.id
            this.$store.commit('wxManager_set', {id: this.$route.params.id})


        },
        data() {
            return {
                cTime: {},
                dTime: {},
                loginImgShow: false,
                imgData: '',
                uploadResMsg: '',
                uploadResMsgTitle: '',
                isResMsg: false,
                isResMsgType: 'error',
                wxID: '',
                uploadAction: consts.env + '/qr00/upQrZip',
                spinShow: false,

            }
        }
    }
</script>
<style lang="less">
    @import '../../../styles/common.less';
</style>