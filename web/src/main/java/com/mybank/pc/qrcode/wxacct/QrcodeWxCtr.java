package com.mybank.pc.qrcode.wxacct;

import cn.hutool.core.io.FileUtil;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.upload.UploadFile;
import com.mybank.pc.core.CoreController;
import com.mybank.pc.kits.ResKit;
import com.mybank.pc.qrcode.model.QrcodeWxacct;

import java.io.File;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
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
        qrcodeWxacct.setDat(new Date());
        qrcodeWxacct.update();
        renderSuccessJSON("删除微信账号成功。");

    }
    /**
     * 查询微信账号详细信息
     */
    public void info() {
        int id=getParaToInt("id");
            QrcodeWxacct qrcodeWxacct = QrcodeWxacct.dao.findById(id );

        Map map = new HashMap();
        map.put("qrcodeWxacct",qrcodeWxacct);
        renderJson(map);

    }

    /**
     * 二维码交易图片压缩包上传
     */
    public void upQrZip()  {
        try {
            UploadFile uf = getFile("file" ,"", 20 * 1024 * 1000);
            File file = uf.getFile();
            Integer id = Integer.valueOf(getPara("id"));
            QrcodeWxacct qw=   QrcodeWxacct.dao.findById(id);
            String zipTempFilePath = ResKit.getConfig("qrcode.ziptemp.path");
            String unzipFilePath = ResKit.getConfig("qrcode.img.path");
            File zipTemp = new File(zipTempFilePath + file.getName());
            FileUtil.copy(file, zipTemp, true);
            System.out.println(zipTemp.getPath());
            if (!qrcodeWxSrv.unzip(zipTemp.getPath(), unzipFilePath,qw)) {
                renderSuccessJSON("文件处理失败");
            }
            FileUtil.del(zipTemp);
            renderSuccessJSON("文件处理成功");
        }catch (Exception e){
            e.printStackTrace();
            renderSuccessJSON("文件处理失败");
        }


    }


}
