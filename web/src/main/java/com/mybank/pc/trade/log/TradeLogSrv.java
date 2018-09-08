package com.mybank.pc.trade.log;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.math.MathUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.jfinal.kit.LogKit;
import com.mybank.pc.kits.DateKit;
import com.mybank.pc.kits.ZipKit;
import com.mybank.pc.merchant.model.MerchantInfo;
import com.mybank.pc.qrcode.model.QrcodeInfo;
import com.mybank.pc.qrcode.model.QrcodeWxacct;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.Date;
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
     *  更新商户账户余额
     * @param merID
     * @param amount
     * @param mathType ture:加法  false:减法
     */
    public void updateMerAmount(Integer merID ,BigDecimal amount,boolean mathType){
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
