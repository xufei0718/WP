package com.mybank.pc.qrcode.info;

import cn.hutool.core.io.FileUtil;
import cn.hutool.db.DaoTemplate;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.upload.UploadFile;
import com.mybank.pc.core.CoreController;
import com.mybank.pc.kits.ResKit;
import com.mybank.pc.kits.json.filter.BigDecimalValueFilter;
import com.mybank.pc.qrcode.model.QrcodeInfo;
import com.mybank.pc.qrcode.model.QrcodeWxacct;
import com.mybank.pc.qrcode.wxacct.QrcodeWxSrv;
import com.mybank.pc.qrcode.wxacct.QrcodeWxValidator;

import java.io.File;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *  微信账号管理
 */

public class QrcodeInfoCtr extends CoreController {


    /**
     * 显示二维码统计列表
     */
    public void list() {
        Page<Record> page;
        String serach = getPara("search");
        StringBuffer select = new StringBuffer(
                "select qi.amount as amount, " +
                "count(qi.amount) as amountCount, " +
                "(select count(q.isLock) from qrcode_info q where q.amount=qi.amount and q.isLock='1' and q.dat is NULL) as lockCount, " +
                "(select count(q.isLock) from qrcode_info q where q.amount=qi.amount and q.isVail='1' and q.dat is NULL) as vailCount, " +
                "(select count(q.isLock) from qrcode_info q where q.amount=qi.amount and q.isVail='0' and q.isLock='0'  and  q.dat is NULL) as grrCount "

        );
        StringBuffer sql = new StringBuffer(
                "from qrcode_info qi where 1=1 and qi.dat is null "
        );
        if (!isParaBlank("search")) {
            sql.append(" and qi.amount =? ");
            sql.append(" GROUP BY qi.amount  ORDER BY grrCount asc ");
            page=Db.paginate(getPN(),getPS(),select.toString(),sql.toString(),new BigDecimal(serach));
        }else{
            sql.append(" GROUP BY qi.amount  ORDER BY grrCount asc ");
            page=Db.paginate(getPN(),getPS(),select.toString(),sql.toString());
        }

        Map map = new HashMap();
        map.put("page",page);
        renderJson(map);

    }








}
