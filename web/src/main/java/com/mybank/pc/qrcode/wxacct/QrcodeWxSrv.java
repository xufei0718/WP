package com.mybank.pc.qrcode.wxacct;

import cn.hutool.core.util.ObjectUtil;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.ehcache.CacheKit;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.rtf.RtfWriter2;
import com.mybank.pc.kits.ZipKit;
import com.mybank.pc.merchant.model.MerchantInfo;
import com.mybank.pc.merchant.model.MerchantUser;
import com.mybank.pc.qrcode.model.QrcodeInfo;
import com.mybank.pc.qrcode.model.QrcodeWxacct;
import org.apache.commons.lang.StringUtils;

import java.awt.*;
import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class QrcodeWxSrv {
    /**
     * 解压缩方法
     *
     * @param zipFileName 压缩文件名
     * @param dstPath     解压目标路径
     * @return
     */

    public boolean unzip(String zipFileName, String dstPath, QrcodeWxacct qw) {

        System.out.println("zip uncompressing...");
        ZipInputStream zipInputStream = null;

        try {
            zipInputStream = new ZipInputStream(new FileInputStream(zipFileName), Charset.forName("GBK"));
            ZipEntry zipEntry = null;
            byte[] buffer = new byte[ZipKit.BUFFER];//缓冲器
            int readLength = 0;//每次读出来的长度

            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                if (zipEntry.isDirectory()) {//若是zip条目目录，则需创建这个目录
                    File dir = new File(dstPath + "/" + zipEntry.getName());
                    if (!dir.exists()) {
                        dir.mkdirs();
                        System.out.println("mkdirs: " + dir.getCanonicalPath());
                        continue;//跳出
                    }
                }


                File file = ZipKit.createFile(dstPath, zipEntry.getName());//若是文件，则需创建该文件
                //System.out.println("file created: " + file.getCanonicalPath());
                OutputStream outputStream = new FileOutputStream(file);

                while ((readLength = zipInputStream.read(buffer, 0, ZipKit.BUFFER)) != -1) {
                    outputStream.write(buffer, 0, readLength);
                }
                outputStream.close();
                //写入数据库
                saveQrCode(file.getName(),qw);
                System.out.println("file uncompressed: " + file.getCanonicalPath());
            }    // end while
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            System.out.println("unzip fail!");
            return false;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            System.out.println("unzip fail!");
            return false;
        } finally {
            if (zipInputStream != null) {
                try {
                    zipInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        System.out.println("unzip success!");
        return true;
    }

    public boolean saveQrCode(String fileName,QrcodeWxacct qw){
        if (StringUtils.isEmpty(fileName)){
          return false;
        }
        String[] s =fileName.split("\\.");
        String[] s1 =s[0].split("_");
        String qrcodeNo =s1[0];
        BigDecimal amount =new BigDecimal( Integer.valueOf(s1[1])/100);

        QrcodeInfo qitemp = QrcodeInfo.dao.findFirst("select * from qrcode_info qi where qi.qrcodeNo= '"+qrcodeNo+"'");

        if (ObjectUtil.isNull(qitemp)){
            QrcodeInfo qi = new QrcodeInfo();
            qi.setWxAcctID(qw.getId());
            qi.setQrcodeNo(s1[0]);
            qi.setAmount(amount);
            qi.setIsLock("0");
            qi.setIsVail("0");
            qi.setImgName(fileName);
            qi.setWxAcct(qw.getWxAcct());
            qi.setCat(new Date());
            qi.save();
        }else{
            qitemp.setAmount(amount);
            qitemp.setImgName(fileName);
            qitemp.update();
        }

        return true;
    }
}
