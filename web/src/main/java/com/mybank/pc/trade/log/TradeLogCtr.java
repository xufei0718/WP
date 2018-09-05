package com.mybank.pc.trade.log;

import cn.hutool.core.util.ObjectUtil;
import com.jfinal.kit.LogKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.mybank.pc.core.CoreController;
import com.mybank.pc.interceptors.AdminIAuthInterceptor;
import com.mybank.pc.kits.ResKit;
import com.mybank.pc.merchant.model.MerchantInfo;
import com.mybank.pc.qrcode.model.QrcodeInfo;
import com.mybank.pc.trade.model.TradeLog;
import org.apache.commons.codec.binary.Base64;

import java.io.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPOutputStream;


/**
 * 交易日志管理
 */

public class TradeLogCtr extends CoreController {
    private TradeLogSrv tradeLogSrv = enhance(TradeLogSrv.class);

    /**
     * 显示交易日志列表
     */
    public void list() {


    }

    /**
     * 执行交易获取交易二维码
     */
    @com.jfinal.aop.Clear(AdminIAuthInterceptor.class)
    public void exeTradeQrcode() {
        //设置发送到客户端的响应内容类型
        getResponse().addHeader("Access-Control-Allow-Origin", "*");   //用于ajax post跨域（*，最好指定确定的http等协议+ip+端口号）
        getResponse().setCharacterEncoding("utf-8");

        FileInputStream inputStream = null;
        Map resMap = new HashMap();

        try {
            String tradeAmount = getPara("tradeAmount");
            String merNo = getPara("merNo");
            String callBackUrl = getPara("callBackUrl");
            LogKit.info(tradeAmount);
            String sql = "select * from qrcode_info qi where  qi.amount=" + tradeAmount + " and qi.isLock='0' and qi.isVail='0' and qi.dat is null";
            QrcodeInfo qrcodeInfo = QrcodeInfo.dao.findFirst(sql);

            String sqlMer = "select * from merchant_info mi where mi.merchantNo='" + merNo + "'";
            MerchantInfo merchantInfo = MerchantInfo.dao.findFirst(sqlMer);
            //如果商户不存在
            if (ObjectUtil.isNull(merchantInfo)) {
                resMap.put("resCode", "3");
                resMap.put("resMsg", "商户信息不存在");
                renderJson(resMap);
            }
            if (ObjectUtil.isNull(qrcodeInfo)) {
                LogKit.info("未找到可用二维码图片");
                resMap.put("resCode", "2");
                resMap.put("resMsg", "未找到可用二维码图片");
                renderJson(resMap);
            }
            //读取本地图片输入流
            String imgPath = ResKit.getConfig("qrcode.img.path");
            inputStream = new FileInputStream(imgPath + "/" + qrcodeInfo.getWxAcct() + "/" + qrcodeInfo.getImgName());

            //byte数组用于存放图片字节数据
            byte[] buff = new byte[inputStream.available()];
            inputStream.read(buff);
            String tradeNo = tradeLogSrv.createTradeNo();
            TradeLog tradeLog = new TradeLog();
            tradeLog.setTradeNo(tradeNo);
            tradeLog.setTradeMerID(merchantInfo.getId());
            tradeLog.setTradeMerNo(merchantInfo.getMerchantNo());
            tradeLog.setTradeMerName(merchantInfo.getMerchantName());
            tradeLog.setTradeAmount(new BigDecimal(tradeAmount));
            tradeLog.setTradeRealAmount(qrcodeInfo.getRealAmount());
            tradeLog.setTradeWxAcct(qrcodeInfo.getWxAcct());
            tradeLog.setTradeQrcodeImg(qrcodeInfo.getImgName());
            tradeLog.setTradeStatus("1");//1:二维码获取成功，交易已发起，0：已支付成功
            tradeLog.setTradeTime(new Date());
            tradeLog.setCat(new Date());
            tradeLog.setCallBackUrl(callBackUrl == null ? "" : callBackUrl);
            tradeLog.save();
            //更新二维码图片状态
            qrcodeInfo.setIsLock("1");//设置为已锁定
            qrcodeInfo.setMat(new Date());
            qrcodeInfo.update();


            resMap.put("tradeNo", tradeNo);
            resMap.put("imgData", new String(Base64.encodeBase64(buff)));
            resMap.put("resCode", "0");
            resMap.put("resMsg", "交易图片获取成功");
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

    }


}
