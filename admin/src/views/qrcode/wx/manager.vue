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
                                <Button type="error" icon="ios-trash" size="large">删除账号</Button>
                            <Button type="success" icon="refresh" size="large" @click="ret">返回</Button>
                        </Col>

                    </Row>
                    <Row>
                        <Col span="12" >
                            <Card style="margin-right: 5px ;height: 400px">
                                <p slot="title">上传支付二维码图片</p>
                                <p align="center">

                                        <Upload
                                                :on-success="handleSuccessZip"
                                                :format="['zip']"
                                                :max-size="20480"
                                                :on-format-error="handleFormatError"
                                                :on-exceeded-size="handleMaxSize"
                                                :before-upload="handleBeforeUpload"
                                                :action="uploadAction"
                                                :data="{id:wxID}">
                                            <Button  type="ghost" icon="ios-cloud-upload-outline">点击图片文件压缩包（.zip）</Button>
                                        </Upload>
                                        <span>{{uploadResMsg}}</span>
                                </p>

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

<script>


    import {mapState} from 'vuex'
    import consts from '../../../libs/consts'
    import Button from "iview/src/components/button/button";

    export default {
        components: {Button},
        computed: {
            ...mapState({
                'qrcodeWx': state => state.qrcodeWx.qrcodeWx,


            })
        },
        methods: {

                ret() {
                    this.$router.push({path: '/qrcode/wx'})
                },
            handleBeforeUpload(res, file) {
                this.modalLoading = true;
            },
            handleSuccessZip(res, file) {
                this.uploadResMsg=res
                this.$Notice.warning({
                    title: '上传成功',
                    desc: '上传文件成功'
                });

            },

            handleFormatError(file) {
                this.$Notice.warning({
                    title: '文件格式不正确',
                    desc: '文件 ' + file.name + ' 格式不正确，请上传 zip 格式的压缩包。'
                });
            },
            handleMaxSize(file) {
                this.$Notice.warning({
                    title: '超出文件大小限制',
                    desc: '文件 ' + file.name + ' 太大，不能超过 20M。'
                });
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
                wxID:'',
                uploadAction: consts.env + '/qr00/upQrZip',

            }
        }
    }
</script>
<style lang="less">
    @import '../../../styles/common.less';
</style>