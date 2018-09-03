package com.mybank.pc.qrcode.info;

import cn.hutool.core.io.FileUtil;
import cn.hutool.db.DaoTemplate;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.upload.UploadFile;
import com.mybank.pc.core.CoreController;
import com.mybank.pc.kits.ResKit;
import com.mybank.pc.qrcode.model.QrcodeInfo;
import com.mybank.pc.qrcode.model.QrcodeWxacct;
import com.mybank.pc.qrcode.wxacct.QrcodeWxSrv;
import com.mybank.pc.qrcode.wxacct.QrcodeWxValidator;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 *  微信账号管理
 */

public class QrcodeInfoCtr extends CoreController {


    /**
     * 显示二维码统计列表
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








}
