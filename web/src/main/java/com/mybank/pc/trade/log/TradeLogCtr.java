package com.mybank.pc.trade.log;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import com.jfinal.aop.Before;
import com.jfinal.kit.Kv;
import com.jfinal.kit.LogKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.upload.UploadFile;
import com.mybank.pc.Consts;
import com.mybank.pc.core.CoreController;
import com.mybank.pc.interceptors.AdminIAuthInterceptor;
import com.mybank.pc.kits.DateKit;
import com.mybank.pc.kits.ResKit;
import com.mybank.pc.merchant.model.MerchantInfo;
import com.mybank.pc.qrcode.model.QrcodeInfo;
import com.mybank.pc.trade.model.TradeLog;
import org.apache.commons.codec.binary.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * 交易日志管理
 */

public class TradeLogCtr extends CoreController {
    private TradeLogSrv tradeLogSrv = enhance(TradeLogSrv.class);

    /**
     * 显示交易日志列表
     */
    public void list() {

        Map map = new HashMap();

        String search = getPara("search");
        String searchWxAcct = getPara("searchWxAcct");
        String searchAmount = getPara("searchAmount");
        Date searchStartDate = getParaToDate("searchDate[0]");
        Date searchEndDate = getParaToDate("searchDate[1]");
        //获取当前登录用户信息
        Kv kv = Kv.create();
        MerchantInfo merInfo = getAttr(Consts.CURR_USER_MER);
        if (merInfo != null) {
            kv.set("searchMerNo", merInfo.getMerchantNo());
            map.put("merAmount", merInfo.getMaxTradeAmount());
        }


        kv.set("search", search);
        kv.set("searchWxAcct", searchWxAcct);
        kv.set("searchAmount", searchAmount);
        kv.set("searchStartDate", DateKit.dateToStr(searchStartDate, DateKit.yyyyMMdd));
        kv.set("searchEndDate", DateKit.dateToStr(searchEndDate, DateKit.yyyyMMdd) + "235959");
        SqlPara sqlPara = Db.getSqlPara("trade.queryTradeLog", kv);


        Page<TradeLog> page = TradeLog.dao.paginate(getPN(), getPS(), sqlPara);


        map.put("page", page);
        renderJson(map);
    }

    /**
     * 交易凭证上传
     */
    @com.jfinal.aop.Clear(AdminIAuthInterceptor.class)
    public void upTradeImg() {
        Map resMap = new HashMap();
        int fileCount = 0;
        try {
            UploadFile uf = getFile("file", "", 4 * 1024 * 1000);
            File file = uf.getFile();
            String tradeNo = getPara("tradeNo");
            String tradeImgPath = ResKit.getConfig("trade.img.path");
            File tradeImg = new File(tradeImgPath + tradeNo + ".jpg");
            FileUtil.copy(file, tradeImg, true);

            resMap.put("tradeNo", tradeNo);
            resMap.put("tradeImgName", tradeImg.getName());
            resMap.put("resCode", "0");
            resMap.put("resMsg", "文件处理成功");
            renderJson(resMap);
        } catch (Exception e) {
            e.printStackTrace();
            resMap.put("resCode", "1");
            resMap.put("resMsg", "文件处理失败");
            renderJson(resMap);
        }
    }

    public void saveTrade() {
        TradeLog tradeLog = getModel(TradeLog.class, "", true);

        QrcodeInfo qrcodeInfo = QrcodeInfo.dao.findById(tradeLog.getTradeQrcodeID());

        tradeLog.setTradeStatus("0");
        tradeLog.setMat(new Date());
        if (ObjectUtil.isNotNull(currUser())) {
            tradeLog.setOperID(String.valueOf(currUser().getId()));
        }
        tradeLog.update();

        qrcodeInfo.setIsLock("0");
        qrcodeInfo.update();
        //累加商户账户余额
        tradeLogSrv.updateMerAmount(tradeLog.getTradeMerID(), tradeLog.getTradeRealAmount(), true);
        renderSuccessJSON("交易状态已更正。", "");
    }

    /**
     * 执行交易获取交易二维码
     */
    @com.jfinal.aop.Clear(AdminIAuthInterceptor.class)
    @Before({Tx.class})
    public void tradeQrcode() {
        //设置发送到客户端的响应内容类型
        getResponse().addHeader("Access-Control-Allow-Origin", "*");   //用于ajax post跨域（*，最好指定确定的http等协议+ip+端口号）
        getResponse().setCharacterEncoding("utf-8");

        FileInputStream inputStream = null;
        Map resMap = new HashMap();

        try {
            String tradeAmount = getPara("tradeAmount");
            String merNo = getPara("merNo");
            String merIdCode = getPara("merIdCode");
            String callBackUrl = getPara("callBackUrl");
            LogKit.info(tradeAmount);


            String sqlMer = "select * from merchant_info mi where mi.merchantNo='" + merNo + "'";
            MerchantInfo merchantInfo = MerchantInfo.dao.findFirst(sqlMer);
            //如果商户不存在
            if (ObjectUtil.isNull(merchantInfo)) {
                resMap.put("resCode", "3");
                resMap.put("resMsg", "商户信息不存在");
                renderJson(resMap);
                return;
            }
            //商户已被禁用
            if ("1".equals(merchantInfo.getStatus())) {
                resMap.put("resCode", "4");
                resMap.put("resMsg", "该商户已被禁用");
                renderJson(resMap);
                return;
            }
            //商户已被禁用
            if (!merIdCode.equals(merchantInfo.getMobile1())) {
                resMap.put("resCode", "5");
                resMap.put("resMsg", "商户信息未正确识别，识别码错误");
                renderJson(resMap);
                return;
            }

            QrcodeInfo qrcodeInfo = tradeLogSrv.getQrcodeAndSaveTradeLog(resMap, tradeAmount, callBackUrl, merchantInfo);

            if (ObjectUtil.isNotNull(qrcodeInfo)) {
                //如果正确获取二维码记录，并保存交易日志，读取本地图片输入流
                String imgPath = ResKit.getConfig("qrcode.img.path");
                inputStream = new FileInputStream(imgPath + "/" + qrcodeInfo.getWxAcct() + "/" + qrcodeInfo.getImgName());

                //byte数组用于存放图片字节数据
                byte[] buff = new byte[inputStream.available()];
                inputStream.read(buff);
                resMap.put("imgData", new String(Base64.encodeBase64(buff)));
            }


            renderJson(resMap);
        } catch (Exception e) {
            e.printStackTrace();
            resMap.put("resCode", "1");
            resMap.put("resMsg", "系统内部异常");
            renderJson(resMap);
        } finally {
            //记得关闭输入流
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    /**
     * 交易结果查询
     */
    @com.jfinal.aop.Clear(AdminIAuthInterceptor.class)
    public void tradeResult() {
        //设置发送到客户端的响应内容类型
        getResponse().addHeader("Access-Control-Allow-Origin", "*");   //用于ajax post跨域（*，最好指定确定的http等协议+ip+端口号）
        getResponse().setCharacterEncoding("utf-8");

        String tradeNo = getPara("tradeNo");
        String sql = "select * from trade_log tt where  tt.tradeNo = '" + tradeNo + "'";

        TradeLog tradeLog = TradeLog.dao.findFirst(sql);

        Map resMap = new HashMap();
        if (ObjectUtil.isNull(tradeLog)) {
            resMap.put("tradeNo", tradeNo);
            resMap.put("resMsg", "无此交易");
            renderJson(resMap);
            return;
        }
        resMap.put("tradeNo", tradeNo);
        resMap.put("tradeStatus", tradeLog.getTradeStatus());
        resMap.put("resMsg", tradeLog.getTradeStatusTxt());
        renderJson(resMap);
    }

    @com.jfinal.aop.Clear(AdminIAuthInterceptor.class)
    @Before({Tx.class})
    public void tradeNotify() {
        //设置发送到客户端的响应内容类型
        getResponse().addHeader("Access-Control-Allow-Origin", "*");   //用于ajax post跨域（*，最好指定确定的http等协议+ip+端口号）
        getResponse().setCharacterEncoding("utf-8");

        String wxAcct = getPara("wxCode");
        String bak = getPara("bak");
        String payAmount = getPara("payAmount");
        TradeLog tradeLog = tradeLogSrv.updateTradeStatus(wxAcct, payAmount);
        //交易结果回调接口，
        tradeLogSrv.tradeCallBack(tradeLog);
        Map resMap = new HashMap();
        resMap.put("resCode", "0000");
        renderJson(resMap);


    }

}
