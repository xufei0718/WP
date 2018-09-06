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

                      <Button  type="primary" icon="search" size="large">微信登录</Button>
                            <Poptip
                                    confirm
                                    placement="bottom"
                                    @on-ok="del(wxID)"
                                    title="删除微信账号，将删除所有该账号二维码图片，确认删除吗?"
                                    >
                                <Button type="error" icon="ios-trash" size="large"@click="">删除账号</Button>
                            </Poptip>

                            <Button type="success" icon="refresh" size="large" @click="ret">返回</Button>
                        </Col>

                    </Row>
                    <Row>
                        <Col span="12" >
                            <Card style="margin-right: 5px ;height: 400px; ">
                                <p slot="title">上传支付二维码图片 <span style="font-size: 14px !important; font-weight: normal !important;  "> （已有二维码图片数量：{{qrCount}}）</span></p>
                                <div class="demo-spin-col" >
                                <p align="center" >
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
                                            <Button  type="ghost" icon="ios-cloud-upload-outline">点击图片文件压缩包（.zip）</Button>
                                        </Upload>

                                        <Spin fix v-if="spinShow">
                                            <Icon type="load-c" size=30 class="demo-spin-icon-load"></Icon>
                                            <div style="font-size: 14px">文件正在处理，请耐心等待...</div>
                                        </Spin>


                                    <Alert :type="isResMsgType" v-if="isResMsg" show-icon style="width: 400px ; margin-top: 20px">
                                        <div align="left" > {{uploadResMsgTitle}}</div>
                                        <span  slot="desc">{{uploadResMsg}}</span>
                                    </Alert>


                                </p>
                                </div>
                            </Card>
                        </Col>
                        <Col span="12" >
                            <Card style="margin-left: 5px; height: 400px">
                                <p slot="title">微信登陆</p>

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
    .demo-spin-icon-load{
        animation: ani-demo-spin 1s linear infinite;
    }
    @keyframes ani-demo-spin {
        from { transform: rotate(0deg);}
        50%  { transform: rotate(180deg);}
        to   { transform: rotate(360deg);}
    }
    .demo-spin-col{
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
                'qrCount':state =>state.qrcodeWx.qrCount,


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
            handleBeforeUpload(res, file) {
                this.modalLoading = true;
                this.isResMsg=false;
                this.spinShow  =true;
            },
            handleSuccessZip(res, file) {

                this.isResMsgType='success'
                this.uploadResMsgTitle='文件处理成功'
                this.uploadResMsg='文件 '+file.name+' 已处理，共记录 '+res.fileCount+' 个文件。'
                this.isResMsg=true;
                this.spinShow=false;
            },
            handleErrorZip(res, file) {

                this.isResMsgType='error'
                this.uploadResMsgTitle='文件处理失败'
                this.uploadResMsg='共处理 '+res.fileCount+' 个文件。'
                this.isResMsg=true;
                this.spinShow=false;
            },
            handleFormatError(file) {
                this.isResMsgType='error'
                this.uploadResMsgTitle='文件格式不正确'
                this.uploadResMsg='文件 ' + file.name + ' 格式不正确，请上传 zip 格式的压缩包。'
                this.isResMsg=true;
                this.spinShow=false;
            },
            handleMaxSize(file) {

                this.isResMsgType='error'
                this.uploadResMsgTitle='超出文件大小限制'
                this.uploadResMsg='文件 ' + file.name + ' 太大，不能超过 20M。'
                this.isResMsg=true;
                this.spinShow=false;
            },


        },
        mounted() {
            //页面加载时或数据方法
            this.uploadResMsg=''
            this.wxID = this.$route.params.id
            this.$store.commit('wxManager_set', {id:this.$route.params.id})

        },
        data() {
            return {
                uploadResMsg:'',
                uploadResMsgTitle:'',
                isResMsg:false,
                isResMsgType:'error',
                wxID:'',
                uploadAction: consts.env + '/qr00/upQrZip',
                spinShow:false,

            }
        }
    }
</script>
<style lang="less">
    @import '../../../styles/common.less';
</style>