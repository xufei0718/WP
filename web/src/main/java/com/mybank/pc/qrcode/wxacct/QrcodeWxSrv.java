package com.mybank.pc.qrcode.wxacct;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import com.jfinal.kit.LogKit;
import com.mybank.pc.kits.ZipKit;
import com.mybank.pc.qrcode.model.QrcodeInfo;
import com.mybank.pc.qrcode.model.QrcodeWxacct;
import org.apache.commons.lang.StringUtils;

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

    public int unzip(String zipFileName, String dstPath, QrcodeWxacct qw) {

        LogKit.info("zip uncompressing...");
        ZipInputStream zipInputStream = null;
        int fileCount = 0;
        try {
            //将文件路径后增加微信账号文件夹
            dstPath = dstPath+ "/"+qw.getWxAcct();
            File dirWx = new File(dstPath);
            //如果没有此文件夹则创建
            if (!dirWx.exists()) {
                dirWx.mkdirs();
                LogKit.info("mkdirs: " + dirWx.getCanonicalPath());
            }

            zipInputStream = new ZipInputStream(new FileInputStream(zipFileName), Charset.forName("GBK"));
            ZipEntry zipEntry = null;
            byte[] buffer = new byte[ZipKit.BUFFER];//缓冲器
            int readLength = 0;//每次读出来的长度

            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                if("__MACOSX".equals(zipEntry.getName().split("/")[0])){
                    continue;//跳出
                }

                if (zipEntry.isDirectory()) {//若是zip条目目录，则需创建这个目录
                    File dir = new File(dstPath + "/" + zipEntry.getName());
                    if (!dir.exists()) {
                        dir.mkdirs();
                        LogKit.info("mkdirs: " + dir.getCanonicalPath());
                        continue;//跳出
                    }
                }
                //解析文件名   微信账号-金额-.jpg
                String fileName =  zipEntry.getName();
                String fileNameWxAcct =fileName.split("-")[0];
                if(fileNameWxAcct.equals(qw.getWxAcct())){

                    File file = ZipKit.createFile(dstPath, fileName);//若是文件，则需创建该文件
                    //System.out.println("file created: " + file.getCanonicalPath());
                    OutputStream outputStream = new FileOutputStream(file);
                    while ((readLength = zipInputStream.read(buffer, 0, ZipKit.BUFFER)) != -1) {
                        outputStream.write(buffer, 0, readLength);
                    }
                    outputStream.close();
                    //保存二维码图片相关信息
                    if(saveQrCode(file.getName(),qw)){
                        fileCount+=1;
                    }

                    LogKit.debug("file uncompressed: " + file.getName());
                }

            }    // end while
        } catch (FileNotFoundException e) {
            LogKit.info(e.getMessage());
            e.printStackTrace();
            LogKit.info("unzip fail!");
            return 0;
        } catch (IOException e) {
            LogKit.info(e.getMessage());
            e.printStackTrace();
            LogKit.info("unzip fail!");
            return 0;
        } finally {
            if (zipInputStream != null) {
                try {
                    zipInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

     LogKit.info("unzip success!");
        return fileCount;
    }

    public boolean saveQrCode(String fileName,QrcodeWxacct qw){
        if (StringUtils.isEmpty(fileName)){
          return false;
        }

        String fileNameWxAcct =fileName.split("-")[0];
        String fileNameAmount =fileName.split("-")[1];
        if(!fileNameWxAcct.equals(qw.getWxAcct())){
            return false;
        }


        BigDecimal realAmount =new BigDecimal(fileNameAmount);
        //System.out.println(realAmount);
        //计算交易金额
        BigDecimal amount = realAmount.setScale( 0, BigDecimal.ROUND_UP ); // 向上取整

        QrcodeInfo qitemp = QrcodeInfo.dao.findFirst("select * from qrcode_info qi where qi.realAmount= "+realAmount+" and qi.wxAcct='"+qw.getWxAcct()+"' and qi.dat is null");

        if (ObjectUtil.isNull(qitemp)){
            QrcodeInfo qi = new QrcodeInfo();
            qi.setWxAcctID(qw.getId());
            qi.setAmount(amount);
            qi.setRealAmount(realAmount);
            qi.setIsLock("0");
            qi.setIsVail("0");
            qi.setImgName(fileName);
            qi.setWxAcct(qw.getWxAcct());
            qi.setCat(new Date());
            qi.save();
        }else{
            qitemp.setImgName(fileName);
            qitemp.setMat(new Date());
            qitemp.update();
        }

        return true;
    }

    public static void main(String[] args) {
        String dir = "C:/Users/Fly/Desktop/img/";
        String wxAcct = "test_wx_acct_01";
        BigDecimal name =  new  BigDecimal("10");
        BigDecimal amount = name.setScale( 1, BigDecimal.ROUND_UP );
        String h = ".jpg";
        File imgSrc = new File(dir + "10.jpg");
        BigDecimal imgName= name;
        for (int i=0; i<100;i++){
            imgName =imgName.add(new BigDecimal("0.1"));
            imgName =imgName.setScale( 1, BigDecimal.ROUND_UP );
            File img = new File(dir+"zip/" +wxAcct+"-"+ imgName +"-"+h);
            FileUtil.copy(imgSrc, img, true);
        }

    }
}
