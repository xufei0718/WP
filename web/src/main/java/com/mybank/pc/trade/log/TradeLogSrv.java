package com.mybank.pc.trade.log;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.math.MathUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.jfinal.kit.LogKit;
import com.mybank.pc.kits.DateKit;
import com.mybank.pc.kits.ResKit;
import com.mybank.pc.kits.ZipKit;
import com.mybank.pc.merchant.model.MerchantInfo;
import com.mybank.pc.qrcode.model.QrcodeInfo;
import com.mybank.pc.qrcode.model.QrcodeWxacct;
import com.mybank.pc.trade.model.TradeLog;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Map;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class TradeLogSrv {


    public  String createTradeNo(){

        String strTime = DateKit.dateToStr(new Date(),DateKit.yyyyMMddHHmmssSSS);

        String strRandom = RandomUtil.randomNumbers(6);

        return strTime +strRandom;

    }

    /**
     * 获取可用二维码，并生成交易记录
     * @param resMap
     * @param tradeAmount
     * @param callBackUrl
     * @param merchantInfo
     * @return
     * @throws IOException
     */
    public synchronized QrcodeInfo getQrcodeAndSaveTradeLog(Map resMap ,  String tradeAmount , String callBackUrl ,MerchantInfo merchantInfo) throws IOException {
        String sql = "select * from qrcode_info qi where  qi.amount=" + tradeAmount + " and qi.isLock='0' and qi.isVail='0' and qi.dat is null order by qi.realAmount desc";
        QrcodeInfo qrcodeInfo = QrcodeInfo.dao.findFirst(sql);

        if (ObjectUtil.isNull(qrcodeInfo)) {
            LogKit.info("未找到可用二维码图片");
            resMap.put("resCode", "2");
            resMap.put("resMsg", "未找到可用二维码图片");
            return null;
        }

        String tradeNo = createTradeNo();

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
        resMap.put("tradeRealAmount",  tradeLog.getTradeRealAmount());
        resMap.put("resCode", "0");
        resMap.put("resMsg", "交易图片获取成功");
        return qrcodeInfo;
    }
    /**
     *  更新商户账户余额
     * @param merID
     * @param amount
     * @param mathType ture:加法  false:减法
     */
    public synchronized void updateMerAmount(Integer merID ,BigDecimal amount,boolean mathType){
        MerchantInfo merchantInfo = MerchantInfo.dao.findById(merID);
        if(ObjectUtil.isNotNull(merchantInfo)){
            BigDecimal Tamount;
            if (mathType){
                 Tamount = merchantInfo.getMaxTradeAmount().add(amount);
            }else{
                Tamount = merchantInfo.getMaxTradeAmount().subtract(amount);
            }

            merchantInfo.setMaxTradeAmount(Tamount);
            merchantInfo.update();
        }
    }
    /**
     * 加载本地文件,并转换为byte数组
     * @return
     */
    public static byte[] loadFile() {

        File file = new File("d:/11.jpg");
        FileInputStream fis = null;
        ByteArrayOutputStream baos = null;
        byte[] data = null ;
        try {
            fis = new FileInputStream(file);
            baos = new ByteArrayOutputStream((int) file.length());
            byte[] buffer = new byte[1024];
            int len = -1;
            while ((len = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            data = baos.toByteArray() ;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                    fis = null;
                }
                baos.close() ;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return data ;
    }



    /**
     * 对byte[]进行压缩
     *
     * @param data 要压缩的数据
     * @return 压缩后的数据
     */
    public static byte[] compress(byte[] data) {
        System.out.println("before:" + data.length);
        GZIPOutputStream gzip = null ;
        ByteArrayOutputStream baos = null ;
        byte[] newData = null ;
        try {
            baos = new ByteArrayOutputStream() ;
            gzip = new GZIPOutputStream(baos);
            gzip.write(data);
            gzip.finish();
            gzip.flush();
            newData = baos.toByteArray() ;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                gzip.close();
                baos.close() ;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("after:" + newData.length);
        return newData ;

    }
}
