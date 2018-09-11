package com.mybank.pc.qrcode.wxacct;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import com.jfinal.aop.Before;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.LogKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.upload.UploadFile;
import com.mybank.pc.core.CoreController;
import com.mybank.pc.interceptors.AdminIAuthInterceptor;
import com.mybank.pc.kits.FileKit;
import com.mybank.pc.kits.ResKit;
import com.mybank.pc.qrcode.model.QrcodeInfo;
import com.mybank.pc.qrcode.model.QrcodeWxacct;
import org.apache.commons.codec.binary.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *  微信账号管理
 */

public class QrcodeWxCtr extends CoreController {
    private QrcodeWxSrv qrcodeWxSrv =enhance(QrcodeWxSrv.class);

    /**
     * 显示微信账号列表
     */
    public void list() {
        Page<QrcodeWxacct> page;
        String serach = getPara("search");
        StringBuffer where = new StringBuffer("from qrcode_wxacct qw where 1=1 and qw.dat is null  ");
        if (!isParaBlank("search")) {
            where.append(" and instr(qw.wxAcct,?)>0 ");
            where.append(" ORDER BY qw.cat desc");
            page = QrcodeWxacct.dao.paginate(getPN(), getPS(), "select * ", where.toString(), serach );
        } else {
            where.append(" ORDER BY qw.cat desc");
            page = QrcodeWxacct.dao.paginate(getPN(), getPS(), "select * ", where.toString());
        }
        Map map = new HashMap();
        map.put("page",page);
        renderJson(map);

    }

    /**
     * 新增微信账号
     */
    @Before({QrcodeWxValidator.class, Tx.class})
    public void save() {
        QrcodeWxacct qrcodeWxacct = getModel(QrcodeWxacct.class,"",true);
        qrcodeWxacct.setCat(new Date());
        qrcodeWxacct.setIsLogin("1");
        qrcodeWxacct.save();
        renderSuccessJSON("新增微信账号成功。", "");
    }

    /**
     * 删除微信账号
     */
    @Before({Tx.class})
    public void del(){
        int id=getParaToInt("id");
        QrcodeWxacct qrcodeWxacct=QrcodeWxacct.dao.findById(id);
        List<QrcodeInfo> list = QrcodeInfo.dao.find("select * from qrcode_info qi where qi.wxAcctID="+id);

        Date date = new Date();
        if(CollectionUtil.isNotEmpty(list)){
            for (QrcodeInfo qi :list) {
                qi.setDat(date);
                qi.update();
            }
        }

        qrcodeWxacct.setDat(date);
        qrcodeWxacct.update();
        renderSuccessJSON("删除微信账号成功。");

    }
    /**
     * 查询微信账号详细信息
     */
    public void info() {
        int id=getParaToInt("id");
            QrcodeWxacct qrcodeWxacct = QrcodeWxacct.dao.findById(id );
            //查询二维码数量
       Integer qrCount = Db.queryInt("select count(qi.id) from qrcode_info qi where qi.wxAcctID=" + id + " and qi.dat is null");
        Map map = new HashMap();
        map.put("qrcodeWxacct",qrcodeWxacct);
        map.put("qrCount",qrCount);
        renderJson(map);

    }

    /**
     * 二维码交易图片压缩包上传
     */
    public void upQrZip()  {
        Map resMap = new HashMap();
        int fileCount=0;
        try {
            UploadFile uf = getFile("file" ,"", 20 * 1024 * 1000);
            File file = uf.getFile();
            Integer id = Integer.valueOf(getPara("id"));
            QrcodeWxacct qw=   QrcodeWxacct.dao.findById(id);
            String zipTempFilePath = ResKit.getConfig("qrcode.ziptemp.path");
            String unzipFilePath = ResKit.getConfig("qrcode.img.path");
            File zipTemp = new File(zipTempFilePath + file.getName());
            FileUtil.copy(file, zipTemp, true);
            //System.out.println(zipTemp.getPath());
             fileCount = qrcodeWxSrv.unzip(zipTemp.getPath(), unzipFilePath,qw);
            if (fileCount==0) {
                resMap.put("resCode","1");
                resMap.put("resMsg","文件处理失败");
                resMap.put("fileCount",fileCount);
                renderJson(resMap);
                return;
            }


            FileUtil.del(zipTemp);
            resMap.put("resCode","0");
            resMap.put("resMsg","文件处理成功");
            resMap.put("fileCount",fileCount);

            renderJson(resMap);
        }catch (Exception e){
            e.printStackTrace();
            resMap.put("resCode","1");
            resMap.put("resMsg","文件处理失败");
            resMap.put("fileCount",fileCount);
            renderJson(resMap);
        }


    }

    /**
     * 微信账号登陆
     */
    public void wxLogin(){
        String wxAcct  =  getPara("wxAcct");
        String imgPath = ResKit.getConfig("login.img.path");
        Map resMap = new HashMap();
        //将文件路径后增加微信账号文件夹
        try {
        imgPath = imgPath+ "/"+wxAcct;
        File dirWx = new File(imgPath);
        //如果没有此文件夹则创建
        if (!dirWx.exists()) {

            dirWx.mkdirs();
                LogKit.info("mkdirs: " + dirWx.getCanonicalPath());
        }else{
            FileKit.deleteDir(dirWx);
            dirWx.mkdirs();
            LogKit.info("mkdirs: " + dirWx.getCanonicalPath());
        }
        String res;

        String url = ResKit.getConfig("wxLogin.url");
        Map<String, String> map = new HashMap<>();
        map.put("wxCode", wxAcct);

        try {

            res = HttpKit.post(url, map, "");
            LogKit.info("返回信息：" + res);

        }catch (Exception e){
            e.printStackTrace();
        }


        resMap.put("code","0000");
        renderJson(resMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     *  获得登录二维码
     */
    public void getLoginImg(){
        String wxAcct  =  getPara("wxAcct");
        Map resMap = new HashMap();
        FileInputStream inputStream ;
        try {
        //获取登录二维码
        String imgPath = ResKit.getConfig("login.img.path");
        inputStream = new FileInputStream(imgPath + "/" + wxAcct + "/" + wxAcct+".jpg");

        //byte数组用于存放图片字节数据
        byte[] buff = new byte[inputStream.available()];
        inputStream.read(buff);

        resMap.put("imgData", new String(Base64.encodeBase64(buff)));
        renderJson(resMap);
        }
        catch (Exception e) {
            //e.printStackTrace();
            resMap.put("imgData","");
            renderJson(resMap);
        }
    }

    /**
     *  查询登录状态
     */
    public void queryLoginStatus(){
        String wxID  =  getPara("wxID");
        Map resMap = new HashMap();
        QrcodeWxacct qrcodeWxacct = QrcodeWxacct.dao.findById(new Integer(wxID));
        resMap.put("isLogin",qrcodeWxacct.getIsLogin());
        resMap.put("wxAcct",qrcodeWxacct.getWxAcct());
        renderJson(resMap);
    }


    /**
     * 上传登陆二维码接口
     */
    @com.jfinal.aop.Clear(AdminIAuthInterceptor.class)
    public void qrUpload(){
        UploadFile uf = getFile("file", "", 4 * 1024 * 1000);
        File file = uf.getFile();
        String wxCode = getPara("wxCode");
        String loginImgPath = ResKit.getConfig("login.img.path");
        File loginImg = new File(loginImgPath +"/"+ wxCode +"/"+ wxCode+".jpg");
        FileUtil.copy(file, loginImg, true);
        Map resMap = new HashMap();
        resMap.put("resCode","0000");
        renderJson(resMap);
    }

    /**
     * 登录成功通知
     */
    @com.jfinal.aop.Clear(AdminIAuthInterceptor.class)
    public void loginNotify(){
        String wxCode = getPara("wxCode");
        QrcodeWxacct qrcodeWxacct =  QrcodeWxacct.dao.findFirst("select * from qrcode_wxacct qw where qw.dat is null and qw.wxAcct='"+wxCode+"'");
        qrcodeWxacct.setIsLogin("0");
        qrcodeWxacct.update();
        Map resMap = new HashMap();
        resMap.put("resCode","0000");
        renderJson(resMap);
    }

    /**
     * 微信登出通知
     */
    @com.jfinal.aop.Clear(AdminIAuthInterceptor.class)
    public void logoutNotify(){
        String wxCode = getPara("wxCode");
        QrcodeWxacct qrcodeWxacct =  QrcodeWxacct.dao.findFirst("select * from qrcode_wxacct qw where qw.dat is null and qw.wxAcct='"+wxCode+"'");
        qrcodeWxacct.setIsLogin("1");
        qrcodeWxacct.update();
        Map resMap = new HashMap();
        resMap.put("resCode","0000");
        renderJson(resMap);
    }

    /**
     * 程序退出通知，按登出处理
     */
    @com.jfinal.aop.Clear(AdminIAuthInterceptor.class)
    public void exitNotify(){
        String wxCode = getPara("wxCode");
        QrcodeWxacct qrcodeWxacct =  QrcodeWxacct.dao.findFirst("select * from qrcode_wxacct qw where qw.dat is null and qw.wxAcct='"+wxCode+"'");
        qrcodeWxacct.setIsLogin("1");
        qrcodeWxacct.update();
        Map resMap = new HashMap();
        resMap.put("resCode","0000");
        renderJson(resMap);
    }
}
